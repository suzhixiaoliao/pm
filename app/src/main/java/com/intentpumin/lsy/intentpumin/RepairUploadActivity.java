package com.intentpumin.lsy.intentpumin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.intenpumin.lsy.intentpumin.repairs.Adapter.ImageAdapter;
import com.intenpumin.lsy.intentpumin.repairs.image.ImageHandleUtils;
import com.intenpumin.lsy.intentpumin.repairs.util.MaxLengthWatcher;
import com.intentpumin.lsy.intentpumin.activity.BaseActivity;
import com.intentpumin.lsy.intentpumin.activity.BcBaseActivity;
import com.intentpumin.lsy.intentpumin.http.HttpUtil;
import com.intentpumin.lsy.intentpumin.logic.MainLogic;
import com.intentpumin.lsy.intentpumin.tools.repairlist.Repair_item;
import com.intentpumin.lsy.intentpumin.util.SimpleDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.okhttpfinal.StringHttpRequestCallback;
import me.iwf.photopicker.PhotoPickerActivity;

public class RepairUploadActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private Repair_item items;
    private static final int REQUEST_CODE = 1;
    private GridView vPhoto;
    private FloatingActionButton vPickImage;
    private MaxLengthWatcher et_shuru;
    private List<String> photoPaths = new ArrayList<>();
    private ImageAdapter mAdapter;
    private TextView tv_fs;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupData();
    }

    @Override
    protected void setupData() {
        super.setupData();
        setContentView(R.layout.activity_repair_upload,R.string.RepairUpload,MODE_BACK_NAVIGATION);
        items = (Repair_item) getIntent().getSerializableExtra("repair_item");
        if (items != null) {
            Log.e("DataExecute", "收到了" + items.toString());
        }
        initView();
    }

    private void initView() {

        tv_fs= (TextView) findViewById(R.id.tv_shuru);
        et_shuru= (MaxLengthWatcher) findViewById(R.id.et_shuru);
        vPickImage = (FloatingActionButton) findViewById(R.id.vPickImage);
        vPhoto = (GridView) findViewById(R.id.photos);
        mAdapter = new ImageAdapter(this, photoPaths);
        vPhoto.setAdapter(mAdapter);
        vPickImage.setOnClickListener(this);
        vPhoto.setOnItemClickListener(this);
        tv_fs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });

    }


    @Override
    public void onClick(View v) {
        boolean showTakePhotoItem =true;
        int id=v.getId();
        if (id == R.id.vPickImage) {
            int imageCount;
            Toast.makeText(this, "默认选择4张", Toast.LENGTH_SHORT).show();
            imageCount = 4;
            Intent intent = ImageHandleUtils.pickMultiImage(this, imageCount, showTakePhotoItem);
            this.startActivityForResult(intent, REQUEST_CODE);
        }else {
            Intent intent =ImageHandleUtils.pickSingleImage(this,true);
            this.startActivityForResult(intent, REQUEST_CODE);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photoPaths.clear();
                photoPaths.addAll(data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS));
                mAdapter.notifyDataSetChanged();
            }
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = ImageHandleUtils.previewImage(this, (ArrayList<String>) photoPaths, position);
        startActivityForResult(intent, REQUEST_CODE);
    }
    private void initData() {

        List<File> files = new ArrayList<>();

        for (int i = 0; i < photoPaths.size(); i++) {
            files.add(new File(photoPaths.get(i)));
        }
        String date = SimpleDate.getDateNow();
        String mdate = SimpleDate.getDateNow();
        String repair_id=items.getRepair_id();
        String et_input=et_shuru.getText().toString();
        sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
        String mPhoneno= sp.getString("phoneno", "");
        String process_flag="00111";
        String signature="1";
        String u = "u";
        final RequestParams params = new RequestParams();
        for (File f : files) {
            params.addFormDataPart("u[]", f);
        }
        params.addFormDataPart("field_name", u);
        params.addFormDataPart("process_flag",process_flag);
        params.addFormDataPart("repair_id",repair_id);
        params.addFormDataPart("signature", signature);
        params.addFormDataPart("onspot_datetime",mdate);
        params.addFormDataPart("done_datetime",date);
        params.addFormDataPart("phoneno",mPhoneno);
        params.addFormDataPart("repair_comment",et_input);
        HttpUtil.getInstance().post(MainLogic.GET_REPAIR_UPLOAD, params, new StringHttpRequestCallback() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog(R.string.tip_making_picture);
            }
            @Override
            protected void onSuccess(String s) {
                // TODO: 2016/6/22 获取数据接口返回服务器的数据
                System.out.println("onSuccess=======" + s);
                System.out.println(s);
                Toast.makeText(RepairUploadActivity.this, "成功"+s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                System.out.println("完成");
                cancelProgressDialog();
            }
        });
    }
}
