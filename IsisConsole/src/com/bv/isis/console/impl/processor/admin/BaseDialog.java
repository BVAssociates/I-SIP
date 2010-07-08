/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Ajout de la m�thode getAgentLayerMode().
*
* Revision 1.20  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.19  2008/06/27 09:41:46  tz
* Modification de ExecutionSurveyor.execute().
*
* Revision 1.18  2008/06/16 11:29:17  tz
* Utilisation de AdministrationCommandFactory.buildAdministrationCommand().
* Suppression des m�thodes buildAdministrationCommand() et addSlashes().
*
* Revision 1.17  2006/10/13 15:11:09  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.16  2006/03/20 15:52:54  tz
* Ajout de la m�thode getTheParameters().
*
* Revision 1.15  2006/03/08 14:08:36  tz
* R�int�gration du processeur d'administration de toute table.
*
* Revision 1.14  2005/07/01 12:21:45  tz
* Modification du composant pour les traces
*
* Revision 1.13  2004/10/22 15:41:40  tz
* Modification des cha�nes de messages.
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.11  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
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
* Modification de la m�thode setEnterCallback pour les JComboBox �ditables.
*
* Revision 1.8  2003/05/16 09:32:01  tz
* Ajout de la m�thode callValidateInput()
*
* Revision 1.7  2003/05/15 12:48:03  tz
* Correction de la fiche Inuit/109.
*
* Revision 1.6  2003/03/12 14:39:47  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.5  2003/03/07 16:21:38  tz
* Prise en compte du m�canisme de log m�tier
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
// D�claration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports syst�me
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
* Cette classe abstraite est la classe de base des sous-processeurs de t�ches
* d'administration. Il d�finit une m�thode permettant d'ex�cuter et de suivre
* l'ex�cution d'une commande (pour l'insertion, la mise � jour ou encore la
* suppression).
* Elle d�finit �galement la m�thode de construction des bo�tes de dialogue des
* sous-processeurs, et d�clare des m�thodes abstraites qui devront �tre
* impl�ment�es dans les sous-classes.
* ----------------------------------------------------------*/
abstract class BaseDialog
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: BaseDialog
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle fixe les r�f�rences
	* sur la fen�tre principale et sur le noeud s�lectionn�.
	*
	* Si une erreur survient lors de la construction de la bo�te de dialogue,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�,
	*  - action: Une cha�ne d�finissant l'action de mise � jour de la table,
	*  - dialogCaller: Une r�f�rence sur l'interface DialogCallerInterface.
	*
	* L�ve: InnerException.
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
	* Cette m�thode abstraite doit �tre red�finie dans les sous-classes. Elle
	* est appel�e lorsque l'utilisateur a cliqu� sur le bouton "Valider".
	* ----------------------------------------------------------*/
	public abstract void validateInput();

	/*----------------------------------------------------------
	* Nom: cancel
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton
	* "Annuler". Elle masque la bo�te de dialogue de sorte que la m�thode show()
	* soit lib�r�e.
	* ----------------------------------------------------------*/
	public void cancel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "cancel");
		
		trace_methods.beginningOfMethod();
		// On masque la fen�tre
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: showDialog
	*
	* Description:
	* Cette m�thode permet de commander l'affichage de la bo�te de dialogue.
	* Elle ne retourne que si l'utilisateur a annul�, ou s'il a valid�, que
	* l'ex�cution de la commande de mise � jour de la table s'est achev�e.
	*
	* Arguments:
	*  - locationComponent: Une r�f�rence sur un composant permettant le
	*    centrage de la bo�te de dialogue par rapport � celui-ci.
	*
	* Retourne: true si la commande s'est bien termin�e, false sinon.
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
		// On positionne la fen�tre en fonction de la fen�tre parent
		setLocationRelativeTo(locationComponent);
		// On affiche la fen�tre. Cette m�thode est bloquante jusqu'� ce
		// que la bo�te de dialogue soit masqu�e
		show();
		// Si on arrive ici, la bo�te de dialogue peut �tre d�truite
		dispose();
		trace_methods.endOfMethod();
		return _executionSucceeded;
	}

	/*----------------------------------------------------------
	* Nom: getActionId
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer l'identifiant de l'action. Elle 
	* appelle la m�thode getTheActionId() de l'interface DialogCallerInterface.
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
	* Cette m�thode permet de r�cup�rer le nom de la table administr�e. Elle 
	* appelle la m�thode getTheTableName() sur l'interface 
	* DialogCallerInterface.
	* 
	* Retourne: Le nom de la table administr�e.
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
	* Cette m�thode permet de r�cup�rer la r�f�rence sur l'interface
	* MainWindowInterface. Elle appelle la m�thode getTheMainWindowInterface() 
	* de l'interface DialogCallerInterface.
	*
	* Retourne: La r�f�rence sur l'interface MainWindowInterface.
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
	* Cette m�thode permet de r�cup�rer les param�tres d'ex�cution du 
	* processeur. Elle appelle la m�thode getTheParameters() de l'interface 
	* DialogCallerInterface.
	* 
	* Retourne: Les param�tres d'ex�cution du processeur.
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
	* Cette m�thode permet de r�cup�rer le mode de la couche d'ex�cution de 
	* l'Agent cible.
	* 
	* Retourne: Le mode de la couche d'ex�cution de l'Agent.
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
	* Cette m�thode est destin�e � �tre appel�e par les sous-classes afin
	* de construire une commande correspondant � une action (ajout,
	* modification ou suppression) sur les donn�es des tables que ces
	* sous-processeurs g�rent.  Les donn�es qui doivent �tre ajout�es ou mises
	* � jour sont pass�es en arguments.
	* Cette m�thode fait appel � la m�thode buildAdministrationCommand() de la 
	* classe AdministrationCommandFactory.
	*
	* Arguments:
	*  - values: Une cha�ne contenant les donn�es � mettre � jour dans la table.
	*
	* Retourne: Une cha�ne contenant la commande de mise � jour de la table.
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
		// On va appeler la m�thode de construction de la command
		command = AdministrationCommandFactory.buildAdministrationCommand(
			_action, getTableName(), values, getAgentLayerMode());
		// On va g�n�rer un log de mise � jour
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
	* Cette m�thode est destin�e � �tre appel�e par les sous-classes afin
	* d'ex�cuter une commande (quelle qu'elle soit).
	* La m�thode instancie un objet ExecutionSurveyor qui est r�ellement charg�
	* de toute la proc�dure d'ex�cution de la commande et d'attendre la fin de
	* celle-ci.
	* Si une erreur est d�tect�e, la m�thode g�re l'affichage d'une fen�tre
	* indiquant la nature du probl�me � l'utilisateur.
	*
	* Arguments:
	*  - command: Une cha�ne contenant la commande � ex�cuter.
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
		// On cr�e un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'ex�cution
		try
		{
		    surveyor.execute(getActionId(), command, _selectedNode, null);
			setExecutionSucceeded(true);
			// On va g�n�rer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'ex�cution de la commande: " + command);
			trace_errors.writeTrace("L'erreur est: " + exception.getMessage());
			// On va afficher le message � l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteCommand", exception);
			// On doit retourner false pour indiquer que la m�thode ne s'est
			// pas ex�cut�e correctement
			setExecutionSucceeded(false);
			// On va g�n�rer un message de log
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
		// On g�n�re la trace de log
		LogServiceProxy.addMessageForAction(getActionId(), message);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getAction
	*
	* Description:
	* Cette m�thode permet de r�cup�rer l'action qui devra �tre ex�cut�e: une
	* insertion ("Insert"), une modification ("Replace") ou encore une
	* suppression ("Remove").
	*
	* Retourne: L'action qui devra �tre ex�cut�e.
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
	* Cette m�thode permet de r�cup�rer la r�f�rence sur le noeud s�lectionn�
	* (s'il y en a un).
	*
	* Retourne: La r�f�rence sur l'objet GenericTreeObjectNode repr�sentant le
	* noeud s�lectionn�.
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
	* Cette m�thode abstraite doit �tre red�finie au niveau de la sous-classe.
	* Elle est appel�e par la m�thode makePanel lors de la construction de la
	* bo�te de dialogue d'administration afin de construire le panneau de saisie
	* des donn�es.
	*
	* Si une erreur survient lors de la cr�ation du panneau, l'exception
	* InnerException est lev�e.
	*
	* Retourne: Une r�f�rence sur le panneau de saisie des donn�es.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	protected abstract JPanel makeFormPanel()
		throws
			InnerException;

	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette m�thode est appel�e automatiquement par le ramasse miettes de Java
	* lorsque un objet est sur le point d'�tre d�truit. Elle permet de lib�rer
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
	* Cette m�thode est appel�e par le constructeur de la classe afin de
	* construire la bo�te de dialogue d'administration. Elle appelle
	* successivement les m�thodes makeFormPanel() et makeButtonsPanel() qui
	* sont charg�es de construire les deux panneaux de la bo�te de dialogue.
	*
	* Si une erreur survient lors de la construction de la bo�te de dialogue,
	* l'exception InnerException est lev�e.
	*
	* L�ve: InnerException.
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
		// On redimensionne la fen�tre
		pack();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setEnterCallback
	*
	* Description:
	* Cette m�thode permet de d�finir la m�thode de callback en
	* cas d'appui sur la touche "Entr�e" sur un objet JComponent
	* pass� en argument.
	* La m�thode de callback est la m�thode validateInput().
	*
	* Arguments:
	*  - component: Un objet JComponent sur lequel le callback
	*    doit �tre d�fini.
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
		// On v�rifie la validit� de l'argument
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
	* Cette m�thode permet de d'effectuer des op�rations graphiques
	* �vitant ainsi une action sur le bo�te de dialogue lors de
	* l'ex�cution d'une op�ration.
	* Elle appelle la m�thode validateInput().
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
	* Cette m�thode permet de positionner la valeur de l'attribut 
	* _executionSucceeded, lequel indique si une ex�cution s'est bien d�roul�e 
	* ou non.
	* 
	* Arguments:
	*  - executionSucceeded: L'indicateur de succ�s d'ex�cution.
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
	* Cet attribut maintient une r�f�rence sur le noeud qui a �t� s�lectionn�
	* lors de l'appel du processeur.
	* ----------------------------------------------------------*/
	private GenericTreeObjectNode _selectedNode;

	/*----------------------------------------------------------
	* Nom: _executionSucceeded
	*
	* Description:
	* Cet attribut permet de retourner (par le biais de la m�thode showDialog())
	* un drapeau indiquant si l'ex�cution s'est bien d�roul�e ou non.
	* ----------------------------------------------------------*/
	private boolean _executionSucceeded;

	/*----------------------------------------------------------
	* Nom: _action
	*
	* Description:
	* Cet attribut maintient le type d'action qui doit �tre ex�cut�e par la
	* m�thode execute(). Elle peut prendre les valeurs "Insert", "Replace" ou
	* encore "Remove".
	* ----------------------------------------------------------*/
	private String _action;

	/*----------------------------------------------------------
	* Nom: _validateButton
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton repr�sentant le
	* bouton "Valider".
	* ----------------------------------------------------------*/
	private JButton _validateButton;

	/*----------------------------------------------------------
	* Nom: _cancelButton
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton repr�sentant le
	* bouton "Annuler".
	* ----------------------------------------------------------*/
	private JButton _cancelButton;

	/*----------------------------------------------------------
	* Nom: _dialogCaller
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur une interface 
	* DialogCallerInterface permettant la communication avec la classe 
	* appelante.
	* ----------------------------------------------------------*/
	private DialogCallerInterface _dialogCaller;

	/*----------------------------------------------------------
	* Nom: makeButtonsPanel
	*
	* Description:
	* Cette m�thode est charg�e de construire le panneau contenant les boutons
	* "Valider" et "Annuler". Un click sur le bouton "Valider" appellera la
	* m�thode callValidateInput(), et un click sur le bouton "Annuler"
	* appellera la m�thode cancel().
	*
	* Retourne: Le panneau contenant les boutons.
	* ----------------------------------------------------------*/
	private JPanel makeButtonsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BaseDialog", "makeButtonsPanel");

		trace_methods.beginningOfMethod();
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		// On cr�e un autre panneau avec un flow layout
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On cr�e le bouton Valider
		_validateButton =
			new JButton(MessageManager.getMessage("&Admin_Validate"));
		// Ajout du callback sur ENTREE
		setEnterCallback(_validateButton);
		// On ajoute le bouton
		panel.add(_validateButton);
		// On cr�e le bouton "Annuler"
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
		// On ajoute la panneau dans le panneau g�n�ral
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		trace_methods.endOfMethod();
		return button_panel;
	}
}