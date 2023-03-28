package nju.researchfun.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeChatSessionModel {
    private String openid;
    private String session_key;
    private String errcode;
    private String errmsg;
}
