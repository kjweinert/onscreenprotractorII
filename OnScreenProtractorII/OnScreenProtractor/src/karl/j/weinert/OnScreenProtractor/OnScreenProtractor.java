/*
 * OnScreenProtractor v0.5
 * =======================
 *
 * Copyright (C) 2016 Paolo Straffi <p_straffi@hotmail.com>
 * 
 * This file is part of OnScreenProtractor.
 *
 * OnScreenProtractor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * OnScreenProtractor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OnScreenProtractor.  If not, see <http://www.gnu.org/licenses/>.    
 */

package karl.j.weinert.OnScreenProtractor;

import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Paolo Straffi
 */
public class OnScreenProtractor extends javax.swing.JFrame implements MouseMotionListener,
        MouseListener, MouseWheelListener, KeyListener {

    // Window size at startup
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int screenWidth = screenSize.width; 
	int screenHeight = screenSize.height;
	
	
    int INI_WINDOW_WIDTH = screenWidth  -100 ;
    int INI_WINDOW_HEIGHT = screenHeight -100 ;

    // Colors
    private Color coloreAssi = new Color(255, 0, 0, 255);
    private Color coloreSegmento01 = new Color(255, 0, 0, 255);
    private Color coloreSegmento02 = new Color(255, 0, 0, 255);
    private Color coloreAngoloNOA = new Color(255, 0, 255, 100);
    private Color coloreAngoloNOB = new Color(0, 0, 255, 100);
    private Color coloreAngoloAOB = new Color(0, 200, 0, 255);
    private Color coloreExplementAOB = new Color(0, 0, 255, 255);
    private Color coloreAtan2OA = new Color(255, 0, 255, 100);
    private Color coloreAtan2OB = new Color(255, 0, 255, 100);
    private Color coloreManiglie = new Color(0, 0, 0, 255);
    private Color coloreTesto = new Color(0, 0, 0, 255);
    private Color coloreSfondoTesto = new Color(255, 255, 255, 255);
    
    //line thickness
    private int spessoreLinee = 2;
    private int massimoSpessoreLinee = 10;
    private int fontSize = 12;
    private int maxFontSize = 40;
    private int minFontSize = 9;

    // mouse coordinates
    int mouseX = 0;
    int mouseY = 0;

    // angle correction
    double correzione = Math.PI / 2;

    // radii of angles
    private int raggio = 50;
    private int raggioAngoloNOA = 55;
    private int raggioAngoloNOB = 50;
    private int raggioAngoloAOB = 160;
    private int raggioExplementAOB = 60;
    private int raggioAtan2OA = 55;
    private int raggioAtan2OB = 50;
    private int maxRadius = 200;

    // Point O (center circle)
    Point v0 = new Point(INI_WINDOW_WIDTH /2 , INI_WINDOW_HEIGHT /2 );

    // Point A
    Point v1 = new Point((INI_WINDOW_WIDTH / 2) - 40, (INI_WINDOW_HEIGHT /2 ) - 153);

    // Point B
    Point v2 = new Point((INI_WINDOW_WIDTH / 2), (INI_WINDOW_HEIGHT / 2) - 400);

    // Initialize the Angle 'Angolo'
    Angolo angolo = new Angolo(v0, v1, v2, correzione);

    // View Menu initial values
    private boolean segment01Visibility = true;
    private boolean segment02Visibility = true;
    private boolean axisVisibility = true;
    private boolean handlesVisibility = true;
    private boolean mouseCoordsVisibility = true;
    private boolean angle01Visibility = false;
    private boolean angle02Visibility = false;
    private boolean angle102Visibility = true;
    private boolean atan01Visibility = false;
    private boolean atan02Visibility = false;
    private boolean explement102Visibility = false;
    private boolean lettersVisibility = true;
    private boolean textBackgroundVisibility = false;
    private boolean radians = false;
    private boolean hideAll = false;
    private boolean defaultVisibility = false;

    // Handles size
    static int LATO_QUADRATO = 10;
    int rectWidth = LATO_QUADRATO;
    int rectHeight = LATO_QUADRATO;
    int deltaRectWidth = rectWidth / 2;
    int deltaRectHeight = rectHeight / 2;
  
    // Handle of point O
    Rectangle rect0 = new Rectangle(v0.x - deltaRectWidth,
            v0.y - deltaRectHeight, rectWidth, rectHeight);
    // Handle of point A
    Rectangle rect1 = new Rectangle(v1.x - deltaRectWidth,
            v1.y - deltaRectHeight, rectWidth, rectHeight);
    // Handle of point B
    Rectangle rect2 = new Rectangle(v2.x - deltaRectWidth,
            v2.y - deltaRectHeight, rectWidth, rectHeight);

    // Mouse cursors
    Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    //Cursor cross = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    Cursor arrow = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    Point mousePressedLocation = new Point(0, 0);

    // True if the user pressed, dragged or released the mouse outside of the
    // rectangle; false otherwise.
    boolean pressOut = false;

    // Selected point
    int pressedPoint = 4;
    // Option Dialog identifier
    String optionDialog = "";

    double minAxesLenght = 100.00d;

    // NOA (Angle between North, O and A)
    double angolo01 = 0.00d;
    // Angle between North, O and B
    double angolo02 = 0.00d;
    // Angle between A, O and B
    double angolo102 = 0.00d;
    // atan2(v0.y - v1.y, v1.x - v0.x)
    double angoloAtan01 = 0.00d;
    // atan2(v0.y - v2.y, v2.x - v0.x)
    double angoloAtan02 = 0.00d;

    /**
     * Creates new form GenericGUI
     */
    public OnScreenProtractor() {
        super(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("ONSCREENPROTRACTOR"));

        setLayout(new BorderLayout());
        setFocusable(true);
        addKeyListener(this);

        setBackground(new Color(0, 0, 0, 0));
        setMinimumSize(new Dimension(200, 200));
        setSize(new Dimension(INI_WINDOW_WIDTH, INI_WINDOW_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                // NOA (Angle between North, O and A)
                angolo01 = angolo.valoreAngolo01();
                // NOB (Angle between North, O and B)
                angolo02 = angolo.valoreAngolo02();
                // AOB (Angle between A, O and B)
                angolo102 = angolo.valoreAngolo();
                // atan01 (Angle between East, O and A)
                angoloAtan01 = angolo.arcoTangente(v0, v1);
                // atan02 (Angle between East, O and B)
                angoloAtan02 = angolo.arcoTangente(v0, v2);

                if (g instanceof Graphics2D) {
                    final int R = 240;
                    final int G = 240;
                    final int B = 240;

                    // Transparent Background
                    Paint p = new Color(0, 0, 0, 1);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(p);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Set thickness of lines
                    g2d.setStroke(new BasicStroke(spessoreLinee));

                    // Set font size
                    g2d.setFont(new Font(g.getFont().getFontName(), Font.BOLD, fontSize));

                    if (minAxesLenght < Math.sqrt(Math.pow(getWidth(),2.0) + Math.pow(getHeight(),2.0))) {
                        minAxesLenght = Math.sqrt(Math.pow(getWidth(),2.0) + Math.pow(getHeight(),2.0));
                    }
                    
                    if (mouseCoordsVisibility) {
                        g2d.setColor(coloreTesto);
                        g2d.drawString("(" + Integer.toString(mouseX) + ", "
                                + Integer.toString(mouseY) + ")", mouseX + 15, mouseY + 20);
                        //String ang = "angle: " + String.format("%.2f 째", angolo.valoreAngolo());
                        String ang = java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("ANGLE") + String.format("%.2f �", angolo.valoreAngolo());
                        //Karl: Moved the angle marker to static spot in window.
                        g2d.drawString(ang, INI_WINDOW_WIDTH -1777 , INI_WINDOW_HEIGHT - 955);
                        //g2d.drawString(ang, mouseX + 15, mouseY + 40);
                    }

                    if (lettersVisibility) {
                        String strO = "O (" + Integer.toString(v0.x) + ", "
                                + Integer.toString(v0.y) + ")";
                        String strA = "A (" + Integer.toString(v1.x) + ", "
                                + Integer.toString(v1.y) + ")";
                        String strB = "B (" + Integer.toString(v2.x) + ", "
                                + Integer.toString(v2.y) + ")";

                        if (textBackgroundVisibility) {
                            g2d.setColor(coloreSfondoTesto);
                            // StrO Background
                            g2d.fillRect(v0.x + 5, v0.y + 20 - g2d.getFontMetrics().getAscent(),
                                    (int) g2d.getFontMetrics().getStringBounds(strO, g)
                                    .getWidth(), (int) g2d.getFontMetrics()
                                    .getStringBounds(strO, g).getHeight());
                            // StrA Background
                            g2d.fillRect(v1.x + 5, v1.y - 10 - g2d.getFontMetrics().getAscent(),
                                    (int) g2d.getFontMetrics().getStringBounds(strA, g)
                                    .getWidth(), (int) g2d.getFontMetrics()
                                    .getStringBounds(strA, g).getHeight());
                            // StrB Background
                            g2d.fillRect(v2.x + 5, v2.y + 20 - g2d.getFontMetrics().getAscent(),
                                    (int) g2d.getFontMetrics().getStringBounds(strB, g)
                                    .getWidth(), (int) g2d.getFontMetrics()
                                    .getStringBounds(strB, g).getHeight());
                        }
                        // Text color
                        g2d.setColor(coloreTesto);
                        // O coordinates
                        g2d.drawString(strO, v0.x + 5, v0.y + 20);
                        // A coordinates
                        g2d.drawString(strA, v1.x + 5, v1.y - 10);
                        // B coordinates
                        g2d.drawString(strB, v2.x + 5, v2.y + 20);

                    }

                    if (handlesVisibility) {
                        g2d.setColor(coloreManiglie);
                        g2d.drawOval(v0.x - deltaRectWidth, v0.y - deltaRectWidth, rectWidth, rectHeight);
                        g2d.drawRect(v1.x - deltaRectWidth, v1.y - deltaRectWidth, rectWidth,
                                rectHeight);
                        g2d.drawRect(v2.x - deltaRectWidth, v2.y - deltaRectWidth, rectWidth,
                                rectHeight);
                    }

                    if (segment01Visibility) {
                        g2d.setColor(coloreAssi);
                        g2d.drawLine(v0.x, v0.y, v1.x, v1.y);
                    }

                    if (segment02Visibility) {
                        g2d.setColor(coloreAssi);
                        g2d.drawLine(v0.x, v0.y, v2.x, v2.y);
                    }

                    if (axisVisibility) {
                        disegnaAssi(g2d);
                    }

                    if (angle01Visibility) {
                        disegnaAngolo01(g2d);

                    }

                    if (angle02Visibility) {
                        disegnaAngolo02(g2d);
                    }

                    if (angle102Visibility) {
                        disegnaAngolo102(g2d);
                    }

                    if (explement102Visibility) {
                        disegnaExplement102(g2d);
                    }
                    if (atan01Visibility) {
                        disegnaAtan01(g2d);
                    }
                    if (atan02Visibility) {
                        disegnaAtan02(g2d);
                    }
                }
            }
        };
        panel.addMouseListener(this);
        panel.addMouseWheelListener(this);
        panel.addMouseMotionListener(this);
        setContentPane(panel);
        //setLayout(new GridBagLayout());

        /**
         * ***************************
         */
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jColorChooser1 = new javax.swing.JColorChooser();
        jFileChooser1 = new javax.swing.JFileChooser();
        jOptionPaneAbout = new javax.swing.JOptionPane();
        jDialogMaxThickness = new javax.swing.JDialog();
        jButtonOK = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jFormattedTextFieldMaxThickness = new javax.swing.JFormattedTextField();
        jDialogMaxFontSize = new javax.swing.JDialog();
        jButtonOK1 = new javax.swing.JButton();
        jButtonCancel1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextFieldMaxFontSize = new javax.swing.JFormattedTextField();
        jDialogIntValue = new javax.swing.JDialog();
        jButtonOKIntValue = new javax.swing.JButton();
        jButtonCancelIntValue = new javax.swing.JButton();
        jLabelIntValue = new javax.swing.JLabel();
        jFormattedTextFieldIntValue = new javax.swing.JFormattedTextField();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemSave = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        jCheckBoxMenuItemMouseCoords = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAxes = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemSegment01 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemSegment02 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAngoloAOB = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemExplement102 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAngoloNOA = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAngoloNOB = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAtan01 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemAtan02 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemHandles = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemLetters = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemTextBackground = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemRadians = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemHideAll = new javax.swing.JCheckBoxMenuItem();
        menuOptions = new javax.swing.JMenu();
        menuLinesThickness = new javax.swing.JMenu();
        menuItemIncrease = new javax.swing.JMenuItem();
        menuItemDecrease = new javax.swing.JMenuItem();
        jMenuItemMaxThickness = new javax.swing.JMenuItem();
        jMenuFontSize = new javax.swing.JMenu();
        jMenuItemIncreaseFontSize = new javax.swing.JMenuItem();
        jMenuItemDecreaseFontSize = new javax.swing.JMenuItem();
        jMenuItemMaxFontSize = new javax.swing.JMenuItem();
        menuColors = new javax.swing.JMenu();
        jMenuAnglesColor = new javax.swing.JMenu();
        jMenuItemAngleAOBColor = new javax.swing.JMenuItem();
        jMenuItemAngleExplementAOBColor = new javax.swing.JMenuItem();
        jMenuItemNOAColor = new javax.swing.JMenuItem();
        jMenuItemNOBColor = new javax.swing.JMenuItem();
        jMenuItemAtan2OAColor = new javax.swing.JMenuItem();
        jMenuItemAtan2OBColor = new javax.swing.JMenuItem();
        menuItemAxesColor = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItemTextColor = new javax.swing.JMenuItem();
        jMenuItemTextBackgroundColor = new javax.swing.JMenuItem();
        jMenuRadius = new javax.swing.JMenu();
        jMenuItemAOBRadius = new javax.swing.JMenuItem();
        jMenuItemExplementAOBRadius = new javax.swing.JMenuItem();
        jMenuItemExplementNOARadius = new javax.swing.JMenuItem();
        jMenuItemExplementNOBRadius = new javax.swing.JMenuItem();
        jMenuItemExplementAtanOARadius = new javax.swing.JMenuItem();
        jMenuItemExplementAtanOBRadius = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();
        menuCapture = new javax.swing.JMenu();

        jFileChooser1.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle"); // NOI18N
        jFileChooser1.setDialogTitle(bundle.getString("OnScreenProtractor.jFileChooser1.dialogTitle")); // NOI18N
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("PNG file", "png");
        jFileChooser1.setFileFilter(filter);

        jDialogMaxThickness.setTitle(bundle.getString("OnScreenProtractor.jDialogMaxThickness.title")); // NOI18N
        jDialogMaxThickness.setLocation(getLocation());
        jDialogMaxThickness.setMinimumSize(new java.awt.Dimension(250, 120));

        jButtonOK.setText(bundle.getString("OnScreenProtractor.jButtonOK.text")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jButtonCancel.setText(bundle.getString("OnScreenProtractor.jButtonCancel.text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("OnScreenProtractor.jLabel1.text")); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jFormattedTextFieldMaxThickness.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jFormattedTextFieldMaxThickness.setText(Integer.toString(massimoSpessoreLinee)
        );
        jFormattedTextFieldMaxThickness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldMaxThicknessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialogMaxThicknessLayout = new javax.swing.GroupLayout(jDialogMaxThickness.getContentPane());
        jDialogMaxThickness.getContentPane().setLayout(jDialogMaxThicknessLayout);
        jDialogMaxThicknessLayout.setHorizontalGroup(
            jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogMaxThicknessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldMaxThickness, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialogMaxThicknessLayout.setVerticalGroup(
            jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogMaxThicknessLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialogMaxThicknessLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jFormattedTextFieldMaxThickness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogMaxThicknessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55))
        );

        jDialogMaxFontSize.setTitle(bundle.getString("OnScreenProtractor.jDialogMaxFontSize.title")); // NOI18N
        jDialogMaxFontSize.setLocation(getLocation());
        jDialogMaxFontSize.setMinimumSize(new java.awt.Dimension(250, 120));

        jButtonOK1.setText(bundle.getString("OnScreenProtractor.jButtonOK1.text")); // NOI18N
        jButtonOK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOK1ActionPerformed(evt);
            }
        });

        jButtonCancel1.setText(bundle.getString("OnScreenProtractor.jButtonCancel1.text")); // NOI18N
        jButtonCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancel1ActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(bundle.getString("OnScreenProtractor.jLabel2.text")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jFormattedTextFieldMaxFontSize.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jFormattedTextFieldMaxFontSize.setText(Integer.toString(maxFontSize)
        );
        jFormattedTextFieldMaxFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldMaxFontSizeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialogMaxFontSizeLayout = new javax.swing.GroupLayout(jDialogMaxFontSize.getContentPane());
        jDialogMaxFontSize.getContentPane().setLayout(jDialogMaxFontSizeLayout);
        jDialogMaxFontSizeLayout.setHorizontalGroup(
            jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogMaxFontSizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonOK1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldMaxFontSize, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonCancel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialogMaxFontSizeLayout.setVerticalGroup(
            jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogMaxFontSizeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialogMaxFontSizeLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jFormattedTextFieldMaxFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogMaxFontSizeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonOK1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55))
        );

        jDialogIntValue.setTitle(bundle.getString("OnScreenProtractor.jDialogIntValue.title")); // NOI18N
        jDialogIntValue.setLocation(getLocation());
        jDialogIntValue.setMinimumSize(new java.awt.Dimension(250, 120));

        jButtonOKIntValue.setText(bundle.getString("OnScreenProtractor.jButtonOKIntValue.text")); // NOI18N
        jButtonOKIntValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKIntValueActionPerformed(evt);
            }
        });

        jButtonCancelIntValue.setText(bundle.getString("OnScreenProtractor.jButtonCancelIntValue.text")); // NOI18N
        jButtonCancelIntValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelIntValueActionPerformed(evt);
            }
        });

        jLabelIntValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelIntValue.setText(bundle.getString("OnScreenProtractor.jLabelIntValue.text")); // NOI18N
        jLabelIntValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jFormattedTextFieldIntValue.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jFormattedTextFieldIntValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldIntValueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialogIntValueLayout = new javax.swing.GroupLayout(jDialogIntValue.getContentPane());
        jDialogIntValue.getContentPane().setLayout(jDialogIntValueLayout);
        jDialogIntValueLayout.setHorizontalGroup(
            jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialogIntValueLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelIntValue, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonOKIntValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFormattedTextFieldIntValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jButtonCancelIntValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialogIntValueLayout.setVerticalGroup(
            jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialogIntValueLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialogIntValueLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabelIntValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jFormattedTextFieldIntValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialogIntValueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelIntValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonOKIntValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(200, 200));

        menuFile.setText(bundle.getString("OnScreenProtractor.menuFile.text")); // NOI18N

        menuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        menuItemSave.setText(bundle.getString("OnScreenProtractor.menuItemSave.text")); // NOI18N
        menuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSaveActionPerformed(evt);
            }
        });
        menuFile.add(menuItemSave);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setText(bundle.getString("OnScreenProtractor.menuItemExit.text")); // NOI18N
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);

        menuView.setText(bundle.getString("OnScreenProtractor.menuView.text")); // NOI18N

        jCheckBoxMenuItemMouseCoords.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemMouseCoords.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemMouseCoords.text")); // NOI18N
        jCheckBoxMenuItemMouseCoords.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemMouseCoordsItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemMouseCoords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemMouseCoordsActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemMouseCoords);

        jCheckBoxMenuItemAxes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemAxes.setSelected(true);
        jCheckBoxMenuItemAxes.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAxes.text")); // NOI18N
        jCheckBoxMenuItemAxes.setToolTipText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAxes.toolTipText")); // NOI18N
        jCheckBoxMenuItemAxes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAxesItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAxes);

        jCheckBoxMenuItemSegment01.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemSegment01.setSelected(true);
        jCheckBoxMenuItemSegment01.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemSegment01.text")); // NOI18N
        jCheckBoxMenuItemSegment01.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemSegment01ItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemSegment01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemSegment01ActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemSegment01);

        jCheckBoxMenuItemSegment02.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemSegment02.setSelected(true);
        jCheckBoxMenuItemSegment02.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemSegment02.text")); // NOI18N
        jCheckBoxMenuItemSegment02.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemSegment02ItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemSegment02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemSegment02ActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemSegment02);

        jCheckBoxMenuItemAngoloAOB.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemAngoloAOB.setSelected(true);
        jCheckBoxMenuItemAngoloAOB.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAngoloAOB.text")); // NOI18N
        jCheckBoxMenuItemAngoloAOB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAngoloAOBItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAngoloAOB);

        jCheckBoxMenuItemExplement102.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemExplement102.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemExplement102.text")); // NOI18N
        jCheckBoxMenuItemExplement102.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemExplement102ItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemExplement102);

        jCheckBoxMenuItemAngoloNOA.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemAngoloNOA.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAngoloNOA.text")); // NOI18N
        jCheckBoxMenuItemAngoloNOA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAngoloNOAItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemAngoloNOA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemAngoloNOAActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAngoloNOA);

        jCheckBoxMenuItemAngoloNOB.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemAngoloNOB.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAngoloNOB.text")); // NOI18N
        jCheckBoxMenuItemAngoloNOB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAngoloNOBItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAngoloNOB);

        jCheckBoxMenuItemAtan01.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAtan01.text")); // NOI18N
        jCheckBoxMenuItemAtan01.setToolTipText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAtan01.toolTipText")); // NOI18N
        jCheckBoxMenuItemAtan01.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAtan01ItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemAtan01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemAtan01ActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAtan01);

        jCheckBoxMenuItemAtan02.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAtan02.text")); // NOI18N
        jCheckBoxMenuItemAtan02.setToolTipText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemAtan02.toolTipText")); // NOI18N
        jCheckBoxMenuItemAtan02.setAutoscrolls(true);
        jCheckBoxMenuItemAtan02.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemAtan02ItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemAtan02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemAtan02ActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemAtan02);

        jCheckBoxMenuItemHandles.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemHandles.setSelected(true);
        jCheckBoxMenuItemHandles.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemHandles.text")); // NOI18N
        jCheckBoxMenuItemHandles.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemHandlesItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemHandles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemHandlesActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemHandles);

        jCheckBoxMenuItemLetters.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemLetters.setSelected(true);
        jCheckBoxMenuItemLetters.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemLetters.text")); // NOI18N
        jCheckBoxMenuItemLetters.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemLettersItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemLetters);

        jCheckBoxMenuItemTextBackground.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemTextBackground.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemTextBackground.text")); // NOI18N
        jCheckBoxMenuItemTextBackground.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemTextBackgroundItemStateChanged(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemTextBackground);

        jCheckBoxMenuItemRadians.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemRadians.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemRadians.text")); // NOI18N
        jCheckBoxMenuItemRadians.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemRadiansItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemRadians.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemRadiansActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemRadians);

        jCheckBoxMenuItemHideAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jCheckBoxMenuItemHideAll.setText(bundle.getString("OnScreenProtractor.jCheckBoxMenuItemHideAll.text")); // NOI18N
        jCheckBoxMenuItemHideAll.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemHideAllItemStateChanged(evt);
            }
        });
        jCheckBoxMenuItemHideAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemHideAllActionPerformed(evt);
            }
        });
        menuView.add(jCheckBoxMenuItemHideAll);

        menuBar.add(menuView);

        menuOptions.setText(bundle.getString("OnScreenProtractor.menuOptions.text")); // NOI18N

        menuLinesThickness.setText(bundle.getString("OnScreenProtractor.menuLinesThickness.text")); // NOI18N

        menuItemIncrease.setText(bundle.getString("OnScreenProtractor.menuItemIncrease.text")); // NOI18N
        menuItemIncrease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemIncreaseActionPerformed(evt);
            }
        });
        menuLinesThickness.add(menuItemIncrease);

        menuItemDecrease.setText(bundle.getString("OnScreenProtractor.menuItemDecrease.text")); // NOI18N
        menuItemDecrease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDecreaseActionPerformed(evt);
            }
        });
        menuLinesThickness.add(menuItemDecrease);

        jMenuItemMaxThickness.setText(bundle.getString("OnScreenProtractor.jMenuItemMaxThickness.text")); // NOI18N
        jMenuItemMaxThickness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMaxThicknessActionPerformed(evt);
            }
        });
        menuLinesThickness.add(jMenuItemMaxThickness);

        menuOptions.add(menuLinesThickness);

        jMenuFontSize.setText(bundle.getString("OnScreenProtractor.jMenuFontSize.text")); // NOI18N
        jMenuFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFontSizeActionPerformed(evt);
            }
        });

        jMenuItemIncreaseFontSize.setText(bundle.getString("OnScreenProtractor.jMenuItemIncreaseFontSize.text")); // NOI18N
        jMenuItemIncreaseFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIncreaseFontSizeActionPerformed(evt);
            }
        });
        jMenuFontSize.add(jMenuItemIncreaseFontSize);

        jMenuItemDecreaseFontSize.setText(bundle.getString("OnScreenProtractor.jMenuItemDecreaseFontSize.text")); // NOI18N
        jMenuItemDecreaseFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDecreaseFontSizeActionPerformed(evt);
            }
        });
        jMenuFontSize.add(jMenuItemDecreaseFontSize);

        jMenuItemMaxFontSize.setText(bundle.getString("OnScreenProtractor.jMenuItemMaxFontSize.text")); // NOI18N
        jMenuItemMaxFontSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMaxFontSizeActionPerformed(evt);
            }
        });
        jMenuFontSize.add(jMenuItemMaxFontSize);

        menuOptions.add(jMenuFontSize);

        menuColors.setText(bundle.getString("OnScreenProtractor.menuColors.text")); // NOI18N

        jMenuAnglesColor.setText(bundle.getString("OnScreenProtractor.jMenuAnglesColor.text")); // NOI18N

        jMenuItemAngleAOBColor.setText(bundle.getString("OnScreenProtractor.jMenuItemAngleAOBColor.text")); // NOI18N
        jMenuItemAngleAOBColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAngleAOBColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemAngleAOBColor);

        jMenuItemAngleExplementAOBColor.setText(bundle.getString("OnScreenProtractor.jMenuItemAngleExplementAOBColor.text")); // NOI18N
        jMenuItemAngleExplementAOBColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAngleExplementAOBColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemAngleExplementAOBColor);

        jMenuItemNOAColor.setText(bundle.getString("OnScreenProtractor.jMenuItemNOAColor.text")); // NOI18N
        jMenuItemNOAColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNOAColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemNOAColor);

        jMenuItemNOBColor.setText(bundle.getString("OnScreenProtractor.jMenuItemNOBColor.text")); // NOI18N
        jMenuItemNOBColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNOBColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemNOBColor);

        jMenuItemAtan2OAColor.setText(bundle.getString("OnScreenProtractor.jMenuItemAtan2OAColor.text")); // NOI18N
        jMenuItemAtan2OAColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAtan2OAColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemAtan2OAColor);

        jMenuItemAtan2OBColor.setText(bundle.getString("OnScreenProtractor.jMenuItemAtan2OBColor.text")); // NOI18N
        jMenuItemAtan2OBColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAtan2OBColorActionPerformed(evt);
            }
        });
        jMenuAnglesColor.add(jMenuItemAtan2OBColor);

        menuColors.add(jMenuAnglesColor);

        menuItemAxesColor.setText(bundle.getString("OnScreenProtractor.menuItemAxesColor.text")); // NOI18N
        menuItemAxesColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAxesColorActionPerformed(evt);
            }
        });
        menuColors.add(menuItemAxesColor);

        jMenuItem2.setText(bundle.getString("OnScreenProtractor.jMenuItem2.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        menuColors.add(jMenuItem2);

        jMenuItemTextColor.setText(bundle.getString("OnScreenProtractor.jMenuItemTextColor.text")); // NOI18N
        jMenuItemTextColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTextColorActionPerformed(evt);
            }
        });
        menuColors.add(jMenuItemTextColor);

        jMenuItemTextBackgroundColor.setText(bundle.getString("OnScreenProtractor.jMenuItemTextBackgroundColor.text")); // NOI18N
        jMenuItemTextBackgroundColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTextBackgroundColorActionPerformed(evt);
            }
        });
        menuColors.add(jMenuItemTextBackgroundColor);

        menuOptions.add(menuColors);

        jMenuRadius.setText(bundle.getString("OnScreenProtractor.jMenuRadius.text")); // NOI18N
        jMenuRadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuRadiusActionPerformed(evt);
            }
        });

        jMenuItemAOBRadius.setText(bundle.getString("OnScreenProtractor.jMenuItemAOBRadius.text")); // NOI18N
        jMenuItemAOBRadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAOBRadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemAOBRadius);

        jMenuItemExplementAOBRadius.setText(bundle.getString("OnScreenProtractor.jMenuItemExplementAOBRadius.text")); // NOI18N
        jMenuItemExplementAOBRadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExplementAOBRadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemExplementAOBRadius);

        jMenuItemExplementNOARadius.setText(bundle.getString("OnScreenProtractor.jMenuItemExplementNOARadius.text")); // NOI18N
        jMenuItemExplementNOARadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExplementNOARadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemExplementNOARadius);

        jMenuItemExplementNOBRadius.setText(bundle.getString("OnScreenProtractor.jMenuItemExplementNOBRadius.text")); // NOI18N
        jMenuItemExplementNOBRadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExplementNOBRadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemExplementNOBRadius);

        jMenuItemExplementAtanOARadius.setText(bundle.getString("OnScreenProtractor.jMenuItemExplementAtanOARadius.text")); // NOI18N
        jMenuItemExplementAtanOARadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExplementAtanOARadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemExplementAtanOARadius);

        jMenuItemExplementAtanOBRadius.setText(bundle.getString("OnScreenProtractor.jMenuItemExplementAtanOBRadius.text")); // NOI18N
        jMenuItemExplementAtanOBRadius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExplementAtanOBRadiusActionPerformed(evt);
            }
        });
        jMenuRadius.add(jMenuItemExplementAtanOBRadius);

        menuOptions.add(jMenuRadius);

        menuBar.add(menuOptions);

        menuHelp.setText(bundle.getString("OnScreenProtractor.menuHelp.text")); // NOI18N

        menuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemHelp.setText(bundle.getString("OnScreenProtractor.menuItemHelp.text")); // NOI18N
        menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHelpActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemHelp);

        menuItemAbout.setText(bundle.getString("OnScreenProtractor.menuItemAbout.text")); // NOI18N
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        menuBar.add(menuHelp);

        menuCapture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/karl/j/weinert/OnScreenProtractor/res/snapshot.png"))); // NOI18N
        menuCapture.setText(bundle.getString("OnScreenProtractor.menuCapture.text")); // NOI18N
        menuCapture.setActionCommand(bundle.getString("OnScreenProtractor.menuCapture.actionCommand")); // NOI18N
        menuCapture.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        menuCapture.setIconTextGap(1);
        menuCapture.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        menuCapture.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuCaptureMouseClicked(evt);
            }
        });

        menuBar.add(javax.swing.Box.createHorizontalGlue());

        menuBar.add(menuCapture);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
        jOptionPaneAbout.showMessageDialog(this, 
                java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jOptionPaneAbout.text"),
                java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jOptionPaneAbout.title"),
                jOptionPaneAbout.INFORMATION_MESSAGE);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuItemAxesColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAxesColorActionPerformed
        // Open color chooser dialog and assign the choice to coloreAssi
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAssi);
        if (color != null) {
            coloreAssi = color;
        }
    }//GEN-LAST:event_menuItemAxesColorActionPerformed

    private void menuCaptureMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuCaptureMouseClicked
        savePNG();
        // Menu Capture reset
        repaint();
    }//GEN-LAST:event_menuCaptureMouseClicked

    private void jCheckBoxMenuItemLettersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemLettersItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            lettersVisibility = true;
        } else {
            lettersVisibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemLettersItemStateChanged

    private void jCheckBoxMenuItemHandlesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemHandlesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            handlesVisibility = true;
        } else {
            handlesVisibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemHandlesItemStateChanged

    private void jCheckBoxMenuItemAxesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAxesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            axisVisibility = true;
        } else {
            axisVisibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAxesItemStateChanged

    private void menuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSaveActionPerformed
        savePNG();

    }//GEN-LAST:event_menuItemSaveActionPerformed

    private void jCheckBoxMenuItemMouseCoordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemMouseCoordsActionPerformed
        
    }//GEN-LAST:event_jCheckBoxMenuItemMouseCoordsActionPerformed

    private void jCheckBoxMenuItemMouseCoordsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemMouseCoordsItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            mouseCoordsVisibility = true;
        } else {
            mouseCoordsVisibility = false;
        }
    }//GEN-LAST:event_jCheckBoxMenuItemMouseCoordsItemStateChanged

    private void menuItemIncreaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemIncreaseActionPerformed
        incrementoSpessore('+');
        repaint();
    }//GEN-LAST:event_menuItemIncreaseActionPerformed

    private void menuItemDecreaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDecreaseActionPerformed
        incrementoSpessore('-');
        repaint();
    }//GEN-LAST:event_menuItemDecreaseActionPerformed

    private void menuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHelpActionPerformed
        try {
            final String filename = java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("HELP");
            final File file = new File(filename);

            if (file.exists()) {
                java.awt.Desktop.getDesktop().browse(file.toURI());
            } else {
                System.out.println(file.getAbsolutePath()
                        + " does not exist");
            }
        } catch (java.io.IOException ex) {
            System.out.println(ex.getMessage());
        }
    }//GEN-LAST:event_menuItemHelpActionPerformed

    private void jMenuItemTextColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextColorActionPerformed
        // Open color chooser dialog and assign the choice to coloreTesto
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreTesto);
        if (color != null) {
            coloreTesto = color;
        }
    }//GEN-LAST:event_jMenuItemTextColorActionPerformed

    private void jCheckBoxMenuItemAngoloNOAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAngoloNOAItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            angle01Visibility = true;
        } else {
            angle01Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAngoloNOAItemStateChanged

    private void jCheckBoxMenuItemAngoloNOAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAngoloNOAActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemAngoloNOAActionPerformed

    private void jCheckBoxMenuItemAngoloNOBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAngoloNOBItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            angle02Visibility = true;
        } else {
            angle02Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAngoloNOBItemStateChanged

    private void jCheckBoxMenuItemAngoloAOBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAngoloAOBItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            angle102Visibility = true;
        } else {
            angle102Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAngoloAOBItemStateChanged

    private void jCheckBoxMenuItemExplement102ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemExplement102ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            explement102Visibility = true;
        } else {
            explement102Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemExplement102ItemStateChanged

    private void jCheckBoxMenuItemHandlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemHandlesActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemHandlesActionPerformed

    private void jCheckBoxMenuItemTextBackgroundItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemTextBackgroundItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            textBackgroundVisibility = true;
        } else {
            textBackgroundVisibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemTextBackgroundItemStateChanged

    private void jCheckBoxMenuItemRadiansItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemRadiansItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            radians = true;
        } else {
            radians = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemRadiansItemStateChanged

    private void jCheckBoxMenuItemRadiansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemRadiansActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemRadiansActionPerformed

    private void jMenuItemAngleAOBColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAngleAOBColorActionPerformed
        // Open color chooser dialog and assign the choice to coloreAssi
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAngoloAOB);
        if (color != null) {
            coloreAngoloAOB = color;
        }
    }//GEN-LAST:event_jMenuItemAngleAOBColorActionPerformed

    private void jCheckBoxMenuItemAtan01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAtan01ActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemAtan01ActionPerformed

    private void jCheckBoxMenuItemAtan01ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAtan01ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            atan01Visibility = true;
        } else {
            atan01Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAtan01ItemStateChanged

    private void jCheckBoxMenuItemAtan02ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAtan02ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            atan02Visibility = true;
        } else {
            atan02Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemAtan02ItemStateChanged

    private void jCheckBoxMenuItemAtan02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemAtan02ActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemAtan02ActionPerformed

    private void jCheckBoxMenuItemSegment01ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSegment01ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            segment01Visibility = true;
        } else {
            segment01Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemSegment01ItemStateChanged

    private void jCheckBoxMenuItemSegment02ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSegment02ItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            segment02Visibility = true;
        } else {
            segment02Visibility = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemSegment02ItemStateChanged

    private void jCheckBoxMenuItemSegment02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSegment02ActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemSegment02ActionPerformed

    private void jCheckBoxMenuItemHideAllItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemHideAllItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            segment01Visibility = false;
            jCheckBoxMenuItemSegment01.setSelected(false);
            segment02Visibility = false;
            jCheckBoxMenuItemSegment02.setSelected(false);
            axisVisibility = false;
            jCheckBoxMenuItemAxes.setSelected(false);
            handlesVisibility = false;
            jCheckBoxMenuItemHandles.setSelected(false);
            mouseCoordsVisibility = false;
            jCheckBoxMenuItemMouseCoords.setSelected(false);
            angle01Visibility = false;
            jCheckBoxMenuItemAngoloNOA.setSelected(false);
            angle02Visibility = false;
            jCheckBoxMenuItemAngoloNOB.setSelected(false);
            angle102Visibility = false;
            jCheckBoxMenuItemAngoloAOB.setSelected(false);
            atan01Visibility = false;
            jCheckBoxMenuItemAtan01.setSelected(false);
            atan02Visibility = false;
            jCheckBoxMenuItemAtan02.setSelected(false);
            explement102Visibility = false;
            jCheckBoxMenuItemExplement102.setSelected(false);
            lettersVisibility = false;
            jCheckBoxMenuItemLetters.setSelected(false);
            textBackgroundVisibility = false;
            jCheckBoxMenuItemTextBackground.setSelected(false);
            radians = false;
            jCheckBoxMenuItemRadians.setSelected(false);
        } else {
            segment01Visibility = true;
            jCheckBoxMenuItemSegment01.setSelected(true);
            segment02Visibility = true;
            jCheckBoxMenuItemSegment02.setSelected(true);
            axisVisibility = true;
            jCheckBoxMenuItemAxes.setSelected(true);
            handlesVisibility = true;
            jCheckBoxMenuItemHandles.setSelected(true);
            mouseCoordsVisibility = false;
            angle01Visibility = false;
            angle02Visibility = false;
            angle102Visibility = true;
            jCheckBoxMenuItemAngoloAOB.setSelected(true);
            atan01Visibility = false;
            atan02Visibility = false;
            explement102Visibility = false;
            lettersVisibility = true;
            jCheckBoxMenuItemLetters.setSelected(true);
            textBackgroundVisibility = false;
            radians = false;
        }
        repaint();
    }//GEN-LAST:event_jCheckBoxMenuItemHideAllItemStateChanged

    private void jCheckBoxMenuItemHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemHideAllActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemHideAllActionPerformed

    private void jMenuItemTextBackgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTextBackgroundColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreSfondoTesto);
        if (color != null) {
            coloreSfondoTesto = color;
        }
    }//GEN-LAST:event_jMenuItemTextBackgroundColorActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        setMaxThickness();
        repaint();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jMenuItemMaxThicknessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMaxThicknessActionPerformed
        jDialogMaxThickness.setVisible(true);
        jDialogMaxThickness.setLocationRelativeTo(rootPane);
        jFormattedTextFieldMaxThickness.setText(jFormattedTextFieldMaxThickness.getText());
        jFormattedTextFieldMaxThickness.selectAll();
    }//GEN-LAST:event_jMenuItemMaxThicknessActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        jDialogMaxThickness.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jFormattedTextFieldMaxThicknessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMaxThicknessActionPerformed
        setMaxThickness();
        repaint();
    }//GEN-LAST:event_jFormattedTextFieldMaxThicknessActionPerformed

    private void jButtonOK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOK1ActionPerformed
        setMaxFontSize();
        repaint();
    }//GEN-LAST:event_jButtonOK1ActionPerformed

    private void jButtonCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancel1ActionPerformed
        jDialogMaxFontSize.dispose();
    }//GEN-LAST:event_jButtonCancel1ActionPerformed

    private void jFormattedTextFieldMaxFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldMaxFontSizeActionPerformed
        setMaxFontSize();
        repaint();
    }//GEN-LAST:event_jFormattedTextFieldMaxFontSizeActionPerformed

    private void jMenuFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFontSizeActionPerformed

    }//GEN-LAST:event_jMenuFontSizeActionPerformed

    private void jMenuItemMaxFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMaxFontSizeActionPerformed
        jDialogMaxFontSize.setVisible(true);
        jDialogMaxFontSize.setLocationRelativeTo(rootPane);
        jFormattedTextFieldMaxFontSize.setText(jFormattedTextFieldMaxFontSize.getText());
        jFormattedTextFieldMaxFontSize.selectAll();
    }//GEN-LAST:event_jMenuItemMaxFontSizeActionPerformed

    private void jMenuItemIncreaseFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemIncreaseFontSizeActionPerformed
        incrementoFontSize('+');
        repaint();
    }//GEN-LAST:event_jMenuItemIncreaseFontSizeActionPerformed

    private void jMenuItemDecreaseFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDecreaseFontSizeActionPerformed
        incrementoFontSize('-');
        repaint();
    }//GEN-LAST:event_jMenuItemDecreaseFontSizeActionPerformed

    private void jMenuItemAtan2OAColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAtan2OAColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAtan2OA);
        if (color != null) {
            coloreAtan2OA = color;
        }

    }//GEN-LAST:event_jMenuItemAtan2OAColorActionPerformed

    private void jMenuItemAtan2OBColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAtan2OBColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAtan2OB);
        if (color != null) {
            coloreAtan2OB = color;
        }
    }//GEN-LAST:event_jMenuItemAtan2OBColorActionPerformed

    private void jMenuItemNOAColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNOAColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAngoloNOA);
        if (color != null) {
            coloreAngoloNOA = color;
        }
    }//GEN-LAST:event_jMenuItemNOAColorActionPerformed

    private void jMenuItemNOBColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNOBColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreAngoloNOB);
        if (color != null) {
            coloreAngoloNOB = color;
        }
    }//GEN-LAST:event_jMenuItemNOBColorActionPerformed

    private void jMenuItemAngleExplementAOBColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAngleExplementAOBColorActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreExplementAOB);
        if (color != null) {
            coloreExplementAOB = color;
        }
    }//GEN-LAST:event_jMenuItemAngleExplementAOBColorActionPerformed

    private void jMenuRadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuRadiusActionPerformed

    }//GEN-LAST:event_jMenuRadiusActionPerformed

    private void jButtonOKIntValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKIntValueActionPerformed
        oKOptionDialog(optionDialog, Integer.parseInt(jFormattedTextFieldIntValue.getText()));
    }//GEN-LAST:event_jButtonOKIntValueActionPerformed

    private void jButtonCancelIntValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelIntValueActionPerformed
        jDialogIntValue.dispose();
    }//GEN-LAST:event_jButtonCancelIntValueActionPerformed

    private void jFormattedTextFieldIntValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldIntValueActionPerformed
        oKOptionDialog(optionDialog, Integer.parseInt(jFormattedTextFieldIntValue.getText()));
    }//GEN-LAST:event_jFormattedTextFieldIntValueActionPerformed

    private void jMenuItemAOBRadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAOBRadiusActionPerformed
        optionDialog = "raggioAOB";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("AOB");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioAngoloAOB));
        jFormattedTextFieldIntValue.selectAll();

    }//GEN-LAST:event_jMenuItemAOBRadiusActionPerformed

    private void jMenuItemExplementAOBRadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExplementAOBRadiusActionPerformed
        optionDialog = "raggioExpAOB";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("360째 - AOB");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioExplementAOB));
        jFormattedTextFieldIntValue.selectAll();
    }//GEN-LAST:event_jMenuItemExplementAOBRadiusActionPerformed

    private void jMenuItemExplementNOARadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExplementNOARadiusActionPerformed
        optionDialog = "raggioNOA";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("NOA");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioAngoloNOA));
        jFormattedTextFieldIntValue.selectAll();
    }//GEN-LAST:event_jMenuItemExplementNOARadiusActionPerformed

    private void jMenuItemExplementNOBRadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExplementNOBRadiusActionPerformed
        optionDialog = "raggioNOB";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("NOB");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioAngoloNOB));
        jFormattedTextFieldIntValue.selectAll();
    }//GEN-LAST:event_jMenuItemExplementNOBRadiusActionPerformed

    private void jMenuItemExplementAtanOARadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExplementAtanOARadiusActionPerformed
        optionDialog = "raggioAtanOA";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("atan2(yO-yA,xA-xO)");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioAtan2OA));
         jFormattedTextFieldIntValue.selectAll();
    }//GEN-LAST:event_jMenuItemExplementAtanOARadiusActionPerformed

    private void jMenuItemExplementAtanOBRadiusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExplementAtanOBRadiusActionPerformed
        optionDialog = "raggioAtanOB";
        jDialogIntValue.setVisible(true);
        jDialogIntValue.setTitle(java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.setRadius.title"));
        jDialogIntValue.setLocationRelativeTo(rootPane);
        jLabelIntValue.setText("atan2(yO-yB,xB-xO)");
        jFormattedTextFieldIntValue.setText(Integer.toString(raggioAtan2OB));
        jFormattedTextFieldIntValue.selectAll();
    }//GEN-LAST:event_jMenuItemExplementAtanOBRadiusActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        Color color = jColorChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.jColorChooser1.dialogTitle"), coloreManiglie);
        if (color != null) {
            coloreManiglie = color;
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jCheckBoxMenuItemSegment01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSegment01ActionPerformed

    }//GEN-LAST:event_jCheckBoxMenuItemSegment01ActionPerformed

    private Color riempimento(Color c) {
        int R = c.getRed();
        int G = c.getGreen();
        int B = c.getGreen();
        int alpha = c.getAlpha();
        alpha = alpha - 220;
        if (alpha >= 35) {
            alpha = alpha;
        } else {
            alpha = 35;
        }
        return new Color(R, G, B, alpha);
    }

    private void savePNG() {
        try {
            java.awt.Component component = (Component) rootPane.getContentPane();
            java.awt.Rectangle rect = component.getBounds();
            java.awt.Point p = component.getLocationOnScreen();
            java.awt.Dimension d = rect.getSize();

            java.awt.Robot robot = new Robot();

            java.awt.image.BufferedImage windowCapture = robot.createScreenCapture(new java.awt.Rectangle(p, d));

            
            //Save Image
            int returnValue = jFileChooser1.showDialog(rootPane, java.util.ResourceBundle.getBundle("karl/j/weinert/OnScreenProtractor/Bundle").getString("OnScreenProtractor.menuItemSave.text"));
            // File chooser approve button
            if (returnValue == jFileChooser1.APPROVE_OPTION) {
                java.io.File file = jFileChooser1.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".png")) {
                    filePath = filePath;
                } else {
                    filePath = filePath + ".png";
                }

                try {
                    javax.imageio.ImageIO.write(windowCapture, "png", new java.io.File(filePath));
                    System.out.println(filePath);
                } catch (IOException ex) {
                    System.err.println(ex);
                }

            }
        } catch (AWTException exception) {
            System.err.println(exception);
            System.out.println("Error: Could not save image");
        }
    }

    private void ruotaAssi(double rotation) {
        double d = v1.distance(v0);
        double incrementoX, incrementoY;
        incrementoX = (d * Math.sin((Math.toRadians(angolo01) + rotation)));
        v1.x = v0.x + (int) Math.round(incrementoX);
        incrementoY = (d * Math.cos((Math.toRadians(angolo01) + rotation)));
        v1.y = v0.y - (int) Math.round(incrementoY);

    }

    private void incrementoSpessore(char c) {
        if (c == '+') {
            if (spessoreLinee >= massimoSpessoreLinee) {
                spessoreLinee = massimoSpessoreLinee;
            } else {
                spessoreLinee++;
            }
        }

        if (c == '-') {
            if (spessoreLinee <= 1) {
                spessoreLinee = 1;
            } else {
                spessoreLinee--;
            }
        }
    }

    private void setMaxThickness() {

        massimoSpessoreLinee = Integer.parseInt(jFormattedTextFieldMaxThickness.getText());
        jDialogMaxThickness.dispose();
        if (massimoSpessoreLinee < spessoreLinee) {
            spessoreLinee = massimoSpessoreLinee;
        }
        if (massimoSpessoreLinee <= 0) {
            massimoSpessoreLinee = 1;
            spessoreLinee = 1;
            jFormattedTextFieldMaxThickness.setText("1");
        }

    }

    private void incrementoFontSize(char c) {
        if (c == '+') {
            if (fontSize >= maxFontSize) {
                fontSize = maxFontSize;
            } else {
                fontSize++;
            }
        }

        if (c == '-') {
            if (fontSize <= minFontSize) {
                fontSize = minFontSize;
            } else {
                fontSize--;
            }
        }
    }

    private void setMaxFontSize() {

        maxFontSize = Integer.parseInt(jFormattedTextFieldMaxFontSize.getText());
        jDialogMaxFontSize.dispose();
        if (maxFontSize < fontSize) {
            fontSize = maxFontSize;
        }
        if (maxFontSize <= 1) {
            maxFontSize = 1;
            fontSize = 1;
            jFormattedTextFieldMaxFontSize.setText("1");
        }

    }

    private void oKOptionDialog(String idDialog, int value) {
        if (idDialog.equals("raggioAOB")) {
            raggioAngoloAOB = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioAngoloAOB <= 1) {
                raggioAngoloAOB = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
        if (idDialog.equals("raggioExpAOB")) {
            raggioExplementAOB = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioExplementAOB <= 1) {
                raggioExplementAOB = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
        if (idDialog.equals("raggioNOA")) {
            raggioAngoloNOA = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioAngoloNOA <= 1) {
                raggioAngoloNOA = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
        if (idDialog.equals("raggioNOB")) {
            raggioAngoloNOB = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioAngoloNOB <= 1) {
                raggioAngoloNOB = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
        if (idDialog.equals("raggioAtanOA")) {
            raggioAtan2OA = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioAtan2OA <= 1) {
                raggioAtan2OA = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
        if (idDialog.equals("raggioAtanOB")) {
            raggioAtan2OB = Integer.parseInt(jFormattedTextFieldIntValue.getText());
            jDialogIntValue.dispose();
            if (raggioAtan2OB <= 1) {
                raggioAtan2OB = 1;
                jFormattedTextFieldIntValue.setText("1");
            }
        }
    }

    private void disegnaAssi(Graphics2D g) {
        g.setColor(coloreAssi);
        g.drawLine(v0.x, v0.y,
                v0.x + (int) (minAxesLenght * Math.sin(Math.toRadians(angolo01))),
                v0.y - (int) (minAxesLenght * Math.cos(Math.toRadians(angolo01)))); // y+
        g.drawLine(
                v0.x,
                v0.y,
                v0.x
                + (int) (minAxesLenght * (Math.sin(Math
                        .toRadians(angolo01) + (Math.PI)))),
                v0.y
                - (int) (minAxesLenght * (Math.cos(Math
                        .toRadians(angolo01) + (Math.PI))))); // y-
        g.drawLine(
                v0.x,
                v0.y,
                v0.x
                + (int) (minAxesLenght * (Math.sin(Math
                        .toRadians(angolo01) + (Math.PI / 2)))),
                v0.y
                - (int) (minAxesLenght * (Math.cos(Math
                        .toRadians(angolo01) + (Math.PI / 2))))); // x+
        g.drawLine(
                v0.x,
                v0.y,
                v0.x
                + (int) (minAxesLenght * (Math.sin(Math
                        .toRadians(angolo01) + 3 * (Math.PI / 2)))),
                v0.y
                - (int) (minAxesLenght * (Math.cos(Math
                        .toRadians(angolo01) + 3 * (Math.PI / 2))))); // x-
    }

    private void disegnaAngolo01(Graphics2D g) {
        g.setColor(coloreAngoloNOA);
        g.fillArc(v0.x - raggioAngoloNOA, v0.y - raggioAngoloNOA, (raggioAngoloNOA * 2), (raggioAngoloNOA * 2),
                (int) Math.toDegrees(correzione), (int) -angolo01);
        String str = "NOA = " + String.format("%.2f 째", angolo01);
        if (radians) {
            str = "NOA = "
                    + String.format("%.2f", Math.toRadians(angolo01));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioAngoloNOA + 20) * Math.sin(Math
                            .toRadians(angolo01 / 2))),
                    v0.y
                    - (int) ((raggioAngoloNOA + 20) * Math.cos(Math
                            .toRadians(angolo01 / 2)))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());

        }
        g.setColor(coloreAngoloNOA);
        g.drawString(
                str,
                v0.x + (int) ((raggioAngoloNOA + 20) * Math.sin(Math.toRadians(angolo01 / 2))),
                v0.y - (int) ((raggioAngoloNOA + 20) * Math.cos(Math.toRadians(angolo01 / 2))));
    }

    private void disegnaAngolo02(Graphics2D g) {
        g.setColor(coloreAngoloNOB);
        g.fillArc(v0.x - raggioAngoloNOB, v0.y - raggioAngoloNOB, (raggioAngoloNOB * 2), (raggioAngoloNOB * 2),
                (int) Math.toDegrees(correzione), (int) -angolo02);
        String str = "NOB = " + String.format("%.2f 째", angolo02);
        if (radians) {
            str = "NOB = " + String.format("%.2f", Math.toRadians(angolo02));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioAngoloNOB + 20) * Math.sin(Math
                            .toRadians(angolo02 / 2))),
                    v0.y
                    - (int) ((raggioAngoloNOB + 20) * Math.cos(Math
                            .toRadians(angolo02 / 2)))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());

        }
        g.setColor(coloreAngoloNOB);
        g.drawString(
                str,
                v0.x + (int) ((raggioAngoloNOB + 20) * Math.sin(Math.toRadians(angolo02 / 2))),
                v0.y - (int) ((raggioAngoloNOB + 20) * Math.cos(Math.toRadians(angolo02 / 2))));
    }

    private void disegnaAngolo102(Graphics2D g) {
        g.setColor(coloreAngoloAOB);
        g.drawArc(v0.x - raggioAngoloAOB, v0.y - raggioAngoloAOB, (raggioAngoloAOB * 2), (raggioAngoloAOB * 2),
                (int) -(angolo01 - Math.toDegrees(correzione)),
                (int) -angolo102);
        g.setColor(riempimento(coloreAngoloAOB));
        g.fillArc(v0.x - raggioAngoloAOB, v0.y - raggioAngoloAOB, (raggioAngoloAOB * 2), (raggioAngoloAOB * 2),
                (int) -(angolo01 - Math.toDegrees(correzione)),
                (int) -angolo102);

        String str = "AOB = " + String.format("%.2f �", angolo102);
        if (radians) {
            str = "AOB = " + String.format("%.2f", Math.toRadians(angolo102));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioAngoloAOB + 20) * Math.sin(Math.toRadians(angolo01
                                    + (angolo102 / 2)))),
                    v0.y
                    - (int) ((raggioAngoloAOB + 20) * Math.cos(Math.toRadians(angolo01
                                    + (angolo102 / 2))))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());

        }

        g.setColor(coloreAngoloAOB);
        g.drawString(
                str,
                v0.x
                + (int) ((raggioAngoloAOB + 20) * Math.sin(Math.toRadians(angolo01
                                + (angolo102 / 2)))),
                v0.y
                - (int) ((raggioAngoloAOB + 20) * Math.cos(Math.toRadians(angolo01
                                + (angolo102 / 2)))));
    }

    private void disegnaExplement102(Graphics2D g) {
        g.setColor(coloreExplementAOB);
        g.drawArc(v0.x - raggioExplementAOB, v0.y - raggioExplementAOB, (raggioExplementAOB * 2), (raggioExplementAOB * 2),
                (int) -(angolo01 - Math.toDegrees(correzione)),
                (int) (-angolo102 + Math.toDegrees(2 * Math.PI)));
        g.setColor(riempimento(coloreExplementAOB));
        g.fillArc(v0.x - raggioExplementAOB, v0.y - raggioExplementAOB, (raggioExplementAOB * 2), (raggioExplementAOB * 2),
                (int) -(angolo01 - Math.toDegrees(correzione)),
                (int) (-angolo102 + Math.toDegrees(2 * Math.PI)));

        String str = "360째-AOB = "
                + String.format("%.2f 째", 360 - angolo102);
        if (radians) {
            str = "2PI-AOB = "
                    + String.format("%.2f", Math.toRadians(360 - angolo102));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioExplementAOB + 30) * Math.sin(Math.toRadians(angolo01
                                    + (angolo102 / 2))
                            + Math.PI)),
                    v0.y
                    - (int) ((raggioExplementAOB + 30) * Math.cos(Math.toRadians(angolo01
                                    + (angolo102 / 2))
                            + Math.PI))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());

        }
        g.setColor(coloreExplementAOB);
        g.drawString(
                str,
                v0.x
                + (int) ((raggioExplementAOB + 30) * Math.sin(Math.toRadians(angolo01
                                + (angolo102 / 2))
                        + Math.PI)),
                v0.y
                - (int) ((raggioExplementAOB + 30) * Math.cos(Math.toRadians(angolo01
                                + (angolo102 / 2))
                        + Math.PI)));
    }

    private void disegnaAtan01(Graphics2D g) {
        g.setColor(coloreAtan2OA);
        g.fillArc(v0.x - raggioAtan2OA, v0.y - raggioAtan2OA, (raggioAtan2OA * 2), (raggioAtan2OA * 2),
                0, (int) angoloAtan01);
        String str = "atan OA = " + String.format("%.2f 째", angoloAtan01);
        if (radians) {
            str = "atan OA = "
                    + String.format("%.2f", Math.toRadians(angoloAtan01));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioAtan2OA + 20) * Math.sin(Math
                            .toRadians(angoloAtan01 / 2))),
                    v0.y
                    - (int) ((raggioAtan2OA + 20) * Math.cos(Math
                            .toRadians(angoloAtan01 / 2)))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());
        }
        g.setColor(coloreAtan2OA);
        g.drawString(
                str,
                v0.x + (int) ((raggioAtan2OA + 20) * Math.sin(Math.toRadians(angoloAtan01 / 2))),
                v0.y - (int) ((raggioAtan2OA + 20) * Math.cos(Math.toRadians(angoloAtan01 / 2))));

    }

    private void disegnaAtan02(Graphics2D g) {
        g.setColor(coloreAtan2OB);
        g.fillArc(v0.x - raggioAtan2OB, v0.y - raggioAtan2OB, (raggioAtan2OB * 2), (raggioAtan2OB * 2),
                0, (int) angoloAtan02);
        String str = "atan OA = " + String.format("%.2f 째", angoloAtan02);
        if (radians) {
            str = "atan OA = "
                    + String.format("%.2f", Math.toRadians(angoloAtan02));
        }
        if (textBackgroundVisibility) {
            g.setColor(coloreSfondoTesto);
            g.fillRect(
                    v0.x
                    + (int) ((raggioAtan2OB + 20) * Math.sin(Math
                            .toRadians(angoloAtan02 / 2))),
                    v0.y
                    - (int) ((raggioAtan2OB + 20) * Math.cos(Math
                            .toRadians(angoloAtan02 / 2)))
                    - g.getFontMetrics().getAscent(), (int) g
                    .getFontMetrics().getStringBounds(str, g)
                    .getWidth(), (int) g.getFontMetrics()
                    .getStringBounds(str, g).getHeight());

        }
        g.setColor(coloreAtan2OB);
        g.drawString(
                str,
                v0.x + (int) ((raggioAtan2OB + 20) * Math.sin(Math.toRadians(angoloAtan02 / 2))),
                v0.y - (int) ((raggioAtan2OB + 20) * Math.cos(Math.toRadians(angoloAtan02 / 2))));

    }

    public void mousePressed(MouseEvent e) {
        // Click on mouse left button
        if (e.getButton() == MouseEvent.BUTTON1) {
            
            if (rect0.contains(e.getX(), e.getY())) {
                // maniglia su cui si clicca
                pressedPoint = 0;
                mousePressedLocation.x = (rect0.x + deltaRectWidth) - e.getX();
                mousePressedLocation.y = (rect0.y + deltaRectHeight) - e.getY();
                pressOut = false;
                v0.x = mousePressedLocation.x + e.getX();
                v0.y = mousePressedLocation.y + e.getY();

            } else if (rect1.contains(e.getX(), e.getY())) {

                pressedPoint = 1;
                mousePressedLocation.x = (rect1.x + deltaRectWidth) - e.getX();
                mousePressedLocation.y = (rect1.y + deltaRectHeight) - e.getY();
                pressOut = false;
                v1.x = mousePressedLocation.x + e.getX();
                v1.y = mousePressedLocation.y + e.getY();

            } else if (rect2.contains(e.getX(), e.getY())) {

                pressedPoint = 2;
                mousePressedLocation.x = (rect2.x + deltaRectWidth) - e.getX();
                mousePressedLocation.y = (rect2.y + deltaRectHeight) - e.getY();
                pressOut = false;
                v2.x = mousePressedLocation.x + e.getX();
                v2.y = mousePressedLocation.y + e.getY();

            } else {
                pressOut = true;
                pressedPoint = 4;

            }
            repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // Click on mouse right button
            v0.x = ((getWidth() / 2));
            v0.y = ((getHeight() / 2));
            rect0.setLocation(v0.x - deltaRectWidth, v0.y - deltaRectHeight);
            correzione = Math.PI / 2;
            v1.x = v0.x;
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            correzione = Math.PI / 2;
            v1.x = v0.x;
        }

        rect0.setLocation(v0.x - deltaRectWidth, v0.y - deltaRectHeight);
        rect1.setLocation(v1.x - deltaRectWidth, v1.y - deltaRectHeight);
        rect2.setLocation(v2.x - deltaRectWidth, v2.y - deltaRectHeight);
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        // Cursor appearance
        if (rect0.contains(mouseX, mouseY) || rect1.contains(mouseX, mouseY)
                || rect2.contains(mouseX, mouseY)) {
            setCursor(hand);
        } else {
            setCursor(arrow);
        }
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        if (!pressOut) {
            mouseX = e.getX();
            mouseY = e.getY();

            // dragging of handles
            if (pressedPoint == 0) {
                v0.x = mousePressedLocation.x + mouseX;

                if (v0.x <= 0) {
                    v0.x = 0;
                }
                if (v0.x >= getWidth() - rectWidth) {
                    v0.x = getWidth() - rectWidth;
                }
                v0.y = mousePressedLocation.y + mouseY;

                if (v0.y <= 0) {
                    v0.y = 0;
                }
                if (v0.y >= getHeight() - rectHeight - 45) {
                    v0.y = getHeight() - rectHeight - 45;
                }
            } else if (pressedPoint == 1) {
                v1.x = mousePressedLocation.x + mouseX;
                if (v1.x <= 0) {
                    v1.x = 0;
                }
                if (v1.x >= getWidth() - rectWidth) {
                    v1.x = getWidth() - rectWidth;
                }
                v1.y = mousePressedLocation.y + mouseY;
                if (v1.y <= 0) {
                    v1.y = 0;
                }
                if (v1.y >= getHeight() - rectHeight - 45) {
                    v1.y = getHeight() - rectHeight - 45;
                }
            } else if (pressedPoint == 2) {
                v2.x = mousePressedLocation.x + mouseX;
                if (v2.x <= 0) {
                    v2.x = 0;
                }
                if (v2.x >= getWidth() - rectWidth) {
                    v2.x = getWidth() - rectWidth;
                }
                v2.y = mousePressedLocation.y + mouseY;
                if (v2.y <= 0) {
                    v2.y = 0;
                }
                if (v2.y >= getHeight() - rectHeight - 45) {
                    v2.y = getHeight() - rectHeight - 45;
                }
            } else {
                pressedPoint = 4;
            }

            rect0.setLocation(v0.x - deltaRectWidth, v0.y - deltaRectHeight);
            rect1.setLocation(v1.x - deltaRectWidth, v1.y - deltaRectHeight);
            rect2.setLocation(v2.x - deltaRectWidth, v2.y - deltaRectHeight);
            repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // axes rotation

        int tacche = e.getWheelRotation();
        double rotation = Math.PI / 256;

        if (tacche < 0) {

            ruotaAssi(-rotation);
        } else {
            ruotaAssi(rotation);
        }

        repaint();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge
                = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isPerPixelTranslucencySupported
                = gd.isWindowTranslucencySupported(PERPIXEL_TRANSLUCENT);

        //If translucent windows aren't supported, exit.
        if (!isPerPixelTranslucencySupported) {
            System.out.println(
                    "Per-pixel translucency is not supported");
            System.exit(0);
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create the GUI on the event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OnScreenProtractor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonCancel1;
    private javax.swing.JButton jButtonCancelIntValue;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonOK1;
    private javax.swing.JButton jButtonOKIntValue;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAngoloAOB;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAngoloNOA;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAngoloNOB;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAtan01;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAtan02;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemAxes;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemExplement102;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemHandles;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemHideAll;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemLetters;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemMouseCoords;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemRadians;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemSegment01;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemSegment02;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemTextBackground;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JDialog jDialogIntValue;
    private javax.swing.JDialog jDialogMaxFontSize;
    private javax.swing.JDialog jDialogMaxThickness;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFormattedTextField jFormattedTextFieldIntValue;
    private javax.swing.JFormattedTextField jFormattedTextFieldMaxFontSize;
    private javax.swing.JFormattedTextField jFormattedTextFieldMaxThickness;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelIntValue;
    private javax.swing.JMenu jMenuAnglesColor;
    private javax.swing.JMenu jMenuFontSize;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItemAOBRadius;
    private javax.swing.JMenuItem jMenuItemAngleAOBColor;
    private javax.swing.JMenuItem jMenuItemAngleExplementAOBColor;
    private javax.swing.JMenuItem jMenuItemAtan2OAColor;
    private javax.swing.JMenuItem jMenuItemAtan2OBColor;
    private javax.swing.JMenuItem jMenuItemDecreaseFontSize;
    private javax.swing.JMenuItem jMenuItemExplementAOBRadius;
    private javax.swing.JMenuItem jMenuItemExplementAtanOARadius;
    private javax.swing.JMenuItem jMenuItemExplementAtanOBRadius;
    private javax.swing.JMenuItem jMenuItemExplementNOARadius;
    private javax.swing.JMenuItem jMenuItemExplementNOBRadius;
    private javax.swing.JMenuItem jMenuItemIncreaseFontSize;
    private javax.swing.JMenuItem jMenuItemMaxFontSize;
    private javax.swing.JMenuItem jMenuItemMaxThickness;
    private javax.swing.JMenuItem jMenuItemNOAColor;
    private javax.swing.JMenuItem jMenuItemNOBColor;
    private javax.swing.JMenuItem jMenuItemTextBackgroundColor;
    private javax.swing.JMenuItem jMenuItemTextColor;
    private javax.swing.JMenu jMenuRadius;
    private javax.swing.JOptionPane jOptionPaneAbout;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCapture;
    private javax.swing.JMenu menuColors;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAxesColor;
    private javax.swing.JMenuItem menuItemDecrease;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemIncrease;
    private javax.swing.JMenuItem menuItemSave;
    private javax.swing.JMenu menuLinesThickness;
    private javax.swing.JMenu menuOptions;
    private javax.swing.JMenu menuView;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Keyboard
    @Override
    public void keyPressed(KeyEvent e) {
        int tasto = e.getKeyCode();

        if (tasto == KeyEvent.VK_RIGHT) {
            double rotation = Math.PI / 256;
            ruotaAssi(rotation);
        }

        if (tasto == KeyEvent.VK_LEFT) {
            double rotation = Math.PI / 256;
            ruotaAssi(-rotation);
        }

        if (tasto == KeyEvent.VK_UP) {
            correzione = Math.PI / 2;
            v1.x = v0.x;
        }

        if (tasto == KeyEvent.VK_PLUS || tasto == KeyEvent.VK_ADD) {
            incrementoSpessore('+');
            incrementoFontSize('+');
        }

        if (tasto == KeyEvent.VK_MINUS || tasto == KeyEvent.VK_SUBTRACT) {
            incrementoSpessore('-');
            incrementoFontSize('-');
        }

        if ((tasto == KeyEvent.VK_O && !e.isControlDown()) ||
            tasto == KeyEvent.VK_0 ||
            tasto == KeyEvent.VK_NUMPAD0) {
            v0.x = mouseX;
            v0.y = mouseY;
        }

        if ((tasto == KeyEvent.VK_A && !e.isControlDown()) ||
            tasto == KeyEvent.VK_1 || 
            tasto == KeyEvent.VK_NUMPAD1) {
            v1.x = mouseX;
            v1.y = mouseY;
        }

        if ((tasto == KeyEvent.VK_B && !e.isControlDown()) ||
            tasto == KeyEvent.VK_2 ||
            tasto == KeyEvent.VK_NUMPAD2) {
            v2.x = mouseX;
            v2.y = mouseY;
        }

        rect0.setLocation(v0.x - deltaRectWidth, v0.y - deltaRectHeight);
        rect1.setLocation(v1.x - deltaRectWidth, v1.y - deltaRectHeight);
        rect2.setLocation(v2.x - deltaRectWidth, v2.y - deltaRectHeight);

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}