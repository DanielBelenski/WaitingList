package com.example.waitinglist.view;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.waitinglist.R;
import com.example.waitinglist.database.DatabaseHelper;
import com.example.waitinglist.database.model.Student;
import com.example.waitinglist.utils.ItemDecorator;
import com.example.waitinglist.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StudentAdapter mAdapter;
    private List<Student> studentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noStudentsView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noStudentsView = findViewById(R.id.no_students_view);

        db = new DatabaseHelper(this);

        studentsList.addAll(db.getAllStudents());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStudentDialog(false, null, -1);
            }
        });

        mAdapter = new StudentAdapter(this, studentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemDecorator(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        for (int i = 0; i < studentsList.size(); i++){
            updateStudent(studentsList.get(i).getStudent(),studentsList.get(i).getCourse(),studentsList.get(i).getPriority(),i);
        }

        toggleEmptyStudents();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    public void createStudent(String name, String course, int priority){
        long id = db.insertStudent(name, course, priority);

        Student student = db.getStudent(id);

        if (student != null){
            studentsList.clear();

            studentsList.addAll(db.getAllStudents());

            mAdapter.notifyDataSetChanged();

            toggleEmptyStudents();
        }
    }

    public void updateStudent(String name, String course, int priority, int position){
        Student student = studentsList.get(position);

        student.setStudent(name);
        student.setCourse(course);
        student.setPriority(priority);

        db.updateStudent(student);

        studentsList.set(position,student);
        mAdapter.notifyItemChanged(position);

        toggleEmptyStudents();
    }

    public void deleteStudent(int position){
        db.deleteStudent(studentsList.get(position));

        studentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyStudents();
    }

    public void showActionsDialog(final int position){
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    showStudentDialog(true, studentsList.get(position), position);
                }
                else{
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }

    private void showStudentDialog(final boolean shouldUpdate, final Student student, final int position){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.stident_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputName = view.findViewById(R.id.studentInfo);
        final EditText inputCourse = view.findViewById(R.id.classInfo);
        final EditText inputPriority = view.findViewById(R.id.priorityEdit);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && student != null){
            inputName.setText(student.getStudent());
            inputCourse.setText(student.getCourse());
            inputPriority.setText(String.valueOf(student.getPriority()));
        }
        alertDialogBuilderUserInput.setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputName.getText().length() <= 0){
                    Toast.makeText(MainActivity.this, "Enter the student's name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (inputCourse.getText().length() <= 0){
                    Toast.makeText(MainActivity.this, "Enter the course information", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (inputPriority.getText().length() <= 0 || Integer.parseInt(inputPriority.getText().toString()) < 1 || Integer.parseInt(inputPriority.getText().toString()) > 5){
                    Toast.makeText(MainActivity.this, "Enter a valid priority number:\n" +
                            "1 = Freshman\n" +
                            "2 = Sophomore\n" +
                            "3 = Junior\n" +
                            "4 = Senior\n" +
                            "5 = Graduate", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    alertDialog.dismiss();
                }

                if (shouldUpdate && student != null){
                    updateStudent(inputName.getText().toString(), inputCourse.getText().toString(), Integer.parseInt(inputPriority.getText().toString()), position);
                } else {
                    createStudent(inputName.getText().toString(), inputCourse.getText().toString(), Integer.parseInt(inputPriority.getText().toString()));
                }

            }
        });
    }

    public void toggleEmptyStudents(){
        if (db.getStudentCount() > 0){
            noStudentsView.setVisibility(View.GONE);
        }
        else{
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }
}
