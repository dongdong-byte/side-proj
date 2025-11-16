구현한 기능

1.복합인덱스
index `idx_property_history` (`property_id`, `history_id`) using  btree ,  -- 복합 인덱스

2.enum 클래스 설정
// 개발자가 코딩할 때 사용 (영어, 대문자)
if (status == BidStatus.PENDING) {
    // ... 로직 처리
}

3.modelmapper 3.x버젼
ModelMapper 3.1.1 에서는 AccessLevel 설정을 제거해야 한다

4.modelmapper

5.DuplicateResourceException 예외처리 클래스 설정

6.@param

7.인덱스 (DB설정)

8.전역예외처리 핸들러
\
9.xml resultmap,sql태그
코드 를 한곳에 모아서 유지 보수가 용이함

10.MyBatis에서 “동적 SQL(Dynamic SQL)” 중에서도 <set> + <if> 조건문을 이용한 부분 업데이트(Partial Update) .
UserBid 객체에서 null이 아닌 값만 골라서 실제 SQL UPDATE 문을 만든다

11.dto클래스
