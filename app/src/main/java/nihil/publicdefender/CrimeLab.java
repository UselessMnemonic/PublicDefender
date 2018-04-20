package nihil.publicdefender;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by be127osx on 4/5/18.
 */

public class CrimeLab {

    private static CrimeLab singleton;
    private ArrayList<Crime> mCrimeList;

    private CrimeLab(Context context) {
        mCrimeList = new ArrayList<Crime>();
    }

    public static CrimeLab get(Context context) {
        if(singleton == null)
            singleton = new CrimeLab(context);
        return singleton;
    }

    public ArrayList<Crime> getCrimes()
    {
        return mCrimeList;
    }

    public Crime getCrime(UUID uuid)
    {
        for(Crime c : mCrimeList)
            if(c.getUUID().toString().equals(uuid.toString()))
                return c;

        return null;
    }

    public void addCrime(Crime c)
    {
        mCrimeList.add(c);
    }

    public void deleteCrime(UUID crimeID)
    {
        Crime crime = getCrime(crimeID);
        if(crime != null)
            mCrimeList.remove(crime);
    }
}
