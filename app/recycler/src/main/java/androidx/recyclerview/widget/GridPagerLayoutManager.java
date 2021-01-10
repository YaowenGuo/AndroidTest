package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.Arrays;

public class GridPagerLayoutManager extends GridLayoutManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "GridPagerLayoutManager";
    private int orientationPageCount;
/*



    OrientationHelper mOrientationHelper;
    int [] mCachedBorders;
    int mSpanCount = DEFAULT_SPAN_COUNT;
    private boolean mMeasurementCacheEnabled = true;

    private boolean mItemPrefetchEnabled = true;


    DefaultSpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();*/


    public GridPagerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridPagerLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

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

/*

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

    private void cachePreLayoutSpanMapping() {
        final int childCount = getChildCount();
        int width = ScreenUtils.getScreenWidth();

        for (int i = 0; i < childCount; i++) {
            final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) getChildAt(i).getLayoutParams();
            final int viewPosition = lp.getViewLayoutPosition();
            mPreLayoutSpanSizeCache.put(viewPosition, lp.getSpanSize());
            mPreLayoutSpanIndexCache.put(viewPosition, lp.getSpanIndex());
        }
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);

        clearPreLayoutSpanMappingCache();
    }



    private void clearPreLayoutSpanMappingCache() {
        mPreLayoutSpanSizeCache.clear();
        mPreLayoutSpanIndexCache.clear();
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

    *//**
     * RecyclerView internally does its own View measurement caching which should help with
     * WRAP_CONTENT.
     * <p>
     * Use this method if the View is already measured once in this layout pass.
     *//*
    boolean shouldReMeasureChild(View child, int widthSpec, int heightSpec, RecyclerView.LayoutParams lp) {
        return !mMeasurementCacheEnabled
                || !isMeasurementUpToDate(child.getMeasuredWidth(), widthSpec, lp.width)
                || !isMeasurementUpToDate(child.getMeasuredHeight(), heightSpec, lp.height);
    }

    // we may consider making this public
    *//**
     * RecyclerView internally does its own View measurement caching which should help with
     * WRAP_CONTENT.
     * <p>
     * Use this method if the View is not yet measured and you need to decide whether to
     * measure this View or not.
     *//*
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
    }*/


    // --------------------------------------- ***** --------------------------------------

    private void ensureViewSet() {
        if (mSet == null || mSet.length != (mSpanCount * orientationPageCount)) {
            mSet = new View[orientationPageCount];
        }
    }


    @Override
    void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state,
                     LayoutState layoutState, LayoutChunkResult result) {
        final int otherDirSpecMode = mOrientationHelper.getModeInOther();
        final boolean flexibleInOtherDir = otherDirSpecMode != View.MeasureSpec.EXACTLY;
        final int currentOtherDirSize = getChildCount() > 0 ? mCachedBorders[mSpanCount] : 0;
        // if grid layout's dimensions are not specified, let the new row change the measurements
        // This is not perfect since we not covering all rows but still solves an important case
        // where they may have a header row which should be laid out according to children.
        if (flexibleInOtherDir) {
            updateMeasurements(); //  reset measurements
        }
        final boolean layingOutInPrimaryDirection =
                layoutState.mItemDirection == LayoutState.ITEM_DIRECTION_TAIL;
        int count = 0;
        int consumedSpanCount = 0;
        int chunkCount = mSpanCount * orientationPageCount;
        int remainingSpan = (mSpanCount * orientationPageCount);
        if (!layingOutInPrimaryDirection) {
            int itemSpanIndex = getSpanIndex(recycler, state, layoutState.mCurrentPosition);
            int itemSpanSize = getSpanSize(recycler, state, layoutState.mCurrentPosition);
            remainingSpan = itemSpanIndex + itemSpanSize;
        }
        while (count < chunkCount && layoutState.hasMore(state) && remainingSpan > 0) {
            int pos = layoutState.mCurrentPosition;
            final int spanSize = getSpanSize(recycler, state, pos);
            if (spanSize > chunkCount) {
                throw new IllegalArgumentException("Item at position " + pos + " requires "
                        + spanSize + " spans but GridLayoutManager has only " + chunkCount
                        + " spans.");
            }
            remainingSpan -= spanSize;
            if (remainingSpan < 0) {
                break; // item did not fit into this row or column
            }
            View view = layoutState.next(recycler);
            if (view == null) {
                break;
            }
            consumedSpanCount += spanSize;
            mSet[count] = view;
            count++;
        }

        if (count == 0) {
            result.mFinished = true;
            return;
        }

        int maxSize = 0;
        float maxSizeInOther = 0; // use a float to get size per span

        // we should assign spans before item decor offsets are calculated
        assignSpans(recycler, state, count, layingOutInPrimaryDirection);
        for (int i = 0; i < count; i++) {
            View view = mSet[i];
            if (layoutState.mScrapList == null) {
                if (layingOutInPrimaryDirection) {
                    addView(view);
                } else {
                    addView(view, 0);
                }
            } else {
                if (layingOutInPrimaryDirection) {
                    addDisappearingView(view);
                } else {
                    addDisappearingView(view, 0);
                }
            }
            calculateItemDecorationsForChild(view, mDecorInsets);

            measureChild(view, otherDirSpecMode, false);
            final int size = mOrientationHelper.getDecoratedMeasurement(view);
            if (size > maxSize) {
                maxSize = size;
            }
            final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            final float otherSize = 1f * mOrientationHelper.getDecoratedMeasurementInOther(view)
                    / lp.mSpanSize;
            if (otherSize > maxSizeInOther) {
                maxSizeInOther = otherSize;
            }
        }
        if (flexibleInOtherDir) {
            // re-distribute columns
            guessMeasurement(maxSizeInOther, currentOtherDirSize);
            // now we should re-measure any item that was match parent.
            maxSize = 0;
            for (int i = 0; i < count; i++) {
                View view = mSet[i];
                measureChild(view, View.MeasureSpec.EXACTLY, true);
                final int size = mOrientationHelper.getDecoratedMeasurement(view);
                if (size > maxSize) {
                    maxSize = size;
                }
            }
        }

        // Views that did not measure the maxSize has to be re-measured
        // We will stop doing this once we introduce Gravity in the GLM layout params
        for (int i = 0; i < count; i++) {
            final View view = mSet[i];
            if (mOrientationHelper.getDecoratedMeasurement(view) != maxSize) {
                final GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                final Rect decorInsets = lp.mDecorInsets;
                final int verticalInsets = decorInsets.top + decorInsets.bottom
                        + lp.topMargin + lp.bottomMargin;
                final int horizontalInsets = decorInsets.left + decorInsets.right
                        + lp.leftMargin + lp.rightMargin;
                final int totalSpaceInOther = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
                final int wSpec;
                final int hSpec;
                if (mOrientation == VERTICAL) {
                    wSpec = getChildMeasureSpec(totalSpaceInOther, View.MeasureSpec.EXACTLY,
                            horizontalInsets, lp.width, false);
                    hSpec = View.MeasureSpec.makeMeasureSpec(maxSize - verticalInsets,
                            View.MeasureSpec.EXACTLY);
                } else {
                    wSpec = View.MeasureSpec.makeMeasureSpec(maxSize - horizontalInsets,
                            View.MeasureSpec.EXACTLY);
                    hSpec = getChildMeasureSpec(totalSpaceInOther, View.MeasureSpec.EXACTLY,
                            verticalInsets, lp.height, false);
                }
                measureChildWithDecorationsAndMargin(view, wSpec, hSpec, true);
            }
        }

        result.mConsumed = maxSize;

        int left = 0, right = 0, top = 0, bottom = 0;
        if (mOrientation == VERTICAL) {
            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                bottom = layoutState.mOffset;
                top = bottom - maxSize;
            } else {
                top = layoutState.mOffset;
                bottom = top + maxSize;
            }
        } else {
            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                right = layoutState.mOffset;
                left = right - maxSize;
            } else {
                left = layoutState.mOffset;
                right = left + maxSize;
            }
        }
        for (int i = 0; i < count; i++) {
            View view = mSet[i];
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            if (mOrientation == VERTICAL) {
                if (isLayoutRTL()) {
                    right = getPaddingLeft() + mCachedBorders[chunkCount - params.mSpanIndex];
                    left = right - mOrientationHelper.getDecoratedMeasurementInOther(view);
                } else {
                    left = getPaddingLeft() + mCachedBorders[params.mSpanIndex];
                    right = left + mOrientationHelper.getDecoratedMeasurementInOther(view);
                }
            } else {
                top = getPaddingTop() + mCachedBorders[params.mSpanIndex];
                bottom = top + mOrientationHelper.getDecoratedMeasurementInOther(view);
            }
            // We calculate everything with View's bounding box (which includes decor and margins)
            // To calculate correct layout position, we subtract margins.
            layoutDecoratedWithMargins(view, left, top, right, bottom);
            if (DEBUG) {
                Log.d(TAG, "laid out child at position " + getPosition(view) + ", with l:"
                        + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                        + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin)
                        + ", span:" + params.mSpanIndex + ", spanSize:" + params.mSpanSize);
            }
            // Consume the available space if the view is not removed OR changed
            if (params.isItemRemoved() || params.isItemChanged()) {
                result.mIgnoreConsumed = true;
            }
            result.mFocusable |= view.hasFocusable();
        }
        Arrays.fill(mSet, null);
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.isPreLayout()) {
            return mSpanSizeLookup.getCachedSpanIndex(pos, mSpanCount);
        }
        final int cached = mPreLayoutSpanIndexCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        final int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition == -1) {
            if (DEBUG) {
                throw new RuntimeException("Cannot find span index for pre layout position. It is"
                        + " not cached, not in the adapter. Pos:" + pos);
            }
            Log.w(TAG, "Cannot find span size for pre layout position. It is"
                    + " not cached, not in the adapter. Pos:" + pos);
            return 0;
        }
        return mSpanSizeLookup.getCachedSpanIndex(adapterPosition, mSpanCount);
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int pos) {
        if (!state.isPreLayout()) {
            return mSpanSizeLookup.getSpanSize(pos);
        }
        final int cached = mPreLayoutSpanSizeCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        final int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition == -1) {
            if (DEBUG) {
                throw new RuntimeException("Cannot find span size for pre layout position. It is"
                        + " not cached, not in the adapter. Pos:" + pos);
            }
            Log.w(TAG, "Cannot find span size for pre layout position. It is"
                    + " not cached, not in the adapter. Pos:" + pos);
            return 1;
        }
        return mSpanSizeLookup.getSpanSize(adapterPosition);
    }

    private void assignSpans(RecyclerView.Recycler recycler, RecyclerView.State state, int count,
                             boolean layingOutInPrimaryDirection) {
        // spans are always assigned from 0 to N no matter if it is RTL or not.
        // RTL is used only when positioning the view.
        int span, start, end, diff;
        // make sure we traverse from min position to max position
        if (layingOutInPrimaryDirection) {
            start = 0;
            end = count;
            diff = 1;
        } else {
            start = count - 1;
            end = -1;
            diff = -1;
        }
        span = 0;
        for (int i = start; i != end; i += diff) {
            View view = mSet[i];
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            params.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            params.mSpanIndex = span;
            span += params.mSpanSize;
        }
    }

    /**
     * This is called after laying out a row (if vertical) or a column (if horizontal) when the
     * RecyclerView does not have exact measurement specs.
     * <p>
     * Here we try to assign a best guess width or height and re-do the layout to update other
     * views that wanted to MATCH_PARENT in the non-scroll orientation.
     *
     * @param maxSizeInOther The maximum size per span ratio from the measurement of the children.
     * @param currentOtherDirSize The size before this layout chunk. There is no reason to go below.
     */
    private void guessMeasurement(float maxSizeInOther, int currentOtherDirSize) {
        final int contentSize = Math.round(maxSizeInOther * mSpanCount);
        // always re-calculate because borders were stretched during the fill
        calculateItemBorders(Math.max(contentSize, currentOtherDirSize));
    }

    /**
     * @param totalSpace Total available space after padding is removed
     */
    private void calculateItemBorders(int totalSpace) {
        mCachedBorders = calculateItemBorders(mCachedBorders, mSpanCount, totalSpace);
    }

    private void updateMeasurements() {
        int totalSpace;
        if (getOrientation() == VERTICAL) {
            totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
        } else {
            totalSpace = getHeight() - getPaddingBottom() - getPaddingTop();
        }
        calculateItemBorders(totalSpace);
    }

    /**
     * Measures a child with currently known information. This is not necessarily the child's final
     * measurement. (see fillChunk for details).
     *
     * @param view The child view to be measured
     * @param otherDirParentSpecMode The RV measure spec that should be used in the secondary
     *                               orientation
     * @param alreadyMeasured True if we've already measured this view once
     */
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
        if (mOrientation == VERTICAL) {
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


}
