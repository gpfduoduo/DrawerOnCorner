package com.example.lenovo.drawerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 郭攀峰 10129302
 *         可以放置在左侧或者右侧的Drawer抽屉
 */
public class Drawer extends LinearLayout {

    private final static int ANIMATION_DURATION = 300;
    private static final int TAG_OPEN = 1;
    private static final int TAG_CLOSE = -1;
    /**
     * 抽屉的位置
     */
    private int mPosition;
    /**
     * 抽屉把手的view的id
     */
    private int mHandleId;
    /**
     * 抽屉内容view的id
     */
    private int mContentId;

    private int mCloseHandleId;

    /**
     * 抽屉中的把手
     */
    private View mHandle;
    /**
     * 打开时的抽屉按钮背景
     */
    private int mOpenedBackground;
    /**
     * 关闭是的抽屉按钮背景
     */
    private int mClosedBackground;
    /**
     * 抽屉的内容
     */
    private View mContent;

    private float mTrackX;
    /**
     * 抽屉的位置
     */
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private int openWidth;
    private int closeWidth;

    private enum State {
        READY, CLICK, END
    }
    /**
     * 抽屉状态
     */
    private State mState;

    /**
     * 抽屉内容View的宽度
     */
    private int mContentWidth;

    private ValueAnimator animWidth, animatorTrackAnimator;

    private ViewWrapper wrapper;

    private AnimatorSet animatorSet = new AnimatorSet();
    private List<Animator> animators = new ArrayList<>();


    public Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Drawer);
        mPosition = a.getInteger(R.styleable.Drawer_drawer_position, LEFT);

        RuntimeException e = null;
        mHandleId = a.getResourceId(R.styleable.Drawer_drawer_handle, -1);
        if (mHandleId == -1) {
            e = new IllegalArgumentException(a.getPositionDescription()
                    + ": 抽屉把手必须制定一个子View");
        }
        mContentId = a.getResourceId(R.styleable.Drawer_drawer_content, -1);
        if (mContentId == -1) {
            e = new IllegalArgumentException(a.getPositionDescription()
                    + ": 抽屉内容必须制定一个子View");
        }
        mCloseHandleId = a.getResourceId(R.styleable.Drawer_drawer_closeHandle, -1);
        mOpenedBackground = a.getResourceId(R.styleable.Drawer_drawer_openedBackground, -1);
        mClosedBackground = a.getResourceId(R.styleable.Drawer_drawer_closedBackground, -1);

        openWidth = a.getDimensionPixelOffset(R.styleable.Drawer_drawer_handle_openWidth,
                dip2px(context, 65));
        closeWidth = a.getDimensionPixelOffset(R.styleable.Drawer_drawer_handle_closeWidth,
                dip2px(context, 30));

        a.recycle(); // 不能忘记

        if (e != null) {
            throw e;
        }

        setOrientation(HORIZONTAL);
        mState = State.READY;

        setBaselineAligned(false);
    }

    /**
     * 设置抽屉的位置
     * @param position
     */
    public void setPosition(int position) {
        mPosition = position;
        setOrientation(HORIZONTAL);

        removeView(mHandle);
        removeView(mContent);

        if (mPosition == LEFT) {
            addView(mContent);
            addView(mHandle);
        } else {
            addView(mHandle);
            addView(mContent);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            String name = getResources().getResourceEntryName(mHandleId);
            throw new RuntimeException("xml中需要添加属性：" + name);
        }
        mHandle.setClickable(true);
        mHandle.setOnClickListener(onClickListener);
        mHandle.setOnTouchListener(onTouchListener);

        View mCloseHandle = findViewById(mCloseHandleId);
        if (mCloseHandle != null) {
            mCloseHandle.setClickable(true);
            mCloseHandle.setOnClickListener(onClickListener);
        }

        mContent = findViewById(mContentId);
        if (mContent == null) {
            String name = getResources().getResourceEntryName(mHandleId);
            throw new RuntimeException("xml中需要添加属性：" + name);
        }

        //重新布局子View
        removeView(mHandle);
        removeView(mContent);
        if (mPosition == LEFT) {
            addView(mContent);
            addView(mHandle);
        } else {
            addView(mHandle);
            addView(mContent);
        }

        if (mOpenedBackground != -1) {
            mHandle.setBackgroundResource(mOpenedBackground);
            mHandle.setTag(TAG_OPEN);
        }

        mContent.setClickable(true);
        mContent.setVisibility(GONE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mContentWidth = mContent.getWidth();

        if (mContentWidth != 0 &&
                mState == State.CLICK &&
                mHandle.getTag().equals(TAG_OPEN)) {
            mHandle.setTag(TAG_CLOSE);
            openOrCloseDrawer(true);
        }
    }

    /**
     * 主要用来绘制子元素
     */
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (mState == State.READY) {
            int delta = mContentWidth;
            if (mPosition == LEFT) {
                delta = -delta;
            }
            canvas.translate(delta, 0);
        } else if (mState == State.CLICK) {
            canvas.translate(mTrackX, 0);
        }
        super.dispatchDraw(canvas);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mHandleId == v.getId()) {
                if (mContent.getVisibility() != VISIBLE) {
                    mState = State.CLICK;
                    mHandle.setTag(TAG_OPEN);
                    mContent.setVisibility(View.VISIBLE);
                } else {
                    if(mCloseHandleId == -1) {
                        mState = State.CLICK;
                        mHandle.setTag(TAG_CLOSE);
                        openOrCloseDrawer(false);
                    }
                }
            }
            if (mCloseHandleId != -1 && v.getId() == mCloseHandleId) {
                mState = State.CLICK;
                mHandle.setTag(TAG_CLOSE);
                openOrCloseDrawer(false);
            }
        }
    };

    private OnTouchListener onTouchListener = new OnTouchListener() {
        int lastX;
        int lastY;
        boolean isPressedDown = false;
        boolean isDragging = false;

        int initialX;
        int initialY;

        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isPressedDown = true;
                    v.postDelayed(new Runnable() {
                        public void run() {
                            if (isPressedDown) {
                                Vibrator vib = (Vibrator) getContext()
                                        .getSystemService(Context.VIBRATOR_SERVICE);
                                vib.vibrate(50);
                                isDragging = true;
                            }
                        }
                    }, 500);

                    initialX = lastX = (int) event.getRawX();
                    initialY = lastY = (int) event.getRawY();
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isDragging) {
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();

                        int deltaX = Math.abs(x - initialX);
                        int deltaY = Math.abs(y - initialY);

                        if ((deltaX > 8 || deltaY > 8) && isPressedDown) {
                            isPressedDown = false;
                        }
                    } else {
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int left = ((LinearLayout) v.getParent()).getLeft() + dx;
                        int top = ((LinearLayout) v.getParent()).getTop() + dy;
                        int right = ((LinearLayout) v.getParent()).getRight() + dx;
                        int bottom = ((LinearLayout) v.getParent()).getBottom() + dy;

                        ((LinearLayout) v.getParent()).layout(left, top, right, bottom); //移动位置

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isPressedDown) {
                        break;
                    }
                    if (!isDragging) {
                        v.performClick();
                    }
                    isDragging = false;
                    isPressedDown = false;
                    break;
            }

            return true;
        }
    };

    private void openOrCloseDrawer(boolean open) {
        if (wrapper == null) {
            wrapper = new ViewWrapper(mHandle);
        }

        animators.clear();
        if (animatorSet.isRunning()) {
            animatorSet.end();
        }

        if (open) {
            if (mPosition == LEFT) {
                animatorTrackAnimator = ValueAnimator.ofInt(-mContentWidth, 0);
            } else if (mPosition == RIGHT) {
                animatorTrackAnimator = ValueAnimator.ofInt(mContentWidth, 0);
            }
            animatorTrackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContent.setVisibility(View.VISIBLE);
                    mHandle.setBackgroundResource(mClosedBackground);
                    mHandle.setTag(TAG_OPEN);
                    mState = State.END;
                }
            });
            animWidth = ObjectAnimator.ofInt(wrapper, "width", openWidth, closeWidth);
        } else {
            if (mPosition == LEFT) {
                animatorTrackAnimator = ValueAnimator.ofInt(0, -mContentWidth);
            } else if (mPosition == RIGHT) {
                animatorTrackAnimator = ValueAnimator.ofInt(0, mContentWidth);
            }
            animatorTrackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContent.setVisibility(View.GONE);
                    mHandle.setBackgroundResource(mOpenedBackground);
                    mHandle.setTag(TAG_CLOSE);
                    mState = State.END;
                }
            });
            animWidth = ObjectAnimator.ofInt(wrapper, "width", closeWidth, openWidth);
        }

        animWidth.setDuration(ANIMATION_DURATION);
        animWidth.setInterpolator(new AccelerateInterpolator());

        animatorTrackAnimator.setDuration(ANIMATION_DURATION);
        animatorTrackAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorTrackAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTrackX = (Integer) animatorTrackAnimator.getAnimatedValue();
                invalidate();
            }
        });

        animators.add(animWidth);
        animators.add(animatorTrackAnimator);

        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    /**
     * 必须实现属性动画的get和set方法
     */
    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
    }

    private int dip2px(Context ctx, float dipValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
