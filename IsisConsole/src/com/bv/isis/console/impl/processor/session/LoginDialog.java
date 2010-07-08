/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/LoginDialog.java,v $
* $Revision: 1.19 $
*
* ------------------------------------------------------------
* DESCRIPTION: Boîte d'identification de l'utilisateur
* DATE:        14/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.session
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: LoginDialog.java,v $
* Revision 1.19  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.18  2009/01/14 09:50:44  tz
* Correction de la fiche FS#586.
*
* Revision 1.17  2006/03/09 13:38:57  tz
* Ajout d'une trace d'arguments.
*
* Revision 1.16  2005/10/07 08:23:43  tz
* Changement non fonctionnel
*
* Revision 1.15  2005/07/01 12:10:10  tz
* Modification du composant pour les traces
*
* Revision 1.14  2004/11/24 16:23:53  tz
* Attachement de la boîte de dialogue à la fenêtre principale (nouveau
* constructeur).
*
* Revision 1.13  2004/11/23 15:46:22  tz
* Adaptation pour corba-R1_1_2-AL-1_0.
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 13:55:26  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.10  2004/07/29 12:03:13  tz
* Mise à jour de la documentation
*
* Revision 1.9  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.8.2.1  2003/10/27 16:52:36  tz
* Support du domaine d'authentification
*
* Revision 1.8  2003/05/15 12:47:11  tz
* Correction des fiches Inuit/109 et Inuit/110.
*
* Revision 1.7  2002/08/13 13:01:29  tz
* Traitement des évènements KeyReleased et plus KeyTyped.
*
* Revision 1.6  2002/04/17 07:59:58  tz
* Correction problème de login
*
* Revision 1.5  2002/04/05 15:52:24  tz
* Cloture itération IT1.2
*
* Revision 1.4  2002/03/27 09:50:11  tz
* Déplacement depuis com.bv.inuit.console.gui
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture itération IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/14 17:16:57  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.session;

//
// Imports système
//
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Dimension;
import java.awt.Toolkit;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.IconLoader;
import com.bv.core.util.StringCODEC;
import java.awt.Component;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: LoginDialog
*
* Description:
* Cette classe représente la boîte de dialogue qui est affichée à l'utilisateur
* afin qu'il puisse s'identifier auprès du système I-SIS. Cette boîte de
* dialogue permet à l'utilisateur de sélectionner son identifiant parmi une
* liste, et de saisir son mot de passe. Ces informations seront utilisées pour
* ouvrir une session sur le processus Portail ou sur les Agents.
* ----------------------------------------------------------*/
class LoginDialog
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: LoginDialog
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Il est le seul
	* constructeur qui puisse être utilisé.
	* Elle appelle la méthode makePanel afin de construire la boîte de dialogue
	* qui sera affichée à l'utilisateur.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    représentant la fenêtre principale de l'application.
	* ----------------------------------------------------------*/
	public LoginDialog(
		MainWindowInterface windowInterface
		)
	{
		super((Frame)windowInterface, MessageManager.getMessage("&ID_Title"), 
			true);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "LoginDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		_validated = false;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		// Construction de la boîte d'identification
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLogin
	*
	* Description:
	* Cette méthode est appelée pour déclencher la procédure d'identification de
	* l'utilisateur.
	* Elle met à jour la liste des utilisateurs au niveau de la boîte de
	* dialogue, et éventuellement le mot de passe précédemment saisi. Si 
	* l'argument component est null, la boîte de dialogue sera centrée
	* par rapport à l'écran, sinon par rapport au composant. La boîte de dialogue
	* s'affiche à l'utilisateur. La méthode reste bloquée jusqu'à ce que la 
	* boîte de dialogue soit cachée (par la méthode validateLogin() ou
	* par la méthode cancelLogin()).
	*
	* Arguments:
	*  - usersNames: Un tableau de chaînes de caractères contenant l'ensemble
	*    des identifiants sélectionnables par l'utilisateur,
	*  - previousPassword: Le mot de passe de la précédente ouverture de
	*    session,
	*  - component: Une référence sur un objet Component par rapport auquel
	*    la boîte de dialogue doit être centrée.
	*
	* Retourne: Si l'utilisateur a validé son identification, la méthode
	* retourne true. Elle retourne false dans tous les autres cas.
	* ----------------------------------------------------------*/
	public boolean getLogin(
		String[] usersNames,
		String previousPassword,
		Component component
		)
		throws
			InnerException
	{
		int x_position;
		int y_position;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "getLogin");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("usersNames=" + usersNames);
		//trace_arguments.writeTrace("previousPassword=" + previousPassword);
		// Vérification de la validité des arguments
		if(usersNames == null)
		{
			// La liste des utilisateurs est nulles, on sort
			trace_methods.endOfMethod();
			return false;
		}
		// Remplissage de la liste des utilisateurs
		for(int index = 0 ; index < usersNames.length ; index ++)
		{
			trace_debug.writeTrace("Ajout de l'identifiant: " +
				usersNames[index]);
			_userNames.addItem(usersNames[index]);
		}
		// Sélectionner le premier identifiant, s'il y en au moins un
		if(usersNames.length > 0) {
			_userNames.setSelectedIndex(0);
		}
		// Si un mot de passe est fourni, on positionne sa valeur dans la zone
		// de texte
		if(previousPassword != null && previousPassword.equals("") == false)
		{
		    _passwordField.setText(StringCODEC.decodeString(previousPassword));
			_userNames.setEnabled(false);
		}
		fieldsHaveChanged();

		// Redimmensionnement de la boîte
		pack();
		setResizable(false);
		if(component == null)
		{
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
		}
		else
		{
			setLocationRelativeTo(component);
		}
		// Affichage de la fenêtre (bloquant)
		show();

		// Lorsque l'on arrive ici, c'est que l'utilisateur a fait un choix
		// Destruction de la boîte
		dispose();
		return _validated;
	}

	/*----------------------------------------------------------
	* Nom: getUserName
	*
	* Description:
	* Cette méthode permet de récupérer le nom de l'utilisateur sélectionné
	* parmis la liste des identifiants proposée à l'utilisateur.
	*
	* Retourne: Le nom de l'utilisateur sélectionné, ou null.
	* ----------------------------------------------------------*/
	public String getUserName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "getUserName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _userNames.getSelectedItem().toString();
	}

	/*----------------------------------------------------------
	* Nom: getPassword
	*
	* Description:
	* Cette méthode permet de récupérer le mot de passe que l'utilisateur a
	* saisi dans la boîte d'identification. Le mot de passe retourné est crypté
	* afin qu'il ne puisse être lu.
	*
	* Retourne: Le mot de passe crypté de l'utilisateur.
	* ----------------------------------------------------------*/
	public String getPassword()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "getPassword");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return StringCODEC.encodeString(
			new String(_passwordField.getPassword()));
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _validated
	*
	* Description:
	* Cet attribut est un drapeau permettant d'indiquer si l'utilisateur a
	* validé son identification. Sa valeur est retournée par la méthode 
	* getLogin().
	* ----------------------------------------------------------*/
	private boolean _validated;

	/*----------------------------------------------------------
	* Nom: _passwordField
	*
	* Description:
	* Cet attribut maintient une référence sur la zone de saisie dans laquelle
	* l'utilisateur entre son mot de passe. Cette référence est nécessaire afin
	* de pouvoir récupérer la valeur du mot de passe.
	* ----------------------------------------------------------*/
	private JPasswordField _passwordField;

	/*----------------------------------------------------------
	* Nom: _userNames
	*
	* Description:
	* Cet attribut maintient une référence sur un objet liste servant à contenir
	* les noms des utilisateurs déclarés sur le système. Cette référence est
	* nécessaire afin de pouvoir remplir la liste et afin de pouvoir déterminer
	* quel nom d'utilisateur a été sélectionné.
	* ----------------------------------------------------------*/
	private JComboBox _userNames;

	/*----------------------------------------------------------
	* Nom: _validateButton
	*
	* Description:
	* Cet attribut maintient une référence sur l'objet graphique représentant le
	* button "Valider". Cette référence est nécessaire afin de pouvoir changer
	* l'état de ce bouton.
	* ----------------------------------------------------------*/
	private JButton _validateButton;

	/*----------------------------------------------------------
	* Nom: validateLogin
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Valider" de la boîte de dialogue. Elle positionne le drapeau _validated
	* à true et masque la fenêtre (pour libérer la méthode show()).
	* ----------------------------------------------------------*/
	private void validateLogin()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "validateLogin");

		trace_methods.beginningOfMethod();
		// Si le bouton est inhibé, il faut sortir
		if(_validateButton.isEnabled() == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Marquage de la validation de l'identification
		_validated = true;
		// Masquage de la boîte (libération de l'appel de la méthode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelLogin
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Annuler" de la boîte d'identification.
	* Elle positionne le drapeau _validated à false et masque la fenêtre afin
	* de libérer l'exécution de la méthode show().
	* ----------------------------------------------------------*/
	private void cancelLogin()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "cancelLogin");

		trace_methods.beginningOfMethod();
		// Marquage de l'annulation de l'identification
		_validated = false;
		// Masquage de la boîte (libération de l'appel de la méthode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fieldsHaveChanged
	*
	* Description:
	* Cette méthode est appelée lorsque la sélection a changé dans la liste des
	* identifiants des utilisateurs ou lorsque le mot de passe a été modifié.
	* Elle permet de mettre à jour l'état du bouton "Valider" en fonction de la
	* présence ou non des informations.
	* ----------------------------------------------------------*/
	private void fieldsHaveChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		// Vérification de la sélection d'un identifiant dans la liste
		if(_userNames.getSelectedIndex() == -1)
		{
			// Aucune valeur n'est sélectionnée
			_validateButton.setEnabled(false);
			return;
		}
		// Vérification de la présence d'un mot de passe
		String password = new String(_passwordField.getPassword());
		if(password == null || password.equals("") == true)
		{
			// Aucun mot de passe n'a été saisi
			_validateButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_validateButton.setEnabled(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est utilisée pour instancier et ajouter tous les objets
	* graphiques qui font partie de la boîte d'identification. Elle est appelée
	* par le constructeur de la classe.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LoginDialog", "makePanel");

		trace_methods.beginningOfMethod();
		// Création du paneau central
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel central_panel = new JPanel(bag_layout);

		// Préparation des contraintes pour l'icône
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		// Création du JLabel d'affichage de l'icône
		JLabel icon_label = new JLabel(IconLoader.getIcon("Login"));
		bag_layout.setConstraints(icon_label, constraints);
		central_panel.add(icon_label);

		// Préparation des contraintes pour le label d'identifiant
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		// Création du JLabel d'affichage de l'identifiant
		JLabel id_label = new JLabel(MessageManager.getMessage("&ID_Identifier"));
		bag_layout.setConstraints(id_label, constraints);
		central_panel.add(id_label);

		// Préparation des contraintes pour la liste des identifiants
		constraints.gridx = 2;
		// Création de la liste des identifiants
		_userNames = new JComboBox();
		// Ajout du callback sur sélection
		_userNames.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				fieldsHaveChanged();
			}
		});
		_userNames.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					validateLogin();
				}
			}
		});
		bag_layout.setConstraints(_userNames, constraints);
		central_panel.add(_userNames);

		// Préparation des contraintes pour le label de mot de passe
		constraints.gridx = 1;
		constraints.gridy = 1;
		// Création du label de mot de passe
		JLabel password_label = new JLabel(MessageManager.getMessage("&ID_Password"));
		bag_layout.setConstraints(password_label, constraints);
		central_panel.add(password_label);

		// Préparation des contraintes pour la zone de saisie du mot de passe
		constraints.gridx = 2;
		// Création de la zone de saisie du mot de passe
		_passwordField = new JPasswordField(10);
		_passwordField.setEchoChar('*');
		// Ajout du callback sur la touche Entrée
		_passwordField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				validateLogin();
			}
		});
		// Ajout du callback sur frappe
		_passwordField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// Appel de la méthode idoine
				fieldsHaveChanged();
			}
		});
		bag_layout.setConstraints(_passwordField, constraints);
		central_panel.add(_passwordField);

		// Création du paneau des boutons
		JPanel buttons_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));

		// Création du bouton Valider
		_validateButton = new JButton(MessageManager.getMessage("&Button_Validate"));
		_validateButton.setEnabled(false);
		_validateButton.setDefaultCapable(true);
		// Ajout du callback sur click
		_validateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				validateLogin();
			}
		});
		buttons_panel.add(_validateButton);

		// Création du bouton Annuler
		JButton cancel_button = new JButton(MessageManager.getMessage("&Button_Cancel"));
		// Ajout du callback sur click
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				cancelLogin();
			}
		});
		buttons_panel.add(cancel_button);

		// Redimensionnement des boutons pour qu'ils aient la même taille
		// Récupération de la largeur des boutons
		int validate_button_width = _validateButton.getPreferredSize().width;
		int cancel_button_width = cancel_button.getPreferredSize().width;
		// Récupération de la hauteur des boutons
		int button_height = _validateButton.getPreferredSize().height;
		// Calcul de la largeur maximale des boutons
		int max_width = Math.max(validate_button_width, cancel_button_width);
		// Positionnement de la taille préférée des boutons en fonction de
		// la valeur calculée
		Dimension buttons_size = new Dimension(max_width, button_height);
		_validateButton.setPreferredSize(buttons_size);
		cancel_button.setPreferredSize(buttons_size);

		// Ajout du paneau des boutons au paneau central
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weighty = 0;
		bag_layout.setConstraints(buttons_panel, constraints);
		central_panel.add(buttons_panel);
		// Ajout du paneau central au content pane
		getContentPane().add(central_panel, BorderLayout.CENTER);

		trace_methods.endOfMethod();
	}
}