package com.tlatla.extimer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class TimerListActivity extends AppCompatActivity {
    private final String FILE_NAME = "timer.list";
    private RecyclerView recyclerView;
    private Adapter adapter;
    private ArrayList<String> titleList = new ArrayList<>();    //리사이클러뷰 출력용 (제목)
    private ArrayList<ArrayList<String>> allList;  //파일 저장용 (제목+시간)

    private Button newBtn;
    private Button addTimerBtn;
    private Button saveBtn;
    private LinearLayout add_layout;
    private TextView title_editText;
    private LinearLayout time_layout;
    private ArrayList<EditText> editTexts;
    private SQLdbHelper helper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerlist);

        //뷰 세팅
        recyclerView = findViewById(R.id.recyclerview);
        add_layout = findViewById(R.id.add_layout);
        newBtn = findViewById(R.id.newBtn);
        addTimerBtn = findViewById(R.id.addTimerBtn);
        saveBtn = findViewById(R.id.saveBtn);
        title_editText = findViewById(R.id.title_editText);
        time_layout = findViewById(R.id.time_layout);
        add_layout.setVisibility(View.GONE);

        //SQLite & ArrayList 세팅
        helper = new SQLdbHelper(this);
        //helper.getInst(this);
        allList = helper.loadDataList();
        for (int i = 0; i < allList.size(); i++) {
            titleList.add(((allList.get(i)).get(0)).split(",")[0]);
        }

        //리사이클러뷰 세팅
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(titleList, this);
        recyclerView.setAdapter(adapter);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.newBtn:
                        editTexts = new ArrayList<>();
                        time_layout.removeAllViews();
                        add_layout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.addTimerBtn:
                        timeTextView();
                        break;
                    case R.id.saveBtn:
                        add_layout.setVisibility(View.GONE);
                        String title = title_editText.getText().toString();
                        ArrayList<String> data = new ArrayList<>();
                        for (int i = 0; i < editTexts.size(); i++) {
                            if (editTexts.get(i).length() != 0)
                                data.add(editTexts.get(i).getText().toString());
                        }
                        if (data.size() > 0 && title.length() > 0) {
                            String line = title + ",";
                            for (int i = 0; i < data.size(); i++) {
                                line = line + data.get(i) + ",";
                            }
                            titleList.add(title);
                            allList.add(data);
                            helper.insertData(line);
                            adapter.notifyDataSetChanged();
                        }
                        add_layout.setVisibility(View.GONE);
                }
            }
        };
        newBtn.setOnClickListener(onClickListener);
        addTimerBtn.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Adapter.Holder holder, View view, int position) {
                Log.d("아이템 클릭함",position + "");
                String item = adapter.getItem(position);
                String line = "";
                for (int j=0; j<titleList.size(); j++){
                    if(titleList.get(j).equals(item)){
                        for(int i=0; i<allList.get(i).size();i++){
                            line = line + (allList.get(j)).get(i)+",";
                        }
                        break;
                    }
                }
                SharedPreferences pref = getSharedPreferences("Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("timer",line);
                editor.commit();
                Log.d("*pref의 data에 ", line+"을 저장함");
            }
        });
    }

    //EditText(타이머) 만들기
    public void timeTextView() {
        EditText myText = new EditText(this);
        myText.setTextColor(Color.BLACK);
        myText.setTextSize(18);
        myText.setHint("타이머 시간");
        myText.setHintTextColor(Color.LTGRAY);
        myText.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myText.setLayoutParams(lp);
        editTexts.add(myText);
        time_layout.addView(myText);
    }
}


        /*
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = adapter.getItemCount();
                int index;
                if (count>0){
                    index = recyclerView.인덱스얻어오기;
                    if(index>-1 && index<count){
                        items.remove(index);
                        adapter.notifyDataSetChanged();
                        saveItems();
                    }
                }
            }
        });
         */
