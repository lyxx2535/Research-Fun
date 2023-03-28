package nju.researchfun.service;

import nju.researchfun.vo.trans.OCRVO;
import nju.researchfun.vo.trans.OCRwithPicVO;
import nju.researchfun.vo.trans.VoiceVO;

public interface TranslateService {

    String translateToChinese(String query);

    OCRVO OCR(String imageUrl);

    OCRwithPicVO OCRWithPic(String url,Integer paste);

    VoiceVO voiceTrans(String url, String from, String to,String format);
}
