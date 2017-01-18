package com.yonyou.shortcut;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

public class ActionDao {
    private Dao<Action, Integer> mActionDao;
    private DatabaseHelper helper;

    @SuppressWarnings("unchecked")
    public ActionDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            mActionDao = helper.getDao(Action.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个Article
     *
     * @param action
     */
    public void add(Action action) {
        try {
            mActionDao.create(action);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param action
     * @return
     */
    public void remove(Action action) {
        try {
            mActionDao.delete(action);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public List<Action> list() {
        try {
            return mActionDao.queryBuilder().
                    query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
