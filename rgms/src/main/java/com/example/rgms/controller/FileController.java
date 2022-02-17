package com.example.rgms.controller;

import com.example.rgms.constant.FileType;
import com.example.rgms.service.FileService;
import com.example.rgms.vo.document.Documentinfo;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/file")
@Api(tags = "文件相关接口")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService=fileService;
    }

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件接口", notes = "该方法会返回存储文件的访问url，可以在浏览器输入url直接访问文件")
    @ApiImplicitParam(name = "fileType", value = "上传的文件类型", defaultValue = "PORTRAIT", required = true)
    public ResponseVO<String> upload(@RequestBody MultipartFile file, @RequestParam FileType fileType){
        return new ResponseVO<>(fileService.upload(file, fileType));
    }

    @GetMapping("/download/{fileType}/{fileName}")
    @ApiOperation(value = "下载文件接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件文件名", required = true),
            @ApiImplicitParam(name = "fileType", value = "文件类型", defaultValue = "PORTRAIT", required = true)
    })
    public void download(@PathVariable FileType fileType, @PathVariable String fileName, HttpServletResponse response){
        fileService.download(fileName, fileType, response);
    }

//    @PutMapping("/update")
//    @ApiOperation(value = "更新文件接口", notes = "该接口会先删除服务器上originalFileUrl所表示的文件，然后保存newFile代替它")
//    @ApiImplicitParam(name = "originalFileUrl", value = "要更新的文件的url", required = true,
//            defaultValue = "http://119.29.53.191:7069/download-file-0.0.1/download?fileType=PDF_DOCUMENT&filePath=/usr/local/rgms-resources/document/5299eb18-a0e7-440e-aa3e-26331937af81_test.pdf")
//    public void update(@RequestBody MultipartFile newFile, @RequestParam String originalFileUrl){
//        fileService.update(newFile, originalFileUrl);
//    }

    @GetMapping("/excutepdf")
    @ApiOperation(value = "解析pdf接口",notes = "返回结果下标0 标题 1作者 2关键字 3文本第一页内容")
    @ApiImplicitParam(name = "fileUrl", value = "文件的url", required = true,
            defaultValue = "http://119.29.53.191:7069/download-file-0.0.1/download?fileType=PDF_DOCUMENT&filePath=/usr/local/rgms-resources/document/5299eb18-a0e7-440e-aa3e-26331937af81_test.pdf")
    public  ResponseVO<List<String>> excutepdf(@RequestParam String fileUrl){
       // return new ResponseVO<>( fileService.excutePdffile(fileUrl));
        List<String> result=fileService.excutePdffile(fileUrl);
        if(result.get(3).equals("由于格式限制，未解析到pdf内容")){
            return new ResponseVO<>(result,"Failure");
        }
        else{
            return new ResponseVO<>(result);
        }

    }

    /*String saveDocumentinfo(String title, List<String> keywords, List<String> authors,Long userId,String url);
     */
    @GetMapping("/saveDocumentinfo")
    @ApiOperation(value = "存储文件信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "文件名", required = true),
            @ApiImplicitParam(name = "keywords", value = "关键字列表", required = true),
            @ApiImplicitParam(name = "authors", value = "作者列表", required = true),
            @ApiImplicitParam(name = "userId", value = "上传者id", required = true),
            @ApiImplicitParam(name = "url", value = "文献存储地址", required = true)
    })
    public  ResponseVO<String> savedocmentinfo(@RequestParam  String title,
                                               @RequestParam List<String> keywords,
                                               @RequestParam List<String> authors,
                                               @RequestParam  Long userId,
                                               @RequestParam  String url)
     {
        return new ResponseVO<>(fileService.saveDocumentinfo(title,keywords,authors,userId,url));
    }

    @PutMapping("/advanceSaveDocumentinfo")
    @ApiOperation(value = "存储文件信息接口改进版", notes = "存储失败该方法会报错")
    @ApiParam(name = "documentinfo", value = "新的研究组信息", required = true)

    public  ResponseVO<String> advanceSaveDocumentinfo(@RequestBody Documentinfo documentinfo){
        return new ResponseVO<>(fileService.advanceSaveDocumentinfo(documentinfo));
    }
    @PutMapping("/modifydocumentinfo")
    @ApiOperation(value = "修改文件信息接口", notes = "传参修改后的info信息")

    @ApiParam(name = "documentinfoafter", value = "新的研究组信息", required = true)

    public  ResponseVO<String> modifyDocumentinfo(@RequestBody Documentinfo documentinfoafter){
        return new ResponseVO<>(fileService.modifyDocumentinfo(documentinfoafter));
    }

}
