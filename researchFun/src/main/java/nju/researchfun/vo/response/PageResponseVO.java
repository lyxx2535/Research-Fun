package nju.researchfun.vo.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResponseVO<T> {
    private List<T> data;
    private String msg;
    private Long total;

    public PageResponseVO(IPage<T> page) {
        this.data = page.getRecords();
        this.msg = "success";
        this.total = page.getTotal();
    }

    public PageResponseVO(List<T> data, Long totalElements) {
        this.data = data;
        this.msg = "success";
        this.total= totalElements;
    }
}
