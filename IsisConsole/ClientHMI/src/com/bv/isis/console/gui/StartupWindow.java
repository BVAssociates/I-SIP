/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/StartupWindow.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Fen�tre de lancement de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: StartupWindow.java,v $
* Revision 1.9  2008/03/03 12:09:01  tz
* Plus de logo BV dans la fen�tre de d�marrage.
*
* Revision 1.8  2005/07/01 12:24:08  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/10/22 15:42:40  tz
* Fond blanc.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.5  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2002/12/26 12:56:52  tz
* Remplacement libell� I-SIS par ic�ne
*
* Revision 1.3  2002/09/20 10:39:58  tz
* Utilisation du nom commercial I-SIS
* Modification de la fen�tre de d�marrage
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.gui;

//
// Imports syst�me
//
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import javax.swing.JWindow;
import javax.swing.JLabel;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.gui.IconLoader;
import com.bv.core.message.MessageManager;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.BorderFactory;
import java.awt.Color;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: StartupWindow
*
* Description:
* Cette classe correspond � la fen�tre d'accueil qui est affich�e au
* d�but de l'ex�cution de l'application.
* Son int�r�t fonctionnel est nul, mais, par contre, elle pr�sente un
* int�r�t esth�tique et permet � l'utilisateur de savoir que l'application
* est bien en train de d�marrer.
* ----------------------------------------------------------*/
public class StartupWindow
	extends JWindow
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: StartupWindow
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. C'est le
	* seul constructeur d�fini pour cette classe.
	* Elle appelle la m�thode makePanel afin de construire la fen�tre qui
	* sera affich�e � l'utilisateur.
	* ----------------------------------------------------------*/
	public StartupWindow()
	{
		int x_position;
		int y_position;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"StartupWindow", "StartupWindow");

		trace_methods.beginningOfMethod();
		// Cr�ation de la fen�tre
		makePanel();
		// Retaillage de la fen�tre par rapport au contenu
		pack();
		// R�cup�ration de la taille de l'�cran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// R�cup�ration de la taille de la fen�tre
		Dimension window_size = getSize();
		// Calcul des coordonn�es de la fen�tre
		x_position = (screen_size.width - window_size.width) / 2;
		y_position = (screen_size.height - window_size.height) / 2;
		// D�placement de la fen�tre
		setLocation(x_position, y_position);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode est utilis�e pour instancier et ajouter tous les
	* objets graphiques qui font partie de la fen�tre d'accueil. Elle est
	* appel�e par le constructeur de la classe.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"StartupWindow", "makePanel");

		trace_methods.beginningOfMethod();
        // R�cup�ration de la police originale
        Font normal_font = new JLabel().getFont();
        // Cr�ation de la police plus large + gras
        Font bold_font = normal_font.deriveFont(Font.BOLD, normal_font.getSize() + 4);
        // Cr�ation de la police plus petite
        Font small_font = normal_font.deriveFont(Font.ITALIC,
			normal_font.getSize() - 3);
		// Cr�ation de la police italique
		Font bold_italic_font = bold_font.deriveFont(Font.ITALIC);

		// Cr�ation du layout
		getContentPane().setBackground(Color.white);
		getContentPane().setLayout(new BorderLayout());
		// Cr�ation de l'image
		JLabel icon_label = new JLabel(IconLoader.getIcon("Welcome"));
		icon_label.setBorder(BorderFactory.createEmptyBorder(2, 2, 20, 2));
		getContentPane().add(icon_label, BorderLayout.CENTER);
		// Cr�ation de la zone de Copyright
		JLabel copyright_label =
			   new JLabel(MessageManager.getMessage("&AD_Copyright"));
		copyright_label.setFont(small_font);
		copyright_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		copyright_label.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(copyright_label, BorderLayout.SOUTH);

		// Cr�ation du paneau contenant les labels
		JPanel label_panel = new JPanel();
		label_panel.setBackground(Color.white);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		label_panel.setLayout(layout);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		JLabel product_label = new JLabel(
			   MessageManager.getMessage("&AD_ProductName") + " " +
			   MessageManager.getMessage("&AD_ApplicationName"));
		product_label.setFont(bold_font);
		product_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		product_label.setHorizontalAlignment(JLabel.CENTER);
		layout.setConstraints(product_label, constraints);
		label_panel.add(product_label);
		// Zone vide
		constraints.gridx++;
		constraints.fill = GridBagConstraints.BOTH;
		JLabel company_label = new JLabel("                      ");
		company_label.setFont(bold_italic_font);
		company_label.setHorizontalAlignment(JLabel.CENTER);
		layout.setConstraints(company_label, constraints);
		label_panel.add(company_label);
		// Cr�ation du label de la soci�t�
		constraints.gridx++;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		// Cr�ation du label de version
		JLabel release_label = new JLabel(
			   MessageManager.getMessage("&AD_Version"));
		release_label.setHorizontalAlignment(JLabel.CENTER);
		release_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
		layout.setConstraints(release_label, constraints);
		label_panel.add(release_label);
		// Ajout du paneau des labels
		label_panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 2));
		getContentPane().add(label_panel, BorderLayout.NORTH);
		// Ajout d'une bordure au paneau
		((JPanel)getContentPane()).setBorder(BorderFactory.createRaisedBevelBorder());
		trace_methods.endOfMethod();
	}
}