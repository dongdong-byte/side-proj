package kim.onbidproperty.service;

import kim.onbidproperty.domain.Property;
import kim.onbidproperty.enums.PropertyStatus;
import kim.onbidproperty.exception.DuplicateResourceException;
import kim.onbidproperty.exception.ResourceNotFoundException;
import kim.onbidproperty.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {
    private final PropertyMapper propertyMapper;

    // 전체 물건 조회
    public List<Property> getAllProperties() {
        log.info("전체 물건 조회");
        return propertyMapper.selectAllProperties();
    }

    // 물건 ID로 조회 (조회수 자동 증가)
    @Transactional
    public Property getPropertyById(Long id) {
        log.info("물건 ID로 조회 : {}", id);

        Property property = propertyMapper.selectPropertyById(id);
        if (property == null) {
            throw new ResourceNotFoundException("물건", "ID", id);
        }

        // 조회수 증가
        propertyMapper.incrementViewCount(id);
        return property;
    }

    // 물건번호로 조회
    public Property getPropertyByCltrNo(String cltrNo) {
        log.info("물건번호로 조회 : {}", cltrNo);

        Property property = propertyMapper.selectPropertyByCltrNo(cltrNo);
        if (property == null) {
            throw new ResourceNotFoundException("물건", "물건번호", cltrNo);
        }
        return property;
    }

    // 물건 검색 (물건명, 위치)
    public List<Property> searchProperties(String keyword) {
        log.info("물건 검색 : {}", keyword);
        return propertyMapper.searchProperties(keyword);
    }

    // 상태별 물건 조회
    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        log.info("상태별 물건 조회 : {}", status);
        return propertyMapper.selectPropertiesByStatus(status.name());
    }

    // 진행중인 경매 물건 조회
    public List<Property> getOngoingAuctions() {
        log.info("진행중인 경매 물건 조회");
        return propertyMapper.selectOngoingAuction();
    }

    // 물건 등록
    @Transactional
    public Long createProperty(Property property) {
        log.info("물건 등록 : {}", property.getName());

        // 중복 체크
        Property existing = propertyMapper.selectPropertyByCltrNo(property.getCltrNo());
        if (existing != null) {
            throw new DuplicateResourceException("물건", "물건번호", property.getCltrNo());
        }

        propertyMapper.insertProperty(property);
        log.info("물건 등록 완료 : {}", property.getId());
        return property.getId();
    }

    // 물건 전체 수정
    @Transactional
    public void updateProperty(Property property) {
        log.info("물건 전체 수정 : {}", property.getId());

        // A 방식: 존재 여부 먼저 확인
        Property existing = propertyMapper.selectPropertyById(property.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("물건", "ID", property.getId());
        }

        propertyMapper.updateProperty(property);
        log.info("물건 수정 완료 : {}", property.getId());
    }

    // 물건 부분 수정
    @Transactional
    public void patchProperty(Property property) {
        log.info("물건 부분 수정 : {}", property.getId());

        // A 방식: 존재 여부 먼저 확인
        Property existing = propertyMapper.selectPropertyById(property.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("물건", "ID", property.getId());
        }

        propertyMapper.patchProperty(property);
        log.info("물건 부분 수정 완료 : {}", property.getId());
    }

    // 물건 상태 변경
    @Transactional
    public void updatePropertyStatus(Long id, PropertyStatus status) {
        log.info("물건 상태 변경 : {} -> {}", id, status);

        // A 방식: 존재 여부 먼저 확인
        Property existing = propertyMapper.selectPropertyById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("물건", "ID", id);
        }

        propertyMapper.updatePropertyStatus(id, status.name());
        log.info("물건 상태 변경 완료");
    }

    // 입찰수 증가
    @Transactional
    public void incrementBidCount(Long id) {
        log.info("입찰수 증가 : {}", id);

        // A 방식: 존재 여부 먼저 확인
        Property existing = propertyMapper.selectPropertyById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("물건", "ID", id);
        }

        propertyMapper.incrementBidCount(id);
        log.info("입찰수 증가 완료");
    }

    // 물건 삭제
    @Transactional
    public void deleteProperty(Long id) {
        log.info("물건 삭제 : {}", id);

        // A 방식: 존재 여부 먼저 확인
        Property existing = propertyMapper.selectPropertyById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("물건", "ID", id);
        }

        propertyMapper.deleteProperty(id);
        log.info("물건 삭제 완료 : {}", id);
    }
}