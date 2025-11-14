package kim.onbidproperty.enums;

public enum PropertyStatus {
    AVAILABLE("판매중" ,"입찰 가능한 물건"),
    SOLD("판매완료","낙찰된 물건" ),
    PENDING("대기" ,"입찰 대기중인 물건"),
    CANCELLED("취소" ,"입찰 취소된 물건");
    private final String displayName;
    private final String description;

    PropertyStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;

    }

    public String getDisplayName() {

        return displayName;
    }

    public String getDescription() {
        return description;
    }


}
