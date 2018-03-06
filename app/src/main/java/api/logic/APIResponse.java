package api.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents JSON API Response
 */
public class APIResponse {

    public static final String MEDIA_TYPE_APPLICATION_JSON = "application/json";
    public static final int CODE_OK = 200;
    public static final int CODE_ERROR = 400;
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_MESSAGE = "message";
    private static final String ATTR_RESULT = "result";
    private static final String MESSAGE_OK = "OK";

    private JSONObject jsonObject = new JSONObject();

    /**
     * Serializes API Response
     *
     * @param code
     * @param message
     * @param result
     * @throws JSONException
     */
    public APIResponse(final int code, final String message, final JSONArray result) throws JSONException {
        jsonObject.put(ATTR_STATUS, code);
        jsonObject.put(ATTR_MESSAGE, message);
        jsonObject.put(ATTR_RESULT, result);
    }

    /**
     * Serializes API Response
     *
     * @throws JSONException
     */
    public APIResponse() throws JSONException {
        this(CODE_OK, MESSAGE_OK, null);
    }

    /**
     * Serializes API Response
     *
     * @param code
     * @param message
     * @throws JSONException
     */
    public APIResponse(int code, String message) throws JSONException {
        this(code, message, null);
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
