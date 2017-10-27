package com.onpositive.semantic.model.entity.stats;

import com.google.appengine.api.datastore.Key;


public interface IKeyNumberer {
	int number(Key key);
	Key key(int number);
}
