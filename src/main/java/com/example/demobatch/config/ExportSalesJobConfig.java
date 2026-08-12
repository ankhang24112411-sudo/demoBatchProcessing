package com.example.demobatch.config;

import com.example.demobatch.dto.SalesDTO;
import com.example.demobatch.processor.SalesProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration

@EnableBatchProcessing
@RequiredArgsConstructor
public class ExportSalesJobConfig {
    private final SalesProcessor processor;
    private final DataSource dataSource;
    private final JobRepository repository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dbToFileJob(Step fromSalesTableToFile) {
        return new JobBuilder("dbToFileJob" ,repository )
                .start(fromSalesTableToFile)
                .build();
    }
    @Bean
    public Step fromSalesTableToFile(FlatFileItemWriter<SalesDTO> flatFileItemWriter) {
        return new StepBuilder("from db to file", repository)
                .<SalesDTO, SalesDTO>chunk(10)
                .transactionManager(transactionManager)
                .reader(salesDTOJdbcCursorItemReader())
                .processor(processor)
                .writer(flatFileItemWriter)
                .build();
    }
    @Bean
    public JdbcCursorItemReader<SalesDTO> salesDTOJdbcCursorItemReader(){
        var sql = """
            select sales_id as sale_id,product_id,  customer_id, sale_date, sale_amount,store_location,    country
            FROM sales
            WHERE processed = false
            """;

        return new JdbcCursorItemReaderBuilder<SalesDTO>()
                .name("sales reader")
                .dataSource(dataSource)
                .sql(sql)
                .fetchSize(100)
                .rowMapper(new DataClassRowMapper<>(SalesDTO.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<SalesDTO> flatFileItemWriter(@Value("#{jobParameters['output.file.name']}") String outputFile) {
        return new FlatFileItemWriterBuilder<SalesDTO>()
                .name("sales file writer")
                .resource(new FileSystemResource(outputFile))
                .headerCallback(writer -> writer.append("Header of File"))
                .delimited()
                .delimiter(";")
                .sourceType(SalesDTO.class)
                .names("saleId", "productId", "customerId", "saleDate", "saleAmount", "storeLocation", "country")
                .shouldDeleteIfEmpty(Boolean.TRUE)
                .append(Boolean.TRUE)
                .build();
    }
    }
//    @Bean
//    public FlatFileItemReader<Student> itemReader(){
////        return new FlatFileItemReaderBuilder<Student>()
////                .name("studentItemReader")
////                .resource(new FileSystemResource("students.csv"))
////                .delimited()
////                .names("id", "firstName", "lastName", "age")
////                .targetType(Student.class)
////                .build();
//        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
//        itemReader.setName("csvReader");
//        itemReader.setLineMapper(lineMapper());
//        return itemReader;
//    }

