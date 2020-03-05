package com.github.pig.common.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.xiaoleilu.hutool.date.DateUtil;

import java.io.IOException;
import java.util.Date;

/**
 * @author yuwei
 * @date 2018/11/28 16:48
 */
public class DateFormatYmtSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try {
            jsonGenerator.writeString(DateUtil.formatDate(date));
        } catch (Exception e) {
            jsonGenerator.writeString(String.valueOf(date.getTime()));
        }
    }
}
