package nju.researchfun.service.impl;


import com.google.gson.Gson;
import nju.researchfun.config.BaiduConfig;
import nju.researchfun.entity.model.BaiduPicTransModel;
import nju.researchfun.entity.model.BaiduTransModel;
import nju.researchfun.entity.model.BaiduVoiceSendModel;
import nju.researchfun.entity.model.BaiduVoiceTransModel;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.InternalServerError;
import nju.researchfun.service.FileService;
import nju.researchfun.utils.Base64ChangeUtil;
import nju.researchfun.utils.HmacSha256Util;
import nju.researchfun.utils.MD5;
import nju.researchfun.utils.UnicodeUtil;
import nju.researchfun.utils.http.HttpClient;
import nju.researchfun.utils.http.HttpParams;
import nju.researchfun.vo.trans.OCRVO;
import nju.researchfun.vo.trans.OCRwithPicVO;
import nju.researchfun.vo.trans.VoiceVO;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import nju.researchfun.service.TranslateService;

import java.io.File;
import java.io.IOException;

@Service
public class TranslateServiceImpl implements TranslateService {

    @Autowired
    private BaiduConfig baiduConfig;

    @Autowired
    private FileService fileService;


    @Override
    public String translateToChinese(String query) {
        String url = getAuthorizationUrl(query, baiduConfig.getDefault_from(), baiduConfig.getDefault_to());
        BaiduTransModel result = getResult(url);
        return result.getResult();
    }

    @Override
    public OCRVO OCR(String imageUrl) {
        String image = fileService.getLocalPath(imageUrl);
        File img = new File(image);
        BaiduPicTransModel model = getPicResult(img, baiduConfig.getDefault_from(), baiduConfig.getDefault_to(), 0);
        return OCRVO.BaiduPicTransModel2OCRVO(model);
    }

    @Override
    public OCRwithPicVO OCRWithPic(String url, Integer paste) {
        String image = fileService.getLocalPath(url);
        File img = new File(image);
        BaiduPicTransModel model = getPicResult(img, baiduConfig.getDefault_from(), baiduConfig.getDefault_to(), paste);
        return OCRwithPicVO.BaiduPicTransModel2OCRwithPicVO(model);
    }

    @Override
    public VoiceVO voiceTrans(String url, String from, String to, String format) {
        String voice = fileService.getLocalPath(url);
        BaiduVoiceTransModel model = getVoiceResult(voice, from, to, format);
        return VoiceVO.BaiduVoiceTransModel2VoiceVO(model);
    }

    //////////////////////////////////////////////////

    /**
     * 文字
     */
    private String getAuthorizationUrl(String query, String from, String to) {
        String appId = baiduConfig.getAppId();
        String appSecret = baiduConfig.getAppSecret();

        String salt = String.valueOf(System.currentTimeMillis());
        String src = appId + query + salt + appSecret; // 加密前的原文
        String sign = MD5.md5(src);

        String url = baiduConfig.getUrl();
        url = String.format(url, query, from, to, appId, salt, sign);
        //System.out.println(url);
        return url;
    }

    private BaiduTransModel getResult(String url) {
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.GET, null, String.class);
        String body = responseEntity.getBody();
        //System.out.println(body);
        Gson gson = new Gson();
        BaiduTransModel model = gson.fromJson(body, BaiduTransModel.class);

        if (model.getError_code() != null)
            getInfo(model.getError_code());

        return model;
    }

    //////////////////////////////////////////////////

    /**
     * 图片
     *
     * @param image 传入图片的 md5值
     * @param paste 图片贴合类型：0 - 关闭文字贴合 、1 - 返回整图贴合 、2 - 返回块区贴合
     */
    private HttpParams getParam(File image, String from, String to, Integer paste) throws IOException {
        String appId = baiduConfig.getAppId();
        String appSecret = baiduConfig.getAppSecret();
        String cuid = baiduConfig.getPicture().getCuid();
        String mac = baiduConfig.getPicture().getMac();
        Integer v = baiduConfig.getPicture().getVersion();

        String salt = String.valueOf(System.currentTimeMillis());
        String src = appId + MD5.md5(image) + salt + cuid + mac + appSecret; // 加密前的原文
        String sign = MD5.md5(src).toLowerCase();

        HttpParams params = new HttpParams();
        params.put("image", image, "mutipart/form-data");
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appId);

        params.put("salt", salt);
        params.put("cuid", cuid);
        params.put("mac", mac);
        params.put("version", v);
        params.put("paste", paste);
        params.put("sign", sign);
        return params;

    }

    public BaiduPicTransModel getPicResult(File image, String from, String to, Integer paste) {
        BaiduPicTransModel model;
        try {
            String url = baiduConfig.getPicture().getPostUrl();
            HttpClient httpClient = new HttpClient();
            HttpParams params = getParam(image, from, to, paste);
            String response = httpClient.post(url, params);
            model = new Gson().fromJson(UnicodeUtil.unicodeToString(response), BaiduPicTransModel.class);
        } catch (IOException e) {
            throw new BadRequestException("网络异常，请稍后再试！");
        }
        if (model.getError_code() != null && !"0".equals(model.getError_code()))
            getPicInfo(model.getError_code());
        return model;
    }

    //////////////////////////////////////////////////

    /**
     * 语音
     * todo restemplate 做不到，需要换一种方式
     *
     * @param format 传入音频的格式，默认是pcm
     */
    private Request proRequest(String voicePath, String from, String to, String format) {
        //请求体
        BaiduVoiceSendModel bodyModel = BaiduVoiceSendModel.builder()
                .voice(Base64ChangeUtil.GetImageStr(voicePath)).format(format).from(from).to(to).build();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(bodyModel));

        //头部信息
        String appid = baiduConfig.getAppId();
        String secret = baiduConfig.getAppSecret();
        String salt = String.valueOf(System.currentTimeMillis());
        salt = salt.substring(salt.length() - 10);
        String message = appid + salt + bodyModel.getVoice();
        String sign = HmacSha256Util.Encode(secret, message);

        assert sign != null;
        Headers headers = new Headers.Builder()
                .add("X-Appid", appid).add("X-Timestamp", salt).add("X-Sign", sign).build();

        return new Request.Builder()
                .url(baiduConfig.getVoice().getUrl()).headers(headers).post(body).header("Content-Type", "application/json").build();
    }

    public BaiduVoiceTransModel getVoiceResult(String voicePath, String from, String to, String format) {
        Request request = proRequest(voicePath, from, to, format);

        OkHttpClient okHttpClient = new OkHttpClient();

        BaiduVoiceTransModel model;
        try {
            Response response = okHttpClient.newCall(request).execute();
            String msg = response.body().string();
            System.out.println(msg);
            model = new Gson().fromJson(msg, BaiduVoiceTransModel.class);
        } catch (IOException e) {
            throw new BadRequestException("网络异常，请重试！");
        }

        return model;
        /*MultiValueMap<String, String> params = getParam(voicePath, from, to, format);



        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Appid", appid);
        headers.set("X-Timestamp", salt);
        headers.set("X-Sign", sign);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<BaiduVoiceTransModel> response = client.exchange(baiduConfig.getVoice().getUrl(), method, requestEntity, BaiduVoiceTransModel.class);

        return response.getBody();*/
    }

    /*
 52001 	 请求超时 	 请重试
 52002 	 系统错误 	 请重试
 52003 	 未授权用户 	 请检查appid是否正确或者服务是否开通
 54000 	 必填参数为空 	 请检查是否少传参数
 54001 	 签名错误 	 请检查您的签名生成方法
 54003 	 访问频率受限 	 请降低您的调用频率，或进行身份认证后切换为高级版/尊享版
 54004 	 账户余额不足 	 请前往管理控制台为账户充值
 54005 	 长query请求频繁 	 请降低长query的发送频率，3s后再试
 58000 	 客户端IP非法 	 检查个人资料里填写的IP地址是否正确，可前往开发者信息-基本信息修改
 58001 	 译文语言方向不支持 	 检查译文语言是否在语言列表里
 58002 	 服务当前已关闭 	 请前往管理控制台开启服务
     */
    private void getInfo(String code) {//如果结果失败 返回原因
        switch (code) {
            case "52001":
            case "52002":
                throw new BadRequestException("网络异常，请重试！");
            case "52003":
            case "54000":
            case "54001":
            case "54004":
            case "58002":
                throw new InternalServerError("服务器错误！");
            case "54003":
            case "54005":
                throw new BadRequestException("操作频率过快，请稍后再试！");
            case "58000":
                throw new InternalServerError("客户端IP非法！");
            case "58001":
                throw new BadRequestException("译文语言方向不支持！");
        }
    }

    /*
    0 	 成功
     52001 	 请求超时 	 请重试
     52002 	 服务端系统错误 	 请重试
     52003 	 未授权用户 	 请检查appid是否正确或者服务是否开通
     52010 	 开放设备授权容量不足 	 联系管理员扩增容量
     54000 	 必填参数为空或固定参数有误 	 检查参数是否误传
     54001 	 签名错误 	 请检查您的签名生成方法
     54003 	 访问频率受限 	 请降低您的调用频率
     54004 	 账户余额不足 	 请前往管理控制平台为账户充值
     54005 	 长query请求频繁 	 请降低长query的发送频率，3s后再试
     58000 	 客户端IP非法 	 检查个人资料里填写的IP地址是否正确可前往管理控制平台修改，IP限制，IP可留空
     58001 	 译文语言方向不支持 	 检查译文语言是否在语言列表里

     69001 	 上传图片数据有误 	 检查图片是否有问题
     69002 	 图片识别超时 	 请重试
     69003 	 内容识别失败 	 检查图片是否存在内容后重试
     69004 	 识别内容为空 	 检查图片是否存在内容后重试
     69005 	 图片大小超限（超过4M） 	 请上传符合图片大小要求的图片
     69006 	 图片尺寸不符合标准（最短边至少30px，最长边最大4096px） 	 请上传符合图片尺寸要求的图片
     69007 	 图片格式不支持（png/jpg） 	 请上传png或jpg格式的图片

     69008 	 设备号为空 	 检查cuid参数
     69012 	 文字贴合参数异常 	 请检查参数 paste，枚举示例：0-关闭文字贴合  1-返回整图贴合  2-返回块区贴合
     */
    private void getPicInfo(String code) {//如果结果失败 返回原因
        switch (code) {
            case "52001":
            case "52002":
                throw new BadRequestException("网络异常，请重试！");
            case "52003":
            case "52010":
            case "54000":
            case "54001":
            case "54003":
            case "54004":
            case "69008":
            case "69012":
                throw new InternalServerError("服务器错误！");
            case "54005":
                throw new BadRequestException("操作频率过快，请稍后再试！");
            case "58000":
                throw new InternalServerError("客户端IP非法！");
            case "58001":
                throw new BadRequestException("译文语言方向不支持！");
            case "69001":
            case "69002":
            case "69003":
            case "69004":
            case "69005":
            case "69006":
            case "69007":
                throw new BadRequestException("该图片解析失败！");
        }
    }
}
