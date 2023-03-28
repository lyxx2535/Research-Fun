package nju.researchfun.service;

import java.io.File;

public interface DocumentService {

    public File downloadFiles(String url, String filePath, String method, String name);

    public String upLoad();


}
