package net.gotev.sipservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;

/**
 * Service with a background worker thread.
 * @author gotev (Aleksandar Gotev)
 */
abstract class BackgroundService extends Service {

    private Handler mHandler;
    private PowerManager.WakeLock mWakeLock;
    private HandlerThread mWorkerThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        acquireWakeLock();

        startWorkerThread();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWorkerThread.quitSafely();
        releaseWakeLock();
    }

    protected void enqueueJob(Runnable job) {
        mHandler.post(job);
    }

    protected void enqueueDelayedJob(Runnable job, long delayMillis) {
        mHandler.postDelayed(job, delayMillis);
    }

    protected void dequeueJob(Runnable job) {
        mHandler.removeCallbacks(job);
    }

    public void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getSimpleName());
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
    }

    public void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private synchronized void startWorkerThread() {
        mWorkerThread = new HandlerThread(getClass().getSimpleName(), Process.THREAD_PRIORITY_FOREGROUND);
        mWorkerThread.setPriority(Thread.MAX_PRIORITY);
        mWorkerThread.start();
        mHandler = new Handler(mWorkerThread.getLooper());
        checkThread(mWorkerThread);
    }

    abstract void checkThread(Thread thread);
}
