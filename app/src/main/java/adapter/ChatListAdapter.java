package adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.Chat_roomActivity;
import com.example.chatproject5.EntranceActivity;
import com.example.chatproject5.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import chat.MsgUtils;
import chat.Signals;
import database.ChattingRoomListHelper;
import dto.ChatListDto;
import dto.ChattingRoomListDto;
import dto.Message;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {


    private final Context context;
    private final String myId;


    private ArrayList<ChatListDto> items;
    private ChatListDto item;


    private ChattingRoomListHelper roomListHelper;
    private ChattingRoomListDto roomListDto;

//    private final Date today = new Date();
//    private final SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");



    public ChatListAdapter(ArrayList<ChatListDto> items, Context context, String myId) {

        this.items = items;
        this.context = context;
        this.myId = myId;

    }


    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.chatlist_item, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {

        item = items.get(position);
        holder.setItem(item);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView workerId, workerContent;
        private Button deleteButton, chatButton;

        private int position;

        public MyViewHolder(View itemView) {
            super(itemView);


            workerId = itemView.findViewById(R.id.workerId_list);
            workerContent = itemView.findViewById(R.id.workerContent_list);
            deleteButton = itemView.findViewById(R.id.deleteButton_list);
            chatButton = itemView.findViewById(R.id.chatButton_list);


            //삭제
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    position = getAdapterPosition();


                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.info);
                    builder.setMessage("삭제하시겠습니까?\n\n채팅내용은 유지되지만 해당 상담사와의 채팅은 더 이상 불가능합니다.");



                    if(position != RecyclerView.NO_POSITION) {

                        builder.setPositiveButton(R.string.delete, (dialog, which) -> {    //-> 람다식


                            System.out.println(items.get(position).toString());


                            final String urlStr = "http://192.168.0.17:8080/webapp/webServer/workerDelete.do";

//                                ↓ thread 실행과 동시에 items.remove(position) 를 실행하면서 IndexOutOfException 이 발생
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {

//                                      ↓ 해당 position 의 DB 정보 가져오기
                                    delete(urlStr, items.get(position).getNum());

//                                    }
//                                }).start();


                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());

                            //hashSet 에서도 삭제해야함
                            //에러 : IndexOutOfBoundsException
                            //for 문에 넣어줌
                            for(int i = 0; i < position; i++ ) {
                                EntranceActivity.hashSet.remove(items.get(i).getWorkerId());
                            }


                            Toast.makeText(v.getContext(), "삭제되었습니다",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }   //end if

                    builder.setNegativeButton(R.string.close, null);
                    builder.show();
                }
            }); //end deleteButton




            //채팅
            chatButton.setOnClickListener(v -> {

                position = getAdapterPosition();

                if(position != RecyclerView.NO_POSITION) {

                    System.out.println("채팅버튼 클릭" + items.get(position).getNum());
                    System.out.println("채팅버튼 클릭" + items.get(position).getWorkerId());

                    roomListHelper = new ChattingRoomListHelper(context);


                    roomListDto = roomListHelper.findRoom(items.get(position).getWorkerId());

                    /*
                    //선택한 상담사와의 채팅방이 없을 경우
                    if( roomListDto == null ) {

                        System.out.println("채팅방 없음");

                        roomListDto = new ChattingRoomListDto().setRoomName(items.get(position).getWorkerId())
                                                                .setMyId(myId)
                                                                .setOtherId(items.get(position).getWorkerId())
                                                                .setTime(timeNow.format(today));

                        roomListHelper.insert(roomListDto);

                    //채팅방이 이미 존재할 경우
                    } else {

                        System.out.println("이미 존재하는 채팅방");

                    }

                     */

                    Message message = new Message();
                    message.setSignal(Signals.CHECK_IN.getSignal() + "");
                    message.setRoomId(items.get(position).getWorkerId());
                    message.setToId(items.get(position).getWorkerId());
                    message.setPhoto("");

                    MsgUtils.sendMsg(message);      //서버에 신호 보내기


                    Intent intent = new Intent(context, Chat_roomActivity.class);
                    intent.putExtra("roomName", items.get(position).getWorkerId());
                    intent.putExtra("userId_db", myId);

                    context.startActivity(intent);  //채팅방으로 이동
                }

            }); //end chatButton


        }   //end myViewHolder



        public void setItem(ChatListDto item) {
            workerId.setText(item.getWorkerId());
            workerContent.setText(item.getContent());
        }



        //선택 항목 삭제 (-> DB 데이터 삭제)
        public void delete(String urlStr, int num) {
            StringBuilder output = new StringBuilder();


            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);

                    OutputStream outputStream = conn.getOutputStream();
                    String params = "num=" + num;


                    outputStream.write(params.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;

                    while(true) {
                        line = reader.readLine();

                        if(line == null) {
                            break;
                        }

                        output.append(line + "\n");

                    }
                    reader.close();
                    conn.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }   //end class MyViewHolder

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(ChatListDto item) {
        items.add(item);
    }

}
