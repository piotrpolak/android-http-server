/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/
package api.logic;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import admin.logic.SmsBox;

public class MessageDTOMapper {

    @NonNull
    public JSONObject toMessageDTO(SmsBox.Message message) throws JSONException {
        JSONObject messageDTO = new JSONObject();
        messageDTO.put("id", message.getId());
        messageDTO.put("address", message.getAddress());
        messageDTO.put("body", message.getBody());
        messageDTO.put("date", message.getDate());
        messageDTO.put("date_sent", message.getDateSent());
        messageDTO.put("is_incoming", message.isIncoming());
        return messageDTO;
    }
}
