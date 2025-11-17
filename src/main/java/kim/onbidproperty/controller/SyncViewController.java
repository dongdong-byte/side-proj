package kim.onbidproperty.controller;

import kim.onbidproperty.service.PropertySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncViewController {
    private  final PropertySyncService propertySyncService;
//    동기화 페이지
    @GetMapping
    public  String syncPage(){
        return "sync/index";
    }
//    동기화 실행
    @PostMapping
    public  String executeSync(
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "5") int maxPages,
            RedirectAttributes redirectAttributes
    ){
        log.info("데이터 동기화 시작: numOfRows={}, maxPages={}", numOfRows, maxPages);
        try {
            // ⬇️⬇️⬇️
            // ⚠️ 여기가 바로 '동기(Synchronous) 방식'으로 작동하는 부분입니다.
            // ⚠️ 서버는 이 줄이 끝날 때까지 멈춰서 기다립니다. (Blocking)
            int syncedCount = propertySyncService.syncProperties(numOfRows, maxPages);
            // ⬇️ 성공 로그 추가
            log.info("데이터 동기화 성공. 처리 건수: {}", syncedCount);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("code", "sync.completed");
            redirectAttributes.addFlashAttribute("syncedCount", syncedCount );
        } catch (Exception e) {
          log.error("데이터 동기화 실패", e);
          redirectAttributes.addFlashAttribute("success", false);
          redirectAttributes.addFlashAttribute("code", "sync.failed");
          redirectAttributes.addFlashAttribute("errorDetail", e.getMessage());
        }
return "redirect:/sync";

    }

}
