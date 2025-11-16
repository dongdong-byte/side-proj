package kim.onbidproperty.mapper;


import kim.onbidproperty.domain.UserBid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBidMapper {
//    CRUD
//    1.READ
//    ID로 조회

    UserBid selectById(Long id);
//    특정물건의 모든 입찰조회
    List<UserBid> selectByPropertyId(Long propertyId);
//    특정회차의 모든입찰조회

    List<UserBid> selectBidsByHistoryID(Long historyId);
//    특정물건의 특정 회차 입찰조회(복합 인덱스 사용)
    List<UserBid> selectBidsByPropertyAndHistory(
            @Param("propertyId") Long propertyId,
            @Param("historyId") Long historyId
    );
//    입찰자명으로 조회
    List<UserBid> selectByBidderName (String bidderName);
//특정 회차의 최고 입찰조회
    UserBid selectHighestBidderByHistoryId(Long historyId);

//    2.create
    int insertBid(UserBid userBid);
//    3.update
    int updateBid(UserBid userBid);
//    낙찰여부 변경
    int updateWinnerStatus(@Param("id") Long id , @Param("isWinner") Boolean isWinner);
//    4.delete
    int deleteBid(Long id);
//    특정물건의 모든 입찰 삭제
    int deleteBidsByPropertyId(Long propertyId);


    List<UserBid> selectWinningBids(Long historyId);
}
