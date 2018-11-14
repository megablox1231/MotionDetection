package detection.flyingfishinc.android.motiondetection;

import android.content.Context;
import android.media.MediaPlayer;

public class Properties {

    private AccelSensor accelSensor;

    private Context myContext;
    private final float DEFAULTMAXACCEL = 3;

    public Properties(Context context){
        myContext = context;
    }

    //intialization fo the accelSensor object cuz dont want to do it in constructor
    public void initAccelSensor(){
        accelSensor = new AccelSensor(myContext, DEFAULTMAXACCEL, this);
    }

    public void onAccelAlert(){
        MediaPlayer mediaPlayer = MediaPlayer.create(myContext, R.raw.siren_sound);
        mediaPlayer.start();
    }

    //furthur abstraction of accel methods
    public void changeAccel(float maxAccel){
        accelSensor.setMyMaxAccel(maxAccel);
    }

    //furthur abstraction of accel methods
    public void registerAccel(){
        accelSensor.registerAccel();
    }

    //furthur abstraction of accel methods
    public void unregisterAccel(){
        accelSensor.unregisterAccel();
    }

}
