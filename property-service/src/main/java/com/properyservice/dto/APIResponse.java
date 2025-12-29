package com.properyservice.dto;

public class APIResponse<T> {

    private String message;
    private int status;
    private T data;

    public APIResponse() {
    }

    public APIResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
