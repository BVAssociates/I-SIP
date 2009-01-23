/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/processor/ProcessingHandler.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement de processing
* DATE:        27/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessingHandler.java,v $
* Revision 1.9  2008/08/11 10:47:25  tz
* Modification de la syntaxe des s�quence de processing.
*
* Revision 1.8  2008/01/31 16:43:55  tz
* Renommage depuis la classe PreprocessingHandler.
* Renommage de la m�thode handlePreprocessing() en
* handleProcessingStatement(), et de la m�thode
* decodePreprocessingInstruction() en decodeProcessingInstruction().
* Ajout du traitement de l'instruction displayMessage().
*
* Revision 1.7  2006/11/09 12:08:41  tz
* Ajout de la m�thode getFileSystemEntry().
* Gestion des param�tres de pr�processing selectFile et selectDirectory.
*
* Revision 1.6  2005/07/01 12:02:14  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/11/09 15:20:49  tz
* Gestion des quotes dans la cr�ation des param�tres de pr�-processing.
*
* Revision 1.4  2004/10/13 13:53:11  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1.
*
* Revision 1.3  2002/11/19 08:37:08  tz
* Correction des fiches Inuit/76 et Inuit/78.
*
* Revision 1.2  2002/08/13 12:57:30  tz
* Evaluation des valeurs de pr�processing
*
* Revision 1.1  2002/04/05 15:50:07  tz
* Cloture it�ration IT1.2
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.processor;

//
// Imports syst�me
//
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;
import com.bv.isis.corbacom.IsisTableDefinition;
import javax.swing.JOptionPane;
import java.util.Vector;
import java.awt.Component;

//
// Imports du projet
//
import com.bv.isis.console.abs.gui.MainWindowInterface;
import com.bv.isis.console.common.IndexedList;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.common.DialogManager;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.com.RemoteFileChooser;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.ServiceSessionInterface;

/*----------------------------------------------------------
* Nom: ProcessingHandler
* 
* Description:
* Cette classe abstraite est en charge du traitement des informations de 
* pr�processing et postprocessing pour les diff�rents processeurs.
* Elle offre une m�thode statique consistant � d�coder la cha�ne de processing 
* et � construire un tableau d'IsisParameter issu de ce d�codage, et 
* �ventuellement � enrichir une liste index�e.
* ----------------------------------------------------------*/
public abstract class ProcessingHandler
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: handleProcessingStatement
	* 
	* Description:
	* Cette m�thode statique est charg�e de d�coder la cha�ne de processing 
	* pass�e en argument et de la traiter afin de construire un tableau 
	* d'IsisParameter qui sera retourn�.
	* La cha�ne de processing doit respecter le format indiqu� dans la 
	* description des champs preprocessing et postprocessing de la classe 
	* IsisMethod.
	* Si l'argument context est non nul, les param�tres de processing sont 
	* directement ajout�s au contexte.
	* 
	* Si une erreur survient lors du d�codage du processing, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - processingStatement: La cha�ne de processing,
	*  - context: Une liste index�e � enrichir avec le r�sultat du traitement 
	*    du processing,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - serviceSession: Une r�f�rence sur une interface ServiceSessionInterface,
	*  - parent: Une r�f�rence sur un objet Component,
	*  - isPreProcessing: Un bool�en indiquant s'il s'agit d'une op�ration de 
	*    pr�processing (true) ou non.
	* 
	* Retourne: Un tableau d'IsisParameter construit � partir du traitement de 
	* la cha�ne de processing.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static IsisParameter[] handleProcessingStatement(
		String processingStatement,
		IndexedList context,
		MainWindowInterface windowInterface,
		String agentLayerMode,
		ServiceSessionInterface serviceSession,
		Component parent,
		boolean isPreProcessing
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "handleProcessingStatement");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Vector parameters = new Vector();
		int start_pos = 0;
		int inside_occurences = 0;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processingStatement=" +
			processingStatement);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("isPreProcessing=" + isPreProcessing);
		// Si la cha�ne est nulle ou vide, il n'y a rien � faire
		if(processingStatement == null ||
			processingStatement.equals("") == true)
		{
			trace_methods.endOfMethod();
			return null;
		}
		// La cha�ne de processing est consistu�e d'instructions. S'il y a
		// plusieurs instructions, chacune d'elles est contenue entre { et }
		// de la forme {<Instruction>}{<Instruction>}...
		while(processingStatement.length() > 0) {
			if(processingStatement.indexOf("{") != 0) {
				// Traitement de l'instruction
				IsisParameter parameter =
					decodeProcessingInstruction(processingStatement.trim(),
					context, windowInterface, agentLayerMode, serviceSession,
					parent, isPreProcessing);
				parameters.add(parameter);
			    // Si le contexte est non nul, on lui ajoute les param�tres
				if(context != null) {
					context.put(parameter.name, parameter);
				}
				break;
			}
			// On va parser la cha�ne � la recherche de l'occurence de '}'
			for(int index = 1 ; index < processingStatement.length() ; 
				index ++) {
				char the_char = processingStatement.charAt(index);
				if(the_char == '{') {
					// On incr�mente le compteur d'occurences "internes"
					inside_occurences++;
					continue;
				}
				if(the_char == '}') {
					// Est-ce que l'on a crois� d'autres occurences 
					// "internes"
					if(inside_occurences > 0) {
						// On d�cr�mente le compteur d'occurences
						inside_occurences--;
						continue;
					}
					// On va d�couper la cha�ne sur les positions de d�part
					// et de fin
					String statement = processingStatement.substring(
						1, index);
					processingStatement = 
						processingStatement.substring(index + 1); 
					// Traitement de l'instruction
					IsisParameter parameter =
						decodeProcessingInstruction(statement.trim(),
						context, windowInterface, agentLayerMode, 
						serviceSession, parent, isPreProcessing);
					parameters.add(parameter);
				    // Si le contexte est non nul, on lui ajoute les param�tres
					if(context != null) {
						context.put(parameter.name, parameter);
					}
					break;
				}
			}
		}
		trace_methods.endOfMethod();
		return (IsisParameter[])parameters.toArray(
			new IsisParameter[0]);
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: decodeProcessingInstruction
	* 
	* Description:
	* Cette m�thode statique permet de construire un objet IsisParameter � 
	* partir d'une instruction de processing pass�e en argument.
	* Si l'instruction de processing fait r�f�rence � une demande de saisie � 
	* l'utilisateur (la valeur du param�tre dans l'instruction vaut 
	* getValue(<prompt>)), la m�thode va appeler la m�thode getValueFromUser() 
	* pour afficher une fen�tre de saisie.
	* 
	* Si le format de l'instruction de processing n'est pas valide, ou qu'une 
	* erreur survient lors du d�codage, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - instruction: L'instruction de processing � d�coder,
	*  - context: Une liste index�e d'o� seront �ventuellement r�cup�r�es les 
	*    valeurs des variables de processing,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - serviceSession: Une r�f�rence sur une interface 
	*    ServiceSessionInterface,
	*  - parent: Une r�f�rence sur un objet Component,
	*  - isPreProcessing: Un bool�en indiquant s'il s'agit d'une instruction de 
	*    pr�processing (true) ou non.
	* 
	* Retourne: Un objet IsisParameter issu du d�codage de l'instruction de 
	* processing.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private static IsisParameter decodeProcessingInstruction(
		String instruction,
		IndexedList context,
		MainWindowInterface windowInterface,
		String agentLayerMode,
		ServiceSessionInterface serviceSession,
		Component parent,
		boolean isPreProcessing
		)
		throws
			InnerException
	{
		IsisParameter parameter = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "decodeProcessingInstruction");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		char quote_char = '"';

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("instruction=" + instruction);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("isPreProcessing=" + isPreProcessing);
		if(isPreProcessing == false) {
			// On est dans le cas du postprocessing
			// A ce jour, la seule instruction qui soit support�e est
			// displayMessage
			if(instruction.startsWith("displayMessage(") == false) {
				trace_errors.writeTrace("Mauvais format de l'instruction: " +
					instruction);
				// Le format de l'instruction est incorrect, on l�ve une 
				// exception
				trace_methods.endOfMethod();
				throw new InnerException(
					"&ERR_IncorrectPreprocessingInstruction", instruction, 
					null);
			}
			// On va r�cup�rer le message
			String message = getValue(instruction.substring(15,
				instruction.length() - 1), context);
			// On va afficher le message � l'utilisateur
			DialogManager.displayDialog("Information", message, null, parent);
			// On peut sortir
			trace_methods.endOfMethod();
			return parameter;
		}
		// On d�coupe l'instruction � partir du signe �gal
		int position = instruction.indexOf("=");
		if(position == -1)
		{
			trace_errors.writeTrace("Mauvais format de l'instruction: " +
				instruction);
			// Le format de l'instruction est incorrect, on l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_IncorrectPreprocessingInstruction",
				instruction, null);
		}
		// Suppression des blancs avant et apr�s le nom de la variable
		String name = instruction.substring(0, position).trim();
		// Suppression des blancs avant et apr�s la valeur
		String value = getValue(instruction.substring(position + 1).trim(),
		    context);
		// Est-ce que la valeur n�cessite une saisie de l'utilisateur ?
		if(value.startsWith("getValue(") == true)
		{
			String prompt = getValue(value.substring(9, value.length() - 1),
				context);
			value = getValueFromUser(prompt, parent);
		}
        // Est-ce que la valeur n�cessite le choix de l'utilisateur dans une liste?
        else if(value.startsWith("getListValue(") == true)
		{
            // remove getListValue("...)
			String params = getValue(value.substring(12), context);

            int index=params.indexOf(',');
            // split with ,
            // remove '"' and ')'
            String message=params.substring(2, index-1).trim();
            String table_name=params.substring(index+1,params.length()-1).trim();
            
			value = getListValueFromUser(message,table_name , parent,context,serviceSession);
		}
		// Est-ce que la valeur n�cessite une s�lection de fichier ou de 
		// r�pertoire par l'utilisateur ?
		else if(value.startsWith("selectFile(") == true ||
			value.startsWith("selectDirectory(") == true)
		{
			boolean select_file = value.startsWith("selectFile(");
			int cut_start = 16;
			String initial_directory = null;
			String accept_type = null;
			String preset_type = null;
			
			if(select_file == true)
			{
				cut_start = 11;
			}
			// On va d�composer les arguments de la s�lection
			String select_arguments = getValue(value.substring(cut_start, 
				value.length() - 1), context);
			// Les diff�rentes arguments sont s�par�s par une virgule
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(select_arguments, ",");
			// Tous les arguments �tant optionnels, ont doit v�rifier s'ils
			// existent
			if(tokenizer.getTokensCount() > 0)
			{
				// On peut extraire le r�pertoire de d�part
				initial_directory = tokenizer.getToken(0);
			}
			if(tokenizer.getTokensCount() > 1)
			{
				// On peut extraire le type d'acceptation
				accept_type = tokenizer.getToken(1);
			}
			if(tokenizer.getTokensCount() > 2)
			{
				// On peut extraire le type de pr�s�lection
				preset_type = tokenizer.getToken(2);
			}
			value = getFileSystemEntry(select_file, initial_directory, 
				accept_type, preset_type, windowInterface, agentLayerMode, 
				serviceSession, parent);
		}
		// On construit l'objet IsisParameter
		parameter = new IsisParameter(name, value, quote_char);
		trace_methods.endOfMethod();
		return parameter;
	}

	/*----------------------------------------------------------
	* Nom: getValueFromUser
	*
	* Description:
	* Cette m�thode statique permet d'afficher une fen�tre � l'utilisateur lui
	* permettant de saisir une valeur pour un param�tre de pr�processing. Le
	* message � afficher comme invite � l'utilisateur est le contenu de
	* l'argument prompt.
	*
	* Si un probl�me survient lors de la saisie du param�tre, ou si
	* l'utilisateur annule la saisie, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - prompt: Le message de la fen�tre de saisie,
	*  - parent: Une r�f�rence sur un objet Component.
	*
	* Retourne: La valeur saisie par l'utilisateur.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private static String getValueFromUser(
		String prompt,
		Component parent
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "getValueFromUser");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("prompt=" + prompt);
		trace_arguments.writeTrace("parent=" + parent);
		// On va afficher une bo�te de dialogue de saisie
		String value = JOptionPane.showInputDialog(parent, prompt, 
			MessageManager.getMessage("&YesNoQuestion"),
			JOptionPane.QUESTION_MESSAGE);
		trace_debug.writeTrace("L'utilisateur a saisi=" + value);
		if(value == null)
		{
			// L'utilisateur a annul�, on l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InputCanceled", null, null);
		}
		trace_methods.endOfMethod();
		return value;
	}

    /*----------------------------------------------------------
	* Nom: getListValueFromUser
	*
	* Description:
	* Cette m�thode statique permet d'afficher une fen�tre � l'utilisateur lui
	* permettant de choisir une valeur pour un param�tre de pr�processing. La
    * liste de choix est la liste des clefs fournies par la table pass�e en 
    * parametre. Le message � afficher comme invite � l'utilisateur est le
    * contenu de l'argument prompt.
	*
	* Si un probl�me survient lors de la saisie du param�tre, ou si
	* l'utilisateur annule la saisie, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - message: Le message de la fen�tre de saisie,
    *  - table_name: La table a interroger,
	*  - parent: Une r�f�rence sur un objet Component.
    *  - context: context d'execution
    *  - serviceSession: Une r�f�rence sur une interface
	*    ServiceSessionInterface,
	*
	* Retourne: La valeur choisie par l'utilisateur.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private static String getListValueFromUser(
        String message,
		String parameters,
		Component parent,
        IndexedList context,
		ServiceSessionInterface serviceSession
		)
		throws
			InnerException
	{

        /**
         * Classe interne stockant une liste renvoy�e par un Select
         * Dans un JOptionPane, permet d'afficher les champs, puis permet de
         * recuperer uniquement la clef de l'element selectionn�
         */
        class IsisParameterOption
        {

            private String _key;
            private IsisParameter[] _fields;
            private IsisTableDefinition _definition;

            /**
             * Constructeur de IsisParameterOption
             * 
             * @param fields ligne renvoy�e par un Select. Doit contenir les
             * clefs primaire en premier. Ils seront supprim�s de l'affichage
             * @param definition la IsisTableDefinition corresondante � fields
             */
            IsisParameterOption(IsisParameter[] fields, IsisTableDefinition definition)
            {
                _fields = fields;
                _definition = definition;
                
                StringBuilder temp_key=new StringBuilder();
                String sep="";
                for (int i=0; i < _definition.key.length; i++) {
                    temp_key.append(sep);
                    temp_key.append(fields[i].value);
                    sep=_definition.separator;
                }
                _key=temp_key.toString();
            }

            /**
             * methode utilis�e dans le JOptionPane pour afficher le texte
             */
            @Override
            public String toString()
            {
                StringBuilder return_string=new StringBuilder();
                String sep="";

                
                
                for (int i=_definition.key.length; i < _fields.length; i++) {
                    return_string.append(sep);
                    return_string.append(_fields[i].value);
                    sep=_definition.separator;
                }
                return return_string.toString();
            }

            /**
             * recup�re la clef sous forme de texte
             */
            public String getKey()
            {
                return _key;
            }
        }

        
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "getListValueFromUser");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("table=" + parameters);
        trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("parent=" + parent);

        String condition="";
        String sort_order="";
        String columns="";
        String selected_column[];
        String table_name;
        IsisTableDefinition definition;

        // On recupere le Proxy associ�
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(serviceSession);

        // condition des param�tres.
        // Le format est: <table>[@<colonnes>[@<condition>[@<sort>[@<log?>]]]
        UtilStringTokenizer tokenizer =
                new UtilStringTokenizer(parameters, "@");
        switch (tokenizer.getTokensCount()) {
            case 4:
                sort_order = tokenizer.getToken(3);
            case 3:
                condition = tokenizer.getToken(2);
            case 2:
                columns = tokenizer.getToken(1);
            case 1:
                table_name=(tokenizer.getToken(0));
                break;
            default:
                throw new InnerException("Format non reconnu", parameters, null);
        }

        // On recupere la definition de la table en cours
        definition=session_proxy.getTableDefinition(table_name, context);
        String keys[]=definition.key;
        
        if (columns.equals("") == false) {
            // On d�coupe la liste des colonnes
            tokenizer = new UtilStringTokenizer(columns, ",");


            selected_column = new String[tokenizer.getTokensCount()+keys.length];

            // on ajoute systematiquement la liste des clefs
            for (int index = 0; index < keys.length; index++) {
                selected_column[index] = keys[index];
            }
            
            for (int index = 0; index < tokenizer.getTokensCount(); index++) {
                selected_column[keys.length+index] = tokenizer.getToken(index);
            }
        } else {
            //Si aucune colonne n'est donn�e, on ne met que les clefs
            
            selected_column = new String[keys.length*2];

            // on ajoute systematiquement la liste des clefs
            // puis on ajoute les clef a nouveau pour affichage
            for (int index = 0; index < keys.length*2; index++) {
                selected_column[index] = keys[index/2];
            }
        }

        // On va chercher les informations dans la table
        String[] result = session_proxy.getSelectResult(table_name, selected_column, condition,sort_order, context);
        //String[] result = session_proxy.getWideSelectResult(selectedIsisNode.getAgentName(), tableName, columnsName, condition, "", context);
        IsisTableDefinition table_definition = TreeNodeFactory.buildDefinitionFromSelectResult(result, table_name);

        if (result.length <= 1) {
            throw new InnerException("&LOG_ErrorWhileLoadingData", null, null);
        }

        // on ne recupere que les clefs
        // on saute la premiere ligne qui contient l'entete du Select
        IsisParameterOption[] options=new IsisParameterOption[result.length-1];
        for (int i=1; i < result.length; i++) {
            IsisParameter[] iparam = TreeNodeFactory.buildParametersFromSelectResult(result, i, table_definition);
            options[i-1]=new IsisParameterOption(iparam, table_definition);
        }

		// On va afficher une bo�te de dialogue de saisie
		IsisParameterOption choice = (IsisParameterOption) JOptionPane.showInputDialog(parent, message,
			MessageManager.getMessage("&YesNoQuestion"),
			JOptionPane.QUESTION_MESSAGE,null, options, options[0]);
		
		if(choice == null)
		{
			// L'utilisateur a annul�, on l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InputCanceled", null, null);
		}
        trace_debug.writeTrace("L'utilisateur a saisi=" + choice.getKey());
		trace_methods.endOfMethod();
		return choice.getKey();
	}

	/*----------------------------------------------------------
	* Nom: getValue
	*
	* Description:
	* Cette m�thode statique permet d'extraire la valeur d'une cha�ne de valeur
	* ayant le format '<value>' ou "<value>". Elle commence par supprimer les
	* quotes.
	* Ensuite, la cha�ne est �valu�e de sorte � r�soudre toute r�f�rence � une 
	* variable.
	*
	* Arguments:
	*  - valueChain: La valeur dont il faut supprimer les quotes s'il y en a,
	*  - context: Une liste index�e fournissant les valeurs de certaines
	*    variables.
	*
	* Retourne: La valeur sans les quotes et �valu�e.
	* ----------------------------------------------------------*/
	private static String getValue(
		String valueChain,
		IndexedList context
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "getValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String value;
		String evaluated_value;
		int start = 0;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("valueChain=" + valueChain);
		trace_arguments.writeTrace("context=" + context);
		// Est-ce que la cha�ne existe et est non vide ?
		if(valueChain == null || valueChain.equals("") == true)
		{
			trace_methods.endOfMethod();
			return valueChain;
		}
		// Est-ce que la valeur commence par des quotes ?
		if(valueChain.charAt(0) == '\'' || valueChain.charAt(0) == '"')
		{
			// On retire les quotes
			value = valueChain.substring(1, valueChain.length() - 1);
		}
		else
		{
			value = valueChain;
		}
		evaluated_value = LabelFactory.evaluate(value, null, context, null);
		trace_methods.endOfMethod();
		return evaluated_value;
	}

	/*----------------------------------------------------------
	* Nom: getFileSystemEntry
	* 
	* Description:
	* Cette m�thode statique permet d'afficher � l'utilisateur une boite de 
	* dialogue d'exploration du syst�me de fichiers afin qu'il puisse 
	* s�lectionner un fichier ou un r�pertoire, suivant les valeurs des 
	* arguments.
	* 
	* Si l'argument selectFile vaut true, il s'agira pour l'utilisateur de 
	* s�lectionner un fichier. Sinon, il s'agira de s�lectionner un r�pertoire.
	* L'argument initialDirectory peut avoir une valeur non nulle, celle-ci 
	* devant �tre un chemin absolu sur le syst�me de fichiers de l'Agent.
	* L'argument acceptType peut avoir une valeur non nulle, celle-ci devant 
	* �tre soit "ALL" ou "EXISTING".
	* L'argument presetType peut avoir une valeur non nulle, celle-ci devant 
	* �tre soit "NONE", soit "EXECUTABLES", soit "DICTIONARIES", soit 
	* "DATAFILES", ou encore "COMMANDPANELS".
	* 
	* Si une erreur survient lors de l'affichage de la boite de dialogue, ou 
	* si l'utilisateur annule la saisie, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - selectFile: Un bool�en indiquant le type d'entr�e devant �tre 
	*    s�lectionn�e,
	*  - initialDirectory: Le chemin absolu du r�pertoire de d�part de 
	*    l'exploration,
	*  - acceptType: Le type d'acceptation de l'entr�e,
	*  - presetType: Le type de pr�s�lection des r�pertoires d'exploration,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - serviceSession: Une r�f�rence sur une interface 
	*    ServiceSessionInterface,
	*  - parent: Une r�f�rence sur un objet Component.
	* 
	* Retourne: Le chemin absolu de l'entr�e s�lectionn�e.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private static String getFileSystemEntry(
		boolean selectFile,
		String initialDirectory,
		String acceptType,
		String presetType,
		MainWindowInterface windowInterface,
		String agentLayerMode,
		ServiceSessionInterface serviceSession,
		Component parent
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessingHandler", "getFileSystemEntry");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		RemoteFileChooser file_chooser = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectFile=" + selectFile);
		trace_arguments.writeTrace("initialDirectory=" + initialDirectory);
		trace_arguments.writeTrace("acceptType=" + acceptType);
		trace_arguments.writeTrace("presetType=" + presetType);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("parent=" + parent);
		// On va v�rifier que les arguments importants sont valides
		if(windowInterface == null || agentLayerMode == null ||
			agentLayerMode.equals("") == true || serviceSession == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va instancier la boite de dialogue
		file_chooser = new RemoteFileChooser(windowInterface, agentLayerMode);
		// On va d�finir les param�tres de fonctionnement
		if(initialDirectory != null && 
			initialDirectory.equalsIgnoreCase("NULL") == true)
		{
			initialDirectory = null;
		}
		if(selectFile == false)
		{
			file_chooser.setSelectionMode(
				RemoteFileChooser.SELECT_DIRECTORIES);
		}
		if(acceptType != null && 
			acceptType.equalsIgnoreCase("EXISTING") == true)
		{
			file_chooser.setAcceptType(RemoteFileChooser.ACCEPT_EXISTING);
		}
		if(presetType != null)
		{
			if(presetType.equalsIgnoreCase("EXECUTABLES") == true)
			{
				file_chooser.setPresetType(
					RemoteFileChooser.PRESET_EXECUTABLES);
			}
			else if(presetType.equalsIgnoreCase("DICTIONARIES") == true)
			{
				file_chooser.setPresetType(
					RemoteFileChooser.PRESET_DICTIONARIES);
			}
			else if(presetType.equalsIgnoreCase("DATAFILES") == true)
			{
				file_chooser.setPresetType(
					RemoteFileChooser.PRESET_DATAFILES);
			}
			else if(presetType.equalsIgnoreCase("COMMANDPANELS") == true)
			{
				file_chooser.setPresetType(
					RemoteFileChooser.PRESET_COMMANDPANELS);
			}
		}
		// Il ne reste plus qu'� commander l'affichage de la boite de dialogue
		if(file_chooser.showDialog(serviceSession, parent, 
			initialDirectory) == RemoteFileChooser.CANCEL_OPTION)
		{
			// L'utilisateur a annul�, on l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InputCanceled", null, null);
		}
		trace_methods.endOfMethod();
		// On retourne la s�lection de l'utilisateur
		return file_chooser.getSelection();
	}
}
