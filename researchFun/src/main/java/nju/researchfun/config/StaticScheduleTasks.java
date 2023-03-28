package nju.researchfun.config;

import nju.researchfun.constant.MessageType;
import nju.researchfun.entity.Schedule;
import nju.researchfun.mapper.ScheduleMapper;
import nju.researchfun.service.MessageService;
import nju.researchfun.service.ScheduleService;
import nju.researchfun.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.List;

/**
 * 定时器配置类
 */
@Configuration
@EnableScheduling
public class StaticScheduleTasks {

    @Autowired
    private ScheduleService scheduleService;


    /**
     * 提醒日程 ddl 计时器
     * 每天的 0:00 触发
     */
    @Scheduled(cron = "0 0 0 * * ?")
    private void remindDDL() {
        scheduleService.remindDDL();
    }

/*
    @Autowired
    ScheduleMapper scheduleMapper;

    @Scheduled(cron = "0/10 * * * * ?")
    private void test() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        List<Schedule> list = scheduleMapper.findByDDL(year, month, date);
        for (Schedule ddl : list) {
            System.out.println("--------------------------");
            System.out.println("测试输出");
            System.out.println(ddl);
            System.out.println("--------------------------");
        }
    }
*/
}
