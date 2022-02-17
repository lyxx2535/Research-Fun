package com.example.rgms.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceImplTest {

    private  FileServiceImpl fileService;

    @Autowired
    public  FileServiceImplTest(FileServiceImpl fileService){
        this.fileService=fileService;
    }
    @Test
     void exterctKetwords() {
//        String test="计算机技术以其强大的实用性得到了快速发展，目前计算机技术在各 " +
//                "# 领域得到了普及，在各种生活场景中发挥着极其重要的作用，为人们生产生活提供了重要的技术保 " +
//                "# 障，改变了人们的生活方式，提高了人们的生活质量[1] 。计算机软件作为计算机重要的部分，发挥着计算机的核心 " +
//                "# 作用，而操作系统与应用软件作为计算机能力发挥的重要保障，通过计算机实现各种所需要的功能。2??计算机软件开发技术发展现状 " +
//                "# 随着计算机的快速稳定发展，为了适应计算机技术的发展需要，强化对计算机软件开发顺应了技术发展趋势，同时也得到了各领域的关注和重视[2] 。 " +
//                "# 在信息全球化的冲击和影响下，计算机软件开发技术遇到了难题，对于其技" +
//                "# 术的发展带来较大的困扰，具体主要体现在：（1）缺乏" +
//                "# 核心技术：与西方发达国家相比较而言，国内计算机软件" +
//                "# 开发技术起步晚，核心技术方面还有待研发，导致计算机" +
//                "# 软件技术整体水平不高，仍然处于相对较低的阶段，导致" +
//                "# 计算机软件开发质量难以得到保障；（2）人才结构不合" +
//                "# 理：在计算机软件开发过程中，需要技术人员提供技术方" +
//                "# 面的支持。从技术人员引进的角度来讲，往往需要大量的" +
//                "# 资金，并且需要较长的技术开发周期，鉴于当前这些问题";
//        String keywords=fileService.exterctKetwords(test);
//        System.out.println(keywo

    }
}