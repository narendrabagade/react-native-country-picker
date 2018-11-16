package com.tofugear.countrypicker;

import com.facebook.react.bridge.ReadableMap;

/**
 * Inform the client which country has been selected
 *
 */
public interface CountryPickerListener {
	// public void onSelectCountry(String code,String name, ReadableMap response);
	public void onSelectCountry(ReadableMap response);
}
