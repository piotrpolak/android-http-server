package api.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents JSON API Response
 */
public class APIResponse {

    public static final int CODE_OK = 200;
    public static final int CODE_ERROR = 400;

    private JSONObject jsonObject = new JSONObject();

    /**
     * Serializes API Response
     *
     * @param code
     * @param message
     * @param result
     * @throws JSONException
     */
    public APIResponse(final int code, final String message, final JSONObject result) throws JSONException {
        jsonObject.put("status", code);
        jsonObject.put("message", message);
        jsonObject.put("result", result);
    }

    /**
     * Serializes API Response
     *
     * @param code
     * @param message
     * @param result
     * @throws JSONException
     */
    public APIResponse(final int code, final String message, final JSONArray result) throws JSONException {
        jsonObject.put("status", code);
        jsonObject.put("message", message);
        jsonObject.put("result", result);
    }

    /**
     * Serializes API Response
     *
     * @throws JSONException
     */
    public APIResponse() throws JSONException {
        jsonObject.put("status", CODE_OK);
        jsonObject.put("message", "OK");
        jsonObject.put("result", null);
    }

    /**
     * Serializes API Response
     *
     * @param code
     * @param message
     * @throws JSONException
     */
    public APIResponse(int code, String message) throws JSONException {
        jsonObject.put("status", code);
        jsonObject.put("message", message);
        jsonObject.put("result", null);
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
