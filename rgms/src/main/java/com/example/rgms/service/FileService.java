package com.example.rgms.service;

import com.example.rgms.constant.FileType;
import com.example.rgms.vo.document.Documentinfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import  org.apache.pdfbox.*;

import java.util.List;

public interface FileService {
    String upload(MultipartFile file, FileType fileType);

    String uploadCommentImg(byte[] bytes);

    void deleteCommentImg(String imgUrl);

    void download(String fileName, FileType fileType, HttpServletResponse response);

    void update(MultipartFile newFile, String originalFileUrl);

    List<String>excutePdffile(String url);


    String saveDocumentinfo(String title, List<String> keywords, List<String> authors,Long userId,String url);


    String advanceSaveDocumentinfo(Documentinfo documentinfo);

    /**
     * 修改文献信息
     *
     * @param documentinfoafter  修改后的信息
     *
     * */
    String modifyDocumentinfo(Documentinfo documentinfoafter );


}
