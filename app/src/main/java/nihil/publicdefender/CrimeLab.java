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

        Random randomizer = new Random();
        Crime filler;
        for(int i = 0; i < 20; i++)
        {
            filler = new Crime();
            filler.setSeverity(randomizer.nextInt(4));
            filler.setSolved(randomizer.nextBoolean());
            filler.setTitle("Crime " + (i+1));
            mCrimeList.add(filler);
        }
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
}
