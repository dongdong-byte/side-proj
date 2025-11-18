package kim.onbidproperty.domain;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//이 도메인은 사용자가 특정 물건의 특정 회차에 입찰한 정보를 저장합니다.
@Getter
@Setter
public class UserBid {
    private  Long id;
//    연결 정보
    private Long propertyId; // 물건 FK
    private Long historyId;  // 입찰 회차 FK

//    입찰자 정보
    private String bidderName;  // 입찰자명
    private String bidderPhone; // 연락처
    private String bidderEmail; // 이메일
//     입찰정보
    private BigDecimal bidAmount; // 입찰금액
    private LocalDateTime bidTime; // 입찰시간
//    결과
    private Boolean isWinner; // 낙찰 여부
//    메타
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
