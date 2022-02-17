package com.example.rgms.service.impl;

import com.example.rgms.entity.document.authorEntity;
import com.example.rgms.entity.document.docEntity;
import com.example.rgms.entity.document.keywordEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.InternalServerError;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.Search.SearchauthorRepository;
import com.example.rgms.repository.Search.SearchdocRepository;
import com.example.rgms.repository.Search.SearchkeywordRepository;
import com.example.rgms.service.SearchService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.document.Doc;
import com.example.rgms.vo.document.Documentinfo;
import com.example.rgms.vo.document.author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

//import javax.sql.rowset.Predicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.criteria.Predicate;
@Service
public class SearchServiceImpl implements SearchService {
    private SearchauthorRepository searchauthorRepository;
    private SearchdocRepository searchdocRepository;
    private SearchkeywordRepository searchkeywordRepository;
    private UserService userService;

    @Autowired
    public SearchServiceImpl(SearchauthorRepository searchauthorRepository,
                             SearchdocRepository searchdocRepository,
                             SearchkeywordRepository searchkeywordRepository,
                             UserService userService) {
        this.searchauthorRepository = searchauthorRepository;
        this.searchdocRepository = searchdocRepository;
        this.searchkeywordRepository = searchkeywordRepository;
        this.userService=userService;
    }


    @Override
    public List<Doc> Searchdoc(int type, String keywords) {
        switch (type) {
            case 1:
                return searchBytitle(keywords);

            case 2:
                return searchBydoi(keywords);

            case 3:
                return searchByauthor(keywords);

            default:
                return null;
        }
    }

    @Override
    public List<Doc> searchBytitle(String keywords) {
        return null;
    }

    @Override
    public List<Doc> searchBydoi(String keywords) {
        return null;
    }

    @Override
    public List<Doc> searchByauthor(String keywords) {
        return null;
    }

    @Override
    public List<author> searchauthorByname(String name) {
        List<authorEntity> authpo = searchauthorRepository.findauthorEntitiesByAname(name);
        List<author> result = new ArrayList<author>();
        for (int i = 0; i < authpo.size(); i++) {
            // System.out.println("auID: "+authpo.get(i).getAuId());
            //  System.out.println("Name: "+authpo.get(i).getAname());
            result.add(authpo.get(i).toauthor());
        }
        return result;
    }

    @Override
    public Page<docEntity> searchDoc(int type, String keyword, int currentPage, int pageSize) {
        System.out.println("类型" + type);
        System.out.println("关键字" + keyword);

        switch (type) {
            case 1:
                return searchdocBytitle(keyword, currentPage, pageSize);

            case 2:
                return searchdocBykeywords(keyword, currentPage, pageSize);
            case 3:
                return searchdocByauthor(keyword, currentPage, pageSize);
            case 4:
                return searchdocByuser(keyword, currentPage, pageSize);
            default:
                throw new BadRequestException("请求失败,请重新查询");
        }
    }

    @Override
    public Page<docEntity> searchdocBytitle(String keyword, int currentPage, int pageSize) {
    /*public Page<MessageVO> (Long userId, int currentPage, int pageSize) {
        Page<MessageEntity> entityPage=messageRepository.findByRecipientOrderByDateDesc(userId, PageRequest.of(currentPage, pageSize));
        List<MessageVO> list=new ArrayList<>(entityPage.getNumberOfElements());
        for(MessageEntity messageEntity : entityPage){
            list.add(messageEntity.toMessageVO(userService));
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entityPage.getTotalElements());
    }*/

        try {
            Page<docEntity> result = searchdocRepository.findByTitle(keyword, PageRequest.of(currentPage, pageSize));
            List<docEntity> list = new ArrayList<>(result.getNumberOfElements());
            for (docEntity entity : result) {
                // System.out.println(entity.getTitle());
                // System.out.println(entity.getDid());
                list.add(entity);
            }
            System.out.println("总数: "+result.getTotalElements());

            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), result.getTotalElements());


        } catch (Exception e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @Override
    public Page<keywordEntity> searchKeywordsBykname(String kname, int currentPage, int pageSize) {
        /*public Page<MessageVO(Long userId, int currentPage, int pageSize) {
        Page<MessageEntity> entityPage=messageRepository.findByRecipientOrderByDateDesc(userId, PageRequest.of(currentPage, pageSize));
        List<MessageVO> list=new ArrayList<>(entityPage.getNumberOfElements());
        for(MessageEntity messageEntity : entityPage){
            list.add(messageEntity.toMessageVO(userService));
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entityPage.getTotalElements());
    }*/
        Page<keywordEntity> res = searchkeywordRepository.findByKname(kname, PageRequest.of(currentPage, pageSize));
        List<keywordEntity> list = new ArrayList<>(res.getNumberOfElements());

        for (keywordEntity entity : res) {
            // System.out.println(entity.getKid());
            // System.out.println(entity.getKname());
            list.add(entity);
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), res.getTotalElements());
    }

    @Override
    public Page<docEntity> searchdocBykeywords(String kname, int currentPage, int pageSize) {
        Page<docEntity> entities = searchdocRepository.findBykeyword(kname,PageRequest.of(currentPage,pageSize));
        System.out.println("总数: "+entities.getTotalElements());
        List<docEntity> list=new ArrayList<>(entities.getNumberOfElements());
        for(docEntity entity:entities){
            list.add(entity);
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entities.getTotalElements());
    }

    @Override
    public Page<docEntity> searchdocByauthor(String keyword, int currentPage, int pageSize) {
        try {
            Page<docEntity> entities= searchdocRepository.findByauthor(keyword,PageRequest.of(currentPage,pageSize));
            System.out.println("总数: "+entities.getTotalElements());
            List<docEntity> list=new ArrayList<>(entities.getNumberOfElements());
            for(docEntity entity:entities){
                list.add(entity);
            }
            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entities.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }


    }

    @Override
    public Page<docEntity> searchdocByuser(String keyword, int currenrPage, int pageSize) {
        int userId = Integer.parseInt(keyword);
        Page<docEntity> docEntities = searchdocRepository.findByUserid(userId, PageRequest.of(currenrPage, pageSize));
        List<docEntity> list = new ArrayList<>(docEntities.getNumberOfElements());
        for (docEntity entity : docEntities) {
            System.out.println(entity.getTitle());
            list.add(entity);
        }
        return new PageImpl<>(list, PageRequest.of(currenrPage, pageSize), docEntities.getTotalElements());

    }

    @Override
    public Page<docEntity> searchAllthedoc(int currentPage, int pageSize) {
        try {
            Page<docEntity> docEntities = searchdocRepository.findAll(PageRequest.of(currentPage,pageSize));
            List<docEntity> list=new ArrayList<>(docEntities.getNumberOfElements());
            System.out.println("总数: "+docEntities.getTotalElements());
            for (docEntity entity : docEntities) {
                list.add(entity);
            }
            return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), docEntities.getTotalElements());

        } catch (Exception e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @Override
    public Page<docEntity> advanceSearch(Documentinfo documentinfo, int currentPage, int pageSize) {
        String title=documentinfo.getTitle();
        List<String> authors=documentinfo.getAuthors();
        List<String>  keywords=documentinfo.getKeywords();
        System.out.println(title);
        List<docEntity> docs=searchdocRepository.findalldocs(title,authors);
        return new PageImpl<>(docs, PageRequest.of(currentPage,pageSize),docs.size());

    }

   // @Override
//    public Page<Documentinfo> searchdoc(int type, String keyword, int currentPage, int pageSize,String direction,int searcherid) {
//        Page<docEntity> docs;
//
//      //  System.out.println("研究方向是:" +direction);
//
//        switch (type) {
//            case 0:
//                docs=searchAllthedoc(currentPage,pageSize);
//                break;
//            case 1:
//                docs = searchdocBytitle(keyword, currentPage, pageSize);
//                break;
//
//            case 2:
//                docs = searchdocBykeywords(keyword, currentPage, pageSize);
//                break;
//            case 3:
//                docs = searchdocByauthor(keyword, currentPage, pageSize);
//                break;
//            case 4:
//                docs = searchdocByuser(keyword, currentPage, pageSize);
//                break;
//            default:
//                throw new BadRequestException("请求失败,请重新查询");
//        }
//
//        List<Documentinfo> result=new ArrayList<>(docs.getNumberOfElements());
//      //  System.out.println("目前结果"+result.size());
//        for(docEntity entity:docs){
//            Documentinfo documentinfo=new Documentinfo();
//            documentinfo.setDid(entity.getDid());
//            documentinfo.setTitle(entity.getTitle());
//            // documentinfo.setAbstract(entity.getAbstract());
//            documentinfo.setDate(entity.getDate());
//            documentinfo.setPdflink(entity.getPdflink());
//            documentinfo.setDoi(entity.getDoi());
//            documentinfo.setUserid(entity.getUserid());
//            documentinfo.setResearch_direction(entity.getResearch_direction());
//
//            Long userid=new Long((long) entity.getUserid());
//            UserEntity userEntity=userService.getUserEntityById(userid);
//            String username=userEntity.getUsername();
//
//            documentinfo.setUsername(username);
//
//
//            int did=entity.getDid();
//            List<keywordEntity> keywordEntities=searchkeywordRepository.findBydId(did);
//            List<String> keywords=new ArrayList<>();
//            for(int i=0;i<keywordEntities.size();i++){
//                keywords.add(keywordEntities.get(i).getKname());
//            }
//            documentinfo.setKeywords(keywords);
//
//            List<authorEntity> authorEntities=searchauthorRepository.findauthorEntitiesBydId(did);
//            List<String> authors=new ArrayList<>();
//            for(int j=0;j<authorEntities.size();j++){
//                authors.add(authorEntities.get(j).getAname());
//            }
//            documentinfo.setAuthors(authors);
//
//            result.add(documentinfo);
//        }
//        //进行过滤
//
//        int formerSize=result.size();
//        if(!direction.equals("null")){
//            result=this.fliterBydirection(result,direction);
//        }
//        result=this.filterByResearchGroup(result,searcherid);
//        int currentSize=result.size();
//        int lost=formerSize-currentSize;
//
//        return new PageImpl<>(result,PageRequest.of(currentPage,pageSize),docs.getTotalElements()-lost);
//    }

    @Override
    public Documentinfo searchCompletedoc(int did) {

        try {
            docEntity entity=searchdocRepository.findByDid(did);
            Documentinfo documentinfo=new Documentinfo();

            //设置基本信息
            documentinfo.setDid(entity.getDid());
            documentinfo.setTitle(entity.getTitle());
            // documentinfo.setAbstract(entity.getAbstract());
            documentinfo.setDate(entity.getDate());
            documentinfo.setPdflink(entity.getPdflink());
            documentinfo.setDoi(entity.getDoi());
            documentinfo.setUserid(entity.getUserid());
            documentinfo.setAbstract(entity.getAbstract());
            documentinfo.setResearch_direction(entity.getResearch_direction());
            documentinfo.setPublishdate(entity.getPublisdate());
            documentinfo.setPublisher(entity.getPublisher());
            //设置上传者信息
            Long userid=new Long((long) entity.getUserid());
            UserEntity userEntity=userService.getUserEntityById(userid);
            String username=userEntity.getUsername();
            documentinfo.setUsername(username);

            //设置作者和关键字信息
            List<keywordEntity> keywordEntities=searchkeywordRepository.findBydId(did);
            List<String> keywords=new ArrayList<>();
            //System.out.println("关键词数量："+keywordEntities.size());

            for(int i=0;i<keywordEntities.size();i++){
                keywords.add(keywordEntities.get(i).getKname());
               // System.out.println("关键词"+ i+keywordEntities.get(i).getKname());
            }
            documentinfo.setKeywords(keywords);

            List<authorEntity> authorEntities=searchauthorRepository.findauthorEntitiesBydId(did);
            List<String> authors=new ArrayList<>();
            for(int j=0;j<authorEntities.size();j++){
                authors.add(authorEntities.get(j).getAname());
            }
            documentinfo.setAuthors(authors);
            return  documentinfo;

        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Page<Documentinfo> searchdoc_v2(int type, String keyword, int currentPage, int pageSize, String direction, int searcherid) {
        try{
            List<docEntity> docEntities;
            switch (type) {
                case 0:
                   docEntities=searchdocRepository.findAll();
                    break;
                case 1:
                   docEntities=searchdocRepository.findByTitle(keyword);
                    break;

                case 2:
                    docEntities=searchdocRepository.findByKeywords(keyword);
                    break;
                case 3:

                    docEntities=searchdocRepository.findByauthor(keyword);
                    break;
                case 4:
                    int id= Integer.parseInt(keyword);

                    docEntities=searchdocRepository.findByUserid(id);
                    break;
                default:
                    throw new BadRequestException("请求失败,请重新查询");
            }
            List<Documentinfo> documentinfos=new ArrayList<>();
            for(docEntity entity:docEntities){
                Documentinfo documentinfo= entity.toDocumentinfo();
                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();

                documentinfo.setUsername(username);

                int did=entity.getDid();
                List<keywordEntity> keywordEntities=searchkeywordRepository.findBydId(did);
                List<String> keywords=new ArrayList<>();
                for(int i=0;i<keywordEntities.size();i++){
                    keywords.add(keywordEntities.get(i).getKname());
                }
                documentinfo.setKeywords(keywords);

                List<authorEntity> authorEntities=searchauthorRepository.findauthorEntitiesBydId(did);
                List<String> authors=new ArrayList<>();
                for(int j=0;j<authorEntities.size();j++){
                    authors.add(authorEntities.get(j).getAname());
                }
                documentinfo.setAuthors(authors);
                documentinfos.add(documentinfo);
            }


            if(!direction.equals("null")){
                documentinfos=this.fliterBydirection(documentinfos,direction);
            }
            documentinfos=this.filterByResearchGroup(documentinfos,searcherid);

            List<Documentinfo> result=new ArrayList<>();
            if(currentPage>(documentinfos.size()/pageSize)){
                throw  new BadRequestException("超过最大页码,请重新选择");
            }
            int index=(currentPage)*pageSize;
            for(int i=0;i<pageSize;i++){
                if(index+i<documentinfos.size()) {
                    result.add(documentinfos.get(index + i));
                }
            }
            return new PageImpl<>(result,PageRequest.of(currentPage,pageSize),documentinfos.size());
        }catch (Exception e){
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }


    @Override
    public Page<Documentinfo> searchDocByUploader(int type, String keyword, int currentPage, int pageSize, String direction, int searcherid, int uploaderid) {
        try{
            List<docEntity> docEntities;
            switch (type) {
                case 0:
                    docEntities=searchdocRepository.findAll();
                    break;
                case 1:
                    docEntities=searchdocRepository.findByTitle(keyword);
                    break;

                case 2:
                    docEntities=searchdocRepository.findByKeywords(keyword);
                    break;
                case 3:

                    docEntities=searchdocRepository.findByauthor(keyword);
                    break;
                case 4:
                    int id= Integer.parseInt(keyword);

                    docEntities=searchdocRepository.findByUserid(id);
                    break;
                default:
                    throw new BadRequestException("请求失败,请重新查询");
            }
            List<Documentinfo> documentinfos=new ArrayList<>();
            for(docEntity entity:docEntities){
                Documentinfo documentinfo= entity.toDocumentinfo();
                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();

                documentinfo.setUsername(username);

                int did=entity.getDid();
                List<keywordEntity> keywordEntities=searchkeywordRepository.findBydId(did);
                List<String> keywords=new ArrayList<>();
                for(int i=0;i<keywordEntities.size();i++){
                    keywords.add(keywordEntities.get(i).getKname());
                }
                documentinfo.setKeywords(keywords);

                List<authorEntity> authorEntities=searchauthorRepository.findauthorEntitiesBydId(did);
                List<String> authors=new ArrayList<>();
                for(int j=0;j<authorEntities.size();j++){
                    authors.add(authorEntities.get(j).getAname());
                }
                documentinfo.setAuthors(authors);
                documentinfos.add(documentinfo);
            }


            if(!direction.equals("null")){
                documentinfos=this.fliterBydirection(documentinfos,direction);
            }
            documentinfos=this.filterByResearchGroup(documentinfos,searcherid);
            documentinfos=this.fliterByUploader(documentinfos,uploaderid);

            List<Documentinfo> result=new ArrayList<>();
            if(currentPage>(documentinfos.size()/pageSize)){
                throw  new BadRequestException("超过最大页码,请重新选择");
            }
            int index=(currentPage)*pageSize;
            for(int i=0;i<pageSize;i++){
                if(index+i<documentinfos.size()) {
                    result.add(documentinfos.get(index + i));
                }
            }
            return new PageImpl<>(result,PageRequest.of(currentPage,pageSize),documentinfos.size());
        }catch (Exception e){
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }





        }


    /*
* 根据研究方向和上传者进行过滤
*根据研究组进行过滤
*
* */
    private  List<Documentinfo> fliterBydirection(List<Documentinfo> documentinfos,String direction){
        Iterator<Documentinfo> iterator=documentinfos.iterator();
        while(iterator.hasNext()){
            Documentinfo info= iterator.next();
            if(!info.getResearch_direction().equals(direction)){
                iterator.remove();
            }
        }
        return documentinfos;
    }

    private List<Documentinfo> fliterByUploader(List<Documentinfo> documentinfos,int userid){
        Iterator<Documentinfo> iterator=documentinfos.iterator();
        while(iterator.hasNext()){
            Documentinfo info= iterator.next();
            if(info.getUserid()!=userid){
                iterator.remove();
            }
        }
        return documentinfos;
    }
    //这里怎么改还没想好
    private List<Documentinfo> filterByResearchGroup(List<Documentinfo> documentinfos,int searcherid) {
//        List<Integer> users=userRepository.findUseridBySameGroup(searcherid);
//        Iterator<Documentinfo> iterator=documentinfos.iterator();
//        while(iterator.hasNext()){
//            Documentinfo info= iterator.next();
//            if(!users.contains(info.getUserid())){
//                iterator.remove();
//            }
//        }
        Long id= new Long(searcherid);
        UserEntity userEntity=userService.getUserEntityById(id);
        int groupid=userEntity.getGroupId().intValue();
        Iterator<Documentinfo> iterator=documentinfos.iterator();
            while(iterator.hasNext()){
            Documentinfo info= iterator.next();
            if(info.getGroupid()!=groupid){
                iterator.remove();
            }
        }

        return documentinfos;
    }




    /*doc组合查询实现Specification接口*/

    private Specification<docEntity> getListSpec(Documentinfo documentinfo){
        return  (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList= new ArrayList<>();
            List<Predicate> titlePredicate=new ArrayList<>();
            String title=documentinfo.getTitle();
            if(!StringUtils.isEmpty(title)){
                titlePredicate.add(criteriaBuilder.like(root.get("title"),"%"+title+"%"));
            }


            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }


}