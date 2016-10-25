package sdx.pulltozoomrecycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by sdx on 2016/10/25.
 */

public class MyRecycleView extends RecyclerView {
    private ArrayList<View> mHeaderViews = new ArrayList<>();

    private ArrayList<View> mFootViews = new ArrayList<>();

    private Adapter mAdapter;

    public MyRecycleView(Context context) {
        super(context);
    }

    public MyRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view) {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFootView(View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof RecyclerWrapAdapter)) {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
//                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean removeHeaderView(View view) {
        for (int i = 0; i < mHeaderViews.size(); i++) {
            View info = mHeaderViews.get(i);
            if (info == view) {
                mHeaderViews.remove(i);
                return true;
            }
        }

        return false;
    }

    public interface OnScrollViewChanged {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public OnScrollViewChanged onChangedListener;

    public void setOnScrollListener(OnScrollViewChanged onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onChangedListener != null) {
            onChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {

        if (mHeaderViews.isEmpty() && mFootViews.isEmpty()) {
            super.setAdapter(adapter);
        } else {
            adapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }
}
