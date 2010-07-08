/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/SummaryPanel.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau résumé de l'assistant
* DATE:        10/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.2  2008/06/24 17:00:00  fcd
* Ajout des champs Responsabilities et ExecutiveUser
* 
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

//
//Imports système
//
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentConfig;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentLog;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentManagementAction;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentStates;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransitionGroups;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentVariable;

/*------------------------------------------------------------
 * Nom: SummaryPanel
 * 
 * Description:
 * Cette classe modélise le dernier panneau de l'assistant 
 * de paramétrage servant de récapitulatif pour les données
 * rentrées.
 * ------------------------------------------------------------*/
public class SummaryPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: SummaryPanel
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ------------------------------------------------------------*/
	public SummaryPanel(WindowInterface mainWindow) {
		super();
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "SummryPanel");
		trace_methods.beginningOfMethod();
		
		_window = mainWindow;
		// Création de l'interface graphique
		makePanel();
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
	 * Nom: beforeDisplay
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle est appelée par la classe AbstractWindow lorsque le panneau doit être
	 * affiché. A partir du modèle de données passé en paramètre, elle se charge
	 * de pré-renseigner le tableau récapitulatif.
	 * 
	 * Paramètres :
	 *  - tabModels : Le tableau de ModelInterface.
	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);
		
		if (modele != null) {
			_styledDocument = _checklist.getStyledDocument();
			addStylesToDocument(_styledDocument);

			try {
				_styledDocument.remove(0, _styledDocument.getLength());
				
				_styledDocument.insertString(0, 
						MessageManager.getMessage(
						"&ComponentConfiguration_Summary") + 
						System.getProperty("line.separator", "\n")+
						System.getProperty("line.separator", "\n"), 
						_checklist.getStyle("bold"));
				
				ArrayList<ModelInterface> tableToElementsToAdd = new ArrayList<ModelInterface>();
				ArrayList<ModelInterface> tableToElementsToDel = new ArrayList<ModelInterface>();
				ArrayList<ModelInterface> tableToElementsToUpd = new ArrayList<ModelInterface>();
				
				for (int count = 0 ; count < 6 ; count++) {
					
					tableToElementsToAdd.clear();
					tableToElementsToDel.clear();
					tableToElementsToUpd.clear();
					
					for (int index = 0 ; index < modele.size() ; index ++) {
					
						if (count == 0 && modele.get(index) instanceof ComponentVariable ) {
							ComponentVariable c = (ComponentVariable) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}
						else if (count == 1 && modele.get(index) instanceof ComponentStates ) {
							ComponentStates c = (ComponentStates) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}else if (count == 2 && modele.get(index) instanceof ComponentTransitionGroups ) {
							ComponentTransitionGroups c = (ComponentTransitionGroups) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}else if (count == 3 && modele.get(index) instanceof ComponentManagementAction ) {
							ComponentManagementAction c = (ComponentManagementAction) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}else if (count == 4 && modele.get(index) instanceof ComponentConfig ) {
							ComponentConfig c = (ComponentConfig) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}else if (count == 5 && modele.get(index) instanceof ComponentLog ) {
							ComponentLog c = (ComponentLog) modele.get(index);
							if (c.getFlag() == 'A') {
								tableToElementsToAdd.add(c);
							}else if (c.getFlag() == 'M') {
								tableToElementsToUpd.add(c);
							}else if (c.getFlag() == 'S') {
								tableToElementsToDel.add(c);
							}
						}
					}
					
					if (count == 0) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_VariablesSummary") + 
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					} else if (count == 1) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_StatesSummary") + 
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					} else if (count == 2) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_TransitionsSummary") +
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					} else if (count == 3) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_ActionsSummary") +
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					} else if (count == 4) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_ConfigurationSummary") +
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					} else if (count == 5) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_LogSummary") +
							System.getProperty("line.separator", "\n"), 
							_checklist.getStyle("regular"));
					}
					
					if(tableToElementsToAdd.size() > 0) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_ToBeAdded") + 
							System.getProperty("line.separator", "\n"),
							_checklist.getStyle("italic"));
							
						for (int index =  0 ; index < tableToElementsToAdd.size() ; index ++) {
							tableToElementsToAdd.get(index).display(_styledDocument, _styledDocument.getLength());							
						}
					}
					
					if(tableToElementsToUpd.size() > 0) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_ToBeModified") + 
							System.getProperty("line.separator", "\n"),
							_checklist.getStyle("italic"));
							
						for (int index =  0 ; index < tableToElementsToUpd.size() ; index ++) {
							tableToElementsToUpd.get(index).display(_styledDocument, _styledDocument.getLength());							
						}
					}
					
					if(tableToElementsToDel.size() > 0) {
						_styledDocument.insertString(_styledDocument.getLength(), 
							MessageManager.getMessage(
							"&ComponentConfiguration_ToBeRemoved") + 
							System.getProperty("line.separator", "\n"),
							_checklist.getStyle("italic"));
	
						for (int index =  0 ; index < tableToElementsToDel.size() ; index ++) {
							tableToElementsToDel.get(index).display(_styledDocument, _styledDocument.getLength());							
						}
					}
				}
			}
			catch (Exception e) {
				// On ne fait rien ?
			}
			// On recharge le document dans la liste récapitulative
			_checklist.setDocument(_styledDocument);
			//_styledDocument.setParagraphAttributes(0, 20, null, false);
		}	
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterDisplay() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "afterDisplay");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette méthode définit le comportement du panneau avant d'être caché.
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle n'est présentée que pour des raisons de lisibilité car aucune saisie
	 * n'est à contrôler.
	 * 
	 * Retourne : vrai (true)
	 * ------------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "beforeHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: afterHide
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Cette méthode définit le comportement du panneau après avoir été caché.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: end
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Cette méthode est appelée lors de la destruction de l'assistant. Elle est 
	 * utiliser pour libèrer l'espace mémoire utilisé par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "end");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: updateModel
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle n'est présentée que pour des raisons de lisibilité car aucune saisie 
	 * n'est à sauvegarder.
	 * 
	 * Paramètre : 
	 *  - tabModels : Le modèle de données avant modification.
	 *  
	 * Retourne : Le modèle de données non modifiées
	 * ------------------------------------------------------------*/
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);
		trace_methods.endOfMethod();
		
		return tabModels;
	}

	// ****************** PROTEGE *********************
	protected void addStylesToDocument(StyledDocument doc) {
        //Initialisation de styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
    }
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _firstDescriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilisé pour écrire un phrase descriptive
	 * au dessus du récapitulaif (début de la phrase).
	 * ------------------------------------------------------------*/	
	private JLabel _firstDescriptionSentence;

	/*------------------------------------------------------------
	 * Nom : _secondDescriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilisé pour écrire un phrase descriptive
	 * au dessus du récapitulaif (fin de la phrase).
	 * ------------------------------------------------------------*/
	private JLabel _secondDescriptionSentence;
	
	/*------------------------------------------------------------
	 * Nom : _scrollPaneChecklist
	 * 
	 * Description :
	 * Cette attribut permet d'intégrer un défilement dans la 
	 * liste récapitulative.
	 * ------------------------------------------------------------*/
	private JScrollPane _scrollPaneChecklist;
	
	/*------------------------------------------------------------
	 * Nom : _checklist
	 * 
	 * Description :
	 * Cette attribut représente la liste récapitulative où seront
	 * affichées toutes les informations saisies dans les précédents
	 * écrans
	 * ------------------------------------------------------------*/
	private JTextPane _checklist;
	
	/*------------------------------------------------------------
	 * Nom : _styledDocument
	 * 
	 * Description :
	 * Cette attribut contient le texte affiché dans le JTextPane
	 * ------------------------------------------------------------*/
	private StyledDocument _styledDocument ;
	
	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette méthode est appelé lors de la construction du panneau.
	 * Son rôle est d'instancier les composants graphiques du panneau et 
	 * de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SummaryPanel", "makePanel");
		trace_methods.beginningOfMethod();
		
		_firstDescriptionSentence = new JLabel();
		_firstDescriptionSentence.setMinimumSize(new Dimension(450, 20));
		_firstDescriptionSentence.setMaximumSize(new Dimension(450, 20));
		_firstDescriptionSentence.setText(MessageManager
				.getMessage("&ComponentConfiguration_FirstDesciptionSentenceSummary"));
		
		_secondDescriptionSentence = new JLabel();
		_secondDescriptionSentence.setMinimumSize(new Dimension(450, 20));
		_secondDescriptionSentence.setMaximumSize(new Dimension(450, 20));
		_secondDescriptionSentence.setText(MessageManager
				.getMessage("&ComponentConfiguration_SecondDesciptionSentenceSummary"));
		
	    _checklist = new JTextPane();
		_checklist.setEditable(false);
	    _styledDocument = new DefaultStyledDocument();
		
		Style racine = StyleContext.
	    getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	    
	    Style italique = _checklist.addStyle("Italique", racine);
	    StyleConstants.setItalic(italique, true);

	    Style gras = _checklist.addStyle("Gras", racine);
	    StyleConstants.setBold(gras, true);
	    
	    _scrollPaneChecklist = new JScrollPane(_checklist);
	    _scrollPaneChecklist.setName("scrollPane");
	
	    
	    Box hBox1 = Box.createHorizontalBox();
	    hBox1.setMinimumSize(new Dimension(490, 20));
		hBox1.setMaximumSize(new Dimension(490, 20));
		hBox1.add(Box.createHorizontalStrut(5));
		hBox1.add(_firstDescriptionSentence);
		hBox1.add(Box.createHorizontalStrut(5));
		
		Box hBox2 = Box.createHorizontalBox();
	    hBox2.setMinimumSize(new Dimension(490, 20));
		hBox2.setMaximumSize(new Dimension(490, 20));
		hBox2.add(Box.createHorizontalStrut(5));
		hBox2.add(_secondDescriptionSentence);
		hBox2.add(Box.createHorizontalStrut(5));
	    
		Box hBox3 = Box.createHorizontalBox();
	    hBox3.setMinimumSize(new Dimension(490, 338));
		hBox3.setMaximumSize(new Dimension(490, 338));
		hBox3.add(Box.createHorizontalStrut(5));
		hBox3.add(_scrollPaneChecklist);
		hBox3.add(Box.createHorizontalStrut(5));
	    
	    Box vBox1 = Box.createVerticalBox();
	    vBox1.setMinimumSize(new Dimension(485, 440));
		vBox1.setMaximumSize(new Dimension(485, 440));
		vBox1.setPreferredSize(new Dimension(485, 440));
		vBox1.add(Box.createVerticalStrut(13));
		vBox1.add(hBox1);
		vBox1.add(Box.createVerticalStrut(5));
		vBox1.add(hBox2);
		vBox1.add(Box.createVerticalStrut(10));
		vBox1.add(hBox3);
		vBox1.add(Box.createVerticalStrut(10));
		
		setLayout(new BorderLayout());
		add(vBox1, BorderLayout.NORTH);
		
		trace_methods.endOfMethod();
	}
}
