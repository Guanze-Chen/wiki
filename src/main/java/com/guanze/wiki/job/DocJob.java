package com.guanze.wiki.job;

import com.guanze.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private DocService docService;


    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     * 没过30s更新一次信息
     */
    @Scheduled(cron = "1/5 * * * * ?")
    public void cron() {
        docService.updateEbookInfo();
        LOG.info("每隔5秒钟执行一次");
    }

}
