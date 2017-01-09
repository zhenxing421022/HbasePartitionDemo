package com.example;


import com.example.data.entity.JobEntityImpl;
import com.example.interfaces.dto.JobEntity;
import com.example.service.HBaseServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws InterruptedException {
//        SpringApplication.run(DemoApplication.class, args);

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
        HBaseServiceImpl hbaseServiceImpl = context.getBean("HBaseServiceImpl", HBaseServiceImpl.class);
//        HBaseEntity hBaseEntity = context.getBean("xdr_data:ps_gn_http_event_K", HBaseEntityImpl.class);
        JobEntity jobEntity = context.getBean("xdr_data:ps_gn_http_event_job", JobEntityImpl.class);
//        hbaseServiceImpl.createSchedulerJob(jobEntity);
        hbaseServiceImpl.partition(jobEntity);
        Thread.sleep(3*60*1000);
    }
}