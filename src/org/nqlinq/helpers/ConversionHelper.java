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
        return StringHelper.isNullOrEmpty(str) ? "" : str;
    }

    public static int ConvertToInt(String str) {
        return Integer.parseInt(StringHelper.isNullOrEmpty(str) ? "0" : str);
    }

    public static Timestamp ConvertToTimestamp(String str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = StringHelper.isNullOrEmpty(str) ? new Timestamp(new Date(0L).getTime()) : formatter.parse(str);

            return new Timestamp(date.getTime());
        } catch (ParseException ex) {
            return new Timestamp(new Date(0L).getTime());
        }
    }

    public static float ConvertToFloat(String str) {
        return Float.parseFloat(StringHelper.isNullOrEmpty(str) ? "0" : str);
    }

    public static boolean ConvertToBoolean(String str) {
        return Boolean.parseBoolean(StringHelper.isNullOrEmpty(str) ? "false" : str);
    }

    public static double ConvertToDouble(String str) {
        return Double.parseDouble(StringHelper.isNullOrEmpty(str) ? "0" : str);
    }

    public static long ConvertToLong(String str) {
        return Long.parseLong(StringHelper.isNullOrEmpty(str) ? "0" : str);
    }

    public static BigDecimal ConvertToBigdecimal(String str) {
        str = StringHelper.isNullOrEmpty(str) ? "0" : str;
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
