package com.onpositive.semantic.model.entity.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class StringKeyNumberer implements IKeyNumberer {

	protected LinkedHashMap<String, Integer> numbers = new LinkedHashMap<String, Integer>();
	protected ArrayList<String> keys = new ArrayList<String>();
	protected String kind;

	@Override
	public int number(Key key) {
		String nm = key.getName();
		Integer mn=numbers.get(key);
		if (mn==null){
			int num=keys.size();
			numbers.put(nm, num);
			keys.add(nm);
			return num;
		}
		return mn.intValue();
	}

	@Override
	public Key key(int number) {
		String c = keys.get(number);
		return KeyFactory.createKey(kind, c);
	}

}
