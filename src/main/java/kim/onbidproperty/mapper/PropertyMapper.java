package kim.onbidproperty.mapper;


import kim.onbidproperty.domain.Property;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyMapper {
    //CRUD
//    1.READ
    //    전체 물건 조회
    List<Property> selectAllProperties();
//    물건 ID로 조회
    Property selectPropertyById(Long id);
//    물건번호(cltr_no) 로 조회

    Property selectPropertyByCltrNo(String cltrNo);
//    물건검색 (물건명 , 위치)

    List<Property> searchProperties( @Param("keyword") String keyword);
//    상태별 물건조회
    List<Property> selectPropertiesByStatus(@Param("status") String status);
//    진행중인 경매 물건 조회
    List<Property> selectOngoingAuction();
    //    2.CREATE
//    물건등록
    int insertProperty(Property property);
//    3.UPDATE
//    물건 전체 수정(모든 필드만)
    int updateProperty(Property property);
//    부분수정 (null 아 아닌 필드만)
    int patchProperty(Property property);
//    특정 필드만 수정하는 전용 메서드들
//    물건 상태 변경
    int updatePropertyStatus(@Param("id") Long id, @Param("status") String status);

//    조회수 증가
    int incrementViewCount(Long id);
//    입찰수 증가
    int incrementBidCount(Long id);
//    4.DELETE
//    물건 삭제
    int deleteProperty(Long id);
//    APi 동기화시간 업데이트
    int updateSyncedAt(Long id);
}
