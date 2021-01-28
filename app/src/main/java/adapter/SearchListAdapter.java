package adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatproject5.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dto.SearchListDto;

public class SearchListAdapter extends BaseAdapter implements Filterable {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList. (원본 데이터 리스트)
    private ArrayList<SearchListDto> listViewItemList = new ArrayList<>() ;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<SearchListDto> filteredItemList = listViewItemList ;

    private Context context;
//    private TextView workerId;

    private Handler handler = new Handler();
    private Filter listFilter;
    private String myId;



    public SearchListAdapter(Context context, String myId) {

        this.context = context;
        this.myId = myId;
    }

    //사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //↓ android.view.InflateException 에러가 나서 추가
        final Context context = parent.getContext();

        // "searchList_item" 을 inflate 하여 convertView 참조 획득.
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.searchlist_item, parent, false);
        }


        ImageView workerProfile = convertView.findViewById(R.id.image_searchList);
        TextView workerId = convertView.findViewById(R.id.id_searchList);
        TextView workerContent = convertView.findViewById(R.id.content_searchList);
        Button addButton = convertView.findViewById(R.id.addButton_searchList);

        // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
        SearchListDto listViewItem = filteredItemList.get(position);


        workerId.setText(listViewItem.getWorkerId());
        workerContent.setText(listViewItem.getWorkerContent());

        //코디네이터 선택
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, workerId.getText().toString() + "선택", Toast.LENGTH_SHORT).show();

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/selectWorker.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        addCoordinator(urlStr);

                        StringBuilder output = new StringBuilder();

                        try {
                            URL url = new URL(urlStr);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            if(conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("POST");
                                conn.setDoInput(true);

                                OutputStream outputStream = conn.getOutputStream();
                                String params = "id=" + myId + "&workerId=" + workerId.getText().toString();

                                System.out.println("id = > " + myId + " & workerId = > " + workerId.getText().toString());

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
                        println(output.toString());
                    }
                }).start();
            }
        });

        return convertView;
    }   //end getView

    /*
    //코디네이터 등록
    public void addCoordinator(String urlStr) {

        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "id=" + myId + "&workerId=" + workerId.getText().toString();
                System.out.println("id = > " + myId + "workerId = > " + workerId.getText().toString());
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
        println(output.toString());
    }

     */

    //출력 메소드
    public  void println(String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(context, "등록성공", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public long getItemId(int position) {
        return position ;
    }


    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    public void addItem(SearchListDto item) {

        listViewItemList.add(item);
    }

    @Override
    public Filter getFilter() {

        if(listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {

                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;

            } else {
                ArrayList<SearchListDto> itemList = new ArrayList<>() ;

                for (SearchListDto item : listViewItemList) {

                    //코디네이터의 아이디와 대화명 모두 필터링 대상으로
                    if (item.getWorkerId().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getWorkerContent().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<SearchListDto>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }

        }
    }


}
