Notes de développement I-SIP

INSTALLATION

I-SIP est constitué d'un couple standard I-SIS 2.0.4 :

* Portail :
	1. Installation et configuration standard
	2. Copie de "V0\Portal\conf\IsisPortal_WIN32.ini" dans "Portal\product\conf"
	3. Configuration des variables dans IsisPortal_WIN32.ini (section "# config for ISIP")
	4. Création de l'arborescence :
		%ISIP_DATA%\log
		%ISIP_DATA%\def
		%ISIP_DATA%\pci
		%ISIP_DATA%\tab
	5. Création des partages Windows :
		%ISIP_EXPORT%
		%ISIP_DOC%
		
	
* Console :
	1. Installation et configuration standard, et connexion au portail I-SIP
	2. Application de la mise à jour "I-SIP_IKOS-install", puis redémarrer
	3. Application de la mise à jour "I-SIP_IKOS-update", puis redémarrer

Notes : les sources des packages de mises à jour Console se trouvent dans le dépôt : /trunk/I-SIS_update/


MISE A JOUR

En cas de modification de fichiers *.pl, éxecuter les scripts suivants sur le portail pour recréer les fichiers BAT associés :
	1. V0\script\bin\CMD_ENV.bat
	2. V0\script\bin\make_bat.pl

Par convention, si une mise à jour nécéssite des actions supplémentaires (ex: modification de schéma), les scripts de mise à jour sont déposés dans :
	V0\update\bin