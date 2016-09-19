package edu.buaa.nlp.es.sort;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ChineseSorter implements CustomSorter{

	private String field;
	private String order;
	
	public ChineseSorter(String field, String order) {
		this.order = order;
		this.field=field;
	}

	@Override
	public int compare(JSONObject o1, JSONObject o2) {
		Collator collator=Collator.getInstance(Locale.CHINA);
		if(order==null || "".equals(order)) this.order="asc";
		if("asc".equals(order)){
			return collator.compare(o1.getString(field), o2.getString(field));
		}else if("desc".equals(order)){
			return collator.compare(o2.getString(field), o1.getString(field));
		}
		return 1;
	}
}
