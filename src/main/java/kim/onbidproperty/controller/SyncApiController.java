package kim.onbidproperty.controller;


import kim.onbidproperty.service.PropertySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//수동 동기화 api
@Slf4j
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncApiController {

    private  final PropertySyncService propertySyncService;
    @PostMapping
    public ResponseEntity<Map<String, Object>> syncProperties(
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "5") int maxPages
    ){
        log.info( " API 온비드 데이터 동기화 시작: numOfRows={}, maxPages={}", numOfRows, maxPages);
        int syncedCount = propertySyncService.syncProperties(numOfRows, maxPages);
        Map<String ,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "데이터 동기화 완료");
        response.put("syncedCount", syncedCount);
        return ResponseEntity.ok(response);
    }
}
