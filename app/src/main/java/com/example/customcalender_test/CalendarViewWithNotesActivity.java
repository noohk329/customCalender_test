package com.example.customcalender_test;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.calenderviewlib.CalendarView;
import com.example.customcalender_test.data.Event;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CalendarViewWithNotesActivity extends AppCompatActivity {

    private String[] mShortMonths;
    private CalendarView mCalendarView;

    private List<Event> mEventList = new ArrayList<>();

    private DatabaseReference rootRefer = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference conditionRef = rootRefer.child("addbook");

    public static Intent makeIntent(Context context) {
        return new Intent(context, CalendarViewWithNotesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mShortMonths = new DateFormatSymbols().getShortMonths();

        initializeEvent();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeUI();
            }
        }, 300);

    }

    private void initializeEvent(){
        conditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String title = postSnapshot.child("title").getValue(String.class);
                    String color = postSnapshot.child("color").getValue(String.class);
                    String author = postSnapshot.child("author").getValue(String.class);

                    int count =0;
                    for (DataSnapshot recorddata: postSnapshot.child("record").getChildren()){

                        String id = postSnapshot.getKey() + count++;
                        String getCal = recorddata.getKey();
                        String[] calArr = getCal.split("-");

                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(calArr[0]), Integer.parseInt(calArr[1])-1, Integer.parseInt(calArr[2]));

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
                                getcolor,
                                isDiary,
                                diaryText,
                                readPage
                        );
                        mGetEvent.setmAuthor(author);
                        mEventList.add(mGetEvent);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initializeUI() {

        super.setContentView(R.layout.activity_calendar_view_with_notes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        super.getMenuInflater().inflate(R.menu.menu_toolbar_calendar_view, toolbar.getMenu());
        setActionBar(toolbar);

        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnMonthChangedListener((month, year) -> {
            if (getActionBar() != null) {
                getActionBar().setTitle(mShortMonths[month]);
                getActionBar().setSubtitle(Integer.toString(year));
            }
        });

        // 날짜 클릭 시 dialog
        mCalendarView.setOnItemClickedListener((calendarObjects, previousDate, selectedDate) -> {
            if (calendarObjects.size() != 0) {

                Intent intent = new Intent(this, DiaryListActivity.class);

                String date = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
                intent.putExtra("selectdate", date);
                startActivity(intent);

            }
        });

        for (Event e : mEventList) {
            mCalendarView.addCalendarObject(parseCalendarObject(e));
        }

        if (getActionBar() != null) {
            int month = mCalendarView.getCurrentDate().get(Calendar.MONTH);
            int year = mCalendarView.getCurrentDate().get(Calendar.YEAR);
            getActionBar().setTitle(mShortMonths[month]);
            getActionBar().setSubtitle(Integer.toString(year));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.getMenuInflater().inflate(R.menu.menu_toolbar_calendar_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // today 클릭시 오늘 날짜로 이동.
        switch (item.getItemId()) {
            case R.id.action_today: {
                mCalendarView.setSelectedDate(Calendar.getInstance());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static CalendarView.CalendarObject parseCalendarObject(Event event) {
        return new CalendarView.CalendarObject(
                event.getID(),
                event.getDate(),
                event.getColor(),
                event.getisDiary() ? Color.BLUE : Color.RED);
    }

}
