package com.example.kingdle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kingdle.Adapter.ReadBookTitleAdapter;
import com.example.kingdle.Adapter.SearchBookAdapter;
import com.example.kingdle.ApiService.AWSBooksApiService;
import com.example.kingdle.ApiService.GoogleBookApiService;
import com.example.kingdle.response.BookItem;
import com.example.kingdle.response.BookeTitle;
import com.example.kingdle.response.IndustryInfo;
import com.example.kingdle.response.SearchBook;
import com.example.kingdle.response.Volumeinfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {
    /**************************************
    Author: Yukan Zhang
    Timer connection
    *****************************************/
    TimerService FragTimer;
    boolean bound;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerService.TimerBinder TimerBinder = (TimerService.TimerBinder) iBinder;
            FragTimer = TimerBinder.getTime();
            FragTimer.runTimer1();
            FragTimer.running = true;
            bound = true;
            Log.v("Main","GetBinder");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            FragTimer.running =false;
            bound = false;
            // Log.v("Main","unBinder");
        }
    };
    /***********************************
    Timer connection end
    ***********************************/
    /***********************************
     Author: Yukan Zhang
     Timer connection, Database access
     ***********************************/
    ServiceDB db;
    ServiceDao sdao;
    ServiceTable stable;
    /***********************************
     Timer connection, database access end
     ***********************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //    if (getArguments() != null) {
    //        mParam1 = getArguments().getString(ARG_PARAM1);
    //        mParam2 = getArguments().getString(ARG_PARAM2);
    //    }
        /***********************************
         Author: Yukan Zhang
         Timer connection, Database access
         ***********************************/
        db = ServiceDB.getDatabase(this.getContext());
        sdao = db.serviceDao();
        stable = new ServiceTable();
        /***********************************
         Timer connection, database access end
         ***********************************/
    }

    static final String BASE_URL = "http://54.241.136.35:8080/";
    static final String TAG = MainActivity.class.getSimpleName();
    static Retrofit retrofit = null;
    private RecyclerView recyclerView;
    ReadBookTitleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);
        recyclerView = view.findViewById(R.id.rvBookTitleList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        connect();
        Button mButton = view.findViewById(R.id.ReButton);
        mButton.setOnClickListener(v -> {
            adapter.isShown = false;
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private void connect() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        AWSBooksApiService awsBooksApiService = retrofit.create(AWSBooksApiService.class);
        Call<List<BookeTitle>> call = awsBooksApiService.getBook();
        call.enqueue(new Callback<List<BookeTitle>>() {
            @Override
            public void onResponse(Call<List<BookeTitle>> call, Response<List<BookeTitle>> response) {
                List<BookeTitle> m = response.body();
                List<List<String>> data = new ArrayList<>();
                for(BookeTitle bt: m){
                    List<String> book = new ArrayList<>();
                    book.add(bt.getName());
                    book.add(bt.getContent());
                    data.add(book);
                }
                adapter = new ReadBookTitleAdapter(data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<BookeTitle>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /***********************************
        Author: Yukan Zhang
        Timer connection
        ***********************************/
        if(!bound) {
            Intent intent = new Intent(getContext(), TimerService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        /***********************************
        Timer connection end
        ***********************************/
    }

    @Override
    public void onStop() {
        super.onStop();
        /***********************************
        Author: Yukan Zhang
        Timer connection, Database access
        ***********************************/
        if(bound) {
            //TimerControl.last_read = FragTimer.seconds1;
            class SaveTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {

                    stable.date =  java.text.DateFormat.getDateTimeInstance().format(new Date());
                    stable.last_read = FragTimer.seconds1;
                    Log.v("last_read to db", String.valueOf(stable.last_read));
                    //adding to database
                    sdao.insert(stable);
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }
            SaveTask st = new SaveTask();
            st.execute();

            getActivity().unbindService(connection);
            FragTimer.running = false;
            bound = false;
        }
        /***********************************
        Timer connection, database access end
        ***********************************/
    }

}