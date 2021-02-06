package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dto.SearchListDto;

public class SearchListAdapter extends BaseAdapter implements Filterable {

    // 기존 데이터 리스트
    private ArrayList<SearchListDto> listViewItemList = new ArrayList<>() ;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<SearchListDto> filteredItemList = listViewItemList ;

    public static Intent intent;

    private Context context;
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
        TextView workerNum = convertView.findViewById(R.id.num_searchList);
        Button addButton = convertView.findViewById(R.id.addButton_searchList);

        // filteredItemList 에서 position 에 위치한 데이터 참조 획득
        SearchListDto listViewItem = filteredItemList.get(position);


        workerId.setText(listViewItem.getWorkerId());
        workerNum.setText(listViewItem.getWorkerNum());
        workerNum.setVisibility(View.GONE);
        workerContent.setText(listViewItem.getWorkerContent());


        //코디네이터 등록 버튼
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String urlStr = "http://192.168.0.17:8080/webapp/webServer/selectWorker.do";


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        StringBuilder output = new StringBuilder();

                        try {
                            URL url = new URL(urlStr);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            if(conn != null) {
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("POST");
                                conn.setDoInput(true);

                                OutputStream outputStream = conn.getOutputStream();
                                String params = "id=" + myId
                                            + "&workerNum=" + workerNum.getText().toString()
                                            + "&workerId=" + workerId.getText().toString();

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
                        setSelectWorker(output.toString());
                        println();
                    }
                }).start();
            }
        });

        return convertView;
    }   //end getView


    public void setSelectWorker(String str) {
        Document doc = Jsoup.parse(str);
        Elements result = doc.select("p.result");
        Elements workerId_db = doc.select("ol > li.workerId");

        for(int i = 0, size = result.size(); i < size; i++) {
            if(result.get(0).text().equals("등록성공")) {

                intent = new Intent();
                intent.putExtra("workerId_db", workerId_db.text());
//                ((Activity)context).setResult(2, intent);

            }
       }
    }



    //출력 메소드
    public  void println() {
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

    //검색 관련 메소드들
    @Override
    public Filter getFilter() {

        if(listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override   //필터링 수행 메소드, 필터링된 결과 리스트를 result 에 담아 리턴한다.
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults() ;

            //검색 결과가 없을 경우
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
                //검색 결과에 맞는 정보와 수를 담는다.
                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override   //performFiltering 에서의 필터링 된 결과를 리스트뷰에 갱신
        protected void publishResults(CharSequence constraint, FilterResults results) {


            filteredItemList = (ArrayList<SearchListDto>) results.values ;

            //검색 결과가 있을 경우
            if (results.count > 0) {
                notifyDataSetChanged() ;

            //검색 결과가 없을 경우
            } else {
                notifyDataSetInvalidated() ;
            }

        }
    }


}
