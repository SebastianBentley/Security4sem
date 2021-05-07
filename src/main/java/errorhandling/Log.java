package errorhandling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;


public class Log {

    private static final String FILENAME = "sebsen_log.log";
    private static final String FILEPATH = "/opt/tomcat/logs";
    private static final String PATH = FILEPATH + FILENAME;

    private Log() {
    }

    private static void log(Level lvl, String decription) throws Exception {

        Logger logger = Logger.getLogger(Log.class.getName());

        FileHandler fh = new FileHandler(PATH, true);
        fh.setFormatter(new VerySimpleFormatter());
        logger.addHandler(fh);

        logger.setLevel(Level.FINEST);   // her sætter vi niveauet for logningen.

        logger.log(lvl ,  decription  );

        fh.close();
    }

    public static void severe(String description ) {

        try {
            log(Level.SEVERE,description );
        } catch (Exception e) {
        }
    }


    public static void info(String description ) {

        try {
            log(Level.INFO,description );
        } catch (Exception e) {
        }
    }


    public static void finest(String description ) {

        try {
            log(Level.FINEST,description );
        } catch (Exception e) {
        }
    }
}


class VerySimpleFormatter extends Formatter {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    @Override
    public String format(final LogRecord record) {
        return String.format(
                "%1$s %2$-7s %3$s\n",
                new SimpleDateFormat(PATTERN).format(
                        new Date(record.getMillis())),
                record.getLevel().getName(),
                formatMessage(record));
    }
}