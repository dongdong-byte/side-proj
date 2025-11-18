package kim.onbidproperty.dto.request.purchase;

import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.enums.PurchaseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseUpdateStatusRequest {
    private PurchaseStatus status;
}
