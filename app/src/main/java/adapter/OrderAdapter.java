package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.OrderDto;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderDto> items = new ArrayList<>();



    public OrderAdapter(Context context) {

        this.context = context;
    }



    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.orderlist_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {

        OrderDto item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(OrderDto item) {
        items.add(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView indate_text, status_text, product_text, sizeAndColor_text, quantity_text, price_text;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_order);
            indate_text = itemView.findViewById(R.id.indate_order);
            status_text = itemView.findViewById(R.id.status_order);
            product_text = itemView.findViewById(R.id.product_order);
            sizeAndColor_text = itemView.findViewById(R.id.sizeAndColor_order);
            quantity_text = itemView.findViewById(R.id.quantity_order);
            price_text = itemView.findViewById(R.id.price_order);

        }

        public void setItem(OrderDto item) {
            imageView.setImageResource(R.drawable.sweatshirt);
            indate_text.setText(item.getIndate());
            status_text.setText(item.getResult());
            product_text.setText(item.getPname());
            sizeAndColor_text.setText(item.getSizeAndColor());
            quantity_text.setText(Integer.toString(item.getQuantity()));
            price_text.setText(Integer.toString(item.getPrice()));


            switch (item.getResult()) {

                case "1" :
                    status_text.setText("주문완료");
                    status_text.setTextColor(Color.parseColor("#D81B60"));
                    break;

                case "2" :
                    status_text.setText("배송준비중");
                    break;

                case "3" :
                    status_text.setText("배송중");
                    break;

                case "4" :
                    status_text.setText("배송완료");
                    status_text.setTextColor(Color.parseColor("#FF018786"));
                   break;
            }
        }
    }
}
