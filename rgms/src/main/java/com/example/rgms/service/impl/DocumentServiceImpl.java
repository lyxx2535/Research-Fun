package com.example.rgms.service.impl;
import java.io.*;
import java.net.*;

import com.example.rgms.service.DocumentService;

public class DocumentServiceImpl  implements DocumentService {

    @Override
    public File downloadFiles(String url, String filePath, String method,String name) {
        File file=null;

       try{
           System.out.println("文件路径"+filePath);
           file= new File(filePath);
           if(!file.exists()){
               file.mkdir();
           }
           FileOutputStream fileOutputStream=null;
           HttpURLConnection httpURLConnection=null;
           InputStream inputStream=null;
           URL httpurl= new URL(url);
           httpURLConnection=(HttpURLConnection) httpurl.openConnection();
           /*建立Http链接设置*/
           httpURLConnection.setRequestMethod(method);
           httpURLConnection.setDoInput(true);
           httpURLConnection.setDoOutput(true);
           httpURLConnection.setUseCaches(false);
           /*链接*/
           httpURLConnection.connect();
           inputStream =httpURLConnection.getInputStream();
           BufferedInputStream bufferedInputStream =new BufferedInputStream(inputStream);
           if (!filePath.endsWith("/")) {
               filePath += "/";
           }
           fileOutputStream = new FileOutputStream(filePath+name);
           BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
           byte[] buf = new byte[4096];
           int length = bufferedInputStream.read(buf);
           while(length!=-1){
               bufferedOutputStream.write(buf,0,length);
               length=bufferedInputStream.read(buf);
           }
           bufferedInputStream.close();
           bufferedOutputStream.close();
           httpURLConnection.disconnect();

           return   file;




       }catch (Exception e){
           e.printStackTrace();
           return file;

       }


    }

    @Override
    public String upLoad() {
        return null;
    }
}
