package detection.flyingfishinc.android.motiondetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ResetDialogFragment.ResetDialogListener {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private String selectedMusic;   //either default or custom music file
    private CheckBox vibeBox;   //checkbox for vibration
    private NDSpinner mySpinner;

    private final int MUSIC_SELECT_CODE = 1;    //code for the music intent
    private boolean initializing;    //true if creation being done  from onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mySpinner = findViewById(R.id.musicSpinner);
        mySpinner.setOnItemSelectedListener(this);    //sets this activity as the listener
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.musicLabels, android.R.layout.simple_spinner_item);  //spinner needs adapter to connect to array of items
        adapter.setDropDownViewResource(R.layout.spinner_item);
        mySpinner.setAdapter(adapter);
        initializing = true;
        initSpinner(mySpinner);
        initVibeBox();
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

    public void initVibeBox(){
        vibeBox = findViewById(R.id.vibeCheckBox);
        if(MainActivity.props.getVibrate()){
            vibeBox.setChecked(true);   //if the vibrate option is on then the checkbox must be checked
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //keeps the permission to access the file persistant even after phone restarts
                    getApplicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
                Log.d(LOG_TAG, "Custom music selected!");
            }
        }

        //saves all of the settings
    public void saveSettings(View view) {
        SharedPreferences.Editor editor = MainActivity.mySharedPrefs.edit();
        editor.putString("musicFileName", selectedMusic);

        if(vibeBox.isChecked()) {
            editor.putBoolean("vibrate", true);
        }
        else{
            editor.putBoolean("vibrate", false);
        }
        editor.apply();

        MainActivity.props.setMusicFileName(selectedMusic);
        MainActivity.props.setVibrate(vibeBox.isChecked());

        Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordLayout), "Settings Saved!", Snackbar.LENGTH_SHORT);  //popup
        snackbar.show();
        Log.d(LOG_TAG, "Settings saved!");
    }


    //sets all settings to defaults
    public void resetSettings(View view) {
        //TODO: Do the dialog thing
        FragmentManager manager = getSupportFragmentManager();
        ResetDialogFragment dialogFragment = new ResetDialogFragment();
        dialogFragment.show(manager, "dialog");
    }

    //the actual resetting, only done if yes in dialog chosen
    public void resetHelper(){
        selectedMusic = "default";
        mySpinner.setSelection(0);
        vibeBox.setChecked(false);
        SharedPreferences.Editor editor = MainActivity.mySharedPrefs.edit();
        editor.putString("musicFileName", selectedMusic);
        editor.putBoolean("vibrate", vibeBox.isChecked());
        editor.apply();

        MainActivity.props.setMusicFileName(selectedMusic);
        MainActivity.props.setVibrate(vibeBox.isChecked());

        // Snackbar snackbar = Snackbar.make(findViewById(R.id.myCoordLayout), "Settings Saved!", Snackbar.LENGTH_SHORT);  //popup
        // snackbar.show();
        Log.d(LOG_TAG, "Settings reset!");
    }

    //if yes is chosen on the resetDialog
    @Override
    public void onYes() {
        resetHelper();
    }
}
