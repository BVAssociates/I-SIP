<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node COLOR="#000000" CREATED="1225378299470" ID="ID_1251294702" LINK="SIP_IKOS_etapes.mm" MODIFIED="1234424743893" TEXT="Bugs/probl&#xe8;mes">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="20"/>
<hook NAME="accessories/plugins/AutomaticLayout.properties"/>
<node COLOR="#0033ff" CREATED="1234424769331" ID="ID_651848506" MODIFIED="1234424857489" POSITION="right" TEXT="erreur technique">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1233907907355" ID="ID_554379378" MODIFIED="1234424841333" TEXT="IsipRules">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1233907918683" ID="ID_1156896831" MODIFIED="1234424788082" TEXT="utiliser class Environnement pour les infos">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1234338616273" ID="ID_1271072566" MODIFIED="1234424792582" TEXT="Acc&#xe8;s concurent UPDATE SAISIE et UPDATE CACHE">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="clanbomber"/>
</node>
<node COLOR="#00b439" CREATED="1232028918934" ID="ID_19736574" MODIFIED="1234424876677" TEXT="sur PC Franck, la colonne TYPE est vide dans TABLE_INFO">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="help"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1234424858302" ID="ID_1534749031" MODIFIED="1234424864474" POSITION="right" TEXT="amelioration technique">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1232532250441" ID="ID_678200473" MODIFIED="1234424818098" TEXT="Grosses tables">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="prepare"/>
<node COLOR="#990000" CREATED="1232532257863" ID="ID_297618266" MODIFIED="1234424818098" TEXT="lenteur g&#xe9;n&#xe9;ration &#xe0; partir de HISTO">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1232364108009" ID="ID_1186803671" MODIFIED="1234424871567" TEXT="fichiers XML">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1232364114088" ID="ID_1991579803" MODIFIED="1234424823176" TEXT="un noeud peut avoir une liste de valeur">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1231402832080" FOLDED="true" ID="ID_1146767311" MODIFIED="1234424880755" TEXT="PRIMARY _KEY from DB2">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="stop-sign"/>
<node COLOR="#990000" CREATED="1231402839315" ID="ID_1868480335" MODIFIED="1234424880771" TEXT="pour l&apos;instant, il faut specifier manuellemment la cl&#xe9; primaire">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1232984975305" ID="ID_1792242803" MODIFIED="1234424880771" TEXT="MS Access sait retrouver les clefs">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="info"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1234164267005" ID="ID_837115907" MODIFIED="1234424901693" TEXT="lenteur processeur EditForm">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
</node>
<node COLOR="#00b439" CREATED="1234433620907" ID="ID_1736402012" MODIFIED="1234433646439" TEXT="cache pendant collecte">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1234424932084" ID="ID_833377592" MODIFIED="1234433624017" TEXT="parcours arbre des tables">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1234425006195" ID="ID_539846159" MODIFIED="1234433624017" TEXT="parcours non d&#xe9;fini">
<font NAME="SansSerif" SIZE="12"/>
<icon BUILTIN="messagebox_warning"/>
<node COLOR="#111111" CREATED="1234425028758" ID="ID_1823837073" MODIFIED="1234425212605" TEXT="pas possible mettre &#xe0; jour cache&#xa;si table p&#xe8;re non mise &#xe0; jour au moment de la recherche des FKEY-&gt;KEY&#xa;"/>
</node>
<node COLOR="#111111" CREATED="1234424956241" ID="ID_183254529" MODIFIED="1234433624017" TEXT="parcours profondeur">
<font NAME="SansSerif" SIZE="12"/>
<icon BUILTIN="idea"/>
<node COLOR="#111111" CREATED="1234424979851" ID="ID_20289023" MODIFIED="1234425083134" TEXT="mettre &#xe0; jour les tables racine en premier"/>
</node>
</node>
<node COLOR="#990000" CREATED="1234425454516" ID="ID_1065961295" MODIFIED="1234433631564" TEXT="pour chaque table, enregistrer toutes les FKEY &quot;avant&quot; de faire la recherche sur les parents">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="idea"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1234424882630" ID="ID_1687217617" MODIFIED="1234518729058" POSITION="left" TEXT="problemes fonctionnels">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="SansSerif" SIZE="18"/>
<node COLOR="#00b439" CREATED="1223891273785" ID="ID_1657773759" MODIFIED="1234424896052" TEXT="informations de la table">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1223891309989" ID="ID_493040543" MODIFIED="1234424896052" TEXT="avec .DEF">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1223891313832" ID="ID_316011901" MODIFIED="1234424896052" TEXT="avec table local">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
<node COLOR="#990000" CREATED="1223891321708" ID="ID_1427410684" MODIFIED="1234424896052" TEXT="avec table distante">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
<node COLOR="#00b439" CREATED="1232544751648" ID="ID_1269526848" MODIFIED="1234424911600" TEXT="saisie concurente">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<icon BUILTIN="help"/>
<node COLOR="#990000" CREATED="1232544762945" ID="ID_1136079168" MODIFIED="1234424904771" TEXT="que faire si 2 personnes &#xe9;dite le m&#xea;me noeud">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1232544773429" ID="ID_617865418" MODIFIED="1234424904771" TEXT="locker">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1232544791836" ID="ID_1306475554" MODIFIED="1234424904771" TEXT="pr&#xe9;venir que qqun d&apos;autres edite le meme noeud">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1232544800555" ID="ID_1558382519" MODIFIED="1234424904771" TEXT="refuser d&apos;enregistrer">
<font NAME="SansSerif" SIZE="12"/>
</node>
<node COLOR="#111111" CREATED="1232544806336" ID="ID_479526919" MODIFIED="1234424904771" TEXT="premier arriv&#xe9;, premier servi">
<font NAME="SansSerif" SIZE="12"/>
</node>
</node>
</node>
<node COLOR="#00b439" CREATED="1234443839308" ID="ID_12850467" MODIFIED="1234443844491" TEXT="Cache">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="SansSerif" SIZE="16"/>
<node COLOR="#990000" CREATED="1234443845537" ID="ID_964477751" MODIFIED="1234443869154" TEXT="ne prend pas en charge les types de champs">
<font NAME="SansSerif" SIZE="14"/>
<node COLOR="#111111" CREATED="1234443870059" ID="ID_205800311" MODIFIED="1234443891288" TEXT="administratif"/>
<node COLOR="#111111" CREATED="1234443880237" ID="ID_1000703991" MODIFIED="1234443891132" TEXT="technique"/>
</node>
<node COLOR="#990000" CREATED="1234453225794" ID="ID_1239970955" MODIFIED="1234453234107" TEXT="ne g&#xe8;re pas l&apos;exploration temporelle">
<font NAME="SansSerif" SIZE="14"/>
</node>
</node>
</node>
</node>
</map>
