package nju.researchfun.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Base64ChangeUtil {

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     */
    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null)
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = Files.newOutputStream(Paths.get(path));
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //图片转化成base64字符串
    public static String GetImageStr(String imgFile) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = Files.newInputStream(Paths.get(imgFile));
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    public static void main(String[] args) throws IOException {
        System.out.println(System.currentTimeMillis());
      /*  StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(new File("D:\\java\\researchFun\\test\\researchFun\\src\\main\\resources\\templates\\p1.txt").toPath())));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        generateImage(sb.toString(),
                "D:\\java\\researchFun\\test\\researchFun\\src\\main\\resources\\templates\\p1.png");*/
    }
}