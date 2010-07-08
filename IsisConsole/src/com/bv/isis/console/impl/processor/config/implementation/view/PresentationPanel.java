/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/PresentationPanel.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau de pr�sentation 
* DATE:        10/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.2  2008/06/10 fc
* Ecriture des commentaires, re-positionnement des �l�ments 
* graphiques
* 
* Revision 1.3  2008/06/20 fc
* Modification du tableau de ModelInterface en 
* ArrayList<ModelInterface>

*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

//
// Imports syst�me
//
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;

/*------------------------------------------------------------
 * Nom: PresentationPanel
 * 
 * Description: 
 * Cette classe mod�lise le premier panneau.
 * Il s'agit ici d'afficher une pr�sentation de l'assistant.
 * ------------------------------------------------------------*/
public class PresentationPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: PresentationPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques a afficher dans le panneau.	
	 * ------------------------------------------------------------*/
	public PresentationPanel(WindowInterface mainWindow) {
		super();
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "PresentationPanel");
		trace_methods.beginningOfMethod();
				
		_window = mainWindow; 
		// Cr�ation de l'interface graphique
		makePanel();
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
	 * Nom: beforeDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Param�tres :
 	 *  - tabModels : Le tableau de ModelInterface.
 	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);
		trace_methods.endOfMethod();
	}
		
	/*------------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterDisplay(){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "afterDisplay");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}
		
	/*------------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette m�thode d�finit le comportement du panneau avant d'�tre cach�.
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit� car aucune saisie
	 * n'est � contr�ler.
	 * Retourne : vrai (true)
	 * ------------------------------------------------------------*/
	public boolean beforeHide(){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "beforeHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
			
		return true;
	}
	
	/*------------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterHide(){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}
		
	/*------------------------------------------------------------
	 * Nom: end
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean end(){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "end");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}
		
	/*------------------------------------------------------------
	 * Nom: update
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit� car aucune saisie 
	 * n'est � sauvegarder.
	 * 
	 * Param�tre : 
	 *  - tabModels : Le mod�le de donn�es avant modification.
	 * 
	 * Retourne : Le mod�le de donn�es non modifi�es
	 * ------------------------------------------------------------*/
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);
		trace_methods.endOfMethod();
		
		return tabModels;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _firstDescriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilis� pour �crire un phrase descriptive
	 * (d�but de la phrase).
	 * ------------------------------------------------------------*/
	private JLabel  _firstDescriptionSentence;
	
	/*------------------------------------------------------------
	 * Nom : _secondDescriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilis� pour �crire un phrase descriptive
	 * (fin de la phrase).
	 * ------------------------------------------------------------*/
	private JLabel  _secondDescriptionSentence;

	/*------------------------------------------------------------
	 * Nom : _thirdDescriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilis� pour �crire un phrase descriptive.
	 * ------------------------------------------------------------*/
	private JLabel  _thirdDescriptionSentence;

	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel� lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau 
	 * et de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel(){
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PresentationPanel", "makePanel");
		trace_methods.beginningOfMethod();
		
		_firstDescriptionSentence = new JLabel();
		_firstDescriptionSentence.setMinimumSize(new Dimension(490, 20));
		_firstDescriptionSentence.setMaximumSize(new Dimension(490, 20));
		_firstDescriptionSentence.setText(
			MessageManager.getMessage("&ComponentConfiguration_Welcome"));
		
		_secondDescriptionSentence = new JLabel();
		_secondDescriptionSentence.setMinimumSize(new Dimension(490, 20));
		_secondDescriptionSentence.setMaximumSize(new Dimension(490, 20));
		_secondDescriptionSentence.setText(
			MessageManager.getMessage("&ComponentConfiguration_ClickNext1"));
		
		_thirdDescriptionSentence = new JLabel();
		_thirdDescriptionSentence.setMinimumSize(new Dimension(490, 20));
		_thirdDescriptionSentence.setMaximumSize(new Dimension(490, 20));
		_thirdDescriptionSentence.setText(
			MessageManager.getMessage("&ComponentConfiguration_ClickNext2"));
		
		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(490, 440));
		vBox1.setMaximumSize(new Dimension(490, 440));
		vBox1.setPreferredSize(new Dimension(490, 440));
		vBox1.add(Box.createVerticalStrut(26));
		vBox1.add(_firstDescriptionSentence);
		vBox1.add(Box.createVerticalStrut(44));
		vBox1.add(_secondDescriptionSentence);
		vBox1.add(Box.createVerticalStrut(10));
		vBox1.add(_thirdDescriptionSentence);
		
		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new Dimension(490, 440));
		hBox1.setMaximumSize(new Dimension(490, 440));
		hBox1.setPreferredSize(new Dimension(490, 440));
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(vBox1);
		hBox1.add(Box.createHorizontalStrut(10));

		setLayout(new BorderLayout());
		add(hBox1,BorderLayout.NORTH);
		
		trace_methods.endOfMethod();
	}
}		

