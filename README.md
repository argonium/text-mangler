# Text Mangler
The Text Mangler is a desktop application, written in Java, that allows a user to perform operations on rows of data. You specify the input data and a pattern, and the program applies the pattern to each row of input data in order to generate the output.

To run the program, Java 5 or later is required. Use this command to execute the application:

```
  java -jar textmangler.jar
```

There are no command-line parameters.

Each row of input data is composed of a set of fields, typically separated by a comma. For example, if the input data is:

```
  Bill,Cindy,David
  Sue,Cathy,Jim
```        
and the pattern is:

```
  $1 and $2 know $3
```

then the output is:

```
  Bill and Cindy know David
  Sue and Cathy know Jim
```

There is a special variable that refers to the current line number from the input data. The variable is $Row. By default, its initial value and increment value are both 1, although that can be modified (see below).

The application supports functions for field references (such as $1). These functions are $upper(), $lower() and $title(). These functions will change the case of their argument to, respectively, uppercase, lowercase and titlecase (only the first letter of each word is made uppercase). An example is $upper($1).

You can include comments in the pattern. Comments are not processed by the application, and not included in the output. To mark a line as a comment, start the line with $//.

Conditionals are also supported. The supported statements are $if, $elseif, $else and $endif. The conditional statement can either be of the format "$Row [op] [#]" or "[string] [op] [string]". Either string can be either a literal, such as "Sam", or a field reference, such as $1. The supported operators are listed below. The supported operators for string comparisons and numeric comparisons are identical. The $if and $elseif statements require a condition argument, such as "$if $Row = 5" or "$elseif $1="Bob"". An if-block can include as many $elseif statements as you like.

The supported conditional operators are:

Operator(s)	Function
=, ==	Equal
!=, <>	Not equal
<	Less than
<=	Less than or equal
>	Greater than
>=	Greater than or equal

To see a demonstration of all of the above, change the pattern in the drop-down listbox to "Demo".

The Options tab has several configuration options available to the user:

* Trim each field - Remove leading and trailing spaces and control charactes
* Ignore first row - Whether to ignore the first row
* Column delimiters - List of characters that can be a column delimiter in a row; any character in this field will be considered a column delimiter
* Row delimiters - List of characters that can be a row delimiter; any character in this field will be considered a row delimiter
* Code symbol prefix - The string that prefaces each field reference (e.g., $1, $Row), required
* Code symbol suffix - The string that follows each field reference (e.g., $1, $Row), not required
* $Row initial value - The value to initialize $Row to (for the first row)
* $Row increment value - The amount to increment $Row by for each row

The code symbol prefix and suffix only apply to field references (such as $1) and $Row, not to the functions (such as $upper()) or the conditionals (such as $if).

The drop-down listbox of pattern choices can be modified by editing the mangler.ini file. To add a pattern, add a section with the pattern name you want to appear in the drop-down listbox. In that section, add the line "Lines=[#]", with Lines set to the number of pattern lines in the section. Then, add the pattern lines to the section, with each line of the format "Line[#]=[string]". So, if the pattern is five lines, the first line would be "Lines=5", followed by five lines, starting with "Line1=[string]", "Line2=[string]", "Line3=[string]", "Line4=[string]" and "Line5=[string]", where [string] would be the pattern string for each line. For examples, see the mangler.ini file.

Part of the code is copyright JGoodies Karsten Lentzsch. This is limited to portions of the GUI.

The source code is released under the MIT license (other than the JGoodies code).
