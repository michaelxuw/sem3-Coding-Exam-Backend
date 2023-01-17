package utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GsonLocalDate implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-mmm-yyyy");
    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String ldtString = jsonElement.getAsString();
        return LocalDate.parse(ldtString, DateTimeFormatter.ISO_LOCAL_DATE);
//        return LocalDate.parse(jsonElement.getAsString(),
//                DateTimeFormatter.ofPattern("d-mmm-yyyy").withLocale(Locale.ENGLISH));
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
//        return new JsonPrimitive(formatter.format(localDate));
    }
}
