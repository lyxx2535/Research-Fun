package nju.researchfun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import nju.researchfun.constant.LanguageType;
import nju.researchfun.service.TranslateService;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.trans.OCRVO;
import nju.researchfun.vo.trans.OCRwithPicVO;
import nju.researchfun.vo.trans.VoiceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/trans")
@Api(tags = "百度翻译接口")
public class TranslateController {
    @Autowired
    private TranslateService translateService;

    @GetMapping(value = "/text", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "翻译接口",
            notes = "传入一个单词或者句子，可以是各种语言，如果正常将返回中文结果"
    )
    @ApiImplicitParam(name = "query", value = "要查询的单词或句子", defaultValue = "", required = true)
    public ResponseVO<String> query(@RequestParam String query) {
        return new ResponseVO<>(translateService.translateToChinese(query));
    }

    @GetMapping(value = "/ocr")
    @ApiOperation(
            value = "图片识别接口",
            notes = "传入一个在服务器里的图片url，如果正常将返回图片的文字内容"
    )
    @ApiImplicitParam(name = "url", value = "要识别的文件url", required = true)
    public ResponseVO<OCRVO> ocr(@RequestParam String url) {
        return new ResponseVO<>(translateService.OCR(url));
    }

    @GetMapping(value = "/ocrWithPic")
    @ApiOperation(
            value = "图片识别接口",
            notes = "传入一个在服务器里的图片url，如果正常将返回图片的文字内容，以及覆盖译文的图片base64编码"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "要识别的文件url", required = true),
            @ApiImplicitParam(name = "paste", value = "贴合格式，1为整图贴合，2为分块贴合", required = true)
    })
    public ResponseVO<OCRwithPicVO> ocrWithPic(@RequestParam String url, @RequestParam Integer paste) {
        return new ResponseVO<>(translateService.OCRWithPic(url, paste));
    }

    @GetMapping(value = "/voice")
    @ApiOperation(
            value = "语音识别接口",
            notes = "传入一个在服务器里的图片url，如果正常将返回图片的文字内容，以及覆盖译文的图片base64编码"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "要识别的文件url", required = true),
            @ApiImplicitParam(name = "from", value = "当前音频语言", required = true),
            @ApiImplicitParam(name = "to", value = "要翻译的语言", required = true),
            @ApiImplicitParam(name = "format", value = "音频格式", required = true)
    })
    public ResponseVO<VoiceVO> voice(@RequestParam String url, @RequestParam String from, @RequestParam String to, @RequestParam String format) {
        return new ResponseVO<>(translateService.voiceTrans(url, from, to,format));
    }
}
