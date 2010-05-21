
PORTALORB_CONFIG=${ISIS_PRODUCT}/conf/IsisPortalORB.config
UTILSORB_CONFIG=${ISIS_PRODUCT}/conf/IsisUtilsORB.config

# config for ISIP
ICleName=ISIP
ISIP_HOME=${CLES_HOME}/${ICleName}
ISIP_DATA=${CLES_HOME}/DATA
ISIP_EXPORT=//isip/export_TEST
ISIP_DOC=//isip/documentation
SMTP_HOST=smtp.sicf.fr
PERL_PATH=${ISIP_HOME}/V0/Core/bin:${ISIP_HOME}/V0/Portal/bin

# edit with care!
ISIP_LOG=${ISIP_DATA}/log
REM PATH=${ISIP_HOME}/V0/script/bin:${ISIP_HOME}/V0/Portal/bin:${ISIP_HOME}/V0/Core/bin:${ISIP_HOME}/V0/batch/bin:${PATH}
PATH=${ISIP_HOME}/V0/bat/bin:${PATH}
BV_DEFPATH=${ISIP_DATA}/def:${ISIP_HOME}/V0/Portal/def:${ISIP_HOME}/V0/Core/def:${BV_DEFPATH}
BV_TABPATH=${ISIP_DATA}/tab:${ISIP_HOME}/V0/Portal/tab:${ISIP_HOME}/V0/Core/tab:${BV_TABPATH}
BV_PCIPATH=${ISIP_DATA}/pci:${ISIP_HOME}/V0/Portal/pci:${ISIP_HOME}/V0/Core/pci:${BV_PCIPATH}

# commenter si utilisation de PAR
PERL5LIB=${PERL5LIB}:${ISIP_HOME}/V0/Core/lib

PERL5LIB=${PERL5LIB}:${PERL_PATH}
