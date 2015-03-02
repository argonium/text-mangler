/**
 * Written by Mike Wallace (mfwallace at gmail.com).  Available
 * on the web site http://mfwallace.googlepages.com/.
 * 
 * Copyright (c) 2006 Mike Wallace.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.miti.textmangler;

import java.util.List;

/**
 * This class is used to store the data for patterns
 * found in the INI file.  It has a string holding the
 * pattern name, and an array of Strings, representing
 * the pattern itself.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Pattern
{
  /**
   * The name of the pattern.
   */
  private String name = null;
  
  /**
   * The pattern data.
   */
  private List<String> pattern = null;
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private Pattern()
  {
    super();
  }


  /**
   * Constructor taking the two fields as arguments.
   * 
   * @param sName the name of the pattern
   * @param lPattern the pattern data
   */
  public Pattern(final String sName, final List<String> lPattern)
  {
    super();
    name = sName;
    pattern = lPattern;
  }
  
  
  /**
   * Returns the pattern name.
   * 
   * @return the pattern name
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Returns the pattern data.
   * 
   * @return the pattern data
   */
  public List<String> getPatternData()
  {
    return pattern;
  }
}
