/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/GenericTreeClassNode.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de repr�sentation d'un noeud table
* DATE:        10/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: GenericTreeClassNode.java,v $
* Revision 1.10  2005/10/07 11:25:45  tz
* Changement mineur.
*
* Revision 1.9  2005/10/07 08:19:22  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.8  2005/07/01 12:08:23  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.6  2004/10/13 13:55:14  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2004/07/29 12:02:09  tz
* Traitement de la destruction sur arr�t du Portail
*
* Revision 1.4  2002/04/05 15:49:49  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:58:13  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/12/12 09:58:23  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.node;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: GenericTreeClassNode
*
* Description:
* Cette classe correspond � un noeud graphique repr�sentant une table (voir la
* m�thode getTableDefinition() de la classe ServiceSessionInterface).
* Elle sp�cialise la classe GenericTreeObjectNode afin de permettre la
* diff�rentiation des types de noeuds, laquelle est n�cessaire lors de la
* construction du libell� et du menu contextuel. Cette diff�rentiation est
* �galement n�cessaire lors de la phase d'exploration.
* ----------------------------------------------------------*/
public class GenericTreeClassNode
	extends GenericTreeObjectNode
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: GenericTreeClassNode
	*
	* Description:
	* Cette m�thode est le seul constructeur de la classe. Elle permet de
	* d�finir le chemin du dictionnaire de la table, le nom de la table, ainsi
	* que la condition de la requ�te qui sert � l'exploration du noeud.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent � partir duquel l'objet a �t� construit,
	*  - iClesName: Le nom du I-CLES auquel est apparent� le noeud,
	*  - serviceType: Le type du service auquel est apparent� le noeud,
 	*  - definitionFilePath: Le chemin complet d'acc�s au fichier dictionnaire
	*    de la table d'o� a �t� extrait l'objet,
	*  - tableName: Le nom de la table correspondante,
	*  - condition: La condition de la requ�te permettant de r�cup�rer la liste
	*    des noeuds fils.
	* ----------------------------------------------------------*/
	public GenericTreeClassNode(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath,
		String tableName,
		String condition
		)
	{
		super(null, "", agentName, iClesName, serviceType, definitionFilePath, 
			tableName);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeClassNode", "GenericTreeClassNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("condition=" + condition);
		// Enregistrement des informations
		_condition = condition;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getCondition
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la condition de la requ�te � ex�cuter 
	* afin de r�cup�rer la liste des noeuds instance correspondant � 
	* l'exploration du noeud.
	*
	* Retourne: La condition de la requ�te a ex�cuter.
	* ----------------------------------------------------------*/
	public String getCondition()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeClassNode", "getCondition");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _condition;
	}

	/*----------------------------------------------------------
	* Nom: destroy
	*
	* Description:
	* Cette m�thode red�fini celle de la super-classe. Son unique int�r�t est
	* de lib�rer les ressources sp�cifiques aux noeuds de type
	* GenericTreeClassNode. Elle appelle la m�thode de la super-classe.
	* 
	* Arguments:
	*  - portalStopped: Un bool�en indiquant si la destruction est caus�e par
	*    l'arr�t du Portail (true) ou non (false).
	* ----------------------------------------------------------*/
	public void destroy(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeClassNode", "destroy");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("portalStopped=" + portalStopped);
		// On appelle la m�thode destroy de la super classe
		super.destroy(portalStopped);
		// On lib�re la r�f�rence sur la condition
		_condition = null;
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _condition
	*
	* Description:
	* Cet attribut maintient une cha�ne contenant la condition d'une requ�te
	* (Select) qui doit �tre ex�cut�e sur l'Agent pour r�cup�rer la liste des
	* objets enfants de ce noeud.
	* ----------------------------------------------------------*/
	private String _condition;
}