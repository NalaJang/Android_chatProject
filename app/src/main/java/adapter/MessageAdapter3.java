package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chatproject5.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dto.ChatListDto;
import dto.Message;
import dto.MessageData;

public class MessageAdapter3 extends BaseAdapter {

    ArrayList<MessageData> messageItems;
    LayoutInflater layoutInflater;
    String myId;

    public MessageAdapter3(ArrayList<MessageData> messageItems, LayoutInflater layoutInflater, String myId) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
        this.myId = myId;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(MessageData item) {
        messageItems.add(item);
    }

    @Override//항목의 index, 전에 inflate 되어있는 view, listView
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        MessageData item = messageItems.get(position);


        //재활용할 뷰는 사용하지 않음
        View itemView=null;


        //내가 보낸 메세지
        if(item.getUserId().equals(myId)){
            System.out.println("myId => " + myId +  "msgAdapter ====> " + item.getContent());


            itemView= layoutInflater.inflate(R.layout.my_msgbox,viewGroup,false);


        //상대방이 보낸 메세지
        }else{

            itemView= layoutInflater.inflate(R.layout.your_msgbox,viewGroup,false);
        }



        //만들어진 itemView 에 값들 설정
        CircleImageView iv= itemView.findViewById(R.id.iv);
        TextView userId_msgBox= itemView.findViewById(R.id.userId_msgBox);
        TextView userMsg_msgBox= itemView.findViewById(R.id.userMsg_msgBox);
        TextView time_msgBox= itemView.findViewById(R.id.time_msgBox);

        userId_msgBox.setText(item.getUserId());
        userMsg_msgBox.setText(item.getContent());
        time_msgBox.setText(item.getTime());

//        Glide.with(itemView).load(item.getPofileUrl()).into(iv);

        return itemView;
    }
}
