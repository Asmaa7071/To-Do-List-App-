package com.ultres.todolist.dataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ultres.todolist.model.Task;

import java.util.List;

@Dao
public interface ITasksDao {

    @Query("SELECT * FROM tasks WHERE status = 'Active'")
    List<Task> getActiveTasks();

    @Query("SELECT * FROM tasks WHERE status = 'Done'")
    List<Task> getDoneTasks();

    @Query("SELECT * FROM tasks WHERE status = 'Archive'")
    List<Task> getArchiveTasks();

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);


}
