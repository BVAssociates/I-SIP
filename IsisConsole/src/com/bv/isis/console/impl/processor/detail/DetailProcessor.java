/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/detail/DetailProcessor.java,v $
* $Revision: 1.18 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage du détail d'une instance
* DATE:        24/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.detail
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DetailProcessor.java,v $
* Revision 1.18  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.17  2008/02/21 12:07:42  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.16  2008/01/31 16:54:25  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.15  2005/12/23 13:18:40  tz
* Correction mineure.
*
* Revision 1.14  2005/10/07 08:30:03  tz
* Changement mineur.
*
* Revision 1.13  2005/07/01 12:18:55  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/03 15:19:29  tz
* Traitement du cas où getNodeLabel() retourne null.
*
* Revision 1.11  2004/10/22 15:40:07  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.9  2004/10/13 13:56:26  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.8  2004/10/06 07:34:26  tz
* Amélioration de la dimension de la frame.
*
* Revision 1.7  2004/07/29 12:15:35  tz
* Mise à jour de la documentation
*
* Revision 1.6  2002/12/26 12:55:33  tz
* Affichage d'un message d'état
*
* Revision 1.5  2002/06/19 12:17:20  tz
* Correction de la fiche Inuit/25
*
* Revision 1.4  2002/04/05 15:50:29  tz
* Cloture itération IT1.2
*
* Revision 1.3  2002/03/27 09:51:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/26 16:57:37  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.detail;

//
// Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import com.bv.core.util.UtilStringTokenizer;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: DetailProcessor
*
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage du
* détail d'une instance d'objet de production. Son but est de présenter,
* sous forme de tableau, tous les paramètres qui constituent l'objet
* (l'ensemble des IsisParameter utilisés lors de la construction du noeud 
* graphique).
* Ce processeur est nécessaire car seule une partie des informations est
* montrée à l'utilisateur dans l'arbre d'exploration.
* Ce processeur est également chargé d'afficher le détail d'une instance
* d'objet lié, via les clés étrangères.
* ----------------------------------------------------------*/
public class DetailProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DetailProcessor
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle n'est présentée
	* que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DetailProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "DetailProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfini celle de la classe ProcessorFrame. Elle est
	* appelée par le ProcessManager afin d'initialiser et de d'exécuter le
	* processeur. Le panneau est construit, soit à partir des paramètres de
	* l'objet sélectionné, soit à partir du résultat de l'exécution d'une
	* requête, puis la sous-fenêtre est affichée.
	*
	* Si un problème est détecté durant la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le processeur
	*    a été exécuté. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractère contenant des paramètres
	*    spécifiques au processeur. Cet argument peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud sélectionné. Cet argument peut
	*    être nul.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void run(
		MainWindowInterface windowInterface,
		JMenuItem menuItem,
		String parameters,
		String preprocessing,
		String postprocessing,
		DefaultMutableTreeNode selectedNode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On appelle la méthode de la super classe
		super.run(windowInterface, menuItem, parameters,
			preprocessing, postprocessing, selectedNode);
		// On vérifie la validité de l'argument
		if(selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud sélectionné !");
			// Il n'y a pas de noeud sélectionné, c'est une erreur
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		windowInterface.setStatus("&Status_BuildingDetail", null, 0);
		windowInterface.setProgressMaximum(2);
		// D'abord, on regarde s'il s'agit de détailler un objet lié ou l'objet
		// sélectionné. Cela est défini par l'argument parameters, qui doit être
		// null dans le deuxième cas.
		if(parameters == null || parameters.equals("") == true)
		{
			trace_debug.writeTrace("Détail de l'objet sélectionné");
			// On récupère les paramètres de l'objet
			IsisParameter[] object_parameters =
				((GenericTreeObjectNode)selectedNode).getObjectParameters();
			if(object_parameters == null || object_parameters.length == 0)
			{
				trace_errors.writeTrace("L'objet n'a pas de paramètres !");
				// L'objet n'a pas de paramètres, c'est une erreur
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_ObjectHasNoParameter", null,
					null);
			}
		    windowInterface.setStatus("&Status_BuildingDetail", null, 1);
			// On construit la sous-fenêtre
			if(selected_node.getLabel() != null)
			{
				makePanel(object_parameters, selected_node.getLabel().label);
			}
			else
			{
				makePanel(object_parameters, "");
			}
		}
		else
		{
			trace_debug.writeTrace("Détail d'un objet lié: " + parameters);
			// On doit récupérer les paramètres de l'objet lié, resultant
			// de l'exécution de la requête (le nom de la table distante et
			// la condition de la requête sont contenus dans les paramètres).
			IsisParameter[] object_parameters =
				getDataFromRequest(selected_node, parameters);
		    windowInterface.setStatus("&Status_BuildingDetail", null, 1);
			if(object_parameters == null || object_parameters.length == 0)
			{
				trace_errors.writeTrace("L'objet n'a pas de paramètres !");
				// L'objet n'a pas de paramètres, il faut sortir
				windowInterface.setStatus(null, null, 0);
				close();
				return;
			}
			// On construit la sous-fenêtre
			makePanel(object_parameters, menuItem.getText());
		}
		windowInterface.setStatus("&Status_BuildingDetail", null, 2);

		// On retaille la fenêtre
		pack();
		// On l'affiche
		display();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour effectuer un pré-chargement du processeur.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("detail.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DetailProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DetailProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getDataFromRequest
	*
	* Description:
	* Cette méthode permet d'exécuter une requête, et de récupérer les
	* paramètres de l'objet résultant de cette requête. Elle effectue le test
	* sur le résultat de la requête: il doit n'y avoir qu'un seul objet
	* résultant de la requête, sinon, c'est une erreur.
	*
	* Arguments:
	*  - selectedNode: Le noeud sélectionné,
	*  - parameters: Le paramètre d'exécution du processeur contenant le nom de
	*    la table et la condition de la requête.
	*
	* Retourne: Un tableau d'IsisParameter correspondant aux paramètres
	* de l'objet lié résultant de l'exécution de la requête.
	* ----------------------------------------------------------*/
	private IsisParameter[] getDataFromRequest(
		GenericTreeObjectNode selectedNode,
		String parameters
		)
	{
		IsisParameter[] object_parameters = null;
		String[] result = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "getDataFromRequest");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("parameters=" + parameters);
		// On récupère le noeud sélectionné, c'est lui qui donne le contexte
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		// Le format est <table>@<condition>
		UtilStringTokenizer tokenizer =
			new UtilStringTokenizer(parameters, "@");
		// On vérifie le format du paramètre
		if(tokenizer.getTokensCount() != 2)
		{
			trace_errors.writeTrace("Paramètre du processeur invalide: " +
				parameters);
			// On va afficher une erreur
			InnerException exception =
				new InnerException("&ERR_InvalidParameter", parameters,
				null);
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotDetailLinkedObject", exception);
			trace_methods.endOfMethod();
			return null;
		}
		// On utilise toutes les colonnes
		String[] selected_columns = { "" };
		try
		{
			ServiceSessionProxy session_proxy =
				new ServiceSessionProxy(selected_node.getServiceSession());
			result = session_proxy.getSelectResult(tokenizer.getToken(0),
				selected_columns, tokenizer.getToken(1), "",
				selected_node.getContext(true));
			// Il faut vérifier qu'il n'y a qu'un seul objet résultat
			if(result.length < 2)
			{
				trace_errors.writeTrace("Il n'y a pas de résultat !");
				// Il n'y a aucun résultat, on affiche un message d'erreur
				InnerException exception =
					new InnerException("&ERR_RequestHasNoResult", null, null);
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotDetailLinkedObject", exception);
				// On retourne null
				trace_methods.endOfMethod();
				return null;
			}
			else if(result.length > 2)
			{
				trace_errors.writeTrace("Il n'y a trop de résultats !");
				// Il n'y a trop de résultats, on affiche un message d'erreur
				InnerException exception =
					new InnerException("&ERR_RequestHasTwoMuchResult",
					"" + (result.length - 1), null);
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotDetailLinkedObject", exception);
				// On retourne null
				trace_methods.endOfMethod();
				return null;
			}
			// On construit la définition à partir du résultat de la requête
			IsisTableDefinition definition =
				TreeNodeFactory.buildDefinitionFromSelectResult(result,
				tokenizer.getToken(0));
			// On construit le tableau d'IsisParameter
			object_parameters =
				TreeNodeFactory.buildParametersFromSelectResult(result, 1,
				definition);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'exécution de la requête: " +
				exception.getMessage());
			// On affiche un message d'erreur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotDetailLinkedObject", exception);
			trace_methods.endOfMethod();
			return null;
		}
		trace_methods.endOfMethod();
		return object_parameters;
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode construit la sous-fenêtre à partir des paramètres d'objet
	* passés en argument. Le titre de la fenêtre dépend du libellé passé en 
	* second argument.
	*
	* Arguments:
	*  - objectParameters: Un tableau d'IsisParameter contenant les
	*    paramètres à utiliser pour construire la sous-fenêtre,
	*  - objectLabel: Un libellé identifiant l'objet étant détaillé.
	* ----------------------------------------------------------*/
	private void makePanel(
		IsisParameter[] objectParameters,
		String objectLabel
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DetailProcessor", "makePanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("objectParameters=" + objectParameters);
		trace_arguments.writeTrace("objectLabel=" + objectLabel);
		// D'abord, on positionne le titre de la fenêtre
		setTitle(MessageManager.getMessage("&Detail_Title") +
			objectLabel);
		// Ensuite on crée la table
		// La liste des colonnes est constitué de nom et valeur
		String[] columns = {
			MessageManager.getMessage("&Detail_Name"),
			MessageManager.getMessage("&Detail_Value")
		};
		// On crée le tableau des données
		String[][] data = new String[objectParameters.length][2];
		// Le tableau est rempli par les noms et les valeurs des paramètres
		for(int index = 0 ; index < objectParameters.length ; index ++)
		{
			data[index][0] = objectParameters[index].name;
			data[index][1] = objectParameters[index].value;
		}
		// Maintenant, on peut instancier le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(320);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		Dimension table_size = table.getPreferredSize();
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(table_size);
		// On ajoute la zone dans le panneau principal
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Detail_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// Le minimum est 200 x 150
		setMinimumSize(new Dimension(200, 150));
		trace_methods.endOfMethod();
	}
}