package kim.onbidproperty.controller;

import jakarta.servlet.http.HttpSession;
import kim.onbidproperty.domain.Property;
import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.domain.Purchase;
import kim.onbidproperty.domain.User;
import kim.onbidproperty.domain.UserBid;
import kim.onbidproperty.dto.request.bid.BidCreateRequest;
import kim.onbidproperty.dto.response.bid.UserBidResponse;
import kim.onbidproperty.enums.PurchaseStatus;
import kim.onbidproperty.service.MessageService;
import kim.onbidproperty.service.PropertyBidHistoryService;
import kim.onbidproperty.service.PropertyService;
import kim.onbidproperty.service.PurchaseService;
import kim.onbidproperty.service.UserBidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidViewController {

//    controller-> return에 화면을 받겠다.
//    restcontroller -> return에 값을 받겠다.
    private final UserBidService userBidService;
    private final PropertyService propertyService;
    private final PropertyBidHistoryService historyService;
    private final PurchaseService purchaseService;
    private final MessageService messageService; // ✅ 추가

    // 입찰 폼 페이지-로그인 검증 추가
    @GetMapping("/new")
    public String bidForm(@RequestParam Long propertyId,
                          @RequestParam Long historyId,
                          Model model,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        // 로그인 체크
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/users/login";
        }

        log.info("입찰 폼 페이지: propertyId={}, historyId={}", propertyId, historyId);

        Property property = propertyService.getPropertyById(propertyId);
        PropertyBidHistory history = historyService.getHistoryById(historyId);

        model.addAttribute("property", property);
        model.addAttribute("history", history);

        return "bids/form";
    }

    // 입찰 등록 처리-로그인 검증추가
    @PostMapping
    public String createBid(@ModelAttribute BidCreateRequest request,
                            RedirectAttributes redirectAttributes,HttpSession session) {
        // 로그인 체크
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/users/login";
        }

        log.info("입찰 등록: propertyId={}, historyId={}, amount={}", request.getPropertyId(), request.getHistoryId(), request.getBidAmount());

        try {
            UserBid userBid = request.toEntity();

            Long bidId = userBidService.createBid(userBid);

            // Purchase 생성
            Purchase purchase = new Purchase();
            purchase.setUserId(loginUser.getId());
            purchase.setPropertyId(request.getPropertyId());
            purchase.setPurchasePrice(request.getBidAmount());
            purchase.setPurchaseTime(java.time.LocalDateTime.now());
            purchase.setStatus(PurchaseStatus.PENDING);
            
            purchaseService.createPurchase(purchase);

            redirectAttributes.addFlashAttribute("success", true);
            // ✅ 변경: code → message, messageService 사용
            redirectAttributes.addFlashAttribute("message",
                    messageService.getMessage("bid", "created") );

        } catch (Exception e) {
            log.error("입찰 등록 실패", e);
            redirectAttributes.addFlashAttribute("success", false);
            // ✅ 변경: code → message, messageService 사용
            redirectAttributes.addFlashAttribute("message",
                    messageService.getMessage("bid", "failed"));
            redirectAttributes.addFlashAttribute("errorDetail" ,e.getMessage());

        }

        return "redirect:/properties/" + request.getPropertyId();
    }

    // 특정 물건의 입찰 목록
    @GetMapping("/property/{propertyId}")
    public String bidsByProperty(@PathVariable Long propertyId, Model model) {
        log.info("물건별 입찰 목록: {}", propertyId);

        Property property = propertyService.getPropertyById(propertyId);
        List<UserBidResponse> bids = userBidService.getBidsByPropertyId(propertyId)
                        .stream()
                                .map(UserBidResponse:: from)
                                        .toList();

        model.addAttribute("property", property);
        model.addAttribute("bids", bids);

        return "bids/list";
    }

    // 낙찰 목록
    @GetMapping("/winners")
    public String winningBids(Model model) {
        log.info("낙찰 목록 조회");

        List<UserBidResponse> winningBids = userBidService.getWinningBids(null)
                .stream()
                .map(UserBidResponse::from)
                .toList();
        model.addAttribute("bids", winningBids);

        return "bids/winners";
    }
}