package org.nqlinq.helpers;

import org.nqlinq.core.Entity;
import org.nqlinq.core.Queryable;
import sun.misc.BASE64Encoder;
import sun.text.Normalizer;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    final static Locale locale = new Locale("en", "US");
    final static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
    final static NumberFormat percentFormatter = NumberFormat.getPercentInstance(locale);
    final static SimpleDateFormat dateFormatter = new  SimpleDateFormat("dd/MM/yyyy");
    final static SimpleDateFormat dateTimeFormatter = new  SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private StringHelper() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    public static String join(Iterable<?> elements) {
        StringBuilder builder = new StringBuilder();

        if (elements != null) {
            Iterator<?> iter = elements.iterator();
            if (iter.hasNext()) {
                Object next = iter.next();
                if (next instanceof Entity)
                    builder.append(String.valueOf(((Entity)next).getId()));
                else
                    builder.append(String.valueOf(next));

                while (iter.hasNext()) {
                    next = iter.next();
                    if (next instanceof Entity)
                        builder.append(", ").append(String.valueOf(((Entity)next).getId()));
                    else
                        builder.append(", ").append(String.valueOf(next));
                }    
            }
        }

        return builder.toString();
    }

    public static String[] splitByLength(String str) {
        int origLen = str.length();

        int splitNum = origLen / 4000;

        if (origLen % 4000 > 0)
            splitNum += 1;

        String[] splits = new String[splitNum];

        for (int i = 0; i < splitNum; i++) {
            int startPos = i * 4000;
            int endPos = startPos + 4000;

            if (endPos > origLen)
                endPos = origLen;

            String substr = str.substring(startPos, endPos);

            splits[i] = substr;
        }

        return splits;
    }

    public static String dateTimeString(Date date) {
        return dateTimeFormatter.format(date);
    }

    public static String dateTimeString(long dateTime) {
        return dateTimeFormatter.format(DateHelper.toInternalDateTime(dateTime));
    }

    public static String dateString(Date dateTime) {
        return dateFormatter.format(dateTime);
    }

    public static String dateString(long dateTime) {
        return dateFormatter.format(DateHelper.toInternalDateTime(dateTime));
    }

    public static String toCurrencyString(Number number){
        return currencyFormatter.format(number);
    }

    public static String toPercentString(Number number){
        return percentFormatter.format(number);
    }

    public static String removeAccents(String text) {
        return Normalizer.decompose(text, false, 0)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String toTitle(String str){
        Pattern p = Pattern.compile("(^|\\W)([a-z])");
        Matcher m = p.matcher(str.toLowerCase());
        StringBuffer sb = new StringBuffer(str.length());
        while(m.find()) {
            m.appendReplacement(sb, m.group(1) + m.group(2).toUpperCase() );
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String fixRoman(String str) {
        Matcher m = Pattern.compile("\\b([XxIiVv])+\\b").matcher(str);

        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (m.find()) {
            sb.append(str.substring(last, m.start()));
            sb.append(m.group(0).toUpperCase());
            last = m.end();
        }
        sb.append(str.substring(last));
        return sb.toString();
    }

    public static String fixName(String s){
        return fixRoman(toTitle(removeAccents(s))).trim();
    }

    public static String getSecureHash(String text) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(text);
            sb.append(reverse(text));
            text = sb.toString();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            text = new BASE64Encoder().encode(md.digest());
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String reverse(String s) {
        String temp = "";
        for (int i = s.length() - 1; i >= 0; i--)
            temp += s.charAt(i);
        return temp;
    }
}
