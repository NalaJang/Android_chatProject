package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatproject5.R;

import java.util.ArrayList;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;
import dto.MessageData;

public class MessageAdapter2 extends BaseAdapter {

    ArrayList<MessageData> messageItems;
    LayoutInflater layoutInflater;
    String myId;

    public MessageAdapter2(ArrayList<MessageData> messageItems, LayoutInflater layoutInflater, String myId) {
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

    @Override//항목의 index, 전에 inflate 되어있는 view, listView
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        MessageData item=messageItems.get(position);

        //재활용할 뷰는 사용하지 않음
        View itemView=null;


        //내가 보낸 메세지
        if(item.getUserId().equals(myId)){
            itemView= layoutInflater.inflate(R.layout.my_msgbox,viewGroup,false);
        }else{
            itemView= layoutInflater.inflate(R.layout.your_msgbox,viewGroup,false);
        }

        //만들어진 itemView 에 값들 설정
        CircleImageView iv= itemView.findViewById(R.id.iv);
        TextView tvName= itemView.findViewById(R.id.tv_name);
        TextView tvMsg= itemView.findViewById(R.id.tv_msg);
        TextView tvTime= itemView.findViewById(R.id.tv_time);

        tvName.setText(item.getUserId());
        tvMsg.setText(item.getContent());
        tvTime.setText(item.getTime());

//        Glide.with(itemView).load(item.getProfileUrl()).into(iv);

        return itemView;
    }
}
