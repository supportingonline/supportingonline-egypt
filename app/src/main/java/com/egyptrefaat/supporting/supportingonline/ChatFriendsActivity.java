package com.egyptrefaat.supporting.supportingonline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.egyptrefaat.supporting.supportingonline.Adapters.ChatHistoryAdapter;
import com.egyptrefaat.supporting.supportingonline.Calls.ErrorCall;
import com.egyptrefaat.supporting.supportingonline.Calls.OnPress;
import com.egyptrefaat.supporting.supportingonline.Custom.MyRequest;
import com.egyptrefaat.supporting.supportingonline.Custom.Myvollysinglton;
import com.egyptrefaat.supporting.supportingonline.Custom.OnErrorRequest;
import com.egyptrefaat.supporting.supportingonline.Models.ChatHistroyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatHistoryAdapter adapter;
    private ArrayList<ChatHistroyModel> arrayList=new ArrayList<>();

    private TextView textNoData;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_friends);

        // no data
        textNoData=(TextView)findViewById(R.id.chatfriends_nodata);
        textNoData.setVisibility(View.GONE);

        // progress
        progressBar=(ProgressBar)findViewById(R.id.chatfriends_progress);
        progressBar.setVisibility(View.GONE);

        // recycler
        arrayList.clear();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_chatFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter=new ChatHistoryAdapter(arrayList, this, new OnPress() {
            @Override
            public void onClick(View view, int position) {

                arrayList.get(position).setReade(true);
                adapter.notifyItemChanged(position);
                startActivity(new Intent(ChatFriendsActivity.this, FriendChatActivity.class)
                        .putExtra("id",arrayList.get(position).getUserId())
                        .putExtra("name",arrayList.get(position).getUserName())
                        .putExtra("image",arrayList.get(position).getUserImage()));
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        recyclerView.setAdapter(adapter);

        loadchatHistory();
    }

    private void loadchatHistory() {
        progressBar.setVisibility(View.VISIBLE);
        String url= HomeActivity.domain+"api/messages";
        StringRequest request=new MyRequest(HomeActivity.token, 1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("messages",response);
                arrayList.clear();

                try {
                    JSONObject sucobject=new JSONObject(response);
                    JSONArray array=sucobject.getJSONArray("success");
                    for (int i =0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        JSONObject userObject=object.getJSONObject("user");
                        JSONObject messageObject=object.getJSONObject("message");

                        String id=userObject.getString("id");
                        String name=userObject.getString("name");
                        String image=userObject.getString("image");
                        String message=messageObject.getString("message");
                        String date=messageObject.getString("date");
                        String readAt=messageObject.getString("read_at");


                        ChatHistroyModel model=new ChatHistroyModel();
                        model.setUserId(id);
                        model.setUserName(name);
                        model.setUserImage(image);
                        model.setContent(message);
                        model.setTime(date);
                        if (!readAt.equals("null")){
                            model.setReade(true);
                        }
                        arrayList.add(model);

                    }
                    Collections.sort(arrayList, new Comparator<ChatHistroyModel>() {
                        @Override
                        public int compare(ChatHistroyModel o1, ChatHistroyModel o2) {
                            return o1.getUserName().compareTo(o2.getUserName());
                        }
                    });
                   adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);

                   if (arrayList.size()==0){
                       textNoData.setVisibility(View.VISIBLE);
                   }else {
                       textNoData.setVisibility(View.GONE);
                   }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new OnErrorRequest(ChatFriendsActivity.this, new ErrorCall() {
            @Override
            public void OnBack() {

                progressBar.setVisibility(View.INVISIBLE);

            }
        }));

        Myvollysinglton.getInstance(this).addtorequst(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_righ, R.anim.slide_to_left);

    }
}
