package com.bv.isis.console.impl.processor.appcfg;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationDocPanel;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationGroupTransitionsPanel;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationPresentationPanel;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationServicesPanel;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationStatesPanel;
import com.bv.isis.console.impl.processor.appcfg.view.ApplicationSummaryPanel;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractWindow;
import com.bv.isis.console.impl.processor.config.framework.view.PanelInterface;

public class ApplicationConfigurationProcessor extends AbstractWindow {

	public ApplicationConfigurationProcessor() {
	}
	
	public void run(
			MainWindowInterface windowInterface,
			JMenuItem menuItem,
			String parameters,
			String preprocessing,
			String postprocessing,
			DefaultMutableTreeNode selectedNode
			) throws
			InnerException
	{
		
		super.run(windowInterface, menuItem, parameters,
				preprocessing, postprocessing, selectedNode);
		
		// On construit la sous fenêtre de l'assistant
		makePanel();
		
		// On définit la liste des panneaux à afficher et leurs ordres
		// d'apparition
		_panelTable = new PanelInterface [6];
		//_panelTable[0] = new ApplicationPresentationPanel(this);
		_panelTable[0] = new ApplicationPresentationPanel(this);
		_panelTable[1] = new ApplicationServicesPanel(this);
		_panelTable[2] = new ApplicationStatesPanel(this);
		_panelTable[3] = new ApplicationGroupTransitionsPanel(this);
		_panelTable[4] = new ApplicationDocPanel(this);
		_panelTable[5] = new ApplicationSummaryPanel(this);
		
		// Une fois le modèle chargé, on peut afficher le premier écran
		displayFirstPanel();
		// On affiche l'assistant
		display();
		
	}
	
	public void preLoad() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "preLoad");
		
		// Chargement du fichier de messages
		//MessageManager.loadFile("componentwizard.mdb", "UTF8");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();		
	}
	
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	
		return "Test"; //MessageManager.getMessage("&ComponentConfigurationProcessorDescription");
	}

	public ProcessorInterface duplicate() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	
		return new ApplicationConfigurationProcessor();
	}

	/*------------------------------------------------------------
	 * Nom: close
	 * 
	 * Description:
	 * Cette méthode rédéfinit celle de la super classe. Elle est appelée lorsque
	 * la sous-fenêtre du processeur doit être fermée (par l'utilisateur ou par
	 * la fermeture de l'application).
	 * ------------------------------------------------------------*/
	public void close() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "close");
		trace_methods.beginningOfMethod();
		
		//freeAndReleaseUnusedMemoryBySettingVariablesNull();
		super.close();
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isGlobalCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return false;
	}
	
	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément de tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isTableCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return false;
	}
	
	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour récupérer l'intitulé de l'élément du menu "Outils" 
	* associé à ce processeur.
	* Ce processeur n'étant pas global, cette méthode ne sera pas appelée.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "getMenuLabel");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return "ApplicationConfiguration";
	}

	
	protected boolean saveModel() throws InnerException {

		return false;
	}
}
