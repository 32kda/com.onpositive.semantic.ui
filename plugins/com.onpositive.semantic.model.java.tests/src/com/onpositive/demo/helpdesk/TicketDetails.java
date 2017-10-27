package com.onpositive.demo.helpdesk;

import java.util.ArrayList;

import com.onpositive.semantic.model.api.property.java.annotations.Embedded;
import com.onpositive.semantic.model.api.property.java.annotations.Id;
import com.onpositive.semantic.model.api.property.java.annotations.Parent;

public class TicketDetails {

	@Id
	Long l;
	
	@Parent
	Ticket parent;
	
	protected String summary;
	
	@Embedded
	protected ArrayList<Comment>embedded;
	
	@Embedded
	protected ArrayList<Attachment>attachments;
}
