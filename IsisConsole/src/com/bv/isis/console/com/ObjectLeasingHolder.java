/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/ObjectLeasingHolder.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de comptage des utilisations d'objets
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ObjectLeasingHolder.java,v $
* Revision 1.6  2005/07/01 12:29:00  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/11/02 09:09:11  tz
* Renommage de AgentSessionLeasingHolder en ObjectLeasingHolder.
*
* Revision 1.4  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.3  2004/07/29 12:25:14  tz
* Mise � jour de la documentation
*
* Revision 1.2  2003/12/08 14:38:07  tz
* Mise � jour du mod�le
*
* Revision 1.1  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.com;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.corbacom.IsisTableDefinition;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: ObjectLeasingHolder
* 
* Description:
* Cette classe est une classe technique charg�e de g�rer les leasings (baux) 
* sur un objet. Elle dispose de deux attributs, l'un �tant une r�f�rence sur 
* l'objet, l'autre sur un compteur de leasing, qui est incr�ment� � chaque 
* fois que la r�f�rence sur l'objet, et qui est d�cr�ment� � chaque fois que 
* la r�f�rence est lib�r�e.
* ----------------------------------------------------------*/
public class ObjectLeasingHolder
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ObjectLeasingHolder
	* 
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle permet de fournir 
	* la r�f�rence sur l'objet sujet des leasings.
	* Le compteur de leasing passe automatiquement � 1, puisque cette classe 
	* n'est cr��e qu'� partir du moment o� un leasing sur l'objet a �t� 
	* demand�.
	* 
	* Arguments:
	*  - leasedObject: Une r�f�rence sur l'objet sujet des leasings.
 	* ----------------------------------------------------------*/
	public ObjectLeasingHolder(
		Object leasedObject
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "ObjectLeasingHolder");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("leasedObject=" + leasedObject);
		// On stocke la r�f�rence sur l'objet
		_leasedObject = leasedObject;
		// Le compteur passe � 1
		_leasingsCounter = 1;
        //met � jour la date de derni�re utilisation
        _lastUsed=System.currentTimeMillis();
        
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLeasedObject
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence sur l'objet pour lequel 
	* l'instance de AgentSessionLeasingHolder a �t� cr��e.
	* 
	* Retourne: Une r�f�rence sur l'objet sujet du leasing.
	* ----------------------------------------------------------*/
	public Object getLeasedObject()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "getLeasedObject");

		trace_methods.beginningOfMethod();
        
		trace_methods.endOfMethod();
		return _leasedObject;
	}

	/*----------------------------------------------------------
	* Nom: addLeasing
	*
	* Description:
	* Cette m�thode permet d'ajouter un leasing sur l'objet. Elle incr�mente 
	* le compteur de leasing.
	* ----------------------------------------------------------*/
	public synchronized void addLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "addLeasing");

		trace_methods.beginningOfMethod();
		// On incr�mente le compteur
		_leasingsCounter ++;
        _lastUsed = System.currentTimeMillis();
        
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: releaseLeasing
	*
	* Description:
	* Cette m�thode permet de retirer un leasing sur l'objet. Elle d�cr�mente 
	* le compteur de leasing.
	* ----------------------------------------------------------*/
	public synchronized void releaseLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "releaseLeasing");

		trace_methods.beginningOfMethod();
		// On d�cr�mente le compteur
		if(_leasingsCounter > 0)
		{
			_leasingsCounter --;
            _lastUsed = System.currentTimeMillis();
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isFreeOfLeasing
	* 
	* Description:
	* Cette m�thode permet de savoir si tous les leasings sur l'objet ont �t� 
	* lib�r�s ou non. Elle retourne true si la valeur du compteur de leasings 
	* vaut 0.
	* 
	* Retourne: true si tous les leasings ont �t� lib�r�s, false sinon.
	* ----------------------------------------------------------*/
	public synchronized boolean isFreeOfLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "getNumberOfLeasings");

		trace_methods.beginningOfMethod();

        if (_leasingsCounter <= 0) {

            // garde le leasing tant que le timeout n'est pas d�pass�
            if ( (System.currentTimeMillis() - _lastUsed) > (_leaseTimeout * 1000)) {
                return true;
            }
        }

		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getNumberOfLeasings
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le nombre de leasings actuellement en 
	* cours sur l'objet.
	* 
	* Retourne: Le nombre de leasings sur l'objet.
	* ----------------------------------------------------------*/
	public synchronized int getNumberOfLeasings()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "getNumberOfLeasings");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _leasingsCounter;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette m�thode est automatiquement appel�e par le ramasse miettes de la
	* machine virtuelle Java lorsque l'instance de la classe est sur le point
	* d'�tre d�truite. Elle permet de lib�rer les ressources allou�es par
	* celle-ci.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "finalize");

		trace_methods.beginningOfMethod();
		// On lib�re la r�f�rence sur la session
		_leasedObject = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _leasingsCounter
	*
	* Description:
	* Cet attribut est un compteur de leasings. Il permet de conna�tre le 
	* nombre de leasings en cours sur l'objet.
	* ----------------------------------------------------------*/
	private int _leasingsCounter;

    /*----------------------------------------------------------
	* Nom: _lastUsed
	*
	* Description:
	* Cet attribut contient la date Epoch (ms) de la derni�re utilisation
    *
	* ----------------------------------------------------------*/
	private long _lastUsed;

    /*----------------------------------------------------------
	* Nom: _leaseTimeout
	*
	* Description:
	* Cet attribut contient l'attente minimum en secondes � attendre avant
    * la suppression du cache
    *
	* ----------------------------------------------------------*/
	final private long _leaseTimeout=10;

	/*----------------------------------------------------------
	* Nom: _leasedObject
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'objet sujet du leasing.
	* ----------------------------------------------------------*/
	private Object _leasedObject;
}