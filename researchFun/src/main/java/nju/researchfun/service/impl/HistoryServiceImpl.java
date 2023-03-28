package nju.researchfun.service.impl;

import nju.researchfun.entity.weekly.History;
import nju.researchfun.mapper.HistoryMapper;
import nju.researchfun.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper historyMapper;
    @Override
    public void record(History history) {
        historyMapper.insert(history);
    }
}
