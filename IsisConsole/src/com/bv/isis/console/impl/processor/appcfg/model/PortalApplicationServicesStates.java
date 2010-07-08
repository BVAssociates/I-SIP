/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits réservés.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/appcfg/model
 * $Revision: 1.2 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: 
 * DATE:        29/08/2008
 * AUTEUR:      Florent Cossard
 * PROJET:      I-SIS
 * GROUPE:      
 * ------------------------------------------------------------
 * CONTROLE DES MODIFICATIONS
 *
 * ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.appcfg.model;

//
//Imports système
//

//
//imports du projet
//

/*----------------------------------------------------------
 * Nom: PortalApplicationServicesStates
 * 
 * Description:
 * Cette classe représente un état d'un service d'une application.
 * Cette classe conserve en mémoire un nom pour l'état du service.
 * 
 * Elle est utilisée lors du processus de création ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationServicesStates {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServicesStates
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesStates() {
		
		_stateServiceName = "";
		_flag = ' ';
	}
	
	/*----------------------------------------------------------
	 * Nom: getStateServiceName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé à l'état
	 * 
	 * Retourne: Une chaine de caractere : Le nom de l'état.
	 * ----------------------------------------------------------*/
	public String getStateServiceName() {
		return _stateServiceName;
	}

	/*----------------------------------------------------------
	 * Nom: getFlag
	 * 
	 * Description:
	 * Cette méthode retourne l'état courant.
	 * 
	 * Retourne: Un caractère, l'état courant.
	 * ----------------------------------------------------------*/
	public char getFlag() {
		return _flag;
	}

	/*----------------------------------------------------------
	 * Nom: setStateServiceName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de l'état avec celui passé en entrée.
	 * 
	 * Paramètre : 
	 *   - stateServiceName : Le nouvel état courant.
	 * ----------------------------------------------------------*/
	public void setStateServiceName(String stateServiceName) {
		_stateServiceName = stateServiceName;
	}

	/*----------------------------------------------------------
	 * Nom: setFlag
	 * 
	 * Description:
	 * Cette méthode modifie l'état courant avec celui passé en entrée.
	 * 
	 * Paramètre : 
	 *   - flag : Le nouvel état courant.
	 * ----------------------------------------------------------*/
	public void setFlag(char flag) {
		_flag = flag;
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _stateServiceName
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur le nom de l'état
	 * d'un service
	 * ----------------------------------------------------------*/
	private String _stateServiceName;
	
	/*----------------------------------------------------------
	 * Nom: _flag
	 *
	 * Description:
	 * Cet attribut représente un état associé au composant vis à vis
	 * de la base de données. 
	 * Celui-ci peut prendre 4 valeurs : 
	 *   - 'A' pour un élément à ajouter dans la base de données.
	 *   - 'E' pour une entrée non modifiée depuis la base de données.
	 *   - 'M' pour une entrée de la base de données mais modifiée par
	 *     l'utilisateur.
	 *   - 'S' pour une entrée de la base à supprimer.
	 * ----------------------------------------------------------*/
	private char _flag;
}
