/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SearchableTable.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de table "recherchable"
* DATE:        17/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SearchableTable.java,v $
* Revision 1.5  2009/01/14 14:18:59  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.4  2008/02/15 14:15:26  tz
* Impl�mentation de la m�thode getFileFilters().
*
* Revision 1.3  2007/04/13 09:54:07  tz
* S�parateur CSV est le point virgule.
*
* Revision 1.2  2007/03/23 15:26:29  tz
* Le s�parateur dans les fichiers CSV est la virgule.
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe JTable afin de cr�er un 
* tableau dans lequel l'utilisateur peut effectuer des recherches de cha�nes 
* de caract�res ou sauvegarder les donn�es dans des fichiers.
* Pour cela, elle impl�mente l'interface SearchableComponentInterface.
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
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau � partir d'un tableau (� deux dimensions) de
	* valeurs et d'un tableau repr�sentant les intitul�s des colonnes.
	* Une instance de SortedTableModel est cr��e � partir des informations 
	* pass�es en arguments et est associ�e � la table.
	*
	* Arguments:
	*  - data: Un tableau � deux dimensions contenant les valeurs � afficher
	*    dans le tableau,
	* - columns: Un tableau � une dimension contenant les intitul�s des
	*   colonnes,
	* - separator: Le s�parateur de la d�finition de la table. 
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
		// On ajoute le listener sur l'ent�te
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	*
	* Description:
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau � partir d'un mod�le de donn�es et d'un mod�le de
	* colonnes.
	* Une instance de SortedTableModel est cr��e � partir des informations 
	* pass�es en arguments et est associ�e � la table.
	*
	* Arguments:
	*  - dataModel: Une r�f�rence sur un objet TableModel d�finissant le mod�le
	*    des donn�es,
	*  - columnModel: Une r�f�rence sur un objet TableColumnModel d�finissant
	*    le mod�le des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
		// On ajoute le listener sur l'ent�te
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	* 
	* Description:
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau � partir d'un mod�le de donn�es.
	* Une instance de SortedTableModel est cr��e � partir des informations 
	* pass�es en arguments et est associ�e � la table.
	* 
	* Arguments:
	*  - dataModel: Une r�f�rence sur un objet TableModel d�finissant le 
	*    mod�le des donn�es et le mod�le des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
		// On ajoute le listener sur l'ent�te
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SearchableTable
	* 
	* Description:
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau � partir d'un vecteur contenant les donn�es et 
	* d'un vecteur contenant les noms des colonnes.
	* Une instance de SortedTableModel est cr��e � partir des informations 
	* pass�es en arguments et est associ�e � la table.
	* 
	* Arguments:
	*  - rowData: Une r�f�rence sur un objet Vector contenant les donn�es,
	*  - columnNames: Une r�f�rence sur un objet Vector contenant les noms 
	*    des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
		// On ajoute le listener sur l'ent�te
		((SortedTableModel)getModel()).setTableHeader(getTableHeader());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getSelectedRow
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe JTable. Elle permet de 
	* r�cup�rer l'indice du premier rang s�lectionn�.
	* Elle est red�finie afin d'effectuer une translation d'indice entre le 
	* mod�le tri� et le mod�le source.
	* 
	* Retourne: -1 si aucun rang n'est s�lectionn�, sinon l'indice du premier 
	* rang s�lectionn�.
	* ----------------------------------------------------------*/
	public int getSelectedRow()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTable", "getSelectedRow");
		int selected_row = SortedTableModel.NO_SELECTION;

		trace_methods.beginningOfMethod();
		// On r�cup�re la valeur r�ellement s�lectionn�e
		selected_row = super.getSelectedRow();
		// S'il y a un rang s�lectionn�, il faut effectuer une translation
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de rechercher une cha�ne de 
	* caract�res dans le tableau.
	* Si une occurence peut �tre trouv�e, la ligne contenant cette cha�ne est 
	* s�lectionn�e, et est rendue visible. La position de la premi�re 
	* occurence trouv�e est enregistr�e.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne de caract�res � rechercher dans le 
	*    tableau.
	* 
	* Retourne: true si la cha�ne a pu �tre trouv�e, false sinon.
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
		// On v�rifie la validit� de l'argument
		if(stringToSearchFor == null || stringToSearchFor.equals("") ==  true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// On va rechercher, ligne par ligne, colonne par colonne une
		// valeur contenant la cha�ne
		for(int row = 0 ; row < getRowCount() ; row ++)
		{
			for(int col = 0 ; col < getColumnCount() ; col ++)
			{
				// On r�cup�re la valeur sous forme de cha�ne
				text = getValueAt(row, col).toString();
				// La cha�ne est-elle contenue dans la valeur ?
				int pos = text.indexOf(stringToSearchFor); 
				if(pos != -1)
				{
					// La cha�ne a �t� trouv�e dans la valeur, on va
					// s�lectionner la ligne correspondant
					changeSelection(row, 0, false, false);
					// On enregistre la position de la derni�re recherche
					// dans le composant, et on sort
					_lastSearchPosition = row + 1;
					trace_methods.endOfMethod();
					return true;
				}
			}
		}
		// Si on arrive ici, c'est que la cha�ne n'a pas �t� trouv�e.
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de rechercher une cha�ne de 
	* caract�res pass�e en argument � partir de la position de la derni�re 
	* occurence enregistr�e.
	* Si une nouvelle occurence est trouv�e, la ligne du tableau la contenant 
	* est s�lectionn� et est rendue visible.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans le tableau.
	* 
	* Retourne: true si une occurence de la cha�ne peut �tre trouv�e, 
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
		// On v�rifie la validit� de l'argument
		if(stringToSearchFor == null || stringToSearchFor.equals("") ==  true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// On va rechercher ligne par ligne, colonne par colonne �
		// partir de la derni�re position de recherche
		int row = _lastSearchPosition;
		while(true)
		{
			// A-t-on atteint la fin de la table ?
			if(row == getRowCount())
			{
				// C'est la fin de la table. Si on n'est pas parti du
				// d�but et que l'on pas recherch� dans les premi�res lignes
				// on repart de z�ro
				if(_lastSearchPosition != 0 && rewinded == false)
				{
					row = 0;
					rewinded = true;
					continue;
				}
				// La recherche a d�marr�e depuis la premi�re ligne, on va
				// arr�ter.
				break;
			}
			if(row == _lastSearchPosition && rewinded == true)
			{
				// La recherche a red�marr� depuis le d�but, on va arr�ter
				break;
			}
			for(int col = 0 ; col < getColumnCount() ; col ++)
			{
				// On r�cup�re la valeur 
				text = getValueAt(row, col).toString();
				// On regarde si la cha�ne est contenue dans la valeur
				int pos = text.indexOf(stringToSearchFor); 
				if(pos != -1)
				{
					// La cha�ne est contenue dans la valeur, on va 
					// s�lectionner la ligne correspondant
					changeSelection(row, 0, false, false);
					// On enregistre la derni�re position de recherche dans le
					// composant, et on sort
					_lastSearchPosition = row + 1;
					trace_methods.endOfMethod();
					return true;
				}
			}
			// On passe � la ligne suivante
			row ++;
		}
		trace_methods.endOfMethod();
		// Si on arrive ici, c'est que l'on n'a pas trouv�
		return false;
	}

	/*----------------------------------------------------------
	* Nom: setLastSearchPosition
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de d�finir la derni�re 
	* position de la recherche d'une cha�ne de caract�res dans le tableau.
	* 
	* Arguments:
	*  - lastSearchPosition: La derni�re position de la recherche dans le 
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de conna�tre la derni�re 
	* position de la recherche d'une cha�ne de caract�res dans le tableau.
	* 
	* Retourne: La derni�re position de la recherche dans le tableau.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet d'enregistrer le contenu du 
	* tableau dans un fichier texte, pass� en argument.
	* L'utilisateur est invit� � indiquer si l'ent�te du tableau doit 
	* �galement �tre sauvegard� ou non.
	* 
	* Arguments:
	*  - file: Une r�f�rence sur un objet File correspondant au fichier dans 
	*    lequel enregistrer les donn�es,
	*  - mainWindowInterface: Une r�f�rence sur une interface 
	*    MainWindowInterface correspondant � l'interface principale.
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
		// On v�rifie la validit� des arguments
		if(file == null || mainWindowInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return;
		}
		String absolute_path = file.getAbsolutePath();
		// On va regarder quelle extension est associ�e au fichier
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
				// On va demander � l'utilisateur s'il veut �galement enregistrer
				// les ent�tes des colonnes
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
						// On r�cup�re l'ent�te de la colonne
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
				// Ecriture de l'ent�te XML
				writer.write("<?xml version=\"1.0\" " +
					"encoding=\"iso-8859-1\"?>");
				writer.newLine();
				writer.write("<Table " +
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
					"xsi:noNamespaceSchemaLocation=\"" +
					xsd_file.toURL() + "\">");
				writer.newLine();
			}
			// On va r�cup�rer, ligne par ligne, colonne par colonne, la
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
						// On r�cup�re la valeur sous forme de cha�ne
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
				// On va g�n�rer le fichier XSD
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
			// On va afficher un message � l'utilisateur
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle est appel�e afin de r�cup�rer la 
	* liste des extensions de fichier pr�f�r�es, autres que "toutes", sous 
	* la forme d'un tableau de FileFilter.
	* 
	* Dans le cas d'un tableau, les extensions pr�f�r�es sont : xml ou csv.
	* 
	* Retourne: La liste des extensions pr�f�r�es sous forme de tableau de 
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
	* Cette m�thode permet de r�cup�rer la valeur du s�parateur de la 
	* d�finition de la table.
	* 
	* Retourne: Le s�parateur de la d�finition de la table.
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
	* Cet attribut permet de stocker la position de la derni�re recherche 
	* d'une cha�ne dans le tableau (voir les m�thodes setLastSearchPosition() 
	* et getLastSearchPosition()).
	* ----------------------------------------------------------*/
	private int _lastSearchPosition;

	/*----------------------------------------------------------
	* Nom: _separator
	* 
	* Description:
	* Cet attribut est destin� � contenir la valeur du s�parateur de la 
	* d�finition de la table utilis�e pour la construction du tableau.
	* ----------------------------------------------------------*/
	private String _separator;
}