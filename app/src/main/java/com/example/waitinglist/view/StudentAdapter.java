package com.example.waitinglist.view;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.example.waitinglist.R;
import com.example.waitinglist.database.model.Student;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder>{

    private Context context;
    private List<Student> studentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView course;
        public TextView name;
        public TextView priority;

        public MyViewHolder(View view) {
            super(view);
            course = view.findViewById(R.id.courseInfo);
            name = view.findViewById(R.id.studentName);
            priority = view.findViewById(R.id.priorityText);
        }
    }

    public StudentAdapter(Context context, List<Student> studentsList){
        this.context = context;
        this.studentsList = studentsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Student student = studentsList.get(position);

        holder.name.setText(student.getStudent());
        holder.course.setText(student.getCourse());
        holder.priority.setText(String.valueOf(student.getPriority()));
    }

    @Override
    public int getItemCount(){
        return studentsList.size();
    }

}
