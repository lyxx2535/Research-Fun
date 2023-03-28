package nju.researchfun.entity.doc;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_doc_keyword")
public class Doc_Keyword {

    private Long kid;
    private Long did;
}
