package nihil.publicdefender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by be127osx on 4/5/18.
 */

 public abstract class SingleFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.activity_crime);
        if(frag == null) {
            frag = createFragment();
            fm.beginTransaction().add(R.id.activity_crime, frag).commit();
        }
    }

    protected abstract Fragment createFragment();
}
