package com.example.rgms.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.test.StepVerifier;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Service
class DocumentServiceImplTest {

    @Autowired
    private DocumentServiceImpl documentServiceimpl ;


/*文件在服务器，url，*/
    @Test
    void downloadFiles() {
        this.documentServiceimpl= new DocumentServiceImpl();
        String photourl="https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
        String filename=photourl.substring(photourl.lastIndexOf("/"));
        System.out.println("filename:"+filename);
        String filepath="d:";
        File file=documentServiceimpl.downloadFiles(photourl,filepath+filename,"GET","10.pdf");
        if(file==null){
            System.out.println("空");
        }
        assertEquals("/bd_logo1_31bdc765.png",filename);

    }
}