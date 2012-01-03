package org.nqlinq.core;

import org.nqlinq.commands.SelectCommand;
import org.nqlinq.constraints.Where;
import org.nqlinq.exceptions.InvalidOperationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;

@SuppressWarnings({ "UnusedDeclaration" })
public class Single<T extends Entity> {
    private T object;
    private static HashMap<String, Class> ReflectionCache = new HashMap<String, Class>();

    @SuppressWarnings("unchecked")
    public Single(UnitOfWork uow, String table, String obj, int id) throws InvalidOperationException {
        uow.open();
        boolean found = false;

        try {
            if (!ReflectionCache.containsKey(obj))
                ReflectionCache.put(obj, Class.forName(obj));

            Statement stmt = uow.Conn.createStatement();

            String sql = new SelectCommand(table, new Where(MessageFormat.format("id = {0}", Long.toString(id)))).getSql();
            uow.logger.debug(sql);
            ResultSet rs = stmt.executeQuery(sql);

            try {
                while (rs.next()) {
                    T cls = (T) ReflectionCache.get(obj).newInstance();

                    cls.Parse(uow, rs);

                    object = cls;

                    found = true;
                    break;
                }

            } catch (SQLException e) {

            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }

            stmt.close();
        } catch (Exception ex) {
            uow.logger.fatal("Stacktrace:", ex);
        }

        uow.close();

        if (!found)
            throw new InvalidOperationException();
    }

    public T getObject() {
        return object;
    }
}
