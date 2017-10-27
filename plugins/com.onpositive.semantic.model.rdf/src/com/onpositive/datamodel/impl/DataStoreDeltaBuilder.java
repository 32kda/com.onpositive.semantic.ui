package com.onpositive.datamodel.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.datamodel.core.IDataStore;
import com.onpositive.datamodel.core.IEntry;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IRealm;

public class DataStoreDeltaBuilder {

	@SuppressWarnings("unchecked")
	public static HashDelta<IEntry> buildDelta(IDataStore oldStore,
			IDataStore newStore) {
		final HashDelta<IEntry> dlt = new HashDelta<IEntry>();
		final Set<String> oldProps = oldStore.getKnownProperties();
		final Set<String> newProps = newStore.getKnownProperties();
		final HashDelta<String> propDelta = HashDelta.buildFrom(oldProps, newProps);
		final Collection<String> removedElements = propDelta.getRemovedElements();
		final Collection<String> addedElements = propDelta.getAddedElements();
		for (final String s : oldProps) {
			if (!removedElements.contains(s)) {
				propDelta.markChanged(s);
			}
		}
		for (final String s : newProps) {
			if (!addedElements.contains(s)) {
				propDelta.markChanged(s);
			}
		}
		final IRealm<IEntry> entities = newStore.getEntities();
		final IRealm<IEntry> oldE = oldStore.getEntities();
		final HashSet<IEntry> cns = new HashSet<IEntry>();
		for (final IEntry m : entities) {
			cns.add(m);
		}
		for (final IEntry e : oldE) {
			if (!cns.contains(e)) {
				dlt.markRemoved(e);
			}
		}
		for (final IEntry m : cns) {
			if (!oldE.contains(m)) {
				dlt.markAdded(m);
			}
		}
		cns.retainAll(dlt.getAddedElements());
		l2: for (final IEntry e : cns) {
			for (final String s : propDelta.getChangedElements()) {
				final Object[] values = oldStore.getValues(e, s);
				final Object[] newValues = newStore.getValues(e, s);
				if (!Arrays.equals(values, newValues)) {
					dlt.markChanged(e);
					continue l2;
				}
			}
			for (final String s : propDelta.getAddedElements()) {
				final Object[] values = newStore.getValues(e, s);
				if ((values != null) && (values.length != 0)) {
					dlt.markChanged(e);
					continue l2;
				}
			}
			for (final String s : propDelta.getRemovedElements()) {
				final Object[] values = oldStore.getValues(e, s);
				if ((values != null) && (values.length != 0)) {
					dlt.markChanged(e);
					continue l2;
				}
			}
		}
		return dlt;
	}

}
