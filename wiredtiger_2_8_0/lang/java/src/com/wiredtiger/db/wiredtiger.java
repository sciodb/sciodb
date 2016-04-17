/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.6
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.wiredtiger.db;

/**
 * @defgroup wt_java WiredTiger Java API
 *
 * Java wrappers around the WiredTiger C API.
 */

/**
 * @ingroup wt_java
 */

public class wiredtiger implements wiredtigerConstants {
  public static String wiredtiger_strerror(int error) {
    return wiredtigerJNI.wiredtiger_strerror(error);
  }

  
  /**
   * @copydoc ::wiredtiger_open
   */
  public  static Connection open(String home, String config) throws com.wiredtiger.db.WiredTigerException {
    long cPtr = wiredtigerJNI.open(home, config);
    return (cPtr == 0) ? null : new Connection(cPtr, false);
  }

}
