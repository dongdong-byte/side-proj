package kim.onbidproperty.dto.response.user;

import kim.onbidproperty.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor // ModelMapper용 추가
@AllArgsConstructor // Builder용 추가
public class UserResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기존 방식도 유지 (선택사항)
    public static UserResponse from(User user){
      return UserResponse.builder()
              .id(user.getId())
              .username(user.getUsername())
              .phone(user.getPhone())
              .email(user.getEmail())
              .name(user.getName())
              .createdAt(user.getCreatedAt())
              .updatedAt(user.getUpdatedAt())
              .build();


    }

}
