package kim.onbidproperty.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTaskService {
    private final PropertySyncService propertySyncService;
    @Scheduled(cron = "0 0 9 * * *")
    public void syncProperty() {
        log.info("스케줄러: 온비드 데이터 자동 동기화 시작");
        try {
            int syncedCount = propertySyncService.syncProperties(10, 10);
            log.info("스케줄러: 동기화 완료 - 총 {} 건 처리", syncedCount);
        } catch (Exception e) {
            log.error("스케줄러: 동기화 실패 - {}", e.getMessage());
        }

        log.info("========================================");
    }
}
