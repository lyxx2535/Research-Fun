package nju.researchfun.vo.document;

import lombok.*;

import java.util.Date;

@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doc {
    private int dId;
    private  String Title;
    private String Abstract;
    private String PdfLink;
    private String Doi;
    private Date date;
    private  int UserId;

}
