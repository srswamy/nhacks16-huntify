package com.nhacks.share.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nhacks.share.Adapters.TimeViewAdapter;
import com.nhacks.share.Adapters.TimeViewRenterAdapter;
import com.nhacks.share.DateDialog;
import com.nhacks.share.Objects.Book;
import com.nhacks.share.Objects.RecyclerViewRow;
import com.nhacks.share.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 3/13/2016.
 */
public class BookDetailsFragment extends Fragment {
    String category;
    private FloatingActionButton mFloatingSaveButton;
    private int lastRecordedReps = 0;
    private double lastRecordedWeight = 0;
    private TextView bookName;
    private TextView bookEdition;
    private TextView schoolName;
    private TextView pricePerHour;
    TextView mDateView;
    ImageView calenderView;
    private RecyclerView mRecyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Date mSelectedDate;
    ImageView mNextDay;
    ImageView mPrevDay;
    int curYear;
    private TimeViewRenterAdapter mAdapter;
    JSONArray timeAndHours;
    String userBookId;
    JSONArray bookHoursAndTime;

    public static BookDetailsFragment getInstance(int position) {
        BookDetailsFragment myFragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        myFragment.setArguments(args);
        return myFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.l_book_details, container, false);
        Bundle b = getArguments();
        if (b != null) {
            category = b.getString("category");
            userBookId = b.getString("user_book_id");
        }
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(category);
        mDateView = (TextView) layout.findViewById(R.id.dateText);
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        pricePerHour = (TextView) layout.findViewById(R.id.price);
        bookName = (TextView) layout.findViewById(R.id.bookName);
        bookEdition = (TextView) layout.findViewById(R.id.bookEdition);
        schoolName = (TextView) layout.findViewById(R.id.schoolName);
        calenderView = (ImageView) layout.findViewById(R.id.datePicker);
        mNextDay = (ImageView) layout.findViewById(R.id.nextDayBtn);
        mPrevDay = (ImageView) layout.findViewById(R.id.prevDayBtn);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.timeRecyclerView);
        final String userId = sharedpreferences.getString("user_id", "");
        timeAndHours = new JSONArray();
        mFloatingSaveButton = (FloatingActionButton) layout.findViewById(R.id.floatingSaveBookButton);

        mFloatingSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeAndHours.length() == 0) {
                    JSONObject obj = new JSONObject();
                    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                    String date = format2.format(mSelectedDate);
                    try {
                        obj.put("date", date);
                        ArrayList hours = new ArrayList();
                        for (int i = 0; i < 24; i++) {
                            if (mAdapter.pickedTimes[i] == 1) {
                                hours.add(i);
                            }
                        }
                        obj.put("hours", new JSONArray(hours));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    timeAndHours.put(obj);
                }

                RequestQueue queue = Volley.newRequestQueue(getContext());

                StringRequest myReq = new StringRequest(Request.Method.POST, "http://52.37.205.141:3001/api/v1/users/" + userId + "/books/rent", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String t = "";
                        //mPostCommentResponse.requestCompleted();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String g = "";
                        //mPostCommentResponse.requestEndedWithError(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("users_book_id", userBookId);
                        params.put("rent_user_times", timeAndHours.toString());

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                queue.add(myReq);
            }
        });

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Calendar c = Calendar.getInstance();
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - 1;
        curYear = c.get(Calendar.YEAR);
        Date d = new Date(curYear, month, day);

        mSelectedDate = new Date();

        updateDateView(d, curYear, month, day);

        final DateDialog dateDialog = new DateDialog() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                Date d = new Date(year - 1900, monthOfYear, dayOfMonth);
                mSelectedDate = d;
                updateDateView(d, year, monthOfYear, dayOfMonth);
            }
        };

        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                dateDialog.show(ft, "DatePicker");
            }
        });

        mPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                String date = format2.format(mSelectedDate);
                try {
                    obj.put("date", date);
                    ArrayList hours = new ArrayList();
                    for (int i = 0; i < 24; i++) {
                        if (mAdapter.pickedTimes[i] == 1) {
                            hours.add(i);
                        }
                    }
                    obj.put("hours", new JSONArray(hours));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                timeAndHours.put(obj);
                mSelectedDate = getPrevDayDate(mSelectedDate);

                for (int i = 0; i < bookHoursAndTime.length(); i++) {
                    try {
                        JSONObject cur = new JSONObject(String.valueOf(bookHoursAndTime.get(i)));
                        if (cur.getString("date").equals(format2.format(mSelectedDate))) {
                            mAdapter.updateData(getIntArray(cur.getString("hours")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                String date = format2.format(mSelectedDate);
                try {
                    obj.put("date", date);
                    ArrayList hours = new ArrayList();
                    for (int i = 0; i < 24; i++) {
                        if (mAdapter.pickedTimes[i] == 1) {
                            hours.add(i);
                        }
                    }
                    obj.put("hours", new JSONArray(hours));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                timeAndHours.put(obj);
                mSelectedDate = getNextDayDate(mSelectedDate);

                for (int i = 0; i < bookHoursAndTime.length(); i++) {
                    try {
                        JSONObject cur = new JSONObject(String.valueOf(bookHoursAndTime.get(i)));
                        if (cur.getString("date").equals(format2.format(mSelectedDate))) {
                            mAdapter.updateData(getIntArray(cur.getString("hours")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        getBookDetails();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int[] tmp = {};
        mAdapter = new TimeViewRenterAdapter(getActivity(), getTimes(), tmp);
        mRecyclerView.setAdapter(mAdapter);

    }

    public int[] getIntArray(String arr){
        String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {};
        }
        return results;
    }

    public Date getPrevDayDate(Date curDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.DATE, -1);
        Date d = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        updateDateView(d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return d;
    }

    public Date getNextDayDate(Date curDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.DATE, 1);

        Date d = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        updateDateView(d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        return d;
    }

    public void updateDateView(Date date, int year, int monthOfYear, int dayOfMonth) {

        String[] monthInStr = getResources().getStringArray(R.array.monthNames);
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String weekDayString = format2.format(date);
        String dateStr;

        if (curYear == year) {
            dateStr = weekDayString + ", " + monthInStr[monthOfYear] + " " + dayOfMonth;
        } else {
            dateStr = weekDayString + ", " + monthInStr[monthOfYear] + " " + dayOfMonth + " " + year;
        }
        mDateView.setText(dateStr);
    }

    public int[] getTimes() {

        int[] timesAvailable = new int[24];
        for (int i = 0; i < 24; i++) {
            timesAvailable[i] = 0;
        }
        return timesAvailable;
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    public void getBookDetails() {
        final List<RecyclerViewRow> data = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest myReq = new StringRequest(Request.Method.GET, "http://52.37.205.141:3001/api/v1/users/books/info?users_book_id=" + userBookId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String t = "";
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    JSONObject book = obj.getJSONObject("book");
                    //JSONArray availibilities = obj.getJSONArray("book_availabilities");
                    JSONObject usersBook = obj.getJSONObject("users_book");

                    bookName.setText(book.getString("name"));
                    pricePerHour.setText("$" + usersBook.getString("price_per_hour") + "/hr");
                    bookEdition.setText("Edition " + book.getString("edition"));
                    bookHoursAndTime = obj.getJSONArray("book_availabilities");

                    populateBookTimes();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //mPostCommentResponse.requestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String g = "";
                //mPostCommentResponse.requestEndedWithError(error);

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(myReq);
    }

    public void populateBookTimes() {
        for (int i = 0; i < bookHoursAndTime.length(); i++) {
            try {
                JSONObject cur = new JSONObject(String.valueOf(bookHoursAndTime.get(i)));
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                if (cur.getString("date").equals(format2.format(mSelectedDate))) {
                    mAdapter.updateData(getIntArray(cur.getString("hours")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
