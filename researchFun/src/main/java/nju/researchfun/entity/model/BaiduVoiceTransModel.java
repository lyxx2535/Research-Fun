package nju.researchfun.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BaiduVoiceTransModel {

    private Integer code;
    private String msg;
    private VoiceData data;

    @NoArgsConstructor
    @Data
    public static class VoiceData {
        private String source;      // 识别原文
        private String target;      // 译文
        private String target_tts;  // 译文TTS ，base64编码
    }
}
