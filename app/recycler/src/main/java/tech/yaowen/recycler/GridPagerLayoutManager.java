package tech.yaowen.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class GridPagerLayoutManager extends GridLayoutManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "GridPagerLayoutManager";


    OrientationHelper mOrientationHelper;
    int [] mCachedBorders;
    int mSpanCount = DEFAULT_SPAN_COUNT;
    private boolean mMeasurementCacheEnabled = true;

    private boolean mItemPrefetchEnabled = true;
    private int orientationPageCount;

    DefaultSpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();


    public GridPagerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

//    public GridPagerLayoutManager(Context context, int spanCount) {
//        super(context, spanCount);
//    }

    public GridPagerLayoutManager setGridRowColumn(int row, int column) {
        setSpanCount(row);
        setOrientationPageCount(column);
        return this;
    }

    public void setOrientationPageCount(int column) {
        if (column < 1) {
            throw new IllegalArgumentException("Column count should be at least 1. Provided " + column);
        }
        orientationPageCount = column;
    }

    public GridPagerLayoutManager(Context context, int row, int column, boolean reverseLayout) {
        super(context, row, RecyclerView.HORIZONTAL, reverseLayout);
        setOrientationPageCount(column);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    public int getRowCountForAccessibility(RecyclerView.Recycler recycler,
                                           RecyclerView.State state) {
        if (getOrientation() == HORIZONTAL) {
            return mSpanCount;
        } else if (getOrientation() == RecyclerView.VERTICAL){
            return orientationPageCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        // Row count is one more than the last item's row index.
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    @Override
    public int getColumnCountForAccessibility(RecyclerView.Recycler recycler,
                                              RecyclerView.State state) {
        if (getOrientation() == VERTICAL) {
            return mSpanCount;
        } else if (getOrientation() == HORIZONTAL) {
            return orientationPageCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        // Column count is one more than the last item's column index.
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }


    private int getSpanGroupIndex(RecyclerView.Recycler recycler, RecyclerView.State state,
                                  int viewPosition) {
        if (!state.isPreLayout()) {
            return mSpanSizeLookup.getCachedSpanGroupIndex(viewPosition, mSpanCount);
        }
        final int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(viewPosition);
        if (adapterPosition == -1) {
            if (DEBUG) {
                throw new RuntimeException("Cannot find span group index for position "
                        + viewPosition);
            }
            Log.w(TAG, "Cannot find span size for pre layout position. " + viewPosition);
            return 0;
        }
        return mSpanSizeLookup.getCachedSpanGroupIndex(adapterPosition, mSpanCount);
    }




    int getSpaceForSpanRange(int startSpan, int spanSize) {
        if (getOrientation() == VERTICAL && isLayoutRTL()) {
            return mCachedBorders[mSpanCount - startSpan]
                    - mCachedBorders[mSpanCount - startSpan - spanSize];
        } else {
            return mCachedBorders[startSpan + spanSize] - mCachedBorders[startSpan];
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    private void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {

        final LayoutParams lp = (LayoutParams) view.getLayoutParams();
        final Rect decorInsets = lp.mDecorInsets;
        final int verticalInsets = decorInsets.top + decorInsets.bottom
                + lp.topMargin + lp.bottomMargin;
        final int horizontalInsets = decorInsets.left + decorInsets.right
                + lp.leftMargin + lp.rightMargin;
        final int availableSpaceInOther = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
        final int wSpec;
        final int hSpec;
        if (getOrientation() == VERTICAL) {
            wSpec = getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode,
                    horizontalInsets, lp.width, false);
            hSpec = getChildMeasureSpec(mOrientationHelper.getTotalSpace(), getHeightMode(),
                    verticalInsets, lp.height, true);
        } else {
            hSpec = getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode,
                    verticalInsets, lp.height, false);
            wSpec = getChildMeasureSpec(mOrientationHelper.getTotalSpace(), getWidthMode(),
                    horizontalInsets, lp.width, true);
        }
        measureChildWithDecorationsAndMargin(view, wSpec, hSpec, alreadyMeasured);
    }



    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec,
                                                      boolean alreadyMeasured) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final boolean measure;
        if (alreadyMeasured) {
            measure = shouldReMeasureChild(child, widthSpec, heightSpec, lp);
        } else {
            measure = shouldMeasureChild(child, widthSpec, heightSpec, lp);
        }
        if (measure) {
            child.measure(widthSpec, heightSpec);
        }
    }



    public static class LayoutParams extends  GridLayoutManager.LayoutParams {
        final Rect mDecorInsets = new Rect();

        int mSpanIndex = INVALID_SPAN_ID;
        int mSpanSize = 0;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }

    /**
     * RecyclerView internally does its own View measurement caching which should help with
     * WRAP_CONTENT.
     * <p>
     * Use this method if the View is already measured once in this layout pass.
     */
    boolean shouldReMeasureChild(View child, int widthSpec, int heightSpec, RecyclerView.LayoutParams lp) {
        return !mMeasurementCacheEnabled
                || !isMeasurementUpToDate(child.getMeasuredWidth(), widthSpec, lp.width)
                || !isMeasurementUpToDate(child.getMeasuredHeight(), heightSpec, lp.height);
    }

    // we may consider making this public
    /**
     * RecyclerView internally does its own View measurement caching which should help with
     * WRAP_CONTENT.
     * <p>
     * Use this method if the View is not yet measured and you need to decide whether to
     * measure this View or not.
     */
    boolean shouldMeasureChild(View child, int widthSpec, int heightSpec, RecyclerView.LayoutParams lp) {
        return child.isLayoutRequested()
                || !mMeasurementCacheEnabled
                || !isMeasurementUpToDate(child.getWidth(), widthSpec, lp.width)
                || !isMeasurementUpToDate(child.getHeight(), heightSpec, lp.height);
    }

    private static boolean isMeasurementUpToDate(int childSize, int spec, int dimension) {
        final int specMode = View.MeasureSpec.getMode(spec);
        final int specSize = View.MeasureSpec.getSize(spec);
        if (dimension > 0 && childSize != dimension) {
            return false;
        }
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                return true;
            case View.MeasureSpec.AT_MOST:
                return specSize >= childSize;
            case View.MeasureSpec.EXACTLY:
                return  specSize == childSize;
        }
        return false;
    }

    public static final class DefaultSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        final SparseIntArray mSpanIndexCache = new SparseIntArray();
        final SparseIntArray mSpanGroupIndexCache = new SparseIntArray();

        private boolean mCacheSpanIndices = false;
        private boolean mCacheSpanGroupIndices = false;

        @Override
        public int getSpanSize(int position) {
            return 1;
        }

        @Override
        public int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }

        int getCachedSpanGroupIndex(int position, int spanCount) {
            if (!mCacheSpanGroupIndices) {
                return getSpanGroupIndex(position, spanCount);
            }
            final int existing = mSpanGroupIndexCache.get(position, -1);
            if (existing != -1) {
                return existing;
            }
            final int value = getSpanGroupIndex(position, spanCount);
            mSpanGroupIndexCache.put(position, value);
            return value;
        }




    }


}
