package org.nqlinq.helpers;

import java.util.Iterator;

public class StringHelper {
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
                builder.append(String.valueOf(iter.next()));

                while (iter.hasNext())
                    builder.append(", ").append(String.valueOf(iter.next()));
            }
        }

        return builder.toString();
    }
}
