package org.nqlinq.ant;

import org.apache.tools.ant.Task;
import org.nqlinq.constants.DbStrings;
import org.nqlinq.helpers.WordHelper;

import java.io.*;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "ALL" })
public class NQLINQJndiTask extends Task {
    private String SqlFile;
    private String Name;
    private String Source;
    private String Url;
    private String Package;
    private String Src;
    private String Sequence;
    private Holder MainHolder;
    private String DBMS;
    private String ctxFactoryName;

    public String getSequence() {
        return Sequence;
    }

    public void setSequence(String sequence) {
        Sequence = sequence;
    }

    public String getSrc() {
        return Src;
    }

    public void setSrc(String src) {
        Src = src;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String pkg) {
        Package = pkg;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSqlFile() {
        return SqlFile;
    }

    public void setSqlFile(String sqlFile) {
        SqlFile = sqlFile;
    }

    public String getDBMS() {
        return DBMS;
    }

    public void setDBMS(String dbms) {
        DBMS = dbms;
    }

    public String getCtxFactoryName() {
        return ctxFactoryName;
    }

    public void setCtxFactoryName(String CtxFactoryName) {
        ctxFactoryName = CtxFactoryName;
    }

    public void execute() {
        String unitOfWork = MessageFormat.format("{0}UnitOfWork", getName());
        System.out.println(MessageFormat.format("Processing file: {0}...", getSqlFile()));
        System.out.println(MessageFormat.format("Unit of Work: {0}", unitOfWork));
        System.out.println(MessageFormat.format("Source: {0}", getSource()));
        System.out.println(MessageFormat.format("DBMS: {0}", getDBMS()));
        DbStrings.init(getDBMS());

        MainHolder = new Holder(unitOfWork, "", getUrl(), "", "", getSrc(), getPackage(), getSequence(), getSource(), getCtxFactoryName(), getDBMS());


        try {
            String file = readFileAsString(getSqlFile());
            Pattern tableRegex = Pattern.compile("CREATE TABLE (\\w+) \\(([^;]+)\\);", Pattern.DOTALL | Pattern.MULTILINE);

            Matcher m = tableRegex.matcher(file);

            while (m.find()) {
                String table = m.group(1);
                String[] contents = m.group(2).replace("Id INT NOT NULL PRIMARY KEY, ", "").replace("ON DELETE CASCADE", "").split(", (?!\\d+)");

                MainHolder.add(table, contents);

                System.out.println(MessageFormat.format("Table: {0}", table));
                String singular = WordHelper.singularize(table);
                String plural = WordHelper.pluralize(table);

                for (int i = 0; i < contents.length; i++) {
                    contents[i] = contents[i].trim();

                    if (!contents[i].toUpperCase().startsWith("FOREIGN "))
                        MainHolder.addField(table, new DbField(contents[i]));
                    else {
                        KeyField field = new KeyField(contents[i]);

                        MainHolder.addKeyField(table, field);

                        MainHolder.addExternalKeyField(field.getTable(), plural);
                    }
                }

                MainHolder.addField(table, new DbField("Id INT NOT NULL"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MainHolder.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;

        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return fileData.toString();
    }

}
