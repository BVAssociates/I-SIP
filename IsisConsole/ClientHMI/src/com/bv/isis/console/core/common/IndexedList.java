/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/IndexedList.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation d'une liste indexée
* DATE:        27/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IndexedList.java,v $
* Revision 1.5  2009/01/14 14:17:01  tz
* Classe déplacée dans le package com.bv.isis.console.core.common.
*
* Revision 1.4  2005/07/01 12:27:45  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/10/13 14:02:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.2  2003/03/07 16:22:35  tz
* Ajout de la méthode values()
*
* Revision 1.1  2002/04/05 15:47:12  tz
* Cloture itération IT1.2
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.common;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: IndexedList
*
* Description:
* Cette classe est une classe de collection (comme un vecteur) qui additionne
* les fonctionnalités du vecteur et celle de la table de hash. C'est à dire que
* les éléments sont placés dans la liste par le biais d'une clé (à une clé ne
* peut correspondre qu'une valeur, comme pour la table de hash), mais les
* données sont restituées dans l'ordre d'insertion (comme pour le vecteur).
* La classe offre principalement les mêmes méthodes que la table de hash. La
* gestion des clés et des valeurs est masquée à l'utilisateur.
* ----------------------------------------------------------*/
public class IndexedList
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: IndexedList
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle crée la table des
	* indexes et le vecteur des données.
	* ----------------------------------------------------------*/
	public IndexedList()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "IndexedList");

		trace_methods.beginningOfMethod();
		_datas = new Vector();
		_indexes = new Hashtable();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: clear
	*
	* Description:
	* Cette méthode permet de retirer tous les éléments ayant été ajoutés à la
	* liste.
	* ----------------------------------------------------------*/
	public void clear()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "clear");

		trace_methods.beginningOfMethod();
		_datas.clear();
		_indexes.clear();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: size
	*
	* Description:
	* Cette méthode permet d'obtenir le nombre d'éléments contenus dans la
	* liste indexée.
	*
	* Retourne: Le nombre d'éléments dans la liste.
	* ----------------------------------------------------------*/
	public int size()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "size");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _datas.size();
	}

	/*----------------------------------------------------------
	* Nom: isEmpty
	*
	* Description:
	* Cette méthode permet de savoir si la liste indexée est vide ou non.
	*
	* Retourne: true si la liste est vide, false sinon.
	* ----------------------------------------------------------*/
	public boolean isEmpty()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "isEmpty");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return size() == 0;
	}

	/*----------------------------------------------------------
	* Nom: put
	*
	* Description:
	* Cette méthode permet d'ajouter un élément à la liste indexée. L'élément
	* est rangé dans l'ordre d'insertion, la clé servant à retrouver plus
	* rapidement cet élément dans la liste.
	* Si un élément existe déjà dans la liste avec la même clé, il sera
	* remplacé par le nouvel élément.
	*
	* Arguments:
	*  - key: La clé de l'élément,
	 * - value: La valeur de l'élément.
	* ----------------------------------------------------------*/
	public void put(
		Object key,
		Object value
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "put");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("key=" + key);
		trace_arguments.writeTrace("value=" + value);
		// On regarde s'il y a déjà une valeur
		Integer index = (Integer)_indexes.get(key);
		if(index == null)
		{
			// C'est une nouvelle insertion
			index = new Integer(_datas.size());
			_datas.add(value);
			_indexes.put(key, index);
		}
		else
		{
			// Il s'agit d'une valeur qui existe déjà
			// On va supprimer la précédente valeur
			_datas.removeElementAt(index.intValue());
			// On va insérer la nouvelle valeur à la même position
			_datas.insertElementAt(value, index.intValue());
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: get
	*
	* Description:
	* Cette méthode permet de récupérer une valeur de la liste indexée en
	* fonction de la clé passé en argument.
	* Si la clé correspond à une valeur, celle-ci est retournée. Dans le cas
	* contraire, la méthode retournera null.
	*
	* Arguments:
	*  - key: La clé de la valeur à récupérer.
	*
	* Retourne: La valeur correspondant à la clé, ou null.
	* ----------------------------------------------------------*/
	public Object get(
		Object key
		)
	{
		Object value = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "get");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("key=" + key);
		Integer index = (Integer)_indexes.get(key);
		if(index != null)
		{
			value = _datas.elementAt(index.intValue());
		}
		trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: containsKey
	*
	* Description:
	* Cette méthode permet de savoir si la clé spécifiée en argument existe
	* dans la liste ou non.
	*
	* Arguments:
	*  - key: La clé dont on veut connaître l'existence.
	*
	* Retourne: true si la clé existe, false sinon.
	* ----------------------------------------------------------*/
	public boolean containsKey(
		Object key
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "containsKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("key=" + key);
		trace_methods.endOfMethod();
		return _indexes.containsKey(key);
	}

	/*----------------------------------------------------------
	* Nom: containsValue
	*
	* Description:
	* Cette méthode permet de savoir si la valeur spécifiée en argument existe
	* dans la liste ou non.
	*
	* Arguments:
	*  - value: La valeur dont on veut connaître l'existence.
	*
	* Retourne: true si la valeur existe, false sinon.
	* ----------------------------------------------------------*/
	public boolean containsValue(
		Object value
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "containsValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_methods.endOfMethod();
		return _datas.contains(value);
	}

	/*----------------------------------------------------------
	* Nom: toArray
	*
	* Description:
	* Cette méthode permet de récupérer l'ensemble des valeurs de la liste sous
	* forme de tableau d'objets. Elle correspond à la méthode de même nom de la
	* classe Vector.
	*
	* Arguments:
	*  - array: Un tableau d'objet devant contenir les valeurs, s'il est assez
	*    grand.
	*
	* Retourne: Un tableau d'objet contenant toutes les valeurs de la liste.
	* ----------------------------------------------------------*/
	public Object[] toArray(
		Object[] array
		)
	{
		Object[] the_array;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "toArray");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("array=" + array);
		if(array == null)
		{
			the_array = _datas.toArray();
		}
		else
		{
			the_array = _datas.toArray(array);
		}
		trace_methods.endOfMethod();
		return the_array;
	}

	/*----------------------------------------------------------
	* Nom: keys
	*
	* Description:
	* Cette méthode permet de récupérer l'ensemble des clés de la liste indexée
	* sous forme d'interface Enumeration.
	*
	* Retourne: Les clés de la liste sous forme d'interface Enumeration.
	* ----------------------------------------------------------*/
	public Enumeration keys()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "keys");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _indexes.keys();
	}

	/*----------------------------------------------------------
	* Nom: values
	*
	* Description:
	* Cette méthode permet de récupérer l'ensemble des valeurs de la liste
	* indexée sous forme d'interface Enumeration.
	*
	* Retourne: Les valeurs de la liste sous forme d'interface Enumeration.
	* ----------------------------------------------------------*/
	public Enumeration values()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "values");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _datas.elements();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette méthode est automatiquement appelée par le ramasse-miettes de la
	* machine virtuelle Java lorsque une instance est sur le point d'être
	* détruite. Elle permet de libérer toutes les ressources allouées par
	* l'instance.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IndexedList", "finalize");

		trace_methods.beginningOfMethod();
		clear();
		_datas = null;
		_indexes = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _datas
	*
	* Description:
	* Cet attribut maintient une référence sur un vecteur chargé de maintenir
	* dans l'ordre d'insertion toutes les valeurs qui ont été ajoutées à la
	* liste indexée.
	* ----------------------------------------------------------*/
	private Vector _datas;

	/*----------------------------------------------------------
	* Nom: _indexes
	*
	* Description:
	* Cet attribut maintient une référence sur une table de hash chargée de
	* maintenir la liste de clés des éléments, et leur index dans le vecteur
	* _datas.
	* La clé de la table de hash est la clé de la liste, tandis que la valeur
	* est une instance de Integer contenant l'index de la valeur dans le
	* vecteur.
	* ----------------------------------------------------------*/
	private Hashtable _indexes;
}