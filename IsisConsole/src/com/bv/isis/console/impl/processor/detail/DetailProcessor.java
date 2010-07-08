/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/detail/DetailProcessor.java,v $
* $Revision: 1.18 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage du d�tail d'une instance
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
* Ajout de l'argument postprocessing � la m�thode run().
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
* Traitement du cas o� getNodeLabel() retourne null.
*
* Revision 1.11  2004/10/22 15:40:07  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.9  2004/10/13 13:56:26  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/10/06 07:34:26  tz
* Am�lioration de la dimension de la frame.
*
* Revision 1.7  2004/07/29 12:15:35  tz
* Mise � jour de la documentation
*
* Revision 1.6  2002/12/26 12:55:33  tz
* Affichage d'un message d'�tat
*
* Revision 1.5  2002/06/19 12:17:20  tz
* Correction de la fiche Inuit/25
*
* Revision 1.4  2002/04/05 15:50:29  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:51:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/26 16:57:37  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.detail;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'affichage du
* d�tail d'une instance d'objet de production. Son but est de pr�senter,
* sous forme de tableau, tous les param�tres qui constituent l'objet
* (l'ensemble des IsisParameter utilis�s lors de la construction du noeud 
* graphique).
* Ce processeur est n�cessaire car seule une partie des informations est
* montr�e � l'utilisateur dans l'arbre d'exploration.
* Ce processeur est �galement charg� d'afficher le d�tail d'une instance
* d'objet li�, via les cl�s �trang�res.
* ----------------------------------------------------------*/
public class DetailProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DetailProcessor
	*
	* Description:
	* Cette m�thode est le seul constructeur de la classe. Elle n'est pr�sent�e
	* que pour des raisons de lisibilit�.
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
	* Cette m�thode red�fini celle de la classe ProcessorFrame. Elle est
	* appel�e par le ProcessManager afin d'initialiser et de d'ex�cuter le
	* processeur. Le panneau est construit, soit � partir des param�tres de
	* l'objet s�lectionn�, soit � partir du r�sultat de l'ex�cution d'une
	* requ�te, puis la sous-fen�tre est affich�e.
	*
	* Si un probl�me est d�tect� durant la phase d'initialisation, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fen�tre principale,
	*  - menuItem: Une r�f�rence sur l'objet JMenuItem par lequel le processeur
	*    a �t� ex�cut�. Cet argument peut �tre nul,
	*  - parameters: Une cha�ne de caract�re contenant des param�tres
	*    sp�cifiques au processeur. Cet argument peut �tre nul,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�. Cet argument peut
	*    �tre nul.
	*
	* L�ve: InnerException.
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
		// On appelle la m�thode de la super classe
		super.run(windowInterface, menuItem, parameters,
			preprocessing, postprocessing, selectedNode);
		// On v�rifie la validit� de l'argument
		if(selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud s�lectionn� !");
			// Il n'y a pas de noeud s�lectionn�, c'est une erreur
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		windowInterface.setStatus("&Status_BuildingDetail", null, 0);
		windowInterface.setProgressMaximum(2);
		// D'abord, on regarde s'il s'agit de d�tailler un objet li� ou l'objet
		// s�lectionn�. Cela est d�fini par l'argument parameters, qui doit �tre
		// null dans le deuxi�me cas.
		if(parameters == null || parameters.equals("") == true)
		{
			trace_debug.writeTrace("D�tail de l'objet s�lectionn�");
			// On r�cup�re les param�tres de l'objet
			IsisParameter[] object_parameters =
				((GenericTreeObjectNode)selectedNode).getObjectParameters();
			if(object_parameters == null || object_parameters.length == 0)
			{
				trace_errors.writeTrace("L'objet n'a pas de param�tres !");
				// L'objet n'a pas de param�tres, c'est une erreur
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_ObjectHasNoParameter", null,
					null);
			}
		    windowInterface.setStatus("&Status_BuildingDetail", null, 1);
			// On construit la sous-fen�tre
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
			trace_debug.writeTrace("D�tail d'un objet li�: " + parameters);
			// On doit r�cup�rer les param�tres de l'objet li�, resultant
			// de l'ex�cution de la requ�te (le nom de la table distante et
			// la condition de la requ�te sont contenus dans les param�tres).
			IsisParameter[] object_parameters =
				getDataFromRequest(selected_node, parameters);
		    windowInterface.setStatus("&Status_BuildingDetail", null, 1);
			if(object_parameters == null || object_parameters.length == 0)
			{
				trace_errors.writeTrace("L'objet n'a pas de param�tres !");
				// L'objet n'a pas de param�tres, il faut sortir
				windowInterface.setStatus(null, null, 0);
				close();
				return;
			}
			// On construit la sous-fen�tre
			makePanel(object_parameters, menuItem.getText());
		}
		windowInterface.setStatus("&Status_BuildingDetail", null, 2);

		// On retaille la fen�tre
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour effectuer un pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode permet d'ex�cuter une requ�te, et de r�cup�rer les
	* param�tres de l'objet r�sultant de cette requ�te. Elle effectue le test
	* sur le r�sultat de la requ�te: il doit n'y avoir qu'un seul objet
	* r�sultant de la requ�te, sinon, c'est une erreur.
	*
	* Arguments:
	*  - selectedNode: Le noeud s�lectionn�,
	*  - parameters: Le param�tre d'ex�cution du processeur contenant le nom de
	*    la table et la condition de la requ�te.
	*
	* Retourne: Un tableau d'IsisParameter correspondant aux param�tres
	* de l'objet li� r�sultant de l'ex�cution de la requ�te.
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
		// On r�cup�re le noeud s�lectionn�, c'est lui qui donne le contexte
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		// Le format est <table>@<condition>
		UtilStringTokenizer tokenizer =
			new UtilStringTokenizer(parameters, "@");
		// On v�rifie le format du param�tre
		if(tokenizer.getTokensCount() != 2)
		{
			trace_errors.writeTrace("Param�tre du processeur invalide: " +
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
			// Il faut v�rifier qu'il n'y a qu'un seul objet r�sultat
			if(result.length < 2)
			{
				trace_errors.writeTrace("Il n'y a pas de r�sultat !");
				// Il n'y a aucun r�sultat, on affiche un message d'erreur
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
				trace_errors.writeTrace("Il n'y a trop de r�sultats !");
				// Il n'y a trop de r�sultats, on affiche un message d'erreur
				InnerException exception =
					new InnerException("&ERR_RequestHasTwoMuchResult",
					"" + (result.length - 1), null);
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotDetailLinkedObject", exception);
				// On retourne null
				trace_methods.endOfMethod();
				return null;
			}
			// On construit la d�finition � partir du r�sultat de la requ�te
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
				"Erreur lors de l'ex�cution de la requ�te: " +
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
	* Cette m�thode construit la sous-fen�tre � partir des param�tres d'objet
	* pass�s en argument. Le titre de la fen�tre d�pend du libell� pass� en 
	* second argument.
	*
	* Arguments:
	*  - objectParameters: Un tableau d'IsisParameter contenant les
	*    param�tres � utiliser pour construire la sous-fen�tre,
	*  - objectLabel: Un libell� identifiant l'objet �tant d�taill�.
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
		// D'abord, on positionne le titre de la fen�tre
		setTitle(MessageManager.getMessage("&Detail_Title") +
			objectLabel);
		// Ensuite on cr�e la table
		// La liste des colonnes est constitu� de nom et valeur
		String[] columns = {
			MessageManager.getMessage("&Detail_Name"),
			MessageManager.getMessage("&Detail_Value")
		};
		// On cr�e le tableau des donn�es
		String[][] data = new String[objectParameters.length][2];
		// Le tableau est rempli par les noms et les valeurs des param�tres
		for(int index = 0 ; index < objectParameters.length ; index ++)
		{
			data[index][0] = objectParameters[index].name;
			data[index][1] = objectParameters[index].value;
		}
		// Maintenant, on peut instancier le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(320);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		Dimension table_size = table.getPreferredSize();
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(table_size);
		// On ajoute la zone dans le panneau principal
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Detail_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
				close();
			}
		});
		// On cr�e un panneau avec un GridBagLayout
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