package adapter;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.ChatListDto;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    ArrayList<ChatListDto> items = new ArrayList<>();

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

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ChatListDto item) {
        items.add(item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ArrayList<ChatListDto> items = new ArrayList<>();

        TextView workerId, workerContent;
        Button deleteButton, chatButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            workerId = itemView.findViewById(R.id.workerId_list);
            workerContent = itemView.findViewById(R.id.workerContent_list);
            deleteButton = itemView.findViewById(R.id.deleteButton_list);
            chatButton = itemView.findViewById(R.id.chatButton_list);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.info);
                    builder.setMessage("삭제하시겠습니까?\n\n채팅내용은 유지되지만 해당 상담사와의 채팅은 더 이상 불가능합니다.");

                    if(position != RecyclerView.NO_POSITION) {

                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(v.getContext(), "삭제되었습니다",
                                        Toast.LENGTH_SHORT).show();

                                items.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, items.size());
                            }
                        });
                    }

                    builder.setNegativeButton(R.string.close, null);
                    builder.show();
                }
            });
        }

        public void setItem(ChatListDto item) {
            workerId.setText(item.getWorkerId());
            workerContent.setText(item.getContent());
        }
    }
}
