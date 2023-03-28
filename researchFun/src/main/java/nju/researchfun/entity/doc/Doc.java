package nju.researchfun.entity.doc;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.vo.document.Documentinfo;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Doc {

    @TableId
    private Long did;
    private String title;

    @TableField(value = "abstract")
    private String abstracts;

    private String pdflink;
    @JsonFormat(pattern = "yyyy-MM-dd ")
    private Date date;
    private Long userid;
    private String researchDirection;
    private Long groupid;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd ")
    private Date publishdate;


    public static Documentinfo toDocumentinfo(Doc doc) {
        Documentinfo documentinfo = new Documentinfo();
        documentinfo.setDid(doc.getDid());
        documentinfo.setTitle(doc.getTitle());
        documentinfo.setAbstract(doc.getAbstracts());
        documentinfo.setDate(doc.getDate());
        documentinfo.setPdflink(doc.getPdflink());
        //documentinfo.setDoi(doc.getDoi());  todo doi是啥
        documentinfo.setUserid(doc.getUserid());
        documentinfo.setResearch_direction(doc.getResearchDirection());
        documentinfo.setGroupid(doc.getGroupid());
        documentinfo.setPublisher(doc.getPublisher());
        documentinfo.setPublishdate(doc.getPublishdate());
        return documentinfo;
    }


}
