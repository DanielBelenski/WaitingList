package com.example.waitinglist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.waitinglist.database.model.Student;

import java.util.ArrayList;
import java.util.List;

//class for interracting with the database, using elemsnets from the Student model
public class DatabaseHelper extends SQLiteOpenHelper {

    //database version for database creation
    public static final int DATABASE_VERSION = 1;

    //database name for database creation
    public static final String DATABASE_NAME = "students_db";

    //creates database and object to interact with it
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creates student table using static string from student model
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Student.CREATE_TABLE);
    }

    //method to upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);
    }

    //method to insert a student objects information into the database
    public long insertStudent(String name, String course, boolean priority){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Student.COLUMN_NAME, name);
        values.put(Student.COLUMN_COURSE, course);
        if (priority){
            values.put(Student.PRIORITY, 1);
        }
        else{
            values.put(Student.PRIORITY, 0);
        }

        long id = db.insert(Student.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    //retrieve a students info from the database and return a student object with that info
    public Student getStudent(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_NAME, Student.COLUMN_COURSE, Student.PRIORITY},
                Student.COLUMN_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        int tempPriority = cursor.getInt(cursor.getColumnIndex(Student.PRIORITY));
        boolean priority;
        if (tempPriority == 1)
            priority = true;
        else
            priority = false;

        Student student = new Student(
                cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_COURSE)),
                priority);

        cursor.close();

        return student;
    }

    //remove a student from the database
    public void deleteStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, Student.COLUMN_ID + "=" + String.valueOf(student.getId()), new String[]{student.getStudent()});
        db.close();
    }

    //method to get a list of all students in the database, listed by priority
    public List<Student> getAllStudents(){
        List<Student> students = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Student.TABLE_NAME + " ORDER BY " + Student.PRIORITY + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do{
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)));
                student.setStudent(cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)));
                student.setCourse(cursor.getString(cursor.getColumnIndex(Student.COLUMN_COURSE)));
                student.setPriority(cursor.getInt(cursor.getColumnIndex(Student.PRIORITY)) == 1);
            } while(cursor.moveToNext());
        }

        db.close();

        return students;
    }

    //get total number of student records
    public int getStudentCount(){
        String countQuery = "SELECT * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //method to update a students information in the database
    public int updateStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_NAME, student.getStudent());
        values.put(Student.COLUMN_COURSE, student.getCourse());
        boolean tempPriority = student.getPriority();
        int priority;
        if (tempPriority)
            priority = 1;
        else
            priority = 0;
        values.put(Student.PRIORITY, priority);

        return db.update(Student.TABLE_NAME, values, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
    }


}
