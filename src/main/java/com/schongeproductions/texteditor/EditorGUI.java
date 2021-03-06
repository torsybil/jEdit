/*
 * The MIT License
 *
 * Copyright 2016 Joseph Godwin Kimani.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.schongeproductions.texteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.html.ImageView;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import Licenses.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;


@SuppressWarnings("serial")
public class EditorGUI extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new EditorGUI();

    }

    //============================================
    // FIELDS
    //============================================

    // Menus
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu aboutMenu;
    private JMenu helpMenu;
    private JMenuItem newFile, openFile, saveFile, saveAsFile, pageSetup, printFile, exit;
    private JMenuItem undoEdit, redoEdit, selectAll, copy, paste, cut, wordwrap, timestamp;
    private JMenuItem aboutMe, license;
    private JMenuItem helpTopics;
    
            
    //==================================
    // Where to edit text..
    private Border textBorder;
    private JScrollPane scroll;
    private JTextArea textArea;
    private Font textFont;

    // Window
    private JFrame window;

    // Printing
    private PrinterJob job;
    public PageFormat format;

    // Is File Saved/Opened
    private boolean opened = false;
    private boolean saved = false;

    // Record Open File for quick saving
    private File openedFile;

    // Undo manager for managing the storage of the undos
    // so that the can be redone if requested
    private UndoManager undo;
    
    int i,len1,len,pos1;  
    String str="";  
    String months[]={"January","February","March","April","May","June","July","August","September","October","November","December"};  
     


    //============================================
    // CONSTRUCTOR
    //============================================

    public EditorGUI() {
        super("jEdit");


        fileMenu();
        editMenu();
        aboutMenu();
        helpMenu();

        createTextArea();


        undoMan();


        createThis();
    }

    private void createThis() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setJMenuBar(createMenuBar());
        this.add(scroll, BorderLayout.CENTER);
        this.pack();
        this.setLocationRelativeTo(null);

        ImageIcon img = new ImageIcon("res/jedit.png");
        this.setIconImage(img.getImage());

        this.setVisible(true);
    }

    private JTextArea createTextArea() {
        textArea = new JTextArea(50, 70);
        textArea.setEditable(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(textBorder, BorderFactory.createEmptyBorder(2, 5, 0, 0)));

        textFont = new Font("Courier", 0, 14);
        textArea.setFont(textFont);

        scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        return textArea;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(aboutMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private UndoManager undoMan() {
        // Listener for undo and redo functions to document
        undo = new UndoManager();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        return undo;
    }

    private void fileMenu() {
        // Create File Menu
        fileMenu = new JMenu("File");
        fileMenu.setPreferredSize(new Dimension(40, 20));

        // Add file menu items
        newFile = new JMenuItem("New");
        newFile.addActionListener(this);
        newFile.setPreferredSize(new Dimension(100, 20));
        newFile.setEnabled(true);

        openFile = new JMenuItem("Open...");
        openFile.addActionListener(this);
        openFile.setPreferredSize(new Dimension(100, 20));
        openFile.setEnabled(true);

        saveFile = new JMenuItem("Save");
        saveFile.addActionListener(this);
        saveFile.setPreferredSize(new Dimension(100, 20));
        saveFile.setEnabled(true);

        saveAsFile = new JMenuItem("Save As...");
        saveAsFile.addActionListener(this);
        saveAsFile.setPreferredSize(new Dimension(100, 20));
        saveAsFile.setEnabled(true);

        pageSetup = new JMenuItem("Page Setup...");
        pageSetup.addActionListener(this);
        pageSetup.setPreferredSize(new Dimension(100, 20));
        pageSetup.setEnabled(true);

        printFile = new JMenuItem("Print...");
        printFile.addActionListener(this);
        printFile.setPreferredSize(new Dimension(100, 20));
        printFile.setEnabled(true);

        exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        exit.setPreferredSize(new Dimension(100, 20));
        exit.setEnabled(true);

        // Add items to menu
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsFile);
        fileMenu.add(pageSetup);
        fileMenu.add(printFile);
        fileMenu.add(exit);
    }

    private void editMenu() {
        editMenu = new JMenu("Edit");
        editMenu.setPreferredSize(new Dimension(40, 20));

        // Add file menu items
        undoEdit = new JMenuItem("Undo");
        undoEdit.addActionListener(this);
        undoEdit.setPreferredSize(new Dimension(100, 20));
        undoEdit.setEnabled(true);

        redoEdit = new JMenuItem("Redo");
        redoEdit.addActionListener(this);
        redoEdit.setPreferredSize(new Dimension(100, 20));
        redoEdit.setEnabled(true);

        selectAll = new JMenuItem("Select All");
        selectAll.addActionListener(this);
        selectAll.setPreferredSize(new Dimension(100, 20));
        selectAll.setEnabled(true);

        copy = new JMenuItem("Copy");
        copy.addActionListener(this);
        copy.setPreferredSize(new Dimension(100, 20));
        copy.setEnabled(true);

        paste = new JMenuItem("Paste");
        paste.addActionListener(this);
        paste.setPreferredSize(new Dimension(100, 20));
        paste.setEnabled(true);

        cut = new JMenuItem("Cut");
        cut.addActionListener(this);
        cut.setPreferredSize(new Dimension(100, 20));
        cut.setEnabled(true);
        
        wordwrap = new JMenuItem("Word wrap");
        wordwrap.addActionListener(this);
        wordwrap.setPreferredSize(new Dimension(100, 20));
        wordwrap.setEnabled(true);
        
        
        timestamp = new JMenuItem("Timestamp");
        timestamp.addActionListener(this);
        timestamp.setPreferredSize(new Dimension(100, 20));
        timestamp.setEnabled(true);

        // Add items to menu
        editMenu.add(undoEdit);
        editMenu.add(redoEdit);
        editMenu.add(selectAll);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.add(cut);
        editMenu.add(wordwrap);
        editMenu.add(timestamp);
    }

    private void aboutMenu() {

        aboutMenu = new JMenu("About");
        aboutMenu.setPreferredSize(new Dimension(50, 20));

        aboutMe = new JMenuItem("Author");
        aboutMe.addActionListener(this);
        aboutMe.setPreferredSize(new Dimension(100, 20));
        aboutMe.setEnabled(true);

        license = new JMenuItem("License");
        license.addActionListener(this);
        license.setPreferredSize(new Dimension(100, 20));
        license.setEnabled(true);

        aboutMenu.add(aboutMe);
        aboutMenu.add(license);

    }
    
    private void helpMenu() {
          
        helpMenu = new JMenu("Help");
        helpMenu.setPreferredSize(new Dimension(40, 20));
        
        helpTopics = new JMenuItem("Help Topics");
        helpTopics.addActionListener(this);
        helpTopics.setPreferredSize(new Dimension(100, 20));
        helpTopics.setEnabled(true);
        
        helpMenu.add(helpTopics);
        
         }
    // Method for saving files - Removes duplication of code
    private void saveFile(File filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(textArea.getText());
            writer.close();
            saved = true;
            window.setTitle("JavaText - " + filename.getName());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // Method for quick saving files
    private void quickSave(File filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(textArea.getText());
            writer.close();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // Method for opening files
    private void openingFiles(File filename) {
        try {
            openedFile = filename;
            FileReader reader = new FileReader(filename);
            textArea.read(reader, null);
            opened = true;
            window.setTitle("jEdit - " + filename.getName());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == newFile) {
            new EditorGUI();
        } else if (event.getSource() == openFile) {
            JFileChooser open = new JFileChooser();
            open.showOpenDialog(null);
            File file = open.getSelectedFile();
            openingFiles(file);
        } else if (event.getSource() == saveFile) {
            JFileChooser save = new JFileChooser();
            File filename = save.getSelectedFile();
            if (opened == false && saved == false) {
                save.showSaveDialog(null);
                int confirmationResult;
                if (filename.exists()) {
                    confirmationResult = JOptionPane.showConfirmDialog(saveFile, "Replace existing file?");
                    if (confirmationResult == JOptionPane.YES_OPTION) {
                        saveFile(filename);
                    }
                } else {
                    saveFile(filename);
                }
            } else {
                quickSave(openedFile);
            }
        } else if (event.getSource() == saveAsFile) {
            JFileChooser saveAs = new JFileChooser();
            saveAs.showSaveDialog(null);
            File filename = saveAs.getSelectedFile();
            int confirmationResult;
            if (filename.exists()) {
                confirmationResult = JOptionPane.showConfirmDialog(saveAsFile, "Replace existing file?");
                if (confirmationResult == JOptionPane.YES_OPTION) {
                    saveFile(filename);
                }
            } else {
                saveFile(filename);
            }
        } else if (event.getSource() == pageSetup) {
            job = PrinterJob.getPrinterJob();
            format = job.pageDialog(job.defaultPage());
        } else if (event.getSource() == printFile) {
            job = PrinterJob.getPrinterJob();
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException err) {
                    err.printStackTrace();
                }
            }
        } else if (event.getSource() == exit) {
            System.exit(0);
        } else if (event.getSource() == undoEdit) {
            try {
                undo.undo();
            } catch (CannotUndoException cu) {
                cu.printStackTrace();
            }
        } else if (event.getSource() == redoEdit) {
            try {
                undo.redo();
            } catch (CannotUndoException cur) {
                cur.printStackTrace();
            }
        } else if (event.getSource() == selectAll) {
            textArea.selectAll();
        } else if (event.getSource() == copy) {
            textArea.copy();
        } else if (event.getSource() == paste) {
            textArea.paste();
            pos1=textArea.getCaretPosition();
            textArea.insert(str, pos1);
        } else if (event.getSource() == cut) {
            textArea.cut();
        } else if (event.getSource() == wordwrap) {
            textArea.setLineWrap(true);
        }else if(event.getSource() == timestamp) {  
            GregorianCalendar gcalendar=new GregorianCalendar();  
            String h=String.valueOf(gcalendar.get(Calendar.HOUR));  
            String m=String.valueOf(gcalendar.get(Calendar.MINUTE));  
            String s=String.valueOf(gcalendar.get(Calendar.SECOND));  
            String date=String.valueOf(gcalendar.get(Calendar.DATE));  
            String mon=months[gcalendar.get(Calendar.MONTH)];  
            String year=String.valueOf(gcalendar.get(Calendar.YEAR));  
            String hms="Time"+" - "+h+":"+m+":"+s+" Date"+" - "+date+" "+mon+" "+year+" ";  
            int loc=textArea.getCaretPosition();  
            textArea.insert(hms,loc);  
        } else if (event.getSource() == aboutMe) {
            About frame = new About();
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } else if (event.getSource() == license) {
            License frame = new License();
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } else if (event.getSource() == helpTopics) {
          Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
          try {
            Desktop.getDesktop().browse(new URL("https://github.com/jgodwin13/jEdit/wiki").toURI());
            } catch (URISyntaxException | IOException e) {}

        }

    }

    //============*=====*=======================
    //// GETTERS AND SETTERS
    //==================*=====*===================

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea text) {
        textArea = text;
    }
}