/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/ConfigFileProcessor.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement d'un patch de fichier de configuration
* DATE:        23/12/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConfigFileProcessor.java,v $
* Revision 1.2  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.1  2005/12/23 13:23:42  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: ConfigFileProcessor
* 
* Description:
* Cette classe est une classe d'implémentation permettant le traitement d'une 
* entrée de type patch de fichier de configuration de la Console I-SIS.
* Son objectif est de lire le contenu du fichier de patch, de lire le contenu 
* du fichier de configuration, d'appliquer les modifications au contenu du 
* fichier de configuration, puis de réécrire ce dernier en y incluant les 
* modifications.
* Elle spécialise la classe LaxFileProcessor, laquelle implémente la majorité 
* des fonctionnalités.
* ----------------------------------------------------------*/
public class ConfigFileProcessor 
	extends LaxFileProcessor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ConfigFileProcessor
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public ConfigFileProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "ConfigFileProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: patchFile
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe LaxFileProcessor. Elle 
	* est chargée d'appliquer le patch, préalablement lu, au fichier à patcher, 
	* également préalablement chargé.
	* Chaque ligne du fichier patch est décomposé en 2 ou 3 champs, suivant la 
	* syntaxe:
	* <Mot-clé> <Section>[:<Paramètre>] [<Valeur>]
	* 
	* Cinq types d'opérations peuvent être effectuées sur un fichier de 
	* configuration:
	*  - L'ajout de section,
	*  - La suppression de section,
	*  - L'ajout de paramètre,
	*  - La modification de paramètre,
	*  - La suppression de paramètre.
	* 
	* L'ajout de section est signifié par le mot-clé "ADD_SECTION". Un nom de 
	* section doit être fourni. Aucun nom de paramètre ou valeur n'est 
	* nécessaire.
	* La suppression de section est signifié par le mot-clé "REMOVE_SECTION". 
	* Un nom de section doit être fourni. Aucun nom de paramètre ou valeur 
	* n'est nécessaire.
	* L'ajout de paramètre est signifié par le mot-clé "ADD_PARAMETER". Un nom 
	* de paramètre et une valeur doivent être fournis.
	* La modification de paramètre est signifiée par le mot-clé 
	* "UPDATE_PARAMETER". Un nom de paramètre et une valeur doiventt être 
	* fournis. La valeur est soit fixe, soit relative à l'ancienne valeur 
	* (en utilisant la chaîne "%[value]").
	* La suppression de paramètre est signifiée par le mot-clé 
	* "REMOVE_PARAMETER". Le nom du paramètre doit être fourni, et aucune 
	* valeur n'est nécessaire.
	* 
	* Si un problème survient pendant l'application du patch, 
	* l'exception InnerException est levée.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected void patchFile()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "patchFile");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On va appliquer l'ensemble des patchs
		while(_patchContent.size() > 0)
		{
			String instruction = null;
			String section_parameter = null;
			String section_name = null;
			String parameter_name = null;
			String value = null;
			String patch_instruction = (String)_patchContent.elementAt(0);
			_patchContent.remove(0);
			trace_debug.writeTrace("patch_instruction=" + patch_instruction);
			// Découpage de la ligne suivant le séparateur ' '
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(patch_instruction, " ");
			// Il doit y avoir au moins deux sous-chaînes
			if(tokenizer.getTokensCount() < 2)
			{
				trace_errors.writeTrace(
					"Le format de la ligne de patch n'est pas valide !");
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			// On récupère de toute façon les deux premières sous-chaînes
			instruction = tokenizer.nextToken();
			section_parameter = tokenizer.nextToken();
			trace_debug.writeTrace("instruction=" + instruction);
			trace_debug.writeTrace("section_parameter=" + section_parameter);
			// Suivant la valeur de l'instruction, on effectue le traitement
			// adéquat
			if(instruction.equalsIgnoreCase("ADD_SECTION") == true)
			{
				trace_debug.writeTrace("Ajout d'une section");
				section_name = section_parameter;
				trace_debug.writeTrace("section_name=" + section_name);
				// On récupère la description
				String description = 
					patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				trace_debug.writeTrace("description=" + description);
				// On vérifie que la section n'existe pas déjà
				if(getSectionPosition(section_name) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute la section à la fin
				_fileContent.add("");
				_fileContent.add("###########################################" +
					"######################");
				_fileContent.add("###");
				_fileContent.add("### " + description);
				_fileContent.add("###");
				_fileContent.add("###########################################" +
					"######################");
				_fileContent.add("[" + section_name + "]");
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("REMOVE_SECTION") == true)
			{
				trace_debug.writeTrace("Suppression d'une section");
				section_name = section_parameter;
				trace_debug.writeTrace("section_name=" + section_name);
				// On récupère la position de départ de la section, et celle
				// de la section suivante
				int section_start = getSectionPosition(section_name);
				if(section_start == -1)
				{
					// La section n'existe pas, on passe au suivant
					continue;
				}
				int section_end = getNextSectionPosition(section_start + 1);
				// On rembobine les commentaires
				section_start = rewindComments(section_start);
				int number_of_elements = section_end - section_start - 1;
				trace_debug.writeTrace("section_start=" + section_start);
				trace_debug.writeTrace("section_end=" + section_end);
				trace_debug.writeTrace("number_of_elements=" + 
					number_of_elements);
				// Il faut supprimer toutes les données entre les deux positions
				while(number_of_elements > 0)
				{
					_fileContent.remove(section_start);
					number_of_elements --;
				}
				// On passe au suivant
				continue;
			}
			// A partir d'ici, on doit avoir le nom de section et le
			// nom de paramètre
			tokenizer = 
				new UtilStringTokenizer(section_parameter, ":");
			// On vérifie qu'il y a deux éléments
			if(tokenizer.getTokensCount() != 2)
			{
				trace_errors.writeTrace("Format d'instruction incorrect: " +
					patch_instruction);
				// On lève une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			section_name = tokenizer.nextToken();
			parameter_name = tokenizer.nextToken();
			trace_debug.writeTrace("section_name=" + section_name);
			trace_debug.writeTrace("parameter_name=" + parameter_name);
			// On vérifie que la section existe
			int section_start = getSectionPosition(section_name); 
			if(section_start == -1)
			{
				// On passe au suivant
				continue;
			}
			// On récupère la position de la section suivante
			int section_end = getNextSectionPosition(section_start + 1);
			if(instruction.equalsIgnoreCase("ADD_PARAMETER") == true)
			{
				trace_debug.writeTrace("Ajout d'un paramètre");
				// On récupère la valeur
				value = patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				// On vérifie que le paramètre n'existe pas déjà
				if(getParameterPosition(parameter_name, section_start + 1,
					section_end) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute une ligne au début de la section
				_fileContent.insertElementAt(parameter_name + "=" +
					value, section_start + 1);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("UPDATE_PARAMETER") == true)
			{
				trace_debug.writeTrace("Modification d'un paramètre");
				// On récupère la valeur
				value = patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				// On récupère la position du paramètre
				int position = getParameterPosition(parameter_name, 
					section_start + 1, section_end);
				// Si la position n'est pas valide, on passe au suivant
				if(position == -1)
				{
					continue;
				}
				String old_value = removeQuotes(getParameterValue(position));
				String new_value = MessageManager.replaceString(value, 
					"%[value]", old_value);
				trace_debug.writeTrace("value=" + value);
				trace_debug.writeTrace("old_value=" + old_value);
				trace_debug.writeTrace("new_value=" + new_value);
				// On remplace la ligne dans le vecteur
				_fileContent.remove(position);
				_fileContent.insertElementAt(parameter_name + "=" +
					new_value, position);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("REMOVE_PARAMETER") == true)
			{
				trace_debug.writeTrace("Suppression d'un paramètre");
				// On récupère la position du paramètre
				int position = getParameterPosition(parameter_name, 
					section_start + 1, section_end);
				// Si la position est valide, on supprime l'élément à
				// cette position
				if(position != -1)
				{
					_fileContent.remove(position);
				}
				// On passe au suivant
				continue;
			}
			// Si on arrive ici, c'est que l'instruction n'est pas 
			// correcte
			trace_errors.writeTrace("Instruction incorrecte: " +
				instruction);
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_IncorrectPatchInstruction", 
				patch_instruction, null);
		}
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getSectionPosition
	* 
	* Description:
	* Cette méthode permet de récupérer la position de démarrage d'une 
	* section, dont le nom est passé en argument, dans le vecteur 
	* correspondant au contenu du fichier de configuration.
	* Une section du fichier de configuration est identifiée par la syntaxe 
	* [<Section>].
	* 
	* Arguments:
	*  - sectionName: Le nom de la section dont la position est recherchée.
	* 
	* Retourne: La position de la section dans le vecteur, ou -1 si elle ne 
	* peut pas être trouvée.
	* ----------------------------------------------------------*/
	private int getSectionPosition(
		String sectionName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "getSectionPosition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("sectionName=" + sectionName);
		// On va rechercher la présence de la section dans le contenu du
		// fichier de configuration
		for(int index = 0 ; index < _fileContent.size() ; index ++)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne ne commence par par '[', on peut passer à la
			// suivante
			if(line.startsWith("[") == false)
			{
				continue;
			}
			trace_debug.writeTrace("Test pour la ligne: " + line);
			// La ligne correspond-elle à la bonne section ?
			if(line.equals("[" + sectionName + "]") == true)
			{
				trace_debug.writeTrace("Section trouvée: " + index);
				// On peut sortir
				trace_methods.endOfMethod();
				return index;
			}
		}
		// Si on arrive ici, c'est que la section n'a pas été trouvée
		trace_debug.writeTrace("Section non trouvée");
		trace_methods.endOfMethod();
		return -1;
	}

	/*----------------------------------------------------------
	* Nom: getNextSectionPosition
	* 
	* Description:
	* Cette méthode est chargée de rechercher la position de la prochaine 
	* section à partir d'un point de départ passé en argument. Une section 
	* étant identifiée par la syntaxe [<Section>], il suffit de rechercher 
	* toute ligne commençant par '[' et se terminant par ']'.
	* La valeur retournée sera la position de la première ligne de commentaire 
	* jouxtant le début de section, s'il y en a une.
	* 
	* Arguments:
	*  - startFrom: La position de départ de la recherche du démarrage de la 
	*    section suivante.
	* 
	* Retourne: La position de la nouvelle section, ou -1 s'il n'y a pas de 
	* nouvelle section.
	* ----------------------------------------------------------*/
	private int getNextSectionPosition(
		int startFrom
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "getNextSectionPosition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int index = startFrom;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("startFrom=" + startFrom);
		// On va rechercher la présence de la section dans le contenu du
		// fichier de configuration
		for(; index < _fileContent.size() ; index ++)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne ne commence par par '[', on peut passer à la
			// suivante
			if(line.startsWith("[") == false)
			{
				continue;
			}
			trace_debug.writeTrace("Nouvelle section trouvée: " + index);
			// On peut provoquer un rembobinage sur les commentaires
			index = rewindComments(index);
			break;
		}
		trace_debug.writeTrace("index=" + index);
		trace_methods.endOfMethod();
		return index;
	}

	/*----------------------------------------------------------
	* Nom: rewindComments
	* 
	* Description:
	* Cette méthode est chargée de trouver la position du premier commentaire 
	* jouxtant la position passée en argument.
	* Pour cela, la méthode recherche à partir de la position précédente celle 
	* de départ la première ligne n'étant pas une ligne de commentaire (ne 
	* commençant pas par '#').
	* 
	* Arguments:
	*  - startFrom: La position de départ pour la recherche.
	* 
	* Retourne: La position de la première ligne de commentaire jouxtant la 
	* position de départ de la recherche.
	* ----------------------------------------------------------*/
	private int rewindComments(
		int startFrom
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "rewindComments");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int index = startFrom - 1;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("startFrom=" + startFrom);
		// On va rechercher la première ligne de commentaire à partir de la
		// position de départ, vers le début
		for(; index >= 0 ; index --)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne commence par '#', on peut passer à la
			// suivante
			if(line.startsWith("#") == true)
			{
				continue;
			}
			trace_debug.writeTrace("Ligne de non commentaire trouvée");
			break;
		}
		// On se positionne sur la ligne de commentaire
		index ++;
		trace_debug.writeTrace("index=" + index);
		trace_methods.endOfMethod();
		return index;
	}

	/*----------------------------------------------------------
	* Nom: removeQuotes
	* 
	* Description:
	* Cette méthode est chargée de supprimer les quotes éventuelles en début 
	* et en fin de la chaîne passée en argument.
	* 
	* Arguments:
	*  - value: La chaîne dont il faut supprimer les quotes éventuelles.
	* 
	* Retourne: La chaîne modifiée, ou la chaîne originale si celle-ci n'a pas 
	* de quote.
	* ----------------------------------------------------------*/
	private String removeQuotes(
		String value
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConfigFileProcessor", "removeQuotes");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String the_value = value;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		// Si value est null ou vide, on sort
		if(value == null || value.equals("") == true)
		{
			trace_methods.endOfMethod();
			return the_value;
		}
		// Si value commence et se termine par une quote, il faut la retirer
		if((value.startsWith("'") == true && value.endsWith("'") == true) ||
			(value.startsWith("\"") == true && value.endsWith("\"") == true))
		{
			the_value = value.substring(1, value.length() - 1);
		}
		trace_debug.writeTrace("the_value=" + the_value);
		trace_methods.endOfMethod();
		return the_value;
	}
}
