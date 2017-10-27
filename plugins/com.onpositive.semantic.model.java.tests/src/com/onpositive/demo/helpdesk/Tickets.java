package com.onpositive.demo.helpdesk;

import java.io.Serializable;

import com.onpositive.semantic.model.api.property.java.annotations.Display;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;

@Display("tickets.dlf")

public class Tickets implements Serializable{

	IRealm<Ticket> tickets=RealmAccess.getRealm(Ticket.class);

}
