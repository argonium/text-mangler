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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class encapsulates the functionality of a popup
 * window that shows an HTML file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class HtmlPopup
{
  /**
   * The window frame.
   */
  private JFrame frame;
  
  /**
   * The directory of the input file.
   */
  private String directory;
  
  /**
   * The name of the file to show.
   */
  private String filename;
  
  /**
   * The URL prefix for a local file.
   */
  private static final String URL_HEADER = "file:///";
  
  
  /**
   * Default constructor.
   */
  @SuppressWarnings("unused")
  private HtmlPopup()
  {
    super();
  }
  
  
  /**
   * Constructor taking the name of the input file.
   * 
   * @param sFilename the name of the input file
   */
  public HtmlPopup(final String sFilename)
  {
    super();
    this.filename = sFilename;
    
    getCurrentDirectory();
    
    try
    {
      initUI();
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog(null, ioe.getMessage());
    }
  }
  
  
  /**
   * Gets the current directory.
   */
  private void getCurrentDirectory()
  {
    File dir = new File(".");
    
    try
    {
      directory = dir.getCanonicalPath();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  
  /**
   * Initialize the UI.
   * 
   * @throws IOException if the input file is not found
   */
  private void initUI() throws IOException
  {
    // Initialize the frame
    frame = new JFrame("Help");
    
    // Declare a File object pointing to the help file
    final File file = new File(directory, filename);
    final String helpUrl = URL_HEADER + file.getCanonicalPath();
    
    // Instantiate the JEditorPane, passing a URL to the help file
    JEditorPane ed = new JEditorPane(helpUrl);
    
    // Don't let the user edit the help
    ed.setEditable(false);
    
    // Add the JEditorPane to a scroll pane, and add that to the frame
    JScrollPane edScroll = new JScrollPane(ed);
    frame.getContentPane().add(edScroll, BorderLayout.CENTER);
    
    // Create the Close button
    JButton btnClose = new JButton("Close");
    
    // Add a handler for the button (close the frame)
    btnClose.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        frame.dispose();
      }
    });
    
    // Create the panel that will hold the button
    JPanel panelClose = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    
    // Add the button to the panel, and the panel to the frame
    panelClose.add(btnClose);
    frame.getContentPane().add(panelClose, BorderLayout.SOUTH);
    
    // Set the window size
    frame.setSize(600, 700);
    
    // Set the default close operation
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    // Center the window on the screen
    centerOnScreen();
    
    // Make the frame visible
    frame.setVisible(true);
    
    // Request focus for the scroll pane
    edScroll.requestFocus();
  }
  
  
  /**
   * Center the application on the screen.
   */
  private void centerOnScreen()
  {
    // Get the size of the screen
    final java.awt.Dimension screenDim = frame.getToolkit().getScreenSize();

    // Determine the new location of the window
    final int x = (screenDim.width - frame.getSize().width) / 2;
    final int y = (screenDim.height - frame.getSize().height) / 2;

    // Move the window
    frame.setLocation(x, y);
  }
}
