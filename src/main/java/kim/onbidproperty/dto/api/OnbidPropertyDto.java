package kim.onbidproperty.dto.api;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OnbidPropertyDto {
    private String cltrNo;           // 물건번호
    private String cltrMnmtNo;       // 물건관리번호
    private String plnmNo;           // 공고번호
    private String cltrNm;           // 물건명
    private String goodsNm;          // 물건정보
    private String ctgrFullNm;       // 물건분류
    private String ldnmAdrs;         // 지번주소
    private String nmrdAdrs;         // 도로명주소
    private String ldnmPnu;          // 토지고유번호
    private BigDecimal apslAsesAvgAmt;   // 감정가 ✅ BigDecimal로 변경
    private BigDecimal minBidPrc;        // 최저입찰가 ✅ BigDecimal로 변경
    private String bidMtdNm;         // 입찰방법
    private String pbctBegnDtm;      // 입찰시작일시
    private String pbctClsDtm;       // 입찰마감일시
    private String pbctCltrStatNm;   // 물건상태
    private Integer iqryCnt;         // 조회수
    private String feeRate;          // 수수료율
    private String pbctNo;           // 공매번호
    private String pbctSeq;          // 공매차수
    private String pbctDgr;          // 공매회차
    private String tdpsRt;           // 입찰보증금율
    private String pbctExctDtm;      // 개찰일시
}
