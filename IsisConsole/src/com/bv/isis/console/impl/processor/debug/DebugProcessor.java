/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/debug/DebugProcessor.java,v $
* $Revision: 1.20 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de debug
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.debug
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DebugProcessor.java,v $
* Revision 1.20  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.19  2008/08/11 10:48:32  tz
* Remplacement SeparatorObjectType par SeparatorType.
*
* Revision 1.18  2008/02/21 12:07:12  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.17  2008/01/31 16:47:07  tz
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.16  2005/12/23 13:17:56  tz
* Correction mineure.
*
* Revision 1.15  2005/10/14 14:38:31  tz
* Changement mineur.
*
* Revision 1.14  2005/10/07 08:30:43  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.13  2005/07/01 12:19:58  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/02 08:56:24  tz
* Gestion des leasings sur les d�finitions.
*
* Revision 1.11  2004/10/22 15:40:21  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.9  2004/10/13 13:56:45  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/10/06 07:38:04  tz
* Am�lioration de la dimension de la frame,
* Utilisation du gestionnaire de rendu pour les bool�ens.
*
* Revision 1.7  2004/07/29 12:16:04  tz
* Mise � jour de la documentation
*
* Revision 1.6  2002/09/20 10:46:31  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.5  2002/04/05 15:50:20  tz
* Cloture it�ration IT1.2
*
* Revision 1.4  2002/03/27 09:51:15  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:25  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/12/12 09:59:26  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.debug;

//
// Imports syst�me
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import java.awt.Insets;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import javax.swing.table.TableCellRenderer;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisMethod;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.impl.processor.display.BooleanCellRenderer;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: DebugProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che permettant d'obtenir une
* fen�tre de d�boggage pr�sentant l'ensemble des informations repr�sentant un
* noeud graphique s�lectionn�.
* Cette fen�tre affiche les informations suivantes:
*  - Informations g�n�rales sur le noeud,
*  - Informations sp�cifiques suivant le type de noeud
*  - Le contexte du noeud et l'environnement du service,
*  - Les m�thodes associ�es au noeud.
*
* Toutes ces informations sont pr�sent�es dans des onglets diff�rents de cette
* fen�tre.
* ----------------------------------------------------------*/
public class DebugProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DebugProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public DebugProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "DebugProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�fini celle de la classe ProcessorFrame. Elle est
	* appel�e par le ProcessManager afin d'initialiser et de d'ex�cuter le
	* processeur. Le panneau est construit, via la m�thode makePanel(), 
	* puis la sous-fen�tre est affich�e.
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
			"DebugProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Si le noeud s�lectionn� est nul, c'est une erreur
		if(selectedNode == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Pas de noeud s�lectionn� !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On appelle la m�thode de la super classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On construit le panneau
		try
		{
		    makePanel();
		}
		catch(InnerException exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de l'ouverture du processeur de d�bogage: " +
				exception.getMessage());
			// Il y a eu une erreur, on affiche le message � l'utilisateur
			windowInterface.showPopupForException(
				"&ERR_CannotOpenDebugProcessor", exception);
		}
		// On positionne les dimensions du panneau
		setMinimumSize(new Dimension(485, 300));
		setPreferredSize(getMinimumSize());
		// On affiche le panneau
		display();
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
			"DebugProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("debug.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel� pour savoir si le processeur peut �tre invoqu� via un �l�ment 
	* de tableau.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "isTableCapable");

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
			"DebugProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DebugProcessorDescription");
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
			"DebugProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DebugProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode est appel�e par la m�thode initialise pour construire le
	* panneau de la sous-fen�tre. Le panneau est construit en plusieurs onglets,
	* afin d'afficher les informations accessibles via le noeud s�lectionn�
	* (obligatoire).
	*
	* Si un probl�me survient lors de la construction de la fen�tre,
	* l'exception InnerException est lev�e.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void makePanel()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// On positionne le layout de la sous-fen�tre
		getContentPane().setLayout(new BorderLayout());
		// On cr�e la barre � onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		// On r�cup�re le noeud s�lectionn�
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		// On positionne le titre
		setTitle(selected_node.getLabel().label);
		// Construction de l'onglet G�n�ral
		JPanel general_panel = makeGeneralPanel(selected_node);
		tabbed_pane.addTab(MessageManager.getMessage("&Debug_GeneralTab"),
			general_panel);
		// Construction de l'onglet Objet
		JPanel object_panel = makeObjectPanel(selected_node);
		String message;
		if(selected_node instanceof GenericTreeClassNode)
		{
			message = "&Debug_ClassTab";
		}
		else
		{
			message = "&Debug_InstanceTab";
		}
		tabbed_pane.addTab(MessageManager.getMessage(message),
			object_panel);
		// Construction de l'onglet d'environnement
		JPanel environment_panel = makeEnvironmentPanel(selected_node);
		tabbed_pane.addTab(MessageManager.getMessage("&Debug_EnvironmentTab"),
			environment_panel);
		// Construction de l'onglet des m�thodes
		JPanel methods_panel = makeMethodsPanel(selected_node);
		tabbed_pane.addTab(MessageManager.getMessage("&Debug_MethodsTab"),
			methods_panel);
		// On place la barre � onglet dans la zone centrale de la sous-fen�tre
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);
		// On ajoute un bouton pour permettre la fermeture de la sous-fen�tre
		JButton close_button = new JButton(
			MessageManager.getMessage("&Debug_Close"));
		// On ajoute le listener sur le click
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode close
				close();
			}
		});
		// On cr�e un panneau pour le bouton
		JPanel button_panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		button_panel.setLayout(layout);
		// On ajoute le bouton dans le panneau des boutons
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);
		// Le bouton est plac� dans la zone inf�rieure
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeGeneralPanel
	*
	* Description:
	* Cette m�thode permet de construire le panneau qui sera plac� dans la
	* barre � onglet de la sous-fen�tre de d�boggage. Ce panneau contient les
	* informations g�n�rales qui sont accessibles sur tous les types de noeuds.
	*
	* Si un probl�me survient lors de la construction du panneau, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - selectedNode: Le noeud graphique s�lectionn�.
	*
	* Retourne: Une instance de JPanel.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private JPanel makeGeneralPanel(
		GenericTreeObjectNode selectedNode
		)
		throws
			InnerException
	{
		String class_object_type = "Table";
		String instance_object_type = "Instance";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "makeGeneralPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		try
		{
			// On r�cup�re les param�tres de config d�finissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
			instance_object_type = configuration_api.getString("I-SIS",
				"InstanceObjectType");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la construction du menu: " +
				exception.getMessage());
		}
		// R�cup�ration du type de noeud
		String object_type;
		if(selectedNode instanceof GenericTreeClassNode)
		{
			object_type = class_object_type;
		}
		else
		{
			object_type = instance_object_type;
		}
		String[] user_responsabilities = null;
		// R�cup�ration de la liste des responsabilit�s
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(selectedNode.getServiceSession());
		user_responsabilities = session_proxy.getUserResponsabilities();
		StringBuffer responsabilities = new StringBuffer();
		for(int index = 0 ; index < user_responsabilities.length ;
			index ++)
		{
			if(index > 0)
			{
				responsabilities.append(",");
			}
			responsabilities.append(user_responsabilities[index]);
		}
		// Les donn�es � afficher sont contenues dans un tableau � 2 dimensions
		String[] columns = {
			MessageManager.getMessage("&Debug_Name"),
			MessageManager.getMessage("&Debug_Value")
			};
		String[][] data = {
			{ MessageManager.getMessage("&Debug_Label"),
				selectedNode.getLabel().label },
			{ MessageManager.getMessage("&Debug_Icon"),
				selectedNode.getLabel().icon },
			{ MessageManager.getMessage("&Debug_Identity"),
				selectedNode.getLabel().nodeIdentity },
			{ MessageManager.getMessage("&Debug_Type"), object_type },
			{ MessageManager.getMessage("&Debug_AgentName"),
				selectedNode.getAgentName() },
			{ MessageManager.getMessage("&Debug_TableName"),
				selectedNode.getTableName() },
			{ MessageManager.getMessage("&Debug_DefinitionPath"),
				selectedNode.getDefinitionFilePath() },
			{ MessageManager.getMessage("&Debug_Responsabilities"),
				responsabilities.toString() }
		};
		// Cr�ation du tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(320);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// Placement du tableau dans une zone de d�filement
		JScrollPane scroll = new JScrollPane(table);
		// Cr�ation du panneau, et ajout de la table dedans
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeObjectPanel
	*
	* Description:
	* Cette m�thode permet de construire le panneau qui sera plac� dans la
	* barre � onglet de la sous-fen�tre de d�boggage. Ce panneau contient les
	* informations d�pendantes du type de noeud (instance ou table).
	* Dans le cas d'un noeud instance, le panneau pr�sente les param�tres de
	* l'objet. Dans l'autre cas, le panneau pr�sente simplement la requ�te.
	*
	* Arguments:
	*  - selectedNode: Le noeud graphique s�lectionn�.
	*
	* Retourne: Une instance de JPanel.
	* ----------------------------------------------------------*/
	private JPanel makeObjectPanel(
		GenericTreeObjectNode selectedNode
		)
	{
		NonEditableTable object_table;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "makeObjectPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Les informations sont contenues dans un tableau � deux dimensions,
		// de toute fa�on
		if(selectedNode instanceof GenericTreeClassNode)
		{
			GenericTreeClassNode class_node = (GenericTreeClassNode)selectedNode;
			// Dans le cas d'un objet de type GenericTreeClassNode, on
			// n'affiche que la requ�te.
			String[][] data = {
				{ MessageManager.getMessage("&Debug_Condition"),
					class_node.getCondition() }
			};
			String[] columns = {
				MessageManager.getMessage("&Debug_Name"),
				MessageManager.getMessage("&Debug_Value")
				};
			// Cr�ation du tableau
			object_table = new NonEditableTable(data, columns, ";");
		}
		else
		{
			// Dans le cas d'un objet de type GenericTreeObjectNode, on
			// affiche la cl� et tous les param�tres de l'objet
			IsisParameter[] parameters =
				selectedNode.getObjectParameters();
			String[][] data = new String[parameters.length + 1][2];
			// R�cup�ration de la d�finition de la table
			TableDefinitionManager manager =
				TableDefinitionManager.getInstance();
			IsisTableDefinition definition = manager.getTableDefinition(
				selectedNode.getAgentName(), selectedNode.getIClesName(),
				selectedNode.getServiceType(),
				selectedNode.getDefinitionFilePath());
			// Ajout de la cl�
			data[0][0] = MessageManager.getMessage("&Debug_Key");
			data[0][1] = selectedNode.getKey();
			// Ajout des param�tres
			for(int index = 0 ; index < parameters.length ; index ++)
			{
				data[index + 1][0] = parameters[index].name;
				data[index + 1][1] = parameters[index].value;
				if(isParameterExportable(definition, data[index + 1][0]) == true)
				{
					data[index + 1][0] = data[index + 1][0] + " (*)";
				}
			}
			// On lib�re l'utilisation de la d�finition
			manager.releaseTableDefinitionLeasing(definition);
			// Cr�ation du tableau
			String[] columns = {
				MessageManager.getMessage("&Debug_Name"),
				MessageManager.getMessage("&Debug_Value")
				};
			object_table = new NonEditableTable(data, columns, ";");
		}
		// On r�gle les param�tres du tableau
		object_table.getColumnModel().getColumn(0).setPreferredWidth(120);
		object_table.getColumnModel().getColumn(1).setPreferredWidth(320);
		object_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// Placement du tableau dans une zone de d�filement
		JScrollPane object_scroll = new JScrollPane(object_table);
		// Ajout d'une bordure � la zone de d�filement
		object_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Debug_Parameters")));

		JPanel object_panel = new JPanel(new BorderLayout());
		object_panel.add(object_scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return object_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeEnvironmentPanel
	*
	* Description:
	* Cette m�thode permet de construire le panneau qui sera plac� dans la
	* barre � onglet de la sous-fen�tre de d�boggage. Ce panneau contient les
	* informations d'environnement que sont le contexte du noeud et
	* l'environnement de service.
	*
	* Si un probl�me survient lors de la construction du panneau, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - selectedNode: Le noeud graphique s�lectionn�.
	*
	* Retourne: Une instance de JPanel.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private JPanel makeEnvironmentPanel(
		GenericTreeObjectNode selectedNode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "makeEnvironmentPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Le panneau d'environnement est fait en deux parties
		// La premi�re partie indique le contexte
		String[] columns = {
			MessageManager.getMessage("&Debug_Name"),
			MessageManager.getMessage("&Debug_Value")
			};
		// R�cup�ration du contexte
		IndexedList context = selectedNode.getContext(true);
		// Cr�ation du tableau des donn�es
		String[][] data = new String[context.size()][2];
		IsisParameter[] object_parameters =
			(IsisParameter[])context.toArray(
			new IsisParameter[0]);
		for(int index = 0 ; index < object_parameters.length ; index ++)
		{
			IsisParameter parameter =
				object_parameters[index];
			data[index][0] = parameter.name;
			data[index][1] = parameter.value;
		}
		// Cr�ation de la table
		NonEditableTable context_table = new NonEditableTable(data, columns, ";");
		// On r�gle les param�tres du tableau
		context_table.getColumnModel().getColumn(0).setPreferredWidth(120);
		context_table.getColumnModel().getColumn(1).setPreferredWidth(320);
		context_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// Placement du tableau dans une zone de d�filement
		JScrollPane context_scroll = new JScrollPane(context_table);
		// Ajout d'une bordure � la zone de d�filement
		context_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Debug_Context")));

		// La deuxi�me partie indique l'environnement de session
		// R�cup�ration de l'environnement de session
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(selectedNode.getServiceSession());
		IsisParameter[] parameters = session_proxy.getEnvironment();
		// Cr�ation du tableau des donn�es
		data = new String[parameters.length][2];
		for(int index = 0 ; index < parameters.length ; index ++)
		{
			data[index][0] = parameters[index].name;
			data[index][1] = parameters[index].value;
		}
		// Cr�ation de la table
		NonEditableTable evaluations_table = new NonEditableTable(data, columns, ";");
		// On r�gle les param�tres du tableau
		evaluations_table.getColumnModel().getColumn(0).setPreferredWidth(120);
		evaluations_table.getColumnModel().getColumn(1).setPreferredWidth(320);
		evaluations_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// Placement du tableau dans une zone de d�filement
		JScrollPane evaluations_scroll = new JScrollPane(evaluations_table);
		// Ajout d'une bordure � la zone de d�filement
		evaluations_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Debug_Environment")));

		// Maintenant, on cr�e le panneau qui contiendra les deux tableaux
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel environment_panel = new JPanel(layout);
		// On ajoute le premier tableau
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 100;
		constraints.weighty = 50;
		layout.setConstraints(context_scroll, constraints);
		environment_panel.add(context_scroll);
		// On ajoute le deuxi�me tableau
		constraints.gridy = 1;
		layout.setConstraints(evaluations_scroll, constraints);
		environment_panel.add(evaluations_scroll);
		trace_methods.endOfMethod();
		return environment_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeMethodsPanel
	*
	* Description:
	* Cette m�thode permet de construire le panneau qui sera plac� dans la
	* barre � onglet de la sous-fen�tre de d�boggage. Ce panneau contient les
	* informations sur toutes les m�thodes d�finies pour la table.
	*
	* Si un probl�me survient lors de la construction du panneau, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - selectedNode: Le noeud graphique s�lectionn�.
	*
	* Retourne: Une instance de JPanel.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private JPanel makeMethodsPanel(
		GenericTreeObjectNode selectedNode
		)
		throws
			InnerException
	{
		NonEditableTable methods_table;
		String class_object_type = "Table";
		String instance_object_type = "Instance";
		String method_type = instance_object_type;
		String separator_type = "Separator";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "makeMethodsPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		try
		{
			// On r�cup�re les param�tres de config d�finissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
			instance_object_type = configuration_api.getString("I-SIS",
				"InstanceObjectType");
			separator_type = configuration_api.getString("I-SIS",
				"SeparatorType");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la construction du menu: " +
				exception.getMessage());
		}
		if(selectedNode instanceof GenericTreeClassNode)
		{
			method_type = class_object_type;
		}
		else
		{
			method_type = instance_object_type;
		}
		// On va r�cup�rer la liste des m�thodes
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(selectedNode.getServiceSession());
		IsisMethod[] methods = session_proxy.getMethods(
			selectedNode.getTableName(), selectedNode.getContext(true));
		String[] responsabilities = session_proxy.getUserResponsabilities();
		String[] columns = {
			MessageManager.getMessage("&Debug_NodeType"),
			MessageManager.getMessage("&Debug_Group"),
			MessageManager.getMessage("&Debug_Label"),
			MessageManager.getMessage("&Debug_Responsabilities"),
			MessageManager.getMessage("&Debug_Condition"),
			MessageManager.getMessage("&Debug_Preprocessing"),
			MessageManager.getMessage("&Debug_Processor"),
			MessageManager.getMessage("&Debug_Arguments"),
			MessageManager.getMessage("&Debug_Confirm"),
			MessageManager.getMessage("&Debug_PostProcessing"),
			MessageManager.getMessage("&Debug_Icon"),
			MessageManager.getMessage("&Debug_Applicable")
		};
		Object[][] data = new Object[methods.length][12];
		for(int index = 0 ; index < methods.length ; index ++)
		{
			data[index][0] = methods[index].nodeType;
			data[index][1] = methods[index].group;
			data[index][2] = methods[index].label;
			StringBuffer responsabilities_buffer = new StringBuffer();
			for(int loop = 0 ; loop < methods[index].responsabilities.length ;
				loop ++)
			{
				if(loop != 0)
				{
					responsabilities_buffer.append(",");
				}
				responsabilities_buffer.append(
					methods[index].responsabilities[loop]);
			}
			data[index][3] = responsabilities_buffer.toString();
			data[index][4] = new Boolean(methods[index].condition);
			data[index][5] = methods[index].preProcessing;
			data[index][6] = methods[index].processor;
			data[index][7] = methods[index].arguments;
			data[index][8] = new Boolean(methods[index].confirm);
			data[index][9] = methods[index].postProcessing;
			data[index][10] = methods[index].icon;
		    data[index][11] = new Boolean(MenuFactory.isMethodMatching(methods[index], responsabilities,
				method_type, separator_type, true));
		}
		// ObjectType:Group:Name:Icon:Resp:Condition:Preprocessing:Processor:Command:Confirm:Reload
		final BooleanCellRenderer boolean_renderer = new BooleanCellRenderer();
		methods_table = new NonEditableTable(data, columns, ";"){
			public TableCellRenderer getCellRenderer(int row, int column)
			{
				if(column == 4 || column == 8 || column == 11)
				{
					return boolean_renderer;
				}
				return super.getCellRenderer(row, column);
			}
		};
		// On r�gle les param�tres du tableau
		methods_table.getColumnModel().getColumn(0).setPreferredWidth(80);
		methods_table.getColumnModel().getColumn(1).setPreferredWidth(100);
		methods_table.getColumnModel().getColumn(2).setPreferredWidth(100);
		methods_table.getColumnModel().getColumn(3).setPreferredWidth(80);
		methods_table.getColumnModel().getColumn(4).setPreferredWidth(80);
		methods_table.getColumnModel().getColumn(5).setPreferredWidth(80);
		methods_table.getColumnModel().getColumn(6).setPreferredWidth(200);
		methods_table.getColumnModel().getColumn(7).setPreferredWidth(80);
		methods_table.getColumnModel().getColumn(8).setPreferredWidth(200);
		methods_table.getColumnModel().getColumn(9).setPreferredWidth(70);
		methods_table.getColumnModel().getColumn(10).setPreferredWidth(70);
		methods_table.getColumnModel().getColumn(11).setPreferredWidth(70);
		methods_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_OFF);
		// Placement du tableau dans une zone de d�filement
		JScrollPane methods_scroll = new JScrollPane(methods_table);

		JPanel methods_panel = new JPanel(new BorderLayout());
		methods_panel.add(methods_scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return methods_panel;
	}

	/*----------------------------------------------------------
	* Nom: isParameterExportable
	*
	* Description:
	* Cette m�thode permet de savoir si un param�tre, d�fini par l'argument
	* parameterName, est exportable ou non. Cette information est pr�sente dans
	* l'objet definition pass� en argument.
	*
	* Argument:
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition contenant
	*    les caract�ristiques de la table,
	*  - parameterName: Le nom du param�tre dont on veut r�cup�rer l'attribut
	*    exportable.
	*
	* Retourne: true si le param�tre est exportable, false sinon.
	* ----------------------------------------------------------*/
	private boolean isParameterExportable(
		IsisTableDefinition definition,
		String parameterName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DebugProcessor", "isParameterExportable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("definition=" + definition);
		trace_arguments.writeTrace("parameterName=" + parameterName);
		if(definition == null)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// On regarde si le param�tre sp�cifi� est dans la cl�
		for(int index = 0 ; index < definition.key.length ; index ++)
		{
			if(definition.key[index].equals(parameterName) == true)
			{
				// On a trouv� le param�tre, il est exportable
				trace_methods.endOfMethod();
				return true;
			}
		}
		trace_methods.endOfMethod();
		return false;
	}
}