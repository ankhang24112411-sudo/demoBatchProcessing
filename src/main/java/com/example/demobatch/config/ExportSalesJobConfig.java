package com.example.demobatch.config;

import com.example.demobatch.dto.SalesDTO;
import com.example.demobatch.processor.SalesProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;

@Configuration

@EnableBatchProcessing
@RequiredArgsConstructor
public class ExportSalesJobConfig {
    private final SalesProcessor processor;
    private final DataSource dataSource;
    @Bean
    public JdbcCursorItemReader<SalesDTO> salesDTOJdbcCursorItemReader(){
        var sql = """
            select product_id,  customer_id, sale_date, sale_amount,store_location,    country
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
    public FlatFileItemWriter<SalesDTO> flatFileItemWriter(){
        return new FlatFileItemWriterBuilder<>()
                .name("sales file writer")
                .resource(new FileSystemResource("sales.csv"))
                .
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
}
