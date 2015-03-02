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
 * Handle parsing the input text and applying a pattern to it.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Scripter
{
  /**
   * The string that starts a tag.
   */
  private String sPrefix = "${";
  
  /**
   * The string that ends a tag.
   */
  private String sSuffix = "}";
  
  /**
   * The line separator string.
   */
  private String lineSeparator;
  
  /**
   * The current row number.
   */
  private int nCurrentRow = 0;
  
  /**
   * The list of valid functions.
   */
  private static final String[] asFunctions = {"$lower(", "$upper(", "$title("};
  
  /**
   * The corresponding function values.
   */
  private static final int[] aiFunctionRefs = {0, 1, 2};
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private Scripter()
  {
    super();
  }
  
  
  /**
   * Constructor taking the code symbols as arguments.
   * 
   * @param startTag the code symbol start tag (prefix)
   * @param endTag the code symbol end tag (suffix)
   * @param lineSep the line separator string
   * @param currentRow the current row number
   */
  public Scripter(final String startTag, final String endTag,
                  final String lineSep, final int currentRow)
  {
    super();
    
    // Save the arguments
    sPrefix = startTag;
    sSuffix = endTag;
    lineSeparator = lineSep;
    nCurrentRow = currentRow;
    
    // Verify the two arrays are the same size
    if (asFunctions.length != aiFunctionRefs.length)
    {
      throw (new RuntimeException(
          "The number of functions does not match the number of function references"));
    }
  }
  
  
  /**
   * Process the string by replacing tags with whatever the main
   * application wants to replace them with.
   * 
   * @param strInput the input string
   * @param pattern the input pattern
   * @return the processed string
   */
  public String processCode(final List<String> strInput,
                            final List<String> pattern)
  {
    // Check the input
    if ((strInput == null) || (strInput.size() < 1) ||
        (pattern == null) || (pattern.size() < 1))
    {
      // Nothing to do, so return null
      return null;
    }
    
    // This will hold the processed contents of the input file
    StringBuilder strOut = new StringBuilder(500);
    
    // This boolean records whether to process the current line of input
    // (occasionally set to false when we're inside an if-block that
    // should not be executed)
    boolean bProcessCode = true;
    
    // Record whether an if-block has been executed
    boolean bIfBlockExecuted = false;
    
    // Iterate over the rows
    for (String patternRow : pattern)
    {
      // Check the current row for a match for an if-type statement
      String trimmedRow = patternRow.trim();
      
      // Check for an empty string or with only whitespace
      if ((patternRow.length() < 1))
      {
        // The line is empty, so add a separator, and continue
        strOut.append(lineSeparator);
        continue;
      }
      else if (trimmedRow.length() < 1)
      {
        // The row is only whitespace, so add the original row and a line
        // separator, and then continue
        strOut.append(patternRow).append(lineSeparator);
        continue;
      }
      
      // Check for lines starting with certain values
      if (trimmedRow.startsWith("$//"))
      {
        // The line starts with a comment, so skip it
        continue;
      }
      else if (trimmedRow.startsWith("$if "))
      {
        // We're entering an if block, so see if the line is true
        bProcessCode = handleIfStatement(trimmedRow.substring(4).trim(), strInput);
        
        // Record the result of the if statement
        bIfBlockExecuted = bProcessCode;
      }
      else if (trimmedRow.startsWith("$else if ") || (trimmedRow.startsWith("$elseif ")))
      {
        // We're entering an else-if block
        if (!bIfBlockExecuted)
        {
          // Calculate how many characters to skip to get to the condition to test
          final int nSkipValue = 8 + ((trimmedRow.charAt(5) == ' ') ? 1 : 0);
          
          // This if-block has not been executed, so check if this
          // if statement is true
          bProcessCode = handleIfStatement(trimmedRow.substring(nSkipValue).trim(),
                                           strInput);
          
          // Save the return value
          bIfBlockExecuted = bProcessCode;
        }
        else
        {
          // We don't want to process the code in this block
          bProcessCode = false;
        }
      }
      else if (trimmedRow.equals("$else"))
      {
        // We're entering an else-if block
        if (!bIfBlockExecuted)
        {
          // This if-block has not been executed, so execute it now
          bProcessCode = true;
          bIfBlockExecuted = bProcessCode;
        }
        else
        {
          // We don't want to process the code in this block
          bProcessCode = false;
        }
      }
      else if (trimmedRow.startsWith("$end"))
      {
        // We're at the end of an if-block, so process the rest of the code, and
        // jump to the next pattern line
        bProcessCode = true;
        continue;
      }
      else if (bProcessCode)
      {
        // We should execute this line, so parse it and save the output
        strOut.append(parseLine(patternRow, strInput));
        strOut.append(lineSeparator);
      }
    }
    
    // If the string ends with a line separator, remove it
    final int nOutLen = strOut.length();
    final int nSepLen = lineSeparator.length();
    if (nOutLen >= nSepLen)
    {
      // See if the string ends with a line separator
      String lastString = strOut.substring(nOutLen - nSepLen);
      if (lastString.equals(lineSeparator))
      {
        // It does, so remove the trailing line separator
        strOut.delete(nOutLen - nSepLen, nOutLen);
      }
    }
    
    // Return the generated string
    return strOut.toString();
  }
  
  
  /**
   * Parse the text of an if-control statement.
   * 
   * @param line the line after the if-statement
   * @param strInput the list of input arguments
   * @return whether this block evaluates to true
   */
  private boolean handleIfStatement(final String line,
                                    final List<String> strInput)
  {
    // This will hold our return value
    boolean result = false;
    
    // Save the length of the line
    final int nLineLen = line.length();
    if (nLineLen < 2)
    {
      return false;
    }
    
    // Need to evaluate line.  Check if it starts with $Row
    if (line.startsWith(sPrefix + "Row"))
    {
      // Jump past the Row variable
      int index = 3 + sPrefix.length() + sSuffix.length();
      
      // Find the first non-space character
      while ((index < nLineLen) && (line.charAt(index) == ' '))
      {
        // Advance to the next character
        ++index;
      }
      
      // See if we hit the end
      if (index >= nLineLen)
      {
        return false;
      }
      
      // Get the operation character(s) - everything before a number
      StringBuilder op = new StringBuilder(5);
      while (index < nLineLen)
      {
        // The character to look at
        char ch = line.charAt(index);
        
        // Abort if we hit a space or number
        if ((ch == ' ') || (Character.isDigit(ch)))
        {
          // We hit a space or a number, so abort
          break;
        }
        
        // Append the character
        op.append(ch);
        
        // Advance to the next character
        ++index;
      }
      
      // Find the next non-space character
      while ((index < nLineLen) && (line.charAt(index) == ' '))
      {
        // Advance to the next character
        ++index;
      }
      
      // Now get the number to compare the row to
      StringBuilder destDigit = new StringBuilder(5);
      while (index < nLineLen)
      {
        // The character to look at
        char ch = line.charAt(index);
        
        // Abort if we hit a space or non-number
        if ((ch == ' ') || (!Character.isDigit(ch)))
        {
          // We hit a space or a non-number, so abort
          break;
        }
        
        // Append the character
        destDigit.append(ch);
        
        // Advance to the next character
        ++index;
      }
      
      // We have everything we need, so get the comparison result
      result = compareRowInteger(op.toString(), destDigit.toString());
      
      // System.out.println("The result of " + line + " is " + Boolean.toString(result));
    }
    else if (line.startsWith(sPrefix))
    {
      // Handle the line starting with a prefix for a field reference
      
      // Get the index of the start of the field reference
      int index = sPrefix.length();
      
      // Find the index of the closing tag
      int nEndTagStartIndex = index;
      if (sSuffix.length() > 0)
      {
        // Find the start of the suffix tag
        nEndTagStartIndex = line.indexOf(sSuffix, index);
        if (nEndTagStartIndex < 0)
        {
          return false;
        }
      }
      else
      {
        // There is no closing tag, so grab everything that's an integer
        while ((nEndTagStartIndex < nLineLen) &&
               (Character.isDigit(line.charAt(nEndTagStartIndex))))
        {
          ++nEndTagStartIndex;
        }
      }
      
      // Grab the field number
      final String sField1Number = line.substring(index, nEndTagStartIndex);
      final int nField1Number = getFieldNumber(sField1Number);
      if ((nField1Number < 1) || (nField1Number > strInput.size()))
      {
        return false;
      }
      
      // Save the field referenced by the field number
      final String sField1 = strInput.get(nField1Number - 1);
      
      // Update index to point after the first field's suffix
      // (before the comparison operator)
      index = nEndTagStartIndex + sSuffix.length();
      
      // Find the index of the string after the operator
      final int nQuoteIndex = line.indexOf('"', index);
      final int nPrefixStartIndex = line.indexOf(sPrefix, index);
      
      // Find the lesser of the two values, whichever is positive
      int nField2StartIndex = 0;
      int nOperatorEndIndex = 0;
      String sField2 = null;
      if ((nQuoteIndex < 0) && (nPrefixStartIndex < 0))
      {
        // Neither was found, so return
        return false;
      }
      else if (nQuoteIndex < 0)
      {
        // We found the prefix only, so jump to the end
        nOperatorEndIndex = nPrefixStartIndex;
        nField2StartIndex = nPrefixStartIndex + sPrefix.length();
        
        // Extract the field
        int nLastSuffixIndex = nField2StartIndex;
        if (sSuffix.length() > 0)
        {
          // There's a suffix, so look for that
          nLastSuffixIndex = line.indexOf(sSuffix, nField2StartIndex);
          if (nLastSuffixIndex < 0)
          {
            // The suffix was not found
            return false;
          }
        }
        else
        {
          // No suffix, so continue until we hit the end of the line or a non-digit
          while ((nLastSuffixIndex < nLineLen) &&
                 (Character.isDigit(line.charAt(nLastSuffixIndex))))
          {
            ++nLastSuffixIndex;
          }
        }
        
        // Get the field number
        final String sField2Num = line.substring(nField2StartIndex, nLastSuffixIndex);
        final int nField2Num = getFieldNumber(sField2Num);
        if ((nField2Num < 1) || (nField2Num > strInput.size()))
        {
          return false;
        }
        
        // Save the field referenced by the field number
        sField2 = strInput.get(nField2Num - 1);
      }
      else if (nPrefixStartIndex < 0)
      {
        // Save the index of the quote character
        nOperatorEndIndex = nQuoteIndex;

        // We found the quote only, so jump past the opening quote
        nField2StartIndex = nQuoteIndex + 1;
        
        // Find the closing quote
        final int nCloseQuoteIndex = line.indexOf('"', nField2StartIndex);
        if (nCloseQuoteIndex < 0)
        {
          return false;
        }
        
        // Save the string between quotes
        sField2 = line.substring(nField2StartIndex, nCloseQuoteIndex);
      }
      else
      {
        // We found both, so save the one found first
        if (nPrefixStartIndex < nQuoteIndex)
        {
          // Save the index of the prefix start
          nOperatorEndIndex = nPrefixStartIndex;
          
          // We found the prefix first
          nField2StartIndex = nPrefixStartIndex + sPrefix.length();
          
          // Extract the field
          int nLastSuffixIndex = nField2StartIndex;
          if (sSuffix.length() > 0)
          {
            // There's a suffix, so look for that
            nLastSuffixIndex = line.indexOf(sSuffix, nField2StartIndex);
            if (nLastSuffixIndex < 0)
            {
              // The suffix was not found
              return false;
            }
          }
          else
          {
            // No suffix, so continue until we hit the end of the line or a non-digit
            while ((nLastSuffixIndex < nLineLen) &&
                   (Character.isDigit(line.charAt(nLastSuffixIndex))))
            {
              ++nLastSuffixIndex;
            }
          }
          
          // Get the field number
          final String sField2Num = line.substring(nField2StartIndex, nLastSuffixIndex);
          final int nField2Num = getFieldNumber(sField2Num);
          if ((nField2Num < 1) || (nField2Num > strInput.size()))
          {
            return false;
          }
          
          // Save the field referenced by the field number
          sField2 = strInput.get(nField2Num - 1);
        }
        else
        {
          // Save the index of the quote character
          nOperatorEndIndex = nQuoteIndex;

          // We found the quote first
          nField2StartIndex = nQuoteIndex + 1;
          
          // Find the closing quote
          final int nCloseQuoteIndex = line.indexOf('"', nField2StartIndex);
          if (nCloseQuoteIndex < 0)
          {
            return false;
          }
          
          // Save the string between quotes
          sField2 = line.substring(nField2StartIndex, nCloseQuoteIndex);
        }
      }
      
      // Save the operator
      final String sOperator = line.substring(index, nOperatorEndIndex).trim();
      
      // Get the comparison value
      result = compareStrings(sField1, sOperator, sField2);
    }
    else if (line.startsWith("\""))
    {
      // Handle the line starting with a string in quotes
      
      // Point the index after the quote symbol
      int index = 1;
      
      // Get the string inside the quotes
      final int nCloserIndex = line.indexOf('"', index);
      if (nCloserIndex < 0)
      {
        // Not found
        return false;
      }
      
      // Save the first field
      final String sField1 = line.substring(index, nCloserIndex);
      
      // Update index to point after the closing quote
      index = nCloserIndex + 1;
      
      // Now we want to get the 2nd field, after the comparison operator
      // Find the index of the string after the operator
      final int nQuoteIndex = line.indexOf('"', index);
      final int nPrefixStartIndex = line.indexOf(sPrefix, index);
      
      // Find the lesser of the two values, whichever is positive
      int nField2StartIndex = 0;
      int nOperatorEndIndex = 0;
      String sField2 = null;
      if ((nQuoteIndex < 0) && (nPrefixStartIndex < 0))
      {
        // Neither was found, so return
        return false;
      }
      else if (nQuoteIndex < 0)
      {
        // We found the prefix only, so jump to the end
        nOperatorEndIndex = nPrefixStartIndex;
        nField2StartIndex = nPrefixStartIndex + sPrefix.length();
        
        // Extract the field
        int nLastSuffixIndex = nField2StartIndex;
        if (sSuffix.length() > 0)
        {
          // There's a suffix, so look for that
          nLastSuffixIndex = line.indexOf(sSuffix, nField2StartIndex);
          if (nLastSuffixIndex < 0)
          {
            // The suffix was not found
            return false;
          }
        }
        else
        {
          // No suffix, so continue until we hit the end of the line or a non-digit
          while ((nLastSuffixIndex < nLineLen) &&
                 (Character.isDigit(line.charAt(nLastSuffixIndex))))
          {
            ++nLastSuffixIndex;
          }
        }
        
        // Get the field number
        final String sField2Num = line.substring(nField2StartIndex, nLastSuffixIndex);
        final int nField2Num = getFieldNumber(sField2Num);
        if ((nField2Num < 1) || (nField2Num > strInput.size()))
        {
          return false;
        }
        
        // Save the field referenced by the field number
        sField2 = strInput.get(nField2Num - 1);
      }
      else if (nPrefixStartIndex < 0)
      {
        // Save the index of the quote character
        nOperatorEndIndex = nQuoteIndex;

        // We found the quote only, so jump past the opening quote
        nField2StartIndex = nQuoteIndex + 1;
        
        // Find the closing quote
        final int nCloseQuoteIndex = line.indexOf('"', nField2StartIndex);
        if (nCloseQuoteIndex < 0)
        {
          return false;
        }
        
        // Save the string between quotes
        sField2 = line.substring(nField2StartIndex, nCloseQuoteIndex);
      }
      else
      {
        // We found both, so save the one found first
        if (nPrefixStartIndex < nQuoteIndex)
        {
          // Save the index of the prefix start
          nOperatorEndIndex = nPrefixStartIndex;
          
          // We found the prefix first
          nField2StartIndex = nPrefixStartIndex + sPrefix.length();
          
          // Extract the field
          int nLastSuffixIndex = nField2StartIndex;
          if (sSuffix.length() > 0)
          {
            // There's a suffix, so look for that
            nLastSuffixIndex = line.indexOf(sSuffix, nField2StartIndex);
            if (nLastSuffixIndex < 0)
            {
              // The suffix was not found
              return false;
            }
          }
          else
          {
            // No suffix, so continue until we hit the end of the line or a non-digit
            while ((nLastSuffixIndex < nLineLen) &&
                   (Character.isDigit(line.charAt(nLastSuffixIndex))))
            {
              ++nLastSuffixIndex;
            }
          }
          
          // Get the field number
          final String sField2Num = line.substring(nField2StartIndex, nLastSuffixIndex);
          final int nField2Num = getFieldNumber(sField2Num);
          if ((nField2Num < 1) || (nField2Num > strInput.size()))
          {
            return false;
          }
          
          // Save the field referenced by the field number
          sField2 = strInput.get(nField2Num - 1);
        }
        else
        {
          // Save the index of the quote character
          nOperatorEndIndex = nQuoteIndex;

          // We found the quote first
          nField2StartIndex = nQuoteIndex + 1;
          
          // Find the closing quote
          final int nCloseQuoteIndex = line.indexOf('"', nField2StartIndex);
          if (nCloseQuoteIndex < 0)
          {
            return false;
          }
          
          // Save the string between quotes
          sField2 = line.substring(nField2StartIndex, nCloseQuoteIndex);
        }
      }
      
      // Save the operator
      final String sOperator = line.substring(index, nOperatorEndIndex).trim();
      
      // Get the comparison value
      result = compareStrings(sField1, sOperator, sField2);
    }
    
    return result;
  }
  
  
  /**
   * Perform the specified operation, comparing the two strings.
   * 
   * @param field1 the first field to compare
   * @param op the comparison operator
   * @param field2 the second field to compare
   * @return the result of the comparison operation
   */
  private boolean compareStrings(final String field1,
                                 final String op,
                                 final String field2)
  {
    // Declare the variable that will hold the return value
    boolean result = false;
    
    // Check for different values for the operation
    if ((op.equals("=")) || (op.equals("==")))
    {
      result = (field1.equals(field2));
    }
    else if ((op.equals("!=")) || (op.equals("<>")))
    {
      result = (!field1.equals(field2));
    }
    else if (op.equals(">"))
    {
      result = (field1.compareTo(field2) > 0);
    }
    else if (op.equals("<"))
    {
      result = (field1.compareTo(field2) < 0);
    }
    else if (op.equals(">="))
    {
      result = (field1.compareTo(field2) >= 0);
    }
    else if (op.equals("<="))
    {
      result = (field1.compareTo(field2) <= 0);
    }
    
    // Return the result
    return result;
  }
  
  
  /**
   * Perform the specified operation, comparing the current row
   * number to the specified numeric value.
   * 
   * @param op the comparison to perform
   * @param numberString the number, as a string
   * @return the result of comparing Row to numberString
   */
  private boolean compareRowInteger(final String op,
                                    final String numberString)
  {
    // Convert the value into a number.  If the string is not
    // a valid number, this method returns zero.
    final int value = getFieldNumber(numberString);
    
    // This stores the result of the comparison
    boolean result = false;
    
    // Check for different values for the operation
    if ((op.equals("=")) || (op.equals("==")))
    {
      result = (nCurrentRow == value);
    }
    else if ((op.equals("!=")) || (op.equals("<>")))
    {
      result = (nCurrentRow != value);
    }
    else if (op.equals(">"))
    {
      result = (nCurrentRow > value);
    }
    else if (op.equals("<"))
    {
      result = (nCurrentRow < value);
    }
    else if (op.equals(">="))
    {
      result = (nCurrentRow >= value);
    }
    else if (op.equals("<="))
    {
      result = (nCurrentRow <= value);
    }
    
    // Return the result
    return result;
  }


  /**
   * Parse the input data using the current row from the pattern.
   * 
   * @param inputLine the current line of input (a pattern row)
   * @param listInput the array of input fields
   * @return the pattern applied to the fields
   */
  private String parseLine(final String inputLine,
                           final List<String> listInput)
  {
    // Save the length of the input line
    final int nSize = inputLine.length();
    
    // Check if the pattern string is empty
    if (nSize == 0)
    {
      // The string is empty, so just return it
      return inputLine;
    }
    
    // Check if the code symbol prefix is in the code
    if (inputLine.indexOf(sPrefix) < 0)
    {
      // There's nothing to do; return the whole input string
      return inputLine;
    }
    
    // If we reached this point, there is a code symbol prefix,
    // and possibly a function.
    
    // This is the variable that will hold the final output string
    String outputLine = inputLine;
    
    // Now iterate backwards over the list of strings in listInput;
    // for each (id+1), replace prefix + str + suffix with listInput[i].
    final int nNumFields = listInput.size();
    for (int index = nNumFields; index > 0; --index)
    {
      // The string builder object used to build the temp output string
      StringBuilder sb = new StringBuilder(outputLine.length());
      
      // The index of the start of the previous string added to
      // the string builder object
      int prevIndex = 0;
      
      // Generate the code tag (e.g., "$1")
      final String tag = sPrefix + Integer.toString(index) + sSuffix;
      
      // See if the tag exists in the string
      int tagIndex = outputLine.indexOf(tag);
      while (tagIndex >= 0)
      {
        // Initialize that we have not yet found a function surrounding
        // this occurrence of a tag
        boolean functionFound = false;
        
        // Iterate over the list of functions
        for (int i = aiFunctionRefs.length - 1; (!functionFound) && (i >= 0); --i)
        {
          // See if the tag is preceded by a function call and followed by
          // a closing parenthesis
          if ((Utility.precededBy(outputLine, tagIndex, asFunctions[i])) &&
              (Utility.followedBy(outputLine, tagIndex + tag.length(), ")")))
          {
            // We found a function, so stop looking for more
            functionFound = true;
            
            // Now we want to append everything before the function
            sb.append(outputLine.substring(prevIndex,
                      tagIndex - asFunctions[i].length()));
            
            // Append the modified string
            sb.append(handleFunction(aiFunctionRefs[i], listInput.get(index - 1)));
            
            // Advance the tag index to just past the tag and the closing parenthesis
            tagIndex += tag.length() + 1;
          }
        }
        
        // Check if a function was found
        if (!functionFound)
        {
          // Append everything before the tag
          sb.append(outputLine.substring(prevIndex, tagIndex));
          
          // Append the string
          sb.append(listInput.get(index - 1));
          
          // Advance the tag index to just past the tag end
          tagIndex += tag.length();
        }
        
        // Update the start of the previous substring to append
        prevIndex = tagIndex;
        
        // Check if we've gone past the end of the line
        if (prevIndex >= (nSize - 1))
        {
          break;
        }
        
        // Find the index of the next occurrence of the tag
        tagIndex = outputLine.indexOf(tag, prevIndex);
      }
      
      sb.append(outputLine.substring(prevIndex));
      outputLine = sb.toString();
    }
    
    // Replace occurrences of the Row variable with the correct value
    outputLine = outputLine.replace(sPrefix + "Row" + sSuffix,
                                    Integer.toString(nCurrentRow));
    
    // Return the output string 
    return outputLine;
  }
  
  
  /**
   * Perform the specified operation on the input string.
   * 
   * @param funcType the type of operation to perform
   * @param string the input string to operate on
   * @return the modified string
   */
  private static String handleFunction(final int funcType,
                                       final String string)
  {
    // Check the type of function we will perform on string
    switch (funcType)
    {
      case 0:
      {
        // Make the string lowercase
        return string.toLowerCase();
      }
      
      case 1:
      {
        // Make the string uppercase
        return string.toUpperCase();
      }
      
      case 2:
      {
        // Make the string titlecase
        return Utility.toTitleCase(string);
      }
      
      default:
      {
        // Unknown operation type
        throw (new RuntimeException("Function type not handled"));
      }
    }
  }
  
  
  /**
   * Converts the field reference into an integer.
   * 
   * @param tag the string containing a number
   * @return tag converted into an integer
   */
  private int getFieldNumber(final String tag)
  {
    // Declare the value we're going to return
    int value = 0;
    
    // Convert the string to an integer
    try
    {
      value = Integer.parseInt(tag);
    }
    catch (NumberFormatException nfe)
    {
      // An exception occurred, so set the value to an illegal field number
      value = 0;
    }
    
    return value;
  }
}
