/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
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
* Prise en charge de la taille des icônes via la configuration.
*
* Revision 1.11  2006/03/07 09:26:13  tz
* Test de l'état OPENED et CHILD_CHANGING
*
* Revision 1.10  2005/07/01 12:25:12  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/11/05 10:43:13  tz
* Traitement du cas où le noeud ne peut pas être exploré.
*
* Revision 1.8  2004/11/03 15:21:22  tz
* Affichage de deux icônes par le renderer.
*
* Revision 1.7  2004/10/22 15:43:24  tz
* Vérification des dimensions des icônes.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 14:02:00  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle,
* Renommage de InuitTreeCellRenderer en IsisTreeCellRenderer.
*
* Revision 1.4  2002/04/05 15:47:21  tz
* Cloture itération IT1.2
*
* Revision 1.3  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
// Imports système
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
* Cette classe est chargée de la représentation graphique des noeuds dans
* l'arbre d'exploration. Elle permet de modifier la représentation des
* différents noeuds en fonction de leur type (noeud table ou noeud instance) et
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
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
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
	* Cette méthode redéfini celle de la super-classe. Elle est appelée à
	* chaque fois qu'un noeud graphique doit être représenté dans l'arbre
	* d'exploration.
	* La méthode va positionner le libellé et l'icône du noeud graphique en
	* fonction des informations contenues dans l'objet IsisNodeLabel associé
	* au noeud. Si l'icône est nulle, une icône par défaut dépendant du type de
	* noeud sera positionnée.
	*
	* Arguments:
	*  - tree: Une référence sur l'arbre dans lequel sont affichés les noeuds,
	*  - value: Une référence sur le noeud à afficher,
	*  - isSelected: Un booléen indiquant si le noeud est sélectionné,
	*  - isExpanded: Un booléen indiquant si le noeud a été étendu,
	*  - isLeaf: Un booléen indiquant si le noeud est un noeud feuille (sans
	*    enfants),
	*  - row: Un entier représentant le rang du noeud,
	*  - hasFocus: Un booléen indiquant si l'arbre a le focus.
	*
	* Retourne: La référence sur le composant graphique chargé de représenté le
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

		// Cette méthode est appelée trop souvent, il faut inhiber les traces
		// pour conserver une exécution rapide.
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisTreeCellRenderer", "getTreeCellRendererComponent");*/

		//trace_methods.beginningOfMethod();
		// On récupère le composant d'affichage par défaut
		JLabel center_component = 
			(JLabel)super.getTreeCellRendererComponent(tree, value, 
			isSelected, isExpanded, isLeaf, row, hasFocus);
		// Si la valeur n'est pas un noeud graphique, on retourne
		// le composant par défaut
		if(!(value instanceof GenericTreeObjectNode))
		{
			//trace_methods.endOfMethod();
			return center_component;
		}
		GenericTreeObjectNode tree_node =
			(GenericTreeObjectNode)value;
		// On récupère l'objet IsisNodeLabel associé au noeud
		IsisNodeLabel node_label = tree_node.getLabel();
		if(node_label != null)
		{
			label = node_label.label;
			icon_name = node_label.icon;
		}
		// S'il n'y a libellé, on le positionne
		if(label != null && label.equals("") == false)
		{
			center_component.setText(label);
		}
		else
		{
			if(tree_node instanceof GenericTreeClassNode)
			{
				// Si le noeud est un noeud table, le libellé par défaut
				// est le nom de la table
				center_component.setText(tree_node.getTableName());
			}
			else
			{
				// Si le noeud n'est pas un noeud table, le libellé par 
				// défaut est la valeur de la clé
				center_component.setText(tree_node.getKey());
			}
		}
		if(icon_name != null && icon_name.equals("") == false)
		{
			// Une icône est spécifiée, on va la charger
			icon = IconLoader.getIcon(icon_name);
			// On vérifie les dimensions de l'icône.
			// On va récupérer la taille des icônes depuis la configuration, si ce
			// n'est pas déjà fait
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
				
				// L'icône n'a pas les bonnes dimensions, on ne la
				// prend pas
				trace_errors.writeTrace("L'icône de noeud n'a pas les " +
					"bonnes dimensions: " + icon_name);
				icon = null;
			}
		}
		if(icon == null)
		{
			// Il n'y a pas d'icône, on utilise l'icône par défaut
			if(value instanceof GenericTreeClassNode)
			{
				// L'icône par défaut pour une table est un dossier ouvert
				// ou fermé suivant que le noeud est étendu ou non
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
				// Il n'y a pas d'icône, on récupère l'icône par défaut
				center_component.setIcon(IconLoader.getIcon("Default_Instance"));
			}
		}
		else
		{
			// Il y a une icône, on la positionne
			center_component.setIcon(icon);
		}
		// On va charger une icône supplémentaire en fonction de l'état
		// du noeud
		int node_state = tree_node.getNodeState(); 
		if(node_state == GenericTreeObjectNode.NodeStateEnum.OPENED ||
			node_state == GenericTreeObjectNode.NodeStateEnum.CHILD_CHANGING)
		{
			// Le noeud est exploré, on va charger l'icône en fonction
			// de la présence ou non d'enfants
			if(tree_node.getChildCount() == 0)
			{
				// Le noeud a été exploré, mais il n'a pas d'enfants, on
				// va récupérer l'icône idoine
				state_icon = IconLoader.getIcon("ExploredNOKPattern");
			}
			else
			{
				// Le noeud a été exploré, et il a des enfants, on
				// va récupérer l'icône idoine
				state_icon = IconLoader.getIcon("ExploredOKPattern");
			}
		}
		else
		{
			// Le noeud n'est pas dans l'état exploré, on va récupérer
			// l'icône d'un noeud fermé
			if(MenuFactory.isExplorable(tree_node.getMenu()) == false)
			{
				state_icon = IconLoader.getIcon("DeadEnd");
			}
			else
			{
				state_icon = IconLoader.getIcon("ClosedPattern");
			}
		}
		// On va créer un JLabel pour contenir l'icône d'état
		JLabel state_label = new JLabel(state_icon);
		// On va créer un objet JPanel qui contiendra le JLabel d'état
		// et l'instance de JLabel par défaut
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
	* Cet attribut statique est chargé de stocker la taille des icônes chargés 
	* d'être associés aux noeuds de l'arbre d'exploration.
	* ----------------------------------------------------------*/
	private static int _iconsSize = 0;
}