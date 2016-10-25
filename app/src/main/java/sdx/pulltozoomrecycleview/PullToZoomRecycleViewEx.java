package sdx.pulltozoomrecycleview;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;


/**
 * Created by sdx on 2016/10/25.
 */

public class PullToZoomRecycleViewEx extends PullToZoomBase<MyRecycleView> implements MyRecycleView.OnScrollViewChanged {
    private static final String TAG = PullToZoomRecycleViewEx.class.getSimpleName();
    private FrameLayout mHeaderContainer;
    private int mHeaderHeight;
    private ScalingRunnable mScalingRunnable;
    private boolean isCustomHeaderHeight = false;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    public PullToZoomRecycleViewEx(Context context) {
        this(context, null);
    }

    public PullToZoomRecycleViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView.setOnScrollListener(this);
        mScalingRunnable = new ScalingRunnable();
    }

    /**
     * 是否显示headerView
     *
     * @param isHideHeader true: show false: hide
     */
    @Override
    public void setHideHeader(boolean isHideHeader) {
        if (isHideHeader != isHideHeader()) {
            super.setHideHeader(isHideHeader);
            if (isHideHeader) {
                removeHeaderView();
            } else {
                updateHeaderView();
            }
        }
    }

    @Override
    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.mHeaderView = headerView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomView(View zoomView) {
        if (zoomView != null) {
            this.mZoomView = zoomView;
            updateHeaderView();
        }
    }

    private void removeHeaderView() {
        if (mHeaderContainer != null) {
            mRootView.removeHeaderView(mHeaderContainer);
        }
    }

    private void updateHeaderView() {
        if (mHeaderContainer != null) {
            mRootView.removeHeaderView(mHeaderContainer);

            mHeaderContainer.removeAllViews();

            if (mZoomView != null) {
                mHeaderContainer.addView(mZoomView);
            }

            if (mHeaderView != null) {
                mHeaderContainer.addView(mHeaderView);
            }

            mHeaderHeight = mHeaderContainer.getHeight();
            mRootView.addHeaderView(mHeaderContainer);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter,Activity activity) {
        mRootView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRootView.setAdapter(adapter);
        setHeaderLayoutParams(activity);
    }

    @Override
    protected MyRecycleView createRootView(Context context, AttributeSet attrs) {
        MyRecycleView lv = new MyRecycleView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    /**
     * 重置动画，自动滑动到顶部
     */
    @Override
    protected void smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ");
        mScalingRunnable.startAnimation(200L);
    }

    @Override
    protected void pullHeaderToZoom(int newScrollValue) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = " + newScrollValue);
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = " + mHeaderHeight);
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        ViewGroup.LayoutParams localLayoutParams = mHeaderContainer.getLayoutParams();
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;
        mHeaderContainer.setLayoutParams(localLayoutParams);

        if (isCustomHeaderHeight) {
            ViewGroup.LayoutParams zoomLayoutParams = mZoomView.getLayoutParams();
            zoomLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;
            mZoomView.setLayoutParams(zoomLayoutParams);
        }
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter adapter = mRootView.getAdapter();

        if (null == adapter) {
            return true;
        } else {
            RecyclerView.LayoutManager layoutManager = mRootView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                if (firstVisibleItemPosition <= 1) {
                    final View firstVisibleChild = mRootView.getChildAt(0);
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.getTop() >= mRootView.getTop();
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {
        mHeaderContainer = new FrameLayout(getContext());
        if (mZoomView != null) {
            mHeaderContainer.addView(mZoomView);
        }
        if (mHeaderView != null) {
            mHeaderContainer.addView(mHeaderView);
        }
        mRootView.addHeaderView(mHeaderContainer);
    }

    /**
     * 设置HeaderView高度
     *
     * @param width
     * @param height
     */
    public void setHeaderViewSize(int width, int height) {
        if (mHeaderContainer != null) {
            Object localObject = mHeaderContainer.getLayoutParams();
            if (localObject == null) {
                localObject = new AbsListView.LayoutParams(width, height);
            }
            ((ViewGroup.LayoutParams) localObject).width = width;
            ((ViewGroup.LayoutParams) localObject).height = height;
            mHeaderContainer.setLayoutParams((ViewGroup.LayoutParams) localObject);
            mHeaderHeight = height;
        }
    }

    public void setHeaderLayoutParams(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        RecyclerView.LayoutParams localObject =
                new RecyclerView.LayoutParams(mScreenWidth, (int) (9.0f * (mScreenWidth / 16.0F)));

        if (mHeaderContainer != null) {
            mHeaderContainer.setLayoutParams(localObject);
            mHeaderHeight = localObject.height;
            isCustomHeaderHeight = true;
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        Log.d(TAG, "onLayout --> ");
        if (mHeaderHeight == 0 && mHeaderContainer != null) {
            mHeaderHeight = mHeaderContainer.getHeight();
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mZoomView != null && !isHideHeader() && isPullToZoomEnabled()) {
            float f = mHeaderHeight - mHeaderContainer.getBottom();
            Log.d(TAG, "onScroll --> f = " + f);
            if (isParallax()) {
                if ((f > 0.0F) && (f < mHeaderHeight)) {
                    int i = (int) (0.65D * f);
                    mHeaderContainer.scrollTo(0, -i);
                } else if (mHeaderContainer.getScrollY() != 0) {
                    mHeaderContainer.scrollTo(0, 0);
                }
            }
        }
    }

    class ScalingRunnable implements Runnable {
        protected long mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams localLayoutParams;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * PullToZoomRecycleViewEx.sInterpolator.getInterpolation(f1);
                    localLayoutParams = mHeaderContainer.getLayoutParams();
                    Log.d(TAG, "ScalingRunnable --> f2 = " + f2);
                    if (f2 > 1.0F) {
                        localLayoutParams.height = ((int) (f2 * mHeaderHeight));
                        mHeaderContainer.setLayoutParams(localLayoutParams);
                        if (isCustomHeaderHeight) {
                            ViewGroup.LayoutParams zoomLayoutParams;
                            zoomLayoutParams = mZoomView.getLayoutParams();
                            zoomLayoutParams.height = ((int) (f2 * mHeaderHeight));
                            mZoomView.setLayoutParams(zoomLayoutParams);
                        }
                        post(this);
                        return;
                    }

                    mIsFinished = true;
                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = ((float) (mHeaderContainer.getBottom()) / mHeaderHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }
}
