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
// Imports système
//

//
// Imports du projet
//

/*----------------------------------------------------------
 * Nom: PortalApplicationServicesSpecificStates
 * 
 * Description:
 * Cette classe représente une composition particulière d'un service
 * et d'un de ses états.
 * Lors de la spécialisation des états d'une application, il est nécessaire
 * de conserver en mémoire une liste des services qui caractérisent cet état
 * et l'état du service particulier qui a été choisit. C'est le rôle de 
 * cette classe.
 * Elle maintient donc 2 références, une sur un service et une 
 * autre sur un état particulier de ce service.
 * ----------------------------------------------------------*/
public class PortalApplicationServicesSpecificStates {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServicesSpecificStates
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesSpecificStates() {
		
	}
	
	/*----------------------------------------------------------
	 * Nom: getService
	 * 
	 * Description:
	 * Cette méthode retourne le service de la composition
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
	 * Cette méthode modifie le service de la composition en lui donnant 
	 * celui passé en parametre.
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
	 * Cette méthode retourne l'état du service de la composition
	 * 
	 * Retourne: Un PortalApplicationServicesStates : L'état du service.
	 * ----------------------------------------------------------*/
	public PortalApplicationServicesStates getServiceState() {
		return _serviceState;
	}

	/*----------------------------------------------------------
	 * Nom: setServiceState
	 * 
	 * Description:
	 * Cette méthode modifie l'état du service de la composition en 
	 * lui donnant celui passé en parametre.
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
	 * Une référence vers un service
	 * ----------------------------------------------------------*/
	private PortalApplicationServices _service;
	
	/*----------------------------------------------------------
	 * Nom: _serviceState
	 * 
	 * Description: 
	 * Une référence vers un état particulier du service de la 
	 * composition
	 * ----------------------------------------------------------*/
	private PortalApplicationServicesStates _serviceState;
}
