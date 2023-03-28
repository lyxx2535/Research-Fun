package nju.researchfun.vo.trans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.entity.model.BaiduVoiceTransModel;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class VoiceVO {

    private String source;  //识别原文
    private String target;  //翻译文

    public static VoiceVO BaiduVoiceTransModel2VoiceVO(BaiduVoiceTransModel model){
        return VoiceVO.builder().source(model.getData().getSource()).target(model.getData().getTarget()).build();
    }
}
