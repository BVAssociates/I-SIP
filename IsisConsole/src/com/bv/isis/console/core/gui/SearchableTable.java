/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SearchableTable.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de table "recherchable"
* DATE:        17/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SearchableTable.java,v $
* Revision 1.5  2009/01/14 14:18:59  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.4  2008/02/15 14:15:26  tz
* Implémentation de la méthode getFileFilters().
*
* Revision 1.3  2007/04/13 09:54:07  tz
* Séparateur CSV est le point virgule.
*
* Revision 1.2  2007/03/23 15:26:29  tz
* Le séparateur dans les fichiers CSV est la virgule.
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
// Imports système
//
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import java.util.Vector;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.abs.processor.SearchableComponentInterface;

/*----------------------------------------------------------
* Nom: SearchableTable
* 
* Description:
* Cette classe est une spécialisation de la classe JTable afin de créer un 
* tableau dans lequel l'utilisateur peut effectuer des recherches de chaînes 
* de caractères ou sauvegarder les données dans des fichiers.
* Pour cela, elle implémente l'interface SearchableComponentInterface.
* ----------------------------------------------------------*/
public class SearchableTable
	extends JTable
	implements SearchableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: XML_FILTER
	* 
	* Description:
	* Cette valeur constante correspond au filtre des extensions pour les 
	* fichiers XML.
	* ----------------------------------------------------------*/
	public static final FileNameExtensionFilter XML_FILTER =
		new FileNameExtensionFilter(MessageManager.getMessage("&XMLFile"), 
		".xml");

	/*----------------------------------------------------------
	* Nom: CSV_FILTER
	* 
	* Description:
	* Cette valeur constante correspond au filtre des extensions pour les 
	* fichiers CSV.
	* ----------------------------------------------------------*/
	public static final FileNameExtensionFilter CSV_FILTER =
		new FileNameExtensionFilter(MessageManager.getMessage("&CSVFile"), 
		".csv");

	/*----------------------------------------------------------
	* Nom: SearchableTable
	*
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau à partir d'un tableau (à deux dimensions) de
	* valeurs et d'un tableau représentant les intitulés des colonnes.
	* Une instance de SortedTableModel est créée à partir des informations 
	* passées en arguments et est associée à la table.
	*
	* Arguments:
	*  - data: Un tableau à deux dimensions contenant les valeurs à afficher
	*    dans le tableau,
	* - columns: Un tableau à une dimension contenant les intitulés des
	*   colonnes,
	* - separator: Le séparateur de la définition de la table. 
	* ----------------------------------------------------------*/
	public SearchableTable(
		Object[][] data,
		Object[] columns,
		String separator
		)
	{
		super(new SortedTableModel(data, columns));

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "SearchableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		trace_arguments.writeTrace("columns=" + columns);
		trace_arguments.writeTrace("separator=" + separator);
		_separator = separator;
		_lastSearchPosition = 0;
		setShowGrid(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// On ajoute le listener sur l'entête
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	*
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau à partir d'un modèle de données et d'un modèle de
	* colonnes.
	* Une instance de SortedTableModel est créée à partir des informations 
	* passées en arguments et est associée à la table.
	*
	* Arguments:
	*  - dataModel: Une référence sur un objet TableModel définissant le modèle
	*    des données,
	*  - columnModel: Une référence sur un objet TableColumnModel définissant
	*    le modèle des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public SearchableTable(
		TableModel dataModel,
		TableColumnModel columnModel,
		String separator
		)
	{
		super(new SortedTableModel(dataModel), columnModel);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "SearchableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("dataModel=" + dataModel);
		trace_arguments.writeTrace("columnModel=" + columnModel);
		trace_arguments.writeTrace("separator=" + separator);
		_separator = separator;
		_lastSearchPosition = 0;
		setShowGrid(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// On ajoute le listener sur l'entête
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	* 
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau à partir d'un modèle de données.
	* Une instance de SortedTableModel est créée à partir des informations 
	* passées en arguments et est associée à la table.
	* 
	* Arguments:
	*  - dataModel: Une référence sur un objet TableModel définissant le 
	*    modèle des données et le modèle des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public SearchableTable(
		TableModel dataModel,
		String separator
		)
	{
		super(new SortedTableModel(dataModel));

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "SearchableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("dataModel=" + dataModel);
		trace_arguments.writeTrace("separator=" + separator);
		_separator = separator;
		_lastSearchPosition = 0;
		setShowGrid(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// On ajoute le listener sur l'entête
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	* 
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau à partir d'un vecteur contenant les données et 
	* d'un vecteur contenant les noms des colonnes.
	* Une instance de SortedTableModel est créée à partir des informations 
	* passées en arguments et est associée à la table.
	* 
	* Arguments:
	*  - rowData: Une référence sur un objet Vector contenant les données,
	*  - columnNames: Une référence sur un objet Vector contenant les noms 
	*    des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public SearchableTable(
		Vector rowData,
		Vector columnNames,
		String separator
		)
	{
		super(new SortedTableModel(rowData, columnNames));

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "SearchableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("rowData=" + rowData);
		trace_arguments.writeTrace("columnNames=" + columnNames);
		trace_arguments.writeTrace("separator=" + separator);
		_separator = separator;
		_lastSearchPosition = 0;
		setShowGrid(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// On ajoute le listener sur l'entête
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getSelectedRow
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe JTable. Elle permet de 
	* récupérer l'indice du premier rang sélectionné.
	* Elle est redéfinie afin d'effectuer une translation d'indice entre le 
	* modèle trié et le modèle source.
	* 
	* Retourne: -1 si aucun rang n'est sélectionné, sinon l'indice du premier 
	* rang sélectionné.
	* ----------------------------------------------------------*/
	public int getSelectedRow()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "getSelectedRow");
		int selected_row = SortedTableModel.NO_SELECTION;

		trace_methods.beginningOfMethod();
		// On récupère la valeur réellement sélectionnée
		selected_row = super.getSelectedRow();
		// S'il y a un rang sélectionné, il faut effectuer une translation
		if(selected_row != SortedTableModel.NO_SELECTION)
		{
			SortedTableModel model = (SortedTableModel)getModel();
			selected_row = model.modelIndex(selected_row); 
		}
		trace_methods.endOfMethod();
		return selected_row;
	}

	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de rechercher une chaîne de 
	* caractères dans le tableau.
	* Si une occurence peut être trouvée, la ligne contenant cette chaîne est 
	* sélectionnée, et est rendue visible. La position de la première 
	* occurence trouvée est enregistrée.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne de caractères à rechercher dans le 
	*    tableau.
	* 
	* Retourne: true si la chaîne a pu être trouvée, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchData(
		String stringToSearchFor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "searchData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String text = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		// On vérifie la validité de l'argument
		if(stringToSearchFor == null || stringToSearchFor.equals("") ==  true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// On va rechercher, ligne par ligne, colonne par colonne une
		// valeur contenant la chaîne
		for(int row = 0 ; row < getRowCount() ; row ++)
		{
			for(int col = 0 ; col < getColumnCount() ; col ++)
			{
				// On récupère la valeur sous forme de chaîne
				text = getValueAt(row, col).toString();
				// La chaîne est-elle contenue dans la valeur ?
				int pos = text.indexOf(stringToSearchFor); 
				if(pos != -1)
				{
					// La chaîne a été trouvée dans la valeur, on va
					// sélectionner la ligne correspondant
					changeSelection(row, 0, false, false);
					// On enregistre la position de la dernière recherche
					// dans le composant, et on sort
					_lastSearchPosition = row + 1;
					trace_methods.endOfMethod();
					return true;
				}
			}
		}
		// Si on arrive ici, c'est que la chaîne n'a pas été trouvée.
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de rechercher une chaîne de 
	* caractères passée en argument à partir de la position de la dernière 
	* occurence enregistrée.
	* Si une nouvelle occurence est trouvée, la ligne du tableau la contenant 
	* est sélectionné et est rendue visible.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans le tableau.
	* 
	* Retourne: true si une occurence de la chaîne peut être trouvée, 
	* false sinon.
	* ----------------------------------------------------------*/
	public boolean searchAgain(
		String stringToSearchFor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "searchAgain");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String text = null;
		boolean rewinded = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		// On vérifie la validité de l'argument
		if(stringToSearchFor == null || stringToSearchFor.equals("") ==  true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// On va rechercher ligne par ligne, colonne par colonne à
		// partir de la dernière position de recherche
		int row = _lastSearchPosition;
		while(true)
		{
			// A-t-on atteint la fin de la table ?
			if(row == getRowCount())
			{
				// C'est la fin de la table. Si on n'est pas parti du
				// début et que l'on pas recherché dans les premières lignes
				// on repart de zéro
				if(_lastSearchPosition != 0 && rewinded == false)
				{
					row = 0;
					rewinded = true;
					continue;
				}
				// La recherche a démarrée depuis la première ligne, on va
				// arrêter.
				break;
			}
			if(row == _lastSearchPosition && rewinded == true)
			{
				// La recherche a redémarré depuis le début, on va arrêter
				break;
			}
			for(int col = 0 ; col < getColumnCount() ; col ++)
			{
				// On récupère la valeur 
				text = getValueAt(row, col).toString();
				// On regarde si la chaîne est contenue dans la valeur
				int pos = text.indexOf(stringToSearchFor); 
				if(pos != -1)
				{
					// La chaîne est contenue dans la valeur, on va 
					// sélectionner la ligne correspondant
					changeSelection(row, 0, false, false);
					// On enregistre la dernière position de recherche dans le
					// composant, et on sort
					_lastSearchPosition = row + 1;
					trace_methods.endOfMethod();
					return true;
				}
			}
			// On passe à la ligne suivante
			row ++;
		}
		trace_methods.endOfMethod();
		// Si on arrive ici, c'est que l'on n'a pas trouvé
		return false;
	}

	/*----------------------------------------------------------
	* Nom: setLastSearchPosition
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de définir la dernière 
	* position de la recherche d'une chaîne de caractères dans le tableau.
	* 
	* Arguments:
	*  - lastSearchPosition: La dernière position de la recherche dans le 
	*    tableau.
 	* ----------------------------------------------------------*/
	public void setLastSearchPosition(
		int lastSearchPosition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "setLastSearchPosition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("lastSearchPosition=" + lastSearchPosition);
		_lastSearchPosition = lastSearchPosition;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLastSearchPosition
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de connaître la dernière 
	* position de la recherche d'une chaîne de caractères dans le tableau.
	* 
	* Retourne: La dernière position de la recherche dans le tableau.
	* ----------------------------------------------------------*/
	public int getLastSearchPosition()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "getLastSearchPosition");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _lastSearchPosition;
	}

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet d'enregistrer le contenu du 
	* tableau dans un fichier texte, passé en argument.
	* L'utilisateur est invité à indiquer si l'entête du tableau doit 
	* également être sauvegardé ou non.
	* 
	* Arguments:
	*  - file: Une référence sur un objet File correspondant au fichier dans 
	*    lequel enregistrer les données,
	*  - mainWindowInterface: Une référence sur une interface 
	*    MainWindowInterface correspondant à l'interface principale.
 	* ----------------------------------------------------------*/
 	public void saveDataToFile(
 		File file,
 		MainWindowInterface mainWindowInterface
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "saveDataToFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_csv_file = false;
		boolean is_xml_file = false;
		File xsd_file = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("file=" + file);
		trace_arguments.writeTrace("mainWindowInterface=" + 
			mainWindowInterface);
		// On vérifie la validité des arguments
		if(file == null || mainWindowInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return;
		}
		String absolute_path = file.getAbsolutePath();
		// On va regarder quelle extension est associée au fichier
		if(CSV_FILTER.accept(file) == true) {
			is_csv_file = true;
		}
		else if(XML_FILTER.accept(file) == true) {
			is_xml_file = true;
			String xsd_file_name = absolute_path.substring(0, 
				absolute_path.length() - 2) + "sd";
			xsd_file = new File(xsd_file_name);
		}
		try
		{
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(file, false));
			StringBuffer text_buffer = new StringBuffer("");
			if(is_xml_file == false)
			{
				// On va demander à l'utilisateur s'il veut également enregistrer
				// les entêtes des colonnes
				int save_headers = JOptionPane.showConfirmDialog(
					(Component)mainWindowInterface, 
					MessageManager.getMessage("&SaveHeadersToo"),
					MessageManager.getMessage("&SaveData"), 
					JOptionPane.YES_NO_OPTION);
				if(save_headers == JOptionPane.YES_OPTION)
				{
					for(int col = 0 ; col < getColumnCount() ; col ++)
					{
						if(col != 0)
						{
							if(is_csv_file == true)
							{
								text_buffer.append(';');
							}
							else
							{
								text_buffer.append(_separator);
							}
						}
						// On récupère l'entête de la colonne
						text_buffer.append(
							getTableHeader().getColumnModel().getColumn(
							col).getHeaderValue().toString());
					}
					writer.write(text_buffer.toString());
					writer.newLine();
				}
			}
			else
			{
				// Ecriture de l'entête XML
				writer.write("<?xml version=\"1.0\" " +
					"encoding=\"iso-8859-1\"?>");
				writer.newLine();
				writer.write("<Table " +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
					"xsi:noNamespaceSchemaLocation=\"" +
					xsd_file.toURL() + "\">");
				writer.newLine();
			}
			// On va récupérer, ligne par ligne, colonne par colonne, la
			// valeur des cellules
			for(int row = 0 ; row < getRowCount() ; row ++)
			{
				if(is_xml_file == true)
				{
					writer.write("\t<Row>");
					writer.newLine();
				}
				text_buffer = new StringBuffer("");
				for(int col = 0 ; col < getColumnCount() ; col ++)
				{
					if(is_xml_file == false)
					{
						if(col != 0)
						{
							if(is_csv_file == true)
							{
								text_buffer.append(';');
							}
							else
							{
								text_buffer.append(_separator);
							}
						}
						// On récupère la valeur sous forme de chaîne
						if(is_csv_file == true)
						{
							text_buffer.append('"');
						}
						text_buffer.append(getValueAt(row, col).toString());
						if(is_csv_file == true)
						{
							text_buffer.append('"');
						}
					}
					else
					{
						text_buffer.append("\t\t<");
						text_buffer.append(getTableHeader().getColumnModel(
								).getColumn(col).getHeaderValue().toString());
						text_buffer.append(">");
						text_buffer.append(getValueAt(row, col).toString());
						text_buffer.append("</");
						text_buffer.append(getTableHeader().getColumnModel(
								).getColumn(col).getHeaderValue().toString());
						text_buffer.append(">");
						writer.write(text_buffer.toString());
						writer.newLine();
						text_buffer = new StringBuffer("");
					}
				}
				if(is_xml_file == false)
				{
					writer.write(text_buffer.toString());
					writer.newLine();
				}
				else
				{
					writer.write("\t</Row>");
					writer.newLine();
				}
			}
			if(is_xml_file == true)
			{
				writer.write("</Table>");
				writer.newLine();
			}
			writer.close();
			if(is_xml_file == true)
			{
				// On va générer le fichier XSD
				writer = new BufferedWriter(
					new FileWriter(xsd_file, false));
				writer.write(
					"<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
				writer.newLine();
				writer.write("<xs:schema " +
					"xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">");
				writer.newLine();
				writer.write("\t<xs:element name=\"Table\">");
				writer.newLine();
				writer.write("\t\t<xs:complexType>");
				writer.newLine();
				writer.write("\t\t\t<xs:sequence>");
				writer.newLine();
				writer.write("\t\t\t\t<xs:element name=\"Row\">");
				writer.newLine();
				writer.write("\t\t\t\t\t<xs:complexType>");
				writer.newLine();
				writer.write("\t\t\t\t\t\t<xs:sequence>");
				writer.newLine();
				for(int col = 0 ; col < getColumnCount() ; col ++)
				{
					writer.write("\t\t\t\t\t\t\t<xs:element name=\"" +
						getTableHeader().getColumnModel().getColumn(
						col).getHeaderValue().toString() +
						"\" type=\"xs:string\" />");
					writer.newLine();
				}
				writer.write("\t\t\t\t\t\t</xs:sequence>");
				writer.newLine();
				writer.write("\t\t\t\t\t</xs:complexType>");
				writer.newLine();
				writer.write("\t\t\t\t</xs:element>");
				writer.newLine();
				writer.write("\t\t\t</xs:sequence>");
				writer.newLine();
				writer.write("\t\t</xs:complexType>");
				writer.newLine();
				writer.write("\t</xs:element>");
				writer.newLine();
				writer.write("</xs:schema>");
				writer.newLine();
				writer.close();
			}
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de l'enregistrement dans " +
				"le fichier: " + e);
			// On va afficher un message à l'utilisateur
			InnerException exception = new InnerException(
				e.getMessage(), null, null);
			mainWindowInterface.showPopupForException(
				MessageManager.getMessage("&ERR_CannotSaveToFile"),
				exception);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getFileFilters
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle est appelée afin de récupérer la 
	* liste des extensions de fichier préférées, autres que "toutes", sous 
	* la forme d'un tableau de FileFilter.
	* 
	* Dans le cas d'un tableau, les extensions préférées sont : xml ou csv.
	* 
	* Retourne: La liste des extensions préférées sous forme de tableau de 
	* FileFilter.
	* ----------------------------------------------------------*/
 	public FileFilter[] getFileFilters() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "getFileFilters");
		FileFilter[] filters = {
			CSV_FILTER,
			XML_FILTER
		};
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return filters;
 	}

	/*----------------------------------------------------------
	* Nom: getSeparator
	* 
	* Description:
	* Cette méthode permet de récupérer la valeur du séparateur de la 
	* définition de la table.
	* 
	* Retourne: Le séparateur de la définition de la table.
	* ----------------------------------------------------------*/
	public String getSeparator()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "getSeparator");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _separator;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _lastSearchPosition
	* 
	* Description:
	* Cet attribut permet de stocker la position de la dernière recherche 
	* d'une chaîne dans le tableau (voir les méthodes setLastSearchPosition() 
	* et getLastSearchPosition()).
	* ----------------------------------------------------------*/
	private int _lastSearchPosition;

	/*----------------------------------------------------------
	* Nom: _separator
	* 
	* Description:
	* Cet attribut est destiné à contenir la valeur du séparateur de la 
	* définition de la table utilisée pour la construction du tableau.
	* ----------------------------------------------------------*/
	private String _separator;
}