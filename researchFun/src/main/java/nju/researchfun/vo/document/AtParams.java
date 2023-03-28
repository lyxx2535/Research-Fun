package nju.researchfun.vo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class AtParams {

    private String pdflink;
    private Long did;
    private String content;
    private String position;
    private Long id;
}
