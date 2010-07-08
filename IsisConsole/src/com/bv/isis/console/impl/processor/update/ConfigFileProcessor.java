/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports syst�me
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
* Cette classe est une classe d'impl�mentation permettant le traitement d'une 
* entr�e de type patch de fichier de configuration de la Console I-SIS.
* Son objectif est de lire le contenu du fichier de patch, de lire le contenu 
* du fichier de configuration, d'appliquer les modifications au contenu du 
* fichier de configuration, puis de r��crire ce dernier en y incluant les 
* modifications.
* Elle sp�cialise la classe LaxFileProcessor, laquelle impl�mente la majorit� 
* des fonctionnalit�s.
* ----------------------------------------------------------*/
public class ConfigFileProcessor 
	extends LaxFileProcessor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ConfigFileProcessor
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe LaxFileProcessor. Elle 
	* est charg�e d'appliquer le patch, pr�alablement lu, au fichier � patcher, 
	* �galement pr�alablement charg�.
	* Chaque ligne du fichier patch est d�compos� en 2 ou 3 champs, suivant la 
	* syntaxe:
	* <Mot-cl�> <Section>[:<Param�tre>] [<Valeur>]
	* 
	* Cinq types d'op�rations peuvent �tre effectu�es sur un fichier de 
	* configuration:
	*  - L'ajout de section,
	*  - La suppression de section,
	*  - L'ajout de param�tre,
	*  - La modification de param�tre,
	*  - La suppression de param�tre.
	* 
	* L'ajout de section est signifi� par le mot-cl� "ADD_SECTION". Un nom de 
	* section doit �tre fourni. Aucun nom de param�tre ou valeur n'est 
	* n�cessaire.
	* La suppression de section est signifi� par le mot-cl� "REMOVE_SECTION". 
	* Un nom de section doit �tre fourni. Aucun nom de param�tre ou valeur 
	* n'est n�cessaire.
	* L'ajout de param�tre est signifi� par le mot-cl� "ADD_PARAMETER". Un nom 
	* de param�tre et une valeur doivent �tre fournis.
	* La modification de param�tre est signifi�e par le mot-cl� 
	* "UPDATE_PARAMETER". Un nom de param�tre et une valeur doiventt �tre 
	* fournis. La valeur est soit fixe, soit relative � l'ancienne valeur 
	* (en utilisant la cha�ne "%[value]").
	* La suppression de param�tre est signifi�e par le mot-cl� 
	* "REMOVE_PARAMETER". Le nom du param�tre doit �tre fourni, et aucune 
	* valeur n'est n�cessaire.
	* 
	* Si un probl�me survient pendant l'application du patch, 
	* l'exception InnerException est lev�e.
	* 
	* L�ve: InnerException.
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
			// D�coupage de la ligne suivant le s�parateur ' '
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(patch_instruction, " ");
			// Il doit y avoir au moins deux sous-cha�nes
			if(tokenizer.getTokensCount() < 2)
			{
				trace_errors.writeTrace(
					"Le format de la ligne de patch n'est pas valide !");
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			// On r�cup�re de toute fa�on les deux premi�res sous-cha�nes
			instruction = tokenizer.nextToken();
			section_parameter = tokenizer.nextToken();
			trace_debug.writeTrace("instruction=" + instruction);
			trace_debug.writeTrace("section_parameter=" + section_parameter);
			// Suivant la valeur de l'instruction, on effectue le traitement
			// ad�quat
			if(instruction.equalsIgnoreCase("ADD_SECTION") == true)
			{
				trace_debug.writeTrace("Ajout d'une section");
				section_name = section_parameter;
				trace_debug.writeTrace("section_name=" + section_name);
				// On r�cup�re la description
				String description = 
					patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				trace_debug.writeTrace("description=" + description);
				// On v�rifie que la section n'existe pas d�j�
				if(getSectionPosition(section_name) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute la section � la fin
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
				// On r�cup�re la position de d�part de la section, et celle
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
				// Il faut supprimer toutes les donn�es entre les deux positions
				while(number_of_elements > 0)
				{
					_fileContent.remove(section_start);
					number_of_elements --;
				}
				// On passe au suivant
				continue;
			}
			// A partir d'ici, on doit avoir le nom de section et le
			// nom de param�tre
			tokenizer = 
				new UtilStringTokenizer(section_parameter, ":");
			// On v�rifie qu'il y a deux �l�ments
			if(tokenizer.getTokensCount() != 2)
			{
				trace_errors.writeTrace("Format d'instruction incorrect: " +
					patch_instruction);
				// On l�ve une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			section_name = tokenizer.nextToken();
			parameter_name = tokenizer.nextToken();
			trace_debug.writeTrace("section_name=" + section_name);
			trace_debug.writeTrace("parameter_name=" + parameter_name);
			// On v�rifie que la section existe
			int section_start = getSectionPosition(section_name); 
			if(section_start == -1)
			{
				// On passe au suivant
				continue;
			}
			// On r�cup�re la position de la section suivante
			int section_end = getNextSectionPosition(section_start + 1);
			if(instruction.equalsIgnoreCase("ADD_PARAMETER") == true)
			{
				trace_debug.writeTrace("Ajout d'un param�tre");
				// On r�cup�re la valeur
				value = patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				// On v�rifie que le param�tre n'existe pas d�j�
				if(getParameterPosition(parameter_name, section_start + 1,
					section_end) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute une ligne au d�but de la section
				_fileContent.insertElementAt(parameter_name + "=" +
					value, section_start + 1);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("UPDATE_PARAMETER") == true)
			{
				trace_debug.writeTrace("Modification d'un param�tre");
				// On r�cup�re la valeur
				value = patch_instruction.substring(instruction.length() +
					section_parameter.length() + 2);
				// On r�cup�re la position du param�tre
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
				trace_debug.writeTrace("Suppression d'un param�tre");
				// On r�cup�re la position du param�tre
				int position = getParameterPosition(parameter_name, 
					section_start + 1, section_end);
				// Si la position est valide, on supprime l'�l�ment �
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
			// On l�ve une exception
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
	* Cette m�thode permet de r�cup�rer la position de d�marrage d'une 
	* section, dont le nom est pass� en argument, dans le vecteur 
	* correspondant au contenu du fichier de configuration.
	* Une section du fichier de configuration est identifi�e par la syntaxe 
	* [<Section>].
	* 
	* Arguments:
	*  - sectionName: Le nom de la section dont la position est recherch�e.
	* 
	* Retourne: La position de la section dans le vecteur, ou -1 si elle ne 
	* peut pas �tre trouv�e.
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
		// On va rechercher la pr�sence de la section dans le contenu du
		// fichier de configuration
		for(int index = 0 ; index < _fileContent.size() ; index ++)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne ne commence par par '[', on peut passer � la
			// suivante
			if(line.startsWith("[") == false)
			{
				continue;
			}
			trace_debug.writeTrace("Test pour la ligne: " + line);
			// La ligne correspond-elle � la bonne section ?
			if(line.equals("[" + sectionName + "]") == true)
			{
				trace_debug.writeTrace("Section trouv�e: " + index);
				// On peut sortir
				trace_methods.endOfMethod();
				return index;
			}
		}
		// Si on arrive ici, c'est que la section n'a pas �t� trouv�e
		trace_debug.writeTrace("Section non trouv�e");
		trace_methods.endOfMethod();
		return -1;
	}

	/*----------------------------------------------------------
	* Nom: getNextSectionPosition
	* 
	* Description:
	* Cette m�thode est charg�e de rechercher la position de la prochaine 
	* section � partir d'un point de d�part pass� en argument. Une section 
	* �tant identifi�e par la syntaxe [<Section>], il suffit de rechercher 
	* toute ligne commen�ant par '[' et se terminant par ']'.
	* La valeur retourn�e sera la position de la premi�re ligne de commentaire 
	* jouxtant le d�but de section, s'il y en a une.
	* 
	* Arguments:
	*  - startFrom: La position de d�part de la recherche du d�marrage de la 
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
		// On va rechercher la pr�sence de la section dans le contenu du
		// fichier de configuration
		for(; index < _fileContent.size() ; index ++)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne ne commence par par '[', on peut passer � la
			// suivante
			if(line.startsWith("[") == false)
			{
				continue;
			}
			trace_debug.writeTrace("Nouvelle section trouv�e: " + index);
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
	* Cette m�thode est charg�e de trouver la position du premier commentaire 
	* jouxtant la position pass�e en argument.
	* Pour cela, la m�thode recherche � partir de la position pr�c�dente celle 
	* de d�part la premi�re ligne n'�tant pas une ligne de commentaire (ne 
	* commen�ant pas par '#').
	* 
	* Arguments:
	*  - startFrom: La position de d�part pour la recherche.
	* 
	* Retourne: La position de la premi�re ligne de commentaire jouxtant la 
	* position de d�part de la recherche.
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
		// On va rechercher la premi�re ligne de commentaire � partir de la
		// position de d�part, vers le d�but
		for(; index >= 0 ; index --)
		{
			String line = (String)_fileContent.elementAt(index);
			// Si la ligne commence par '#', on peut passer � la
			// suivante
			if(line.startsWith("#") == true)
			{
				continue;
			}
			trace_debug.writeTrace("Ligne de non commentaire trouv�e");
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
	* Cette m�thode est charg�e de supprimer les quotes �ventuelles en d�but 
	* et en fin de la cha�ne pass�e en argument.
	* 
	* Arguments:
	*  - value: La cha�ne dont il faut supprimer les quotes �ventuelles.
	* 
	* Retourne: La cha�ne modifi�e, ou la cha�ne originale si celle-ci n'a pas 
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
