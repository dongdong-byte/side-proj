package kim.onbidproperty.service;

import kim.onbidproperty.domain.Property;
import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.dto.api.OnbidApiResponse;
import kim.onbidproperty.dto.api.OnbidPropertyDto;
import kim.onbidproperty.dto.api.PaginationRequest;
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

    private final OnbidApiService onbidApiService;
    private final PropertyMapper propertyMapper;
    private final PropertyBidHistoryMapper historyMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 온비드 API 데이터를 DB에 동기화
     */
    @Transactional
    public int syncProperties(int numOfRows, int maxPages) {
        log.info("온비드 데이터 동기화 시작");

        // ✅ PaginationRequest 객체 생성
        PaginationRequest pagination = PaginationRequest.of(numOfRows, maxPages);
        OnbidApiResponse response = onbidApiService.getAllProperties(pagination);

        // ✅ API 호출 실패 처리
        if (!response.isSuccess()) {
            log.error("API 호출 실패: {} - {}", response.getResultCode(), response.getResultMsg());
            return 0;
        }

        List<OnbidPropertyDto> apiProperties = response.getItems();
        int insertCount = 0;
        int updateCount = 0;

        for (OnbidPropertyDto dto : apiProperties) {
            try {
                Property existing = propertyMapper.selectPropertyByCltrNo(dto.getCltrNo());

                if (existing == null) {
                    // 신규 등록
                    Property newProperty = convertToProperty(dto);
                    // 신규 등록 시에만 기본값 설정
                    newProperty.setSaleType(SalesType.AUCTION);
                    newProperty.setStatus(PropertyStatus.AVAILABLE);
                    newProperty.setBidCount(0);
                    propertyMapper.insertProperty(newProperty);

                    // 회차 이력 정보 등록
                    if (dto.getPbctNo() != null) {
                        PropertyBidHistory history = convertToHistory(dto, newProperty.getId());
                        historyMapper.insertHistory(history);
                    }

                    insertCount++;
                    log.debug("신규 물건 등록: {}", dto.getCltrNo());

                } else {
                    // 기존 물건 업데이트
                    Property updatedProperty = convertToProperty(dto);
                    updatedProperty.setId(existing.getId());
                    propertyMapper.patchProperty(updatedProperty);

                    // 회차 이력 확인 및 등록
                    if (dto.getPbctNo() != null) {
                        PropertyBidHistory existingHistory =
                                historyMapper.selectHistoryByPropertyIdAndPbctNo(
                                        existing.getId(), dto.getPbctNo());
                        if (existingHistory == null) {
                            PropertyBidHistory newHistory = convertToHistory(dto, existing.getId());
                            historyMapper.insertHistory(newHistory);
                        }
                    }

                    updateCount++;
                    log.debug("기존 물건 업데이트: {}", dto.getCltrNo());
                }

            } catch (Exception e) {
                log.error("물건 동기화 실패: cltrNo={}, error={}",
                        dto.getCltrNo(), e.getMessage());
            }
        }

        log.info("온비드 데이터 동기화 완료: 신규={}, 업데이트={}", insertCount, updateCount);
        return insertCount + updateCount;
    }

    /**
     * DTO를 PropertyBidHistory 엔티티로 변환
     */
    private PropertyBidHistory convertToHistory(OnbidPropertyDto dto, Long propertyId) {
        PropertyBidHistory history = new PropertyBidHistory();

        history.setPropertyId(propertyId);
        history.setPbctNo(dto.getPbctNo());
        history.setCltrNo(dto.getCltrNo());
        history.setPbctSeq(dto.getPbctSeq());
        history.setPbctDgr(dto.getPbctDgr());
        history.setFeeRate(dto.getFeeRate());

        // BigDecimal (이미 파싱됨)
        if (dto.getMinBidPrc() != null) {
            history.setMinBidPrice(dto.getMinBidPrc());
        }
        if (dto.getApslAsesAvgAmt() != null) {
            history.setAppraisalPrice(dto.getApslAsesAvgAmt());
        }

        // 날짜 변환
        history.setAuctionStartTime(parseDateTime(dto.getPbctBegnDtm()));
        history.setAuctionEndTime(parseDateTime(dto.getPbctClsDtm()));
        history.setAuctionResultTime(parseDateTime(dto.getPbctExctDtm()));

        // 입찰 보증금율
        if (dto.getTdpsRt() != null && !dto.getTdpsRt().isEmpty()) {
            try {
                // 소수점(.)을 남겨서 "10.5" -> "10.5"가 되도록 함
                String rateStr = dto.getTdpsRt().replaceAll("[^0-9.]", "");
                if (!rateStr.isEmpty()) {
                    history.setDepositRate(new BigDecimal(rateStr));
                }
            } catch (Exception e) {
                log.warn("입찰 보증금율 변환 실패: {}", dto.getTdpsRt());
            }
        }

        history.setBidStatus(BidStatus.PENDING);
        return history;
    }

    /**
     * DTO를 Property 엔티티로 변환 (수동 매핑)
     */
    private Property convertToProperty(OnbidPropertyDto dto) {
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
        property.setRoadAddress(dto.getNmrdAdrs());
        property.setPnu(dto.getLdnmPnu());

        // BigDecimal (이미 파싱됨)
        if (dto.getApslAsesAvgAmt() != null) {
            property.setAppraisalPrice(dto.getApslAsesAvgAmt());
        }
        if (dto.getMinBidPrc() != null) {
            property.setMinBidPrice(dto.getMinBidPrc());
        }

        // 날짜 변환
        property.setAuctionStartTime(parseDateTime(dto.getPbctBegnDtm()));
        property.setAuctionEndTime(parseDateTime(dto.getPbctClsDtm()));

        // 조회수 및 동기화 시간
        property.setViewCount(dto.getIqryCnt() != null ? dto.getIqryCnt() : 0);
        property.setSyncedAt(LocalDateTime.now());

        return property;
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 변환
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.length() < 14) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateTimeStr);
            return null;
        }
    }
}