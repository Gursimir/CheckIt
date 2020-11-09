package com.example.todolist.Database;

import android.provider.BaseColumns;

//This class defines constants which used to access the data in the database.
//we also need a helper class called TaskDbHelper to open the database.
public class TaskContract {
    public static final String DB_NAME = "com.example.todolist.Database";
    public static final int DB_VERSION = 1;

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
    }
}