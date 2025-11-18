package kim.onbidproperty.dto.request.purchase;


import kim.onbidproperty.domain.Purchase;
import kim.onbidproperty.enums.PurchaseStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PurchaseCreateRequest {
    private  Long propertyId;
    private Long userId;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseTime;
    private PurchaseStatus status;

    public Purchase toEntity(){
        Purchase purchase = new Purchase();
        purchase.setUserId(this.userId);
        purchase.setPropertyId(this.propertyId);
        purchase.setPurchasePrice(this.purchasePrice);
        purchase.setPurchaseTime(this.purchaseTime);
        purchase.setStatus(this.status);
        return purchase;
    }
}
