package com.servfix.manualesapp.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_ENLINIA = "enlinea";

    public static HashMap<String, String> remoteMsgHeaders = null;

    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "KEY=AAAAwaLQcwg:APA91bHSjBxeMszub-V_F3F8RYKKVfs07jgCDso7ox7rUKXy6m0ExkW-_mSXcJpsPavHEHJdQzMp2fRAc7zS8a2nKQHNqwanSShuB3nKVCC6JyaW-cA_yDqsxhi6isIbIsZlBVbdNCVb"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
