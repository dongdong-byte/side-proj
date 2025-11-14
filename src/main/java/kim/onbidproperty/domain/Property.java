package kim.onbidproperty.domain;


import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.enums.SalesType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Property {
    private Long id;
//    Api 핵심 식별자
    private String cltrNo;//    식별번호
    private  String cltrMnmtNo;//    물건 관리 번호
    private  String plnmNo;//    공고 번호
//    물건 기본정보
    private String name; //    물건명
    private  String goodsDescription;//    물건 정보
    private String category;//    물건 분류
//    위치 정보
    private String location;//    지번 주소
    private String roadAddress;//    도로명 주소
    private  String pnu;//    토지 고유 번호
    //     가격 정보 (최신 회차 기준)
    private BigDecimal appraisalPrice;//   감정가
    private BigDecimal minBidPrice;//    최저 입찰가
    private BigDecimal currentPrice;//    현재가
    //    입찰정보
    private String bidMethod;//    입찰방법
    private LocalDateTime auctionStartTime;//    입찰 시작일시
    private  LocalDateTime auctionEndTime;//    입찰 마감일시

//    상태
    private  String pbctStatus;//    물건상태
    private SalesType saleType;//    판매 방식(AUCTION/DIRECT) String → SaleType ENUM
    private PropertyStatus status;//    시스템상태(AVAILABLE/SOLD/...) String → PropertyStatus ENUM
//    통계
    private Integer viewCount;//    조회수
    private Integer bidCount;//    입찰수
//    이미지
    private String imageUrl;//    물건 이미지
//    메타
    private  LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime syncedAt;//    api마지막 동기화 시간
}
