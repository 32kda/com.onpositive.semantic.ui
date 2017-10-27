package com.onpositive.semantic.model.api.query;

import java.io.Serializable;
import java.util.HashMap;

import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class QueryResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected Object cursor;
	
	protected Object[] result;
	
	protected Long totalcount;
	
	protected Long totalKindcount;
	
	public Long getTotalKindcount() {
		return totalKindcount;
	}

	public void setTotalKindcount(Long totalKindcount) {
		this.totalKindcount = totalKindcount;
	}

	protected CodeAndMessage message;
	
	protected Query original;
	
	protected HashMap<String, Object>aggregators=new HashMap<String, Object>();
	
	protected long timeStamp;

	public Query getOriginal() {
		return original;
	}

	public void setOriginal(Query original) {
		this.original = original;
	}

	public void setMessage(CodeAndMessage message) {
		this.message = message;
	}

	public Long getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(Long totalcount) {
		this.totalcount = totalcount;
	}

	
	
	public Object getCursor() {
		return cursor;
	}

	public void setCursor(Object cursor) {
		this.cursor = cursor;
	}

	public Object[] getResult() {
		return result;
	}

	public void setResult(Object[] result) {
		this.result = result;
	}

	public CodeAndMessage getStatus() {
		if (message==null){
			return CodeAndMessage.OK_MESSAGE;
		}
		return message;
	}

	public void setStatus(CodeAndMessage status) {
		this.message= status;
	}

	public QueryResult(Object... result){
		this.result=result;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public Object getAggregatorValue(String key){
		return aggregators.get(key);
	}
	
	public void setAggregatorValue(String key,Object object){
		this.aggregators.put(key, object);
	}
	
}
