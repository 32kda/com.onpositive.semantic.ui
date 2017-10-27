package com.onpositive.semantic.ui.businessdroids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Units {

	public static ArrayList<Unit> units = new ArrayList<Unit>();
	public static HashMap<String, String[]> unitEquivalents = new HashMap<String, String[]>();

	public final static String CANONIC_LABEL_KILOMETER = "km";
	public final static String CANONIC_LABEL_METER = "m";
	public final static String CANONIC_LABEL_CANTIMETER = "cm";
	public final static String CANONIC_LABEL_MILLIMETER = "mm";
	
	public final static String CANONIC_LABEL_FOOT = "ft";
	public final static String CANONIC_LABEL_MILE = "mi";
	public final static String CANONIC_LABEL_INCH = "in";
	public final static String CANONIC_LABEL_NAUTICAL_MILE = "nmi";

	public final static String CANONIC_LABEL_SQUARE_METER = "m²";
	public final static String CANONIC_LABEL_SQUARE_FOOT = "ft²";

	public final static String CANONIC_LABEL_KILOMETERS_PER_HOUR = "km/h";
	public final static String CANONIC_LABEL_MILES_PER_HOUR = "mph";
	public final static String CANONIC_LABEL_KNOT = "knots";
	public final static String CANONIC_LABEL_MACH = "mach";

	public final static String CANONIC_LABEL_METERS_PER_MINUTE = "m/min";
	public final static String CANONIC_LABEL_METERS_PER_SECOND = "m/sec";
	public final static String CANONIC_LABEL_FEET_PER_MINUTE = "ft/min";
	public final static String CANONIC_LABEL_FEET_PER_SECOND = "ft/sec";
	public static final String CANONIC_LABEL_TONN = "t";
	public final static String CANONIC_LABEL_WATT = "W";
	public final static String CANONIC_LABEL_KILOWATT = "kW";
	public final static String CANONIC_LABEL_HOURSE_POOWER = "hp";
	public final static String CANONIC_LABEL_SHAFT_HORSE_POWER = "shp";

	public final static String CANONIC_LABEL_KILOGRAMM = "kg";
	public final static String CANONIC_LABEL_POUND = "lb";

	public final static String CANONIC_LABEL_NEWTON = "N";
	public final static String CANONIC_LABEL_KILOGRAMM_FORCE = "kgf";
	public final static String CANONIC_LABEL_POUND_FORCE = "lbf";
	public final static String CANONIC_LABEL_KILONEWTON = "kN";
	

	public final static String CANONIC_LABEL_KILOGRAMM_PER_SQUARE_METER = "kg/m²";
	public final static String CANONIC_LABEL_POUND_PER_SQUARER_FOOT = "lb/ft²";
	
	//public final static String CANONIC_LABEL_PS = "ps";

	public final static String CANONIC_LABEL_MINUTE = "min";
	public final static String CANONIC_LABEL_HOUR = "h";
	
	public final static String CANONIC_LABEL_WATT_PER_KILOGRAMM = "W/kg";
	public final static String CANONIC_LABEL_KILOWATT_PER_KILOGRAMM = "kW/kg";
	public final static String CANONIC_LABEL_HORSE_POWER_PER_POUND = "hp/lb";
	public final static String CANONIC_LABEL_HORSE_POWER_PER_TONN = "hp/t";
	
	public static final Unit[] PRIMARY_UNITS = {
		new Unit(CANONIC_LABEL_METER, UnitKind.SIZE, 1.00),
		new Unit(CANONIC_LABEL_KILOMETERS_PER_HOUR, UnitKind.SPEED, 1.00),
		new Unit(CANONIC_LABEL_METERS_PER_MINUTE, UnitKind.CLIMB_RATE, 1.00),
		new Unit(CANONIC_LABEL_KILOGRAMM, UnitKind.WEIGHT, 1.00),
		new Unit(CANONIC_LABEL_SQUARE_METER, UnitKind.AREA, 1.00),
		new Unit(CANONIC_LABEL_NEWTON, UnitKind.FORCE, 1.00),
		new Unit(CANONIC_LABEL_KILOGRAMM_PER_SQUARE_METER, UnitKind.LOADING, 1.00),
		new Unit(CANONIC_LABEL_WATT, UnitKind.POWER, 1.00),
		new Unit(CANONIC_LABEL_MINUTE, UnitKind.TIME, 1.00),
		new Unit(CANONIC_LABEL_WATT_PER_KILOGRAMM, UnitKind.POWER_TO_MASS_RATIO, 1.00)
		};
	
	private static HashMap<UnitKind,Unit> primaryUnitMap = new HashMap<UnitKind, Unit>() ;
	
	private static void fillPrimaryUnitsMap()
	{
		for( Unit u : PRIMARY_UNITS )
			primaryUnitMap.put( u.getKind(), u ) ;
	}
	
	public static Unit getPrimaryUnit( UnitKind kind )
	{
		return primaryUnitMap.get(kind) ;
	}

	private static HashMap<String,Boolean> uniquenessMap = new HashMap<String, Boolean>() ;
	static HashMap<String, Unit> map = new HashMap<String, Unit>();
	private static HashMap<String,Unit[]> congruentUnitsMap = new HashMap<String, Unit[]>() ;
	static {
		fillUnitEquivalents();
		fillUnitsArray();
		fillPrimaryUnitsMap() ;
		fillCongruenUnitsMap() ;
	}

	

	public static Unit getUnitByShortName(String _shortName, String _kind)
	{
		if( _shortName == null )
			return null ;
		
		String shortName = _shortName.trim().toLowerCase() ;
		
		Boolean unique = Units.isUnique(shortName);
		if( unique == null )
			return null ;
		
		if( _kind == null && !unique )
			return produceUndef(shortName) ;
		
		if( UnitKind.UNDEF.name().equals(_kind) )
			return produceUndef(shortName) ;
		
		String kind = _kind != null ? _kind.toLowerCase().trim() : null ;
		if( kind != null )
			kind = kind.length() != 0 ? kind : null ;		 
	
		Unit unit = map.get(shortName);
		if (unit != null ) {
			if( kind != null )
			{
				String kind_str = unit.getKind().toString().toLowerCase() ;			
				if(  kind_str.equals(kind) )
					return unit;
			}
			else
				return unit ;
		}
		
		for( Map.Entry<String, String[]> e : unitEquivalents.entrySet() )
		{
			String primName = e.getKey().toLowerCase() ;
			String[] altNames = e.getValue() ;
			
			String sn = shortName ;
			for( String s : altNames )
				if( s.toLowerCase().equals(sn) )
				{
					sn = primName ;
					break ;
				}

			if( primName.equals(sn) )				
				for (Unit u : units)
					if ( u.shortName.toLowerCase().equals(sn) )
					{
						if( kind != null )
						{
							String kind_str = u.getKind().toString().toLowerCase() ;
							if( kind_str.equals(kind) ){
								map.put( sn, unit );
								return u;
							}
						}
						else{
							map.put(sn,unit);
							return u;
						}
					}
		}

		return null;
	}
	
	private static Unit produceUndef(String sn)
	{
		return new Unit( sn, UnitKind.UNDEF, -1.0 ) ;
	}
	
	@Deprecated
	public static String adjustUnits(String s) {
		String result = s;
		
		for (String cLabel : unitEquivalents.keySet()) {
			String[] altLabels = unitEquivalents.get(cLabel);
			for (String label : altLabels)
				result = result.replaceAll("(\\s|\\d|\\A)(" + label
						+ ")(\\s|\\d|\\z|\\=)", "$1" + cLabel + "$3");

			result = result.replaceAll("(\\d)" + cLabel, "$1 " + cLabel);
		}
		return result;
	}

	private static void fillUnitEquivalents() {
		{
			String[] s = { "meters" };
			unitEquivalents.put(CANONIC_LABEL_METER, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_CANTIMETER, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_MILLIMETER, s);
		}
		{
			String[] s = { "feet", "foot (length)", "foot (unit)" };
			unitEquivalents.put(CANONIC_LABEL_FOOT, s);
		}
		{
			String[] s = { "inch", "inches" };
			unitEquivalents.put(CANONIC_LABEL_INCH, s);
		}
		{
			String[] s = { "sqm", "sq m", "mI", "m^2", "m2", "sq.m", "sq. m", "m<sup>2</sup>" };
			unitEquivalents.put(CANONIC_LABEL_SQUARE_METER, s);
		}
		{
			String[] s = { "sqft", "sq ft", "ftI", "ft^2", "ft2", "sq.ft",
					"sq. ft", "ft<sup>2</sup>" };
			unitEquivalents.put(CANONIC_LABEL_SQUARE_FOOT, s);
		}
		{
			String[] s = { "miles" };
			unitEquivalents.put(CANONIC_LABEL_MILE, s);
		}
		{
			String[] s = { };
			unitEquivalents.put(CANONIC_LABEL_KILOMETER, s);
		}
		{
			String[] s = { "tons","tonnes" };
			unitEquivalents.put(CANONIC_LABEL_TONN, s);
		}
		{
			String[] s = { "nm", "nautical mi", "nautical miles", "nautical mile", "nautical mile" };
			unitEquivalents.put(CANONIC_LABEL_NAUTICAL_MILE, s);
		}
		{
			String[] s = { "kmh" };
			unitEquivalents.put(CANONIC_LABEL_KILOMETERS_PER_HOUR, s);
		}
		{
			String[] s = { "mp/h", "mi/h" };
			unitEquivalents.put(CANONIC_LABEL_MILES_PER_HOUR, s);
		}
		{
			String[] s = { "kn", "kts" };
			unitEquivalents.put(CANONIC_LABEL_KNOT, s);
		}
		{
			String[] s = { "mach number" };
			unitEquivalents.put(CANONIC_LABEL_MACH, s);
		}
		{
			String[] s = { "m/m", "m/mn" };
			unitEquivalents.put(CANONIC_LABEL_METERS_PER_MINUTE, s);
		}
		{
			String[] s = { "ms", "m/s", "metre per second" };
			unitEquivalents.put(CANONIC_LABEL_METERS_PER_SECOND, s);
		}
		{
			String[] s = { "ft/s" };
			unitEquivalents.put(CANONIC_LABEL_FEET_PER_SECOND, s);
		}
		{
			String[] s = { "ftmin", "ft/mn", "ft/m" };
			unitEquivalents.put(CANONIC_LABEL_FEET_PER_MINUTE, s);
		}
		{
			String[] s = { "ps" };//"ps" is German name for horse power
			unitEquivalents.put(CANONIC_LABEL_HOURSE_POOWER, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_SHAFT_HORSE_POWER, s);
		}
		{
			String[] s = { };
			unitEquivalents.put(CANONIC_LABEL_WATT, s);
		}
		{
			String[] s = { };
			unitEquivalents.put(CANONIC_LABEL_KILOWATT, s);
		}
		{
			String[] s = { };
			unitEquivalents.put(CANONIC_LABEL_KILOGRAMM, s);
		}
		{
			String[] s = { "pounds", "lbs" };
			unitEquivalents.put(CANONIC_LABEL_POUND, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_KILOGRAMM_FORCE, s);
		}
		{
			String[] s = { "lb<sub>f</sub>" };
			unitEquivalents.put(CANONIC_LABEL_POUND_FORCE, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_NEWTON, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_KILONEWTON, s);
		}		
		{
			String[] s = { "kg/sq m", "kg/m<sup>2</sup>", "kg/m2" };
			unitEquivalents.put(CANONIC_LABEL_KILOGRAMM_PER_SQUARE_METER, s);
		}
		{
			String[] s = { "lb/sq ft", "lb/ft<sup>2</sup>", "lb/sqft" };
			unitEquivalents.put(CANONIC_LABEL_POUND_PER_SQUARER_FOOT, s);
		}
//		{
//			String[] s = {};
//			unitEquivalents.put(CANONIC_LABEL_PS, s);
//		}
		{
			String[] s = {};//{ "m" };
			unitEquivalents.put(CANONIC_LABEL_MINUTE, s);
		}
		{
			String[] s = { "hour", "hours" };
			unitEquivalents.put(CANONIC_LABEL_HOUR, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_WATT_PER_KILOGRAMM, s);
		}
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_KILOWATT_PER_KILOGRAMM, s);
		}				
		{
			String[] s = {};
			unitEquivalents.put(CANONIC_LABEL_HORSE_POWER_PER_POUND, s);
		}		
		{
			String[] s = { "hp/tonne" };
			unitEquivalents.put(CANONIC_LABEL_HORSE_POWER_PER_TONN, s);
		}

	}

	private static void fillUnitsArray() {
		int i = 0;
		Unit primaryUnit;

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_METER ,
											// UnitKind.SIZE, 1.00 ),
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_KILOMETER, UnitKind.SIZE, 1000.00,	primaryUnit));
		units.add(new Unit(CANONIC_LABEL_CANTIMETER, UnitKind.SIZE, 0.01 , primaryUnit));
		units.add(new Unit(CANONIC_LABEL_MILLIMETER, UnitKind.SIZE, 0.001, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_MILE, UnitKind.SIZE, 1609.344,	primaryUnit));
		units.add(new Unit(CANONIC_LABEL_NAUTICAL_MILE, UnitKind.SIZE, 1853.248, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_FOOT, UnitKind.SIZE, 0.3048, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_INCH, UnitKind.SIZE, 0.3048 / 12, primaryUnit));		

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_KILOMETER_PER_HOUR ,
		
											// UnitKind.SPEED, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_MILES_PER_HOUR, UnitKind.SPEED, 1.61, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_KNOT, UnitKind.SPEED, 1.852,primaryUnit));
		units.add(new Unit(CANONIC_LABEL_MACH, UnitKind.SPEED, 1150, primaryUnit));
		

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_METERS_PER_MINUTE,
											// UnitKind.CLIMB_RATE, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_METERS_PER_SECOND,	UnitKind.CLIMB_RATE, 60.00, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_FEET_PER_MINUTE, UnitKind.CLIMB_RATE, 0.3048, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_FEET_PER_SECOND, UnitKind.CLIMB_RATE, 60 * 0.3048, primaryUnit));

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_KILOGRAMM,
											// UnitKind.WEIGHT, 1.00) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_POUND, UnitKind.WEIGHT, 0.45359237,primaryUnit));
		units.add(new Unit(CANONIC_LABEL_TONN, UnitKind.WEIGHT, 1000, primaryUnit));
		
		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_SQUARE_METER ,
											// UnitKind.AREA, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_SQUARE_FOOT, UnitKind.AREA, 0.3048 * 0.3048, primaryUnit));

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_NEWTON ,
											// UnitKind.FORCE, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_KILOGRAMM_FORCE, UnitKind.FORCE, 1 / 0.10197, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_POUND_FORCE, UnitKind.FORCE, 1 / (0.10197 * 2.2046), primaryUnit));
		units.add(new Unit(CANONIC_LABEL_KILONEWTON, UnitKind.FORCE, 1000, primaryUnit));		

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_KILOGRAMM_PER_SQUARE_METER,
											// UnitKind.LOADING, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_POUND_PER_SQUARER_FOOT, UnitKind.LOADING, 0.45359237 / (0.3048 * 0.3048)));
		// units.add(new Unit("kg/mІ", UnitKind.LOADING, 1 / 0.10197,
		// primaryUnit));
		// units.add(new Unit("lb/ftІ", UnitKind.LOADING, 0.45359237 / 0.10197 /
		// (0.45359237 * 0.45359237)));

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_WATT, UnitKind.POWER,
											// 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_HOURSE_POOWER, UnitKind.POWER, 750, primaryUnit));
		units.add(new Unit(CANONIC_LABEL_SHAFT_HORSE_POWER, UnitKind.POWER, 750, primaryUnit));// !!! actualy, it's not the same as horse power 
		//units.add(new Unit(CANONIC_LABEL_PS, UnitKind.POWER, 750, primaryUnit));//german name for horse power
		units.add(new Unit(CANONIC_LABEL_KILOWATT, UnitKind.POWER, 1000, primaryUnit));

		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_MINUTE,
											// UnitKind.TIME, 1.00 ) ;
		units.add(primaryUnit);
		units.add(new Unit(CANONIC_LABEL_HOUR, UnitKind.TIME, 60, primaryUnit));
		
		primaryUnit = PRIMARY_UNITS[i++]; // CANONIC_LABEL_WATT_PER_KILOGRAMM = "kW/kg";
		units.add(primaryUnit);
		units.add(new Unit( CANONIC_LABEL_KILOWATT_PER_KILOGRAMM, UnitKind.POWER_TO_MASS_RATIO, 1000.0 , primaryUnit));
		units.add(new Unit( CANONIC_LABEL_HORSE_POWER_PER_POUND, UnitKind.POWER_TO_MASS_RATIO, 750.0/0.45359237, primaryUnit));
		units.add(new Unit( CANONIC_LABEL_HORSE_POWER_PER_TONN, UnitKind.POWER_TO_MASS_RATIO, 750.0/1000.0, primaryUnit));
		
	}
	
	
	private final static String congruentUnitsMapKeySeparator = "####" ; 
	private static void fillCongruenUnitsMap() {
		
		Unit[] unitArr ;
		
		unitArr = new Unit[]{
				getUnitByShortName(CANONIC_LABEL_FOOT, UnitKind.SIZE.name() ),
				getUnitByShortName(CANONIC_LABEL_INCH, UnitKind.SIZE.name() ),
		} ;
		for( Unit u : unitArr ){
			String keyString = buildCongruenceMapKey(u); 
			congruentUnitsMap.put( keyString, unitArr) ;
		}
		
		unitArr = new Unit[]{
				getUnitByShortName(CANONIC_LABEL_MINUTE, UnitKind.TIME.name() ),
				getUnitByShortName(CANONIC_LABEL_HOUR  , UnitKind.TIME.name() ),
		} ;
		for( Unit u : unitArr ){
			String keyString = buildCongruenceMapKey(u); 
			congruentUnitsMap.put( keyString, unitArr) ;
		}
		
		unitArr = new Unit[]{
				getUnitByShortName(CANONIC_LABEL_METER, UnitKind.SIZE.name() ),
				getUnitByShortName(CANONIC_LABEL_KILOMETER  , UnitKind.SIZE.name() ),
				getUnitByShortName(CANONIC_LABEL_CANTIMETER  , UnitKind.SIZE.name() ),
				getUnitByShortName(CANONIC_LABEL_MILLIMETER  , UnitKind.SIZE.name() ),
		} ;
		for( Unit u : unitArr ){
			String keyString = buildCongruenceMapKey(u); 
			congruentUnitsMap.put( keyString, unitArr) ;
		}
	}

	public static String buildCongruenceMapKey(Unit u) {
		return buildCongruencemapKey( u.shortName, u.getKind().name() );
	}
	
	public static String buildCongruencemapKey( String shortName, String unitKind) {
		return shortName + congruentUnitsMapKeySeparator + unitKind;
	}
	
	public static Unit[] getCongruentUnits( String shortName, String unitKind )
	{
		if( shortName == null || unitKind == null )			
			return null ;
		
		String keyString = buildCongruencemapKey(shortName,unitKind);
		return congruentUnitsMap.get( keyString ) ; 
	}
	public static Unit[] getCongruentUnits( Unit u ){
		return u != null ? getCongruentUnits( u.getShortName(), u.getKind().name() ) : null ;
	}
	

	public static boolean areCongruent(Unit unit0, Unit unit1) {
		if( unit0 == null || unit1 == null )
			return false;
		
		if( unit0.equals(unit1) )
			return true ;
		
		Unit[] congruentUnits = getCongruentUnits( unit0.getShortName(), unit0.getKind().name() ) ;
		if( congruentUnits == null )
			return false ;
		
		for( Unit u : congruentUnits ){
			if( unit1.equals(u) )
				return true ;
		}
		return false ;
	}	
	

	public static boolean isUnit(String _s) {
		
		String s =  _s.trim().toLowerCase();
		for( String str : unitEquivalents.keySet() )
			if( str.toLowerCase().equals(s) )
				return true ;
		
		for( String[] strArr : unitEquivalents.values() )
			for( String str : strArr )
				if( str.toLowerCase().equals(s) )
					return true ;
					
		return false ;
	}

	public static String[] getUnits(String s) {
		ArrayList<String> obj = new ArrayList<String>();
		Unit primary=null;
		for (Unit u : units) {
			if (u.shortName.equals(s)) {
				primary=u.primaryUnit;
			}
		}
		for (Unit u : units) {
			if (u.primaryUnit != null) {
				if (u.primaryUnit.equals(primary)) {
					obj.add(u.shortName);
				}
			}
		}
		return obj.toArray(new String[obj.size()]);
	}

	public static double convertToPrimary(double value, String unit) {
		Unit unitByShortName = getUnitByShortName(unit,null);
		if (unitByShortName != null) {
			return convertToPrimary(value, unitByShortName);
		}
		return Double.MIN_VALUE;
	}

	public static double convertToPrimary(double value, Unit unit) {
		return unit.getRelationToPrimary() * value;
	}

	public static double convertFromPrimary(double value, String unit) {
		Unit unitByShortName = getUnitByShortName(unit,null);
		if (unitByShortName != null) {
			return convertFromPrimary(value, unitByShortName) ;
		}
		return Double.MIN_VALUE;
	}

	public static double convertFromPrimary(double value, Unit unit) {
		return value/unit.getRelationToPrimary();
	}
	
	
	
	public static Boolean isUnique( String _unitName )
	{
		String unitName = _unitName.toLowerCase() ;
		Boolean result = uniquenessMap.get( unitName ) ;
		if( result != null )
			return result ;
		
		boolean gotOnce = false ;
		for( Map.Entry<String,String[]> entry : unitEquivalents.entrySet()  )
		{			 
			String n = entry.getKey().toLowerCase() ;
			if( n.equals(unitName) )
			{
				if( gotOnce )
				{
					uniquenessMap.put( unitName, false ) ;
					return false ;
				}
				gotOnce = true ;			
			}

			for( String m : entry.getValue() )
			{
				if( !m.toLowerCase().equals(unitName) )
					continue;
				
				if( gotOnce ){
					uniquenessMap.put(unitName, false ) ;
					return false ;
				}
				gotOnce = true ;
			}			
		}
		uniquenessMap.put(unitName, true) ;
		return gotOnce ? true : null ;
	}

	
}
