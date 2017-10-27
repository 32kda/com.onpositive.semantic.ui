package com.onpositive.demo.helpdesk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Child;
import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
@Display("ticket.dlf")

public class Ticket implements Serializable{

	@Id
	long id;
	
	@Caption("Subject")
	@Required
	protected String title="AA";
	
	
	protected String type;
	
	@Caption("Type")
	@RealmProvider(expression="{'Bug','Feature','Task'}")
	@Required
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected String reporter;
	
	@Caption("Owner")
	protected String owner;
	
	@Caption("Priority")
	@RealmProvider(expression="{'Low','Average','High'}")
	@Required
	protected String priority;
	
	@Caption("Status")
	protected String status;
	
	
	
	protected boolean publicTicket;
	
	protected boolean lockedTicket;
	
	@Child
	protected TicketDetails details;
	
	@ReadOnly
	protected Date created;
	
	@ReadOnly
	protected Date lastModified;
	
	@ReadOnly
	protected Date closedAt;
}
