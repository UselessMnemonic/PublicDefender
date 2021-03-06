package nihil.publicdefender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by be127osx on 4/17/18.
 */

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "nihil.publicdefender.extra_cime_id";

    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    private Button mFirstButton, mLastButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        Crime currentCrime = CrimeLab.get(this).getCrime( (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID));

        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getUUID());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setCurrentItem(mCrimes.indexOf(currentCrime));

        mFirstButton = findViewById(R.id.go_to_first_button);
        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLastButton = findViewById(R.id.go_to_last_button);
        mLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });
    }

    public static Intent newIntent(Context context, UUID crimeID)
    {
        Intent newIntent = new Intent(context, CrimePagerActivity.class);
        newIntent.putExtra(EXTRA_CRIME_ID, crimeID);
        return newIntent;
    }
}