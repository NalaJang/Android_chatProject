package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.OnItemClickListener;
import com.example.chatproject5.OnListItemClickListener;
import com.example.chatproject5.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dto.ChatListDto;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>
        implements OnItemClickListener {

    private TextView workerId;


    private OnItemClickListener listener;

    private ArrayList<ChatListDto> items = new ArrayList<>();



    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.chatlist_item, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {

        ChatListDto item = items.get(position);
        holder.setItem(item);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView workerContent;
        private Button deleteButton, chatButton;

        private int position = getAdapterPosition();

        public MyViewHolder(View itemView) {
            super(itemView);

            workerId = itemView.findViewById(R.id.workerId_list);
            workerContent = itemView.findViewById(R.id.workerContent_list);
            deleteButton = itemView.findViewById(R.id.deleteButton_list);
            chatButton = itemView.findViewById(R.id.chatButton_list);


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.info);
                    builder.setMessage("삭제하시겠습니까?\n\n채팅내용은 유지되지만 해당 상담사와의 채팅은 더 이상 불가능합니다.");

                    if(position != RecyclerView.NO_POSITION) {

                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                items.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, items.size());

                                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/workerDelete.do";

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        delete(urlStr);

                                    }
                                }).start();


//                                items.remove(position);
//                                notifyItemRemoved(position);
//                                notifyItemRangeChanged(position, items.size());


                                Toast.makeText(v.getContext(), "삭제되었습니다",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }   //end if

                    builder.setNegativeButton(R.string.close, null);
                    builder.show();
                }
            }); //end deleteButton


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int position = getAdapterPosition();
                    if(listener != null) {

                        listener.onItemClick(ChatListAdapter.MyViewHolder.this, v, position);

                    }

                }
            });

            /*
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(listener != null) {  //-> array index of 에러
                        listener.onItemClick(MyViewHolder.this, v, position);
                    }

                }
            });

             */


        }   //end myViewHolder



        public void setItem(ChatListDto item) {
            workerId.setText(item.getWorkerId());
            workerContent.setText(item.getContent());
        }



        public void delete(String urlStr) {
            StringBuilder output = new StringBuilder();


            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);

                    OutputStream outputStream = conn.getOutputStream();
                    String params = "workerId=" + workerId;

                    Log.d("workerId", workerId.getText().toString());

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


    public ChatListDto getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onItemClick(ChatListAdapter.MyViewHolder holder, View view, int position) {

        if(listener != null) {

            listener.onItemClick(holder, view, position);
        }

    }

}
