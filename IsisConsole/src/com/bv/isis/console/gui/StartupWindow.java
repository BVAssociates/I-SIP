/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/StartupWindow.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Fenêtre de lancement de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: StartupWindow.java,v $
* Revision 1.9  2008/03/03 12:09:01  tz
* Plus de logo BV dans la fenêtre de démarrage.
*
* Revision 1.8  2005/07/01 12:24:08  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/10/22 15:42:40  tz
* Fond blanc.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2002/12/26 12:56:52  tz
* Remplacement libellé I-SIS par icône
*
* Revision 1.3  2002/09/20 10:39:58  tz
* Utilisation du nom commercial I-SIS
* Modification de la fenêtre de démarrage
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture itération IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
// Imports système
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
* Cette classe correspond à la fenêtre d'accueil qui est affichée au
* début de l'exécution de l'application.
* Son intérêt fonctionnel est nul, mais, par contre, elle présente un
* intérêt esthétique et permet à l'utilisateur de savoir que l'application
* est bien en train de démarrer.
* ----------------------------------------------------------*/
public class StartupWindow
	extends JWindow
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: StartupWindow
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. C'est le
	* seul constructeur défini pour cette classe.
	* Elle appelle la méthode makePanel afin de construire la fenêtre qui
	* sera affichée à l'utilisateur.
	* ----------------------------------------------------------*/
	public StartupWindow()
	{
		int x_position;
		int y_position;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"StartupWindow", "StartupWindow");

		trace_methods.beginningOfMethod();
		// Création de la fenêtre
		makePanel();
		// Retaillage de la fenêtre par rapport au contenu
		pack();
		// Récupération de la taille de l'écran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// Récupération de la taille de la fenêtre
		Dimension window_size = getSize();
		// Calcul des coordonnées de la fenêtre
		x_position = (screen_size.width - window_size.width) / 2;
		y_position = (screen_size.height - window_size.height) / 2;
		// Déplacement de la fenêtre
		setLocation(x_position, y_position);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est utilisée pour instancier et ajouter tous les
	* objets graphiques qui font partie de la fenêtre d'accueil. Elle est
	* appelée par le constructeur de la classe.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"StartupWindow", "makePanel");

		trace_methods.beginningOfMethod();
        // Récupération de la police originale
        Font normal_font = new JLabel().getFont();
        // Création de la police plus large + gras
        Font bold_font = normal_font.deriveFont(Font.BOLD, normal_font.getSize() + 4);
        // Création de la police plus petite
        Font small_font = normal_font.deriveFont(Font.ITALIC,
			normal_font.getSize() - 3);
		// Création de la police italique
		Font bold_italic_font = bold_font.deriveFont(Font.ITALIC);

		// Création du layout
		getContentPane().setBackground(Color.white);
		getContentPane().setLayout(new BorderLayout());
		// Création de l'image
		JLabel icon_label = new JLabel(IconLoader.getIcon("Welcome"));
		icon_label.setBorder(BorderFactory.createEmptyBorder(2, 2, 20, 2));
		getContentPane().add(icon_label, BorderLayout.CENTER);
		// Création de la zone de Copyright
		JLabel copyright_label =
			   new JLabel(MessageManager.getMessage("&AD_Copyright"));
		copyright_label.setFont(small_font);
		copyright_label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		copyright_label.setHorizontalAlignment(JLabel.CENTER);
		getContentPane().add(copyright_label, BorderLayout.SOUTH);

		// Création du paneau contenant les labels
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
		// Création du label de la société
		constraints.gridx++;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		// Création du label de version
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