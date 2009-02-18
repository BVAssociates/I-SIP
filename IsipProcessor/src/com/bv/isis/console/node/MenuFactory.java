/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/MenuFactory.java,v $
* $Revision: 1.37 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de menu contextuel
* DATE:        12/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MenuFactory.java,v $
* Revision 1.37  2009/01/23 17:30:28  tz
* Ajout des m�thodes hasMethodItems(), getSingleActiveExploreItem()
* et createMenuItem().
*
* Revision 1.36  2009/01/14 14:22:26  tz
* Prise en compte de la modification des packages.
*
* Revision 1.35  2008/08/05 15:53:43  tz
* Suppression de la m�thode d'�dition des libell�s.
*
* Revision 1.34  2008/06/27 09:40:35  tz
* M�thodes avec condition fausse cach�e ou non suivant la valeur
* du param�tre GUI.HideDisabledMethods.
*
* Revision 1.33  2008/03/07 17:27:19  tz
* Correction de plantage lors du r�-affichage d'un menu apr�s
* ouverture de session sur l'environnement sis21.
*
* Revision 1.32  2008/01/31 16:41:05  tz
* Prise en compte du postprocessing.
*
* Revision 1.31  2008/01/16 15:48:44  tz
* Gestion de la surcharge possible de la m�thode d'exploration
* d'un noeud table.
*
* Revision 1.30  2007/03/23 15:25:52  tz
* Dans le cas d'un menu pour un �l�ment de tableau, il se peut qu'il n'y ait
* pas de dictionnaire associ�. Ce n'est pas une erreur.
* Dans ce cas, on n'a pas de foreign-key.
*
* Revision 1.29  2006/11/09 12:07:13  tz
* Ajout des m�thodes d'�dition de fichier et de script.
*
* Revision 1.28  2006/10/13 15:09:09  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.27  2006/03/20 15:50:46  tz
* Ajout de la m�thode d'�dition des libell�s des noeuds,
* Menu de d�veloppement dans le groupe de menus.
*
* Revision 1.26  2006/03/08 14:06:32  tz
* Ajout de la m�thode de v�rification de l'int�grit� des donn�es.
*
* Revision 1.25  2006/03/07 09:29:44  tz
* Sortie lorsque le noeud est ferm�, ou lorsqu'il a �t� explor� et n'a pas d'enfant.
*
* Revision 1.24  2005/10/14 14:38:04  tz
* Ajout de la gestion des s�parateurs dans les menus.
*
* Revision 1.23  2005/10/07 08:18:57  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.22  2005/07/01 12:07:04  tz
* Modification du composant pour les traces
* Modification de la m�thode isExplorable()
*
* Revision 1.21  2004/11/23 15:38:36  tz
* Am�lioration du m�canisme d'exploration automatique (en fonction de l'�tat
* des �l�ments de menu d'exploration).
*
* Revision 1.20  2004/11/09 15:21:54  tz
* Modification pour gestion du rafra�chissement des menus contextuels.
*
* Revision 1.19  2004/11/05 10:40:59  tz
* Ajout de la m�thode isExplorable().
*
* Revision 1.18  2004/11/03 15:16:58  tz
* Ajout de l'�l�ment "D�tailler cet �l�ment" au menu d'un tableau.
*
* Revision 1.17  2004/11/02 08:49:27  tz
* Ajout des m�thodes updateMenuItemStates(), searchCloseItem(),
* et searchOpenSessionItems(),
* Gestion des leasings sur les d�finitions.
*
* Revision 1.16  2004/10/22 15:35:43  tz
* V�rification des dimensions des ic�nes de m�thode,
* Externalisation de la classe IsisMenuItem -> ContextualMenuItem,
* Utilisation de la nouvelle interface ProcessorInterface.
*
* Revision 1.15  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.14  2004/10/13 13:54:50  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.13  2004/10/06 07:29:37  tz
* Option de menu de d�finition de table dans le menu d�veloppeur.
*
* Revision 1.12  2004/07/29 12:01:16  tz
* Suppression d'imports inutiles
*
* Revision 1.11  2003/12/08 14:35:05  tz
* Am�lioration de l'auto-exploration.
*
* Revision 1.10  2003/06/10 13:57:30  tz
* Correction de la fiche Inuit/122
*
* Revision 1.9  2003/03/07 16:19:49  tz
* Ajout de l'auto-exploration
*
* Revision 1.8  2002/11/19 08:38:58  tz
* Gestion de la progression de la t�che.
* Suppression de la traduction des noms des m�thodes d'exploitation.
*
* Revision 1.7  2002/09/20 10:47:23  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.6  2002/08/13 12:58:54  tz
* Ajout identifiant de menu et ajout du sous-menu d�veloppeur.
*
* Revision 1.5  2002/04/05 15:49:49  tz
* Cloture it�ration IT1.2
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
* Revision 1.1  2001/12/14 16:40:34  tz
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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.IconLoader;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.prefs.PreferencesAPI;
import javax.swing.ImageIcon;
import java.awt.Cursor;
import javax.swing.JFrame;
import java.util.Vector;
import java.util.Enumeration;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisMethod;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.IndexedList;

/*----------------------------------------------------------
* Nom: MenuFactory
*
* Description:
* Cette classe abstraite a la responsabilit� de construire un menu contextuel
* correspondant � l'ensemble des m�thodes (internes et d'exploitation) qui
* peuvent �tre appliqu�es sur un noeud graphique s�lectionn�.
* Le menu contextuel est divis� en deux parties. La premi�re partie contient
* des m�thodes de la console (qui sont d�finies dans celle-ci). La seconde
* partie contient les m�thodes d'exploitation applicables sur le noeud, et sur
* lesquelles l'utilisateur a un droit d'ex�cution.
* ----------------------------------------------------------*/
public abstract class MenuFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: createContextualMenu
	*
	* Description:
	* Cette m�thode statique permet de cr�er un menu contextuel en fonction du
	* noeud graphique pass� en param�tre. Le contenu du menu diff�re suivant
	* que le menu est � afficher dans l'arbre d'exploration ou non.
	* La m�thode va tout d'abord ajouter des m�thodes "internes" communes � tous
	* les types de noeuds:
	*  - Fermer: Fermeture avec destruction de la portion d'arbre.
	*  - D�bogguer: Ouverture d'une sous-fen�tre affichant des informations de
	*    d�boggage sur le noeud.
	*
	* Si le noeud graphique est une instance de GenericTreeObjectClass, la
	* m�thode va appeler la m�thode addClassNodeSpecificItems() pour r�cup�rer
	* les m�thodes "internes" sp�cifiques � ce type de noeud. Dans l'autre cas,
	* la m�thode appelle la m�thode addObjectNodeSpecificItems().
	* Ensuite, la m�thode appelle la m�thode addMethodsItems() pour r�cup�rer
	* les m�thodes d'exploitations qui peuvent �tre appliqu�es au noeud dans son
	* contexte (suivant les responsabilit�s de l'utilisateur).
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud graphique pour lequel on veut
	*    obtenir le menu contextuel,
	*  - isForTree: Un bool�en indiquant si le menu doit �tre affich� dans
	*    l'arbre d'exploration ou non,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    n�cessaire � l'ex�cution des processeurs de t�che.
	*
	* Retourne: Une r�f�rence sur le menu contextuel.
	* ----------------------------------------------------------*/
	public static JMenu createContextualMenu(
		GenericTreeObjectNode selectedNode,
		boolean isForTree,
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "createContextualMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		JMenu contextual_menu = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("isForTree=" + isForTree);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On affiche l'�tat
		windowInterface.setProgressMaximum(4);
		windowInterface.setStatus("&Status_BuildingMenu", null, 0);
		// On cr�e le menu
		contextual_menu = new JMenu();
		if(isForTree == true)
		{
			// On ajoute l'�l�ment de fermeture
			IsisMethod close_method = new IsisMethod("", "", "", null, true,
				"", "Close", "", false, "", "");
			contextual_menu.add(createMenuItem("&MW_Menu_Close", "Close",
				close_method, windowInterface, selectedNode));
		}
		windowInterface.setStatus("&Status_BuildingMenu", null, 1);
		// On regarde quel est le type de noeud
		if(selectedNode instanceof GenericTreeClassNode)
		{
			// On ajoute les �l�ments de menu relatifs � un noeud table
			if(addClassNodeSpecificItems((GenericTreeClassNode)selectedNode,
				windowInterface, contextual_menu) == false)
			{
				// On sort
				trace_methods.endOfMethod();
				return null;
			}
		}
		else
		{
			// On ajoute les �l�ments de menu relatifs � un noeud instance
			if(addObjectNodeSpecificItems(selectedNode, isForTree, windowInterface,
				contextual_menu) == false)
			{
				// On sort
				trace_methods.endOfMethod();
				return null;
			}
		}
		windowInterface.setStatus("&Status_BuildingMenu", null, 2);
		// Ensuite, il faut ajouter les �l�ments relatifs aux m�thodes
		// d'exploitation
		if(addMethodsItems(selectedNode, windowInterface, contextual_menu,
			isForTree) == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return null;
		}
		
		windowInterface.setStatus("&Status_BuildingMenu", null, 3);
		// On positionne le menu sur le noeud
		selectedNode.setMenu(contextual_menu);
		// On va rafra�chir l'�tat des �l�ments du menu
		updateMenuItemsState(selectedNode);
		// On force le rafra�chissement de l'affichage de l'item
		windowInterface.getTreeInterface().nodeStructureChanged(selectedNode);
		windowInterface.setStatus("&Status_BuildingMenu", null, 4);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
		return contextual_menu;
	}

	/*----------------------------------------------------------
	* Nom: doAutomaticExplore
	*
	* Description:
	* Cette m�thode statique permet de contr�ler si l'exploration automatique
	* du noeud de session doit �tre ex�cut�e, et commande l'ex�cution de
	* celle-ci.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - treeNode: Une r�f�rence sur un noeud de l'arbre � explorer,
	*  - checkInConfiguration: Un drapeau indiquant s'il y a lieu de v�rifier
	*    si le noeud appartient � ceux qui doivent �tre toujours auto-explor�s,
	*  - numberOfSiblings : Un entier indiquant le nombre total de fr�res du
	*    noeud.
	*
	* Retourne: true si l'exploration automatique a eu lieu, false sinon.
	* ----------------------------------------------------------*/
	static public boolean doAutomaticExplore(
		MainWindowInterface windowInterface,
		GenericTreeObjectNode treeNode,
		boolean checkInConfiguration,
		int numberOfSiblings
		)
		throws
			InnerException
	{
		boolean automatic_explore_enabled = false;
		String table_names = "";
		boolean is_ok = false;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MenuFactory", "doAutomaticExplore");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		ContextualMenuItem the_explore_item = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("treeNode=" + treeNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("treeNode=" + treeNode);
		trace_arguments.writeTrace("checkInConfiguration=" +
			checkInConfiguration);
		// Si le noeud n'est pas dans l'�tat ferm�, on sort
		if(treeNode.getNodeState() != GenericTreeObjectNode.NodeStateEnum.CLOSED)
		{
			trace_methods.endOfMethod();
			return is_ok;
		}
		// On r�cup�re les donn�es de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("AUTO-EXPLORE");
			automatic_explore_enabled = preferences.getBoolean("Enabled");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// On tente de r�cup�rer la liste des tables
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("AUTO-EXPLORE");
			table_names = preferences.getString("Tables");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// Si l'exploration automatique n'est activ�e, on sort
		if(checkInConfiguration == true && automatic_explore_enabled == false)
		{
			trace_methods.endOfMethod();
			return is_ok;
		}
		// On regarde si la table du noeud correspond � une table � explorer
		// en automatique ou s'il est le seul fils
		if(checkInConfiguration == true &&
			isExplorableTable(treeNode.getTableName(), table_names,
			numberOfSiblings) == false)
		{
			trace_methods.endOfMethod();
			return is_ok;
		}
		try
		{
			// On regarde si le noeud a d�j� un menu contextuel
			JMenu menu = treeNode.getMenu();
			if(menu == null)
			{
			    // On va construire le menu contextuel du noeud
			    menu = createContextualMenu(treeNode, true,
					windowInterface);
			    windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, null);
			}
			the_explore_item = getSingleActiveExploreItem(menu);
			// Si on a un et un seul �l�ment d'exploration activ�, on va
			// le d�clencher pour proc�der � l'exploration automatique
			if(the_explore_item != null)
			{
				windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, null);
				the_explore_item.fireActionPerformed();
				is_ok = true;
			}
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Erreur lors de l'exploration automatique: "
				+ exception.getMessage());
			// On affiche le message d'erreur
			windowInterface.showPopupForException(
				"&ERR_ErrorWhileExploringAutomatically", exception);
			is_ok = false;
		}
		windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
		trace_methods.endOfMethod();
		return is_ok;
	}

	/*----------------------------------------------------------
	* Nom: isMethodMatching
	*
	* Description:
	* Cette m�thode statique permet de v�rifier si une m�thode (objet
	* IsisMethod) pass�e en argument est bien du bon type, et que l'utilisateur
	* dispose de la responsabilit� requise pour ex�cuter la m�thode.
	* Le type de la m�thode (attribut objectType) doit correspondre au param�tre
	* expectedMethodType. La responsabilit� n�cessaire pour la m�thode (attribut
	* responsabilities) doit exister dans la liste des responsabilit�s de
	* l'utilisateur.
	*
	* Arguments:
	*  - method: Une r�f�rence sur l'objet IsisMethod contenant la d�finition
	*    de la m�thode,
	*  - userResponsabilities: Un tableau de cha�nes de caract�res contenant la
	*    liste des responsabilit�s de l'utilisateur,
	*  - expectedNodeType: Une cha�ne indiquant le type de noeud attendu,
	*  - separatorType: Une cha�ne indiquant le type pour un s�parateur,
	*  - isForTree: Un bool�en indiquant si le menu doit �tre affich� dans
	*    l'arbre d'exploration ou non.
	*
	* Retourne: true si la m�thode est applicable sur le noeud graphique et est
	* autoris� � l'utilisateur, false sinon.
	* ----------------------------------------------------------*/
	public static boolean isMethodMatching(
		IsisMethod method,
		String[] userResponsabilities,
		String expectedNodeType,
		String separatorType,
		boolean isForTree
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "isMethodMatching");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("method=" + method);
		trace_arguments.writeTrace("userResponsabilities=" + userResponsabilities);
		trace_arguments.writeTrace("expectedNodeType=" + expectedNodeType);
		trace_arguments.writeTrace("separatorType=" + separatorType);
		trace_arguments.writeTrace("isForTree=" + isForTree);
		// On v�rifie si la m�thode correspond � un s�parateur
		if(method.nodeType.equals(separatorType) == true)
		{
			// Il s'agit d'un s�parateur, on retourne true
			trace_debug.writeTrace("Il s'agit d'un s�parateur");
			trace_methods.endOfMethod();
			return true;
		}
		// On compare le type d'objet
		if(method.nodeType.equals(expectedNodeType) == false)
		{
			// Le type de la m�thode ne correspond pas
			trace_debug.writeTrace("Le type de la m�thode ne correspond pas");
			trace_methods.endOfMethod();
			return false;
		}
		// On regarde si le processeur est capable d'�tre invoqu� depuis
		// un noeud d'exploration ou depuis un �l�ment de tableau
		if((isForTree == true && 
			ProcessorManager.isProcessorTreeCapable(method.processor) == false) ||
			(isForTree == false && 
			ProcessorManager.isProcessorTableCapable(method.processor) == false)) 
		{
			trace_debug.writeTrace("Le processeur ne peut pas �tre invoqu�");
			trace_methods.endOfMethod();
			return false;
		}
		// Maintenant, on compare les responsabilit�s
		for(int index = 0 ; index < userResponsabilities.length ; index ++)
		{
			for(int loop = 0 ; loop < method.responsabilities.length ; loop ++)
			{
				if(userResponsabilities[index].equals(
					method.responsabilities[loop]) == true)
				{
					trace_debug.writeTrace(
						"L'utilisateur a la responsabilit� n�cessaire.");
					trace_methods.endOfMethod();
					return true;
				}
			}
		}
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: updateMenuItemsState
	* 
	* Description:
	* Cette m�thode statique permet de rafra�chir les �tats des �l�ments du 
	* menu contextuel du noeud pass� en argument en fonction de son �tat.
	* La m�thode va commencer par r�cup�rer l'�l�ment de menu "Fermer", les 
	* �l�ments d'exploration et les �l�ments d'ouverture de session.
	* Les �tats des �l�ments d�pendent de l'�tat du noeud:
	*  - Noeud ferm�: Tous les �l�ments d'exploration et d'ouverture de 
	*    session sont disponibles, et l'�l�ment de fermeture est d�sactiv�e,
	*  - Noeud explor�: Tous les �l�ments d'exploration, sauf celui 
	*    pr�c�demment utilis�, ainsi que l'�l�ment de fermeture sont 
	*    disponibles. Les �l�ments d'ouverture de session sont indisponibles,
	*  - Autres �tats: Tous les �l�ments sont indisponibles.
	* 
	* Arguments:
	*  - treeNode: Le noeud d'exploration dont le menu contextual doit �tre 
	*    rafra�chit.
   	* ----------------------------------------------------------*/
 	public static void updateMenuItemsState(
 		GenericTreeObjectNode treeNode
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MenuFactory", "updateMenuItemsState");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("treeNode=" + treeNode);
		// On v�rifie que l'argument est correct
		if(treeNode == null)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On commence par r�cup�rer le menu contextuel et l'�tat du noeud
		JMenu contextual_menu = treeNode.getMenu();
		int node_state = treeNode.getNodeState();
		// Si le menu est null, on sort
		if(contextual_menu == null)
		{
			trace_debug.writeTrace("Le menu contextuel est null");
			trace_methods.endOfMethod();
			return;
		}
		// On construit une fausse m�thode pour avoir une condition vraie
		ContextualMenuItem dummy_item = new ContextualMenuItem("",
			new IsisMethod("", "", "", null, true, "", "", "", false, "", 
			""), false);
		// On va rechercher les �l�ments d'exploration
		ContextualMenuItem[] explore_items = 
			searchExploreItems(contextual_menu);
		// On va rechercher les �l�ments d'ouverture de session
		ContextualMenuItem[] session_items = 
			searchOpenSessionItems(contextual_menu);
		// On va rechercher l'�l�ment de fermeture
		ContextualMenuItem close_item = 
			searchCloseItem(contextual_menu);
		// Suivant l'�tat du noeud, on va modifier les �tats des �l�ments
		// de menu
		if(node_state == GenericTreeObjectNode.NodeStateEnum.CLOSED ||
			(node_state == GenericTreeObjectNode.NodeStateEnum.OPENED &&
			treeNode.getChildCount() == 0))
		{
			// Tous les �l�ments d'exploration sont actifs
			for(int index = 0 ; index < explore_items.length ; index ++)
			{
				explore_items[index].setEnabled(
					explore_items[index].isSameCondition(dummy_item));
			}
			// Tous les �l�ments d'ouverture de session sont actifs
			for(int index = 0 ; index < session_items.length ; index ++)
			{
				session_items[index].setEnabled(
					session_items[index].isSameCondition(dummy_item));
			}
			// L'�l�ment de fermeture est d�sactiv�
			if(close_item != null)
			{
				close_item.setEnabled(false);
			}
		}
		// Si le noeud est dans l'�tat explor�, on va activer tous les
		// �l�ments de menu sauf celui utilis� pour la pr�c�dente exploration
		else if(node_state == GenericTreeObjectNode.NodeStateEnum.OPENED)
		{
			String previous_menu_id = (String)treeNode.getUserObject();
			// Tous les �l�ments d'exploration sont actifs sauf celui
			// qui a �t� utilis� pr�c�demment
			for(int index = 0 ; index < explore_items.length ; index ++)
			{
				if(previous_menu_id != null && previous_menu_id.equals(
					explore_items[index].getName()) == true)
				{
					explore_items[index].setEnabled(false);
				}
				else
				{
					explore_items[index].setEnabled(
						explore_items[index].isSameCondition(dummy_item));
				}
			}
			// Tous les �l�ments d'ouverture de session sont d�sactiv�s
			for(int index = 0 ; index < session_items.length ; index ++)
			{
				session_items[index].setEnabled(false);
			}
			// L'�l�ment de fermeture est activ�
			if(close_item != null)
			{
				close_item.setEnabled(true);
			}
		}
		// Dans tous les autres cas, tous les �l�ments de menu sont d�sactiv�s
		else
		{
			for(int index = 0 ; index < explore_items.length ; index ++)
			{
				explore_items[index].setEnabled(false);
			}
			// Tous les �l�ments d'ouverture de session sont d�sactiv�s
			for(int index = 0 ; index < session_items.length ; index ++)
			{
				session_items[index].setEnabled(false);
			}
			// L'�l�ment de fermeture est d�sactiv�
			if(close_item != null)
			{
				close_item.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
 	}
 	
	/*----------------------------------------------------------
	* Nom: isExplorable
	* 
	* Description:
	* Cette m�thode statique permet de savoir si un menu pass� en argument 
	* permet une exploration (ou une ouverture de session), donc que le noeud 
	* d'exploration auquel appartient le noeud peut avoir des enfants ou non.
	* Si le menu ne contient aucune m�thode d'exploration ou d'ouverture de 
	* session, la m�thode retourne false. Elle retourne true dans le cas 
	* contraire.
	* 
	* Arguments:
	*  - menu: Une r�f�rence sur un objet JMenu dans lequel chercher des 
	*    m�thodes d'exploration ou d'ouverture de session.
	* 
	* Retourne: true si le menu contient au moins une m�thode d'exploration ou 
	* d'ouverture de session, false sinon.
	* ----------------------------------------------------------*/
	public static boolean isExplorable(
 		JMenu menu
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MenuFactory", "isExplorable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// Si le menu est null, on ne sait pas, on retourne true
		if(menu == null)
		{
			trace_methods.endOfMethod();
			return true;
		}
		// On regarde si le menu contient des �l�ments d'exploration ou
		// d'ouverture de session
		ContextualMenuItem[] explore_items = searchExploreItems(menu);
		ContextualMenuItem[] session_items = searchOpenSessionItems(menu);
		// Un noeud est inexplorable s'il n'a aucun �l�ment d'exploration ou
		// d'ouverture de session, ou si tous les �l�ments sont d�sactiv�s
		if(explore_items != null && explore_items.length > 0)
		{
			for(int index = 0 ; index < explore_items.length ; index ++)
			{
				if(explore_items[index].isEnabled() == true)
				{
					// Le noeud est explorable, aucun besoin de continuer
					trace_methods.endOfMethod();
					return true;
				}
			}
		}
		if(session_items != null && session_items.length > 0)
		{
			for(int index = 0 ; index < session_items.length ; index ++)
			{
				if(session_items[index].isEnabled() == true)
				{
					// Le noeud est explorable, aucun besoin de continuer
					trace_methods.endOfMethod();
					return true;
				}
			}
		}
		trace_methods.endOfMethod();
		return false;
 	}
 	
	/*----------------------------------------------------------
	* Nom: hasMethodItems
	* 
	* Description:
	* Cette m�thode statique permet de savoir si un menu pass� en argument 
	* contient au moins une m�thode d'exploitation.
	* 
	* Arguments:
	*  - menu: Une r�f�rence sur un objet JMenu dans lequel chercher des 
	*    m�thodes d'exploitation.
	* 
	* Retourne: true si le menu contient au moins une m�thode 
	* d'exploitation, false sinon.
	* ----------------------------------------------------------*/
	public static boolean hasMethodItems(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MenuFactory", "hasMethodItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean has_method_items = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// Si le menu est null, on retourne false
		if(menu == null)
		{
			trace_methods.endOfMethod();
			return has_method_items;
		}
		// On va regarder chaque �l�ment du menu
		for(int index = 0 ; index < menu.getItemCount() ; index ++) {
			JMenuItem child_item = menu.getItem(index);
			if(child_item == null) {
				continue;
			}
			// S'agit-il d'un menu ?
			if(child_item instanceof JMenu) {
				has_method_items = hasMethodItems((JMenu)child_item);
			}
			else {
				ContextualMenuItem contextual_menu_item = 
					(ContextualMenuItem)child_item;
				has_method_items = contextual_menu_item.isMethodItem();
			}
			// Si on a trouv� au moins une m�thode d'exploitation, on
			// peut s'arr�ter
			if(has_method_items == true) {
				break;
			}
		}
		trace_methods.endOfMethod();
		return has_method_items;
	}

	/*----------------------------------------------------------
	* Nom: getSingleActiveExploreItem
	* 
	* Description:
	* Cette m�thode statique recherche dans un menu, et ses sous-menus, 
	* s'il y a un et un seul �lement d'exploration qui soit actif.
	* Elle appelle la m�thode searchExploreItems() afin de r�cup�rer 
	* l'ensemble des �l�ments de menu correspondant � de l'exploration.
	* 
	* Arguments:
	*  - menu: Une r�f�rence sur un JMenu dans lequel rechercher les 
	*    �l�ments d'exploration.
	* 
	* Retourne: Une r�f�rence sur un objet ContextualMenuItem 
	* correspondant � l'unique m�thode d'exploration active, ou null s'il 
	* y a plusieurs m�thodes ou aucune.
	----------------------------------------------------------*/
	public static ContextualMenuItem getSingleActiveExploreItem(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "getSingleActiveExploreItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		ContextualMenuItem the_explore_item = null;
		boolean has_one_explore_item = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// On tente de r�cup�rer la m�thode d'exploration du menu
		ContextualMenuItem[] explore_items = searchExploreItems(menu);
		// Si la m�thode a retourn� au moins un �l�ment de menu, on va le 
		// rechercher si un seul d'entre-eux est actif
		if(explore_items != null && explore_items.length > 0) {
			for(int index = 0 ; index < explore_items.length ; index ++) {
				if(explore_items[index].isEnabled() == false) {
					continue;
				} 
				if(has_one_explore_item == false) {
					// C'est le premier �l�ment d'exploration valid�
					// rencontr�
					the_explore_item = explore_items[index];
					has_one_explore_item = true; 
				}
				else {
					// On a rencontr� plusieurs �l�ments d'exploration
					// valid�, on annule la s�lection de l'�l�ment
					// pr�c�dent
					the_explore_item = null;
					has_one_explore_item = false;
					// On arr�te la recherche
					break;
				}
			}
		}
		trace_methods.endOfMethod();
		return the_explore_item;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _idCounter
	*
	* Description:
	* Cet attribut statique permet de g�n�rer un identifiant unique pour chaque
	* �l�ment de menu cr��.
	----------------------------------------------------------*/
	private static long _idCounter;

	/*----------------------------------------------------------
	* Nom: addClassNodeSpecificItems
	*
	* Description:
	* Cette m�thode statique permet d'ajouter � un menu pass� en argument
	* l'ensemble des m�thodes "internes" applicables � un noeud graphique de
	* type GenericTreeClassNode.
	* Ces m�thodes internes sont:
	*  - Ouvrir: Affichage du r�sultat de la requ�te (d�finie dans le noeud)
	*    sous forme de tableau, dans une sous-fen�tre,
	*  - Cr�er une requ�te: Ouverture d'une sous-fen�tre permettant de
	*    construire de mani�re assist�e des requ�tes sur la table,
	*  - Voir le dictionnaire de la table: Affichage de la d�finition de la
	*    table dans une sous-fen�tre.
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    n�cessaire � l'ex�cution des processeurs de t�che,
	*  - menu: Une r�f�rence sur le menu auquel il faut ajouter les m�thodes.
	*
	* Retourne: true si la construction du menu s'est bien d�roul�e, false sinon.
	* ----------------------------------------------------------*/
	private static boolean addClassNodeSpecificItems(
		GenericTreeClassNode selectedNode,
		MainWindowInterface windowInterface,
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "addClassNodeSpecificItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menu=" + menu);
		// On ajoute le menu "Ouvrir"
		IsisMethod open_method = new IsisMethod("", "", "", null, true, "",
			"DisplayTable", "", false, "", "");
		menu.add(createMenuItem("&MW_Menu_Open", "Display", open_method,
			windowInterface, selectedNode));
		// On ajoute le menu "Requ�te"
		IsisMethod query_method = new IsisMethod("", "", "", null, true, "", 
			"Query", "", false, "", "");
		menu.add(createMenuItem("&MW_Menu_Query", "Query", query_method,
			windowInterface, selectedNode));
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: addObjectNodeSpecificItems
	*
	* Description:
	* Cette m�thode statique permet d'ajouter � un menu pass� en argument
	* l'ensemble des m�thodes "internes" applicables � un noeud graphique de
	* type GenericTreeObjectNode.
	* Ces m�thodes internes sont:
	*  - D�tailler l'�l�ment: Ouverture d'une sous-fen�tre pr�sentant tous les
	*    param�tres de l'objet repr�sent� par le noeud graphique. Cette option
	*    n'est disponible que si le menu doit �tre affich� dans l'arbre
	*    d'exploration,
	*  - D�tailler les �l�ments li�s: Ouverture d'une sous-fen�tre pr�sentant
	*    tous les param�tres des objets li�s (r�sultants de requ�tes d�finies
	*    par les cl�s �trang�res de la table).
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�,
	*  - isForTree: Un bool�en indiquant si le menu est destin� � �tre affich�
	*    dans l'arbre d'exploration ou non,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    n�cessaire � l'ex�cution des processeurs de t�che,
	*  - menu: Une r�f�rence sur le menu auquel il faut ajouter les m�thodes.
	*
	* Retourne: true si la construction du menu s'est bien d�roul�e, false sinon.
	* ----------------------------------------------------------*/
	private static boolean addObjectNodeSpecificItems(
		GenericTreeObjectNode selectedNode,
		boolean isForTree,
		MainWindowInterface windowInterface,
		JMenu menu
		)
	{
		String parent_table_name = "";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "addObjectNodeSpecificItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("isForTree=" + isForTree);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menu=" + menu);
		// On cherche le nom de la table parent
		GenericTreeObjectNode parent_node =
			(GenericTreeObjectNode)selectedNode.getParent();
		while(true)
		{
			if(parent_node == null)
			{
				break;
			}
			if(parent_node.getTableName().equals(selectedNode.getTableName())
				== false)
			{
				parent_table_name = parent_node.getTableName();
				break;
			}
			parent_node = (GenericTreeObjectNode)parent_node.getParent();
		}
		// La premi�re chose est d'ajouter un menu pour contenir les �l�ments
		// de menu.
		JMenu detail_menu =
			new JMenu(MessageManager.getMessage("&MW_Menu_Detail"));
		detail_menu.setIcon(IconLoader.getIcon("DetailNode"));
		// Si le menu est pour l'arbre d'exploration, on ajoute la possibilit�
		// de d�tailler le noeud s�lectionn�
		IsisMethod detail_method = new IsisMethod("", "", "", null, true,
			"", "Detail", "", false, "", "");
		if(isForTree == true)
		{
			// On cr�e le menu pour d�tailler cet �l�ment
			JMenuItem detail_item = createMenuItem("&MW_Menu_DetailThisNode",
				"DetailNode", detail_method, windowInterface, selectedNode);
			// On l'ajoute au menu
			detail_menu.add(detail_item);
		}
		else
		{
			// On cr�e le menu pour d�tailler cet �l�ment
			JMenuItem detail_item = createMenuItem("&MW_Menu_DetailThisElement",
				"DetailNode", detail_method, windowInterface, selectedNode);
			// On l'ajoute au menu
			detail_menu.add(detail_item);
		}
		// Ensuite, on cr�e un �l�ment de menu par foreign key
		// Il faut donc r�cup�rer la d�finition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition =
			manager.getTableDefinition(selectedNode.getAgentName(),
			selectedNode.getIClesName(), selectedNode.getServiceType(),
			selectedNode.getDefinitionFilePath());
		if(table_definition == null)
		{
			if(isForTree == true)
			{
				trace_errors.writeTrace(
					"Impossible de r�cup�rer la d�finition de la table !");
				// Il faut afficher un message d'erreur � l'utilisateur
				InnerException exception =
					new InnerException("&ERR_NoDefinitionForTable", null, null);
				windowInterface.showPopupForException("&ERR_ErrorWhileBuildingMenu",
					exception);
				// Il faut sortir
				trace_methods.endOfMethod();
				return false;
			}
		}
		else
		{
			// On regarde chaque cl� �trang�re
			for(int index = 0 ; index < table_definition.foreignKeys.length ;
				index ++)
			{
				String condition = null;
	
				IsisForeignKey foreign_key = 
					table_definition.foreignKeys[index];
				// Si la cl� fait r�f�rence � la table parent, on saute
				if(foreign_key.foreignTableName.equals(
					parent_table_name) == true)
				{
					continue;
				}
				try
				{
					// On va r�cup�rer la requ�te associ�e � la cl�
					condition =
						ConditionFactory.getConditionFromForeignKey(
						selectedNode.getAgentName(), foreign_key,
						table_definition);
					// On va cr�er un �l�ment de menu
					IsisMethod link_method = new IsisMethod("", "", "", null, 
						true, "", "Detail", foreign_key.foreignTableName + 
						'@' + condition, false, "", "");
					JMenuItem detail_item = 
						createMenuItem("&MW_Menu_DetailThisNode", 
						"DetailLinkedNode", link_method, windowInterface, 
						selectedNode);
					// On change le libell� de l'�l�ment
					detail_item.setText(
						LabelFactory.createLabelForForeignKey(selectedNode,
						foreign_key.foreignTableName));
					// On l'ajoute au menu
					detail_menu.add(detail_item);
				}
				catch(InnerException exception)
				{
					trace_errors.writeTrace(
						"Erreur lors de la construction du menu: " +
						exception.getMessage());
					// Il y a eu une erreur, il faut afficher un message �
					// l'utilisateur
					windowInterface.showPopupForException(
						"&ERR_ErrorWhileBuildingMenu", exception);
					// On lib�re l'utilisation de la d�finition
					manager.releaseTableDefinitionLeasing(table_definition);
					// Il faut sortir
					trace_methods.endOfMethod();
					return false;
				}
			}
			// On lib�re l'utilisation de la d�finition
			manager.releaseTableDefinitionLeasing(table_definition);
		}
		// S'il n'y a aucun �l�ment dans le menu, il ne faut pas l'ajouter
		if(detail_menu.getItemCount() > 0)
		{
			// On ajoute le menu au menu contextuel
			menu.add(detail_menu);
		}
		if(isForTree == false)
		{
			// On ajoute le menu "Requ�te"
			IsisMethod query_method = new IsisMethod("", "", "", null, true, "", 
				"Query", "", false, "", "");
			menu.add(createMenuItem("&MW_Menu_Query", "Query", query_method,
				windowInterface, selectedNode));
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: addMethodsItems
	*
	* Description:
	* Cette m�thode statique permet d'ajouter au menu contextuel l'ensemble des
	* m�thodes d'exploitation applicable au noeud graphique.
	* Ces m�thodes sont applicables si et seulement si:
	*  - leur attribut objectType correspond au type de m�thodes du noeud,
	*  - et que l'utilisateur dispose des responsabilit�s n�cessaires (attribut
	*    responsabilities).
	*
	* Les m�thodes d'exploitation sont regroup�es par sous-menu, en fonction de
	* leur attribut group.
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud graphique s�lectionn�,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    n�cessaire � l'ex�cution des processeurs de t�che,
	*  - menu: Une r�f�rence sur le menu auquel il faut ajouter les m�thodes
	*    d'exploitation,
	*  - isForTree: Un bool�en indiquant si le menu doit �tre affich� dans
	*    l'arbre d'exploration ou non.
	*
	* Retourne: true si la construction du menu s'est bien d�roul�e, false sinon.
	* ----------------------------------------------------------*/
	private static boolean addMethodsItems(
		GenericTreeObjectNode selectedNode,
		MainWindowInterface windowInterface,
		JMenu menu,
		boolean isForTree
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "addMethodsItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		IndexedList groups;
		String[] responsabilities = null;
		IsisMethod[] methods = null;
		String class_object_type = "Table";
		String instance_object_type = "Instance";
		String object_type;
		boolean separator_added = false;
		String separator_type = "Separator";
		JMenu developper_menu = null;
		String agent_layer_mode = null;
		String command = null;
		boolean explore_method_present = false;
		boolean hide_false_methods = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menu=" + menu);
		trace_arguments.writeTrace("isForTree=" + isForTree);
		// La premi�re chose � faire est de r�cup�rer la liste des m�thodes
		try
		{
			ServiceSessionProxy session_proxy =
				new ServiceSessionProxy(selectedNode.getServiceSession());
			// On r�cup�re la liste des m�thodes
			methods = session_proxy.getMethods(selectedNode.getTableName(),
				selectedNode.getContext(true));
			// On r�cup�re les responsabilit�s de l'utilisateur
			responsabilities = session_proxy.getUserResponsabilities();
			// On r�cup�re les param�tres de config d�finissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
			instance_object_type = configuration_api.getString("I-SIS",
				"InstanceObjectType");
			separator_type = configuration_api.getString("I-SIS",
				"SeparatorType");
			hide_false_methods = configuration_api.getBoolean("GUI", 
				"HideDisabledMethods");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la construction du menu: " +
				exception.getMessage());
			// On va afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_ErrorWhileBuildingMenu",
				exception);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// Est-ce qu'il y a des responsabilit�s
		if(responsabilities == null || responsabilities.length == 0)
		{
			trace_debug.writeTrace("Il n'y a pas de responsabilit�s.");
			// On sort
			trace_methods.endOfMethod();
			return true;
		}
		// Quel est le type de l'objet ?
		if(selectedNode instanceof GenericTreeClassNode)
		{
			object_type = class_object_type;
		}
		else
		{
			object_type = instance_object_type;
		}
		// On va r�cup�rer le mode de la couche d'ex�cution de l'Agent
		agent_layer_mode = AgentSessionManager.getInstance().getAgentLayerMode(
			selectedNode.getAgentName());
		// On instancie la table de hash qui contiendra les groupes
		groups = new IndexedList();
		// Est-ce que l'utilisateur a la responsabilit� "dev" ?
		// On cr�e une fausse m�thode
		String[] developper_responsabilities = { "dev" };
		IsisMethod dev_method = new IsisMethod(instance_object_type, "", "", 
			developper_responsabilities, true, "", "Debug", "", false, "", "");
		if(isMethodMatching(dev_method, responsabilities, instance_object_type,
			separator_type, isForTree) == true)
		{
			menu.addSeparator();
			// On cr�e un sous-menu D�veloppement
			developper_menu = new JMenu(MessageManager.getMessage(
				"&MW_Menu_Development"));
			groups.put(developper_menu.getLabel(), developper_menu);
			// TODO: Ic�ne MethodGroup ?
			developper_menu.setIcon(IconLoader.getIcon("MethodGroup"));
			// On ajoute la vue d�veloppeur
		    developper_menu.add(createMenuItem("&MW_Menu_Debug", "Debug",
				dev_method, windowInterface, selectedNode));
			// On ajoute la m�thode de d�finition de la table
			IsisMethod define_method = new IsisMethod("", "", "", null, true,
				"", "DefineTable", "", false, "", "");
			developper_menu.add(createMenuItem("&MW_Menu_Define", "DefineTable", 
				define_method, windowInterface, selectedNode));
			developper_menu.addSeparator();
			// On ajoute la m�thode de v�rification de l'int�grit� des donn�es
			// seulement sur les noeuds Table
			if(selectedNode instanceof GenericTreeClassNode)
			{
				command = "Check " + 
					AgentLayerAbstractor.getVariableReference(agent_layer_mode, 
					"TableName") + " ON";
				IsisMethod check_method = new IsisMethod("", "", "", null, true, "",
					"ExecuteProcedure", command, false, "", "");
				developper_menu.add(createMenuItem("&MW_Menu_CheckTable", "Run", 
					check_method, windowInterface, selectedNode));
			}
			// On ajoute la m�thode d'ex�cution de commande en ligne
			if(agent_layer_mode.equalsIgnoreCase("WINDOWS") == true)
			{
				command = 
					AgentLayerAbstractor.getVariableReference(agent_layer_mode,
					"_command_");
			}
			else
			{
				command = "eval \"" +  
					AgentLayerAbstractor.getVariableReference(agent_layer_mode,
					"_command_") + "\"";
			}
			IsisMethod exec_method = new IsisMethod("", "", "", null, true,
				"_command_=getValue(\"" + MessageManager.getMessage(
				"&Dev_CommandPrompt") + "\")", "ExecuteProcedure", 
				command, false, "", "");
			developper_menu.add(createMenuItem("&MW_Menu_ExecuteCommand",
				"Method", exec_method, windowInterface, selectedNode));
			// On ajoute les m�thodes d'�dition de fichier et de script
			IsisMethod file_edit_method = new IsisMethod("", "", "", null, 
				true, "File=selectFile()", "EditFile", "%[File]", false, "",
				"");
			developper_menu.add(createMenuItem("&MW_Menu_EditFile", "EditFile",
				file_edit_method, windowInterface, selectedNode));
			IsisMethod script_edit_method = new IsisMethod("", "", "", null, 
				true, "File=selectFile(NULL,ALL,EXECUTABLES)", "EditScript", 
				"%[File]", false, "", "");
			developper_menu.add(createMenuItem("&MW_Menu_EditScript", 
				"EditScript", script_edit_method, windowInterface, 
				selectedNode));
			//developper_menu.addSeparator();
			// On ajoute la m�thode d'�dition des libell�s du noeud
			/*String label_table = "ICleLabels@Restricted";
			if(selectedNode.getAgentName().equals(
				selectedNode.getServiceName()) == true && 
				selectedNode.getIClesName().equals("I-SIS") == true)
			{
				// Si (agent) = (service) et (I-CLES) = I-SIS, on est dans
				// le SIS I-SIS 
				label_table = "IsisLabels@Restricted";
			}
			IsisMethod label_edit_method = new IsisMethod("", "", "", null, 
				true, "", "Administrate", label_table, false, "", "");
			developper_menu.add(createMenuItem("&MW_Menu_EditNodeLabels",
				"Configure", label_edit_method, windowInterface,
				selectedNode));*/
            // Enfin, on va ajouter une m�thode de rafra�chissement des �l�ments
            // du menu
            IsisMethod refresh_method = new IsisMethod("", "", "", null, true,
            	"", "RefreshMenu", "" + isForTree, false, "", "");
            developper_menu.addSeparator();
            developper_menu.add(createMenuItem("&MW_Menu_Refresh", "Refresh",
            	refresh_method, windowInterface, selectedNode));
			menu.add(developper_menu);
		}
		// On va parcourir toutes les m�thodes d'exploitation pour voir
		// s'il y en a une d'exploration
		for(int index = 0 ; index < methods.length ; index ++) {
			// Est-ce que l'utilisateur a la responsabilit� n�cessaire
			if(isMethodMatching(methods[index], responsabilities,
				object_type, separator_type, isForTree) == true && 
				methods[index].processor.equals("Explore") == true) {
				explore_method_present = true;
				break;
			}
		}
		// Si on n'a pas trouv� de m�thode d'exploration, on regarde si il
		// est possible d'en ajouter une par d�faut pour un noeud table
		if(explore_method_present == false) {
			// On ajoute la m�thode d'exploration des noeuds table
			String[] user_responsabilities = { "user" };
			IsisMethod explore_method = new IsisMethod(class_object_type, "", "", 
				user_responsabilities, true, "", "Explore", "", false, "", "");
			if(isMethodMatching(explore_method, responsabilities, object_type,
				separator_type, isForTree) == true)
			{
				// On peut ajouter la m�thode d'exploration par d�faut
				menu.addSeparator();
				separator_added = true;
			    menu.add(createMenuItem("&MW_Menu_Explore", "Expand",
					explore_method, windowInterface, selectedNode));
			}
		}
		// Est-ce qu'il y a des m�thodes
		if(methods.length == 0)
		{
			trace_debug.writeTrace("Il n'y a pas de m�thode d'exploitation.");
			// On sort
			trace_methods.endOfMethod();
			return true;
		}
		// On traite les m�thodes une par une
		for(int index = 0 ; index < methods.length ; index ++)
		{
			trace_debug.writeTrace("method=" + methods[index]);
			// Est-ce que l'utilisateur a la responsabilit� n�cessaire
			if(isMethodMatching(methods[index], responsabilities,
				object_type, separator_type, isForTree) == false)
			{
				// L'utilisateur n'a pas le droit d'ex�cuter cette m�thode,
				// On passe � la suivante
				trace_debug.writeTrace(
					"L'utilisateur n'a pas les droits pour la m�thode, ou elle" +
					" ne correspond pas au type de m�thode.");
				continue;
			}
			// On r�cup�re le groupe de la m�thode
			JMenu group_menu = null;
			if(methods[index].group.equals("") == false)
			{
				if(groups.containsKey(methods[index].group) == false)
				{
					// Il faut cr�er le nouveau menu de groupe
					group_menu = new JMenu(methods[index].group);
					// TODO: Ic�ne MethodGroup ?
					group_menu.setIcon(IconLoader.getIcon("MethodGroup"));
					// Il faut placer le menu de groupe dans la table
					groups.put(methods[index].group, group_menu);
				}
				else
				{
					// On r�cup�re la r�f�rence du menu de groupe
					group_menu = (JMenu)groups.get(methods[index].group);
				}
			}
			if(methods[index].nodeType.equals(separator_type) == true)
			{
				// Il s'agit d'un s�parateur, on n'a pas besoin de cr�er un
				// �l�ment de menu
				if(group_menu != null)
				{
					// On ajoute le s�parateur dans le groupe si et seulement 
					// s'il contient au moins un �l�ment et que l'�l�ment 
					// pr�c�dent est un �l�ment de menu
					if(group_menu.getItemCount() > 0 &&
						group_menu.getItem(group_menu.getItemCount() - 1) 
						instanceof ContextualMenuItem)
					{
						group_menu.addSeparator();
					}
				}
				else
				{
					if(separator_added == false)
					{
						menu.addSeparator();
						separator_added = true;
					}
					else if(menu.getItem(menu.getItemCount() - 1) 
						instanceof ContextualMenuItem)
					{
						menu.addSeparator();
					}
				}
			}
			else
			{
				// Si la m�thode a une condition fausse et qu'il faut la
				// cacher, on passe � la suivante
				if(methods[index].condition == false && 
					hide_false_methods == true) {
					continue;
				}
				// Il faut cr�er un �l�ment de menu pour la m�thode
				JMenuItem method_item = createMenuItem(methods[index].label,
					methods[index].icon, methods[index], windowInterface, 
					selectedNode, true);
				method_item.setText(methods[index].label);
				method_item.setEnabled(methods[index].condition);
				// On ajoute l'�l�ment au menu, s'il y en a un
				if(group_menu != null)
				{
					group_menu.add(method_item);
				}
				else
				{
					if(separator_added == false)
					{
						menu.addSeparator();
						separator_added = true;
					}
					menu.add(method_item);
				}
			}
		}
		// Maintenant, on va ajouter les menus de groupe au menu contextuel
		// s'il y en a.
		if(groups.size() > 0)
		{
			boolean menu_added = false;
			boolean menu_separator_added = false;
			if(separator_added == false)
			{
			    menu.addSeparator();
				menu_separator_added = true;
			}
			// On r�cup�re les menus un par un
		    Enumeration iterator = groups.values();
			while(iterator.hasMoreElements() == true)
			{
				JMenu group_menu = (JMenu)iterator.nextElement();
				// S'il s'agit du menu D�veloppement, on le saute
				if(group_menu == developper_menu)
				{
					continue;
				}
				if(group_menu.getItemCount() == 0)
				{
					// Le menu est vide, on ne l'ajoute pas
					continue;
				}
				menu.add(group_menu);
				menu_added = true;
			}
			// On v�rifie que l'on a bien ajout� des menus
			if(menu_added == false && menu_separator_added == true)
			{
				// On n'a pas ajout� de menu, il faut supprimer le s�parateur
				menu.remove(menu.getItemCount() - 1);
			}
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: createMenuItem
	*
	* Description:
	* Cette m�thode statique permet de constuire un �l�ment de menu contextuel 
	* � partir des informations pass�es en argument.
	* Si l'argument itemIcon est non nul, une ic�ne est r�cup�r�e et affect�e �
	* l'�l�ment. Dans le cas contraire, une ic�ne vide est affect�e.
	* Ensuite, un callback est affect� � l'�l�ment de sorte � ex�cuter le
	* processeur correspondant lors d'un click sur celui-ci.
	*
	* Arguments:
	*  - itemLabel: Le libell� de l'�l�ment de menu,
	*  - itemIcon: Un nom d'ic�ne � affecter � l'�l�ment,
	*  - method: Une r�f�rence sur un objet IsisMethod correspondant � la 
	*    m�thode utilis�e pour construire l'�l�ment,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*    n�cessaire � l'ex�cution du processeur,
	*  - selectedNode: Une r�f�rence sur le noeud graphique s�lectionn�,
	*  - isMethodItem: Un bool�en indiquant si l'�l�ment de menu 
	*    correspond � une m�thode d'exploitation.
	*
	* Retourne: Une r�f�rence sur l'�l�ment de menu cr��.
	* ----------------------------------------------------------*/
	private static JMenuItem createMenuItem(
		String itemLabel,
		String itemIcon,
		final IsisMethod method,
		final MainWindowInterface windowInterface,
		final GenericTreeObjectNode selectedNode,
		boolean isMethodItem
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "createMenuItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("itemLabel=" + itemLabel);
		trace_arguments.writeTrace("itemIcon=" + itemIcon);
		trace_arguments.writeTrace("method=" + method);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("isMethodItem=" + isMethodItem);
		if(itemLabel.startsWith("&") == true)
		{
			itemLabel = MessageManager.getMessage(itemLabel);
		}
		// On cr�e l'item
		final ContextualMenuItem menu_item = 
			new ContextualMenuItem(itemLabel, method, isMethodItem);
		// On positionne l'identifiant de l'�l�ment de menu
		menu_item.setName(getMenuId());
		menu_item.setActionCommand(method.processor);
		// On r�cup�re l'ic�ne
		ImageIcon icon = null;
		if(itemIcon != null)
		{
			icon = IconLoader.getIcon(itemIcon);
			// On v�rifie les dimensions de l'ic�ne. Elle doivent �tre
			// de 24x24
			if(icon != null && (icon.getIconHeight() != 24 || 
				icon.getIconWidth() != 24))
			{
				trace_errors.writeTrace("Ic�ne invalide pour les m�thodes: " + 
					itemIcon);
				icon = null;
			}
		}
		if(icon == null)
		{
			// S'il n'y a pas d'ic�ne, r�cup�ration de l'ic�ne par d�faut
			icon = IconLoader.getIcon("Empty");
		}
		// On positionne l'ic�ne de l'item
		menu_item.setIcon(icon);
		// On ajoute le callback sur click
		menu_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				boolean wait = false;
				if(event.getActionCommand().equals("synchronize") == true)
				{
					wait = true;
				}
				// On positionne le curseur --> sablier
				windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, null);
				try
				{
					// On ex�cute le processeur de t�che qui convient
					ProcessorManager.executeProcessor(method.processor,
						windowInterface, menu_item, method.arguments, 
						method.preProcessing, method.postProcessing, 
						selectedNode, method.confirm, wait);
				}
				catch(InnerException exception)
				{
					Trace trace_errors = TraceAPI.declareTraceErrors("Console");

					trace_errors.writeTrace(
						"Erreur lors de l'ex�cution du processeur " +
						method.processor + ": " + exception.getMessage());
					// R�cup�ration du message d'erreur
					windowInterface.showPopupForException(
						"&ERR_CannotExecuteMethod", exception);
					// On positionne le curseur par d�faut
					windowInterface.setCurrentCursor(
						Cursor.DEFAULT_CURSOR, null);
					((JFrame)windowInterface).repaint();
				}
			}
		});
		trace_methods.endOfMethod();
		return menu_item;
	}

	/*----------------------------------------------------------
	* Nom: createMenuItem
	* 
	* Description:
	* Cette m�thode statique permet de constuire un �l�ment de menu 
	* contextuel non issu d'une m�thode d'exploitation � partir des 
	* informations pass�es en argument.
	* Elle appelle la m�thode de m�me nom en positionnant l'argument 
	* isMethodItem � false.
	* 
	* Arguments:
	*  - itemLabel: Le libell� de l'�l�ment de menu,
	*  - itemIcon: Un nom d'ic�ne � affecter � l'�l�ment,
	*  - method: Une r�f�rence sur un objet IsisMethod correspondant � la 
	*    m�thode utilis�e pour construire l'�l�ment,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface, 
	*    n�cessaire � l'ex�cution du processeur,
	*  - selectedNode: Une r�f�rence sur le noeud graphique s�lectionn�,
	* 
	* Retourne: Une r�f�rence sur l'�l�ment de menu cr��.
	* ----------------------------------------------------------*/
	private static JMenuItem createMenuItem(
		String itemLabel,
		String itemIcon,
		final IsisMethod method,
		final MainWindowInterface windowInterface,
		final GenericTreeObjectNode selectedNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "createMenuItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		JMenuItem menu_item = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("itemLabel=" + itemLabel);
		trace_arguments.writeTrace("itemIcon=" + itemIcon);
		trace_arguments.writeTrace("method=" + method);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		menu_item = createMenuItem(itemLabel, itemIcon, method,
			windowInterface, selectedNode, false);
		trace_methods.endOfMethod();
		return menu_item;
	}

	/*----------------------------------------------------------
	* Nom: getMenuId
	*
	* Description:
	* Cette m�thode statique permet de g�n�rer et de retourner un identifiant
	* unique pour un �l�ment de menu. Elle incr�mente la valeur de l'attribut
	* _idCounter � chaque appel.
	*
	* Retourne: un identifiant unique de menu.
	----------------------------------------------------------*/
	private static String getMenuId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "getMenuId");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "" + _idCounter++;
	}

	/*----------------------------------------------------------
	* Nom: isExploreTable
	*
	* Description:
	* Cette m�thode statique recherche dans la liste des tables pass�e en
	* argument si le nom de la table pass� en premier argument en fait
	* partie.
	*
	* Arguments:
	*  - nodeTableName: Le nom de la table � rechercher dans la liste,
	*  - tableNames: La liste des noms de tables � explorer, s�par�s par des
	*    virgules,
	*  - numberOfSiblings: Le nombre de fr�res du noeud.
	*
	* Retourne: true si la table fait partie de la liste, false sinon.
	----------------------------------------------------------*/
	private static boolean isExplorableTable(
		String nodeTableName,
		String tableNames,
		int numberOfSiblings
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "isExplorableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("nodeTableName=" + nodeTableName);
		trace_arguments.writeTrace("tableNames=" + tableNames);
		// On v�rifie la validit� des arguments
		if(nodeTableName == null || nodeTableName.equals("") == true ||
			tableNames == null || tableNames.equals("") == true)
		{
			// Au moins un des arguments n'est pas valide, on sort
			trace_methods.endOfMethod();
			return false;
		}
		// Si le nombre de fr�res vaut 1 (c-a-d que le noeud est le seul fils),
		// alors on autorise l'exploration
		if(numberOfSiblings == 1)
		{
			trace_methods.endOfMethod();
			return true;
		}
		// On va convertir d�couper la liste en tokens
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(tableNames, ",");
		// On regarde dans la liste si le nom de la table est pr�sent
		while(tokenizer.hasMoreTokens() == true)
		{
			if(nodeTableName.equals(tokenizer.nextToken()) == true)
			{
				// On a trouv� la table, on sort
				trace_methods.endOfMethod();
				return true;
			}
		}
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: searchExploreItems
	*
	* Description:
	* Cette m�thode statique recherche dans un menu, et ses sous-menus, tous
	* les �l�ments impliquant une exploration (nom de processeur "Explore").
	* La m�thode est r�cursive: elle s'appelle elle-m�me pour la recherche dans
	* les sous-menus.
	*
	* Arguments:
	*  - menu: Une r�f�rence sur un JMenu dans lequel rechercher les �l�ments
	*    d'exploration.
	*
	* Retourne: Un tableau de ContextualMenuItem contenant tous les �l�ments 
	* de menu impliquant une exploration.
	----------------------------------------------------------*/
	private static ContextualMenuItem[] searchExploreItems(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "searchExploreItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Vector explore_items = new Vector();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// On va regarder chaque �l�ment du menu
		for(int index = 0 ; index < menu.getItemCount() ; index ++)
		{
			JMenuItem child_item = menu.getItem(index);
			if(child_item == null)
			{
				continue;
			}
			// S'agit-il d'un menu ?
			if(child_item instanceof JMenu)
			{
				ContextualMenuItem[] items = 
					searchExploreItems((JMenu)child_item);
				for(int loop = 0 ; loop < items.length ; loop ++)
				{
					explore_items.add(items[loop]);
				}
				continue;
			}
			if(child_item.getActionCommand().equals("Explore") == true)
			{
				explore_items.add((ContextualMenuItem)child_item);
			}
		}
		trace_methods.endOfMethod();
		// On retourne le contenu du vecteur
		return (ContextualMenuItem[])explore_items.toArray(
			new ContextualMenuItem[0]);
	}

	/*----------------------------------------------------------
	* Nom: searchCloseItem
	* 
	* Description:
	* Cette m�thode statique recherche dans un menu l'�l�ment impliquant une 
	* fermeture d'un noeud d'exploration (nom de processeur "Close").
	* 
	* Arguments:
	*  - menu: Une r�f�rence sur un JMenu dans lequel rechercher l'�l�ment de 
	*    fermeture.
	* 
	* Retourne: Une r�f�rence sur un objet ContextualMenuItem correspondant � 
	* l'�l�ment de fermeture du noeud.
	----------------------------------------------------------*/
	private static ContextualMenuItem searchCloseItem(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "searchCloseItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// On va regarder chaque �l�ment du menu
		for(int index = 0 ; index < menu.getItemCount() ; index ++)
		{
			JMenuItem child_item = menu.getItem(index);
			if(child_item == null)
			{
				continue;
			}
			// S'agit-il d'un menu ?
			if(child_item instanceof JMenu)
			{
				// On passe au suivant
				continue;
			}
			if(child_item.getActionCommand().equals("Close") == true)
			{
				// On sort
				trace_methods.endOfMethod();
				return (ContextualMenuItem)child_item;
			}
		}
		trace_methods.endOfMethod();
		// On retourne null
		return null;
	}

	/*----------------------------------------------------------
	* Nom: searchOpenSessionItems
	* 
	* Description:
	* Cette m�thode statique recherche dans un menu, et ses sous-menus, tous 
	* les �l�ments impliquant une ouverture de session (nom de processeur 
	* "OpenAgentSession" ou "OpenServiceSession").
	* La m�thode est r�cursive: elle s'appelle elle-m�me pour la recherche 
	* dans les sous-menus.
	* 
	* Arguments:
	*  - menu: Une r�f�rence sur un JMenu dans lequel rechercher les �l�ments 
	*    d'ouverture de session.
	* 
	* Retourne: Un tableau de ContextualMenuItem contenant tous les �l�ments 
	* de menu impliquant une ouverture de session.
	----------------------------------------------------------*/
	private static ContextualMenuItem[] searchOpenSessionItems(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MenuFactory", "searchOpenSessionItems");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Vector session_items = new Vector();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		// On va regarder chaque �l�ment du menu
		for(int index = 0 ; index < menu.getItemCount() ; index ++)
		{
			JMenuItem child_item = menu.getItem(index);
			if(child_item == null)
			{
				continue;
			}
			// S'agit-il d'un menu ?
			if(child_item instanceof JMenu)
			{
				ContextualMenuItem[] items = 
					searchOpenSessionItems((JMenu)child_item);
				for(int loop = 0 ; loop < items.length ; loop ++)
				{
					session_items.add(items[loop]);
				}
				continue;
			}
			String processor = child_item.getActionCommand();
			if(processor.equals("OpenAgentSession") == true ||
				processor.equals("OpenServiceSession") == true)
			{
				session_items.add((ContextualMenuItem)child_item);
			}
		}
		trace_methods.endOfMethod();
		// On retourne le contenu du vecteur
		return (ContextualMenuItem[])session_items.toArray(
			new ContextualMenuItem[0]);
	}
}