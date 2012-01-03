package org.nqlinq.helpers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConversionHelper {
    private ConversionHelper() {

    }

    public static String ConvertToString(String str) {
        return str;
    }

    public static int ConvertToInt(String str) {
        return Integer.parseInt(str);
    }

    public static Timestamp ConvertToTimestamp(String str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = formatter.parse(str);

            return new Timestamp(date.getTime());
        } catch (ParseException ex) {
            return new Timestamp(new Date(0L).getTime());
        }
    }

    public static float ConvertToFloat(String str) {
        return Float.parseFloat(str);
    }

    public static boolean ConvertToBoolean(String str) {
        return Boolean.parseBoolean(str);
    }

    public static double ConvertToDouble(String str) {
        return Double.parseDouble(str);
    }

    public static BigDecimal ConvertToBigdecimal(String str) {
        final DecimalFormatSymbols symbols;
        final char groupSeparatorChar;
        final String groupSeparator;
        final char decimalSeparatorChar;
        final String decimalSeparator;
        String fixedString;
        final BigDecimal number;

        symbols = new DecimalFormatSymbols(Locale.getDefault());
        groupSeparatorChar = symbols.getGroupingSeparator();
        decimalSeparatorChar = symbols.getDecimalSeparator();
        groupSeparator = groupSeparatorChar == '.' ? "\\" + groupSeparatorChar : Character.toString(groupSeparatorChar);

        decimalSeparator = decimalSeparatorChar == '.' ? "\\" + decimalSeparatorChar : Character.toString(decimalSeparatorChar);

        fixedString = str.replaceAll(groupSeparator, "");
        fixedString = fixedString.replaceAll(decimalSeparator, ".");
        number = new BigDecimal(fixedString);

        return (number);
    }
}
