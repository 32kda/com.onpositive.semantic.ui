package com.onpositive.semantic.model.ui.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PropertyStatistics {

	public static class StatEntry implements Comparable<StatEntry>{
		Object value;
		int count;
		long lastUsed;
		
		public int compareTo(StatEntry o) {			
			return o.count-this.count;
		}
	}
	
	
	protected HashMap<Object,StatEntry>map=new HashMap<Object,StatEntry>();
	protected Set<Object>restrictTo;
	
	long t0=System.currentTimeMillis()/(1000*3600);
	public PropertyStatistics(){
		
	}
	public PropertyStatistics(Set<Object>restrict){
		this.restrictTo=restrict;
	}
	
	public void register(Object value,long lastUsed){
		StatEntry statEntry = map.get(value);
		if (statEntry==null){
			statEntry=new StatEntry();
			statEntry.value=value;
			map.put(value, statEntry);
		}
		lastUsed=lastUsed/(1000*3600);
		int delta=(int) (t0-lastUsed);
		int scale=1;
		if (delta<1){
			scale=400;
		}
		else if (delta<100){
			scale=100;
		}
		else if (delta<1000){
			scale=50;
		}
		else if (delta<4000){
			scale=10;
		}
		statEntry.count+=scale;
		statEntry.lastUsed=Math.max(statEntry.lastUsed, lastUsed);
	}
	
	protected void unregister(Object value){
		StatEntry statEntry = map.get(value);
		if (statEntry==null){
			return;
		}
	}
	
	public void merge(StatEntry entry){
		if (restrictTo!=null){
			if (!restrictTo.contains(entry.value)){
				return;
			}
		}
		{
			StatEntry statEntry = map.get(entry.value);
			if (statEntry!=null){
				statEntry.count+=entry.count;
				statEntry.lastUsed=Math.max(statEntry.lastUsed, entry.lastUsed);
			}
			else{
				map.put(entry.value, entry);
			}
		}
	}
	public Collection<StatEntry> stat() {
		return map.values();
	}
	public HashSet<Object> getBest(int i, HashSet<Object> values1) {
		ArrayList<StatEntry> arrayList = new ArrayList<StatEntry>(map.values());
		Collections.sort(arrayList);		
		HashSet<Object>result=new HashSet<Object>(values1);
		int initialCost=-1;
		for (StatEntry e:arrayList){
			if (result.size()==20){
				return result;
			}
			if (initialCost==-1){
				initialCost=e.count;
			}
			if (e.count<initialCost/100){
				break;
			}
			result.add(e.value);
		}
		return result;
	}
		
}
