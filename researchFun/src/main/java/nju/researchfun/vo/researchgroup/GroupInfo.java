package nju.researchfun.vo.researchgroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class GroupInfo {
    @ApiModelProperty(value = "研究组id", example = "1")
    private Long id;
    @ApiModelProperty(value = "研究组名", example = "云计算研究组")
    private String groupName;
    @ApiModelProperty(value = "研究组简介", example = "主要研究云计算在教学方面的应用")
    private String description;
    @ApiModelProperty(value = "研究组头像url", example = "http://119.29.53.191:7069/download-file-0.0.1/download/PORTRAIT/c8025876-9c62-43f5-b3a1-31cb7cdacc8e_胡桃.jpg")
    private String portrait;
    @ApiModelProperty(value = "研究方向列表", example = "[]")
    private List<String> directions;

    //  这里在干弔 没事了 让他搞了
    public String gainDirectionStr(String directionSep){
        StringBuilder res=new StringBuilder();
        for(String direction : directions)
            res.append(direction).append(directionSep);
        res.delete(res.length()-2, res.length());
        return res.toString();
    }
}
