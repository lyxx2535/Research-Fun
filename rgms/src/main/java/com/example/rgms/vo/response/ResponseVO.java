package com.example.rgms.vo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseVO<T> {
    private String msg;
    private T data;

    public ResponseVO(T data) {
        this.data = data;
        this.msg = "success";
    }

    public ResponseVO(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }
}