package com.onpositive.semantic.model.java.tests;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandListener;

public class TrListener implements ICommandListener{

	@Override
	public void commandExecuted(ICommand cm) {
		CompositeCommand c=(CompositeCommand) cm;
		int ca=0;
		if (BasicPlatformExtensionTest.k>0){
			throw new RuntimeException();
		}
		for (ICommand z:c){			
			ca++;
			BasicPlatformExtensionTest.k++;
		}
		if (ca!=2){
			throw new RuntimeException();
		}
		
	}

}
