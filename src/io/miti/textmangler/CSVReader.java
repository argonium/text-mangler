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

import java.util.ArrayList;
import java.util.List;

/**
 * Parse a line of comma-separated values.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class CSVReader
{
  /*
   * The following four variables can be viewed and
   * modified by the user.
   */
  
  /**
   * Whether to skip the first line of input.
   */
  private boolean bSkipFirstLine = false;
  
  /**
   * Whether the trim each field.
   */
  private boolean bTrimFields = false;
  
  /**
   * The quoting character.
   */
  private char cQuote = '"';
  
  /**
   * The list of field separators.
   */
  private String sSeparators = ",";
  
  
  /**
   * Default constructor.  The default values are to trim fields,
   * don't skip the first line, the quote character is the double
   * quotes ("), and use the comma as the only field separator.
   */
  public CSVReader()
  {
    // Call the parent constructor
    super();
  }
  
  
  /**
   * Constructor taking an argument of whether to trim the fields.
   * 
   * @param trimFields whether to trim each field
   */
  public CSVReader(final boolean trimFields)
  {
    // Call the parent constructor
    super();
    
    // Save the value passed to this constructor
    trimFields(trimFields);
  }
  
  
  /**
   * Constructor taking arguments of whether to trim the fields
   * and whether to skip the first line.
   * 
   * @param trimFields whether to trim each field
   * @param skipFirstLine whether to skip the first line
   */
  public CSVReader(final boolean trimFields, final boolean skipFirstLine)
  {
    // Call the parent constructor
    super();
    
    // Save the values passed to this constructor
    trimFields(trimFields);
    skipFirstLine(skipFirstLine);
  }
  
  
  /**
   * Constructor taking arguments of whether to trim the fields,
   * whether to skip the first line, and what quoting character
   * to use.
   * 
   * @param trimFields whether to trim each field
   * @param skipFirstLine whether to skip the first line
   * @param quote the quote character to use (typically ' or ")
   */
  public CSVReader(final boolean trimFields, final boolean skipFirstLine,
                   final char quote)
  {
    // Call the parent constructor
    super();
    
    // Save the values passed to this constructor
    trimFields(trimFields);
    skipFirstLine(skipFirstLine);
    setQuote(quote);
  }
  
  
  /**
   * Constructor taking arguments of whether to trim the fields,
   * whether to skip the first line, what quoting character to
   * use, and what field separators to use.
   * 
   * @param trimFields whether to trim each field
   * @param skipFirstLine whether to skip the first line
   * @param quote the quote character to use (typically ' or ")
   * @param separators the list of field separators
   */
  public CSVReader(final boolean trimFields, final boolean skipFirstLine,
                   final char quote, final String separators)
  {
    // Call the parent constructor
    super();
    
    // Save the values passed to this constructor
    trimFields(trimFields);
    skipFirstLine(skipFirstLine);
    setQuote(quote);
    setSeparators(separators);
  }
  
  
  /**
   * Parses the specified line.
   * 
   * @param sInput the line of data to parse
   * @return the list of Strings of data parsed from the input line
   */
  public List<String> parseLine(final String sInput)
  {
    // Allocate an array to hold the items
    List<String> list = new ArrayList<String>(20);
    
    // Check the input string
    if (sInput == null)
    {
      return null;
    }
    else if (sInput.length() < 1)
    {
      // Add an empty String to the list
      list.add("");
      
      // Return the list variable
      return list;
    }
    
    // Check the field separators
    if ((sSeparators == null) || (sSeparators.length() < 1))
    {
      // No separators, so add the whole line.  Trim if necessary.
      if (bTrimFields)
      {
        list.add(sInput.trim());
      }
      else
      {
        list.add(sInput);
      }
      
      // Return the list variable
      return list;
    }
    
    // Build the list
    final int nLen = sInput.length();
    int i = 0;
    while (i < nLen)
    {
      // Check if the user wants the field trimmed
      if (bTrimFields)
      {
        // Remove the leading spaces
        while ((i < nLen) && (sInput.charAt(i) == ' '))
        {
          ++i;
        }
        
        if (i == nLen)
        {
          break;
        }
      }
      
      // Check for a leading quote
      if (sInput.charAt(i) == cQuote)
      {
        // Read until closing quote
        ++i;
        boolean bInQuote = true;
        StringBuffer buf = new StringBuffer(20);
        while (i < nLen)
        {
          if (sInput.charAt(i) == cQuote)
          {
            if (i >= (nLen - 1))
            {
              break;
            }
            else if (sInput.charAt(i + 1) == cQuote)
            {
              // The user entered 2 quotes in a row, inside a quote
              buf.append("'");
              ++i;
            }
            else
            {
              bInQuote = !bInQuote;
            }
          }
          else if ((sSeparators.indexOf(sInput.charAt(i)) >= 0) && (!bInQuote))
          {
            break;
          }
          else
          {
            buf.append(sInput.charAt(i));
          }
          
          ++i;
        }
        list.add(buf.toString());
        ++i;
      }
      else if (sSeparators.indexOf(sInput.charAt(i)) >= 0)
      {
        // Empty field
        list.add("");
        ++i;
      }
      else
      {
        // Build the string until we hit another comma
        StringBuffer buf = new StringBuffer(20);
        while ((i < nLen) && (sSeparators.indexOf(sInput.charAt(i)) < 0))
        {
          buf.append(sInput.charAt(i++));
        }
        
        if (bTrimFields)
        {
          list.add(buf.toString().trim());
        }
        else
        {
          list.add(buf.toString());
        }
        
        ++i;
      }
    } // while (i < nLen) - end
    
    // Check for a trailing comma
    if (sSeparators.indexOf(sInput.charAt(nLen - 1)) >= 0)
    {
      list.add("");
    }
    
    // Return the list as an iterator
    return list;
  }
  
  
  /**
   * Whether we're skipping the first row.
   * 
   * @return if we're skipping the first row
   */
  public boolean isSkippingFirstLine()
  {
    return bSkipFirstLine;
  }
  
  
  /**
   * Set the value for skipping the first row.
   * 
   * @param skipFirstLine whether we shold skip the first row
   */
  public void skipFirstLine(final boolean skipFirstLine)
  {
    bSkipFirstLine = skipFirstLine;
  }
  
  
  /**
   * Return whether we're trimming the fields.
   * 
   * @return if we're trimming the fields
   */
  public boolean isTrimmingFields()
  {
    return bTrimFields;
  }
  
  
  /**
   * Constructor taking the boolean of whether to trim the fields.
   * 
   * @param trimFields whether to trim each field
   */
  public void trimFields(final boolean trimFields)
  {
    bTrimFields = trimFields;
  }
  
  
  /**
   * Return the quote character.
   * 
   * @return the quote character
   */
  public char getQuote()
  {
    return cQuote;
  }
  
  
  /**
   * Set the quote character.
   * 
   * @param quote the new quote character
   */
  public void setQuote(final char quote)
  {
    cQuote = quote;
  }
  
  
  /**
   * Get the field separators.
   * 
   * @return the field separators
   */
  public String getSeparators()
  {
    return sSeparators;
  }
  
  
  /**
   * Set the column delimiters.
   * 
   * @param separators the field separators
   */
  public void setSeparators(final String separators)
  {
    sSeparators = separators;
  }
  
  
  /**
   * Write the string to standard out.
   * 
   * @param line the string to write out
   */
  public static void write(final String line)
  {
    System.out.println(line);
  }
  
  
  /**
   * Write the array of strings to standard out.
   * 
   * @param line the array of strings to write out
   */
  public static void writeLine(final List<String> line)
  {
    if (line == null)
    {
      write("The line is null");
    }
    else if (line.size() < 1)
    {
      write("The line is empty");
    }
    else
    {
      final int nSize = line.size();
      for (int i = 0; i < nSize; ++i)
      {
        StringBuffer buf = new StringBuffer(100);
        buf.append("#").append(Integer.toString(i + 1))
           .append(": '").append(line.get(i)).append("'");
        write(buf.toString());
      }
    }
  }
}
