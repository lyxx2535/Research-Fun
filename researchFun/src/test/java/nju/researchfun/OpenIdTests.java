package nju.researchfun;

import nju.researchfun.config.BaiduConfig;
import nju.researchfun.entity.model.BaiduPicTransModel;
import nju.researchfun.entity.model.BaiduVoiceTransModel;
import nju.researchfun.entity.user.UserOpenId;
import nju.researchfun.service.impl.TranslateServiceImpl;
import nju.researchfun.service.impl.UserOpenIdServiceImpl;
import nju.researchfun.utils.MD5;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SpringBootTest
class OpenIdTests {
    @Autowired
    private UserOpenIdServiceImpl service;

    @Test
    void testCamel() {
        /*UserOpenId check = service.check("51351531");

        System.out.println(check);

        service.insert(UserOpenId.builder().userId(1L).openId("dsada").build());

        System.out.println();*/

/*        UserOpenId userOpenId = service.checkByUserId(1L);
        System.out.println(userOpenId);*/

        int update = service.update(UserOpenId.builder().userId(6L).openId("dsada").build());
        System.out.println(update);
    }

    @Autowired
    TranslateServiceImpl translateService;

    @Autowired
    BaiduConfig baiduConfig;

    @Test
    void testBaiduPic() throws IOException {
        File f = new File("D:\\java\\researchFun\\test\\researchFun\\src\\main\\resources\\templates\\img.png");
        BaiduPicTransModel model = translateService.getPicResult(f, "auto", "zh", 0);
        System.out.println(model);

    }

    @Test
    void testBaiduVoice() throws IOException {
        String voicePath = "C:\\Users\\18933\\Desktop\\DC\\音频\\zh.m4a";
        BaiduVoiceTransModel voice = translateService.getVoiceResult(voicePath, "zh", "zh", "pcm");
        System.out.println(voice);
    }
}
