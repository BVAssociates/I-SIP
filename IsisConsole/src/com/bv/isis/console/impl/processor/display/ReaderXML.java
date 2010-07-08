/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/ReaderXML.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe permet de lire des fichiers XML.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ReaderXML.java,v $
* Revision 1.1  2009/03/05 15:59:12  jy
* La classe abstraite ReaderXML permet d'abord de valider un fichier de XML
* de description des masques d'affichage avec un fichier de XSD qui s'est
* situ� dans le r�pertoire xsd du projet, puis de  lire le fichier XML si valid�.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports syst�me
//
import java.io.File;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//
//Imports du projet
//
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.corbacom.IsisTableDefinition;

/*----------------------------------------------------------
* Nom: ReaderXML
*
* Description:
* Cette classe abstraite se charge de lire des fichiers XML de description des
* masques d'affichage.
* Elle contient une m�thode statique reader(path) qui lit un fichier XML et 
* retourne un tableau de type MaskRule 
* correspondant aux diff�rents masques d�crits dans le fichier.
* ----------------------------------------------------------*/
public abstract class ReaderXML 
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: readXMLFile
	*
	* Description:
	* Cette m�thode statique est charg�e de lire un fichier XML de description 
	* des masques d'affichage, en utilisant DOM (Document Object Model,mod�le 
	* objet de document en fran�ais, permet de d�finir la structure d'un 
	* document sous forme d'une hi�rarchie d'objets) pour parcourir le fichier.
	* C'est via la m�thode parse() de la classe DocumentBuilder, 
	* - une instance de DocumentBuilder est cr��e par DocumentBuilderFactory, 
	* qui est un usine de l'API permettant d'activer les applications de 
	* production d'objets de DOM via un fichier XML -, qu'on construit un objet
	* DOM Document (Avant de cette m�thode on appele la m�thode statique 
	* validateXML() pour la validation de la structure du fichier XML avec un 
	* fichier XSD correspond.) .
	* Puis, on utilise la m�thode de Document getElementsByTagName() pour 
	* obtenir une list de neouds repr�sentants l'ensemble de masques (une masque
	* est un objet de MaskRule repr�sentant une r�gle d�crit dans le fichier XML).
	* Si un probl�me est d�tect� durant la lecture de fichier, l'exception 
	* InnerException doit �tre lev�e.
	* 
	* Arguments:
	* - path: Une chaine de caract�res repr�sent�e le chemin de fichier XML.
	* - isisTableDefinition: Une r�f�rence sur un objet de IsisTableDefinition
	* 	qui est initialis� dans la m�thode reloadData() de la classe 
	* 	DisplayProcessor.
	* 
	* Retourne: Un tableau de MaskRule.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static MaskRule[] readXMLFile(
		String path,
		IsisTableDefinition isisTableDefinition
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ReaderXML", "readXMLFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debugs = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		String condition = null;
		String color = null;
		String background_color = null;
		boolean is_reverted = false;
		boolean is_italic = false;
		String icon_name = null;
		String icon_field = null;
		Vector<MaskRule> mask_rules = new Vector();
	
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("path=" + path);
		trace_arguments.writeTrace("isisTableDefinition=" + 
			isisTableDefinition);
		try {   
			//on active les applications de production des objets de DOM.
	        DocumentBuilderFactory factory = 
	        	DocumentBuilderFactory.newInstance();  
	        //on constuit un objet de Document.
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        //on teste si un fichier XSD existe.
	        String xsd_directory = null;
	        String maskrule_xsd = null;
	        try {
				ConfigurationAPI configuration = new ConfigurationAPI();
				xsd_directory = configuration.getString("Console", 
					"XSDDirectory");
				maskrule_xsd = configuration.getString("MaskRule.XSD");
			}
			catch(Exception ex) {
				// On ne fait rien
			}
			String pathXSD = xsd_directory + File.separatorChar + maskrule_xsd;
			//on valide la structure de fichier XML avant de la lecture.
	        if(validateXML(path, pathXSD)) {
	        	Document doc = builder.parse(new File(path));
		        //on parcourt la structure du objet de Document.
		        NodeList mask = doc.getElementsByTagName("Rule"); 
		        for(int i=0; i<mask.getLength(); i++) {   
		            Element rule=(Element)mask.item(i);
		            MaskRule mask_rule = null;
	            	try{
			            //on v�rifie si la condition existe dans la masque.
			            if(rule.getElementsByTagName(
			            	"Condition").getLength() != 0) {
			            	//Cr�ation d'un objet MaskRule.
			            	condition = rule.getElementsByTagName(
			            		"Condition").item(0).getFirstChild(
			            			).getNodeValue();
			            	mask_rule = new MaskRule(condition, 
				            		isisTableDefinition);
		            		//Tester si la couleur de police existe dans le masque.
				            if(rule.getElementsByTagName(
				            	"Color").getLength() != 0) {
				            	try{
				            		color = rule.getElementsByTagName(
				            			"Color").item(0).getFirstChild(
				            				).getNodeValue();
				            		mask_rule.setColor(color);
				            	}
				            	catch(Exception ex){
				            		mask_rule.setColor(null);
				            	}
				            }
				            //Tester si la couleur du fond existe dans le masque.
				            if(rule.getElementsByTagName(
				            	"BackgroundColor").getLength() != 0) {
				            	try{
				            		background_color = rule.getElementsByTagName(
				            			"BackgroundColor").item(0).getFirstChild(
				            				).getNodeValue();
				            		mask_rule.setBackgroundColor(background_color);
				            	}
				            	catch(Exception ex) {
				            		mask_rule.setBackgroundColor(null);
				            	}
				            }
				            //Tester si la valeur pour _isReverted(voire la 
				            // d�finition dans MaskRule) existe dans le masque.
				            if(rule.getElementsByTagName(
				            	"Revert").getLength() !=  0) {
				            	try{
				            		is_reverted = Boolean.parseBoolean(
				            			rule.getElementsByTagName("Revert").item(
				            				0).getFirstChild().getNodeValue());
				            		mask_rule.setReverted(is_reverted);
				            	}
				            	catch(Exception ex) {
				            		mask_rule.setReverted(false);
				            	}
				            }
				            //Tester si la valeur pour _isItalic(voire la d�finition
				            // dans MaskRule) existe dans le masque.
				            if(rule.getElementsByTagName("Italic").getLength() 
				            	!= 0) {
				            	try{
				            		is_italic = Boolean.parseBoolean(
				            			rule.getElementsByTagName("Italic").item(
				            				0).getFirstChild().getNodeValue());
				            		mask_rule.setItalic(is_italic);
				            	}
				            	catch(Exception ex) {
				            		mask_rule.setItalic(false);
				            	}
				            }
				            //Tester s'il existe un Icon dans le masque.
				            if(rule.getElementsByTagName(
				            	"Icon").getLength() != 0) {
				            	NodeList icons = rule.getElementsByTagName(
				            		"Icon");
				            	Element icon = (Element) icons.item(0);
				            	try{
				            		icon_name = icon.getElementsByTagName(
				            			"Name").item(0).getFirstChild(
				            				).getNodeValue();
				            		icon_field = icon.getElementsByTagName(
				            			"Field").item(0).getFirstChild(
				            				).getNodeValue();
				            		mask_rule.setIcon(icon_name, icon_field);
				            	}
				            	catch(Exception ex) {
				            		mask_rule.setIcon(null, null);
				            	}
				            }
				            //S'il le masque est vide.
				            if(rule.getElementsByTagName(
				            		"Icon").getLength() == 0 && 
				            	rule.getElementsByTagName(
				            		"Italic").getLength() == 0 && 
				            	rule.getElementsByTagName(
				            		"Revert").getLength() ==  0 && 
				            	rule.getElementsByTagName(
				            		"BackgroundColor").getLength() == 0 && 
				            	rule.getElementsByTagName(
				            		"Color").getLength() == 0) {
				            	trace_errors.writeTrace("La r�gle est vide.");
				            	trace_methods.endOfMethod();
			            		throw new InnerException("&ERR_EmptyRule", 
			            			null, null);
				            }          
				        } 
			            //S'il n'y a pas de condition dans le masque.
			            else {
			            	trace_errors.writeTrace(
			            		"Il n'y a pas de condition dans la r�gle.");
			            	trace_methods.endOfMethod();
			            	throw new InnerException("&ERR_NoConditionInRule", 
			            		null, null);
			            }
			            mask_rules.add(mask_rule);
	            	}
		            catch(InnerException e) {
		            	trace_errors.writeTrace("Condition non cr��e : " + 
		            		e.getMessage());
	            	}
		        }
	        }
	     }
		catch(Exception ex){
			trace_debugs.writeTrace("Fichier XML non trouv� : " + 
				ex.getMessage());
	    	trace_methods.endOfMethod();
	    	//Probl�me de charger le fichier XML.
	    	throw new InnerException("&ERR_FailOfLoadXML", ex.getMessage(), ex);
	     }
	     if(mask_rules.size() == 0) {
	    	 trace_methods.endOfMethod();
	    	 //S'il n'y a pas de masque dans le fichier XML.
	    	 throw new InnerException("&ERR_NoRuleInXML", null, null);
	     }
	     trace_methods.endOfMethod();
	     return (MaskRule[])mask_rules.toArray(new MaskRule[mask_rules.size()]);
	}
	
	/*----------------------------------------------------------
	* Nom: validateXML
	*
	* Description:
	* Cette m�thode statique est charg�e de valider un fichier XML de 
	* description des masques d'affichage avec un fichier de XSD, en utilisant 
	* les classes dans le paquet javax.xml.validation.
	* On cr�e d'abord une instance de ScemaFactory pour activer les appications
	* de cr�ation de Schema. (Ici on a XMLConstants.W3C_XML_SCHEMA_NS_URI comme
	* argument, car on utilise les schemas de XML de W3C.)
	* Puis la cr�ation d'une instance de Schema permet de instancier la classe 
	* Validator, c'est la derni�re qui a une m�thode validate() pour la 
	* v�rification.
	* (La utilisation de l'interface ErrorHandler permet de personnaliser 
	* comment les erreurs sont manipul�es.)
	* 
	* Si un probl�me est d�tect� durant la validation, l'exception
	* InnerException doit �tre lev�e.
	* 
	* Arguments:
	* - pathXML: Une chaine de caract�res repr�sent�e le chemin de fichier XML.
	* - pathXSD: Une chaine de caract�res repr�sent�e le chemin de fichier XSD.
	* 
	* Retourne: True si le fichier XMl est v�rif�, false sinon.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static boolean validateXML(
		String pathXML, 
		String pathXSD
		) 
		throws 
			InnerException 
	{   
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ReaderXML", "validateXML");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("pathXML=" + pathXML);
		trace_arguments.writeTrace("pathXSD=" + pathXSD);
        try { 
            //On active les applications.
            SchemaFactory factory = SchemaFactory.newInstance(
            	XMLConstants.W3C_XML_SCHEMA_NS_URI); 
            //Assurer qu'il y a au plus qu'une thread qui utilisee l'objet de 
            // SchemaFactory.
            synchronized (factory) {
            	//Cr�er un schema.
            	Schema schemaFile = factory.newSchema(new File(pathXSD));
            	//Cr�er un objet de Validator.
                Validator validator = schemaFile.newValidator();
                //On peut personnaliser les messages des erreurs si besoins.
                //ErrorHandler mySchemaErrorHandler = new MySchemaErrorHandler();
                //validator.setErrorHandler(mySchemaErrorHandler);
                validator.validate(new StreamSource(new File(pathXML))); 
            }
            trace_methods.endOfMethod();
            return true;   
        } catch (Exception e) {
        	trace_methods.endOfMethod();
            throw new InnerException("&ERR_FailInCheck", e.getMessage(), e); 
        }   
    }   

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}

