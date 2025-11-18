package kim.onbidproperty.dto.response.bid;


import kim.onbidproperty.domain.UserBid;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserBidResponse {
    private Long id;
    private Long propertyId;
    private Long historyId;
    private String bidderName;
    private String bidderPhone;
    private String bidderEmail;
    private BigDecimal bidAmount;
    private Boolean isWinner;
    private LocalDateTime createdAt;
    private LocalDateTime bidTime;
    public static UserBidResponse from(UserBid bid){
        return UserBidResponse.builder()
                .id(bid.getId())
                .propertyId(bid.getPropertyId())
                .historyId(bid.getHistoryId())
                .bidderName(bid.getBidderName())
                .bidderPhone(bid.getBidderPhone())
                .bidderEmail(bid.getBidderEmail())
                .bidAmount(bid.getBidAmount())
                .isWinner(bid.getIsWinner())
                .createdAt(bid.getCreatedAt())
                .bidTime(bid.getBidTime())
                .build();


    }
    // 전화번호 마스킹: 010-1234-5678 → 010-****-5678
    private static  String maskPhoneNumber(String phone){
        if(phone == null || phone.length() < 9){
            return phone;
        }
        // 하이픈 있는 경우: 010-1234-5678
        if(phone.contains("-")){
            String[] parts = phone.split("-");
            if(parts.length == 3){
                return parts[0] + "-****-" + parts[2];
            }
        }
        // 하이픈 없는 경우: 01012345678
        if(phone.length() == 11){
            return phone.substring(0,3) + "-****-" + phone.substring(7);
        }
        return phone;
    }
//  이메일 마스킹: example@email.com → ex***@email.com
    private static String maskEmail(String email){
        if(email == null || email.length() < 5){
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return parts[0] + "***@" + parts[1];
        }
        return parts[0].substring(0, 2) + "***@" + parts[1];

    }
}
