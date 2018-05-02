package nihil.publicdefender;


import android.location.Location;

import java.util.Date;
import java.util.UUID;

/**
 * Crime Model
 */

public class Crime {

    private UUID mUUID;
    private String mTitle;
    private Date mDate;
    private Location mLocation;
    private boolean mSolved;
    private int mSeverity;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid)
    {
        mUUID = uuid;
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

    public void setDate(Date date) { mDate = date; }

    public Location getLocation() {
        if(mLocation == null)
            return null;
        return new Location(mLocation);
    }

    public void setLocation(Location location) {
        if(location == null)
            return;
        mLocation = new Location(location);
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
