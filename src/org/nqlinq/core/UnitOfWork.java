package org.nqlinq.core;

import org.apache.log4j.Logger;
import org.nqlinq.annotations.JdbcConnection;
import org.nqlinq.helpers.LoggerHelper;
import snaq.db.ConnectionPool;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;

@SuppressWarnings({ "UnusedDeclaration" })
public class UnitOfWork {
    protected static final Logger logger = LoggerHelper.getLogger();
    protected ArrayList<Entity> entities = new ArrayList<Entity>();
    protected ArrayList<Entity> removedEntities = new ArrayList<Entity>();
    protected NQLINQConnection Conn = new NQLINQConnection();
    protected static ConnectionPool Pool = null;

    @SuppressWarnings("unchecked")
    public UnitOfWork() {
        try {
            JdbcConnection jdbc = this.getClass().getAnnotation(JdbcConnection.class);

            if (jdbc == null)
                throw new Exception("JDBC annotation not found");

            if (jdbc.user().isEmpty() && jdbc.password().isEmpty()) {
                Hashtable properties = new Hashtable();
                properties.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
                properties.put(Context.PROVIDER_URL, "t3://localhost:7001");
                Context ctx = new InitialContext(properties);

                javax.sql.DataSource ds = (javax.sql.DataSource) ctx.lookup(jdbc.url());
                Conn.setConnection(ds.getConnection());
            } else {
                Class c = Class.forName(jdbc.driver());
                Driver driver = (Driver) c.newInstance();
                DriverManager.registerDriver(driver);

                if (Pool == null) {
                    Pool = new ConnectionPool("NQLINQ", 5, 10, 30, 3600, jdbc.url(), jdbc.user(), jdbc.password());
                    Pool.registerShutdownHook();
                }

                Conn.setConnection(Pool.getConnection());
            }
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void open() {
    }

    public void releasePool() {
        Pool.releaseForcibly();
    }

    public void close() {
    }

    public void add(Entity e) {
        entities.add(e);
    }

    public void delete(Entity e) {
        removedEntities.add(e);
    }

    public <T extends Entity> void delete(Queryable<T> queryable) {
        for (T obj : queryable)
            removedEntities.add(obj);
    }

    public void saveChanges() {
        open();

        for (Entity e : entities)
            if (e != null)
                e.save(this);

        for (Entity e : removedEntities)
            if (e != null)
                e.delete(this);

        close();

        entities.clear();
        removedEntities.clear();
    }

    public String ExecuteInsert(String sql, String sequence, Object[] objects) {
        String retVal = "-1";

        try {
            PreparedStatement stmt = Conn.prepareStatement(sql);

            for (int i = 0; i < objects.length; i++)
                stmt.setObject(i + 1, objects[i]);

            logger.debug(sql);

            stmt.execute();

            stmt.close();
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }

        try {
            Statement stmt = Conn.createStatement();
            ResultSet rs = stmt.executeQuery(MessageFormat.format("SELECT {0}.currval FROM DUAL", sequence));
            rs.next();
            retVal = rs.getString(1);

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }

        return retVal;
    }

    public void ExecuteSql(String sql, Object[] objects) {
        try {
            PreparedStatement stmt = Conn.prepareStatement(sql);

            for (int i = 0; i < objects.length; i++)
                stmt.setObject(i + 1, objects[i]);

            logger.debug(sql);
            stmt.execute();

            stmt.close();
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }
    }

    public <T extends Entity> T registerNew(Class<T> cls) {
        try {
            T obj = cls.newInstance();
            entities.add(obj);

            return obj;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
