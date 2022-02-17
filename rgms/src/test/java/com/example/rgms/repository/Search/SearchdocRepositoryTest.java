package com.example.rgms.repository.Search;

import com.example.rgms.entity.document.docEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchdocRepositoryTest {
    private final SearchdocRepository searchdocRepository;
    @Autowired
    public  SearchdocRepositoryTest(SearchdocRepository searchdocRepository){
        this.searchdocRepository=searchdocRepository;
    }

    @Test
    void findalldocs() {
        String title="test3";
        List<String> authors=new ArrayList<>();
        authors.add("Sam");
        authors.add("Kate");
        authors.add("James");
        List<docEntity> docs= searchdocRepository.findalldocs(title,authors);
        for(int i=0;i<docs.size();i++){
            System.out.println(docs.get(i).getTitle());
        }
        assertEquals(2,docs.size());
    }
}