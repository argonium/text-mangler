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

import io.miti.ui.component.Factory;
import io.miti.ui.panel.SimpleInternalFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * This class is the main class for the TextMangler application.
 * 
 * @author Mike Wallace, 03 August 2006
 */
public final class TextMangler implements ComponentListener, ItemListener
{
  /**
   * The name of the INI file for the application.  It contains pattern data.
   */
  private static final String INI_FILE_NAME = "./mangler.ini";
  
  /**
   * The name of the help file.
   */
  private static final String HELP_FILE_NAME = "mangler.html";
  
  /**
   * The pattern manager.
   */
  private PatternManager patternManager = null;
  
  /**
   * The application frame.
   */
  private JFrame m_appFrame;
  
  /**
   * The pattern to apply to the input data.
   */
  private JTextArea taPattern = null;
  
  /**
   * The input text area.
   */
  private JTextArea taInput = null;
  
  /**
   * The output text area.
   */
  private JTextArea taOutput = null;
  
  /**
   * The patterns combo box.
   */
  private JComboBox<String> cbPattern = null;
  
  /**
   * Text field for column delimiter.
   */
  private JTextField tfColDelim = null;
  
  /**
   * Text field for the row delimiter.
   */
  private JTextField tfRowDelim = null;
  
  /**
   * Text field for code symbol prefix.
   */
  private JTextField tfCodePrefix = null;
  
  /**
   * The Pattern panel.
   */
  private JPanel patternPanel = null;
  
  /**
   * Text field for the code symbol suffix.
   */
  private JTextField tfCodeSuffix = null;
  
  /**
   * Text field for the starting $Row value.
   */
  private JTextField tfRowStart = null;
  
  /**
   * Text field for the increment value for $Row.
   */
  private JTextField tfRowIncrement = null;
  
  /**
   * Checkbox to limit the number of matches.
   */
  private JCheckBox cbIgnoreFirstRow = null;
  
  /**
   * Checkbox to be case-sensitive.
   */
  private JCheckBox cbTrim = null;
  
  /**
   * The button used to parse the input and generate the output.
   */
  private JButton btnGo = null;
  
  /**
   * The default row delimiters.
   */
  private static final String DEFAULT_ROW_DELIMITERS = "\\r\\n";
  
  /**
   * The default column delimiters.
   */
  private static final String DEFAULT_COLUMN_DELIMITERS = ",";
  
  /**
   * The default code prefix symbol.
   */
  private static final String DEFAULT_CODE_PREFIX = "$";
  
  /**
   * The line separator string for this OS.
   */
  private static final String lineSeparator;
  
  /**
   * Load the line separator string.
   */
  static
  {
    lineSeparator = System.getProperty("line.separator");
  }
  
  
  /**
   * Default constructor.
   */
  private TextMangler()
  {
    // Call the parent constructor
    super();
  }
  
  
  /**
   * Create the application's GUI.
   */
  private void createApp()
  {
    // Create and set up the window
    m_appFrame = new JFrame("Text Mangler");
    m_appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    m_appFrame.addComponentListener(this);
    
    // Set the window size and center it
    m_appFrame.setMinimumSize(new Dimension(300, 300));
    m_appFrame.setPreferredSize(new Dimension(800, 600));
    m_appFrame.setSize(new Dimension(800, 600));
    centerOnScreen();
    
    // Get the pattern data
    patternManager = new PatternManager(INI_FILE_NAME, lineSeparator);
    
    // Generate the GUI and add it to the frame
    buildUI();
    
    // Display the window
    m_appFrame.pack();
    m_appFrame.setVisible(true);
    taPattern.requestFocusInWindow();
  }
  
  
  /**
   * Construct the user interface.
   */
  private void buildUI()
  {
    // Set up the right-side split pane (input, output)
    JSplitPane spRight = Factory.createStrippedSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        initInputPanel(),
        initOutputPanel(),
        0.5f);
    spRight.setDividerSize(3);
    spRight.setDividerLocation(300);
    spRight.setContinuousLayout(true);
    
    // Set up the split pane (Pattern on the left, other split
    // pane on the right)
    JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            initPatternPanel(),
            spRight);
    sp.setDividerSize(3);
    sp.setDividerLocation(300);
    sp.setResizeWeight(0.0);
    sp.setContinuousLayout(true);
    
    // Set operation to do when user presses enter.
    // Default is GO.
    m_appFrame.getRootPane().setDefaultButton(btnGo);
    
    // Add the main panel to the content pane
    m_appFrame.getContentPane().add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the Pattern panel.
   * 
   * @return the tabbed pane
   */
  private JComponent initPatternPanel()
  {
    JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
    
    buildPatternPanel();
    
    tabbedPane.addTab("Pattern", Factory.createStrippedScrollPane(patternPanel));
    tabbedPane.addTab("Options", Factory.createStrippedScrollPane(buildOptions()));
    
    // Set mnemonics for the tabs
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_P);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_O);
    
    // Create the Pattern frame and add the tabbed pages
    SimpleInternalFrame sif = new SimpleInternalFrame("Pattern");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(tabbedPane);
    
    return tabbedPane;
  }
  
  
  /**
   * Build the Pattern tabbed pane.
   */
  private void buildPatternPanel()
  {
    // Build the panel for the pane
    patternPanel = new JPanel(new GridBagLayout());
    
    // Add the label (top row)
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(16, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    
    JLabel label1 = new JLabel("Pattern:");
    patternPanel.add(label1, c);
    
    // Add the combobox (2nd row) and populate with the pattern names
    cbPattern = new JComboBox(patternManager.getPatternNames().toArray());
    
    // Create and register listener
    cbPattern.addItemListener(this);
    
    c.insets = new Insets(4, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    
    patternPanel.add(cbPattern, c);
    
    // Add the text area (3rd row)
    c.insets = new Insets(4, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    
    // Add the pattern text area to the panel
    taPattern = new JTextArea(8, 33);
    JScrollPane patternScrollPane = new JScrollPane(taPattern);
    patternPanel.add(patternScrollPane, c);
    
    // Add the Go button (4th row)
    c.insets = new Insets(2, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 3;
    
    btnGo = new JButton("Go");
    btnGo.setMnemonic(KeyEvent.VK_G);
    btnGo.setToolTipText("Apply the pattern to the input");
    btnGo.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        parseInput();
      }
    });
    patternPanel.add(btnGo, c);
    
    // Add the About button (5th row)
    c.insets = new Insets(29, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 2;
    
    JButton btnAbout = new JButton("About");
    btnAbout.setMnemonic(KeyEvent.VK_A);
    btnAbout.setToolTipText("Show information about the application");
    btnAbout.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        showAboutDialog(evt);
      }
    });
    patternPanel.add(btnAbout, c);
    
    // Add the Help button (6th row)
    c.insets = new Insets(12, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 2;
    
    JButton btnHelp = new JButton(" Help ");
    btnHelp.setMnemonic(KeyEvent.VK_H);
    btnHelp.setToolTipText("Show the help file");
    btnHelp.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        showHelpDialog(evt);
      }
    });
    patternPanel.add(btnHelp, c);
    
    // Add the Quit button (7th row)
    c.insets = new Insets(12, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.NORTH;
    c.weighty = 1.0;
    
    JButton btnQuit = new JButton(" Quit ");
    btnQuit.setMnemonic(KeyEvent.VK_Q);
    btnQuit.setToolTipText("Quit the application");
    btnQuit.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        exitApplication(evt);
      }
    });
    patternPanel.add(btnQuit, c);
    
    // Add the patterns
    updatePatternInput(0);
  }
  
  
  /**
   * Get the notification that the selected item in the combox changed.
   * 
   * @param evt the item event
   */
  public void itemStateChanged(final ItemEvent evt)
  {
    // Get the combo box
    @SuppressWarnings("unchecked")
	JComboBox<String> cb = (JComboBox<String>) evt.getSource();
    
    // Only handle selection events
    if (evt.getStateChange() == ItemEvent.SELECTED)
    {
      // Item was just selected
      updatePatternInput(cb.getSelectedIndex());
    }
  }
  
  
  /**
   * The selected pattern changed, so update the pattern input box.
   * 
   * @param nPatternIndex the index of the new pattern
   */
  private void updatePatternInput(final int nPatternIndex)
  {
    // Get the pattern for the selected index
    taPattern.setText(patternManager.getPatternDataByIndex(nPatternIndex));
    
    // Set taPattern to show the first line
    if (taPattern.getText().length() > 0)
    {
      taPattern.setCaretPosition(0);
    }
    
    // Force a redraw of the text area
    patternPanel.revalidate();
    patternPanel.repaint();
  }
  
  
  /**
   * Exit the application.
   * 
   * @param evt the event
   */
  private void exitApplication(final ActionEvent evt)
  {
    // The window closed, so exit the application
    Runtime.getRuntime().exit(0);
  }
  
  
  /**
   * Show the Help file.
   * 
   * @param evt the event
   */
  private void showHelpDialog(final ActionEvent evt)
  {
    // Show the Help file
    new HtmlPopup(HELP_FILE_NAME);
  }
  
  
  /**
   * Show the About dialog.
   * 
   * @param evt the event
   */
  private void showAboutDialog(final ActionEvent evt)
  {
    // Show the About dialog
    JOptionPane.showMessageDialog(m_appFrame, getAboutDialogText(),
        "About Text Mangler", JOptionPane.INFORMATION_MESSAGE);
  }
  
  
  /**
   * Returns the text for the About box.
   * 
   * @return the text for the About box
   */
  private String getAboutDialogText()
  {
    // Build the text
    StringBuffer buf = new StringBuffer(200);
    
    buf.append("Text Mangler: A simple code generator. ")
       .append("Written by Mike Wallace, 2006.\n")
       .append("Released under the MIT license. Free for any use.\n")
       .append("Portions of the source code copyright JGoodies Karsten Lentzsch.\n")
       .append("Based on the World's Simplest Code Generator: ")
       .append("http://secretgeek.net/wscg.asp\n");
    
    return buf.toString();
  }
  
  
  /**
   * Parse the input data and perform the conversion.
   */
  private void parseInput()
  {
    // Clear the output text
    taOutput.setText("");
    
    // Get the input text
    final String inputData = taInput.getText();
    
    // Get the pattern text
    final String sPatternText = taPattern.getText();
    
    // Get the row delimiter
    final String rowDelim = Utility.fixSpecialCharacters(getRowDelimiters());
    
    // Get the column delimiter
    final String colDelim = getColumnDelimiters();
    
    // Save the values for the row
    int nCurrentRow = getFirstRowValue();
    final int nRowIncrement = getRowIncrement();
    boolean readFirstRow = false;
    
    // Special booleans
    final boolean skipFirstRow = getIgnoreFirstRow();
    final boolean trimFields = getFieldTrimming();
    
    // Declare our CSV parser
    CSVReader rowParser = new CSVReader(trimFields);
    
    // Set the column delimiters; any character in the string is
    // considered a delimiter
    rowParser.setSeparators(colDelim);
    
    // This will hold the output data
    StringBuilder sb = new StringBuilder(200);
    
    // Build the list of strings from the pattern
    List<String> patternList = new ArrayList<String>(20);
    StringSetTokenizer sst = new StringSetTokenizer(sPatternText, "\r\n");
    while (sst.hasMoreTokens())
    {
      // Get the current row from the pattern and save it
      patternList.add(sst.nextToken());
    }
    
    // Iterate over the input rows
    StringTokenizer rowTokenizer = new StringTokenizer(inputData, rowDelim);
    while (rowTokenizer.hasMoreTokens())
    {
      // Get the next row
      String row = rowTokenizer.nextToken();
      
      // Check if we're on the first row and want to skip it
      if ((!readFirstRow) && (skipFirstRow))
      {
        // Mark that we read the first row
        readFirstRow = true;
        
        // Skip to the next row
        continue;
      }
      
      // Parse the current row
      List<String> rowData = rowParser.parseLine(row);
      
      // Apply the pattern to the input row
      final String out = generateOutput(rowData, patternList, nCurrentRow);
      
      // If there was text to add, add it and a line separator
      if ((out != null) && (out.length() > 0))
      {
        sb.append(out);
        sb.append(lineSeparator);
      }
      
      // Increment the row counter
      nCurrentRow += nRowIncrement;
    }
    
    // Set the output text
    taOutput.setText(sb.toString());
    
    // Set taOutput to show the first line
    if (sb.length() > 0)
    {
      taOutput.setCaretPosition(0);
    }
  }
  
  
  /**
   * Convert the row of input into a string, based on the pattern.
   * 
   * @param rowData the array of field elements
   * @param sPattern the input pattern
   * @param nCurrentRow the current row number
   * @return the string to add to the output panel
   */
  private String generateOutput(final List<String> rowData,
                                final List<String> sPattern,
                                final int nCurrentRow)
  {
    // Get the code symbol prefix and suffix
    final String symbolPrefix = getCodePrefix();
    final String symbolSuffix = getCodeSuffix();
    
    // Instantiate a Scripter object to handle parsing
    Scripter scripter = new Scripter(symbolPrefix, symbolSuffix,
                                     lineSeparator, nCurrentRow);
    
    // Apply the pattern to the row of fields
    String lines = scripter.processCode(rowData, sPattern);
    
    // Return the string
    return lines;
  }
  
  
  /**
   * Return the code prefix, and check for a non-empty value for it.
   * 
   * @return the code symbol prefix
   */
  private String getCodePrefix()
  {
    // Check the code prefix
    String prefix = tfCodePrefix.getText();
    if (prefix.length() < 1)
    {
      tfCodePrefix.setText(DEFAULT_CODE_PREFIX);
      prefix = DEFAULT_CODE_PREFIX;
    }
    
    return prefix;
  }
  
  
  /**
   * Return the code suffix.
   * 
   * @return the code symbol suffix
   */
  private String getCodeSuffix()
  {
    // Get the code suffix.  We currently don't enforce any value
    // for the code suffix.
    String suffix = tfCodeSuffix.getText();
    
    // Return the suffix
    return suffix;
  }
  
  
  /**
   * Return the row delimiter string.
   * 
   * @return the row delimiter string
   */
  private String getRowDelimiters()
  {
    // Get the delimiter string
    String delim = tfRowDelim.getText();
    
    // Check the value
    if (delim.length() < 1)
    {
      // The delimiter string is empty, so set it to the default
      tfRowDelim.setText(DEFAULT_ROW_DELIMITERS);
      delim = DEFAULT_ROW_DELIMITERS;
    }
    
    // Return the delimiter string
    return delim;
  }
  
  
  /**
   * Return the column delimiter string.
   * 
   * @return the column delimiter string
   */
  private String getColumnDelimiters()
  {
    // Get the delimiter string
    String delim = tfColDelim.getText();
    
    // Check the value
    if (delim.length() < 1)
    {
      // The delimiter string is empty, so set it to the default
      tfColDelim.setText(DEFAULT_COLUMN_DELIMITERS);
      delim = DEFAULT_COLUMN_DELIMITERS;
    }
    
    // Return the delimiter string
    return delim;
  }
  
  
  /**
   * Build the Options tabbed pane of the Pattern page.
   * 
   * @return the contents of the Options tab
   */
  private JComponent buildOptions()
  {
    // Build the panel for the pane
    JPanel panel = new JPanel(new GridBagLayout());
    
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(12, 20, 4, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.NORTH;
    c.weightx = 1.0;
    
    /*
     * Options:
     *   Trim each field?
     *   Ignore first row?
     *   Column Delimiter
     *   Row Delimiter
     *   Code Symbol Prefix
     *   Code Symbol Suffix
     *   $Row Initial Value
     *   $Row Increment
     */
    cbTrim = new JCheckBox("Trim each field?");
    cbTrim.setMnemonic(KeyEvent.VK_T);
    cbTrim.setToolTipText(
        "Whether to remove leading and trailing spaces from each field");
    cbTrim.setSelected(false);
    panel.add(cbTrim, c);
    
    c.insets = new Insets(3, 20, 0, 3);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    
    // Add a checkbox to let a user ignore the first row
    cbIgnoreFirstRow = new JCheckBox("Ignore first row?");
    cbIgnoreFirstRow.setMnemonic(KeyEvent.VK_I);
    cbIgnoreFirstRow.setToolTipText(
        "Whether to skip processing of the first row of input");
    cbIgnoreFirstRow.setSelected(false);
    panel.add(cbIgnoreFirstRow, c);
    
    // Add the Delimiters group box
    {
      JPanel delimPanel = new JPanel(new GridLayout(0, 2, 5, 5));
      TitledBorder titledBorder =
        BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(java.awt.Color.black, 1),
             "Delimiters");
      delimPanel.setBorder(titledBorder);
      
      // Initialize the row and column delimiter text fields
      tfColDelim = new JTextField(DEFAULT_COLUMN_DELIMITERS, 4);
      tfRowDelim = new JTextField(DEFAULT_ROW_DELIMITERS, 4);
      
      tfColDelim.setToolTipText("List of column delimiter characters");
      tfRowDelim.setToolTipText("List of row delimiter characters");
      
      delimPanel.add(new JLabel("  Column: "));
      delimPanel.add(tfColDelim);
      delimPanel.add(new JLabel("  Row: "));
      delimPanel.add(tfRowDelim);
      
      c.insets = new Insets(11, 25, 11, 3);
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 1;
      
      panel.add(delimPanel, c);
    }
    
    // Add the Code Symbol group box
    {
      JPanel codePanel = new JPanel(new GridLayout(0, 2));
      TitledBorder titledCodeBorder =
        BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(java.awt.Color.black, 1),
             "Code Symbol");
      codePanel.setBorder(titledCodeBorder);
      
      tfCodePrefix = new JTextField(DEFAULT_CODE_PREFIX, 4);
      tfCodeSuffix = new JTextField("", 4);
      
      tfCodePrefix.setToolTipText("Prefix character(s) for special codes");
      tfCodeSuffix.setToolTipText("Suffix character(s) for special codes");
      
      codePanel.add(new JLabel("  Prefix: "));
      codePanel.add(tfCodePrefix);
      codePanel.add(new JLabel("  Suffix: "));
      codePanel.add(tfCodeSuffix);
      
      c.insets = new Insets(11, 25, 11, 3);
      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 1;
      
      panel.add(codePanel, c);
    }
    
    // Add the group box for $Row
    {
      JPanel rowPanel = new JPanel(new GridLayout(0, 2));
      TitledBorder titledRowBorder =
        BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(java.awt.Color.black, 1),
             "$Row Variable");
      rowPanel.setBorder(titledRowBorder);
      
      tfRowStart = new JTextField("1", 3);
      tfRowIncrement = new JTextField("1", 3);
      
      tfRowStart.setToolTipText("The number of the first row");
      tfRowIncrement.setToolTipText("The increment value for $Row");
      
      rowPanel.add(new JLabel("  Initial: "));
      rowPanel.add(tfRowStart);
      rowPanel.add(new JLabel("  Increment: "));
      rowPanel.add(tfRowIncrement);
      
      c.insets = new Insets(11, 25, 11, 3);
      c.gridx = 0;
      c.gridy = 4;
      c.gridwidth = 1;
      c.anchor = GridBagConstraints.NORTH;
      c.weighty = 1.0;
      c.weightx = 0.0;
      
      panel.add(rowPanel, c);
    }
    
    // Return the panel
    return panel;
  }
  
  
  /**
   * Initialize the Input panel.
   * 
   * @return the Input panel
   */
  private JComponent initInputPanel()
  {
    JPanel results = new JPanel(new BorderLayout());
    results.setMinimumSize(new Dimension(200, 200));
    results.setPreferredSize(new Dimension(500, 300));
    
    taInput = new JTextArea(50, 10);
    initInputArea();
    
    JScrollPane scrollPane = new JScrollPane(taInput);
    results.add(scrollPane);
    
    SimpleInternalFrame sif = new SimpleInternalFrame("Input");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(results);
    
    return sif;
  }
  
  
  /**
   * Initialize the text input area with some text.
   */
  private void initInputArea()
  {
    // Declare our string builder
    StringBuilder sb = new StringBuilder(300);
    
    // Build the string
    sb.append("Bill,Ted,Sam").append(lineSeparator)
      .append("Jen,Val,Kris").append(lineSeparator)
      .append("Dave,Lou,Doug").append(lineSeparator)
      .append("Sid,Jack,Chloe");
    
    // Put the string in the text area
    taInput.setText(sb.toString());
  }
  
  
  /**
   * Initialize the Output panel.
   * 
   * @return the output panel
   */
  private JComponent initOutputPanel()
  {
    JPanel results = new JPanel(new BorderLayout());
    results.setMinimumSize(new Dimension(200, 200));
    results.setPreferredSize(new Dimension(500, 300));
    
    taOutput = new JTextArea(50, 10);
    
    JScrollPane scrollPane = new JScrollPane(taOutput);
    results.add(scrollPane);
    
    SimpleInternalFrame sif = new SimpleInternalFrame("Output");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(results);
    
    return sif;
  }
  
  
  /**
   * Returns whether the user wants to trim each field.
   * 
   * @return whether the user wants to trim each field
   */
  private boolean getFieldTrimming()
  {
    return cbTrim.isSelected();
  }
  
  
  /**
   * Returns whether the user wants to ignore the first row.
   * 
   * @return whether the user wants to ignore the first row
   */
  private boolean getIgnoreFirstRow()
  {
    return cbIgnoreFirstRow.isSelected();
  }
  
  
  /**
   * Returns the initial value for $Row.
   * 
   * @return the initial value for $Row
   */
  private int getFirstRowValue()
  {
    // The value we return
    int nValue = 0;
    
    try
    {
      // Convert into an integer
      nValue = Integer.parseInt(tfRowStart.getText());
    }
    catch (NumberFormatException nfe)
    {
      // An exception occurred, so set the text field
      tfRowStart.setText("1");
      nValue = 1;
    }
    
    // Return the converted string's value
    return nValue;
  }
  
  
  /**
   * Returns the increment value for $Row.
   * 
   * @return the increment value for $Row
   */
  private int getRowIncrement()
  {
    // The value we return
    int nValue = 0;
    
    try
    {
      // Convert into an integer
      nValue = Integer.parseInt(tfRowIncrement.getText());
    }
    catch (NumberFormatException nfe)
    {
      // An exception occurred, so set the text field
      tfRowIncrement.setText("1");
      nValue = 1;
    }
    
    // Return the converted string's value
    return nValue;
  }
  
  
  /**
   * Center the application on the screen.
   */
  private void centerOnScreen()
  {
    // Get the size of the screen
    Dimension screenDim = java.awt.Toolkit.getDefaultToolkit()
            .getScreenSize();

    // Determine the new location of the window
    int x = (screenDim.width - m_appFrame.getSize().width) / 2;
    int y = (screenDim.height - m_appFrame.getSize().height) / 2;

    // Move the window
    m_appFrame.setLocation(x, y);
  }
  
  
  /**
   * Handle the component getting resized.
   * 
   * @param e the component event
   */
  public void componentResized(final ComponentEvent e)
  {
    // Get the current window size
    Dimension d = m_appFrame.getSize();
    int nWidth = (int) d.getWidth();
    int nHeight = (int) d.getHeight();
    
    // Default to not resizing
    boolean bResize = false;
    
    // Check the width to see if it's below the minimum
    if (nWidth < 500)
    {
      // It is, so modify the value and record that we want to resize
      nWidth = 500;
      bResize = true;
    }
    
    // Check the height to see if it's below the minimum
    if (nHeight < 450)
    {
      // It is, so modify the value and record that we want to resize
      nHeight = 450;
      bResize = true;
    }
    
    // Check if we need to resize the window
    if (bResize)
    {
      // We do, so set the new size
      m_appFrame.setSize(nWidth, nHeight);
    }
  }
  
  
  /**
   * Handle the component getting moved.
   * 
   * @param e the component event
   */
  public void componentMoved(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Handle the component getting shown.
   * 
   * @param e the component event
   */
  public void componentShown(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Handle the component getting hidden.
   * 
   * @param e the component event
   */
  public void componentHidden(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Use the default look and feel.
   */
  private static void initLookAndFeel()
  {
    // Use this system's look and feel
    try
    {
      javax.swing.UIManager.setLookAndFeel(
        javax.swing.UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      System.out.println("Exception: " + e.getMessage());
    }
  }
  
  
  /**
   * Initialize the look and feel, instantiate the app, and run it.
   */
  private static void createAndRun()
  {
    initLookAndFeel();

    TextMangler app = new TextMangler();
    app.createApp();
  }
  
  
  /**
   * Make the application compatible with Apple Macs.
   * 
   * @param appName the name of the application
   */
  public static void makeMacCompatible(final String appName)
  {
    // Set the system properties that a Mac uses
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
  }
  
  
  /**
   * Main method for the application.
   * 
   * @param args command-line arguments
   */
  public static void main(final String[] args)
  {
    // Set up the Mac-related properties
    makeMacCompatible("TextMangler");
    
    // Schedule a job for the event-dispatching thread
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        createAndRun();
      }
    });
  }
}
