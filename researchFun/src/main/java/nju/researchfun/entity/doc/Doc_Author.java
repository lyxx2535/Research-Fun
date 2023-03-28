package nju.researchfun.entity.doc;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_doc_author")
public class Doc_Author {

    private Long did;
    private Long auid;
}
