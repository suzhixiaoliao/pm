package com.intentpumin.lsy.intentpumin.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intentpumin.lsy.intentpumin.R;
import com.intentpumin.lsy.intentpumin.TaskslistActivity;
import com.intentpumin.lsy.intentpumin.network.LogUtils;
import com.intentpumin.lsy.intentpumin.tools.d_exec_m;
import com.intentpumin.lsy.intentpumin.tools.items;
import com.intentpumin.lsy.intentpumin.zxing.CaptureActivity;

import java.util.List;

/**
 * Created by yang on 2016/4/5.
 */
public class TasklistAdapter extends BaseAdapter {
    private SharedPreferences sp;
    private static final String TAG = "TasklistAdapter";
    private List<items> list;
    private Context context;
    private LayoutInflater inflater;//布局填充器。生成所对应的view对象，系统内置

    public TasklistAdapter(Context context, List<items> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tastlist, null);
            hodler.tv_time = (TextView) convertView.findViewById(R.id.tv_tasklist_time);
            hodler.tv_device = (TextView) convertView.findViewById(R.id.tv_tasklist_device);
            hodler.tv_Location = (TextView) convertView.findViewById(R.id.tv_tasklist_Location);
            ImageView bt_tasklist_scan = (ImageView) convertView.findViewById(R.id.bt_tasklist_scan);
            bt_tasklist_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CaptureActivity.class);
                    context.startActivity(intent);
                }
            });

        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        hodler.tv_time.setText(list.get(i).getDate());
        hodler.tv_device.setText(list.get(i).getEqpt_name());
        hodler.tv_Location.setText(list.get(i).getLoct_name());
       /* tv_tasklist_device.setText(info.get(position).getEQPT_NAME());
        tv_tasklist_Location.setText(info.get(position).getLOCT_NAME());
        tv_tasklist_time.setText(info.get(position).getDATE());*/
      /*  hodler.tv_device.setText("DDC-1");
        hodler.tv_Location.setText("长寿路");
        hodler.tv_time.setText("2012-04-08");*/
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

    public void setItems(List<items> list) {
        this.list = list;
        notifyDataSetChanged();
        if (list!=null&&list.size()>0){
        System.out.println("list的size是"+list.size());}else {
            System.out.println("list的null");
        }
    }

    class ViewHodler {
        private TextView tv_time;
        private TextView tv_device;
        private TextView tv_Location;
    }
}

