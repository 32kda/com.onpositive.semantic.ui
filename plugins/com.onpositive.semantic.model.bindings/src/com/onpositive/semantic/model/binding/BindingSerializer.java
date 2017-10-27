package com.onpositive.semantic.model.binding;

import java.io.DataOutputStream;
import java.io.IOException;

public class BindingSerializer {

	public static void serializeBinding(AbstractBinding bnd,DataOutputStream ds) throws IOException{
		SmartSerializer.serialize(bnd, Binding.class);		
	}
}
