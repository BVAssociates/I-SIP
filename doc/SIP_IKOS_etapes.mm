<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1222942171837" HGAP="35" ID="ID_1430043560" LINK="SIP_IKOS_.mm" MODIFIED="1232639917886" VSHIFT="45">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Etapes
    </p>
  </body>
</html></richcontent>
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<node COLOR="#0033ff" CREATED="1222942183618" FOLDED="true" ID="ID_674890175" MODIFIED="1232639520418" POSITION="left" TEXT="Choix techniques">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1223018765140" ID="ID_810845374" LINK="SIP_IKOS_stockage.mm" MODIFIED="1232639520418" TEXT="Sqlite">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1223649048260" ID="ID_1050739387" MODIFIED="1228752396038" TEXT="Utilisation Sqlite">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1222944191398" ID="ID_1946678977" MODIFIED="1228752396038" TEXT="Gestion du LOCK">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#990000" CREATED="1223649054166" ID="ID_762944450" MODIFIED="1228752396038" TEXT="Possibilit&#xe9; changer">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1223019634154" ID="ID_679775720" LINK="C:\Documents%20and%20Settings\vb\Mes%20documents\dossiers\ICF\rapport\SIP_IKOS_Itools.mm" MODIFIED="1232639520418" TEXT="SIP_IKOS_Itools.mm">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1223649086306" ID="ID_159816354" MODIFIED="1228752396038" TEXT="Interface Unifi&#xe9;e d&apos;Acc&#xe8;s aux Donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1223649192056" ID="ID_341249203" MODIFIED="1228752396053" TEXT="Creation backend par h&#xe9;ritage">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1234424735424" ID="ID_1287471318" LINK="SIP_IKOS_problemes.mm" MODIFIED="1234424735440" POSITION="left" TEXT="Bugs/probl&#xe8;mes">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
</node>
<node COLOR="#0033ff" CREATED="1222944375705" ID="ID_696898736" MODIFIED="1232639520402" POSITION="left" TEXT="Developpement">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1232707682370" FOLDED="true" ID="ID_1771195327" MODIFIED="1234781348096" TEXT="Exploration">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1232707685495" ID="ID_1811604659" MODIFIED="1233320416281" TEXT="certaines valeur de champ sont des noms de programmes">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1232707710620" ID="ID_1478013745" MODIFIED="1232707862712" TEXT="programme -&gt; vue -&gt; table">
<icon BUILTIN="messagebox_warning"/>
</node>
<node COLOR="#111111" CREATED="1233320428890" ID="ID_1439654871" MODIFIED="1233320444906" TEXT="seulement sur les crit&#xe8;res  d&apos;exploitation de type &quot;TAB&quot;"/>
<node COLOR="#111111" CREATED="1232707863744" ID="ID_781218865" MODIFIED="1232707892133" TEXT="explorer la table correspondante"/>
</node>
<node COLOR="#990000" CREATED="1232095664180" ID="Freemind_Link_284547311" MODIFIED="1234520295880" TEXT="remont&#xe9;e info tables li&#xe9;es">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="clanbomber"/>
<icon BUILTIN="prepare"/>
<node COLOR="#111111" CREATED="1233223353825" ID="ID_684112259" MODIFIED="1234433739316" TEXT="utilisation d&apos;une table FLAG">
<icon BUILTIN="idea"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1233223361872" ID="ID_367656426" MODIFIED="1234176393477" TEXT="TABLE_NAME | TABLE_KEY | DIRTY"/>
<node COLOR="#111111" CREATED="1233764763433" ID="ID_918769673" MODIFIED="1234176362332" TEXT="pour chaque noeud &quot;sale&quot; (modifi&#xe9; ou different...), chercher les autres table/clef impliqu&#xe9;s">
<icon BUILTIN="wizard"/>
</node>
</node>
<node COLOR="#111111" CREATED="1233829829449" ID="ID_1342204930" MODIFIED="1233829861136" TEXT="contraintes">
<icon BUILTIN="clanbomber"/>
<node COLOR="#111111" CREATED="1233829862355" ID="ID_1887687082" MODIFIED="1233829871449" TEXT="&#xea;tre au maximum &quot;temps r&#xe9;el&quot;">
<node COLOR="#111111" CREATED="1233829871996" ID="ID_1122640329" MODIFIED="1234176302219" TEXT="dans I-SIS, utilisation des informations des noeud parcourus"/>
<node COLOR="#111111" CREATED="1233829930871" ID="ID_1689139566" MODIFIED="1234176289740" TEXT="Apr&#xe8;s chaque collecte, utilisation des informations de DIFF"/>
</node>
<node COLOR="#111111" CREATED="1233829893214" ID="ID_191064990" MODIFIED="1233829914996" TEXT="pouvoir reconstruire toute la table &#xe0; tout moment">
<node COLOR="#111111" CREATED="1233829918277" ID="ID_267776779" MODIFIED="1233829928621" TEXT="en cas d&apos;erreur lors de la collecte"/>
<node COLOR="#111111" CREATED="1233829968714" ID="ID_1475745236" MODIFIED="1233829978480" TEXT="en cas d&apos;erreur du processeur"/>
</node>
<node COLOR="#111111" CREATED="1233829979949" ID="ID_1784312466" MODIFIED="1234433752832" TEXT="algo adaptable">
<icon BUILTIN="button_cancel"/>
<node COLOR="#111111" CREATED="1233830007964" FOLDED="true" ID="ID_1989701949" MODIFIED="1234433874053" TEXT="comparaison">
<node COLOR="#111111" CREATED="1233830013152" ID="ID_1172067318" MODIFIED="1233830024730" TEXT="ajout notion histo (timestamp)"/>
<node COLOR="#111111" CREATED="1233830050652" ID="ID_1363651798" MODIFIED="1233830054902" TEXT="pas de suppression"/>
</node>
<node COLOR="#111111" CREATED="1233830027136" FOLDED="true" ID="ID_956280369" MODIFIED="1234433874709" TEXT="commentaire">
<node COLOR="#111111" CREATED="1233830031089" ID="ID_1980799411" MODIFIED="1233830037136" TEXT="pas d&apos;histo"/>
<node COLOR="#111111" CREATED="1233830040121" ID="ID_1998303324" MODIFIED="1233830048808" TEXT="ajout/suppression"/>
</node>
</node>
</node>
<node COLOR="#111111" CREATED="1234433813567" ID="ID_1933116799" MODIFIED="1234433818489" TEXT="comparaison">
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1234434017134" ID="ID_1535564090" MODIFIED="1234434038306" TEXT="une comparation = ENV@DATE  -&gt; ENV2@DATE2"/>
<node COLOR="#111111" CREATED="1234433822849" ID="ID_1732283495" MODIFIED="1234433829989" TEXT="on peut pas calculer tous les cas">
<node COLOR="#111111" CREATED="1234433955086" ID="ID_1949871713" MODIFIED="1234434015665" TEXT="mise &#xe0; jour table COMP_CACHE si n&apos;existe pas pour cette comp"/>
</node>
<node COLOR="#111111" CREATED="1234433970820" ID="ID_263661436" MODIFIED="1234433996774" TEXT="consultation table COMP_CACHE">
<node COLOR="#111111" CREATED="1234434042165" ID="ID_908876925" MODIFIED="1234434054259" TEXT="cache toujours valable"/>
<node COLOR="#111111" CREATED="1234434054806" ID="ID_1079822293" MODIFIED="1234434089463" TEXT="jamais purg&#xe9;">
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1233827820791" ID="ID_1615125592" MODIFIED="1233830249761" TEXT="ameliorer algo">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="idea"/>
<node COLOR="#111111" CREATED="1233827856651" ID="ID_1412188571" MODIFIED="1233837034421" TEXT="si T1 et T2 sur m&#xea;me table HISTO">
<node COLOR="#111111" CREATED="1233827837932" ID="ID_1514939759" MODIFIED="1233828033418" TEXT="au lieu de faire  T = T2 - T1"/>
<node COLOR="#111111" CREATED="1233827870698" ID="ID_423465097" MODIFIED="1233837021468" TEXT="calculer  T = T1 - diff1+diff2+diff3+..."/>
</node>
</node>
<node COLOR="#990000" CREATED="1232011327440" FOLDED="true" ID="Freemind_Link_804043892" MODIFIED="1234520291365" TEXT="base documentaire">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="help"/>
<icon BUILTIN="stop"/>
<node COLOR="#111111" CREATED="1232011339393" ID="Freemind_Link_2805040" MODIFIED="1232011345331" TEXT="Sqlite">
<node COLOR="#111111" CREATED="1232011389519" ID="Freemind_Link_302489924" MODIFIED="1232011402394" TEXT="une par table">
<node COLOR="#111111" CREATED="1232011407301" ID="Freemind_Link_200203510" MODIFIED="1232011478942" TEXT="IKOS_DOC_NATPROP.sqlite"/>
<node COLOR="#111111" CREATED="1232011486020" ID="Freemind_Link_83956550" MODIFIED="1232011487005" TEXT="..."/>
</node>
</node>
<node COLOR="#111111" CREATED="1232099656159" ID="Freemind_Link_1355001456" MODIFIED="1232099675268" TEXT="Mysql">
<icon BUILTIN="idea"/>
<node COLOR="#111111" CREATED="1232099661409" ID="Freemind_Link_981522471" MODIFIED="1232099664299" TEXT="base de donn&#xe9;e unique"/>
<node COLOR="#111111" CREATED="1232099664534" ID="Freemind_Link_573171843" MODIFIED="1232099693112" TEXT="joiture possible!"/>
</node>
<node COLOR="#111111" CREATED="1232011352659" ID="Freemind_Link_1935158697" MODIFIED="1232011385910" TEXT="TABLE_NAME;FIELD_NAME;TABLE_KEY;documentation"/>
<node COLOR="#111111" CREATED="1232099030764" ID="Freemind_Link_410456388" MODIFIED="1232099037952" TEXT="synchro avec tous les environnements">
<node COLOR="#111111" CREATED="1232099039311" ID="Freemind_Link_1473740824" MODIFIED="1232099043467" TEXT="ligne ajout&#xe9;e"/>
<node COLOR="#111111" CREATED="1232099043733" ID="Freemind_Link_1052665860" MODIFIED="1232099046952" TEXT="ligne supprim&#xe9;e"/>
</node>
<node COLOR="#111111" CREATED="1232985047540" ID="ID_691925866" MODIFIED="1233138628740" TEXT="Attention, doc differente suivant version/environnement">
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1228754623572" ID="_" MODIFIED="1232639520418" TEXT="collecte">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1228754627018" ID="Freemind_Link_352837347" MODIFIED="1228815000478" TEXT="inclure/exclure certaines lignes">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1228754645625" ID="Freemind_Link_1777319399" MODIFIED="1228815000494" TEXT="surveillance partielle de la table">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1232985375823" ID="ID_1687062989" MODIFIED="1232985399636" TEXT="utilisation fausse table IKOS par ITools">
<node COLOR="#111111" CREATED="1232985401292" ID="ID_1183712387" MODIFIED="1232985408011" TEXT="utilisation F_KEY"/>
<node COLOR="#111111" CREATED="1232985408495" ID="ID_1239192838" MODIFIED="1232985420245" TEXT="racine de l&apos;exploration"/>
</node>
<node COLOR="#111111" CREATED="1232011293815" ID="Freemind_Link_163457832" MODIFIED="1232011308846" TEXT="table administrable">
<node COLOR="#111111" CREATED="1232011309799" ID="Freemind_Link_101083818" MODIFIED="1232011323643" TEXT="TABLE, CLEF"/>
<node COLOR="#111111" CREATED="1232985487542" ID="ID_1716679056" MODIFIED="1232985521464" TEXT="construction requete IKOS">
<node COLOR="#111111" CREATED="1232985494183" ID="ID_577768476" MODIFIED="1232985513074" TEXT="Select .... where FIELD1 IN (XXX,YYY,ZZZ,...)"/>
</node>
</node>
<node COLOR="#111111" CREATED="1232095478570" ID="Freemind_Link_1577788265" MODIFIED="1232095538836" TEXT="Gestion table li&#xe9;e">
<icon BUILTIN="clanbomber"/>
<node COLOR="#111111" CREATED="1232095493398" ID="Freemind_Link_1284066891" MODIFIED="1232095519617" TEXT="collecte partielle sur FKEY"/>
<node COLOR="#111111" CREATED="1232095528976" ID="Freemind_Link_1162519178" MODIFIED="1232095532789" TEXT="gerer les dependances"/>
</node>
</node>
<node COLOR="#990000" CREATED="1229359291065" ID="Freemind_Link_1513764654" MODIFIED="1229359300768" TEXT="gerer l&apos;ajout/suppression de colonne">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1231334336847" ID="Freemind_Link_65583872" MODIFIED="1231334366379" TEXT="Modifier TABLE_INFO"/>
<node COLOR="#111111" CREATED="1231334370911" ID="Freemind_Link_404655222" MODIFIED="1233212949946" TEXT="permanent, m&#xea;me si comparaison dans le temps"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1223652737533" ID="ID_1487120510" MODIFIED="1233746129198" TEXT="Baseline">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1233224129936" ID="ID_107129492" MODIFIED="1233746129213" TEXT="TABLE;DATE_COLLECTE;IS_BASELINE;DESCRIPTION">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1233224168660" ID="ID_1776960995" MODIFIED="1233746129213" TEXT="date de collecte immediatement inf&#xe9;rieure ou &#xe9;gale">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="help"/>
</node>
<node COLOR="#990000" CREATED="1233224157741" ID="ID_985623057" MODIFIED="1233746129213" TEXT="choix parmis les date de collectes">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="help"/>
</node>
<node COLOR="#990000" CREATED="1225462427775" ID="ID_1044662442" MODIFIED="1234269653170" TEXT="sauvegarde de &quot;date jalon&quot;">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#990000" CREATED="1231332539434" ID="Freemind_Link_85326917" MODIFIED="1233746129213" TEXT="sur un environnement complet">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1234174664159" ID="ID_119348845" MODIFIED="1234174677022" TEXT="commentaires fig&#xe9;s">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1234174679838" ID="ID_1010126165" MODIFIED="1234174688945" TEXT="eventuellement editable"/>
<node COLOR="#111111" CREATED="1234174689352" ID="ID_1569509818" MODIFIED="1234174693092" TEXT="propre &#xe0; baseline"/>
<node COLOR="#111111" CREATED="1234174694437" ID="ID_917780460" MODIFIED="1234174703231" TEXT="&quot;vrai&quot; snapshot"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1232617696631" ID="Freemind_Link_997562137" MODIFIED="1234174789690" TEXT="Saisie">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#990000" CREATED="1232617699365" ID="Freemind_Link_965659872" MODIFIED="1234174779388" TEXT="saisie clef = saisie ligne">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1232617710553" ID="Freemind_Link_1988306063" MODIFIED="1232617726459" TEXT="grouper les multi clefs primaires"/>
<node COLOR="#111111" CREATED="1234272949598" ID="ID_1579839246" MODIFIED="1234272961192" TEXT="&quot;administrate&quot; table">
<node COLOR="#111111" CREATED="1234272980020" ID="ID_721409823" MODIFIED="1234272998379" TEXT="modifier =&gt; mettre &#xe0; jour commmentaire"/>
</node>
<node COLOR="#111111" CREATED="1232617735553" ID="Freemind_Link_1866043766" MODIFIED="1232617775007" TEXT="mettre la saisie de la clef directement sur le noeud Table">
<icon BUILTIN="help"/>
</node>
</node>
<node COLOR="#990000" CREATED="1234175027319" ID="ID_734544120" MODIFIED="1234175031114" TEXT="champ commentaire">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1234175031768" ID="ID_1397569904" MODIFIED="1234175035175" TEXT="liste deroulante">
<node COLOR="#111111" CREATED="1234175041958" ID="ID_858488295" MODIFIED="1234175044695" TEXT="liste de projet"/>
<node COLOR="#111111" CREATED="1234175045053" ID="ID_568316398" MODIFIED="1234175049876" TEXT="list de projet d&#xe9;j&#xe0; entr&#xe9;s"/>
</node>
<node COLOR="#111111" CREATED="1234175035626" ID="ID_1396925117" MODIFIED="1234259539786" TEXT="champ multiligne">
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1234262552755" ID="ID_326767738" MODIFIED="1234780590580" TEXT="probleme avec les caractere sp&#xe9;ciaux">
<icon BUILTIN="messagebox_warning"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#111111" CREATED="1234175050467" ID="ID_1003302787" MODIFIED="1234175054885" TEXT="purg&#xe9;s &#xe0; chaque baseline"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1234531641598" ID="ID_96573361" MODIFIED="1234531646660" TEXT="Rapport">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="go"/>
<node COLOR="#990000" CREATED="1234531657442" ID="ID_593138181" MODIFIED="1234531659755" TEXT="tableau de bord">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1234531660661" ID="ID_1769898440" MODIFIED="1234531663536" TEXT="indicateurs"/>
</node>
<node COLOR="#990000" CREATED="1234531664739" ID="ID_35411149" MODIFIED="1234531677927" TEXT="rapport de mise en production">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1234773953492" ID="ID_161474513" MODIFIED="1234773954804" TEXT="format">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1234773957726" ID="ID_700165275" MODIFIED="1234773961351" TEXT="HTML">
<icon BUILTIN="stop"/>
</node>
<node COLOR="#111111" CREATED="1234773962273" ID="ID_1503456068" MODIFIED="1234773973913" TEXT="CSV">
<node COLOR="#111111" CREATED="1234773965320" ID="ID_759692006" MODIFIED="1234773968069" TEXT="export Excel"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1225123252002" ID="ID_798828344" MODIFIED="1232639520418" POSITION="left" TEXT="Tests">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<icon BUILTIN="clanbomber"/>
<node COLOR="#00b439" CREATED="1225123255283" ID="ID_695013339" MODIFIED="1233224621796" TEXT="multiple PRIMARY KEY">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1232531830280" ID="Freemind_Link_1482942655" MODIFIED="1233224624530" TEXT="multiple FOREIGN KEY">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="button_ok"/>
</node>
<node COLOR="#00b439" CREATED="1232700536363" ID="ID_1666359468" MODIFIED="1232700543504" TEXT="verifier que les donn&#xe9;es sont bien celles attendues">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1226055791376" ID="ID_186767283" MODIFIED="1233224641450" POSITION="left" TEXT="Documentation">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<icon BUILTIN="attach"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#00b439" CREATED="1226055794454" ID="ID_1968266380" MODIFIED="1232639520418" TEXT="Technique">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1226055796376" ID="ID_1583754665" MODIFIED="1232639520418" TEXT="Utilisateur">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1223648918307" ID="ID_985870356" MODIFIED="1234773906759" POSITION="right" TEXT="Termin&#xe9;">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<arrowlink DESTINATION="ID_985870356" ENDARROW="Default" ENDINCLINATION="0;0;" ID="Arrow_ID_1227032256" STARTARROW="None" STARTINCLINATION="0;0;"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1223648943354" FOLDED="true" ID="ID_961439069" MODIFIED="1233752327402" TEXT="backend">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1223540074018" ID="ID_1283329046" LINK="SIP_IKOS_Perl.mm" MODIFIED="1228752396053" TEXT="Module d&apos;acc&#xe8;s uniforme aux donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1223540081376" ID="ID_841926956" MODIFIED="1228752396053" TEXT="Utilisation Perl Object Oriented">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#990000" CREATED="1223278057447" ID="ID_926910856" MODIFIED="1228752396053" TEXT="table virtuelle depuis historique">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1223278920585" ID="ID_117462760" MODIFIED="1228752396053" TEXT="Afficher une table">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1223018669468" ID="ID_1072211482" MODIFIED="1228752396053" TEXT="acc&#xe8;s aux donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1225461628978" ID="ID_1050016152" MODIFIED="1228752395975" TEXT="Entre 2 tables de meme nom">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1225461635540" ID="ID_557177834" MODIFIED="1228752395975" TEXT="table1">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1225461598025" ID="ID_98690798" MODIFIED="1228752395975" TEXT="Environnement">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1225461603790" ID="ID_438702851" MODIFIED="1228752395975" TEXT="date">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#111111" CREATED="1225461637681" ID="ID_1867876163" MODIFIED="1228752395991" TEXT="table2">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1225461598025" ID="ID_481232720" MODIFIED="1228752395991" TEXT="Environnement">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1225461603790" ID="ID_1363689955" MODIFIED="1228752395991" TEXT="date">
<edge WIDTH="thin"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1223648962213" FOLDED="true" ID="ID_1140168810" MODIFIED="1234781252348" TEXT="I-SIS">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1226074928046" ID="ID_1346159912" MODIFIED="1228752396069" TEXT="Collecte">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1223651398963" ID="ID_869275006" MODIFIED="1228752396069" TEXT="construire le r&#xe9;f&#xe9;rentiel">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1223278038317" ID="ID_1561458750" MODIFIED="1228752396069" TEXT="Mettre &#xe0; jour le r&#xe9;f&#xe9;rentiel">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1228815159367" ID="Freemind_Link_614148264" MODIFIED="1232544890275" TEXT="Gerer les fichiers XML">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1232011246424" ID="Freemind_Link_428183319" MODIFIED="1232011253127" TEXT="Module Perl XML::Simple"/>
<node COLOR="#111111" CREATED="1232011256564" ID="Freemind_Link_868418067" MODIFIED="1232011267002" TEXT="class ITable::XML"/>
</node>
</node>
<node COLOR="#990000" CREATED="1222944098869" ID="ID_1202313044" MODIFIED="1234781208271" TEXT="Exploration">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1223646276758" ID="ID_847158975" MODIFIED="1228752396084" TEXT="Voir historique d&apos;un champ">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1223646258789" ID="ID_1472694252" MODIFIED="1228752396084" TEXT="Afficher table depuis copie locale">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#111111" CREATED="1223646281821" ID="ID_1583046772" MODIFIED="1228752396084" TEXT="editer information historique champ">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1225098884370" ID="ID_190538378" MODIFIED="1228752396084" TEXT="Administrate">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1227517625312" ID="ID_1145132861" MODIFIED="1228752396084" TEXT="ReplaceAndExec">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1227517634891" ID="ID_1148499658" MODIFIED="1228752396084" TEXT="Implemente script pour Sqlite">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1227517643829" ID="ID_1627451557" MODIFIED="1228752396084" TEXT="Redirige vers script standard pour table Itools">
<edge WIDTH="thin"/>
</node>
</node>
</node>
<node COLOR="#111111" CREATED="1225462055228" ID="ID_1289666093" MODIFIED="1228752396084" TEXT="Tracage des mise &#xe0; jour">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1225462062915" ID="ID_1518469392" MODIFIED="1228752396084" TEXT="user">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1225462082837" ID="ID_1606358100" MODIFIED="1228752396100" TEXT="date">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#111111" CREATED="1232532268504" ID="Freemind_Link_1514189164" MODIFIED="1232706410237" TEXT="exploration clef etrangere">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1232532275363" FOLDED="true" ID="Freemind_Link_103414476" MODIFIED="1232639339902" TEXT="Select table where condition">
<icon BUILTIN="button_cancel"/>
<node COLOR="#111111" CREATED="1232532282192" ID="Freemind_Link_673484073" MODIFIED="1232532296036" TEXT="script Perl ramene toute la table"/>
<node COLOR="#111111" CREATED="1232532296270" ID="Freemind_Link_1073497247" MODIFIED="1232532300176" TEXT="Select fait le tri"/>
<node COLOR="#111111" CREATED="1232532300661" ID="Freemind_Link_1701425865" MODIFIED="1232532314255" TEXT="PAS efficace">
<icon BUILTIN="clanbomber"/>
</node>
</node>
<node COLOR="#111111" CREATED="1232532329723" FOLDED="true" ID="Freemind_Link_595925172" MODIFIED="1234781239739" TEXT="I-SIS d&#xe9;duit la condition">
<node COLOR="#111111" CREATED="1232532347458" ID="Freemind_Link_886151660" MODIFIED="1232532352161" TEXT="A partir de la table explor&#xe9;e"/>
<node COLOR="#111111" CREATED="1232532352427" ID="Freemind_Link_264827773" MODIFIED="1232532361193" TEXT="A partir des F_KEY"/>
<node COLOR="#111111" CREATED="1232545109059" ID="Freemind_Link_567377563" MODIFIED="1232639322871" TEXT="EFFICACE mais erreur possible&#xa;car on ne connait pas le nom de la table parente&#xa;Donc on teste toute les clefs"/>
<node COLOR="#111111" CREATED="1232706423424" FOLDED="true" ID="ID_867103590" MODIFIED="1234781237427" TEXT="Attenton sur les tables de type Histo">
<node COLOR="#111111" CREATED="1232706433049" ID="ID_1262056554" MODIFIED="1232706482768" TEXT="le filtre query_condition n&apos;est pas&#xa;optimale en terme de perf">
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
</node>
<node COLOR="#111111" CREATED="1232984849694" ID="ID_221325329" MODIFIED="1232984856257" TEXT="Supprimer les noeuds Table inutiles"/>
<node COLOR="#111111" CREATED="1234433753816" ID="ID_574248097" MODIFIED="1234433772942" TEXT="remont&#xe9;e etat commentaire">
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1234433854537" ID="ID_1048401931" MODIFIED="1234433888866" TEXT="algo parcours d&apos;abre en profondeur">
<node COLOR="#111111" CREATED="1234433890584" ID="ID_1920636928" MODIFIED="1234433909225" TEXT="enfants en premier, puis noeuds p&#xe8;res"/>
</node>
<node COLOR="#111111" CREATED="1234433773926" ID="ID_1972486193" MODIFIED="1234433780035" TEXT="&#xe0; la collecte">
<node COLOR="#111111" CREATED="1234433781754" ID="ID_432207883" MODIFIED="1234433793583" TEXT="information des modifications"/>
</node>
<node COLOR="#111111" CREATED="1234433795989" ID="ID_1408898255" MODIFIED="1234433802989" TEXT="&#xe0; la saisie"/>
<node COLOR="#111111" CREATED="1234433806520" ID="ID_138289888" MODIFIED="1234433810255" TEXT="reconstruction complete"/>
</node>
<node COLOR="#111111" CREATED="1232639945918" ID="ID_476578029" MODIFIED="1234773916196" TEXT="recherche rapide">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1232639951324" ID="ID_1995364412" MODIFIED="1232639962465" TEXT="recherche sur un champ contenant ...">
<node COLOR="#111111" CREATED="1232974847765" ID="ID_1660515958" MODIFIED="1232974854359" TEXT="Menu &quot;Requete&quot;">
<node COLOR="#111111" CREATED="1232974855030" ID="ID_1387057353" MODIFIED="1232974865202" TEXT="Probl&#xe8;me : affiche une table en sortie"/>
</node>
<node COLOR="#111111" CREATED="1232974827062" ID="ID_1050841986" MODIFIED="1233130674507" TEXT="Pr&#xe9;processeur selection">
<node COLOR="#111111" CREATED="1233130683633" ID="ID_206847129" MODIFIED="1233130697133" TEXT="noeud item ROOT_TABLE d&#xe9;j&#xe0; surcharg&#xe9;"/>
<node COLOR="#111111" CREATED="1233130675804" ID="ID_600072299" MODIFIED="1233320374093" TEXT="pas de pr&#xe9;processeur sur noeud table">
<icon BUILTIN="stop-sign"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1225461592806" ID="ID_1791650320" MODIFIED="1232531713387" TEXT="comparaison">
<edge WIDTH="thin"/>
<cloud/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1228815016791" ID="Freemind_Link_748043249" MODIFIED="1232531713403" TEXT="Repr&#xe9;sentation diff en Explore">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1231146981682" ID="Freemind_Link_1315597071" MODIFIED="1231146986854" TEXT="Explore key">
<node COLOR="#111111" CREATED="1231146987338" ID="Freemind_Link_252585733" MODIFIED="1231146989995" TEXT="Explore field"/>
</node>
</node>
<node COLOR="#111111" CREATED="1232023390688" ID="Freemind_Link_658323170" MODIFIED="1232544875540" TEXT="Pr&#xe9;processeur">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1232023396829" ID="Freemind_Link_1826259817" MODIFIED="1232023403251" TEXT="Saisie pour comparaison"/>
</node>
</node>
<node COLOR="#990000" CREATED="1223624497656" ID="ID_1714867649" MODIFIED="1228752396100" TEXT="configurer I-SIS">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1223624518092" ID="ID_195430861" MODIFIED="1228752396100" TEXT="A partir de la table INFO_TABLE">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1223624505187" ID="ID_1689301323" MODIFIED="1228752396100" TEXT="Creation des DEF">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1223624511233" ID="ID_1976995722" MODIFIED="1228752396100" TEXT="Creation des PCI">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#111111" CREATED="1222944132805" ID="ID_1020522006" MODIFIED="1228752396100" TEXT="Scripts de g&#xe9;n&#xe9;ration">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1222944336644" ID="ID_243290744" MODIFIED="1228752396100" TEXT="Dynamique">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1222944342034" ID="ID_300204715" MODIFIED="1228752396100" TEXT="Statique">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1223652476113" FOLDED="true" ID="ID_201068058" MODIFIED="1234781206505" TEXT="Configuration">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1223652605299" ID="ID_970449716" MODIFIED="1228752395944" TEXT="Infos champs">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1226055385415" FOLDED="true" ID="ID_289071555" MODIFIED="1233752322964" TEXT="r&#xe8;gles d&apos;affichage">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1229673606907" ID="Freemind_Link_778072425" MODIFIED="1231334231780" TEXT="calcul icone du champ &#xe0; afficher">
<node COLOR="#111111" CREATED="1229674123070" ID="Freemind_Link_807760271" MODIFIED="1229674130304" TEXT="en fonction de DATE_COLLECTE"/>
<node COLOR="#111111" CREATED="1229673966916" ID="Freemind_Link_790596143" MODIFIED="1229673984556" TEXT="en fonction de la valeur de COMMENT"/>
<node COLOR="#111111" CREATED="1229673985056" ID="Freemind_Link_1972265361" MODIFIED="1229679311022" TEXT="en fonction de la valeur explicite de STATUS"/>
<node COLOR="#111111" CREATED="1229673992291" ID="Freemind_Link_349604229" MODIFIED="1229673997337" TEXT="en fonction du type"/>
<node COLOR="#111111" CREATED="1229674096914" ID="Freemind_Link_1786791447" MODIFIED="1229674101148" TEXT="exemples">
<node COLOR="#111111" CREATED="1229674070836" ID="Freemind_Link_598783791" MODIFIED="1229674079555" TEXT="COMMENT vide autoris&#xe9; pour un certain type"/>
<node COLOR="#111111" CREATED="1229674406975" ID="Freemind_Link_1919310103" MODIFIED="1231334251093" TEXT="si STATUS est valoris&#xe9;, force l&apos;icone &#xe0; STATUS"/>
<node COLOR="#111111" CREATED="1229673998353" ID="Freemind_Link_1047226187" MODIFIED="1231342689431" TEXT="l&apos;icone ne peut pas etre valide si COMMENT est vide"/>
</node>
</node>
<node COLOR="#111111" CREATED="1229673767829" ID="Freemind_Link_1099036328" MODIFIED="1231334241687" TEXT="calcul icone de difference &#xe0; afficher">
<node COLOR="#111111" CREATED="1229673805171" ID="Freemind_Link_1927062255" MODIFIED="1229673826608" TEXT="en fonction de la difference de valeur"/>
<node COLOR="#111111" CREATED="1229673826921" ID="Freemind_Link_705994708" MODIFIED="1229673829874" TEXT="en fonction du type"/>
<node COLOR="#111111" CREATED="1229674164335" ID="Freemind_Link_593200234" MODIFIED="1229674166179" TEXT="exemples">
<node COLOR="#111111" CREATED="1231334294423" ID="Freemind_Link_413117155" MODIFIED="1231334316768" TEXT="pour le type Fonctionnel, l&apos;icone est en fonction du STATUS"/>
<node COLOR="#111111" CREATED="1229674167023" ID="Freemind_Link_968737505" MODIFIED="1231334289157" TEXT="pour le type &quot;Administratif&quot;, l&apos;icone est toujours OK"/>
<node COLOR="#111111" CREATED="1231334267735" ID="Freemind_Link_1222244309" MODIFIED="1231334279532" TEXT="pour le type exclus, le champ n&apos;est pas affich&#xe9;"/>
</node>
</node>
<node COLOR="#111111" CREATED="1229351579274" ID="Freemind_Link_33500930" MODIFIED="1231332741864" TEXT="Class IsipRules">
<node COLOR="#111111" CREATED="1229351600274" ID="Freemind_Link_1561922820" MODIFIED="1232002327092" TEXT="get_field_icon">
<node COLOR="#111111" CREATED="1229351589227" ID="Freemind_Link_1440163433" MODIFIED="1229351670382" TEXT="commentaire+date collecte+valeur"/>
<node COLOR="#111111" CREATED="1229351647039" ID="Freemind_Link_1465535672" MODIFIED="1229351653008" TEXT="renvoie un status calcul&#xe9;"/>
</node>
<node COLOR="#111111" CREATED="1229351600274" ID="Freemind_Link_71848983" MODIFIED="1232002334047" TEXT="get_field_diff_icon">
<node COLOR="#111111" CREATED="1229351589227" ID="Freemind_Link_719095429" MODIFIED="1229351670382" TEXT="commentaire+date collecte+valeur"/>
<node COLOR="#111111" CREATED="1229351647039" ID="Freemind_Link_1790480929" MODIFIED="1229351653008" TEXT="renvoie un status calcul&#xe9;"/>
</node>
<node COLOR="#111111" CREATED="1229673704130" ID="Freemind_Link_90399578" MODIFIED="1232002337985" TEXT="get_type">
<node COLOR="#111111" CREATED="1229673709661" ID="Freemind_Link_703072855" MODIFIED="1229673712848" TEXT="champ"/>
<node COLOR="#111111" CREATED="1229673713535" ID="Freemind_Link_1736334859" MODIFIED="1229673717222" TEXT="renvoie le type du champ"/>
</node>
</node>
<node COLOR="#111111" CREATED="1229673920543" ID="Freemind_Link_1186944173" MODIFIED="1231341799084" TEXT="STATUS &lt;&gt; ICON">
<icon BUILTIN="idea"/>
</node>
</node>
<node COLOR="#990000" CREATED="1229071244562" ID="Freemind_Link_1706115275" MODIFIED="1234780833794" TEXT="type icone">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1229071259703" ID="Freemind_Link_1763216505" MODIFIED="1229071320798" TEXT="mode &quot;etat commentaire&quot;">
<node COLOR="#111111" CREATED="1229071935294" ID="Freemind_Link_630799528" MODIFIED="1229071942887" TEXT="affich&#xe9; en mode exploration"/>
<node COLOR="#111111" CREATED="1229071274469" ID="Freemind_Link_1983010707" MODIFIED="1229071791202" TEXT=" nouveau">
<icon BUILTIN="bookmark"/>
</node>
<node COLOR="#111111" CREATED="1229071279547" ID="Freemind_Link_51111029" MODIFIED="1229071831170" TEXT=" en cours">
<icon BUILTIN="idea"/>
</node>
<node COLOR="#111111" CREATED="1229071286688" ID="Freemind_Link_446510688" MODIFIED="1229071800420" TEXT=" valid&#xe9;">
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#111111" CREATED="1229071291235" ID="Freemind_Link_366814135" MODIFIED="1229071392501" TEXT="mode &quot;difference&quot;">
<node COLOR="#111111" CREATED="1229071882825" ID="Freemind_Link_527350420" MODIFIED="1229071900247" TEXT="affich&#xe9; lors de comparaison"/>
<node COLOR="#111111" CREATED="1229071900653" FOLDED="true" ID="Freemind_Link_597850572" MODIFIED="1229071932356" TEXT="affich&#xe9; lors d&apos;exploration temporelle">
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1229071985199" ID="Freemind_Link_1485663263" MODIFIED="1229071997121" TEXT="comparaison entre 2 dates"/>
</node>
<node COLOR="#111111" CREATED="1229071294969" ID="Freemind_Link_762565603" MODIFIED="1229071803530" TEXT="nouveau">
<icon BUILTIN="bookmark"/>
</node>
<node COLOR="#111111" CREATED="1229071400829" ID="Freemind_Link_425125764" MODIFIED="1229071807170" TEXT="supprim&#xe9;">
<icon BUILTIN="button_cancel"/>
</node>
<node COLOR="#111111" CREATED="1229071345110" ID="Freemind_Link_1249608122" MODIFIED="1229348045506" TEXT="modifi&#xe9;">
<icon BUILTIN="pencil"/>
</node>
<node COLOR="#111111" CREATED="1229071363407" ID="Freemind_Link_1023628724" MODIFIED="1229071816311" TEXT="non modifi&#xe9;">
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#111111" CREATED="1231341057368" ID="Freemind_Link_469578360" MODIFIED="1231341061024" TEXT="differencier les icones"/>
</node>
<node COLOR="#990000" CREATED="1234174740176" ID="ID_1277980299" MODIFIED="1234433712534" TEXT="recuperer description table dans SYSTAB">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1226074949046" FOLDED="true" ID="ID_1166585546" MODIFIED="1234780685000" TEXT="Collecte">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1226074975421" ID="ID_366790996" MODIFIED="1228752396006" TEXT="Mettre certains champs auto">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1226074985593" ID="ID_232801555" MODIFIED="1234433688534" TEXT="DATE_HISTO">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1226074952593" ID="ID_159434867" MODIFIED="1228752396006" TEXT="DATE_UPDATE">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1229529719776" ID="Freemind_Link_1470322029" MODIFIED="1229529724558" TEXT="ajout de ligne">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1229529724792" ID="Freemind_Link_98629024" MODIFIED="1229529727698" TEXT="suppression de ligne">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1229529744339" ID="Freemind_Link_1701870218" MODIFIED="1229529749714" TEXT="modification de ligne">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1229353344559" FOLDED="true" ID="Freemind_Link_1275485142" MODIFIED="1233320323733" TEXT="DATE_HISTO coh&#xe9;rent pendant tout le processus de collecte">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1229353404028" ID="Freemind_Link_452504700" MODIFIED="1233223288388" TEXT="1 date = 1 collecte">
<font NAME="SansSerif" SIZE="12"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1229353327216" ID="Freemind_Link_1941574420" MODIFIED="1233223249247" TEXT="Histo-&gt;set_timestamp_update(timestamp)">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1229353333591" ID="Freemind_Link_1824360316" MODIFIED="1233223253263" TEXT="insert_row()">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1233223253638" ID="ID_681695983" MODIFIED="1233223263982" TEXT="update_row()"/>
</node>
<node COLOR="#111111" CREATED="1230020015094" ID="Freemind_Link_478327558" MODIFIED="1233223288404" TEXT="1 collecte = 1 identifiant">
<font NAME="SansSerif" SIZE="12"/>
<icon BUILTIN="button_cancel"/>
<node COLOR="#111111" CREATED="1230020031531" ID="Freemind_Link_1152952196" MODIFIED="1230020038562" TEXT="1 identifiant= 1 date"/>
<node COLOR="#111111" CREATED="1230020038968" ID="Freemind_Link_1867661637" MODIFIED="1230020045093" TEXT="index sur identifiant"/>
<node COLOR="#111111" CREATED="1230020045640" ID="Freemind_Link_1965587519" MODIFIED="1230020067451" TEXT="+ rapide">
<icon BUILTIN="idea"/>
</node>
<node COLOR="#111111" CREATED="1230020048264" ID="Freemind_Link_1912367596" MODIFIED="1230020073232" TEXT="2e table">
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1229350923572" ID="Freemind_Link_1408098271" MODIFIED="1233320314655" TEXT="table &quot;vue&quot;">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_cancel"/>
<node COLOR="#111111" CREATED="1229350944869" ID="Freemind_Link_1129063714" MODIFIED="1233320284296" TEXT="table non editable/collectable">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1229350952166" ID="Freemind_Link_1390643109" MODIFIED="1233665534239" TEXT="requete sur une table ITools">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1229350975104" ID="Freemind_Link_993230142" MODIFIED="1233320284296" TEXT="contient une clef etrangere">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1229350960150" ID="Freemind_Link_277372585" MODIFIED="1233320284296" TEXT="permet de fragmenter l&apos;exploration">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1232960589346" ID="ID_169407319" MODIFIED="1233299629650" TEXT="Table &quot;Jointure&quot;">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1233223726763" ID="ID_316619182" MODIFIED="1233223730185" TEXT="Exemples">
<node COLOR="#111111" CREATED="1232960628925" FOLDED="true" ID="ID_732051629" MODIFIED="1233223802685" TEXT="Exemples : organisme **">
<node COLOR="#111111" CREATED="1233223737716" ID="ID_869110843" MODIFIED="1233223758982" TEXT="Select distinct COL1,COL2 from CROEXPP"/>
</node>
<node COLOR="#111111" CREATED="1233130763478" FOLDED="true" ID="ID_1642580040" MODIFIED="1233223801232" TEXT="Exemple : libell&#xe9; dans autre table">
<node COLOR="#111111" CREATED="1233223763732" ID="ID_1238160684" MODIFIED="1233223788950" TEXT="Select COL1,COL2,LABEL from CROEXPP,CRITRTP where COL1=CRIT1"/>
</node>
</node>
<node COLOR="#111111" CREATED="1232960611362" ID="ID_342286946" MODIFIED="1232967529738" TEXT="utiliser des requetes pour recuperer les informations lors de la collecte"/>
<node COLOR="#111111" CREATED="1232960409500" ID="ID_1132864687" MODIFIED="1233223471325" TEXT="jointure au moment de la collecte">
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1233223574497" ID="ID_1534359006" MODIFIED="1233223579263" TEXT="jointure explicite"/>
<node COLOR="#111111" CREATED="1233130837105" ID="ID_1182240231" MODIFIED="1233130851262" TEXT="Plus simple &#xe0; r&#xe9;aliser"/>
<node COLOR="#111111" CREATED="1233130851730" ID="ID_1185530586" MODIFIED="1233223707310" TEXT="l&apos;utilisateur doit construire la requete">
<icon BUILTIN="messagebox_warning"/>
</node>
<node COLOR="#111111" CREATED="1233223581841" ID="ID_874551799" MODIFIED="1233223614716" TEXT="nom colonne obligatoire"/>
</node>
<node COLOR="#111111" CREATED="1232960432626" ID="ID_9885595" MODIFIED="1233223657779" TEXT="jointure au moment de l&apos;affichage">
<icon BUILTIN="button_cancel"/>
<node COLOR="#111111" CREATED="1232960448204" ID="ID_1045844756" MODIFIED="1232960452017" TEXT="utilisation clef etrang&#xe8;re"/>
<node COLOR="#111111" CREATED="1232960453689" ID="ID_1766220160" MODIFIED="1232960466657" TEXT="&quot;jointure&quot; applicative"/>
<node COLOR="#111111" CREATED="1233130821261" ID="ID_940181757" MODIFIED="1233130826152" TEXT="Plus compliqu&#xe9; &#xe0; r&#xe9;alis&#xe9;"/>
<node COLOR="#111111" CREATED="1233130826824" ID="ID_1523008056" MODIFIED="1233130834699" TEXT="Plus simple pour l&apos;utilisateur"/>
<node COLOR="#111111" CREATED="1233223665200" ID="ID_1815984242" MODIFIED="1233223698872" TEXT="Permet jointure SQlite/ITools">
<icon BUILTIN="idea"/>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1234164351096" ID="ID_901497022" MODIFIED="1234433707190" TEXT="diff&#xe9;rencier collecte &quot;Compl&#xe8;te/Partielle&quot;">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="button_ok"/>
<node COLOR="#111111" CREATED="1234164320237" ID="ID_882545944" MODIFIED="1234164369767" TEXT="Collecte &#xe0; la demande sur &quot;Module&quot;">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1234164333502" ID="ID_291195354" MODIFIED="1234164348236" TEXT="ne pas inclure cette date dans les dates de collecte &quot;complete&quot;"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1229498147961" FOLDED="true" ID="Freemind_Link_118026965" MODIFIED="1234780778873" TEXT="probl&#xe8;mes rencontr&#xe9;s">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1229360332055" ID="Freemind_Link_159190829" MODIFIED="1229498156665" TEXT="tri differents sur les chiffres">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="clanbomber"/>
<node COLOR="#111111" CREATED="1229498133699" ID="Freemind_Link_652840755" MODIFIED="1229498156665" TEXT="solution : tri en m&#xe9;moire">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1231342616943" ID="Freemind_Link_554690837" MODIFIED="1232531730622" TEXT="Codage caractere ANSI">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="clanbomber"/>
<node COLOR="#111111" CREATED="1232028935137" ID="Freemind_Link_1028744832" MODIFIED="1232531730622" TEXT="r&#xe9;solu avec Encode(&quot;cp850&quot;) dans les *AndExec.pl">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1231169890331" ID="Freemind_Link_908840290" MODIFIED="1232531738263" TEXT="STATUS != VALIDATION">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1231169901284" ID="Freemind_Link_551272998" MODIFIED="1232531738263" TEXT="status = calcul&#xe9; en fonction regle gestion">
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1231169936410" ID="Freemind_Link_725228413" MODIFIED="1231169944691" TEXT="mode validation"/>
<node COLOR="#111111" CREATED="1231169945066" ID="Freemind_Link_488084792" MODIFIED="1231342644179" TEXT="mode comparaison"/>
</node>
<node COLOR="#111111" CREATED="1231169913409" ID="Freemind_Link_1963527983" MODIFIED="1232531738278" TEXT="validation = etat explicite du changement">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1228126627021" ID="ID_680753007" MODIFIED="1232531753669" TEXT="multiple PRIMARY KEY">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="clanbomber"/>
<node COLOR="#111111" CREATED="1228126637974" ID="ID_265855855" MODIFIED="1232531753685" TEXT="multiple FOREIGN KEY">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1228754666961" ID="Freemind_Link_1576079395" MODIFIED="1233756077338" TEXT="Saisie">
<edge STYLE="bezier" WIDTH="thin"/>
<cloud/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1228754671686" ID="Freemind_Link_1630041746" MODIFIED="1229672467627" TEXT="Processeur Java/Swing">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1228839292917" ID="Freemind_Link_180695803" MODIFIED="1229672467627" TEXT="sur une ligne">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1228839295432" ID="Freemind_Link_1862404765" MODIFIED="1229672467627" TEXT="sur un champ">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1233657320609" ID="ID_129167161" MODIFIED="1233657324546" TEXT="packaging">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1233657325359" ID="ID_1587889089" MODIFIED="1233657335687" TEXT="utilisation Perl ARchiver">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1233657339531" ID="ID_727697791" MODIFIED="1233657342749" TEXT="code cach&#xe9;">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1233665560333" ID="ID_108701784" MODIFIED="1233665565958" TEXT="algo &quot;Bleach&quot;"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1229350719572" ID="Freemind_Link_698416190" MODIFIED="1232639520433" POSITION="right" TEXT="A &#xe9;tudier (V2)">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1229350731479" ID="Freemind_Link_1719791519" MODIFIED="1232639520433" TEXT="structure ICles">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1229350782619" ID="Freemind_Link_901330458" MODIFIED="1229350787697" TEXT="Eclater INFO_TABLE">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1229350788807" ID="Freemind_Link_872324095" MODIFIED="1229350791854" TEXT="INFO_TABLE">
<node COLOR="#111111" CREATED="1229350796916" ID="Freemind_Link_373918053" MODIFIED="1229350804557" TEXT="description table pour tout env"/>
</node>
<node COLOR="#111111" CREATED="1229350792291" ID="Freemind_Link_195323971" MODIFIED="1229350795963" TEXT="ENV_TABLE">
<node COLOR="#111111" CREATED="1229350805791" ID="Freemind_Link_1275076453" MODIFIED="1229350820854" TEXT="indique si table est inclus dans un ENV+source de donn&#xe9;e"/>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1231147020417" ID="Freemind_Link_1210307027" MODIFIED="1232639520449" TEXT="Perl">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1229672381535" ID="Freemind_Link_183713291" MODIFIED="1231147023229" TEXT="Exception Handling">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1229672385660" ID="Freemind_Link_279414033" MODIFIED="1231147023229" TEXT="Error.pm">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
<node COLOR="#990000" CREATED="1225976324661" ID="ID_514623334" MODIFIED="1231147024432" TEXT="Log">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1225976328614" ID="ID_246862931" MODIFIED="1231147024432" TEXT="Class configurable">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="12"/>
<node COLOR="#111111" CREATED="1225976334583" ID="ID_19883512" MODIFIED="1228752395975" TEXT="niveau de log">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1225976341677" ID="ID_289440653" MODIFIED="1228752395975" TEXT="fichier">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1225976344520" ID="ID_1951607798" MODIFIED="1228752395975" TEXT="stdout">
<edge WIDTH="thin"/>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1233848126064" ID="ID_969306471" MODIFIED="1233848148377" TEXT="Rewrite with modern OO model">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="closed"/>
<node COLOR="#111111" CREATED="1233848132892" ID="ID_823566224" MODIFIED="1233848139283" TEXT="use field, use base"/>
<node COLOR="#111111" CREATED="1233848139627" ID="ID_1025551253" MODIFIED="1233848144267" TEXT="Moose"/>
</node>
<node COLOR="#990000" CREATED="1233848501348" ID="ID_554645004" MODIFIED="1233848520567" TEXT="Rewrite in Python">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="closed"/>
<icon BUILTIN="smiley-oh"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1231140418694" ID="Freemind_Link_554666299" MODIFIED="1233755945588" TEXT="Optimisation">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1232002280411" ID="Freemind_Link_1077499383" MODIFIED="1232002294586" TEXT="utiliser numero de lot plutot que date de collecte">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1234781299253" ID="ID_1163345034" MODIFIED="1234781318050" TEXT="optimisation &quot;remont&#xe9;e&quot; par ajout colonne &quot;parent_key&quot;">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1233830118464" ID="ID_1217949357" MODIFIED="1233830129214" TEXT="PostgreS">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1233830132699" ID="ID_1190388140" MODIFIED="1233830141917" TEXT="commandes SQL portables">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1233830182855" ID="ID_1173450349" MODIFIED="1233830186324" TEXT="Histo.pm"/>
</node>
<node COLOR="#990000" CREATED="1233830146902" ID="ID_1436928145" MODIFIED="1233830155199" TEXT="Class Perl DBI">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1233830169542" ID="ID_734740799" MODIFIED="1233830174839" TEXT="adapter IsipConfig.pm">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1233830155652" ID="ID_344437322" MODIFIED="1233830168105" TEXT="adapter Environnement.pm">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
</node>
</map>
