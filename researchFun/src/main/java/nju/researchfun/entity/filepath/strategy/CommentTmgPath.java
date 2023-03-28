package nju.researchfun.entity.filepath.strategy;


import nju.researchfun.constant.FileType;


public class CommentTmgPath extends FilePath {


    public CommentTmgPath(String downloadUrl, String typePath, FileType fileType) {
        super(downloadUrl, typePath, fileType);
    }

    @Override
    public String getContentType() {
        return "image/png";
    }

}
