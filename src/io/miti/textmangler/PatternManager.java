/*
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

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the patters read from the INI file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class PatternManager
{
  /**
   * The name of the INI file.
   */
  private String filename = null;
  
  /**
   * The line separator.
   */
  private String lineSeparator = null;
  
  /**
   * The list of pattern objects read from the INI file.
   */
  private List<Pattern> listPatterns = null;
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private PatternManager()
  {
    super();
  }
  
  
  /**
   * Constructor taking the name of the INI file.
   * 
   * @param sFilename the name of the INI file
   * @param sLineSep the line separator string for this OS
   */
  public PatternManager(final String sFilename, final String sLineSep)
  {
    super();
    
    filename = sFilename;
    lineSeparator = sLineSep;
    
    // Read the pattern data
    readPatternData();
    
    // Check if the list is empty
    checkForNoPatterns();
  }
  
  
  /**
   * Checks for no patterns found in the INI file.
   * If there are no patterns stored, 
   */
  private void checkForNoPatterns()
  {
    // Is the list empty?
    if (listPatterns.size() > 0)
    {
      // It's not empty, so there's nothing to do
      return;
    }
    
    // Build some pattern data
    List<String> data = new ArrayList<String>(10);
    data.add("$1 and $2 know $3" + lineSeparator);
    data.add("$1 uppercase is $upper($1)" + lineSeparator);
    
    // The list is empty, so add some entries
    Pattern pattern = new Pattern("Custom", data);
    listPatterns.add(pattern);
  }
  
  
  /**
   * Read the data for all patterns in the INI file.
   */
  private void readPatternData()
  {
    // Initialize our list of patterns
    listPatterns = new ArrayList<Pattern>(10);
    
    // Initialize the INI file reader
    IniFile file = new IniFile(filename);
    
    // Get the name of all sections
    List<String> sections = file.getAllSectionNames();
    
    // See if any sections were found
    if ((sections == null) || (sections.size() < 1))
    {
      return;
    }
    
    // Iterate over the section names, and look for the pattern data
    for (String section : sections)
    {
      // Check the name
      if (section.length() < 1)
      {
        continue;
      }
      
      // Get the number of lines for the section
      final int numLines = file.getIntegerProperty(section, "Lines");
      if (numLines < 1)
      {
        // This section doesn't have a Lines property, so skip it
        continue;
      }
      
      // This will hold the list of lines for this section
      List<String> data = new ArrayList<String>(numLines);
      
      // Now get all lines for this section
      for (int i = 0; i < numLines; ++i)
      {
        String line = file.getStringProperty(section, "Line" + Integer.toString(i + 1));
        if (line != null)
        {
          // If we're on the last line, don't include a line separator
          if (i == (numLines - 1))
          {
            data.add(line);
          }
          else
          {
            data.add(line + lineSeparator);
          }
        }
      }
      
      // Check the size
      if (data.size() > 0)
      {
        // There is at least one row in the list, so save the pattern
        Pattern pattern = new Pattern(section, data);
        listPatterns.add(pattern);
      }
    }
  }
  
  
  /**
   * Return the list of pattern names.
   * 
   * @return the list of pattern names
   */
  public List<String> getPatternNames()
  {
    // Save the number of patterns
    final int nListSize = listPatterns.size();
    
    // Declare our output list
    List<String> names = new ArrayList<String>(nListSize);
    
    // Check if the list is empty
    if (nListSize < 1)
    {
      return names;
    }
    
    // Iterate over the patterns, saving the name of each one
    for (int i = 0; i < nListSize; ++i)
    {
      names.add(listPatterns.get(i).getName());
    }
    
    return names;
  }
  
  
  /**
   * Get the pattern data by matching on pattern name.  Returns
   * null if the pattern name is not found.
   * 
   * @param patternName the name of the pattern to match on
   * @return the pattern data for the pattern with a name of patternName
   */
  public String getPatternDataByName(final String patternName)
  {
    // Check the list size
    if (listPatterns.size() < 1)
    {
      return null;
    }
    else if (patternName == null)
    {
      return null;
    }
    
    // Declare our return variable
    String returnList = null;
    
    // Find the pattern that matches on name
    for (Pattern pattern : listPatterns)
    {
      if (patternName.equals(pattern.getName()))
      {
        returnList = arrayToString(pattern.getPatternData());
        break;
      }
    }
    
    // Return the pattern data
    return returnList;
  }
  
  
  /**
   * Get the pattern data by matching on pattern index.  Returns
   * null if the pattern index is invalid.
   * 
   * @param patternIndex the index of the pattern to match on
   * @return the pattern data for the pattern with an index of patternIndex
   */
  public String getPatternDataByIndex(final int patternIndex)
  {
    // Check the list size
    if ((patternIndex < 0) || (patternIndex >= listPatterns.size()))
    {
      return null;
    }
    
    return (arrayToString(listPatterns.get(patternIndex).getPatternData()));
  }
  
  
  /**
   * Convert the array of strings into one long string.
   * 
   * @param listData the list of strings
   * @return the strings as one long string
   */
  private String arrayToString(final List<String> listData)
  {
    // Declare the object that gets returned
    StringBuilder sb = new StringBuilder(200);
    
    final int listSize = listData.size();
    for (int i = 0; i < listSize; ++i)
    {
      sb.append(listData.get(i));
    }
    
    // Return the string
    return sb.toString();
  }
}
