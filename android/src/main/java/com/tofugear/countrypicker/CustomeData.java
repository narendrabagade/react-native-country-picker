package com.tofugear.countrypicker;

import com.facebook.react.bridge.ReadableArray;

import java.io.Serializable;

/**
 * Created by nabagade on 3/15/17.
 */
public class CustomeData implements Serializable {

    ReadableArray jsInput;

    public ReadableArray getJsInput() {
        return jsInput;
    }

    public void setJsInput(ReadableArray jsInput) {
        this.jsInput = jsInput;
    }
}
