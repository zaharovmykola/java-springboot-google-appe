package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.codehaus.jackson.JsonParseException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class BigDecimalDeserializer extends JsonDeserializer {
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            NumberFormat numberFormatter = NumberFormat.getInstance(LocaleContextHolder.getLocale());
            String numberText = jsonParser.getText();
            if (numberText == null || "".equals(numberText)) {
                return null;
            }
            return new BigDecimal(numberFormatter.parse(numberText).doubleValue());
        } catch (ParseException e) {
            throw new JsonParseException(null, null, e);
        }
    }
}
