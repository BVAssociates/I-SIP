/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/ConditionFactory.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de condition de requ�tes
* DATE:        10/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConditionFactory.java,v $
* Revision 1.13  2009/01/14 14:22:25  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2007/12/28 17:42:36  tz
* D�placement de la m�thode revertForeignKey() depuis la classe
* ExploreProcessor.
*
* Revision 1.11  2006/10/13 15:09:00  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.10  2005/10/07 08:19:34  tz
* Changement non fonctionnel
*
* Revision 1.9  2005/07/01 12:08:47  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.7  2004/10/13 13:55:14  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.6  2004/07/29 12:02:20  tz
* Suppression d'imports inutiles
*
* Revision 1.5  2002/04/05 15:49:41  tz
* Cloture iteration IT1.2
*
* Revision 1.4  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
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
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisForeignKeyLink;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.console.com.AgentSessionManager;

/*----------------------------------------------------------
* Nom: ConditionFactory
*
* Description:
* Cette classe abstraite a la responsabilit� de convertir des instances
* d'IsisForeignKey en des conditions de requ�tes ex�cutables sur les
* plates-formes h�tes via les ITools (voir la m�thode
* getConditionFromForeignKey()).
* ----------------------------------------------------------*/
public abstract class ConditionFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getConditionFromForeignKey
	*
	* Description:
	* Cette m�thode statique permet de convertir un objet IsisForeignKey
	* correspondant � un lien entre tables en une cha�ne de caract�res contenant
	* la condition de la requ�te I-TOOLS permettant d'impl�menter ce lien
	* relationnel.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent d'o� provient la cl� �trang�re,
	*  - link: Un objet IsisForeignKey contenant le lien � convertir en
	*    condition de requ�te,
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition
	*    contenant la d�finition de la table.
	*
	* Retourne: Une cha�ne de caract�res contenant la condition de la requ�te
	* r�sultant de la conversion.
	* ----------------------------------------------------------*/
	public static String getConditionFromForeignKey(
		String agentName,
		IsisForeignKey link,
		IsisTableDefinition definition
		)
	{
		StringBuffer condition;
		String agent_layer_mode = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "getConditionFromForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("link=" + link);
		trace_arguments.writeTrace("definition=" + definition);
		// Test de l'argument
		if(link == null)
		{
			// On n'a rien � faire, on sort
			trace_methods.endOfMethod();
			return null;
		}
		// On va r�cup�rer le mode de la couche d'ex�cution de l'Agent
		agent_layer_mode = AgentSessionManager.getInstance().getAgentLayerMode(
			agentName);
		condition = new StringBuffer();
		// On construit la condition, s'il y en a besoin:
		// "<fcol1>='${<lcol1>}' AND ... "
		// On ajoute chaque colonne dans la condition
		for(int index = 0 ; index < link.links.length ; index ++)
		{
			String local_column = link.links[index].localColumnName;
			String foreign_column = link.links[index].foreignColumnName;

			if(index != 0)
			{
				// S'il ne s'agit pas du premier lien, il faut ajouter AND
				condition.append(" AND ");
			}
			// On ajoute <fcol1>="${<lcol1>}"
			condition.append(foreign_column);
			char column_type = getColumnType(local_column, definition);
			if(column_type == 's')
			{
			    condition.append("='");
				condition.append(AgentLayerAbstractor.getVariableReference(
					agent_layer_mode, local_column));
				condition.append("'");
			}
			else
			{
			    condition.append("=");
				condition.append(AgentLayerAbstractor.getVariableReference(
					agent_layer_mode, local_column));
				condition.append("");
			}
			trace_debug.writeTrace("condition=" + condition);
		}
		trace_methods.endOfMethod();
		return condition.toString();
	}

	/*----------------------------------------------------------
	* Nom: revertForeignKey
	*
	* Description:
	* Cette m�thode statique permet de renverser une cl� �trang�re afin d'en 
	* faire une cl� �trang�re inverse qui sera utilis�e pour la fabrication de 
	* noeuds table.
	*
	* Arguments:
	*  - foreignKey: La cl� �trang�re � renverser,
	*  - foreignTableName: Le nom de la table �trang�re.
	*
	* Retourne: La cl� �trang�re inverse.
	* ----------------------------------------------------------*/
	public static IsisForeignKey revertForeignKey(
		IsisForeignKey foreignKey,
		String foreignTableName
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "revertForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		IsisForeignKey reverse_foreign_key = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		trace_arguments.writeTrace("foreignTableName=" + foreignTableName);
		// On construit la nouvelle cl� �trang�re
		reverse_foreign_key = new IsisForeignKey(foreignTableName, null);
		reverse_foreign_key.links =
			new IsisForeignKeyLink[foreignKey.links.length];
		// On va inverser les liens un par un
		for(int index = 0 ; index < foreignKey.links.length ; index ++) {
			reverse_foreign_key.links[index] = new IsisForeignKeyLink();
			reverse_foreign_key.links[index].foreignColumnName =
				foreignKey.links[index].localColumnName;
			reverse_foreign_key.links[index].localColumnName =
				foreignKey.links[index].foreignColumnName;
		}
		trace_methods.endOfMethod();
		return reverse_foreign_key;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getColumnType
	*
	* Description:
	* Cette m�thode statique permet de r�cup�rer le caract�re d�finissant le
	* type de la colonne (cha�ne, num�rique...). Elle recherche dans la
	* d�finition de la table la colonne de m�me nom que celle pass�e en
	* argument, et retourne son type.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on veut conna�tre le type,
	*  - definition: Une r�f�rence sur la d�finition de la table.
	*
	* Retourne: Le caract�re d�finissant le type de la colonne.
	* ----------------------------------------------------------*/
	private static char getColumnType(
		String columnName,
		IsisTableDefinition definition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "getColumnType");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		for(int index = 0 ; index < definition.columns.length ; index ++)
		{
			if(definition.columns[index].name.equals(columnName) == true)
			{
				trace_methods.endOfMethod();
				return definition.columns[index].type;
			}
		}
		trace_errors.writeTrace("Colonne inconnue: " + columnName);
		trace_methods.endOfMethod();
		return 's';
	}
}