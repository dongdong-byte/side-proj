package kim.onbidproperty.enums;

public enum SalesType {
    AUCTION("경매" ," 경매 방식 판매"),
    DIRECT("직접구매" ,"즉시 구매 가능");

    private final String displayName;
    private final String description;
    SalesType(String displayName ,String description) {
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
