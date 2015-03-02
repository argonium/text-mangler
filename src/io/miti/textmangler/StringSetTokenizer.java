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
import java.util.HashSet;
import java.util.List;

/**
 * Handle tokenizing a string and check for repeats of a token
 * delimiter within the same cluster of delimiters.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class StringSetTokenizer
{
  /**
   * The input data to be tokenized.
   */
  private String input;
  
  /**
   * The set of delimiters.
   */
  private String delimiters;
  
  /**
   * The list of parsed substrings.
   */
  private List<String> output;
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private StringSetTokenizer()
  {
    super();
    input = null;
    delimiters = null;
    
    // Initialize the output variable
    parseData();
  }
  
  
  /**
   * Constructor taking the input data and delimiters.
   * 
   * @param inputData the data to tokenize
   * @param delims the set of delimiters
   */
  public StringSetTokenizer(final String inputData, final String delims)
  {
    input = inputData;
    delimiters = delims;
    
    // Parse the data
    parseData();
  }
  
  
  /**
   * Returns whether there are more tokens in the input data.
   * 
   * @return whether there are more tokens in the input data
   */
  public boolean hasMoreTokens()
  {
    // If there are any elements in the output array,
    // then there are more tokens
    return (output.size() > 0);
  }
  
  
  /**
   * Return the next String token.
   * 
   * @return the next String token
   */
  public String nextToken()
  {
    // Return the first token, after removing it
    return (output.remove(0));
  }
  
  
  /**
   * Parse the input data, based on the pattern.
   */
  private void parseData()
  {
    // Declare the output String array
    output = new ArrayList<String>(50);
    
    // Check the input
    if (input == null)
    {
      return;
    }
    else if ((input.length() < 1) || (delimiters == null) || (delimiters.length() < 1))
    {
      // Either the input string is empty, or the delimiters set is null
      // or empty.  Whichever the reason, add the input string to the list
      // and return it.
      output.add(input);
      return;
    }
    
    // The start of the current string to add to the array
    int lastStartIndex = 0;
    
    // This holds the set of delimiters found in the current zone.
    // When we hit a repeat, we empty the list and readd the current
    // token.
    HashSet<Integer> setDelims = new HashSet<Integer>(20);
    
    // Whether we're in a zone of delimiters
    boolean inZone = false;
    
    // Save the length of the input string
    final int len = input.length();
    
    // Iterate over each character in the input string
    for (int i = 0; i < len; ++i)
    {
      // Get the current character
      char ch = input.charAt(i);
      
      // Record whether this character is a delimiter
      final int nDelimIndex = delimiters.indexOf(ch);
      
      // See if this character is a delimiter
      if (nDelimIndex >= 0)
      {
        // We're at a delimiter.  See if we were already in a zone.
        if (inZone)
        {
          // We were already in a zone.  See if we've hit this character before.
          if (setDelims.contains(nDelimIndex))
          {
            // This character is a repeat in this zone, so we add a new line
            output.add("");
            
            // Clear the hash, so that after the next line, this character
            // will be the only one in the set
            setDelims.clear();
          }
          
          // Add this character to the delimiter set
          setDelims.add(nDelimIndex);
        }
        else
        {
          // We're at the start of a zone
          setDelims.clear();
          setDelims.add(nDelimIndex);
          inZone = true;
          
          // Add the string before this delimiter to our output array
          // output.add(input.substring(lastStartIndex, i));
          output.add(input.substring(lastStartIndex, i));
        }
        
        // Set the lastStartIndex to point to the next character
        lastStartIndex = i + 1;
      }
      else
      {
        // This is not a delimiter.  See if we were previously in a zone.
        if (inZone)
        {
          // We were in a zone.  We're not any longer.
          inZone = false;
          setDelims.clear();
          
          // When we hit a delimiter, add the string starting here
          lastStartIndex = i;
        }
      }
    }
    
    // Add the final string to the output
    output.add(input.substring(lastStartIndex));
  }
}
