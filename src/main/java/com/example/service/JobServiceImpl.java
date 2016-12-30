package com.example.service;

import com.example.interfaces.dto.HBaseEntity;
import com.example.interfaces.dto.JobEntity;
import com.example.interfaces.service.HBaseService;
import com.example.interfaces.service.JobService;
import com.example.interfaces.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.Map;

/**
 * Created by linghang.kong on 2016/12/27.
 */
public class JobServiceImpl implements JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private HBaseService<JobEntity,HBaseEntity> hbaseService;

    private Map<String, JobEntity<HBaseEntity>> tasksMap = taskService.getTasksMap();

    private String name;

    public void doPartitionTableJobs() {
        long currentTime = System.currentTimeMillis();
        if (tasksMap.size()>0){
            for (String name: tasksMap.keySet()
                 ) {
                final JobEntity job= tasksMap.get(name);
                if (!job.isStatus()) {
                    final HBaseEntity hBaseEntity = (HBaseEntity) job.getTableEntity();
                    // 开启任务
                    job.setId(job.getName()+"-"+currentTime);
                    job.setStatus(true);
                    job.setCreateTime(currentTime);
                    this.name = name;
                    final JobServiceImpl jobService = this;
                    threadPoolTaskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            job.setStartTime(System.currentTimeMillis());
                            hbaseService.partition(hBaseEntity);
                            //关闭任务
                            job.setStatus(false);
                            job.setStopTime(System.currentTimeMillis());
                        }
                    });
                    logger.info("create the {} job.", name);
                }else {
                    logger.info("The table of {} is executing!",name);
                }
            }
        }else{
            logger.info("Noting to do for this task list, because the list is empty.");
        }
    }

    /**
     *  excute job
     * @param tableName table name
     * @return 0:successful 1:exits 2: can't find the table
     */
    public int doPartitionTableJob(String tableName){
        for (String name:tasksMap.keySet()
             ) {
            if (tableName.equals(name)){
                JobEntity job = tasksMap.get(tableName);
                if (!job.isStatus()) {
                    hbaseService.partition(tasksMap.get(tableName).getTableEntity());
                    logger.info("create the {} job.", tableName);
                    return 0;
                }else {
                    logger.info("The table of {} is executing!",tableName);
                    return 1;
                }
            }
        }
        logger.info("Can't find the table of {}!",tableName);
        return 2;
    }

    public void stopJobs(){

    }

    public String getName() {
        return null;
    }

    public void setName() {

    }

    public void close() throws IOException {

    }
}
