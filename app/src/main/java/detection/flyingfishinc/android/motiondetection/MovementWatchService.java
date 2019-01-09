package detection.flyingfishinc.android.motiondetection;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MovementWatchService extends IntentService {

    private static final String LOG_TAG = MovementWatchService.class.getSimpleName();
    private final IBinder myBinder = new LocalBinder(); //binder given to clients
    private Properties myProps;
    private AccelSensor myAccelSensor;
    private Vibrator myVibrator;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "detection.flyingfishinc.android.motiondetection.action.FOO";
    private static final String ACTION_BAZ = "detection.flyingfishinc.android.motiondetection.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "detection.flyingfishinc.android.motiondetection.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "detection.flyingfishinc.android.motiondetection.extra.PARAM2";

    public MovementWatchService() {
        super("MovementWatchService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent activityIntent = new Intent(this, MainActivity.class);   //making activity intent for the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel Id") //making the notification
                //.setContentTitle("yo")
                //.setContentText("yoyo")
                .setSmallIcon(R.drawable.ic_locklocked)
                .setPriority(NotificationCompat.PRIORITY_LOW)  //same as importance for channel
                .setContentIntent(pendingIntent)    //sets the intent to start when service is clicked
                .setAutoCancel(false);  //notification doesn't die when clicked
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(12345, builder.build());
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    //class used for client binder
    public class LocalBinder extends Binder {
        MovementWatchService getService(){
            return MovementWatchService.this;
        }
    }

    @Override   //what is given to the client when bound
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public Properties getProperties(){
        return myProps;
    }

    public AccelSensor getAccelSensor(){
        return myAccelSensor;
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MovementWatchService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MovementWatchService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Service started!");
        myProps = intent.getParcelableExtra("props");
        myAccelSensor = new AccelSensor(getApplicationContext(), myProps);
        //delayed setting up of the listener
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "oof interuption");
        }
        myAccelSensor.registerAccel();
        Log.d(LOG_TAG, "done");
        myProps.checking = true;
        Log.d(LOG_TAG, Boolean.toString(myProps.checking));
        while(myProps.checking){  //keeps service running
            Log.d(LOG_TAG, "kill me");   //keeps doing this even after death of service; stops when app dies
        }
        vibrate();
        Log.d(LOG_TAG, "To vibrate");
        while(true){
            //keeps service running after alarm triggered
        }
    }

    public void vibrate() {
        Log.d(LOG_TAG, "vibrating!");
        myVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //if build version is 26 or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //2147483647 is max int; about 24 days; amplitude is 1-255
            myVibrator.vibrate(VibrationEffect.createOneShot(2147483647, 100));
        }
        else{
            //deprecated version
            myVibrator.vibrate(2147483647);
        }
    }

    //stuff done before stopping service
    public void stopServing() {
        Log.d(LOG_TAG, "donezo");
        MediaPlayer mediaPlayer = myAccelSensor.getMediaPlayer();
        if(mediaPlayer.isPlaying()){    //see if the alarm has been triggered yet
            mediaPlayer.stop();
            if(myProps.getVibrate()) {
                myVibrator.cancel();
            }
        }
        else{
            myAccelSensor.unregisterAccel();
        }
        stopForeground(true);
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Service Destroyed!");
        super.onDestroy();
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
