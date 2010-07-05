<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "file:../dtd/helpset_1_0.dtd">

<helpset version="1.0">

  <!-- title -->
  <title>I-SIS Explorer - Aide</title>

  <!-- maps -->
  <maps>
     <homeID>top</homeID>
     <mapref location="ExplorerMap.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Sommaire</label>
    <type>javax.help.TOCView</type>
    <data>ExplorerTOC.xml</data>
  </view>

  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>ExplorerIndex.xml</data>
  </view>

  <!--
  <view>
    <name>Search</name>
    <label>Recherche</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>
  -->
</helpset>
