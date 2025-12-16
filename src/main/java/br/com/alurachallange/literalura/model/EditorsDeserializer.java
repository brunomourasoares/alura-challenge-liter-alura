package br.com.alurachallange.literalura.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EditorsDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> result = new ArrayList<>();

        JsonToken currentToken = p.currentToken();
        if (currentToken == null) currentToken = p.nextToken();

        if (currentToken == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                JsonToken t = p.currentToken();
                if (t == JsonToken.VALUE_STRING) {
                    result.add(p.getValueAsString());
                } else if (t == JsonToken.START_OBJECT) {
                    JsonNode node = p.readValueAsTree();
                    String extracted = extractNameFromNode(node);
                    result.add(extracted);
                } else {
                    JsonNode node = p.readValueAsTree();
                    result.add(node.isTextual() ? node.asText() : node.toString());
                }
            }
        } else if (currentToken == JsonToken.START_OBJECT) {
            JsonNode node = p.readValueAsTree();
            result.add(extractNameFromNode(node));
        } else if (currentToken == JsonToken.VALUE_STRING) {
            result.add(p.getValueAsString());
        } else if (currentToken == JsonToken.VALUE_NULL) {
            // return empty list on null
        } else {
            JsonNode node = p.readValueAsTree();
            if (node.isArray()) {
                Iterator<JsonNode> it = node.elements();
                while (it.hasNext()) {
                    JsonNode el = it.next();
                    if (el.isTextual()) result.add(el.asText());
                    else result.add(extractNameFromNode(el));
                }
            } else if (node.isTextual()) {
                result.add(node.asText());
            } else if (node.isObject()) {
                result.add(extractNameFromNode(node));
            }
        }

        return result;
    }

    private String extractNameFromNode(JsonNode node) {
        if (node == null || node.isNull()) return null;
        String[] candidateFields = new String[]{"name", "title", "person", "editor"};
        for (String f : candidateFields) {
            JsonNode v = node.get(f);
            if (v != null && !v.isNull()) {
                if (v.isTextual()) return v.asText();
                else return v.toString();
            }
        }
        if (node.isTextual()) return node.asText();
        return node.toString();
    }
}

