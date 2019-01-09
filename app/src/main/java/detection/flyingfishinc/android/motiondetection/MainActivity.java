package detection.flyingfishinc.android.motiondetection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static Properties props;
    private ImageButton alarmButton;
    private MovementWatchService myService;
    public static SharedPreferences mySharedPrefs;    //settings

    private boolean checked;    //true if alarm is on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySharedPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        initProperties();
        createNotificationChannel();
        alarmButton = findViewById(R.id.alarmBtn);
        Log.d(LOG_TAG, "onCreate");
    }

    //creates the notification channel only on AP! 26 and up
    //because NotificationChannel is a new library not in support library
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= 26){
            CharSequence name = "Motion Detection Alarm";
            String description = "The Motion Detection Alarm";
            int importance = NotificationManager.IMPORTANCE_LOW;    //low as in how intrusive the notification is
            NotificationChannel channel = new NotificationChannel("Channel Id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void initAlarmButton(){
        if(checked){
            alarmButton.setImageResource(R.drawable.ic_locklocked);
            Intent intent = new Intent(this, MovementWatchService.class);
            Intent serviceIntent = new Intent(this, MovementWatchService.class);
            serviceIntent.putExtra("props", props);
            ContextCompat.startForegroundService(this, serviceIntent);  //services are singletons, guaranteeing only 1 of them so this is fine
            bindService(intent, myConnection, 0);
            Log.d(LOG_TAG, "checked and initialized!");
        }
        else {
            alarmButton.setImageResource(R.drawable.ic_lockunlocked);
        }
    }

    public void initProperties(){
        String musicfileName = mySharedPrefs.getString("musicFileName", "default");
        //Log.d(LOG_TAG, musicfileName);
        if(!musicfileName.equals("default") && verifyMusic(musicfileName)) {
            props = new Properties(musicfileName);
        }
        else{
            props = new Properties("default");  //music file not found
        }
        props.setVibrate(mySharedPrefs.getBoolean("vibrate", false));   //vibration option
    }

    //checks if the music file exists
    public boolean verifyMusic(String musicFile){
        if(AccelSensor.tryMusicFile(musicFile, getApplicationContext())){
            return true;
        }
        else{
            SharedPreferences.Editor editor = mySharedPrefs.edit();
            editor.remove("musicFileName"); //does not exist so removing it
            editor.apply();
            FragmentManager manager = getSupportFragmentManager();
            MusicErrorDialogFragment dialogFragment = new MusicErrorDialogFragment();
            dialogFragment.show(manager, "dialog");
            return false;
        }
    }

    public void enterSettings(View view) {
        Log.d(LOG_TAG, "bbbutton clicked!");
        if(checked) {
            turnOffAlarm();
            checked = false;
        }
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checked = mySharedPrefs.getBoolean("checked", false);
        initAlarmButton();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mySharedPrefs.edit();
        editor.putBoolean("checked", checked);
        editor.apply();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        String yo = mySharedPrefs.getString("musicFileName","default");
        if(checked) {   //making sure there is a service to unbind
            unbindService(myConnection);
        }
        Log.d(LOG_TAG, "onStop: " + yo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    public void turnOnOffAlarm(View view) {
        checked = !checked;
        if(checked){   //means it was turned on
            alarmButton.setImageResource(R.drawable.ic_locklocked);
            Intent serviceIntent = new Intent(this, MovementWatchService.class);  //making service intent
            serviceIntent.putExtra("props", props);
            ContextCompat.startForegroundService(this, serviceIntent);  //starting the service
            Intent serviceIntent2 = new Intent(this, MovementWatchService.class);  //second service intent for binding
            bindService(serviceIntent2, myConnection, 0);
        }
        else {
            turnOffAlarm();
        }
    }

    public void turnOffAlarm(){
        alarmButton.setImageResource(R.drawable.ic_lockunlocked);
        unbindService(myConnection);
        myService.stopServing();
    }

    //Defines callbacks for service binding, passed to bindService()
    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MovementWatchService.LocalBinder binder = (MovementWatchService.LocalBinder) service;
            myService = binder.getService();
            Log.d(LOG_TAG, "We've bounded!");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Service unbounded!");
        }
    };
}
