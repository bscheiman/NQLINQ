package org.nqlinq.core;

import org.apache.log4j.Logger;
import org.nqlinq.annotations.*;
import org.nqlinq.helpers.*;
import snaq.db.ConnectionPool;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

@SuppressWarnings({ "UnusedDeclaration" })
public class UnitOfWork {
    protected static final Logger logger = LoggerHelper.getLogger();
    protected static Vector<Entity> entities = new Vector<Entity>();
    protected static Vector<Entity> removedEntities = new Vector<Entity>();
    protected NQLINQConnection Conn = new NQLINQConnection();
    protected static ConnectionPool Pool = null;
    protected boolean logQueries = true;
    protected boolean isOpen = false;
    Context ctx = null;

    private static DataSource ds = null;

    @SuppressWarnings("unchecked")
    public UnitOfWork() {

        Hashtable ht = new Hashtable();
        try {
            JndiConnection jndi = this.getClass().getAnnotation(JndiConnection.class);
            JdbcConnection jdbc = this.getClass().getAnnotation(JdbcConnection.class);

            if (jdbc == null && jndi == null)
                throw new Exception("No JNDI nor JDBC annotation found");

            if(StringHelper.isNullOrEmpty(jndi.url())){
                Class c = Class.forName(jdbc.driver());
                Driver driver = (Driver) c.newInstance();
                DriverManager.registerDriver(driver);

                if (Pool == null) {
                    Pool = new ConnectionPool("NQLINQ", 1, 1, 30, 3600, jdbc.url(), jdbc.user(), jdbc.password());
                    Pool.registerShutdownHook();
                    Pool.setCaching(false);
                }

                Conn.setConnection(Pool.getConnection());
            }
            else {
                ht.put(Context.INITIAL_CONTEXT_FACTORY,
                        "weblogic.jndi.WLInitialContextFactory");
                ht.put(Context.PROVIDER_URL,
                        jndi.url());
                ctx = new InitialContext(ht);
                ds = (DataSource) ctx.lookup(jndi.source());
                Conn.setConnection(ds.getConnection());
            }
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void open() {
        if (!isOpen) {
            System.out.println("Opening Connection");
            Conn.open();
            isOpen = true;
        } else
            System.out.println("Connection already opened, using that");
    }

    public void releasePool() {
        if(Pool != null)
            Pool.releaseForcibly();
    }

    public void close() {
        try {
            if (isOpen) {
                System.out.println("Closing Connection");
                Conn.close();
                isOpen = false;
            } else
                System.out.println("Connection already closed");
        }
        catch (Exception e) {
            // a failure occurred
        }
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

    public void saveChanges(boolean log) {
        if (!log)
            logQueries = false;
        saveChanges();
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
        open();
        String retVal = "-1";

        try {
            PreparedStatement stmt = Conn.prepareStatement(sql);

            for (int i = 0; i < objects.length; i++) {
                //System.out.println(" - " + objects[i]);
                stmt.setObject(i + 1, objects[i]);
            }

            if(logQueries)
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
        close();
        return retVal;
    }

    public void ExecuteSql(String sql, Object[] objects) {
        try {
            open();
            PreparedStatement stmt = Conn.prepareStatement(sql);
            for (int i = 0; i < objects.length; i++)
                stmt.setObject(i + 1, objects[i]);

            logger.debug(sql);
            stmt.execute();

            stmt.close();
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        } finally {
            close();
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
