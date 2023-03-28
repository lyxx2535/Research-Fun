package nju.researchfun.entity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class BaiduVoiceSendModel {

    private String from;
    private String to;
    private String format;
    private String voice;
}
