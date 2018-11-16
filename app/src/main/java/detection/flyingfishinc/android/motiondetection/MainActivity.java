package detection.flyingfishinc.android.motiondetection;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Properties props;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: check if properties have already been created
        Properties props = new Properties();
        Intent notifIntent = new Intent(this, MovementWatchService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                .setContentTitle(getText(getApplicationContext().))
        Log.d(LOG_TAG, "onCreate");
    }

    public void enterSettings(View view) {
        Log.d(LOG_TAG, "button clicked!");
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
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
}
