package com.example.demobatch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JobScheduler {
    private final Job dbToFileJob;
    private final JobOperator jobOperator;

    @Scheduled(cron ="0/30 * * * * *")
    @SneakyThrows
    void trigger(){
        var fileName = LocalDate.now().toString().concat("_sales.csv");

        var jobParameters = new JobParametersBuilder()
                .addString("output.file.name", fileName)
                .addDate("processed", new Date())
                .toJobParameters();

        this.jobOperator.start(dbToFileJob,jobParameters);
    }
}
