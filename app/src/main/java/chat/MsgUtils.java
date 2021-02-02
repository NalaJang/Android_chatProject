package chat;

import android.content.Context;
import android.util.Log;


import dto.Message;

public class MsgUtils {
    private static ChatConnThread connThread;

    public static void setConnThread(ChatConnThread thread) {
        connThread = thread;
        Log.d("==== MsgUtils, setConnThread ====", thread.toString());
    }

    public static void sendMsg(Message msg) {
        connThread.sendMsg(msg);
    }

    public static void setCurrentRoom(String roomId) {
        connThread.setRoomId(roomId);
    }

    public static void setContext(Context context) {
        connThread.setContext(context);

        Log.d("==== MsgUtils ====", context.toString() + "로 바뀜");
    }
}
