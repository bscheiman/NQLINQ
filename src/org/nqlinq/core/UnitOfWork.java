package org.nqlinq.core;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import org.apache.log4j.Logger;
import org.nqlinq.annotations.*;
import org.nqlinq.constants.DbStrings;
import org.nqlinq.helpers.*;
import snaq.db.ConnectionPool;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({ "UnusedDeclaration" })
public class UnitOfWork {
    protected static final Logger logger = LoggerHelper.getLogger();
    protected static CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<Entity>();
    protected static CopyOnWriteArrayList<Entity> removedEntities = new CopyOnWriteArrayList<Entity>();
    protected NQLINQConnection Conn = new NQLINQConnection();
    protected static ConnectionPool Pool = null;
    protected boolean logQueries = true;
    protected boolean isOpen = false;
    Context ctx = null;
    Configuration config;
    CacheManager cacheManager;
    String dbms;

    @SuppressWarnings("unchecked")
    public UnitOfWork() {

        Hashtable ht = new Hashtable();
        config = new Configuration();
        config.setName("Base");
        config.setMonitoring("autodetect");
        cacheManager = CacheManager.create(config);
        try {
            JndiConnection jndi = this.getClass().getAnnotation(JndiConnection.class);
            JdbcConnection jdbc = this.getClass().getAnnotation(JdbcConnection.class);
            ContextFactoryName ctxName = this.getClass().getAnnotation(ContextFactoryName.class);
            DBMS dbmsName = this.getClass().getAnnotation(DBMS.class);

            if (jdbc == null && jndi == null)
                throw new Exception("No JNDI nor JDBC annotation found");
            
            dbms = dbmsName == null || StringHelper.isNullOrEmpty(dbmsName.name()) ||
                    dbmsName.name().equals("null") ? "" : dbmsName.name();
            DbStrings.init(dbms);

            if(StringHelper.isNullOrEmpty(jndi.url())){
                assert jdbc != null;
                Class c = Class.forName(jdbc.driver());
                Driver driver = (Driver) c.newInstance();
                DriverManager.registerDriver(driver);

                if (Pool == null) {
                    Pool = new ConnectionPool("NQLINQ", 1, 1, 30, 3600, jdbc.url(), jdbc.user(), jdbc.password());
                    Pool.registerShutdownHook();
                    Pool.setCaching(false);
                }

                NQLINQConnection.setConnection(Pool.getConnection());
            }
            else {
                if (ctxName == null)
                    ht.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
                else
                    ht.put(Context.INITIAL_CONTEXT_FACTORY, ctxName.FQDN());
                ht.put(Context.PROVIDER_URL, jndi.url());
                ctx = new InitialContext(ht);
                DataSource ds = (DataSource) ctx.lookup(jndi.source());
                NQLINQConnection.setConnection(ds.getConnection());
            }
        } catch (Exception ex) {
            logger.fatal("Stacktrace:", ex);
        }
        setLogger(false);
    }

    @SuppressWarnings("unchecked")
    public void open() {
        if (!isOpen) {
            Conn.open();
            isOpen = true;
        }
    }

    public void releasePool() {
        if(Pool != null)
            Pool.releaseForcibly();
    }

    public void close() {
        try {
            if (isOpen) {
                Conn.close();
                isOpen = false;
            }
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
        setLogger(log);
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
        setLogger(false);
    }

    public void discardChanges() {
        entities.clear();
        removedEntities.clear();
    }

    public String ExecuteInsert(String sql, String sequence, Object[] objects) {
        open();
        String retVal = "-1";

        try {
            PreparedStatement stmt = Conn.prepareStatement(sql);

            for (int i = 0; i < objects.length; i++) {
                if(logQueries)
                    System.out.println(" - " + objects[i]);
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
            if (StringHelper.isNullOrEmpty(dbms) || dbms.equals("Oracle")) {
                Statement stmt = Conn.createStatement();
                ResultSet rs = stmt.executeQuery(MessageFormat.format(DbStrings.IdentityCurrVal, sequence));
                rs.next();
                retVal = rs.getString(1);
    
                rs.close();
                stmt.close(); 
            } else if(dbms.equals("Postgres")){
                Statement stmt = Conn.createStatement();
                ResultSet rs = stmt.executeQuery(MessageFormat.format(DbStrings.IdentityCurrVal, sequence));
                rs.next();
                retVal = rs.getString(1);

                rs.close();
                stmt.close();
            }
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

            if(logQueries)
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
    
    public void setLogger(boolean log){
        logQueries = log;
    }
    
    public String getDbms(){
        return dbms;
    }
}
