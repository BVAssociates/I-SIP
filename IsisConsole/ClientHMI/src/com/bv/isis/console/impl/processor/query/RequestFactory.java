/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/query/RequestFactory.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de création de requête
* DATE:        29/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.query
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: RequestFactory.java,v $
* Revision 1.10  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.9  2008/01/31 16:58:09  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.8  2005/10/07 08:24:25  tz
* Changement non fonctionnel
*
* Revision 1.7  2005/07/01 12:11:48  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/10/13 13:55:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2004/07/29 12:04:29  tz
* Suppression des warnings
*
* Revision 1.4  2003/03/07 16:20:36  tz
* Ajout de l'auto-exploration
*
* Revision 1.3  2002/08/13 13:03:08  tz
* Retrait des black-slash sur opérateurs < et >
*
* Revision 1.2  2002/05/29 09:16:43  tz
* Correction fiches Inuit/21, Inuit/22 et Inuit/23
* Cloture R1.0.3
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
import java.util.Hashtable;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.processor.ProcessorManager;

/*----------------------------------------------------------
* Nom: RequestFactory
*
* Description:
* Cette classe abstraite est chargée de la construction d'un objet
* RequestDefinition, du contrôle de la validité des valeurs saisies dans les
* critères, de la visualisation de la commande d'une requête, et enfin, de
* l'exécution de la requête.
* ----------------------------------------------------------*/
abstract class RequestFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: buildRequest
	*
	* Description:
	* Cette méthode permet de construire un objet RequestDefinition contenant
	* tous les éléments de la requête définie par l'utilisateur. Si une
	* information nécessaire à la définition de la requête manque, ou qu'elle
	* est incomplète, la méthode retournera une référence nulle.
	*
	* Arguments:
	*  - frameComponents: Une table de hash contenant les références de tous
	*    les composants graphiques nécessaires à la construction de la requête,
	*  - tableDefinition: Un objet IsisTableDefinition contenant la définition
	*    de la table,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
	*
	* Retourne: Un objet RequestDefinition contenant l'ensemble des informations
	* définies par l'utilisateur, ou null.
	* ----------------------------------------------------------*/
	public static RequestDefinition buildRequest(
		Hashtable frameComponents,
		IsisTableDefinition tableDefinition,
		MainWindowInterface windowInterface
		)
	{
		RequestDefinition request = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "buildRequest");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("frameComponents=" + frameComponents);
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// On commence par créer la définition de la requête
		request = new RequestDefinition();
		// Récupération de la référence sur la barre à onglets
		JTabbedPane tabbed_pane =
			(JTabbedPane)frameComponents.get("TabbedPane");
		// On récupère la référence sur la liste des colonnes sélectionnées
		JList selected_columns = (JList)frameComponents.get("SelectedList");
		// On regarde s'il y a des colonnes
		Object[] selected_values =
			((DefaultListModel)selected_columns.getModel()).toArray();
		if(selected_values == null || selected_values.length == 0)
		{
			trace_debug.writeTrace("Aucune colonne n'est sélectionnée !");
			// Aucune colonne n'est sélectionnée, on sort
			trace_methods.endOfMethod();
			return null;
		}
		// On va ajouter toutes les colonnes sélectionnées
		for(int index = 0 ; index < selected_values.length ; index ++)
		{
			request.addColumn(selected_values[index].toString());
		}
		// Maintenant, on ajoute les critères de sélection
		for(int index = 1 ; index < 5 ; index ++)
		{
			// On récupère les références sur les objets graphiques de la ligne
			JComboBox link_combo = null;
			if(index > 1)
			{
				link_combo =
					(JComboBox)frameComponents.get("CriteriaLink" + index);
			}
			JComboBox column_combo =
				(JComboBox)frameComponents.get("CriteriaColumn" + index);
			JComboBox operator_combo =
				(JComboBox)frameComponents.get("CriteriaOperator" + index);
			JTextField value_field =
				(JTextField)frameComponents.get("CriteriaValue" + index);
			// Si le combo de colonne est invalide, on s'arrête
			int selected_index = column_combo.getSelectedIndex();
			if(column_combo.isEnabled() == false)
			{
				break;
			}
			// On vérifie que les données sont cohérentes
			int link_operator = RequestDefinition.NO_OPERATOR;
			if(link_combo != null && link_combo.getSelectedIndex() > 0)
			{
				// Si un opérateur de liaison a été choisi, il doit y avoir
				// un nom de colonne
				if(selected_index <= 0)
				{
					// La requête n'est pas complete, on sort
					trace_methods.endOfMethod();
					return null;
				}
				if(link_combo.getSelectedIndex() == 1)
				{
					link_operator = RequestDefinition.AND_OPERATOR;
				}
				else
				{
					link_operator = RequestDefinition.OR_OPERATOR;
				}
			}
			if(selected_index <= 0)
			{
				break;
			}
			// Si une colonne a été choisie, la valeur associée doit être
			// valide
			if(checkValueType(value_field.getText(),
				tableDefinition.columns[selected_index - 1],
				windowInterface) == false)
			{
				// On affiche l'onglet de la condition
				tabbed_pane.setSelectedIndex(1);
				value_field.setSelectionStart(0);
				value_field.setSelectionEnd(value_field.getText().length());
				value_field.requestFocus();
				// La requête n'est pas complete, on sort
				trace_methods.endOfMethod();
				return null;
			}
			// On construit la condition
			StringBuffer condition = new StringBuffer();
			condition.append(column_combo.getSelectedItem().toString());
			String the_operator =
				operator_combo.getSelectedItem().toString();
			condition.append(the_operator);
			// S'il s'agit d'une colonne de type chaîne, il faut placer
			// la valeur entre doubles-quotes
			if(tableDefinition.columns[selected_index - 1].type == 's')
			{
				condition.append("'" + value_field.getText() + "'");
			}
			else
			{
				condition.append(value_field.getText());
			}
			request.addCriteria(link_operator, condition.toString());
		}
		// Maintenant, on ajoute les critères de tri
		for(int index = 1 ; index < 5 ; index ++)
		{
			// On récupère les références sur les objets graphiques de la ligne
			JComboBox column_combo =
				(JComboBox)frameComponents.get("SortColumn" + index);
			JCheckBox ascending_box =
				(JCheckBox)frameComponents.get("SortAscending" + index);
			// Si la combo n'est pas valide, ou qu'aucune colonne n'est
			// sélectionnée, on s'arrête
			if(column_combo.getSelectedIndex() == 0 ||
				column_combo.isEnabled() == false)
			{
				break;
			}
			// On ajoute l'ordre de tri
			request.addSortOrder(column_combo.getSelectedItem().toString(),
				ascending_box.isSelected());
		}
		trace_methods.endOfMethod();
		return request;
	}

	/*----------------------------------------------------------
	* Nom: checkValueType
	*
	* Description:
	* Cette méthode permet de vérifier qu'une valeur saisie par l'utilisateur
	* dans un des champs d'une condition correspond au type spécifié par la
	* colonne sélectionnée, passée en argument.
	*
	* Arguments:
	*  - value: La valeur à vérifier,
	*  - column: Un objet IsisTableColumn correspondant à la colonne
	*    sélectionnée,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
 	*
	* Retourne: true si la valeur est du bon type, false sinon.
	* ----------------------------------------------------------*/
	public static boolean checkValueType(
		String value,
		IsisTableColumn column,
		MainWindowInterface windowInterface
		)
	{
		boolean type_match = false;
		String reason = "";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "checkValueType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("column=" + column);
		// On va contrôler en fonction du type de la colonne
		switch(column.type)
		{
			case 's':
				// Type chaîne, tout ira
				type_match = true;
				break;
			case 'n':
				// Type numérique, on va tenter de le convertir en flottant
				try
				{
					Float.parseFloat(value);
					type_match = true;
				}
				catch(Exception exception)
				{
					reason = "&ERR_ValueIsNotNumeric";
				}
				break;
			case 'b':
				// Type booléen, doit être égal à 0 ou 1
				if(value.equals("0") == true || value.equals("1") == true)
				{
					type_match = true;
				}
				else
				{
					reason = "&ERR_ValueIsNotBoolean";
				}
				break;
			case 'd':
				reason = "&ERR_ValueIsNotDate";
				// Type date, la longueur doit être de 14 caractères, et de type
				// numérique
				if(value.length() != 14)
				{
					break;
				}
				try
				{
					int year = Integer.parseInt(value.substring(0, 4));
					int month = Integer.parseInt(value.substring(4, 6));
					int day = Integer.parseInt(value.substring(6, 8));
					int hour = Integer.parseInt(value.substring(8, 10));
					int minutes = Integer.parseInt(value.substring(10, 12));
					int seconds = Integer.parseInt(value.substring(12, 14));
					// Vérification de la validité des informations
					if(year < 1900 || month < 1 || month > 12 || day < 1 ||
						day > 31 || hour < 0 || hour > 23 || minutes < 0 ||
						minutes > 59 || seconds < 0 || seconds > 59)
					{
						break;
					}
					type_match = true;
				}
				catch(Exception exception)
				{
					// Vide
				}
				break;
			case 'p':
				reason = "&ERR_ValueIsNotPercent";
				// Type pourcentage, doit être numérique et comprise entre
				// 0 et 100
				float float_value = 0;
				try
				{
					float_value = Float.parseFloat(value);
				}
				catch(Exception exception)
				{
					break;
				}
				if(float_value >= 0 && float_value <= 100)
				{
					type_match = true;
				}
				break;
			default:
				trace_debug.writeTrace("Type inconnu: " + column.type);
				reason = "&ERR_UnknownType";
				break;
		}
		trace_debug.writeTrace("type_match=" + type_match);
		if(type_match == false && windowInterface != null)
		{
			windowInterface.showPopup("Error", reason, null);
		}
		trace_methods.endOfMethod();
		return type_match;
	}

	/*----------------------------------------------------------
	* Nom: buildCommand
	*
	* Description:
	* Cette méthode permet de construire la commande I-TOOLS correspondant à la
	* requête définie par l'utilisateur. La définition de la requête en
	* question est passée en argument.
	*
	* Arguments:
	*  - tableName: Le nom de la table sur laquelle la requête doit être
	*    exécutée,
	*  - requestDefinition: La définition de la requête.
	* ----------------------------------------------------------*/
	public static String buildCommand(
		String tableName,
		RequestDefinition requestDefinition
		)
	{
		StringBuffer request = new StringBuffer();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "buildCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("requestDefinition=" + requestDefinition);
		// On construit la commande de la requête
		request.append("Select ");
		// On ajoute la liste des colonnes
		request.append(requestDefinition.getColumns());
		// On ajoute le nom de la table
		request.append(" \\\nfrom " + tableName);
		// On ajoute la condition (s'il y en a une)
		String condition = requestDefinition.getCondition();
		if(condition.equals("") == false)
		{
			request.append(" \\\nwhere \"" + condition + "\"");
		}
		// On ajoute l'ordre de tri, s'il y en a un
		String sort = requestDefinition.getSort();
		if(sort.equals("") == false)
		{
			request.append(" \\\norder_by " + sort);
		}
		trace_methods.endOfMethod();
		return request.toString();
	}

	/*----------------------------------------------------------
	* Nom: executeRequest
	*
	* Description:
	* Cette méthode permet d'exécuter le processeur d'exécution de requête (et
	* d'affichage du résultat sous forme de tableau) "DisplayTable" à partir de
	* la définition d'une requête construite par l'utilisateur via la
	* processeur de définition de requête "Query".
	*
	* Si un problème survient lors de l'exécution du processeur, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - tableName: Le nom de la table sur laquelle la requête doit être
	*    exécutée,
	*  - requestDefinition: Un objet RequestDefinition contenant la définition
	*    de la table,
	*  - selectedNode: Une référence sur le noeud graphique sélectionné,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static void executeRequest(
		String tableName,
		RequestDefinition requestDefinition,
		GenericTreeObjectNode selectedNode,
		MainWindowInterface windowInterface
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RequestFactory", "executeRequest");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("requestDefinition=" + requestDefinition);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On vérifie que les arguments sont valides
		if(tableName == null || tableName.equals("") == true ||
			requestDefinition == null || selectedNode == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Au moins un argument n'est pas valide.");
			// Au moins un des arguments n'est pas valide, on lève une
			// exception afin d'afficher une erreur à l'utilisateur
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On construit les paramètres du processeur. La syntaxe est
		// <table>@<col1>,<col2>@<condition>@<sort>
		StringBuffer parameters = new StringBuffer(tableName);
		// On ajoute la liste des colonnes
		parameters.append("@" + requestDefinition.getColumns());
		// On ajoute la condition de la requête
		parameters.append("@" + requestDefinition.getCondition());
		// On ajoute l'ordre de tri
		parameters.append("@" + requestDefinition.getSort());
		trace_debug.writeTrace("parameters=" + parameters.toString());
		// On exécute le processeur d'affichage sous forme de tableau "Display"
		ProcessorManager.executeProcessor("DisplayTable", windowInterface, null,
			parameters.toString(), null, null, selectedNode, false, false);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}