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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Date;
import java.util.Set;

/**
 * This class provides methods for reading and writing a Windows-style
 * INI file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class IniFile
{
  /**
   * The name of the INI file.
   */
  private String iniFileName = null;
  
  /**
   * The line separator for this OS.
   */
  private static final String lineSep;
  
  /**
   * Get the line separator for the OS.
   */
  static
  {
    lineSep = System.getProperty("line.separator");
  }
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private IniFile()
  {
    super();
  }
  
  
  /**
   * Constructor to take the name of the INI file.
   * 
   * @param fileName the name of the INI file to read
   */
  public IniFile(final String fileName)
  {
    iniFileName = fileName;
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public boolean getBooleanProperty(final String sectionName,
                                    final String propertyName)
  {
    return getBooleanProperty(sectionName, propertyName, false);
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param defaultValue the default value to return, if it's not found
   * @return the value for the specified section/property
   */
  public boolean getBooleanProperty(final String sectionName,
                                    final String propertyName,
                                    final boolean defaultValue)
  {
    // Get the string from the input file
    final String str = getStringProperty(sectionName, propertyName);
    
    // Check if the returned string is null
    if (str == null)
    {
      // It is, so return the default value
      return defaultValue;
    }
    
    // Declare our boolean variable that gets returned.  The default
    // value is false.
    boolean val = false;
    
    // Convert the string into a boolean
    if ((str.equals("1")) || (str.equals("true")))
    {
      // Set val to true for certain string values
      val = true;
    }
    
    // Return the boolean value
    return val;
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public int getIntegerProperty(final String sectionName,
                                final String propertyName)
  {
    return getIntegerProperty(sectionName, propertyName, 0);
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param defaultValue the default value to return, if it's not found
   * @return the value for the specified section/property
   */
  public int getIntegerProperty(final String sectionName,
                                final String propertyName,
                                final int defaultValue)
  {
    // Get the string from the input file
    final String str = getStringProperty(sectionName, propertyName);
    
    // Check if the returned string is null
    if (str == null)
    {
      // It is, so return the default value
      return defaultValue;
    }
    
    // Declare our variable that gets returned.  Set it to
    // the default value in case an exception is thrown while
    // parsing the string.
    int val = defaultValue;
    
    // Parse the string as a number
    try
    {
      // Parse the string
      int tempValue = Integer.parseInt(str);
      
      // If we reach this point, it was a success
      val = tempValue;
    }
    catch (NumberFormatException nfe)
    {
      val = defaultValue;
    }
    
    // Return the value
    return val;
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public long getLongProperty(final String sectionName,
                              final String propertyName)
  {
    return getLongProperty(sectionName, propertyName, 0L);
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param defaultValue the default value to return, if it's not found
   * @return the value for the specified section/property
   */
  public long getLongProperty(final String sectionName,
                              final String propertyName,
                              final long defaultValue)
  {
    // Get the string from the input file
    final String str = getStringProperty(sectionName, propertyName);
    
    // Check if the returned string is null
    if (str == null)
    {
      // It is, so return the default value
      return defaultValue;
    }
    
    // Declare our variable that gets returned.  Set it to
    // the default value in case an exception is thrown while
    // parsing the string.
    long val = defaultValue;
    
    // Parse the string as a number
    try
    {
      // Parse the string
      long tempValue = Long.parseLong(str);
      
      // If we reach this point, it was a success
      val = tempValue;
    }
    catch (NumberFormatException nfe)
    {
      val = defaultValue;
    }
    
    // Return the value
    return val;
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public double getDoubleProperty(final String sectionName,
                                  final String propertyName)
  {
    return getDoubleProperty(sectionName, propertyName, 0.0);
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param defaultValue the default value to return, if it's not found
   * @return the value for the specified section/property
   */
  public double getDoubleProperty(final String sectionName,
                                  final String propertyName,
                                  final double defaultValue)
  {
    // Get the string from the input file
    final String str = getStringProperty(sectionName, propertyName);
    
    // Check if the returned string is null
    if (str == null)
    {
      // It is, so return the default value
      return defaultValue;
    }
    
    // Declare our variable that gets returned.  Set it to
    // the default value in case an exception is thrown while
    // parsing the string.
    double val = defaultValue;
    
    // Parse the string as a number
    try
    {
      // Parse the string
      double tempValue = Double.parseDouble(str);
      
      // If we reach this point, it was a success
      val = tempValue;
    }
    catch (NumberFormatException nfe)
    {
      val = defaultValue;
    }
    
    // Return the value
    return val;
  }
  
  
  /**
   * Returns the property value for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public Date getDateProperty(final String sectionName,
                              final String propertyName)
  {
    // Get the string from the input file
    final String str = getStringProperty(sectionName, propertyName);
    
    // Check if the returned string is null
    if (str == null)
    {
      // It is, so return the default value
      return null;
    }
    
    // Declare our variable that gets returned.  Set it to
    // the default value in case an exception is thrown while
    // parsing the string.
    Date date = null;
    
    // Parse the string as a number
    try
    {
      // Parse the string
      long tempValue = Long.parseLong(str);
      
      // If we reach this point, the long was parsed correctly
      if (tempValue >= 0)
      {
        // Create a Date object using the temp value
        date = new Date(tempValue);
      }
    }
    catch (NumberFormatException nfe)
    {
      // Default to the current time
      date = new Date(System.currentTimeMillis());
    }
    
    // Return the value
    return date;
  }
  
  
  /**
   * Returns a string property for the specified section and
   * property.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @return the value for the specified section/property
   */
  public String getStringProperty(final String sectionName,
                                  final String propertyName)
  {
    // Make null the default value if the section/property combo is not found
    return getStringProperty(sectionName, propertyName, null);
  }
  
  
  /**
   * Returns a string property for the specified section and
   * property.  If the property is not found, defaultValue
   * is returned instead.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param defaultValue the default value to return, if it's not found
   * @return the value for the specified section/property
   */
  public String getStringProperty(final String sectionName,
                                  final String propertyName,
                                  final String defaultValue)
  {
    // Check the section name
    if ((sectionName == null) || (sectionName.length() < 1))
    {
      throw new RuntimeException("The name of the section has not been specified");
    }
    // Check the property name
    else if ((propertyName == null) || (propertyName.length() < 1))
    {
      throw new RuntimeException("The name of the property has not been specified");
    }
    
    // Verify the file exists and is a file
    if (!iniFileExists())
    {
      return defaultValue;
    }
    
    // The current section name
    String currentSection = null;
    
    // The current property name
    String currentProperty = null;
    
    // This will hold the value that gets returned
    String value = defaultValue;
    
    // Declare the reader
    BufferedReader in = null;
    
    // Open the input file and gather the section names
    try
    {
      // Open the file reader
      in = new BufferedReader(new FileReader(iniFileName));
      
      // This will hold each line read from the file
      String str;
      
      // Whether to continue processing
      boolean bContinue = true;
      
      // Read each line until we hit the end of the file
      while ((bContinue) && (str = in.readLine()) != null)
      {
        // Check if the line has some length and starts with a [
        if ((str.length() > 2) && (str.charAt(0) == '['))
        {
          // Check if the line has a ]
          int lastIndex = str.indexOf(']');
          if (lastIndex > 0)
          {
            // It does, so save everything between the [ and ] as a section name
            currentSection = str.substring(1, lastIndex);
          }
        }
        else
        {
          // Check if we're in the specified section
          if ((currentSection != null) && (currentSection.equals(sectionName)))
          {
            // Save the property name (ignore comments)
            final int commentIndex = str.indexOf(';');
            final int equalsIndex = str.indexOf('=');
            if ((equalsIndex > 0) && ((commentIndex < 0) || (commentIndex > equalsIndex)))
            {
              currentProperty = str.substring(0, equalsIndex).trim();
            }
            else
            {
              currentProperty = null;
            }
            
            // See if we're in the right property
            if ((currentProperty != null) && (currentProperty.equals(propertyName)))
            {
              // We are, so check the line
              if (equalsIndex < (str.length() - 1))
              {
                // There is a string after the equals sign, so save it
                value = str.substring(equalsIndex + 1);
                
                // Stop processing
                bContinue = false;
              }
              else
              {
                // The line ends with an '=', so set the value to an
                // empty string and stop reading the input file
                value = "";
                bContinue = false;
              }
            }
          }
        }
      }
      
      // Close the input stream
      in.close();
      in = null;
    }
    catch (IOException ioe)
    {
      // Verify the file reader is closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (Exception e)
        {
          in = null;
        }
        
        in = null;
      }
      
      // Throw the exception
      throw new RuntimeException(ioe.getMessage());
    }
    
    // Return the value for this property
    return value;
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setBooleanProperty(final String sectionName,
                                    final String propertyName,
                                    final boolean value)
  {
    // Save the value as a string
    final String sValue = Boolean.toString(value);
    
    // Pass the string to setStringProperty
    return setStringProperty(sectionName, propertyName, sValue);
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setIntegerProperty(final String sectionName,
                                    final String propertyName,
                                    final int value)
  {
    // Save the value as a string
    final String sValue = Integer.toString(value);
    
    // Pass the string to setStringProperty
    return setStringProperty(sectionName, propertyName, sValue);
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setLongProperty(final String sectionName,
                                 final String propertyName,
                                 final long value)
  {
    // Save the value as a string
    final String sValue = Long.toString(value);
    
    // Pass the string to setStringProperty
    return setStringProperty(sectionName, propertyName, sValue);
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setDoubleProperty(final String sectionName,
                                   final String propertyName,
                                   final double value)
  {
    // Save the value as a string
    final String sValue = Double.toString(value);
    
    // Pass the string to setStringProperty
    return setStringProperty(sectionName, propertyName, sValue);
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setDateProperty(final String sectionName,
                                 final String propertyName,
                                 final Date value)
  {
    // Check the date
    if (value == null)
    {
      // It's null, so pass null to setStringProperty
      return setStringProperty(sectionName, propertyName, null);
    }
    
    // Convert the date to milliseconds
    final long lDateInMS = value.getTime();
    
    // Convert the milliseconds to a string
    final String sDate = Long.toString(lDateInMS);
    
    // Pass the milliseconds string to setStringProperty
    return setStringProperty(sectionName, propertyName, sDate);
  }
  
  
  /**
   * Writes the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  public boolean setStringProperty(final String sectionName,
                                   final String propertyName,
                                   final String value)
  {
    // Declare the result code
    boolean rc = true;
    
    // Check the inputs
    if ((sectionName == null) || (propertyName == null))
    {
      // The section and property cannot be null
      return false;
    }
    else if ((sectionName.length() < 1) || (propertyName.length() < 1))
    {
      // The section and property cannot be empty
      return false;
    }
    
    // Save whether the input file exists
    final boolean fileExists = iniFileExists();
    
    // See if the section is already in the file
    final boolean sectionExists = containsSection(sectionName);
    
    // Handle the case of the section not existing
    if (!sectionExists)
    {
      // Add the section, property and value to the file
      rc = addToFile(sectionName, propertyName, value, fileExists);
    }
    else
    {
      // Handle the case of the section existing. Get the property's
      // current value
      final String oldValue = getStringProperty(sectionName, propertyName);
      
      // If oldValue is not null, then the property exists; handle
      // that case first
      if (oldValue != null)
      {
        // The property exists.  See if it has the value we want to save.
        if ((value != null) && (oldValue.equals(value)))
        {
          // The values match, so there's nothing to do
          rc = true;
        }
        else
        {
          // The section and property exist, so update property
          rc = updatePropertyInSection(sectionName, propertyName, value);
        }
      }
      else
      {
        // The section exists, but not the property, so we have
        // to add the property to the section
        rc = addPropertyToSection(sectionName, propertyName, value);
      }
    }
    
    // Return the result of the operation
    return rc;
  }
  
  
  /**
   * Modifies the property's value for the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @param fileExists whether the file exists
   * @return the success of the operation
   */
  private boolean addToFile(final String sectionName,
                            final String propertyName,
                            final String value,
                            final boolean fileExists)
  {
    // Our return code
    boolean rc = true;
    
    // Declare our file writer
    BufferedWriter out = null;
    
    // Append a blank line, and the the section, and property=value
    try
    {
      // Open the file for appending
      out = new BufferedWriter(new FileWriter(iniFileName, true));
      
      // If the file exists, write two newlines to the file
      if (fileExists)
      {
        out.write(lineSep);
        out.write(lineSep);
      }
      
      // Build the string to write out
      StringBuilder sb = new StringBuilder(100);
      
      // Write the section name
      sb.append("[").append(sectionName).append("]").append(lineSep);
      
      // Check if value is null
      if (value != null)
      {
        // It's not null, so write the property and value
        sb.append(propertyName).append("=").append(value).append(lineSep);
      }
      
      // Append the string to the end of the file
      out.write(sb.toString());
      
      // Close the output buffer
      out.close();
      out = null;
    }
    catch (IOException ioe)
    {
      rc = false;
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          out = null;
        }
        
        out = null;
      }
      
      // Throw the exception
      throw new RuntimeException(ioe.getMessage());
    }
    
    return rc;
  }
  
  
  /**
   * Modifies the property's value for the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  private boolean updatePropertyInSection(final String sectionName,
                                          final String propertyName,
                                          final String value)
  {
    // We need to replace the line in the file, so
    // read all lines from the file and store here
    List<String> fileLines = getLinesFromFile();
    
    // Store the lines we will later write to the file
    List<String> outLines = new ArrayList<String>(fileLines.size());
    
    // This is the name of the current section
    String currSection = null;
    
    // Iterate over the lines
    for (String line : fileLines)
    {
      // Record whether this is the line with the right
      // property and we're in the right section
      boolean bFound = false;
      
      // See if we're in a new section
      final String tempSection = getSectionNameFromLine(line);
      if (tempSection != null)
      {
        // We're at the start of a section, so save the name
        currSection = tempSection;
      }
      else
      {
        // Check if we're in the right section
        if ((currSection != null) && (currSection.equals(sectionName)))
        {
          // Get the name of the property on this line
          final String currProp = getPropertyNameFromLine(line);
          
          // Check if the property name matches
          if ((currProp != null) && (currProp.equals(propertyName)))
          {
            // Check if value is null
            if (value == null)
            {
              // It is, so don't write this line
              bFound = true;
            }
            else
            {
              // We found the property
              bFound = true;
              
              // Build the line
              StringBuilder sb = new StringBuilder(100);
              sb.append(propertyName).append("=").append(value);
              
              // Save it to the output array
              outLines.add(sb.toString());
            }
          }
        }
      }
      
      // Check if the line was found
      if (!bFound)
      {
        // It wasn't, so we can just write out the line
        outLines.add(line);
      }
    }
    
    // Write outLines to the file
    writeArrayToFile(outLines);
    
    // Return success
    return true;
  }
  
  
  /**
   * Adds the propertyName=value to the specified section.
   * 
   * @param sectionName the name of the section
   * @param propertyName the name of the property
   * @param value the property value
   * @return the success of the operation
   */
  private boolean addPropertyToSection(final String sectionName,
                                       final String propertyName,
                                       final String value)
  {
    // If value is null, just return
    if (value == null)
    {
      return true;
    }
    
    // Read all lines from the file and store here
    List<String> fileLines = getLinesFromFile();
    
    // Store the lines we will later write to the file
    List<String> outLines = new ArrayList<String>(fileLines.size() + 1);
    
    // This is the name of the current section
    String currSection = null;
    
    // Save the name of the previous section
    String prevSection = null;
    
    // Save whether we hit the end of the matching section
    boolean foundSectionEnd = false;
    
    // Iterate over the lines
    for (String line : fileLines)
    {
      // Record whether this is the line with the right
      // property and we're in the right section
      boolean bFound = false;
      
      // See if we're in a new section
      final String tempSection = getSectionNameFromLine(line);
      if (tempSection != null)
      {
        // We're at the start of a section, so update the
        // prevSection variable and then update currSection
        prevSection = currSection;
        currSection = tempSection;
        
        if ((prevSection != null) &&
            (prevSection.equals(sectionName)) &&
            (!foundSectionEnd))
        {
          // We got to the section after the one we wanted, and
          // we didn't write out the property line, so there must
          // not have been a blank line at the end of the section.
          // Now we need to write out the property line, followed
          // by a blank line.
          
          // Build the line
          StringBuilder sb = new StringBuilder(100);
          sb.append(propertyName).append("=").append(value);
          sb.append(lineSep);
          
          // Save it to the output array
          outLines.add(sb.toString());
          
          // Update that we hit the end of the section
          foundSectionEnd = true;
        }
      }
      else
      {
        // Check that we're in a section
        if (currSection != null)
        {
          // Check that we're in the right section
          if (currSection.equals(sectionName))
          {
            // See if we've not written the property yet,
            // and the line is empty
            if ((!foundSectionEnd) && (line.length() < 1))
            {
              // The line is blank, so assume we're at the end
              // of a section; add the property name here
              // We found the property
              bFound = true;
              
              // Build the line
              StringBuilder sb = new StringBuilder(100);
              sb.append(propertyName).append("=").append(value);
              sb.append(lineSep);
              
              // Save it to the output array
              outLines.add(sb.toString());
              
              // Update that we hit the end of the section
              foundSectionEnd = true;
            }
          }
        }
      }
      
      // Check if the line was found
      if (!bFound)
      {
        // It wasn't, so we can just write out the line
        outLines.add(line);
      }
    }
    
    // Check if we never found the section end
    if (!foundSectionEnd)
    {
      // The section must have been the last section
      // in the file, and didn't end with a blank
      // line, so add the line now.
      
      // Build the line
      StringBuilder sb = new StringBuilder(100);
      sb.append(propertyName).append("=").append(value);
      
      // Save it to the output array
      outLines.add(sb.toString());
    }
    
    // Write outLines to the file
    writeArrayToFile(outLines);
    
    // Return success
    return true;
  }
  
  
  /**
   * Write the array of Strings to the output file.
   * 
   * @param outLines the array of Strings to write
   */
  private void writeArrayToFile(final List<String> outLines)
  {
    // Check the input
    if (outLines == null)
    {
      return;
    }
    
    // Declare our file writer
    BufferedWriter out = null;
    
    // Append a blank line, and the the section, and property=value
    try
    {
      // Open the file for writing
      out = new BufferedWriter(new FileWriter(iniFileName, false));
      
      // Iterate over the array
      for (String str : outLines)
      {
        // Write each line, followed by a line separator
        out.write(str);
        out.write(lineSep);
      }
      
      // Close the output buffer
      out.close();
      out = null;
    }
    catch (IOException ioe)
    {
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          out = null;
        }
        
        out = null;
      }
      
      // Throw the exception
      throw new RuntimeException(ioe.getMessage());
    }
  }
  
  
  /**
   * Parse the property name from the line.
   * 
   * @param line the line from the input file
   * @return the property name, or null if this isn't a property line
   */
  private String getPropertyNameFromLine(final String line)
  {
    // Check the input
    if ((line == null) || (line.length() < 2))
    {
      return null;
    }
    
    // Save the property name (ignore comments)
    final int commentIndex = line.indexOf(';');
    final int equalsIndex = line.indexOf('=');
    if ((equalsIndex > 0) && ((commentIndex < 0) || (commentIndex > equalsIndex)))
    {
      String propName = line.substring(0, equalsIndex).trim();
      return propName;
    }
    
    // The line does not have a property
    return null;
  }
  
  
  /**
   * Parse the section name from the line.
   * 
   * @param line the input line from the file
   * @return the parsed section name, or null if there is no section name
   */
  private String getSectionNameFromLine(final String line)
  {
    // Check the input
    if ((line == null) || (line.length() < 3))
    {
      return null;
    }
    
    // Check if the line starts with a [
    if (line.charAt(0) == '[')
    {
      // Check if the line has a ]
      int lastIndex = line.indexOf(']');
      if (lastIndex > 0)
      {
        // It does, so save everything between the [ and ] as a section name
        return (line.substring(1, lastIndex));
      }
    }
    
    // No section name found
    return null;
  }


  /**
   * Read the INI file into memory.
   * 
   * @return the contents of the INI file
   */
  private List<String> getLinesFromFile()
  {
    // This will hold the file contents
    List<String> fileLines = new ArrayList<String>(200);
    
    // Declare our file reader
    BufferedReader in = null;
    
    // Open the input file and gather the section names
    try
    {
      // Open the file reader
      in = new BufferedReader(new FileReader(iniFileName));
      
      // This will hold each line read from the file
      String str;
      
      // Read each line until we hit the end of the file
      while ((str = in.readLine()) != null)
      {
        fileLines.add(str);
      }
      
      // Close the input stream
      in.close();
      in = null;
    }
    catch (IOException ioe)
    {
      // Verify the file reader is closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (Exception e)
        {
          in = null;
        }
        
        in = null;
      }
      
      // Throw the exception
      throw new RuntimeException(ioe.getMessage());
    }
    
    // Return the array of lines
    return fileLines;
  }


  /**
   * Return whether the specified section name is in the
   * input file.
   * 
   * @param sectionName the section name of interest
   * @return whether sectionName is a section in the file
   */
  private boolean containsSection(final String sectionName)
  {
    // Get the list of sections
    Set<String> set = getAllSectionNamesAsHashSet();
    if (set == null)
    {
      // No sections in the file
      return false;
    }
    
    // See if the section is in the set
    return (set.contains(sectionName));
  }
  
  
  /**
   * Return an array of the names of all properties in the given section.
   * 
   * @param sectionName the name of the section of interest
   * @return an array of the property names in the section
   */
  public List<String> getAllPropertyNames(final String sectionName)
  {
    // Check the input parameter
    if ((sectionName == null) || (sectionName.length() < 1))
    {
      throw new RuntimeException("The name of the section was not specified");
    }
    
    // Verify the file exists and is a file
    if (!iniFileExists())
    {
      return null;
    }
    
    // Declare our array to hold the string that will get returned
    List<String> props = new ArrayList<String>(20);
    
    // The current section name
    String currentSection = null;
    
    // Declare the file reader
    BufferedReader in = null;
    
    // Open the input file and gather the section names
    try
    {
      // Open the file reader
      in = new BufferedReader(new FileReader(iniFileName));
      
      // This will hold each line read from the file
      String str;
      
      // Read each line until we hit the end of the file
      while ((str = in.readLine()) != null)
      {
        // Try to parse this line as a section name
        String tempSectionName = getSectionNameFromLine(str);
        
        // See if it was successful
        if (tempSectionName != null)
        {
          // A section name was returned
          currentSection = tempSectionName;
        }
        else
        {
          // This line does not have a section name
          if ((currentSection != null) && (currentSection.equals(sectionName)))
          {
            // Try to parse the property name from the line
            String propName = getPropertyNameFromLine(str);
            if (propName != null)
            {
              props.add(propName);
            }
          }
        }
      }
      
      // Close the input stream
      in.close();
      in = null;
    }
    catch (IOException ioe)
    {
      // Verify the file reader is closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (Exception e)
        {
          in = null;
        }
        
        in = null;
      }
      
      // Throw the exception
      throw new RuntimeException(ioe.getMessage());
    }
    
    // Return the list of section names
    return props;
  }
  
  
  /**
   * Return the section names as a hash set.
   * 
   * @return a hash set of section names
   */
  public Set<String> getAllSectionNamesAsHashSet()
  {
    // Get the list of section names
    List<String> sections = getAllSectionNames();
    if (sections == null)
    {
      return null;
    }
    
    // This will hold the data we return
    Set<String> sectionsSet = new HashSet<String>(sections.size());
    
    // Now build the set
    sectionsSet.addAll(sections);
    
    // Return the set
    return sectionsSet;
  }
  
  
  /**
   * Return an array of section names.
   * 
   * @return an array of section names
   */
  public List<String> getAllSectionNames()
  {
    // Declare our array to hold the string that will get returned
    List<String> sections = new ArrayList<String>(20);
    
    // Verify the file exists and is a file
    if (!iniFileExists())
    {
      return sections;
    }
    
    // Declare our file reader
    BufferedReader in = null;
    
    // Open the input file and gather the section names
    try
    {
      // Open the file reader
      in = new BufferedReader(new FileReader(iniFileName));
      
      // This will hold each line read from the file
      String str;
      
      // Read each line until we hit the end of the file
      while ((str = in.readLine()) != null)
      {
        String tempName = getSectionNameFromLine(str);
        if (tempName != null)
        {
          sections.add(tempName);
        }
      }
      
      // Close the input stream
      in.close();
      in = null;
    }
    catch (IOException ioe)
    {
      // Verify the file reader is closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (Exception e)
        {
          in = null;
        }
        
        in = null;
      }
      
      throw new RuntimeException(ioe.getMessage());
    }
    
    // Return the list of section names
    return sections;
  }
  
  
  /**
   * Return whether the file exists.  This will throw an
   * exception if the file name variable is null or
   * empty, or if the filename points to a directory.
   * 
   * @return whether the file exists
   */
  private boolean iniFileExists()
  {
    // Check the input filename
    if ((iniFileName == null) || (iniFileName.length() < 1))
    {
      throw new RuntimeException("The name of the INI file has not been specified");
    }
    
    // Declare our result code variable
    boolean rc = true;
    
    // Create a file object for the file
    File file = new File(iniFileName);
    
    // Check if the file exists
    if (!file.exists())
    {
      // The file doesn't exist
      rc = false;
    }
    // Check if the file is a directory
    else if (!file.isFile())
    {
      // The file is a directory
      file = null;
      throw (new RuntimeException("The specified file is a directory"));
    }
    
    // Set the file object to null
    file = null;
    
    // Return the result code
    return rc;
  }
  
  
  /**
   * Returns the name of the underlying INI file.
   * 
   * @return the name of the INI file
   */
  public String getIniFilename()
  {
    return iniFileName;
  }
}
