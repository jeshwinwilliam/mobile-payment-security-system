package com.jeshwin.mobilepayments.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static Map<String, String> parseObject(String json) {
        String trimmed = json == null ? "" : json.trim();
        if (trimmed.isEmpty()) {
            return Map.of();
        }
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Request body must be a JSON object");
        }

        String content = trimmed.substring(1, trimmed.length() - 1).trim();
        Map<String, String> result = new LinkedHashMap<>();
        if (content.isEmpty()) {
            return result;
        }

        for (String pair : splitTopLevel(content)) {
            String[] parts = pair.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid JSON field: " + pair);
            }
            String key = unquote(parts[0].trim());
            String value = unquote(parts[1].trim());
            result.put(key, value);
        }
        return result;
    }

    public static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String string) {
            return "\"" + escape(string) + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder builder = new StringBuilder("{");
            Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> entry = iterator.next();
                builder.append(toJson(String.valueOf(entry.getKey())));
                builder.append(":");
                builder.append(toJson(entry.getValue()));
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            builder.append("}");
            return builder.toString();
        }
        if (value instanceof List<?> list) {
            StringBuilder builder = new StringBuilder("[");
            for (int index = 0; index < list.size(); index++) {
                builder.append(toJson(list.get(index)));
                if (index < list.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
            return builder.toString();
        }
        return toJson(value.toString());
    }

    private static List<String> splitTopLevel(String content) {
        java.util.ArrayList<String> parts = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < content.length(); index++) {
            char currentChar = content.charAt(index);
            if (currentChar == '"' && (index == 0 || content.charAt(index - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (currentChar == ',' && !inQuotes) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(currentChar);
            }
        }

        if (!current.isEmpty()) {
            parts.add(current.toString().trim());
        }
        return parts;
    }

    private static String unquote(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed.replace("\\\"", "\"");
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
