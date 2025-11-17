package kim.onbidproperty.controller;

import kim.onbidproperty.domain.UserBid;
import kim.onbidproperty.service.UserBidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidApiController {

    private final UserBidService userBidService;

    // 입찰 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserBid> getBid(@PathVariable Long id) {
        log.info("API 입찰 조회: {}", id);

        return ResponseEntity.ok(userBidService.getBidById(id));
    }

    // 특정 물건의 모든 입찰 조회
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<UserBid>> getBidsByProperty(@PathVariable Long propertyId) {
        log.info("API 물건별 입찰 조회: {}", propertyId);
        List<UserBid> bids = userBidService.getBidsByPropertyId(propertyId);
        return ResponseEntity.ok(bids);
    }

    // 특정 회차의 모든 입찰 조회
    @GetMapping("/history/{historyId}")
    public ResponseEntity<List<UserBid>> getBidsByHistory(@PathVariable Long historyId) {
        log.info("API 회차별 입찰 조회: {}", historyId);
        List<UserBid> bids = userBidService.getBidsByHistoryId(historyId);
        return ResponseEntity.ok(bids);
    }

    // 특정 회차의 최고 입찰 조회
    @GetMapping("/history/{historyId}/highest")
    public ResponseEntity<UserBid> getHighestBid(@PathVariable Long historyId) {
        log.info("API 최고 입찰 조회: {}", historyId);
        UserBid highestBid = userBidService.getHighestBid(historyId);
        return ResponseEntity.ok(highestBid);
    }

    // 입찰 등록
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBid(@RequestBody UserBid userBid) {
        log.info("API 입찰 등록: propertyId={}, historyId={}, amount={}",
                userBid.getPropertyId(), userBid.getHistoryId(), userBid.getBidAmount());

        Long bidId = userBidService.createBid(userBid);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", "bid.created");
        response.put("data", Map.of("bidId",bidId));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 입찰 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBid(@PathVariable Long id,
                                                         @RequestBody UserBid userBid) {
        log.info("API 입찰 수정: {}", id);

        userBid.setId(id);
        userBidService.updateBid(userBid);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", "bid.updated");

        return ResponseEntity.ok(response);
    }

    // 낙찰자 지정
    @PatchMapping("/{id}/winner")
    public ResponseEntity<Map<String, Object>> setWinner(@PathVariable Long id) {
        log.info("API 낙찰자 지정: {}", id);

        userBidService.setWinner(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", "bid.winnerAssigned");

        return ResponseEntity.ok(response);
    }

    // 입찰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBid(@PathVariable Long id) {
        log.info("API 입찰 삭제: {}", id);

        userBidService.deleteBid(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("code", "bid.deleted");

        return ResponseEntity.ok(response);
    }
}