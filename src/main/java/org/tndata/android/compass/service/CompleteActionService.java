package org.tndata.android.compass.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.task.CompleteActionTask;
import org.tndata.android.compass.task.GetUserActionsTask;

import java.util.ArrayList;
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
                CompleteActionTask.CompleteActionInterface,
                GetUserActionsTask.GetUserActionsListener{

    public static final String ACTION_ID_KEY = "org.tndata.compass.CompleteAction.Id";
    public static final String NOTIFICATION_ID_KEY = "org.tndata.compass.CompleteAction.NotificationId";
    public static final String ACTION_MAPPING_ID_KEY = "org.tndata.compass.CompleteAction.MappingId";

    private LinkedList<Integer> mCompletedActions;
    private int requestCount;


    @Override
    public void onCreate(){
        super.onCreate();
        mCompletedActions = new LinkedList<>();
        requestCount = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int actionMappingId = intent.getIntExtra(ACTION_MAPPING_ID_KEY, -1);
        if (actionMappingId != -1){
            if (isQueueEmpty()){
                queueAction(actionMappingId);
                new CompleteActionTask(this, this).execute();
            }
            else{
                queueAction(actionMappingId);
            }
        }
        else{
            int notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(notificationId);

            fetchAction(intent.getIntExtra(ACTION_ID_KEY, -1));
        }

        return START_NOT_STICKY;
    }

    /**
     * Retrieves an action from an id.
     *
     * @param actionId the id of the action to be fetched.
     */
    private void fetchAction(int actionId){
        CompassApplication application = (CompassApplication)getApplication();
        String token = application.getToken();
        if (!token.isEmpty()){
            new GetUserActionsTask(this).execute(token, "action:" + actionId);
            requestCount++;
        }
    }

    @Override
    public synchronized void actionsLoaded(ArrayList<Action> actions){
        boolean success = false;
        if (actions != null && actions.size() > 0){
            int actionMappingId = actions.get(0).getMappingId();
            if (actionMappingId != -1){
                success = true;
                if (isQueueEmpty()){
                    queueAction(actionMappingId);
                    new CompleteActionTask(this, this).execute();
                }
                else{
                    queueAction(actionMappingId);
                }
            }
        }
        requestCount--;
        if (!success && requestCount == 0 && isQueueEmpty()){
            stopSelf();
        }
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
    public synchronized void onTaskComplete(){
        if (!isQueueEmpty()){
            new CompleteActionTask(this, this).execute();
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
