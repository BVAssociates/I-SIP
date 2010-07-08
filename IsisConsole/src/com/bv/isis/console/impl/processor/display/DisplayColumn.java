/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayColumn.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de d�finition des colonnes du tableau
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayColumn.java,v $
* Revision 1.10  2009/03/05 09:32:41  jy
* Remplacer DefaultTableCellRenderer par DisplayCellRenderer.
*
* Revision 1.9  2006/03/20 15:57:15  tz
* Passage d'un gestionnaire de rendu de cellule au constructeur.
*
* Revision 1.8  2005/07/01 12:16:43  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.6  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2004/10/06 07:33:44  tz
* Am�lioration de la dimension de la frame : gestion des largeurs des
* colonnes sur redimmensionnement.
*
* Revision 1.4  2004/07/29 12:11:38  tz
* Mise � jour de la documentation
*
* Revision 1.3  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisTableColumn;

/*----------------------------------------------------------
* Nom: DisplayColumn
*
* Description:
* Cette classe est une sp�cialisation de la classe TableColumn destin�e �
* calculer automatiquement la largeur de la colonne ainsi qu'� positionner le
* gestionnaire d'affichage de cellule correspondant au type.
*  - pour les colonnes de type texte (suffixe 's'), le gestionnaire sera une
*    instance de DefaultTableCellRenderer,
*  - pour les colonnes de type num�rique (suffixe 'n'), le gestionnaire sera une
*    instance de NumericCellRenderer,
*  - pour les colonnes de type bool�en (suffixe 'b'), le gestionnaire sera une
*    instance de BooleanCellRenderer,
*  - pour les colonnes de type pourcent (suffixe 'p'), le gestionnaire sera une
*    instance de PercentCellRenderer,
*  - pour les colonnes de type date (suffixe 'd'), le gestionnaire sera une
*    instance de DateCellRenderer.
* ----------------------------------------------------------*/
class DisplayColumn
	extends TableColumn
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayColumn
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle va d�terminer le 
	* type de donn�es qui seront affich�es dans la colonne (en fonction du 
	* suffixe), d�terminera la largeur de la colonne (via la m�thode 
	* computeColumnWidth()), et positionnera le gestionnaire d'affichage 
	* adapt�, au cas o� un gestionnaire sp�cifique n'est pas pass� en argument.
	* 
	* Arguments:
	*  - modelIndex: L'index de la colonne dans le mod�le (commen�ant � 0),
	*  - tableColumn: Une r�f�rence sur l'objet IsisTableColumn contenant les 
	*    sp�cifications de la colonne,
	*  - cellRenderer: Une r�f�rence sur un objet TableCellRenderer � utiliser 
	*    pour effectuer l'affichage des cellules.
 	* ----------------------------------------------------------*/
	public DisplayColumn(
		int modelIndex,
		IsisTableColumn tableColumn,
		TableCellRenderer cellRenderer
		)
	{
		super(modelIndex);
		int size = 10;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayColumn", "DisplayColumn");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modelIndex=" + modelIndex);
		trace_arguments.writeTrace("tableColumn=" + tableColumn);
		trace_arguments.writeTrace("cellRenderer=" + cellRenderer);
		// On positionne l'intitul� de la colonne
		setHeaderValue(tableColumn.name);
		// On va r�cup�rer la taille et le type de donn�es
		char type = tableColumn.type;
		size = tableColumn.size;
		if(cellRenderer == null)
		{
			// Suivant le type, on positionne le gestionnaire de rendu
			switch(type)
			{
				case 's':
					// On positionne le gestionnaire de texte
					//cellRenderer = new DefaultTableCellRenderer();
					cellRenderer = new DisplayCellRenderer();
					break;
				case 'n':
					// On positionne le gestionnaire num�rique
					cellRenderer = new NumericCellRenderer();
					break;
				case 'b':
					// On positionne le gestionnaire bool�en
					cellRenderer = new BooleanCellRenderer();
					break;
				case 'p':
					// On positionne le gestionnaire de pourcent
					cellRenderer = new PercentCellRenderer();
					break;
				case 'd':
					// On positionne le gestionnaire de date
					cellRenderer = new DateCellRenderer();
					break;
				default:
					trace_errors.writeTrace("Type inconnu: " + type);
					// On n'a rien � faire
					break;
			}
		}
		setCellRenderer(cellRenderer);
		// On positionne la largeur de la colonne
		int column_width = computeColumnWidth(type, size);
		int header_width = computeColumnWidth('s', tableColumn.name.length());
		setPreferredWidth(Math.max(column_width, header_width));
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _meanCharacterWidth
	*
	* Description:
	* Cet attribut statique contient la largeur moyenne d'un caract�re pouvant
	* �tre affich� dans le tableau. Cette valeur est n�cessaire afin de
	* d�terminer la largeur des colonnes.
	* ----------------------------------------------------------*/
	private static int _meanCharacterWidth = 0;

	/*----------------------------------------------------------
	* Nom: computeColumnWidth
	*
	* Description:
	* Cette m�thode est charg�e de calculer la largeur de la colonne en
	* fonction du type de donn�es et de la taille d�finie dans le dictionnaire
	* de la table.
	* Si l'attribut statique _meanCharacterWidth n'a pas encore �t� calcul�,
	* la m�thode effectue le calcul sur un �chantillon de caract�res.
	*
	* Arguments:
	*  - type: Le type des donn�es qui seront affich�es,
	*  - size: La taille (en nombre de caract�res) sp�cifi�e dans le
	*    dictionnaire.
	*
	* Retourne: La largeur de la colonne, en pixels.
	* ----------------------------------------------------------*/
	private int computeColumnWidth(
		char type,
		int size
		)
	{
		int column_width = 0;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayColumn", "computeColumnWidth");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("type=" + type);
		trace_arguments.writeTrace("size=" + size);
		// On commence par r�cup�rer la largeur moyenne d'un caract�re
		if(_meanCharacterWidth == 0)
		{
			JTable table = new JTable();
			_meanCharacterWidth = table.getFontMetrics(
				table.getFont()).stringWidth("aAbBcCdDeE") / 10;
		}
		// On calcule la largeur de la colonne en fonction du type
		switch(type)
		{
			case 'p':
			    // On ajoute deux caract�re � la taille
				column_width = _meanCharacterWidth * (size + 2);
				break;
			case 'b':
				// La taille est de 50
				column_width = 50;
				break;
			case 'd':
				// La taille est de 19 caract�res
				column_width = _meanCharacterWidth * 19;
				break;
			case 'n':
			case 's':
			default:
				// On applique le mode de calcul par d�faut
				column_width = _meanCharacterWidth * size;
				break;
		}
		trace_methods.endOfMethod();
		return column_width;
	}
}