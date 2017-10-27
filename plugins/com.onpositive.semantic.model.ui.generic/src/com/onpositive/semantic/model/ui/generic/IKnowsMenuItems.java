package com.onpositive.semantic.model.ui.generic;

import java.io.Serializable;

/**
 * This interface should be used for indicating, that it's implementor knows
 * element list for it's menu contribution
 * @author 32kda
 *
 */
public interface IKnowsMenuItems<T> extends Serializable
{
	/**
	 * Returns menu items list 
	 * @param object object, for which current menu is contributed
	 * @param menuId Some menu identifier to determine, which menu's elements we need at moment 
	 * @return list of menu elements
	 */
	public T[] getMenuItems(Object object, String menuId);
}
