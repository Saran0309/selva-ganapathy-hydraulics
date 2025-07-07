package com.sgh.selvaganapathyhydraulics.helper;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class Translator {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Translator.class);

    @Value("${gemini.api.url}")
    private  String geminiAPIUrl;


    
    
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public String translate(String text, String targetLang) {
        List<String> result = batchTranslate(Collections.singletonList(text), targetLang);
        return result.isEmpty() ? text : result.get(0);
    }

    public Object translateObject(Object input, String lang) {
        if (lang == null || lang.equalsIgnoreCase("en") || input == null) return input;

        try {
            if (input instanceof String str) {
                return translate(str, lang);
            }

            if (input instanceof List<?> list) {
                if (!list.isEmpty() && list.get(0) instanceof String) {
                    List<String> stringList = (List<String>) list;
                    return batchTranslate(stringList, lang);
                } else {
                    for (Object obj : list) {
                        if (obj != null) translateFields(obj, lang);
                    }
                    return input;
                }
            }

            if (input instanceof Map<?, ?> map && !map.isEmpty()) {
                return translateMap((Map<String, String>) map, lang);
            }

            translateFields(input, lang);
            return input;

        } catch (Exception e) {
            log.error("❌ Translation failed for input type: {}", input.getClass().getName(), e);
            return input;
        }
    }

    private void translateFields(Object model, String lang) {
        try {
            PropertyDescriptor[] props = Introspector.getBeanInfo(model.getClass(), Object.class).getPropertyDescriptors();
            Map<String, String> fieldToOriginal = new LinkedHashMap<>();
            Map<String, Method> fieldToSetter = new HashMap<>();

            for (PropertyDescriptor prop : props) {
                String fieldName = prop.getName();

                if (fieldName.equalsIgnoreCase("videoLink") || fieldName.equalsIgnoreCase("images")) continue;

                Method getter = prop.getReadMethod();
                Method setter = prop.getWriteMethod();
                if (getter == null || setter == null) continue;

                if (prop.getPropertyType() == String.class) {
                    String value = (String) getter.invoke(model);
                    if (value != null && !value.trim().isEmpty() && !value.matches("\\d+")) {
                        fieldToOriginal.put(fieldName, value);
                        fieldToSetter.put(fieldName, setter);
                    }
                } else if (List.class.isAssignableFrom(prop.getPropertyType())) {
                    Object value = getter.invoke(model);
                    if (value instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String) {
                        List<String> filtered = ((List<String>) list).stream().filter(v -> !v.matches("\\d+")).collect(Collectors.toList());
                        if (!filtered.isEmpty()) {
                            List<String> translated = batchTranslate(filtered, lang);
                            setter.invoke(model, translated);
                        }
                    }
                } else if (Map.class.isAssignableFrom(prop.getPropertyType())) {
                    Object value = getter.invoke(model);
                    if (value instanceof Map<?, ?> map) {
                        Map<String, String> filtered = ((Map<String, String>) map).entrySet().stream()
                                .filter(e -> e.getValue() != null && !e.getValue().matches("\\d+") && !e.getValue().isBlank())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                        if (!filtered.isEmpty()) {
                            setter.invoke(model, translateMap(filtered, lang));
                        }
                    }
                }
            }

            if (!fieldToOriginal.isEmpty()) {
                List<String> originals = new ArrayList<>(fieldToOriginal.values());
                List<String> translations = batchTranslate(originals, lang);

                int index = 0;
                for (String field : fieldToOriginal.keySet()) {
                    fieldToSetter.get(field).invoke(model, translations.get(index++));
                }
            }

        } catch (Exception e) {
            log.error("❌ Field translation failed for model: {}", model.getClass().getSimpleName(), e);
        }
    }

    private Map<String, String> translateMap(Map<String, String> inputMap, String lang) {
        Map<String, String> translatedMap = new LinkedHashMap<>();
        List<String> values = new ArrayList<>(inputMap.values());
        List<String> translatedValues = batchTranslate(values, lang);
        int i = 0;
        for (String key : inputMap.keySet()) {
            translatedMap.put(key, translatedValues.get(i++));
        }
        return translatedMap;
    }

    public List<String> batchTranslate(List<String> texts, String targetLang) {
        if (targetLang.equalsIgnoreCase("en") || texts == null || texts.isEmpty()) {
            return texts;
        }

        try {
            String langName = getLangName(targetLang);
            log.info("🔁 Translating {} texts to '{}'", texts.size(), langName);

            JSONArray inputArray = new JSONArray();
            for (String text : texts) {
                inputArray.put(text);
            }

            String prompt = String.format(
                "Translate each of the following English phrases to %s. " +
                "Return only a pure JSON array of translated lines in native script, in the same order. " +
                "Example: [\"...\", \"...\"]\n\n%s",
                langName, inputArray.toString()
            );

            JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                    .put(new JSONObject()
                        .put("parts", new JSONArray()
                            .put(new JSONObject().put("text", prompt))
                        )
                    )
                );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geminiAPIUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            String jsonArrayText = json.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

            // Fix: Ensure trimming and remove metadata if Gemini includes any text around array
            jsonArrayText = jsonArrayText.trim();
            if (!jsonArrayText.startsWith("[")) {
                int idx = jsonArrayText.indexOf("[");
                if (idx >= 0) jsonArrayText = jsonArrayText.substring(idx);
            }

            JSONArray translatedArray = new JSONArray(jsonArrayText);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < translatedArray.length(); i++) {
                result.add(translatedArray.getString(i).trim());
            }

            log.info("✅ Batch translated {} items", result.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Batch translation failed", e);
            return texts; // fallback
        }
    }



    private String getLangName(String langCode) {
        return switch (langCode.toLowerCase()) {
            case "ta" -> "Tamil";
            case "te" -> "Telugu";
            case "hi" -> "Hindi";
            case "ml" -> "Malayalam";
            case "kn" -> "Kannada";
            case "mr" -> "Marathi";
            case "gu" -> "Gujarati";
            case "bn" -> "Bengali";
            case "pa" -> "Punjabi";
            case "ur" -> "Urdu";
            case "fr" -> "French";
            case "de" -> "German";
            case "es" -> "Spanish";
            case "zh" -> "Chinese";
            case "ar" -> "Arabic";
            default -> "Tamil";
        };
    }
}
