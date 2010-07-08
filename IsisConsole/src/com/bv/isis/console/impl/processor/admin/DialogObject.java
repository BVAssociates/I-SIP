/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/DialogObject.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe 
* DATE:        15/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DialogObject.java,v $
* Revision 1.6  2008/06/27 09:42:35  tz
* Prise en compte du cas où la clé étrangère référence un objet null.
*
* Revision 1.5  2008/06/12 15:48:43  tz
* Sélection automatique de la valeur si la liste déroulante ne contient
* qu'une seule valeur.
*
* Revision 1.4  2008/02/13 16:25:42  tz
* Gestion des valeurs absentes sur liste déroulante.
* Gestion des cas de valeurs nulles.
*
* Revision 1.3  2008/01/31 16:45:10  tz
* Utilisation d'un tableau de DialogObject au lieu d'un vecteur pour
* l'attribut _keyFields.
*
* Revision 1.2  2007/12/28 17:40:01  tz
* Ajout de la valeur par défaut pour un booléen nul.
*
* Revision 1.1  2007/12/07 10:31:52  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
//Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.util.UtilStringTokenizer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JTextField;

//
//Imports du projet
//
import com.bv.isis.console.impl.processor.admin.checkers.CheckerFactory;
import com.bv.isis.console.impl.processor.admin.checkers.ValueCheckInterface;

/*----------------------------------------------------------
* Nom: DialogObject
* 
* Description:
* Cette classe représente un élément de la boîte de dialogue permettant 
* l'administration d'une table. Cet élément est constitué d'un composant 
* graphique, chargé du rendu de la valeur de la colonne ainsi que sa saisie, 
* et d'un ensemble de vérificateurs, permettant de vérifier si la valeur 
* saisie dans le composant correspond à ce qui est attendu.
* ----------------------------------------------------------*/
public class DialogObject {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Il s'agit d'un des constructeurs de la classe. Il prend en argument le 
	* type de la colonne, ainsi que des booléens permettant de définir un 
	* certain nombre de caractéristiques qui seront associées à l'objet.
	* L'objet graphique est construit, via la méthode 
	* createEditionComponent(), puis les vérificateurs sont créés via la 
	* méthode createCheckers().
	* 
	* Arguments:
	*  - columnType: Le type de la colonne pour laquelle une zone de saisie 
	*    est nécessaire,
	*  - isEditable: Un booléen indiquant si la zone de saisie doit être 
	*    éditable,
	*  - isInKey: Un booléen indiquant si la colonne appartient à la clé 
	*    primaire de la table,
	*  - isInNotNull: Un booleén indiquant si la colonne appartient à la liste 
	*    des colonnes non nulles de la table.
	* ----------------------------------------------------------*/
	public DialogObject(
		char columnType,
		boolean isEditable,
		boolean isInKey,
		boolean isInNotNull
		) {	
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "DialogObject");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnType=" + columnType);
		trace_arguments.writeTrace("isEditable=" + isEditable);
		trace_arguments.writeTrace("isInKey=" + isInKey);
		trace_arguments.writeTrace("isInNotNull=" + isInNotNull);
		// Initialisation des valeurs
		_keyValueSeparator = null;
		_representsBoolean = false;
		_editionComponent = null;
		_keyFields = new DialogObject[0];
		_checkers = new Vector();
		// On commence par créer l'objet d'édition
		createEditionComponent(columnType, isEditable, null, false);
		// On va ensuite créer les vérificateurs de valeurs
		createCheckers(columnType, isInKey, isInNotNull);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Il s'agit d'un des constructeurs de la classe. Il prend en argument le 
	* type de la colonne, une liste de valeurs, ainsi que des booléens 
	* permettant de définir un certain nombre de caractéristiques qui seront 
	* associées à l'objet.
	* L'objet graphique est construit, via la méthode 
	* createEditionComponent(), puis les vérificateurs sont créés via la 
	* méthode createCheckers().
	* 
	* Arguments:
	*  - columnType: Le type de la colonne pour laquelle une zone de saisie 
	*    est nécessaire,
	*  - values: Un tableau de chaînes de caractères correspondant à la liste 
	*    des valeurs,
	*  - keyValueSeparator: Un caractère correspondant au séparateur des 
	*    valeurs dans la liste values,
	*  - keySize: Le nombre de colonnes dans la clé,
	*  - isAloneInFKey: Un booléen indiquant si la colonne est seule présente 
	*    dans la clé étrangère,
	*  - isEditable: Un booléen indiquant si la zone de saisie doit être 
	*    éditable,
	*  - isInKey: Un booléen indiquant si la colonne appartient à la clé 
	*    primaire de la table,
	*  - isInNotNull: Un booleén indiquant si la colonne appartient à la liste 
	*    des colonnes non nulles de la table.
	* ----------------------------------------------------------*/
	public DialogObject(
		char columnType,
		String[] values,
		String keyValueSeparator,
		int keySize,
		boolean isAloneInFKey,
		boolean isEditable,
		boolean isInKey,
		boolean isInNotNull
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "DialogObject");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnType=" + columnType);
		trace_arguments.writeTrace("values=" + values);
		trace_arguments.writeTrace("keyValueSeparator=" + keyValueSeparator);
		trace_arguments.writeTrace("isAloneInFKey=" + isAloneInFKey);
		trace_arguments.writeTrace("keySize=" + keySize);
		trace_arguments.writeTrace("isEditable=" + isEditable);
		trace_arguments.writeTrace("isInKey=" + isInKey);
		trace_arguments.writeTrace("isInNotNull=" + isInNotNull);
		// Initialisation des valeurs
		_keyValueSeparator = keyValueSeparator;
		_representsBoolean = false;
		_editionComponent = null;
		_keyFields = new DialogObject[keySize];
		_checkers = new Vector();
		// On commence par créer l'objet d'édition
		createEditionComponent(columnType, isEditable, values, isAloneInFKey);
		// On va ensuite créer les vérificateurs de valeurs (uniquement dans
		// le cas où une seule colonne constitue la clé étrangère)
		if(isAloneInFKey == true) {
			createCheckers(columnType, isInKey, isInNotNull);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getEditionComponent
	* 
	* Description:
	* Cette méthode permet de récupérer une référence sur le composant chargé 
	* de la saisie pour la colonne.
	* 
	* Retourne: Une référence sur un objet JComponent.
	* ----------------------------------------------------------*/
	public JComponent getEditionComponent() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "getEditionComponent");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _editionComponent;
	}

	/*----------------------------------------------------------
	* Nom: setValue
	* 
	* Description:
	* Cette méthode permet de définir la valeur de la colonne. Cette valeur 
	* est associée au composant graphique chargé de la saisie.
	* 
	* Arguments:
	*  - value: La valeur de la colonne,
	*  - addIfMissing: Un booléen indiquant si la valeur doit être ajoutée si 
	*    absente (true) ou non.
 	* ----------------------------------------------------------*/
	public void setValue(
		String value,
		boolean addIfMissing
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "setValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("addIfMissing=" + addIfMissing);
		// La manière de positionner la valeur dépend du type de champ
		if(_editionComponent instanceof JTextField) {
			trace_debug.writeTrace("Le composant est un JTextField");
			// Le positionnement se fait via setText
			((JTextField)_editionComponent).setText(value);
		}
		else {
			JComboBox combo_box =(JComboBox)_editionComponent; 
			if(_representsBoolean == true) {
				trace_debug.writeTrace("Le composant représente un booléen");
				if(value != null && (value.equals("0") == true || 
					value.equalsIgnoreCase("FALSE") == true ||
					value.equalsIgnoreCase("FAUX") == true ||
					value.equalsIgnoreCase("N") == true)) {
					combo_box.setSelectedIndex(1);
				}
				else if(value != null && (value.equals("1") == true || 
					value.equalsIgnoreCase("TRUE") == true ||
					value.equalsIgnoreCase("VRAI") == true ||
					value.equalsIgnoreCase("Y") == true ||
					value.equalsIgnoreCase("O") == true)) {
					combo_box.setSelectedIndex(2);
				}
				else {
					combo_box.setSelectedIndex(0);
				}
			}
			else {
				trace_debug.writeTrace(
					"Le composant représente une clé étrangère");
				combo_box.setSelectedIndex(-1);
				if(value != null) {
					combo_box.setSelectedItem(value);
					// On vérifie qu'il y a une valeur sélectionnée
					if(combo_box.getSelectedIndex() == -1 &&
						addIfMissing == true) {
						// Cela signifie que la valeur n'existe plus, il
						// faut positionner la valeur dans la liste et la
						// sélectionner
						combo_box.addItem(value);
						combo_box.setSelectedItem(value);
					}
				}
				else {
					if(combo_box.getSelectedIndex() == -1 &&
						combo_box.getModel().getSize() == 1) {
						combo_box.setSelectedIndex(0);
					}
				}
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getValue
	* 
	* Description:
	* Cette méthode permet de récupérer la valeur saisie dans la zone 
	* correspondante.
	* 
	* Retourne: La valeur de la zone.
	* ----------------------------------------------------------*/
	public String getValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "getValue");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = "";
		
		trace_methods.beginningOfMethod();
		// La façon de récupérer la valeur dépend du type de champ
		if(_editionComponent instanceof JComboBox) {
			trace_debug.writeTrace("Le composant est une JComboBox");
			if(_representsBoolean == true) {
				trace_debug.writeTrace("Le composant représente un booléen");
				int selected_index = 
					((JComboBox)_editionComponent).getSelectedIndex();
				switch(selected_index) {
				case 1:
					value = "0";
					break;
				case 2:
					value = "1";
					break;
				default:
					value = "";
					break;
				}
			}
			else {
				trace_debug.writeTrace(
					"Le composant représente une clé étrangère");
				JComboBox combo_box = ((JComboBox)_editionComponent);
				if(combo_box.getSelectedItem() != null) {
					value = combo_box.getSelectedItem().toString();
				}
			}
			
		}
		else {
			trace_debug.writeTrace("Le composant est un JTextField");
			value = ((JTextField)_editionComponent).getText();
		}
		trace_debug.writeTrace("value=" + value);
		trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: checkValue
	* 
	* Description:
	* Cette méthode permet d'effectuer les vérifications sur la valeur saisie.
	* La vérification consiste à enclencher la méthode checkValueIsCorrect() 
	* sur l'ensemble des objets de la liste _checkers.
	* Cette liste a été construite par le biais de la méthode createCheckers(), 
	* en fonction du type de données et des contraintes associées.
	* 
	* Retourne: true si la valeur saisie est correcte, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "checkValue");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = null;
		
		trace_methods.beginningOfMethod();
		// On va commencer par regarder s'il y a des vérificateurs
		if(_checkers == null || _checkers.size() == 0) {
			// Il n'y a pas de vérificateurs, on peut sortir
			trace_debug.writeTrace("Il n'y a pas de vérificateurs");
			trace_methods.endOfMethod();
			return true;
		}
		// On va récupérer la valeur
		value = getValue();
		// On va exécuter les vérificateurs un par un pour s'assurer que la
		// valeur est bonne
		for(int index = 0 ; index < _checkers.size() ; index ++) {
			trace_debug.writeTrace("Exécution du vérificateur n°" + index);
			// On exécute le test de validité de la valeur
			ValueCheckInterface checker = 
				(ValueCheckInterface)_checkers.elementAt(index);
			if(checker.checkValueIsCorrect(value) == false) {
				trace_debug.writeTrace("Le vérificateur a retourné faux");
				// Le contrôle a échoué, on peut sortir
				trace_methods.endOfMethod();
				return false;
			}
			trace_debug.writeTrace("Le vérificateur a retourné vrai");
			// La vérification est bonne, on passe à la suivante
		}
		// Si on arrive ici, c'est que toutes les vérifications ont été bonnes
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: addKeyField
	* 
	* Description:
	* Cette méthode permet d'ajouter, au niveau d'un objet de dialogue, un 
	* object enfant qui devra être mis à jour en fonction des sélections dans 
	* l'objet courant.
	* Ce n'est valable que dans le cadre d'un objet de dialogue gérant une 
	* liste de valeurs composées.
	* 
	* Arguments:
	*  - keyField: Une référence sur un objet DialogObject à ajouter en tant 
	*    qu'objet enfant,
	*  - fieldIndex: L'indice de la colonne dans la clé étrangère.
 	* ----------------------------------------------------------*/
	public void addKeyField(
		DialogObject keyField,
		int fieldIndex
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "addKeyField");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("keyField=" + keyField);
		trace_arguments.writeTrace("fieldIndex=" + fieldIndex);
		if(keyField == null) {
			// L'argument est null, on sort (il n'y a rien à ajouter)
			trace_methods.endOfMethod();
			return;
		}
		// Si l'indice est hors limites, on sort
		if(fieldIndex < 0 || fieldIndex >= _keyFields.length) {
			trace_methods.endOfMethod();
			return;
		}
		// Ajout de l'objet à l'indice indiqué
		_keyFields[fieldIndex] = keyField;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getKeyValuesSeparator
	* 
	* Description:
	* Cette méthode permet de récupérer le séparateur des champs de la valeur 
	* de la clé, dans le cas d'un objet représentant une clé étrangère.
	* 
	* Retourne: Le séparateur des champs de la valeur de la clé.
 	*----------------------------------------------------------*/
	public String getKeyValuesSepatator() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "getKeyValuesSepatator");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _keyValueSeparator;
	}

	// ****************** PROTEGE *********************
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
			"DialogObject", "finalize");

		trace_methods.beginningOfMethod();
		// On va tout libérer
		_editionComponent = null;
		_keyFields = null;
		_checkers.clear();
		_checkers = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _keyValueSeparator
	* 
	* Description:
	* Cet attribut maintient le caractère de séparation des valeurs utilisées 
	* lors de la création de l'objet représentant une clé étrangère composée.
	* ----------------------------------------------------------*/
	private String _keyValueSeparator;

	/*----------------------------------------------------------
	* Nom: _representsBoolean
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si l'objet courant 
	* représente une valeur booléenne, ou non.
	* ----------------------------------------------------------*/
	private boolean _representsBoolean;

	/*----------------------------------------------------------
	* Nom: _editionComponent
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JComponent 
	* correspondant au composant graphique chargé de la saisie pour la colonne.
	* ----------------------------------------------------------*/
	private JComponent _editionComponent;

	/*----------------------------------------------------------
	* Nom: _keyFields
	* 
	* Description:
	* Cet attribut maintient une référence sur un tableau d'objets enfants 
	* associés à l'objet courant. Ce tableau n'est valorisé que via la méthode 
	* addKeyField() dans le cas d'un objet de dialogue gérant un ensemble de 
	* valeurs composées.
	* ----------------------------------------------------------*/
	private DialogObject[] _keyFields;

	/*----------------------------------------------------------
	* Nom: _checkers
	* 
	* Description:
	* Cet attribut statique maintient une liste sur des objets 
	* ValueCheckInterface chargés de réaliser les vérifications des 
	* contraintes sur la valeur saisie.
	* Cette liste est construite par la méthode createCherckers().
	* ----------------------------------------------------------*/
	private Vector _checkers;

	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Constructeur par défaut. Il ne doit pas être utilisé.
	* ----------------------------------------------------------*/
	private DialogObject() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "DialogObject");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: createEditionComponent
	* 
	* Description:
	* Cette méthode est chargée de la création de la zone de saisie de la 
	* valeur pour le type de colonne passé en argument.
	* Si une liste de valeur est passée en deuxième argument, même vide, 
	* l'objet graphique associé à la colonne sera un objet JComboBox (liste 
	* déroulante).
	* Sinon, un objet JComboBox sera également créé si la colonne est de type 
	* booléen ('b'). Dans tous les autres cas, un objet JTextField (zone de 
	* texte) sera créé.
	* 
	* Arguments:
	*  - columnType: Un caractère correspondant au type de la colonne,
	*  - isEditable: Un booléen indiquant si la zone de saisie doit être 
	*    éditable,
	*  - values: Un tableau de chaîne de caractères correspondant 
	*    éventuellement à une liste de valeurs,
	*  - isAloneInFKey: Un booléen indiquant si la colonne est seule présente 
	*    dans la clé étrangère,
 	* ----------------------------------------------------------*/
	private void createEditionComponent(
		char columnType,
		boolean isEditable,
		String[] values,
		boolean isAloneInFKey
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "createEditionComponent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnType=" + columnType);
		trace_arguments.writeTrace("isEditable=" + isEditable);
		trace_arguments.writeTrace("values=" + values);
		trace_arguments.writeTrace("isAloneInFKey=" + isAloneInFKey);
		// On va regarder s'il y a une liste de valeur passée en argument
		if(values != null) {
			JComboBox combo_box = new JComboBox(values); 
			combo_box.setEditable(false);
			if(isAloneInFKey == false) {
				// On va ajouter un callback sur la sélection de valeur
				combo_box.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						keyValueSelected();
					}
				});
			}
			_editionComponent = combo_box;
		}
		else if(columnType == 'b') {
			// On va construire une liste déroulante
			values = new String[3];
			values[0] = "";
			values[1] = MessageManager.getMessage("&Admin_FalseValue");
			values[2] = MessageManager.getMessage("&Admin_TrueValue");
			JComboBox combo_box = new JComboBox(values); 
			combo_box.setEditable(false);
			_editionComponent = combo_box;
			_representsBoolean = true;
		}
		else {
			// Dans les autres cas, on construit une zone de texte
			_editionComponent = new JTextField(30);
		}
		// Si le composant doit être non-éditable, on va le désactiver
		_editionComponent.setEnabled(isEditable);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: createCheckers
	* 
	* Description:
	* Cette méthode est chargée d'instancier les vérificateurs qui 
	* constitueront la liste _checkers. Ces vérificateurs seront chargés de 
	* contrôler que la valeur saisie correspond à l'ensemble des contraintes 
	* associées à la colonne.
	* La construction des objets de vérification est déléguée à la classe 
	* CheckerFactory.
	* 
	* Arguments:
	*  - columnType: Le type de la colonne, déterminant le vérificateur de 
	*    type à créer,
	*  - isInKey: Un booléen indiquant si la colonne appartient à la clé 
	*    primaire,
	*  - isInNotNull: Un booléen indiquant si la colonnne appartient à la 
	*    liste des colonnes non nulles.
 	* ----------------------------------------------------------*/
	private void createCheckers(
		char columnType,
		boolean isInKey,
		boolean isInNotNull
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "createCheckers");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnType=" + columnType);
		trace_arguments.writeTrace("isInKey=" + isInKey);
		trace_arguments.writeTrace("isInNotNull=" + isInNotNull);
		// On va commencer par vérifier que le vecteur existe
		if(_checkers == null) {
			_checkers = new Vector();
		}
		// On va commencer éventuellement par le vérificateur de non-nullité
		if(isInKey == true || isInNotNull == true) {
			_checkers.add(CheckerFactory.createChecker('!'));
		}
		// Ensuite, on ajoute le vérificateur pour le type de colonne
		_checkers.add(CheckerFactory.createChecker(columnType));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: keyValueSelected
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a sélectionné une valeur 
	* dans la liste déroulante correspondant à la valeur issue d'une clé 
	* étrangère composée.
	* La méthode gère la diffusion des données dans les différents champs 
	* associés, via la liste _keyFields.
 	* ----------------------------------------------------------*/
	private void keyValueSelected() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "createCheckers");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int selected_index = -1;
		String selected_value = null;

		trace_methods.beginningOfMethod();
		// S'il n'y aucun champ à mettre à jour, on peut sortir tout de suite
		if(_keyFields.length == 0 || _keyValueSeparator == null) {
			trace_methods.endOfMethod();
			return;
		}
		// On commence par récupérer la valeur sélectionnée
		selected_index = ((JComboBox)_editionComponent).getSelectedIndex();
		trace_debug.writeTrace("selected_index=" + selected_index);
		if(selected_index < 0) {
			// Aucune valeur n'est sélectionnée, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère la valeur sélectionnée
		selected_value = getValue();
		// On va découper la valeur sélectionnée en champs
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(selected_value,
			_keyValueSeparator);
		trace_debug.writeTrace("_keyFields.length=" + _keyFields.length);
		trace_debug.writeTrace("tokenizer.getTokensCount=" + tokenizer.getTokensCount());
		if(tokenizer.getTokensCount() != _keyFields.length) {
			trace_errors.writeTrace("Nombre de valeurs incohérent !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va remplir les champs un par un
		for(int index = 0 ; index < tokenizer.getTokensCount() ; index ++) {
			DialogObject key_object = (DialogObject)_keyFields[index];
			if(key_object == null) {
				continue;
			}
			trace_debug.writeTrace("Mise à jour du champ " + index + ": " + 
				tokenizer.getToken(index));
			key_object.setValue(tokenizer.getToken(index), false);
		}
		trace_methods.endOfMethod();
	}
}
