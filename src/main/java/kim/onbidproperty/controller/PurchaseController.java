package kim.onbidproperty.controller;


import kim.onbidproperty.domain.Purchase;
import kim.onbidproperty.dto.request.purchase.PurchaseCreateRequest;
import kim.onbidproperty.dto.request.purchase.PurchaseUpdateStatusRequest;
import kim.onbidproperty.dto.response.purchase.PurchaseResponse;
import kim.onbidproperty.enums.PurchaseStatus;
import kim.onbidproperty.service.MessageService;
import kim.onbidproperty.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final MessageService messageService;

    //    구매 등록 폼
    @GetMapping("/create")
    public String createForm() {
        return "purchases/create";
    }
//    구매등록 처리

    @PostMapping("/create")
    public String create(@ModelAttribute PurchaseCreateRequest request,
                         RedirectAttributes redirectAttributes) {
        Purchase purchase = request.toEntity();
        Long purchaseId = purchaseService.createPurchase(purchase);
        addSuccessMessage(redirectAttributes, "purchase", "created");
        return "redirect:/purchases/" + purchaseId;

    }

    //    구매 목록 조회(전채)
    @GetMapping
    public String list(Model model) {
        // 기본적으로 전체 조회는 구현하지 않음 (userId 또는 propertyId로 조회)
        return "purchases/list";
    }

    //사용자별 구매 목록 조회
    @GetMapping("/user/{userId}")
    public String listByUser(@PathVariable Long userId, Model model) {
        List<PurchaseResponse> purchases = convertToResponseList(
                purchaseService.findByUserId(userId));
        model.addAttribute("purchases", purchases);
        model.addAttribute("userId", userId);
        return "purchases/list";
    }

    //물건별 구매 목록 조회
    @GetMapping("/property/{propertyId}")
    public String listByProperty(@PathVariable Long propertyId, Model model) {
        List<PurchaseResponse> purchases = convertToResponseList(
                purchaseService.findByPropertyId(propertyId));
        model.addAttribute("purchases", purchases);
        model.addAttribute("propertyId", propertyId);
        return "purchases/list";
    }

    // 사용자 + 물건별 구매 목록 조회
    @GetMapping("/user/{userId}/property/{propertyId}")
    public String listByUserAndProperty(@PathVariable Long userId, @PathVariable Long propertyId, Model model) {
        List<PurchaseResponse> purchases = convertToResponseList(
                purchaseService.findByUserIdAndPropertyId(userId, propertyId));
        model.addAttribute("purchases", purchases);
        model.addAttribute("userId", userId);
        model.addAttribute("propertyId", propertyId);
        return "purchases/list";
    }

    //    구매 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Purchase purchase = purchaseService.findById(id);
        PurchaseResponse purchaseResponse = PurchaseResponse.from(purchase);
        model.addAttribute("purchase", purchaseResponse);

        return "purchases/detail";
    }

    //    구매 상태 변경 폼
    @GetMapping("/{id}/status")
    public String statusForm(@PathVariable Long id, Model model) {
        Purchase purchase = purchaseService.findById(id);
        PurchaseResponse purchaseResponse = PurchaseResponse.from(purchase);
        model.addAttribute("purchase", purchaseResponse);
        return "purchases/status";
    }

    // 구매 상태 변경 처리
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @ModelAttribute PurchaseUpdateStatusRequest request,
                               RedirectAttributes redirectAttributes) {
        purchaseService.updateStatus(id, request.getStatus());

        addSuccessMessage(redirectAttributes, "purchase", "statusUpdated");

        return "redirect:/purchases/" + id;
    }

//    구매취소(삭제)
@PostMapping("/{id}/delete")
public String delete(@PathVariable Long id,
                     @RequestParam(required = false) Long userId,
                     @RequestParam(required = false) Long propertyId,
                     RedirectAttributes redirectAttributes) {

    purchaseService.deletePurchase(id);
    addSuccessMessage(redirectAttributes, "purchase", "deleted");

    if (userId != null) return "redirect:/purchases/user/" + userId;
    if (propertyId != null) return "redirect:/purchases/property/" + propertyId;

    return "redirect:/purchases";
}
//    ==================
    //공통 메서드
//    성공 메시지를 RedirectAttributes에 추가
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String category, String key) {
    redirectAttributes.addFlashAttribute("message", messageService.getMessage(category, key));

    }
// Purchase 리스트를 PurchaseResponse 리스트로 변환
    private List<PurchaseResponse> convertToResponseList(List<Purchase> purchases) {
    return purchases.stream()
            .map(PurchaseResponse::from)
            .toList();
    }
}
