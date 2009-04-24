<map version="0.8.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1222419162847" ID="Freemind_Link_1423714273" LINK="SIP_IKOS_.mm" MODIFIED="1228754573148" TEXT="validation stockage SQLITE">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<node COLOR="#0033ff" CREATED="1222419210862" ID="_" MODIFIED="1228754573102" POSITION="right" TEXT="Acc&#xe8;s concurents">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222419483843" ID="Freemind_Link_492181983" MODIFIED="1228754573039" TEXT="comportement">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222867226084" ID="Freemind_Link_1464456946" MODIFIED="1228754573039" TEXT="acc&#xe8;s concurent &quot;applicatif&quot;">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1222877338568" ID="Freemind_Link_1392773700" MODIFIED="1228754572992" TEXT="Gerer le code retour SQLITE_BUSY">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1222877347084" ID="Freemind_Link_1212230800" MODIFIED="1228754572961" TEXT="mettre un TIMEOUT avant chanque requete">
<edge WIDTH="thin"/>
</node>
</node>
<node COLOR="#990000" CREATED="1222944206147" ID="ID_223556633" MODIFIED="1228754572914" TEXT="Probl&#xe8;mes potentiels">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1222944214225" ID="ID_1721079823" MODIFIED="1228754572883" TEXT="KILL du process">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1222944220803" ID="ID_1338349991" MODIFIED="1228754572852" TEXT="process zombie">
<edge WIDTH="thin"/>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1222419491514" ID="Freemind_Link_1347016120" MODIFIED="1228754572790" TEXT="int&#xe9;grit&#xe9; des donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222867255022" ID="Freemind_Link_978734226" MODIFIED="1228754572790" TEXT="transactionnel OK">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222419220658" ID="Freemind_Link_812718655" MODIFIED="1228754572743" POSITION="right" TEXT="vitesse acc&#xe8;s">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222867247303" ID="Freemind_Link_1270267216" MODIFIED="1228754572681" TEXT="utilisation d&apos;index">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222421788573" FOLDED="true" ID="Freemind_Link_330998209" MODIFIED="1228754572681" POSITION="left" TEXT="Autres possibilit&#xe9;s">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222421800348" ID="Freemind_Link_963815850" MODIFIED="1222941413899" TEXT="Fichiers plats">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932182346" ID="Freemind_Link_339675596" MODIFIED="1222932189767" TEXT="Lock_Table dangereux">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222421806510" ID="Freemind_Link_1493224777" MODIFIED="1222941413899" TEXT="Mysql">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932169190" ID="Freemind_Link_802565979" MODIFIED="1222932174158" TEXT="Meilleur gestion concurence">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222421809465" ID="Freemind_Link_1902251474" MODIFIED="1222867147178" TEXT="implique l&apos;exploitation du serveur">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222943962044" ID="ID_1858820179" MODIFIED="1222943964763" TEXT="Autres">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222943966419" ID="ID_389281693" MODIFIED="1222943977387" TEXT="possible si G&#xe9;n&#xe9;ricit&#xe9; du code">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="messagebox_warning"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222419251455" FOLDED="true" ID="Freemind_Link_1337825180" MODIFIED="1228754572649" POSITION="left" TEXT="conformit&#xe9; type de donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222867055678" ID="Freemind_Link_33301797" MODIFIED="1222941413899" TEXT="Affichage des REAL">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="messagebox_warning"/>
</node>
<node COLOR="#00b439" CREATED="1222867286631" ID="Freemind_Link_1993774466" MODIFIED="1222941413899" TEXT="gestion des donn&#xe9;es NULL ou &quot;&quot;">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222867515615" ID="Freemind_Link_903195597" MODIFIED="1222941413899" TEXT="Type de donn&#xe9;es non strict">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222867533131" ID="Freemind_Link_1646034785" MODIFIED="1222932160565" TEXT="insertion de TEXT dans INTEGER possible">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222421739050" ID="Freemind_Link_135581092" MODIFIED="1228754572618" POSITION="left" TEXT="calcul de differences">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222867074225" ID="Freemind_Link_1262765353" MODIFIED="1228754572540" TEXT="bas&#xe9; sur le snapshot">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222867080943" ID="Freemind_Link_1152611428" MODIFIED="1228754572509" TEXT="bas&#xe9; sur histo">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222421744398" ID="Freemind_Link_581636296" MODIFIED="1228754572478" TEXT="calcul d&apos;arbres &#xe0; l&apos;instant T">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222867167943" ID="Freemind_Link_1761844918" MODIFIED="1228754572478" TEXT="bas&#xe9; sur histo uniquement">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222932765650" ID="Freemind_Link_1380655400" MODIFIED="1228754572431" TEXT="par table">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222867179725" FOLDED="true" ID="Freemind_Link_741018966" MODIFIED="1228754572415" TEXT="reconstruction &quot;virtuelle&quot; d&apos;une table">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1222932732009" ID="Freemind_Link_112991970" MODIFIED="1222932737900" TEXT="Impossible en SQL pur"/>
<node COLOR="#111111" CREATED="1222932738259" ID="Freemind_Link_1635264972" MODIFIED="1222932752712" TEXT="Calcul n&#xe9;c&#xe9;ssaire en Perl"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222932024133" ID="Freemind_Link_1541564218" MODIFIED="1228754572369" POSITION="right" TEXT="Acc&#xe8;s aux donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222932037335" FOLDED="true" ID="Freemind_Link_1333241860" MODIFIED="1228754572291" TEXT="module Perl">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932052334" ID="Freemind_Link_23152104" MODIFIED="1222932063005" TEXT="DBD::Sqlite">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222932068302" FOLDED="true" ID="Freemind_Link_1570681950" MODIFIED="1228754572259" TEXT="ligne de commande">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932196330" ID="Freemind_Link_41635117" MODIFIED="1222932202361" TEXT="langage SQL92">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222932079129" FOLDED="true" ID="Freemind_Link_1219453889" MODIFIED="1228754572228" TEXT="sqlitebrowser">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932085535" ID="Freemind_Link_1102394481" MODIFIED="1222932112347" TEXT="attention, termine la transaction &#xe0; la fermeture">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222932116175" ID="Freemind_Link_1305162864" MODIFIED="1222932121144" TEXT="lock pendant la transaction">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222932207423" FOLDED="true" ID="Freemind_Link_1213283788" MODIFIED="1228754572197" TEXT="export">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222932215876" ID="Freemind_Link_269363348" MODIFIED="1222932217641" TEXT="CSV">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222932217891" ID="Freemind_Link_848502200" MODIFIED="1222932222782" TEXT="Tabulations">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222932223141" ID="Freemind_Link_938904253" MODIFIED="1222932225329" TEXT="HTML">
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1222932225704" ID="Freemind_Link_1871635139" MODIFIED="1222932254312" TEXT="ordre INSERT">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1222867387115" ID="Freemind_Link_1637509008" MODIFIED="1228754572166" TEXT="Int&#xe9;gration">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1222867401193" ID="Freemind_Link_1470362792" MODIFIED="1228754572166" TEXT="Compatibilit&#xe9; ITools">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1222932666243" ID="Freemind_Link_340154588" MODIFIED="1228754572119" TEXT="R&#xe9;&#xe9;criture scripts">
<edge WIDTH="thin"/>
<node COLOR="#111111" CREATED="1222932632666" ID="Freemind_Link_716969702" MODIFIED="1228754572088" TEXT="Select">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1222932641915" ID="Freemind_Link_1568242060" MODIFIED="1228754572057" TEXT="Update">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1222932646556" ID="Freemind_Link_396313567" MODIFIED="1228754572025" TEXT="Insert">
<edge WIDTH="thin"/>
</node>
<node COLOR="#111111" CREATED="1222932680040" ID="Freemind_Link_1578944259" MODIFIED="1228754571994" TEXT="Delete">
<edge WIDTH="thin"/>
</node>
</node>
</node>
<node COLOR="#990000" CREATED="1223646421681" ID="ID_1425669278" MODIFIED="1228754571963" TEXT="Utilisation d&apos;une interface Perl uniforme ITools/Sqlite">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1222421760489" ID="Freemind_Link_1240343411" MODIFIED="1228754571932" POSITION="left" TEXT="Stockage des fichiers">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1222867204209" ID="Freemind_Link_126510768" MODIFIED="1228754571854" TEXT="1 base pour toutes les donn&#xe9;es">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222867101475" ID="Freemind_Link_1999319312" MODIFIED="1228754571823" TEXT="1 base par environnement">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222867117350" ID="Freemind_Link_1444700703" MODIFIED="1228754571792" TEXT="1 base par couple TABLE, FIELD_HISTO">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1222932841899" ID="Freemind_Link_460239553" MODIFIED="1228754571760" TEXT="un seul service ITools">
<edge WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
</node>
</node>
</map>
