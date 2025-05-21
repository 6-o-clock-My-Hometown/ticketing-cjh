package com.example.sparta_ticketing.common.scheduler;

import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowScheduler {

    private final ShowService showService;


    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "checkTicketExpired", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    public void checkTicketExpired(){
        String instanceId = System.getProperty("server.port", "default");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("시간: "+now+"[checkTicketExpired] 실행됨 by port: " + instanceId);

        List<Show> showList= showService.findExpiredShow();

        if(showList!=null && !showList.isEmpty()){
            for(Show show:showList){
                // 만료된 공연 비활성화
                showService.changeShowStatus(show.getId());
            }
        }
    }
}
