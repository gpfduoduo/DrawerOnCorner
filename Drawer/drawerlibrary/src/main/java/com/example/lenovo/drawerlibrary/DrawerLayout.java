package com.example.lenovo.drawerlibrary;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * Created by 10129302 郭攀峰 on 15-5-19.
 */
public class DrawerLayout extends RelativeLayout
{

    private static final String tag = DrawerLayout.class.getSimpleName();

    /**
     * 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
     */
    public static final int SNAP_VELOCITY = 200;

    private final static int DEFAULT_DURATION = 250;

    private final static int MAX_CLICK_TIME = 300;
    private final static float MAX_CLICK_DISTANCE = 5;

    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int touchSlop;

    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown;

    /**
     * 记录手指按下时的纵坐标。
     */
    private float yDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float yMove;

    private MarginLayoutParams drawerLayoutParams;
    private int mDrawerLayoutHandlerId;
    private View mDrawerLayoutHandler;
    private int mDrawerLayoutContentId;
    private View mDrawerLayoutContent;
    private int mDrawerLayoutId = -1;
    private View mDrawer;

    private boolean isDragging;

    private boolean isTouchingDrawer = false;
    private boolean isShowing = false;
    private long mPressStartTime;

    private boolean isFirstInit = false;

    private int initialState = State.Close;

    private DrawerListener mDrawerListener;

    public static interface State
    {
        public static final int Open = 0;
        public static final int Close = 1;
    }

    public interface DrawerListener
    {
        public void drawerOpened();

        public void drawerClosed();
    }

    public DrawerLayout(Context context)
    {
        super(context);
        init(context, null);
    }

    public DrawerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setInitialState(int state)
    {
        initialState = state;
    }

    public void setDrawerListener(DrawerListener listener)
    {
        this.mDrawerListener = listener;
    }

    /**
     * 判断是打开还是关闭
     * 
     * @return
     */
    public boolean isOpened()
    {
        return isShowing;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        if (!isFirstInit)
        {
            for (int i = 0; i < getChildCount(); i++)
            {
                View view = getChildAt(i);
                if (view.equals(mDrawer))
                {
                    Log.d(tag, "onLayout");
                    drawerLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                    if (initialState == State.Close)
                    {
                        drawerLayoutParams.bottomMargin = -mDrawerLayoutContent
                                .getMeasuredHeight();
                        isShowing = false;
                    }
                    else
                    {
                        drawerLayoutParams.bottomMargin = 0;
                        isShowing = true;
                    }

                    view.setLayoutParams(drawerLayoutParams);
                    break;
                }
            }
            isFirstInit = true;
        }
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mDrawerLayoutHandler = findViewById(mDrawerLayoutHandlerId);
        mDrawerLayoutContent = findViewById(mDrawerLayoutContentId);
        mDrawer = findViewById(mDrawerLayoutId);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        boolean isConsumed = false;
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN :
                isConsumed = processDown(event);
                break;
            case MotionEvent.ACTION_MOVE :
                processMove(event);
                break;
            case MotionEvent.ACTION_UP :
                processUp(event);
                break;
        }

        return isConsumed || super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return isDragging;
    }

    private void init(Context context, AttributeSet attrs)
    {
        if (attrs != null)
        {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.DrawerLayout);
            mDrawerLayoutId = ta.getResourceId(R.styleable.DrawerLayout_DrawerLayout_ID,
                -1);
            mDrawerLayoutHandlerId = ta.getResourceId(
                R.styleable.DrawerLayout_DrawerLayoutHandler_ID, -1);
            mDrawerLayoutContentId = ta.getResourceId(
                R.styleable.DrawerLayout_DrawerLayoutContent_ID, -1);

            if (mDrawerLayoutContentId == -1 || mDrawerLayoutHandlerId == -1
                || mDrawerLayoutId == -1)
            {
                throw new IllegalArgumentException("必须指定属性值");
            }
            ta.recycle();
        }
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private boolean isViewHit(View view, int x, int y)
    {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);

        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;

        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth()
            && screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    private boolean processDown(MotionEvent event)
    {
        xDown = event.getX();
        yDown = event.getY();

        if (!isShowing && isViewHit(mDrawer, (int) xDown, (int) yDown))
        {
            isTouchingDrawer = true;
            return true;
        }

        //Drawer已经打开,
        if (isShowing)
        {
            //点击其他位置，关闭Drawer
            if (!isViewHit(mDrawer, (int) xDown, (int) yDown))
            {
                closeDrawer();
            }
            else
            {
                isTouchingDrawer = true;
            }
        }

        return false;
    }

    private void processMove(MotionEvent event)
    {
        if (!isTouchingDrawer)
        {
            return;
        }

        mPressStartTime = System.currentTimeMillis();

        yMove = event.getY();
        // 手指移动时，对比按下时的坐标，计算出移动的距离。
        int moveDistanceY = (int) (yMove - yDown);

        if (!isDragging && Math.abs(moveDistanceY) > touchSlop)
        {
            isDragging = true;
        }

        if (isDragging)
        {
            yDown = yMove;
            drawerLayoutParams.bottomMargin -= moveDistanceY;

            if (drawerLayoutParams.bottomMargin >= 0)
            {
                drawerLayoutParams.bottomMargin = 0;
                isShowing = true;
                mDrawerListener.drawerOpened();
            }

            if (drawerLayoutParams.bottomMargin < -mDrawerLayoutContent
                    .getMeasuredHeight())
            {
                drawerLayoutParams.bottomMargin = -mDrawerLayoutContent
                        .getMeasuredHeight();
                isShowing = false;
                mDrawerListener.drawerClosed();
            }

            mDrawer.setLayoutParams(drawerLayoutParams);
        }
    }

    private void processUp(MotionEvent event)
    {
        Log.d(tag, "process up isShowing = " + isShowing);
        if (!isTouchingDrawer)
        {
            return;
        }
        //long pressDuration = System.currentTimeMillis() - mPressStartTime;

        isTouchingDrawer = false;

        //相当于点击打开
        if (!isShowing /* && pressDuration < MAX_CLICK_TIME */
            && distance(xDown, yDown, event.getX(), event.getY()) < MAX_CLICK_DISTANCE)
        {
            openDrawer();
            return;
        }

        //相当于点击关闭
        if (isShowing
            && isViewHit(mDrawerLayoutHandler, (int) event.getX(), (int) event.getY())
            /* && pressDuration < MAX_CLICK_TIME */
            && distance(xDown, yDown, event.getX(), event.getY()) < MAX_CLICK_DISTANCE)
        {
            closeDrawer();
            return;
        }

        //无论打开还是关闭只要过半就关闭，反之打开
        if (drawerLayoutParams.bottomMargin <= -(mDrawerLayoutContent.getMeasuredHeight()) / 2)
        {
            isShowing = true;
            closeDrawer();
        }
        else
        {
            isShowing = false;
            openDrawer();
        }

        isDragging = false;
    }

    public void openDrawer()
    {
        Log.d(tag, "openDrawer");

        if (isShowing)
            return;

        ValueAnimator openAnimator = ValueAnimator.ofFloat(
            drawerLayoutParams.bottomMargin, 0).setDuration(DEFAULT_DURATION);
        openAnimator.setTarget(mDrawer);
        openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                float value = (float) valueAnimator.getAnimatedValue();
                drawerLayoutParams.bottomMargin = (int) value;
                mDrawer.setLayoutParams(drawerLayoutParams);
            }
        });
        openAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isShowing = true;
                Log.d(tag, "open drawer end isShowing = " + isShowing);
                mDrawerListener.drawerOpened();
            }
        });
        openAnimator.setInterpolator(new LinearInterpolator());
        openAnimator.start();
    }

    public void closeDrawer()
    {
        Log.d(tag, "closeDrawer function");

        if (!isShowing)
            return;

        ValueAnimator closeAnimator = ValueAnimator.ofFloat(
            drawerLayoutParams.bottomMargin, -mDrawerLayoutContent.getMeasuredHeight())
                .setDuration(DEFAULT_DURATION);
        closeAnimator.setTarget(mDrawer);
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                float value = (float) valueAnimator.getAnimatedValue();
                drawerLayoutParams.bottomMargin = (int) value;
                mDrawer.setLayoutParams(drawerLayoutParams);
            }
        });
        closeAnimator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isShowing = false;
                Log.d(tag, "close drawer end isShowing = " + isShowing);
                mDrawerListener.drawerClosed();
            }
        });
        closeAnimator.setInterpolator(new LinearInterpolator());
        closeAnimator.start();
    }

    private double distance(float x1, float y1, float x2, float y2)
    {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

}
