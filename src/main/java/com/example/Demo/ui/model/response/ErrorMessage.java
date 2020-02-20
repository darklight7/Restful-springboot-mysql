package com.example.Demo.ui.model.response;

import java.util.Date;

public class ErrorMessage {

    private Date timestamp;
    private String massage;

    public ErrorMessage(){}
    public ErrorMessage(Date timestamp, String massage) {
        this.timestamp = timestamp;
        this.massage = massage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

}
