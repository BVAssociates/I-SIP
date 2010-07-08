/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/PasswordManager.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des mots de passe
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      Inuit
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: PasswordManager.java,v $
* Revision 1.8  2005/07/01 12:28:49  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/11/23 15:46:22  tz
* Adaptation pour corba-R1_1_2-AL-1_0.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 12:23:11  tz
* Utilisation de Portal* au lieu de Master*
* Mise à jour de la documentation
*
* Revision 1.3  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.2.2.1  2003/10/27 16:56:36  tz
* Support du domaine d'authentification
*
* Revision 1.2  2002/06/19 12:19:13  tz
* Suppression de la trace affichant le mot de passe
*
* Revision 1.1  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.util.Hashtable;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: PasswordManager
*
* Description:
* Cette classe est une classe technique chargée de la gestion des mots de passe
* et des domaines d'authentification de l'utilisateur sur les différentes 
* plates-formes auxquelles il accède (Portail et Agents).
* Elle permet de stocker et de récupérer les mots de passes d'ouverture de 
* session Agent et les domaines, saisis par l'utilisateur lors de l'ouverture 
* de ces sessions.
* Les mots de passe sont stockés par rapport au nom de l'agent concerné via une 
* table de Hash dont la clé est le nom de l'agent. Les mots de passe sont stockés
* en crypté afin qu'ils ne soient pas lisibles. Les domaines d'authentification 
* sont également stockés dans une table de Hash dont la clé est le nom de 
* l'Agent.
* Cette classe implémente le canevas de conception (Design Pattern) Singleton 
* afin qu'une seule et unique instance existe tout au long du cycle de vie de 
* l'application, et que celle-ci soit accessible par toute classe de 
* l'application.
* ----------------------------------------------------------*/
public class PasswordManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette méthode statique fait partie intégrante du canevas de conception
	* Singleton. Elle permet de récupérer l'unique instance de la classe, et
	* éventuellement de la créer.
	*
	* Retourne: L'unique instance de PasswordManager.
	* ----------------------------------------------------------*/
	public static PasswordManager getInstance()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "getInstance");

		trace_methods.beginningOfMethod();
		// Est-ce que l'instance a déjà été construite ?
		if(_instance == null)
		{
			// Il faut la construire
			_instance = new PasswordManager();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette méthode statique fait partie intégrante du canevas de conception
	* Singleton. Elle permet de libérer l'unique instance de la classe, et, par
	* conséquent, de libérer toutes les ressources allouées par celle-ci.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		if(_instance != null)
		{
		    // Il faut libérer les ressources
			_instance._passwords.clear();
			_instance._passwords = null;
			_instance._userName = null;
			_instance = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setUserName
	*
	* Description:
	* Cette méthode permet de fixer le nom de l'utilisateur qui sera réutilisé
	* à chaque ouverture de session Agent.
	*
	* Arguments:
	*  - userName: L'identifiant de l'utilisateur de la Console.
	* ----------------------------------------------------------*/
	public void setUserName(
		String userName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "setUserName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("userName=" + userName);
		// On enregistre le nom de l'utilisateur
		_userName = userName;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getUserName
	*
	* Description:
	* Cette méthode permet de récupérer l'identifiant de l'utilisateur de la
	* Console. Elle retourne la valeur de l'attribut _userName.
	*
	* Retourne: L'identifiant de l'utilisateur ou null.
	* ----------------------------------------------------------*/
	public String getUserName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "getUserName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _userName;
	}

	/*----------------------------------------------------------
	* Nom: addPassword
	*
	* Description:
	* Cette méthode permet d'enregistrer un nouveau mot de passe par rapport à
	* un Agent déterminé. Ce mot de passe pourra ensuite être retrouvé via la
	* méthode getPassword().
	*
	* Arguments:
	*  - agentName: Le nom de l'agent pour lequel le mot de passe doit être
	*    enregistré,
	*  - password: Le mot de passe de l'utilisateur pour l'Agent.
	* ----------------------------------------------------------*/
	public void addPassword(
		String agentName,
		String password
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "addPassword");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		//trace_arguments.writeTrace("password=" + password);
		// On enregistre le mot de passe dans la table
		_passwords.put(agentName, password);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getPassword
	*
	* Description:
	* Cette méthode permet de récupérer le mot de passe qui a été enregistré
	* pour l'Agent spécifié en argument. Si aucun mot de passe n'a été
	* enregistré pour cet Agent, la méthode retournera le mot de passe
	* enregistré pour le Portail.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent pour lequel on souhaite récupérer le mot
	*    de passe de l'utilisateur.
	*
	* Retourne: Le mot de passe de l'utilisateur, ou null.
	* ----------------------------------------------------------*/
	public String getPassword(
		String agentName
		)
	{
		String password = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "getPassword");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// Est-ce que le mot de passe pour l'agent a déjà été enregistré
		if(_passwords.containsKey(agentName) == false)
		{
			// Est-ce qu'il s'agit du Portail ?
			if(agentName.equals("Portal") == true)
			{
				// On retourne null
				password = null;
			}
			else
			{
				// On retourne le mot de passe du Portail
				password = getPassword("Portal");
			}
		}
		else
		{
			// On récupère le mot de passe
			password = (String)_passwords.get(agentName);
		}
		trace_methods.endOfMethod();
		return password;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _instance
	*
	* Description:
	* Cet attribut statique fait partie intégrante du canevas de conception
	* Singleton. Il maintient une référence sur l'unique instance de la classe.
	* ----------------------------------------------------------*/
	private static PasswordManager _instance;

	/*----------------------------------------------------------
	* Nom: _userName
	*
	* Description:
	* Cet attribut maintient le nom de l'utilisateur de la Console, tel qu'il a
	* été sélectionné dans la liste des utilisateurs déclarés. Il sera
	* réutilisé à chaque ouverture de session Agent.
	* ----------------------------------------------------------*/
	private String _userName;

	/*----------------------------------------------------------
	* Nom: _passwords
	*
	* Description:
	* Cet attribut maintient une référence sur une table de Hash destinée à
	* contenir les associations Agent/mot de passe. La clé de la table de Hash
	* est le nom de l'Agent.
	* ----------------------------------------------------------*/
	private Hashtable _passwords;

	/*----------------------------------------------------------
	* Nom: PasswordManager
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle est placée en zone
	* privée conformément au canevas de conception Singleton.
	* Elle instancie la table de Hash destinées à contenir les mots de passe.
	* ----------------------------------------------------------*/
	private PasswordManager()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PasswordManager", "PasswordManager");

		trace_methods.beginningOfMethod();
		// On instancie la table de mots de passe
		_passwords = new Hashtable();
		trace_methods.endOfMethod();
	}
}