package io.flutter.plugins.webviewflutter;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import io.flutter.plugin.common.MethodChannel;

/**
 * <pre>
 *     author: LoquatZ
 *     blog  : http://loquat-z.com
 *     time  : 2021/9/20
 *     desc  :
 * </pre>
 */
public class SyncExecutor {
    private static volatile SyncExecutor syncExecutor;

    private SyncExecutor() {

    }

    public static SyncExecutor getInstance() {
        if (null == syncExecutor) {
            synchronized (SyncExecutor.class) {
                if (null == syncExecutor) {
                    syncExecutor = new SyncExecutor();
                }
            }
        }
        return syncExecutor;
    }

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    Handler mainHandler = new Handler(Looper.getMainLooper());

    public void invokeMethod(final MethodChannel methodChannel, final String method, final String url) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> args = new HashMap<>();
                args.put("url", url);
                methodChannel.invokeMethod(method, args);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

