package nju.researchfun.service;

import nju.researchfun.entity.weekly.Weekly;
import nju.researchfun.vo.weeklywriting.WeeklySaveVO;

import javax.servlet.http.HttpServletResponse;

public interface WeeklyService {
    void save(WeeklySaveVO vo);

    Weekly getWeeklyByUidAndGid(Long uid, Long gid);

    WeeklySaveVO get(Long uid, Long gid);

    /**
     * 这个返回的是英文名称 不需要 request
     */
    void download(WeeklySaveVO vo, HttpServletResponse response);
}
