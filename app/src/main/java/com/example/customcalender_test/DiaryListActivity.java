package com.example.customcalender_test;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customcalender_test.data.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// select date 받아오면 데이터베이스에서 받아오기
// 날짜, 요일 리스트뷰
public class DiaryListActivity extends AppCompatActivity {

    private DatabaseReference rootRefer = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = rootRefer.child("addbook");

    private Calendar mSelectDate;
    private List<Event> mEventList = new ArrayList<>();
    private String selectkey;

    public void setSelectDate(Calendar selectDate){this.mSelectDate = selectDate;}

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_diary);

        selectkey = getIntent().getExtras().getString("selectdate");

        getRecordsOfDay();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUI();
            }
        }, 300);

        FloatingActionButton homefloatbtn = (FloatingActionButton) findViewById(R.id.gobackCal_btn);
        homefloatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CalendarViewWithNotesActivity.makeIntent(v.getContext()));
            }
        });
    }

    private void getRecordsOfDay(){
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String title = postSnapshot.child("title").getValue(String.class);
                    String color = postSnapshot.child("color").getValue(String.class);
                    String author = postSnapshot.child("author").getValue(String.class);

                    int count =0;
                    for (DataSnapshot recorddata: postSnapshot.child("record").getChildren()){
                        if(recorddata.getKey().equals(selectkey)){ // 선택한 날짜와 같을 경우에만 추가
                            String id = postSnapshot.getKey() + count++;
                            String getCal = recorddata.getKey();
                            String[] calArr = getCal.split("-");

                            Calendar cal = Calendar.getInstance();
                            cal.set(Integer.parseInt(calArr[0]), Integer.parseInt(calArr[1])-1, Integer.parseInt(calArr[2]));

                            mSelectDate = cal;

                            String checkDiary = recorddata.child("isdiary").getValue(String.class);
                            String readPage = recorddata.child("pages").getValue(String.class);
                            String diaryText = "";

                            Boolean isDiary = null;

                            if(checkDiary.equals("0")){ // no diary
                                isDiary = false;
                                diaryText = "";
                            }else if(checkDiary.equals("1")){
                                isDiary = true;
                                diaryText = recorddata.child("diarytext").getValue(String.class);
                            }


                            int getcolor;
                            try{
                                getcolor = Integer.parseInt(color);
                            }catch (NumberFormatException e){
                                getcolor = 0;
                            }

                            Event mGetEvent = new Event(
                                    id,
                                    title,
                                    cal,
                                    Integer.parseInt(color),
                                    isDiary,
                                    diaryText,
                                    readPage
                            );
                            mGetEvent.setmAuthor(author);

                            mEventList.add(mGetEvent);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setUI(){
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Calendar day = mSelectDate;
        TextView showDate = findViewById(R.id.listD_date);
        TextView showDay = findViewById(R.id.listD_day);
        RecyclerView rDiaryList = findViewById(R.id.calendar_events);

        showDate.setText(new SimpleDateFormat("MM월 dd일 ", Locale.getDefault()).format(day.getTime()));
        showDay.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(day.getTime()));

        rDiaryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rDiaryList.setAdapter(new DiaryListActivity.CalendarDiaryAdapter(mEventList));
        rDiaryList.setVisibility(mEventList.size() == 0? View.GONE : View.VISIBLE);

    }


    public class CalendarDiaryAdapter extends RecyclerView.Adapter<DiaryListActivity.CalendarDiaryAdapter.ViewHolder>{

        private final List<Event> mCalendarEvents; // 선택한 날짜의 책 기록  저장

        CalendarDiaryAdapter(List<Event> events) { mCalendarEvents = events; }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public CalendarDiaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());
            View v = vi.inflate(R.layout.list_item_calendar_event, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Event event = mCalendarEvents.get(position);
            String title = event.getTitle();

            holder.tvBookTitle.setText(title);
            holder.rclEventIcon.setBackgroundColor(event.getColor());
            holder.tvReadPage.setText("last page: p."+ event.getmPage());

            if(event.getisDiary()==true){
                holder.tvDiaryPreview.setText(event.getDiaryText());
            }else{
                holder.tvDiaryPreview.setText("다이어리를 입력해 주세요 :)");
            }
        }

        @Override
        public int getItemCount() {
            return mCalendarEvents.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            View rclEventIcon;
            TextView tvBookTitle;
            TextView tvReadPage;
            TextView tvDiaryPreview;

            ViewHolder(View view) {
                super(view);
                rclEventIcon = view.findViewById(R.id.rcl_calendar_event_icon);
                tvBookTitle = view.findViewById(R.id.tv_calendar_book_title);
                tvReadPage = view.findViewById(R.id.tv_readpages);
                tvDiaryPreview = view.findViewById(R.id.tv_diaryPreview);

                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                Event selected = mEventList.get(pos); // 선택한 이벤트

                Intent intent = new Intent(v.getContext(), ShowDiaryDetailActivity.class);
                intent.putExtra("diary", (Parcelable) selected);
                startActivity(intent);
                finish();
            }
        }
    }

}
