package kim.onbidproperty.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class OnbidApiRequest {
    private int pageNo;
    private int numOfRows;

    // 정적 팩토리 메서드
    public static OnbidApiRequest of(int pageNo, int numOfRows) {
        return OnbidApiRequest.builder()
                .pageNo(pageNo)
                .numOfRows(numOfRows)
                .build();
    }

    // 기본값 설정
    public static OnbidApiRequest withDefaults() {
        return OnbidApiRequest.builder()
                .pageNo(1)
                .numOfRows(10)
                .build();
    }
}