package kim.onbidproperty.service;

import kim.onbidproperty.client.OnbidApiClient;
import kim.onbidproperty.dto.api.OnbidApiRequest;
import kim.onbidproperty.dto.api.OnbidApiResponse;
import kim.onbidproperty.dto.api.OnbidPropertyDto;
import kim.onbidproperty.dto.api.PaginationRequest;
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

    /**
     * 온비드 API에서 물건 목록 조회 후 DTO로 변환
     */
    public OnbidApiResponse getPropertyList(OnbidApiRequest request) {
        log.info("온비드 API에서 물건 목록 조회: {}", request);
        try {
            String xmlResponse = onbidApiClient.fetchPropertyList(
                    request.getPageNo(),
                    request.getNumOfRows()
            );
            return parseXmlResponse(xmlResponse);
        } catch (Exception e) {
            log.error("API 호출 실패", e);
            return OnbidApiResponse.error("99", "API 호출 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * XML 응답을 OnbidApiResponse로 변환
     */
    private OnbidApiResponse parseXmlResponse(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // XXE 방지용 보안 설정
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

        // 결과 코드 확인
        NodeList resultCodeNode = document.getElementsByTagName("resultCode");
        if(resultCodeNode.getLength() > 0) {
            String resultCode = resultCodeNode.item(0).getTextContent();
            if(!"00".equals(resultCode)) {
                NodeList resultMsgNodes = document.getElementsByTagName("resultMsg");
                String resultMsg = resultMsgNodes.getLength() > 0
                        ? resultMsgNodes.item(0).getTextContent()
                        : "알 수 없는 에러";
                log.error("API 오류: {} - {}", resultCode, resultMsg);
                return OnbidApiResponse.error(resultCode, resultMsg);
            }
        }

        // 아이템 파싱
        NodeList itemNodes = document.getElementsByTagName("item");
        log.info("조회된 물건 수: {}", itemNodes.getLength());

        if(itemNodes.getLength() == 0) {
            return OnbidApiResponse.empty();
        }

        List<OnbidPropertyDto> properties = new ArrayList<>();
        for(int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            OnbidPropertyDto dto = parseItemElement(item);
            properties.add(dto);
        }

        return OnbidApiResponse.success(properties);
    }

    /**
     * item 요소를 DTO로 변환 (Builder 패턴 활용)
     */
    private OnbidPropertyDto parseItemElement(Element item) {
        OnbidPropertyDto dto = new OnbidPropertyDto();

        // 문자열 필드들
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

        // 타입 변환이 필요한 필드들 (헬퍼 메서드 사용)
        dto.setApslAsesAvgAmt(parseBigDecimal(item, "APSL_ASES_AVG_AMT"));
        dto.setMinBidPrc(parseBigDecimal(item, "MIN_BID_PRC"));
        dto.setIqryCnt(parseInteger(item, "IQRY_CNT"));

        return dto;
    }

    /**
     * XML 요소에서 텍스트 값 추출
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if(nodes.getLength() > 0) {
            String text = nodes.item(0).getTextContent();
            return text != null ? text.trim() : null;
        }
        return null;
    }

    /**
     * BigDecimal 파싱 헬퍼 메서드
     */
    private BigDecimal parseBigDecimal(Element item, String tagName) {
        String value = getElementText(item, tagName);
        if(value != null && !value.isEmpty()) {
            try {
                // 숫자가 아닌 문자 제거 (콤마, 공백 등)
                String numericValue = value.replaceAll("[^0-9]", "");
                return numericValue.isEmpty() ? BigDecimal.ZERO : new BigDecimal(numericValue);
            } catch (NumberFormatException e) {
                log.warn("{} 파싱 실패: {}", tagName, value);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Integer 파싱 헬퍼 메서드
     */
    private Integer parseInteger(Element item, String tagName) {
        String value = getElementText(item, tagName);
        if(value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                log.warn("{} 파싱 실패: {}", tagName, value);
                return 0;
            }
        }
        return 0;
    }

    /**
     * 전체 페이지 조회 (페이지네이션)
     */
    public OnbidApiResponse getAllProperties(PaginationRequest pagination) {
        log.info("온비드 API에서 전체 물건 목록 조회: {}", pagination);
        List<OnbidPropertyDto> allProperties = new ArrayList<>();

        for(int page = 1; page <= pagination.getMaxPages(); page++) {
            OnbidApiRequest request = OnbidApiRequest.of(page, pagination.getNumOfRows());
            OnbidApiResponse response = getPropertyList(request);

            // API 오류 처리
            if(!response.isSuccess()) {
                log.error("API 오류 발생: {} - {}", response.getResultCode(), response.getResultMsg());
                break;
            }

            // 데이터가 없으면 종료
            if(response.getItems().isEmpty()) {
                log.info("데이터가 더 이상 없습니다. 페이지 번호: {}", page);
                break;
            }

            allProperties.addAll(response.getItems());
            log.info("페이지 {} 조회 완료. 누적: {}", page, allProperties.size());

            // 선택적: API 호출 간 딜레이 (과도한 요청 방지)
            try {
                Thread.sleep(100); // 100ms 딜레이
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("페이지 조회 중 인터럽트 발생");
                break;
            }
        }

        log.info("전체 물건 조회 완료. 총 물건 수: {}", allProperties.size());
        return OnbidApiResponse.success(allProperties);
    }
}