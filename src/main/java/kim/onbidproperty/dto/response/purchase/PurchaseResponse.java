package kim.onbidproperty.dto.response.purchase;

import kim.onbidproperty.domain.Purchase;
import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.enums.PurchaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseResponse {
    private Long id;
    private Long userId;
    private Long propertyId;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseTime;
    private PurchaseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static PurchaseResponse from(Purchase purchase){
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .userId(purchase.getUserId())
                .propertyId(purchase.getPropertyId())
                .purchasePrice(purchase.getPurchasePrice())
                .purchaseTime(purchase.getPurchaseTime())
                .status(purchase.getStatus())
                .createdAt(purchase.getCreatedAt())
                .updatedAt(purchase.getUpdatedAt())
                .build();

    }

}
