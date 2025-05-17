package detect.flyingfishinc.android.motiondetection;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class MovementWatchService extends IntentService {

    private static final String LOG_TAG = MovementWatchService.class.getSimpleName();
    private final IBinder myBinder = new LocalBinder(); //binder given to clients
    private Properties myProps;
    private AccelSensor myAccelSensor;
    private Vibrator myVibrator;
    private boolean destroy = false;    //true if onDestroy has been called

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
        MovementWatchService getService() {
            return MovementWatchService.this;
        }
    }

    @Override   //what is given to the client when bound
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public Properties getProperties() {
        return myProps;
    }

    public AccelSensor getAccelSensor() {
        return myAccelSensor;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Service started!");
        myProps = intent.getParcelableExtra("props");
        myAccelSensor = new AccelSensor(getApplicationContext(), myProps, this);
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
        int i = 0;
        while (myProps.checking && !destroy) {  //keeps service running
            if (i == 1000000) {
                Log.d(LOG_TAG, "kill me");   //keeps doing this even after death of service; stops when app dies
                i = 0;
            }
            i++;
        }
        while (!destroy) {
            //keeps service running after alarm triggered
        }
    }

    public void vibrate() {
        Log.d(LOG_TAG, "vibrating!");
        myVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //if build version is 26 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //2147483647 is max int; about 24 days; amplitude is 1-255
            myVibrator.vibrate(VibrationEffect.createOneShot(2147483647, 100));
        } else {
            //deprecated version
            myVibrator.vibrate(2147483647);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Service Destroyed!");
        MediaPlayer mediaPlayer = myAccelSensor.getMediaPlayer();
        if (mediaPlayer.isPlaying()) {    //see if the alarm has been triggered yet
            mediaPlayer.stop();
            if (myProps.getVibrate()) {
                myVibrator.cancel();
            }
        }
        myAccelSensor.unregisterAccel();
        stopForeground(true);
        destroy = true;
        super.onDestroy();
    }
}