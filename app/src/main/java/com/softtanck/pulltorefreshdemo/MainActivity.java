package com.softtanck.pulltorefreshdemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.softtanck.pulltorefreshdemo.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PullToRefreshView.OnRefreshListener, AdapterView.OnItemClickListener {

    private ListView lv;

    private List<String> mlist;

    private PullToRefreshView pl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pl = (PullToRefreshView) findViewById(R.id.pl);
        pl.setOnRefreshListener(this);
        mlist = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mlist.add("----" + i);
        }
        lv = (ListView) findViewById(R.id.lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mlist);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void OnRefreshing() {
        // need doing background.
        Log.d("Tanck", "refreshing...");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pl.stopRefresh();
    }
}
