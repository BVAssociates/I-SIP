package com.bv.isis.console.impl.processor.appcfg.model;

import java.util.ArrayList;

public class PortalApplicationTransitions {

	public PortalApplicationTransitions() {
		
	}
	
	
	public PortalApplicationStates getStartState() {
		return _startState;
	}

	public void setStartState(PortalApplicationStates state) {
		_startState = state;
	}

	public ArrayList<PortalApplicationTransitionServices> getTransitionServices() {
		return _transitionServices;
	}

	public void setTransitionServices(
			ArrayList<PortalApplicationTransitionServices> services) {
		_transitionServices = services;
	}

	public ArrayList<PortalApplicationTransitionServices> getNewTransitionServices() {
		return _newTransitionServices;
	}

	public void setNewTransitionServices(
			ArrayList<PortalApplicationTransitionServices> transitionServices) {
		_newTransitionServices = transitionServices;
	}

	public char getFlag() {
		return _flag;
	}
	
	public void setFlag(char flag) {
		_flag = flag;
		
		// Pour chaque transitions, on pose le flag à 'S' si celui du groupe vaut 'S'
		if (flag == 'S' && _transitionServices != null) {
			for(int index = 0; index < _transitionServices.size();index++) {
				PortalApplicationTransitionServices transServ = _transitionServices.get(index);
				// En cas d'un élément qui venait d'être créer, on le supprime
				if (transServ.getFlag() == 'A') {
					_transitionServices.remove(index);
					index --;
				}
				// Si l'élement existait, on le supprimera de la base de données
				else {
					transServ.setFlag('S');
				}
			}
		}
	}
	
	public PortalApplicationTransitions clone() {
		
		ArrayList<PortalApplicationTransitionServices> transServClone = 
				new ArrayList<PortalApplicationTransitionServices>();
		if (_transitionServices != null) {
			for (int index = 0 ; index < _transitionServices.size() ; index ++) {
				transServClone.add(_transitionServices.get(index).clone());
			}
		}
		else {
			transServClone = null;
		}
		
		ArrayList<PortalApplicationTransitionServices> newTransServClone = 
				new ArrayList<PortalApplicationTransitionServices>();
		if (_newTransitionServices != null) {
			for (int index = 0 ; index < _newTransitionServices.size() ; index ++) {
				newTransServClone.add(_newTransitionServices.get(index).clone());
			}
		}
		else {
			newTransServClone = null;
		}
		
		PortalApplicationTransitions clone = new PortalApplicationTransitions();
		clone.setStartState(_startState);
		clone.setTransitionServices(transServClone);
		clone.setNewTransitionServices(newTransServClone);
		clone.setFlag(_flag);
		
		return clone;
		
	}
	
	private PortalApplicationStates _startState;
	
	private ArrayList<PortalApplicationTransitionServices> _transitionServices;
	
	private ArrayList<PortalApplicationTransitionServices> _newTransitionServices;
	
	private char _flag;
	
}
