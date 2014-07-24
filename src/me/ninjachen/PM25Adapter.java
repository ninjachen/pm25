package me.ninjachen;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.ninjachen.util.PM25Provider;

public class PM25Adapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater;
	private List<PM25Provider.PM25> mList;

	public PM25Adapter(Context context, List<PM25Provider.PM25> pm25List) {
		this.mLayoutInflater = LayoutInflater.from(context);
		this.mList = pm25List;
	}

	public int getCount() {
		return -1 + this.mList.size();
	}

	public Object getItem(int paramInt) {
		return this.mList.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int position, View view, ViewGroup parent) {
		PM25Provider.PM25 localPM25 = (PM25Provider.PM25) this.mList
				.get(position);
		Holder viewHolder;
		if (view == null) {
			view = this.mLayoutInflater.inflate(R.layout.paper_item, null);
			viewHolder = new Holder();
			viewHolder.txt_position_name = ((TextView) view
					.findViewById(R.id.paper_item_position_name));
			viewHolder.txt_aqi = ((TextView) view
					.findViewById(R.id.paper_item_aqi));
			viewHolder.txt_pm25 = ((TextView) view
					.findViewById(R.id.paper_item_pm25));
			viewHolder.txt_pm10 = ((TextView) view
					.findViewById(R.id.paper_item_pm10));
			view.setTag(viewHolder);
		} else {
			viewHolder = (Holder) view.getTag();
		}
		viewHolder.txt_aqi.setText(localPM25.aqi);
		viewHolder.txt_pm25.setText(localPM25.pm2_5);
		viewHolder.txt_position_name.setText(localPM25.position_name);
		viewHolder.txt_pm10.setText(localPM25.pm10);
		return view;
	}

	public static class Holder {
		TextView txt_aqi;
		TextView txt_pm10;
		TextView txt_pm25;
		TextView txt_position_name;
	}
}
