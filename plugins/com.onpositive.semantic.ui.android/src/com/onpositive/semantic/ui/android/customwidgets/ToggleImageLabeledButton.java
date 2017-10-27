/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package com.onpositive.semantic.ui.android.customwidgets;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class ToggleImageLabeledButton extends ImageView {

  private int imageOn;
  private int imageOff;
  private AtomicBoolean on = new AtomicBoolean(false);
  
  public ToggleImageLabeledButton(Context context) {
	  super(context);
      imageOn = com.onpositive.semantic.ui.android.R.drawable.down;
      imageOff = com.onpositive.semantic.ui.android.R.drawable.up;
      setImageResource(imageOff);
  }

  private void handleNewState(boolean newState) {
    if (newState) {
      setImageResource(imageOn);
    } else {
      setImageResource(imageOff);
    }
  }

  @Override
  public void setOnClickListener(final OnClickListener l) {
    OnClickListener wrappingListener = new OnClickListener() {

      public void onClick(View v) {
        boolean newState = !on.get();
        on.set(newState);
        handleNewState(newState);
        l.onClick(v);
      }

    };

    super.setOnClickListener(wrappingListener);
  }

  public void setState(boolean b) {
    on.set(b);
    handleNewState(b);
  }

}
