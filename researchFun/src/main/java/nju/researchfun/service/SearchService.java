package nju.researchfun.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import nju.researchfun.entity.doc.Keyword;
import nju.researchfun.entity.doc.Doc;
import nju.researchfun.vo.document.Documentinfo;
import nju.researchfun.vo.document.author;

import java.util.List;

public interface SearchService {

    List<author> searchAuthorByName(String name);

    IPage<Keyword> searchKeywordsByKname(String kname, int currentPage, int pageSize);

    //以下是改进版？

    IPage<Documentinfo> searchDoc_v2(int type, String keyword, int currentPage, int pageSize, String direction, Long searcherid);

    IPage<Doc> searchAllthedoc(int currentPage, int pageSize);

    Documentinfo searchCompletedoc(Long did);

    IPage<Doc> advanceSearch(Documentinfo documentinfo, int currentPage, int pageSize);

    IPage<Documentinfo> searchDocByUploader(int type, String keyword, int currentPage, int pageSize,
                                            String direction, Long searcherid, Long uploaderid);


}
