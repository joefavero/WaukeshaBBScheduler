/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.util;

import java.io.*;
import java.util.*;

import org.apache.xerces.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class AppUtil {
  private static final String CONFIG_TAG = "config";
  private static final String PROPERTY_TAG = "property";
  private static final String ELEMENT_TAG = "element";
  private static final String TYPE_ATTR_TAG = "type";
  private static final String NAME_ATTR_TAG = "name";

  private static final String TYPE_ATTR_SIMPLE = "simple";
  private static final String TYPE_ATTR_LIST = "list";
  private static final String TYPE_ATTR_MAP = "map";

  private static String RESOURCE_FILE = "app.xml";
  private static LinkedHashMap<String, Map<String, Object>> mProps = new LinkedHashMap<String, Map<String, Object>>();
  private static Map<String, Long> mTimeStamp = new LinkedHashMap<String, Long>();
  private static Logger mLog = LoggerFactory.getLogger( AppUtil.class);

  /**
   * Extend this class and create your own getResources with your own resource file.
   * 
   * @return
   */
  public static Map<String, Object> getResources() {
    return getResources( RESOURCE_FILE );
  }

  /**
   * Extend this class and create your own getResources with your own resource file.
   * 
   * @return
   */
  public static Map<String, Object> getResources( String p_resourceFile ) {

    if ( p_resourceFile == null )
      return null;

    Map<String, Object> l_prop = new LinkedHashMap<String, Object>();
    long l_timeStamp = 0;

    if ( mProps.get( p_resourceFile ) == null ) {
      l_prop = new LinkedHashMap<String, Object>();
      l_timeStamp = 0;
    }
    else {
      l_prop = mProps.get( p_resourceFile );
      l_timeStamp = mTimeStamp.get( p_resourceFile );
    }

    boolean isChanged = false;
    long l_lastModified = 0;
    boolean l_isResource = true;
    try {
      l_lastModified = AppUtil.class.getResource( "/" + p_resourceFile ).openConnection().getLastModified();
    }
    catch ( Exception e ) {
      try {
        l_lastModified = new File( p_resourceFile ).lastModified();
        l_isResource = false;
      }
      catch ( Exception l_e2 ) {
        l_e2.printStackTrace();
      }
    }

    if ( l_lastModified > l_timeStamp ) {
      isChanged = true;
      l_timeStamp = l_lastModified;
      mLog.trace( "getResources:  Loading Resource File." );
    }

    if ( isChanged ) {
      l_prop.clear();

      try {
        if ( l_isResource ) {
          l_prop = loadResources( AppUtil.class.getResourceAsStream( "/" + p_resourceFile ) );
        }
        else {
          l_prop = loadResources( new FileInputStream( new File( p_resourceFile ) ) );
        }

      }
      catch ( Exception l_e ) {
        l_e.printStackTrace();
      }
    }

    mTimeStamp.put( p_resourceFile, l_timeStamp );
    mProps.put( p_resourceFile, l_prop );
    return l_prop;
  }

  public static String getSimpleProperty( String p_key ) {
    return getSimpleProperty( getResources(), p_key, null );
  }

  public static String getSimpleProperty( String p_key, String p_default ) {
    return getSimpleProperty( getResources(), p_key, p_default );
  }

  public static String getSimpleProperty( Map<String, Object> p_prop, String p_key ) {
    return getSimpleProperty( p_prop, p_key, null );
  }

  public static String getSimpleProperty( Map<String, Object> p_prop, String p_key, String p_default ) {
    Object l_returnVal = p_prop.get( p_key );
    if ( l_returnVal instanceof String ) {
      return (String) l_returnVal;
    }
    else {
      return p_default;
    }
  }

  public static List<String> getListProperty( String p_key ) {
    return getListProperty( getResources(), p_key, null );
  }

  public static List<String> getListProperty( String p_key, List<String> p_default ) {
    return getListProperty( getResources(), p_key, p_default );
  }

  public static List<String> getListProperty( Map<String, Object> p_prop, String p_key ) {
    return getListProperty( p_prop, p_key, null );
  }

  @SuppressWarnings( "unchecked" )
  public static List<String> getListProperty( Map<String, Object> p_prop, String p_key, List<String> p_default ) {
    Object l_returnVal = p_prop.get( p_key );
    if ( l_returnVal instanceof List ) {
      return (List<String>) l_returnVal;
    }
    else {
      return p_default;
    }
  }

  public static Map<String, Map<String, String>> getMapProperty( String p_key ) {
    return getMapProperty( getResources(), p_key, null );
  }

  public static Map<String, Map<String, String>> getMapProperty( String p_key,
                                                                 Map<String, Map<String, String>> p_default ) {
    return getMapProperty( getResources(), p_key, null );
  }

  public static Map<String, Map<String, String>> getMapProperty( Map<String, Object> p_prop, String p_key ) {
    return getMapProperty( p_prop, p_key, null );
  }

  @SuppressWarnings( "unchecked" )
  public static Map<String, Map<String, String>> getMapProperty( Map<String, Object> p_prop, String p_key,
                                                                 Map<String, Map<String, String>> p_default ) {
    Object l_returnVal = p_prop.get( p_key );
    if ( l_returnVal instanceof Map ) {
      return (Map<String, Map<String, String>>) l_returnVal;
    }
    else {
      return p_default;
    }
  }

  private static Map<String, Object> loadResources( InputStream p_resources ) {
    String l_msgLoc = "loadResources: ";
    Map<String, Object> l_hashMap = new LinkedHashMap<String, Object>();
    try {

      DOMParser l_parser = new DOMParser();
      l_parser.parse( new InputSource( p_resources ) );
      Document l_document = l_parser.getDocument();

      NodeList l_configNodes = l_document.getElementsByTagName( CONFIG_TAG );
      if ( l_configNodes.getLength() == 0 ) {
        mLog.trace( l_msgLoc + "The root tag must be <config/>." );
        return l_hashMap;
      }

      NodeList l_propNodes = l_configNodes.item( 0 ).getChildNodes();
      Node l_childNode = null;
      NamedNodeMap l_attr = null;
      Node l_attrNode = null;
      Node l_tagAttrNode = null;
      String l_propName = null;
      String l_propType = null;
      for ( int i = 0; i < l_propNodes.getLength(); i++ ) {
        l_childNode = l_propNodes.item( i );
        if ( l_childNode.getNodeName().equals( PROPERTY_TAG ) ) {
          l_attr = l_childNode.getAttributes();
          l_attrNode = l_attr.getNamedItem( NAME_ATTR_TAG );
          if ( l_attrNode == null ) {
            mLog.trace( l_msgLoc + "Property tag has missing 'name' attribute. Ignoring property: "
                        + l_childNode.getNodeName() );
            continue;
          }
          l_propName = l_attrNode.getNodeValue();
          l_tagAttrNode = l_attr.getNamedItem( TYPE_ATTR_TAG );
          if ( l_tagAttrNode == null ) {
            l_propType = TYPE_ATTR_SIMPLE;
          }
          else {
            l_propType = l_tagAttrNode.getNodeValue();
          }
          if ( l_propType.equalsIgnoreCase( TYPE_ATTR_SIMPLE ) ) {
            l_hashMap.put( l_propName, processSimpleType( l_childNode ) );
          }
          else if ( l_propType.equalsIgnoreCase( TYPE_ATTR_LIST ) ) {
            l_hashMap.put( l_propName, processListType( l_childNode ) );
          }
          else if ( l_propType.equalsIgnoreCase( TYPE_ATTR_MAP ) ) {
            l_hashMap.put( l_propName, processMapType( l_childNode ) );
          }
          else {
            l_hashMap.put( l_propName, processSimpleType( l_childNode ) );
          }
        }
      }

    }
    catch ( Exception l_ex ) {
      mLog.error( l_msgLoc + "Error getting properties", l_ex );
    }

    return l_hashMap;
  }

  private static String processSimpleType( Node p_node ) {
    Node l_firstChild = p_node.getFirstChild();
    return ( l_firstChild == null ? null : l_firstChild.getNodeValue() );
  }

  private static List<String> processListType( Node p_node ) {
    String l_msgLoc = "processListType: ";
    ArrayList<String> l_retList = new ArrayList<String>();

    NodeList l_nodeList = p_node.getChildNodes();
    Node l_childNode = null;
    NamedNodeMap l_attr = null;
    Node l_attrNode = null;
    for ( int i = 0; i < l_nodeList.getLength(); i++ ) {
      l_childNode = l_nodeList.item( i );
      if ( l_childNode.getNodeName().equals( ELEMENT_TAG ) ) {
        l_attr = l_childNode.getAttributes();
        l_attrNode = l_attr.getNamedItem( NAME_ATTR_TAG );
        if ( l_attrNode == null ) {
          mLog.trace( l_msgLoc + "Property tag has missing 'name' attribute. Ignoring property: "
                      + l_childNode.getNodeName() );
          continue;
        }
        l_retList.add( l_attrNode.getNodeValue() );
      }
    }
    return l_retList;
  }

  /**
   * Escapes the XML control characters in the string
   * 
   * @param p_text A String to be escaped
   * @return The converted string
   */
  public static String escapeXmlString( String p_text ) {
    if ( p_text == null )
      return "";

    p_text = p_text.replaceAll( "#", "#shrp;" );
    p_text = p_text.replaceAll( "&", "#amp;" );
    p_text = p_text.replaceAll( ">", "#gt;" );
    p_text = p_text.replaceAll( "<", "#lt;" );
    p_text = p_text.replaceAll( "\"", "#quot;" );
    p_text = p_text.replaceAll( "'", "#apos;" );
    p_text = p_text.replaceAll( "\n", "#newl;" );
    p_text = p_text.replaceAll( "\t", "#tab;" );
    return p_text;
  }

  /**
   * Unescapes the XML control characters.
   * 
   * @param p_xmlText A String to be unescaped
   * @return The converted string
   */
  public static String unescapeXmlString( String p_xmlText ) {

    if ( p_xmlText == null )
      return null;

    p_xmlText = p_xmlText.replaceAll( "#tab;", "\t" );
    p_xmlText = p_xmlText.replaceAll( "#newl;", "\n" );
    p_xmlText = p_xmlText.replaceAll( "#gt;", ">" );
    p_xmlText = p_xmlText.replaceAll( "#lt;", "<" );
    p_xmlText = p_xmlText.replaceAll( "#quot;", "\"" );
    p_xmlText = p_xmlText.replaceAll( "#apos;", "'" );
    p_xmlText = p_xmlText.replaceAll( "#amp;", "&" );
    p_xmlText = p_xmlText.replaceAll( "#shrp;", "#" );
    return p_xmlText;
  }

  private static Map<String, Map<String, String>> processMapType( Node p_node ) {
    String l_msgLoc = "processMapType: ";
    Map<String, Map<String, String>> l_eleHash = new LinkedHashMap<String, Map<String, String>>();

    NodeList l_nodeList = p_node.getChildNodes();
    Node l_childNode = null;
    NamedNodeMap l_attr = null;
    Node l_attrNode = null;
    String l_propName = null;
    Map<String, String> l_attrHash = null;
    for ( int i = 0; i < l_nodeList.getLength(); i++ ) {
      l_childNode = l_nodeList.item( i );
      if ( l_childNode.getNodeName().equals( ELEMENT_TAG ) ) {
        l_attr = l_childNode.getAttributes();
        l_attrNode = l_attr.getNamedItem( NAME_ATTR_TAG );
        if ( l_attrNode == null ) {
          mLog.trace( l_msgLoc + "Property tag has missing 'name' attribute. Ignoring property: "
                      + l_childNode.getNodeName() );
          continue;
        }
        l_propName = l_attrNode.getNodeValue();
        l_attrHash = new LinkedHashMap<String, String>();
        for ( int j = 0; j < l_attr.getLength(); j++ ) {
          l_attrNode = l_attr.item( j );
          l_attrHash.put( l_attrNode.getNodeName(), l_attrNode.getNodeValue() );
        }
        l_eleHash.put( l_propName, l_attrHash );
      }
    }
    return l_eleHash;
  }

  public static String escapeRegex( String p_src ) {
    String l_retStr = p_src;
    l_retStr = l_retStr.replaceAll( "\\\\", "\\\\\\\\" );
    l_retStr = l_retStr.replaceAll( "\\[", "\\\\[" );
    l_retStr = l_retStr.replaceAll( "\\^", "\\\\^" );
    l_retStr = l_retStr.replaceAll( "\\$", "\\\\\\$" );
    l_retStr = l_retStr.replaceAll( "\\.", "\\\\." );
    l_retStr = l_retStr.replaceAll( "\\|", "\\\\|" );
    l_retStr = l_retStr.replaceAll( "\\?", "\\\\?" );
    l_retStr = l_retStr.replaceAll( "\\*", "\\\\*" );
    l_retStr = l_retStr.replaceAll( "\\+", "\\\\+" );
    l_retStr = l_retStr.replaceAll( "\\(", "\\\\(" );
    l_retStr = l_retStr.replaceAll( "\\)", "\\\\)" );
    l_retStr = l_retStr.replaceAll( "\\{", "\\\\{" );
    l_retStr = l_retStr.replaceAll( "\\}", "\\\\}" );

    return l_retStr;
  }
}
