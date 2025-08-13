package ru.liga.packagefactory.domain.controller;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;

@Slf4j
public class JsonValidator {

    public boolean validatePackagesJson(String jsonContent, InputStream schemaStream) {
        try {
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
            Schema schema = SchemaLoader.load(rawSchema);
            JSONArray jsonData = new JSONArray(new JSONTokener(jsonContent));
            schema.validate(jsonData); // Выбросит ValidationException при ошибке
            return true;
        } catch (ValidationException e) {
            System.err.println("JSON validation failed: " + e.getMessage());
            e.getCausingExceptions().stream()
                    .map(ValidationException::getMessage)
                    .forEach(System.err::println);
            return false;
        } catch (Exception e) {
            log.debug("Invalid JSON file");
            return false;
        }
    }
}
