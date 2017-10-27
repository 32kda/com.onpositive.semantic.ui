package com.onpositive.semantic.ui.snippets.engine;

import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public class EngineSpecs {
	
	public static class Types implements IRealmProvider<String> {

		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>( "Electric", "Explosion" );
		}
	}
	public static class ElectricSubTypes implements IRealmProvider<String>
	{
		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>( "Gas-turbine", "Accumulator", "Hydrogen" );
		}
	}	
	public static class ExplosionSubTypes implements IRealmProvider<String>
	{
		public IRealm<String> getRealm(IBinding model) {
			return new Realm<String>( "Diesel", "Gasoline", "Gas" );
		}
	}
	
	final static int fieldOffset0 = 0, fieldWidth0 = 2 ;
	public static int EXPLOSION_ENGINE = 2 << fieldOffset0 ;
	public static int ELECTRIC = 3 << fieldOffset0 ;
	
	//gasoline engine type:
	public static int fieldOffset1 = fieldOffset0 + fieldWidth0, fireldWidth1 = 2 ;
	public static int DIESEL = 0 << fieldOffset1 ;
	public static int GASOLINE = 1 << fieldOffset1 ;
	
	public static int ACCUMULATOR = 0 << fieldOffset1 ;
	public static int HYDROGEN = 1 << fieldOffset1 ;
	public static int GAS_TURBINE = 1 << fieldOffset1 ;

	
	protected String name ;
	protected int power ;
	protected double weught ;
	protected int pistonsCount ;
	protected int type ;   
	protected boolean isNew ;
}
