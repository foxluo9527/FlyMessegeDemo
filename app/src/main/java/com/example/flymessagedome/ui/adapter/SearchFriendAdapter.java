package com.example.flymessagedome.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.bean.FriendsBean;
import com.example.flymessagedome.bean.UserBean;
import com.example.flymessagedome.utils.TextSpanUtil;
import com.example.flymessagedome.view.CircleImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFriendAdapter extends RecyclerView.Adapter {
    ArrayList<FriendsBean> friendsBeans;
    private LayoutInflater mLayoutInflater;
    Context context;
    private OnRecyclerViewItemClickListener listener;
    private HttpProxyCacheServer proxyCacheServer;
    String searchString="";
    public SearchFriendAdapter(ArrayList<FriendsBean> friendsBeans, Context context) {
        this.friendsBeans = friendsBeans;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
        proxyCacheServer= FlyMessageApplication.getProxy(context);
    }
    public void setkeyString(String searchString){
        this.searchString=searchString;
    }
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        listener=onRecyclerViewItemClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchFriendViewHolder(mLayoutInflater.inflate(R.layout.search_friend_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchFriendViewHolder viewHolder=(SearchFriendViewHolder)holder;
        FriendsBean friendsBean=friendsBeans.get(position);
        UserBean userBean=friendsBean.getFriendUser();
        Glide.with(context).load(proxyCacheServer.getProxyUrl(userBean.getU_head_img()))
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(viewHolder.headImg);
        if (!TextUtils.isEmpty(friendsBean.getF_remarks_name())){
            viewHolder.name.setText(TextSpanUtil.getInstant().setColor(friendsBean.getF_remarks_name()+"("+userBean.getU_nick_name()+")",
                    searchString,context.getApplicationContext().getColor(R.color.blue_1)));
        }else {
            viewHolder.name.setText(TextSpanUtil.getInstant().setColor(friendsBean.getF_remarks_name(),
                    searchString,context.getApplicationContext().getColor(R.color.blue_1)));
        }
        viewHolder.u_name.setText(TextSpanUtil.getInstant().setColor(userBean.getU_name(),
                searchString,context.getApplicationContext().getColor(R.color.blue_1)));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsBeans.size();
    }
    static class SearchFriendViewHolder extends RecyclerView.ViewHolder{
        @Nullable
        @BindView(R.id.user_head_img)
        CircleImageView headImg;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.u_name)
        TextView u_name;
        public SearchFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
