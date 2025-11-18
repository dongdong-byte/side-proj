package kim.onbidproperty.dto.api;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
public class OnbidApiResponse {
    private String resultCode;
    private String resultMsg;
    private int totalCount;
    private List<OnbidPropertyDto> items;
    private boolean success;

    // 성공 응답 생성
    public static OnbidApiResponse success(List<OnbidPropertyDto> items) {
        return OnbidApiResponse.builder()
                .resultCode("00")
                .resultMsg("SUCCESS")
                .totalCount(items.size())
                .items(items)
                .success(true)
                .build();
    }

    // 에러 응답 생성
    public static OnbidApiResponse error(String resultCode, String resultMsg) {
        return OnbidApiResponse.builder()
                .resultCode(resultCode)
                .resultMsg(resultMsg)
                .totalCount(0)
                .items(new ArrayList<>())
                .success(false)
                .build();
    }

    // 빈 결과
    public static OnbidApiResponse empty() {
        return OnbidApiResponse.builder()
                .resultCode("00")
                .resultMsg("NO_DATA")
                .totalCount(0)
                .items(new ArrayList<>())
                .success(true)
                .build();
    }
}