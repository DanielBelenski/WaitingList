package com.example.waitinglist.view;

//imports
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

//main activity
public class MainActivity extends AppCompatActivity {

    // create objects that will be needed
    private StudentAdapter mAdapter;
    private List<Student> studentsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noStudentsView;

    //create database object
    private DatabaseHelper db;

    //when app is opened/created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //elements in the activity that change
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noStudentsView = findViewById(R.id.no_students_view);

        //create/access database
        db = new DatabaseHelper(this);

        //get all students sorted by priority
        studentsList.addAll(db.getAllStudents());

        //floating action button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open dialog to add a student
                showStudentDialog(false, null, -1);
            }
        });

        //making the recycler view functional
        mAdapter = new StudentAdapter(this, studentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.addItemDecoration(new ItemDecorator(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        //change visibility of no student view so the screen is never empty
        toggleEmptyStudents();

        //listener to interact with student objects
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            //no action
            @Override
            public void onClick(View view, int position) {

            }

            //open a dialog to choose whether to edit entry or delete it
            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    //method to insert an entry into the database, check if insertion was successful
    //then refreshes the list of entries, organized by priority
    public void createStudent(String name, String course, int priority){
        //insertion
        long id = db.insertStudent(name, course, priority);

        //checking if insertion was successful
        Student student = db.getStudent(id);
        //if successful
        if (student != null){
            //empty list of students
            studentsList.clear();

            //get all students from updated database, sorted by priority
            studentsList.addAll(db.getAllStudents());

            //update screen
            mAdapter.notifyDataSetChanged();

            //check if list is empty
            toggleEmptyStudents();
        }
    }

    //update a student entry
    public void updateStudent(String name, String course, int priority, int position){
        //get specific entry
        Student student = studentsList.get(position);

        //set new information in the student object
        student.setStudent(name);
        student.setCourse(course);
        student.setPriority(priority);

        //update the information in the database
        db.updateStudent(student);

        //set the students new position in the list
        studentsList.set(position,student);
        mAdapter.notifyItemChanged(position);

        //check if list is empty
        toggleEmptyStudents();
    }

    //remove entry from list and database
    public void deleteStudent(int position){
        //remove entry from database
        db.deleteStudent(studentsList.get(position));

        //remove student from list
        studentsList.remove(position);
        //update screen
        mAdapter.notifyItemRemoved(position);

        //check if list is empty
        toggleEmptyStudents();
    }

    //show a dialog for decision to update or delete an entry
    public void showActionsDialog(final int position){
        //options
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        //build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Choose an option");
        //put options in dialog
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //edit option
                if (which == 0){
                    showStudentDialog(true, studentsList.get(position), position);
                }
                //delete option
                else{
                    deleteStudent(position);
                }
            }
        });
        //show dialog
        builder.show();
    }

    //show dialog for student creation and editing
    private void showStudentDialog(final boolean shouldUpdate, final Student student, final int position){
        //inflator for dialog
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.stident_dialog, null);

        //building dialog
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        //views in dialog
        final EditText inputName = view.findViewById(R.id.studentInfo);
        final EditText inputCourse = view.findViewById(R.id.classInfo);
        final EditText inputPriority = view.findViewById(R.id.priorityEdit);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        //if updating and student exists
        if (shouldUpdate && student != null){
            inputName.setText(student.getStudent());
            inputCourse.setText(student.getCourse());
            inputPriority.setText(String.valueOf(student.getPriority()));
        }
        //not cancellable
        alertDialogBuilderUserInput.setCancelable(false)
                //update/save button
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {

                    }
                })
                //cancel button
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {
                        dialogBox.cancel();
                    }
                });

        //create and show dialog
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        //get update/save button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if inputs are valid
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
                //close dialog if all inputs are acceptable
                else {
                    alertDialog.dismiss();
                }

                //if updating, the update entry
                if (shouldUpdate && student != null){
                    updateStudent(inputName.getText().toString(), inputCourse.getText().toString(), Integer.parseInt(inputPriority.getText().toString()), position);
                }
                //if creating, save information in a new entry
                else {
                    createStudent(inputName.getText().toString(), inputCourse.getText().toString(), Integer.parseInt(inputPriority.getText().toString()));
                }

            }
        });
    }

    //method to decide whether the no students found message appears or not
    public void toggleEmptyStudents(){
        if (db.getStudentCount() > 0){
            noStudentsView.setVisibility(View.GONE);
        }
        else{
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }
}
