package kim.onbidproperty.service;

import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.enums.BidStatus;
import kim.onbidproperty.enums.ResultStatus;
import kim.onbidproperty.exception.DuplicateResourceException;
import kim.onbidproperty.exception.ResourceNotFoundException;
import kim.onbidproperty.mapper.PropertyBidHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyBidHistoryService {
    private final PropertyBidHistoryMapper historyMapper;

    // 특정 물건의 전체 회차 조회
    public List<PropertyBidHistory> getHistoriesByPropertyId(Long propertyId) {
        log.info("물건 회차 조회 : propertyId {}", propertyId);
        return historyMapper.selectHistoriesByPropertyId(propertyId);
    }

    // 특정 물건의 최신 회차 조회
    public PropertyBidHistory getLatestHistory(Long propertyId) {
        log.info("최신 회차 조회 : propertyId {}", propertyId);

        PropertyBidHistory history = historyMapper.selectLatestHistoryByPropertyId(propertyId);
        if (history == null) {
            throw new ResourceNotFoundException("회차", "propertyId", propertyId);
        }

        return history;
    }

    // ID로 조회
    public PropertyBidHistory getHistoryById(Long id) {
        log.info("회차 ID로 조회 : {}", id);

        PropertyBidHistory history = historyMapper.selectHistoryById(id);
        if (history == null) {
            throw new ResourceNotFoundException("회차", "ID", id);
        }

        return history;
    }

    // 물건번호로 전체 회차 조회
    public List<PropertyBidHistory> getHistoriesByCltrNo(String cltrNo) {
        log.info("물건번호로 회차 조회 : {}", cltrNo);
        return historyMapper.selectHistoriesByCltrNo(cltrNo);
    }

    // 입찰 상태별 조회 (ENUM 사용)
    public List<PropertyBidHistory> getHistoriesByBidStatus(BidStatus bidStatus) {
        log.info("입찰 상태별 조회 : {}", bidStatus);
        return historyMapper.selectHistoriesByBidStatus(bidStatus.name());
    }

    // 진행중인 회차 조회
    public List<PropertyBidHistory> getOngoingHistories() {
        log.info("진행중인 회차 조회");
        return historyMapper.selectOngoingHistories();
    }

    // 회차 등록
    @Transactional
    public Long createHistory(PropertyBidHistory history) {
        log.info("회차 등록 : propertyId {}, pbctNo {}",
                history.getPropertyId(), history.getPbctNo());

        // 중복 체크 (같은 물건의 같은 공매번호)
        PropertyBidHistory existing = historyMapper.selectHistoryByPropertyIdAndPbctNo(
                history.getPropertyId(), history.getPbctNo()
        );

        if (existing != null) {
            throw new DuplicateResourceException(
                    "이미 존재하는 회차입니다. propertyId: " + history.getPropertyId() +
                            ", pbctNo: " + history.getPbctNo()
            );
        }

        historyMapper.insertHistory(history);
        log.info("회차 등록 완료 : {}", history.getId());

        return history.getId();
    }

    // 회차 전체 수정
    @Transactional
    public void updateHistory(PropertyBidHistory history) {
        log.info("회차 전체 수정 : {}", history.getId());

        // A 방식: 존재 여부 먼저 확인
        PropertyBidHistory existing = historyMapper.selectHistoryById(history.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("회차", "ID", history.getId());
        }

        historyMapper.updateHistory(history);
        log.info("회차 수정 완료 : {}", history.getId());
    }

    // 회차 부분 수정
    @Transactional
    public void patchHistory(PropertyBidHistory history) {
        log.info("회차 부분 수정 : {}", history.getId());

        // A 방식: 존재 여부 먼저 확인
        PropertyBidHistory existing = historyMapper.selectHistoryById(history.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("회차", "ID", history.getId());
        }

        historyMapper.patchHistory(history);
        log.info("회차 부분 수정 완료 : {}", history.getId());
    }

    // 입찰 상태 변경 (ENUM 사용)
    @Transactional
    public void updateBidStatus(Long id, BidStatus bidStatus) {
        log.info("입찰 상태 변경 : {} -> {}", id, bidStatus);

        // A 방식: 존재 여부 먼저 확인
        PropertyBidHistory existing = historyMapper.selectHistoryById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("회차", "ID", id);
        }

        historyMapper.updateBidStatus(id, bidStatus.name());
        log.info("입찰 상태 변경 완료");
    }

    // 낙찰 상태 변경 (ENUM 사용)
    @Transactional
    public void updateResultStatus(Long id, ResultStatus resultStatus) {
        log.info("낙찰 상태 변경 : {} -> {}", id, resultStatus);

        // A 방식: 존재 여부 먼저 확인
        PropertyBidHistory existing = historyMapper.selectHistoryById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("회차", "ID", id);
        }

        historyMapper.updateResultStatus(id, resultStatus.name());
        log.info("낙찰 상태 변경 완료");
    }

    // 회차 삭제
    @Transactional
    public void deleteHistory(Long id) {
        log.info("회차 삭제 : {}", id);

        // A 방식: 존재 여부 먼저 확인
        PropertyBidHistory existing = historyMapper.selectHistoryById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("회차", "ID", id);
        }

        historyMapper.deleteHistory(id);
        log.info("회차 삭제 완료 : {}", id);
    }

    // 특정 물건의 모든 회차 삭제
    @Transactional
    public void deleteHistoriesByPropertyId(Long propertyId) {
        log.info("물건의 모든 회차 삭제 : propertyId {}", propertyId);

        int result = historyMapper.deleteHistoriesByPropertyId(propertyId);
        log.info("회차 삭제 완료. 삭제된 개수 : {}", result);
    }
}