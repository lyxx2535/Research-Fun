package com.example.rgms.entity.document;


import lombok.*;

import javax.persistence.*;
@Getter
@Setter

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "keyword")
public class keywordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int kid;
    private String kname;
}
