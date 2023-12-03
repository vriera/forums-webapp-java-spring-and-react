package ar.edu.itba.paw.webapp.dto.errors;

import org.json.JSONObject;

import java.util.Map;

public class ErrorHttpServletResponseDto {
    public static JSONObject produceErrorDto(final String code, final String message, final Map<String, String> data) {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject errorDescription = new JSONObject();
        errorDescription.put("code", code);
        errorDescription.put("message", message);
        final JSONObject errorData = new JSONObject();
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                errorData.put(entry.getKey(), entry.getValue());
            }
        }
        jsonObject.put("data", errorData);
        jsonObject.put("error", errorDescription);
        return jsonObject;
    }

}
