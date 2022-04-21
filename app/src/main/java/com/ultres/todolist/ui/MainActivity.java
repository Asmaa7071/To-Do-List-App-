package com.ultres.todolist.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.ultres.todolist.R;
import com.ultres.todolist.adapter.TasksAdapter;
import com.ultres.todolist.dataBase.TasksDatabase;
import com.ultres.todolist.interfaces.ItemClickListener;
import com.ultres.todolist.model.Task;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ItemClickListener {

    FloatingActionButton addFloatingBtn;
    ViewGroup bottomSheetContainer;
    EditText titleET,timeET,dateET,bodyET;
    Button bottomSheetBtn;
    View bottomSheetView;
    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView recyclerViewTasks;
    List<Task>myTasks;
    int doneImageID ;
    int archiveImageID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateViews();
        myTasks = TasksDatabase.instance.tasksDao().getActiveTasks();
        showTasksData(myTasks,doneImageID,archiveImageID);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                if (item.getItemId() == R.id.item_active_tasks) {
                    List<Task> tasks = TasksDatabase.instance.tasksDao().getActiveTasks();
                    doneImageID = R.drawable.ic_baseline_done_all_24;
                    archiveImageID = R.drawable.ic_baseline_archive_24;
                    showTasksData(tasks,doneImageID,archiveImageID);

                } else if (item.getItemId() == R.id.item_done_tasks) {
                    List<Task> tasks = TasksDatabase.instance.tasksDao().getDoneTasks();
                    doneImageID = R.drawable.done_blue;
                    archiveImageID = R.drawable.ic_baseline_archive_24;
                    showTasksData(tasks,doneImageID,archiveImageID);

                } else if (item.getItemId() == R.id.item_archive_tasks) {
                    List<Task> tasks = TasksDatabase.instance.tasksDao().getArchiveTasks();
                    doneImageID = R.drawable.ic_baseline_done_all_24;
                    archiveImageID = R.drawable.archive_blue;
                    showTasksData(tasks,doneImageID,archiveImageID);
                }

                return false;
            }
        });

    }

    private void initiateViews() {
        addFloatingBtn = findViewById(R.id.addFloatingBtn);
        bottomNavigationView = findViewById(R.id.database_bottom_navigation);
        addFloatingBtn.setOnClickListener(this);
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer);
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        recyclerViewTasks = findViewById(R.id.rv_tasks);
        TasksDatabase.getInstance(getApplicationContext());
    }

    //FloatingAction Button Click action
    @Override
    public void onClick(View v) {
        openBottomSheet();
        insertTask();
    }

    //open bottom sheet to insert a new task or edit an existing task
    private void openBottomSheet() {
        bottomSheetView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.bottom_sheet, bottomSheetContainer);

        titleET = bottomSheetView.findViewById(R.id.titleET);
        timeET = bottomSheetView.findViewById(R.id.timeET);
        dateET = bottomSheetView.findViewById(R.id.dateET);
        bodyET = bottomSheetView.findViewById(R.id.bodyET);
        bottomSheetBtn = bottomSheetView.findViewById(R.id.bottomSheetBtn);
    }

    //insert a new task in recycler view
    private void insertTask(){
        bottomSheetBtn.setText("INSERT TASK");
        setFocusTrue();
        bottomSheetBtn.setOnClickListener(v -> {
            //to get data entered in the bottomSheet
            String title = titleET.getText().toString();
            String time = timeET.getText().toString();
            String date = dateET.getText().toString();
            String body = bodyET.getText().toString();

            //invalidate task data is entered not empty
            if (invalidateDataEntered(title,time,date,body)){
                //insert task in room database
                Task task = new Task(title,time,date,body,"Active");
                TasksDatabase.instance.tasksDao().insertTask(task);
                bottomSheetDialog.dismiss();
                recyclerViewTasks.getAdapter().notifyDataSetChanged();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    //set focus on Edit Text which is selected to show time/date picker dialog
    private void setFocusTrue() {
        //set focus true when time edit text selected
        timeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.timeET && hasFocus) showTimePickerDialog();
            }
        });

        //set focus true when time date text selected
        dateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.dateET && hasFocus) {
                    showDatePickerDialog();
                }
            }
        });
    }

    //ensure the data validation
    private boolean invalidateDataEntered(String title, String time, String date , String body) {
        if (title.isEmpty()) {
            titleET.setError("Title required");
            return false;
        }

        if (date.isEmpty()) {
            dateET.setError("Date required");
            return false;
        }

        if (time.isEmpty()) {
            timeET.setError("Time required");
            return false;
        }

        if (body.isEmpty()){
            bodyET.setError("Body required");
            return false;
        }
        return true;
    }

    //show time picker Dialog when time editText selected
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String realTime = (selectedHour > 12)
                        ? ( (selectedHour - 12) + ":" + selectedMinute + " PM")
                        : (selectedHour + ":" + selectedMinute + " AM");
                timeET.setText(realTime);
            }
        }, hour, minute, true);//Yes 24 hour time
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    //show Date picker Dialog when date editText selected
    private void showDatePickerDialog() {
        final Calendar newCalendar = Calendar.getInstance();

        final DatePickerDialog StartTime = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);


                        dateET.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }

                },
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        StartTime.show();

    }

    //set Adapter to recycler View
    public void showTasksData(List<Task> tasks, int doneImageID,int archiveImageID ){
        TasksAdapter tasksAdapter = new TasksAdapter(tasks,doneImageID,archiveImageID,this);
        tasksAdapter.notifyDataSetChanged();
        recyclerViewTasks.setAdapter(tasksAdapter);
    }

    //update task data on room dataBase
    @Override
    public void EditTask(int position) {
        openBottomSheet();
        bottomSheetBtn.setText("Update TASK");

        titleET.setText(myTasks.get(position).getTitle());
        timeET.setText(myTasks.get(position).getTime());
        dateET.setText(myTasks.get(position).getDate());
        bodyET.setText(myTasks.get(position).getBody());

        setFocusTrue();
        bottomSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedTitle = titleET.getText().toString();
                String updatedTime = timeET.getText().toString();
                String updatedDate = dateET.getText().toString();
                String updatedBody = bodyET.getText().toString();

                //invalidate task data is entered not empty
                if (invalidateDataEntered(updatedTitle,updatedTime,updatedDate,updatedBody))
                {
                    Task updatedTask = myTasks.get(position);
                    Log.e("TAG", "onClick: " + position);
                    Log.e("TAG", "onClick: " + updatedTask.getTitle());
                    Log.e("TAG", "onClick: " + updatedTask.getTime());
                    Log.e("TAG", "onClick: " + updatedTask.getDate());
                    Log.e("TAG", "onClick: " + updatedTask.getBody());

                    updatedTask.setTitle(updatedTitle);
                    updatedTask.setTime(updatedTime);
                    updatedTask.setDate(updatedDate);
                    updatedTask.setBody(updatedBody);
                    TasksDatabase.instance.tasksDao().updateTask(updatedTask);
                    Log.e("TAG", "onClick: "+ updatedTask.getTitle());
                    Log.e("TAG", "onClick: "+ updatedTask.getTime());
                    Log.e("TAG", "onClick: "+ updatedTask.getDate());
                    Log.e("TAG", "onClick: "+ updatedTask.getBody());
                    Objects.requireNonNull(recyclerViewTasks.getAdapter()).notifyItemChanged(position,updatedTask);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}