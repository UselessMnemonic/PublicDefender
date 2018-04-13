package nihil.publicdefender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.MapView;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by be127osx on 4/3/18.
 */

public class CrimeFragment extends Fragment
{
    private static final String ARG_CRIME_ID = "crime_id";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Spinner mSeveritySpinner;
    private CheckBox mSolvedCheckBox;
    //private MapView mLocationView;

    public static CrimeFragment newInstance(UUID crimeID)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        CrimeFragment instance = new CrimeFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID argCrimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(argCrimeID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = view.findViewById(R.id.crime_title_editor);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = view.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setEnabled(false);

        mSeveritySpinner = view.findViewById(R.id.severity_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.severity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSeveritySpinner.setAdapter(adapter);
        //NOTE must set adapter before selection
        mSeveritySpinner.setSelection(mCrime.getSeverity());
        mSeveritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Crime Fragment", "-------------------------------------Set selected to " + i);
                mCrime.setSeverity(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked)
            {
                mCrime.setSolved(isChecked);
            }
        });

        //mLocationView = view.findViewById(R.id.location_view);

        return view;
    }
}
