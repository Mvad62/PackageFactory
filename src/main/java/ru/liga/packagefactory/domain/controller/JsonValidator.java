package ru.liga.packagefactory.domain.controller;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonValidator {


    public boolean validateTrucksPackagesJson(String jsonContent, InputStream schemaStream) {
        try {
            // Java 9+ способ чтения InputStream
            String schemaString = new String(schemaStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaString));
            Schema schema = SchemaLoader.load(rawSchema);

            JSONObject jsonData = new JSONObject(new JSONTokener(jsonContent));
            schema.validate(jsonData);
            return true;

        } catch (ValidationException e) {
            handleValidationException(e);
            return false;
        } catch (JSONException e) {
            System.err.println("Некорректный JSON: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    private void handleValidationException(ValidationException e) {
        System.err.println("Ошибки валидации:");
        if (e.getCausingExceptions() != null && !e.getCausingExceptions().isEmpty()) {
            e.getCausingExceptions().forEach(error ->
                    System.err.println("  - " + error.getMessage()));
        } else {
            System.err.println("  - " + e.getMessage());
        }
    }
}
