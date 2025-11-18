package kim.onbidproperty.dto.request.bid;

import kim.onbidproperty.domain.UserBid;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BidCreateRequest {
    private Long propertyId;
    private Long historyId;
    private String bidderName;
    private String bidderPhone;
    private String bidderEmail;
    private BigDecimal bidAmount;

    public UserBid toEntity(){
        UserBid userBid = new UserBid();
        userBid.setPropertyId(this.propertyId);
        userBid.setHistoryId(this.historyId);
        userBid.setBidderName(this.bidderName);
        userBid.setBidderPhone(this.bidderPhone);
        userBid.setBidderEmail(this.bidderEmail);
        userBid.setBidAmount(this.bidAmount);
        return userBid;
    }
}
