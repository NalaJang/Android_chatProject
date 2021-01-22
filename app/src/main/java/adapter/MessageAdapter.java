package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.R;

import java.util.Vector;

import dto.MessageData;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private Vector<MessageData> messageData;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userId_text, myId_text;
        TextView message_text, myMessage_text;
        TextView time_text, myTime_text;
        ImageView userImage, myImage;

        public MyViewHolder(View view) {
            super(view);

            //상대방
            userId_text = view.findViewById(R.id.userId_text);
            message_text = view.findViewById(R.id.message_text);
            time_text = view.findViewById(R.id.message_time);
            userImage = view.findViewById(R.id.userImage);

            //나
            myMessage_text = view.findViewById(R.id.myMessage_text);
            myTime_text = view.findViewById(R.id.myMessage_time);
            myImage = view.findViewById(R.id.myImage);

            //메시지 클릭 이벤트
            myMessage_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {

                        builder.setTitle("삭제");
                        builder.setMessage("삭제하시겠습니까?");

                        //"네"
                        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                messageData.remove(position);
                                notifyDataSetChanged(); //새로고침
                            }
                        });

                        //"아니오"
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                    }

                    builder.show();
                }
            });
        }
    }   //end MyViewHolder

    public MessageAdapter (Vector<MessageData> myData, Context context, String myId) {

        messageData = myData;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        MyViewHolder holder = new MyViewHolder(linearLayout);

        return holder;
    }

    @Override  //아이디를 비교하여 메세지 정렬(세팅한 값들을 화면에 보여줌)
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        //위치에 맞는 아이템들을 가져온다.
        MessageData message = messageData.get(position);

        //뷰홀더의 아이템 뷰에 표시
        //상대방
        holder.userId_text.setText(message.getUserId());
        holder.message_text.setText(message.getMessage());
        holder.time_text.setText(message.getTime());

        //나
        holder.myId_text.setText(message.getUserId());
        holder.myMessage_text.setText(message.getMessage());
        holder.myTime_text.setText(message.getTime());

        //보낸 사람이 "나"인 경우
        if (message.getUserId().equals(message.getUserId())) {

            holder.userId_text.setVisibility(View.GONE);
            holder.message_text.setVisibility(View.GONE);
            holder.time_text.setVisibility(View.GONE);
            holder.userImage.setVisibility(View.GONE);


        } else {

            holder.myId_text.setVisibility(View.GONE);
            holder.myMessage_text.setVisibility(View.GONE);
            holder.myTime_text.setVisibility(View.GONE);
            holder.myImage.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {

        return messageData.size();
    }

    public void addChat(MessageData message) {
        messageData.add(message);
        notifyItemInserted(messageData.size() -1);    //메세지 갱신
    }

}
