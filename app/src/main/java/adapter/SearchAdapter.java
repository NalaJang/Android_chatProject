package adapter;

/*
public class SearchAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflate.inflate(R.layout.row_listview, null);

            viewHolder = new ViewHolder();
            viewHolder.label = convertView.findViewById(R.id.image_listView);
            viewHolder.label = convertView.findViewById(R.id.id_listView);
            viewHolder.label = convertView.findViewById(R.id.content_listView);

        } else {

            viewHolder = (ViewHolder)convertView.getTag();
        }
        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position));

        return convertView;
    }

    class ViewHolder{
        public TextView label;
    }


}
*/