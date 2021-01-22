package chat;

import android.os.Handler;
import android.os.HandlerThread;

//동일한 thread 에서 생성이 일어나도록 유틸 클래스를 통해 thread 생성
public class ThreadUtils {

    public static Handler GetMultiHandler(String name) {
        HandlerThread thread = new HandlerThread(name + "_MultiThread");
        thread.start();

        return new Handler(thread.getLooper());
    }
}
