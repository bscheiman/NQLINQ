package org.nqlinq.constants;

import org.nqlinq.helpers.StringHelper;

public class DbStrings {
    public static String IdentityCurrVal;
    public static String IdentityNextVal;
    public static String SelectVal;
    public static String AppendVal;
    public static String AppendWhereVal;
    public static void init(String Db){
        if(StringHelper.isNullOrEmpty(Db) || Db.equalsIgnoreCase("Oracle")) {
            IdentityCurrVal = "SELECT {0}.currval FROM DUAL";
            IdentityNextVal = "{0}.nextval";
            SelectVal = "SELECT * FROM {0}";
            AppendWhereVal = "new Operation(\"ROWNUM\", \"<=\", count).toString() + \" AND \" + ";
            AppendVal = "";
        } else if(Db.equalsIgnoreCase("Postgres")){
            IdentityCurrVal = "SELECT currval('{0}')";
            IdentityNextVal = "nextval('{0}')";
            SelectVal = "SELECT * FROM {0}";
            AppendWhereVal = "";
            AppendVal = " LIMIT {0}";
        } else if(Db.equalsIgnoreCase("MySQL")) {
            IdentityCurrVal = "";
            IdentityNextVal = "";
            SelectVal = "SELECT * FROM {0}";
            AppendWhereVal = "";
            AppendVal = " LIMIT {0}";
        } else if(Db.equalsIgnoreCase("Sybase")) {
            IdentityCurrVal = "";
            IdentityNextVal = "";
            SelectVal = "SELECT TOP {1} * FROM {0}";
            AppendWhereVal = "";
            AppendVal = "";
        } else if(Db.equalsIgnoreCase("MSSQL")) {
            IdentityCurrVal = "";
            IdentityNextVal = "";
            SelectVal = "SELECT TOP {1} * FROM {0}";
            AppendWhereVal = "";
            AppendVal = "";
        }  else if(Db.equalsIgnoreCase("Firebird")) {
            IdentityCurrVal = "";
            IdentityNextVal = "";
            SelectVal = "SELECT FIRST {1} * FROM {0}";
            AppendWhereVal = "";
            AppendVal = "";
        }
    }
}
