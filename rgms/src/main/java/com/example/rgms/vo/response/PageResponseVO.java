package com.example.rgms.vo.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponseVO<T> {
    private List<T> data;
    private String msg;
    private Long total;

    public PageResponseVO(Page<T> page) {
        this.data = page.toList();
        this.msg = "success";
        this.total = page.getTotalElements();
    }

    public PageResponseVO(List<T> data, Long totalElements) {
        this.data = data;
        this.msg = "success";
        this.total= totalElements;
    }
}
