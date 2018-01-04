package com.creative.hanjung.app.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creative.hanjung.app.R;

import java.util.ArrayList;
import java.util.HashMap;


public class DriverAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private ArrayList<HashMap<String,String>> boardList;
	private ViewHolder viewHolder;
	private Context con;
	private String name="";


	public DriverAdapter(Context con , ArrayList<HashMap<String,String>> array){
		inflater = LayoutInflater.from(con);
		boardList = array;
		this.con = con;
	}

	public DriverAdapter(Context con , ArrayList<HashMap<String,String>> array, String name){
		inflater = LayoutInflater.from(con);
		boardList = array;
		this.con = con;
		this.name = name;
	}

	@Override
	public int getCount() {
		return boardList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, final View convertview, ViewGroup parent) {

		View v = convertview;

		if(v == null){
			viewHolder = new ViewHolder();

			if(name.equals("CarWrite")){
				v = inflater.inflate(R.layout.car_item, parent,false);

				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data2 = (TextView)v.findViewById(R.id.textView2);
			}else{
				v = inflater.inflate(R.layout.car_result_item, parent,false);

				viewHolder.board_data1 = (TextView)v.findViewById(R.id.textView1);
				viewHolder.board_data2 = (TextView)v.findViewById(R.id.textView2);
				viewHolder.board_data3 = (TextView)v.findViewById(R.id.textView3);
				viewHolder.board_data4 = (TextView)v.findViewById(R.id.textView4);
				viewHolder.board_data5 = (TextView)v.findViewById(R.id.textView5);
				viewHolder.board_data6 = (TextView)v.findViewById(R.id.textView6);
			}

			v.setTag(viewHolder);

		}else {
			viewHolder = (ViewHolder)v.getTag();
		}

		if(name.equals("CarWrite")){
			viewHolder.board_data1.setText(boardList.get(position).get("data2"));
			if(boardList.get(position).get("data5")!=null){
				if(boardList.get(position).get("data5").equals(0)){
					viewHolder.board_data2.setText("미처리");
				}else{
					viewHolder.board_data2.setText(boardList.get(position).get("data5"));
				}
			}else{
				viewHolder.board_data2.setText("미처리");
			}
		}else {
			viewHolder.board_data1.setText(boardList.get(position).get("data1"));
			viewHolder.board_data2.setText(boardList.get(position).get("data2"));
			viewHolder.board_data3.setText(boardList.get(position).get("data3"));
			viewHolder.board_data4.setText(boardList.get(position).get("data4"));
			viewHolder.board_data5.setText(boardList.get(position).get("data5"));
			viewHolder.board_data6.setText(boardList.get(position).get("data6"));
		}
		return v;
	}

	
	public void setArrayList(ArrayList<HashMap<String,String>> arrays){
		this.boardList = arrays;
	}
	
	public ArrayList<HashMap<String,String>> getArrayList(){
		return boardList;
	}
	
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		TextView board_data1;
		TextView board_data2;
		TextView board_data3;
		TextView board_data4;
		TextView board_data5;
		TextView board_data6;
	}
	

}







