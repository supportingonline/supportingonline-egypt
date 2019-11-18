package com.egyptrefaat.supporting.supportingonline.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.egyptrefaat.supporting.supportingonline.Calls.OnPress;
import com.egyptrefaat.supporting.supportingonline.Custom.URLImageParser;
import com.egyptrefaat.supporting.supportingonline.Models.CommentModel;
import com.egyptrefaat.supporting.supportingonline.R;

import org.xml.sax.XMLReader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private ArrayList<CommentModel> arrayList;
    private Context context;
    private OnPress onPressImage;

    public CommentAdapter(ArrayList<CommentModel> arrayList, Context context, OnPress onPressImage) {
        this.arrayList = arrayList;
        this.onPressImage = onPressImage;
        this.context=context;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_comment,parent,false);
        CommentHolder vh=new CommentHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        CommentModel model=arrayList.get(position);

        // image
        Glide.with(context).load(model.getImage()).error(context.getResources().getDrawable(R.drawable.ic_user_profile))
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPressImage.onClick(v,position);
            }
        });

        //name
        holder.name.setText(model.getUser_name());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPressImage.onClick(v,position);
            }
        });

        //date
        holder.date.setText(model.getDate());

        // text
        holder.text.setText(Html.fromHtml(model.getDetails()));

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    protected class CommentHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView name,date,text;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            imageView=(CircleImageView)itemView.findViewById(R.id.r_comment_improfile);
            name=(TextView)itemView.findViewById(R.id.r_comment_name);
            date=(TextView)itemView.findViewById(R.id.r_comment_date);
            text=(TextView)itemView.findViewById(R.id.r_comment_text);

        }
    }
}
