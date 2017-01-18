package com.yonyou.shortcut;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Chen on 2017/1/6.
 */


@DatabaseTable(tableName = "Action")
public class Action implements Cloneable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField()
    private String name;
    @DatabaseField()
    private String action;
    @DatabaseField()
    private String protocol;
    @DatabaseField()
    private String value;
    @DatabaseField()
    private int iconResId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
