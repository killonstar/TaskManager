package greenrabbit.taskmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainView extends AppCompatActivity{

    ListView listView;
    Button bt_add, bt_sort, bt_delall;

    DataBase dataBase;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;
    final Context context = this;
    String[] from = new String[] {DataBase.column_title, DataBase.column_status, DataBase.column_date};
    int[] to = new int[] {R.id.tv_title, R.id.tv_status, R.id.tv_date};
    public static final int ID_DEl = 101;
    public static final int ID_MORE = 103;
    public static final int ID_CHANGE = 104;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        init();
    }

    private void init(){
        dataBase = new DataBase(this);

        listView = findViewById(R.id.listview);
        setList();
        registerForContextMenu(listView);

        bt_add = findViewById(R.id.bt_add_m);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });
        bt_sort = findViewById(R.id.bt_sort_m);
        bt_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTask();
            }
        });
        bt_delall = findViewById(R.id.bt_delall_m);
        bt_delall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delAllTask();
            }
        });
    }

    private void addNewTask(){
        Intent intent = new Intent(MainView.this, AddView.class);
        startActivity(intent);
    }
    private void sortTask(){
        dataBase.openDB();
        setupDialogSort();
        dataBase.closeDB();
    }
    private void delAllTask(){
        dataBase.openDB();
        dataBase.delRecAll();
        dataBase.closeDB();
        // updatelist
        updateList();
    }

    private void setList(){
        dataBase.openDB();
        cursor = dataBase.getAllData();
        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.item_view,
                cursor,
                from,
                to,
                0);
        listView.setAdapter(simpleCursorAdapter);
        dataBase.closeDB();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, ID_MORE, 0, R.string.more);
        menu.add(0, ID_CHANGE, 0, R.string.change);
        menu.add(0, ID_DEl, 0, R.string.del);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case ID_DEl:
                AdapterView.AdapterContextMenuInfo acmi_del = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.openDB();
                dataBase.delRec(acmi_del.id);
                dataBase.closeDB();
                // updatelist
                updateList();
                break;
            case ID_CHANGE:
                final AdapterView.AdapterContextMenuInfo acmi_change = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.openDB();
                ArrayList<String> detailList = dataBase.detailsRec(acmi_change.id);
                dataBase.closeDB();
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.dialog_edit, null);
                AlertDialog.Builder alDB = new AlertDialog.Builder(context);
                alDB.setView(view);
                final EditText task_d = view.findViewById(R.id.et_task_d);
                final EditText note_d = view.findViewById(R.id.et_note_d);
                final Spinner spinner_status_d = view.findViewById(R.id.spinner_status_d);
                final TextView date_d = view.findViewById(R.id.tv_date_d);
                task_d.setText(detailList.get(1));
                note_d.setText(detailList.get(3));
                String position_status = detailList.get(2);
                ArrayAdapter adapter = (ArrayAdapter) spinner_status_d.getAdapter();
                int position_status_number = adapter.getPosition(position_status);
                spinner_status_d.setSelection(position_status_number);
                date_d.setText(detailList.get(4));
                alDB
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(task_d.length() > 0){
                                    dataBase.openDB();
                                    dataBase.editRec(
                                            task_d.getText().toString(),
                                            note_d.getText().toString(),
                                            spinner_status_d.getSelectedItem().toString(),
                                            date_d.getText().toString(),
                                            Long.toString(acmi_change.id)
                                    );
                                    dataBase.closeDB();
                                }else{
                                    callToast(getResources().getString(R.string.warning));
                                }
                                // update list
                                updateList();

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = alDB.create();
                alertDialog.show();
                break;
            case ID_MORE:
                AdapterView.AdapterContextMenuInfo acmi_more = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainView.this);

                dataBase.openDB();
                ArrayList<String> detailinfo = dataBase.detailsRec(acmi_more.id);
                String detail_info =
                        getResources().getString(R.string.task) + " : " + detailinfo.get(1) + "\n\n" +
                        getResources().getString(R.string.status) + " : " + detailinfo.get(2) + "\n\n" +
                        getResources().getString(R.string.note) + " : " + detailinfo.get(3) + "\n\n" +
                        getResources().getString(R.string.date) + " : " + detailinfo.get(4);
                builder.setMessage(detail_info)
                        .setCancelable(false)
                        .setNegativeButton(R.string.done,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                ArrayList<String> a = dataBase.widgetData();
                String m = a.get(0) + " " + a.get(1);
                dataBase.closeDB();

                alert.show();

                callToast(m);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void updateList(){
        dataBase.openDB();
        cursor = dataBase.getAllData();
        SimpleCursorAdapter simpleCursorAdapter1 = new SimpleCursorAdapter(this, R.layout.item_view, cursor, from, to, 0);
        simpleCursorAdapter1.changeCursor(cursor);
        listView.setAdapter(simpleCursorAdapter1);
        dataBase.closeDB();
    }

    public void callToast(String m){
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
    }

    public void sortList(){
        dataBase.openDB();
        cursor = dataBase.sortData(status);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_view, cursor, from, to, 0);
        adapter.changeCursor(cursor);
        listView.setAdapter(adapter);
        dataBase.closeDB();
    }

    public void setupDialogSort(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_sort, null);
        AlertDialog.Builder alDB = new AlertDialog.Builder(context);
        alDB.setView(view);
        final RadioButton rb_new = view.findViewById(R.id.bt_new_sort);
        final RadioButton rb_progress = view.findViewById(R.id.bt_progress_sort);
        final RadioButton rb_done = view.findViewById(R.id.bt_done_sort);
        final RadioButton rb_all = view.findViewById(R.id.bt_all_sort);
        rb_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = getResources().getString(R.string.new_task);
            }
        });
        rb_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = getResources().getString(R.string.progress_task);
            }
        });
        rb_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = getResources().getString(R.string.done_task);
            }
        });
        rb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = getResources().getString(R.string.all_task);
            }
        });

        alDB
                .setCancelable(false) // Разрешить закрыть диалог кнопкой "back"
                .setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(status.equals(getResources().getString(R.string.all_task))){
                            setList();
                        }else
                            sortList();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alDB.create();
        alertDialog.show();
    }


}
