package nju.researchfun.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtil {

    private static final String name = "nju.researchfun";

    private static final String lineSep = "\r\n";

    private static final String errMsg = "出现了异常情况如下：" + lineSep + lineSep +
            "出错原因：" + lineSep + "%s" + lineSep + lineSep +
            "出错位置：" + lineSep + "%s" + lineSep + lineSep +
            "详细出错位置：" + lineSep + "%s" + lineSep + lineSep;

    public static String getExceptionAllInformation(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = out.toString();
        pout.close();
        try {
            out.close();
        } catch (Exception ignored) {
        }
        return ret;
    }

    public static String getExceptionPartInformation(Exception ex) {
        StringBuilder res = new StringBuilder();
        String error = getExceptionAllInformation(ex);
        String[] errPart = error.split(System.lineSeparator());
        for (String err : errPart) {
            if (err.contains(name))
                res.append(err).append(lineSep);
        }
        return res.toString();
    }

    public static String getExceptionDetailInformation(Exception ex) {
        StringBuilder res = new StringBuilder();
        String error = getExceptionAllInformation(ex);
        String[] errPart = error.split(System.lineSeparator());
        for (String err : errPart) {
            if (err.contains(name))
                res.append(err);
        }

        return String.format(errMsg, ex.getMessage(), res.toString(), error);
    }


}
