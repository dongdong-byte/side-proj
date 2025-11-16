## Property (물건) 기능

**도메인**
- `Property.java` - 물건 엔티티
- `PropertyBidHistory.java` - 입찰 회차 엔티티

**Enum**
- `PropertyStatus.java` - 물건 상태 (AVAILABLE, SOLD, PENDING, CANCELLED)
- `SalesType.java` - 판매 방식 (AUCTION, DIRECT)
- `BidStatus.java` - 입찰 상태 (PENDING, ONGOING, COMPLETED, FAILED)
- `ResultStatus.java` - 낙찰 상태 (SUCCESS, FAIL, CANCEL)

**데이터베이스**
- `properties` 테이블
- `properties_bids_history` 테이블

**데이터 접근 계층**
- `PropertyMapper.java` + `PropertyMapper.xml`
- `PropertyBidHistoryMapper.java` + `PropertyBidMapper.xml`

**서비스**
- `PropertyService.java` - 물건 CRUD, 검색, 상태별 조회
- `PropertyBidHistoryService.java` - 회차 CRUD, 상태 변경
- `OnbidApiService.java` - API 응답 XML 파싱
- `PropertySyncService.java` - 온비드 API 데이터 동기화

**컨트롤러**
- `HomeController.java` - 메인 페이지
- `PropertyViewController.java` - 물건 목록, 상세, 검색
- `SyncViewController.java` - 동기화 화면
- `SyncApiController.java` - 동기화 REST API

**외부 API 연동**
- `OnbidApiClient.java` - WebClient로 온비드 API 호출
- `OnbidPropertyDto.java` - API 응답 DTO
- `WebClientConfig.java` - WebClient 설정
- `ModelMapperConfig.java` - ModelMapper 설정

**예외 처리**
- `GlobalExceptionHandler.java` - 전역 예외 처리
- `ResourceNotFoundException.java` - 리소스 없음 예외
- `DuplicateResourceException.java` - 중복 리소스 예외

**화면 템플릿**
- `index.html` - 메인 페이지
- `properties/list.html` - 물건 목록
- `properties/detail.html` - 물건 상세
- `sync/index.html` - 동기화 페이지

---

## Bid (입찰) 기능

**도메인**
- `UserBid.java` - 사용자 입찰 엔티티

**데이터베이스**
- `user_bids` 테이블 (복합 인덱스 포함)

**데이터 접근 계층**
- `UserBidMapper.java` + `UserBidMapper.xml`

**서비스**
- `UserBidService.java` - 입찰 CRUD, 유효성 검증, 낙찰자 지정

**컨트롤러**
- `BidViewController.java` - 입찰 폼, 목록, 낙찰 목록 화면
- `BidApiController.java` - 입찰 REST API

**화면 템플릿**
- `bids/form.html` - 입찰 폼
- `bids/list.html` - 입찰 목록
- `bids/winners.html` - 낙찰 목록
- `properties/detail.html` 수정 - 입찰하기 버튼 추가
