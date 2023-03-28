package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nju.researchfun.constant.FileType;
import nju.researchfun.entity.doc.*;
import nju.researchfun.entity.filepath.factory.FilePathFactory;
import nju.researchfun.entity.filepath.strategy.FilePath;
import nju.researchfun.entity.user.User;
import nju.researchfun.entity.weekly.History;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.InternalServerError;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.*;
import nju.researchfun.service.FileService;
import nju.researchfun.service.HistoryService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.document.Documentinfo;
import nju.researchfun.vo.document.ReadVO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private DocMapper docMapper;
    @Autowired
    private KeywordMapper keywordMapper;
    @Autowired
    private Doc_KeywordMapper doc_keywordMapper;
    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private Doc_AuthorMapper doc_authorMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private HistoryService historyService;


    @Override
    public String upload(MultipartFile file, FileType fileType) {
        FilePath filePath = FilePathFactory.getFilePathInstance(fileType);
        filePath.checkDir();
        String storedFilePath, randomUUID = UUID.randomUUID().toString();
        String fileName = randomUUID + "_" + file.getOriginalFilename();
        fileName = fileName.replaceAll(" ", "");
        storedFilePath = filePath.getStoredFilePath(fileName);
        // 保存文件
        try {
            file.transferTo(new File(storedFilePath));
            fileName = URLEncoder.encode(fileName, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerError(e.getMessage());
        }

        return filePath.getDownLoadPath(fileName);
    }


    public void download(String fileName, FileType fileType, HttpServletResponse response) {
        String storedFilePath;
        // 设置返回的文件类型并得到文件存储的文件夹
        FilePath filePath = FilePathFactory.getFilePathInstance(fileType);
        storedFilePath = filePath.getStoredFilePath(fileName);
        response.setContentType(filePath.getContentType());
        try {
            FileCopyUtils.copy(Files.newInputStream(Paths.get(storedFilePath)), response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * todo 出大问题 弔了
     */
    @Override
    public List<String> excutePdffile(String url) {
        // String filePath="E:\\计算机视觉\\作业3\\reading03-09.pdf";
        String filePath = url;
        try {
            return pdfParse(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解析失败");
            return null;
        }
    }


    @Override
    @Transactional
    public String saveDocumentinfo(String title, List<String> keywords, List<String> authors, Long userId, String url) {
        try {
            Doc doc = new Doc();
            doc.setTitle(title);
            doc.setUserid(userId);
            doc.setPdflink(url);

            docMapper.insert(doc);
            proDocAndKeyWord(doc.getDid(), keywords, authors);

            return "Save DocumentInfo Success";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public String advanceSaveDocumentinfo(Documentinfo documentinfo) {
        try {
            Doc doc = new Doc();

            doc.setTitle(documentinfo.getTitle());
            doc.setUserid(documentinfo.getUserid());
            doc.setPdflink(documentinfo.getPdflink());
            doc.setAbstracts(documentinfo.getAbstract());
            doc.setResearchDirection(documentinfo.getResearch_direction());
            doc.setPublisher(documentinfo.getPublisher());
            doc.setPublishdate(documentinfo.getDate());

            Long id;
            User user = userService.getUserById(doc.getUserid());
            id = user.getGroupId();
            doc.setGroupid(id);
            Date date = new Date(System.currentTimeMillis());
            doc.setDate(date);

            List<String> keywords = documentinfo.getKeywords();
            List<String> authors = documentinfo.getAuthors();

            docMapper.insert(doc);
            proDocAndKeyWord(doc.getDid(), keywords, authors);

            return "Save DocumentInfo Success";
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public String modifyDocumentinfo(Documentinfo documentinfoafter) {
        long did = documentinfoafter.getDid();
        try {
            Doc doc = docMapper.selectById(did);

            doc.setTitle(documentinfoafter.getTitle());
            doc.setAbstracts(documentinfoafter.getAbstract());
            doc.setResearchDirection(documentinfoafter.getResearch_direction());
            doc.setPublisher(documentinfoafter.getPublisher());
            doc.setPublishdate(documentinfoafter.getPublishdate());

            //把关系表删掉
            doc_authorMapper.delete(new QueryWrapper<>(Doc_Author.builder().did(did).build()));
            doc_keywordMapper.delete(new QueryWrapper<>(Doc_Keyword.builder().did(did).build()));

            List<String> keywords = documentinfoafter.getKeywords();
            List<String> authors = documentinfoafter.getAuthors();

            docMapper.updateById(doc);
            proDocAndKeyWord(doc.getDid(), keywords, authors);

            return "Modify Success!";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void record(ReadVO vo) {
        History history = History.builder().did(vo.getDid()).gid(vo.getGid()).uid(vo.getUid()).time(new Date()).build();
        historyService.record(history);
    }

    private void proDocAndKeyWord(Long did, List<String> keywords, List<String> authors) {
        long kid, auid;
        for (String s : keywords) { //处理关键词
            Keyword keyword = keywordMapper.selectOne(new QueryWrapper<>(Keyword.builder().kname(s).build()));
            if (keyword == null) {
                keyword = new Keyword();
                keyword.setKname(s);
                keywordMapper.insert(keyword);
            }
            kid = keyword.getKid();
            doc_keywordMapper.insert(Doc_Keyword.builder().kid(kid).did(did).build());
        }

        for (String auth : authors) {
            Author author = authorMapper.selectOne(new QueryWrapper<>(Author.builder().aname(auth).build()));
            if (author == null) {
                author = new Author();
                author.setAname(auth);
                authorMapper.insert(author);
            }
            auid = author.getAuId();
            doc_authorMapper.insert(Doc_Author.builder().auid(auid).did(did).build());
        }
    }


    //TODO 看逻辑
    public List<String> pdfParse(String pdfPath) throws Exception {
        PDDocument document = null;
        List<String> result = new ArrayList<>();
        try {
            URL url = new URL(pdfPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            document = PDDocument.load(inputStream);

            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("标题:" + info.getTitle());
            String title = info.getTitle();
            if (title == null) {//把文件名作为标题
                String fileName = pdfPath.substring(pdfPath.lastIndexOf("/") + 1);
                fileName = URLDecoder.decode(fileName, "utf-8");
                title = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
            }
            result.add(title);
            result.add(info.getAuthor());


            //获取内容信息
            PDFTextStripper pts = new PDFTextStripper();
            String textcontent = pts.getText(document);
            //System.out.println( "内容:" + textcontent );

            String information = textcontent.substring(0, Math.min(2000, textcontent.length()));

            if (information.length() == 0) {
                information = "由于格式限制，未解析到pdf内容";
            }
            System.out.println("信息");
            System.out.println(information);

            if (info.getKeywords() != null && info.getKeywords().length() != 0) {
                result.add(info.getKeywords());
            } else {
                //result.add("由于格式限制，暂不支持解析关键字");
                result.add("还欠缺NLP的实现");
                //TODO textRank实现
               /* String keywords = textRank.textRank(information);
                if (keywords != null && keywords.length() != 0) {
                    result.add(keywords);
                } else {
                    result.add("由于格式限制，暂不支持解析关键字");
                }*/
            }

            result.add(information);
            inputStream.close();
            httpURLConnection.disconnect();


            // 文档页面信息
        } catch (Exception e) {
            throw new InternalServerError(e.getMessage());
        } finally {
            if (null != document)
                document.close();
            return result;
        }
    }

    @Override
    public String uploadCommentImg(byte[] bytes) {
        FilePath filePath = FilePathFactory.getFilePathInstance(FileType.COMMENT_IMG);
        filePath.checkDir();
        String imgName = UUID.randomUUID() + ".jpg", imgPath = filePath.getStoredFilePath(imgName);
        OutputStream out = null;
        try {
            out = Files.newOutputStream(Paths.get(imgPath));
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath.getDownLoadPath(imgName);
    }

    @Override
    public String getLocalPath(String url) {
        String[] strs = url.split("/");
        if (strs.length < 2)
            throw new BadRequestException("找不到该文件");
        String fileType = strs[strs.length - 2], fileName = strs[strs.length - 1];
        FilePath filePath = FilePathFactory.getFilePathInstance(fileType);
        return filePath.getStoredFilePath(fileName);
    }

    @Override
    public void deleteCommentImg(String imgUrl) {
        String[] strs = imgUrl.split("/");
        if (strs.length < 2)
            throw new BadRequestException("批注图片url格式错误");
        String fileType = strs[strs.length - 2], fileName = strs[strs.length - 1];
        FilePath filePath = FilePathFactory.getFilePathInstance(fileType);
        if (!filePath.getFileType().equals(FileType.COMMENT_IMG))
            throw new BadRequestException("要删除的文件类型错误");
        String pathname = filePath.getStoredFilePath(fileName);
        File file = new File(pathname);
        file.delete();
    }


}
