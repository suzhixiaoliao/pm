package com.intentpumin.lsy.intentpumin.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.intentpumin.lsy.intentpumin.R;
import com.intentpumin.lsy.intentpumin.tools.device.items;
import com.intentpumin.lsy.intentpumin.util.Stats_icon;

import java.util.List;

/**
 * Created by yang on 2016/4/5.
 */
public class MainDeviceAdapter extends BaseAdapter {
    private SharedPreferences sp;
    private List<items> list;
    private Context context;
    private LayoutInflater inflater;//布局填充器。生成所对应的view对象，系统内置

    public MainDeviceAdapter(Context context, List<items> list) {
        super();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public Object getItem(int i) {
        if (list != null && list.size() > 0) {
            return list.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {

        if (list != null && list.size() > 0) {
            return i;
        } else {
            return 0;
        }
    }
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device_lv, null);
            hodler.tv_loct = (TextView) convertView.findViewById(R.id.tv_loct);
            hodler.tv_eqpt = (TextView) convertView.findViewById(R.id.tv_eqpt);
            hodler.tv_area = (TextView) convertView.findViewById(R.id.tv_area);
            hodler.iv_finished= (ImageView) convertView.findViewById(R.id.iv_finished);

        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        String s=list.get(i).getDate();
        String subDate=s.substring(0,10);//这里将会获得Hello
        hodler.tv_loct.setText(list.get(i).getLoct_name());
        hodler.tv_eqpt.setText(list.get(i).getEqpt_name());
        hodler.tv_area.setText(list.get(i).getArea_name());
        int resId = Stats_icon.getStatIcon(list.get(i).getFinished());
        hodler.iv_finished.setImageResource(resId);
         convertView.setTag(hodler);
        return convertView;
    }

    @Override
    public int getCount() {
        if (list!=null&&list.size()>0){
            System.out.println("`list的size是"+list.size());}else {
            System.out.println("`list的null");
        }
        return list.size();
    }

    public void setItems(List<items> mdata) {
        this.list=mdata;
        notifyDataSetChanged();
        if (list!=null&&list.size()>0){
        }
    }
    public void addDate(List<items> mlist){
        if(null!= mlist){
            this.list.addAll(mlist);
        }
    }
    class ViewHodler {
        private TextView tv_loct;
        private TextView tv_eqpt;
        private TextView tv_area;
        private ImageView iv_finished;
    }
}

