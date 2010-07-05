/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayProcessor.java,v $
* $Revision: 1.39 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage du résultat d'une requête en tableau
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayProcessor.java,v $
* Revision 1.39  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.38  2009/01/08 15:26:22  tz
* Modification d'ordre esthétique.
*
* Revision 1.37  2008/08/05 15:53:20  tz
* Suppression des données du tableau avant masquage de la fenêtre.
*
* Revision 1.36  2008/02/21 12:07:52  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.35  2008/01/31 16:54:51  tz
* Classe PreprocessingHandler renommée.
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.34  2008/01/29 15:52:15  tz
* Correction de la fiche FS#464.
* Correction de l'appel à la méthode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.33  2007/12/07 10:32:48  tz
* Adaptation pour l'administration améliorée.
*
* Revision 1.32  2007/10/23 11:56:11  tz
* Enregistrement dans les logs I-SIS de l'affichage.
*
* Revision 1.31  2007/09/24 10:41:47  tz
* Protection du rechargement automatique.
* Période de rechargement exprimée en minutes.
*
* Revision 1.30  2007/03/23 15:27:58  tz
* Correction du problème d'affichage des requêtes avec sélection
* des colonnes. Le tableau est construit après exécution de la requête.
*
* Revision 1.29  2006/11/09 12:10:02  tz
* Adaptation à la nouvelle méthode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.28  2006/10/13 15:12:37  tz
* Adaptation aux modifications de la classe ServiceSessionProxy.
*
* Revision 1.27  2006/08/11 13:34:56  tz
* Stockage du contexte avec pré-processing.
*
* Revision 1.26  2006/03/20 15:57:57  tz
* Ajout de la méthode getCellRendererForColumn(),
* Support d'un gestionnaire de rendu spécifique.
*
* Revision 1.25  2006/03/09 13:38:27  tz
* Ajout de la gestion du rechargement automatique.
*
* Revision 1.24  2006/03/08 14:09:51  tz
* Ajout des méthodes setTableName() et getTableName().
*
* Revision 1.23  2005/12/23 13:19:07  tz
* Correction mineure.
*
* Revision 1.22  2005/10/07 13:40:38  tz
* Modification du mode de redimensionnement automatique.
*
* Revision 1.21  2005/10/07 08:29:40  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
* Suppression de _acceptErrors et de setAcceptErrors().
*
* Revision 1.20  2005/07/06 10:07:21  tz
* Libération de la définition de la table même en cas d'erreur.
* Evitement du rafraîchissement de l'affichage en cas de rechargement.
*
* Revision 1.19  2005/07/01 12:18:36  tz
* Modification du composant pour les traces
* Support de la fonctionnalité de rechargement des données
*
* Revision 1.18  2004/11/23 15:41:51  tz
* Utilisation de la méthode getWideSelectResult().
*
* Revision 1.17  2004/11/09 15:25:37  tz
* Modification de la liste des colonnes vide.
*
* Revision 1.16  2004/11/03 15:18:59  tz
* Suppression du mode de sélection.
*
* Revision 1.15  2004/11/02 08:54:55  tz
* Gestion des leasings sur les définitions.
*
* Revision 1.14  2004/10/22 15:39:31  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.13  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.12  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.11  2004/10/06 07:33:01  tz
* Amélioration de la dimension de la frame : gestion des largeurs des
* colonnes sur redimmensionnement.
*
* Revision 1.10  2004/07/29 12:10:06  tz
* Suppression d'imports inutiles
* Mise à jour de la documentation
* Utilisation de DisplayProcessorInterface
*
* Revision 1.9  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.8.2.1  2003/10/27 16:53:32  tz
* Modification du label du message d'erreur ERR_ErrorOnRequest
*
* Revision 1.8  2002/11/22 15:28:20  tz
* Cloture IT1.0.7
* Ajout du AcceptErrors (et construction d'une entête bidon).
*
* Revision 1.7  2002/11/19 08:42:46  tz
* Gestion de la progression de la tâche.
*
* Revision 1.6  2002/08/13 13:13:11  tz
* Ajout méthode d'accès au modèle des données
*
* Revision 1.5  2002/06/19 12:16:49  tz
* Modification pour processeur d'administration
*
* Revision 1.4  2002/04/05 15:50:35  tz
* Cloture itération IT1.2
*
* Revision 1.3  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Cursor;
import javax.swing.JLabel;
import com.bv.core.message.MessageManager;
import javax.swing.JPanel;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: DisplayProcessor
*
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage du
* résultat d'une requête sous forme de tableau. Ce processeur revient à
* effectuer un affichage détaillé de tous les noeuds résultant d'une requête.
* Ce processeur est nécessaire car seule une partie des informations est montrée
* à l'utilisateur dans l'arbre d'exploration.
* Il peut également être utilisé pour visualiser le résultat d'une requête
* construite par l'utilisateur.
* ----------------------------------------------------------*/
public class DisplayProcessor
	extends ProcessorFrame
	implements DisplayProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayProcessor
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DisplayProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "DisplayProcessor");

		trace_methods.beginningOfMethod();
		_keyPresent = false;
		_reloadPeriod = 0;
		_reloadTimer = null;
		_reloadInProgress = false;
		_logData = false;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfinit celle de la classe ProcessorFrame. Elle est
	* appelée par le ProcessManager afin d'initialiser et de d'exécuter le
	* processeur.  Les données de la requêtes sont extraites des arguments ou 
	* du noeud sélectionné, le panneau est construit, les données sont 
	* chargées (via la méthode reloadData()) puis la sous-fenêtre est affichée.
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
			"DisplayProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String the_columns = null;
		IsisTableDefinition table_definition = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, il faut vérifier l'intégrité des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// Message d'état
		windowInterface.setProgressMaximum(4);
		windowInterface.setStatus("&Status_BuildingTable", null, 0);
		// On cast le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters,
			preprocessing, postprocessing, selectedNode);
		// Si le paramètre est null, la requête provient de l'objet sélectionné
		if(parameters == null || parameters.equals("") == true)
		{
			if((selected_node instanceof GenericTreeClassNode)) {
				_condition = ((GenericTreeClassNode)selected_node).getCondition();
			}
			else {
				_condition = "";
			}
			_selectedColumns = new String[0];
			setTableName(selected_node.getTableName());
			_sortOrder = "";
			// On positionne le titre de la fenêtre
			setTitle(MessageManager.getMessage("&Display_Title") +
				selected_node.getLabel().label);
		}
		else
		{
			// On extrait le nom de la table, la liste des colonnes et la
			// condition des paramètres.
			// Le format est: <table>[@<colonnes>[@<condition>[@<sort>[@<log?>]]]
			UtilStringTokenizer tokenizer =
				new UtilStringTokenizer(parameters, "@");
			switch(tokenizer.getTokensCount())
			{
				case 5:
					if(tokenizer.getToken(4).equals("1") == true)
					{
						_logData = true;
					}
				case 4:
					_sortOrder = tokenizer.getToken(3);
				case 3:
					_condition = tokenizer.getToken(2);
				case 2:
					the_columns = tokenizer.getToken(1);
				case 1:
					setTableName(tokenizer.getToken(0));
					break;
				default:
					windowInterface.showPopup("Error",
						"&ERR_InvalidParametersFormat", null);
					windowInterface.setStatus(null, null, 0);
					// On sort
					close();
					trace_methods.endOfMethod();
					return;
			}
			if(the_columns != null && the_columns.equals("") == false)
			{
				// On découpe la liste des colonnes
				tokenizer = new UtilStringTokenizer(the_columns, ",");
				_selectedColumns = new String[tokenizer.getTokensCount()];
				for(int index = 0 ; index < tokenizer.getTokensCount() ;
					index ++)
				{
					_selectedColumns[index] = tokenizer.getToken(index);
				}
			}
			else
			{
				_selectedColumns = new String[0];
			}
			// On positionne le titre de la fenêtre
			if(menuItem != null)
			{
			    setTitle(menuItem.getText());
			}
			else
			{
				setTitle(MessageManager.getMessage("&RequestExecutionResult"));
			}
		}
		windowInterface.setStatus("&Status_BuildingTable", null, 1);
		trace_debug.writeTrace("_tableName=" + getTableName());
		trace_debug.writeTrace("_selectedColumns=" + _selectedColumns);
		trace_debug.writeTrace("_condition=" + _condition);
		trace_debug.writeTrace("_sortOrder=" + _sortOrder);
		// On récupère le gestionnaire de définitions de table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		// On construit le contexte et les évaluations
		_nodeContext = selected_node.getContext(true);
		// Il faut traiter le préprocessing
		ProcessingHandler.handleProcessingStatement(preprocessing, _nodeContext, 
			windowInterface, AgentSessionManager.getInstance().getAgentLayerMode(
			selected_node.getAgentName()), selected_node.getServiceSession(), 
			this, true);
		if(_selectedColumns == null || _selectedColumns.length == 0)
		{
			// On tente de récupérer la définition de la table, on considère 
			// que celle-ci n'a jamais été chargée
			table_definition = manager.getTableDefinition(
				selected_node.getAgentName(), selected_node.getIClesName(), 
				selected_node.getServiceType(), getTableName(), _nodeContext,
				selected_node.getServiceSession());
			windowInterface.setStatus("&Status_BuildingTable", null, 2);
			// On crée la fenêtre
			try
			{
				makePanel(table_definition);
				windowInterface.setStatus("&Status_BuildingTable", null, 3);
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la construction du tableau: "
					+ exception);
				// Il faut afficher un message à l'utilisateur
				windowInterface.showPopupForException(
					"&ERR_CannotBuildTable", exception);
				windowInterface.setStatus(null, null, 0);
				// On libère l'utilisation de la définition
				manager.releaseTableDefinitionLeasing(table_definition);
				// On sort
				close();
				trace_methods.endOfMethod();
				return;
			}
		}
		// On charge les données
		reloadData(true);
		windowInterface.setStatus("&Status_BuildingTable", null, 4);
		// On libère l'utilisation de la définition
		if(table_definition != null)
		{
			manager.releaseTableDefinitionLeasing(table_definition);
		}
		// On positionne les dimensions de la fenêtre
		setMinimumSize(new Dimension(200, 150));
		// On affiche la fenêtre
		display();
		checkFrameSize();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode est appelée lors de la fermeture de la fenêtre. Elle permet
	* de libérer les ressources.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "close");

		trace_methods.beginningOfMethod();
		// On arrête le timer, s'il y en a un
		if(_reloadTimer != null)
		{
			_reloadTimer.stop();
			_reloadTimer = null;
		}
		if(_dataModel != null)
		{
			_dataModel.setFireEventsOnChange(false);
			while(_dataModel.getRowCount() > 0)
			{
				_dataModel.removeRow(0);
			}
			_dataModel.setFireEventsOnChange(true);
			_dataModel.fireTableDataChanged();
		}
		super.close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isKeyPresent
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface DisplayProcessorInterface.
	* Elle permet de savoir si toutes les colonnes composant la clé de l'objet 
	* (par rapport à la définition de la table) sont présentes ou non.
	* Elle retourne la valeur du paramètre _keyPresent.
	*
	* Retourne: true si toutes les colonnes composant la clé sont présentes,
	* false sinon.
	* ----------------------------------------------------------*/
	public boolean isKeyPresent()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "isKeyPresent");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _keyPresent;
	}
	
	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface DisplayProcessorInterface. 
	* Elle permet de récupérer une référence sur l'interface de la fenêtre 
	* principale.
	* 
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "getTheMainWindowInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return getMainWindowInterface();
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
			"DisplayProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("display.mdb", "UTF8");
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
			"DisplayProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DisplayProcessorDescription");
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
			"DisplayProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DisplayProcessor();
	}

	/*----------------------------------------------------------
	* Nom: reloadData
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Recharger". Elle supprime toutes les informations du modèle des données 
	* et le recharge à partir du résultat d'un Select sur la table.
	* 
	* Arguments:
	*  - isInitialLoading: Un booléen indiquant si cet appel correspond au 
	*    premier chargement des données ou non.
	* ----------------------------------------------------------*/
	public void reloadData(
		boolean isFirstLoading
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "reloadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		MainWindowInterface window_interface = getMainWindowInterface();
		IsisTableDefinition table_definition = null;
		String action_id = null;
		int counter = 0;
		String[] message = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isFirstLoading=" + isFirstLoading);
		synchronized(this)
		{
			if(_reloadInProgress == true)
			{
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");
				
				trace_debug.writeTrace("Chargement déjà en cours !");
				return;
			}
			_reloadInProgress = true;
		}
		if(isFirstLoading == false)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			window_interface.setCurrentCursor(Cursor.WAIT_CURSOR, this);
			window_interface.setProgressMaximum(2);
			window_interface.setStatus("&Status_BuildingTable", null, 0);
			// On va interdire l'envoi de notifications
			_dataModel.setFireEventsOnChange(false);
			// On commence par effacer toutes les données du modèle
			while(_dataModel.getRowCount() > 0)
			{
				_dataModel.removeRow(0);
			}
			// On va déclencher la mise à jour de l'affichage du contenu
			// du tableau
			_dataModel.fireTableDataChanged();
			window_interface.setStatus("&Status_BuildingTable", null, 1);
		}
		// Ensuite, on va ré-exécuter le Select sur la table
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		TableDefinitionManager manager =
			TableDefinitionManager.getInstance();
		// On va récupérer un numéro d'action si nécessaire
		if(_logData == true)
		{
			action_id = LogServiceProxy.getActionIdentifier(
					selected_node.getAgentName(), 
					MessageManager.getMessage("&LOG_TableDataDisplay"),
					null, selected_node.getServiceName(),
					selected_node.getServiceType(), 
					selected_node.getIClesName());
			message = new String[11 + _nodeContext.size()];
			message[counter++] = 
				MessageManager.getMessage("&LOG_TableDataDisplay");
			message[counter++] = MessageManager.getMessage("&LOG_AgentName") +
				selected_node.getAgentName();
			message[counter++] = MessageManager.getMessage("&LOG_UserName") +
				PasswordManager.getInstance().getUserName();
			message[counter++] = 
				MessageManager.getMessage("&LOG_ServiceSessionId") +
				selected_node.getServiceSessionId();
			message[counter++] = MessageManager.getMessage("&LOG_TableName") +
				getTableName();
			message[counter++] = 
				MessageManager.getMessage("&LOG_SelectedColumns");
			message[counter++] = MessageManager.getMessage("&LOG_Condition");
			message[counter++] = MessageManager.getMessage("&LOG_SortOrder");
			message[counter++] = MessageManager.getMessage("&LOG_NodeContext");
			message[counter++] = 
				MessageManager.getMessage("&LOG_OpeningQuote");
			IsisParameter[] context_parameters =
				CommonFeatures.buildParametersArray(_nodeContext);
			for(int index = 0 ; index < context_parameters.length ; index ++)
			{
				message[counter++] = "\t" + context_parameters[index].name +
					"=" + context_parameters[index].value;
			}
			message[counter++] = 
				MessageManager.getMessage("&LOG_ClosingQuote");
			LogServiceProxy.addMessageForAction(action_id, message);
		}
		try
		{
			// On récupère le proxy de session
			ServiceSessionProxy session_proxy =
				new ServiceSessionProxy(selected_node.getServiceSession());
			// On récupère le résultat de l'exécution de la requête
			String[] result =
				session_proxy.getWideSelectResult(
				selected_node.getAgentName(), getTableName(),
				_selectedColumns, _condition, _sortOrder, _nodeContext);
			if(_selectedColumns == null || _selectedColumns.length == 0)
			{
				// On récupère la définition de la table
				table_definition = manager.getTableDefinition(
					selected_node.getAgentName(), selected_node.getIClesName(), 
					selected_node.getServiceType(), getTableName(), 
					_nodeContext, selected_node.getServiceSession());
			}
			else
			{
				// On construit la définition de la table à partir du résultat
				// de la requête
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, getTableName());
				if(isFirstLoading == true) {
					makePanel(table_definition);
				}
			}
			// On s'assure que l'on a bien une définition
			if(table_definition == null)
			{
				trace_errors.writeTrace(
					"Il n'y a pas de définition pour la table !");
				if(_logData == true)
				{
					// On génère un message de log
					message = new String[2];
					message[counter++] = 
						MessageManager.getMessage("&LOG_ErrorWhileLoadingData");
					message[counter++] = 
						MessageManager.getMessage("&ERR_NoDefinitionForTable");
					LogServiceProxy.addMessageForAction(action_id, message);
				}
				// Il n'y a pas de définition pour la table, on affiche un
				// message d'erreur à l'utilisateur
				InnerException exception =
					new InnerException("&ERR_NoDefinitionForTable", null, null);
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotReloadData", exception);
				setCursor(Cursor.getDefaultCursor());
				window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
				window_interface.setStatus(null, null, 0);
				// On sort
				_reloadInProgress = false;
				trace_methods.endOfMethod();
				return;
			}
			// Et on remplit le modèle à partir des données
			for(int index = 1 ; index < result.length ; index ++)
			{
				IsisParameter[] object_parameters =
					TreeNodeFactory.buildParametersFromSelectResult(
					result, index, table_definition);
				String key = TreeNodeFactory.buildKeyFromSelectResult(
					object_parameters, table_definition);
				GenericTreeObjectNode node =
					new GenericTreeObjectNode(object_parameters, key,
					selected_node.getAgentName(), selected_node.getIClesName(), 
					selected_node.getServiceType(),
					table_definition.definitionFilePath, 
					table_definition.tableName);
				// On positionne le père comme étant le noeud sélectionné
				node.setParent(getSelectedNode());
				_dataModel.add(node);
			}
			if(_logData == true)
			{
				message = new String[3 + result.length - 1];
				counter = 0;
				message[counter++] = MessageManager.getMessage("&LOG_Data");
				message[counter++] = 
					MessageManager.getMessage("&LOG_OpeningQuote");
				for(int index = 1 ; index < result.length ; index ++)
				{
					message[counter++] = "\t" + result[index];
				}
				message[counter++] = 
					MessageManager.getMessage("&LOG_ClosingQuote");
				LogServiceProxy.addMessageForAction(action_id, message);
			}
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors du rechargement des données: " + exception);
			if(_logData == true)
			{
				// On génère un message de log
				message = new String[2];
				counter = 0;
				message[counter++] = 
					MessageManager.getMessage("&LOG_ErrorWhileLoadingData");
				message[counter++] = exception.toString();
				LogServiceProxy.addMessageForAction(action_id, message);
			}
			// Il y a eu une erreur lors du rechargement des données, on
			// affiche un message d'erreur.
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotReloadData", exception);
		}
		if(table_definition != null)
		{
			// On libère l'utilisation de la définition
			manager.releaseTableDefinitionLeasing(table_definition);
		}
		if(isFirstLoading == false)
		{
			// On va déclencher la mise à jour de l'affichage
			_dataModel.fireTableDataChanged();
			// On va ré-autoriser l'envoi de notifications
			_dataModel.setFireEventsOnChange(true);
			window_interface.setStatus(null, null, 0);
			window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
			setCursor(Cursor.getDefaultCursor());
		}
		_reloadInProgress = false;
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getDataModel
	*
	* Description:
	* Cette méthode permet de récupérer la référence de l'objet
	* DisplayDataModel gérant les données à afficher dans la fenêtre. Elle est
	* surtout destinée à être utilisée par des sous-classes.
	*
	* Retourne: La référence sur l'objet DisplayDataModel.
	* ----------------------------------------------------------*/
	protected DisplayDataModel getDataModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "getDataModel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _dataModel;
	}

	/*----------------------------------------------------------
	* Nom: makeButtonsPanel
	*
	* Description:
	* Cette méthode est chargée de construire et de retourner un objet JPanel
	* contenant les boutons qui seront affichés en bas de la fenêtre. Pour
	* l'affichage sous forme de tableau, il n'y a que les boutons "Recharger" 
	* et "Fermer".
	*
	* Retourne: Un objet JPanel contenant les boutons de la fenêtre.
	* ----------------------------------------------------------*/
	protected JPanel makeButtonsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "makeButtonsPanel");

		trace_methods.beginningOfMethod();
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		// On crée un autre panneau avec un flow layout
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On crée le bouton "Recharger"
		JButton reload_button =
			new JButton(MessageManager.getMessage("&Display_Reload"));
		// On ajoute le callback sur le bouton
		reload_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						reloadData(false);
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton
		panel.add(reload_button);
		// On crée le bouton "Auto"
		JButton auto_button =
			new JButton(MessageManager.getMessage("&Display_Auto"));
		// On ajoute le callback sur le bouton
		auto_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						setAutoReload();
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton
		panel.add(auto_button);
		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Display_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On ajoute le bouton
		panel.add(close_button);
		// On calcule la dimension de tous les boutons
		int button_width = Math.max(reload_button.getPreferredSize().width,
				Math.max(close_button.getPreferredSize().width,
				auto_button.getPreferredSize().width));
		Dimension size = new Dimension(button_width,
			reload_button.getPreferredSize().height);
		reload_button.setPreferredSize(size);
		close_button.setPreferredSize(size);
		auto_button.setPreferredSize(size);
		// On ajoute la panneau dans le panneau général
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		trace_methods.endOfMethod();
		return button_panel;
	}

	/*----------------------------------------------------------
	* Nom: getPopupTrigger
	*
	* Description:
	* Cette méthode crée l'objet chargé de la gestion des clicks droits sur les
	* éléments du tableau. Elle crée une instance de TablePopupTrigger et la
	* retourne.
	*
	* Retourne: Une instance de TablePopupTrigger.
	* ----------------------------------------------------------*/
	protected TablePopupTrigger getPopupTrigger()
	{
		TablePopupTrigger popup_trigger = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "getPopupTrigger");

		trace_methods.beginningOfMethod();
		// On crée le popup trigger
		popup_trigger = new TablePopupTrigger(this);
		trace_methods.endOfMethod();
		return popup_trigger;
	}

	/*----------------------------------------------------------
	* Nom: setTableName
	* 
	* Description:
	* Cette méthode permet de positionner le nom de la table dont proviennent 
	* les données à afficher.
	* 
	* Arguments:
	*  - tableName: Le nom de la table source des données.
 	* ----------------------------------------------------------*/
 	protected void setTableName(
 		String tableName
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "setTableName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		_tableName = tableName;
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getTableName
	* 
	* Description:
	* Cette méthode permet de récupérer le nom de la table d'où sont extraites 
	* les données à afficher.
	* 
	* Retourne: Le nom de la table.
	* ----------------------------------------------------------*/
	protected String getTableName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "getTableName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _tableName;
	}

	/*----------------------------------------------------------
	* Nom: getCellRendererForColumn
	* 
	* Description:
	* Cette méthode est appelée lors de la création des colonnes de la table. 
	* Elle permet de fournir un gestionnaire d'affichage spécifique pour une 
	* colonne.
	* Elle est destinée à être surchargée dans les sous-classes.
	* 
	* Arguments:
	*  - index: L'indice de la colonne,
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	protected TableCellRenderer getCellRendererForColumn(
		int index,
		IsisTableColumn column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "getCellRendererForColumn");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("index=" + index);
		trace_arguments.writeTrace("column=" + column);
		trace_methods.endOfMethod();
		return null;
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _dataModel
	*
	* Description:
	* Cet attribut maintient une référence sur le modèle des données du tableau.
	* Il permet de manipuler simplement les données (ajout ou suppression).
	* ----------------------------------------------------------*/
	private DisplayDataModel _dataModel;

	/*----------------------------------------------------------
	* Nom: _tableName
	* 
	* Description:
	* Cet attribut maintient le nom de la table sur laquelle la sélection est 
	* effectuée.
	* ----------------------------------------------------------*/
	private String _tableName;

	/*----------------------------------------------------------
	* Nom: _condition
	* 
	* Description:
	* Cet attribut maintient la condition de sélection des données dans la 
	* table.
	* ----------------------------------------------------------*/
	private String _condition;

	/*----------------------------------------------------------
	* Nom: _selectedColumns
	* 
	* Description:
	* Cet attribut maintient un tableau contenant les noms des colonnes 
	* sélectionnées pour l'affichage.
	* ----------------------------------------------------------*/
	private String[] _selectedColumns;

	/*----------------------------------------------------------
	* Nom: _sortOrder
	* 
	* Description:
	* Cet attribut maintient l'ordre de tri à utiliser pour la sélection des 
	* données.
	* ----------------------------------------------------------*/
	private String _sortOrder;

	/*----------------------------------------------------------
	* Nom: _keyPresent
	*
	* Description:
	* Cet attribut permet de savoir si toutes les colonnes constituant la clé de
	* la table sont présentes (true) ou non (false). Il est utile lors du
	* traitement du click droit.
	* ----------------------------------------------------------*/
	private boolean _keyPresent;

	/*----------------------------------------------------------
	* Nom: _table
	*
	* Description:
	* Cet attribut maintient une référence sur la table d'affichage des
	* données. Il est principalement utilisé pour la gestion des
	* redimensionnements des colonnes.
	* ----------------------------------------------------------*/
	private NonEditableTable _table;

	/*----------------------------------------------------------
	* Nom: _tableDimension
	*
	* Description:
	* Cet attribut maintient une référence sur la dimension idéale
	* de la table. Il est principalement utilisé pour la gestion des
	* redimensionnements des colonnes.
	* ----------------------------------------------------------*/
	private Dimension _tableDimension;

	/*----------------------------------------------------------
	* Nom: _reloadPeriod
	* 
	* Description:
	* Cet attribut maintient la valeur de la période de rechargement 
	* automatique des données. S'il est positif, un rechargement sera 
	* automatiquement provoqué à l'issue de la période.
	* ----------------------------------------------------------*/
	private int _reloadPeriod;

	/*----------------------------------------------------------
	* Nom: _reloadTimer
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet Timer correspondant au 
	* timer responsable des rechargements automatiques des données de la table.
	* ----------------------------------------------------------*/
	private Timer _reloadTimer;

	/*----------------------------------------------------------
	* Nom: _nodeContext
	*
	* Description:
	* Cet attribut maintient une référence sur un objet IndexedList 
	* correspondant au contexte du noeud, incluant les résultats de 
	* pré-processing.
	* ----------------------------------------------------------*/
	private IndexedList _nodeContext;
	
	/*----------------------------------------------------------
	* Nom: _reloadInProgress
	* 
	* Description:
	* Cet attribut permet de ne pas effectuer plusieurs rechargements des 
	* données en même temps.
	* ----------------------------------------------------------*/
	private boolean _reloadInProgress;
	
	/*----------------------------------------------------------
	* Nom: _logData
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si les données affichées 
	* doivent être enregistrées dans les journaux (true), ou non (false).
	* ----------------------------------------------------------*/
	private boolean _logData;
	
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode construit le panneau de la sous-fenêtre en construisant
	* l'ensemble des objets nécessaires à l'affichage d'un tableau où chaque
	* objet résultant de l'exécution de la requête occupera une ligne.
	*
	* Si une erreur est détectée lors de cla création du panneau, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition
	*    contenant la définition de la table.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private void makePanel(
		IsisTableDefinition tableDefinition
		)
		throws
			InnerException
	{
		DefaultTableColumnModel column_model = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "makePanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// Il faut commencer par construire la liste des colonnes
		column_model = makeColumns(tableDefinition);
		// On va également vérifier si la clé est présente
		checkKeyIsPresent(tableDefinition);
		// Ensuite, il faut construire le modèle des données
		_dataModel = new DisplayDataModel();
		// On construit la table
		_table = new NonEditableTable(_dataModel, column_model,
			tableDefinition.separator);
		// On règle quelques paramètres
		_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_OFF);
		// On ajoute le trigger sur la table
		_table.addMouseListener(getPopupTrigger());
		// On intègre la table dans une zone de défilement
		JScrollPane scroll = new JScrollPane(_table);
		// Définition de la taille du tableau
		Dimension table_size = _table.getPreferredSize();
		table_size.height = 200;
		_tableDimension = table_size;
		_table.setPreferredScrollableViewportSize(_tableDimension);
		// On ajoute la zone dans le panneau principal
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		// On crée le panneau des boutons
		JPanel buttons_panel = makeButtonsPanel();
		// On place ce panneau dans la zone sud
		getContentPane().add(buttons_panel, BorderLayout.SOUTH);
		// On ajoute un listener sur redimensionnement
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				checkFrameSize();
			}
		});
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeColumns
	* 
	* Description:
	* Cette méthode est appelée par la méthode makePanel() afin de construire 
	* les colonnes de la table en fonction de la définition de la table et de 
	* la liste des colonnes sélectionnées (fournie par l'attribut 
	* _selectedColumns).
	* 
	* Si une erreur est détectée lors de la création des colonnes, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - tableDefinition: Une référence sur une instance d'IsisTableDefinition 
	*    contenant la description des colonnes de la table.
	* 
	* Retourne: Une instance de DefaultTableColumnModel contenant l'ensemble 
	* des colonnes de la table.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private DefaultTableColumnModel makeColumns(
		IsisTableDefinition tableDefinition
		)
		throws
			InnerException
	{
		DefaultTableColumnModel column_model = null;
	
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "makeColumns");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// On va construire le modèle de colonnes à partir de la définition
		// construite à partir du résultat du Select
		column_model = new DefaultTableColumnModel();
		// Si la liste des colonnes sélectionnées est vide, on prend toutes les
		// colonnes depuis la définition de la table
		if(_selectedColumns.length == 0)
		{
			for(int index = 0 ; index < tableDefinition.columns.length ; 
				index ++)
			{
				IsisTableColumn column = tableDefinition.columns[index];
				trace_debug.writeTrace("Construction de la colonne pour: " +
					column.name);
				// On va construire une nouvelle colonne à partir de la définition
				DisplayColumn display_column = new DisplayColumn(index, column,
					getCellRendererForColumn(index, column));
				// On l'ajoute au modèle
				column_model.addColumn(display_column);
			}
		}
		else
		{
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < _selectedColumns.length ;
				index ++)
			{
				// Il faut récupérer la définition de la colonne
				IsisTableColumn column = null;
				for(int counter = 0 ; counter < tableDefinition.columns.length ;
					counter ++)
				{
					if(tableDefinition.columns[counter].name.equals(
						_selectedColumns[index]) == true)
					{
						column = tableDefinition.columns[counter];
					}
				}
				if(column == null)
				{
					// On n'a pas trouvé la colonne, il faut signaler l'erreur
					trace_errors.writeTrace("Colonne " + _selectedColumns[index] +
						" non définie dans le modèle !");
					// On va lever une exception
					throw new InnerException(
						MessageManager.getMessage("&ERR_ColumnNotExistent"), null,
						null);
				}
				trace_debug.writeTrace("Construction de la colonne pour: " +
					column.name);
				// On va construire une nouvelle colonne à partir de la définition
				DisplayColumn display_column = new DisplayColumn(index, column,
					getCellRendererForColumn(index, column));
				// On l'ajoute au modèle
				column_model.addColumn(display_column);
			}
		}
		trace_methods.endOfMethod();
		return column_model;
	}
	
	/*----------------------------------------------------------
	* Nom: checkFrameSize
	*
	* Description:
	* Cette méthode est appelée à chaque fois que la fenêtre est redimensionnée.
	* Elle permet d'ajuster les règles de redimensionnement des colonnes en
	* fonction de la taille de la fenêtre par rapport à la taille idéale du
	* tableau.
	* ----------------------------------------------------------*/
	private void checkFrameSize()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "checkFrameSize");
			
		trace_methods.beginningOfMethod();
		Dimension frame_size = getSize();
		if(frame_size.width > _tableDimension.width + 10)
		{
			// La taille de la fenêtre est assez grande pour que l'on 
			// redimensionne les colonnes
			_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		}
		else
		{
			// La taille de la fenêtre est trop petite pour que l'on 
			// redimensionne les colonnes
			_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_OFF);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkKeyIsPresent
	* 
	* Description:
	* Cette méthode est chargée de vérifier si la clé de la table est présente. 
	* Cette vérification est effectuée en vérifiant que toutes les colonnes 
	* constituant la clé de la table (fournie par la définition) sont contenues 
	* dans le tableau des colonnes sélectionnées pour l'affichage.
	* 
	* Arguments:
	*  - tableDefinition: La définition de la table.
 	* ----------------------------------------------------------*/
 	private void checkKeyIsPresent(
 		IsisTableDefinition tableDefinition
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "checkKeyIsPresent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		boolean key_present = true;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// Si la liste des colonnes sélectionnée est vide, c'est que toutes
		// les colonnes sont à utiliser
		if(_selectedColumns.length == 0)
		{
			// On considère donc que la clé est présente
			trace_debug.writeTrace("Clé présente !");
			_keyPresent = true;
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va commencer la vérification en regardant si la dimension de la
		// liste des colonnes sélectionnées n'est pas inférieure à celle de la
		// liste des colonnes constituant la clé
		if(_selectedColumns.length < tableDefinition.key.length)
		{
			trace_debug.writeTrace("Clé non présente !");
			_keyPresent = false;
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va regarder la liste des colonnes constituant la clé pour
		// vérifier si chacune est référencée dans la liste des clés
		// sélectionnées
		for(int index = 0 ; index < tableDefinition.key.length ; index ++)
		{
			boolean column_found = false;
			
			String key_column = tableDefinition.key[index];
			trace_debug.writeTrace("Vérification de la présence de la colonne: " +
				key_column);
			for(int counter = 0 ; counter < _selectedColumns.length ; counter ++)
			{
				if(_selectedColumns[counter].equals(key_column) == true)
				{
					// La colonne est présente, on peut passer à la suivante
					trace_debug.writeTrace("La colonne est présente !");
					column_found = true;
					break;
				}
			}
			// Si on n'a pas trouvé la colonne, on peut sortir tout de suite
			if(column_found == false)
			{
				trace_debug.writeTrace("Colonne non trouvée !");
				key_present = false;
				break;
			}
		}
		_keyPresent = key_present;
		trace_debug.writeTrace("_keyPresent=" + _keyPresent);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setAutoReload
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Auto".
	* La boîte de dialogue de définition du rechargement automatique est créée 
	* puis affichée par le biais de la classe RefreshDialog.
	* Si le rechargement automatique est activé, un timer est enclenché à 
	* partir de la période définie, puis, à chaque occurence, la méthode 
	* reloadData() est appelée.
	* ----------------------------------------------------------*/
	private void setAutoReload()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "checkKeyIsPresent");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		RefreshDialog dialog = new RefreshDialog(getMainWindowInterface());
		_reloadPeriod = dialog.getPeriod(_reloadPeriod, this);
		// S'il y a déjà un timer en cours, on l'annule
		if(_reloadTimer != null)
		{
			_reloadTimer.stop();
			_reloadTimer = null;
		}
		// Y a-t-il besoin de définir un timer ?
		if(_reloadPeriod > 0)
		{
			// On va lancer un timer avec la période définie
			_reloadTimer = new Timer(_reloadPeriod * 60 * 1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// Le rafraîchissement des données doit être effectué dans
					// un thread séparé
					Thread thread = new Thread(new Runnable()
					{
						public void run()
						{
							reloadData(false);
						}
					});
					thread.start();
				}
			});
			_reloadTimer.start();
		}
		trace_methods.endOfMethod();
	}
}