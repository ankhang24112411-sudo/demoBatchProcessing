package com.example.demobatch.listeners;

import com.example.demobatch.dto.SalesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SalesWriterListener implements ItemWriteListener<SalesDTO> {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterWrite(Chunk<? extends SalesDTO> items){
        List<Long> itemWrittenIds = items.getItems().stream()
                .map(SalesDTO::saleId).toList();

        var sql = """ 
                UPDATE sales SET processed = true where sale_id IN (:ids)
                """;

        var sqlParamSource = new MapSqlParameterSource("ids", itemWrittenIds);

        var namedParamJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        int totalRowsAffected = namedParamJdbcTemplate.update(sql,sqlParamSource);
    }
}
