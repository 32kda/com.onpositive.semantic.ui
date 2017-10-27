package com.onpositive.semantic.model.api.property.java;

import java.util.ArrayList;
import java.util.Map;

import com.onpositive.semantic.model.api.command.DefaultCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.property.AbstractWritableProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;

public class MapPropertyProvider implements IPropertyProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static class MapProperty extends  AbstractWritableProperty{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		private MapProperty(String s) {
			super(s);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object getValue(Object obj) {
			if (obj==null)
				return null ;
			
			Object value = ((Map<String, ?>) obj).get(id);
			if( value != null )
				return value ;
			
			if (id.equals("entrySet")){
				Map m=(Map) obj;
				return m.entrySet();	
			}
			if (id.equals("keySet")){
				Map m=(Map) obj;
				return m.keySet();	
			}
			if (id.equals("values")){
				Map m=(Map) obj;
				return m.values();	
			}
			if (id.equals("size")){
				Map m=(Map) obj;
				return m.size();	
			}
			if (id.equals("empty")){
				Map m=(Map) obj;
				return m.isEmpty();	
			}
			return value;
		}



		
		public ICommandFactory getCommandFactory() {
			return DefaultCommandFactory.INSTANCE;
		}
		
		public IMeta getMeta() {
			return super.getMeta();
		}

		public boolean isCollection() {
			return false;
		}

		@Override
		protected void doSet(Object target, Object object)
				throws IllegalAccessException {
			Map targetMap=(Map) target;
			targetMap.put(id, object);
		}
	}

	static MapPropertyProvider instance = new MapPropertyProvider();

	public IProperty getProperty(Object obj, String name) {
		if (name != null) {
			final String s = name;
			return new MapProperty(s);
		}
		return null;
	}

	public Iterable<IProperty> getProperties(Object obj) {
		ArrayList<IProperty> result = new ArrayList<IProperty>();
		for (String s : ((Map<String, ?>) obj).keySet())
			result.add(new MapProperty(s));
		return result;
	}

}
