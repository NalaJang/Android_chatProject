package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.EditAddressActivity;
import com.example.chatproject5.MyInfoActivity;
import com.example.chatproject5.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dto.AddressDto;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.getIntentOld;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    private String myId, nickName_db;
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

        TextView num, result, nickName, userName, userPhone, userAddress1, userAddress2;
        Button editButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);


            num = itemView.findViewById(R.id.num_addressList);
            result = itemView.findViewById(R.id.result_addressList);
            nickName = itemView.findViewById(R.id.nickName_addressList);
            userName = itemView.findViewById(R.id.userName_addressList);
            userPhone = itemView.findViewById(R.id.userPhone_addressList);
            userAddress1 = itemView.findViewById(R.id.userAddress1_addressList);
            userAddress2 = itemView.findViewById(R.id.userAddress2_addressList);
            editButton = itemView.findViewById(R.id.edit_addressList);
            deleteButton = itemView.findViewById(R.id.delete_addressList);


            //주소 수정 버튼
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, EditAddressActivity.class);
                    intent.putExtra("userId_db", myId);
                    intent.putExtra("num_db", num.getText().toString());
                    intent.putExtra("nickName_db", nickName.getText().toString());
                    intent.putExtra("userName_db", userName.getText().toString());
                    intent.putExtra("userPhone_db", userPhone.getText().toString());
                    intent.putExtra("userAddress1_db", userAddress1.getText().toString());
                    intent.putExtra("userAddress2e_db", userAddress2.getText().toString());

                    //context.startActivity(intent);
                    //에러 : FLAG_ACTIVITY_NEW_TASK flag
                    //↑ ListView 의 "한 아이템을 클릭했을 경우, startActivity 를 사용해 새로운 Activity 를 생성하려 했을 때"
                    //↓ 수정
//                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    ((Activity)context).startActivityForResult(intent, MyInfoActivity.EDIT_REQUEST_CODE);

                }

            });


            //주소 삭제 버튼
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.info);
                    builder.setMessage("삭제하시겠습니까?");

                    if(position != RecyclerView.NO_POSITION) {

                        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/addressDelete.do";

                                delete(urlStr, Integer.parseInt(addressList.get(position).getNo()));

                                addressList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, addressList.size());

                            }
                        });
                    }

                    builder.setNegativeButton(R.string.close, null);

                    //window -- token null is not valid; is your activity running?
                    // ↑ Dialog 생성 시 getApplicationContext() 를 사용해서 나는 에러
                    //MyInfoActivity 에서 adapter 설정을 클래스이름.this 로 수정
                    builder.show();
                }
            }); //end deleteButton
        }


        public void setItem(AddressDto address) {
            num.setText(address.getNo());
            nickName.setText(address.getNickName());
            userName.setText(address.getUserName());
            userPhone.setText(address.getPhone());
            userAddress1.setText(address.getAddress1());
            userAddress2.setText(address.getAddress2());

            switch (address.getResult()) {

                case "0" :
//                    result.setText("");
                    result.setVisibility(View.GONE);
                    break;

                case "1" :
                    result.setText("(기본 배송지)");
                    break;
            }
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

    }   //end viewHolder

    //추가
//    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if(requestCode == 1) {
//            if(resultCode == 1) {
//
//            }
//        }
//    }
}
