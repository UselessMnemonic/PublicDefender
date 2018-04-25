package nihil.publicdefender;


import android.database.Cursor;
import android.database.CursorWrapper;
import android.location.Location;
import android.location.LocationProvider;

import java.util.Date;
import java.util.UUID;

/**
 * Created by be127osx on 4/24/18.
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {

        String UUIDString = getString(getColumnIndex(CrimeSchema.CrimeTable.Columns.UUID));
        String title = getString(getColumnIndex(CrimeSchema.CrimeTable.Columns.TITILE));
        long date = getLong(getColumnIndex(CrimeSchema.CrimeTable.Columns.DATE));
        int isSolved = getInt(getColumnIndex(CrimeSchema.CrimeTable.Columns.SOLVED));
        int severity = getInt(getColumnIndex(CrimeSchema.CrimeTable.Columns.SEVERITY));
        double locLat = getDouble(getColumnIndex(CrimeSchema.CrimeTable.Columns.LOCATION_LAT));
        double locLong = getDouble(getColumnIndex(CrimeSchema.CrimeTable.Columns.LOCATION_LONG));

        Location crimeLocation = new Location("");
        crimeLocation.setLatitude(locLat);
        crimeLocation.setLongitude(locLong);

        Crime crime = new Crime(UUID.fromString(UUIDString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSeverity(severity);
        crime.setLocation(crimeLocation);
        return crime;
    }
}
