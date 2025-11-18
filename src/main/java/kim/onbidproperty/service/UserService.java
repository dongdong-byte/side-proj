package kim.onbidproperty.service;


import kim.onbidproperty.domain.User;
import kim.onbidproperty.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private  final UserMapper userMapper;
    private  final MessageService messageService;
//    회원가입
//    username ,email 중복체크
@Transactional
public Long register(User user){
    checkUsernameDuplicate(user.getUsername());
    checkEmailDuplicate(user.getEmail());
    userMapper.insertUser(user);
    return user.getId();
}
//    로그인
public User login(String username, String password){
    User user = findByUsername(username);
    if(!user.getPassword().equals(password)){
        throw  new IllegalArgumentException(
                messageService.getMessage("user", "invalidPassword")
        );
    }
    return user;
}
//조회 기능
private User findByUsername(String username) {
    return find("username" ,username);
}
public User findById(Long id){
    return find("id", String.valueOf(id));
}
public User findByEmail(String email){
    return find("email", email);
}
public List<User> findAll(){

    return userMapper.selectAll();

}
//공통조회 로직
    private User find(String type, String value){

        User user =switch (type) {
            case "id" -> userMapper.selectById(Long.valueOf(value));
            case "username" -> userMapper.selectByUserName(value);
            case "email" -> userMapper.selectByEmail(value);
            default -> throw new IllegalArgumentException(
                    messageService.getMessage("user", "invalidType")
            );
        };
        if(user == null){
            throw new IllegalArgumentException(
                    messageService.getMessage("user", "notFound")
            );
        }
        return user;
    }

//    사용자 정보 수정
    @Transactional
    public void updateUser(User user){
        User existUser = findById(user.getId());
//        username 변경시 기존  username과 다르면 중복 검사
        if(user.getUsername() != null && !user.getUsername().equals(existUser.getUsername())){
            checkUsernameDuplicate(user.getUsername());
        }
//        email 변경시 기존 email과 다르면 중복 검사
        if(user.getEmail() != null && !user.getEmail().equals(existUser.getEmail())){
            checkEmailDuplicate(user.getEmail());

        }

        int result = userMapper.updateUser(user);
        if(result == 0){
            throw new IllegalArgumentException(
                    messageService.getMessage("user", "updateFailed")
            );
        }

//
    }
//    사용자 삭제

    @Transactional
    public void deleteUser(Long id){
        findById(id); //        존재 검증
        int result = userMapper.deleteUser(id);
        if(result == 0){
            throw new IllegalArgumentException(
                    messageService.getMessage("user", "deleteFailed")
            );
        }
    }
    //중복체크
    private void checkEmailDuplicate(String email) {
        if(isEmailExists(email)){
            throw new IllegalArgumentException(
                    messageService.getMessage("user", "duplicateEmail")
            );
        }
    }



    private void checkUsernameDuplicate(String username) {
        if(isUsernameExists(username)){

            throw  new IllegalArgumentException(
                    messageService.getMessage("user","duplicateUsername"));
        }
    }

    private boolean isUsernameExists(String username) {
        return userMapper.existsByUsername(username);
    }
    private boolean isEmailExists(String email) {
        return userMapper.existsByEmail(email);
    }

}

