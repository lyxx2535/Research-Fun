package com.example.rgms.entity.comment;


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
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private int did;
    private int comment_type;
    private String content;
    private String comment_text;
    private  String comment_emoji;
    private int pagenumber;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;
    private  int userid;

}
