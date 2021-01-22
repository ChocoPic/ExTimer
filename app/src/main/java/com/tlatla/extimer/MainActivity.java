package com.tlatla.extimer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_main);

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
                        for(int i=0; i<allList.get(j).size(); i++){
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
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent);
            }

            @Override
            public boolean onLongItemClick(Adapter.Holder holder, View view, int position) {
                Log.d("아이템 길게 클릭함",position+"");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = adapter.getItem(position);
                        String line = "";
                        for (int j=0; j<titleList.size(); j++){
                            if(titleList.get(j).equals(item)){
                                for(int i=0; i<allList.get(j).size();i++){
                                    line = line +(allList.get(j)).get(i)+",";
                                }
                                break;
                            }
                        }
                        titleList.remove(position);
                        allList.remove(position);
                        helper.deleteData(line);
                        adapter.notifyDataSetChanged();
                        Log.d("크기", titleList.size() +","+allList.size());
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
                //adapter.notifyItemRemoved(position);
                //adapter.notifyItemRangeChanged(position, titleList.size());
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
