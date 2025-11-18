package kim.onbidproperty.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class PaginationRequest {
    private int numOfRows;
    private int maxPages;

    public static PaginationRequest of(int numOfRows, int maxPages) {
        return PaginationRequest.builder()
                .numOfRows(numOfRows)
                .maxPages(maxPages)
                .build();
    }

    // 기본값 (한 페이지에 10개, 최대 10페이지)
    public static PaginationRequest withDefaults() {
        return PaginationRequest.builder()
                .numOfRows(10)
                .maxPages(10)
                .build();
    }
}