package kim.onbidproperty.service;


import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.domain.UserBid;
import kim.onbidproperty.exception.ResourceNotFoundException;
import kim.onbidproperty.mapper.PropertyBidHistoryMapper;
import kim.onbidproperty.mapper.PropertyMapper;
import kim.onbidproperty.mapper.UserBidMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBidService {
    private  final UserBidMapper userBidMapper;
    private  final PropertyMapper propertyMapper;
    private  final PropertyBidHistoryMapper historyMapper;
//CRUD
//    1.READ

    //    입찰조회
    public UserBid getBidById(Long id){
        log.info("입찰조회 : id ={}",id);
        UserBid bid = userBidMapper.selectById(id);
        if(bid == null){
            log.error("입찰조회 실패 : id ={}", id);
            throw new RuntimeException("입찰조회 실패");
        }
        return bid;
    }

//    특정 물건의 모든 입찰조회
    public List<UserBid> getBidsByPropertyId(Long propertyId){
        log.info(" 물건별 입찰조회 : propertyId ={}", propertyId);
        return userBidMapper.selectByPropertyId(propertyId);

    }
//    특정 회차의 모든 입찰 조회
    public List<UserBid> getBidsByHistoryId(Long historyId){
        log.info("회차별 입찰조회 : historyId = {}", historyId);
        return userBidMapper.selectBidsByHistoryID(historyId);
    }
//    특정 물건의 특정 회차 입찰조회
    public List<UserBid> getBidsByPropertyAndHistory(Long propertyId , Long historyId){
        log.info("물건별 회차별 입찰조회 : propertyId ={}, historyId ={}", propertyId, historyId);
        return  userBidMapper.selectBidsByPropertyAndHistory(propertyId,historyId);
    }
//    특정 회차의 최고 입찰조회
public UserBid getHighestBid(Long historyId){

    log.info("회차별 최고 입찰 조회 : historyId ={}", historyId);
    return userBidMapper.selectHighestBidderByHistoryId(historyId);

}
//    낙찰된 입찰 조회
public  List<UserBid> getWinningBids(Long historyId){
        log.info("낙찰된 입찰 목록 조회 : historyId ={}", historyId);
//     문제 :   메서드는 historyId를 파라미터로 받지만, userBidMapper를 호출할 때 이 historyId를 전달하지 않습니다.
//
//결과: "특정 회차"의 낙찰자가 아닌, "모든 회차"의 낙찰자가 조회됩니다.
        return  userBidMapper.selectWinningBids(historyId);
}
//2. create 입찰등록 ( 핵심 비즈니스 목록)
    @Transactional
    public Long createBid(UserBid userBid){
        log.info("입찰 등록 : propertyId ={} ,historyId ={},amount={} ",
                userBid.getPropertyId(),userBid.getHistoryId(),userBid.getBidAmount());
//        1.물건 존재 확인
        if(propertyMapper.selectPropertyById(userBid.getPropertyId()) == null){
            throw  new ResourceNotFoundException("물건 " , "ID" , userBid.getPropertyId());
        }
//        2.회차 존재 확인 및 정보 조회
        PropertyBidHistory history = historyMapper.selectHistoryById(userBid.getHistoryId());
        if(history == null){
            throw new ResourceNotFoundException("입찰회차", "ID", userBid.getHistoryId());
        }
//        3.입찰 마감 시간 확인
        if(history.getAuctionEndTime() != null && LocalDateTime.now().isAfter(history.getAuctionEndTime())){
            throw new RuntimeException("입찰이 마감되었습니다.");
        }
//        4.최저 입찰가 이상인지 확인
        if(history.getMinBidPrice() != null && userBid.getBidAmount().compareTo(history.getMinBidPrice()) < 0){
            throw new IllegalArgumentException(String.format("입찰금액이 최저입찰가(%s원) 이상이어야 합니다.", history.getMinBidPrice()));
        }
//         5.입찰시간 설정 (조건 없이 항상 현재 시간)

            userBid.setBidTime(LocalDateTime.now());

//        6.기본값 설정(조건없이 항상 flase)

            userBid.setIsWinner(false);

//        7. 입찰 지정
        userBidMapper.insertBid(userBid);
//        8. 물건의 입찰 수 증가
        propertyMapper.incrementBidCount(userBid.getPropertyId());
        log.info("입찰 등록 성공 : propertyId ={} ,historyId ={},amount={} ",
                userBid.getPropertyId(),userBid.getHistoryId(),userBid.getBidAmount());
        return  userBid.getId();

    }
//    3.update 입찰수정
    @Transactional
    public void updateBid(UserBid userBid){
        log.info("입찰 수정 : id ={}, propertyId ={}, historyId ={}, amount ={}",
                userBid.getId(), userBid.getPropertyId(), userBid.getHistoryId(), userBid.getBidAmount());
        UserBid existing = userBidMapper.selectById(userBid.getId());
        if(existing == null){
            throw new ResourceNotFoundException("입찰", "ID", userBid.getId());
        }
        userBidMapper.updateBid(userBid);
        log.info("입찰 수정 성공 : id ={}, propertyId ={}, historyId ={}, amount ={}",
                userBid.getId(), userBid.getPropertyId(), userBid.getHistoryId(), userBid.getBidAmount());
    }
//    낙찰자 지정
    @Transactional
    public  void  setWinner(Long id){
        log.info("낙찰자 지정 : id ={}", id);
        UserBid bid = userBidMapper.selectById(id);
        if(bid == null){
            throw new ResourceNotFoundException("입찰", "ID", id);
        }
        userBidMapper.updateWinnerStatus(id, true);
        log.info("낙찰자 지정 성공 : id ={}", id);
    }
//    입찰 삭제

    @Transactional
    public void deleteBid(Long id){
        log.info("입찰 삭제 : id ={}", id);
        UserBid bid = userBidMapper.selectById(id);
        if(bid == null){
            throw new ResourceNotFoundException("입찰", "ID", id);
        }
        userBidMapper.deleteBid(id);
        log.info("입찰 삭제 성공 : id ={}", id);
    }
}
