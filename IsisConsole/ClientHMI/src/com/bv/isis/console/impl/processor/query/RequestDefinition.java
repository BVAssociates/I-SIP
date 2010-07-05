/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/query/RequestDefinition.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de définition de requête
* DATE:        28/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.query
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: RequestDefinition.java,v $
* Revision 1.3  2005/07/01 12:11:58  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/10/13 13:55:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.1  2002/04/05 15:51:02  tz
* Cloture itération IT1.2
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.query;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: RequestDefinition
*
* Description:
* Cette classe est une classe technique chargée de contenir la définition de la
* requête construite à partir du processeur de construction de requête.
* Elle permet d'encapsuler toutes les informations nécessaires (nom de la table,
* colonnes sélectionnées, condition, ordre de tri) à l'exécution d'une requête.
* ----------------------------------------------------------*/
class RequestDefinition
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NO_OPERATOR
	*
	* Description:
	* Cet attribut statique défini une valeur pour l'argument linkOperator de la
	* méthode addCriteria() spécifiant aucun opérateur de liaison.
	* ----------------------------------------------------------*/
	final static int NO_OPERATOR = 0;

	/*----------------------------------------------------------
	* Nom: AND_OPERATOR
	*
	* Description:
	* Cet attribut statique défini une valeur pour l'argument linkOperator de la
	* méthode addCriteria() spécifiant un opérateur de liaison de type ET.
	* ----------------------------------------------------------*/
	final static int AND_OPERATOR = 1;

	/*----------------------------------------------------------
	* Nom: OR_OPERATOR
	*
	* Description:
	* Cet attribut statique défini une valeur pour l'argument linkOperator de la
	* méthode addCriteria() spécifiant aucun opérateur de liaison de type OU.
	* ----------------------------------------------------------*/
	final static int OR_OPERATOR = 2;

	/*----------------------------------------------------------
	* Nom: RequestDefinition
	*
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public RequestDefinition()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "RequestDefinition");

		trace_methods.beginningOfMethod();
		// On construit les attributs
		_columns = new StringBuffer("");
		_condition = new StringBuffer("");
		_sort = new StringBuffer("");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addColumn
	*
	* Description:
	* Cette méthode permet d'ajouter une colonne à la liste des colonnes
	* sélectionnées.
	*
	* Arguments:
	*  - column: La colonne à ajouter à la liste des colonnes sélectionnées.
	* ----------------------------------------------------------*/
	public void addColumn(
		String column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "addColumn");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		// On vérifie que la colonne est non nulle
		if(column != null && column.equals("") == false)
		{
			if(_columns.toString().equals("") == false)
			{
				_columns.append(",");
			}
			_columns.append(column);
		}
		else
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"La colonne n'est pas valide: nulle ou vide");
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getColumns
	*
	* Description:
	* Cette méthode permet de récupérer la liste des colonnes sélectionnées
	* sous forme de chaîne de caractères (chaque colonne étant séparé par le
	* caractère ",".
	*
	* Retourne: La liste des colonnes sélectionnées sous forme de chaîne.
	* ----------------------------------------------------------*/
	public String getColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "getColumns");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		// On retourne le contenu de l'attribut _columns
		return _columns.toString();
	}

	/*----------------------------------------------------------
	* Nom: addCriteria
	*
	* Description:
	* Cette méthode permet d'ajouter un critère à la condition de la requête.
	* La condition est enrichie à partir du critère, et de l'opérateur de
	* liaison passé en argument.
	* L'opérateur est obligatoire si la condition contient déjà au moins un
	* critère.
	*
	* Arguments:
	*  - linkOperator: L'opérateur de liaison entre le critère précédent et le
	*    critère courant,
	*  - criteria: Le critère de condition à ajouter.
	* ----------------------------------------------------------*/
	public void addCriteria(
		int linkOperator,
		String criteria
		)
	{
		String or_operator = "OR";
		String and_operator = "AND";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "addCriteria");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("linkOperator=" + linkOperator);
		trace_arguments.writeTrace("criteria=" + criteria);
		// On vérifie la validité des arguments
		if(criteria == null || criteria.equals("") == true ||
			linkOperator < NO_OPERATOR || linkOperator > OR_OPERATOR)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide.");
			// Au moins un des arguments n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère les paramètres de configuration
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-TOOLS");
			and_operator = configuration_api.getString("Request.AndOperator");
			or_operator = configuration_api.getString("Request.OrOperator");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception.getMessage());
			// Il y a eu une erreur lors de la récupération de la configuration,
			// on utilise les valeurs par défaut
		}
		if(linkOperator == NO_OPERATOR)
		{
			// On vérifie que l'opérande est présente si un critère a déjà été
			// ajouté à la condition
			if(_condition.toString().equals("") == false)
			{
				trace_errors.writeTrace("Aucune opérande de liaison spécifié !");
				// Aucune opérande n'a été spécifiée alors qu'un critère a déjà
				// été ajouté, on sort
				trace_methods.endOfMethod();
				return;
			}
		}
		else
		{
			// On vérifie qu'un critère avait déjà été ajouté à la condition
			if(_condition.toString().equals("") == true)
			{
				trace_errors.writeTrace(
					"Une opérande de liaison a été spécifiée !");
				// Une opérande a été spécifiée alors qu'aucun critère n'a été
				// ajouté, on sort
				trace_methods.endOfMethod();
				return;
			}
		}
		switch(linkOperator)
		{
			case NO_OPERATOR:
				// On ne fait rien
				break;
			case AND_OPERATOR:
				// On ajoute l'opérator de liaison de type ET
				_condition.append(" " + and_operator + " ");
				break;
			case OR_OPERATOR:
				// On ajoute l'opérateur de liaison de type OU
				_condition.append(" " + or_operator + " ");
				break;
			default:
				break;
		}
		// On ajoute le critère
		_condition.append(criteria);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getCondition
	*
	* Description:
	* Cette méthode permet d'obtenir toute la condition de requête contruite
	* par les ajouts successifs de critère. La condition est retournée sous
	* forme de chaîne de caractères.
	*
	* Retourne: La condition de la requête.
	* ----------------------------------------------------------*/
	public String getCondition()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "getCondition");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		// On retourne le contenu de l'attribut _condition
		return _condition.toString();
	}

	/*----------------------------------------------------------
	* Nom: addSortOrder
	*
	* Description:
	* Cette méthode permet d'ajouter un ordre de tri à la définition de la
	* requête. Suivant l'argument ascending, l'ordre de tri sera ajouté en mode
	* ascendant (ASC) ou descendant (DES).
	*
	* Arguments:
	*  - column: La colonne à ajouter à l'ordre de tri,
	*  - ascending: Indique si le tri doit être ascendant (true) ou non (false).
	* ----------------------------------------------------------*/
	public void addSortOrder(
		String column,
		boolean ascending
		)
	{
		String ascending_mode = "ASC";
		String descending_mode = "DESC";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "addSortOrder");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("ascending=" + ascending);
		// On vérifie la validité de l'argument
		// On vérifie la validité des arguments
		if(column == null || column.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide.");
			// Au moins un des arguments n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère les paramètres de configuration
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-TOOLS");
			descending_mode = configuration_api.getString("Request.Descending");
			ascending_mode = configuration_api.getString("Request.Ascending");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception.getMessage());
			// Il y a eu une erreur lors de la récupération de la configuration,
			// on utilise les valeurs par défaut
		}
		// On ajoute l'ordre de tri
		if(_sort.toString().equals("") == false)
		{
			_sort.append(", ");
		}
		if(ascending == true)
		{
		    _sort.append(column + " " + ascending_mode);
		}
		else
		{
		    _sort.append(column + " " + descending_mode);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getSort
	*
	* Description:
	* Cette méthode permet de récupérer l'ordre de tri construit par les appels
	* successifs à la méthode addSortOrder(). L'ordre de tri est retourné sous
	* forme de chaîne de caractères.
	*
	* Retourne: Une chaîne de caractères contenant les ordres de tri de la
	* requête.
	* ----------------------------------------------------------*/
	public String getSort()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "getSort");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		// On retourne le contenu de l'attribut _sort
		return _sort.toString();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette méthode est appelée automatiquement par le ramasse-miettes de la
	* machine virtuelle Java lorsque un instance est sur le point d'être
	* détruite. Elle permet de libérer les ressources allouées par l'instance.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "finalize");

		trace_methods.beginningOfMethod();
		// On libère les références
		_columns = null;
		_condition = null;
		_sort = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _columns
	*
	* Description:
	* Cet attribut maintient une référence sur un objet StringBuffer chargé de
	* contenir la liste des colonnes de la requête.
	* ----------------------------------------------------------*/
	private StringBuffer _columns;

	/*----------------------------------------------------------
	* Nom: _condition
	*
	* Description:
	* Cet attribut maintient une référence sur un objet StringBuffer qui
	* contient la condition de la requête.
	* ----------------------------------------------------------*/
	private StringBuffer _condition;

	/*----------------------------------------------------------
	* Nom: _sort
	*
	* Description:
	* Cet attribut maintient une référence sur un objet StringBuffer qui
	* contient l'ordre de tri de la requête.
	* ----------------------------------------------------------*/
	private StringBuffer _sort;
}