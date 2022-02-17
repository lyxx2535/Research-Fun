package com.example.rgms.runner;

import com.example.rgms.constant.MessageType;
import com.example.rgms.entity.ScheduleEntity;
import com.example.rgms.repository.ScheduleRepository;
import com.example.rgms.service.MessageService;
import com.example.rgms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScheduleExamineRunner implements ApplicationRunner {
    private final String TO_USER_AT_21="您今日的日程尚未完成，请抓紧时间！";
    private final String TO_USER_AT_24="很遗憾，您昨日的日程未能完成……";
    private final String TO_TEACHER_AT_21="您的学生{sender}还未完成今日的日程！";
    private final String TO_TEACHER_AT_24="您的学生{sender}未能完成昨日的日程……";

    private final ScheduleRepository scheduleRepository;
    private final MessageService messageService;
    private final StudentService studentService;

    @Autowired
    public ScheduleExamineRunner(ScheduleRepository scheduleRepository, MessageService messageService,
                                 StudentService studentService){
        this.scheduleRepository=scheduleRepository;
        this.messageService=messageService;
        this.studentService=studentService;
    }

    @Override
    public void run(ApplicationArguments args){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar=Calendar.getInstance();
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int minute=calendar.get(Calendar.MINUTE);
                if(minute==0 && (hour==21 || hour==0)){
                    String bodyToUser;
                    List<ScheduleEntity> scheduleEntities;
                    if(hour==21) {
                        bodyToUser=TO_USER_AT_21;

                        scheduleEntities = scheduleRepository.findUnfinishedScheduleByDate(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1,
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                    } else { // 如果是0点，那么是检查昨日的日程是否完成，需要将当前日期减一天
                        bodyToUser=TO_USER_AT_24;

                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        scheduleEntities = scheduleRepository.findUnfinishedScheduleByDate(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1,
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                    }

                    for(ScheduleEntity scheduleEntity : scheduleEntities){
                        Long userId=scheduleEntity.getUserId();
                        messageService.sendMessage(null, userId, bodyToUser,
                                MessageType.SCHEDULE_RELATIVE, scheduleEntity.getId());
                    }
                }
            }
        }, 0, 60*1000);
    }
}
