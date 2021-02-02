package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.OnListItemClickListener;
import com.example.chatproject5.R;

import database.ChattingRoomListHelper;
import database.MessageHelper;
import dto.ChattingRoomListDto;

import java.util.ArrayList;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class Chat_room_list_Adapter extends RecyclerView.Adapter<Chat_room_list_Adapter.MyViewHolder>
        implements OnListItemClickListener {

    private ArrayList<ChattingRoomListDto> lists;
    private Activity activity;
    private Context context;
    private OnListItemClickListener listener;


    public Chat_room_list_Adapter(ArrayList<ChattingRoomListDto> lists, Context context, Activity activity) {

        this.lists = lists;
        this.context = context;
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
        ChattingRoomListDto list = lists.get(position);

//        holder.user_img.setImageResource(Integer.parseInt(list.getProfileImage()));
        holder.room_name.setText(list.getRoomName());
        holder.last_content.setText(list.getLastContent());
        holder.last_time.setText(list.getTime());

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView user_img;
        TextView room_name, last_content, last_time;
        Button deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            user_img = itemView.findViewById(R.id.user_img_item);
            room_name = itemView.findViewById(R.id.room_name_item);
            last_content = itemView.findViewById(R.id.content_item);
            last_time = itemView.findViewById(R.id.time_item);
            deleteButton = itemView.findViewById(R.id.delete_roomList);


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

            //나가기
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //getAdapterPosition() : viewHolder 에서 제공하는 현재 위치를 확인 할 수 있는 메소드
                    int position = getAdapterPosition();

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.info);
                    builder.setMessage("삭제하시겠습니까?\n\n채팅내용은 저장되지 않습니다.");


                    if(position != RecyclerView.NO_POSITION) {
                        builder.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ChattingRoomListHelper db = new ChattingRoomListHelper(context);
                                MessageHelper msgHelper = new MessageHelper(context);


                                msgHelper.deleteMessage(lists.get(position).getRoomName()); //-> 메세지도 삭제
                                db.deleteRoom(lists.get(position).getNum());


                                lists.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, lists.size());

                            }

                        });
                    }   //end if

                    builder.setNegativeButton(R.string.close, null);
                    builder.show();
                }
            });


        }
    }   //end MyViewHolder

    @Override
    public int getItemCount() {

        return lists.size();
    }


    public void addChat(ChattingRoomListDto roomList) {
        lists.add(roomList);
//        notifyItemInserted(lists.size() -1);

    }

    //채팅방을 넘기기 위해 추가한 부분들 ~ onItemClick 까지
    public ChattingRoomListDto getItem(int position) {
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
