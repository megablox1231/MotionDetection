package detection.flyingfishinc.android.motiondetection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class MusicErrorDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        //builder builds the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layoutInflater.inflate(R.layout.dialog_music_error, null))
        //Adding action buttons
        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicErrorDialogFragment.this.getDialog().cancel(); //dismisses the dialog
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBacking));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorAccent));
        alertDialog.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF000000, 0xFF000000));
        alertDialog.show();
        return alertDialog;
    }
}
