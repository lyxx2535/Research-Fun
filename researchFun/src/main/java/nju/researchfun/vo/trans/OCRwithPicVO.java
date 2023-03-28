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
public class OCRwithPicVO {
    private String sumSrc;
    private String sumDst;
    private String pasteImg; //Base64格式编码

    public static OCRwithPicVO BaiduPicTransModel2OCRwithPicVO(BaiduPicTransModel m){
        return OCRwithPicVO.builder()
                .sumSrc(m.getData().getSumSrc())
                .sumDst(m.getData().getSumDst())
                .pasteImg(m.getData().getPasteImg())
                .build();
    }
}
