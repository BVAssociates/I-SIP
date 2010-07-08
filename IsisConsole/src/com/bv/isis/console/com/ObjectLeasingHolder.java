/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits réservés.
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
* Mise à jour du modèle.
*
* Revision 1.3  2004/07/29 12:25:14  tz
* Mise à jour de la documentation
*
* Revision 1.2  2003/12/08 14:38:07  tz
* Mise à jour du modèle
*
* Revision 1.1  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
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
* Cette classe est une classe technique chargée de gérer les leasings (baux) 
* sur un objet. Elle dispose de deux attributs, l'un étant une référence sur 
* l'objet, l'autre sur un compteur de leasing, qui est incrémenté à chaque 
* fois que la référence sur l'objet, et qui est décrémenté à chaque fois que 
* la référence est libérée.
* ----------------------------------------------------------*/
public class ObjectLeasingHolder
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ObjectLeasingHolder
	* 
	* Description:
	* Cette méthode est le constructeur de la classe. Elle permet de fournir 
	* la référence sur l'objet sujet des leasings.
	* Le compteur de leasing passe automatiquement à 1, puisque cette classe 
	* n'est créée qu'à partir du moment où un leasing sur l'objet a été 
	* demandé.
	* 
	* Arguments:
	*  - leasedObject: Une référence sur l'objet sujet des leasings.
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
		// On stocke la référence sur l'objet
		_leasedObject = leasedObject;
		// Le compteur passe à 1
		_leasingsCounter = 1;
        //met à jour la date de dernière utilisation
        _lastUsed=System.currentTimeMillis();
        
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLeasedObject
	* 
	* Description:
	* Cette méthode permet de récupérer la référence sur l'objet pour lequel 
	* l'instance de AgentSessionLeasingHolder a été créée.
	* 
	* Retourne: Une référence sur l'objet sujet du leasing.
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
	* Cette méthode permet d'ajouter un leasing sur l'objet. Elle incrémente 
	* le compteur de leasing.
	* ----------------------------------------------------------*/
	public synchronized void addLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "addLeasing");

		trace_methods.beginningOfMethod();
		// On incrémente le compteur
		_leasingsCounter ++;
        _lastUsed = System.currentTimeMillis();
        
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: releaseLeasing
	*
	* Description:
	* Cette méthode permet de retirer un leasing sur l'objet. Elle décrémente 
	* le compteur de leasing.
	* ----------------------------------------------------------*/
	public synchronized void releaseLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "releaseLeasing");

		trace_methods.beginningOfMethod();
		// On décrémente le compteur
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
	* Cette méthode permet de savoir si tous les leasings sur l'objet ont été 
	* libérés ou non. Elle retourne true si la valeur du compteur de leasings 
	* vaut 0.
	* 
	* Retourne: true si tous les leasings ont été libérés, false sinon.
	* ----------------------------------------------------------*/
	public synchronized boolean isFreeOfLeasing()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "getNumberOfLeasings");

		trace_methods.beginningOfMethod();

        if (_leasingsCounter <= 0) {

            // garde le leasing tant que le timeout n'est pas dépassé
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
	* Cette méthode permet de récupérer le nombre de leasings actuellement en 
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
	* Cette méthode est automatiquement appelée par le ramasse miettes de la
	* machine virtuelle Java lorsque l'instance de la classe est sur le point
	* d'être détruite. Elle permet de libérer les ressources allouées par
	* celle-ci.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ObjectLeasingHolder", "finalize");

		trace_methods.beginningOfMethod();
		// On libère la référence sur la session
		_leasedObject = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _leasingsCounter
	*
	* Description:
	* Cet attribut est un compteur de leasings. Il permet de connaître le 
	* nombre de leasings en cours sur l'objet.
	* ----------------------------------------------------------*/
	private int _leasingsCounter;

    /*----------------------------------------------------------
	* Nom: _lastUsed
	*
	* Description:
	* Cet attribut contient la date Epoch (ms) de la dernière utilisation
    *
	* ----------------------------------------------------------*/
	private long _lastUsed;

    /*----------------------------------------------------------
	* Nom: _leaseTimeout
	*
	* Description:
	* Cet attribut contient l'attente minimum en secondes à attendre avant
    * la suppression du cache
    *
	* ----------------------------------------------------------*/
	final private long _leaseTimeout=10;

	/*----------------------------------------------------------
	* Nom: _leasedObject
	*
	* Description:
	* Cet attribut maintient une référence sur l'objet sujet du leasing.
	* ----------------------------------------------------------*/
	private Object _leasedObject;
}