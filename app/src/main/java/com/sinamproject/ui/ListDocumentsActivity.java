package com.sinamproject.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.sinamproject.R;
import com.sinamproject.data.Document;
import com.sinamproject.data.databasehelper.DocumentSQLiteHelper;
import com.sinamproject.ui.adapters.DocumentAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZkHaider on 4/19/15.
 */
public class ListDocumentsActivity extends ActionBarActivity {

    // Db helper
    private DocumentSQLiteHelper db;
    private List<Document> mDocuments;

    // UI
    private RecyclerView mRecyclerView;
    private DocumentAdapter mDocumentAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_list_documents));

        initDBHelper();
        loadDocuments();
        initActionBar();
        initViews();
        initItemTouchListener();
    }

    private void initDBHelper() {
        db = DocumentSQLiteHelper.getDatabaseHelper(this);
    }

    private void loadDocuments() {
        mDocuments = db.getAllDocuments();
        Collections.reverse(mDocuments);
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.myDocuments));
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.documentsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mDocumentAdapter = new DocumentAdapter();
        mDocumentAdapter.setDocuments(mDocuments);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mDocumentAdapter);
    }

    private void initItemTouchListener() {
        final GestureDetector mGestureDetector = new GestureDetector(ListDocumentsActivity.this,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(child);

                    Document document = mDocumentAdapter.getDocument(position);

                    int color = mDocumentAdapter.getVibrantColor(position);
                    int darkColor = mDocumentAdapter.getDarkVibrantColor(position);

                    Intent i = new Intent(ListDocumentsActivity.this, DocumentDetailActivity.class);
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    bundle.putInt("darkcolor", darkColor);
                    bundle.putInt("color", color);
                    bundle.putString("title", document.getTitle());
                    bundle.putString("description", document.getRecognizedText());
                    i.putExtras(bundle);

                    ImageButton documentImage = (ImageButton) child.findViewById(R.id.documentImage);

                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityOptions options = ActivityOptions
                                .makeSceneTransitionAnimation(ListDocumentsActivity.this,
                                        documentImage, "hero_event"
                                );
                        startActivity(i, options.toBundle());
                    } else {
                        startActivityForResult(i, 0);
                    }
                    return true;
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent i = new Intent(ListDocumentsActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
