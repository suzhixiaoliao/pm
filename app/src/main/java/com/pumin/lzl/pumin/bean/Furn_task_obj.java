package com.pumin.lzl.pumin.bean;

/**
 * 作者：lzl on 2016/7/29 11:16
 * 邮箱：zhilin——comeon@163.com
 */
public class Furn_task_obj {
    private String furn_task_name;
    private String furn_task_id;

    public Furn_task_obj(String furn_task_name, String furn_task_id) {
        this.furn_task_name = furn_task_name;
        this.furn_task_id=furn_task_id;
    }

    public String getFurn_task_id() {
        return furn_task_id;
    }

    public void setFurn_task_id(String furn_task_id) {
        this.furn_task_id = furn_task_id;
    }

    public String getFurn_task_name() {
        return furn_task_name;
    }

    public void setFurn_task_name(String furn_task_name) {
        this.furn_task_name = furn_task_name;
    }
}
