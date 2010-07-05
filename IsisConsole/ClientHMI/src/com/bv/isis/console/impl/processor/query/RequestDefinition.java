/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/query/RequestDefinition.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de d�finition de requ�te
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
* Mise � jour du mod�le.
*
* Revision 1.1  2002/04/05 15:51:02  tz
* Cloture it�ration IT1.2
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.query;

//
// Imports syst�me
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
* Cette classe est une classe technique charg�e de contenir la d�finition de la
* requ�te construite � partir du processeur de construction de requ�te.
* Elle permet d'encapsuler toutes les informations n�cessaires (nom de la table,
* colonnes s�lectionn�es, condition, ordre de tri) � l'ex�cution d'une requ�te.
* ----------------------------------------------------------*/
class RequestDefinition
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NO_OPERATOR
	*
	* Description:
	* Cet attribut statique d�fini une valeur pour l'argument linkOperator de la
	* m�thode addCriteria() sp�cifiant aucun op�rateur de liaison.
	* ----------------------------------------------------------*/
	final static int NO_OPERATOR = 0;

	/*----------------------------------------------------------
	* Nom: AND_OPERATOR
	*
	* Description:
	* Cet attribut statique d�fini une valeur pour l'argument linkOperator de la
	* m�thode addCriteria() sp�cifiant un op�rateur de liaison de type ET.
	* ----------------------------------------------------------*/
	final static int AND_OPERATOR = 1;

	/*----------------------------------------------------------
	* Nom: OR_OPERATOR
	*
	* Description:
	* Cet attribut statique d�fini une valeur pour l'argument linkOperator de la
	* m�thode addCriteria() sp�cifiant aucun op�rateur de liaison de type OU.
	* ----------------------------------------------------------*/
	final static int OR_OPERATOR = 2;

	/*----------------------------------------------------------
	* Nom: RequestDefinition
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode permet d'ajouter une colonne � la liste des colonnes
	* s�lectionn�es.
	*
	* Arguments:
	*  - column: La colonne � ajouter � la liste des colonnes s�lectionn�es.
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
		// On v�rifie que la colonne est non nulle
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
	* Cette m�thode permet de r�cup�rer la liste des colonnes s�lectionn�es
	* sous forme de cha�ne de caract�res (chaque colonne �tant s�par� par le
	* caract�re ",".
	*
	* Retourne: La liste des colonnes s�lectionn�es sous forme de cha�ne.
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
	* Cette m�thode permet d'ajouter un crit�re � la condition de la requ�te.
	* La condition est enrichie � partir du crit�re, et de l'op�rateur de
	* liaison pass� en argument.
	* L'op�rateur est obligatoire si la condition contient d�j� au moins un
	* crit�re.
	*
	* Arguments:
	*  - linkOperator: L'op�rateur de liaison entre le crit�re pr�c�dent et le
	*    crit�re courant,
	*  - criteria: Le crit�re de condition � ajouter.
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
		// On v�rifie la validit� des arguments
		if(criteria == null || criteria.equals("") == true ||
			linkOperator < NO_OPERATOR || linkOperator > OR_OPERATOR)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide.");
			// Au moins un des arguments n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re les param�tres de configuration
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception.getMessage());
			// Il y a eu une erreur lors de la r�cup�ration de la configuration,
			// on utilise les valeurs par d�faut
		}
		if(linkOperator == NO_OPERATOR)
		{
			// On v�rifie que l'op�rande est pr�sente si un crit�re a d�j� �t�
			// ajout� � la condition
			if(_condition.toString().equals("") == false)
			{
				trace_errors.writeTrace("Aucune op�rande de liaison sp�cifi� !");
				// Aucune op�rande n'a �t� sp�cifi�e alors qu'un crit�re a d�j�
				// �t� ajout�, on sort
				trace_methods.endOfMethod();
				return;
			}
		}
		else
		{
			// On v�rifie qu'un crit�re avait d�j� �t� ajout� � la condition
			if(_condition.toString().equals("") == true)
			{
				trace_errors.writeTrace(
					"Une op�rande de liaison a �t� sp�cifi�e !");
				// Une op�rande a �t� sp�cifi�e alors qu'aucun crit�re n'a �t�
				// ajout�, on sort
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
				// On ajoute l'op�rator de liaison de type ET
				_condition.append(" " + and_operator + " ");
				break;
			case OR_OPERATOR:
				// On ajoute l'op�rateur de liaison de type OU
				_condition.append(" " + or_operator + " ");
				break;
			default:
				break;
		}
		// On ajoute le crit�re
		_condition.append(criteria);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getCondition
	*
	* Description:
	* Cette m�thode permet d'obtenir toute la condition de requ�te contruite
	* par les ajouts successifs de crit�re. La condition est retourn�e sous
	* forme de cha�ne de caract�res.
	*
	* Retourne: La condition de la requ�te.
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
	* Cette m�thode permet d'ajouter un ordre de tri � la d�finition de la
	* requ�te. Suivant l'argument ascending, l'ordre de tri sera ajout� en mode
	* ascendant (ASC) ou descendant (DES).
	*
	* Arguments:
	*  - column: La colonne � ajouter � l'ordre de tri,
	*  - ascending: Indique si le tri doit �tre ascendant (true) ou non (false).
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
		// On v�rifie la validit� de l'argument
		// On v�rifie la validit� des arguments
		if(column == null || column.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide.");
			// Au moins un des arguments n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re les param�tres de configuration
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception.getMessage());
			// Il y a eu une erreur lors de la r�cup�ration de la configuration,
			// on utilise les valeurs par d�faut
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
	* Cette m�thode permet de r�cup�rer l'ordre de tri construit par les appels
	* successifs � la m�thode addSortOrder(). L'ordre de tri est retourn� sous
	* forme de cha�ne de caract�res.
	*
	* Retourne: Une cha�ne de caract�res contenant les ordres de tri de la
	* requ�te.
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
	* Cette m�thode est appel�e automatiquement par le ramasse-miettes de la
	* machine virtuelle Java lorsque un instance est sur le point d'�tre
	* d�truite. Elle permet de lib�rer les ressources allou�es par l'instance.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestDefinition", "finalize");

		trace_methods.beginningOfMethod();
		// On lib�re les r�f�rences
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
	* Cet attribut maintient une r�f�rence sur un objet StringBuffer charg� de
	* contenir la liste des colonnes de la requ�te.
	* ----------------------------------------------------------*/
	private StringBuffer _columns;

	/*----------------------------------------------------------
	* Nom: _condition
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet StringBuffer qui
	* contient la condition de la requ�te.
	* ----------------------------------------------------------*/
	private StringBuffer _condition;

	/*----------------------------------------------------------
	* Nom: _sort
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet StringBuffer qui
	* contient l'ordre de tri de la requ�te.
	* ----------------------------------------------------------*/
	private StringBuffer _sort;
}