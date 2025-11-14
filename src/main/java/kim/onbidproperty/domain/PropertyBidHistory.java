package kim.onbidproperty.domain;


import kim.onbidproperty.enums.BidStatus;
import kim.onbidproperty.enums.ResultStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PropertyBidHistory {
    private Long id;//    Pk
//    연결
    private  Long propertyId;// Property FK
    private  String cltrNo;// 물건번호
//    회차 정보
    private  String pbctNo;//    공매 번호 (회차별 고유)
    private String pbctSeq;//     공매 차수
    private String pbctDgr;//    공매 회차
//가격(회차별)
    private BigDecimal minBidPrice;//    최저입찰가
    private  BigDecimal appraisalPrice;//    감정가
    private  String feeRate;//    수수료율
//    일정 (회차별)
    private LocalDateTime auctionStartTime;//    입찰 시작일시
    private  LocalDateTime auctionEndTime;//    입찰 종료 일시
private LocalDateTime auctionResultTime;//개찰일시
//    입찰설정
    private BigDecimal depositRate ;//    입찰 보증금율
//    상태
    private BidStatus bidStatus;//    입찰상태(PENDING/ONGOING/COMPLETED/FAILED) String → BidStatus ENUM
    private ResultStatus resultStatus;//    낙찰상태(SUCCESS/FAIL/CANCEL) String → ResultStatus ENUM
//    메타
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
