package com.zykj.landous.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.external.activeandroid.Model;
import com.external.activeandroid.annotation.Column;
import com.external.activeandroid.annotation.Table;

/**
 * @author 作者 zhang
 * @version 创建时间：2015年1月5日 下午2:00:34 类说明
 */
@Table(name = "SHOT")
public class SHOT extends Model {
	@Column(name = "shot_id")
	public String pic_id;

	

	public void fromJson(JSONObject jsonObject) throws JSONException {
		if (null == jsonObject) {
			return;
		}

		JSONArray subItemArray;

		this.pic_id = jsonObject.optString("pic_id");

		
	}

	public JSONObject toJson() throws JSONException {
		JSONObject localItemObject = new JSONObject();
		JSONArray itemJSONArray = new JSONArray();
		localItemObject.put("pic_id", pic_id);
		
		return localItemObject;
	}

}
