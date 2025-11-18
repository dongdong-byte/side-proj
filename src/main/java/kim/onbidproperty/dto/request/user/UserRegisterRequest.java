package kim.onbidproperty.dto.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterRequest {
    private String username;
    private String phone;
    private String email;
    private String password;
    private String name;
}
