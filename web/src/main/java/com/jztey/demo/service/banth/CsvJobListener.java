package com.jztey.demo.service.banth;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Job监听
 * 
 * @author yushi
 *
 */
public class CsvJobListener implements JobExecutionListener {// 监听器实现JobExecutionListener接口，重写其方法即可

	long startTime;
	long endTime;

	@Override
	public void afterJob(JobExecution jobExecution) {
		startTime = System.currentTimeMillis();
		System.out.println("任务开始处理");
	}

	@Override
	public void beforeJob(JobExecution arg0) {
		endTime = System.currentTimeMillis();
		System.out.println("任务处理结束");
		System.out.println("耗时：" + (endTime - startTime) + "ms");
	}

}
