package org.dew.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.ini.WINI;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestWINI extends TestCase {
  
  public TestWINI(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestWINI.class);
  }
  
  public 
  void testApp() 
    throws Exception 
  {
    Map<String, List<String>> comments = new HashMap<String, List<String>>();
    
    Map<String, Object> config = WINI.loadResource("test.ini", comments);
    
    System.out.println(config);
    
    System.out.println(comments);
  }
  
  public static
  String getDesktop()
  {
    String sUserHome = System.getProperty("user.home");
    return sUserHome + File.separator + "Desktop";
  }
  
  public static
  String getDesktopPath(String sFileName)
  {
    String sUserHome = System.getProperty("user.home");
    return sUserHome + File.separator + "Desktop" + File.separator + sFileName;
  }
}
