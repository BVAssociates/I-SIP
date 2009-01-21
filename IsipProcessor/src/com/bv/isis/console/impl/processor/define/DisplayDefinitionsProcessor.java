/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/define/DisplayDefinitionsProcessor.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage du cache des définitions
* DATE:        27/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.define
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayDefinitionsProcessor.java,v $
* Revision 1.11  2008/02/21 12:07:27  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.10  2008/01/31 16:54:03  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.9  2006/10/13 15:12:04  tz
* Remplacement du séparateur dans la construction de la clé de la table
* de hash pour la gestion des dictionnaires.
*
* Revision 1.8  2005/12/23 13:18:24  tz
* Correction mineure.
*
* Revision 1.7  2005/10/12 14:27:49  tz
* Libération systématique des jetons des dictionnaires de tables.
*
* Revision 1.6  2005/10/07 13:40:32  tz
* Modification du mode de redimensionnement automatique.
*
* Revision 1.5  2005/10/07 11:26:55  tz
* Modification de la méthode setAgentAndSession().
*
* Revision 1.4  2005/10/07 08:30:22  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.3  2005/07/01 12:19:24  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/11/03 15:19:45  tz
* Suppression du mode de sélection.
*
* Revision 1.1  2004/11/02 08:55:54  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.define;

//
//Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.IconLoader;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import com.bv.core.util.UtilStringTokenizer;
import javax.swing.table.DefaultTableModel;
import java.util.Enumeration;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import java.awt.Cursor;
import javax.swing.JOptionPane;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.abs.gui.MainWindowInterface;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.processor.NonEditableTable;
import com.bv.isis.console.abs.processor.ProcessorInterface;
import com.bv.isis.console.abs.processor.TableDefinitionListener;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.com.ObjectLeasingHolder;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.SessionTreeObjectNode;

/*----------------------------------------------------------
* Nom: DisplayDefinitionsProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage et de 
* la gestion du cache des définitions de tables I-TOOLS. Etant un processeur 
* graphique, elle dérive de la classe ProcessorFrame.
* Ce processeur n'est destiné qu'à être invoqué via le menu "Outils" de la 
* Console, aussi les méthodes isTreeCapable(), isTableCapable(), 
* isGlobalCapable() et getMenuLabel() sont redéfinies.
* Elle implémente l'interface TableDefinitionListener afin d'être informée des 
* changements dans le cache des définitions.
* Elle implémente également l'interface MouseListener afin de permettre 
* l'affichage d'un menu contextuel sur le tableau.
* ----------------------------------------------------------*/
public class DisplayDefinitionsProcessor 
	extends ProcessorFrame
	implements 
		TableDefinitionListener,
		MouseListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayDefinitionsProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DisplayDefinitionsProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "DisplayDefinitionsProcessor");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour effectuer le pré-chargement du processeur.
	* Elle déclenche le chargement du fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "preLoad");
		
		trace_methods.beginningOfMethod();
		MessageManager.loadFile("definitions.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "isTableCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "isGlobalCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour récupérer l'intitulé de l'élément du menu "Outils" 
	* associé à ce processeur.
	* 
	* Retourne: L'intitulé de l'élément de menu.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "getMenuLabel");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "Definitions";
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour déclencher l'exécution du processeur.
	* La méthode construit la fenêtre via un appel à la méthode makePanel(), 
	* puis s'affiche à l'écran.
	* 
	* Si un problème est détecté durant la phase d'exécution, l'exception 
	* InnerException doit être levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le 
	*    processeur a été exécuté,
	*  - parameters: Une chaîne de caractère contenant des paramètres 
	*    spécifiques au processeur,
	*  - preprocessing: Une chaîne de caractères contenant des instructions 
	*    de préprocessing,
	*  - postprocessing: Une chaîne de caractères contenant des instructions 
	*    de postprocessing,
	*  - selectedNode: Une référence sur le noeud sélectionné.
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
			"DisplayDefinitionsProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la fenêtre
		setTitle(MessageManager.getMessage("&Definitions_Title"));
		windowInterface.setProgressMaximum(1);
		windowInterface.setStatus("&Definitions_BuildingTable", null, 0);
		// On construit le panneau
		makePanel();
		windowInterface.setStatus("&Definitions_BuildingTable", null, 1);
		// On l'affiche
		setMinimumSize(new Dimension(200, 150));
		display();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque la fenêtre du processeur est fermée.
	* La méthode libère les données du modèle associé au tableau, puis se 
	* désenregistre au niveau de la classe TableDefinitionManager.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "close");
		
		trace_methods.beginningOfMethod();
		// On se désenregistre du TableDefinitionManager
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.unregisterListener(this);
		// On supprime les données du modèle
		if(_model != null)
		{
			while(_model.getRowCount() > 0)
			{
				_model.removeRow(0);
			}
		}
		super.close();
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
			"DisplayDefinitionsProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DefinitionsProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de DisplayDefinitionsProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DisplayDefinitionsProcessor();
	}

	/*----------------------------------------------------------
	* Nom: tableDefinitionAdded
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableDefinitionListener. 
	* Elle est appelée lorsqu'une nouvelle définition a été chargée en cache.
	* Elle récupère l'objet ObjectLeasingHolder associé à l'Agent, au I-CLES, 
	* au type de service et au chemin du dictionnaire, et ajoute les données 
	* au modèle.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
 	*  - definitionFilePath: Le chemin du dictionnaire.
 	* ----------------------------------------------------------*/
	public void tableDefinitionAdded(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "tableDefinitionAdded");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// On va récupérer l'objet ObjectLeasingHolder associé à la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		String cache_key = agentName + ';' + iClesName + ';' + serviceType + 
			';' + definitionFilePath; 
		ObjectLeasingHolder leasing_holder = manager.getTableDefinitionLeasing(
			cache_key);
		if(leasing_holder == null)
		{
			trace_errors.writeTrace("Le leasing pour la définition " +
				cache_key + " n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère la définition de la table
		IsisTableDefinition definition = 
			(IsisTableDefinition)leasing_holder.getLeasedObject();
		// On va construire un vecteur avec les nouvelles valeurs et
		// l'ajouter au modèle
		Vector data = new Vector();
		data.add(definition.tableName);
		data.add(agentName);
		data.add(iClesName);
		data.add(serviceType);
		data.add(definitionFilePath);
		data.add("" + leasing_holder.getNumberOfLeasings());
		synchronized(_model)
		{
			_model.addRow(data);
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: tableDefinitionRemoved
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableDefinitionListener. 
	* Elle est appelée lorsqu'une définition a été supprimée du cache.
	* Elle recherche la ligne correspondant, et la supprime de la liste.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin du dictionnaire.
 	* ----------------------------------------------------------*/
	public void tableDefinitionRemoved(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "tableDefinitionRemoved");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		synchronized(_model)
		{
			// On va chercher dans toutes les données du modèle
			for(int index = 0 ; index < _model.getRowCount() ; index ++)
			{
				String agent_name = (String)_model.getValueAt(index, 1);
				String icles_name = (String)_model.getValueAt(index, 2);
				String service_type = (String)_model.getValueAt(index, 3);
				String file_path = (String)_model.getValueAt(index, 4);
				if(agentName.equals(agent_name) == true &&
					iClesName.equals(icles_name) == true &&
					serviceType.equals(service_type) == true &&
					definitionFilePath.equals(file_path) == true)
				{
					trace_debug.writeTrace("La ligne de données a été trouvée");
					// On va commander la suppression de la ligne
					_model.removeRow(index);
					// On peut sortir
					trace_methods.endOfMethod();
					return;
				}
			}
			// Si on arrive ici, c'est que l'on n'a pas trouvé
			trace_debug.writeTrace("La ligne de données n'a pas été trouvée");
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: tableDefinitionUseChanged
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableDefinitionListener. 
	* Elle est appelée lorsqu'une utilisation d'une définition a changé.
	* Elle recherche la ligne de données associée, et met à jour le nombre de 
	* ses utilisations.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin du dictionnaire,
	*  - numberOfUses: Le nombre d'utilisations de la définition.
 	* ----------------------------------------------------------*/
	public synchronized void tableDefinitionUseChanged(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath,
		int numberOfUses
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "tableDefinitionUseChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		trace_arguments.writeTrace("numberOfUses=" + numberOfUses);
		synchronized(_model)
		{
			// On va chercher dans toutes les données du modèle
			for(int index = 0 ; index < _model.getRowCount() ; index ++)
			{
				String agent_name = (String)_model.getValueAt(index, 1);
				String icles_name = (String)_model.getValueAt(index, 2);
				String service_type = (String)_model.getValueAt(index, 3);
				String file_path = (String)_model.getValueAt(index, 4);
				if(agentName.equals(agent_name) == true &&
					iClesName.equals(icles_name) == true &&
					serviceType.equals(service_type) == true &&
					definitionFilePath.equals(file_path) == true)
				{
					trace_debug.writeTrace("La ligne de données a été trouvée");
					// On va modifier la valeur du nombre d'utilisations
					Vector data = (Vector)_model.getDataVector().elementAt(index);
					data.setElementAt("" + numberOfUses, 5);
					// On va signaler le changement de la valeur
					_model.fireTableRowsUpdated(index, index);
					// On peut sortir
					trace_methods.endOfMethod();
					return;
				}
			}
			// Si on arrive ici, c'est que l'on n'a pas trouvé
			trace_debug.writeTrace("La ligne de données n'a pas été trouvée");
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: mouseReleased
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a relaché la pression sur l'un des boutons 
	* de la souris.
	* La méthode vérifie que l'utilisateur a utilisé le bouton droit, et 
	* qu'une ligne de données a été sélectionnée.
	* Puis, un menu contextuel est construit (voir la méthode 
	* buildContextualMenu()), et est affiché à l'écran.
	* 
	* Arguments:
	*  - event: L'événement de déclenchement de la méthode.
 	* ----------------------------------------------------------*/
 	public void mouseReleased(
 		MouseEvent event
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "tableDefinitionUseChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		MainWindowInterface window_interface = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// Si l'événement est null ou qu'il ne s'agit pas d'un click sur le
		// bouton droit, on sort
		if(event == null || SwingUtilities.isRightMouseButton(event) == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		window_interface = getMainWindowInterface();
		window_interface.setCurrentCursor(Cursor.WAIT_CURSOR, this);
		// Ensuite, on regarde s'il y a une ligne sélectionnée dans
		// le tableau
		NonEditableTable table = (NonEditableTable)event.getSource();
		int selected_row = table.getSelectedRow();
		if(selected_row == -1)
		{
			trace_debug.writeTrace(
				"Il n'y a pas de ligne sélectionnée !");
			// Il n'y a pas de ligne sélectionnée, on sort
			window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
			trace_methods.endOfMethod();
			return;
		}
		// On va construire le menu contextuel associé
		final JPopupMenu contextual_menu = buildContextualMenu(selected_row);
		// On vérifie que le menu contient au moins un élément
		if(contextual_menu == null)
		{
			trace_debug.writeTrace("Il n'y a pas de menu à afficher !");
			// Il n'y a pas de menu à afficher, on sort
			window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
			trace_methods.endOfMethod();
			return;
		}
		// Il y a un menu à afficher, on l'affiche dans le tableau
		// aux coordonnées du click
		contextual_menu.show(table, event.getX(), event.getY());
		window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: mouseClicked
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a cliqué.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseClicked(
 		MouseEvent event
 		)
 	{
 		// On ne fait rien
 	}

	/*----------------------------------------------------------
	* Nom: mouseEntered
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque la souris est entrée dans la zone d'affichage du 
	* composant.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseEntered(
 		MouseEvent event
 		)
	{
		// On ne fait rien
	}

	/*----------------------------------------------------------
	* Nom: mouseExited
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque la souris est sortie de la zone d'affichage du composant.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseExited(
 		MouseEvent event
 		)
	{
		// On ne fait rien
	}

	/*----------------------------------------------------------
	* Nom: mousePressed
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a pressé sur l'un des boutons de la souris.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mousePressed(
 		MouseEvent event
 		)
	{
		// On ne fait rien
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _model
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet DefaultTableModel 
	* correspondant au modèle des données du tableau d'affichage du cache des 
	* définitions des tables I-TOOLS.
	* ----------------------------------------------------------*/
	private DefaultTableModel _model;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre du processeur. 
	* Elle construit les données à partir de la liste des clés des définitions 
	* récupérées via la méthode getTableDefinitonKeys().
	* Ensuite, un tableau est créé et ajouté dans la fenêtre.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "makePanel");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		// On va s'enregistrer en tant que listener
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.registerListener(this);
		// On récupère toutes les clés des définitions
		Enumeration keys = manager.getTableDefinitionKeys();
		Vector rows = new Vector();
		while(keys.hasMoreElements() == true)
		{
			String key = (String)keys.nextElement();
			ObjectLeasingHolder leasing_holder =
				manager.getTableDefinitionLeasing(key);
			if(leasing_holder == null)
			{
				trace_errors.writeTrace("Le leasing pour la définition " +
					key + " n'est pas valide !");
				// On continue
				continue; 
			}
			UtilStringTokenizer tokenizer = new UtilStringTokenizer(key, ";");
			IsisTableDefinition definition = 
				(IsisTableDefinition)leasing_holder.getLeasedObject();
			Vector data = new Vector();
			data.add(definition.tableName);
			data.add(tokenizer.nextToken());
			data.add(tokenizer.nextToken());
			data.add(tokenizer.nextToken());
			data.add(definition.definitionFilePath);
			data.add("" + leasing_holder.getNumberOfLeasings());
			rows.add(data);
		}
		// Le panneau central correspond à un tableau
		// On crée les colonnes
		Vector columns = new Vector();
		columns.add(MessageManager.getMessage("&Definitions_Table"));
		columns.add(MessageManager.getMessage("&Definitions_Agent"));
		columns.add(MessageManager.getMessage("&Definitions_I-CLES"));
		columns.add(MessageManager.getMessage("&Definitions_ServiceType"));
		columns.add(MessageManager.getMessage("&Definitions_FilePath"));
		columns.add(MessageManager.getMessage("&Definitions_Uses"));
		// On crée le tableau
		_model = new DefaultTableModel(rows, columns);
		NonEditableTable table = new NonEditableTable(_model, ";");
		table.addMouseListener(this);
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(4).setPreferredWidth(300);
		table.getColumnModel().getColumn(5).setPreferredWidth(40);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fenêtre
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scroll, BorderLayout.CENTER);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Definitions_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
        // Maintenant, on va créer le bouton Fermer
		JButton empty_button =
			new JButton(MessageManager.getMessage("Vider non utilisés"));
		// On ajoute le callback sur le bouton
		empty_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
                // On va demander confirmation à l'utilisateur
                MainWindowInterface window_interface = getMainWindowInterface();
                int reply = window_interface.showPopup("YesNoQuestion",
                        "Supprimer les définitions non utilisées\nEtes vous sûr?", null);
                if (reply != JOptionPane.YES_OPTION) {
                    // L'utilisateur n'a pas validé la sortie
                    return;
                }
				// On appelle la méthode de nettoyage
				remove_unused();
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
        GridBagConstraints constraints2 =
			new GridBagConstraints(1, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		layout.setConstraints(empty_button, constraints2);
        button_panel.add(empty_button);
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: buildContextualMenu
	* 
	* Description:
	* Cette méthode est chargée de la construction d'un menu contextuel associé 
	* à la ligne de données identifiée par l'argument selectedRow.
	* Elle construit un menu contenant deux éléments: l'élément d'affichage de 
	* la définition et celui de suppression de la définition.
	* Si l'élément sélectionné a au moins une utilisation, le dernier élément 
	* est désactivé.
	* 
	* Arguments:
	*  - selectedRow: L'indice du rang de données sélectionné.
	* 
	* Retourne: Une référence sur un objet JPopupMenu correspondant au menu 
	* contextuel de la ligne de données sélectionné.
	* ----------------------------------------------------------*/
	private JPopupMenu buildContextualMenu(
		final int selectedRow
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "buildContextualMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		JPopupMenu popup_menu = null;
		String uses = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedRow=" + selectedRow);
		// On va synchroniser sur le modèle
		synchronized(_model)
		{
			// On vérifie que la ligne existe toujours (on ne sait jamais)
			try
			{
				uses = (String)_model.getValueAt(selectedRow, 5);
			}
			catch(Throwable throwable)
			{
				trace_debug.writeTrace("La ligne sélectionnée n'existe plus");
				// On sort
				trace_methods.endOfMethod();
				return popup_menu;
			}
		}
		// On va construire le menu
		JMenu menu = new JMenu();
		// On va construire l'élément de menu de définition
		JMenuItem define_item = new JMenuItem(
			MessageManager.getMessage("&DP_Menu_Define"));
		define_item.setIcon(IconLoader.getIcon("DefineTable"));
		define_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				displayDefinition(selectedRow);
			}
		});
		menu.add(define_item);
		// On va construire l'élément de menu de suppression
		JMenuItem remove_item = new JMenuItem(
			MessageManager.getMessage("&DP_Menu_Remove"));
		Icon icon = IconLoader.getIcon("Remove");
		if(icon == null)
		{
			icon = IconLoader.getIcon("Empty");
		}
		remove_item.setIcon(icon);
		if(uses.equals("0") == false)
		{
			remove_item.setEnabled(false);
		}
		remove_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
                // On va demander confirmation à l'utilisateur
                MainWindowInterface window_interface = getMainWindowInterface();
                int reply = window_interface.showPopup("YesNoQuestion",
                        "&Question_ConfirmRemoval", null);
                if (reply != JOptionPane.YES_OPTION) {
                    // L'utilisateur n'a pas validé la sortie
                    return;
                }
				removeDefinition(selectedRow);
			}
		});
		menu.add(remove_item);
		popup_menu = menu.getPopupMenu();
		trace_methods.endOfMethod();
		return popup_menu;
	}

	/*----------------------------------------------------------
	* Nom: displayDefinition
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément 
	* de menu permettant l'affichage de la définition sélectionnée.
	* Elle appelle la méthode executeProcessor() de la classe ProcessorManager 
	* afin de déclencher l'exécution du processeur d'affichage de la définition 
	* d'une table.
	* 
	* Arguments:
	*  - selectedRow: L'indice du rang de données sélectionné.
	* ----------------------------------------------------------*/
	private void displayDefinition(
		int selectedRow
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "displayDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String agent_name = null;
		String icles_name = null;
		String service_type = null;
		String file_path = null;
		String table_name = null;
		MainWindowInterface window_interface = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedRow=" + selectedRow);
		// On va synchroniser sur le modèle
		synchronized(_model)
		{
			// On vérifie que la ligne existe toujours (on ne sait jamais)
			try
			{
				table_name = (String)_model.getValueAt(selectedRow, 0);
				agent_name = (String)_model.getValueAt(selectedRow, 1);
				icles_name = (String)_model.getValueAt(selectedRow, 2);
				service_type = (String)_model.getValueAt(selectedRow, 3);
				file_path = (String)_model.getValueAt(selectedRow, 4); 
			}
			catch(Throwable throwable)
			{
				trace_errors.writeTrace("La ligne sélectionnée n'existe plus");
				// On sort
				trace_methods.endOfMethod();
				return;
			}
		}
		window_interface = getMainWindowInterface();
		// On va créer un faux noeud temporaire
		SessionTreeObjectNode node = new SessionTreeObjectNode(agent_name, 
			icles_name, service_type, new GenericTreeObjectNode(null, null, 
			agent_name, icles_name, service_type, file_path, table_name));
		node.setAgentAndSession(null, null, null);
		TableDefinitionManager manager = 
			TableDefinitionManager.getInstance();
		manager.releaseTableDefinitionLeasing(agent_name, icles_name, 
			service_type, file_path);
		manager.releaseTableDefinitionLeasing(agent_name, icles_name, 
			service_type, file_path);
		// On va commander l'invocation du processeur d'affichage de la
		// définition
		try
		{
			JMenuItem menu_item = new JMenuItem("dummy");
			menu_item.setIcon(IconLoader.getIcon("DefineTable"));
			ProcessorManager.executeProcessor("DefineTable", window_interface,
				menu_item, null, null, null, node, false, false);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'affichage de " +
				"la définition: " + exception);
			// On affiche l'erreur à l'utilisateur
			window_interface.showPopupForException(
				"&ERR_CannotDisplayDefinition", exception);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeDefinition
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément 
	* de suppression d'une définition.
	* Une confirmation est demandée à l'utilisateur, et s'il confirme, la 
	* définition est supprimée via la méthode dumpTableDefinition() de la 
	* classe TableDefinitionManager.
	* 
	* Arguments:
	*  - selectedRow: L'indice du rang de données sélectionné.
 	* ----------------------------------------------------------*/
 	private void removeDefinition(
 		int selectedRow
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDefinitionsProcessor", "removeDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String uses = null;
		String agent_name = null;
		String icles_name = null;
		String service_type = null;
		String file_path = null;
		MainWindowInterface window_interface = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedRow=" + selectedRow);
		// On va synchroniser sur le modèle
		synchronized(_model)
		{
			// On vérifie que la ligne existe toujours (on ne sait jamais)
			try
			{
				agent_name = (String)_model.getValueAt(selectedRow, 1);
				icles_name = (String)_model.getValueAt(selectedRow, 2);
				service_type = (String)_model.getValueAt(selectedRow, 3);
				file_path = (String)_model.getValueAt(selectedRow, 4); 
				uses = (String)_model.getValueAt(selectedRow, 5);
			}
			catch(Throwable throwable)
			{
				trace_errors.writeTrace("La ligne sélectionnée n'existe plus");
				// On sort
				trace_methods.endOfMethod();
				return;
			}
		}
		window_interface = getMainWindowInterface();
		// On vérifie que la définition n'est pas utilisée
		if(uses.equals("0") == false)
		{
			// On va afficher un message d'erreur à l'utilisateur
			window_interface.showPopup("Error",
				"&ERR_DefinitionStillInUse", null);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		
		// On tente de supprimer la définition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		try
		{
			manager.dumpTableDefinition(agent_name, icles_name, service_type, 
				file_path);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la suppression de " +
				"la définition: " + exception);
			// On affiche l'erreur à l'utilisateur
			window_interface.showPopupForException(
				"&ERR_CannotRemoveDefinition", exception);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		trace_methods.endOfMethod();
 	}

    private void remove_unused()
    {
        for (int row=_model.getRowCount()-1; row >= 0 ; row--) {
            if(((String)_model.getValueAt(row, 5)).equals("0")) {
                removeDefinition(row);
            }
        }
    }
}
