package me.ajax.cardswipe3.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;

import static me.ajax.cardswipe3.utils.GeometryUtils.polarX;
import static me.ajax.cardswipe3.utils.GeometryUtils.polarY;

/**
 * Created by aj on 2018/4/24
 */

public class CardLayoutManger extends RecyclerView.LayoutManager {

    private int mAllOffsetAngle;


    private SparseIntArray mAllItemAngles = new SparseIntArray();
    private SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        int offsetAngle = (int) (-dy / 10F);

        if (mAllOffsetAngle + offsetAngle > 0) {
            offsetAngle = 0;
        } else if (mAllOffsetAngle + offsetAngle < -(getItemCount() - 3) * 60) {
            offsetAngle = 0;
        }
        mAllOffsetAngle += offsetAngle;

        for (int i = 0; i < getItemCount(); i++) {
            mAllItemAngles.put(i, mAllItemAngles.get(i) + offsetAngle);
        }

        layoutItems(recycler);

        return dy;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getItemCount() == 0 || state.isPreLayout()) return;

        mAllOffsetAngle = 0;

        //初始化
        for (int i = 0; i < getItemCount(); i++) {
            mAllItemAngles.append(i, (i - 1) * 60);
            mHasAttachedItems.append(i, false);
        }

        //回收所有先
        detachAndScrapAttachedViews(recycler);

        //布局
        layoutItems(recycler);
    }

    private void layoutItems(RecyclerView.Recycler recycler) {


        //回收或刷新位置
        for (int i = 0; i < getChildCount(); i++) {

            View view = getChildAt(i);
            int position = getPosition(view);
            int viewAngle = mAllItemAngles.get(position);

            if (viewAngle < -120 || viewAngle > 120) {
                removeAndRecycleView(view, recycler);
                l("回收 " + position);
                mHasAttachedItems.append(position, false);
            } else {
                polarLayout(view, viewAngle);
                mHasAttachedItems.append(position, true);
            }
        }

        //添加新的
        for (int i = 0; i < getItemCount(); i++) {

            View view = recycler.getViewForPosition(i);
            int viewAngle = mAllItemAngles.get(i);
            if (viewAngle < -120 || viewAngle > 120) continue;
            if (mHasAttachedItems.get(i)) continue;

            addView(view);
            l("添加 " + i);

            polarLayout(view, mAllItemAngles.get(i));
        }
    }

    //按照极坐标布局
    private void polarLayout(View view, double angle) {

        int baseY = getHeight() / 2;

        int centerX = (int) polarX(dp(200), angle);
        int centerY = baseY + (int) polarY(dp(200), angle);

        measureChildWithMargins(view, 0, 0);
        int viewWidth = getDecoratedMeasuredWidth(view);
        int viewHeight = getDecoratedMeasuredHeight(view);

        layoutDecoratedWithMargins(view, centerX - viewWidth / 2, centerY - viewHeight / 2,
                centerX + viewWidth / 2, centerY + viewHeight / 2);
        view.setRotation((float) angle);
    }

    private int dp(float dp) {
        return (int) dp * 3;
    }

    static void l(Object... list) {
        String text = "";
        for (Object o : list) {
            text += "   " + o.toString();
        }
        Log.e("######", text);
    }
}
