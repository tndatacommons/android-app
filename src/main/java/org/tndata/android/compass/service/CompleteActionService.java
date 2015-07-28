package org.tndata.android.compass.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.tndata.android.compass.task.CompleteActionTask;

import java.util.LinkedList;


/**
 * Service that marks actions as complete in the backend.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class CompleteActionService
        extends Service
        implements
                CompleteActionTask.CompleteActionInterface{

    public static final String ACTION_KEY = "action";

    private LinkedList<Integer> mCompletedActions;


    @Override
    public void onCreate(){
        super.onCreate();
        mCompletedActions = new LinkedList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int actionId = intent.getExtras().getInt(ACTION_KEY);
        if (isQueueEmpty()){
            queueAction(actionId);
            new CompleteActionTask(this, this).execute();
        }
        else{
            queueAction(actionId);
        }

        return START_NOT_STICKY;
    }

    /**
     * Queues an action to be marked as complete.
     *
     * @param actionId the action to be marked as complete.
     */
    public synchronized void queueAction(int actionId){
        mCompletedActions.addLast(actionId);
    }

    @Override
    public synchronized boolean isQueueEmpty(){
        return mCompletedActions.isEmpty();
    }

    @Override
    public synchronized int dequeueAction(){
        return mCompletedActions.removeFirst();
    }

    @Override
    public void onTaskComplete(){
        if (!isQueueEmpty()){
            new CompleteActionTask(this, this).execute();
        }
        else{
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        //Unused, this is not a bound service.
        return null;
    }
}
