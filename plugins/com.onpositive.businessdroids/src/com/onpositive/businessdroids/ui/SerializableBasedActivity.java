package com.onpositive.businessdroids.ui;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class SerializableBasedActivity<T extends Serializable> extends Activity {

	public static <T extends Serializable, A extends T> void launch(
			Context ctx,
			Class<? extends SerializableBasedActivity<T>> activityClass,
			T activityObject,Object... extras) {
		final Intent intent = new Intent(ctx, activityClass);
		intent.putExtra("object", (Serializable) activityObject);
		intent.putExtra("extras", extras);
		ctx.startActivity(intent);
		
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		Intent intent = getIntent();
		return (T) intent.getSerializableExtra("object");
	}
	
	protected Object[] getExtras(){
		Intent intent = getIntent();
		return (Object[]) intent.getSerializableExtra("extras");
	}
}
