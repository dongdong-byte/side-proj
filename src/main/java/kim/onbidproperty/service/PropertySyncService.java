package kim.onbidproperty.service;


import kim.onbidproperty.domain.Property;
import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.dto.api.OnbidPropertyDto;
import kim.onbidproperty.enums.BidStatus;
import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.enums.SalesType;
import kim.onbidproperty.mapper.PropertyBidHistoryMapper;
import kim.onbidproperty.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertySyncService {


    private  final  OnbidApiService onbidApiService;
    private  final PropertyMapper propertyMapper;
    private  final PropertyBidHistoryMapper historyMapper;

    private  static  final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//    온비드 api 데이터를 DB에 동기화
    @Transactional
    public int syncProperties(int numOfRows , int maxPages){
        log.info("온비드 데이터 동기화시작");
        List<OnbidPropertyDto> apiProperties = onbidApiService.getAllProperties(numOfRows, maxPages);
        int insertCount = 0;
        int updateCount = 0;
        for(OnbidPropertyDto dto : apiProperties){
            try {
                Property existing = propertyMapper.selectPropertyByCltrNo(dto.getCltrNo());
                if(existing == null){
//                    신규 등록
                    Property newProperty = convertToProperty(dto);
//                    [추가] 신규 등록 시에만 기본값 설정
                    newProperty.setSaleType(SalesType.AUCTION);
                    newProperty.setStatus(PropertyStatus.AVAILABLE);
                    newProperty.setBidCount(0); //         입찰수 0으로 초기화
                    propertyMapper.insertProperty(newProperty);
//                    회차 이력 정보 등록
                    if(dto.getPbctNo() != null){
                        PropertyBidHistory history = convertToHistory(dto,newProperty.getId());
                        historyMapper.insertHistory(history);

                    }
                    insertCount++;
                    log.debug("신규 물건 등록  : {}" , dto.getCltrNo());

                }else{
//                    기존 물건 업데이트
                    Property updatedProperty = convertToProperty(dto);
                    updatedProperty.setId(existing.getId()); //    ID 설정
//                    [수정] 기본값을 덮어쓰지 않고 업데이트
                    propertyMapper.patchProperty(updatedProperty);
//                    회차 이력 확인 및 등록
                    if(dto.getPbctNo() != null){
                        PropertyBidHistory existingHistory =
                                historyMapper.selectHistoryByPropertyIdAndPbctNo(existing.getId(), dto.getPbctNo());
                        if(existingHistory == null){
                            PropertyBidHistory newHistory = convertToHistory(dto, existing.getId());
                            historyMapper.insertHistory(newHistory);
                        }
                    }
                    updateCount++;
                    log.debug("기존 물건 업데이트 : {}", dto.getCltrNo());
                }

            } catch (Exception e) {
               log.error("물건 동기화 실패: cltrNo :{}, error= {}",dto.getCltrNo(), e.getMessage());
            }



        }
        log.info("온비드 데이터 동기화 완료: 신규={}, 업데이트={}", insertCount, updateCount);
        return insertCount + updateCount;
    }
// DTO를 PropertyBidHistory 엔티티로 변환
    private PropertyBidHistory convertToHistory(OnbidPropertyDto dto, Long propertyId) {
        PropertyBidHistory history = new PropertyBidHistory();

        history.setPropertyId(propertyId);
        history.setPbctNo(dto.getPbctNo());
      history.setCltrNo(dto.getCltrNo());
        history.setPbctSeq(dto.getPbctSeq());
        history.setPbctDgr(dto.getPbctDgr());
        history.setFeeRate(dto.getFeeRate());

        //        BigDecimal(이미 파싱됨)
        if(dto.getMinBidPrc() != null){
            history.setMinBidPrice(dto.getMinBidPrc());
        }
        if(dto.getApslAsesAvgAmt() != null){
            history.setAppraisalPrice(dto.getApslAsesAvgAmt());
        }
//날짜 변환
        history.setAuctionStartTime(parseDateTime(dto.getPbctBegnDtm()));
        history.setAuctionEndTime(parseDateTime(dto.getPbctClsDtm()));
        history.setAuctionResultTime(parseDateTime(dto.getPbctExctDtm()));

//        입찰 보증금율
        if(dto.getTdpsRt() != null && !dto.getTdpsRt().isEmpty()){
            try {
//                [수정] 소수점(.)을 남겨서 "10.5" -> "10.5"가 되도록 함
                String rateStr =dto.getTdpsRt().replaceAll("[^0-9.]", "");
                if(!rateStr.isEmpty()){
  // 숫자가 하나도 없으면 파싱 오류가 날 수 있으므로 방지
                    history.setDepositRate(new BigDecimal(rateStr));
                }
            } catch (Exception e) {
                log.warn("입찰 보증금율 변환 실패 : {}", dto.getTdpsRt());
            }
        }
history.setBidStatus(BidStatus.PENDING);
        return history;
    }

    //Dto를 Property엔티티로 변환 (수동매핑)
    private Property convertToProperty(OnbidPropertyDto dto) {
//        modelmapper를 안사용하고 수동으로 매핑 하는 이유
//        필드명이 다르므로 수동으로 매핑함
        Property property = new Property();
        property.setCltrNo(dto.getCltrNo());
        property.setCltrMnmtNo(dto.getCltrMnmtNo());
        property.setPlnmNo(dto.getPlnmNo());
        property.setName(dto.getCltrNm());
        property.setGoodsDescription(dto.getGoodsNm());
        property.setCategory(dto.getCtgrFullNm());
        property.setLocation(dto.getLdnmAdrs());
        property.setBidMethod(dto.getBidMtdNm());
        property.setPbctStatus(dto.getPbctCltrStatNm());
        property.setRoadAddress(dto.getNmrdAdrs());  // ✅ 추가
        property.setPnu(dto.getLdnmPnu());           // ✅ 추가
//        BigDecimal(이미 파싱됨)
        if(dto.getApslAsesAvgAmt() != null){
            property.setAppraisalPrice(dto.getApslAsesAvgAmt());
        }
        if(dto.getMinBidPrc() != null){
            property.setMinBidPrice(dto.getMinBidPrc());
        }
//날짜 변환
        property.setAuctionStartTime(parseDateTime(dto.getPbctBegnDtm()));
        property.setAuctionEndTime(parseDateTime(dto.getPbctClsDtm()));
//        기본값 설정
//       [제거] 기본값 설정 (신규 등록 시에만 설정해야 함)
//        property.setSaleType(SalesType.AUCTION);
//        property.setStatus(PropertyStatus.AVAILABLE);

//        property.setBidCount(0);
//        [유지] 이 값들은 DTO에서 오거나 매번 갱신되어야 함
        property.setViewCount(dto.getIqryCnt() != null ? dto.getIqryCnt() : 0);
        property.setSyncedAt(LocalDateTime.now());


        return property;
    }
//    날짜 문자열을 LocalDateTime으로 변환
    private LocalDateTime parseDateTime(String dateTimeStr){
        if(dateTimeStr == null || dateTimeStr.length()<14){
            return null;
        }
        try {
            return  LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패 : {}" ,dateTimeStr);
            return null;
        }
    }
}
