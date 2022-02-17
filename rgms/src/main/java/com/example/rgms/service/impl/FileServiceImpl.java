package com.example.rgms.service.impl;

import com.example.rgms.constant.FileType;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.InternalServerError;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.Search.*;
import com.example.rgms.repository.user.UserRepository;
import com.example.rgms.service.FileService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.document.Documentinfo;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import com.example.rgms.entity.document.*;
import sun.misc.BASE64Decoder;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.net.*;

@Service
public class FileServiceImpl implements FileService {
    @Value(value = "${file.portrait-path}")
    private String portraitPath;
    @Value(value = "${file.document-path}")
    private String documentPath;
    @Value(value = "${file.download-url}")
    private String downloadUrl;
    @Value(value = "${file.comment-img-path}")
    private String commentImgPath;

    private  final SearchkeywordRepository searchkeywordRepository;
    private final SearchdocRepository searchdocRepository;
    private final SearchauthorRepository searchauthorRepository;
    private final  author_docRepository authordocRepository;
    private final Keyword_DocRepository keyword_docRepository;
    private final UserRepository userRepository;
    private final TextRank textRank;
    private final UserService userService;
    @Autowired
    public  FileServiceImpl(SearchauthorRepository searchauthorRepository, SearchkeywordRepository searchkeywordRepository,
                            SearchdocRepository searchdocRepository,
                            author_docRepository authordocRepository,
                            Keyword_DocRepository keyword_docRepository,
                            UserRepository userRepository, TextRank textRank,
                            UserService userService){
        this.searchauthorRepository=searchauthorRepository;
        this.searchkeywordRepository=searchkeywordRepository;
        this.searchdocRepository=searchdocRepository;
        this.authordocRepository=authordocRepository;
        this.keyword_docRepository=keyword_docRepository;
        this.userRepository=userRepository;
        this.textRank = textRank;
        this.userService = userService;
    }




    @Override
    public String upload(MultipartFile file, FileType fileType) {
        checkDirectory();

        String storedFilePath, randomUUID=UUID.randomUUID().toString(), fileName=randomUUID+"_"+file.getOriginalFilename();
        switch (fileType) {
            case PORTRAIT:
                storedFilePath = portraitPath+File.separator+fileName;
                break;
            case PDF_DOCUMENT:
                storedFilePath=documentPath+File.separator+fileName;
                break;
            default:
                throw new BadRequestException("上传了不支持的文件种类！");
        }

        // 保存文件
        try{
            file.transferTo(new File(storedFilePath));
        } catch (Exception e){
            e.printStackTrace();
            throw new InternalServerError(e.getMessage());
        }

        return downloadUrl+"/"+fileType.toString()+ "/" +fileName;
    }

    @Override
    public String uploadCommentImg(byte[] bytes) {
        checkDirectory();

        String imgName=UUID.randomUUID().toString() + ".jpg", imgPath=commentImgPath+File.separator+imgName;
        OutputStream out=null;
        try {
            out = new FileOutputStream(imgPath);
            out.write(bytes);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return downloadUrl + File.separator + FileType.COMMENT_IMG.toString() + File.separator + imgName;
    }

    @Override
    public void deleteCommentImg(String imgUrl) {
        String[] strs=imgUrl.split("/");
        if(strs.length<2)
            throw new BadRequestException("批注图片url格式错误");
        String fileType=strs[strs.length-2], fileName=strs[strs.length-1];
        if(!fileType.equals(FileType.COMMENT_IMG.toString()))
            throw new BadRequestException("要删除的文件类型错误");
        File file=new File(commentImgPath+File.separator+fileName);
        file.delete();
    }


    @Override
    public void download(String fileName, FileType fileType, HttpServletResponse response) {
        String dirPath;
        // 设置返回的文件类型并得到文件存储的文件夹
        switch (fileType){
            case PORTRAIT:
                dirPath=portraitPath+File.separator;
                response.setContentType("image/png");
                break;
            case PDF_DOCUMENT:
                dirPath=documentPath+File.separator;
                response.setContentType("application/pdf");
                break;
            case COMMENT_IMG:
                dirPath=commentImgPath+File.separator;
                response.setContentType("image/png");
                break;
            default:
                throw new RuntimeException("该文件种类无法识别！");
        }

        try {
            FileCopyUtils.copy(new FileInputStream(dirPath+fileName), response.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(MultipartFile newFile, String originalFileUrl) {
        String[] strs=originalFileUrl.split(File.separator);
        if(strs.length<2)
            throw new BadRequestException("url格式错误");
        String fileType=strs[strs.length-2], fileName=strs[strs.length-1];
        String directory;
        if(fileType.equals(FileType.COMMENT_IMG.toString()))
            directory=commentImgPath;
        else if(fileType.equals(FileType.PORTRAIT.toString()))
            directory=portraitPath;
        else if(fileType.equals(FileType.PDF_DOCUMENT.toString()))
            directory=documentPath;
        else
            throw new BadRequestException("url格式错误");
        File file=new File(directory+File.separator+fileName);
        file.delete();
        try {
            newFile.transferTo(file);
        } catch (IOException e) {
            throw new InternalServerError(e.getMessage());
        }
    }


    @Override
    public List<String> excutePdffile(String url) {
       // String filePath="E:\\计算机视觉\\作业3\\reading03-09.pdf";
        String filesavepath="E:\\计算机视觉\\作业3\\image\\";
        String filePath=url;
        try {
           return pdfParse(filePath, filesavepath);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("解析失败");
            return null;
        }
    }

    @Override
    public String saveDocumentinfo(String title, List<String> keywords, List<String> authors, Long userId, String url) {
       try {
            docEntity doc = new docEntity();
            doc.setTitle(title);
            doc.setUserid(userId.intValue());
            doc.setPdflink(url);


            int did = searchdocRepository.save(doc).getDid();
            System.out.println("dId:" + did);
            int kid = 0;
            int auid = 0;

            for (int i = 0; i < keywords.size(); i++) {
                if (searchkeywordRepository.existsByKname(keywords.get(i))) {
                    kid = searchkeywordRepository.findByKname(keywords.get(i)).getKid();
                    System.out.println("kid");
                } else {
                    keywordEntity key = new keywordEntity();
                    key.setKname(keywords.get(i));
                    kid = searchkeywordRepository.save(key).getKid();
                    Keyword_DocEntity keyword_docEntity=new Keyword_DocEntity();
                    keyword_docEntity.setKid(kid);
                    keyword_docEntity.setDid(did);
                    int savekid=keyword_docRepository.save(keyword_docEntity).getKid();
                    System.out.println(savekid);

                }
            }

            for (int i = 0; i < authors.size(); i++) {
                if (searchauthorRepository.existsByAname(authors.get(i))) {
                    auid=searchauthorRepository.findByAname(authors.get(i)).getAuId();
                } else {
                    authorEntity author=new authorEntity();
                    author.setAname(authors.get(i));
                    auid=searchauthorRepository.save(author).getAuId();
                    author_docEntity author_doc=new author_docEntity();
                    author_doc.setAuid(auid);
                    author_doc.setDid(did);
                    author_doc=authordocRepository.save(author_doc);
                }
                System.out.println("auid: "+auid);
            }
            return  "Save DocumentInfo Success";
        }catch (Exception e){
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public String advanceSaveDocumentinfo(Documentinfo documentinfo) {
        try{
        docEntity doc = new docEntity();

        doc.setTitle(documentinfo.getTitle());
        doc.setUserid(documentinfo.getUserid());
        doc.setPdflink(documentinfo.getPdflink());
        doc.setAbstract(documentinfo.getAbstract());
        doc.setResearch_direction(documentinfo.getResearch_direction());
        doc.setPublisher(documentinfo.getPublisher());
        doc.setPublisdate(documentinfo.getDate());

        int id=0;
        //UserEntity userEntity=userRepository.findById(doc.getUserid());
        UserEntity userEntity= userService.getUserEntityById(new Long(doc.getUserid())) ;
        Long groupid=userEntity.getGroupId();
        id=groupid.intValue();
        doc.setGroupid(id);



        Date date= new Date(System.currentTimeMillis());
        doc.setDate(date);
        List<String> keywords=documentinfo.getKeywords();
        List<String> authors=documentinfo.getAuthors();


        int did = searchdocRepository.save(doc).getDid();
        System.out.println("dId:" + did);
        int kid = 0;
        int auid = 0;

        for (int i = 0; i < keywords.size(); i++) {
            if (searchkeywordRepository.existsByKname(keywords.get(i))) {
                kid = searchkeywordRepository.findByKname(keywords.get(i)).getKid();
                System.out.println("kid");
            } else {
                keywordEntity key = new keywordEntity();
                key.setKname(keywords.get(i));
                kid = searchkeywordRepository.save(key).getKid();
                Keyword_DocEntity keyword_docEntity=new Keyword_DocEntity();
                keyword_docEntity.setKid(kid);
                keyword_docEntity.setDid(did);
                int savekid=keyword_docRepository.save(keyword_docEntity).getKid();
                System.out.println(savekid);

            }
        }

        for (int i = 0; i < authors.size(); i++) {
            if (searchauthorRepository.existsByAname(authors.get(i))) {
                auid=searchauthorRepository.findByAname(authors.get(i)).getAuId();
            } else {
                authorEntity author=new authorEntity();
                author.setAname(authors.get(i));
                auid=searchauthorRepository.save(author).getAuId();
                author_docEntity author_doc=new author_docEntity();
                author_doc.setAuid(auid);
                author_doc.setDid(did);
                author_doc=authordocRepository.save(author_doc);
            }
            System.out.println("auid: "+auid);
        }
        return  "Save DocumentInfo Success";
    }catch (Exception e){
        e.printStackTrace();
        throw new NotFoundException(e.getMessage());
    }
    }

    @Override
    public String modifyDocumentinfo( Documentinfo documentinfoafter) {
        int did=documentinfoafter.getDid();
        try {
            docEntity docEntity= searchdocRepository.findByDid(did);

            docEntity.setTitle(documentinfoafter.getTitle());
            docEntity.setAbstract(documentinfoafter.getAbstract());
            docEntity.setResearch_direction(documentinfoafter.getResearch_direction());
            docEntity.setPublisher(documentinfoafter.getPublisher());
            docEntity.setPublisdate(documentinfoafter.getPublishdate());

            authordocRepository.deleteAllByDid(did);
            keyword_docRepository.deleteAllByDid(did);

            List<String> keywords=documentinfoafter.getKeywords();
            List<String> authors=documentinfoafter.getAuthors();


           // int did = searchdocRepository.save(doc).getDid();
            System.out.println("dId:" + did);
            int kid = 0;
            int auid = 0;

            for (int i = 0; i < keywords.size(); i++) {
                if (searchkeywordRepository.existsByKname(keywords.get(i))) {
                    kid = searchkeywordRepository.findByKname(keywords.get(i)).getKid();
//                    System.out.println("kid");
                } else {
                    keywordEntity key = new keywordEntity();
                    key.setKname(keywords.get(i));
                    kid = searchkeywordRepository.save(key).getKid();
                }
                Keyword_DocEntity keyword_docEntity=new Keyword_DocEntity();
                keyword_docEntity.setKid(kid);
                keyword_docEntity.setDid(did);
                keyword_docRepository.save(keyword_docEntity);
//                System.out.println(savekid);
            }

            for (int i = 0; i < authors.size(); i++) {
                if (searchauthorRepository.existsByAname(authors.get(i))) {
                    auid=searchauthorRepository.findByAname(authors.get(i)).getAuId();
                } else {
                    authorEntity author=new authorEntity();
                    author.setAname(authors.get(i));
                    auid=searchauthorRepository.save(author).getAuId();
                }
                author_docEntity author_doc=new author_docEntity();
                author_doc.setAuid(auid);
                author_doc.setDid(did);
                authordocRepository.save(author_doc);
//                System.out.println("auid: "+auid);
            }
            return "Modify Success!";
        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }
    }


    public List<String> pdfParse( String pdfPath, String imgSavePath ) throws Exception
    {
        InputStream input = null;
        File pdfFile = new File( pdfPath );
        PDDocument document = null;
        List<String> result= new ArrayList<>();
        try{
            URL url= new URL(pdfPath);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            document=PDDocument.load(inputStream);


            //input = new FileInputStream( pdfFile );
            //加载 pdf 文档
            //document = PDDocument.load( input );

            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println( "标题:" + info.getTitle() );
            String title=info.getTitle();
            result.add(title);
            result.add(info.getAuthor());



            //



            //获取内容信息
            PDFTextStripper pts = new PDFTextStripper();
            String textcontent = pts.getText( document );
            //System.out.println( "内容:" + textcontent );

            String information= textcontent.substring(0,2000);

            if(information.length()==0){
                information="由于格式限制，未解析到pdf内容";
            }
            System.out.println("信息");
            System.out.println(information);

            if(info.getKeywords()!=null && info.getKeywords().length()!=0) {
                result.add(info.getKeywords());
            }
            else{
               String keywords=textRank.textRank(information);
               if(keywords!=null && keywords.length()!=0){
                   result.add(keywords);
               }
               else{
                   result.add("由于格式限制，暂不支持解析关键字");
               }
            }

            result.add(information);

            Splitter splitter=new Splitter();
            splitter.setStartPage(0);
            splitter.setEndPage(15);
            List<PDDocument>  pages=splitter.split(document);
            for(int i=0;i<pages.size();i++){
                String content =pts.getText(pages.get(i));
                System.out.println("第"+i+"页内容");
                System.out.println(content);
            }
            inputStream.close();
            httpURLConnection.disconnect();




            /** 文档页面信息 **/
//
        }catch( Exception e)
        {
            throw e;
        }finally{
            if( null != input )
                input.close();

            if( null != document )
                document.close();
            return  result;
        }
    }






    private void checkDirectory(){
        File file=new File(portraitPath);
        if(!file.exists())
            file.mkdirs();
        file=new File(documentPath);
        if(!file.exists())
            file.mkdirs();
        file=new File(commentImgPath);
        if(!file.exists())
            file.mkdirs();
    }





}
