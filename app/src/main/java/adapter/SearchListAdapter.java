package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chatproject5.R;

import java.util.ArrayList;

import dto.SearchListDto;

public class SearchListAdapter extends BaseAdapter {

    private ArrayList<SearchListDto> items = new ArrayList<>();
    private ArrayList<String> workerIds;

    public SearchListAdapter(ArrayList<String> workerIds) {
        this.workerIds = workerIds;

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override   // 지정한 위치(position)에 있는 데이터 리턴
    public Object getItem(int position) {
        return null;
    }

    @Override   // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    public long getItemId(int position) {
        return position;
    }

    public void addItem(String workerId) {
        SearchListDto item = new SearchListDto();

        item.setWorkerId(workerId);
//        item.setWorkerContent(workerContent);

        items.add(item);
    }

    @Override   //position 에 위치한 데이터를 화면에 출력하는데 사용될 view 리턴
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        //convertView 참조 획득
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.searchlist_item, parent, false);

        }

        TextView workerId = convertView.findViewById(R.id.workerId_list);
        TextView workerContent = convertView.findViewById(R.id.workerContent_list);

        SearchListDto searchListDto = items.get(position);

        workerId.setText(searchListDto.getWorkerId());
//        workerContent.setText(searchListDto.getWorkerContent());


        return convertView;
    }
}
