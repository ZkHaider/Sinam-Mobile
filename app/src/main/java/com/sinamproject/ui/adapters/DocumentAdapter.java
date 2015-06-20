package com.sinamproject.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sinamproject.R;
import com.sinamproject.data.Document;
import com.sinamproject.ui.rippledrawable.RippleDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZkHaider on 4/19/15.
 */
public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    public static final String TAG = DocumentAdapter.class.getSimpleName();

    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private List<Document> mDocuments;
    private int[] mVibrantColors;
    private int vibrantColor;
    private int[] mDarkVibrantColors;
    private int darkVibrantColor;

    public int getVibrantColor(int position) {
        Log.d(TAG, "getVibrantColor(" + String.valueOf(position) + ")");
        return mVibrantColors[position];
    }

    public int getDarkVibrantColor(int position) {
        Log.d(TAG, "getDarkVibrantColor(" + String.valueOf(position) + ")");
        return mDarkVibrantColors[position];
    }

    public void setDocuments(List<Document> documents) {
        mDocuments = documents;
        mVibrantColors = new int[mDocuments.size()];
        mDarkVibrantColors = new int[mDocuments.size()];
    }

    public Document getDocument(int position) {
        return mDocuments.get(position);
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.document_item,
                    viewGroup, false);
            mContext = v.getContext();
            DocumentViewHolder viewItem = new DocumentViewHolder(v, viewType); // Row Item

            return viewItem;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final DocumentViewHolder viewHolder, final int position) {
        if (viewHolder.holderId == 1 && mDocuments != null) {
            // Initialize Row Item Stuff
            Document document = mDocuments.get(position);

            viewHolder.mDocumentTitle.setText(document.getTitle());

            String randomImageUrl = "http://lorempixel.com/400/200";

            Picasso.with(viewHolder.itemView.getContext())
                    .load(randomImageUrl)
                    .fit()
                    .centerCrop()
                    .skipMemoryCache()
                    .into(viewHolder.mDocumentImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) viewHolder.mDocumentImage.getDrawable()).getBitmap();
                            Palette palette = Palette.generate(bitmap);
                            vibrantColor = palette.getVibrantColor(mContext.getResources().getColor(R.color.dark_purple_700));
                            darkVibrantColor = palette.getDarkVibrantColor(mContext.getResources().getColor(R.color.dark_purple_700));
                            mVibrantColors[position] = vibrantColor;
                            mDarkVibrantColors[position] = darkVibrantColor;
                            viewHolder.mRelativeLayout.setBackgroundColor(vibrantColor);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            viewHolder.mDocumentMask.setClickable(true);

            viewHolder.mRelativeLayout.setClickable(true);

            RippleDrawable.makeFor(viewHolder.mDocumentMask, viewHolder.colorBackgroundStateList, true);
            RippleDrawable.makeFor(viewHolder.mRelativeLayout, viewHolder.colorBackgroundStateList, true);

        }
    }

    @Override
    public int getItemCount() {
        return mDocuments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {

        int holderId;

        // For row item
        protected ImageButton mDocumentImage;
        protected TextView mOtherDocumentInfo;
        protected TextView mDocumentTitle;
        protected RelativeLayout mRelativeLayout;
        protected View mDocumentMask;
        private ImageButton[] mDocumentButtons;

        // Color state list
        protected Resources resources;
        protected ColorStateList colorViewStateList;
        protected ColorStateList colorBackgroundStateList;

        public DocumentViewHolder(View v, int viewType) {
            super(v);

            resources = v.getContext().getResources();
            colorViewStateList = resources.getColorStateList(R.color.ripple_drawable_text);
            colorBackgroundStateList = resources.getColorStateList(R.color.ripple_drawable_view);

            if (viewType == TYPE_ITEM) {
                mDocumentImage = (ImageButton) v.findViewById(R.id.documentImage);
                mOtherDocumentInfo = (TextView) v.findViewById(R.id.otherDocumentInfo);
                mDocumentTitle = (TextView) v.findViewById(R.id.documentTitle);
                mRelativeLayout = (RelativeLayout) v.findViewById(R.id.documentInfoBox);
                mDocumentMask = v.findViewById(R.id.documentMask);
                holderId = 1;
            }
        }
    }
}
