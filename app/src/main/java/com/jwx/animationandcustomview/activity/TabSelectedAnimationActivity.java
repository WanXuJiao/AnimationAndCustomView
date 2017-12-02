package com.jwx.animationandcustomview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.jwx.animationandcustomview.R;
import com.jwx.animationandcustomview.adapter.ChangeColorPagerAdapter;
import com.jwx.animationandcustomview.fragment.BlueFragment;
import com.jwx.animationandcustomview.fragment.GreenFragment;
import com.jwx.animationandcustomview.fragment.OrangeFragment;
import com.jwx.animationandcustomview.util.DisplayUtil;
import com.jwx.animationandcustomview.widget.tablayout.FixedLengthIndicatorTabLayout;
import com.jwx.animationandcustomview.widget.tablayout.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwx on 2017/12/2
 */

public class TabSelectedAnimationActivity extends AppCompatActivity {

    private static final int VIEW_PAGER_DEFAULT_PAGE_NUMBER = 5;//ViewPager默认加载页数
    private static final int VIEW_PAGE_SCROLL_ANIMATION_TIME = 350;//ViewPager页面滚动动画执行时间

    private ViewPager viewPager;
    private FixedLengthIndicatorTabLayout mTabLayoutOrange;
    private FixedLengthIndicatorTabLayout mTabLayoutBlue;
    private FixedLengthIndicatorTabLayout mTabLayoutGreen;
    private FrameLayout fl_controller;
    private ChangeColorPagerAdapter mAdapter;
    private View transparent_view;
    private boolean isFirstStartAnimator = true;
    private float lastPositionOffset;
    private boolean isDragging;
    private int lastPosition;
    private boolean isLeftScroll;
    private boolean isRightScroll;
    private int cha; //ViewPager页面差值
    private boolean isPageEqual;
    private int tempPosition;
    private boolean isPageChanged;
    private boolean isFirstIn = true;
    private boolean isRight;
    private int scrollCha;
    private List<String> tagList;
    private boolean isTabChanged; //是否由后台控制变化了tab

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_select);
        initView();
    }

    private void initView() {
        List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        titleList.add("小黄");
        titleList.add("小绿");
        titleList.add("小蓝");
        fragmentList.add(new OrangeFragment());
        fragmentList.add(new GreenFragment());
        fragmentList.add(new BlueFragment());
        viewPager = findViewById(R.id.compatible_viewpager);
        mTabLayoutOrange = findViewById(R.id.tablayout_orange);
        mTabLayoutBlue = findViewById(R.id.tablayout_blue);
        mTabLayoutGreen = findViewById(R.id.tablayout_green);
        transparent_view = findViewById(R.id.transparent_view);
        fl_controller  = findViewById(R.id.fl_controller);
        mAdapter = new ChangeColorPagerAdapter(this.getSupportFragmentManager(),titleList,fragmentList);
        viewPager.setOffscreenPageLimit(VIEW_PAGER_DEFAULT_PAGE_NUMBER);
        viewPager.setAdapter(mAdapter);
        mTabLayoutOrange.setupWithViewPager(viewPager);
        mTabLayoutBlue.setupWithViewPager(viewPager);
        mTabLayoutGreen.setupWithViewPager(viewPager);
        initListener();
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("jwx", "onPageScrolled:       position=" + position + ";positionOffset=" + positionOffset + ";positionOffsetPixels=" + positionOffsetPixels);
                if (position == 0 && positionOffset == 0 && positionOffsetPixels == 0) {
                    //当初始化页面为0页时，不走onPageSelect方法，为确保第一次滑动或滚动执行动画，添加此逻辑
                    isFirstIn = false;
                }
                //当页面值变化时，允许切换滑动方向对应动画,同时避免因页面切换
                // 出现的position变化进入逻辑（在最后），增加判断条件positionOffset!=0
                if (lastPosition != position) {
                    lastPosition = position;
                    isFirstStartAnimator = true;
                    isRight = false;
                } else {
                    isRight = true;
                }
                float tempPositionOffset = positionOffset;
                if (tempPositionOffset == 0 && lastPositionOffset > 0.5) {
                    tempPositionOffset = 1;
                }
                if (isFirstStartAnimator && tempPositionOffset != 1) {
                    //当首次开启页面移动值时，判断左右运动方向
                    //当滑动中方向改变时，为改变后的运动提供方向支持
                    if (positionOffsetPixels < 500) {//左滑
                        isLeftScroll = true;
                        isRightScroll = false;
                    } else {
                        isLeftScroll = false;
                        isRightScroll = true;
                    }
                }
                if (isDragging) {
                    if (isLeftScroll) {//左滑
                        if (!isRightScroll) {//非右滑状态下的左滑，执行以下逻辑
                            viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                            if (position < viewPager.getChildCount() - 1) {
                                viewPager.getChildAt(position + 1).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                            }
                        } else { //右滑状态下的左滑
                            viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset - viewPager.getWidth());
                            if (position < viewPager.getChildCount() - 1) {
                                viewPager.getChildAt(position + 1).setTranslationX(viewPager.getWidth() * tempPositionOffset - viewPager.getWidth());
                            }
                        }
                    } else {//右滑
                        if (isRightScroll) {//非左滑状态下的右滑，执行以下逻辑
                            viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset - viewPager.getWidth());
                            if (position < viewPager.getChildCount() - 1) {
                                viewPager.getChildAt(position + 1).setTranslationX(-viewPager.getWidth() * (1 - tempPositionOffset));
                            }
                        } else {//左滑状态下的右滑
                            viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                            if (position < viewPager.getChildCount() - 1) {
                                viewPager.getChildAt(position + 1).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                            }
                        }
                    }
                } else if (cha == 2 && !isTabChanged) {
                    if (scrollCha == 2) {//向左滚动两页
                        //viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset+2*viewPager.getWidth());
                        viewPager.getChildAt(0).setTranslationX(viewPager.getWidth() * tempPositionOffset + viewPager.getWidth());
                        viewPager.getChildAt(1).setVisibility(View.GONE);
                    } else {//向右滚动两页
                        viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset - viewPager.getWidth());
                        viewPager.getChildAt(2).setTranslationX(viewPager.getWidth() * tempPositionOffset - 2 * viewPager.getWidth());
                        viewPager.getChildAt(1).setVisibility(View.GONE);
                    }
                } else if (cha == 1 && !isTabChanged) {
                    if (scrollCha == 1) {//向左滚动一页
                        viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                        if (position < viewPager.getChildCount() - 1) {
                            viewPager.getChildAt(position + 1).setTranslationX(viewPager.getWidth() * tempPositionOffset);
                        }
                    } else {//向右滚动一页
                        viewPager.getChildAt(position).setTranslationX(viewPager.getWidth() * tempPositionOffset - viewPager.getWidth());
                        //viewPager.getChildAt(position + 1).setTranslationX(viewPager.getWidth() * positionOffset - viewPager.getWidth());
                        if (position < viewPager.getChildCount() - 1) {
                            viewPager.getChildAt(position + 1).setTranslationX(-viewPager.getWidth() * (1 - tempPositionOffset));
                        }
                    }
                }
                lastPositionOffset = tempPositionOffset;
                isFirstStartAnimator = false;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageSelected(final int position) {
//                Log.e("jwx", "onPageSelected:   position=" + position);
                cha = Math.abs(position - tempPosition);
                scrollCha = position - tempPosition;
                if (cha == 2) {
                    viewPager.getChildAt(position).setVisibility(View.INVISIBLE);
                }
                if (isFirstIn) {
                    isFirstIn = false;
                } else {
                    isTabChanged = false;//当非第一次进入导致的滚动时，也就是用户手动操作的滚动，置为false
                    isPageChanged = true;
                    transparent_view.setVisibility(View.VISIBLE);
                }
                if (lastPosition == position) {
                    isPageEqual = true;
                }
                if (!isDragging) {
                    if (cha == 1) { //跳一页
                        if (position > lastPosition) {
                            viewPager.getChildAt(lastPosition).setTranslationX(viewPager.getWidth());
                        }
                    }
                }
                tempPosition = position;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private void setTabOrangeAnimator(int cenX, int cenY) {
                fl_controller.bringChildToFront(mTabLayoutOrange);
                mTabLayoutOrange.setVisibility(View.VISIBLE);
                Animator anTabWhite = ViewAnimationUtils.createCircularReveal(mTabLayoutOrange, cenX, mTabLayoutOrange.getHeight(), 0, cenY);
                anTabWhite.setDuration(VIEW_PAGE_SCROLL_ANIMATION_TIME);
                anTabWhite.setInterpolator(new AccelerateInterpolator());
                anTabWhite.start();
                anTabWhite.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTabLayoutBlue.setVisibility(View.INVISIBLE);
                        mTabLayoutGreen.setVisibility(View.INVISIBLE);
                        mTabLayoutOrange.setVisibility(View.VISIBLE);
                        super.onAnimationEnd(animation);
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private void setTabBlueAnimator(int cenX, int cenY) {
                fl_controller.bringChildToFront(mTabLayoutBlue);
                mTabLayoutBlue.setVisibility(View.VISIBLE);
                Animator anTabBlack = ViewAnimationUtils.createCircularReveal(mTabLayoutBlue, cenX, mTabLayoutBlue.getHeight(), 0, cenY);
                anTabBlack.setDuration(VIEW_PAGE_SCROLL_ANIMATION_TIME);
                anTabBlack.setInterpolator(new AccelerateInterpolator());
                anTabBlack.start();
                anTabBlack.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTabLayoutOrange.setVisibility(View.INVISIBLE);
                        mTabLayoutGreen.setVisibility(View.INVISIBLE);
                        mTabLayoutBlue.setVisibility(View.VISIBLE);
                        super.onAnimationEnd(animation);
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private void setTabGreenAnimator(int cenX, int cenY) {
                fl_controller.bringChildToFront(mTabLayoutGreen);
                mTabLayoutGreen.setVisibility(View.VISIBLE);
                Animator anTabBlack = ViewAnimationUtils.createCircularReveal(mTabLayoutGreen, cenX, mTabLayoutGreen.getHeight(), 0, cenY);
                anTabBlack.setDuration(VIEW_PAGE_SCROLL_ANIMATION_TIME);
                anTabBlack.setInterpolator(new AccelerateInterpolator());
                anTabBlack.start();
                anTabBlack.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTabLayoutOrange.setVisibility(View.INVISIBLE);
                        mTabLayoutBlue.setVisibility(View.INVISIBLE);
                        mTabLayoutGreen.setVisibility(View.VISIBLE);
                        super.onAnimationEnd(animation);
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.e("jwx", "onPageScrollStateChanged:   state=" + state);
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    // 用户拖动ViewPager, 取消自动滑动动画
                    isDragging = true;
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    // 滑动结束, 视情况是否重启自动滑动
                    isFirstStartAnimator = true;
                    lastPositionOffset = 0;
                    //处理tablayout隐藏逻辑
                    if (lastPosition == 1&&tempPosition==2) {
                        fl_controller.bringChildToFront(mTabLayoutBlue);
                        mTabLayoutBlue.setVisibility(View.VISIBLE);
                    } else if(lastPosition == 0){
                        fl_controller.bringChildToFront(mTabLayoutOrange);
                        mTabLayoutOrange.setVisibility(View.VISIBLE);
                    } else {
                        fl_controller.bringChildToFront(mTabLayoutGreen);
                        mTabLayoutGreen.setVisibility(View.VISIBLE);
                    }
                    if (isPageChanged) {
                        int cenX = getCenX(lastPosition);
                        int cenY = viewPager.getHeight() + cenX;
                        if (isDragging) {
                            if (isPageEqual) {//右滑
                                viewPager.getChildAt(lastPosition).setTranslationX(0);
                                if (lastPosition < viewPager.getChildCount() - 1) {
                                    viewPager.getChildAt(lastPosition + 1).setTranslationZ(-1);
                                }
                            } else {//左滑
                                viewPager.getChildAt(lastPosition).setTranslationX(0);
                            }
                            Animator an = ViewAnimationUtils.createCircularReveal(viewPager.getChildAt(lastPosition), cenX, 0, 0, cenY);
                            an.setDuration(VIEW_PAGE_SCROLL_ANIMATION_TIME);
                            an.setInterpolator(new AccelerateInterpolator());
                            an.start();
                            if (lastPosition == 0 || lastPosition == 2) { //0切1,2切1
                                if(tempPosition==0){
                                    setTabOrangeAnimator(cenX, cenY);
                                } else {
                                    setTabBlueAnimator(cenX, cenY);
                                }
                            } else if (lastPosition == 1) { //1切0,1切2
                                if(tempPosition == 1){
                                    setTabGreenAnimator(cenX, cenY);
                                }
                            }
                            an.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (viewPager.getChildCount() > 0) {
                                        viewPager.getChildAt(0).setTranslationX(0);
                                        viewPager.getChildAt(0).setTranslationZ(0);
                                        if (viewPager.getChildCount() > 1) {
                                            viewPager.getChildAt(1).setTranslationX(0);
                                            viewPager.getChildAt(1).setTranslationZ(0);
                                            viewPager.getChildAt(1).setVisibility(View.VISIBLE);
                                            if (viewPager.getChildCount() > 2) {
                                                viewPager.getChildAt(2).setTranslationX(0);
                                                viewPager.getChildAt(2).setTranslationZ(0);
                                            }
                                        }
                                    }
                                    transparent_view.setVisibility(View.GONE);
                                    super.onAnimationEnd(animation);
                                }
                            });
                        } else {
                            if (cha == 1) {
                                if (lastPosition == 0 ) { //0切1
                                    setTabOrangeAnimator(cenX, cenY);
                                } else if(lastPosition == 2){//2切1
                                    setTabBlueAnimator(cenX, cenY);
                                }else if (lastPosition == 1) { //1切0,1切2
                                    setTabGreenAnimator(cenX, cenY);
                                }
                                if (scrollCha == 1) {
                                    viewPager.getChildAt(lastPosition).setTranslationX(0);
                                } else {
                                    if (lastPosition < viewPager.getChildCount() - 1) {
                                        viewPager.getChildAt(lastPosition + 1).setTranslationZ(-1);
                                    }
                                    viewPager.getChildAt(lastPosition).setTranslationZ(0);
                                    viewPager.getChildAt(lastPosition).setTranslationX(0);
                                }
                            } else if (cha == 2) {
                                if (lastPosition == 0) {
                                    viewPager.getChildAt(2).setTranslationZ(-1);
                                    viewPager.getChildAt(lastPosition).setTranslationX(0);
                                } else if (lastPosition == 2) {
                                    viewPager.getChildAt(lastPosition).setTranslationX(0);
                                    viewPager.getChildAt(0).setTranslationX(viewPager.getWidth() * 2);
                                }
                                viewPager.getChildAt(lastPosition).setVisibility(View.VISIBLE);
                            }
                            Animator an = ViewAnimationUtils.createCircularReveal(viewPager.getChildAt(lastPosition), cenX, 0, 0, cenY);
                            an.setDuration(VIEW_PAGE_SCROLL_ANIMATION_TIME);
                            an.setInterpolator(new AccelerateInterpolator());
                            an.start();
                            an.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (viewPager.getChildCount() > 0) {
                                        viewPager.getChildAt(0).setTranslationX(0);
                                        viewPager.getChildAt(0).setTranslationZ(0);
                                        if (viewPager.getChildCount() > 1) {
                                            viewPager.getChildAt(1).setTranslationX(0);
                                            viewPager.getChildAt(1).setTranslationZ(0);
                                            viewPager.getChildAt(1).setVisibility(View.VISIBLE);
                                            if (viewPager.getChildCount() > 2) {
                                                viewPager.getChildAt(2).setTranslationX(0);
                                                viewPager.getChildAt(2).setTranslationZ(0);
                                            }
                                        }
                                    }
                                    transparent_view.setVisibility(View.GONE);
                                    super.onAnimationEnd(animation);
                                }
                            });
                        }
                        isDragging = false;
                        isLeftScroll = false;
                        isRightScroll = false;
                        isPageEqual = false;
                        isPageChanged = false;
                    }
                }
            }
        });
    }

    private int getCenX(int lastPosition) {
        int TAB_WIDTH = DisplayUtil.dip2px(this,40);
        int cenX = 0;
        switch (viewPager.getChildCount()) {
            case 2:
                if (lastPosition == 0) {
                    cenX = viewPager.getWidth() / 2 - (viewPager.getWidth() / 2 - TAB_WIDTH) / 2;
                } else {
                    cenX = viewPager.getWidth() / 2 + (viewPager.getWidth() / 2 - TAB_WIDTH) / 2;
                }
                break;
            case 3:
                if (lastPosition == 0) {
                    cenX = viewPager.getWidth() / 3 - (viewPager.getWidth() / 3 - TAB_WIDTH) / 2;
                } else if (lastPosition == 2) {
                    cenX = viewPager.getWidth() * 2 / 3 + (viewPager.getWidth() / 3 - TAB_WIDTH) / 2;
                } else {
                    //当选中tab在中间时，需判断左右
                    if (isRight) {//右滑
                        cenX = viewPager.getWidth() * 2 / 3 - (viewPager.getWidth() / 3 - TAB_WIDTH) / 2;
                    } else {//左滑
                        cenX = viewPager.getWidth() / 3 + (viewPager.getWidth() / 3 - TAB_WIDTH) / 2;
                    }
                }
                break;
            default:
                cenX = viewPager.getWidth() / 2;
                break;
        }
        return cenX;
    }
}
