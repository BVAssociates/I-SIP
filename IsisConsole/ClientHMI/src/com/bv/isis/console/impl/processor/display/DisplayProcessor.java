/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayProcessor.java,v $
* $Revision: 1.39 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage du r�sultat d'une requ�te en tableau
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
* Modification d'ordre esth�tique.
*
* Revision 1.37  2008/08/05 15:53:20  tz
* Suppression des donn�es du tableau avant masquage de la fen�tre.
*
* Revision 1.36  2008/02/21 12:07:52  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.35  2008/01/31 16:54:51  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.34  2008/01/29 15:52:15  tz
* Correction de la fiche FS#464.
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.33  2007/12/07 10:32:48  tz
* Adaptation pour l'administration am�lior�e.
*
* Revision 1.32  2007/10/23 11:56:11  tz
* Enregistrement dans les logs I-SIS de l'affichage.
*
* Revision 1.31  2007/09/24 10:41:47  tz
* Protection du rechargement automatique.
* P�riode de rechargement exprim�e en minutes.
*
* Revision 1.30  2007/03/23 15:27:58  tz
* Correction du probl�me d'affichage des requ�tes avec s�lection
* des colonnes. Le tableau est construit apr�s ex�cution de la requ�te.
*
* Revision 1.29  2006/11/09 12:10:02  tz
* Adaptation � la nouvelle m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.28  2006/10/13 15:12:37  tz
* Adaptation aux modifications de la classe ServiceSessionProxy.
*
* Revision 1.27  2006/08/11 13:34:56  tz
* Stockage du contexte avec pr�-processing.
*
* Revision 1.26  2006/03/20 15:57:57  tz
* Ajout de la m�thode getCellRendererForColumn(),
* Support d'un gestionnaire de rendu sp�cifique.
*
* Revision 1.25  2006/03/09 13:38:27  tz
* Ajout de la gestion du rechargement automatique.
*
* Revision 1.24  2006/03/08 14:09:51  tz
* Ajout des m�thodes setTableName() et getTableName().
*
* Revision 1.23  2005/12/23 13:19:07  tz
* Correction mineure.
*
* Revision 1.22  2005/10/07 13:40:38  tz
* Modification du mode de redimensionnement automatique.
*
* Revision 1.21  2005/10/07 08:29:40  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
* Suppression de _acceptErrors et de setAcceptErrors().
*
* Revision 1.20  2005/07/06 10:07:21  tz
* Lib�ration de la d�finition de la table m�me en cas d'erreur.
* Evitement du rafra�chissement de l'affichage en cas de rechargement.
*
* Revision 1.19  2005/07/01 12:18:36  tz
* Modification du composant pour les traces
* Support de la fonctionnalit� de rechargement des donn�es
*
* Revision 1.18  2004/11/23 15:41:51  tz
* Utilisation de la m�thode getWideSelectResult().
*
* Revision 1.17  2004/11/09 15:25:37  tz
* Modification de la liste des colonnes vide.
*
* Revision 1.16  2004/11/03 15:18:59  tz
* Suppression du mode de s�lection.
*
* Revision 1.15  2004/11/02 08:54:55  tz
* Gestion des leasings sur les d�finitions.
*
* Revision 1.14  2004/10/22 15:39:31  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.13  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.12  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.11  2004/10/06 07:33:01  tz
* Am�lioration de la dimension de la frame : gestion des largeurs des
* colonnes sur redimmensionnement.
*
* Revision 1.10  2004/07/29 12:10:06  tz
* Suppression d'imports inutiles
* Mise � jour de la documentation
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
* Ajout du AcceptErrors (et construction d'une ent�te bidon).
*
* Revision 1.7  2002/11/19 08:42:46  tz
* Gestion de la progression de la t�che.
*
* Revision 1.6  2002/08/13 13:13:11  tz
* Ajout m�thode d'acc�s au mod�le des donn�es
*
* Revision 1.5  2002/06/19 12:16:49  tz
* Modification pour processeur d'administration
*
* Revision 1.4  2002/04/05 15:50:35  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'affichage du
* r�sultat d'une requ�te sous forme de tableau. Ce processeur revient �
* effectuer un affichage d�taill� de tous les noeuds r�sultant d'une requ�te.
* Ce processeur est n�cessaire car seule une partie des informations est montr�e
* � l'utilisateur dans l'arbre d'exploration.
* Il peut �galement �tre utilis� pour visualiser le r�sultat d'une requ�te
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
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la classe ProcessorFrame. Elle est
	* appel�e par le ProcessManager afin d'initialiser et de d'ex�cuter le
	* processeur.  Les donn�es de la requ�tes sont extraites des arguments ou 
	* du noeud s�lectionn�, le panneau est construit, les donn�es sont 
	* charg�es (via la m�thode reloadData()) puis la sous-fen�tre est affich�e.
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
		// Tout d'abord, il faut v�rifier l'int�grit� des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// Message d'�tat
		windowInterface.setProgressMaximum(4);
		windowInterface.setStatus("&Status_BuildingTable", null, 0);
		// On cast le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters,
			preprocessing, postprocessing, selectedNode);
		// Si le param�tre est null, la requ�te provient de l'objet s�lectionn�
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
			// On positionne le titre de la fen�tre
			setTitle(MessageManager.getMessage("&Display_Title") +
				selected_node.getLabel().label);
		}
		else
		{
			// On extrait le nom de la table, la liste des colonnes et la
			// condition des param�tres.
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
				// On d�coupe la liste des colonnes
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
			// On positionne le titre de la fen�tre
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
		// On r�cup�re le gestionnaire de d�finitions de table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		// On construit le contexte et les �valuations
		_nodeContext = selected_node.getContext(true);
		// Il faut traiter le pr�processing
		ProcessingHandler.handleProcessingStatement(preprocessing, _nodeContext, 
			windowInterface, AgentSessionManager.getInstance().getAgentLayerMode(
			selected_node.getAgentName()), selected_node.getServiceSession(), 
			this, true);
		if(_selectedColumns == null || _selectedColumns.length == 0)
		{
			// On tente de r�cup�rer la d�finition de la table, on consid�re 
			// que celle-ci n'a jamais �t� charg�e
			table_definition = manager.getTableDefinition(
				selected_node.getAgentName(), selected_node.getIClesName(), 
				selected_node.getServiceType(), getTableName(), _nodeContext,
				selected_node.getServiceSession());
			windowInterface.setStatus("&Status_BuildingTable", null, 2);
			// On cr�e la fen�tre
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
				// Il faut afficher un message � l'utilisateur
				windowInterface.showPopupForException(
					"&ERR_CannotBuildTable", exception);
				windowInterface.setStatus(null, null, 0);
				// On lib�re l'utilisation de la d�finition
				manager.releaseTableDefinitionLeasing(table_definition);
				// On sort
				close();
				trace_methods.endOfMethod();
				return;
			}
		}
		// On charge les donn�es
		reloadData(true);
		windowInterface.setStatus("&Status_BuildingTable", null, 4);
		// On lib�re l'utilisation de la d�finition
		if(table_definition != null)
		{
			manager.releaseTableDefinitionLeasing(table_definition);
		}
		// On positionne les dimensions de la fen�tre
		setMinimumSize(new Dimension(200, 150));
		// On affiche la fen�tre
		display();
		checkFrameSize();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode est appel�e lors de la fermeture de la fen�tre. Elle permet
	* de lib�rer les ressources.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "close");

		trace_methods.beginningOfMethod();
		// On arr�te le timer, s'il y en a un
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
	* Cette m�thode red�finit celle de l'interface DisplayProcessorInterface.
	* Elle permet de savoir si toutes les colonnes composant la cl� de l'objet 
	* (par rapport � la d�finition de la table) sont pr�sentes ou non.
	* Elle retourne la valeur du param�tre _keyPresent.
	*
	* Retourne: true si toutes les colonnes composant la cl� sont pr�sentes,
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
	* Cette m�thode red�finit celle de l'interface DisplayProcessorInterface. 
	* Elle permet de r�cup�rer une r�f�rence sur l'interface de la fen�tre 
	* principale.
	* 
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour effectuer un pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Recharger". Elle supprime toutes les informations du mod�le des donn�es 
	* et le recharge � partir du r�sultat d'un Select sur la table.
	* 
	* Arguments:
	*  - isInitialLoading: Un bool�en indiquant si cet appel correspond au 
	*    premier chargement des donn�es ou non.
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
				
				trace_debug.writeTrace("Chargement d�j� en cours !");
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
			// On commence par effacer toutes les donn�es du mod�le
			while(_dataModel.getRowCount() > 0)
			{
				_dataModel.removeRow(0);
			}
			// On va d�clencher la mise � jour de l'affichage du contenu
			// du tableau
			_dataModel.fireTableDataChanged();
			window_interface.setStatus("&Status_BuildingTable", null, 1);
		}
		// Ensuite, on va r�-ex�cuter le Select sur la table
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		TableDefinitionManager manager =
			TableDefinitionManager.getInstance();
		// On va r�cup�rer un num�ro d'action si n�cessaire
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
			// On r�cup�re le proxy de session
			ServiceSessionProxy session_proxy =
				new ServiceSessionProxy(selected_node.getServiceSession());
			// On r�cup�re le r�sultat de l'ex�cution de la requ�te
			String[] result =
				session_proxy.getWideSelectResult(
				selected_node.getAgentName(), getTableName(),
				_selectedColumns, _condition, _sortOrder, _nodeContext);
			if(_selectedColumns == null || _selectedColumns.length == 0)
			{
				// On r�cup�re la d�finition de la table
				table_definition = manager.getTableDefinition(
					selected_node.getAgentName(), selected_node.getIClesName(), 
					selected_node.getServiceType(), getTableName(), 
					_nodeContext, selected_node.getServiceSession());
			}
			else
			{
				// On construit la d�finition de la table � partir du r�sultat
				// de la requ�te
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, getTableName());
				if(isFirstLoading == true) {
					makePanel(table_definition);
				}
			}
			// On s'assure que l'on a bien une d�finition
			if(table_definition == null)
			{
				trace_errors.writeTrace(
					"Il n'y a pas de d�finition pour la table !");
				if(_logData == true)
				{
					// On g�n�re un message de log
					message = new String[2];
					message[counter++] = 
						MessageManager.getMessage("&LOG_ErrorWhileLoadingData");
					message[counter++] = 
						MessageManager.getMessage("&ERR_NoDefinitionForTable");
					LogServiceProxy.addMessageForAction(action_id, message);
				}
				// Il n'y a pas de d�finition pour la table, on affiche un
				// message d'erreur � l'utilisateur
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
			// Et on remplit le mod�le � partir des donn�es
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
				// On positionne le p�re comme �tant le noeud s�lectionn�
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
				"Erreur lors du rechargement des donn�es: " + exception);
			if(_logData == true)
			{
				// On g�n�re un message de log
				message = new String[2];
				counter = 0;
				message[counter++] = 
					MessageManager.getMessage("&LOG_ErrorWhileLoadingData");
				message[counter++] = exception.toString();
				LogServiceProxy.addMessageForAction(action_id, message);
			}
			// Il y a eu une erreur lors du rechargement des donn�es, on
			// affiche un message d'erreur.
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotReloadData", exception);
		}
		if(table_definition != null)
		{
			// On lib�re l'utilisation de la d�finition
			manager.releaseTableDefinitionLeasing(table_definition);
		}
		if(isFirstLoading == false)
		{
			// On va d�clencher la mise � jour de l'affichage
			_dataModel.fireTableDataChanged();
			// On va r�-autoriser l'envoi de notifications
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
	* Cette m�thode permet de r�cup�rer la r�f�rence de l'objet
	* DisplayDataModel g�rant les donn�es � afficher dans la fen�tre. Elle est
	* surtout destin�e � �tre utilis�e par des sous-classes.
	*
	* Retourne: La r�f�rence sur l'objet DisplayDataModel.
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
	* Cette m�thode est charg�e de construire et de retourner un objet JPanel
	* contenant les boutons qui seront affich�s en bas de la fen�tre. Pour
	* l'affichage sous forme de tableau, il n'y a que les boutons "Recharger" 
	* et "Fermer".
	*
	* Retourne: Un objet JPanel contenant les boutons de la fen�tre.
	* ----------------------------------------------------------*/
	protected JPanel makeButtonsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "makeButtonsPanel");

		trace_methods.beginningOfMethod();
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		// On cr�e un autre panneau avec un flow layout
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On cr�e le bouton "Recharger"
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
		// On cr�e le bouton "Auto"
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
		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Display_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
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
		// On ajoute la panneau dans le panneau g�n�ral
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		trace_methods.endOfMethod();
		return button_panel;
	}

	/*----------------------------------------------------------
	* Nom: getPopupTrigger
	*
	* Description:
	* Cette m�thode cr�e l'objet charg� de la gestion des clicks droits sur les
	* �l�ments du tableau. Elle cr�e une instance de TablePopupTrigger et la
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
		// On cr�e le popup trigger
		popup_trigger = new TablePopupTrigger(this);
		trace_methods.endOfMethod();
		return popup_trigger;
	}

	/*----------------------------------------------------------
	* Nom: setTableName
	* 
	* Description:
	* Cette m�thode permet de positionner le nom de la table dont proviennent 
	* les donn�es � afficher.
	* 
	* Arguments:
	*  - tableName: Le nom de la table source des donn�es.
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
	* Cette m�thode permet de r�cup�rer le nom de la table d'o� sont extraites 
	* les donn�es � afficher.
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
	* Cette m�thode est appel�e lors de la cr�ation des colonnes de la table. 
	* Elle permet de fournir un gestionnaire d'affichage sp�cifique pour une 
	* colonne.
	* Elle est destin�e � �tre surcharg�e dans les sous-classes.
	* 
	* Arguments:
	*  - index: L'indice de la colonne,
	*  - column: Une r�f�rence sur un objet IsisTableColumn correspondant � la 
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
	* Cet attribut maintient une r�f�rence sur le mod�le des donn�es du tableau.
	* Il permet de manipuler simplement les donn�es (ajout ou suppression).
	* ----------------------------------------------------------*/
	private DisplayDataModel _dataModel;

	/*----------------------------------------------------------
	* Nom: _tableName
	* 
	* Description:
	* Cet attribut maintient le nom de la table sur laquelle la s�lection est 
	* effectu�e.
	* ----------------------------------------------------------*/
	private String _tableName;

	/*----------------------------------------------------------
	* Nom: _condition
	* 
	* Description:
	* Cet attribut maintient la condition de s�lection des donn�es dans la 
	* table.
	* ----------------------------------------------------------*/
	private String _condition;

	/*----------------------------------------------------------
	* Nom: _selectedColumns
	* 
	* Description:
	* Cet attribut maintient un tableau contenant les noms des colonnes 
	* s�lectionn�es pour l'affichage.
	* ----------------------------------------------------------*/
	private String[] _selectedColumns;

	/*----------------------------------------------------------
	* Nom: _sortOrder
	* 
	* Description:
	* Cet attribut maintient l'ordre de tri � utiliser pour la s�lection des 
	* donn�es.
	* ----------------------------------------------------------*/
	private String _sortOrder;

	/*----------------------------------------------------------
	* Nom: _keyPresent
	*
	* Description:
	* Cet attribut permet de savoir si toutes les colonnes constituant la cl� de
	* la table sont pr�sentes (true) ou non (false). Il est utile lors du
	* traitement du click droit.
	* ----------------------------------------------------------*/
	private boolean _keyPresent;

	/*----------------------------------------------------------
	* Nom: _table
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur la table d'affichage des
	* donn�es. Il est principalement utilis� pour la gestion des
	* redimensionnements des colonnes.
	* ----------------------------------------------------------*/
	private NonEditableTable _table;

	/*----------------------------------------------------------
	* Nom: _tableDimension
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur la dimension id�ale
	* de la table. Il est principalement utilis� pour la gestion des
	* redimensionnements des colonnes.
	* ----------------------------------------------------------*/
	private Dimension _tableDimension;

	/*----------------------------------------------------------
	* Nom: _reloadPeriod
	* 
	* Description:
	* Cet attribut maintient la valeur de la p�riode de rechargement 
	* automatique des donn�es. S'il est positif, un rechargement sera 
	* automatiquement provoqu� � l'issue de la p�riode.
	* ----------------------------------------------------------*/
	private int _reloadPeriod;

	/*----------------------------------------------------------
	* Nom: _reloadTimer
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet Timer correspondant au 
	* timer responsable des rechargements automatiques des donn�es de la table.
	* ----------------------------------------------------------*/
	private Timer _reloadTimer;

	/*----------------------------------------------------------
	* Nom: _nodeContext
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet IndexedList 
	* correspondant au contexte du noeud, incluant les r�sultats de 
	* pr�-processing.
	* ----------------------------------------------------------*/
	private IndexedList _nodeContext;
	
	/*----------------------------------------------------------
	* Nom: _reloadInProgress
	* 
	* Description:
	* Cet attribut permet de ne pas effectuer plusieurs rechargements des 
	* donn�es en m�me temps.
	* ----------------------------------------------------------*/
	private boolean _reloadInProgress;
	
	/*----------------------------------------------------------
	* Nom: _logData
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si les donn�es affich�es 
	* doivent �tre enregistr�es dans les journaux (true), ou non (false).
	* ----------------------------------------------------------*/
	private boolean _logData;
	
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode construit le panneau de la sous-fen�tre en construisant
	* l'ensemble des objets n�cessaires � l'affichage d'un tableau o� chaque
	* objet r�sultant de l'ex�cution de la requ�te occupera une ligne.
	*
	* Si une erreur est d�tect�e lors de cla cr�ation du panneau, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition
	*    contenant la d�finition de la table.
	*
	* L�ve: InnerException.
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
		// On va �galement v�rifier si la cl� est pr�sente
		checkKeyIsPresent(tableDefinition);
		// Ensuite, il faut construire le mod�le des donn�es
		_dataModel = new DisplayDataModel();
		// On construit la table
		_table = new NonEditableTable(_dataModel, column_model,
			tableDefinition.separator);
		// On r�gle quelques param�tres
		_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_OFF);
		// On ajoute le trigger sur la table
		_table.addMouseListener(getPopupTrigger());
		// On int�gre la table dans une zone de d�filement
		JScrollPane scroll = new JScrollPane(_table);
		// D�finition de la taille du tableau
		Dimension table_size = _table.getPreferredSize();
		table_size.height = 200;
		_tableDimension = table_size;
		_table.setPreferredScrollableViewportSize(_tableDimension);
		// On ajoute la zone dans le panneau principal
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		// On cr�e le panneau des boutons
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
	* Cette m�thode est appel�e par la m�thode makePanel() afin de construire 
	* les colonnes de la table en fonction de la d�finition de la table et de 
	* la liste des colonnes s�lectionn�es (fournie par l'attribut 
	* _selectedColumns).
	* 
	* Si une erreur est d�tect�e lors de la cr�ation des colonnes, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur une instance d'IsisTableDefinition 
	*    contenant la description des colonnes de la table.
	* 
	* Retourne: Une instance de DefaultTableColumnModel contenant l'ensemble 
	* des colonnes de la table.
	* 
	* L�ve: InnerException.
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
		// On va construire le mod�le de colonnes � partir de la d�finition
		// construite � partir du r�sultat du Select
		column_model = new DefaultTableColumnModel();
		// Si la liste des colonnes s�lectionn�es est vide, on prend toutes les
		// colonnes depuis la d�finition de la table
		if(_selectedColumns.length == 0)
		{
			for(int index = 0 ; index < tableDefinition.columns.length ; 
				index ++)
			{
				IsisTableColumn column = tableDefinition.columns[index];
				trace_debug.writeTrace("Construction de la colonne pour: " +
					column.name);
				// On va construire une nouvelle colonne � partir de la d�finition
				DisplayColumn display_column = new DisplayColumn(index, column,
					getCellRendererForColumn(index, column));
				// On l'ajoute au mod�le
				column_model.addColumn(display_column);
			}
		}
		else
		{
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < _selectedColumns.length ;
				index ++)
			{
				// Il faut r�cup�rer la d�finition de la colonne
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
					// On n'a pas trouv� la colonne, il faut signaler l'erreur
					trace_errors.writeTrace("Colonne " + _selectedColumns[index] +
						" non d�finie dans le mod�le !");
					// On va lever une exception
					throw new InnerException(
						MessageManager.getMessage("&ERR_ColumnNotExistent"), null,
						null);
				}
				trace_debug.writeTrace("Construction de la colonne pour: " +
					column.name);
				// On va construire une nouvelle colonne � partir de la d�finition
				DisplayColumn display_column = new DisplayColumn(index, column,
					getCellRendererForColumn(index, column));
				// On l'ajoute au mod�le
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
	* Cette m�thode est appel�e � chaque fois que la fen�tre est redimensionn�e.
	* Elle permet d'ajuster les r�gles de redimensionnement des colonnes en
	* fonction de la taille de la fen�tre par rapport � la taille id�ale du
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
			// La taille de la fen�tre est assez grande pour que l'on 
			// redimensionne les colonnes
			_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		}
		else
		{
			// La taille de la fen�tre est trop petite pour que l'on 
			// redimensionne les colonnes
			_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_OFF);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkKeyIsPresent
	* 
	* Description:
	* Cette m�thode est charg�e de v�rifier si la cl� de la table est pr�sente. 
	* Cette v�rification est effectu�e en v�rifiant que toutes les colonnes 
	* constituant la cl� de la table (fournie par la d�finition) sont contenues 
	* dans le tableau des colonnes s�lectionn�es pour l'affichage.
	* 
	* Arguments:
	*  - tableDefinition: La d�finition de la table.
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
		// Si la liste des colonnes s�lectionn�e est vide, c'est que toutes
		// les colonnes sont � utiliser
		if(_selectedColumns.length == 0)
		{
			// On consid�re donc que la cl� est pr�sente
			trace_debug.writeTrace("Cl� pr�sente !");
			_keyPresent = true;
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va commencer la v�rification en regardant si la dimension de la
		// liste des colonnes s�lectionn�es n'est pas inf�rieure � celle de la
		// liste des colonnes constituant la cl�
		if(_selectedColumns.length < tableDefinition.key.length)
		{
			trace_debug.writeTrace("Cl� non pr�sente !");
			_keyPresent = false;
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va regarder la liste des colonnes constituant la cl� pour
		// v�rifier si chacune est r�f�renc�e dans la liste des cl�s
		// s�lectionn�es
		for(int index = 0 ; index < tableDefinition.key.length ; index ++)
		{
			boolean column_found = false;
			
			String key_column = tableDefinition.key[index];
			trace_debug.writeTrace("V�rification de la pr�sence de la colonne: " +
				key_column);
			for(int counter = 0 ; counter < _selectedColumns.length ; counter ++)
			{
				if(_selectedColumns[counter].equals(key_column) == true)
				{
					// La colonne est pr�sente, on peut passer � la suivante
					trace_debug.writeTrace("La colonne est pr�sente !");
					column_found = true;
					break;
				}
			}
			// Si on n'a pas trouv� la colonne, on peut sortir tout de suite
			if(column_found == false)
			{
				trace_debug.writeTrace("Colonne non trouv�e !");
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Auto".
	* La bo�te de dialogue de d�finition du rechargement automatique est cr��e 
	* puis affich�e par le biais de la classe RefreshDialog.
	* Si le rechargement automatique est activ�, un timer est enclench� � 
	* partir de la p�riode d�finie, puis, � chaque occurence, la m�thode 
	* reloadData() est appel�e.
	* ----------------------------------------------------------*/
	private void setAutoReload()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayProcessor", "checkKeyIsPresent");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		RefreshDialog dialog = new RefreshDialog(getMainWindowInterface());
		_reloadPeriod = dialog.getPeriod(_reloadPeriod, this);
		// S'il y a d�j� un timer en cours, on l'annule
		if(_reloadTimer != null)
		{
			_reloadTimer.stop();
			_reloadTimer = null;
		}
		// Y a-t-il besoin de d�finir un timer ?
		if(_reloadPeriod > 0)
		{
			// On va lancer un timer avec la p�riode d�finie
			_reloadTimer = new Timer(_reloadPeriod * 60 * 1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// Le rafra�chissement des donn�es doit �tre effectu� dans
					// un thread s�par�
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