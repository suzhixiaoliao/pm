package com.intentpumin.lsy.intentpumin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intentpumin.lsy.intentpumin.activity.BaseActivity;
import com.intentpumin.lsy.intentpumin.adapter.StatListAdapter;
import com.intentpumin.lsy.intentpumin.adapter.TaskGridAdapter;
import com.intentpumin.lsy.intentpumin.http.HttpUtil;
import com.intentpumin.lsy.intentpumin.logic.MainLogic;
import com.intentpumin.lsy.intentpumin.network.LogUtils;
import com.intentpumin.lsy.intentpumin.tools.stat.result_stat_get;
import com.intentpumin.lsy.intentpumin.tools.stat.stat_get;
import com.intentpumin.lsy.intentpumin.tools.task.result_task_get;
import com.intentpumin.lsy.intentpumin.tools.device.result_device_items;
import com.intentpumin.lsy.intentpumin.tools.task.task_get;
import com.intentpumin.lsy.intentpumin.tools.logindate.login;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.okhttpfinal.StringHttpRequestCallback;

public class DataExecuteTasksActivity extends BaseActivity{
    private GridView mtasklist;
    private ListView mdatalist;
    private TaskGridAdapter adapter;
    private RelativeLayout inflater;
    // TODO: 2016/6/21
    private StatListAdapter dataadapter;
    private List<task_get> mtask;//任务
    private List<stat_get> mstat;//数据
    SharedPreferences sp;
    Context mContext;
    private TextView tv_queding;
    private TextView tv_fanhui;
    public LocationClient mClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类
    double Mapx, Mapy;
    String result = "";

    private com.intentpumin.lsy.intentpumin.tools.device.items items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    protected void setupData() {
        sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        result = this.getIntent().getStringExtra("result");
        Log.e("TAG", "===========result==========" + result);
        mContext = this;
        setContentView(R.layout.activity_data_execute_tasks, R.string.mDataExecuteTasks,MODE_BACK_NAVIGATION);

        init();
        initAction();
        initLocation();
    }
    private void initLocation() {
        mClient = new LocationClient(this);
        mClient.start();
        mClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location.getLocType() == BDLocation.TypeServerError
                        || location.getLocType() == BDLocation.TypeCriteriaException
                        ) {
                    Mapx = 0;
                    Mapy = 0;
                } else {
                    Mapx = location.getLongitude();
                    Mapy = location.getLatitude();
                    LogUtils.LOGE("x", "" + Mapx);
                    LogUtils.LOGE("y", "" + Mapy);
                }
            }
        });
        LocationClientOption option = new LocationClientOption();
        mMyLocationListener = new MyLocationListener();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);//返回定位结果包含地址信息
        option.setNeedDeviceDirect(true);
        mClient.setLocOption(option);
        mClient.registerLocationListener(mMyLocationListener);
    }

    private void initAction() {
        mtasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //最后提交的时候，需要循环数组把N1和Y1对应的变为N
                String finished = mtask.get(position).getFinished();
                if (finished.equals("N")) {
                    mtask.get(position).setFinished("Y");
                } else if (finished.equals("Y")) {
                    mtask.get(position).setFinished("N");
                }

                adapter.notifyDataSetChanged();
                //  dataadapter.notifyDataSetChanged();
            }
        });

    }

    private void init() {
        mtasklist = (GridView) findViewById(R.id.gv_task);
        mtasklist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                task_get task_get =mtask.get(position);
                Intent inter = getIntent();
                inter.setClass(DataExecuteTasksActivity.this, TaskRemarkActivity.class);
                inter.putExtra("task", (Serializable) task_get);
                startActivityForResult(inter, 1);
                return true;
            }

        });
        mdatalist = (ListView) findViewById(R.id.gv_shuju);


        tv_queding = (TextView) findViewById(R.id.tv_queding);
        tv_fanhui = (TextView) findViewById(R.id.tv_fanhui);
        tv_fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DataExecuteTasksActivity.this, MipcaActivityCapture.class);
                startActivity(i);
            }
        });
        mtask = new ArrayList<>();
        mstat = new ArrayList<>();
        int ScreeWidth = getWindowManager().getDefaultDisplay().getWidth();//获取屏幕的宽度
        adapter = new TaskGridAdapter(this, mtask, ScreeWidth / 21);

        if (mtasklist == null || adapter == null) {
        }
        mtasklist.setAdapter(adapter);
        requestData(null);
        mstat = new ArrayList<>();
        dataadapter = new StatListAdapter(this, mstat);
        mtasklist.setAdapter(adapter);
      if (mdatalist == null || dataadapter == null) {
        }
        mdatalist.setAdapter(dataadapter);
        requestvalue();
        tv_queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestqueding();
            }
        });
    }

    // TODO: 2016/6/22     获取数据接口
    private void requestvalue() {
        final login mlogin = (login) getIntent().getSerializableExtra("login");
        RequestParams params = new RequestParams();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String exec_time = sDateFormat.format(new java.util.Date());
        final String date = exec_time;
        String phoneno = "13000000000";
        sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String mPhoneno= sp.getString("phoneno","");
        String area_id = " ";
        String eqpt_id =result;
        params.addFormDataPart("signature", 1);
        params.addFormDataPart("date",date);
        params.addFormDataPart("phoneno", mPhoneno);
        params.addFormDataPart("area_id", area_id);
        params.addFormDataPart("eqpt_id", eqpt_id);
        HttpUtil.getInstance().post(MainLogic.GET_STAT, params, new StringHttpRequestCallback() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            protected void onSuccess(String s) {
                // TODO: 2016/6/22 获取数据接口返回服务器的数据
                System.out.println("onSuccess=======" + s);

                result_stat_get resulut = null;

                try {
                    if (!TextUtils.isEmpty(s)) {
                        Gson gson = new Gson();

                        resulut = gson.fromJson(s, result_stat_get.class);//将JSON数据转成Result对象
                        // TODO: 2016/6/22 保存Sp数据
                        SharedPreferences sp = getSharedPreferences("lsy", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("resulut", s).commit();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    System.out.println("解析异常");
                }
                System.out.println(s);

                if (resulut != null && resulut.getData() != null && resulut.getData().getItems() != null) {
                    mstat.addAll(resulut.getData().getItems());
                    Log.d("mlist", "" + mstat.size());
                }

                setAutoGridViewWidth();
                dataadapter.notifyDataSetChanged();
//                LogUtils.LOGD("login4", mdata.toString());
            }

            /**
             *根据屏幕适配，动态设置GridVeiw的宽度。。。
             */
            private void setAutoGridViewWidth() {
                int size = mtask.size();
                Log.e("log", "GridView_Size:" + size);
                DisplayMetrics dm = new DisplayMetrics();
                int ScreeWidth = getWindowManager().getDefaultDisplay().getWidth();//获取屏幕的宽度
                //int itemWidth = (int) (ScreeWidth / 3.0-ScreeWidth/21.0);
                int itemWidth = (int) (ScreeWidth / 3.0);
                int allWidth = itemWidth * size;
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) mtasklist.getLayoutParams();
                params.width=allWidth;
                params.height=LinearLayout.LayoutParams.FILL_PARENT;
                mtasklist.setLayoutParams(params);
                mtasklist.setColumnWidth(itemWidth);
                //  mtasklist.setHorizontalSpacing(10);
                mtasklist.setStretchMode(GridView.NO_STRETCH);
                mtasklist.setNumColumns(size);
            }


            @Override
            public void onFinish() {
                //结束刷新
                // swipe.setRefreshing(false);
                System.out.println("完成");
            }
        });

    }

    //获取任务接口
    private void requestData(String eqptid) {

        //获取Sp数据
       /* SharedPreferences sp = getSharedPreferences("lsy", Activity.MODE_PRIVATE);
        String tastList = sp.getString("TastList", "");
        //进行解析
        Type type = new TypeToken<result_device_items>() {
        }.getType();
        Gson gson = new Gson();
        result_device_items b = gson.fromJson(tastList, type);
        final login mlogin = (login) getIntent().getSerializableExtra("login");*/
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String exec_time = sDateFormat.format(new java.util.Date());
        sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String mPhoneno= sp.getString("phoneno", "");
        RequestParams params = new RequestParams();
        String phoneno ="13000000000";
        String date = exec_time;
        String area_id = "";
        String eqpt_id = result;
        params.addFormDataPart("signature", 1);
        params.addFormDataPart("date", date);
        params.addFormDataPart("phoneno", mPhoneno);
        params.addFormDataPart("area_id", area_id);
        params.addFormDataPart("eqpt_id", eqpt_id);
        HttpUtil.getInstance().post(MainLogic.GET_TASK, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                // TODO: 2016/6/22 获取任务接口的服务器返回的数据
                System.out.println("onSuccess=======" + s);
                result_task_get result = null;
                try {
                    if (!TextUtils.isEmpty(s)) {
                        Gson gson = new Gson();
                        result = gson.fromJson(s, result_task_get.class);//将JSON数据转成Result对象
                        // TODO: 2016/6/22 保存Sp数据
                        SharedPreferences sp = getSharedPreferences("task", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("task", s).commit();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    System.out.println("解析异常");
                }


                System.out.println("task list " + s);

                mtask = result.getData().items;
                adapter.setData(mtask);
//                adapter.setItems(mtask);
                adapter.notifyDataSetChanged();
                LogUtils.LOGD("login4", mtask.toString());
            }

            @Override
            public void onFinish() {
                //结束刷新
                // swipe.setRefreshing(false);
                System.out.println("完成");
            }
        });

    }


    //向服务器发送数据进行保存
    private void requestqueding() {

        postTask();
        postStat(null);


    }

    // TODO: 2016/6/22  上传数据接口

    private void postStat(stat_get stat) {

        // TODO: 2016/6/22 获取SP数据
        //获取Sp数据
        SharedPreferences sp = getSharedPreferences("lsy", Activity.MODE_PRIVATE);
        String resulut = sp.getString("resulut", "");
        //进行解析
        Type type = new TypeToken<result_stat_get>() {
        }.getType();
        Gson gson = new Gson();
        result_stat_get s = gson.fromJson(resulut, type);
        sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String mPhoneno= sp.getString("phoneno", "");
        if (mstat != null && mstat.size() > 0) {
            for (int i = 0; i < mstat.size(); i++) {
                final int j = i;
                stat_get item = mstat.get(i);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String exec_time = sDateFormat.format(new java.util.Date());
                   String mdate = s.getData().getItems().get(i).getDate().substring(0, 10);
                String area_id = s.getData().getItems().get(i).getArea_id();
                String eqpt_id = s.getData().getItems().get(i).getEqpt_id();
                String stat_id = s.getData().getItems().get(i).getStat_id();
                String r_value =s.getData().getItems().get(i).getR_value();
                String signature="1";
                RequestParams params = new RequestParams();
                params.addFormDataPart("signature", signature);
                params.addFormDataPart("date", mdate);
                params.addFormDataPart("area_id", area_id);
                params.addFormDataPart("eqpt_id", eqpt_id);
                params.addFormDataPart("stat_id", stat_id);
                // TODO: 2016/6/21 传值
                params.addFormDataPart("r_value",item.getR_value());
                if (item.getFinished().equals("Y1")) {
                    params.addFormDataPart("finished", "Y");
                    System.out.println("llllllllllll" + item.getFinished());
                } else if (item.getFinished().equals("N1")) {
                    params.addFormDataPart("finished", "N");
                    System.out.println("llllllllllll" + item.getFinished());
                } else {
                    params.addFormDataPart("finished", item.getFinished());
                }
                params.addFormDataPart("spot_x", Mapx);
                System.out.println("llllllllllll" + Mapx);
                params.addFormDataPart("spot_y", Mapy);
                params.addFormDataPart("phoneno",mPhoneno);
                params.addFormDataPart("exec_time", exec_time);
                HttpUtil.getInstance().post(MainLogic.SET_STAT, params, new StringHttpRequestCallback() {
                    @Override
                    protected void onSuccess(String s) {
                        System.out.println("onSuccess=======" + s);
                        if (mstat.get(j).getFinished().equals("Y1")) {
                            mstat.get(j).setFinished("N");
                            LogUtils.LOGD("login8", s.toString());
                        }
                        if (mstat.get(j).getFinished().equals("N1")) {
                            mstat.get(j).setFinished("Y");
                            LogUtils.LOGD("login9", s.toString());
                        }

                        System.out.println(s);
                        LogUtils.LOGD("login6", s.toString());
                        Toast.makeText(DataExecuteTasksActivity.this, "保存数据成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {

                        System.out.println("postDate失败");
                    }
                });
//                }
            }
        }
    }

    // TODO: 2016/6/22 上传任务接口
    //任务上传
    private void postTask() {
        // TODO: 2016/6/22 获取SP数据
        //获取Sp数据
        SharedPreferences sp = getSharedPreferences("lsytask", Activity.MODE_PRIVATE);
        String result = sp.getString("result", "");
        //进行解析
        Type type = new TypeToken<result_task_get>() {
        }.getType();
        Gson gson = new Gson();
        result_task_get b = gson.fromJson(result, type);
        if (mtask != null && mtask.size() > 0) {
            for (int i = 0; i < mtask.size(); i++) {
                final int j = i;
                task_get item = mtask.get(i);
                System.out.println("lsy" + i);
                sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
                String mPhoneno= sp.getString("phoneno","");
                RequestParams params = new RequestParams();
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String exec_time = sDateFormat.format(new java.util.Date());
                String mdate = b.getData().items.get(i).date;
               // String mdate=b.getData().getItems().get(i).getDate();
                 String date = mdate.substring(0,10);

                 String area_id = b.getData().items.get(i).area_id;
                //String area_id = "47875310-1A24-2B35-2783-AE12D8334E2D";
                String eqpt_id = b.getData().items.get(i).eqpt_id;
               // String eqpt_id = "47875315-1A24-2B35-2783-AE19D7334E2D";
                 String task_id = b.getData().items.get(i).task_id;
                //String task_id = "0456DAB3-6A37-FCAC-33C8-31FEA4B4B43E";
                //String finished="Y";
                String phoneno = mPhoneno;
                String signature = "1";
               // String date = "2016-06-21";
                params.addFormDataPart("phoneno", phoneno);
                params.addFormDataPart("date", date);
                params.addFormDataPart("signature", signature);
                params.addFormDataPart("area_id", area_id);
                params.addFormDataPart("eqpt_id", eqpt_id);
                params.addFormDataPart("task_id", task_id);
                //params.addFormDataPart("finished",finished);
                // params.addFormDataPart("finished", b.getData().getItems().get(i).getFinished());
                if (item.getFinished().equals("Y1")) {
                    params.addFormDataPart("finished", "Y");
                    System.out.println("llllllllllll" + item.getFinished().toString());
                } else if (item.getFinished().equals("N1")) {
                    params.addFormDataPart("finished", "N");
                    System.out.println("llllllllllll" + item.getFinished().toString());
                } else {
                    params.addFormDataPart("finished", item.getFinished());
                }
                params.addFormDataPart("spot_x", Mapx);
                params.addFormDataPart("spot_y", Mapy);
                params.addFormDataPart("exec_time", exec_time);
                HttpUtil.getInstance().post(MainLogic.SET_TASK, params, new StringHttpRequestCallback() {
                    @Override
                    protected void onSuccess(String s) {
                        System.out.println("onSuccess=======" + s);
                        System.out.println(s);
                        LogUtils.LOGD("login6", s.toString());
                        Toast.makeText(DataExecuteTasksActivity.this, "保存任务成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFinish() {

                        System.out.println("postSave失败");
                    }
                });
            }
        }
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());//获得当前时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());//获得erro code得知定位现状
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//获得纬度
            Mapy = location.getLatitude();
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//获得经度
            Mapx = location.getLongitude();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {//通过GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());//获得速度
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {//通过网络连接定位
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());//获得经营商？
            }
            Log.i("BaiduLocation", sb.toString());
        }
    }
}
