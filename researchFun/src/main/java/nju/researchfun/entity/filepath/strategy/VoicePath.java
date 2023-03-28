package nju.researchfun.entity.filepath.strategy;


import nju.researchfun.constant.FileType;


public class VoicePath extends FilePath {


    public VoicePath(String downloadUrl, String typePath, FileType fileType) {
        super(downloadUrl, typePath, fileType);
    }

    @Override
    public String getContentType() {
        return "multipart/form-data";
    }


}
