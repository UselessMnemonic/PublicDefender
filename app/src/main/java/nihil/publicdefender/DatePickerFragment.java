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
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by be127osx on 4/17/18.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "nihil.publicdefender.date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date)
    {
        Bundle argumentBundle = new Bundle();
        argumentBundle.putSerializable(ARG_DATE, date);
        DatePickerFragment dpFrag = new DatePickerFragment();
        dpFrag.setArguments(argumentBundle);
        return dpFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int crimeYear = calendar.get(Calendar.YEAR);
        int crimeMonth = calendar.get(Calendar.MONTH);
        int crimeDay = calendar.get(Calendar.DAY_OF_MONTH);

        final int crimeHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int crimeMinute = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_picker, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker_date_picker);
        mDatePicker.init(crimeYear, crimeMonth, crimeDay, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Date retDate = new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), crimeHour, crimeMinute).getTime();
                        sendResult(Activity.RESULT_OK, retDate);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if(getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
