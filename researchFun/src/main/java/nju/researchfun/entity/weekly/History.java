package nju.researchfun.entity.weekly;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @TableId
    private Long id;
    private Long uid;
    private Long gid;
    private Long did;
    private Date time;
}
