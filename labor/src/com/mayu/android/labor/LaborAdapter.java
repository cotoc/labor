package com.mayu.android.labor;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LaborAdapter extends ArrayAdapter<LaborInfo> {

	private List<LaborInfo> mListdata = null;
	private Context mContext;
	private LayoutInflater inflater;

	public LaborAdapter(Context context, int textViewResourceId,
			List<LaborInfo> objects) {
		super(context, textViewResourceId, objects);

		mListdata = objects;
		mContext = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ビューを受け取る
		View view = convertView;
		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = inflater.inflate(R.layout.listrow, null);
		}
		LaborInfo item = mListdata.get(mListdata.size() - position - 1);
		if (item != null) {
			TextView time = (TextView) view.findViewById(R.id.text_time);
			TextView memo = (TextView) view.findViewById(R.id.text_memo);
			TextView interval = (TextView) view
					.findViewById(R.id.text_interval);
			TextView textDuration = (TextView) view
					.findViewById(R.id.text_duration);
			ImageView imgInputType = (ImageView) view
					.findViewById(R.id.image_condition);
			ImageView imgWarning = (ImageView) view
					.findViewById(R.id.image_level);

			time.setText(item.getStartTime().toLocaleString());
			memo.setText(item.getMemo());
			interval.setText(item.getStrInterval());

			switch (item.getInputType()) {
			case LaborInfo.LABOR:
				imgInputType.setImageResource(R.drawable.ic_hart);
				if (item.getDuration() > 0) {
					textDuration.setTextColor(mContext.getResources().getColor(
							R.color.color_text));
					textDuration
							.setText(Long.toString(item.getDuration() / 1000)
									+ "秒");
				} else {
					// 陣痛中
					textDuration.setTextColor(mContext.getResources().getColor(
							R.color.color_text_sub));
					textDuration.setTextColor(Color.YELLOW);
					textDuration.setText("Pain Now!");
				}

				break;
			case LaborInfo.MARK:
				imgInputType.setImageResource(R.drawable.ic_heart_orange);
				break;
			case LaborInfo.RUPTURE:
				imgInputType.setImageResource(R.drawable.ic_water);
				break;
			case LaborInfo.BARTH:
				imgInputType.setImageResource(R.drawable.ic_family);
				break;
			default:
				imgInputType.setImageResource(R.drawable.ic_pen);
				break;
			}

			if (item.getInputType() == LaborInfo.LABOR) {
				switch (item.getWarningLevel()) {
				case 0:
					imgWarning.setImageResource(R.drawable.face_sleep);
					break;
				case 1:
					imgWarning.setImageResource(R.drawable.face_embarrassed);
					break;
				case 2:
					imgWarning.setImageResource(R.drawable.face_surprise);
					break;
				case 3:
					imgWarning.setImageResource(R.drawable.face_smile);
					break;
				case 4:
					imgWarning.setImageResource(R.drawable.face_laughing);
					break;
				case 5:
					imgWarning.setImageResource(R.drawable.face_wink);
					break;
				default:
					imgWarning.setImageResource(R.drawable.face_sleep);
					break;
				}
			} else {
				imgWarning.setVisibility(View.INVISIBLE);
				imgWarning.setMaxHeight(0);
				
				textDuration.setVisibility(View.INVISIBLE);
			}

		}
		return view;
	}

	@Override
	public void insert(LaborInfo object, int index) {
		// TODO Auto-generated method stub
		super.insert(object, index);
	}
}
