//------------------------------------------------------------
// Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
//------------------------------------------------------------
//
// FICHIER: Frame1.java
// VERSION: 1.0
//
//----------------------------------------------------------
// DESCRIPTION: ComposantDescriptionPanel 
// DATE: 4/06/08
// AUTEUR: Hicham Doghmi & Florent Cossard
// PROJET: All-In
// GROUPE: view
//----------------------------------------------------------

package com.bv.isis.console.impl.processor.config.implementation.view;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;


public class ComposantDescriptionPanel 
    extends AbstractPanel{
	
	// Attributs
	
	private static final long serialVersionUID = 1L;
	
	protected JLabel _text1;
	protected JLabel _name;
	protected JLabel _description;
	protected JLabel _ChampsObligatoires;
	
	protected JTextField _text3;
	protected JTextField _text2;
	

	
	//----------------------------------------------------------
	//
	// Nom: ComposantDescriptionPanel
	// Description: Constructeur
	//----------------------------------------------------------	
	public ComposantDescriptionPanel() {
		super();
		makePanel();
	}
	
	//----------------------------------------------------------	
	//	
	// Nom: makePanel
	
	// Description: 
	// Cette m�thode est appel� lors de la construction du panneau.
    // Son r�le est d'instancier les composants graphiques du panneau et de les
	// positionner.
	//----------------------------------------------------------	
	public void makePanel()
	{
		_name = new JLabel();
		_name.setMinimumSize(new Dimension(167, 20));
		_name.setMaximumSize(new Dimension(167, 20));
		_name.setPreferredSize(new Dimension(167, 20));
		_name.setText("Nom du composant :   *");
		
		_description = new JLabel();
		_description.setMinimumSize(new Dimension(167, 20));
		_description.setMaximumSize(new Dimension(167, 20));
		_description.setPreferredSize(new Dimension(167, 20));
		_description.setText("Description du composant : ");

		
		_ChampsObligatoires = new JLabel();
		_ChampsObligatoires.setMinimumSize(new java.awt.Dimension(200, 20));
		_ChampsObligatoires.setMaximumSize(new java.awt.Dimension(200, 20));
		_ChampsObligatoires.setText("* Champs obligatoires.");

		_text1 = new JLabel();
		_text1.setMinimumSize(new Dimension(290, 20));
		_text1.setMaximumSize(new Dimension(290, 20));
		_text1.setPreferredSize(new Dimension(290, 20));
		_text1.setText("Veuillez d�finir ci-dessous le composant:");
		
		_text3 = new JTextField(20);
		_text3.setMinimumSize(new Dimension(310, 20));
		_text3.setMaximumSize(new Dimension(310, 20));
		_text3.setPreferredSize(new Dimension(310, 20));
		
		_text2 = new JTextField(20);
		_text2.setMinimumSize(new Dimension(300, 20));
		_text2.setMaximumSize(new Dimension(300, 20));
		_text2.setPreferredSize(new Dimension(300, 20));
		
		
		Box hBox4 = Box.createHorizontalBox();
		hBox4.setMinimumSize(new java.awt.Dimension(450, 50));
		hBox4.setMaximumSize(new java.awt.Dimension(450, 50));
		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(_text1);
		
		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new java.awt.Dimension(450, 20));
		hBox1.setMaximumSize(new java.awt.Dimension(450, 20));
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_name);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_text3);
		hBox1.add(Box.createHorizontalStrut(10));
		
		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new java.awt.Dimension(450, 50));
		hBox2.setMaximumSize(new java.awt.Dimension(450, 50));
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(_description);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(_text2);
		hBox2.add(Box.createHorizontalStrut(10));
		
		Box hBox3 = Box.createHorizontalBox();
		hBox3.setMinimumSize(new java.awt.Dimension(450, 50));
		hBox3.setMaximumSize(new java.awt.Dimension(450, 50));
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(_ChampsObligatoires);
		
		Box vBox = Box.createVerticalBox();
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox4);
		vBox.add(Box.createVerticalStrut(30));
		vBox.add(hBox1);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox2);
		vBox.add(Box.createVerticalStrut(300));
		vBox.add(hBox3);
		
		setLayout(new BorderLayout());
		add(vBox,BorderLayout.CENTER);
	}

	//----------------------------------------------------------	
	//	
	// Nom: beforeDisplay
	
	// Description: 
	// Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
    // Elle n'est pr�sent�e que pour des raisons de lisibilit�.
    //
	// Param�tres :
	// - tabModels : Le tableau de ModelInterface.
	//
	//----------------------------------------------------------			
	public void beforeDisplay(ArrayList<ModelInterface> modele){

			}

	//----------------------------------------------------------	
	//	
	// Nom: afterDisplay
	
	// Description: 
    //	Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
    //  Elle n'est pr�sent�e que pour des raisons de lisibilit�.
    // 
	// Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	//----------------------------------------------------------	
	public boolean afterDisplay(){
			return true;
	}
	
	//----------------------------------------------------------	
	//	
	// Nom: beforeHide
	
	// Description: 
    //  Cette m�thode d�finit le comportement du panneau avant d'�tre cach�.
	//  Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	//  Elle n'est pr�sent�e que pour des raisons de lisibilit� car aucune saisie
	//  n'est � contr�ler.
	//
	//  Retourne : vrai (true)
	//----------------------------------------------------------	
	public boolean beforeHide(){
			return true;
	}
	
	//----------------------------------------------------------	
	//	
	// Nom: afterHIde
	
	// Description: 
    //  Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	//  Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	//  Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	//
	//  Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	//----------------------------------------------------------	
	public boolean afterHide(){
			return true;
	}
	
	//----------------------------------------------------------	
	//	
	// Nom: end
	
	// Description: 
    //  Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	//  Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est
	//  utiliser pour lib�rer l'espace m�moire utilis� par les variables des classes.
	//
	//  Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	//----------------------------------------------------------	
	public boolean end(){
			return true;
	}
		
	//----------------------------------------------------------	
	//	
	// Nom: update
	// Description: 
    //  Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	//  Elle n'est pr�sent�e que pour des raisons de lisibilit� car aucune saisie
	//  n'est � sauvegarder.
	//
	// Param�tre : 
	//  - tabModels : Le mod�le de donn�es avant modification.
	//
	// Retourne : Le mod�le de donn�es non modifi�es
	//----------------------------------------------------------	
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
			return tabModels;
	}
	
}
