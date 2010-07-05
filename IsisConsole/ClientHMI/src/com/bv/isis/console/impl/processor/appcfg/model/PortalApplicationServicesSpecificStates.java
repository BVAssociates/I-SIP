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
// Imports syst�me
//

//
// Imports du projet
//

/*----------------------------------------------------------
 * Nom: PortalApplicationServicesSpecificStates
 * 
 * Description:
 * Cette classe repr�sente une composition particuli�re d'un service
 * et d'un de ses �tats.
 * Lors de la sp�cialisation des �tats d'une application, il est n�cessaire
 * de conserver en m�moire une liste des services qui caract�risent cet �tat
 * et l'�tat du service particulier qui a �t� choisit. C'est le r�le de 
 * cette classe.
 * Elle maintient donc 2 r�f�rences, une sur un service et une 
 * autre sur un �tat particulier de ce service.
 * ----------------------------------------------------------*/
public class PortalApplicationServicesSpecificStates {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServicesSpecificStates
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesSpecificStates() {
		
	}
	
	/*----------------------------------------------------------
	 * Nom: getService
	 * 
	 * Description:
	 * Cette m�thode retourne le service de la composition
	 * 
	 * Retourne: Un PortalApplicationServices : Le service.
	 * ----------------------------------------------------------*/
	public PortalApplicationServices getService() {
		return _service;
	}

	/*----------------------------------------------------------
	 * Nom: setService
	 * 
	 * Description:
	 * Cette m�thode modifie le service de la composition en lui donnant 
	 * celui pass� en parametre.
	 * 
	 * Arguments:
	 * - service: Le nouveau nom du service
	 * ----------------------------------------------------------*/
	public void setService(PortalApplicationServices service) {
		_service = service;
	}

	/*----------------------------------------------------------
	 * Nom: getService
	 * 
	 * Description:
	 * Cette m�thode retourne l'�tat du service de la composition
	 * 
	 * Retourne: Un PortalApplicationServicesStates : L'�tat du service.
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesStates getServiceState() {
		return _serviceState;
	}

	/*----------------------------------------------------------
	 * Nom: setServiceState
	 * 
	 * Description:
	 * Cette m�thode modifie l'�tat du service de la composition en 
	 * lui donnant celui pass� en parametre.
	 * 
	 * Arguments:
	 * - service: Le nouveau nom du service
	 * ----------------------------------------------------------*/
	public void setServiceState(PortalApplicationServicesStates state) {
		_serviceState = state;
	}

	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _service
	 * 
	 * Description: 
	 * Une r�f�rence vers un service
	 * ----------------------------------------------------------*/
	private PortalApplicationServices _service;
	
	/*----------------------------------------------------------
	 * Nom: _serviceState
	 * 
	 * Description: 
	 * Une r�f�rence vers un �tat particulier du service de la 
	 * composition
	 * ----------------------------------------------------------*/
	private PortalApplicationServicesStates _serviceState;
}
