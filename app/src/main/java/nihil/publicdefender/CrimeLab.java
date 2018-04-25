package nihil.publicdefender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab singleton = null;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mDatabase = new CrimeDatabaseHelper(context).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if(singleton == null)
            singleton = new CrimeLab(context);
        return singleton;
    }

    public ArrayList<Crime> getCrimes()
    {
        ArrayList<Crime> list = new ArrayList<Crime>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                list.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return list;
    }

    public Crime getCrime(UUID uuid)
    {
        CrimeCursorWrapper cursor = queryCrimes(CrimeSchema.CrimeTable.Columns.UUID + " = ?", new String[]{uuid.toString()});
        try {
            if(cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void addCrime(Crime c)
    {
        mDatabase.insert(CrimeSchema.CrimeTable.NAME, null, getContentValues(c));
    }

    public void updateCrime(Crime crime)
    {
        String UUIDString = crime.getUUID().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeSchema.CrimeTable.NAME, values,
                CrimeSchema.CrimeTable.Columns.UUID + " = ?",
                new String[] { UUIDString });
    }

    public void deleteCrime(UUID crimeID)
    {
        mDatabase.delete(CrimeSchema.CrimeTable.NAME, CrimeSchema.CrimeTable.Columns.UUID + " = ?", new String[] { crimeID.toString() });
    }

    private static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(CrimeSchema.CrimeTable.Columns.UUID, crime.getUUID().toString());
        values.put(CrimeSchema.CrimeTable.Columns.DATE, crime.getDate().getTime());

        Location loc = crime.getLocation();
        double locLat = 0.0, locLng = 0.0;
        if(loc != null) {
            locLng = crime.getLocation().getLatitude();
            locLng = crime.getLocation().getLongitude();
        }

        values.put(CrimeSchema.CrimeTable.Columns.LOCATION_LONG, locLng);
        values.put(CrimeSchema.CrimeTable.Columns.LOCATION_LAT, locLat);

        values.put(CrimeSchema.CrimeTable.Columns.SEVERITY, crime.getSeverity());
        values.put(CrimeSchema.CrimeTable.Columns.SOLVED, crime.isSolved());
        values.put(CrimeSchema.CrimeTable.Columns.TITILE, crime.getTitle());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeSchema.CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }
}
