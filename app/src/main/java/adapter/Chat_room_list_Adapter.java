package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.OnListItemClickListener;
import com.example.chatproject5.R;
import dto.RoomList;

import java.util.Vector;

public class Chat_room_list_Adapter extends RecyclerView.Adapter<Chat_room_list_Adapter.MyViewHolder>
        implements OnListItemClickListener {

    private Vector<RoomList> lists;
    private Activity activity;
    private OnListItemClickListener listener;

    public Chat_room_list_Adapter(Vector<RoomList> lists, Context context, Activity activity) {

        this.lists = lists;
        this.activity = activity;

    }

    @NonNull
    @Override
    public Chat_room_list_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        E/RecyclerView: No layout manager attached; skipping layout
//         : 에러 -> LayoutManager 를 설정하지 않아서 발생한 현상
//        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_room_list, parent, false);
//        이렇게 고쳐줌 ↓

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_list, parent, false);

        //레이아웃을 viewHolder 에 넣어줌
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //위치에 맞는 아이템들을 가져온다.
        RoomList list = lists.get(position);

        holder.user_img.setImageResource(list.getUser_img());
        holder.room_name.setText(list.getRoom_name());
        holder.last_content.setText(list.getLast_content());
        holder.last_time.setText(list.getLast_time());

    }

    @Override
    public int getItemCount() {
        int a;

        if(lists != null && !lists.isEmpty()) {
            a = lists.size();
        } else {
            a = 0;
        }
        return a;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView user_img;
        TextView room_name, last_content, last_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_img = itemView.findViewById(R.id.user_img_item);
            room_name = itemView.findViewById(R.id.room_name_item);
            last_content = itemView.findViewById(R.id.content_item);
            last_time = itemView.findViewById(R.id.time_item);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(activity, "클릭", Toast.LENGTH_SHORT).show();

                    int position = getAdapterPosition();
                    if(listener != null) {

                        listener.onItemClick(MyViewHolder.this, v, position);
                    }

                }
            });


        }
    }   //end MyViewHolder

    public void addChat(RoomList roomList) {
        lists.add(roomList);
        notifyItemInserted(lists.size() -1);

    }

    //채팅방을 넘기기 위해 추가한 부분들 ~ onItemClick 까지
    public RoomList getItem(int position) {
        return lists.get(position);
    }

    public void setOnItemClickListener(OnListItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(MyViewHolder holder, View view, int position) {

        if(listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

}
