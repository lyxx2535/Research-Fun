package nju.researchfun.entity.filepath.strategy;


import nju.researchfun.constant.FileType;


public class PortraitPath extends FilePath {


    public PortraitPath(String downloadUrl, String typePath, FileType fileType) {
        super(downloadUrl, typePath, fileType);
    }

    @Override
    public String getContentType() {
        return "image/png";
    }

}
