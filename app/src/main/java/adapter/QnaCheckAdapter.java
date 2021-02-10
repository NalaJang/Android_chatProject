package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatproject5.QnaContentActivity;
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

import chat.Constants;
import dto.QnaDto;

public class QnaCheckAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QnaDto> qnaList;

    public QnaCheckAdapter(ArrayList<QnaDto> qnaList, Context context) {
        this.context = context;
        this.qnaList = qnaList;
    }

    @Override
    public int getCount() {
        return qnaList.size();
    }

    @Override
    public Object getItem(int position) {
        return qnaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(QnaDto item) {
        qnaList.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.qnachecklist_item, parent, false);

        }

        //위치 데이터 참조
        QnaDto qnaDto = qnaList.get(position);

        TextView indate = convertView.findViewById(R.id.indate_qnaCheck);
        TextView title = convertView.findViewById(R.id.title_qnaCheck);
        TextView num = convertView.findViewById(R.id.num__qnaCheck);
        TextView result = convertView.findViewById(R.id.result_qnaCheck);

        indate.setText(qnaDto.getIndate());
        title.setText(qnaDto.getTitle());
        num.setText(qnaDto.getNum());
        result.setText(qnaDto.getResult());



        switch (qnaDto.getResult()) {
            case "1" :
                result.setText("답변 준비중");
                result.setTextColor(Color.parseColor("black"));
                break;

            case "2" :
                result.setText("답변 완료");
                result.setTextColor(Color.parseColor("#D81B60"));
                break;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String urlStr = Constants.SERVER_URL + "qnaContent.do";

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        clickQna(urlStr, Integer.parseInt(num.getText().toString()));
                    }
                }).start();

            }
        });

        return convertView;
    }


    //문의 내용 보기
   public void clickQna(String urlStr, int num) {
        StringBuilder output = new StringBuilder();

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                OutputStream outputStream = conn.getOutputStream();
                String params = "num=" + num; //int 로 고쳐주기

                outputStream.write(params.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

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
        setQnaList(output.toString());
    }

    public void setQnaList(String str) {
        Document doc = Jsoup.parse(str);
        Elements email = doc.select("ol > li.email");
        Elements subject = doc.select("ol > li.subject");
        Elements title = doc.select("ol > li.title");
        Elements content = doc.select("ol > li.content");
        Elements indate = doc.select("ol > li.indate");
        Elements result = doc.select("ol > li.result");


        Intent intent = new Intent(context, QnaContentActivity.class);
        intent.putExtra("email", email.text());
        intent.putExtra("subject", subject.text());
        intent.putExtra("title", title.text());
        intent.putExtra("content", content.text());
        intent.putExtra("indate", indate.text());
        intent.putExtra("result", result.text());

        context.startActivity(intent);
    }
}
