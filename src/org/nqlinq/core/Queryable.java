package org.nqlinq.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.nqlinq.commands.BaseCommand;
import org.nqlinq.exceptions.InvalidOperationException;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({ "UnusedDeclaration" })
public class Queryable<T extends Entity> implements Iterable<T> {
    protected ArrayList<T> list = new ArrayList<T>();
    private static HashMap<String, Class> ReflectionCache = new HashMap<String, Class>();
    protected String TStr;
    Cache cache;

    @SuppressWarnings("unchecked")
    public Queryable(UnitOfWork uow, String obj, BaseCommand cmd) {
        uow.open();
        TStr = obj;

        try {
            if (!ReflectionCache.containsKey(obj))
                ReflectionCache.put(obj, Class.forName(obj));
            if (!uow.cacheManager.cacheExists(obj))
                uow.cacheManager.addCache(new Cache(obj, 10000, false, false, 600, 600));
            cache = uow.cacheManager.getCache(obj);

            Statement stmt = uow.Conn.createStatement();

            String sql = cmd.getSql();
            if(uow.logQueries)
                uow.logger.debug(sql);
            ResultSet rs = stmt.executeQuery(sql);

            try {
                while (rs.next()) {
                    T cls = (T) ReflectionCache.get(obj).newInstance();

                    cls.Parse(uow, rs);

                    cache.put(new Element(cls.getId(), cls));
                    list.add(cls);
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
    }

    public T Single() throws InvalidOperationException {
        if (list.size() == 1)
            return list.get(0);

        throw new InvalidOperationException();
    }

    @SuppressWarnings("unchecked")
    public T SingleOrDefault() {
        try {
            return Single();
        } catch (InvalidOperationException ex) {
            T cls;

            try {
                cls = (T) ReflectionCache.get(TStr).newInstance();
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }

            return cls;
        }
    }

    public T First() throws InvalidOperationException {
        if (list.size() > 0)
            return list.get(0);

        throw new InvalidOperationException();
    }

    @SuppressWarnings("unchecked")
    public T FirstOrDefault() {
        try {
            return First();
        } catch (InvalidOperationException ex) {
            T cls;

            try {
                cls = (T) ReflectionCache.get(TStr).newInstance();
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }

            return cls;
        }
    }

    public int Count() {
        return list.size();
    }

    public boolean Any() {
        return list.size() > 0;
    }
    
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] array = (T[]) Array.newInstance(ReflectionCache.get(TStr), list.size());
        int count = 0;

        for (T obj : list)
            array[count++] = obj;

        return array;
    }
}
