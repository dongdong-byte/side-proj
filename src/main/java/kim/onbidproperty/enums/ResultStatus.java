package kim.onbidproperty.enums;



public enum ResultStatus {
    SUCCESS("낙찰 ", "낙찰 성공"),
    FAIL("유찰", "낙찰 실패"),
    CANCEL("취소", "입찰 취소");
    private final String displayName;
    private final String description;

    ResultStatus(String displayName, String description) {
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