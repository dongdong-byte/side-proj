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
            int syncedCount = propertySyncService.syncProperties(numOfRows, maxPages);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "데이터 동기화 완료! 처리된 물건 수 " + syncedCount);

        } catch (Exception e) {
          log.error("데이터 동기화 실패", e);
          redirectAttributes.addFlashAttribute("success", false);
          redirectAttributes.addFlashAttribute("message", "데이터 동기화 실패: " + (e.getMessage() != null ? e.getMessage() : "알 수 없는 오류 발생"));
        }
return "redirect:/sync";

    }

}
