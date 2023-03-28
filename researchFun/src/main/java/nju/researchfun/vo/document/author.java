package nju.researchfun.vo.document;

import lombok.*;

/**
 * 小写 a 是 vo 大写 A 是实体类
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class author {
    private Long auId;
    private String aname;


}
