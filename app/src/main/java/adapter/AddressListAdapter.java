package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.EditAddressFragment;
import com.example.chatproject5.OnItemClickListener;
import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.AddressDto;
import dto.ChattingRoomListDto;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {


    private ArrayList<AddressDto> addressList = new ArrayList<>();



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

                case "1" :
                    result.setText("(기본 배송지)");
                    break;
            }
        }

    }   //end viewHolder

}
