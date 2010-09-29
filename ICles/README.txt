Notes de d�veloppement I-SIP

INSTALLATION

I-SIP est constitu� d'un couple standard I-SIS 2.0.4 :

* Portail :
	1. Installation et configuration standard
	2. Copie de "V0\Portal\conf\IsisPortal_WIN32.ini" dans "Portal\product\conf"
	3. Configuration des variables dans IsisPortal_WIN32.ini (section "# config for ISIP")
	4. Cr�ation de l'arborescence :
		%ISIP_DATA%\log
		%ISIP_DATA%\def
		%ISIP_DATA%\pci
		%ISIP_DATA%\tab
	5. Cr�ation des partages Windows :
		%ISIP_EXPORT%
		%ISIP_DOC%
		
	
* Console :
	1. Installation et configuration standard, et connexion au portail I-SIP
	2. Application de la mise � jour "I-SIP_IKOS-install", puis red�marrer
	3. Application de la mise � jour "I-SIP_IKOS-update", puis red�marrer

Notes : les sources des packages de mises � jour Console se trouvent dans le d�p�t : /trunk/I-SIS_update/


MISE A JOUR

En cas de modification de fichiers *.pl, �xecuter les scripts suivants sur le portail pour recr�er les fichiers BAT associ�s :
	1. V0\script\bin\CMD_ENV.bat
	2. V0\script\bin\make_bat.pl

Par convention, si une mise � jour n�c�ssite des actions suppl�mentaires (ex: modification de sch�ma), les scripts de mise � jour sont d�pos�s dans :
	V0\update\bin