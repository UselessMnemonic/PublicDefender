package nihil.publicdefender;


import android.content.Intent;
import android.location.Location;

import java.util.Date;
import java.util.UUID;

/**
 * Crime Model
 */

public class Crime {

    //TO-DO provide location services
    private UUID mUUID;
    private String mTitle;
    private Date mDate;
    private Location mLocation;
    private boolean mSolved;
    private int mSeverity;

    public Crime() {
        mUUID = UUID.randomUUID();
        mDate = new Date();
        setSeverity(0);
        setLocation(null);
        setSolved(false);
        setTitle("");
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setSeverity(int severity) { mSeverity = severity; }

    public int getSeverity() { return mSeverity; }
}
