package com.rc.abovesound.model;

/**
 * Md. Rashadul Alam
 */
public class ResponseUserBuyMusic extends ResponseBase {

    private String status = "";
    private String msg = "";

    public ResponseUserBuyMusic(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResponseUserBuyMusic{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
