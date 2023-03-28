package nju.researchfun.vo.researchgroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import nju.researchfun.entity.ResearchGroup;

import java.util.List;

@Data
@ApiModel
public class GroupInfoWithoutId {
    @ApiModelProperty(value = "研究组名称", example = "智力成长研究组")
    private String groupName;
    @ApiModelProperty(value = "研究组简介", example = "研究组孩子成长过程中智力的变化情况")
    private String description;
    @ApiModelProperty(value = "创建者id", example = "2")
    private Long creatorId;
    @ApiModelProperty(value = "研究组头像url", example = "http://42.193.37.120:9712/file/download/PORTRAIT/cd95fe4b-bed8-431d-90b5-166dccc011da_default.jpg")
    private String portrait;
    @ApiModelProperty(value = "研究方向数组", example = "[\"机器学习\", \"计算机视觉\"]")
    private List<String> directions;

    public ResearchGroup toResearchGroup(String directionSep) {
        ResearchGroup entity = ResearchGroup.builder()
                .groupName(groupName)
                .description(description)
                .creatorId(creatorId)
                .portrait(portrait)
                .build();
        // 研究方向实现 懒得改了
        StringBuilder direction = new StringBuilder();
        for (String dir : directions)
            direction.append(dir).append(directionSep);
        direction.delete(direction.length() - 2, direction.length());
        entity.setDirections(direction.toString());
        return entity;
    }
}
