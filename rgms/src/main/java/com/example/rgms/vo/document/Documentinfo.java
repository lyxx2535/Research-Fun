package com.example.rgms.vo.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class Documentinfo {
    @ApiModelProperty(value = "文件id", example = "1")
    private int did;
    @ApiModelProperty(value = "文献标题", example = "deep learning tests")
    private String title;
    @ApiModelProperty(value = "文件的url", example = "http://119.29.53.191:7069/download-file-0.0.1/download?fileType=PDF_DOCUMENT&filePath=/usr/local/rgms-resources/document/5299eb18-a0e7-440e-aa3e-26331937af81_test.pdf")
    private String pdflink;
    @ApiModelProperty(value = "文件DOI", example = "1")
    private String doi;
    @ApiModelProperty(value = "日期", example = "2021-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    @ApiModelProperty(value = "上传者id", example = "5")
    private  int userid;
    @ApiModelProperty(value = "上传者姓名", example = "李明")
    private  String username;
    @ApiModelProperty(value = "摘要", example = "test")
    private String Abstract;
    @ApiModelProperty(value = "作者列表", example = "['Sam', 'Kate']")
    private List<String> authors;
    @ApiModelProperty(value = "关键词", example = "['deep learning', 'Kate']")
    private List<String> keywords;
    @ApiModelProperty(value = "研究方向", example = "face dection")
    private  String research_direction;
    @ApiModelProperty(value = "研究组id", example = "0")
    private  int groupid;
    @ApiModelProperty(value = "发表的刊物名称", example = "ACM")
    private  String publisher;
    @ApiModelProperty(value = "发表日期", example = "2021-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date publishdate;





}
