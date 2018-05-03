package nihil.publicdefender;

/**
 * Created by be127osx on 4/24/18.
 */

public class CrimeSchema {

    public static final class CrimeTable{

        public static final String NAME = "crimes";

        public static final class Columns {

            public static final String UUID = "uuid";
            public static final String TITILE = "title";
            public static final String SEVERITY = "severity";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String LOCATION_LONG = "location_long";
            public static final String LOCATION_LAT = "location_lat";;
            public static final String SUSPECT = "suspect";
            public static final String HAS_LOCATION = "has_location";
        }
    }
}
