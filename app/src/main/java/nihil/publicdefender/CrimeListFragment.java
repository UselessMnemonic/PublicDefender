package nihil.publicdefender;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by be127osx on 4/5/18.
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

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
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getUUID());
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
        updateUI();
        return view;
    }

    private void updateUI()
    {
        CrimeLab lab = CrimeLab.get(getActivity());
        if(mAdapter == null)
        {
            mAdapter = new CrimeAdapter(lab.getCrimes());
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else
            mAdapter.notifyDataSetChanged();
    }
}
