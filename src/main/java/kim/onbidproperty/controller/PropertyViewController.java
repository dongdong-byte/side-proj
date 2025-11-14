package kim.onbidproperty.controller;


import kim.onbidproperty.domain.Property;
import kim.onbidproperty.domain.PropertyBidHistory;
import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.service.PropertyBidHistoryService;
import kim.onbidproperty.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//화면용 Controller
@Slf4j
@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyViewController {
    private final PropertyService propertyService;
    private  final PropertyBidHistoryService historyService;
//    물건 목록 페이지
    @GetMapping
    public String listProperties(Model model
                                  ){
        log.info("물건 목록 페이지");
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "properties/list";
    }
//물건 상세 페이지
@GetMapping("/{id}")
public  String propertyDetail(@PathVariable Long id, Model model){
    log.info("물건 상세 페이지: {}", id);
    Property property = propertyService.getPropertyById(id);
//    💡 (중요) '데이터 없음' 처리
    if(property == null){
        log.warn("물건을 찾을수 없습니다: {}", id);
        return "redirect:/properties";//       목록 페이지로 리다이렉트
    }
    List<PropertyBidHistory> histories = historyService.getHistoriesByPropertyId(id);
    model.addAttribute("property", property);
    model.addAttribute("histories", histories);
    return "properties/detail";
}
//물건검색
    @GetMapping("/search")
    public  String searchProperties(@RequestParam String keyword, Model model){
        log.info("물건 검색: {}", keyword);
        List<Property> properties = propertyService.searchProperties(keyword);
        model.addAttribute("properties", properties);
        model.addAttribute("keyword", keyword);
        return "properties/list";
    }
//    상태별 물건 조회
    @GetMapping("/status/{status}")
    public  String propertiesByStatus(@PathVariable PropertyStatus status ,Model model){
        log.info("상태별 물건조회 : {}" ,status);
        List<Property> properties = propertyService.getPropertiesByStatus(status);
        model.addAttribute("properties", properties);
        model.addAttribute("status", status);
        return "properties/list";
    }
//    진행중인 경매
    @GetMapping("/ongoing")
    public  String ongoingAuctions(Model model){
        log.info("진행중인 경매 물건 조회");
        List<Property> properties = propertyService.getOngoingAuctions();
        model.addAttribute("properties", properties);
        return "properties/list";

    }
}
