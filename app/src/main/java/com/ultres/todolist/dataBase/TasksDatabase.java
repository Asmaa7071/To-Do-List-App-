package com.ultres.todolist.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ultres.todolist.model.Task;

@Database(entities = {Task.class},version = 1)
public abstract class TasksDatabase extends RoomDatabase {

    public abstract ITasksDao tasksDao();

    public static TasksDatabase instance;

    public static void getInstance(Context context) {
        if (instance == null)
            instance = Room
                    .databaseBuilder(context, TasksDatabase.class, "TasksDatabase")
                    .allowMainThreadQueries()
                    .build();
    }

}
