package org.nqlinq.helpers;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    final static SimpleDateFormat dateTimeFormatter = new  SimpleDateFormat("yyyy-MM-dd-HH-mm");
    final static SimpleDateFormat dateFormatter = new  SimpleDateFormat("yyyy-MM-dd");

    public static long elapsed (Date dateTime) {
        return System.currentTimeMillis() - toEpoch(dateTime);
    }

    public static long toEpoch (Date dateTime) {
        return dateTime.getTime();
    }

    public static long toEpoch (String dateTime) {
        return toNormalDateTime(dateTime).getTime();
    }

    public static Date toInternalDateTime(long dateTime){
        return new Date(dateTime);
    }

    public static Date toNormalDateTime(String str){
        str = str.replace('/', '-');

        if (str.length() == 8)
            str = MessageFormat.format("{0}-{1}-{2}-00-00", str.substring(0, 4), str.substring(4, 2), str.substring(6, 2));

        if (str.length() == 12) {
            str =  MessageFormat.format("{0}-{1}-{2}-{3}-{4}", str.substring(0, 4), str.substring(4, 2), str.substring(6, 2),
                    str.substring(8, 2), str.substring(10, 2));
        }

        try {
            return dateTimeFormatter.parse(str);
        } catch (ParseException e) {
            try {
                return dateFormatter.parse(str);
            } catch (ParseException e1) {
                return Calendar.getInstance().getTime();
            }
        }
    }
}
