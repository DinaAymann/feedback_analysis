package com.dina.feedback.batch;

import com.dina.feedback.DTO.FeedbackDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class FeedbackJsonItemReader implements ItemReader<FeedbackDTO> {

    private final Iterator<FeedbackDTO> iterator;

    public FeedbackJsonItemReader(Resource fileResource) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = fileResource.getInputStream();
        List<FeedbackDTO> list = mapper.readValue(inputStream, new TypeReference<List<FeedbackDTO>>() {});
        this.iterator = list.iterator();
    }

    @Override
    public FeedbackDTO read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
