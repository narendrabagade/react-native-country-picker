package com.tofugear.countrypicker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class CountryPicker extends DialogFragment implements
		Comparator<ResponseData> {
	/**
	 * View components
	 */
	private EditText searchEditText;
	private ListView countryListView;
	static CustomeData customeData;

	/**
	 * Adapter for the listview
	 */
	private CountryListAdapter adapter;

	/**
	 * Hold all data, sorted by name
	 */

	private List<ResponseData> responseDataList;

	/**
	 * Hold data that matched user query
	 */
	private List<ResponseData> selectedResponseData;

	/**
	 * Listener to which country user selected
	 */
	private CountryPickerListener listener;

	/**
	 * Set listener
	 *
	 * @param listener
	 */
	public void setListener(CountryPickerListener listener) {
		this.listener = listener;
	}

	public EditText getSearchEditText() {
		return searchEditText;
	}

	public ListView getCountryListView() {
		return countryListView;
	}

	/**
	 * Convenient function to get currency code from country code currency code
	 * is in English locale
	 *
	 * @param countryCode
	 * @return
	 */
	public static Currency getCurrencyCode(String countryCode) {
		try {
			return Currency.getInstance(new Locale("en", countryCode));
		} catch (Exception e) {

		}
		return null;
	}

	private List<ResponseData> getData(CustomeData customeData,String codeKey,String nameKey) {
		if(customeData == null)
		{
			return null;
		}
		if (responseDataList == null) {
			try {
				responseDataList = new ArrayList<ResponseData>();

				ReadableArray array = customeData.getJsInput();

				for(int i=0 ;i < array.size();i++){
					ReadableMap readableMap = array.getMap(i);
					ResponseData responseData = new ResponseData();
					if(codeKey!=null && !codeKey.equals("") && readableMap.hasKey(codeKey)){
						responseData.setCode(readableMap.getString(codeKey));
					}else{
						responseData.setCode("");
					}
					if(nameKey !=null && !nameKey.equals("") && readableMap.hasKey(nameKey)){
						responseData.setName(readableMap.getString(nameKey));
					}else{
						responseData.setName("");
					}
					responseData.setResponse(readableMap);
					responseDataList.add(responseData);
				}
				// Sort the all countries list based on country name
				// Collections.sort(responseDataList,this);

				// Initialize selected countries with all countries
				selectedResponseData = new ArrayList<ResponseData>();
				selectedResponseData.addAll(responseDataList);

				// Return
				return responseDataList;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * To support show as dialog
	 *
	 */
	public static CountryPicker newInstance(ReadableMap readableMap,CustomeData customeData) {
		CountryPicker.customeData = customeData;
		CountryPicker picker = new CountryPicker();
		Bundle bundle = new Bundle();
		bundle.putString("dialogTitle", readableMap.getString("title"));
		bundle.putBoolean("isImageRequired", readableMap.getBoolean("isImageRequired"));
		bundle.putBoolean("isSearchable", readableMap.getBoolean("isSearchable"));
		//bundle.putSerializable("data",customeData);
		if(!readableMap.hasKey("codeKey")  || (readableMap.getString("codeKey") == null || readableMap.getString("codeKey").equals(""))){
			bundle.putString("codeKey", "");
		}else if(readableMap.hasKey("codeKey")){
			bundle.putString("codeKey", readableMap.getString("codeKey"));
		}
		if(!readableMap.hasKey("nameKey") || (readableMap.getString("nameKey") == null || readableMap.getString("nameKey").equals(""))){
			bundle.putString("nameKey", "");
		}else{
			bundle.putString("nameKey", readableMap.getString("nameKey"));
		}
		picker.setArguments(bundle);
		return picker;
	}

	/**
	 * Create view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate view
		View view = inflater.inflate(R.layout.country_picker, null);
		// Set dialog title if show as dialog
		Bundle args = getArguments();
		boolean isSearchable=false;
		boolean isImageRequired=false;
		CustomeData customeData = CountryPicker.customeData;
		String codeKey = "";
		String nameKey = "";
		if (args != null) {
			isImageRequired = args.getBoolean("isImageRequired");
			String dialogTitle = args.getString("dialogTitle");
			isSearchable = args.getBoolean("isSearchable");
			//customeData = (CustomeData) args.getSerializable("data");
			if(args.getString("codeKey") !=null){
				codeKey=args.getString("codeKey");
			}
			if(args.getString("nameKey") !=null){
				nameKey=args.getString("nameKey");
			}
			getDialog().setTitle(dialogTitle);

			int width = getResources().getDimensionPixelSize(
					R.dimen.cp_dialog_width);
			int height = getResources().getDimensionPixelSize(
					R.dimen.cp_dialog_height);
			getDialog().getWindow().setLayout(width, height);
		}
		getData(customeData,codeKey,nameKey);
		// Get view components
		searchEditText = (EditText) view
				.findViewById(R.id.country_picker_search);
		if(!isSearchable){
			searchEditText.setVisibility(View.GONE);
		}
		countryListView = (ListView) view
				.findViewById(R.id.country_picker_listview);

		// Set adapter
		adapter = new CountryListAdapter(getActivity(), selectedResponseData,isImageRequired);
		countryListView.setAdapter(adapter);

		// Inform listener
		countryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listener != null) {
					ResponseData data = selectedResponseData.get(position);
//					listener.onSelectCountry(data.getCode(),data.getName(),
//							data.getResponse());
					listener.onSelectCountry(data.getResponse());


				}
			}
		});

		// Search for which countries matched user query
		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				search(s.toString());
			}
		});
		setCancelable(false);
		return view;
	}

	/**
	 * Search allCountriesList contains text and put result into
	 * selectedCountriesList
	 *
	 * @param text
	 */
	@SuppressLint("DefaultLocale")
	private void search(String text) {
		selectedResponseData.clear();

		for (ResponseData data : responseDataList) {
			if (data.getName().toLowerCase(Locale.ENGLISH)
					.contains(text.toLowerCase())) {
				selectedResponseData.add(data);
			}
		}

		adapter.notifyDataSetChanged();
	}

	/**
	 * Support sorting the countries list
	 */
	@Override
	public int compare(ResponseData lhs, ResponseData rhs) {
		return lhs.getName().compareTo(rhs.getName());
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		try {
			FragmentTransaction ft = manager.beginTransaction();
			ft.add(this, tag);
			ft.commitAllowingStateLoss();
		} catch (IllegalStateException e) {
			Log.i("CameraModule", "Exception in picker dialog", e);
		}
	}
}
