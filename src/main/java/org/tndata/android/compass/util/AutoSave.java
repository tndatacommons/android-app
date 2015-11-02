package org.tndata.android.compass.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by isma on 10/27/15.
 */
public class AutoSave implements Runnable{
    private Activity mActivity;
    private AutoSaveInterface mInterface;
    private int mInterval;

    private boolean running;


    private AutoSave(@NonNull Activity activity, @NonNull AutoSaveInterface autoSaveInterface, int interval){
        mActivity = activity;
        mInterface = autoSaveInterface;
        mInterval = interval;
        running = true;
    }

    @Override
    public void run(){
        while (running){
            Log.d("AutoSave", "loop");
            long lastUpdateTime = mInterface.getLastUpdateTime();
            long currentTime = System.currentTimeMillis();
            int sleepTime;

            if (lastUpdateTime == -1){
                Log.d("AutoSave", "-1");
                sleepTime = mInterval;
            }
            else{
                if (currentTime - lastUpdateTime >= mInterval){
                    Log.d("AutoSave", "saving");
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run(){
                            mInterface.save();
                        }
                    });
                    sleepTime = mInterval;
                }
                else{
                    Log.d("AutoSave", "sleeping " + ((int)(currentTime-lastUpdateTime) + 10));
                    sleepTime = mInterval - (int)(currentTime-lastUpdateTime) + 10;
                }
            }

            try{
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException ix){
                ix.printStackTrace();
                return;
            }
        }
    }

    public void stop(){
        running = false;
    }


    public static AutoSave start(@NonNull Activity activity, @NonNull AutoSaveInterface autoSaveInterface, int interval){
        if (interval < 0){
            throw new IllegalArgumentException("The time interval cannot be negative");
        }
        AutoSave autoSave = new AutoSave(activity, autoSaveInterface, interval);
        new Thread(autoSave).start();
        return autoSave;
    }


    public interface AutoSaveInterface{
        long getLastUpdateTime();
        void save();
    }
}
