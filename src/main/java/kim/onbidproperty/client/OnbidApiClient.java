package kim.onbidproperty.client;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;




//외부 api호출
@Slf4j
@Component
public class OnbidApiClient {
    private final WebClient webClient ;
//    생성자 추가
// 스프링이 WebClientConfig에서 만든 WebClient Bean을
// 이 생성자의 파라미터로 자동으로 넣어줍니다.
public OnbidApiClient(WebClient webClient) {
    this.webClient = webClient;
}

    @Value("${onbid.api.service-key}")
    private String serviceKey;

    private static final String BASE_URL
            = "http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList";

//    온비드 공매물건 물건 조회
public  String fetchPropertyList(int pageNo, int numOfRows){
    log.info("온비드 api호출 : pageNo={}, numOfRows={}" , pageNo, numOfRows);
    String uri = UriComponentsBuilder.fromUriString(BASE_URL)
            .queryParam("serviceKey", serviceKey)
            .queryParam("numOfRows", numOfRows)
            .queryParam("pageNo", pageNo)
            .queryParam("DPSL_MTD_CD", "0001")
            .build(true)// .build(false) 대신 .build() 사용 (알아서 인코딩해줌)
            .toUriString();
    log.debug("API URL : {}" ,uri);

    return  sendRequest(uri);



}



    //필터를 적용한 물건 목록 조회
    public  String fetchPropertyListWithFilter(
            int pageNo, int numOfRows
            ,String sido ,String sgg,
            String emd ,
            Long priceFrom, Long priceTo,
            String cltrNm
    ){
        log.info("온비드 api 호출 : pageNo={}, numOfRows={}, sido={}, sgg={}, emd={}, priceFrom={}, priceTo={}, cltrNm={}",
                pageNo, numOfRows, sido, sgg, emd, priceFrom, priceTo, cltrNm);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("DPSL_MTD_CD", "0001");
        addIfNotEmpty(uriBuilder, "SIDO", sido);
        addIfNotEmpty(uriBuilder, "SGG", sgg);
        addIfNotEmpty(uriBuilder , "EMD" ,emd);
        addIfNotNull(uriBuilder , "GOODS_PRICE_FROM" ,priceFrom);
        addIfNotNull(uriBuilder , "GOODS_PRICE_TO" ,priceTo);
        addIfNotEmpty(uriBuilder , "CLTR_NM" ,cltrNm);


        String uri = uriBuilder.build(true).toUriString();
        return sendRequest(uri);
    }

    private void addIfNotNull(UriComponentsBuilder builder, String key, Long value) {
        if(value != null){
            builder.queryParam(key, value);
        }
    }

    private void addIfNotEmpty(UriComponentsBuilder builder, String key, String value) {
        if(value != null && !value.isEmpty()){
            builder.queryParam(key, value);
        }
    }

    private String sendRequest(String uri) {
        try {
            String response = webClient.get()
                    .uri(uri)
                    .header("Content-Type", "application/xml")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("온비드 API 조회 성공");
            return response;

        } catch (Exception e) {
            log.error("온비드 api 필터 조회 실패 : {}", e.getMessage());
            throw new RuntimeException("온비드 api 필터 조회 실패", e);
        }
    }
}
