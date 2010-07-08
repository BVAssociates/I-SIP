/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayCellRenderer.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des données.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayCellRenderer.java,v $
* Revision 1.1  2009/03/05 15:58:05  jy
* La classe DisplayCellRenderer est une spécification de la classe
* DefaultTableCellRenderer chargée de l'affichage des cellules.
* Elle permet d'abord de récupérer un objet de GenericTreeObjectNode et une
* copie de l'ensemble de MaskRule originaux dans le modèle, et puis de vérifier
* la condition décrit dans le MaskRule en appelant la méthode testCondition()
* de la classe MaskRule.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports système
//
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//
//Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.gui.SortedTableModel;
import com.bv.core.gui.IconLoader;

/*----------------------------------------------------------
* Nom: DisplayCellRenderer
*
* Description:
* Cette classe est une spécialisation de la classe DefaultTableCellRenderer
* chargée de l'affichage des cellules.
* Elle permet d'abord de récupérer un objet de GenericTreeObjectNode et une 
* copie de l'ensemble de MaskRule originaux dans le modèle, et puis de vérifier
* la condition décrit dans le MaskRule en appelant la méthode testCondition() 
* de la classe MaskRule.  
* ----------------------------------------------------------*/
public class DisplayCellRenderer 
	extends DefaultTableCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. 
	* Elle est appelée à chaque fois qu'une cellule doit être affichée. 
	* Elle se charge de modifier les affichages de table(par exemple: 
	* la couleur du fond), en récupérant des données et une copie des masques
	* via le modèle de table(qui est passée en argument dans cette fonction), 
	* et puis vérifiant la condition en appelant la méthode testCondition de la
	* classe MaskRule.
	* Avant de la vérification de condition, il faut vérifier d'abord (via la 
	* méthode isConditionTested de MaskRule) si cette condition est deja testée
	* pour cette ligne(d'où la cellule à affichier se situe) ou non.
	* Si la condition est testée pour cette ligne, - et si la condtion est 
	* vérifiée -,on effectue les modifications définits dans la masque.
	* Si non, on passe à la vérification.
	* 
	* Arguments:
	* - table: Une instance de JTable.
	* - value: Des données à affichés pour une cellule, ici c'est un objet de 
	* 	IsisParameter.
	* - isSelected:	Paramètre de type boolean, vrais si la cellule est 
	* 	selectionnée.
	* - ishasFocus:	Paramètre de type boolean, vrais si la cellule a le focus.
	* - row: Le numéro de ligne de cellule.
	* - column:	Le numéro de colonne de cellule.  
	* 
	* Retourne: Une instance de composant graphique chargé de l'affichage de la
	* cellule.
	* ----------------------------------------------------------*/
	public Component getTableCellRendererComponent(
		JTable table, 
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column
		)
	{
		// Les traces sont désactivées, cette méthode étant appelée trop souvent
		/*
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BooleanCellRenderer", "getTableCellRendererComponent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("table=" + table);
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("isSelected=" + isSelected);
		trace_arguments.writeTrace("hasFocus=" + hasFocus);
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		*/
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
			row, column);
		if(isSelected == false) {
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
		}
		this.setIcon(null);
		SortedTableModel sorted_model = (SortedTableModel)table.getModel();
		DisplayDataModel data_model = (DisplayDataModel)sorted_model.getModel();
		GenericTreeObjectNode genericTreeObjectNode = 
			data_model.getParametersForRow(row);
		MaskRule[] maskRules = data_model.getMaskRulesForRow(row);
		//on vérifie si les masques existent.
		if(maskRules != null) {
			for(int i=0; i<maskRules.length; i++) {
				int font_style = getFont().getStyle();
				MaskRule maskRule = ((MaskRule)maskRules[i]);
				if(maskRule.isConditionTested() == false) {
					maskRule.testCondition(genericTreeObjectNode);
				}
				if(maskRule.doesConditionMatch() == true) {
					if(maskRule.getBackgroundColor() != null && 
						isSelected == false) {
						this.setBackground(Color.decode(
							maskRule.getBackgroundColor()));
					}
					if(maskRule.isReverted() == true && isSelected == false) {
						//font_style |= Font.BOLD;
						Color color_back = this.getBackground();
						Color color_font = this.getForeground();
						this.setBackground(color_font);
						this.setForeground(color_back);
					}
					if(maskRule.isItalic() == true) {
						font_style |= Font.ITALIC;
					}
					if(maskRule.getColor() != null && isSelected == false) {
						this.setForeground(Color.decode(maskRule.getColor()));
					}
					if(maskRule.getIconField() != null && 
						maskRule.getIconName() != null) {
						String column_name = table.getColumnModel().getColumn(
							column).getHeaderValue().toString();
						if(maskRule.getIconField().equals(column_name) == true) {
							this.setIcon(IconLoader.getIcon(
								maskRule.getIconName()));
							
						}
					}
					this.setFont(getFont().deriveFont(font_style));
				}
			}
		}
		//trace_methods.endOfMethod();
		return this;
	}
	

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
