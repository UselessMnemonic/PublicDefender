package nihil.publicdefender;

import android.support.v4.app.Fragment;

/**
 * Created by be127osx on 4/5/18.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }

}
