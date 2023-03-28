package nju.researchfun.entity.filepath.factory;

import nju.researchfun.constant.FileType;
import nju.researchfun.entity.filepath.strategy.CommentTmgPath;
import nju.researchfun.entity.filepath.strategy.DocumentPath;
import nju.researchfun.entity.filepath.strategy.FilePath;
import nju.researchfun.entity.filepath.strategy.PortraitPath;
import nju.researchfun.entity.filepath.strategy.VoicePath;
import nju.researchfun.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilePathFactory {

    private static String portraitPath;
    private static String documentPath;
    private static String commentImgPath;
    private static String voicePath;
    private static String downloadUrl;

    private FilePathFactory() {
    }

    // 静态成员变量的自动注入
    @Value(value = "${file.portrait-path}")
    public void setPortraitPath(String portraitPath) {
        FilePathFactory.portraitPath = portraitPath;
    }

    @Value(value = "${file.document-path}")
    public void setDocumentPath(String documentPath) {
        FilePathFactory.documentPath = documentPath;
    }

    @Value(value = "${file.comment-img-path}")
    public void setCommentImgPath(String commentImgPath) {
        FilePathFactory.commentImgPath = commentImgPath;
    }

    @Value(value = "${file.voice-path}")
    public void setVoicePath(String voicePath) {
        FilePathFactory.voicePath = voicePath;
    }

    @Value(value = "${file.download-url}")
    public void setDownloadUrl(String downloadUrl) {
        FilePathFactory.downloadUrl = downloadUrl;
    }


    //////////////////////////////////////////////////////////

    public static FilePath getFilePathInstance(FileType fileType) {
        FilePath filePath;
        switch (fileType) {
            case PORTRAIT:
                filePath = new PortraitPath(downloadUrl, portraitPath, FileType.PORTRAIT);
                break;
            case PDF_DOCUMENT:
                filePath = new DocumentPath(downloadUrl, documentPath, FileType.PDF_DOCUMENT);
                break;
            case COMMENT_IMG:
                filePath = new CommentTmgPath(downloadUrl, commentImgPath, FileType.COMMENT_IMG);
                break;
            case VOICE:
                filePath = new VoicePath(downloadUrl, voicePath, FileType.VOICE);
                break;
            default:
                throw new BadRequestException("不支持的文件种类！");
        }
        return filePath;
    }

    public static FilePath getFilePathInstance(String fileType) {
        FilePath filePath;
        if (fileType.equals(FileType.PORTRAIT.toString())) {
            filePath = new PortraitPath(downloadUrl, portraitPath, FileType.PORTRAIT);
        } else if (fileType.equals(FileType.PDF_DOCUMENT.toString())) {
            filePath = new DocumentPath(downloadUrl, documentPath, FileType.PDF_DOCUMENT);
        } else if (fileType.equals(FileType.COMMENT_IMG.toString())) {
            filePath = new CommentTmgPath(downloadUrl, commentImgPath, FileType.COMMENT_IMG);
        } else if (fileType.equals(FileType.VOICE.toString())) {
            filePath = new VoicePath(downloadUrl, voicePath, FileType.VOICE);
        } else
            throw new BadRequestException("不支持的文件种类！");

        return filePath;
    }
}
