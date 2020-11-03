package com.example.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todolist.Database.TaskContract;
import com.example.todolist.Database.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private ActionBar actionBar;
    //TAG constant is created with the name of the class for logging.
    private static final String TAG = "MainActivity";
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    //ArrayAdapter will help populate the ListView with the data.
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#BB86FC"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);

        //initializing mHelper variable
        mHelper = new TaskDbHelper(this);

        //initializing mTaskListView
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        //To see the updated data, you need to call the updateUI() method every time the underlying data of the app changes. So, add it in two places:
        //In the onCreate() method, that initially shows all the data
        //After adding a new task using the AlertDialog
        updateUI();
    }

    //The onCreateOptionsMenu() method inflates (renders) the menu
    // in the main activity, and uses the onOptionsItemSelected()
    // method to react to different user interactions with the menu item(s).
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //the onOptionsItemSelected() method is created to react
    // to different user interactions with the menu item(s).
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);

                //AlertDialog is added to get the task from the user
                // when the add item button is clicked.
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //line 48 to 56 adapt MainActivity to store data in the database
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                //To see the updated data, you need to call the updateUI() method every time the underlying data of the app changes. So, add it in two places:
                                //In the onCreate() method, that initially shows all the data
                                //After adding a new task using the AlertDialog
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //fetch all the data from the database and show it in the main view.
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,// what view to use for the items
                    R.id.task_title,// where to put the String of data
                    taskList);// where to get all the data
            mTaskListView.setAdapter(mAdapter);// set it as the adapter of the ListView instance
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    //the task is deleted once it is finish mean when delete button is clicked
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }
}