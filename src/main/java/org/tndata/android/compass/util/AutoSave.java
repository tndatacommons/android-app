package org.tndata.android.compass.util;

import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by isma on 10/27/15.
 */
public class AutoSave implements Runnable{
    private AutoSaveInterface mInterface;
    private int mInterval;

    private boolean running;


    private AutoSave(@NonNull AutoSaveInterface autoSaveInterface, int interval){
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
                    mInterface.save();
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


    public static AutoSave start(@NonNull AutoSaveInterface autoSaveInterface, int interval){
        AutoSave autoSave = new AutoSave(autoSaveInterface, interval);
        new Thread(autoSave).start();
        return autoSave;
    }


    public interface AutoSaveInterface{
        long getLastUpdateTime();
        void save();
    }
}
