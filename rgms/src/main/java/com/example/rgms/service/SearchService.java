package com.example.rgms.service;

import com.example.rgms.entity.MessageEntity;
import com.example.rgms.entity.document.docEntity;
import com.example.rgms.entity.document.keywordEntity;
import com.example.rgms.vo.document.Doc;
import com.example.rgms.vo.document.Documentinfo;
import com.example.rgms.vo.document.author;
import com.example.rgms.vo.response.ResponseVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {

    public List<Doc> Searchdoc(int type,String keywords);

    public List<Doc> searchBytitle(String keywords);

    public  List<Doc> searchBydoi(String keywords);

    public List<Doc> searchByauthor(String keywords);

    public List<author> searchauthorByname(String name);

    /*
    * Page<MessageVO> getAllMessagesByUserId(Long userId, int currentPage, int pageSize);*/

    Page<docEntity> searchDoc(int type,String keyword,int currentPage,int pageSize);

    Page<docEntity> searchdocBytitle(String keyword,int currentPage,int pageSize);

    Page<keywordEntity> searchKeywordsBykname(String kname,int currentPage, int pageSize);

    Page<docEntity> searchdocBykeywords(String kname,int currentPage,int pageSize);

    Page<docEntity> searchdocByauthor(String keyword,int currentPage,int pageSize);

    Page<docEntity> searchdocByuser(String keyword,int currenrPage,int pageSize);


    Page<docEntity> searchAllthedoc(int currentPage,int pageSize);

    Page<docEntity> advanceSearch(Documentinfo documentinfo,int currentPage,int pageSize);



    /*搜索改进版*/
  //  Page<Documentinfo>  searchdoc(int type,String keyword,int currentPage,int pageSize,String dirtection,int searcherid);

    Documentinfo searchCompletedoc(int did);

 //   Page<Documentinfo> searchDocByUploader(int userid);


    Page<Documentinfo>  searchdoc_v2(int type,String keyword,int currentPage,int pageSize,String dirtection,int searcherid);



    Page<Documentinfo> searchDocByUploader(int type,
                                           String keyword,
                                           int currentPage,
                                           int pageSize,
                                           String dirtection,
                                           int searcherid,
                                           int uploaderid);













}
