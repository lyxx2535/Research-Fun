package nju.researchfun.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BaiduPicTransModel {

    private String error_code;
    private String error_msg;
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String from;
        private String to;
        private List<ContentDTO> content;
        private String sumSrc;
        private String sumDst;
        private String pasteImg;

        @NoArgsConstructor
        @Data
        public static class ContentDTO {
            private String src;
            private String dst;
            private String rect;
            private Integer lineCount;
            private String pasteImg;
            private List<PointsDTO> points;//左上，右上，右下，左下

            @NoArgsConstructor
            @Data
            public static class PointsDTO {//左上，右上，右下，左下
                private Integer x;
                private Integer y;
            }
        }
    }
}
