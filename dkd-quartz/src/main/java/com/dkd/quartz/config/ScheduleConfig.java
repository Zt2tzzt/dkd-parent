//package com.dkd.quartz.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
///**
// * 定时任务配置（单机部署建议删除此类和qrtz数据库表，默认走内存会最高效）
// *
// * @author ruoyi
// */
//@Configuration
//public class ScheduleConfig {
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
//        // 创建SchedulerFactoryBean实例
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        // 设置数据源
//        factory.setDataSource(dataSource);
//
//        // quartz参数
//        Properties prop = new Properties();
//        // 设置调度器实例名称，在同一个集群内多个节点上，该实例名称必须一致
//        prop.put("org.quartz.scheduler.instanceName", "RuoyiScheduler");
//        // 设置调度器实例ID生成方式，在同一个集群内多个节点上，该id必须唯一
//        prop.put("org.quartz.scheduler.instanceId", "AUTO");
//
//        // 线程池配置
//        // 设置线程池类
//        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
//        // 设置线程数量
//        prop.put("org.quartz.threadPool.threadCount", "20");
//        // 设置线程优先级
//        prop.put("org.quartz.threadPool.threadPriority", "5");
//
//        // JobStore配置
//        // 设置JobStore类
//        prop.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
//        // 集群配置
//        // 设置是否启用集群
//        prop.put("org.quartz.jobStore.isClustered", "true");
//        // 设置集群检查间隔
//        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
//        // 设置每次处理的最大错误触发器数量
//        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "10");
//        // 设置事务隔离级别为可序列化
//        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
//
//        // sqlserver 启用
//        // 用于SQL Server的锁定SQL语句配置
//        // prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");
//        // 设置错误触发器阈值
//        prop.put("org.quartz.jobStore.misfireThreshold", "12000");
//        // 设置表前缀
//        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
//        // 设置Quartz属性
//        factory.setQuartzProperties(prop);
//
//        // 设置调度器名称
//        factory.setSchedulerName("RuoyiScheduler");
//        // 延时启动，单位为秒
//        factory.setStartupDelay(1);
//        // 设置Spring应用上下文在SchedulerContext中的键
//        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
//        // 可选，QuartzScheduler
//        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
//        factory.setOverwriteExistingJobs(true);
//        // 设置自动启动，默认为true
//        factory.setAutoStartup(true);
//
//        // 返回配置好的SchedulerFactoryBean实例
//        return factory;
//    }
//}
