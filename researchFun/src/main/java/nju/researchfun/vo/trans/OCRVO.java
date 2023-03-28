package nju.researchfun.vo.trans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.entity.model.BaiduPicTransModel;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class OCRVO {
    private String sumSrc;
    private String sumDst;

    public static OCRVO BaiduPicTransModel2OCRVO(BaiduPicTransModel m){
        return OCRVO.builder()
                .sumSrc(m.getData().getSumSrc())
                .sumDst(m.getData().getSumDst())
                .build();
    }
}
