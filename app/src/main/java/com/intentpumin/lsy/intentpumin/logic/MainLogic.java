package com.intentpumin.lsy.intentpumin.logic;

import com.intentpumin.lsy.intentpumin.tools.data.LoginRequestMsg;

/**
 * Created by yang on 2016/5/3.
 */
public class MainLogic extends BaseLogic {
    /**
     * 运维人员接口
     */
    //登录
    public final static String LOGIN= HOST_URL + "/login";
    //注销
    public final static String LOGOUT= HOST_URL + "/logout";
    //任务清单
    public final static String GET_TASK_LIST= HOST_URL + "/get_task_list_m";
    //任务明细
    public final static String GET_TASK= HOST_URL + "/get_task_list_d";
    //任务上传
    public final static String SET_TASK= HOST_URL + "/set_task_status";
    //数据明细
    public final static String GET_STAT=HOST_URL+ "/get_stat_list_d";
    //数据上传
    public final static String SET_STAT=HOST_URL+"/set_stat_info";
    /**
   * 管理人员接口
   */
    //管理人员获取全部设备接口
    public final static String GET_EQPT=HOST_URL+"/get_eqpt_list";
    //管理人员获取单个设备下多个状态的接口
    public final static String GET_EQPT_STAT=HOST_URL+"/get_stat_list_by_eqpt";
    //管理人员获取指定日期及设备信息下的状态趋势图
    public final static String GET_VALUE=HOST_URL+"/get_stat_value";

    public final static MainLogic sInstance = new MainLogic();

    public void requestLogin(String phoneno,String pswd){
        LoginRequestMsg loginRequestMsg=new LoginRequestMsg();
        loginRequestMsg.setPhoneno(phoneno);
        loginRequestMsg.setPswd(pswd);
    }

}
