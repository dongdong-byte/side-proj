package kim.onbidproperty.mapper;

import kim.onbidproperty.domain.Purchase;

import kim.onbidproperty.enums.PurchaseStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseMapper {
//    CRUD
//    1.READ
//    ID로 조회
    Purchase selectById(Long id);
//    사용자 ID로 조회
    List<Purchase> selectByUserId( Long userId);
//     property ID로 조회
    List<Purchase> selectByPropertyId(Long propertyId);
//    userID 하고  property ID로 조회 유니크 제약 조건이 없다면(예: 한 사용자가 같은 물건에 대해 'FAILED' 기록과 'COMPLETED' 기록을 모두 가질 수 있다면) 이 쿼리는 2개 이상의 결과를 반환할 수 있으며,
//    수정 제안 (List로 받기)
    List<Purchase> selectByUserIdAndPropertyId(@Param("userId") Long userId ,
                                         @Param("propertyId") Long propertyId);
    //    2.create : 등록
    void  insertPurchase(Purchase purchase);
//    3.update :  상태 수정
    void  updateStatus(@Param("id") Long id ,@Param("status") PurchaseStatus status);
//    4. delete : 삭제
    void  deletePurchase(Long id);

}
