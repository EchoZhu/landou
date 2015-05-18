package com.zykj.landous.Tools; 

import android.util.SparseArray;
import android.view.View;

/** 
 * @author 作者 zhang 
 * @version 创建时间：2015年1月16日 下午3:42:15 
 * 类说明 
 */
public class ViewHolder {  
    
    @SuppressWarnings("unchecked")    
    public static <T extends View> T get(View view, int id) {    
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();    
        if (viewHolder == null) {  
            viewHolder = new SparseArray<View>();    
            view.setTag(viewHolder);    
        }  
        View childView = viewHolder.get(id);    
        if (childView == null) {    
            childView = view.findViewById(id);    
            viewHolder.put(id, childView);    
        }    
        return (T) childView;    
    }   
      
}  
