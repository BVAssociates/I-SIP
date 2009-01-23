/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
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
* Modification de la syntaxe des séquence de processing.
*
* Revision 1.8  2008/01/31 16:43:55  tz
* Renommage depuis la classe PreprocessingHandler.
* Renommage de la méthode handlePreprocessing() en
* handleProcessingStatement(), et de la méthode
* decodePreprocessingInstruction() en decodeProcessingInstruction().
* Ajout du traitement de l'instruction displayMessage().
*
* Revision 1.7  2006/11/09 12:08:41  tz
* Ajout de la méthode getFileSystemEntry().
* Gestion des paramètres de préprocessing selectFile et selectDirectory.
*
* Revision 1.6  2005/07/01 12:02:14  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/11/09 15:20:49  tz
* Gestion des quotes dans la création des paramètres de pré-processing.
*
* Revision 1.4  2004/10/13 13:53:11  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1.
*
* Revision 1.3  2002/11/19 08:37:08  tz
* Correction des fiches Inuit/76 et Inuit/78.
*
* Revision 1.2  2002/08/13 12:57:30  tz
* Evaluation des valeurs de préprocessing
*
* Revision 1.1  2002/04/05 15:50:07  tz
* Cloture itération IT1.2
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.processor;

//
// Imports système
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
* préprocessing et postprocessing pour les différents processeurs.
* Elle offre une méthode statique consistant à décoder la chaîne de processing 
* et à construire un tableau d'IsisParameter issu de ce décodage, et 
* éventuellement à enrichir une liste indexée.
* ----------------------------------------------------------*/
public abstract class ProcessingHandler
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: handleProcessingStatement
	* 
	* Description:
	* Cette méthode statique est chargée de décoder la chaîne de processing 
	* passée en argument et de la traiter afin de construire un tableau 
	* d'IsisParameter qui sera retourné.
	* La chaîne de processing doit respecter le format indiqué dans la 
	* description des champs preprocessing et postprocessing de la classe 
	* IsisMethod.
	* Si l'argument context est non nul, les paramètres de processing sont 
	* directement ajoutés au contexte.
	* 
	* Si une erreur survient lors du décodage du processing, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - processingStatement: La chaîne de processing,
	*  - context: Une liste indexée à enrichir avec le résultat du traitement 
	*    du processing,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - serviceSession: Une référence sur une interface ServiceSessionInterface,
	*  - parent: Une référence sur un objet Component,
	*  - isPreProcessing: Un booléen indiquant s'il s'agit d'une opération de 
	*    préprocessing (true) ou non.
	* 
	* Retourne: Un tableau d'IsisParameter construit à partir du traitement de 
	* la chaîne de processing.
	* 
	* Lève: InnerException.
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
		// Si la chaîne est nulle ou vide, il n'y a rien à faire
		if(processingStatement == null ||
			processingStatement.equals("") == true)
		{
			trace_methods.endOfMethod();
			return null;
		}
		// La chaîne de processing est consistuée d'instructions. S'il y a
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
			    // Si le contexte est non nul, on lui ajoute les paramètres
				if(context != null) {
					context.put(parameter.name, parameter);
				}
				break;
			}
			// On va parser la chaîne à la recherche de l'occurence de '}'
			for(int index = 1 ; index < processingStatement.length() ; 
				index ++) {
				char the_char = processingStatement.charAt(index);
				if(the_char == '{') {
					// On incrémente le compteur d'occurences "internes"
					inside_occurences++;
					continue;
				}
				if(the_char == '}') {
					// Est-ce que l'on a croisé d'autres occurences 
					// "internes"
					if(inside_occurences > 0) {
						// On décrémente le compteur d'occurences
						inside_occurences--;
						continue;
					}
					// On va découper la chaîne sur les positions de départ
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
				    // Si le contexte est non nul, on lui ajoute les paramètres
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
	* Cette méthode statique permet de construire un objet IsisParameter à 
	* partir d'une instruction de processing passée en argument.
	* Si l'instruction de processing fait référence à une demande de saisie à 
	* l'utilisateur (la valeur du paramètre dans l'instruction vaut 
	* getValue(<prompt>)), la méthode va appeler la méthode getValueFromUser() 
	* pour afficher une fenêtre de saisie.
	* 
	* Si le format de l'instruction de processing n'est pas valide, ou qu'une 
	* erreur survient lors du décodage, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - instruction: L'instruction de processing à décoder,
	*  - context: Une liste indexée d'où seront éventuellement récupérées les 
	*    valeurs des variables de processing,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - serviceSession: Une référence sur une interface 
	*    ServiceSessionInterface,
	*  - parent: Une référence sur un objet Component,
	*  - isPreProcessing: Un booléen indiquant s'il s'agit d'une instruction de 
	*    préprocessing (true) ou non.
	* 
	* Retourne: Un objet IsisParameter issu du décodage de l'instruction de 
	* processing.
	* 
	* Lève: InnerException.
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
			// A ce jour, la seule instruction qui soit supportée est
			// displayMessage
			if(instruction.startsWith("displayMessage(") == false) {
				trace_errors.writeTrace("Mauvais format de l'instruction: " +
					instruction);
				// Le format de l'instruction est incorrect, on lève une 
				// exception
				trace_methods.endOfMethod();
				throw new InnerException(
					"&ERR_IncorrectPreprocessingInstruction", instruction, 
					null);
			}
			// On va récupérer le message
			String message = getValue(instruction.substring(15,
				instruction.length() - 1), context);
			// On va afficher le message à l'utilisateur
			DialogManager.displayDialog("Information", message, null, parent);
			// On peut sortir
			trace_methods.endOfMethod();
			return parameter;
		}
		// On découpe l'instruction à partir du signe égal
		int position = instruction.indexOf("=");
		if(position == -1)
		{
			trace_errors.writeTrace("Mauvais format de l'instruction: " +
				instruction);
			// Le format de l'instruction est incorrect, on lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_IncorrectPreprocessingInstruction",
				instruction, null);
		}
		// Suppression des blancs avant et après le nom de la variable
		String name = instruction.substring(0, position).trim();
		// Suppression des blancs avant et après la valeur
		String value = getValue(instruction.substring(position + 1).trim(),
		    context);
		// Est-ce que la valeur nécessite une saisie de l'utilisateur ?
		if(value.startsWith("getValue(") == true)
		{
			String prompt = getValue(value.substring(9, value.length() - 1),
				context);
			value = getValueFromUser(prompt, parent);
		}
        // Est-ce que la valeur nécessite le choix de l'utilisateur dans une liste?
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
		// Est-ce que la valeur nécessite une sélection de fichier ou de 
		// répertoire par l'utilisateur ?
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
			// On va décomposer les arguments de la sélection
			String select_arguments = getValue(value.substring(cut_start, 
				value.length() - 1), context);
			// Les différentes arguments sont séparés par une virgule
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(select_arguments, ",");
			// Tous les arguments étant optionnels, ont doit vérifier s'ils
			// existent
			if(tokenizer.getTokensCount() > 0)
			{
				// On peut extraire le répertoire de départ
				initial_directory = tokenizer.getToken(0);
			}
			if(tokenizer.getTokensCount() > 1)
			{
				// On peut extraire le type d'acceptation
				accept_type = tokenizer.getToken(1);
			}
			if(tokenizer.getTokensCount() > 2)
			{
				// On peut extraire le type de présélection
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
	* Cette méthode statique permet d'afficher une fenêtre à l'utilisateur lui
	* permettant de saisir une valeur pour un paramètre de préprocessing. Le
	* message à afficher comme invite à l'utilisateur est le contenu de
	* l'argument prompt.
	*
	* Si un problème survient lors de la saisie du paramètre, ou si
	* l'utilisateur annule la saisie, l'exception InnerException est levée.
	*
	* Arguments:
	*  - prompt: Le message de la fenêtre de saisie,
	*  - parent: Une référence sur un objet Component.
	*
	* Retourne: La valeur saisie par l'utilisateur.
	*
	* Lève: InnerException.
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
		// On va afficher une boîte de dialogue de saisie
		String value = JOptionPane.showInputDialog(parent, prompt, 
			MessageManager.getMessage("&YesNoQuestion"),
			JOptionPane.QUESTION_MESSAGE);
		trace_debug.writeTrace("L'utilisateur a saisi=" + value);
		if(value == null)
		{
			// L'utilisateur a annulé, on lève une exception
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
	* Cette méthode statique permet d'afficher une fenêtre à l'utilisateur lui
	* permettant de choisir une valeur pour un paramètre de préprocessing. La
    * liste de choix est la liste des clefs fournies par la table passée en 
    * parametre. Le message à afficher comme invite à l'utilisateur est le
    * contenu de l'argument prompt.
	*
	* Si un problème survient lors de la saisie du paramètre, ou si
	* l'utilisateur annule la saisie, l'exception InnerException est levée.
	*
	* Arguments:
	*  - message: Le message de la fenêtre de saisie,
    *  - table_name: La table a interroger,
	*  - parent: Une référence sur un objet Component.
    *  - context: context d'execution
    *  - serviceSession: Une référence sur une interface
	*    ServiceSessionInterface,
	*
	* Retourne: La valeur choisie par l'utilisateur.
	*
	* Lève: InnerException.
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
         * Classe interne stockant une liste renvoyée par un Select
         * Dans un JOptionPane, permet d'afficher les champs, puis permet de
         * recuperer uniquement la clef de l'element selectionné
         */
        class IsisParameterOption
        {

            private String _key;
            private IsisParameter[] _fields;
            private IsisTableDefinition _definition;

            /**
             * Constructeur de IsisParameterOption
             * 
             * @param fields ligne renvoyée par un Select. Doit contenir les
             * clefs primaire en premier. Ils seront supprimés de l'affichage
             * @param definition la IsisTableDefinition corresondante à fields
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
             * methode utilisée dans le JOptionPane pour afficher le texte
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
             * recupère la clef sous forme de texte
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

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(serviceSession);

        // condition des paramètres.
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
            // On découpe la liste des colonnes
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
            //Si aucune colonne n'est donnée, on ne met que les clefs
            
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

		// On va afficher une boîte de dialogue de saisie
		IsisParameterOption choice = (IsisParameterOption) JOptionPane.showInputDialog(parent, message,
			MessageManager.getMessage("&YesNoQuestion"),
			JOptionPane.QUESTION_MESSAGE,null, options, options[0]);
		
		if(choice == null)
		{
			// L'utilisateur a annulé, on lève une exception
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
	* Cette méthode statique permet d'extraire la valeur d'une chaîne de valeur
	* ayant le format '<value>' ou "<value>". Elle commence par supprimer les
	* quotes.
	* Ensuite, la chaîne est évaluée de sorte à résoudre toute référence à une 
	* variable.
	*
	* Arguments:
	*  - valueChain: La valeur dont il faut supprimer les quotes s'il y en a,
	*  - context: Une liste indexée fournissant les valeurs de certaines
	*    variables.
	*
	* Retourne: La valeur sans les quotes et évaluée.
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
		// Est-ce que la chaîne existe et est non vide ?
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
	* Cette méthode statique permet d'afficher à l'utilisateur une boite de 
	* dialogue d'exploration du système de fichiers afin qu'il puisse 
	* sélectionner un fichier ou un répertoire, suivant les valeurs des 
	* arguments.
	* 
	* Si l'argument selectFile vaut true, il s'agira pour l'utilisateur de 
	* sélectionner un fichier. Sinon, il s'agira de sélectionner un répertoire.
	* L'argument initialDirectory peut avoir une valeur non nulle, celle-ci 
	* devant être un chemin absolu sur le système de fichiers de l'Agent.
	* L'argument acceptType peut avoir une valeur non nulle, celle-ci devant 
	* être soit "ALL" ou "EXISTING".
	* L'argument presetType peut avoir une valeur non nulle, celle-ci devant 
	* être soit "NONE", soit "EXECUTABLES", soit "DICTIONARIES", soit 
	* "DATAFILES", ou encore "COMMANDPANELS".
	* 
	* Si une erreur survient lors de l'affichage de la boite de dialogue, ou 
	* si l'utilisateur annule la saisie, l'exception InnerException est levée.
	*
	* Arguments:
	*  - selectFile: Un booléen indiquant le type d'entrée devant être 
	*    sélectionnée,
	*  - initialDirectory: Le chemin absolu du répertoire de départ de 
	*    l'exploration,
	*  - acceptType: Le type d'acceptation de l'entrée,
	*  - presetType: Le type de présélection des répertoires d'exploration,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - serviceSession: Une référence sur une interface 
	*    ServiceSessionInterface,
	*  - parent: Une référence sur un objet Component.
	* 
	* Retourne: Le chemin absolu de l'entrée sélectionnée.
	* 
	* Lève: InnerException.
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
		// On va vérifier que les arguments importants sont valides
		if(windowInterface == null || agentLayerMode == null ||
			agentLayerMode.equals("") == true || serviceSession == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va instancier la boite de dialogue
		file_chooser = new RemoteFileChooser(windowInterface, agentLayerMode);
		// On va définir les paramètres de fonctionnement
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
		// Il ne reste plus qu'à commander l'affichage de la boite de dialogue
		if(file_chooser.showDialog(serviceSession, parent, 
			initialDirectory) == RemoteFileChooser.CANCEL_OPTION)
		{
			// L'utilisateur a annulé, on lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InputCanceled", null, null);
		}
		trace_methods.endOfMethod();
		// On retourne la sélection de l'utilisateur
		return file_chooser.getSelection();
	}
}
