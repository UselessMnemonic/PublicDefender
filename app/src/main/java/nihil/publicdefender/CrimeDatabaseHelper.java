package nihil.publicdefender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CrimeDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeDatabase.db";

    public CrimeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeSchema.CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeSchema.CrimeTable.Columns.UUID + ", " +
                CrimeSchema.CrimeTable.Columns.DATE + ", " +
                CrimeSchema.CrimeTable.Columns.SEVERITY + ", " +
                CrimeSchema.CrimeTable.Columns.TITILE + ", " +
                CrimeSchema.CrimeTable.Columns.SOLVED + ", " +
                CrimeSchema.CrimeTable.Columns.HAS_LOCATION + ", " +
                CrimeSchema.CrimeTable.Columns.LOCATION_LAT + ", " +
                CrimeSchema.CrimeTable.Columns.LOCATION_LONG + ", " +
                CrimeSchema.CrimeTable.Columns.SUSPECT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
