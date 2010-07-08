/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/IsisTreeCellRenderer.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de rendu graphique des noeuds
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IsisTreeCellRenderer.java,v $
* Revision 1.12  2008/07/17 13:29:07  tz
* Prise en charge de la taille des ic�nes via la configuration.
*
* Revision 1.11  2006/03/07 09:26:13  tz
* Test de l'�tat OPENED et CHILD_CHANGING
*
* Revision 1.10  2005/07/01 12:25:12  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/11/05 10:43:13  tz
* Traitement du cas o� le noeud ne peut pas �tre explor�.
*
* Revision 1.8  2004/11/03 15:21:22  tz
* Affichage de deux ic�nes par le renderer.
*
* Revision 1.7  2004/10/22 15:43:24  tz
* V�rification des dimensions des ic�nes.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.5  2004/10/13 14:02:00  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le,
* Renommage de InuitTreeCellRenderer en IsisTreeCellRenderer.
*
* Revision 1.4  2002/04/05 15:47:21  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.gui;

//
// Imports syst�me
//
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import java.awt.Component;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;
import javax.swing.ImageIcon;
import com.bv.core.gui.IconLoader;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.corbacom.IsisNodeLabel;

/*----------------------------------------------------------
* Nom: IsisTreeCellRenderer
*
* Description:
* Cette classe est charg�e de la repr�sentation graphique des noeuds dans
* l'arbre d'exploration. Elle permet de modifier la repr�sentation des
* diff�rents noeuds en fonction de leur type (noeud table ou noeud instance) et
* de la table I-TOOLS d'origine.
* ----------------------------------------------------------*/
class IsisTreeCellRenderer
	extends DefaultTreeCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: IsisTreeCellRenderer
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est
	* pr�sent�e que pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public IsisTreeCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisTreeCellRenderer", "IsisTreeCellRenderer");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTreeCellRendererComponent
	*
	* Description:
	* Cette m�thode red�fini celle de la super-classe. Elle est appel�e �
	* chaque fois qu'un noeud graphique doit �tre repr�sent� dans l'arbre
	* d'exploration.
	* La m�thode va positionner le libell� et l'ic�ne du noeud graphique en
	* fonction des informations contenues dans l'objet IsisNodeLabel associ�
	* au noeud. Si l'ic�ne est nulle, une ic�ne par d�faut d�pendant du type de
	* noeud sera positionn�e.
	*
	* Arguments:
	*  - tree: Une r�f�rence sur l'arbre dans lequel sont affich�s les noeuds,
	*  - value: Une r�f�rence sur le noeud � afficher,
	*  - isSelected: Un bool�en indiquant si le noeud est s�lectionn�,
	*  - isExpanded: Un bool�en indiquant si le noeud a �t� �tendu,
	*  - isLeaf: Un bool�en indiquant si le noeud est un noeud feuille (sans
	*    enfants),
	*  - row: Un entier repr�sentant le rang du noeud,
	*  - hasFocus: Un bool�en indiquant si l'arbre a le focus.
	*
	* Retourne: La r�f�rence sur le composant graphique charg� de repr�sent� le
	* noeud graphique.
	* ----------------------------------------------------------*/
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean isSelected,
		boolean isExpanded,
		boolean isLeaf,
		int row,
		boolean hasFocus
		)
	{
		String label = null;
		String icon_name = null;
		ImageIcon icon = null;
		ImageIcon state_icon = null;

		// Cette m�thode est appel�e trop souvent, il faut inhiber les traces
		// pour conserver une ex�cution rapide.
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisTreeCellRenderer", "getTreeCellRendererComponent");*/

		//trace_methods.beginningOfMethod();
		// On r�cup�re le composant d'affichage par d�faut
		JLabel center_component = 
			(JLabel)super.getTreeCellRendererComponent(tree, value, 
			isSelected, isExpanded, isLeaf, row, hasFocus);
		// Si la valeur n'est pas un noeud graphique, on retourne
		// le composant par d�faut
		if(!(value instanceof GenericTreeObjectNode))
		{
			//trace_methods.endOfMethod();
			return center_component;
		}
		GenericTreeObjectNode tree_node =
			(GenericTreeObjectNode)value;
		// On r�cup�re l'objet IsisNodeLabel associ� au noeud
		IsisNodeLabel node_label = tree_node.getLabel();
		if(node_label != null)
		{
			label = node_label.label;
			icon_name = node_label.icon;
		}
		// S'il n'y a libell�, on le positionne
		if(label != null && label.equals("") == false)
		{
			center_component.setText(label);
		}
		else
		{
			if(tree_node instanceof GenericTreeClassNode)
			{
				// Si le noeud est un noeud table, le libell� par d�faut
				// est le nom de la table
				center_component.setText(tree_node.getTableName());
			}
			else
			{
				// Si le noeud n'est pas un noeud table, le libell� par 
				// d�faut est la valeur de la cl�
				center_component.setText(tree_node.getKey());
			}
		}
		if(icon_name != null && icon_name.equals("") == false)
		{
			// Une ic�ne est sp�cifi�e, on va la charger
			icon = IconLoader.getIcon(icon_name);
			// On v�rifie les dimensions de l'ic�ne.
			// On va r�cup�rer la taille des ic�nes depuis la configuration, si ce
			// n'est pas d�j� fait
			synchronized(this) {
				if(_iconsSize == 0) {
					try {
						ConfigurationAPI configuration = new ConfigurationAPI();
						_iconsSize = configuration.getInt("GUI", 
							"NodeIcons.Size");
					}
					catch(Exception e) {
						_iconsSize = 13;
					}
				}
			}
			if(icon != null && icon.getIconHeight() != _iconsSize && 
				icon.getIconWidth() != _iconsSize)
			{
				Trace trace_errors =
					TraceAPI.declareTraceErrors("Console");
				
				// L'ic�ne n'a pas les bonnes dimensions, on ne la
				// prend pas
				trace_errors.writeTrace("L'ic�ne de noeud n'a pas les " +
					"bonnes dimensions: " + icon_name);
				icon = null;
			}
		}
		if(icon == null)
		{
			// Il n'y a pas d'ic�ne, on utilise l'ic�ne par d�faut
			if(value instanceof GenericTreeClassNode)
			{
				// L'ic�ne par d�faut pour une table est un dossier ouvert
				// ou ferm� suivant que le noeud est �tendu ou non
				if(isExpanded == false)
				{
					center_component.setIcon(getDefaultClosedIcon());
				}
				else
				{
					center_component.setIcon(getDefaultOpenIcon());
				}
			}
			else
			{
				// Il n'y a pas d'ic�ne, on r�cup�re l'ic�ne par d�faut
				center_component.setIcon(IconLoader.getIcon("Default_Instance"));
			}
		}
		else
		{
			// Il y a une ic�ne, on la positionne
			center_component.setIcon(icon);
		}
		// On va charger une ic�ne suppl�mentaire en fonction de l'�tat
		// du noeud
		int node_state = tree_node.getNodeState(); 
		if(node_state == GenericTreeObjectNode.NodeStateEnum.OPENED ||
			node_state == GenericTreeObjectNode.NodeStateEnum.CHILD_CHANGING)
		{
			// Le noeud est explor�, on va charger l'ic�ne en fonction
			// de la pr�sence ou non d'enfants
			if(tree_node.getChildCount() == 0)
			{
				// Le noeud a �t� explor�, mais il n'a pas d'enfants, on
				// va r�cup�rer l'ic�ne idoine
				state_icon = IconLoader.getIcon("ExploredNOKPattern");
			}
			else
			{
				// Le noeud a �t� explor�, et il a des enfants, on
				// va r�cup�rer l'ic�ne idoine
				state_icon = IconLoader.getIcon("ExploredOKPattern");
			}
		}
		else
		{
			// Le noeud n'est pas dans l'�tat explor�, on va r�cup�rer
			// l'ic�ne d'un noeud ferm�
			if(MenuFactory.isExplorable(tree_node.getMenu()) == false)
			{
				state_icon = IconLoader.getIcon("DeadEnd");
			}
			else
			{
				state_icon = IconLoader.getIcon("ClosedPattern");
			}
		}
		// On va cr�er un JLabel pour contenir l'ic�ne d'�tat
		JLabel state_label = new JLabel(state_icon);
		// On va cr�er un objet JPanel qui contiendra le JLabel d'�tat
		// et l'instance de JLabel par d�faut
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(tree.getBackground());
		panel.add(state_label, BorderLayout.WEST);
		panel.add(center_component, BorderLayout.CENTER);
		panel.setSize(center_component.getWidth() + 15, center_component.getHeight());
		//trace_methods.endOfMethod();
		// On retourne le panneau
		return panel;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _iconsSize
	* 
	* Description:
	* Cet attribut statique est charg� de stocker la taille des ic�nes charg�s 
	* d'�tre associ�s aux noeuds de l'arbre d'exploration.
	* ----------------------------------------------------------*/
	private static int _iconsSize = 0;
}