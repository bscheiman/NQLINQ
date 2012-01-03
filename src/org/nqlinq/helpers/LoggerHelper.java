package org.nqlinq.helpers;

import org.apache.log4j.Logger;

public class LoggerHelper extends SecurityManager {
    public static org.apache.log4j.Logger getLogger() {
        StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
        return Logger.getLogger(myCaller.getClassName());
    }
}
