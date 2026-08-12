package com.example.demobatch.processor;

import com.example.demobatch.dto.SalesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesProcessor implements ItemProcessor<SalesDTO, SalesDTO> {
    @Override
    public  SalesDTO process(SalesDTO item) throws Exception {
        log.info("processing the item: {} ", item);
        if("United States".equalsIgnoreCase(item.country())){
            return null;
        }
        return item;
    }
}
