package com.example.rgms.controller;

import com.example.rgms.entity.document.docEntity;
import com.example.rgms.entity.document.keywordEntity;
import com.example.rgms.service.SearchService;
import com.example.rgms.vo.document.Doc;
import com.example.rgms.vo.document.Documentinfo;
import com.example.rgms.vo.document.author;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;


import java.util.List;
/*
* 所有关于文献搜索的接口
*
* */





@RestController
@RequestMapping("/search")
@Api(tags = "文献搜索相关接口")


public class SearchController {
    private  final SearchService searchService;
    public SearchController(SearchService searchService){this.searchService=searchService;}

    @PostMapping("/searchauthor")
    @ApiOperation(value = "根据姓名搜索作者", notes = "不报错就是创建成功")
    @ApiImplicitParam(name = "aname", value = "作者姓名", defaultValue = "Sam", required = true)

    public ResponseVO<List<author>> getAuthorByname(@RequestParam String aname){
        return  new ResponseVO<>(searchService.searchauthorByname(aname));
    }


    @PostMapping("/searchdocinfo")
    @ApiOperation(value = "搜索文献改进版", notes = "失败会返回失败原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询类型,1根据文章标题，2关键字，3作者4上传者," +
                    "如果direction字符串为null，则不进行研究方向的过滤", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "keyword", value = "查询关键字", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "direction", value = "研究方向", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "searcherid", value = "搜索者id", defaultValue = "1", required =true)
    })
    public PageResponseVO<Documentinfo> getdocinfo(@RequestParam int type,
                                            @RequestParam String keyword,
                                            @RequestParam int currentPage,
                                            @RequestParam int pageSize,
                                                   @RequestParam String direction,
                                                   @RequestParam int searcherid){
        return  new PageResponseVO<>(searchService.searchdoc_v2(type,keyword,currentPage,pageSize,direction,searcherid));
    }

    @PostMapping("/searchdocbyuploader")
    @ApiOperation(value = "根据上传者搜索文献改进版", notes = "输入关键字和搜索类型，还需要上传者id和使用搜索的搜索者id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询类型,1根据文章标题，2关键字，3作者4上传者," +
                    "如果direction字符串为null，则不进行研究方向的过滤", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "keyword", value = "查询关键字", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "direction", value = "研究方向", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "searcherid", value = "搜索者id", defaultValue = "1", required =true),
            @ApiImplicitParam(name = "uploaderid", value = "上传者者id", defaultValue = "1", required =true)
    })

    PageResponseVO<Documentinfo> searchdocByUploader(@RequestParam int type,
                                                     @RequestParam String keyword,
                                                     @RequestParam int currentPage,
                                                     @RequestParam int pageSize,
                                                     @RequestParam String direction,
                                                     @RequestParam int searcherid,
                                                     @RequestParam int uploaderid)
    {
        return  new PageResponseVO<>( searchService.searchDocByUploader(type,keyword,currentPage,pageSize,direction,searcherid,uploaderid));
    }


    @PostMapping("/searchkeywords")
    @ApiOperation(value = "搜索关键字", notes = "失败会返回失败原因,")
    @ApiImplicitParams({

            @ApiImplicitParam(name = "keyword", value = "查询关键字", defaultValue = "test", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public  PageResponseVO<keywordEntity> getkeywords(@RequestParam String keyword,
                                                      @RequestParam int currentPage
                                                       ,@RequestParam int pageSize){
        return  new PageResponseVO<>(searchService.searchKeywordsBykname(keyword,currentPage,pageSize));

    }

    @PostMapping("/searchalldocs")
    @ApiOperation(value = "搜索所有文献", notes = "失败会返回失败原因,")
    @ApiImplicitParams({


            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })

    public PageResponseVO<docEntity> getallthedocs(@RequestParam int currentPage,@RequestParam int pageSize){



        return  new PageResponseVO<>(searchService.searchAllthedoc(currentPage,pageSize));
    }

    @PutMapping("/advancesearch")
    @ApiOperation(value = "组合查询", notes = "如果不存在对应id的研究组，该方法会报错")
    @ApiParam(name = "documentinfo", value = "新的研究组信息", required = true)
    @ApiImplicitParams({


            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public  PageResponseVO<docEntity> advancesearchAllthedocs(@RequestBody Documentinfo documentinfo,int currentPage,int pageSize){
              return  new PageResponseVO<>(searchService.advanceSearch(documentinfo,currentPage,pageSize));
    }

    @PostMapping("/searchcompletedoc")
    @ApiOperation(value = "搜索文献详细信息", notes = "失败会返回失败原因,")
    @ApiImplicitParam(name = "did", value = "文章id", defaultValue = "0", required = true)

    public  ResponseVO<Documentinfo> getCompletedocinfo(@RequestParam int did){
        return  new ResponseVO<>(searchService.searchCompletedoc(did));
    }









}
