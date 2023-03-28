package nju.researchfun;

import nju.researchfun.constant.FileType;
import nju.researchfun.entity.filepath.factory.FilePathFactory;
import nju.researchfun.entity.filepath.strategy.FilePath;
import nju.researchfun.entity.weekly.Weekly;
import nju.researchfun.mapper.*;
import nju.researchfun.service.MailService;
import nju.researchfun.vo.weeklywriting.WeeklySaveVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ResearchfunApplicationTests {
    /*
        @Autowired
        private UserMapper userMapper;

        @Autowired
        private TeacherMapper teacherMapper;
        @Autowired
        private StudentMapper studentMapper;
        @Autowired
        private ResearchGroupMapper researchGroupMapper;

        @Autowired
        private MessageMapper messageMapper;
     */
    @Autowired
    private MailService mailService;

    @Test
    void testMail() throws MessagingException {
        String text = "<a href='http://168.192.67.1//user/active?code="
                + "1111111111111111111111" + "'>点击激活【研坊学术研究管理系统】</a>";
        mailService.sendHtmlMail("201830168@smail.nju.edu.cn", "激活邮件", text);
    }

    @Autowired
    private WeeklyMapper weeklyMapper;



    @Transactional
    @Test
    public void save() {
        WeeklySaveVO vo = new WeeklySaveVO();
        vo.setGid(1L);
        vo.setUid(1L);
        Weekly weekly = Weekly.WeeklySaveVO2Weekly(vo);
        weeklyMapper.insert(weekly);
        System.out.println(weekly.getId());
    }



    @Test
    public void testFac(){
        FilePath filepath = FilePathFactory.getFilePathInstance(FileType.PORTRAIT);
        System.out.println(filepath.getDirPath());
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String docs = "[西瓜书, 摸鱼导论, 为什么今天什么都没做,哈哈哈]";

        docs = docs.replaceAll("[\\[ \\]]", "");
        List<String> docsList = new ArrayList<>(Arrays.asList(docs.split(",")));

        for (String s : docsList) {
            System.out.println(s);
        }
    }
}
