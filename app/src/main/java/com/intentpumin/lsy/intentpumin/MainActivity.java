package com.intentpumin.lsy.intentpumin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intentpumin.lsy.intentpumin.activity.BaseActivity;
import com.intentpumin.lsy.intentpumin.adapter.MainViewPagerAdapter;
import com.intentpumin.lsy.intentpumin.adapter.MainDeviceAdapter;
import com.intentpumin.lsy.intentpumin.http.HttpUtil;
import com.intentpumin.lsy.intentpumin.logic.MainLogic;
import com.intentpumin.lsy.intentpumin.network.LogUtils;
import com.intentpumin.lsy.intentpumin.tools.logindate.login;
import com.intentpumin.lsy.intentpumin.tools.maindevice.items;
import com.intentpumin.lsy.intentpumin.tools.maindevice.result_device_items;
import com.intentpumin.lsy.intentpumin.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.okhttpfinal.StringHttpRequestCallback;

public class MainActivity extends BaseActivity{
    private TextView tv_main;
    private TextView tv_return;
    private TextView tv_chakan;
    private ListView mtasklist;
    private MainDeviceAdapter adapter;
    private List<items> mdata;
    private SharedPreferences sp;
    private SwipeRefreshLayout swip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        initViewpager();
        mtasklist = (ListView) findViewById(R.id.list_tasklist);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swip.setRefreshing(false);
                //  requestData();

            }
        });
        mdata = new ArrayList<>();
        if (adapter == null) {
            adapter = new MainDeviceAdapter(this, mdata);
        }
        mtasklist.setAdapter(adapter);
        mtasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               items items=mdata.get(position);
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        requestData();

    }


    private void initViewpager() {
        final login login = new login();
        tv_main = (TextView) findViewById(R.id.tv_main);
        //第四步
        final login mlogin= (login) getIntent().getSerializableExtra("login");
        tv_main.setText(mlogin.getName() + ",您好");
        tv_return = (TextView) findViewById(R.id.tv_return);

        tv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.addFormDataPart("signature", "1");
                HttpUtil.getInstance().post(MainLogic.LOGOUT, params, new StringHttpRequestCallback() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    protected void onSuccess(String s) {
                        System.out.println(s);
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        LogUtils.LOGD("login", s.toString());

                    }

                    @Override
                    public void onFinish() {
                        System.out.println("");
                    }
                });
            }
        });
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        View view1 = LayoutInflater.from(this).inflate(R.layout.layout_yunwei_zhu, null);
      ImageView scan= (ImageView) view1.findViewById(R.id.iv_scan);
        //第四步
      scan.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent inter1 = getIntent();
              inter1.setClass(MainActivity.this, CaptureActivity.class);
              inter1.putExtra("str_all", "0");
              inter1.putExtra("login", mlogin);
              startActivity(inter1);
          }
      });
        ImageView renwu= (ImageView) view1.findViewById(R.id.iv_renwu);
         renwu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inter1 = getIntent();
                inter1.setClass(MainActivity.this, UnfinishedDeviceActivity.class);
                mlogin.getPhoneno();
                startActivity(inter1);

            }
        });
        View view2 = LayoutInflater.from(this).inflate(R.layout.layout_yunwei_two, null);
        View view3 = LayoutInflater.from(this).inflate(R.layout.layout_yunwei_there, null);

        ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter();
        adapter.setViews(views);
        viewPager.setAdapter(adapter);
    }
    private void requestData() {
        RequestParams params = new RequestParams();
        final login mlogin = (login) getIntent().getSerializableExtra("login");
        String finished = "N";
        params.addFormDataPart("signature", "1");
        params.addFormDataPart("finished", finished);
        params.addFormDataPart("phoneno", mlogin.getPhoneno());

        HttpUtil.getInstance().post(MainLogic.GET_TASK_LIST, params, new StringHttpRequestCallback() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            protected void onSuccess(String s) {
                String eqpt_name = "";
                String date = "";
                String loct_name = "";
                System.out.println("onSuccess");
                result_device_items resulut = null;
                try {
                    if (!TextUtils.isEmpty(s)) {
                        Gson gson = new Gson();
                        resulut = gson.fromJson(s, result_device_items.class);
                        //正常情况是用result.getData().getItems得到数据组，而不是直接去获取result.getData().getItems().get(0)
                        if (resulut != null && resulut.getData() != null && resulut.getData()
                                .getItems() != null && resulut.getData().getItems().size() > 0) {
                            date = resulut.getData().getItems().get(0).getDate();
                            eqpt_name = resulut.getData().getItems().get(0).getEqpt_name();
                            loct_name = resulut.getData().getItems().get(0).getLoct_name();
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                System.out.println(s);
                if (resulut != null && resulut.getData() != null && resulut.getData().getItems() != null && resulut.getData().getItems().size() > 0) {
                    mdata.addAll(resulut.getData().getItems());
                }
                adapter.setItems(mdata);
            }

            @Override
            public void onFinish() {
                //结束刷新
                swip.setRefreshing(false);
                System.out.println("完成");
            }
        });

    }
}