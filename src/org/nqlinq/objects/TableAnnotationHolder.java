package org.nqlinq.objects;

import org.nqlinq.annotations.Table;
import org.nqlinq.annotations.TableObject;

public class TableAnnotationHolder {
    public Table table;
    public TableObject tableObject;

    public Table getTable() {
        return table;
    }

    public TableAnnotationHolder setTable(Table table) {
        this.table = table;

        return this;
    }

    public TableObject getTableObject() {
        return tableObject;
    }

    public TableAnnotationHolder setTableObject(TableObject tableObject) {
        this.tableObject = tableObject;

        return this;
    }
}
