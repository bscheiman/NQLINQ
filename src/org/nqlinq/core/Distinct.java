package org.nqlinq.core;

import org.nqlinq.commands.SelectDistinctCommand;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "UnusedDeclaration" })
public class Distinct{
    //TODO: This is still a work in progress, feel free to try and implement it, it's not as easy as it sounds though
    private List<String> value;

    public List<String> getValue() {
        return value;
    }

    public Distinct(UnitOfWork uow, SelectDistinctCommand dis) {
        uow.open();

        try {
            value = new ArrayList<String>();
            String sql = dis.getSql();
            uow.logger.debug(sql);
            Statement stmt = uow.Conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                value.add(rs.getString(1));
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            uow.logger.fatal("Stacktrace:", ex);
        }

        uow.open();
    }
}
