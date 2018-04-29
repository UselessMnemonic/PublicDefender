package nihil.publicdefender;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;
import java.util.UUID;

/**
 * Created by be127osx on 4/3/18.
 */

public class CrimeFragment extends Fragment
{
    private static final String ARG_CRIME_ID = "crime_id";

    private static final String DIALOGE_DATE = "dialoge_date";
    private static final String DIALOGE_TIME = "dialoge_time";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Spinner mSeveritySpinner;
    private CheckBox mSolvedCheckBox;

    private MapView mLocationView;
    private GoogleMap mMap;

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
        setHasOptionsMenu(true);
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
        updateDate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getFragmentManager();

                    DatePickerFragment dDialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dDialog.show(fm, DIALOGE_DATE);
                }
            });
        }
        else {
            mDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), R.string.unsupported_version_message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        mSeveritySpinner = view.findViewById(R.id.severity_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.severity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSeveritySpinner.setAdapter(adapter);
        //NOTE must set adapter before selection
        mSeveritySpinner.setSelection(mCrime.getSeverity());
        mSeveritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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


        //MAP STUFF
        mLocationView = (MapView) view.findViewById(R.id.location_view);
        mLocationView.onCreate(savedInstanceState);

        mLocationView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mLocationView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                mMap = gMap;

                Location crimeLoc = mCrime.getLocation();

                // For dropping a marker at a point on the Map
                LatLng crimeLatLng = new LatLng(crimeLoc.getLatitude(), crimeLoc.getLongitude());
                mMap.addMarker(new MarkerOptions().draggable(true).position(crimeLatLng).title(getString(R.string.crime_location_title)).snippet(getString(R.string.crime_location_description)));
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                         LatLng nextLatLng = marker.getPosition();
                         Location nextLocation = new Location("");
                         nextLocation.setLatitude(nextLatLng.latitude);
                         nextLocation.setLongitude(nextLatLng.longitude);
                         mCrime.setLocation(nextLocation);
                    }
                });
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException se) {
                    Toast.makeText(getContext(), R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(crimeLatLng).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        //END MAP STUFF

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getContext()).deleteCrime(mCrime.getUUID());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode)
        {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();

                FragmentManager fm = getFragmentManager();

                TimePickerFragment tDialog = TimePickerFragment.newInstance(mCrime.getDate());
                tDialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                tDialog.show(fm, DIALOGE_TIME);
                break;

            case REQUEST_TIME:
                Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setDate(time);
                updateDate();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
}