package kim.onbidproperty.service;

import kim.onbidproperty.domain.Purchase;
import kim.onbidproperty.enums.PurchaseStatus;
import kim.onbidproperty.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final MessageService messageService;
//    구매등록
    @Transactional
    public  Long createPurchase(Purchase purchase){
        purchaseMapper.insertPurchase(purchase);
        return purchase.getId();
    }
//    ID로 구매조회
    public Purchase findById(Long id){
        Purchase purchase = purchaseMapper.selectById(id);
        if(purchase == null){
            throw new IllegalStateException(
                    messageService.getMessage("purchase", "notFound")
            );
        }
        return purchase;
    }
//    사용자 ID로 구매 목록조회
    public List<Purchase> findByUserId(Long userId){
        return purchaseMapper.selectByUserId(userId);
    }
    //    물건 ID로 구매 목록조회
    public List<Purchase> findByPropertyId(Long propertyId){
        return purchaseMapper.selectByPropertyId(propertyId);
    }
    //    사용자 ID와 물건 ID로 구매 목록조회
    public List<Purchase> findByUserIdAndPropertyId(Long userId, Long propertyId){
        return purchaseMapper.selectByUserIdAndPropertyId(userId,propertyId);
    }
//    구매 상태 변경

    @Transactional
    public void updateStatus(Long id , PurchaseStatus status) {
        findById(id); // 1. 존재하는지 확인 (Good)
        int result = purchaseMapper.updateStatus(id, status);
        if (result == 0) {
            throw new IllegalStateException(
                    messageService.getMessage("purchase", "updateFailed")
            );

        }
    }
//    구매 취소 (삭제)
    @Transactional
    public void deletePurchase(Long id){
        findById(id);
        int result = purchaseMapper.deletePurchase(id);
        if (result == 0) {
            throw new IllegalStateException(
                    messageService.getMessage("purchase", "deleteFailed")
            );
        }
    }
}
