package kim.onbidproperty.service;


import kim.onbidproperty.client.OnbidApiClient;
import kim.onbidproperty.dto.api.OnbidPropertyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnbidApiService {
    private final OnbidApiClient onbidApiClient;
//    온비드 API에서 물건 목록 조회후 DTO로 전환
    public List<OnbidPropertyDto> getPropertyList(int pageNo, int numOfRows){
        log.info("온비드 API에서 물건 목록 조회: pageNo = {} ,numOfRows={} ",pageNo,numOfRows);
        try {
            String xmlResponse = onbidApiClient.fetchPropertyList(pageNo,numOfRows);
            return parseXmlResponse(xmlResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//XML응답을 DTO리스트로 전환
    private List<OnbidPropertyDto> parseXmlResponse(String xmlResponse) throws Exception {
        List<OnbidPropertyDto> properties = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 💡 XXE 방지용 보안 설정 추가
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

        NodeList resultCodeNode = document.getElementsByTagName("resultCode");
        if(resultCodeNode.getLength() > 0 ){
            String resultCode = resultCodeNode.item(0).getTextContent();
            if(!"00".equals(resultCode)){
             NodeList resultMsgNodes = document.getElementsByTagName("resultMsg");
             String resultMsg = resultMsgNodes.getLength() > 0 ? resultMsgNodes.item(0).getTextContent() : "알수 없는 에러";
             log.error("API 오류 : {} -{}" ,resultCode,resultMsg);
                throw new RuntimeException("온비드 API 오류:  " + resultMsg);
            }
        }
NodeList itemNodes = document.getElementsByTagName("item");
        log.info("조회된 물건수 : {}" ,itemNodes.getLength());
        for(int i =0; i< itemNodes.getLength(); i++){
            Element item = (Element) itemNodes.item(i);
            OnbidPropertyDto dto = parseItemElement(item);
            properties.add(dto);
        }
        return properties;
    }
//item 요소를 DTo로 변환
    private OnbidPropertyDto parseItemElement(Element item) {
        OnbidPropertyDto dto = new OnbidPropertyDto();
        dto.setCltrNo(getElementText(item, "CLTR_NO"));
        dto.setCltrMnmtNo(getElementText(item, "CLTR_MNMT_NO"));
        dto.setPlnmNo(getElementText(item, "PLNM_NO"));
        dto.setCltrNm(getElementText(item, "CLTR_NM"));
        dto.setGoodsNm(getElementText(item, "GOODS_NM"));
        dto.setCtgrFullNm(getElementText(item, "CTGR_FULL_NM"));
        dto.setLdnmAdrs(getElementText(item, "LDNM_ADRS"));
        dto.setNmrdAdrs(getElementText(item, "NMRD_ADRS"));
        dto.setLdnmPnu(getElementText(item, "LDNM_PNU"));
        dto.setBidMtdNm(getElementText(item, "BID_MTD_NM"));
        dto.setPbctBegnDtm(getElementText(item, "PBCT_BEGN_DTM"));
        dto.setPbctClsDtm(getElementText(item, "PBCT_CLS_DTM"));
        dto.setPbctCltrStatNm(getElementText(item, "PBCT_CLTR_STAT_NM"));
        dto.setFeeRate(getElementText(item, "FEE_RATE"));
        dto.setPbctNo(getElementText(item, "PBCT_NO"));
        dto.setPbctSeq(getElementText(item, "PBCT_SEQ"));
        dto.setPbctDgr(getElementText(item, "PBCT_DGR"));
        dto.setTdpsRt(getElementText(item, "TDPS_RT"));
        dto.setPbctExctDtm(getElementText(item, "PBCT_EXCT_DTM"));

//        감정가 (BigDecimal)
        String apslAsesAvgAmt = getElementText(item,"APSL_ASES_AVG_AMT");
        if(apslAsesAvgAmt != null && !apslAsesAvgAmt.isEmpty()){
            try {
                dto.setApslAsesAvgAmt(new BigDecimal(apslAsesAvgAmt.replaceAll("[^0-9]", "")));
            }catch (NumberFormatException e){
log.warn("감정가 파싱 실패 : {}" ,apslAsesAvgAmt);
dto.setApslAsesAvgAmt(BigDecimal.ZERO);
            }
        }
//        최저 입찰가(BigDecimal)
        String minBidPrc = getElementText(item, "MIN_BID_PRC");
        if(minBidPrc != null && !minBidPrc.isEmpty()){
            try {
                dto.setMinBidPrc(new BigDecimal(minBidPrc.replaceAll("[^0-9]", "")));
            }catch (NumberFormatException e){
                log.warn("최저 입찰가 파싱 실패 : {}", minBidPrc);
                dto.setMinBidPrc(BigDecimal.ZERO);
            }
        }

//조회수
        String iqryCnt = getElementText(item, "IQRY_CNT");
        if(iqryCnt != null && !iqryCnt.isEmpty()){
            try {
                dto.setIqryCnt(Integer.parseInt(iqryCnt));
            }catch (NumberFormatException e){
                log.error("IQRY_CNT 파싱 오류 : {}", e.getMessage());
                dto.setIqryCnt(0);
            }

        }


        return dto;

    }
//xml 요소에서 텍스트값 추출
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if(nodes.getLength()>0){
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }
//전체 페이지 조회
    public List<OnbidPropertyDto> getAllProperties(int numOfRows , int maxPages){
        log.info("온비드 API에서 전체 물건 목록 조회: numOfRows = {} ,maxPages={} ",numOfRows, maxPages);
        List<OnbidPropertyDto> allProperties = new ArrayList<>();
        for(int page = 1;page <= maxPages; page++){
            List<OnbidPropertyDto> pageResult = getPropertyList(page,numOfRows);
            if(pageResult.isEmpty()){
                log.info("데이터가 더이상 없습니다. 페이지 번호 : {}", page);
                break;
            }
            allProperties.addAll(pageResult);
            log.info("페이지 {} 조회 완료 . 누적 : {}" ,page,allProperties.size());

        }
        log.info("전체 물건 조회 완료. 총 물건 수 : {}", allProperties.size());
        return allProperties;


    }
}
