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
    private boolean mHasLocation;
    private Location mLocation;
    private boolean mSolved;
    private int mSeverity;
    private String mSuspect;

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
        if(mHasLocation)
            return new Location(mLocation);
        else
            return null;
    }

    public void setLocation(Location location) {
        if(location == null)
        {
            mHasLocation = false;
        }
        else
        {
            mLocation = new Location(location);
            mHasLocation = true;
        }
    }

    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setSeverity(int severity) { mSeverity = severity; }

    public int getSeverity() { return mSeverity; }

    public boolean hasLocation()
    {
        return mHasLocation;
    }
}
