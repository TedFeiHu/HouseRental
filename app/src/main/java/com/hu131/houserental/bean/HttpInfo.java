package com.hu131.houserental.bean;

import java.util.List;

/**
 * json请求最外层
 * Created by Hu131 on 2016/5/15.
 */
public class HttpInfo {
    String resultcode;
    String reason;
    List<HouseInfo> result;
    String error_code;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<HouseInfo> getResult() {
        return result;
    }

    public void setResult(List<HouseInfo> result) {
        this.result = result;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
