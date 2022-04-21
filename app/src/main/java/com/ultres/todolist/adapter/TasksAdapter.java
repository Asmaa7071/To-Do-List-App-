package com.ultres.todolist.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ultres.todolist.R;
import com.ultres.todolist.dataBase.TasksDatabase;
import com.ultres.todolist.interfaces.ItemClickListener;
import com.ultres.todolist.model.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder>{

    List<Task> myTasks;
    ItemClickListener itemClickListener;
    int doneImageId, archiveImageId;
    public TasksAdapter(List<Task> myTasks , int doneImageId, int archiveImageId, ItemClickListener itemClickListener) {
        this.myTasks = myTasks;
        this.doneImageId = doneImageId;
        this.archiveImageId = archiveImageId;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TasksViewHolder holder, int position) {
        Task currentTask = myTasks.get(holder.getAdapterPosition());

        holder.titleTV.setText(myTasks.get(position).getTitle());
        holder.timeTV.setText(myTasks.get(position).getTime());
        holder.dateTV.setText(myTasks.get(position).getDate());
        holder.bodyTV.setText(myTasks.get(position).getBody());
        holder.doneIV.setImageResource(doneImageId);
        holder.archiveIV.setImageResource(archiveImageId);

        holder.doneIV.setOnClickListener(v -> {
            if (currentTask.getStatus().equals("Active") || currentTask.getStatus().equals("Archive")){
                currentTask.setStatus("Done");
            }else if (currentTask.getStatus().equals("Done")){
                currentTask.setStatus("Active");
            }

            TasksDatabase.instance.tasksDao().updateTask(currentTask);
            myTasks.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.archiveIV.setOnClickListener(v -> {
            if (currentTask.getStatus().equals("Active") || currentTask.getStatus().equals("Done")){
                currentTask.setStatus("Archive");
            }else if (currentTask.getStatus().equals("Archive")){
                currentTask.setStatus("Active");
            }
            TasksDatabase.instance.tasksDao().updateTask(currentTask);
            myTasks.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setMessage("Are you sure to delete this task ?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TasksDatabase.instance.tasksDao().deleteTask(currentTask);
                            myTasks.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    })
                    .show();

            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            itemClickListener.EditTask(holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return myTasks.size();
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV,timeTV,dateTV,bodyTV;
        ImageView doneIV, archiveIV;
        public TasksViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.titleTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            bodyTV = itemView.findViewById(R.id.bodyTV);
            doneIV = itemView.findViewById(R.id.doneIV);
            archiveIV = itemView.findViewById(R.id.archiveIV);

        }
    }

}
