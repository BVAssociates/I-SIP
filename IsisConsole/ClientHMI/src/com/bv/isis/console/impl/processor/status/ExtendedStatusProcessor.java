/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/status/ExtendedStatusProcessor.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'état étendu d'un Agent
* DATE:        04/11/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.status
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExtendedStatusProcessor.java,v $
* Revision 1.7  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.6  2008/02/21 12:09:47  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.5  2008/01/31 16:59:16  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.4  2006/03/07 09:33:06  tz
* Mise à jour du graphique toutes les 2 secondes, au lieu de toutes les 10 secondes.
*
* Revision 1.3  2005/12/23 13:23:04  tz
* Correction mineure.
*
* Revision 1.2  2005/07/01 12:09:12  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/11/05 10:41:33  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.status;

//
// Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.bv.core.message.MessageManager;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Second;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.corbacom.AgentInterface;
import com.bv.isis.corbacom.IsisServerStatus;

/*----------------------------------------------------------
* Nom: ExtendedStatusProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage de 
* certaines informations de l'état complet d'un Agent. Il s'agit d'afficher la 
* charge de l'Agent par le biais d'un graphique en camembert, ainsi qu'un 
* affichage temporel, et d'afficher le nombre de sessions ouvertes et le 
* nombre d'environnements de services en cache par un graphique temporel.
* Le processeur étant un processeur graphique, cette classe spécialise la 
* classe ProcessorFrame. Elle implémente l'interface ActionListener afin de 
* permettre la récupération périodique des données d'état de l'Agent.
* ----------------------------------------------------------*/
public class ExtendedStatusProcessor
	extends ProcessorFrame
	implements ActionListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExtendedStatusProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public ExtendedStatusProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "ExtendedStatusProcessor");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée afin d'effectuer le pré-chargement du processeur.
	* Elle effectue le chargement du fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "preLoad");
			
		trace_methods.beginningOfMethod();
		MessageManager.loadFile("xstatus.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour déclencher l'exécution du processeur.
	* Le panneau d'affichage de l'état détaillé est construit par un appel à 
	* la méthode makePanel(), puis la fenêtre est affichée.
	* 
	* Si un problème est détecté lors de l'exécution du processeur, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - menuItem: Une référence sur l'élément de menu par lequel le 
	*    processeur a été lancé,
	*  - parameters: Une chaîne contenant des paramètres optionnels du 
	*    processeur de tâche,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud à explorer.
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
			"ExtendedStatusProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Si le noeud sélectionné est nul, c'est une erreur
		if(selectedNode == null || windowInterface == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud sélectionné !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On appelle la méthode de la super classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		GenericTreeObjectNode selected_node = 
			(GenericTreeObjectNode)selectedNode;
		// Le nom de l'Agent provient des arguments du processeur ou du noeud
		if(parameters != null && parameters.equals("") == false)
		{
			_agentName = parameters;
		}
		else
		{
			_agentName = selected_node.getAgentName();
		}
		// On va construire la fenêtre
		windowInterface.setProgressMaximum(1);
		windowInterface.setStatus("&StatusProcessor_BuildingFrame", null, 0);
		makePanel();
		// On positionne les dimensions du panneau
		setMinimumSize(new Dimension(200, 150));
		setPreferredSize(new Dimension(400, 300));
		// On va créer le timer
		_timer = new Timer(2 * 1000, this);
		// On lance le timer
		_timer.setInitialDelay(1000);
		_timer.start();
		// On affiche le panneau
		display();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque la fenêtre du processeur doit être fermée.
	* Le processeur arrête le timer de récupération des données d'état, puis 
	* appelle la méthode de la super-classe.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "close");
			
		trace_methods.beginningOfMethod();
		// On arrête le timer
		_timer.stop();
		// On libère les ressources
		_timer = null;
		_agentName = null;
		_piePlot = null;
		_loadSeries = null;
		_sessionsSeries = null;
		_sessionsAxis = null;
		_environmentsSeries = null;
		_environmentsAxis = null;
		// On appelle la méthode de la super-classe
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
			"ExtendedStatusProcessor", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&XStatusProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de ExtendedStatusProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "duplicate");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ExtendedStatusProcessor();
	}

	/*----------------------------------------------------------
	* Nom: actionPerformed
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ActionListener. Elle est 
	* automatiquement appelée par le Timer à la fin de chaque période.
	* La méthode récupére l'interface de l'Agent concerné, via la méthode 
	* getAgentInterface() de la classe PortalInterfaceProxy, appelle la méthode 
	* getStatus() sur cette interface, puis positionne les données sur les 
	* différents modèles de données des graphiques.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
	public void actionPerformed(
		ActionEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "actionPerformed");
		double sessions_max = 0;
		double environments_max = 0;
		IsisServerStatus agent_status = null;
		int load = 0;
			
		trace_methods.beginningOfMethod();
		// On va commencer par récupérer l'état de l'Agent
		try
		{
			PortalInterfaceProxy portal_proxy = 
				PortalInterfaceProxy.getInstance();
			AgentInterface agent_interface = 
				portal_proxy.getAgentInterface(_agentName);
			agent_status = agent_interface.getStatus();
		}
		catch(Exception exception)
		{
			InnerException inner_exception =
				CommonFeatures.processException("la récupération de l'état " +
				"de l'Agent", exception);
			// On va afficher un message d'erreur à l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotGetAgentXStatus", inner_exception);
			// On ferme la fenêtre et on sort
			close();
			trace_methods.endOfMethod();
			return;
		}
		// La première chose à faire est de positionner la valeur de
		// la charge sur le camembert
		load = (agent_status.currentOperations * 100) /
			agent_status.maxOperations;
		DefaultPieDataset pie_data = (DefaultPieDataset)_piePlot.getDataset();
		pie_data.setValue(MessageManager.getMessage("&XStatusProcessor_OnGoing"),
			new Integer(agent_status.currentOperations));
		pie_data.setValue(
			MessageManager.getMessage("&XStatusProcessor_Remaining"),
			new Integer(agent_status.maxOperations - 
			agent_status.currentOperations));
		// Si la charge est supérieure à 75%, la couleur de la première portion
		// du camembert est rouge, sinon elle est verte
		if(load > 75)
		{
			_piePlot.setSectionPaint(0, Color.red); 
		}
		else
		{
			_piePlot.setSectionPaint(0, Color.blue);
		}
		// On va mettre à jour le graphique temporel de la charge
		_loadSeries.add(new Second(), new Integer(load));
		// On va mettre à jour le graphique des sessions
		_sessionsSeries.add(new Second(),  
			new Integer(agent_status.activeSessions));
		sessions_max = _sessionsAxis.getUpperBound();
		if(sessions_max < (double)agent_status.activeSessions)
		{
			// On va changer l'échelle des sessions
			_sessionsAxis.setUpperBound((double)agent_status.activeSessions);
		}
		// On va mettre à jour le graphique des environnements
		_environmentsSeries.add(new Second(),  
			new Integer(agent_status.iClesServices));
		environments_max = _environmentsAxis.getUpperBound();
		if(environments_max < (double)agent_status.iClesServices)
		{
			// On va changer l'échelle des environnements
			_environmentsAxis.setUpperBound((double)agent_status.iClesServices);
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _agentName
	* 
	* Description:
	* Cet attribut maintient le nom de l'Agent dont l'état doit être affiché.
	* ----------------------------------------------------------*/
	private String _agentName;

	/*----------------------------------------------------------
	* Nom: _timer
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet Timer permettant de 
	* récupérer périodiquement les données d'état de l'Agent et de mettre à 
	* jour les graphiques.
	* ----------------------------------------------------------*/
	private Timer _timer;

	/*----------------------------------------------------------
	* Nom: _piePlot
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet PiePlot3D correspondant 
	* au gestionnaire d'affichage du graphique en camembert. Cet attribut est 
	* nécessaire pour modifier les couleurs d'affichage des portions du 
	* camembert, ainsi que pour accéder au modèle des données du camembert.
	* ----------------------------------------------------------*/
	private PiePlot3D _piePlot;

	/*----------------------------------------------------------
	* Nom: _loadSeries
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet TimeSeries 
	* correspondant au modèle des données du graphique des évolutions de la 
	* charge de l'Agent. Il est nécessaire afin d'enrichir le graphique avec 
	* de nouvelles données.
	* ----------------------------------------------------------*/
	private TimeSeries _loadSeries;

	/*----------------------------------------------------------
	* Nom: _sessionsSeries
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet TimeSeries 
	* correspondant au modèles des données du graphique des évolutions des 
	* sessions actives de l'Agent. Il est nécessaire afin d'enrichir le 
	* graphique avec de nouvelles données.
	* ----------------------------------------------------------*/
	private TimeSeries _sessionsSeries;

	/*----------------------------------------------------------
	* Nom: _sessionsAxis
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet NumberAxis 
	* correspondant au gestionnaire de l'axe des ordonnées du graphique des 
	* sessions actives de l'Agent. Il est nécessaire afin de faire évoluer la 
	* graduation en fonction de la valeur instantannée des sessions actives.
	* ----------------------------------------------------------*/
	private NumberAxis _sessionsAxis;

	/*----------------------------------------------------------
	* Nom: _environmentsSeries
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet TimeSeries 
	* correspondant au modèles des données du graphique des évolutions du 
	* cache des environnements de service de l'Agent. Il est nécessaire afin 
	* d'enrichir le graphique avec de nouvelles données.
	* ----------------------------------------------------------*/
	private TimeSeries _environmentsSeries;

	/*----------------------------------------------------------
	* Nom: _environmentsAxis
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet NumberAxis 
	* correspondant au gestionnaire de l'axe des ordonnées du graphique du 
	* chache des environnements de service de l'Agent. Il est nécessaire afin 
	* de faire évoluer la graduation en fonction de la valeur instantannée de 
	* la taille du cache.
	* ----------------------------------------------------------*/
	private NumberAxis _environmentsAxis;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre d'affichage 
	* de l'état étendu d'un Agent. Pour cela, elle construit un graphique sous 
	* forme de camembert, chargé de réprésenter la charge instantannée de 
	* l'Agent, un graphique temporel chargé de représenter l'évolution de la 
	* charge de l'Agent, ainsi qu'un dernier graphique temporel chargé de 
	* représenter les évolutions du nombre de sessions actives et du nombre 
	* d'environnements de service en cache.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "makePanel");
			
		trace_methods.beginningOfMethod();
		setTitle(MessageManager.getMessage("&XStatusProcessor_Title") +
			_agentName);
		// On crée le panneau de charge
		JPanel load_panel = new JPanel();
		GridBagLayout load_layout = new GridBagLayout();
		GridBagConstraints load_constraints = new GridBagConstraints();
		load_panel.setLayout(load_layout);
		// On va créer le camembert de charge
		// On commence par le modèle des données
		DefaultPieDataset pie_dataset = new DefaultPieDataset();
		pie_dataset.setValue(
			MessageManager.getMessage("&XStatusProcessor_OnGoing"), 
			new Integer(0));
		pie_dataset.setValue(
			MessageManager.getMessage("&XStatusProcessor_Remaining"), 
			new Integer(1));
		// On va créer le graphique en 3D
		JFreeChart pie_chart = ChartFactory.createPieChart3D(null, pie_dataset, 
			false, true, false);
		// On récupère le gestionnaire d'affichage
		_piePlot = (PiePlot3D)pie_chart.getPlot();
		// On positionne les caractéristiques d'affichage
		_piePlot.setStartAngle(0);
		_piePlot.setDirection(Rotation.ANTICLOCKWISE);
		_piePlot.setForegroundAlpha(0.5F);
		_piePlot.setLabelGenerator(null);
		_piePlot.setSectionPaint(0, Color.blue);
		_piePlot.setSectionPaint(1, Color.green);
		_piePlot.setBackgroundPaint(getContentPane().getBackground());
		// On récupère le panneau du graphique
		ChartPanel pie_panel = new ChartPanel(pie_chart);
		// On va positionner le panneau dans le panneau de charge
		load_constraints.gridx = 0;
		load_constraints.gridy = 0;
		load_constraints.weightx = 0.4;
		load_constraints.weighty = 1;
		load_constraints.anchor = GridBagConstraints.CENTER;
		load_constraints.fill = GridBagConstraints.BOTH;
		load_layout.setConstraints(pie_panel, load_constraints);
		load_panel.add(pie_panel);

		// On va créer le graphique temporel de charge
		// On commence par les données
		_loadSeries = new TimeSeries(
			MessageManager.getMessage("&XStatusProcessor_Load"), 
			new Second().getClass());
		TimeSeriesCollection load_collection = 
			new TimeSeriesCollection(_loadSeries);
		// On crée le graphique
		JFreeChart load_chart = ChartFactory.createTimeSeriesChart(null, null, 
			null, load_collection, true, true, false);
		// On positionne les paramètres d'affichage
		XYPlot load_plot = load_chart.getXYPlot();
		ValueAxis time_axis = load_plot.getDomainAxis();
		time_axis.setAutoRange(true);
		// On positionne l'échelle de l'axe temporel pour afficher 30 mn
		time_axis.setFixedAutoRange(1000 * 60 * 30);
		ValueAxis value_axis = load_plot.getRangeAxis();
		// On positionne l'échelle de l'axe des opérations de 0 à 100 (en pourcent) 
		value_axis.setRange(0, 100);
		// On récupère le panneau du graphique
		ChartPanel load_chart_panel = new ChartPanel(load_chart);
		// On va positionner le panneau dans le panneau de charge
		load_constraints.gridx = 1;
		load_constraints.weightx = 0.6;
		load_layout.setConstraints(load_chart_panel, load_constraints);
		load_panel.add(load_chart_panel);
		// On va placer le panneau de charge dans une bordure
		load_panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&XStatusProcessor_Operations")));

		// On va créer le graphique temporel des sessions et des environnements
		// On commence par les données
		_sessionsSeries = new TimeSeries(
			MessageManager.getMessage("&XStatusProcessor_Sessions"), 
			new Second().getClass());
		TimeSeriesCollection sessions_collection = 
			new TimeSeriesCollection(_sessionsSeries);
		_environmentsSeries = new TimeSeries(
			MessageManager.getMessage("&XStatusProcessor_Environments"), 
			new Second().getClass());
		TimeSeriesCollection environments_collection =
			new TimeSeriesCollection(_environmentsSeries);
		// On crée le graphique
		JFreeChart sessions_chart = 
			ChartFactory.createTimeSeriesChart(null, null, 
			MessageManager.getMessage("&XStatusProcessor_Sessions"), 
			sessions_collection, true, true, false);
		// On positionne les paramètres d'affichage
		XYPlot sessions_plot = sessions_chart.getXYPlot();
		time_axis = sessions_plot.getDomainAxis();
		time_axis.setAutoRange(true);
		// On positionne l'échelle de l'axe temporel pour afficher 30 mn
		time_axis.setFixedAutoRange(1000 * 60 * 30);
		// On positionne la source du deuxième graphique (environnements)
		sessions_plot.setDataset(1, environments_collection);
		// On récupère l'axe des sessions
		_sessionsAxis = (NumberAxis)sessions_plot.getRangeAxis();
		// On positionne l'échelle de l'axe des sessions de 0 à 5 
		_sessionsAxis.setRange(0, 5);
		// On crée l'axe des environnements
		_environmentsAxis = new NumberAxis(
			MessageManager.getMessage("&XStatusProcessor_Environments"));
		// On positionne l'échelle de l'axe des sessions de 0 à 5 
		_environmentsAxis.setRange(0, 5);
		// On positionne le gestionnaire d'affichage sur la deuxième série
		// des données
		sessions_plot.setRenderer(1, new DefaultXYItemRenderer());
		sessions_plot.setRangeAxis(1, _environmentsAxis);
		sessions_plot.mapDatasetToRangeAxis(1, 1);
		// On récupère le panneau du graphique
		ChartPanel sessions_chart_panel = new ChartPanel(sessions_chart);
		// On va positionner le panneau dans le panneau des sessions
		JPanel sessions_panel = new JPanel(new BorderLayout());
		sessions_panel.add(sessions_chart_panel, BorderLayout.CENTER);
		// On met une bordure au panneau
		sessions_panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), 
			MessageManager.getMessage(
			"&XStatusProcessor_SessionsEnvironements")));
		
		// On va créer un Layout Manager pour le panneau de la fenêtre
		GridBagLayout frame_layout = new GridBagLayout();
		GridBagConstraints frame_constraints = new GridBagConstraints();
		getContentPane().setLayout(frame_layout);
		// On positionne les premières contraintes
		frame_constraints.gridx = 0;
		frame_constraints.gridy = 0;
		frame_constraints.weightx = 1;
		frame_constraints.weighty = 0.4;
		frame_constraints.anchor = GridBagConstraints.CENTER;
		frame_constraints.fill = GridBagConstraints.BOTH;
		// On va positionner la panneau de charge en premier
		frame_layout.setConstraints(load_panel, frame_constraints);
		getContentPane().add(load_panel);
		// On positionne les contraintes pour le panneau des sessions
		frame_constraints.gridy ++;
		frame_constraints.weighty = 0.6;
		frame_layout.setConstraints(sessions_panel, frame_constraints);
		getContentPane().add(sessions_panel);
		// Il ne reste plus qu'à créer le bouton Fermer
		// On ajoute un bouton pour permettre la fermeture de la sous-fenêtre
		JButton close_button = new JButton(
			MessageManager.getMessage("&XStatusProcessor_Close"));
		// On ajoute le listener sur le click
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode close
				close();
			}
		});
		// On positionne les contraintes et on l'ajoute au panneau
		frame_constraints.gridy ++;
		frame_constraints.weighty = 0;
		frame_constraints.anchor = GridBagConstraints.CENTER;
		frame_constraints.fill = GridBagConstraints.NONE;
		frame_layout.setConstraints(close_button, frame_constraints);
		getContentPane().add(close_button);
		trace_methods.endOfMethod();
	}
}
