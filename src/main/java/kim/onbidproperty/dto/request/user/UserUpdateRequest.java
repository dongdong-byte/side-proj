package kim.onbidproperty.dto.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String name;
    private String password;
}
