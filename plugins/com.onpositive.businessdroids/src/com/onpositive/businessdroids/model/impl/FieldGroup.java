package com.onpositive.businessdroids.model.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.IFieldGroup;
import com.onpositive.businessdroids.model.IFieldProvider;

public class FieldGroup implements IFieldGroup{

	private static final String[] STRINGS = new String[0];
	protected String id;
	protected String title;
	protected String[]parentGroups;
	private IField[] members;
	private IFieldGroup[] childGroups;
	
	public FieldGroup(String id, String title, String[] parentGroups,
			IField[] members,IFieldGroup[] childGroup) {
		super();
		this.id = id;
		this.title = title;
		this.parentGroups = parentGroups;
		this.members = members;
	}
	public FieldGroup(String id, String title,
			IField... members) {
		super();
		this.id = id;
		this.title = title;
		this.parentGroups = STRINGS;
		this.members = members;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String[] getParentGroups() {
		return parentGroups;
	}

	@Override
	public IField[] getFields() {
		return members;
	}
	@Override
	public IFieldGroup[] getChildGroups() {
		return childGroups;
	}

	protected int level=-1;
	
	public int getHyerarchyDeepness(){
		if (level==-1){
			int cm=0;
			if (this.childGroups!=null){
				for (IFieldGroup f:childGroups){
					cm=Math.max(cm,f.getHyerarchyDeepness()+1);
				}
			}
			this.level=cm;
		}
		return level;		
	}
	
	public static FieldGroup build(IFieldProvider p,Object object){
		IField[] fields = p.getFields(object);
		HashMap<String, ArrayList<IField>>ls=new HashMap<String, ArrayList<IField>>();
		for (IField f:fields){
			String[] categories = f.getCategories();
			if (categories!=null){
				for (String s:categories){
					ArrayList<IField> arrayList = ls.get(s);
					if (arrayList==null){
						arrayList=new ArrayList<IField>();
						ls.put(s, arrayList);
					}
					arrayList.add(f);
				}
			}
		}
		IFieldGroup[] grs=new IFieldGroup[ls.size()];
		int a=0;
		for (String m:ls.keySet()){
			ArrayList<IField> arrayList = ls.get(m);
			FieldGroup g=new FieldGroup(m, m, new String[0], fields, arrayList.toArray(new IFieldGroup[arrayList.size()]));
			grs[a]=g;
		}
		return new FieldGroup("", "", new String[0], new IField[0],grs);	
	}
}
