package com.example.rgms.entity.document;

import com.example.rgms.vo.document.Documentinfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doc")

public class docEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int did;
    private  String title;

    @Column(name = "abstract", length = 5000)
    private String Abstract;

    @Column(length = 2000)
    private String pdflink;
    private String doi;
    @JsonFormat(pattern = "yyyy-MM-dd " )
    private Date date;
    private  int userid;
    private  String research_direction;
    private int groupid;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd " )
    private Date publisdate;


    public Documentinfo toDocumentinfo(){
        Documentinfo documentinfo=new Documentinfo();
        documentinfo.setDid(this.getDid());
        documentinfo.setTitle(this.getTitle());
        // documentinfo.setAbstract(entity.getAbstract());
        documentinfo.setDate(this.getDate());
        documentinfo.setPdflink(this.getPdflink());
        documentinfo.setDoi(this.getDoi());
        documentinfo.setUserid(this.getUserid());
        documentinfo.setResearch_direction(this.getResearch_direction());
        documentinfo.setGroupid(this.getGroupid());
        documentinfo.setPublisher(this.publisher);
        documentinfo.setPublishdate(this.publisdate);
        return documentinfo;
    }


}
