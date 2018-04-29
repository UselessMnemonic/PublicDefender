package nihil.publicdefender;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class CrimeListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    private GoogleApiClient mGoogleApiClient;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //setup google api
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Log.d("CRIME_LIST", "API GET!");

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("CRIME_LIST", "LOCATION UPDATE!");
                if (locationResult == null)
                    return;
                mLastLocation = locationResult.getLastLocation();
                Log.d("CRIME_LIST", mLastLocation.toString());
            };
        };

        mRequestingLocationUpdates = false;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private ProgressBar mSeverityBar;

        private Crime mCrime;

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            mSeverityBar.setMax(3);
            int s = mCrime.getSeverity();
            int color = android.R.color.background_light;
            switch (s) {
                case 0:
                    break;
                case 1:
                    color = android.R.color.holo_blue_dark;
                    break;
                case 2:
                    color = android.R.color.holo_orange_light;
                    break;
                case 3:
                    color = android.R.color.holo_red_light;
                    break;
            }
            mSeverityBar.setProgress(s);
            mSeverityBar.getProgressDrawable().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN);
        }

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedImageView = itemView.findViewById(R.id.list_item_crime_solved_image_view);
            mSeverityBar = itemView.findViewById(R.id.list_item_crime_severity_progress_bar);
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getUUID());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        private ArrayList<Crime> mCrimes;

        public CrimeAdapter(ArrayList<Crime> crimes)
        {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from((getActivity()));
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position)
        {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        public void setCrimes(ArrayList<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount()
        {
            return mCrimes.size();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle)
    {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceBundle != null)
            mSubtitleVisible = savedInstanceBundle.getBoolean(SAVED_SUBTITLE_VISIBLE);

        updateUI();
        return view;
    }

    private void updateUI()
    {
        CrimeLab lab = CrimeLab.get(getActivity());
        ArrayList<Crime> crimes = lab.getCrimes();

        if(mAdapter == null)
        {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }
    
    public void onSaveInstanceSate(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);
        savedState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible)
            subtitleItem.setTitle(R.string.hide_subtitle);
        else
            subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                crime.setLocation(mLastLocation);
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getUUID());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!mSubtitleVisible)
            subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LIST_FRAG", "Connected to API!");
        mFusedLocationClient =  LocationServices.getFusedLocationProviderClient(getActivity());
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("LIST_FRAG", "Connection to API suspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("LIST_FRAG", "Connection to API failed!");
    }

    //FIGUrE LOCATION OUT PLZ
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRequestingLocationUpdates)
            startLocationUpdates();
        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        mRequestingLocationUpdates = false;
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(10000), mLocationCallback, null);
        }catch(SecurityException se) {
            Toast.makeText(getContext(), R.string.unable_to_get_location, Toast.LENGTH_LONG).show();
        }
    }
}
