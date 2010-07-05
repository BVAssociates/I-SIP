/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/SimpleDialog.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'administration d'une table quelconque
* DATE:        08/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SimpleDialog.java,v $
* Revision 1.8  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.7  2008/02/13 16:26:09  tz
* Gestion des valeurs absentes sur liste déroulante.
* Gestion des cas de valeurs nulles.
*
* Revision 1.6  2008/01/31 16:45:33  tz
* Gestion de la position de la colonne dans la clé étrangère.
*
* Revision 1.5  2008/01/16 15:47:56  tz
* Renvoi de l'erreur lors de la construction du panneau.
*
* Revision 1.4  2008/01/08 17:38:46  tz
* Ajout de la méthode createConditionForForeignKey().
* Gestion du filtrage des valeurs des listes déroulantes lorsque :
*  - la clé étrangère pointe sur la table parent,
*  - la clé étrangère contient la clé primaire de la table.
*
* Revision 1.3  2007/12/28 17:40:59  tz
* Sortie de fonction sur erreur.
*
* Revision 1.2  2007/12/07 10:31:40  tz
* Evolution pour la prise en compte des FKEY.
* Création des DialogObject.
* Gestion des vérifications.
*
* Revision 1.1  2006/03/08 14:07:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: SimpleDialog
* 
* Description:
* Cette classe est une spécialisation de la classe BaseDialog chargée de 
* permettre l'administration d'une table quelconque.
* Elle est destinée à permettre la saisie des informations constituant les 
* champs d'une ligne de données de la table à éditer sous forme d'un ensemble 
* de champs de saisie simples, ou de listes pré-remplies dans le cas de tables 
* disposant de clés étrangères.
* Elle ne redéfinit que les méthodes validateInput() et makeFormPanel().
* ----------------------------------------------------------*/
public class SimpleDialog 
	extends BaseDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SimpleDialog
	* 
	* Description:
	* Constructeur. Il permet d'initialiser une instance de la classe.
	* 
	* Si une erreur survient lors de l'initialisation de l'instance, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - selectedNode: Une référence sur un objet GenericTreeObjectNode,
	*  - action: Une chaîne contenant l'action destinée à être exécutée,
	*  - dialogCaller: Une référence sur l'interface DialogCallerInterface.
	* 
	* Lève: InnerException.
 	* ----------------------------------------------------------*/
 	public SimpleDialog(
 		GenericTreeObjectNode selectedNode,
 		String action,
 		DialogCallerInterface dialogCaller
 		)
 		throws
 			InnerException {
 		super(selectedNode, action, dialogCaller);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "SimpleDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("dialogCaller=" + dialogCaller);
		_columnObject = new Hashtable();
		_keyObjects = null;
		// On construit le panneau
		makePanel();
		trace_methods.endOfMethod();

 	}

	/*----------------------------------------------------------
	* Nom: validateInput
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque l'utilisateur a cliqué sur le bouton "Valider".
	* Elle va vérifier la validité des données saisies en appelant la méthode 
	* checkValue() pour chacun des objets DialogObject associé aux champs, 
	* puis va construire la chaîne de données, via un appel à la méthode 
	* buildAdministrationCommand(), et enfin va commander la mise à jour de la 
	* table, via la méthode execute().
	* ----------------------------------------------------------*/
	public void validateInput() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "validateInput");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		StringBuffer values = null;
		IsisTableDefinition definition = null;

		trace_methods.beginningOfMethod();
		values = new StringBuffer();
		// On va commencer par récupérer le dictionnaire de la table
		GenericTreeObjectNode selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		try
		{
			definition = 
				manager.getTableDefinition(selected_node.getAgentName(), 
				selected_node.getIClesName(), selected_node.getServiceType(), 
				getTableName(), selected_node.getContext(true), 
				selected_node.getServiceSession());
		}
		catch(InnerException exception)
		{
			// Ca ne devrait normalement pas arriver
			trace_errors.writeTrace("Erreur lors de la récupération de la " +
				"définition de la table " + getTableName() + ": " + 
				exception.getMessage());
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		try {
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < definition.columns.length ; index ++)
			{
				DialogObject dialog_object = null;
				String value = null;
				
				IsisTableColumn column = definition.columns[index];
				trace_debug.writeTrace("Traitement de la colonne " + 
					column.name);
				// On va récupérer l'objet de dialogue associé à la colonne
				dialog_object = (DialogObject)_columnObject.get(column.name);
				// Si l'objet de dialogue est null, on arrête
				if(dialog_object == null) {
					trace_errors.writeTrace("L'objet de dialogue pour la " +
						"colonne " + column.name + " est null !");
					values = null;
					String[] extra_info = { column.name };
					getMainWindowInterface().showPopup("Error", 
						"&ERR_NoDialogObjectForColumn", extra_info);
					trace_methods.endOfMethod();
					return;
				}
				// On va vérifier si la valeur est correcte
				if(dialog_object.checkValue() == false) {
					String[] extra_infos = { column.name };
					// On va afficher un message d'erreur
					getMainWindowInterface().showPopup("Error",
						"&ERR_InvalidValue", extra_infos);
					// On arrête
					values = null;
					trace_methods.endOfMethod();
					return;
				}
				// On va ajouter la valeur du champ
				if(index > 0)
				{
					// On ajoute le séparateur
					values.append(definition.separator);
				}
				value = dialog_object.getValue();
				trace_debug.writeTrace("value=" + value);
				values.append(value);
			}
			// On peut construire la commande et l'exécuter
			if(values != null) {
				trace_debug.writeTrace("values=" + values);
				execute(buildAdministrationCommand(values.toString()));
			}
		}
		catch(Exception e) {
			manager.releaseTableDefinitionLeasing(definition);
			trace_errors.writeTrace("Erreur lors de la validation des " +
				"données: " + e.getMessage());
			// On va afficher l'erreur à l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileValidatingData", e);
		}
		hide();
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: makeFormPanel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque le panneau de saisie des données doit être construit.
	* Dans le cas de la saisie des données d'une table quelconque, le panneau 
	* est construit de sorte à ce qu'il y ait une zone de saisie par colonne 
	* de la table dans la majorité des cas, une liste déroulante pour les 
	* booléens et également pour les colonnes composant une clé étrangère vers 
	* une autre table, via la méthode createObjectForForeignKey().
	* 
	* Si une erreur survient lors de la création du panneau de saisie, 
	* l'exception InnerException est levée.
	* 
	* Retourne: Une référence sur le panneau contenant le formulaire de saisie.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected JPanel makeFormPanel()
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "makeFormPanel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int counter = 0;

		trace_methods.beginningOfMethod();
		// Message d'état
		getMainWindowInterface().setProgressMaximum(2);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 0);
		// On va construire un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1,
			0, 50, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(1, 1, 1, 1), 0, 0);
		JPanel panel = new JPanel(layout);
		// On récupère le dictionnaire de la table
		GenericTreeObjectNode selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition definition = manager.getTableDefinition(
			selected_node.getAgentName(), selected_node.getIClesName(),
			selected_node.getServiceType(), getTableName(),
			selected_node.getContext(true), selected_node.getServiceSession());
		try {
			// On va créer la liste des objets de clés
			_keyObjects = new DialogObject[definition.foreignKeys.length];
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < definition.columns.length ; index ++)
			{
				DialogObject parent_object = null;
				DialogObject dialog_object = null;
				int column_index_in_key = -1;
				boolean is_editable = true;
				
				IsisTableColumn column = definition.columns[index];
				trace_debug.writeTrace("Traitement de la colonne: " +
					column.name);
				// On va regarder si la colonne est dans une clé étrangère
				for(int loop = 0 ; loop < definition.foreignKeys.length ; 
					loop ++) {
					boolean is_new_object = false;
					
					IsisForeignKey foreign_key = definition.foreignKeys[loop];
					column_index_in_key = getColumnIndexInForeignKey(column, 
						foreign_key); 
					if(column_index_in_key != -1) {
						// La colonne est dans une clé étrangère, on va
						// construire un objet si ce n'est pas déjà fait
						if(_keyObjects[loop] == null) {
							_keyObjects[loop] = createObjectForForeignKey(
								foreign_key, selected_node, column, 
								definition);
							is_new_object = true;
						}
						// Si la colonne est seule dans la clé étrangère, il ne
						// sera pas nécessaire de fabriquer un objet 
						// supplémentaire pour elle
						if(foreign_key.links.length == 1) {
							dialog_object = _keyObjects[loop];
						}
						else {
							// Sinon, il faut créer un objet qui sera rattaché 
							// à celui représentant la clé étrangère
							parent_object = _keyObjects[loop];
							is_editable = false;
							if(is_new_object == true) {
								// On ajoute le composant au panneau
								addComponentInPanel(
									parent_object.getEditionComponent(), panel,
									layout, constraints, counter++, false, 
									null);
							}
						}
						break;
					}
				}
				if(dialog_object == null) {
					// Il faut créer un objet de saisie pour la colonne
					dialog_object = new DialogObject(column.type, is_editable,
						isColumnInKey(column, definition), 
						isColumnInNotNull(column, definition));
					if(parent_object != null) {
						// On ajoute l'objet comme rattaché à celui 
						// représentant la clé étrangère
						parent_object.addKeyField(dialog_object, 
							column_index_in_key);
					}
				}
				// On ajoute le composant au panneau
				addComponentInPanel(dialog_object.getEditionComponent(), panel,
					layout, constraints, counter++, true, column.name);
				// On l'ajoute à la table de Hash
				_columnObject.put(column.name, dialog_object);
			}
			getMainWindowInterface().setStatus("&Status_BuildingDialog", 
				null, 1);
			// On va gérer le positionnement des valeurs en fonction de l'action
			manageAction(definition);
		}
		catch(InnerException exception) {
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de la construction du " +
				"panneau de saisie: " + exception);
			// On renvoie l'exception
			throw exception;
		}
		manager.releaseTableDefinitionLeasing(definition);
		getMainWindowInterface().setStatus(null, null, 0);
		trace_methods.endOfMethod();
		return panel;
	}

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
			Throwable {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "finalize");

		trace_methods.beginningOfMethod();
		_columnObject.clear();
		_columnObject = null;
		_keyObjects = null;
		trace_methods.endOfMethod();
	}
	
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _columnObject
	* 
	* Description:
	* Cet attribut maintient une référence paramétrée sur un objet 
	* DialogObject correspondant à une zone de saisie pour une colonne. Le 
	* paramétrage correspond au nom de la colonne.
	* Cet référence paramétrée est implémentée sous la forme d'une table de 
	* hash.
	* ----------------------------------------------------------*/
	private Hashtable _columnObject;

	/*----------------------------------------------------------
	* Nom: _keyObjects
	* 
	* Description:
	* Cet attribut maintient une liste d'objet DialogObject correspondant aux 
	* clés étrangères de la table. La liste est implémentée sous forme d'un 
	* tableau.
	* ----------------------------------------------------------*/
	private DialogObject[] _keyObjects;

	/*----------------------------------------------------------
	* Nom: createObjectForForeignKey
	* 
	* Description:
	* Cette méthode est chargée de la construction d'un DialogObject associé 
	* à une déclaration de clé étrangère.
	* La définition de la clé étrangère est utilisée afin de constituer la 
	* liste des colonnes sélectionnées, ainsi que pour récupérer le nom de la 
	* table étrangère.
	* La sélection est effectuée sur ces données, et est utilisée pour créer 
	* l'objet DialogObject représentant la clé étrangère.
	* Les objets DialogObject correspondant aux colonnes constituant la clé 
	* étrangères doivent être associés à celui représentant la clé afin de 
	* permettre la mise à jour automatique des valeurs lors de la sélection 
	* d'un élément dans la liste déroulante.
	* 
	* Si un problème survient lors de l'exécution de la sélection, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - foreignKey: La clé étrangère pour laquelle un objet DialogObject doit 
	*    être créé,
	*  - selectedNode: Le noeud d'exploration permettant l'exécution de la 
	*    sélection,
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne concernée,
	*  - definition: Une référence sur un objet IsisTableDefinition 
	*    correspondant à la table concernée.
	* 
	* Retourne: Une référence sur un objet DialogObject correspondant à la clé 
	* étrangère.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private DialogObject createObjectForForeignKey(
		IsisForeignKey foreignKey,
		GenericTreeObjectNode selectedNode,
		IsisTableColumn column,
		IsisTableDefinition definition
		)
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "createObjectForForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		DialogObject dialog_object = null;
		String[] selected_columns;
		String[] result = null;
		String[] values = null;
		ServiceSessionInterface service_session = null;
		IndexedList node_context = null;
		IsisTableDefinition foreign_definition = null;
		char column_type = 0;
		boolean alone_in_key = false;
		String values_separator = null;
		boolean is_in_key = false;
		boolean is_in_not_null = false;
		String parent_table_name = null;
		GenericTreeObjectNode parent_node = null; 

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On va vérifier que les arguments sont valides
		if(foreignKey == null || foreignKey.links == null || 
			foreignKey.links.length == 0 || selectedNode == null) {
			trace_errors.writeTrace("Au moins un des arguments n'est pas" +
				" valide");
			// On lève une exception
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va construire la liste des colonnes
		if(foreignKey.links.length == 1) {
			alone_in_key = true;
			if(column == null || definition == null) {
				trace_errors.writeTrace("L'argument column n'est pas valide");
				// On lève une exception
				throw new InnerException("&ERR_InvalidArgument", null, null);
			}
			column_type = column.type;
			is_in_key = isColumnInKey(column, definition);
			is_in_not_null = isColumnInNotNull(column, definition);
		}
		service_session = selectedNode.getServiceSession();
		node_context = selectedNode.getContext(true);
		if(alone_in_key == false) {
			// Il faut récupérer le dictionnaire de la table distante afin d'en 
			// connaître le séparateur
			TableDefinitionManager definition_manager = 
				TableDefinitionManager.getInstance();
			foreign_definition = definition_manager.getTableDefinition(
				selectedNode.getAgentName(), selectedNode.getIClesName(), 
				selectedNode.getServiceType(), foreignKey.foreignTableName, 
				node_context, service_session);
			values_separator = foreign_definition.separator;
			definition_manager.releaseTableDefinitionLeasing(
				foreign_definition);
		}
		// On va regarder si on doit filtrer les résultats
		if(getAction().equals("Insert") == true || 
			getAction().equals("Replace") == true) {
			parent_node = getSelectedNode();
			while(parent_node != null) {
				// On recherche le premier nom de table qui ne soit pas 
				// identique à la table courante
				parent_table_name = parent_node.getTableName();
				if(parent_table_name == null || 
					parent_table_name.equals("") == true ||
					parent_table_name.equals(getTableName()) == false) {
					break;
				}
				parent_node = (GenericTreeObjectNode)parent_node.getParent();
			}
		}
		if(parent_table_name != null && 
			parent_table_name.equals("") == false && 
			foreignKey.foreignTableName.equals(parent_table_name) == true) {
			// La clé étrangère pointe sur la table parent, il faut n'afficher 
			// que les données correspondant à l'objet parent
			IsisParameter[] parent_parameters = 
				parent_node.getObjectParameters();
			values = new String[1];
			// On va construire la valeur de la clé à partir des données du
			// parent
			StringBuffer temp_value = new StringBuffer();
			for(int index = 0 ; index < foreignKey.links.length ; index ++) {
				if(index > 0) {
					temp_value.append(values_separator);
				}
				temp_value.append(getParameterValue(
					foreignKey.links[index].foreignColumnName, 
					parent_parameters));
			}
			values[0] = temp_value.toString();
		}
		else {
			String condition = "";
			
			selected_columns = new String[foreignKey.links.length];
			for(int index = 0 ; index < foreignKey.links.length ; index ++) {
				selected_columns[index] = 
					foreignKey.links[index].foreignColumnName;
			}
			if(getAction().equals("Replace") == true) {
				// Pour la modification, on filtre éventuellement les données
				condition = createConditionForForeignKey(foreignKey,
					definition.key, node_context);
			}
			// On va exécuter la sélection des données sur la table étrangère
			ServiceSessionProxy session_proxy = 
				new ServiceSessionProxy(service_session);
			result = session_proxy.getWideSelectResult(
				selectedNode.getAgentName(), foreignKey.foreignTableName, 
				selected_columns, condition, "", node_context);
			// On va créer le tableau des valeurs en ne prenant qu'à partir du
			// deuxième élément
			values = new String[result.length - 1];
			System.arraycopy(result, 1, values, 0, values.length);
		}
		// On peut maintenant créer l'objet DialogObject associé
		dialog_object = new DialogObject(column_type, values,
			values_separator, foreignKey.links.length, alone_in_key, true, 
			is_in_key, is_in_not_null);
		trace_methods.endOfMethod();
		return dialog_object;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInKey
	* 
	* Description:
	* Cette méthode permet de savoir si la colonne passée argument fait partie 
	* de la clé primaire de la table dont la définition est passée en second 
	* argument.
	* La vérification est effectuée en recherchant la présence de la colonne 
	* dans le champ 'key' de la définition.
	* 
	* Arguments:
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne concernée,
	*  - definition: Une référence sur un objet IsisTableDefinition 
	*    correspondant à la table concernée.
	* 
	* Retourne: true si la colonne est référencée dans la clé de la table, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean isColumnInKey(
		IsisTableColumn column,
		IsisTableDefinition definition
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "isColumnInKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_in_key = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On vérifie la validité des arguments
		if(column == null || definition == null) {
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return is_in_key;
		}
		// Normalement, il y a au moins un champ dans la clé primaire
		if(definition.key == null || definition.key.length == 0) {
			trace_debug.writeTrace("La table n'a pas de clé primaire");
			// La table n'a pas de clé primaire, on peut sortir
			trace_methods.endOfMethod();
			return is_in_key;
		}
		// On va parcourir les colonnes de la clé pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < definition.key.length ; index ++) {
			if(definition.key[index].equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a été trouvée dans " +
					"la clé primaire");
				is_in_key = true;
				// On peut arrêter la recherche
				break;
			}
		}
		if(is_in_key == false) {
			trace_debug.writeTrace("La colonne n'a pas été trouvée dans la " +
				"clé primaire");
		}
		trace_methods.endOfMethod();
		return is_in_key;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInNotNull
	* 
	* Description:
	* Cette méthode permet de savoir si la colonne passée argument fait partie 
	* des champs non nuls de la table dont la définition est passée en second 
	* argument.
	* La vérification est effectuée en recherchant la présence de la colonne 
	* dans le champ 'notNull' de la définition.
	* 
	* Arguments:
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne concernée,
	*  - definition: Une référence sur un objet IsisTableDefinition 
	*    correspondant à la table concernée.
	* 
	* Retourne: true si la colonne est référencée dans les champs non nuls, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean isColumnInNotNull(
		IsisTableColumn column,
		IsisTableDefinition definition
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "isColumnInNotNull");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_in_not_null = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On vérifie la validité des arguments
		if(column == null || definition == null) {
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return is_in_not_null;
		}
		// Normalement, il y a au moins un champ dans la clé primaire
		if(definition.notNull == null || definition.notNull.length == 0) {
			trace_debug.writeTrace("La table n'a pas de champs non nuls");
			// La table n'a pas de clé primaire, on peut sortir
			trace_methods.endOfMethod();
			return is_in_not_null;
		}
		// On va parcourir les colonnes de la clé pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < definition.notNull.length ; index ++) {
			if(definition.notNull[index].equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a été trouvée dans " +
					"les champs non nuls");
				is_in_not_null = true;
				// On peut arrêter la recherche
				break;
			}
		}
		if(is_in_not_null == false) {
			trace_debug.writeTrace("La colonne n'a pas été trouvée dans les " +
				"champs non nuls");
		}
		trace_methods.endOfMethod();
		return is_in_not_null;
	}

	/*----------------------------------------------------------
	* Nom: getColumnIndexInForeignKey
	* 
	* Description:
	* Cette méthode permet de connaître la position de la colonne passée 
	* argument dans la clé étrangère dont la définition est passée en second 
	* argument.
	* La vérification est effectuée en recherchant la présence de la colonne 
	* dans le champ 'localColumnName' des liens de la définition.
	* 
	* Arguments:
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne concernée,
	*  - foreignKey: Une référence sur un objet IsisForeignKey correspondant à 
	*    la clé étrangère concernée.
	* 
	* Retourne: -1 si la colonne ne fait pas partie de la clé étrangère, sinon 
	* la position de celle-ci.
	* ----------------------------------------------------------*/
	private int getColumnIndexInForeignKey(
		IsisTableColumn column,
		IsisForeignKey foreignKey
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "getColumnIndexInForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int field_position = -1;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		// On vérifie la validité des arguments
		if(column == null || foreignKey == null || 
			foreignKey.links == null || foreignKey.links.length == 0) { 
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return field_position;
		}
		// On va parcourir les liens de la clé pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < foreignKey.links.length ; index ++) {
			if(foreignKey.links[index].localColumnName.equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a été trouvée dans " +
					"la clé étrangère");
				field_position = index;
				// On peut arrêter la recherche
				break;
			}
		}
		if(field_position == -1) {
			trace_debug.writeTrace("La colonne n'a pas été trouvée dans la " +
				"clé étrangère");
		}
		trace_methods.endOfMethod();
		return field_position;
	}

	/*----------------------------------------------------------
	* Nom: addComponentInPanel
	* 
	* Description:
	* Cette méthode permet de gérer l'insertion du composant graphique passé 
	* en argument dans le panneau de saisie.
	* Si l'argument 'setLabel' est positionné à true, un libellé est tout 
	* d'abord ajouté dans la zone de gauche, contenant le libellé 'label'.
	* Ensuite, le composant graphique est positionné en zone de droite.
	* L'ensemble est placé sur la ligne d'incide 'index'.
	* 
	* Arguments:
	*  - component: Une référence sur un objet JComponent correspondant au 
	*    composant graphique à positionner,
	*  - panel: Le panneau dans lequel positionner le composant,
	*  - layout: Une référence sur le LayoutManager du panneau,
	*  - constraints: Une référence sur les contraintes de positionnement,
	*  - index: L'indice de la ligne d'insertion du composant,
	*  - setLabel: Un booléen indiquant si un libellé doit être construit,
	*  - label: Le libellé à construire.
 	* ----------------------------------------------------------*/
	private void addComponentInPanel(
		JComponent component,
		JPanel panel,
		GridBagLayout layout,
		GridBagConstraints constraints,
		int index,
		boolean setLabel,
		String label
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "addComponentInPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Component label_component = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		trace_arguments.writeTrace("panel=" + panel);
		trace_arguments.writeTrace("layout=" + layout);
		trace_arguments.writeTrace("constraints=" + constraints);
		trace_arguments.writeTrace("index=" + index);
		trace_arguments.writeTrace("setLabel=" + setLabel);
		trace_arguments.writeTrace("label=" + label);
		// On vérifie la validité des arguments
		if(component == null || panel == null || layout == null ||
			constraints == null) {
			trace_errors.writeTrace("Au moins un argument n'est pas valide");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On fabrique le libellé du champ si nécessaire
		if(setLabel == true) {
			label_component = new JLabel(label + " :");
		}
		else {
			label_component = Box.createHorizontalGlue();
		}
		// On fixe les contraintes
		constraints.weightx = 0;
		constraints.gridx = 0;
		constraints.gridy = index;
		// On l'ajoute au panneau
		layout.setConstraints(label_component, constraints);
		panel.add(label_component);
		// On positionne la validation par entrée
		setEnterCallback(component);
		// On fixe les contraintes
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		// On l'ajoute au panneau
		layout.setConstraints(component, constraints);
		panel.add(component);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: manageAction
	* 
	* Description:
	* Cette méthode est chargée de gérer le postionnement des valeurs dans les 
	* différentes zones en fonction de l'action concernée.
	* Dans le cas d'un ajout, les valeurs sont éventuellement prélevées depuis 
	* le contexte du noeud, ou plus exactement des parents.
	* Dans le cas d'une modification ou d'une suppression, les valeurs sont 
	* prélevées depuis les paramètres du noeud.
	* 
	* Si une erreur survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - definition: Une référence sur un objet IsisTableDefinition 
	*    correspondant à la définition de la table.
	*
	* Lève: InnerException.
 	* ----------------------------------------------------------*/
	private void manageAction(
		IsisTableDefinition definition
		)
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "manageAction");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String action = null;
		IsisParameter[] values = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("definition=" + definition);
		// On vérifie la validité de l'argument
		if(definition == null) {
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		action = getAction();
		trace_debug.writeTrace("action=" + action);
		if(action.equals("Insert") == true) {
			values = (IsisParameter[])getSelectedNode().getContext(
					true).toArray(new IsisParameter[0]);
		}
		else {
			values = getSelectedNode().getObjectParameters();
		}
		// On va traiter les clés étrangères une par une
		for(int index = 0 ; index < definition.foreignKeys.length ; index ++) {
			IsisForeignKey foreign_key = definition.foreignKeys[index];
			DialogObject dialog_object = _keyObjects[index];
			StringBuffer key_value = new StringBuffer();
			boolean fkey_in_key = true;
			boolean set_enabled = true;
			
			trace_debug.writeTrace("Traitement de la clé: " + index);
			if(dialog_object == null) {
				continue;
			}
			// On va tenter de construire la valeur de la clé
			for(int loop = 0 ; loop < foreign_key.links.length ; loop ++) {
				String column_name = foreign_key.links[loop].localColumnName;
				String column_value = null;
				
				if(loop != 0) {
					key_value.append(dialog_object.getKeyValuesSepatator());
				}
				trace_debug.writeTrace("Traitement de la colonne de clé: " +
					column_name);
				column_value = getParameterValue(column_name, values);
				trace_debug.writeTrace("column_value=" + column_value);
				if(column_value == null) {
					// Il y a au moins une colonne de vide dans la clé, on ne
					// positionnera pas de valeur
					key_value = null;
					break;
				}
				key_value.append(column_value);
				if(fkey_in_key == true) {
					if(isColumnInKey(new IsisTableColumn(column_name, 1, 's'),
						definition) == false) {
						fkey_in_key = false;
					}
				}
			}
			if(key_value == null) {
				dialog_object.setValue(null, false);
			}
			else {
				// On n'ajoute la valeur que dans le cas de la suppression
				boolean add_if_missing = action.equals("Remove");
				dialog_object.setValue(key_value.toString(), add_if_missing);
			}
			if(action.equals("Remove") == true || (
				action.equals("Replace") == true && fkey_in_key == true)) {
				set_enabled = false;
			}
			dialog_object.getEditionComponent().setEnabled(set_enabled);
		}
		// On va traiter les colonnes une par une
		for(int index = 0 ; index < definition.columns.length ; index ++)
		{
			DialogObject dialog_object = null;
			String column_value = null;
			boolean next_column = false;
			boolean set_enabled = true;
			
			IsisTableColumn column = definition.columns[index];
			trace_debug.writeTrace("Traitement de la colonne: " +
				column.name);
			// On va regarder si la colonne est dans une clé étrangère
			for(int loop = 0 ; loop < definition.foreignKeys.length ; 
				loop ++) {
				IsisForeignKey foreign_key = definition.foreignKeys[loop];
				if(getColumnIndexInForeignKey(column, foreign_key) != -1) {
					// La colonne est dans une clé étrangère, on passe à la 
					// suivante
					next_column = true;
					break;
				}
			}
			if(next_column == true) {
				continue;
			}
			// On va récupérer l'objet de représentation depuis la
			// table de Hash
			dialog_object = (DialogObject)_columnObject.get(column.name);
			// On va récupérer éventuellement la valeur
			column_value = getParameterValue(column.name, values);
			if(dialog_object != null) {
				// Il y a une valeur, on va la positionner
				dialog_object.setValue(column_value, false);
				if(action.equals("Remove") == true || 
					(action.equals("Replace") == true &&
					isColumnInKey(column, definition) == true)) {
					set_enabled = false;
				}
				dialog_object.getEditionComponent().setEnabled(set_enabled);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getParameterValue
	* 
	* Description:
	* Cette méthode est chargée de retourner la valeur éventuelle du paramètre 
	* dont le nom est passé en argument en recherchant sa présence dans le 
	* tableau passé en second argument.
	* Si le paramètre est trouvé, sa valeur est retournée. Sinon, la méthode 
	* retourne null.
	* 
	* Arguments:
	*  - parameterName: Nom du paramètre dont on souhaite récupérer la valeur,
	*  - parametersArray: Un tableau de IsisParameter dans lequel chercher la 
	*    présence du paramètre.
	* 
	* Retourne: La valeur du paramètre, ou null.
 	* ----------------------------------------------------------*/
	private String getParameterValue(
		String parameterName,
		IsisParameter[] parametersArray
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "getParameterValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterName=" + parameterName);
		trace_arguments.writeTrace("parametersArray=" + parametersArray);
		// On vérifie la validité des arguments
		if(parameterName == null || parameterName.equals("") == true ||
			parametersArray == null || parametersArray.length == 0) {
			trace_debug.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return value;
		}
		// On va rechercher le paramètre dans le tableau
		for(int index = 0 ; index < parametersArray.length ; index ++) {
			if(parametersArray[index].name.equals(parameterName) == true) {
				value = parametersArray[index].value;
				trace_debug.writeTrace("Paramètre trouvé. Valeur: " + value);
				// Le paramètre a été trouvé, on peut arrêter de chercher
				break;
			}
		}
		trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: createConditionForForeignKey
	* 
	* Description:
	* Cette méthode est chargée de construire une condition de filtrage des 
	* données en fonction d'une clé étrangère, d'une clé primaire et d'un 
	* contexte.
	* Cette condition de sélection est construite en prenant, pour chaque 
	* colonne de la clé appartenant à la clé étrangère, la valeur depuis le 
	* contexte et le nom de la colonne distante.
	* 
	* Arguments:
	*  - foreignKey: La définition de la clé étrangère,
	*  - key: La définition de la clé primaire,
	*  - context: Le contexte fournissant les valeurs de filtrage.
	* 
	* Retourne: Une chaîne correspondant à la condition de filtrage, ou une 
	* chaîne vide si aucune condition ne peut être établie.
	* ----------------------------------------------------------*/
	private String createConditionForForeignKey(
		IsisForeignKey foreignKey,
		String[] key,
		IndexedList context
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "createConditionForForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		StringBuffer condition = new StringBuffer();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		trace_arguments.writeTrace("key=" + key);
		trace_arguments.writeTrace("context=" + context);
		// On vérifie la validité des arguments
		if(key == null || key.length == 0 || foreignKey == null ||
			foreignKey.links == null || foreignKey.links.length == 0 ||
			context == null) {
			trace_debug.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return "";
		}
		// On va rechercher la présence d'au moins une des colonnes de la clé
		// dans la clé étrangère
		for(int index = 0 ; index < key.length ; index ++) {
			trace_debug.writeTrace("Traitement de la colonne: " + key[index]);
			for(int loop = 0 ; loop < foreignKey.links.length ; loop ++) {
				if(key[index].equals(
					foreignKey.links[loop].localColumnName) == true) {
					trace_debug.writeTrace("La colonne de la clé est " +
						"présente dans la clé étrangère");
					// La colonne de la clé est présente, on va ajouter une
					// condition sur la colonne distante
					if(condition.length() > 0) {
						condition.append(" AND ");
					}
					condition.append(foreignKey.links[loop].foreignColumnName);
					condition.append("=");
					IsisParameter parameter = (IsisParameter)context.get(key[index]);
					if(parameter != null) {
						condition.append(parameter.value);
					}
					break;
				}
			}
		}
		trace_debug.writeTrace("condition='" + condition.toString() + "'");
		trace_methods.endOfMethod();
		return condition.toString();
	}
}
