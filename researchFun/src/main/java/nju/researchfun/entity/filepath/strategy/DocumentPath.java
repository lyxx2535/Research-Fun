package nju.researchfun.entity.filepath.strategy;


import nju.researchfun.constant.FileType;


public class DocumentPath extends FilePath {


    public DocumentPath(String downloadUrl, String typePath, FileType fileType) {
        super(downloadUrl, typePath, fileType);
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }


}
