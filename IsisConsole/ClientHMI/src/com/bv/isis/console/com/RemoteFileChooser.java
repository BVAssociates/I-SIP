/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/RemoteFileChooser.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Explorateur de syst�me de fichiers distant
* DATE:        30/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: RemoteFileChooser.java,v $
* Revision 1.4  2009/01/15 16:51:58  tz
* Correction de la fiche FS#460.
*
* Revision 1.3  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2006/11/09 11:56:29  tz
* R�organisation de l'ajout des entr�es (r�pertoire d'abord, fichiers ensuite).
* Am�lioration de la gestion du cas d'un r�pertoire racine.
*
* Revision 1.1  2006/11/03 10:25:16  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.com;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.gui.IconLoader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.JScrollBar;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.FileSystemEntry;
import com.bv.isis.corbacom.EntryTypeEnum;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.gui.SortedTableModel;
import com.bv.isis.corbacom.FileReaderInterface;

/*----------------------------------------------------------
* Nom: RemoteFileChooser
* 
* Description:
* Cette classe est une classe technique charg�e de l'exploration du syst�me de 
* fichiers d'une plate-forme distante afin de permettre la s�lection d'un 
* fichier ou d'un r�pertoire.
* Il s'agit d'une boite de dialogue, aussi elle sp�cialise la classe JDialog.
* 
* Le mode de s�lection, et de fonctionnement de la boite de dialogue peut �tre 
* sp�cifi�e via l'utilisation des m�thodes setSelectionMode(), setAcceptType() 
* et setPresetType().
* La boite de dialogue est affich�e � l'utilisateur jursqu'� ce que ce dernier 
* approuve ou annule une s�lection via la m�thode showDialog().
* Enfin, le chemin complet de la s�lection peut �tre r�cup�r�e via la m�thode 
* getSelection().
* ----------------------------------------------------------*/
public class RemoteFileChooser
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SELECT_FILES
	* 
	* Description:
	* Cette valeur constante permet de d�finir le mode de s�lection de 
	* fichiers (voir setSelectionMode()).
	* ----------------------------------------------------------*/
	public static final int SELECT_FILES = 0;

	/*----------------------------------------------------------
	* Nom: SELECT_DIRECTORIES
	* 
	* Description:
	* Cette valeur constante permet de d�finir le mode de s�lection de 
	* r�pertoires (voir setSelectionMode()).
	* ----------------------------------------------------------*/
	public static final int SELECT_DIRECTORIES = 1;

	/*----------------------------------------------------------
	* Nom: ACCEPT_ALL
	* 
	* Description:
	* Cette valeur constante statique permet de d�finir une acceptation de 
	* fichiers ou de r�pertoires existants ou non (voir setAcceptType()).
	* ----------------------------------------------------------*/
	public static final int ACCEPT_ALL = 0;

	/*----------------------------------------------------------
	* Nom: ACCEPT_EXISTING
	* 
	* Description:
	* Cette valeur constante permet de d�finir une acceptation de fichiers ou 
	* de r�pertoires existants seulement (voir setAcceptType()).
	* ----------------------------------------------------------*/
	public static final int ACCEPT_EXISTING = 1;

	/*----------------------------------------------------------
	* Nom: CANCEL_OPTION
	* 
	* Description:
	* Cette valeur constante est la valeur de retour de la m�thode 
	* showDialog() lorsque l'utilisateur a annul� la s�lection.
	* ----------------------------------------------------------*/
	public static final int CANCEL_OPTION = 0;

	/*----------------------------------------------------------
	* Nom: APPROVE_OPTION
	* 
	* Description:
	* Cette valeur constante est la valeur de retour de la m�thode 
	* showDialog() lorsque l'utilisateur a valid� la s�lection.
	* ----------------------------------------------------------*/
	public static final int APPROVE_OPTION = 1;

	/*----------------------------------------------------------
	* Nom: PRESET_NONE
	* 
	* Description:
	* Cette valeur constante permet de fixer un type de pr�s�lection de la 
	* liste des r�pertoires de parcours � une pr�s�lection vide (voir 
	* setPresetType()).
	* ----------------------------------------------------------*/
	public static final int PRESET_NONE = 0;

	/*----------------------------------------------------------
	* Nom: PRESET_EXECUTABLES
	* 
	* Description:
	* Cette valeur constante permet de fixer un type de pr�s�lection de la 
	* liste des r�pertoires de parcours � une pr�s�lection sur les ex�cutables 
	* (voir setPresetType()).
	* ----------------------------------------------------------*/
	public static final int PRESET_EXECUTABLES = 1; 

	/*----------------------------------------------------------
	* Nom: PRESET_DICTIONARIES
	*
	* Description:
	* Cette valeur constante permet de fixer un type de pr�s�lection de la 
	* liste des r�pertoires de parcours � une pr�s�lection sur les 
	* dictionnaires (voir setPresetType()).
	* ----------------------------------------------------------*/
	public static final int PRESET_DICTIONARIES = 2;

	/*----------------------------------------------------------
	* Nom: PRESET_DATAFILES
	* 
	* Description:
	* Cette valeur constante permet de fixer un type de pr�s�lection de la 
	* liste des r�pertoires de parcours � une pr�s�lection sur les fichiers de 
	* donn�es (voir setPresetType()).
	* ----------------------------------------------------------*/
	public static final int PRESET_DATAFILES = 3; 

	/*----------------------------------------------------------
	* Nom: PRESET_COMMANDPANELS
	*
	* Description:
	* Cette valeur constante permet de fixer un type de pr�s�lection de la 
	* liste des r�pertoires de parcours � une pr�s�lection sur les panneaux de 
	* commandes (voir setPresetType()).
	* ----------------------------------------------------------*/
	public static final int PRESET_COMMANDPANELS = 4;

	/*----------------------------------------------------------
	* Nom: RemoteFileChooser
	* 
	* Description:
	* Il s'agit du constructeur de la classe. Il permet de construire la boite 
	* de dialogue en lui fournissant une r�f�rence sur l'interface de la 
	* fen�tre principale.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur un objet MainWindowInterface 
	*    correspondant � la fen�tre principale de l'application,
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
 	* ----------------------------------------------------------*/
 	public RemoteFileChooser(
 		MainWindowInterface windowInterface,
 		String agentLayerMode
 		)
 	{
 		super((Frame)windowInterface, true);
 		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "RemoteFileChooser");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		_selectionMode = SELECT_FILES;
		_acceptType = ACCEPT_ALL;
		_presetType = PRESET_NONE;
		_windowInterface = windowInterface;
		_agentLayerMode = agentLayerMode;
		// On interdit le redimensionnement
		setResizable(false);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setSelectionMode
	* 
	* Description:
	* Cette m�thode permet de d�finir le mode de s�lection des entr�es du 
	* syst�me de fichiers distant.
	* Le mode de s�lection peut �tre soit SELECT_FILES, soit 
	* SELECT_DIRECTORIES.
	* Suivant le mode, le comportement de la boite de dialogue sera diff�rent:
	*  - SELECT_FILES: L'utilisateur ne pourra choisir qu'un fichier,
	*  - SELECT_DICTIONARIES: L'utilisateur ne pourra choisir qu'un r�pertoire.
	* 
	* Arguments:
	*  - selectionMode: Le mode de s�lection.
 	* ----------------------------------------------------------*/
 	public void setSelectionMode(
 		int selectionMode
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "setSelectionMode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectionMode=" + selectionMode);
		if(selectionMode != SELECT_FILES && 
			selectionMode != SELECT_DIRECTORIES)
		{
			trace_errors.writeTrace("Mode de s�lection invalide !");
		}
		else
		{
			_selectionMode = selectionMode;
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setAcceptType
	* 
	* Description:
	* Cette m�thode permet de d�finir le type d'acceptation concernant les 
	* entr�es du syst�me de fichiers distant.
	* Le type d'acceptation peut �tre soit ACCEPT_EXISTING, soit ACCEPT_ALL.
	* Suivant le type d'acceptation, le comportement de la boite de dialogue 
	* sera diff�rent:
	*  - ACCEPT_EXISTING: L'utilisateur ne pourra choisir qu'un fichier ou un 
	*    r�pertoire �xistant,
	*  - ACCEPT_ALL: L'utilisateur pourra sp�cifier un fichier ou un 
	*    r�pertoire inexistant.
	* 
	* Arguments:
	*  - acceptType: Le type d'acceptation.
 	* ----------------------------------------------------------*/
 	public void setAcceptType(
 		int acceptType
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "setAcceptType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("acceptType=" + acceptType);
		if(acceptType != ACCEPT_EXISTING && acceptType != ACCEPT_ALL)
		{
			trace_errors.writeTrace("Type d'acceptation invalide !");
		}
		else
		{
			_acceptType = acceptType;
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setPresetType
	* 
	* Description:
	* Cette m�thode permet de d�finir le type de pr�s�lection des r�pertoires 
	* de l'exploration du syst�me de fichiers distant.
	* Le type de pr�s�lection est soit PRESET_NONE, soit PRESET_EXECUTABLES, 
	* soit PRESET_DICTIONARIES, soit PRESET_DATAFILES ou enfin 
	* PRESET_COMMANDPANELS.
	* Suivant le type de pr�s�lection, le comportement de la boite de dialogue 
	* sera diff�rent:
	*  - PRESET_NONE: La zone "Rechercher dans" ne contiendra que la liste des 
	*    racines du syst�me de fichiers distant,
	*  - PRESET_EXECUTABLES: La zone "Rechercher dans" contiendra la liste des 
	*    r�pertoires de recherche des ex�cutables ainsi que les racines du 
	*    syst�me de fichiers distant,
	*  - PRESET_DICTIONARIES: La zone "Rechercher dans" contiendra la liste 
	*    des r�pertoires de recherche des dictionnaires ainsi que les racines 
	*    du syst�me de fichiers distant,
	*  - PRESET_DATAFILES: La zone "Rechercher dans" contiendra la liste des 
	*    r�pertoires de recherche des fichiers de donn�es ainsi que les 
	*    racines du syst�me de fichiers distant,
	*  - PRESET_COMMANDPANELS: La zone "Rechercher dans" contiendra la liste 
	*    des r�pertoires de recherche des panneaux de commandes ainsi que les 
	*    racines du syst�me de fichiers distant.
	* 
	* Arguments:
	*  - presetType: Le type de pr�s�lection.
 	* ----------------------------------------------------------*/
 	public void setPresetType(
 		int presetType
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "setPresetType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("presetType=" + presetType);
		if(presetType != PRESET_NONE &&
			presetType != PRESET_EXECUTABLES &&
			presetType != PRESET_DICTIONARIES &&
			presetType != PRESET_DATAFILES &&
			presetType != PRESET_COMMANDPANELS)
		{
			trace_errors.writeTrace("Type de pr�s�lection invalide !");
		}
		else
		{
			_presetType = presetType;
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: showDialog
	* 
	* Description:
	* Cette m�thode est charg�e de la construction de la boite de dialogue, 
	* via un appel � la m�thode makePanel(), puis de son affichage.
	* La m�thode est bloquante jusqu'� ce que l'utilisateur ait valid� son 
	* choix ou annul�.
	* 
	* Si une erreur survient lors de la construction de la boite de dialogue, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - serviceSession: Une r�f�rence sur l'interface ServiceSessionInterface 
	*    correspondant � la session � travers laquelle doit �tre effectu�e 
	*    l'exploration,
	*  - parent: Une r�f�rence sur un objet Component correspondant au 
	*    composant graphique parent de la boite de dialogue,
	*  - initialDirectory: Le r�pertoire initial � afficher dans la boite 
	*    de dialogue. Peut �tre null.
	* 
	* Retourne: APPROVE_OPTION si l'utilisateur a valid� sa s�lection, 
	* CANCEL_OPTION dans le cas contraire.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public int showDialog(
		ServiceSessionInterface serviceSession,
		Component parent,
		String initialDirectory
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "showDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int return_value = CANCEL_OPTION;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("initialDirectory=" + initialDirectory);
		// On v�rifie la validit� de l'argument
		if(serviceSession == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On va lever une exception
			throw new InnerException(
				MessageManager.getMessage("&ERR_InvalidArgument"), null, null);
		}
		// On va cr�er le proxy pour la session de service
		_sessionProxy = new ServiceSessionProxy(serviceSession);
		// On va r�cup�rer les racines du syst�me de fichiers
		_roots = _sessionProxy.getFileSystemRoots();
		// On va d�clencher la cr�ation de la boite de dialogue
		makePanel();
		// On va pr�s�lectionner la premi�re entr�e de la liste des
		// pr�s�lections
		if(initialDirectory == null || initialDirectory.equals("") == true)
		{
			_presetPaths.setSelectedIndex(0);
		}
		else
		{
			_presetPaths.setSelectedItem(initialDirectory);
		}
		// On va afficher la bo�te de dialogue
		if(parent != null)
		{
			setLocationRelativeTo(parent);
		}
		show();
		// Si on arrive ici, c'est que l'utilisateur a annul� ou
		// valid� sa s�lection
		// On regarde s'il y a un chemin de s�lectionn�
		if(_selectedFilePath != null && _selectedFilePath.equals("") == false)
		{
			return_value = APPROVE_OPTION;
		}
		// On va s'auto-d�truire
		dispose();
		trace_methods.endOfMethod();
		return return_value;
	}

	/*----------------------------------------------------------
	* Nom: getSelection
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin absolu de la s�lection de 
	* l'utilisateur, qu'il s'agisse d'un fichier ou d'un r�pertoire.
	* 
	* Retourne: Le chemin absolu de la s�lection de l'utilisateur, ou null si 
	* celui-ci a annul� sa s�lection.
	* ----------------------------------------------------------*/
	public String getSelection()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "getSelection");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _selectedFilePath;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: FileSystemEntryHolder
	* 
	* Description:
	* Cette classe est charg�e de contenir toutes les informations 
	* relatives � une entr�e du syst�me de fichiers distant.
	* Cela concerne le nom de l'entr�e, son chemin absolu, son type, sa 
	* taille et sa date de modification.
	* Elle est destin�e � �tre utilis�e par les diff�rentes vues de 
	* l'exploration de syst�me de fichiers distant.
	* ----------------------------------------------------------*/
	private class FileSystemEntryHolder
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: FileSystemEntryHolder
		* 
		* Description:
		* Il s'agit du seul constructeur pouvant �tre utilis�. Il permet 
		* d'associer l'entr�e du syst�me de fichiers distant � l'objet 
		* courant.
		* 
		* Arguments:
		*  - entry: Une r�f�rence sur un objet FileSystemEntry 
		*    correspondant � l'entr�e du syst�me de fichiers distant.
 		* ----------------------------------------------------------*/
		public FileSystemEntryHolder(
			FileSystemEntry entry
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "FileSystemEntryHolder");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("entry=" + entry);
			// On positionne les valeurs pour les attributs
			_entry = entry;
			_entrySize = -1;
			_entryModificationTime = -1;
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: getEntryName
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer le nom de l'entr�e du syst�me 
		* de fichiers distant.
		* Le nom est r�cup�r� depuis la r�f�rence sur l'objet 
		* FileSystemEntry stock� dans l'attribut _entry.
		* 
		* Retourne: Le nom de l'entr�e du syst�me de fichiers distant.
		* ----------------------------------------------------------*/
		public String getEntryName()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "getEntryName");
			String entry_name = "";
		
			trace_methods.beginningOfMethod();
			if(_entry != null) {
				entry_name = _entry.entryName;
			}
			trace_methods.endOfMethod();
			return entry_name;
		}

		/*----------------------------------------------------------
		* Nom: getEntryPath
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer le chemin absolu de l'entr�e 
		* du syst�me de fichiers distant.
		* Le chemin absolu est r�cup�r� depuis la r�f�rence sur l'objet 
		* FileSystemEntry stock� dans l'attribut _entry.
		* 
		* Retourne: Le chemin absolu de l'entr�e du syst�me de fichiers 
		* distant.
		* ----------------------------------------------------------*/
		public String getEntryPath()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "getEntryPath");
			String entry_path = "";
		
			trace_methods.beginningOfMethod();
			if(_entry != null) {
				entry_path = _entry.entryPath;
			}
			trace_methods.endOfMethod();
			return entry_path;
		}

		/*----------------------------------------------------------
		* Nom: getEntryType
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer le type de l'entr�e du syst�me 
		* de fichiers distant.
		* Le type est r�cup�r� depuis la r�f�rence sur l'objet 
		* FileSystemEntry stock� dans l'attribut _entry.
		* 
		* Retourne: Le type de l'entr�e du syst�me de fichiers distant, 
		* sous forme d'un EntryTypeEnum.
		* ----------------------------------------------------------*/
		public EntryTypeEnum getEntryType()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "getEntryType");
			EntryTypeEnum entry_type = EntryTypeEnum.ALL;
		
			trace_methods.beginningOfMethod();
			if(_entry != null) {
				entry_type = _entry.entryType;
			}
			trace_methods.endOfMethod();
			return entry_type;
		}

		/*----------------------------------------------------------
		* Nom: getEntrySize
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer la taille de l'entr�e du 
		* syst�me de fichiers distant.
		* La taille est r�cup�r�e via l'attribut _entrySize.
		* 
		* Retourne: La taille de l'entr�e du syst�me de fichiers distant, 
		* en octets.
		* ----------------------------------------------------------*/
		public long getEntrySize()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "getEntrySize");
		
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _entrySize;
		}

		/*----------------------------------------------------------
		* Nom: getEntryModificationTime
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer la date de modification de 
		* l'entr�e du syst�me de fichiers distant.
		* La date de modification est r�cup�r�e via l'attribut 
		* _entryModificationTime.
		* 
		* Retourne: La date de modification de l'entr�e du syst�me de 
		* fichiers distant, en nombre de secondes depuis le 01/01/1970.
		* ----------------------------------------------------------*/
		public long getEntryModificationTime()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "getEntryModificationTime");
		
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _entryModificationTime;
		}

		/*----------------------------------------------------------
		* Nom: setEntrySize
		* 
		* Description:
		* Cette m�thode permet de positionner la taille de l'entr�e du 
		* syst�me de fichiers distant.
		* Elle positionne la valeur de l'attribut _entrySize.
		* 
		* Arguments:
		*  - entrySize: La taille, en octets, de l'entr�e du syst�me de 
		*    fichiers distant.
 		* ----------------------------------------------------------*/
		public void setEntrySize(
			long entrySize
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "setEntrySize");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("entrySize=" + entrySize);
			_entrySize = entrySize;
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: setEntryModificationTime
		* 
		* Description:
		* Cette m�thode permet de positionner la date de modification de 
		* l'entr�e du syst�me de fichiers distant.
		* Elle positionne la valeur de l'attribut _entryModificationTime.
		* 
		* Arguments:
		*  - entryModificationTime: La date de modification, en nombre de 
		*    secondes depuis le 01/01/1970, de l'entr�e du syst�me de 
		*    fichiers distant.
 		* ----------------------------------------------------------*/
		public void setEntryModificationTime(
			long entryModificationTime
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "setEntryModificationTime");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("entryModificationTime=" + 
				entryModificationTime);
			_entryModificationTime = entryModificationTime;
			trace_methods.endOfMethod();
		}

		// ****************** PROTEGE *********************
		/*----------------------------------------------------------
		* Nom: finalize
		* 
		* Description:
		* Cette m�thode est automatiquement appel�e par le ramasse 
		* miettes de la machine virtuelle Java lorsque l'instance de 
		* la classe est sur le point d'�tre d�truite. Elle permet de 
		* lib�rer les ressources allou�es par celle-ci.
		* ----------------------------------------------------------*/
		protected void finalize()
			throws
				Throwable
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemEntryHolder", "finalize");
		
			trace_methods.beginningOfMethod();
			_entry = null;
			trace_methods.endOfMethod();
		}

		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _entrySize
		* 
		* Description:
		* Cet attribut maintient la taille de l'entr�e du syst�me de 
		* fichiers distant, en octets.
		* ----------------------------------------------------------*/
		private long _entrySize;

		/*----------------------------------------------------------
		* Nom: _entryModificationTime
		* 
		* Description:
		* Cet attribut maintient la date de modification de l'entr�e du 
		* syst�me de fichiers distant, en nombre de secondes depuis le 
		* 01/01/1970.
		* ----------------------------------------------------------*/
		private long _entryModificationTime;

		/*----------------------------------------------------------
		* Nom: _entry
		* 
		* Description:
		* Cet attribut maintient une r�f�rence sur un objet 
		* FileSystemEntry correspondant � l'entr�e du syst�me de fichiers 
		* distant qui doit �tre repr�sent�e dans la fen�tre d'exploration.
		* ----------------------------------------------------------*/
		private FileSystemEntry _entry;
	}
	
	/*----------------------------------------------------------
	* Nom: FileSystemListCellRenderer
	* 
	* Description:
	* Cette classe embarqu�e est charg�e de l'affichage des �l�ments de liste 
	* correspondant aux fichiers et r�pertoires distants.
	* Pour cela, elle sp�cialise la classe RemoteFileCellRenderer.
	* ----------------------------------------------------------*/
	private class FileSystemListCellRenderer
		extends DefaultListCellRenderer
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: FileSystemListCellRenderer
		* 
		* Description:
		* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
		* lisibilit�.
		* ----------------------------------------------------------*/
		public FileSystemListCellRenderer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemListCellRenderer", "FileSystemListCellRenderer");
		
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: getListCellRendererComponent
		* 
		* Description:
		* Cette m�thode red�finit celle de la super-classe 
		* DefaultListCellRenderer. Elle est appel�e afin de fournir un 
		* composant charg� de la repr�sentation d'un �l�ment de la liste.
		* Dans le cas pr�sent, l'ic�ne associ�e � l'�l�ment d�pend du type 
		* d'entr�e (r�pertoire ou fichier), et le nom affich� correspond au 
		* nom (court) de l'entr�e.
		* 
		* Arguments:
		*  - list: Une r�f�rence sur la JList,
		*  - value: Une r�f�rence sur un Objet correspondant � l'objet � 
		*    repr�senter,
		*  - index: L'index de la valeur dans la liste,
		*  - isSelected: Un bool�en indiquant si l'�l�ment est s�lectionn�,
		*  - cellHasFocus: Un bool�en indiquant si l'�l�ment a le focus.
		* 
		* Retourne: Une r�f�rence sur un objet Component charg� de la 
		* repr�sentation graphique de l'entr�e du syst�me de fichiers distant.
		* ----------------------------------------------------------*/
		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus
			)
		{
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemListCellRenderer", "getListCellRendererComponent");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");*/
		
			/*trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("list=" + list);
			trace_arguments.writeTrace("value=" + value);
			trace_arguments.writeTrace("index=" + index);
			trace_arguments.writeTrace("isSelected=" + isSelected);
			trace_arguments.writeTrace("cellHasFocus=" + cellHasFocus);*/
			// On appelle la m�thode de la super-classe
			super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
			if(value != null && value instanceof FileSystemEntryHolder)
			{
				FileSystemEntryHolder entry = (FileSystemEntryHolder)value;
				
				setText(entry.getEntryName());
				if(entry.getEntryType() == EntryTypeEnum.DIRECTORY)
				{
					setIcon(IconLoader.getIcon("Directory"));
				}
				else
				{
					setIcon(IconLoader.getIcon("File"));
				}
			}
			//trace_methods.endOfMethod();
			return this;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	}

	/*----------------------------------------------------------
	* Nom: FileSystemTableModel
	* 
	* Description:
	* Cette classe permet de g�rer le mod�le des entr�es du syst�me de 
	* fichiers distant pour l'affichage sous forme de tableau.
	* Il sp�cialise la classe DefaultTableModel afin de profiter des 
	* fonctionnalit�s offertes par celle-ci.
	* Elle red�finit la m�thode getValueAt() afin de traiter les demandes 
	* de valeur au niveau des colonnes, dont les valeurs sont contenues 
	* dans les objets FileSystemEntryHolder stock�s au niveau du mod�le 
	* parent.
	* ----------------------------------------------------------*/
	private class FileSystemTableModel
		extends DefaultTableModel
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: FileSystemTableModel
		* 
		* Description:
		* Il s'agit du constructeur par d�faut. Il permet d'ajouter 
		* automatiquement les colonnes n�cessaires � l'affichage des 
		* entr�es du syst�me de fichiers distant sous forme de tableau.
		* ----------------------------------------------------------*/
		public FileSystemTableModel()
		{
			super();

			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemTableModel", "FileSystemTableModel");
		
			trace_methods.beginningOfMethod();
			// On va cr�er les colonnes
			addColumn(MessageManager.getMessage("&RFCEntryType"));
			addColumn(MessageManager.getMessage("&RFCEntryName"));
			addColumn(MessageManager.getMessage("&RFCEntrySize"));
			addColumn(MessageManager.getMessage("&RFCEntryModificationTime"));
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: getValueAt
		* 
		* Description:
		* Cette m�thode red�finit celle de la super-classe 
		* DefaultTableModel. Elle est appel�e afin de r�cup�rer la valeur 
		* d'une cellule dont le rang et le num�ro de colonne sont pass�s 
		* en argument.
		* La m�thode extrait la valeur correspondant au num�ro de colonne 
		* de l'objet FileSystemEntryHolder stock� au rang indiqu�.
		* 
		* Si les valeurs row ou column sont incorrectes, l'exception 
		* ArrayIndexOutOfBoundsException est lev�e.
		* 
		* Arguments:
		*  - row: L'indice du rang de la cellule,
		*  - column: L'indice de la colonne de la cellule.
		* 
		* Retourne: Un objet contenant la valeur de la cellule.
		* 
		* L�ve: ArrayIndexOutOfBoundsException.
		* ----------------------------------------------------------*/
		public Object getValueAt(
			int row,
			int column
			)
			throws
				ArrayIndexOutOfBoundsException
		{
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemTableModel", "FileSystemTableModel");*/
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			FileSystemEntryHolder entry = null;
			Object value = null;
		
			//trace_methods.beginningOfMethod();
			// On commence par r�cup�rer l'objet au rang row depuis le
			// mod�le parent
			entry = (FileSystemEntryHolder)super.getValueAt(row, 0);
			// On v�rifie qu'il y a bien une valeur
			if(entry == null) {
				trace_errors.writeTrace("Il n'y a aucune entr�e au rang " +
					row);
				// On l�ve une exception
				//trace_methods.endOfMethod();
				throw new ArrayIndexOutOfBoundsException(row);
			}
			// On v�rifie que le num�ro de colonne est valide (entre 0 et 3)
			if(column < 0 || column > 3) {
				trace_errors.writeTrace("Indice de colonne incorrect: " +
					column);
				// On l�ve une exception
				//trace_methods.endOfMethod();
				throw new ArrayIndexOutOfBoundsException(column);
			}
			// On va r�cup�rer la valeur correspondant � l'indice de la
			// colonne
			switch(column) {
				case 0: // Le type: d pour r�pertoire, f sinon 
					if(entry.getEntryType() == EntryTypeEnum.DIRECTORY) {
						value ="d";
					}
					else {
						value = "f";
					}
					break;
				case 1: // Le nom de l'entr�e
					value = entry.getEntryName();
					break;
				case 2: // La taille de l'entr�e
					value = new Long(entry.getEntrySize());
					break;
				default: // La date de modification
					value = new Long(entry.getEntryModificationTime());
					break;
			}
			//trace_methods.endOfMethod();
			return value;
		}
		
		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	}

	/*----------------------------------------------------------
	* Nom: FileSystemTableCellRenderer
	* 
	* Description:
	* Cette classe est charg�e de la r�pr�sentation d'une entr�e de 
	* syst�me de fichiers distant dans un tableau.
	* Elle sp�cialise la classe DefaultTableCellRenderer afin de profiter 
	* des fonctionnalit�s d�j� impl�ment�es.
	* Elle red�finit la m�thode getTableCellRendererComponent() afin de 
	* sp�cialiser l'affichage des donn�es.
	* ----------------------------------------------------------*/
	private class FileSystemTableCellRenderer
		extends DefaultTableCellRenderer
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: FileSystemTableCellRenderer
		* 
		* Description:
		* Il s'agit du constructeur par d�faut. Il n'est pr�sent� que pour 
		* des raisons de lisibilit�.
		* ----------------------------------------------------------*/
		public FileSystemTableCellRenderer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemTableCellRenderer", "FileSystemTableCellRenderer");
		
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
		}
			
		/*----------------------------------------------------------
		* Nom: getTableCellRendererComponent
		* 
		* Description:
		* Cette m�thode red�finit celle de la super-classe 
		* DefaultTableCellRenderer. Elle appel�e afin de r�cup�rer un 
		* composant charg� de la repr�sentation de la cellule identifi�e 
		* par le couple (row,column).
		* Suivant l'indice de colonne, une repr�sentation diff�rente de la 
		* donn�e est retourn�e : 
		*  - Pour la premi�re colonne, une ic�ne est charg�e de repr�senter 
		*    le type de l'entr�e,
		*  - Pour la seconde colonne, la repr�sentation par d�faut est 
		*    conserv�e,
		*  - Pour la troisi�me colonne, la taille est sp�cifi�e avec une 
		*    unit�,
		*  - Pour la derni�re colonne, la date est reformat�e de mani�re 
		*    lisible.
		* 
		* Arguments:
		*  - table: Une r�f�rence sur un objet JTable correspondant � la 
		*    table dont la cellule doit �tre affich�e,
		*  - value: Une r�f�rence sur un objet correspondant � la valeur � 
		*    afficher,
		*  - isSelected: Un bool�en indiquant si la cellule est s�lectionn�e,
		*  - hasFocus: Un bool�en indiquant si la cellule a le focus,
		*  - row: L'indice du rang de la cellule,
		*  - column: L'indice de colonne de la cellule.
		* 
		* Retourne: Une r�f�rence sur un objet Component charg� du rendu de 
		* la valeur.
		* ----------------------------------------------------------*/
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
			)
		{
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"FileSystemTableCellRenderer", "getTableCellRendererComponent");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");*/
			long long_value = -1;
		
			/*trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("table=" + table);
			trace_arguments.writeTrace("value=" + value);
			trace_arguments.writeTrace("isSelected=" + isSelected);
			trace_arguments.writeTrace("hasFocus=" + hasFocus);
			trace_arguments.writeTrace("row=" + row);
			trace_arguments.writeTrace("column=" + column);*/
			// On appelle la m�thode de la super-classe
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
			// On va sp�cialiser l'affichage en fonction de l'indice de
			// colonne
			switch(column) {
				case 0: // Ic�ne repr�sentant le type de l'entr�e
					String string_value = getText();
					setText("");
					if(string_value.compareToIgnoreCase("D") == 0) {
						setIcon(IconLoader.getIcon("Directory"));
					}
					else {
						setIcon(IconLoader.getIcon("File"));
					}
					setHorizontalAlignment(this.CENTER);
					break;
				case 2: // Taille de l'entr�e
					long_value = ((Long)value).longValue();
					if(long_value >= 0) {
						int unit = 0;
						
						// On va monter dans les unit�s pour avoir un 
						// affiche r�duit (sans d�passer le To)
						while(long_value >= 1024 && unit < 4) {
							unit ++;
							long_value /= 1024;
						}
						setText("" + long_value + " " +
							MessageManager.getMessage("&RFCUnit" + unit));
					}
					else {
						setText("");
					}
					setHorizontalAlignment(this.RIGHT);
					break;
				case 3: // Date de modification de l'entr�e
					long_value = ((Long)value).longValue();
					if(long_value >= 0) {
						Date date_value = new Date(long_value * 1000);
						SimpleDateFormat formatter = new SimpleDateFormat(
							MessageManager.getMessage("&DateFormat"));
						setText(formatter.format(date_value));
					}
					else {
						setText("");
					}
					setHorizontalAlignment(this.CENTER);
					break;
				default: // Il ne reste que le nom de l'entr�e, on laisse
						 // que l'entr�e
					break;
			}
			//trace_methods.endOfMethod();
			return this;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	}

	/*----------------------------------------------------------
	* Nom: _selectionMode
	* 
	* Description:
	* Cet attribut maintient la valeur du mode de s�lection.
	* Les deux valeurs possibles sont SELECT_FILES (par d�faut) et 
	* SELECT_DIRECTORIES.
	* ----------------------------------------------------------*/
	private int _selectionMode;

	/*----------------------------------------------------------
	* Nom: _acceptType
	* 
	* Description:
	* Cet attribut maintient la valeur du type d'acceptation.
	* Les deux valeurs possibles sont ACCEPT_EXISTING (d�faut) et ACCEPT_ALL.
	* ----------------------------------------------------------*/
	private int _acceptType;

	/*----------------------------------------------------------
	* Nom: _presetType
	* 
	* Description:
	* Cet attribut maintient la valeur du type de pr�s�lection.
	* Les valeurs possibles sont PRESET_NONE (d�faut), PRESET_EXECUTABLES, 
	* PRESET_DICTIONARIES, PRESET_DATAFILES et PRESET_COMMANDPANELS.
	* ----------------------------------------------------------*/
	private int _presetType;

	/*----------------------------------------------------------
	* Nom: _selectedFilePath
	* 
	* Description:
	* Cet attribut maintient le chemin sur le fichier, ou le r�pertoire, 
	* s�lectionn� par l'utilisateur.
	* ----------------------------------------------------------*/
	private String _selectedFilePath;

	/*----------------------------------------------------------
	* Nom: _agentLayerMode
	* 
	* Description:
	* Cet attribut maintient le mode de la couche d'ex�cution de l'Agent 
	* concern�.
	* ----------------------------------------------------------*/
	private String _agentLayerMode;

	/*----------------------------------------------------------
	* Nom: _windowInterface
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur une interface 
	* MainWindowInterface correspondant � la fen�tre principale de 
	* l'application.
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;

	/*----------------------------------------------------------
	* Nom: _sessionProxy
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet ServiceSessionProxy 
	* n�cessaire � l'exploration du syst�me de fichiers distant.
	* ----------------------------------------------------------*/
	private ServiceSessionProxy _sessionProxy;

	/*----------------------------------------------------------
	* Nom: _roots
	* 
	* Description:
	* Cet attribut maintient un tableau de FileSystemEntry correspondant � 
	* l'ensemble des racines du syst�me de fichiers de la plate-forme distante.
	* ----------------------------------------------------------*/
	private FileSystemEntry[] _roots;

	/*----------------------------------------------------------
	* Nom: _presetPaths
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JComboBox 
	* correspondant � la liste des pr�s�lections des r�pertoires.
	* ----------------------------------------------------------*/
	private JComboBox _presetPaths;

	/*----------------------------------------------------------
	* Nom: _entriesList
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JList correspondant � 
	* la liste des entr�es du r�pertoire courant sous forme de liste.
	* ----------------------------------------------------------*/
	private JList _entriesList;

	/*----------------------------------------------------------
	* Nom: _fileNameField
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField 
	* correspondant � la zone de saisie de l'entr�e s�lectionn�e.
	* ----------------------------------------------------------*/
	private JTextField _fileNameField;

	/*----------------------------------------------------------
	* Nom: _approveButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton correspondant 
	* au bouton de validation de la s�lection.
	* ----------------------------------------------------------*/
	private JButton _approveButton;

	/*----------------------------------------------------------
	* Nom: _entriesTable
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NonEditableTable 
	* correspondant � la liste des entr�es du r�pertoire courant sous 
	* forme de tableau.
	* ----------------------------------------------------------*/
	private NonEditableTable _entriesTable;

	/*----------------------------------------------------------
	* Nom: _entriesScrollPane
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JScrollPane dans 
	* lequel est plac� soit la liste soit le tableau d'affichage des entr�es 
	* du syst�me de fichiers distant.
	* ----------------------------------------------------------*/
	private JScrollPane _entriesScrollPane;

	/*----------------------------------------------------------
	* Nom: _displayModeCombo
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JComboBox 
	* correspondant � la liste des modes d'affichage des entr�es du 
	* syst�me de fichiers distant.
	* ----------------------------------------------------------*/
	private JComboBox _displayModeCombo;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est charg�e de la construction du panneau de la boite de 
	* dialogue.
	* Le panneau est compos� de:
	*  - un JComboBox permettant la s�lection de r�pertoires pr�-d�finis, la 
	*    saisie d'un r�pertoire, ou encore l'affichage du r�pertoire courant,
	*  - une liste contenant les entr�es du r�pertoire courant,
	*  - une zone de saisie permettant �ventuellement la saisie d'un nom d'un 
	*    fichier ou d'un r�pertoire inexistant,
	*  - des boutons "Ok" et "Annuler".
	* 
	* Si une erreur survient lors de la construction du panneau de la boite de 
	* dialogue, l'exception InnerException est lev�e.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void makePanel()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "makePanel");
		boolean display_as_list = true;
		
		trace_methods.beginningOfMethod();
		// Lecture des pr�f�rences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("GUI");
			display_as_list = preferences.getBoolean("FS.DisplayAsList");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		getContentPane().setLayout(new BorderLayout());
		// On va cr�er le panneau central
		JPanel central_panel = new JPanel();
		// On va cr�er le LayoutManager pour celui-ci
		GridBagLayout central_layout = new GridBagLayout();
		GridBagConstraints central_constraints = 
			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(1, 1, 1, 1), 0, 0);
		central_panel.setLayout(central_layout);
		// On va cr�er le label pour la zone "Regarder dans"
		JLabel lookin_label = 
			new JLabel(MessageManager.getMessage("&RFCLookin"));
		central_layout.setConstraints(lookin_label, central_constraints);
		central_panel.add(lookin_label);
		// On va maintenant cr�er la ComboBox correspondant � la liste des
		// pr�s�lections
		_presetPaths = new JComboBox();
		_presetPaths.setEditable(true);
		// On va ins�rer les informations en fonction de la liste de
		// pr�s�lection
		fillInPresetPaths();
		// On va ajouter le listener sur la s�lection
		_presetPaths.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// On va appeler la m�thode presetPathSelected()
				presetPathSelected();
			}
		});
		// Il ne reste plus qu'� ajouter la liste dans le panneau
		central_constraints.gridx ++;
		central_constraints.weightx = 1;
		central_constraints.fill = GridBagConstraints.BOTH;
		central_constraints.anchor = GridBagConstraints.CENTER;
		central_layout.setConstraints(_presetPaths, central_constraints);
		central_panel.add(_presetPaths);
		// On va ajouter un ComboBox destin� � g�rer le mode d'affichage
		String[] display_modes = {
			MessageManager.getMessage("&RFCList"),
			MessageManager.getMessage("&RFCDetails")
		};
		_displayModeCombo = new JComboBox(display_modes);
		_displayModeCombo.setEditable(false);
		if(display_as_list == true) {
			_displayModeCombo.setSelectedIndex(0);
		}
		else {
			_displayModeCombo.setSelectedIndex(1);
		}
		// On va ajouter le listener sur la s�lection
		_displayModeCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// On va appeler la m�thode displayModeChanged()
				displayModeChanged();
			}
		});
		// On ajoute le combo au panneau
		central_constraints.gridx ++;
		central_constraints.weightx = 0;
		central_layout.setConstraints(_displayModeCombo, central_constraints);
		central_panel.add(_displayModeCombo);
		// On va maintenant cr�er la liste destin�e � contenir les entr�es
		// du syst�me de fichiers
		_entriesList = new JList(new DefaultListModel());
		_entriesList.setCellRenderer(new FileSystemListCellRenderer());
		_entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_entriesList.setLayoutOrientation(JList.VERTICAL_WRAP);
		_entriesList.setVisibleRowCount(-1);
		// On va ajouter le listener sur la s�lection
		_entriesList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				if(event.getValueIsAdjusting() == false)
				{
					// On appelle la m�thode entrySelected()
					entrySelected();
				}
			}
		});
		// On va ajouter un listener pour g�rer le double-click
		_entriesList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				// On v�rifie qu'il s'agit d'un double-click
				if(event.getClickCount() == 2)
				{
					// Il ne reste plus qu'� appeler la m�thode 
					// entryDoubleClicked()
					entryDoubleClicked();
				}
			}
		});
		// On va cr�er le tableau destin� � afficher les entr�es
		_entriesTable = new NonEditableTable(new FileSystemTableModel(), ";");
		// On va positionner le gestionnaire de rendu sur les colonnes
		_entriesTable.getColumnModel().getColumn(0).setCellRenderer(
			new FileSystemTableCellRenderer());
		_entriesTable.getColumnModel().getColumn(1).setCellRenderer(
			new FileSystemTableCellRenderer());
		_entriesTable.getColumnModel().getColumn(2).setCellRenderer(
			new FileSystemTableCellRenderer());
		_entriesTable.getColumnModel().getColumn(3).setCellRenderer(
			new FileSystemTableCellRenderer());
		// On va positionner les param�tres de s�lection
		_entriesTable.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION);
		_entriesTable.setColumnSelectionAllowed(false);
		// On va ajuster les dimensions des colonnes
		_entriesTable.getColumnModel().getColumn(0).setPreferredWidth(70);
		_entriesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		_entriesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		_entriesTable.getColumnModel().getColumn(3).setPreferredWidth(140);
		_entriesTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// On va s'assurer d'un tri sur les deux premi�res colonnes
		SortedTableModel table_model = 
			(SortedTableModel)_entriesTable.getModel();
		table_model.setSortingStatus(0, SortedTableModel.ASCENDING);
		table_model.setSortingStatus(1, SortedTableModel.ASCENDING);
		// On va ajouter le listener sur la s�lection
		_entriesTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting() == false) {
					// On appelle la m�thode entrySelected()
					entrySelected();
				}
			}
		});
		// On va ajouter un listener pour g�rer le double-click
		_entriesTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				// On v�rifie qu'il s'agit d'un double-click
				if(event.getClickCount() == 2)
				{
					// Il ne reste plus qu'� appeler la m�thode 
					// entryDoubleClicked()
					entryDoubleClicked();
				}
			}
		});
		// On va mettre la liste dans un JScrollPane
		_entriesScrollPane = new JScrollPane();
		_entriesScrollPane.setPreferredSize(new Dimension(450, 200));
		_entriesScrollPane.setBorder(BorderFactory.createEtchedBorder());
		if(display_as_list == true) {
			_entriesScrollPane.setViewportView(_entriesList);
			_entriesScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			_entriesScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		else {
			_entriesScrollPane.setViewportView(_entriesTable);
			_entriesScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			_entriesScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		// On peut ajouter la liste dans le panneau central
		central_constraints.gridx = 0;
		central_constraints.gridy ++;
		central_constraints.gridwidth = 3;
		central_constraints.weightx = 1;
		central_constraints.weighty = 1;
		central_layout.setConstraints(_entriesScrollPane, central_constraints);
		central_panel.add(_entriesScrollPane);
		// Il faut maintenant cr�er le label pour le nom du fichier
		JLabel selection_label = null;
		if(_selectionMode == SELECT_FILES)
		{
			setTitle(MessageManager.getMessage("&RFCSelectFile"));
			selection_label = 
				new JLabel(MessageManager.getMessage("&RFCSelectedFile"));
		}
		else
		{
			setTitle(MessageManager.getMessage("&RFCSelectDirectory"));
			selection_label = 
				new JLabel(MessageManager.getMessage("&RFCSelectedDirectory"));
		}
		central_constraints.gridy ++;
		central_constraints.gridwidth = 1;
		central_constraints.weightx = 0;
		central_constraints.weighty = 0;
		central_constraints.anchor = GridBagConstraints.EAST;
		central_constraints.fill = GridBagConstraints.NONE;
		central_layout.setConstraints(selection_label, central_constraints);
		central_panel.add(selection_label);
		// Enfin, il faut ajouter la zone de saisie pour le nom du fichier
		_fileNameField = new JTextField();
		if(_acceptType == ACCEPT_EXISTING)
		{
			_fileNameField.setEnabled(false);
		}
		else
		{
			// On ajoute un callback sur la saisie
			_fileNameField.addKeyListener(new KeyAdapter()
			{
				public void keyReleased(KeyEvent e)
				{
					// On va appeler la m�thode fileNameChanged()
					fileNameChanged();
				}
			});
		}
		// On l'ajoute au panneau central
		central_constraints.gridx ++;
		central_constraints.weightx = 1;
		central_constraints.gridwidth = 2;
		central_constraints.anchor = GridBagConstraints.CENTER;
		central_constraints.fill = GridBagConstraints.BOTH;
		central_layout.setConstraints(_fileNameField, central_constraints);
		central_panel.add(_fileNameField);
		// On cr�e un panneau avec un flow layout
		JPanel button_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On cr�e le bouton Valider
		_approveButton =
			new JButton(MessageManager.getMessage("&RFCOkButton"));
		_approveButton.setEnabled(false);
		// Ajout du callback sur le click
		_approveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode approveSelection()
				approveSelection();
			}
		});
		// On ajoute le bouton
		button_panel.add(_approveButton);
		// On cr�e le bouton "Annuler"
		JButton cancel_button =	
			new JButton(MessageManager.getMessage("&RFCCancelButton"));
		// On ajoute le bouton
		button_panel.add(cancel_button);
		// On ajoute le callback sur le click
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On a juste � masquer la boite de dialogue
				setVisible(false);
			}
		});
		// On calcule la dimension de tous les boutons
		int button_width = Math.max(_approveButton.getPreferredSize().width,
			cancel_button.getPreferredSize().width);
		Dimension size = new Dimension(button_width,
			_approveButton.getPreferredSize().height);
		_approveButton.setPreferredSize(size);
		cancel_button.setPreferredSize(size);
		// On ajoute la panneau dans le panneau central
		central_constraints.gridx = 0;
		central_constraints.gridy ++;
		central_constraints.gridwidth = 3;
		central_constraints.anchor = GridBagConstraints.CENTER;
		central_constraints.fill = GridBagConstraints.NONE;
		central_constraints.weightx = 1;
		central_layout.setConstraints(button_panel, central_constraints);
		central_panel.add(button_panel);
		// On n'a plus qu'� ajouter le panneau central au centre du panneau
		// de la boite de dialogue
		getContentPane().add(central_panel, BorderLayout.CENTER);
		pack();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: changeDirectoryTo
	* 
	* Description:
	* Cette m�thode est appel�e � l'issue de la construction de la boite de 
	* dialogue, puis lorsque l'utilisateur a double-cliqu� sur un r�pertoire 
	* dans la liste des entr�es du r�pertoire courant, ou encore lorsqu'il a 
	* s�lectionn� ou saisi un nouveau r�pertoire dans la liste de pr�s�lection.
	* Le chemin du r�pertoire courant est mis � jour, puis la liste des 
	* entr�es du r�pertoire courant est mise � jour en fonction des 
	* informations r�cup�r�es via la m�thode getDirectoryEntries() de la 
	* classe ServiceSessionProxy.
	* 
	* Arguments:
	*  - directory: Le chemin absolu du nouveau r�pertoire courant.
	* 
	* Retourne: true si le changement de r�pertoire s'est bien d�roul�, false 
	* sinon.
	* ----------------------------------------------------------*/
	private boolean changeDirectoryTo(
		String directory
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "changeToDirectory");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean directory_changed_ok = false;
		DefaultListModel list_model = null;
		FileSystemTableModel table_model = null;
		EntryTypeEnum entry_type = EntryTypeEnum.ALL;
		boolean at_root_level;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("directory=" + directory);
		// On va v�rifier que l'argument est valide
		if(directory == null || directory.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide, ou le " +
				"r�pertoire est le m�me");
			// On sort
			return directory_changed_ok;
		}
		_windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, this);
		// On va r�cup�rer le mod�le de la liste ou celui du tableau
		if(_displayModeCombo.getSelectedIndex() == 0) {
			list_model = (DefaultListModel)_entriesList.getModel();
			// On va effacer la liste des entr�es
			list_model.clear();
			_entriesList.clearSelection();
		}
		else {
			table_model = (FileSystemTableModel)
				((SortedTableModel)_entriesTable.getModel()).getModel();
			// On va effacer la liste des entr�es
			table_model.getDataVector().clear();
			_entriesTable.clearSelection();
		}
		at_root_level = isAtRootLevel(directory);
		// On va r�cup�rer la liste des entr�es du r�pertoire
		try
		{
			FileSystemEntry[] entries = 
				_sessionProxy.getDirectoryEntries(directory, entry_type);
			// On va ajouter les entr�es dans la liste, en comman�ant par les
			// r�pertoires
			for(int index = 0 ; index < entries.length ; index ++)
			{
				if((entries[index].entryName.equals(".") == true) ||
					(at_root_level == true && 
					entries[index].entryName.equals("..") == true) ||
					entries[index].entryType != EntryTypeEnum.DIRECTORY)
				{
					continue;
				}
				trace_debug.writeTrace("Ajout de l'entr�e: " +
					entries[index].entryName);
				if(list_model != null) {
					list_model.addElement(createEntryHolder(entries[index]));
				}
				else {
					FileSystemEntryHolder[] holders = {
						createEntryHolder(entries[index])
					};
					table_model.addRow(holders);
				}
			}
			// On va maintenant ajouter les fichiers
			for(int index = 0 ; index < entries.length ; index ++)
			{
				if(entries[index].entryType == EntryTypeEnum.DIRECTORY)
				{
					continue;
				}
				trace_debug.writeTrace("Ajout de l'entr�e: " +
					entries[index].entryName);
				if(list_model != null) {
					list_model.addElement(createEntryHolder(entries[index]));
				}
				else {
					FileSystemEntryHolder[] holders = {
						createEntryHolder(entries[index])
					};
					table_model.addRow(holders);
				}
			}
			directory_changed_ok = true;
			// On va s'assurer que la barre de d�filement vertical est
			// remont�e
			JScrollBar vertical_scroll_bar = 
				_entriesScrollPane.getVerticalScrollBar();
			if(vertical_scroll_bar != null) {
				vertical_scroll_bar.setValue(vertical_scroll_bar.getMinimum());
			}
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors la r�cup�ration des entr�es " +
				"du r�pertoire " + directory + ": " + exception.getMessage());
			// On va afficher un message d'erreur � l'utilisateur
			_windowInterface.showPopupForException(
				"&ERR_CannotExploreDirectory", exception);
		}
		_windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		trace_methods.endOfMethod();
		return directory_changed_ok;
	}

	/*----------------------------------------------------------
	* Nom: presetPathSelected
	* 
	* Description:
	* Cette m�thode est automatiquement appel�e lorsque l'utilisateur a 
	* s�lectionn� un r�pertoire depuis la liste des pr�s�lections ou qu'il 
	* en a saisi un nouveau puis tap� sur la touche "Entr�e".
	* Le chemin du r�pertoire s�lectionn� ou saisi est r�cup�r� puis la 
	* m�thode changeDirectoryTo() est appel�e.
	* ----------------------------------------------------------*/
	private void presetPathSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "presetPathSelected");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String new_directory;
		
		trace_methods.beginningOfMethod();
		// On va r�cup�rer le r�pertoire s�lectionn�
		new_directory = (String)_presetPaths.getSelectedItem();
		trace_debug.writeTrace("new_directory=" + new_directory);
		if(new_directory != null && new_directory.equals("") == false)
		{
			// S'il y a un '/' ou un '\' � la fin du r�pertoire, on va le 
			// retirer, sauf s'il s'agit du r�pertoire racine
			if(isAtRootLevel(new_directory) == false)
			{
				int new_directory_length = new_directory.length();
				if(new_directory.charAt(new_directory_length - 1) == '/' ||
					new_directory.charAt(new_directory_length - 1) == '\\')
				{
					new_directory = new_directory.substring(0, 
						new_directory_length - 1);
					_presetPaths.setSelectedItem(new_directory);
				}
			}
			// Il y a un r�pertoire de s�lectionn�, on provoque le changement
			// de r�pertoire
			changeDirectoryTo(new_directory);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: entrySelected
	* 
	* Description:
	* Cette m�thode est automatiquement appel�e lorsqu'un �l�ment de la liste 
	* des entr�es du r�pertoire courant est s�lectionn�.
	* Suivant le mode de s�lection, la zone de saisie de la s�lection, et 
	* l'�tat du bouton "Ok" sont mis � jour.
	* ----------------------------------------------------------*/
	private void entrySelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "entrySelected");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		FileSystemEntryHolder selected_entry = null;
		String entry_name;
		EntryTypeEnum entry_type;
		
		trace_methods.beginningOfMethod();
		if(_displayModeCombo.getSelectedIndex() == 0) {
			selected_entry = 
				(FileSystemEntryHolder)_entriesList.getSelectedValue();
		}
		else {
			FileSystemTableModel table_model = (FileSystemTableModel)
				((SortedTableModel)_entriesTable.getModel()).getModel();
			int selected_row = _entriesTable.getSelectedRow();
			if(selected_row >= 0) {
				Vector<FileSystemEntryHolder> row_vector = 
					(Vector<FileSystemEntryHolder>)
					(table_model.getDataVector().elementAt(selected_row));
				selected_entry = row_vector.elementAt(0);
			}
		}
		if(selected_entry == null)
		{
			_approveButton.setEnabled(false);
			_fileNameField.setText("");
			trace_methods.endOfMethod();
			return;
		}
		entry_name = selected_entry.getEntryName();
		entry_type = selected_entry.getEntryType();
		trace_debug.writeTrace("selected_entry=" + entry_name);
		trace_debug.writeTrace("entry type=" + entry_type);
		// On v�rifie que la s�lection correspond � ce qui est suppos� �tre
		// choisi
		if((_selectionMode == SELECT_DIRECTORIES && 
			entry_type == EntryTypeEnum.DIRECTORY &&
			entry_name.equals("..") == false) ||
			(_selectionMode == SELECT_FILES &&
			entry_type == EntryTypeEnum.FILE))
		{
			_fileNameField.setText(entry_name);
			_approveButton.setEnabled(true);
		}
		else
		{
			_approveButton.setEnabled(false);
			_fileNameField.setText("");
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: entryDoubleClicked
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a double-cliqu� sur une 
	* des entr�es du syst�me de fichiers.
	* Si l'entr�e est de type r�pertoire, l'action consiste � commander un 
	* changement de r�pertoire via un appel � la m�thode changeDirectoryTo().
	* Dans le cas contraire, aucune action n'est effectu�e.
	* ----------------------------------------------------------*/
	private void entryDoubleClicked()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "entryDoubleClicked");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		FileSystemEntryHolder selected_entry = null;
		String base_directory = null;
		String new_directory = null;
		
		trace_methods.beginningOfMethod();
		if(_displayModeCombo.getSelectedIndex() == 0) {
			selected_entry = 
				(FileSystemEntryHolder)_entriesList.getSelectedValue();
		}
		else {
			FileSystemTableModel table_model = (FileSystemTableModel)
				((SortedTableModel)_entriesTable.getModel()).getModel();
			int selected_row = _entriesTable.getSelectedRow();
			if(selected_row >= 0) {
				Vector<FileSystemEntryHolder> row_vector = 
					(Vector<FileSystemEntryHolder>)
					(table_model.getDataVector().elementAt(selected_row));
				selected_entry = row_vector.elementAt(0);
			}
		}
		base_directory = (String)_presetPaths.getSelectedItem();
		trace_debug.writeTrace("base_directory=" + base_directory);
		if(selected_entry != null)
		{
			String entry_name = selected_entry.getEntryName();
			EntryTypeEnum entry_type = selected_entry.getEntryType();
			trace_debug.writeTrace("selected_entry=" + entry_name);
			trace_debug.writeTrace("entry type=" + entry_type);
			// On v�rifie que l'entr�e correspond � un r�pertoire
			if(entry_type == EntryTypeEnum.DIRECTORY)
			{
				new_directory = AgentLayerAbstractor.buildFilePath(
					_agentLayerMode, base_directory, entry_name);
				// On va commander un changement de r�pertoire
				trace_debug.writeTrace("new_directory=" + new_directory);
				_presetPaths.setSelectedItem(new_directory);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isAtRootLevel
	* 
	* Description:
	* Cette m�thode permet de savoir si le r�pertoire pass� en argument 
	* correspond � une des racines du syst�me de fichiers distant.
	* 
	* Arguments:
	*  - directory: Le r�pertoire � tester.
	* 
	* Retourne: true si le r�pertoire correspond � l'une des racines du 
	* syst�me de fichiers, false sinon.
	* ----------------------------------------------------------*/
	private boolean isAtRootLevel(
		String directory
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "isAtRootLevel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean is_at_root_level = false;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("directory=" + directory);
		if(directory == null || directory.equals("") == true)
		{
			trace_methods.endOfMethod();
			return is_at_root_level;
		}
		// On va regarder dans la liste des racines du syst�me de fichiers
		for(int index = 0 ; index < _roots.length ; index ++)
		{
			if(_roots[index].entryPath.equalsIgnoreCase(directory) == true)
			{
				is_at_root_level = true;
				break;
			}
		}
		trace_methods.endOfMethod();
		return is_at_root_level;
	}

	/*----------------------------------------------------------
	* Nom: fillInPresetPaths
	* 
	* Description:
	* Cette m�thode est charg�e du remplissage de la liste des pr�s�lections 
	* des chemins d'exploration du syst�me de fichiers de la plate-forme 
	* distante.
	* Suivant la valeur de l'attribut _presetType, des chemins sont ajout�s, 
	* puis la liste des racines du syst�me de fichiers est ajout�e.
	* ----------------------------------------------------------*/
	private void fillInPresetPaths()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "fillInPresetPaths");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String[] preset_directories = new String[0];
		
		trace_methods.beginningOfMethod();
		try
		{
			// On va r�cup�rer les r�pertoires en fonction de l'attribut
			// _presetType
			switch(_presetType)
			{
				case PRESET_NONE:
					break;
				case PRESET_EXECUTABLES:
					preset_directories = 
						_sessionProxy.getExecutableDirectories(
						_agentLayerMode);
					break;
				case PRESET_DICTIONARIES:
					preset_directories = 
						_sessionProxy.getDictionaryDirectories(
						_agentLayerMode);
					break;
				case PRESET_DATAFILES:
					preset_directories = 
						_sessionProxy.getDataDirectories(
						_agentLayerMode);
					break;
				case PRESET_COMMANDPANELS:
					preset_directories = 
						_sessionProxy.getControlPanelDirectories(
						_agentLayerMode);
					break;
			}
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration des " +
				"r�pertoires de pr�s�lection: " + exception.getMessage());
		}
		// On va ajouter les r�pertoire de pr�s�lection
		for(int index = 0 ; index < preset_directories.length ; index ++)
		{
			trace_debug.writeTrace("Ajout du r�pertoire: " + 
				preset_directories[index]);
			_presetPaths.addItem(preset_directories[index]);
		}
		for(int index = 0 ; index < _roots.length ; index ++)
		{
			trace_debug.writeTrace("Ajout du r�pertoire: " + 
				_roots[index].entryPath);
			_presetPaths.addItem(_roots[index].entryPath);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: approveSelection
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Valider".
	* La m�thode va constituer le chemin de la s�lection, l'enregistre dans l'
	* attribut _selectedFilePath, puis masque la boite de dialogue afin de 
	* lib�rer l'ex�cution de la m�thode showDialog().
	* ----------------------------------------------------------*/
	private void approveSelection()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("&Console",
			"RemoteFileChooser", "approveSelection");
		String base_directory = null;
		String file_name = null;
		
		trace_methods.beginningOfMethod();
		// On v�rifie que le bouton est bien activ�
		if(_approveButton.isEnabled() == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On va fixer la valeur de la s�lection
		base_directory = (String)_presetPaths.getSelectedItem();
		file_name = _fileNameField.getText();
		if(base_directory == null || base_directory.equals("") == true ||
			file_name == null || file_name.equals("") == true)
		{
			_approveButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		_selectedFilePath = AgentLayerAbstractor.buildFilePath(
			_agentLayerMode, base_directory, file_name);
		// Il ne reste plus qu'� masquer la boite de dialogue pour
		// lib�rer l'ex�cution de la m�thode showDialog().
		setVisible(false);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fileNameChanged
	* 
	* Description:
	* Cette m�thode est automatiquement appel�e lorsque l'utilisateur a 
	* modifi� le nom du fichier dans la zone de saisie.
	* Elle ne peut �tre appel�e que lorsque le mode d'acceptation permet la 
	* s�lection de fichier, ou de r�pertoires, inexistants.
	* Son but est de mettre � jour l'�tat du bouton "Valider" en fonction du 
	* nom saisi.
	* ----------------------------------------------------------*/
	private void fileNameChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "fileNameChanged");
		String file_name = null;
		DefaultListModel model = null;
		
		trace_methods.beginningOfMethod();
		// On va r�cup�rer le nom du fichier (ou du r�pertoire)
		file_name = _fileNameField.getText();
		if(file_name == null || file_name.equals("") == true)
		{
			// On d�sactive le bouton "Valider"
			_approveButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// On va regarder s'il existe une entr�e avec ce nom
		model = (DefaultListModel)_entriesList.getModel();
		for(int index = 0 ; index < model.getSize() ; index ++)
		{
			FileSystemEntryHolder entry = 
				(FileSystemEntryHolder)model.elementAt(index);
			if(entry.getEntryName().equals(file_name) == true)
			{
				// Il existe bien une entr�e avec le m�me nom, on va regarder 
				// si elle a le bon type
				if((_selectionMode == SELECT_FILES && 
					entry.getEntryType() == EntryTypeEnum.FILE) ||
					(_selectionMode == SELECT_DIRECTORIES &&
					entry.getEntryType() == EntryTypeEnum.DIRECTORY))
				{
					// La s�lection correspond � un bon type, on
					// peut sortir de la boucle
					break;
				}
				// La s�lection ne correspond pas au bon type, on
				// va d�sactiver le bouton "Valider"
				_approveButton.setEnabled(false);
				trace_methods.endOfMethod();
				return;
			}
		}
		// Si on arrive ici, c'est qu'il n'y a aucune entr�e avec le
		// m�me nom, on va activer le bouton "Valider"
		_approveButton.setEnabled(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: createEntryHolder
	* 
	* Description:
	* Cette m�thode est charg�e de cr�er et de retourner un objet 
	* FileSystemEntryHolder � partir de l'objet FileSystemEntry pass� en 
	* argument.
	* Si l'attribut _displayAsList vaut false, les informations de taille 
	* et de date de modification sont r�cup�r�es pour �tre ajout�es � 
	* l'objet FileSystemEntryHolder cr��.
	* 
	* Arguments:
	*  - entry: Un objet FileSystemEntry pour lequel un objet 
	*    FileSystemEntryHolder doit �tre cr��.
	* 
	* Retourne: Un objet FileSystemEntryHolder cr�� � partir de l'entr�e 
	* pass�e en argument.
	* ----------------------------------------------------------*/
	private FileSystemEntryHolder createEntryHolder(
		FileSystemEntry entry
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "createEntryHolder");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		FileSystemEntryHolder entry_holder = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("entry=" + entry);
		// On commence par cr�er l'objet FileSystemEntryHolder
		entry_holder = new FileSystemEntryHolder(entry);
		// Faut-il r�cup�rer les informations de taille ?
		if(_displayModeCombo.getSelectedIndex() == 1) {
			try {
				FileReaderInterface reader_interface = 
					_sessionProxy.getFileReader(entry.entryPath);
				entry_holder.setEntryModificationTime(
					reader_interface.getFileModificationTime());
				entry_holder.setEntrySize(
					reader_interface.getFileSize());
			}
			catch(Exception exception) {
				trace_errors.writeTrace("Erreur lors de la r�cup�ration " +
					"des donn�es de l'entr�e " + entry.entryPath + ": " +
					exception.getMessage());
			}
		}
		trace_methods.endOfMethod();
		return entry_holder;
	}

	/*----------------------------------------------------------
	* Nom: displayModeChanged
	* 
	* Description:
	* Cette m�thode est charg�e de g�rer les changements de mode 
	* d'affichage des entr�es du syst�me de fichiers distant.
	* En mode liste, la liste _entriesList est associ�e au panneau de 
	* d�filement _entriesScrollPane. En mode d�taill�, le tableau 
	* _entriesTable est associ� au panneau de d�filement.
	* Les s�lections sont effac�es, puis le contenu de l'affichage est 
	* r�actualis� via un appel � la m�thode presetPathSelected().
	* ----------------------------------------------------------*/
	private void displayModeChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RemoteFileChooser", "displayModeChanged");
		
		trace_methods.beginningOfMethod();
		if(_displayModeCombo.getSelectedIndex() == 0) {
			// On efface les donn�es et la s�lection de la table
			FileSystemTableModel table_model = (FileSystemTableModel)
				((SortedTableModel)_entriesTable.getModel()).getModel();
			table_model.getDataVector().clear();
			_entriesTable.clearSelection();
			// On positionne la liste comme composant � afficher
			_entriesScrollPane.setViewportView(_entriesList);
			// On red�finit les politiques des barres de d�filement
			_entriesScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			_entriesScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		else {
			// On efface les donn�es et la s�lection de la liste
			DefaultListModel list_model = 
				(DefaultListModel)_entriesList.getModel();
			list_model.clear();
			_entriesList.clearSelection();
			// On positionne la table comme composant � afficher
			_entriesScrollPane.setViewportView(_entriesTable);
			// On red�finit les politiques des barres de d�filement
			_entriesScrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			_entriesScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		// Il faut maintenant commander le rafra�chissement du contenu
		// de l'affichage
		presetPathSelected();
		trace_methods.endOfMethod();
	}
}
