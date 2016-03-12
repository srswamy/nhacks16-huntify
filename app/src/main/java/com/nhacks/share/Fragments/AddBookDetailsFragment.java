package com.nhacks.share.Fragments;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.nhacks.share.Objects.Book;
import com.nhacks.share.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Sagar on 3/12/2016.
 */
public class AddBookDetailsFragment extends Fragment implements WeekView.EmptyViewLongPressListener, WeekView.EventLongPressListener, MonthLoader.MonthChangeListener, WeekView.EventClickListener {
    String category;
    private FloatingActionButton mFloatingSaveButton;
    private int lastRecordedReps = 0;
    private double lastRecordedWeight = 0;
    private EditText bookName;
    private EditText bookEdition;
    private EditText schoolName;
    private WeekView mWeekView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.l_add_book_data, container, false);
        Bundle b = getArguments();
        if (b != null) {
            category = b.getString("category");
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(category);

        bookName = (EditText) layout.findViewById(R.id.bookName);
        bookEdition = (EditText) layout.findViewById(R.id.bookEdition);
        schoolName = (EditText) layout.findViewById(R.id.schoolName);

        Button addBook = (Button) layout.findViewById(R.id.add_btn);
        Button cancel = (Button) layout.findViewById(R.id.cancel_btn);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) layout.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);


        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book book = new Book();
                String name = bookName.getText().toString();
                String edition = bookEdition.getText().toString();
                String school = schoolName.getText().toString();

                Toast toast;
                if (name.equals("")) {
                    toast = Toast.makeText(getContext(), "Please enter Book Name!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    book.setName(name);
                }
                if (!edition.equals("")) {
                    book.setEdition(Integer.valueOf(edition));

                }
                if (!school.equals("")) {

                }
            }
        });

        mFloatingSaveButton = (FloatingActionButton) layout.findViewById(R.id.floatingSaveBookButton);
        mFloatingSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return layout;
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return new ArrayList<>();
    }
}
