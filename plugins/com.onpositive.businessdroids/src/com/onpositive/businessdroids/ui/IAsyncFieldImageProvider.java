package com.onpositive.businessdroids.ui;

import com.onpositive.businessdroids.model.IField;

public interface IAsyncFieldImageProvider extends IFieldImageProvider{

	/**
	 * 
	 * @param column
	 * @param fieldValue
	 * @param v
	 * @param object
	 * @return true if background image retrieval is needed.
	 */
	boolean hasAllInfo(IField column, Object fieldValue, IViewer v,
			Object object);

	/**
	 * 
	 * @param column
	 * @param fieldValue
	 * @param table
	 * @param parenObj
	 * @return key to identify an required image
	 */
	Object getUpdateKey(IField column, Object fieldValue, IViewer table,
			Object parenObj);

	/**
	 * fetches and caches image. Called from background thread;
	 * @param key
	 * @param c
	 * @param t
	 * 
	 */
	void doGet(Object key, IField c, IViewer t);

}
