package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.PointDto;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.ViewHolder> {

    ArrayList<PointDto> items = new ArrayList<>();

    @NonNull
    @Override
    public PointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.point_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PointAdapter.ViewHolder holder, int position) {

        PointDto item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PointDto item) {
        items.add(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView indate_text, product_text, point_text;

        public ViewHolder(View itemView) {
            super(itemView);

            indate_text = itemView.findViewById(R.id.indate_point);
            product_text = itemView.findViewById(R.id.product_point);
            point_text = itemView.findViewById(R.id.point_point);
        }

        public void setItem(PointDto item) {
            indate_text.setText(item.getIndate());
            product_text.setText(item.getProduct());
            point_text.setText(Integer.toString(item.getPoint()));
            //에러 : Resources$NotFoundException 가 나서 수정함  ↑
        }
    }
}
