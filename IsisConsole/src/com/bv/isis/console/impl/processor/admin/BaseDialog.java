/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/BaseDialog.java,v $
* $Revision: 1.21 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de base des sous-processeurs
* DATE:        04/06/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: BaseDialog.java,v $
* Revision 1.21  2009/02/25 16:40:02  tz
* Ajout de la méthode getAgentLayerMode().
*
* Revision 1.20  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.19  2008/06/27 09:41:46  tz
* Modification de ExecutionSurveyor.execute().
*
* Revision 1.18  2008/06/16 11:29:17  tz
* Utilisation de AdministrationCommandFactory.buildAdministrationCommand().
* Suppression des méthodes buildAdministrationCommand() et addSlashes().
*
* Revision 1.17  2006/10/13 15:11:09  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.16  2006/03/20 15:52:54  tz
* Ajout de la méthode getTheParameters().
*
* Revision 1.15  2006/03/08 14:08:36  tz
* Réintégration du processeur d'administration de toute table.
*
* Revision 1.14  2005/07/01 12:21:45  tz
* Modification du composant pour les traces
*
* Revision 1.13  2004/10/22 15:41:40  tz
* Modification des chaînes de messages.
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.10  2004/07/29 12:17:24  tz
* Suppression d'imports inutiles
*
* Revision 1.9  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.8.2.2  2003/11/19 09:56:05  tz
* Correction pb de callback sur l'administration des abonnements.
*
* Revision 1.8.2.1  2003/11/13 10:35:40  tz
* Modification de la méthode setEnterCallback pour les JComboBox éditables.
*
* Revision 1.8  2003/05/16 09:32:01  tz
* Ajout de la méthode callValidateInput()
*
* Revision 1.7  2003/05/15 12:48:03  tz
* Correction de la fiche Inuit/109.
*
* Revision 1.6  2003/03/12 14:39:47  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.5  2003/03/07 16:21:38  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.4  2002/09/20 10:46:10  tz
* Correction de la fiche Inuit/62
*
* Revision 1.3  2002/08/13 13:10:05  tz
* Ajout curseur -> sablier
*
* Revision 1.2  2002/06/27 14:13:04  tz
* Ajout des processeurs d'administration des I-CLE et des Agents
*
* Revision 1.1  2002/06/19 12:17:52  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JButton;
import com.bv.core.message.MessageManager;
import java.awt.Component;
import com.bv.core.config.ConfigurationAPI;
import java.awt.Cursor;
import javax.swing.JComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;

/*----------------------------------------------------------
* Nom: BaseDialog
*
* Description:
* Cette classe abstraite est la classe de base des sous-processeurs de tâches
* d'administration. Il définit une méthode permettant d'exécuter et de suivre
* l'exécution d'une commande (pour l'insertion, la mise à jour ou encore la
* suppression).
* Elle définit également la méthode de construction des boîtes de dialogue des
* sous-processeurs, et déclare des méthodes abstraites qui devront être
* implémentées dans les sous-classes.
* ----------------------------------------------------------*/
abstract class BaseDialog
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: BaseDialog
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle fixe les références
	* sur la fenêtre principale et sur le noeud sélectionné.
	*
	* Si une erreur survient lors de la construction de la boîte de dialogue,
	* l'exception InnerException est levée.
	*
	* Arguments:
	*  - selectedNode: Une référence sur le noeud sélectionné,
	*  - action: Une chaîne définissant l'action de mise à jour de la table,
	*  - dialogCaller: Une référence sur l'interface DialogCallerInterface.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public BaseDialog(
		GenericTreeObjectNode selectedNode,
		String action,
		DialogCallerInterface dialogCaller
		)
		throws
			InnerException
	{
		super((Frame)dialogCaller.getTheMainWindowInterface(), "", true);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "BaseDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("dialogCaller=" + dialogCaller);
		_action = action;
		_selectedNode = selectedNode;
		_dialogCaller = dialogCaller;
		_executionSucceeded = false;
		setTitle(MessageManager.getMessage("&Admin_Action_" + action));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateInput
	*
	* Description:
	* Cette méthode abstraite doit être redéfinie dans les sous-classes. Elle
	* est appelée lorsque l'utilisateur a cliqué sur le bouton "Valider".
	* ----------------------------------------------------------*/
	public abstract void validateInput();

	/*----------------------------------------------------------
	* Nom: cancel
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton
	* "Annuler". Elle masque la boîte de dialogue de sorte que la méthode show()
	* soit libérée.
	* ----------------------------------------------------------*/
	public void cancel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "cancel");
		
		trace_methods.beginningOfMethod();
		// On masque la fenêtre
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: showDialog
	*
	* Description:
	* Cette méthode permet de commander l'affichage de la boîte de dialogue.
	* Elle ne retourne que si l'utilisateur a annulé, ou s'il a validé, que
	* l'exécution de la commande de mise à jour de la table s'est achevée.
	*
	* Arguments:
	*  - locationComponent: Une référence sur un composant permettant le
	*    centrage de la boîte de dialogue par rapport à celui-ci.
	*
	* Retourne: true si la commande s'est bien terminée, false sinon.
	* ----------------------------------------------------------*/
	public boolean showDialog(
		Component locationComponent
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "BaseDialog", "showDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("locationComponent=" + locationComponent);
		// On positionne la fenêtre en fonction de la fenêtre parent
		setLocationRelativeTo(locationComponent);
		// On affiche la fenêtre. Cette méthode est bloquante jusqu'à ce
		// que la boîte de dialogue soit masquée
		show();
		// Si on arrive ici, la boîte de dialogue peut être détruite
		dispose();
		trace_methods.endOfMethod();
		return _executionSucceeded;
	}

	/*----------------------------------------------------------
	* Nom: getActionId
	* 
	* Description:
	* Cette méthode permet de récupérer l'identifiant de l'action. Elle 
	* appelle la méthode getTheActionId() de l'interface DialogCallerInterface.
	* 
	* Retourne: L'identifiant unique de l'action.
	* ----------------------------------------------------------*/
	public String getActionId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getActionId");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _dialogCaller.getTheActionId();
	}

	/*----------------------------------------------------------
	* Nom: getTableName
	* 
	* Description:
	* Cette méthode permet de récupérer le nom de la table administrée. Elle 
	* appelle la méthode getTheTableName() sur l'interface 
	* DialogCallerInterface.
	* 
	* Retourne: Le nom de la table administrée.
	* ----------------------------------------------------------*/
	public String getTableName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getTableName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _dialogCaller.getTheTableName();
	}

	/*----------------------------------------------------------
	* Nom: getMainWindowInterface
	*
	* Description:
	* Cette méthode permet de récupérer la référence sur l'interface
	* MainWindowInterface. Elle appelle la méthode getTheMainWindowInterface() 
	* de l'interface DialogCallerInterface.
	*
	* Retourne: La référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getMainWindowInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getMainWindowInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _dialogCaller.getTheMainWindowInterface();
	}

	/*----------------------------------------------------------
	* Nom: getParameters
	* 
	* Description:
	* Cette méthode permet de récupérer les paramètres d'exécution du 
	* processeur. Elle appelle la méthode getTheParameters() de l'interface 
	* DialogCallerInterface.
	* 
	* Retourne: Les paramètres d'exécution du processeur.
	* ----------------------------------------------------------*/
	public String getParameters()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getParameters");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _dialogCaller.getTheParameters();
	}

	/*----------------------------------------------------------
	* Nom: getAgentLayerMode
	* 
	* Description:
	* Cette méthode permet de récupérer le mode de la couche d'exécution de 
	* l'Agent cible.
	* 
	* Retourne: Le mode de la couche d'exécution de l'Agent.
	* ----------------------------------------------------------*/
	public String getAgentLayerMode()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getAgentLayerMode");
		String agent_layer_mode = null;

		trace_methods.beginningOfMethod();
		agent_layer_mode = 
			AgentSessionManager.getInstance().getAgentLayerMode(
			_selectedNode.getAgentName());
		trace_methods.endOfMethod();
		return agent_layer_mode;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: buildAdministrationCommand
	*
	* Description:
	* Cette méthode est destinée à être appelée par les sous-classes afin
	* de construire une commande correspondant à une action (ajout,
	* modification ou suppression) sur les données des tables que ces
	* sous-processeurs gèrent.  Les données qui doivent être ajoutées ou mises
	* à jour sont passées en arguments.
	* Cette méthode fait appel à la méthode buildAdministrationCommand() de la 
	* classe AdministrationCommandFactory.
	*
	* Arguments:
	*  - values: Une chaîne contenant les données à mettre à jour dans la table.
	*
	* Retourne: Une chaîne contenant la commande de mise à jour de la table.
	* ----------------------------------------------------------*/
	protected String buildAdministrationCommand(
		String values
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "buildAdministrationCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String command;
		String message;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("values=" + values);
		// S'il n'y a pas de valeur, on sort
		if(values == null || values.equals("") == true)
		{
			trace_methods.endOfMethod();
			return null;
		}
		// On va construire le message de log suivant l'action
		if(_action.equals("Insert") == true ||
			_action.equals("Replace") == true)
		{
			if(_action.equals("Insert") == true)
			{
				message = "&LOG_DataInsertion";
			}
			else
			{
				message = "&LOG_DataModification";
			}
		}
		else
		{
			message = "&LOG_DataRemoval";
		}
		// On va appeler la méthode de construction de la command
		command = AdministrationCommandFactory.buildAdministrationCommand(
			_action, getTableName(), values, getAgentLayerMode());
		// On va générer un log de mise à jour
		String[] message_data = new String[1];
		message_data[0] = MessageManager.getMessage(message) + "'" + values +
			"'";
		LogServiceProxy.addMessageForAction(getActionId(), message_data);
		// On retourne la commande
		trace_methods.endOfMethod();
		return command;
	}

	/*----------------------------------------------------------
	* Nom: execute
	*
	* Description:
	* Cette méthode est destinée à être appelée par les sous-classes afin
	* d'exécuter une commande (quelle qu'elle soit).
	* La méthode instancie un objet ExecutionSurveyor qui est réellement chargé
	* de toute la procédure d'exécution de la commande et d'attendre la fin de
	* celle-ci.
	* Si une erreur est détectée, la méthode gère l'affichage d'une fenêtre
	* indiquant la nature du problème à l'utilisateur.
	*
	* Arguments:
	*  - command: Une chaîne contenant la commande à exécuter.
	* ----------------------------------------------------------*/
	protected void execute(
		String command
		)
	{
		String[] message = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "execute");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);
		// S'il n'y a pas de valeur, on sort
		if(command == null || command.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On crée un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'exécution
		try
		{
		    surveyor.execute(getActionId(), command, _selectedNode, null);
			setExecutionSucceeded(true);
			// On va générer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'exécution de la commande: " + command);
			trace_errors.writeTrace("L'erreur est: " + exception.getMessage());
			// On va afficher le message à l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteCommand", exception);
			// On doit retourner false pour indiquer que la méthode ne s'est
			// pas exécutée correctement
			setExecutionSucceeded(false);
			// On va générer un message de log
			String[] errors =
				CommonFeatures.buildArrayFromString(exception.getMessage());
			message = new String[3 + errors.length];
			int counter = 0;
			message[counter++] =
				MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[counter++] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandFailed");
			message[counter++] = MessageManager.getMessage("&LOG_CommandError");
			for(int index = 0 ; index < errors.length ; index ++)
			{
				message[counter++] = errors[index];
			}
		}
		// On génère la trace de log
		LogServiceProxy.addMessageForAction(getActionId(), message);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getAction
	*
	* Description:
	* Cette méthode permet de récupérer l'action qui devra être exécutée: une
	* insertion ("Insert"), une modification ("Replace") ou encore une
	* suppression ("Remove").
	*
	* Retourne: L'action qui devra être exécutée.
	* ----------------------------------------------------------*/
	protected String getAction()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getAction");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _action;
	}

	/*----------------------------------------------------------
	* Nom: getSelectedNode
	*
	* Description:
	* Cette méthode permet de récupérer la référence sur le noeud sélectionné
	* (s'il y en a un).
	*
	* Retourne: La référence sur l'objet GenericTreeObjectNode représentant le
	* noeud sélectionné.
	* ----------------------------------------------------------*/
	protected GenericTreeObjectNode getSelectedNode()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "getSelectedNode");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _selectedNode;
	}

	/*----------------------------------------------------------
	* Nom: makeFormPanel
	*
	* Description:
	* Cette méthode abstraite doit être redéfinie au niveau de la sous-classe.
	* Elle est appelée par la méthode makePanel lors de la construction de la
	* boîte de dialogue d'administration afin de construire le panneau de saisie
	* des données.
	*
	* Si une erreur survient lors de la création du panneau, l'exception
	* InnerException est levée.
	*
	* Retourne: Une référence sur le panneau de saisie des données.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected abstract JPanel makeFormPanel()
		throws
			InnerException;

	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette méthode est appelée automatiquement par le ramasse miettes de Java
	* lorsque un objet est sur le point d'être détruit. Elle permet de libérer
	* les ressources.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "finalize");

		trace_methods.beginningOfMethod();
		_selectedNode = null;
		_action = null;
		super.finalize();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est appelée par le constructeur de la classe afin de
	* construire la boîte de dialogue d'administration. Elle appelle
	* successivement les méthodes makeFormPanel() et makeButtonsPanel() qui
	* sont chargées de construire les deux panneaux de la boîte de dialogue.
	*
	* Si une erreur survient lors de la construction de la boîte de dialogue,
	* l'exception InnerException est levée.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected void makePanel()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "makePanel");

		trace_methods.beginningOfMethod();
		// On va construire le panneau de saisie
		JPanel form_panel = makeFormPanel();
		// On va construire le panneau des boutons
		JPanel buttons_panel = makeButtonsPanel();
		getContentPane().setLayout(new BorderLayout());
		// On place le panneau de saisie dans la zone centrale
		getContentPane().add(form_panel, BorderLayout.CENTER);
		// On place le panneau des boutons dans la zone sud
		getContentPane().add(buttons_panel, BorderLayout.SOUTH);
		// On redimensionne la fenêtre
		pack();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setEnterCallback
	*
	* Description:
	* Cette méthode permet de définir la méthode de callback en
	* cas d'appui sur la touche "Entrée" sur un objet JComponent
	* passé en argument.
	* La méthode de callback est la méthode validateInput().
	*
	* Arguments:
	*  - component: Un objet JComponent sur lequel le callback
	*    doit être défini.
	* ----------------------------------------------------------*/
	protected void setEnterCallback(
		JComponent component
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "BaseDialog", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On vérifie la validité de l'argument
		if(component == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On positionne le callback sur Entree
		component.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					callValidateInput();
				}
			}
		});
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: callValidateInput
	*
	* Description:
	* Cette méthode permet de d'effectuer des opérations graphiques
	* évitant ainsi une action sur le boîte de dialogue lors de
	* l'exécution d'une opération.
	* Elle appelle la méthode validateInput().
	* ----------------------------------------------------------*/
	protected void callValidateInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "BaseDialog", "callValidateInput");

		trace_methods.beginningOfMethod();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setEnabled(false);
		_validateButton.setEnabled(false);
		_cancelButton.setEnabled(false);
		Thread processing_thread = new Thread(new Runnable()
		{
			public void run()
			{
				validateInput();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				setEnabled(true);
				_validateButton.setEnabled(true);
				_cancelButton.setEnabled(true);
			}
		});
		processing_thread.start();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setExecutionSucceeded
	* 
	* Description:
	* Cette méthode permet de positionner la valeur de l'attribut 
	* _executionSucceeded, lequel indique si une exécution s'est bien déroulée 
	* ou non.
	* 
	* Arguments:
	*  - executionSucceeded: L'indicateur de succès d'exécution.
 	* ----------------------------------------------------------*/
 	protected void setExecutionSucceeded(
 		boolean executionSucceeded
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "setExecutionSucceeded");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("executionSucceeded=" + executionSucceeded);
		_executionSucceeded = executionSucceeded;
		trace_methods.endOfMethod();
 	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _selectedNode
	*
	* Description:
	* Cet attribut maintient une référence sur le noeud qui a été sélectionné
	* lors de l'appel du processeur.
	* ----------------------------------------------------------*/
	private GenericTreeObjectNode _selectedNode;

	/*----------------------------------------------------------
	* Nom: _executionSucceeded
	*
	* Description:
	* Cet attribut permet de retourner (par le biais de la méthode showDialog())
	* un drapeau indiquant si l'exécution s'est bien déroulée ou non.
	* ----------------------------------------------------------*/
	private boolean _executionSucceeded;

	/*----------------------------------------------------------
	* Nom: _action
	*
	* Description:
	* Cet attribut maintient le type d'action qui doit être exécutée par la
	* méthode execute(). Elle peut prendre les valeurs "Insert", "Replace" ou
	* encore "Remove".
	* ----------------------------------------------------------*/
	private String _action;

	/*----------------------------------------------------------
	* Nom: _validateButton
	*
	* Description:
	* Cet attribut maintient une référence sur un objet JButton représentant le
	* bouton "Valider".
	* ----------------------------------------------------------*/
	private JButton _validateButton;

	/*----------------------------------------------------------
	* Nom: _cancelButton
	*
	* Description:
	* Cet attribut maintient une référence sur un objet JButton représentant le
	* bouton "Annuler".
	* ----------------------------------------------------------*/
	private JButton _cancelButton;

	/*----------------------------------------------------------
	* Nom: _dialogCaller
	* 
	* Description:
	* Cet attribut maintient une référence sur une interface 
	* DialogCallerInterface permettant la communication avec la classe 
	* appelante.
	* ----------------------------------------------------------*/
	private DialogCallerInterface _dialogCaller;

	/*----------------------------------------------------------
	* Nom: makeButtonsPanel
	*
	* Description:
	* Cette méthode est chargée de construire le panneau contenant les boutons
	* "Valider" et "Annuler". Un click sur le bouton "Valider" appellera la
	* méthode callValidateInput(), et un click sur le bouton "Annuler"
	* appellera la méthode cancel().
	*
	* Retourne: Le panneau contenant les boutons.
	* ----------------------------------------------------------*/
	private JPanel makeButtonsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "makeButtonsPanel");

		trace_methods.beginningOfMethod();
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		// On crée un autre panneau avec un flow layout
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On crée le bouton Valider
		_validateButton =
			new JButton(MessageManager.getMessage("&Admin_Validate"));
		// Ajout du callback sur ENTREE
		setEnterCallback(_validateButton);
		// On ajoute le bouton
		panel.add(_validateButton);
		// On crée le bouton "Annuler"
		_cancelButton =	new JButton(MessageManager.getMessage("&Admin_Cancel"));
		// On ajoute le bouton
		panel.add(_cancelButton);
		// On ajoute les callbacks sur les boutons
		_validateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				callValidateInput();
			}
		});
		_cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				cancel();
			}
		});
		// On calcule la dimension de tous les boutons
		int button_width = Math.max(_validateButton.getPreferredSize().width,
			_cancelButton.getPreferredSize().width);
		Dimension size = new Dimension(button_width,
			_validateButton.getPreferredSize().height);
		_validateButton.setPreferredSize(size);
		_cancelButton.setPreferredSize(size);
		// On ajoute la panneau dans le panneau général
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		trace_methods.endOfMethod();
		return button_panel;
	}
}