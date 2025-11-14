package kim.onbidproperty.enums;




public enum BidStatus {

PENDING("대기","입찰 시작전")
    , ONGOING("진행중" ,"현재 입찰 진행중")
    , COMPLETED("종료" ,"입찰마감"),
    FAILED("실패", "입찰실패");
private  final  String displayName;
private  final  String description;
BidStatus(String displayName, String description){
    this.displayName = displayName;
    this.description = description;

}
public String getDisplayName(){

    return  displayName;
}

public String getDescription(){
    return  description;
}

}
