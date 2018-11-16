package com.tofugear.countrypicker;
import android.app.Activity;
import android.app.FragmentManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.UiThreadUtil;

public class CountryPickerModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
   private CountryPicker mostRecentCountryPicker;

    // note that webView.isPaused() is not Xwalk compatible, so tracking it poor-man style
    private boolean isPaused;

    public CountryPickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
       reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "CountryPicker";
    }

    @ReactMethod
    public void show(final ReadableMap readableMap,final Callback callback) throws Exception {
        if (this.isPaused) {
            return;
        }
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return;
        }
        ReadableArray array = readableMap.getArray("jsonData");
        final CustomeData customeData = new CustomeData();
        if(array !=null){
            customeData.setJsInput(array);
        }
        UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    final CountryPicker picker = CountryPicker.newInstance(readableMap,customeData);
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(ReadableMap data) {
                            WritableMap response= Arguments.createMap();
                            WritableMap mapData = recursivelyDeconstructReadableMap(data);
                            picker.dismiss();
//                            response.putString("code",code);
//                            response.putString("name",name);
                            response.putMap("selectedObject",mapData);
                            callback.invoke(response);
                          }
                    });
                    mostRecentCountryPicker = picker;
                    picker.show(currentActivity.getFragmentManager(), "COUNTRY_PICKER");
                }
        });
    }

    @ReactMethod
    public void hide() throws Exception {
        if (mostRecentCountryPicker != null) {
            //mostRecentCountryPicker.cancel();
        }
    }

    @Override
    public void initialize() {
        getReactApplicationContext().addLifecycleEventListener(this);
    }


    @Override
    public void onHostPause() {
        if (mostRecentCountryPicker != null) {
            //mostRecentCountryPicker.cancel();
        }
        this.isPaused = true;
    }

    @Override
    public void onHostResume() {
        this.isPaused = false;
    }

    @Override
    public void onHostDestroy() {
        this.isPaused = true;
    }


    private WritableMap recursivelyDeconstructReadableMap(ReadableMap readableMap) {
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        WritableMap deconstructedMap = Arguments.createMap();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);
            switch (type) {
                case Null:
                    deconstructedMap.putNull(key);
                    break;
                case Boolean:
                    deconstructedMap.putBoolean(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    deconstructedMap.putDouble(key, readableMap.getDouble(key));
                    break;
                case String:
                    deconstructedMap.putString(key, readableMap.getString(key));
                    break;
                case Map:
                    deconstructedMap.putMap(key, recursivelyDeconstructReadableMap(readableMap.getMap(key)));
                    break;
                case Array:
                    deconstructedMap.putArray(key, recursivelyDeconstructReadableArray(readableMap.getArray(key)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }

        }
        return deconstructedMap;
    }

    private WritableArray recursivelyDeconstructReadableArray(ReadableArray readableArray) {
        WritableArray deconstructedList = Arguments.createArray();
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType indexType = readableArray.getType(i);
            switch(indexType) {
                case Null:
                    deconstructedList.pushNull();
                    break;
                case Boolean:
                    deconstructedList.pushBoolean(readableArray.getBoolean(i));
                    break;
                case Number:
                    deconstructedList.pushDouble(readableArray.getDouble(i));
                    break;
                case String:
                    deconstructedList.pushString(readableArray.getString(i));
                    break;
                case Map:
                    deconstructedList.pushMap(recursivelyDeconstructReadableMap(readableArray.getMap(i)));
                    break;
                case Array:
                    deconstructedList.pushArray(recursivelyDeconstructReadableArray(readableArray.getArray(i)));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object at index " + i + ".");
            }
        }
        return deconstructedList;
    }
}
