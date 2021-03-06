package org.nqlinq.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
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
    Cache cache;

    @SuppressWarnings("unchecked")
    public Single(UnitOfWork uow, String table, String obj, long id) throws InvalidOperationException {
        boolean found = false;

        try {
            if (!ReflectionCache.containsKey(obj))
                ReflectionCache.put(obj, Class.forName(obj));

            if (!uow.cacheManager.cacheExists(obj))
                uow.cacheManager.addCache(new Cache(obj, 10000, false, false, 600, 600));
            cache = uow.cacheManager.getCache(obj);
            if (cache.get(id) != null) {
                object = (T)cache.get(id).getObjectValue();
                found = true;
                return;
            }
            uow.open();

            Statement stmt = uow.Conn.createStatement();

            String sql = new SelectCommand(table, new Where(MessageFormat.format("id = {0}", Long.toString(id)))).getSql();
            if(uow.logQueries)
                UnitOfWork.logger.debug(sql);
            ResultSet rs = stmt.executeQuery(sql);

            try {
                while (rs.next()) {
                    T cls = (T) ReflectionCache.get(obj).newInstance();

                    cls.Parse(uow, rs);

                    object = cls;

                    cache.put(new Element(cls.getId(), cls));

                    found = true;
                    break;
                }

            } catch (SQLException ignored) {

            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }

            stmt.close();
        } catch (Exception ex) {
            UnitOfWork.logger.fatal("Stacktrace:", ex);
        }

        uow.close();

        if (!found)
            throw new InvalidOperationException();
    }

    @SuppressWarnings("unchecked")
    public Single(UnitOfWork uow, String table, String obj, int id) throws InvalidOperationException {
        boolean found = false;

        try {
            if (!ReflectionCache.containsKey(obj))
                ReflectionCache.put(obj, Class.forName(obj));

            if (!uow.cacheManager.cacheExists(obj))
                uow.cacheManager.addCache(new Cache(obj, 10000, false, false, 600, 600));
            cache = uow.cacheManager.getCache(obj);
            if (cache.get(id) != null) {
                object = (T)cache.get(id).getObjectValue();
                found = true;
                return;
            }
            uow.open();

            Statement stmt = uow.Conn.createStatement();

            String sql = new SelectCommand(table, new Where(MessageFormat.format("id = {0}", Long.toString(id)))).getSql();
            if(uow.logQueries)
                UnitOfWork.logger.debug(sql);
            ResultSet rs = stmt.executeQuery(sql);

            try {
                while (rs.next()) {
                    T cls = (T) ReflectionCache.get(obj).newInstance();

                    cls.Parse(uow, rs);

                    object = cls;

                    cache.put(new Element(cls.getId(), cls));

                    found = true;
                    break;
                }

            } catch (SQLException ignored) {

            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }

            stmt.close();
        } catch (Exception ex) {
            UnitOfWork.logger.fatal("Stacktrace:", ex);
        }

        uow.close();

        if (!found)
            throw new InvalidOperationException();
    }

    public T getObject() {
        return object;
    }
}
