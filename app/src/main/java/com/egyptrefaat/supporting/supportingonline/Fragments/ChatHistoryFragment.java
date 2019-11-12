package com.egyptrefaat.supporting.supportingonline.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.egyptrefaat.supporting.supportingonline.Adapters.ChatHistoryAdapter;
import com.egyptrefaat.supporting.supportingonline.Calls.OnPress;
import com.egyptrefaat.supporting.supportingonline.FriendChatActivity;
import com.egyptrefaat.supporting.supportingonline.HomeActivity;
import com.egyptrefaat.supporting.supportingonline.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatHistoryFragment extends Fragment {


    private RecyclerView recyclerView;
    public static ChatHistoryAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_history, container, false);

        // recycler
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_chathistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter=new ChatHistoryAdapter(HomeActivity.chatHistoryList, getActivity(), new OnPress() {
            @Override
            public void onClick(View view, int position) {

                HomeActivity.chatHistoryList.get(position).setReade(true);
                adapter.notifyItemChanged(position);

                startActivity(new Intent(getActivity(), FriendChatActivity.class)
                .putExtra("id",HomeActivity.chatHistoryList.get(position).getUserId())
                        .putExtra("name",HomeActivity.chatHistoryList.get(position).getUserName())
                        .putExtra("image",HomeActivity.chatHistoryList.get(position).getUserImage()));

                getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        recyclerView.setAdapter(adapter);


        return view;
    }

}
