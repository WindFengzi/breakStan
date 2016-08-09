package com.winso.break_law.util;
import com.winso.break_law.R;
import com.winso.comm_library.icedb.SelectHelp;

import java.util.ArrayList;    
import java.util.HashMap;    
import java.util.List;    
import java.util.Map;    
    
import android.content.Context;    
import android.view.LayoutInflater;    
import android.view.View;    
import android.view.ViewGroup;    
import android.widget.BaseAdapter;    
import android.widget.CheckBox;    
import android.widget.ImageView;    
import android.widget.TextView;    
    
public class DangerSelectAdapter extends BaseAdapter {    
    private LayoutInflater mInflater;    
    private List<Map<String, Object>> mData;    
    public static Map<Integer, Boolean> isSelected;    
    
    public DangerSelectAdapter(Context context) {    
        mInflater = LayoutInflater.from(context);    
        init();    
    }    
    
    //初始化    
    private void init() {    
        mData=new ArrayList<Map<String, Object>>();    
//        for (int i = 0; i < 5; i++) {    
//            Map<String, Object> map = new HashMap<String, Object>();    
//            //map.put("img", R.drawable.icon);    
//            map.put("list_item_title", "第" + (i + 1) + "行的标题");  
//            map.put("detail_id", "" + (i + 1) );    
//            mData.add(map);    
//        }    
//        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。    
//        isSelected = new HashMap<Integer, Boolean>();    
//        for (int i = 0; i < mData.size(); i++) {    
//            isSelected.put(i, false);    
//        }    
    }   
    public void updateData(SelectHelp help)
    {
        mData=new ArrayList<Map<String, Object>>();    
        for (int i = 0; i < help.size(); i++) {    
            Map<String, Object> map = new HashMap<String, Object>();    
            //map.put("img", R.drawable.icon);    
            map.put("list_item_title", help.valueStringByName(i, "hazard_content"));  
            map.put("detail_id", help.valueStringByName(i, "hazard_id") );    
            mData.add(map);    
        }    
        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。    
        isSelected = new HashMap<Integer, Boolean>();    
        for (int i = 0; i < mData.size(); i++) {    
            isSelected.put(i, false);    
        } 
    }
    
    @Override    
    public int getCount() {    
        return mData.size();    
    }    
    
    @Override    
    public Object getItem(int position) {    
        return null;    
    }    
    
    @Override    
    public long getItemId(int position) {    
        return 0;    
    }    
    public String getNameStrings()
    {
    	String sNames="";
        for (int i = 0; i < mData.size(); i++) {    
        	if( isSelected.get(i) )
        	{
        		Map<String, Object> map =mData.get(i);
        		sNames += map.get("list_item_title").toString();
        		sNames += "\n";
        	}
        } 	
        
        return sNames;
    }
    
    @Override    
    public View getView(int position, View convertView, ViewGroup parent) {    
        ViewHolder holder = null;    
        //convertView为null的时候初始化convertView。    
        if (convertView == null) {    
            holder = new ViewHolder();    
            convertView = mInflater.inflate(R.layout.list_view_select_action, null);    
            holder.detail_id = (TextView) convertView.findViewById(R.id.detail_id);    
            holder.list_item_title = (TextView) convertView.findViewById(R.id.org_name);   
            holder.list_item_title.setTextSize((float)13);
            holder.cBox = (CheckBox) convertView.findViewById(R.id.ck_action);    
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }    
      
        holder.detail_id.setText(mData.get(position).get("detail_id").toString());   
        holder.list_item_title.setText(mData.get(position).get("list_item_title").toString());    
        holder.cBox.setChecked(isSelected.get(position));    
        return convertView;    
    }    
    
    public final class ViewHolder {    
        public TextView detail_id;    
        public TextView list_item_title;    
        public CheckBox cBox;    
    }    
}    