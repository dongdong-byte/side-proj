package kim.onbidproperty.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PurchaseStatus {
    PENDING("대기" ,"구매 또는 입찰이 접수되었지만 아직 처리전입니다."),
    COMPLETED("완료","구매 또는 낙찰이 성공적으로 완료되었습니다."),
    CANCELLED("취소" ,"사용자 또는 시스템에 의해 취소되었습니다."),
    FAILED("실패" ,"결제 실패 또는 낙찰 실패 등으로 구매 처리가 되지 못했습니다."),
    REFUNDED("환불" ,"구매가 취소되어 금액이 환불되었습니다.");

    private final String displayName;
    private final String description;
    PurchaseStatus(String displayName ,String description){
        this.displayName =displayName;
        this.description =description;
    }
    public  String getDisplayName(){
        return displayName;
    }
    public String getDescription(){
        return description;
    }
//    DB에 저장하거나 json요청시 문자열 값을 그대로 쓰기 위한 메서드
@JsonValue
public  String toValue(){
        return  name();
}


//    JSON → Enum 변환 시(대소문자 무관) 안전하게 처리
    @JsonCreator
    public  static  PurchaseStatus from(String value){
        if(value == null || value.trim().isEmpty()){
            throw  new IllegalArgumentException("PurchaseStatus 값은 null이거나 비어있을 수 없습니다. " );
        }
        for(PurchaseStatus status : PurchaseStatus.values()){
            if(status.name().equalsIgnoreCase(value.trim())){
                return status;
            }

        }
        throw  new IllegalArgumentException("알 수 없는 PurchaseStatus 값입니다. : " +value);

    }

}
