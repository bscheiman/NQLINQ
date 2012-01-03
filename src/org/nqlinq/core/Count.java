package org.nqlinq.core;

import com.asn1c.core.Int32;
import org.nqlinq.commands.CountCommand;

import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings({ "UnusedDeclaration" })
public class Count {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Count(UnitOfWork uow, CountCommand cnt) {
        uow.open();

        try {
            String sql = cnt.getSql();
            uow.logger.debug(sql);

            Statement stmt = uow.Conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            setValue(Int32.parseInt(rs.getString(1)));

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            uow.logger.fatal("Stacktrace:", ex);
        }

        uow.open();
    }
}
