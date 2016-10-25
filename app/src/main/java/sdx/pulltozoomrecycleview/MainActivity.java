package sdx.pulltozoomrecycleview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private PullToZoomRecycleViewEx mRecycleView;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initList();
        mRecycleView = (PullToZoomRecycleViewEx) findViewById(R.id.scroll_view);
        inflater = LayoutInflater.from(this);

        initZoomView(mRecycleView);
    }
    private void initList() {
        list = new ArrayList<>();
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
        list.add("阿什顿发");
    }

    private void initZoomView(PullToZoomRecycleViewEx scrollView) {
        View headView = inflater.inflate(R.layout.head_view, null);
        View zoomView = inflater.inflate(R.layout.head_zoom_view, mRecycleView, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setAdapter(new MyAdapter(), this);
    }


    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {


        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MainActivity.this, android.R.layout.simple_list_item_1, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                holder.textView.setText(list.get(position));
                holder.textView.setTextColor(Color.parseColor("#000000"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position < list.size())
                return 0;
            return 1;
        }
    }

    private ArrayList<String> list;

    private class MyHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
