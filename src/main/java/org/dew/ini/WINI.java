package org.dew.ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public 
class WINI
{
  public static
  Map<String, Object> load(InputStream is)
    throws Exception
  {
    return load(is, null);
  }
  
  public static
  Map<String, Object> load(InputStream is, Map<String, List<String>> mapComments)
    throws Exception
  {
    if(is == null) {
      throw new Exception("InputStream is null");
    }
    
    Map<String, Object> result = new HashMap<String, Object>();
    
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    String line = null;
    String section = null;
    List<String> comments = new ArrayList<String>();
    while((line = br.readLine()) != null) {
      line = line.trim();
      if(line.length() < 2) continue;
      
      char c0 = line.charAt(0);
      if(c0 == '[') {
        section = line.substring(1, line.length()-1);
        if(mapComments != null && comments.size() > 0) {
          mapComments.put("[" + section + "]", comments);
          comments = new ArrayList<String>();
        }
      }
      else if(c0 == ';') {
        comments.add(line.substring(1));
      }
      else if(c0 == '#') {
        comments.add(line.substring(1));
      }
      else if(c0 == '/' && line.length() > 2) {
        comments.add(line.substring(2));
      }
      else {
        int sep = line.indexOf('=');
        if(sep > 0) {
          String cfg = line.substring(0,sep).trim();
          String val = line.substring(sep+1).trim();
          String key = section != null && section.length() > 0 ? section + "." + cfg : cfg;
          result.put(key, val);
          if(mapComments != null && comments.size() > 0) {
            mapComments.put(key, comments);
            comments = new ArrayList<String>();
          }
        }
      }
    }
    
    return result;
  }
  
  public static
  void save(OutputStream os, Map<String, Object> config)
    throws Exception
  {
    save(os, config, null);
  }
  
  public static
  void save(OutputStream os, Map<String, Object> config, Map<String, List<String>> mapComments)
    throws Exception
  {
    if(os == null) {
      throw new Exception("OutputStream is null");
    }
    if(config == null || config.isEmpty()) {
      return;
    }
    if(mapComments == null) {
      mapComments = new HashMap<String, List<String>>();
    }
    
    List<String> keysNS = new ArrayList<String>();
    List<String> keysWS = new ArrayList<String>();
    
    Iterator<String> iterator = config.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      int sep = key.indexOf('.');
      if(sep < 0) {
        keysNS.add(key);
      }
      else {
        keysWS.add(key);
      }
    }
    
    Collections.sort(keysNS);
    Collections.sort(keysWS);
    
    PrintStream ps = new PrintStream(os);
    
    for(int i = 0; i < keysNS.size(); i++) {
      String key = keysNS.get(i);
      Object val = config.get(key);
      List<String> comments = mapComments.get(key);
      if(comments != null && comments.size() > 0) {
        for(int j = 0; j < comments.size(); j++) {
          ps.println(";" + comments.get(j));
        }
      }
      ps.println(key + "=" + toString(val));
    }
    
    Set<String> sections = new HashSet<String>();
    for(int i = 0; i < keysWS.size(); i++) {
      String key = keysWS.get(i);
      Object val = config.get(key);
      int sep = key.lastIndexOf('.');
      if(sep < 0) {
        // Unlikely...
        List<String> comments = mapComments.get(key);
        if(comments != null && comments.size() > 0) {
          for(int j = 0; j < comments.size(); j++) {
            ps.println(";" + comments.get(j));
          }
        }
        ps.println(key + "=" + toString(val));
      }
      else {
        String section = key.substring(0,sep);
        if(!sections.contains(section)) {
          if(sections.size() == 0) {
            if(keysNS.size() > 0) ps.println("");
          }
          else {
            ps.println("");
          }
          List<String> comments = mapComments.get("[" + section + "]");
          if(comments != null && comments.size() > 0) {
            for(int j = 0; j < comments.size(); j++) {
              ps.println(";" + comments.get(j));
            }
          }
          ps.println("[" + section + "]");
          sections.add(section);
        }
        List<String> comments = mapComments.get(key);
        if(comments != null && comments.size() > 0) {
          for(int j = 0; j < comments.size(); j++) {
            ps.println(";" + comments.get(j));
          }
        }
        ps.println(key.substring(sep + 1) + "=" + toString(val));
      }
    }
  }
  
  public static
  Map<String, Object> load(String filePath)
    throws Exception
  {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filePath);
      
      return load(fis);
    }
    finally {
      if(fis != null) try { fis.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> load(String filePath, Map<String, List<String>> mapComments)
    throws Exception
  {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filePath);
      
      return load(fis, mapComments);
    }
    finally {
      if(fis != null) try { fis.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> load(File file)
    throws Exception
  {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      
      return load(fis);
    }
    finally {
      if(fis != null) try { fis.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> load(File file, Map<String, List<String>> mapComments)
    throws Exception
  {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      
      return load(fis, mapComments);
    }
    finally {
      if(fis != null) try { fis.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> load(URL url)
    throws Exception
  {
    InputStream is = null;
    try {
      is = url.openStream();
      
      return load(is);
    }
    finally {
      if(is != null) try { is.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> load(URL url, Map<String, List<String>> mapComments)
    throws Exception
  {
    InputStream is = null;
    try {
      is = url.openStream();
      
      return load(is, mapComments);
    }
    finally {
      if(is != null) try { is.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> loadResource(String resource)
    throws Exception
  {
    InputStream is = null;
    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
      
      if(url == null) {
        throw new Exception("Resource " + resource + " not found.");
      }
      
      is = url.openStream();
      
      return load(is);
    }
    finally {
      if(is != null) try { is.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  Map<String, Object> loadResource(String resource, Map<String, List<String>> mapComments)
    throws Exception
  {
    InputStream is = null;
    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
      
      if(url == null) {
        throw new Exception("Resource " + resource + " not found.");
      }
      
      is = url.openStream();
      
      return load(is, mapComments);
    }
    finally {
      if(is != null) try { is.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  void save(String filePath, Map<String, Object> config)
    throws Exception
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filePath);
      
      save(fos, config);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  void save(String filePath, Map<String, Object> config, Map<String, List<String>> mapComments)
    throws Exception
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(filePath);
      
      save(fos, config, mapComments);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  void save(File file, Map<String, Object> config)
    throws Exception
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      
      save(fos, config);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  void save(File file, Map<String, Object> config, Map<String, List<String>> mapComments)
    throws Exception
  {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      
      save(fos, config, mapComments);
    }
    finally {
      if(fos != null) try { fos.close(); } catch(Exception ex) {}
    }
  }
  
  public static
  String toString(Object val)
  {
    if(val == null) {
      return "";
    }
    if(val instanceof Date) {
      return dateToString((Date) val);
    }
    else if(val instanceof Calendar) {
      return calendarToString((Calendar) val);
    }
    String result = val.toString();
    if(result == null) return "";
    return result; 
  }
  
  public static
  String dateToString(Date date)
  {
    if(date == null) return "";
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DAY_OF_MONTH);
    String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay   = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    return iYear + "-" + sMonth + "-" + sDay;
  }
  
  public static
  String calendarToString(Calendar cal)
  {
    if(cal == null) return "";
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DAY_OF_MONTH);
    String sMonth = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay   = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    return iYear + "-" + sMonth + "-" + sDay;
  }
}
