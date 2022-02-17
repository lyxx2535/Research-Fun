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
@Table(name = "keyword_doc")
@Getter
@Setter
public class Keyword_DocEntity {
    @Id
    @Column(name = "kid")
    private int kid;
    @Column(name = "did")
    private int did;
}
