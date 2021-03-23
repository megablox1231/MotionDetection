package detect.flyingfishinc.android.motiondetection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class AccelSensor implements SensorEventListener{

    private static final String LOG_TAG = AccelSensor.class.getSimpleName();

    private SensorManager mySensorManager;
    private Sensor  myAccelerometer;
    private Properties myProps;   //the properties object that owns this listener
    private Context myContext;  //the context of the service
    private MediaPlayer myMediaPlayer;
    private MovementWatchService mwService;


    private long lastTime;  //the time of the last call of accelchange
    private long curTime;   //the time of the current accelchange
    private float[] curAccels;   //the absolute value x, y, and z values of the curAccels from the accelerometer
    private float[] lastAccels; //the absolute value x, y, and z values of the curAccels from the accelerometer on the last change
    private float myMaxAccel; //the max curAccels change before alarm goes off
    private boolean initAccels;    //true if last Accels have not been recorded yet
    private String myMusicFileName; //the name of the music file for the alarm

    public AccelSensor(Context context, Properties props, MovementWatchService service){
        myContext = context;
        mwService = service;
        mySensorManager = (SensorManager) myContext.getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        curAccels = new float[3];
        lastAccels = new float[3];
        initAccels = true;
        myProps = props;
        myMaxAccel = myProps.myMaxAccel;
        myMusicFileName = myProps.myMusicFileName;
        initMediaPlayer();
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            curTime = System.currentTimeMillis();
            if (initAccels) {
                lastAccels[0] = Math.abs(event.values[0]);  //abs may not work well because going from +1 to -1 is change == 2 but no alert.
                lastAccels[1] = Math.abs(event.values[1]);
                lastAccels[2] = Math.abs(event.values[2]);
                initAccels = false;
            } else if(curTime - lastTime > 100) {   //ensures not too many checks happening
                curAccels[0] = Math.abs(event.values[0]);
                curAccels[1] = Math.abs(event.values[1]);
                curAccels[2] = Math.abs(event.values[2]);
                for (int i = 0; i < 3; i++) {
                    if (curAccels[i] - lastAccels[i] > myMaxAccel) {
                        onAccelAlert();
                        Log.d(LOG_TAG, "ALERT DEVICE MOVED!!!");
                    }
                }
            }
            lastTime = curTime;
        }
    }

    //use to register the sensor listener when resuming app
    public void registerAccel(){
        mySensorManager.registerListener(this, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(LOG_TAG, "Listener Registered");
    }

    //use to unregister the sensor listener when pausing or elsewhere
    public void unregisterAccel() {
        mySensorManager.unregisterListener(this);
        Log.d(LOG_TAG, "Listener Unregistered");
    }

    public void setMyMaxAccel(float maxAccel){
        myMaxAccel = maxAccel;
    }

    //plays the alarm sound and calls all the other stuff apparently
    public void onAccelAlert(){
        unregisterAccel();
        myMediaPlayer.setLooping(true);
        myMediaPlayer.start();
        myProps.checking = false;
        if(myProps.getVibrate()) {
            mwService.vibrate();
            Log.d(LOG_TAG, "To vibrate");
        }
    }

    private void initMediaPlayer(){
        if(myMusicFileName.equals("default")) { //play the default alarm sound
            myMediaPlayer = MediaPlayer.create(myContext, R.raw.siren_sound);
        }
        else{
            Uri uri = Uri.parse(myMusicFileName);
            myMediaPlayer = MediaPlayer.create(myContext, uri);
        }
    }

    //used to check if a music file exists
    public static boolean tryMusicFile(String myMusicFileName, Context myContext){
        try {
            Uri uri = Uri.parse(myMusicFileName);
            MediaPlayer myMediaPlayer = MediaPlayer.create(myContext, uri);
            myMediaPlayer.getDuration(); //called to check if myMediaPlayer is null
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public MediaPlayer getMediaPlayer(){
        return myMediaPlayer;
    }
}
