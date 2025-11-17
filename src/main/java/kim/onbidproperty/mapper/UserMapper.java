package kim.onbidproperty.mapper;


import kim.onbidproperty.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserMapper {
//CRUD
//    1.조회 READ
//    ID로 조회
User selectById( Long id);
//    이름으로 조회
User selectByUserName(String username);
//(추가) 이메일로 조회
User selectByEmail(String email);
//    (추가 ) 전체 조회
List<User> selectAll();
//    (추가) 이름/이메일 존재 여부 카운트-> 로그인 기능 추가할때 필요
int countByUsername(String username);
    int countByEmail(String email);
//    exist 패턴 추가 (선택)
//    username이 존재 여부
boolean existsByUsername(String username);
//    email이 존재 여부
boolean existsByEmail(String email);
//    2.create : 등록
//   void로 두면 쿼리가 실행되었는지만 알 수 있습니다. int로 바꾸면 **"그래서 몇 개의 행(row)이 실제로 변경되었는지"**를 알 수 있습니다.
int  insertUser(User user);
//    3.update : 수정 (전체 업데이트 또는 부분 업데이트)
//    updateUser는 <if> 기반 조건부 업데이트 고려
int  updateUser(User user);
//  4. delete : 삭제
int  deleteUser(Long id);
}
