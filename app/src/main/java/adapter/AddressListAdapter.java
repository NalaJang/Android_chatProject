package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.EditAddressActivity;
import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.AddressDto;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    private String myId;
    private final Context context;
    private ArrayList<AddressDto> addressList = new ArrayList<>();


    public AddressListAdapter(ArrayList<AddressDto> addressList, Context context, String myId) {
        this.addressList = addressList;
        this.context = context;
        this.myId = myId;
    }


    @NonNull
    @Override
    public AddressListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.addresslist_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListAdapter.ViewHolder holder, int position) {

        AddressDto addressDto = addressList.get(position);
        holder.setItem(addressDto);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void addItem(AddressDto item) {
        addressList.add(item);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView result, nickName, userName, userPhone, userAddress1, userAddress2;
        Button editButton;

        public ViewHolder(View itemView) {
            super(itemView);


            result = itemView.findViewById(R.id.result_addressList);
            nickName = itemView.findViewById(R.id.nickName_addressList);
            userName = itemView.findViewById(R.id.userName_addressList);
            userPhone = itemView.findViewById(R.id.userPhone_addressList);
            userAddress1 = itemView.findViewById(R.id.userAddress1_addressList);
            userAddress2 = itemView.findViewById(R.id.userAddress2_addressList);
            editButton = itemView.findViewById(R.id.edit_addressList);


            //주소 수정 버튼
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EditAddressActivity.class);
                    intent.putExtra("userId_db", myId);
                    intent.putExtra("nickName_db", nickName.getText().toString());
                    intent.putExtra("userName_db", userName.getText().toString());
                    intent.putExtra("userPhone_db", userPhone.getText().toString());
                    intent.putExtra("userAddress1_db", userAddress1.getText().toString());
                    intent.putExtra("userAddress2e_db", userAddress2.getText().toString());

                    //context.startActivity(intent);
                    //에러 : FLAG_ACTIVITY_NEW_TASK flag -> ListView 의 "한 아이템을 클릭했을 경우, startActivity를 사용해 새로운 Activity를 생성하려 했을 때"
                    //수정 ↓
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }

        public void setItem(AddressDto address) {
            nickName.setText(address.getNickName());
            userName.setText(address.getUserName());
            userPhone.setText(address.getPhone());
            userAddress1.setText(address.getAddress1());
            userAddress2.setText(address.getAddress2());

            switch (address.getResult()) {

                case "0" :
                    result.setText("");
                    break;

                case "1" :
                    result.setText("(기본 배송지)");
                    break;
            }
        }

    }   //end viewHolder

}
