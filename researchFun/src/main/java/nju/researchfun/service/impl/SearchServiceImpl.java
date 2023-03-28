package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nju.researchfun.entity.doc.*;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.InternalServerError;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.*;
import nju.researchfun.service.SearchService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.document.Documentinfo;
import nju.researchfun.vo.document.author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private KeywordMapper keywordMapper;
    @Autowired
    private Doc_AuthorMapper doc_authorMapper;
    @Autowired
    private Doc_KeywordMapper doc_keywordMapper;

    @Override
    public List<author> searchAuthorByName(String name) {
        List<Author> authpo = authorMapper.selectList(new QueryWrapper<>(Author.builder().aname(name).build()));
        List<author> result = new ArrayList<>();
        for (Author author : authpo) {
            result.add(author.toauthor());
        }
        return result;
    }


    public IPage<Keyword> searchKeywordsByKname(String kname, int currentPage, int pageSize) {
        Page<Keyword> page = new Page<>(currentPage, pageSize);

        return keywordMapper.selectPage(page, new QueryWrapper<>(Keyword.builder().kname(kname).build()));
    }

    @Override
    public IPage<Documentinfo> searchDoc_v2(int type, String keyword, int currentPage, int pageSize, String direction, Long searcherid) {
        return searchByCondition(type, keyword, currentPage, pageSize, direction, searcherid, null, false);
    }

    @Override
    public Page<Documentinfo> searchDocByUploader(int type, String keyword, int currentPage, int pageSize, String direction, Long searcherid, Long uploaderid) {
        return searchByCondition(type, keyword, currentPage, pageSize, direction, searcherid, uploaderid, true);
    }


    //整合了上面两种查询
    private Page<Documentinfo> searchByCondition(int type, String keyword, int currentPage, int pageSize, String direction, Long searcherid, Long uploaderid, boolean isByUser) {
        try {
            List<Doc> doc;
            QueryWrapper<Doc> wrapper = new QueryWrapper<>();
            switch (type) {
                case 0:
                    doc = docMapper.selectList(null);
                    break;
                case 1:
                    wrapper.like("title", keyword).orderByDesc("date");
                    doc = docMapper.selectList(wrapper);
                    break;
                case 2:
                    wrapper.orderByDesc("date");
                    doc = docMapper.selectByKeyword(keyword);
                    break;
                case 3:
                    //多对多 三表联合查询
                    doc = docMapper.selectByAuthorName(keyword);
                    break;
                case 4:
                    Long id = Long.parseLong(keyword);
                    wrapper.eq("userid", id).orderByDesc("date");
                    doc = docMapper.selectList(wrapper);
                    break;
                default:
                    throw new BadRequestException("请求失败,请重新查询");
            }

            List<Documentinfo> documentinfos = new ArrayList<>();
            for (Doc document : doc) {
                Documentinfo documentinfo = Doc.toDocumentinfo(document);
                Long userid = document.getUserid();
                User user = userService.getUserById(userid);
                String username = user.getUsername();
                documentinfo.setUsername(username);

                Long did = document.getDid();
                //根据文献id查找关键词
                List<Keyword> keywordEntities = keywordMapper.selectByDocid(did);
                List<String> keywords = new ArrayList<>();

                for (Keyword keywordEntity : keywordEntities) {
                    keywords.add(keywordEntity.getKname());
                }
                documentinfo.setKeywords(keywords);
                //根据文献id查找作者信息
                List<Author> authorEntities = authorMapper.selectByDocid(did);
                List<String> authors = new ArrayList<>();
                for (Author authorEntity : authorEntities) {
                    authors.add(authorEntity.getAname());
                }
                documentinfo.setAuthors(authors);
                documentinfos.add(documentinfo);
            }

            if (!"null".equals(direction)) {//双层过滤 根据研究方向和研究组过滤
                documentinfos = this.filterByDirection(documentinfos, direction);
            }
            documentinfos = this.filterByResearchGroup(documentinfos, searcherid);

            if (isByUser) {
                documentinfos = this.filterByUploader(documentinfos, uploaderid);
            }


            //过滤完之后似乎就只能手动分页查询了 流汗黄豆
            List<Documentinfo> result = new ArrayList<>();
            if (currentPage > (documentinfos.size() / pageSize)) {
                throw new BadRequestException("超过最大页码,请重新选择");
            }
            int index = (currentPage) * pageSize;
            for (int i = 0; i < pageSize; i++) {
                if (index + i < documentinfos.size()) {
                    result.add(documentinfos.get(index + i));
                }
            }
            Page<Documentinfo> page = new Page<>(currentPage, pageSize, documentinfos.size());
            page.setRecords(result);

            return page;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }


    @Override
    public IPage<Doc> searchAllthedoc(int currentPage, int pageSize) {
        try {
            Page<Doc> page = new Page<>(currentPage, pageSize);
            return docMapper.selectPage(page, null);
        } catch (Exception e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @Override
    public Documentinfo searchCompletedoc(Long did) {
        try {
            Doc doc = docMapper.selectById(did);

            Documentinfo documentinfo = Doc.toDocumentinfo(doc);

            //设置上传者信息
            Long userid = doc.getUserid();
            User userEntity = userService.getUserById(userid);
            String username = userEntity.getUsername();
            documentinfo.setUsername(username);

            //设置作者和关键字信息
            List<Keyword> keywordEntities = keywordMapper.selectByDocid(did);
            List<String> keywords = new ArrayList<>();
            for (Keyword keywordEntity : keywordEntities) {
                keywords.add(keywordEntity.getKname());
            }
            documentinfo.setKeywords(keywords);

            List<Author> authorEntities = authorMapper.selectByDocid(did);
            List<String> authors = new ArrayList<>();
            for (Author authorEntity : authorEntities) {
                authors.add(authorEntity.getAname());
            }
            documentinfo.setAuthors(authors);
            return documentinfo;
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public IPage<Doc> advanceSearch(Documentinfo documentinfo, int currentPage, int pageSize) {
        String title = documentinfo.getTitle();
        List<String> authors = documentinfo.getAuthors();
        List<String> keywords = documentinfo.getKeywords();
        System.out.println(title);
        return findAllDocByAuthorAndTitle(title, authors.size() == 0 ? null : authors, keywords.size() == 0 ? null : keywords, currentPage, pageSize);
    }

    //这里为了方便我就不多表查询了
    //根据文献标题，文献作者，文献关键词查询
    private IPage<Doc> findAllDocByAuthorAndTitle(String title, List<String> authors, List<String> keywords, int currentPage, int pageSize) {
        List<Long> dids = new ArrayList<>();//最终的文献id集合

        if (authors != null) {//查作者
            List<Long> auids = new ArrayList<>();
            for (Author author : authorMapper.selectList(new QueryWrapper<Author>().in("aname", authors))) {
                auids.add(author.getAuId());
            }

            for (Doc_Author doc_author : doc_authorMapper.selectList(new QueryWrapper<Doc_Author>().in("auid", auids))) {
                dids.add(doc_author.getDid());
            }
        }

        if (keywords != null) {//查关键词
            List<Long> kids = new ArrayList<>();
            for (Keyword keyword : keywordMapper.selectList(new QueryWrapper<Keyword>().in("kname", keywords))) {
                kids.add(keyword.getKid());
            }

            for (Doc_Keyword doc_keyword : doc_keywordMapper.selectList(new QueryWrapper<Doc_Keyword>().in("kid", kids))) {
                dids.add(doc_keyword.getDid());
            }
        }

        QueryWrapper<Doc> wrapper = new QueryWrapper<>();
        if (dids.size() != 0)
            wrapper.in("did", dids);
        return docMapper.selectPage(new Page<>(currentPage, pageSize), wrapper);
    }

    /**
     * 根据研究方向和上传者进行过滤
     * 根据研究组进行过滤
     */
    private List<Documentinfo> filterByDirection(List<Documentinfo> documentinfos, String direction) {

        List<Documentinfo> res = new ArrayList<>();

        for (Documentinfo info : documentinfos) {
            if (info.getResearch_direction().equals(direction))
                res.add(info);
        }
        return res;
    }

    private List<Documentinfo> filterByUploader(List<Documentinfo> documentinfos, Long userid) {

        List<Documentinfo> res = new ArrayList<>();

        for (Documentinfo info : documentinfos) {
            if (info.getUserid().equals(userid))
                res.add(info);
        }
        return res;
    }

    private List<Documentinfo> filterByResearchGroup(List<Documentinfo> documentinfos, Long searcherid) {
        List<Documentinfo> res = new ArrayList<>();

        User user = userService.getUserById(searcherid);
        Long groupid = user.getGroupId();

        for (Documentinfo info : documentinfos) {
            if (info.getGroupid().equals(groupid))
                res.add(info);
        }
        return res;
    }
}
