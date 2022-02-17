package com.example.rgms.entity.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reply")
@Getter@Setter
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int commentid;
    private int type;
    private int replyid;
    private String content;
    private int userid;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    //@JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
