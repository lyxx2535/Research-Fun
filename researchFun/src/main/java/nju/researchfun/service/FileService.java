package nju.researchfun.service;

import nju.researchfun.constant.FileType;
import nju.researchfun.vo.document.Documentinfo;
import nju.researchfun.vo.document.ReadVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileService {

    String upload(MultipartFile file, FileType fileType);

    void download(String fileName, FileType fileType, HttpServletResponse response);

    List<String> excutePdffile(String url);

    String saveDocumentinfo(String title, List<String> keywords, List<String> authors, Long userId, String url);

    String advanceSaveDocumentinfo(Documentinfo documentinfo);

    /**
     * 修改文献信息
     *
     * @param documentinfoafter 修改后的信息
     */
    String modifyDocumentinfo(Documentinfo documentinfoafter);

    void record(ReadVO vo);

    /**
     * TODO 待完成
     * 批注 评论相关
     */
    void deleteCommentImg(String imgUrl);

    String uploadCommentImg(byte[] bytes);

    /**
     * 根据 url获取本地图片的路径
     */
    String getLocalPath( String url);


}
