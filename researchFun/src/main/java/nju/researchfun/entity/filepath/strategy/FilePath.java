package nju.researchfun.entity.filepath.strategy;


import nju.researchfun.constant.FileType;

import java.io.File;

public abstract class FilePath {

    protected String downloadUrl;
    protected String typePath;
    protected FileType fileType;


    public FilePath(String downloadUrl, String typePath, FileType fileType) {
        this.downloadUrl = downloadUrl;
        this.typePath = typePath;
        this.fileType = fileType;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getStoredFilePath(String fileName) {
        return typePath + File.separator + fileName;
    }


    public String getDirPath() {
        return typePath + File.separator;
    }


    public String getDownLoadPath(String filename) {
        return downloadUrl + File.separator + fileType + File.separator + filename;
    }

    public void checkDir() {
        File file = new File(typePath);
        if (!file.exists())
            file.mkdirs();
    }


    public abstract String getContentType();
}
