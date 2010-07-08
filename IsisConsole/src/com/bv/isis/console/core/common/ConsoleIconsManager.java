/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/ConsoleIconsManager.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe technique de recherche d'icônes
* DATE:        13/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConsoleIconsManager.java,v $
* Revision 1.3  2009/01/14 14:16:26  tz
* Classe déplacée dans le package com.bv.isis.console.core.common.
*
* Revision 1.2  2008/05/23 10:48:48  tz
* Extensions et tailles des icônes en configuration.
*
* Revision 1.1  2006/03/13 15:13:08  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.common;

//
//Imports système
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
* Cette classe abstraite est une classe technique chargée de la recherche des 
* icônes de la Console I-SIS.
* Elle offre trois méthodes de recherche d'icônes :
*  - getAllIcons(), permettant de récupérer les listes des trois types 
*    d'icônes (icônes de noeuds, icônes de méthodes et autres icônes),
*  - getNodeIcons(), permettant de récupérer la liste des icônes de noeuds,
*  - getMethodIcons(), permettant de récupérer la liste des icônes de méthodes.
* ----------------------------------------------------------*/
public abstract class ConsoleIconsManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getAllIcons
	* 
	* Description:
	* Cette méthode statique est chargée de récupérer la liste de toutes les 
	* icônes disponibles, en allant rechercher tous les fichiers images 
	* (d'extension ".gif" ".jpg" ou ".png") dans le répertoire des icônes de 
	* la Console.
	* Ensuite, chaque icône est chargée en mémoire, via la classe IconLoader, 
	* puis les dimensions des icônes sont récupérées.
	* Suivant ces dimensions, les identifiants des icônes (nom du fichier 
	* image sans son extension), sont placés dans l'un des trois vecteurs 
	* passés en argument. Les données de ces vecteurs sont stockées dans 
	* d'autres vecteurs, où le premier élément est l'icône, et le deuxième son 
	* identifiant.
	* 
	* Arguments:
	*  - nodeIcons: Une référence sur un vecteur destiné à contenir les icônes 
	*    de noeuds,
	*  - methodIcons: Une référence sur un vecteur destiné à contenir les 
	*    icônes de méthodes,
	*  - otherIcons: Une référence sur un vecteur destiné à contenir les 
	*    autres icônes,
	*  - rowsHeight: Une référence sur un vecteur destiné à contenir les 
	*    hauteurs des autres icônes.
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
		// On va récupérer le répertoire des fichiers images, ainsi que
		// les dimensions des icônes
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
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"paramètre: " + exception);
			// On continue quand même
		}
		tokenizer = new UtilStringTokenizer(extensions, " ");
		String images_directory = base_directory + File.separatorChar +
			MessageManager.getCurrentLanguage() + File.separatorChar +
			"icons";
		trace_debug.writeTrace("images_directory=" + images_directory);
		File directory = new File(images_directory);
		// On vérifie que le répertoire existe
		if(directory.exists() == false || directory.isDirectory() == false)
		{
			trace_errors.writeTrace("Le répertoire " + images_directory +
				" n'existe pas ou n'est pas un répertoire !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va récupérer tous les fichiers enfants
		File[] files = directory.listFiles();
		// On va traiter chaque élément
		for(int index = 0 ; index < files.length ; index ++)
		{
			Vector vector_to_use = null;
			String icon_id = null;

			String file_name = files[index].getName();
			trace_debug.writeTrace("Traitement de l'élément " + file_name);
			// S'il s'agit d'un répertoire on passe au suivant
			if(files[index].isDirectory() == true)
			{
				trace_debug.writeTrace("Le fichier est un répertoire");
				continue;
			}
			for(int loop = 0 ; loop < tokenizer.getTokensCount() ; loop ++) {
				// Si le fichier n'a pas l'extension de fichier image on continue
				if(file_name.endsWith(tokenizer.getToken(loop)) == false)
				{
					continue;
				}
				// On va récupérer l'icône de l'image
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
			// On teste les dimensions de l'icône
			if(icon.getIconHeight() == node_icons_size && 
				icon.getIconWidth() == node_icons_size)
			{
				// Il s'agit d'une icône de noeud
				trace_debug.writeTrace("Ajout de l'icône " + icon_id + 
					" à la liste des icônes de noeuds");
				vector_to_use = nodeIcons;
			}
			else if(icon.getIconHeight() == method_icons_size && 
				icon.getIconWidth() == method_icons_size)
			{
				// Il s'agit d'une icône de méthode
				trace_debug.writeTrace("Ajout de l'icône " + icon_id + 
					" à la liste des icônes de méthodes");
				vector_to_use = methodIcons;
			}
			else
			{
				// Il s'agit d'une icône "autre"
				trace_debug.writeTrace("Ajout de l'icône " + icon_id + 
					" à la liste des autres icônes");
				vector_to_use = otherIcons;
				if(rowsHeight != null)
				{
					rowsHeight.add(new Integer(icon.getIconHeight()));
				}
			}
			// On ajoute les données au vecteur
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
	* Cette méthode statique est chargée de récupérer la liste de toutes les 
	* icônes de noeuds, en allant rechercher tous les fichiers images 
	* (d'extension ".gif") dans le répertoire des icônes de la Console, dont 
	* la dimension est de 13x13.
	* Les données de ces icônes sont stockées dans des vecteurs, où le premier 
	* élément est l'icône, et le deuxième son identifiant.
	* 
	* Retourne: Un vecteur contenant la liste des icônes de noeuds.
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
	* Cette méthode statique est chargée de récupérer la liste de toutes les 
	* icônes de méthodes, en allant rechercher tous les fichiers images 
	* (d'extension ".gif") dans le répertoire des icônes de la Console, dont 
	* la dimension est de 24x24.
	* Les données de ces icônes sont stockées dans des vecteurs, où le premier 
	* élément est l'icône, et le deuxième son identifiant.
	* 
	* Retourne: Un vecteur contenant la liste des icônes de méthodes.
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
