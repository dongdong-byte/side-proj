package kim.onbidproperty.mapper;

import kim.onbidproperty.domain.PropertyBidHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyBidHistoryMapper {
//    CRUD
//    1.Read:조회
//    특정물건의 전체 회차 조회
    List<PropertyBidHistory> selectHistoriesByPropertyId( Long propertyId);
//     특정 물건의 특정회차 조회
    PropertyBidHistory selectHistoryByPropertyIdAndPbctNo(
            @Param("propertyId") Long propertyId,
            @Param("pbctNo") String pbctNo
    );
//    ID로 조회

    PropertyBidHistory selectHistoryById(Long id);
//    물건번호르 전체 회차 조회
    List<PropertyBidHistory> selectHistoriesByCltrNo(String cltrNo);
//    입찰 상태별 조회
    List<PropertyBidHistory> selectHistoriesByBidStatus(@Param(("bidStatus")) String bidStatus);
//    진행중인 회차 조회 (현재 시간 기준)
    List<PropertyBidHistory> selectOngoingHistories();
//    특정 물건의 최신 회차 조회
    PropertyBidHistory selectLatestHistoryByPropertyId(Long propertyId);
    //    2.CREATE
//    입찰 등록
    int insertHistory(PropertyBidHistory history);
    //    3.UPDATE
//    전체 수정
    int updateHistory(PropertyBidHistory history);
//    부분수정
    int patchHistory(PropertyBidHistory history);
//    입찰 상태 변경
    int updateBidStatus(@Param("id") Long id, @Param("bidStatus") String bidStatus);
//    낙찰상태 변경
    int updateResultStatus(@Param("id") Long id,@Param("resultStatus") String resultStatus);
//    삭제
    int deleteHistory(Long id);
//    특정 물건의 모든 회차 삭제
    int deleteHistoriesByPropertyId(Long propertyId);


}
