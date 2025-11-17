package kim.onbidproperty.domain;

import kim.onbidproperty.enums.PurchaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Purchase {
    private Long id;
    private Long userId;
    private Long propertyId;
    private BigDecimal purchasePrice;
    private  LocalDateTime purchaseTime;
    private PurchaseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
