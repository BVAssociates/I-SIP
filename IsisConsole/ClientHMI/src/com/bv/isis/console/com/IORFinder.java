/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/IORFinder.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de connexion au processus Portail
* DATE:        14/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IORFinder.java,v $
* Revision 1.13  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2009/01/14 09:50:22  tz
* Correction de la fiche FS#587.
*
* Revision 1.11  2005/10/07 08:43:55  tz
* Gestion du profil de connexion au Portail par les propriétés système.
*
* Revision 1.10  2005/07/01 12:29:15  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.8  2004/10/06 07:43:51  tz
* Utilisation du mode bidirectionnel.
*
* Revision 1.7  2004/07/29 12:24:17  tz
* Utilisation de Portal* au lieu de Master*
* Mise à jour de la documentation
*
* Revision 1.6  2003/12/08 14:37:48  tz
* Mise à jour du modèle
*
* Revision 1.5  2002/09/20 10:39:33  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.4  2002/08/26 09:50:09  tz
* Utilisation de JacORB.
*
* Revision 1.3  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:59:03  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/14 17:17:35  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
//
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Any;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.BiDirPolicy.BidirectionalPolicyValueHelper;
import org.omg.BiDirPolicy.BOTH;
import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import com.bv.core.util.UtilStringTokenizer;

//
// Imports du projet
//
import com.bv.isis.corbacom.PortalInterface;
import com.bv.isis.corbacom.PortalInterfaceHelper;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: IORFinder
*
* Description:
* Cette classe est une classe technique chargée d'établir la communication avec
* le composant Portail du logiciel. Elle permet de récupérer la référence de
* l'interface CORBA publiée par le composant Portail (PortalInterface).
*
* Pour connaître la technique utilisée pour récupérer la référence de
* l'interface PortalInterface, se référer à la description de la méthode
* lookupPortal().
* ----------------------------------------------------------*/
public abstract class IORFinder
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: lookupPortal
	*
	* Description:
	* Cette méthode statique est utilisée pour établir la communication avec le
	* processus Portail du système, et pour récupérer la référence de l'interface
	* PortalInterface publiée par celui-ci.
	* Pour cela, elle ouvre une connexion TCP/IP avec la plate-forme définie
	* comme étant la plate-forme Portail, sur un port déterminé. Lorsque la
	* connexion est établie, le processus Portail envoie la référence CORBA de
	* son interface (IOR) sous forme de chaîne de caractères.
	* Cette chaîne est transformé en objet, puis la méthode tente d'appeler la
	* méthode _non_existent() de l'interface pour vérifier que la connexion a bien
	* été établie.
	* Cette méthode effectue également l'initialisation préalable de l'ORB, si 
	* cela n'a pas déjà été effectué, et récupère la référence sur l'objet POA.
	*
	* Si un problème survient lors de la tentative de connexion, l'exception
	* InnerException est levée avec un message indiquant la nature de l'exception.
	*
	* Retourne: Une référence sur l'interface PortalInterface si la connexion a
	* pu être établie, ou null.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static PortalInterface lookupPortal()
	    throws
			InnerException
	{
		String portal_host = "localhost";
		int portal_port = 4444;
		Socket socket = null;
		String interface_ior;
		PortalInterface portal_interface = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IORFinder", "lookupPortal");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// La première chose à faire est d'initialiser l'ORB
		if(_orb == null)
		{
			try
			{
				trace_debug.writeTrace("Initialisation de l'ORB");
				Properties properties = new Properties();
				properties.put(
					"org.omg.PortableInterceptor.ORBInitializerClass.bidir_init",
					"org.jacorb.orb.giop.BiDirConnectionInitializer");
				_orb = ORB.init(new String[0], properties);
				//init POA
				trace_debug.writeTrace("Activation du POA Manager");
				POA root_poa = POAHelper.narrow(_orb.resolve_initial_references(
					"RootPOA"));
				Any any = _orb.create_any();
				BidirectionalPolicyValueHelper.insert(any, BOTH.value);
				Policy[] policies = new Policy[2];
				policies[0] = 
					_orb.create_policy(BIDIRECTIONAL_POLICY_TYPE.value, any);
				policies[1] = root_poa.create_implicit_activation_policy(
					ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
				_poa = root_poa.create_POA("bidirpoa", root_poa.the_POAManager(), 
					policies);
				_poa.the_POAManager().activate();
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de l'initialisation de l'ORB: "
					 + exception);
				exception.printStackTrace();
				// On arrête là. On ne peut pas continuer
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_InitializationFailed",
					exception.getMessage(), exception);
			}
			final ORB orb = _orb;
			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					Trace a_trace_debug =
						TraceAPI.declareTraceDebug("Console");
				    a_trace_debug.writeTrace("Démarrage de l'ORB");
					orb.run();
				    a_trace_debug.writeTrace("ORB arrêté, destruction");
					orb.destroy();
				}
			});
			t.start();
		}
		// Ensuite, il faut récupérer les paramètres du Portail
		String configuration = System.getProperty("Profile.Configuration");
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(configuration, ":");
		portal_host = tokenizer.getToken(0);
		portal_port = Integer.valueOf(tokenizer.getToken(1)).intValue();

		// Maintenant, on ouvre une connexion TCP/IP avec le processus Portail
		try
		{
		    socket = new Socket(portal_host, portal_port);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
			    "Erreur lors de l'ouverture de la connexion TCP/IP: " +
				exception);
			// On en peut pas continuer
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ConnectionFailed",
				exception.getMessage(), exception);
		}
		// On va lire les informations sur la socket
		try
		{
			InputStreamReader reader =
			    new InputStreamReader(socket.getInputStream());
			BufferedReader buffer = new BufferedReader(reader);
			interface_ior = buffer.readLine();
			trace_io.writeTrace("Lu sur la socket: " + interface_ior);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
			    "Erreur lors de la lecture des information sur la socket: " +
				exception);
			// On ferme la socket
			try {
				socket.close();
			}
			catch(Exception e) {
				// On ne fait rien
			}
			// On en peut pas continuer
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ConnectionFailed",
				exception.getMessage(), exception);
		}
		// On ferme la socket
		try {
			socket.close();
		}
		catch(Exception e) {
			// On ne fait rien
		}
		// Conversion de la chaîne en objet CORBA puis en interface
		// PortalInterface
		try
		{
			org.omg.CORBA.Object interface_object =
				_orb.string_to_object(interface_ior);
			portal_interface = PortalInterfaceHelper.narrow(interface_object);
			// On vérifie la disponibilité du processus Portail
			if(portal_interface._non_existent() == false)
			{
				trace_debug.writeTrace("Le Portail I-SIS est disponible");
			}
			else
			{
				trace_errors.writeTrace("Le Portail I-SIS n'est pas disponible");
				// On en peut pas continuer
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_PortalNotAvailable", null, null);
			}
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
			    "Erreur lors de conversion de la référence: " +
				exception);
			// On en peut pas continuer
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ConnectionFailed",
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
		return portal_interface;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette méthode statique permet de libérer les ressources allouées pendant
	* l'exécution de l'application. Elle libère la référence sur l'objet ORB
	* stocké statiquement afin de communiquer avec les autres processus du
	* système, ainsi que la référence sur l'objet POA.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IORFinder", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		if(_orb != null)
		{
			// Arrêt de l'ORB
			_orb.shutdown(true);
			_orb = null;
			_poa = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: servantToReference
	*
	* Description:
	* Cette méthode permet de convertir un objet de type Servant (une
	* implémentation d'une interface CORBA) en une référence CORBA. Elle fait
	* appel à la méthode servant_to_reference() de la classe POA. Cette
	* méthode effectue l'activation automatique de l'interface.
	*
	* Arguments:
	*  - Une référence sur un object Servant à convertir en référence CORBA.
	*
	* Retourne: Une référence sur un objet CORBA correspondant à la référence
	* CORBA du Servant.
	* ----------------------------------------------------------*/
	static public org.omg.CORBA.Object servantToReference(
		Servant servant
		)
	{
		org.omg.CORBA.Object object = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IORFinder", "servantToReference");

		trace_methods.beginningOfMethod();
		try
		{
		    object = _poa.servant_to_reference(servant);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la conversion en référence: " +
				exception);
		}
		trace_methods.endOfMethod();
		return object;
	}
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _orb
	*
	* Description:
	* Cet attribut statique contient une référence sur l'objet ORB devant être
	* créé afin d'être capable de communiquer avec les autres processus du
	* système.
	* Le stockage de cette référence est nécessaire pour:
	*  - être capable de transformer des chaînes de caractères en objets CORBA,
	*  - de libérer les ressources allouées par l'ORB de la machine virtuelle
	*    Java.
	* ----------------------------------------------------------*/
	private static ORB _orb = null;

	/*----------------------------------------------------------
	* Nom: _poa
	*
	* Description:
	* Cet attribut statique contient une référence sur l'objet POA permettant
	* l'activation (et la transmission) des références des interfaces
	* IsisEventsListenerInterface et ExecutionListenerInterface.
	* ----------------------------------------------------------*/
	private static POA _poa = null;
}