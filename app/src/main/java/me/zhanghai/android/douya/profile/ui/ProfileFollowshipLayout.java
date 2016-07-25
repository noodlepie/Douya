/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.ui.BroadcastListActivity;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileFollowshipLayout extends FriendlyCardView {

    private static final int USER_COUNT_MAX = 5;

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.following_list)
    LinearLayout mFollowingList;
    @BindView(R.id.empty)
    View mEmptyView;
    @BindView(R.id.view_more)
    TextView mViewMoreText;
    @BindView(R.id.follower)
    TextView mFollwerText;

    public ProfileFollowshipLayout(Context context) {
        super(context);

        init();
    }

    public ProfileFollowshipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileFollowshipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_followship_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(final UserInfo userInfo, List<User> followingList) {

        final Context context = getContext();
        OnClickListener viewMoreListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(BroadcastListActivity.makeIntent(userInfo, context));
            }
        };
        mTitleText.setOnClickListener(viewMoreListener);
        mViewMoreText.setOnClickListener(viewMoreListener);

        int i = 0;
        for (final User user : followingList) {

            if (i >= USER_COUNT_MAX) {
                break;
            }

            if (i >= mFollowingList.getChildCount()) {
                LayoutInflater.from(context)
                        .inflate(R.layout.profile_user_item, mFollowingList);
            }
            View userLayout = mFollowingList.getChildAt(i);
            UserLayoutHolder holder = (UserLayoutHolder) userLayout.getTag();
            if (holder == null) {
                holder = new UserLayoutHolder(userLayout);
                userLayout.setTag(holder);
            }

            ImageUtils.loadAvatar(holder.avatarImage, user.avatar, context);
            holder.nameText.setText(user.name);
            userLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(ProfileActivity.makeIntent(user, context));
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mFollowingList, i != 0);
        ViewUtils.setVisibleOrGone(mEmptyView, i == 0);

        for (int count = mFollowingList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mFollowingList.getChildAt(i), false);
        }

        if (userInfo.followingCount > 0) {
            mViewMoreText.setText(context.getString(R.string.view_more_with_count_format,
                    userInfo.followingCount));
        } else {
            mViewMoreText.setVisibility(GONE);
        }

        mFollwerText.setText(context.getString(R.string.profile_follower_count_format,
                userInfo.followerCount));
        mFollwerText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
    }

    static class UserLayoutHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.name)
        public TextView nameText;

        public UserLayoutHolder(View broadcastLayout) {
            ButterKnife.bind(this, broadcastLayout);
        }
    }
}