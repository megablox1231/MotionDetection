package detect.flyingfishinc.android.motiondetection;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Properties implements Parcelable {


    private final float DEFAULTMAXACCEL = 0.5f;
    public float myMaxAccel;
    public String myMusicFileName;  //"default" if it is the default music file
    public boolean checking;    //true if the accelerometer listener is registered and checking
    public boolean vibrate; //true if the alarm vibrates when triggered

    public Properties(){
        myMaxAccel = DEFAULTMAXACCEL;
        myMusicFileName = "default";
        checking = false;
        vibrate = false;
    }

    public Properties(String musicFileName) {
        myMaxAccel = DEFAULTMAXACCEL;
        myMusicFileName = musicFileName;
        checking = false;
        vibrate = false;
    }

    public Properties(Parcel in){
        Bundle bundle = in.readBundle();
        myMaxAccel = bundle.getFloat("myMaxAccel");
        myMusicFileName = bundle.getString("myMusicFileName");
        vibrate = bundle.getBoolean("vibrate");
    }

    public void setMusicFileName(String musicFileName) {
        myMusicFileName = musicFileName;
    }

    public void setVibrate(boolean vibing){
        vibrate = vibing;
    }

    public boolean getVibrate(){
        return vibrate;
    }

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
        bundle.putBoolean("vibrate", vibrate);
        dest.writeBundle(bundle);
    }
}
