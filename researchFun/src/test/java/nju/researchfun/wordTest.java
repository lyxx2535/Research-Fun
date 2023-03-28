package nju.researchfun;

import cn.afterturn.easypoi.word.WordExportUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class wordTest {



    public static void main(String[] args) throws Exception {
        List<String> arr = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            arr.add("文献" + (i + 1));
        }
        for (String s : arr) {
            sb.append(s).append(System.lineSeparator());
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("info", "由研坊——学研小管家生成");
        map.put("name", "lqf");
        map.put("doc", sb.toString());
        map.put("startDate", new Date());
        map.put("endDate", new Date());
        map.put("username", "鲁权锋");
        map.put("group", "大创");
        map.put("done", "什么都没做完");
        map.put("todo", "模式识别作业没写完，" + System.lineSeparator() + "模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，" +
                "模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，" +
                "模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，" +
                "模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，" +
                "模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，模式识别作业没写完，");
        XWPFDocument doc = WordExportUtil.exportWord07(
                "http://42.193.37.120:9712/file/download/PDF_DOCUMENT/weeklyWriting.docx", map);

        FileOutputStream fos = new FileOutputStream("D:\\java\\researchFun\\test\\researchFun\\src\\main\\resources\\word\\test.docx");
        doc.write(fos);
        System.out.println("解析完了");
        fos.close();
    }
}
