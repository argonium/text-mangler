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

/**
 * Home of various utility methods.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Utility
{
  /**
   * Default constructor.  Private should it should not be constructed.
   */
  private Utility()
  {
    super();
  }
  
  
  /**
   * Returns whether prevTag immediately precedes the line
   * substring starting at tagIndex.
   * 
   * @param line the complete line
   * @param tagIndex the start of the tag within line
   * @param prevTag the previous tag to check for
   * @return whether prevTag precedes tagIndex in line
   */
  public static boolean precededBy(final String line,
                                   final int tagIndex,
                                   final String prevTag)
  {
    // Compute where the previous tag must start
    final int prevTagStart = tagIndex - prevTag.length();
    
    // Check if there's room before the tag for the prevTag
    if (prevTagStart < 0)
    {
      // No room for it
      return false;
    }
    
    // Compare the previous tag with the substring
    return (prevTag.equals(line.substring(prevTagStart, tagIndex)));
  }
  
  
  /**
   * Look for characters preceded by a backslash, and treat as control characters.
   * 
   * @param line the string of characters
   * @return the input with replacements for backslashed characters
   */
  public static String fixSpecialCharacters(final String line)
  {
    // Check the input
    if (line == null)
    {
      // The input string is null
      return null;
    }
    else if (line.length() < 1)
    {
      // The input string is empty
      return line;
    }
    else if (line.indexOf('\\') < 0)
    {
      // The input string has no backslashes
      return line;
    }
    
    // Save the length
    final int nLen = line.length();
    
    // Create our output string builder
    StringBuilder sb = new StringBuilder(nLen);
    
    // Iterate over the characters
    for (int i = 0; i < nLen; ++i)
    {
      // Get the current character
      final char ch = line.charAt(i);
      
      // Check if it should be handled specially
      if (ch == '\\')
      {
        // If this is the last character, just add it
        if (i == (nLen - 1))
        {
          sb.append(ch);
        }
        else
        {
          // Skip to the next character
          ++i;
          
          // Get the new character
          char nextChar = line.charAt(i);
          
          // Check if it's a letter
          if ((Character.isLowerCase(nextChar)) || (Character.isUpperCase(nextChar)))
          {
            // We have a letter, so convert it to a control character.
            // First check if it's uppercase
            if (Character.isUpperCase(nextChar))
            {
              // Convert the letter to lowercase
              nextChar = Character.toLowerCase(nextChar);
            }
            
            // Check for common line delimiters
            if (nextChar == 'r')
            {
              sb.append('\r');
            }
            else if (nextChar == 'n')
            {
              sb.append('\n');
            }
            else if (nextChar == 'b')
            {
              sb.append('\b');
            }
            else if (nextChar == 't')
            {
              sb.append('\t');
            }
            else if (nextChar == 'f')
            {
              sb.append('\f');
            }
            else
            {
              // Some other character, so just add it
              sb.append(nextChar);
            }
          }
          else
          {
            // It's not a letter, so just add it
            sb.append(ch);
          }
        }
      }
      else
      {
        // Not a special character, so just add it
        sb.append(ch);
      }
    }
    
    
    // Return our built string
    return (sb.toString());
  }
  
  
  /**
   * Converts the string to title case.
   * 
   * @param inStr the string to make upper case
   * @return the string parameter, in upper case
   */
  public static String toTitleCase(final String inStr)
  {
    // Check for a null or empty string
    if ((inStr == null) || (inStr.length() < 1))
    {
      return "";
    }
    else
    {
      // Save the length
      final int nLen = inStr.length();

      // If one character, make it uppercase and return it
      if (nLen == 1)
      {
        return inStr.toUpperCase();
      }
      
      // Set this to true because we want to make the first character uppercase
      boolean blankFound = true;
      
      // Save the string to a stringbuffer
      StringBuilder buf = new StringBuilder(inStr.toLowerCase());
      
      // Traverse the character array
      for (int nIndex = 0; nIndex < nLen; ++nIndex)
      {
        // Save the current character
        char ch = buf.charAt(nIndex);
        
        // If we hit a space, set a flag so we make the next non-space
        // char uppercase
        if ((ch == ' ') || (ch == '(') || (ch == '-') || (ch == '/'))
        {
          blankFound = true;
          continue;
        }
        else
        {
          // Check if it's lowercase and the last character was a space
          if (blankFound)
          {
            // It is, so make it uppercase and replace in the buffer
            // ch = Character.toUpperCase(ch);
            buf.setCharAt(nIndex, Character.toUpperCase(ch));
          }
          
          // Clear the flag
          blankFound = false;
        }
      }
      
      // Make it a string
      String outStr = buf.toString();
      
      // Return it
      return outStr;
    }
  }
  
  
  /**
   * Returns whether the substring in line, starting at postTagIndex,
   * equals postTag.
   * 
   * @param line the complete line
   * @param postTagIndex the starting index
   * @param postTag the tag to match on
   * @return whether line contains postTag, starting at postTagIndex
   */
  public static boolean followedBy(final String line,
                                   final int postTagIndex,
                                   final String postTag)
  {
    // Save the end index
    final int postTagEnd = postTagIndex + postTag.length();
    
    // Check if post tag would end after the end of the line
    if (postTagEnd > line.length())
    {
      // The post tag would end after the end of the line
      return false;
    }
    
    // Return whether we have a match
    return (postTag.equals(line.substring(postTagIndex, postTagEnd)));
  }
}
