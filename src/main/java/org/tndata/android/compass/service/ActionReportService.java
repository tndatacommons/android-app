package org.tndata.android.compass.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.tndata.android.compass.task.ActionReportTask;
import org.tndata.android.compass.util.NotificationUtil;

import java.util.LinkedList;


/**
 * Service that marks actions as complete in the backend.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ActionReportService
        extends Service
        implements ActionReportTask.CompleteActionInterface{

    public static final String PUSH_NOTIFICATION_ID_KEY = "org.tndata.compass.CompleteAction.NotificationId";
    public static final String ACTION_MAPPING_ID_KEY = "org.tndata.compass.CompleteAction.MappingId";
    public static final String STATE_KEY = "org.tndata.compass.CompleteAction.State";

    private LinkedList<Integer> mActions;
    private LinkedList<String> mStates;
    private int requestCount;


    @Override
    public void onCreate(){
        super.onCreate();
        mActions = new LinkedList<>();
        mStates = new LinkedList<>();
        requestCount = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int notificationId = intent.getIntExtra(PUSH_NOTIFICATION_ID_KEY, -1);
        if (notificationId != -1){
            NotificationManager manager = ((NotificationManager)getSystemService(NOTIFICATION_SERVICE));
            manager.cancel(NotificationUtil.NOTIFICATION_TYPE_ACTION_TAG, notificationId);
        }

        int actionMappingId = intent.getIntExtra(ACTION_MAPPING_ID_KEY, -1);
        String state = intent.getStringExtra(STATE_KEY);
        if (actionMappingId != -1 && state != null){
            if (isQueueEmpty()){
                queueAction(actionMappingId, state);
                new ActionReportTask(this, this).execute();
            }
            else{
                queueAction(actionMappingId, state);
            }
        }

        if (isQueueEmpty()){
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    /**
     * Queues an action to be marked as complete.
     *
     * @param actionId the action to be marked as complete.
     */
    public synchronized void queueAction(int actionId, String state){
        mActions.addLast(actionId);
        mStates.addLast(state);
    }

    @Override
    public synchronized boolean isQueueEmpty(){
        return mActions.isEmpty();
    }

    @Override
    public synchronized int dequeueAction(){
        return mActions.removeFirst();
    }

    @Override
    public synchronized String dequeueState(){
        return mStates.removeFirst();
    }

    @Override
    public synchronized void onTaskComplete(){
        if (!isQueueEmpty()){
            new ActionReportTask(this, this).execute();
        }
        else if (requestCount == 0){
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        //Unused, this is not a bound service.
        return null;
    }
}
