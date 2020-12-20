package era.apps.happinessjar.models.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import era.apps.happinessjar.R;
import era.apps.happinessjar.models.chat.adapter.ChatAdapter;
import era.apps.happinessjar.models.chat.model.ChatMessages;
import era.apps.happinessjar.models.chat.model.ChatViewModel;
import era.apps.happinessjar.models.chat.model.Conversation;
import era.apps.happinessjar.util.DataManger;

public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    private EditText getMsg;
    private String myId;
    private ChatViewModel model;
    private Conversation con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DataManger.getInstance().setChatOpen(true);
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView allChat = root.findViewById(R.id.chat);
        allChat.setLayoutManager(new LinearLayoutManager(getContext()));
        Context context = getContext();
        assert context != null;
        myId = context.getSharedPreferences("info", 0)
                .getString("chatId", "");
        String name = context.getSharedPreferences("info", 0)
                .getString("chatName", "");
        DataManger.getInstance().loading();
        ChatAdapter adapter = new ChatAdapter(myId);
        allChat.setAdapter(adapter);
        getMsg = root.findViewById(R.id.getMessage);
        if (model == null) {
            model = new ViewModelProvider(this).get(ChatViewModel.class);
        }
        model.setOnMessageSent(() -> getMsg.setText(""));
        model.getAllChatMessage().observe(requireActivity(), conversation -> {
            DataManger.getInstance().normal();
            if (conversation != null) {
                con = conversation;
                adapter.setDataList(con.getList());
                return;
            }
            con = new Conversation();
            con.setName(name);
            con.setId(myId);

        });

        root.findViewById(R.id.sendMsg).setOnClickListener(v -> {
            if (getMsg.getText().length() > 0) {
                if (!getMsg.getText().toString().equals(" ")) {
                    prepareMessage(con);
                }
            }
        });
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void prepareMessage(Conversation conversation) {
        ChatMessages oneMessage = new ChatMessages();
        oneMessage.setSenderId(myId);
        oneMessage.setMessages(getMsg.getText().toString());
        oneMessage.setTime(Calendar.getInstance().getTimeInMillis());
        if (conversation.getList() == null) {
            conversation.setList(new ArrayList<>());
        }
        conversation.getList().add(oneMessage);
        conversation.setLastTimeSend(oneMessage.getTime());
        model.sendMessage(conversation);//sendMsg(con);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DataManger.getInstance().setChatOpen(false);

    }
}