package com.example.rgms.vo.document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter@Setter
public class Doc {
    private int dId;
    private  String Title;
    private String Abstract;
    private String PdfLink;
    private String Doi;
    private Date date;
    private  int UserId;

}
