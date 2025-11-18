package kim.onbidproperty.dto.response.user;

import kim.onbidproperty.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
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
