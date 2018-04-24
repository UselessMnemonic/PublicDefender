package nihil.publicdefender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by be127osx on 4/17/18.
 */

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "nihil.publicdefender.time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date)
    {
        Bundle argumentBundle = new Bundle();
        argumentBundle.putSerializable(ARG_TIME, date);
        TimePickerFragment tpFrag = new TimePickerFragment();
        tpFrag.setArguments(argumentBundle);
        return tpFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Date time = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time_picker, null);

        mTimePicker = v.findViewById(R.id.dialog_date_picker_time_picker);
        //set the time to whatever the crime indicates
        mTimePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setMinute(cal.get(Calendar.MINUTE));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //create a calendar with the same date and time as we started
                        Calendar retCal = GregorianCalendar.getInstance();
                        retCal.setTime(time);
                        //set the new *TIME*
                        retCal.set(Calendar.HOUR, mTimePicker.getHour());
                        retCal.set(Calendar.MINUTE, mTimePicker.getMinute());

                        sendResult(Activity.RESULT_OK, retCal.getTime());
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date time) {
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
