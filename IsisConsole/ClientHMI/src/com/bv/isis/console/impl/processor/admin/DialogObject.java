/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
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
* Prise en compte du cas o� la cl� �trang�re r�f�rence un objet null.
*
* Revision 1.5  2008/06/12 15:48:43  tz
* S�lection automatique de la valeur si la liste d�roulante ne contient
* qu'une seule valeur.
*
* Revision 1.4  2008/02/13 16:25:42  tz
* Gestion des valeurs absentes sur liste d�roulante.
* Gestion des cas de valeurs nulles.
*
* Revision 1.3  2008/01/31 16:45:10  tz
* Utilisation d'un tableau de DialogObject au lieu d'un vecteur pour
* l'attribut _keyFields.
*
* Revision 1.2  2007/12/28 17:40:01  tz
* Ajout de la valeur par d�faut pour un bool�en nul.
*
* Revision 1.1  2007/12/07 10:31:52  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.admin;

//
//Imports syst�me
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
* Cette classe repr�sente un �l�ment de la bo�te de dialogue permettant 
* l'administration d'une table. Cet �l�ment est constitu� d'un composant 
* graphique, charg� du rendu de la valeur de la colonne ainsi que sa saisie, 
* et d'un ensemble de v�rificateurs, permettant de v�rifier si la valeur 
* saisie dans le composant correspond � ce qui est attendu.
* ----------------------------------------------------------*/
public class DialogObject {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Il s'agit d'un des constructeurs de la classe. Il prend en argument le 
	* type de la colonne, ainsi que des bool�ens permettant de d�finir un 
	* certain nombre de caract�ristiques qui seront associ�es � l'objet.
	* L'objet graphique est construit, via la m�thode 
	* createEditionComponent(), puis les v�rificateurs sont cr��s via la 
	* m�thode createCheckers().
	* 
	* Arguments:
	*  - columnType: Le type de la colonne pour laquelle une zone de saisie 
	*    est n�cessaire,
	*  - isEditable: Un bool�en indiquant si la zone de saisie doit �tre 
	*    �ditable,
	*  - isInKey: Un bool�en indiquant si la colonne appartient � la cl� 
	*    primaire de la table,
	*  - isInNotNull: Un boole�n indiquant si la colonne appartient � la liste 
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
		// On commence par cr�er l'objet d'�dition
		createEditionComponent(columnType, isEditable, null, false);
		// On va ensuite cr�er les v�rificateurs de valeurs
		createCheckers(columnType, isInKey, isInNotNull);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Il s'agit d'un des constructeurs de la classe. Il prend en argument le 
	* type de la colonne, une liste de valeurs, ainsi que des bool�ens 
	* permettant de d�finir un certain nombre de caract�ristiques qui seront 
	* associ�es � l'objet.
	* L'objet graphique est construit, via la m�thode 
	* createEditionComponent(), puis les v�rificateurs sont cr��s via la 
	* m�thode createCheckers().
	* 
	* Arguments:
	*  - columnType: Le type de la colonne pour laquelle une zone de saisie 
	*    est n�cessaire,
	*  - values: Un tableau de cha�nes de caract�res correspondant � la liste 
	*    des valeurs,
	*  - keyValueSeparator: Un caract�re correspondant au s�parateur des 
	*    valeurs dans la liste values,
	*  - keySize: Le nombre de colonnes dans la cl�,
	*  - isAloneInFKey: Un bool�en indiquant si la colonne est seule pr�sente 
	*    dans la cl� �trang�re,
	*  - isEditable: Un bool�en indiquant si la zone de saisie doit �tre 
	*    �ditable,
	*  - isInKey: Un bool�en indiquant si la colonne appartient � la cl� 
	*    primaire de la table,
	*  - isInNotNull: Un boole�n indiquant si la colonne appartient � la liste 
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
		// On commence par cr�er l'objet d'�dition
		createEditionComponent(columnType, isEditable, values, isAloneInFKey);
		// On va ensuite cr�er les v�rificateurs de valeurs (uniquement dans
		// le cas o� une seule colonne constitue la cl� �trang�re)
		if(isAloneInFKey == true) {
			createCheckers(columnType, isInKey, isInNotNull);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getEditionComponent
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une r�f�rence sur le composant charg� 
	* de la saisie pour la colonne.
	* 
	* Retourne: Une r�f�rence sur un objet JComponent.
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
	* Cette m�thode permet de d�finir la valeur de la colonne. Cette valeur 
	* est associ�e au composant graphique charg� de la saisie.
	* 
	* Arguments:
	*  - value: La valeur de la colonne,
	*  - addIfMissing: Un bool�en indiquant si la valeur doit �tre ajout�e si 
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
		// La mani�re de positionner la valeur d�pend du type de champ
		if(_editionComponent instanceof JTextField) {
			trace_debug.writeTrace("Le composant est un JTextField");
			// Le positionnement se fait via setText
			((JTextField)_editionComponent).setText(value);
		}
		else {
			JComboBox combo_box =(JComboBox)_editionComponent; 
			if(_representsBoolean == true) {
				trace_debug.writeTrace("Le composant repr�sente un bool�en");
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
					"Le composant repr�sente une cl� �trang�re");
				combo_box.setSelectedIndex(-1);
				if(value != null) {
					combo_box.setSelectedItem(value);
					// On v�rifie qu'il y a une valeur s�lectionn�e
					if(combo_box.getSelectedIndex() == -1 &&
						addIfMissing == true) {
						// Cela signifie que la valeur n'existe plus, il
						// faut positionner la valeur dans la liste et la
						// s�lectionner
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
	* Cette m�thode permet de r�cup�rer la valeur saisie dans la zone 
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
		// La fa�on de r�cup�rer la valeur d�pend du type de champ
		if(_editionComponent instanceof JComboBox) {
			trace_debug.writeTrace("Le composant est une JComboBox");
			if(_representsBoolean == true) {
				trace_debug.writeTrace("Le composant repr�sente un bool�en");
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
					"Le composant repr�sente une cl� �trang�re");
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
	* Cette m�thode permet d'effectuer les v�rifications sur la valeur saisie.
	* La v�rification consiste � enclencher la m�thode checkValueIsCorrect() 
	* sur l'ensemble des objets de la liste _checkers.
	* Cette liste a �t� construite par le biais de la m�thode createCheckers(), 
	* en fonction du type de donn�es et des contraintes associ�es.
	* 
	* Retourne: true si la valeur saisie est correcte, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "checkValue");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = null;
		
		trace_methods.beginningOfMethod();
		// On va commencer par regarder s'il y a des v�rificateurs
		if(_checkers == null || _checkers.size() == 0) {
			// Il n'y a pas de v�rificateurs, on peut sortir
			trace_debug.writeTrace("Il n'y a pas de v�rificateurs");
			trace_methods.endOfMethod();
			return true;
		}
		// On va r�cup�rer la valeur
		value = getValue();
		// On va ex�cuter les v�rificateurs un par un pour s'assurer que la
		// valeur est bonne
		for(int index = 0 ; index < _checkers.size() ; index ++) {
			trace_debug.writeTrace("Ex�cution du v�rificateur n�" + index);
			// On ex�cute le test de validit� de la valeur
			ValueCheckInterface checker = 
				(ValueCheckInterface)_checkers.elementAt(index);
			if(checker.checkValueIsCorrect(value) == false) {
				trace_debug.writeTrace("Le v�rificateur a retourn� faux");
				// Le contr�le a �chou�, on peut sortir
				trace_methods.endOfMethod();
				return false;
			}
			trace_debug.writeTrace("Le v�rificateur a retourn� vrai");
			// La v�rification est bonne, on passe � la suivante
		}
		// Si on arrive ici, c'est que toutes les v�rifications ont �t� bonnes
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: addKeyField
	* 
	* Description:
	* Cette m�thode permet d'ajouter, au niveau d'un objet de dialogue, un 
	* object enfant qui devra �tre mis � jour en fonction des s�lections dans 
	* l'objet courant.
	* Ce n'est valable que dans le cadre d'un objet de dialogue g�rant une 
	* liste de valeurs compos�es.
	* 
	* Arguments:
	*  - keyField: Une r�f�rence sur un objet DialogObject � ajouter en tant 
	*    qu'objet enfant,
	*  - fieldIndex: L'indice de la colonne dans la cl� �trang�re.
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
			// L'argument est null, on sort (il n'y a rien � ajouter)
			trace_methods.endOfMethod();
			return;
		}
		// Si l'indice est hors limites, on sort
		if(fieldIndex < 0 || fieldIndex >= _keyFields.length) {
			trace_methods.endOfMethod();
			return;
		}
		// Ajout de l'objet � l'indice indiqu�
		_keyFields[fieldIndex] = keyField;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getKeyValuesSeparator
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le s�parateur des champs de la valeur 
	* de la cl�, dans le cas d'un objet repr�sentant une cl� �trang�re.
	* 
	* Retourne: Le s�parateur des champs de la valeur de la cl�.
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
	* Cette m�thode est appel�e automatiquement par le ramasse miettes de Java 
	* lorsque un objet est sur le point d'�tre d�truit. Elle permet de lib�rer 
	* les ressources.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "finalize");

		trace_methods.beginningOfMethod();
		// On va tout lib�rer
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
	* Cet attribut maintient le caract�re de s�paration des valeurs utilis�es 
	* lors de la cr�ation de l'objet repr�sentant une cl� �trang�re compos�e.
	* ----------------------------------------------------------*/
	private String _keyValueSeparator;

	/*----------------------------------------------------------
	* Nom: _representsBoolean
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si l'objet courant 
	* repr�sente une valeur bool�enne, ou non.
	* ----------------------------------------------------------*/
	private boolean _representsBoolean;

	/*----------------------------------------------------------
	* Nom: _editionComponent
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JComponent 
	* correspondant au composant graphique charg� de la saisie pour la colonne.
	* ----------------------------------------------------------*/
	private JComponent _editionComponent;

	/*----------------------------------------------------------
	* Nom: _keyFields
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un tableau d'objets enfants 
	* associ�s � l'objet courant. Ce tableau n'est valoris� que via la m�thode 
	* addKeyField() dans le cas d'un objet de dialogue g�rant un ensemble de 
	* valeurs compos�es.
	* ----------------------------------------------------------*/
	private DialogObject[] _keyFields;

	/*----------------------------------------------------------
	* Nom: _checkers
	* 
	* Description:
	* Cet attribut statique maintient une liste sur des objets 
	* ValueCheckInterface charg�s de r�aliser les v�rifications des 
	* contraintes sur la valeur saisie.
	* Cette liste est construite par la m�thode createCherckers().
	* ----------------------------------------------------------*/
	private Vector _checkers;

	/*----------------------------------------------------------
	* Nom: DialogObject
	* 
	* Description:
	* Constructeur par d�faut. Il ne doit pas �tre utilis�.
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
	* Cette m�thode est charg�e de la cr�ation de la zone de saisie de la 
	* valeur pour le type de colonne pass� en argument.
	* Si une liste de valeur est pass�e en deuxi�me argument, m�me vide, 
	* l'objet graphique associ� � la colonne sera un objet JComboBox (liste 
	* d�roulante).
	* Sinon, un objet JComboBox sera �galement cr�� si la colonne est de type 
	* bool�en ('b'). Dans tous les autres cas, un objet JTextField (zone de 
	* texte) sera cr��.
	* 
	* Arguments:
	*  - columnType: Un caract�re correspondant au type de la colonne,
	*  - isEditable: Un bool�en indiquant si la zone de saisie doit �tre 
	*    �ditable,
	*  - values: Un tableau de cha�ne de caract�res correspondant 
	*    �ventuellement � une liste de valeurs,
	*  - isAloneInFKey: Un bool�en indiquant si la colonne est seule pr�sente 
	*    dans la cl� �trang�re,
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
		// On va regarder s'il y a une liste de valeur pass�e en argument
		if(values != null) {
			JComboBox combo_box = new JComboBox(values); 
			combo_box.setEditable(false);
			if(isAloneInFKey == false) {
				// On va ajouter un callback sur la s�lection de valeur
				combo_box.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						keyValueSelected();
					}
				});
			}
			_editionComponent = combo_box;
		}
		else if(columnType == 'b') {
			// On va construire une liste d�roulante
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
		// Si le composant doit �tre non-�ditable, on va le d�sactiver
		_editionComponent.setEnabled(isEditable);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: createCheckers
	* 
	* Description:
	* Cette m�thode est charg�e d'instancier les v�rificateurs qui 
	* constitueront la liste _checkers. Ces v�rificateurs seront charg�s de 
	* contr�ler que la valeur saisie correspond � l'ensemble des contraintes 
	* associ�es � la colonne.
	* La construction des objets de v�rification est d�l�gu�e � la classe 
	* CheckerFactory.
	* 
	* Arguments:
	*  - columnType: Le type de la colonne, d�terminant le v�rificateur de 
	*    type � cr�er,
	*  - isInKey: Un bool�en indiquant si la colonne appartient � la cl� 
	*    primaire,
	*  - isInNotNull: Un bool�en indiquant si la colonnne appartient � la 
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
		// On va commencer par v�rifier que le vecteur existe
		if(_checkers == null) {
			_checkers = new Vector();
		}
		// On va commencer �ventuellement par le v�rificateur de non-nullit�
		if(isInKey == true || isInNotNull == true) {
			_checkers.add(CheckerFactory.createChecker('!'));
		}
		// Ensuite, on ajoute le v�rificateur pour le type de colonne
		_checkers.add(CheckerFactory.createChecker(columnType));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: keyValueSelected
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a s�lectionn� une valeur 
	* dans la liste d�roulante correspondant � la valeur issue d'une cl� 
	* �trang�re compos�e.
	* La m�thode g�re la diffusion des donn�es dans les diff�rents champs 
	* associ�s, via la liste _keyFields.
 	* ----------------------------------------------------------*/
	private void keyValueSelected() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogObject", "createCheckers");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int selected_index = -1;
		String selected_value = null;

		trace_methods.beginningOfMethod();
		// S'il n'y aucun champ � mettre � jour, on peut sortir tout de suite
		if(_keyFields.length == 0 || _keyValueSeparator == null) {
			trace_methods.endOfMethod();
			return;
		}
		// On commence par r�cup�rer la valeur s�lectionn�e
		selected_index = ((JComboBox)_editionComponent).getSelectedIndex();
		trace_debug.writeTrace("selected_index=" + selected_index);
		if(selected_index < 0) {
			// Aucune valeur n'est s�lectionn�e, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re la valeur s�lectionn�e
		selected_value = getValue();
		// On va d�couper la valeur s�lectionn�e en champs
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(selected_value,
			_keyValueSeparator);
		trace_debug.writeTrace("_keyFields.length=" + _keyFields.length);
		trace_debug.writeTrace("tokenizer.getTokensCount=" + tokenizer.getTokensCount());
		if(tokenizer.getTokensCount() != _keyFields.length) {
			trace_errors.writeTrace("Nombre de valeurs incoh�rent !");
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
			trace_debug.writeTrace("Mise � jour du champ " + index + ": " + 
				tokenizer.getToken(index));
			key_object.setValue(tokenizer.getToken(index), false);
		}
		trace_methods.endOfMethod();
	}
}
