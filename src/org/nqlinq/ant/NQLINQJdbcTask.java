package org.nqlinq.ant;

import org.apache.tools.ant.Task;
import org.nqlinq.helpers.WordHelper;

import java.io.*;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "ALL" })
public class NQLINQJdbcTask extends Task {
    private String SqlFile;
    private String Name;
    private String Driver;
    private String Url;
    private String User;
    private String Password;
    private String Package;
    private String Src;
    private String Sequence;
    private Holder MainHolder;

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

    public String getDriver() {
        return Driver;
    }

    public void setDriver(String driver) {
        Driver = driver;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public void execute() {
        String unitOfWork = MessageFormat.format("{0}UnitOfWork", getName());
        System.out.println(MessageFormat.format("Processing file: {0}...", getSqlFile()));
        System.out.println(MessageFormat.format("Unit of Work: {0}", unitOfWork));

        MainHolder = new Holder(unitOfWork, getDriver(), getUrl(), getUser(), getPassword(), getSrc(), getPackage(), getSequence(), "", "", "");
        
        try {
            String file = readFileAsString(getSqlFile());
            Pattern tableRegex = Pattern.compile("CREATE TABLE (\\w+) \\(([^;]+)\\);", Pattern.DOTALL | Pattern.MULTILINE);

            Matcher m = tableRegex.matcher(file);

            while (m.find()) {
                String table = m.group(1);
                String[] contents = m.group(2).replace("Id INT NOT NULL PRIMARY KEY, ", "").split(", (?!\\d+)");

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

        MainHolder.execute();
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
