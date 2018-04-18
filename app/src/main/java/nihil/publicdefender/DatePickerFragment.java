package nihil.publicdefender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by be127osx on 4/17/18.
 */

//TODO PASSING DATA SLIDE, DO IT!!!!!!!

public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_picker, null);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker)
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
