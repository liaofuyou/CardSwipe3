package me.ajax.cardswipe3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import me.ajax.cardswipe3.layoutmanager.CardLayoutManger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new CardLayoutManger());
        recyclerView.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
            @Override
            public int onGetChildDrawingOrder(int childCount, int i) {
                return childCount - i - 1;
            }
        });
        recyclerView.setAdapter(new MyAdapter());

    }
}
