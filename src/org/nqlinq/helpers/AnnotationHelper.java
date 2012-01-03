package org.nqlinq.helpers;

import org.nqlinq.annotations.Table;
import org.nqlinq.annotations.TableObject;
import org.nqlinq.objects.TableAnnotationHolder;

import java.util.HashMap;

public class AnnotationHelper {
    protected static HashMap<String, Class> ReflectionCache = new HashMap<String, Class>();

    private AnnotationHelper() {}

    public static TableAnnotationHolder GetTableAnnotations(Class cls, String method) {
        return GetTableAnnotations(cls, method, String.class);
    }

    @SuppressWarnings("unchecked")
    public static TableAnnotationHolder GetTableAnnotations(Class cls, String method, Class type) {
        try {
            TableObject tableObj = cls.getMethod(method, type).getAnnotation(TableObject.class);

            if (!ReflectionCache.containsKey(tableObj.name()))
                ReflectionCache.put(tableObj.name(), Class.forName(tableObj.name()));

            Table table = (Table) ReflectionCache.get(tableObj.name()).getAnnotation(Table.class);

            return new TableAnnotationHolder().setTable(table).setTableObject(tableObj);
        } catch (Exception ex) {
            return new TableAnnotationHolder();
        }
    }
}
