package detection.flyingfishinc.android.motiondetection;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class Properties implements Parcelable {

    //private AccelSensor accelSensor;
    //private Context myContext;

    private final float DEFAULTMAXACCEL = 3;
    public float myMaxAccel;
    public String myMusicFileName;  //"default" if it is the default music file
    public boolean checking;    //true if the accelerometer listener is registered and checking

    public Properties(){
        myMaxAccel = DEFAULTMAXACCEL;
        myMusicFileName = "default";
        checking = false;
    }

    public Properties(Parcel in){
       // myContext = context;
        Bundle bundle = in.readBundle();
        //if(bundle.getFloat("myMaxAccel"))
        myMaxAccel = bundle.getFloat("myMaxAccel");
        myMusicFileName = bundle.getString("myMusicFileName");
    }

    //intialization fo the accelSensor object cuz dont want to do it in constructor
    public void initAccelSensor(){
       // accelSensor = new AccelSensor(myContext, DEFAULTMAXACCEL, this);
    }

//    public void onAccelAlert(){
//        MediaPlayer mediaPlayer = MediaPlayer.create(myContext, R.raw.siren_sound);
//        mediaPlayer.start();
//    }
//
//    //furthur abstraction of accel methods
//    public void changeAccel(float maxAccel){
//        accelSensor.setMyMaxAccel(maxAccel);
//
//    }
//
//    //furthur abstraction of accel methods
//    public void registerAccel(){
//        accelSensor.registerAccel();
//    }
//
//    //furthur abstraction of accel methods
//    public void unregisterAccel(){
//        accelSensor.unregisterAccel();
//    }

    //default implementation, nothing to see here folks.
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Properties> CREATOR = new Parcelable.Creator<Properties>() {
        public Properties createFromParcel(Parcel in) {
            return new Properties(in);
        }

        public Properties[] newArray(int size) {
            return new Properties[size];
        }
    };

    //Add this prop object's data to a bundle and that to the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("myMusicFileName", myMusicFileName);
        bundle.putFloat("myMaxAccel", myMaxAccel);
    }
}
