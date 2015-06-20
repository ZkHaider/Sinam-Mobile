package com.sinamproject.ui;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.gson.Gson;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.sinamproject.R;
import com.sinamproject.bus.BusProvider;
import com.sinamproject.data.Document;
import com.sinamproject.events.UploadFormEvent;
import com.sinamproject.ui.rippledrawable.RippleDrawable;
import com.sinamproject.utils.DPToPixel;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

/**
 * Created by ZkHaider on 4/19/15.
 */
public class DocumentDetailActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    public static final String TAG = DocumentDetailActivity.class.getSimpleName();

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    private View mToolbar;
    private ImageView mDocumentImage;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mDocumentTitle;
    private TextView mDocumentDescription;
    private FloatingActionButton mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mVibrantColor;
    private int mDarkVibrantColor;
    private boolean mFabIsShown;
    private RelativeLayout mRelativeLayout;

    private String mTitle;
    private String mDescription;

    private Bus mBus = BusProvider.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        initBundle();
        initActionBar();
        initView();
        initDocumentImage();
        initDocumentText();
        initDimens();

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, 0);
                onScrollChanged(0, false, false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        mVibrantColor = bundle.getInt("color");
        mDarkVibrantColor = bundle.getInt("darkcolor");
        mTitle = bundle.getString("title");
        mDescription = bundle.getString("description");
    }

    private void initActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        mToolbar = findViewById(R.id.toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.customDocumentInfoBox);
        mRelativeLayout.setBackgroundColor(mVibrantColor);
        mRelativeLayout.setClickable(true);
        RippleDrawable.makeFor(mRelativeLayout, getResources().getColorStateList(R.color.ripple_drawable_view));
        mDocumentDescription = (TextView) findViewById(R.id.customDocumentDetailDescription);
        mOverlayView = findViewById(R.id.overlay);
        mOverlayView.setBackgroundColor(mVibrantColor);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mDocumentTitle = (TextView) findViewById(R.id.customDocumentDetailTitle2);
        mDocumentImage = (ImageView) findViewById(R.id.customDocumentDetailImage);
        setTitle(null);
        mFab = (FloatingActionButton) findViewById(R.id.documentEditButton);
        mFab.setColorNormal(mDarkVibrantColor);
        ViewHelper.setScaleX(mFab, 1);
        ViewHelper.setScaleY(mFab, 1);
        mFabIsShown = true;
    }

    private void initDocumentImage() {

        String randomImageUrl = "http://lorempixel.com/400/200";

        Picasso.with(this)
                .load(randomImageUrl)
                .fit()
                .centerCrop()
                .into(mDocumentImage);
    }

    private void initDocumentText() {
        mDocumentTitle.setText(mTitle);
        mDocumentDescription.setText(mDescription);
    }

    private void initDimens() {
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize - mRelativeLayout.getHeight();
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mDocumentImage, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        ViewHelper.setAlpha(mRelativeLayout, ScrollUtils.getFloat(flexibleRange / (flexibleRange + scrollY), 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mDocumentTitle, 0);
        ViewHelper.setPivotY(mDocumentTitle, 0);
        ViewHelper.setScaleX(mDocumentTitle, scale);
        ViewHelper.setScaleY(mDocumentTitle, scale);

        // Translate Title text
        ImageButton calendarButton = (ImageButton) findViewById(R.id.documentDetailIcon);
        int yOffset = calendarButton.getTop() + 14;
        int layoutOffSet = mRelativeLayout.getHeight();
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mDocumentTitle.getHeight() * scale) - yOffset;
        int titleTranslationY = maxTitleTranslationY - scrollY + layoutOffSet;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }

        ViewHelper.setTranslationY(mDocumentTitle, titleTranslationY);
        ViewHelper.setTranslationX(mDocumentTitle, DPToPixel.dpToPx(this, 120));

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationY(mFab, fabTranslationY);
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mVibrantColor));
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mVibrantColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void initFabButtonClickListener() {
        // Setup listener for joining unjoining
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBus.post(new UploadFormEvent());
            }
        });
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }


    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    private void colorToDone() {
        Integer colorFrom = getResources().getColor(mDarkVibrantColor);
        Integer colorTo = getResources().getColor(R.color.colorAccent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFab.setColorNormal((Integer) animation.getAnimatedValue());
            }
        });
        mFab.setIcon(R.drawable.ic_done);
        colorAnimation.start();
    }

    private void colorToSync() {
        Integer colorFrom = getResources().getColor(R.color.colorAccent);
        Integer colorTo = getResources().getColor(mDarkVibrantColor);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFab.setColorNormal((Integer) animation.getAnimatedValue());
            }
        });
        mFab.setIcon(R.drawable.ic_cloud_sync);
        colorAnimation.start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
