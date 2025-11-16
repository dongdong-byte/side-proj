완료된 작업
1. 도메인

UserBid.java 엔티티 생성

2. 데이터베이스

user_bids 테이블 생성 (복합 인덱스 포함)

3. 데이터 접근 계층

UserBidMapper.java 인터페이스
UserBidMapper.xml SQL 쿼리

4. 비즈니스 로직

UserBidService.java (입찰 등록, 조회, 낙찰자 지정 등)

5. 컨트롤러

BidViewController.java (화면용)
BidApiController.java (REST API)

6. 화면 템플릿

bids/form.html (입찰 폼)
bids/list.html (입찰 목록)
bids/winners.html (낙찰 목록)
properties/detail.html 수정 (입찰하기 버튼 추가)
