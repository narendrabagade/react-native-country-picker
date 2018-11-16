package com.tofugear.countrypicker;

import com.facebook.react.bridge.ReadableMap;

/**
 * Created by nabagade on 3/15/17.
 */
public class ResponseData {
    private String code;
    private String name;
    private ReadableMap response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReadableMap getResponse() {
        return response;
    }

    public void setResponse(ReadableMap response) {
        this.response = response;
    }
}
