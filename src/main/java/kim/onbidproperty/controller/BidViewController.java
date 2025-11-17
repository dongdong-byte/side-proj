package kim.onbidproperty.controller;

import kim.onbidproperty.domain.Property;
import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.domain.UserBid;
import kim.onbidproperty.service.PropertyBidHistoryService;
import kim.onbidproperty.service.PropertyService;
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

    // 입찰 폼 페이지
    @GetMapping("/new")
    public String bidForm(@RequestParam Long propertyId,
                          @RequestParam Long historyId,
                          Model model) {
        log.info("입찰 폼 페이지: propertyId={}, historyId={}", propertyId, historyId);

        Property property = propertyService.getPropertyById(propertyId);
        PropertyBidHistory history = historyService.getHistoryById(historyId);

        model.addAttribute("property", property);
        model.addAttribute("history", history);

        return "bids/form";
    }

    // 입찰 등록 처리
    @PostMapping
    public String createBid(@RequestParam Long propertyId,
                            @RequestParam Long historyId,
                            @RequestParam String bidderName,
                            @RequestParam(required = false) String bidderPhone,
                            @RequestParam(required = false) String bidderEmail,
                            @RequestParam BigDecimal bidAmount,
                            RedirectAttributes redirectAttributes) {
        log.info("입찰 등록: propertyId={}, historyId={}, amount={}", propertyId, historyId, bidAmount);

        try {
            UserBid userBid = new UserBid();
            userBid.setPropertyId(propertyId);
            userBid.setHistoryId(historyId);
            userBid.setBidderName(bidderName);
            userBid.setBidderPhone(bidderPhone);
            userBid.setBidderEmail(bidderEmail);
            userBid.setBidAmount(bidAmount);

            Long bidId = userBidService.createBid(userBid);

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("code", "bid.created" );

        } catch (Exception e) {
            log.error("입찰 등록 실패", e);
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("code", "bid.failed");
        }

        return "redirect:/properties/" + propertyId;
    }

    // 특정 물건의 입찰 목록
    @GetMapping("/property/{propertyId}")
    public String bidsByProperty(@PathVariable Long propertyId, Model model) {
        log.info("물건별 입찰 목록: {}", propertyId);

        Property property = propertyService.getPropertyById(propertyId);
        List<UserBid> bids = userBidService.getBidsByPropertyId(propertyId);

        model.addAttribute("property", property);
        model.addAttribute("bids", bids);

        return "bids/list";
    }

    // 낙찰 목록
    @GetMapping("/winners")
    public String winningBids(Model model) {
        log.info("낙찰 목록 조회");

        List<UserBid> winningBids = userBidService.getWinningBids(null);
        model.addAttribute("bids", winningBids);

        return "bids/winners";
    }
}