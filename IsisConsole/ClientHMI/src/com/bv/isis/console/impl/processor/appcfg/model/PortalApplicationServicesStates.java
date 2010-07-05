/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.appcfg.model;

//
//Imports syst�me
//

//
//imports du projet
//

/*----------------------------------------------------------
 * Nom: PortalApplicationServicesStates
 * 
 * Description:
 * Cette classe repr�sente un �tat d'un service d'une application.
 * Cette classe conserve en m�moire un nom pour l'�tat du service.
 * 
 * Elle est utilis�e lors du processus de cr�ation ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationServicesStates {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServicesStates
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesStates() {
		
		_stateServiceName = "";
		_flag = ' ';
	}
	
	/*----------------------------------------------------------
	 * Nom: getStateServiceName
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� � l'�tat
	 * 
	 * Retourne: Une chaine de caractere : Le nom de l'�tat.
	 * ----------------------------------------------------------*/
	public String getStateServiceName() {
		return _stateServiceName;
	}

	/*----------------------------------------------------------
	 * Nom: getFlag
	 * 
	 * Description:
	 * Cette m�thode retourne l'�tat courant.
	 * 
	 * Retourne: Un caract�re, l'�tat courant.
	 * ----------------------------------------------------------*/
	public char getFlag() {
		return _flag;
	}

	/*----------------------------------------------------------
	 * Nom: setStateServiceName
	 * 
	 * Description:
	 * Cette m�thode modifie le nom de l'�tat avec celui pass� en entr�e.
	 * 
	 * Param�tre : 
	 *   - stateServiceName : Le nouvel �tat courant.
	 * ----------------------------------------------------------*/
	public void setStateServiceName(String stateServiceName) {
		_stateServiceName = stateServiceName;
	}

	/*----------------------------------------------------------
	 * Nom: setFlag
	 * 
	 * Description:
	 * Cette m�thode modifie l'�tat courant avec celui pass� en entr�e.
	 * 
	 * Param�tre : 
	 *   - flag : Le nouvel �tat courant.
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
	 * Cet attribut maintient une r�f�rence sur le nom de l'�tat
	 * d'un service
	 * ----------------------------------------------------------*/
	private String _stateServiceName;
	
	/*----------------------------------------------------------
	 * Nom: _flag
	 *
	 * Description:
	 * Cet attribut repr�sente un �tat associ� au composant vis � vis
	 * de la base de donn�es. 
	 * Celui-ci peut prendre 4 valeurs : 
	 *   - 'A' pour un �l�ment � ajouter dans la base de donn�es.
	 *   - 'E' pour une entr�e non modifi�e depuis la base de donn�es.
	 *   - 'M' pour une entr�e de la base de donn�es mais modifi�e par
	 *     l'utilisateur.
	 *   - 'S' pour une entr�e de la base � supprimer.
	 * ----------------------------------------------------------*/
	private char _flag;
}
