/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/explore/ExploreProcessor.java,v $
* $Revision: 1.28 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'exploration de noeud
* DATE:        14/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.explore
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExploreProcessor.java,v $
* Revision 1.28  2009/02/13 12:43:35  tz
* Impl�mentation de la fiche FS#600 : Passage du contexte modifi� par
* le pr�-processing lors de la construction des noeuds Item.
*
* Revision 1.27  2009/02/11 16:33:15  tz
* Impl�mentation de la fiche FS#600.
*
* Revision 1.26  2009/02/11 15:12:00  tz
* Plus d'obligation de chargement automatique des menus lors de
* la suppression des noeuds interm�diaires.
*
* Revision 1.25  2009/02/05 16:15:54  tz
* Correction de la fiche FS#577 : Propagation des donn�es de
* pr�-processing dans les noeuds enfants en cas de suppression du
* noeud table.
*
* Revision 1.24  2009/01/23 17:25:45  tz
* Correction de la fiche FS#577.
*
* Revision 1.23  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.22  2008/01/31 16:56:48  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.21  2008/01/29 15:53:18  tz
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.20  2007/12/28 17:42:02  tz
* D�placement de la m�thode revertForeignKey() vers la classe
* ConditionFactory.
*
* Revision 1.19  2007/10/23 11:55:38  tz
* Gestion du cas o� _menuId est null.
*
* Revision 1.18  2006/11/09 12:10:33  tz
* Adaptation � la nouvelle m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.17  2006/03/08 14:10:23  tz
* Retrait du break si l'exploration automatique n'a rien donn�.
*
* Revision 1.16  2006/03/07 09:31:55  tz
* Mise � jour par rapport aux �tats du noeud.
*
* Revision 1.15  2005/10/07 08:26:41  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.14  2005/07/01 12:13:52  tz
* Modification du composant pour les traces
*
* Revision 1.13  2004/11/02 08:54:28  tz
* Gestion de l'�tat du noeud,
* Gestion des leasings sur les d�finitions.
*
* Revision 1.12  2004/10/22 15:38:31  tz
* Adaptation pour la nouvelle interface ProcessorInterface,
* Affichage de tous les messages dans la m�me zone.
*
* Revision 1.11  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.10  2004/10/13 13:55:55  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.9  2004/07/29 12:05:59  tz
* Mise � jour de la documentation
* Suppression d'imports inutiles
*
* Revision 1.8  2003/12/08 14:35:52  tz
* Am�lioration de l'auto-exploration.
*
* Revision 1.7  2003/03/07 16:21:08  tz
* Prise en compte du m�canisme de log m�tier
* Ajout de l'auto-exploration
*
* Revision 1.6  2002/11/19 08:42:19  tz
* Gestion de la progression de la t�che.
* Correction de la fiche Inuit/78.
*
* Revision 1.5  2002/08/13 13:03:57  tz
* Traitement des r�-explorations par menu diff�rent
*
* Revision 1.4  2002/04/05 15:50:47  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:50:49  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:58:21  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/12/14 16:40:25  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.explore;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;
import com.bv.core.prefs.PreferencesAPI;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.node.ConditionFactory;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisForeignKeyLink;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;

/*----------------------------------------------------------
* Nom: ExploreProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che ex�cut� lorsque un noeud
* graphique est explor�. Le principe de l'exploration est l�g�rement diff�rent
* suivant le type du noeud qui est explor�.
* S'il s'agit d'un noeud table (GenericTreeClassNode), l'exploration du noeud
* consiste � ex�cuter une requ�te dont la condition fait partie de la
* d�finition du noeud, et de construire des noeuds instances pour chaque objet
* r�sultant de la requ�te.
* S'il s'agit d'un noeud instance (GenericTreeObjectNode), l'exploration du
* noeud consiste � r�cup�rer tous les liens que ce noeud peut avoir sur d'autre
* tables (pass� en argument du processeur). Pour chacun des liens, un noeud
* table est cr��, avec la construction d'une condition de requ�te
* correspondante.
* ----------------------------------------------------------*/
public class ExploreProcessor
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExploreProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sente que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public ExploreProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "ExploreProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e afin d'initialiser et d'ex�cuter le processeur.
	* Dans le cas du processeur d'expansion, la m�thode commence par v�rifier
	* si le noeud n'a pas d�j� �t� explor�. Si le noeud n'a pas �t� explor�, il
	* ne doit avoir aucun un noeud fils.
	* Dans ce cas, suivant le type du noeud � explorer, l'une des m�thodes
	* exploreXXXNode() est appel�e afin d'effectuer les t�ches n�cessaires �
	* l'exploration du noeud, puis, pour chaque noeud enfant, l'exploration
	* automatique est d�clench�e.
	*
	* Si une erreur est d�tect�e lors de l'initialization, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - menuItem: Une r�f�rence sur l'�l�ment de menu par lequel le processeur 
	*    a �t� lanc�,
	*  - parameters: Une cha�ne contenant des param�tres optionnels du
	*    processeur de t�che,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur le noeud � explorer.
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
			"ExploreProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean remove_unnecessary_nodes = true;
		boolean preload_menus = true;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On va r�cup�rer des pr�f�rences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On utilise la section AUTO-EXPLORE
			preferences.useSection("AUTO-EXPLORE");
			remove_unnecessary_nodes = 
				preferences.getBoolean("RemoveUnnecessaryNodes");
			preload_menus = preferences.getBoolean("PreloadMenus");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// On enregistre l'identifiant du menu appelant
		if(menuItem != null) {
			_menuId = menuItem.getName();
		}
		else {
			_menuId = "Undefined";
		}
		// On caste le noeud en GenericTreeObjectNode.
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		// On v�rifie l'�tat du noeud
		int node_state = selected_node.getNodeState();
		if(node_state != GenericTreeObjectNode.NodeStateEnum.CLOSED &&
			node_state != GenericTreeObjectNode.NodeStateEnum.OPENED)
		{
			trace_errors.writeTrace("Le noeud n'est pas dans le bon �tat");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On affiche le message dans la barre d'�tat
		String[] extra_information = { selected_node.getLabel().label };
		windowInterface.setStatus("&Status_ExploringNode", extra_information,
			0);
		// D'abord, il faut v�rifier que le noeud n'a pas d�j� �t� explor�
		// A-t-il au moins un noeud fils ?
		if(selected_node.getChildCount() > 0)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			// Le noeud a d�j� �t� explor�, mais est-ce par la m�me m�thode ?
			// On va r�cup�rer l'identifiant du menu de la derni�re exploration
			String last_explore_id = (String)selected_node.getUserObject();
			// Si cet identifiant est identique, on ne r�-explore pas
			if(_menuId.equals(last_explore_id) == true)
			{
				// Le noeud a d�j� �t� explor�, �a ne sert � rien de continuer
				trace_debug.writeTrace("Le noeud a d�j� �t� explor�");
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
			// Le noeud a �t� explor� via une autre m�thode, on va fermer le
			// noeud et continuer l'exploration
			trace_debug.writeTrace(
				"Le noeud a �t� explor� via une autre m�thode");
		    windowInterface.getTreeInterface().collapseNode(selected_node);
			selected_node.close(false);
		}
		// Maintenant, on appelle la m�thode correspondant au type de noeud
		if(selected_node instanceof GenericTreeClassNode)
		{
			exploreClassNode(windowInterface,
				(GenericTreeClassNode)selected_node, 
				preprocessing);
		}
		else
		{
			GenericTreeObjectNode child_node = null;
			
			exploreObjectNode(windowInterface, selected_node, parameters,
				preprocessing);
			// La v�rification de la suppression des noeuds interm�diaires
			// est faite ici, apr�s avoir explor� un noeud Item
			if(selected_node.getChildCount() == 1) {
				child_node = 
					(GenericTreeObjectNode)selected_node.getChildAt(0);
			}
			// Il faut que le noeud Item n'ait comme enfant qu'un seul noeud
			// Table pour que cela soit valable
			if(remove_unnecessary_nodes == true && child_node != null &&
				child_node instanceof GenericTreeClassNode) {
				// Le noeud Item n'a qu'un seul enfant, et c'est un noeud
				// Table, on peut le caster
				GenericTreeClassNode child_class_node = 
					(GenericTreeClassNode)child_node;
				// On va r�cup�rer le menu contextuel du noeud
				JMenu class_node_menu = 
					MenuFactory.createContextualMenu(child_class_node, 
					true, windowInterface);
				// La suppression des noeuds interm�diaire ne peut �tre
				// effectu�e que sur des noeuds Table ne disposant pas
				// de m�thode d'exploitation
				if(MenuFactory.hasMethodItems(class_node_menu) == false) {
					// On va provoquer l'exploration du noeud table
					exploreClassNode(windowInterface, child_class_node, null);
					// On va r�cup�rer les donn�es de pr�-processing du
					// noeud table
					IsisParameter[] table_preprocessing = 
						child_class_node.getPreprocessingData();
					// On va d�placer les enfants du noeud Table vers
					// le noeud Item parent
					while(child_class_node.getChildCount() > 0) {
						GenericTreeObjectNode sub_child_node =
							(GenericTreeObjectNode)
							child_class_node.getChildAt(0);
						// On positionne au niveau du noeud enfant les
						// donn�es de pr�-processing du noeud parent
						if(table_preprocessing != null) {
							sub_child_node.setPreprocessingData(
								table_preprocessing);
						}
						selected_node.add(sub_child_node);
					}
					// On peut d�truire le noeud Table, et le supprimer
					// du noeud Item
					child_node.destroy(false);
					selected_node.remove(child_class_node);
				}
			}
		}
		// On informe l'arbre que la structure du noeud s�lectionn� a chang�e
		windowInterface.getTreeInterface().nodeStructureChanged(selectedNode);
		// On force l'expansion du noeud s�lectionn�
		windowInterface.getTreeInterface().expandNode(selectedNode);
		windowInterface.setStatus(null, null, 0);
		// Pour chaque noeud fils, on va charger le menu contextuel et 
		// d�clencher l'exploration automatique
		try
		{
			for(int index = 0 ; index < selectedNode.getChildCount() ; index ++)
			{
				GenericTreeObjectNode child_node =
					(GenericTreeObjectNode)selectedNode.getChildAt(index);
				// On va charger automatiquement le menu contextuel du
				// noeud
				if(preload_menus == true) {
					MenuFactory.createContextualMenu(child_node, true, 
						windowInterface);
				}
				// On va d�clencher l'exploration automatique du noeud
				MenuFactory.doAutomaticExplore(windowInterface,
					child_node, true, selectedNode.getChildCount());
			}
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de l'auto-exploration du noeud");
		}
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e pour arr�ter l'ex�cution du processor.
	* Dans le cas pr�sent, elle n'effectue aucun traitement particulier.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
	* Pour ce processeur, la m�thode ne fait rien.
	----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a �t� configur� ou s'il a 
	* besoin d'une configuration.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* La m�thode cr�e une instance de ExploreConfigurationPanel et la retourne.
	* 
	* Retourne: Un tableau ne contenant qu'une instance de 
	* ExploreConfigurationPanel.
	----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		ConfigurationPanelInterface[] panels = 
			new ConfigurationPanelInterface[1];
		panels[0] = new ExploreConfigurationPanel();
		trace_methods.endOfMethod();
		return panels;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un �l�ment 
	* d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� hors d'un 
	* noeud d'exploration ou d'un �l�ment d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
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
			"ExploreProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&ExploreProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment de menu associ�.
	* Ce processeur n'�tant pas global, cette m�thode ne sera pas appel�e.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
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
			"ExploreProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ExploreProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _menuId
	*
	* Description:
	* Cet attribut maintient l'identifiant du menu par lequel le processeur
	* d'exploration a �t� appel�. Il permet de contr�ler si un noeud a �t�
	* explor� via la m�me m�thode ou non.
	----------------------------------------------------------*/
	private String _menuId;

	/*----------------------------------------------------------
	* Nom: exploreObjectNode
	*
	* Description:
	* Cette m�thode permet d'explorer un noeud graphique de type Instance (une
	* instance de GenericTreeObjectNode). L'exploration de ce type de noeud
	* consiste � r�cup�rer, au niveau du noeud s�lectionn�, l'ensemble des
	* liens qu'il peut avoir sur d'autres tables, via l'argument d'ex�cution du
	* processeur.
	* Pour chacun de liens, un noeud table est cr�� et ajout� au noeud
	* s�lectionn�, via la classe TreeNodeFactory.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    utilis�e pour afficher des messages d'erreurs,
	*  - selectedNode: Une r�f�rence sur le noeud � explorer,
	*  - parameters: Les param�tres d'ex�cution du processeur,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions de
	*    pr�processing.
	* ----------------------------------------------------------*/
	private void exploreObjectNode(
		MainWindowInterface windowInterface,
		GenericTreeObjectNode selectedNode,
		String parameters,
		String preprocessing
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "exploreObjectNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_debug.writeTrace("Exploration du noeud instance: " +
			selectedNode.getLabel().label);
		// On fixe l'�tat du noeud
		selectedNode.setNodeState(
			GenericTreeObjectNode.NodeStateEnum.STATE_CHANGING);
		// On affiche le message dans la barre d'�tat
		String[] extra_information = { selectedNode.getLabel().label };
		windowInterface.setStatus("&Status_BuildingTableNodes",
			extra_information, 0);
		// L'exploration d'un noeud instance consiste � r�cup�rer toutes
		// les tables avec lesquelles le noeud a des liens. La liste des
		// tables est indiqu�e dans les param�tres du processeur.
		// Le format est <table>,<table>,...
		if(parameters == null || parameters.equals("") == true)
		{
			trace_debug.writeTrace("Il n'y a aucune table li�e");
			// On fixe l'�tat du noeud
			selectedNode.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.OPENED);
			// Il n'y a aucune table li�e, on sort
			trace_methods.endOfMethod();
			return;
		}
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(parameters, 
			",");
		windowInterface.setProgressMaximum(tokenizer.getTokensCount());
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		try
		{
			int loop = 1;
			IndexedList context = selectedNode.getContext(true);
			// On va traiter le pr�processing
			IsisParameter[] preprocessing_parameters =
				ProcessingHandler.handleProcessingStatement(preprocessing,
				context, windowInterface, 
				AgentSessionManager.getInstance().getAgentLayerMode(
				selectedNode.getAgentName()), 
				selectedNode.getServiceSession(), 
				(Component)windowInterface, true);
			// On va traiter chaque table une par une
			while(tokenizer.hasMoreTokens() == true)
			{
				String table_name = tokenizer.nextToken().trim();
				trace_debug.writeTrace("Traitement de la table " + table_name);
				// Il faut r�cup�rer la d�finition de la table
				IsisTableDefinition definition =
					definition_manager.getTableDefinition(selectedNode.getAgentName(),
					selectedNode.getIClesName(), selectedNode.getServiceType(), 
					table_name, context, selectedNode.getServiceSession());
				// Affichage de l'�tat
				windowInterface.setStatus("&Status_BuildingTableNodes",
					extra_information, loop++);
				// On va chercher une cl� �trang�re faisant r�f�rence � la table du
				// noeud s�lectionn�
				IsisForeignKey foreign_key = searchForeignKey(definition,
					selectedNode.getTableName());
				// S'il n'y a pas de cl� �trang�re, on va cr�er une cl�
				// �trang�re vide
				if(foreign_key == null)
				{
					trace_debug.writeTrace(
						"Il n'y a pas de lien de la table " + table_name +
						" vers la table " + selectedNode.getTableName() + ".");
					foreign_key =
						new IsisForeignKey(table_name, new IsisForeignKeyLink[0]);
				}
				// On lib�re l'utilisation de la d�finition
				definition_manager.releaseTableDefinitionLeasing(definition);
				// Il faut renverser la cl� �trang�re et creer un noeud table
				GenericTreeClassNode class_node =
					TreeNodeFactory.makeTreeClassNode(
					selectedNode.getServiceSession(),
					selectedNode.getAgentName(), selectedNode.getIClesName(),
					selectedNode.getServiceType(), table_name,
					ConditionFactory.revertForeignKey(foreign_key, 
					selectedNode.getTableName()), selectedNode, context);
				// On ajoute les param�tres de pr�processing au noeud
				class_node.setPreprocessingData(preprocessing_parameters);
			}
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'exploration du noeud: " +
				exception.getMessage());
			// Il y a eu une erreur, on va fermer le noeud s�lectionn�
			selectedNode.close(false);
			// On affiche un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotExploreTreeNode",
				exception);
		}
		// On positionne l'identifiant de la m�thode d'exploration
		selectedNode.setUserObject(_menuId);
		// On fixe l'�tat du noeud
		selectedNode.setNodeState(GenericTreeObjectNode.NodeStateEnum.OPENED);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: exploreClassNode
	*
	* Description:
	* Cette m�thode permet d'explorer un noeud graphique de type Table (une
	* instance de GenericTreeClassNode). L'exploration de ce type de noeud
	* consiste � r�cup�rer, au niveau du noeud s�lectionn�, la condition de
	* requ�te ayant �t� construite lors de la construction du noeud.
	* La requ�te correspondant va �tre ex�cut�e sur la plate-forme agent, afin
	* de r�cup�rer un ensemble d'objets de production. A partir de ces objets,
	* des noeuds Instances vont �t� cr��s et ajout�s au noeud s�lectionn�.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    utilis�e pour afficher des messages d'erreurs,
	*  - selectedNode: Une r�f�rence sur le noeud � explorer,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions de 
	*    pr�processing.
	* ----------------------------------------------------------*/
	private void exploreClassNode(
		MainWindowInterface windowInterface,
		GenericTreeClassNode selectedNode,
		String preprocessing
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "exploreClassNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_debug.writeTrace("Exploration du noeud table " +
			selectedNode.getLabel().label);
		// On fixe l'�tat du noeud
		selectedNode.setNodeState(
			GenericTreeObjectNode.NodeStateEnum.STATE_CHANGING);
		try
		{
			IndexedList context = selectedNode.getContext(true);
			// On va traiter le pr�processing
			IsisParameter[] preprocessing_parameters =
				ProcessingHandler.handleProcessingStatement(preprocessing,
				context, windowInterface, 
				AgentSessionManager.getInstance().getAgentLayerMode(
				selectedNode.getAgentName()), 
				selectedNode.getServiceSession(), 
				(Component)windowInterface, true);
			// On ex�cute cr�e les noeuds r�sultant de l'ex�cution d'une
			// requ�te
			TreeNodeFactory.makeTreeObjectNodes(
				selectedNode.getServiceSession(), selectedNode.getAgentName(),
				selectedNode.getIClesName(), selectedNode.getServiceType(),
				selectedNode.getTableName(), selectedNode.getCondition(),
				selectedNode, context, windowInterface, 
				selectedNode.getLabel().label);
			// On va positionner les donn�es de pr�-processing sur les noeuds
			// enfant
			for(int index = 0 ; index < selectedNode.getChildCount() ; 
				index ++) {
				GenericTreeObjectNode child_node = 
					(GenericTreeObjectNode)selectedNode.getChildAt(index);
				child_node.setPreprocessingData(preprocessing_parameters);
			}
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'exploration du noeud: " +
				exception.getMessage());
			// On remonte le message d'erreur � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotExploreTreeNode",
				exception);
			// On fixe l'�tat du noeud
			selectedNode.close(false);
			// On arr�te l�
			trace_methods.endOfMethod();
			return;
		}
		// On positionne l'identifiant de la m�thode d'exploration
		selectedNode.setUserObject(_menuId);
		// On fixe l'�tat du noeud
		selectedNode.setNodeState(GenericTreeObjectNode.NodeStateEnum.OPENED);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: searchForeignKey
	*
	* Description:
	* Cette m�thode permet de rechercher dans la d�finition d'une table une
	* cl� �trang�re pointant sur la table pass�e en second argument.
	*
	* Arguments:
	*  - definition: La d�finition de la table o� chercher les cl�s �trang�res,
	*  - foreignTableName: La table �trang�re vers laquelle la cl� �trang�re
	*    recherch�e doit pointer.
	*
	* Retourne: La cl� �trang�re pointant vers la table, ou null.
	* ----------------------------------------------------------*/
	private IsisForeignKey searchForeignKey(
		IsisTableDefinition definition,
		String foreignTableName
		)
	{
		IsisForeignKey foreign_key = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreProcessor", "searchForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("definition=" + definition);
		trace_arguments.writeTrace("foreignTableName=" + foreignTableName);
		// On va regarder dans toutes les cl�s �trang�res de la table
		if(definition.foreignKeys == null || definition.foreignKeys.length == 0)
		{
			trace_debug.writeTrace("Aucune cl� �trang�re !");
			// Il n'y a pas de cl� �trang�re, on sort
			trace_methods.endOfMethod();
			return null;
		}
		for(int index = 0 ; index < definition.foreignKeys.length ; index ++)
		{
			// On v�rifie si la table distante correspond � la table d�sir�e
			if(definition.foreignKeys[index].foreignTableName.equals(
				foreignTableName) == true)
			{
				trace_debug.writeTrace("Cl� �trang�re trouv�e.");
				foreign_key = definition.foreignKeys[index];
				break;
			}
		}
		trace_methods.endOfMethod();
		return foreign_key;
	}
}