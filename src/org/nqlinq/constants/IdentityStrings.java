package org.nqlinq.constants;

public class IdentityStrings {
    public static String oracleIdentityCurrVal = "SELECT {0}.currval FROM DUAL";
    public static String oracleIdentityNextVal = "{0}.nextval";
    public static String postgresIdentityCurrVal = "SELECT currval('{0}')";
    public static String postgresIdentityNextVal = "nextval('{0}')";
}
