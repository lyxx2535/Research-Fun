package com.example.rgms.controller;

import com.example.rgms.service.ResearchGroupService;
import com.example.rgms.vo.researchgroup.GroupInfo;
import com.example.rgms.vo.response.ResponseVO;
import com.example.rgms.vo.researchgroup.ResearchGroupDetailedInfo;
import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/researchGroup")
@Api(tags = "研究组相关接口")
public class ResearchGroupController {
    private final ResearchGroupService researchGroupService;
    public ResearchGroupController(ResearchGroupService researchGroupService){
        this.researchGroupService=researchGroupService;
    }

    @GetMapping("/existsByGroupName")
    @ApiOperation(value = "判断是否已经存在该名称的研究组")
    @ApiImplicitParam(name = "groupName", value = "待判断的研究组名称", defaultValue = "testGroup", required = true)
    public ResponseVO<Boolean> existsByGroupName(@RequestParam String groupName){
        return new ResponseVO<>(researchGroupService.existsByGroupName(groupName));
    }

    @GetMapping("/simpleInfo/all")
    @ApiOperation(value = "得到所有研究组的简单信息（id、名称、头像、研究方向）", notes = "可以用来在选择研究组的时候使用")
    public ResponseVO<List<ResearchGroupSimpleInfo>> getAllResearchGroupSimpleInfos(){
        return new ResponseVO<>(researchGroupService.getAllResearchGroupSimpleInfos());
    }

    @GetMapping("/detailedInfo")
    @ApiOperation(value = "获取研究组详细信息")
    @ApiImplicitParam(name = "groupId", value = "用户组id", defaultValue = "1", required = true)
    public ResponseVO<ResearchGroupDetailedInfo> getDetailedInfoById(Long groupId){
        return new ResponseVO<>(researchGroupService.getDetailedInfoById(groupId));
    }

    @PutMapping("/groupInfo")
    @ApiOperation(value = "修改研究组信息", notes = "如果不存在对应id的研究组，该方法会报错")
    @ApiParam(name = "form", value = "新的研究组信息", required = true)
    public void editGroupInfo(@RequestBody GroupInfo form){
        researchGroupService.editGroupInfo(form);
    }

    @GetMapping("/directions")
    @ApiOperation(value = "获取研究组研究方向列表")
    @ApiImplicitParam(name = "groupId", value = "研究组id", defaultValue = "4", required = true)
    public ResponseVO<List<String>> getDirections(@RequestParam Long groupId){
        return new ResponseVO<>(researchGroupService.getDirections(groupId));
    }
}
