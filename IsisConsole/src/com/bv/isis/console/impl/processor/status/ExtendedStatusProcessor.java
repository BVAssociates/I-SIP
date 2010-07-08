/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/status/ExtendedStatusProcessor.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'�tat �tendu d'un Agent
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.4  2006/03/07 09:33:06  tz
* Mise � jour du graphique toutes les 2 secondes, au lieu de toutes les 10 secondes.
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
// D�claration du package
package com.bv.isis.console.impl.processor.status;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'affichage de 
* certaines informations de l'�tat complet d'un Agent. Il s'agit d'afficher la 
* charge de l'Agent par le biais d'un graphique en camembert, ainsi qu'un 
* affichage temporel, et d'afficher le nombre de sessions ouvertes et le 
* nombre d'environnements de services en cache par un graphique temporel.
* Le processeur �tant un processeur graphique, cette classe sp�cialise la 
* classe ProcessorFrame. Elle impl�mente l'interface ActionListener afin de 
* permettre la r�cup�ration p�riodique des donn�es d'�tat de l'Agent.
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
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e afin d'effectuer le pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour d�clencher l'ex�cution du processeur.
	* Le panneau d'affichage de l'�tat d�taill� est construit par un appel � 
	* la m�thode makePanel(), puis la fen�tre est affich�e.
	* 
	* Si un probl�me est d�tect� lors de l'ex�cution du processeur, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - menuItem: Une r�f�rence sur l'�l�ment de menu par lequel le 
	*    processeur a �t� lanc�,
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
		// Si le noeud s�lectionn� est nul, c'est une erreur
		if(selectedNode == null || windowInterface == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud s�lectionn� !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On appelle la m�thode de la super classe
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
		// On va construire la fen�tre
		windowInterface.setProgressMaximum(1);
		windowInterface.setStatus("&StatusProcessor_BuildingFrame", null, 0);
		makePanel();
		// On positionne les dimensions du panneau
		setMinimumSize(new Dimension(200, 150));
		setPreferredSize(new Dimension(400, 300));
		// On va cr�er le timer
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
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e 
	* lorsque la fen�tre du processeur doit �tre ferm�e.
	* Le processeur arr�te le timer de r�cup�ration des donn�es d'�tat, puis 
	* appelle la m�thode de la super-classe.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "close");
			
		trace_methods.beginningOfMethod();
		// On arr�te le timer
		_timer.stop();
		// On lib�re les ressources
		_timer = null;
		_agentName = null;
		_piePlot = null;
		_loadSeries = null;
		_sessionsSeries = null;
		_sessionsAxis = null;
		_environmentsSeries = null;
		_environmentsAxis = null;
		// On appelle la m�thode de la super-classe
		super.close();
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
			"ExtendedStatusProcessor", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&XStatusProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode red�finit celle de l'interface ActionListener. Elle est 
	* automatiquement appel�e par le Timer � la fin de chaque p�riode.
	* La m�thode r�cup�re l'interface de l'Agent concern�, via la m�thode 
	* getAgentInterface() de la classe PortalInterfaceProxy, appelle la m�thode 
	* getStatus() sur cette interface, puis positionne les donn�es sur les 
	* diff�rents mod�les de donn�es des graphiques.
	* 
	* Arguments:
	*  - event: Non utilis�.
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
		// On va commencer par r�cup�rer l'�tat de l'Agent
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
				CommonFeatures.processException("la r�cup�ration de l'�tat " +
				"de l'Agent", exception);
			// On va afficher un message d'erreur � l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotGetAgentXStatus", inner_exception);
			// On ferme la fen�tre et on sort
			close();
			trace_methods.endOfMethod();
			return;
		}
		// La premi�re chose � faire est de positionner la valeur de
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
		// Si la charge est sup�rieure � 75%, la couleur de la premi�re portion
		// du camembert est rouge, sinon elle est verte
		if(load > 75)
		{
			_piePlot.setSectionPaint(0, Color.red); 
		}
		else
		{
			_piePlot.setSectionPaint(0, Color.blue);
		}
		// On va mettre � jour le graphique temporel de la charge
		_loadSeries.add(new Second(), new Integer(load));
		// On va mettre � jour le graphique des sessions
		_sessionsSeries.add(new Second(),  
			new Integer(agent_status.activeSessions));
		sessions_max = _sessionsAxis.getUpperBound();
		if(sessions_max < (double)agent_status.activeSessions)
		{
			// On va changer l'�chelle des sessions
			_sessionsAxis.setUpperBound((double)agent_status.activeSessions);
		}
		// On va mettre � jour le graphique des environnements
		_environmentsSeries.add(new Second(),  
			new Integer(agent_status.iClesServices));
		environments_max = _environmentsAxis.getUpperBound();
		if(environments_max < (double)agent_status.iClesServices)
		{
			// On va changer l'�chelle des environnements
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
	* Cet attribut maintient le nom de l'Agent dont l'�tat doit �tre affich�.
	* ----------------------------------------------------------*/
	private String _agentName;

	/*----------------------------------------------------------
	* Nom: _timer
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet Timer permettant de 
	* r�cup�rer p�riodiquement les donn�es d'�tat de l'Agent et de mettre � 
	* jour les graphiques.
	* ----------------------------------------------------------*/
	private Timer _timer;

	/*----------------------------------------------------------
	* Nom: _piePlot
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet PiePlot3D correspondant 
	* au gestionnaire d'affichage du graphique en camembert. Cet attribut est 
	* n�cessaire pour modifier les couleurs d'affichage des portions du 
	* camembert, ainsi que pour acc�der au mod�le des donn�es du camembert.
	* ----------------------------------------------------------*/
	private PiePlot3D _piePlot;

	/*----------------------------------------------------------
	* Nom: _loadSeries
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet TimeSeries 
	* correspondant au mod�le des donn�es du graphique des �volutions de la 
	* charge de l'Agent. Il est n�cessaire afin d'enrichir le graphique avec 
	* de nouvelles donn�es.
	* ----------------------------------------------------------*/
	private TimeSeries _loadSeries;

	/*----------------------------------------------------------
	* Nom: _sessionsSeries
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet TimeSeries 
	* correspondant au mod�les des donn�es du graphique des �volutions des 
	* sessions actives de l'Agent. Il est n�cessaire afin d'enrichir le 
	* graphique avec de nouvelles donn�es.
	* ----------------------------------------------------------*/
	private TimeSeries _sessionsSeries;

	/*----------------------------------------------------------
	* Nom: _sessionsAxis
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NumberAxis 
	* correspondant au gestionnaire de l'axe des ordonn�es du graphique des 
	* sessions actives de l'Agent. Il est n�cessaire afin de faire �voluer la 
	* graduation en fonction de la valeur instantann�e des sessions actives.
	* ----------------------------------------------------------*/
	private NumberAxis _sessionsAxis;

	/*----------------------------------------------------------
	* Nom: _environmentsSeries
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet TimeSeries 
	* correspondant au mod�les des donn�es du graphique des �volutions du 
	* cache des environnements de service de l'Agent. Il est n�cessaire afin 
	* d'enrichir le graphique avec de nouvelles donn�es.
	* ----------------------------------------------------------*/
	private TimeSeries _environmentsSeries;

	/*----------------------------------------------------------
	* Nom: _environmentsAxis
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NumberAxis 
	* correspondant au gestionnaire de l'axe des ordonn�es du graphique du 
	* chache des environnements de service de l'Agent. Il est n�cessaire afin 
	* de faire �voluer la graduation en fonction de la valeur instantann�e de 
	* la taille du cache.
	* ----------------------------------------------------------*/
	private NumberAxis _environmentsAxis;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est charg�e de la construction de la fen�tre d'affichage 
	* de l'�tat �tendu d'un Agent. Pour cela, elle construit un graphique sous 
	* forme de camembert, charg� de r�pr�senter la charge instantann�e de 
	* l'Agent, un graphique temporel charg� de repr�senter l'�volution de la 
	* charge de l'Agent, ainsi qu'un dernier graphique temporel charg� de 
	* repr�senter les �volutions du nombre de sessions actives et du nombre 
	* d'environnements de service en cache.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExtendedStatusProcessor", "makePanel");
			
		trace_methods.beginningOfMethod();
		setTitle(MessageManager.getMessage("&XStatusProcessor_Title") +
			_agentName);
		// On cr�e le panneau de charge
		JPanel load_panel = new JPanel();
		GridBagLayout load_layout = new GridBagLayout();
		GridBagConstraints load_constraints = new GridBagConstraints();
		load_panel.setLayout(load_layout);
		// On va cr�er le camembert de charge
		// On commence par le mod�le des donn�es
		DefaultPieDataset pie_dataset = new DefaultPieDataset();
		pie_dataset.setValue(
			MessageManager.getMessage("&XStatusProcessor_OnGoing"), 
			new Integer(0));
		pie_dataset.setValue(
			MessageManager.getMessage("&XStatusProcessor_Remaining"), 
			new Integer(1));
		// On va cr�er le graphique en 3D
		JFreeChart pie_chart = ChartFactory.createPieChart3D(null, pie_dataset, 
			false, true, false);
		// On r�cup�re le gestionnaire d'affichage
		_piePlot = (PiePlot3D)pie_chart.getPlot();
		// On positionne les caract�ristiques d'affichage
		_piePlot.setStartAngle(0);
		_piePlot.setDirection(Rotation.ANTICLOCKWISE);
		_piePlot.setForegroundAlpha(0.5F);
		_piePlot.setLabelGenerator(null);
		_piePlot.setSectionPaint(0, Color.blue);
		_piePlot.setSectionPaint(1, Color.green);
		_piePlot.setBackgroundPaint(getContentPane().getBackground());
		// On r�cup�re le panneau du graphique
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

		// On va cr�er le graphique temporel de charge
		// On commence par les donn�es
		_loadSeries = new TimeSeries(
			MessageManager.getMessage("&XStatusProcessor_Load"), 
			new Second().getClass());
		TimeSeriesCollection load_collection = 
			new TimeSeriesCollection(_loadSeries);
		// On cr�e le graphique
		JFreeChart load_chart = ChartFactory.createTimeSeriesChart(null, null, 
			null, load_collection, true, true, false);
		// On positionne les param�tres d'affichage
		XYPlot load_plot = load_chart.getXYPlot();
		ValueAxis time_axis = load_plot.getDomainAxis();
		time_axis.setAutoRange(true);
		// On positionne l'�chelle de l'axe temporel pour afficher 30 mn
		time_axis.setFixedAutoRange(1000 * 60 * 30);
		ValueAxis value_axis = load_plot.getRangeAxis();
		// On positionne l'�chelle de l'axe des op�rations de 0 � 100 (en pourcent) 
		value_axis.setRange(0, 100);
		// On r�cup�re le panneau du graphique
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

		// On va cr�er le graphique temporel des sessions et des environnements
		// On commence par les donn�es
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
		// On cr�e le graphique
		JFreeChart sessions_chart = 
			ChartFactory.createTimeSeriesChart(null, null, 
			MessageManager.getMessage("&XStatusProcessor_Sessions"), 
			sessions_collection, true, true, false);
		// On positionne les param�tres d'affichage
		XYPlot sessions_plot = sessions_chart.getXYPlot();
		time_axis = sessions_plot.getDomainAxis();
		time_axis.setAutoRange(true);
		// On positionne l'�chelle de l'axe temporel pour afficher 30 mn
		time_axis.setFixedAutoRange(1000 * 60 * 30);
		// On positionne la source du deuxi�me graphique (environnements)
		sessions_plot.setDataset(1, environments_collection);
		// On r�cup�re l'axe des sessions
		_sessionsAxis = (NumberAxis)sessions_plot.getRangeAxis();
		// On positionne l'�chelle de l'axe des sessions de 0 � 5 
		_sessionsAxis.setRange(0, 5);
		// On cr�e l'axe des environnements
		_environmentsAxis = new NumberAxis(
			MessageManager.getMessage("&XStatusProcessor_Environments"));
		// On positionne l'�chelle de l'axe des sessions de 0 � 5 
		_environmentsAxis.setRange(0, 5);
		// On positionne le gestionnaire d'affichage sur la deuxi�me s�rie
		// des donn�es
		sessions_plot.setRenderer(1, new DefaultXYItemRenderer());
		sessions_plot.setRangeAxis(1, _environmentsAxis);
		sessions_plot.mapDatasetToRangeAxis(1, 1);
		// On r�cup�re le panneau du graphique
		ChartPanel sessions_chart_panel = new ChartPanel(sessions_chart);
		// On va positionner le panneau dans le panneau des sessions
		JPanel sessions_panel = new JPanel(new BorderLayout());
		sessions_panel.add(sessions_chart_panel, BorderLayout.CENTER);
		// On met une bordure au panneau
		sessions_panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), 
			MessageManager.getMessage(
			"&XStatusProcessor_SessionsEnvironements")));
		
		// On va cr�er un Layout Manager pour le panneau de la fen�tre
		GridBagLayout frame_layout = new GridBagLayout();
		GridBagConstraints frame_constraints = new GridBagConstraints();
		getContentPane().setLayout(frame_layout);
		// On positionne les premi�res contraintes
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
		// Il ne reste plus qu'� cr�er le bouton Fermer
		// On ajoute un bouton pour permettre la fermeture de la sous-fen�tre
		JButton close_button = new JButton(
			MessageManager.getMessage("&XStatusProcessor_Close"));
		// On ajoute le listener sur le click
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode close
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
