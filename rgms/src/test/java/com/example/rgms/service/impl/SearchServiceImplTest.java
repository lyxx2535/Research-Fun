package com.example.rgms.service.impl;

import com.example.rgms.repository.Search.SearchauthorRepository;
import com.example.rgms.service.SearchService;
import com.example.rgms.vo.document.author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Service
class SearchServiceImplTest {
    @Autowired
    private SearchServiceImpl searchService;


    @Test
    void searchauthorByname() {



        List<author> res=searchService.searchauthorByname("Sam");
        assertEquals(2,res.get(0).getAuId());
    }
}