package com.bv.isis.console.impl.processor.appcfg.model;

import java.util.ArrayList;

public class PortalApplicationTransitionServices {

	public PortalApplicationTransitionServices() {
		
	}
	
	
	
	
	public PortalApplicationServicesSpecificStates getComposition() {
		return _composition;
	}

	public void setComposition(
			PortalApplicationServicesSpecificStates composition) {
		_composition = composition;
	}

	public boolean getCritical() {
		return _critical;
	}

	public void setCritical(boolean critical) {
		_critical = critical;
	}

	public int getOrder() {
		return _order;
	}

	public void setOrder(int order) {
		_order = order;
	}

	public boolean getNewCritical() {
		return _newCritical;
	}

	public void setNewCritical(boolean critical) {
		_newCritical = critical;
	}

	public int getNewOrder() {
		return _newOrder;
	}

	public void setNewOrder(int order) {
		_newOrder = order;
	}

	public char getFlag() {
		return _flag;
	}

	public void setFlag(char flag) {
		_flag = flag;
	}

	public PortalApplicationTransitionServices clone() {
		
		PortalApplicationTransitionServices clone =
				new PortalApplicationTransitionServices();
		
		clone.setComposition(_composition);
		clone.setCritical(_critical);
		clone.setOrder(_order);
		clone.setNewCritical(_newCritical);
		clone.setNewOrder(_newOrder);
		clone.setFlag(_flag);
		
		return clone;
	}
	
	
	
	private PortalApplicationServicesSpecificStates _composition;
	
	private boolean _critical;
	
	private int _order;
	
	private boolean _newCritical;
	
	private int _newOrder;
	
	private char _flag;
}
