package nihil.publicdefender;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.util.Date;
import java.util.List;
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
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Spinner mSeveritySpinner;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;

    private ImageView mCrimePhoto;
    private File mPhotoFile;
    private ImageButton mPhotoButton;

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
        mPhotoFile = CrimeLab.get(getActivity()).getCrimeImageFile(mCrime);
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
        mDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dDialog = DatePickerFragment.newInstance(mCrime.getDate());
                dDialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dDialog.show(fm, DIALOGE_DATE);
                }
            });

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

        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

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
                if(crimeLoc == null)
                {
                    Toast.makeText(getContext(), R.string.crime_report_location_unknown, Toast.LENGTH_SHORT).show();
                    crimeLoc = new Location("");
                    crimeLoc.setLongitude(0.0);
                    crimeLoc.setLatitude(0.0);
                }

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

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCrimePhoto = view.findViewById(R.id.crime_photo);
        mPhotoButton = view.findViewById(R.id.crime_camera_button);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "nihil.publicdefender.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        setHasOptionsMenu(true);
        updatePhotoView();
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
            case REQUEST_CONTACT:
                if(data != null) {
                    Uri contactUri = data.getData();
                    String[] queryFields = new String[] {
                            ContactsContract.Contacts.DISPLAY_NAME
                    };
                    Cursor c = getActivity().getContentResolver()
                            .query(contactUri, queryFields, null, null, null);
                    try {
                        if (c.getCount() == 0) {
                            return;
                        }
                        c.moveToFirst();
                        String suspect = c.getString(0);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                    }
                    finally {
                        c.close();
                    }
                }
                break;
            case REQUEST_PHOTO:
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.baker.android.workcrimes.fileprovider",
                        mPhotoFile);
                getActivity().revokeUriPermission(uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                updatePhotoView();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mCrimePhoto.setImageDrawable(null);
        }
        else {
            Bitmap bitmap =
                    PictureUtils.getScaledBitmap(mPhotoFile.getPath(),
                            getActivity());
            mCrimePhoto.setImageBitmap(bitmap);
        }
    }

    public String getCrimeReport() {
        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String locationString;
        Location loc = mCrime.getLocation();
        if(mCrime.hasLocation())
            locationString = getString(R.string.crime_report_location, loc.getLatitude(), loc.getLongitude());
        else
            locationString = getString(R.string.crime_report_location_unknown);

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect, locationString);
        return report;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
}