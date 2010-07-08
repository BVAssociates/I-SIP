/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/IndexedList.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation d'une liste index�e
* DATE:        27/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IndexedList.java,v $
* Revision 1.5  2009/01/14 14:17:01  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.common.
*
* Revision 1.4  2005/07/01 12:27:45  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/10/13 14:02:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.2  2003/03/07 16:22:35  tz
* Ajout de la m�thode values()
*
* Revision 1.1  2002/04/05 15:47:12  tz
* Cloture it�ration IT1.2
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.common;

//
// Imports syst�me
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
* les fonctionnalit�s du vecteur et celle de la table de hash. C'est � dire que
* les �l�ments sont plac�s dans la liste par le biais d'une cl� (� une cl� ne
* peut correspondre qu'une valeur, comme pour la table de hash), mais les
* donn�es sont restitu�es dans l'ordre d'insertion (comme pour le vecteur).
* La classe offre principalement les m�mes m�thodes que la table de hash. La
* gestion des cl�s et des valeurs est masqu�e � l'utilisateur.
* ----------------------------------------------------------*/
public class IndexedList
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: IndexedList
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle cr�e la table des
	* indexes et le vecteur des donn�es.
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
	* Cette m�thode permet de retirer tous les �l�ments ayant �t� ajout�s � la
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
	* Cette m�thode permet d'obtenir le nombre d'�l�ments contenus dans la
	* liste index�e.
	*
	* Retourne: Le nombre d'�l�ments dans la liste.
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
	* Cette m�thode permet de savoir si la liste index�e est vide ou non.
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
	* Cette m�thode permet d'ajouter un �l�ment � la liste index�e. L'�l�ment
	* est rang� dans l'ordre d'insertion, la cl� servant � retrouver plus
	* rapidement cet �l�ment dans la liste.
	* Si un �l�ment existe d�j� dans la liste avec la m�me cl�, il sera
	* remplac� par le nouvel �l�ment.
	*
	* Arguments:
	*  - key: La cl� de l'�l�ment,
	 * - value: La valeur de l'�l�ment.
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
		// On regarde s'il y a d�j� une valeur
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
			// Il s'agit d'une valeur qui existe d�j�
			// On va supprimer la pr�c�dente valeur
			_datas.removeElementAt(index.intValue());
			// On va ins�rer la nouvelle valeur � la m�me position
			_datas.insertElementAt(value, index.intValue());
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: get
	*
	* Description:
	* Cette m�thode permet de r�cup�rer une valeur de la liste index�e en
	* fonction de la cl� pass� en argument.
	* Si la cl� correspond � une valeur, celle-ci est retourn�e. Dans le cas
	* contraire, la m�thode retournera null.
	*
	* Arguments:
	*  - key: La cl� de la valeur � r�cup�rer.
	*
	* Retourne: La valeur correspondant � la cl�, ou null.
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
	* Cette m�thode permet de savoir si la cl� sp�cifi�e en argument existe
	* dans la liste ou non.
	*
	* Arguments:
	*  - key: La cl� dont on veut conna�tre l'existence.
	*
	* Retourne: true si la cl� existe, false sinon.
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
	* Cette m�thode permet de savoir si la valeur sp�cifi�e en argument existe
	* dans la liste ou non.
	*
	* Arguments:
	*  - value: La valeur dont on veut conna�tre l'existence.
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
	* Cette m�thode permet de r�cup�rer l'ensemble des valeurs de la liste sous
	* forme de tableau d'objets. Elle correspond � la m�thode de m�me nom de la
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
	* Cette m�thode permet de r�cup�rer l'ensemble des cl�s de la liste index�e
	* sous forme d'interface Enumeration.
	*
	* Retourne: Les cl�s de la liste sous forme d'interface Enumeration.
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
	* Cette m�thode permet de r�cup�rer l'ensemble des valeurs de la liste
	* index�e sous forme d'interface Enumeration.
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
	* Cette m�thode est automatiquement appel�e par le ramasse-miettes de la
	* machine virtuelle Java lorsque une instance est sur le point d'�tre
	* d�truite. Elle permet de lib�rer toutes les ressources allou�es par
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
	* Cet attribut maintient une r�f�rence sur un vecteur charg� de maintenir
	* dans l'ordre d'insertion toutes les valeurs qui ont �t� ajout�es � la
	* liste index�e.
	* ----------------------------------------------------------*/
	private Vector _datas;

	/*----------------------------------------------------------
	* Nom: _indexes
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur une table de hash charg�e de
	* maintenir la liste de cl�s des �l�ments, et leur index dans le vecteur
	* _datas.
	* La cl� de la table de hash est la cl� de la liste, tandis que la valeur
	* est une instance de Integer contenant l'index de la valeur dans le
	* vecteur.
	* ----------------------------------------------------------*/
	private Hashtable _indexes;
}