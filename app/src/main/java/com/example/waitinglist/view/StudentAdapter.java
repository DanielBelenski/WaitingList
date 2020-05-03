package com.example.waitinglist.view;

//import
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

//class to help manage the list of student objects as well as be a mediary to main activity and
// database helper
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder>{

    //creating list and context reference objects
    private Context context;
    private List<Student> studentsList;

    //holder class for each entry in a row of the main activity
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView course;
        public TextView name;
        public TextView priority;

        //set views to their specific ids
        public MyViewHolder(View view) {
            super(view);
            course = view.findViewById(R.id.courseInfo);
            name = view.findViewById(R.id.studentName);
            priority = view.findViewById(R.id.priorityText);
        }
    }

    //constructor
    public StudentAdapter(Context context, List<Student> studentsList){
        this.context = context;
        this.studentsList = studentsList;
    }

    //used when an entry is created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    //set value of views in the holder
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Student student = studentsList.get(position);

        holder.name.setText(student.getStudent());
        holder.course.setText(student.getCourse());
        holder.priority.setText(String.valueOf(student.getPriority()));
    }

    //get the size of the list of student arrays
    @Override
    public int getItemCount(){
        return studentsList.size();
    }

}
