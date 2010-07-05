/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/ConsoleIconsManager.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe technique de recherche d'ic�nes
* DATE:        13/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConsoleIconsManager.java,v $
* Revision 1.3  2009/01/14 14:16:26  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.common.
*
* Revision 1.2  2008/05/23 10:48:48  tz
* Extensions et tailles des ic�nes en configuration.
*
* Revision 1.1  2006/03/13 15:13:08  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.common;

//
//Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.IconLoader;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.util.UtilStringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import java.io.File;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: ConsoleIconsManager
* 
* Description:
* Cette classe abstraite est une classe technique charg�e de la recherche des 
* ic�nes de la Console I-SIS.
* Elle offre trois m�thodes de recherche d'ic�nes :
*  - getAllIcons(), permettant de r�cup�rer les listes des trois types 
*    d'ic�nes (ic�nes de noeuds, ic�nes de m�thodes et autres ic�nes),
*  - getNodeIcons(), permettant de r�cup�rer la liste des ic�nes de noeuds,
*  - getMethodIcons(), permettant de r�cup�rer la liste des ic�nes de m�thodes.
* ----------------------------------------------------------*/
public abstract class ConsoleIconsManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getAllIcons
	* 
	* Description:
	* Cette m�thode statique est charg�e de r�cup�rer la liste de toutes les 
	* ic�nes disponibles, en allant rechercher tous les fichiers images 
	* (d'extension ".gif" ".jpg" ou ".png") dans le r�pertoire des ic�nes de 
	* la Console.
	* Ensuite, chaque ic�ne est charg�e en m�moire, via la classe IconLoader, 
	* puis les dimensions des ic�nes sont r�cup�r�es.
	* Suivant ces dimensions, les identifiants des ic�nes (nom du fichier 
	* image sans son extension), sont plac�s dans l'un des trois vecteurs 
	* pass�s en argument. Les donn�es de ces vecteurs sont stock�es dans 
	* d'autres vecteurs, o� le premier �l�ment est l'ic�ne, et le deuxi�me son 
	* identifiant.
	* 
	* Arguments:
	*  - nodeIcons: Une r�f�rence sur un vecteur destin� � contenir les ic�nes 
	*    de noeuds,
	*  - methodIcons: Une r�f�rence sur un vecteur destin� � contenir les 
	*    ic�nes de m�thodes,
	*  - otherIcons: Une r�f�rence sur un vecteur destin� � contenir les 
	*    autres ic�nes,
	*  - rowsHeight: Une r�f�rence sur un vecteur destin� � contenir les 
	*    hauteurs des autres ic�nes.
 	* ----------------------------------------------------------*/
 	public static void getAllIcons(
 		Vector nodeIcons,
 		Vector methodIcons,
 		Vector otherIcons,
 		Vector rowsHeight
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleIconsManager", "getAllIcons");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String base_directory = "data";
		String extensions = ".gif .jpg .png";
		int node_icons_size = 13;
		int method_icons_size = 24;
		UtilStringTokenizer tokenizer = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("nodeIcons=" + nodeIcons);
		trace_arguments.writeTrace("methodIcons=" + methodIcons);
		trace_arguments.writeTrace("otherIcons=" + otherIcons);
		trace_arguments.writeTrace("rowsHeight=" + rowsHeight);
		// Si tous les arguments sont nulls, on peut sortir
		if(nodeIcons == null && methodIcons == null && otherIcons == null &&
			rowsHeight == null)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va r�cup�rer le r�pertoire des fichiers images, ainsi que
		// les dimensions des ic�nes
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			base_directory = configuration.getString("Console",
				"DataPath");
			extensions = configuration.getString("CORE", 
				"IconExtensions.List");
			node_icons_size = configuration.getInt("GUI", "NodeIcons.Size");
			method_icons_size = configuration.getInt("MethodIcons.Size");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration du " +
				"param�tre: " + exception);
			// On continue quand m�me
		}
		tokenizer = new UtilStringTokenizer(extensions, " ");
		String images_directory = base_directory + File.separatorChar +
			MessageManager.getCurrentLanguage() + File.separatorChar +
			"icons";
		trace_debug.writeTrace("images_directory=" + images_directory);
		File directory = new File(images_directory);
		// On v�rifie que le r�pertoire existe
		if(directory.exists() == false || directory.isDirectory() == false)
		{
			trace_errors.writeTrace("Le r�pertoire " + images_directory +
				" n'existe pas ou n'est pas un r�pertoire !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va r�cup�rer tous les fichiers enfants
		File[] files = directory.listFiles();
		// On va traiter chaque �l�ment
		for(int index = 0 ; index < files.length ; index ++)
		{
			Vector vector_to_use = null;
			String icon_id = null;

			String file_name = files[index].getName();
			trace_debug.writeTrace("Traitement de l'�l�ment " + file_name);
			// S'il s'agit d'un r�pertoire on passe au suivant
			if(files[index].isDirectory() == true)
			{
				trace_debug.writeTrace("Le fichier est un r�pertoire");
				continue;
			}
			for(int loop = 0 ; loop < tokenizer.getTokensCount() ; loop ++) {
				// Si le fichier n'a pas l'extension de fichier image on continue
				if(file_name.endsWith(tokenizer.getToken(loop)) == false)
				{
					continue;
				}
				// On va r�cup�rer l'ic�ne de l'image
				icon_id = file_name.substring(0, 
					file_name.indexOf(tokenizer.getToken(loop)));
			}
			// Si le fichier n'a pas l'extension de fichier image on continue
			if(icon_id == null)
			{
				trace_debug.writeTrace("Le fichier n'a pas la bonne extension");
				continue;
			}
			Icon icon = IconLoader.getIcon(icon_id);
			// On teste les dimensions de l'ic�ne
			if(icon.getIconHeight() == node_icons_size && 
				icon.getIconWidth() == node_icons_size)
			{
				// Il s'agit d'une ic�ne de noeud
				trace_debug.writeTrace("Ajout de l'ic�ne " + icon_id + 
					" � la liste des ic�nes de noeuds");
				vector_to_use = nodeIcons;
			}
			else if(icon.getIconHeight() == method_icons_size && 
				icon.getIconWidth() == method_icons_size)
			{
				// Il s'agit d'une ic�ne de m�thode
				trace_debug.writeTrace("Ajout de l'ic�ne " + icon_id + 
					" � la liste des ic�nes de m�thodes");
				vector_to_use = methodIcons;
			}
			else
			{
				// Il s'agit d'une ic�ne "autre"
				trace_debug.writeTrace("Ajout de l'ic�ne " + icon_id + 
					" � la liste des autres ic�nes");
				vector_to_use = otherIcons;
				if(rowsHeight != null)
				{
					rowsHeight.add(new Integer(icon.getIconHeight()));
				}
			}
			// On ajoute les donn�es au vecteur
			if(vector_to_use != null)
			{
				Vector values = new Vector();
				values.add(icon);
				values.add(icon_id);
				vector_to_use.add(values);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getNodeIcons
	* 
	* Description:
	* Cette m�thode statique est charg�e de r�cup�rer la liste de toutes les 
	* ic�nes de noeuds, en allant rechercher tous les fichiers images 
	* (d'extension ".gif") dans le r�pertoire des ic�nes de la Console, dont 
	* la dimension est de 13x13.
	* Les donn�es de ces ic�nes sont stock�es dans des vecteurs, o� le premier 
	* �l�ment est l'ic�ne, et le deuxi�me son identifiant.
	* 
	* Retourne: Un vecteur contenant la liste des ic�nes de noeuds.
 	* ----------------------------------------------------------*/
 	public static Vector getNodeIcons()
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleIconsManager", "getNodeIcons");
		Vector node_icons = new Vector();
			
		trace_methods.beginningOfMethod();
		getAllIcons(node_icons, null, null, null);
		trace_methods.endOfMethod();
		return node_icons;
 	}

	/*----------------------------------------------------------
	* Nom: getMethodIcons
	* 
	* Description:
	* Cette m�thode statique est charg�e de r�cup�rer la liste de toutes les 
	* ic�nes de m�thodes, en allant rechercher tous les fichiers images 
	* (d'extension ".gif") dans le r�pertoire des ic�nes de la Console, dont 
	* la dimension est de 24x24.
	* Les donn�es de ces ic�nes sont stock�es dans des vecteurs, o� le premier 
	* �l�ment est l'ic�ne, et le deuxi�me son identifiant.
	* 
	* Retourne: Un vecteur contenant la liste des ic�nes de m�thodes.
	* ----------------------------------------------------------*/
	public static Vector getMethodIcons()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleIconsManager", "getMethodIcons");
		Vector method_icons = new Vector();
			
		trace_methods.beginningOfMethod();
		getAllIcons(null, method_icons, null, null);
		trace_methods.endOfMethod();
		return method_icons;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
