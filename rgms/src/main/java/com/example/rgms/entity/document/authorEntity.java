package com.example.rgms.entity.document;


import com.example.rgms.vo.document.author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "author")
public class authorEntity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auId;
    private String aname;

    public author toauthor(){
        return author.builder()
                .auId(auId)
                .aname(aname)
                .build();
    }

}
