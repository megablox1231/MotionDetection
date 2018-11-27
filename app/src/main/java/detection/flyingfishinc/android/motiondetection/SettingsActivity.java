package detection.flyingfishinc.android.motiondetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private String selectedMusic;

    private final int MUSIC_SELECT_CODE = 1;    //code for the music intent
    private boolean initializing;    //true if creation being done  from onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NDSpinner spinner = findViewById(R.id.musicSpinner);
        spinner.setOnItemSelectedListener(this);    //sets this activity as the listener
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.musicLabels, android.R.layout.simple_spinner_item);  //spinner needs adapter to connect to array of items
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        initializing = true;
        initSpinner(spinner);
        Log.d(LOG_TAG, "onCreate");
    }

    //sets the selected spinner item
    public void initSpinner(Spinner spinner){
        String music = MainActivity.mySharedPrefs.getString("musicFileName", "default");
        selectedMusic = music;
        if(music.equals("default")){
            spinner.setSelection(0);
        }
        else {
            spinner.setSelection(1);
        }
    }

    public void exitSettings(View view) {
        Log.d(LOG_TAG, "exit button");
        finish();
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

    /*called when an item in the spinner is selected
    Params: view: the view/item selected
            position: the position of the view
            id: the row id of the view
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!initializing) {
            if (position == 1) {
                Intent musicIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                musicIntent.setType("audio/*");
                startActivityForResult(Intent.createChooser(musicIntent, "Choose a music file"), MUSIC_SELECT_CODE);
            }
            else if(position == 0) {
                selectedMusic = "default";
            }
            else {
                selectedMusic = parent.getItemAtPosition(position).toString();
            }
        }
        else{
            initializing = false;   //done so this stuff not called when settings activity being made
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MUSIC_SELECT_CODE) {
            if (resultCode == RESULT_OK) {    //file chosen
                Uri uri = data.getData();
                String path = uri.toString();
                selectedMusic = path;
                Log.d(LOG_TAG, "Custom music selected!");
            }
        }
    }

    public void saveSettings(View view) {
        SharedPreferences.Editor editor = MainActivity.mySharedPrefs.edit();
        editor.putString("musicFileName", selectedMusic);
        editor.apply();
        MainActivity.props.setMusicFileName(selectedMusic);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordLayout), "Settings Saved!", Snackbar.LENGTH_SHORT);  //popup
        snackbar.show();
        Log.d(LOG_TAG, "settings Saved!");
    }
}
