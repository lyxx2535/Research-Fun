package com.example.rgms.entity.document;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "author_doc")
@Getter@Setter
public class author_docEntity {
    @Id
    @Column(name = "auid")
    private int auid;
    @Column(name = "did")
    private  int did;
}
