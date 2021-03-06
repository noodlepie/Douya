/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
//import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.glide.progress.ProgressListener;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class GalleryAdapter extends PagerAdapter {

    private List<String> mImageList;
    private OnTapListener mOnTapListener;

    public GalleryAdapter(List<String> imageList, OnTapListener onTapListener) {
        mImageList = imageList;
        mOnTapListener = onTapListener;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View layout = ViewUtils.inflate(R.layout.gallery_item, container);
        final ViewHolder holder = new ViewHolder(layout);
        layout.setTag(holder);
        holder.image.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (mOnTapListener != null) {
                    mOnTapListener.onTap();
                }
            }
        });
        ViewUtils.fadeIn(holder.progress);
        ImageUtils.loadImage(holder.image, mImageList.get(position),
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        (e != null ? e : new NullPointerException()).printStackTrace();
                        // FIXME: Don't think this will work.
                        int errorRes = CollectionUtils.firstOrNull(e.getCauses())
                                instanceof SocketTimeoutException ? R.string.gallery_load_timeout
                                : R.string.gallery_load_error;
                        holder.errorText.setText(errorRes);
                        ViewUtils.crossfade(holder.progress, holder.errorText);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        ViewUtils.fadeOut(holder.progress);
                        return false;
                    }
                }, new ProgressListener() {
                    @Override
                    public void onProgress(long bytesRead, long contentLength, boolean done) {
                        int progress = Math.round((float) bytesRead / contentLength
                                * holder.progress.getMax());
                        ProgressBarCompat.setProgress(holder.progress, progress, true);
                    }
                });
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        ViewHolder holder = (ViewHolder) view.getTag();
        GlideApp.with(holder.image).clear(holder.image);
        container.removeView(view);
    }

    public interface OnTapListener {
        void onTap();
    }

    static class ViewHolder {

        @BindView(R.id.image)
        public PhotoView image;
        @BindView(R.id.error)
        public TextView errorText;
        @BindView(R.id.progress)
        public ProgressBar progress;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
