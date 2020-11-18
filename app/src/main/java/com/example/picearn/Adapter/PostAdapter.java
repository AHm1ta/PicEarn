package com.example.picearn.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.example.picearn.CommentActivity;
import com.example.picearn.Model.Post;
import com.example.picearn.Model.User;
import com.example.picearn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<Post> postList;
    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item_post_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Post post= postList.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.postedImage);

        if (post.getDescription().equals("")){
            holder.postCaption.setVisibility(View.GONE);
        }else{
            holder.postCaption.setVisibility(View.VISIBLE);
            holder.postCaption.setText(post.getDescription());
        }

        PublisherInfo(holder.proPic, (CircleImageView) holder.proPicCmmnt, holder.userNamePost, post.getPublisher());
        isLiked(post.getPostId(),holder.like);
        noLikes(holder.likeCount, post.getPostId());
        getComments(post.getPostId(), holder.addCommnt);
//        holder.proPicCmmnt.setImageResource(images[position]);

        final Drawable drawable = holder.likeAnimation.getDrawable();

        holder.postedImage.setOnClickListener(new DoubleClickListener(500) {
            AnimatedVectorDrawableCompat avd;
            AnimatedVectorDrawable avd2;

            @SuppressLint("NewApi")
            @Override
            public void onDoubleClick() {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);
                }
                holder.likeAnimation.setAlpha(0.70f);
                if(drawable instanceof AnimatedStateListDrawableCompat){
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                }else if(drawable instanceof AnimatedVectorDrawable){
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
            }
        });
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.like.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                                    .child(firebaseUser.getUid()).setValue(true);

                        }else{
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });
        holder.addCommnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("PostId", post.getPostId());
                intent.putExtra("PublisherId", post.getPublisher());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNamePost;
        TextView postCaption;
        TextView addCommnt;
        ImageView postedImage, proPic, proPicCmmnt, likeAnimation;
        ImageView like;
        TextView likeCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            proPicCmmnt = itemView.findViewById(R.id.proPicCmmnt);
            proPic = itemView.findViewById(R.id.proPic);
            like = itemView.findViewById(R.id.like);
            addCommnt = itemView.findViewById(R.id.addCommnt);
            userNamePost = itemView.findViewById(R.id.userIdPost);
            postCaption = itemView.findViewById(R.id.postCaption);
            postedImage = itemView.findViewById(R.id.postedImage);
            likeCount = itemView.findViewById(R.id.likeCount);
            likeAnimation = itemView.findViewById(R.id.likeAnimation);

        }
    }
    private void getComments(String postId, TextView comments){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("See all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLiked(String PostId, ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(PostId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("Liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_anim_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void noLikes(TextView likes, String postId){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+ " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void PublisherInfo(final ImageView imageProfile, final CircleImageView proPicCmmnt, final TextView textView, String Id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(imageProfile);
                Glide.with(context).load(user.getImageurl()).into(proPicCmmnt);
                textView.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });
    }

    public abstract class DoubleClickListener implements View.OnClickListener {

        // The time in which the second tap should be done in order to qualify as
        // a double click
        private static final long DEFAULT_QUALIFICATION_SPAN = 200;
        private long doubleClickQualificationSpanInMillis;
        private long timestampLastClick;

        public DoubleClickListener() {
            doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
            timestampLastClick = 0;
        }

        public DoubleClickListener(long doubleClickQualificationSpanInMillis) {
            this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
            timestampLastClick = 0;
        }

        @Override
        public void onClick(View v) {
            if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
                onDoubleClick();
            }
            timestampLastClick = SystemClock.elapsedRealtime();
        }

        public abstract void onDoubleClick();

    }
}
