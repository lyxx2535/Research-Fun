package nju.researchfun.entity.doc;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.vo.document.author;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @TableId(value = "auid")
    private Long auId;
    private String aname;

    public author toauthor() {
        return author.builder()
                .auId(auId)
                .aname(aname)
                .build();
    }

}
