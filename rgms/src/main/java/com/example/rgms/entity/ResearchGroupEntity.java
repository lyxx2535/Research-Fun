package com.example.rgms.entity;

import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "research_group")
public class ResearchGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String description;
    private Long creatorId;
    private String portrait;
    private String directions; // 研究方向。不用的研究方向用"; "隔开

    public ResearchGroupSimpleInfo toResearchGroupSimpleInfo(String directionSep){
        return ResearchGroupSimpleInfo.builder()
                .groupId(id)
                .groupName(groupName)
                .portrait(portrait)
                .directions(new ArrayList<>(Arrays.asList(directions.split(directionSep))))
                .build();
    }
}
