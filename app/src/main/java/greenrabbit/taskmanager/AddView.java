package greenrabbit.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddView extends AppCompatActivity{

    EditText et_task, et_note;
    Spinner spinner;
    Button bt_add;

    DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view);
        init();
    }

    public void init(){
        dataBase = new DataBase(this);

        et_task = findViewById(R.id.et_task);
        et_note = findViewById(R.id.et_note);
        spinner = findViewById(R.id.spinner);
        bt_add = findViewById(R.id.bt_add_a);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTask();
            }
        });

    }

    public ArrayList<String> getTask() {
        ArrayList<String> al = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = new Date();
        String date = format.format(date1).toString();

        al.add(et_task.getText().toString());
        al.add(spinner.getSelectedItem().toString());
        al.add(et_note.getText().toString());
        al.add(date);

        return al;
    }

    private void addNewTask(){
        ArrayList<String> al = getTask();
        if(al.get(0).length() > 0){
            dataBase.openDB();
            dataBase.addRec(al.get(0), al.get(1), al.get(2), al.get(3));
            dataBase.closeDB();
            Intent intent = new Intent(AddView.this,MainView.class);
            startActivity(intent);
        }else
            callToast(getResources().getString(R.string.warning));
    }

    public void callToast(String m){
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
    }

}
