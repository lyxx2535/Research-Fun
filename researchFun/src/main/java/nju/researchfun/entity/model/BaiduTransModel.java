package nju.researchfun.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaiduTransModel {
    private String from;
    private String to;
    private List<TransResultDTO> trans_result;
    private String error_code;
    private String error_msg;

    @NoArgsConstructor
    @Data
    public static class TransResultDTO {
        private String src;
        private String dst;
    }

    public String getResult() {
        return trans_result.get(0).getDst();
    }
}

