/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IsipPanel.java
 *
 * Created on 10 d�c. 2008, 11:51:54
 */

package com.bv.isip;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import javax.swing.SwingUtilities;

/**
 *
 * @author BAUCHART
 */
public class IsipPanel extends javax.swing.JPanel
{

    /** Creates new form IsipPanel */
    public IsipPanel() {
        initComponents();
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPopupMenu1 = new javax.swing.JPopupMenu();
        test2 = new javax.swing.JMenuItem();
        test = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jLabelTable = new javax.swing.JLabel();
        jLabelDateUpdate = new javax.swing.JLabel();
        jTextDateUpdate = new javax.swing.JTextField();
        jLabelUserUpdate = new javax.swing.JLabel();
        jTextUserUpdate = new javax.swing.JTextField();
        jLabelStatus = new javax.swing.JLabel();
        jLabelComment = new javax.swing.JLabel();
        jTextComment = new javax.swing.JTextField();
        jTextTable = new javax.swing.JLabel();
        jLabelTableKey = new javax.swing.JLabel();
        jTextTableKey = new javax.swing.JLabel();
        jComboBoxStatus = new javax.swing.JComboBox();
        jLabelEnvironnement = new javax.swing.JLabel();
        jTextEnvironnement = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabelDateExplore = new javax.swing.JLabel();
        jTextDateExplore = new javax.swing.JLabel();

        test2.setText("Afficher");
        jPopupMenu1.add(test2);

        test.setText("Modifier");
        jPopupMenu1.add(test);

        jMenuItem1.setText("Historique");
        jPopupMenu1.add(jMenuItem1);

        setLayout(new java.awt.GridBagLayout());

        jLabelTable.setText("Table en cours");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelTable, gridBagConstraints);

        jLabelDateUpdate.setText("Commentaire modifi� le");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelDateUpdate, gridBagConstraints);

        jTextDateUpdate.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextDateUpdate, gridBagConstraints);

        jLabelUserUpdate.setText("par");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelUserUpdate, gridBagConstraints);

        jTextUserUpdate.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextUserUpdate, gridBagConstraints);

        jLabelStatus.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelStatus, gridBagConstraints);

        jLabelComment.setText("Commentaire");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelComment, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextComment, gridBagConstraints);

        jTextTable.setText("###");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextTable, gridBagConstraints);

        jLabelTableKey.setText("Ligne en cours");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelTableKey, gridBagConstraints);

        jTextTableKey.setText("###");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextTableKey, gridBagConstraints);

        jComboBoxStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jComboBoxStatus, gridBagConstraints);

        jLabelEnvironnement.setText("Environnement");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelEnvironnement, gridBagConstraints);

        jTextEnvironnement.setText("###");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextEnvironnement, gridBagConstraints);

        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 200));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nom", "Libell�", "Valeur", "Commentaire", "Status", "Derni�re Collecte"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            @Override
			public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setAutoscrolls(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            @Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jLabelDateExplore.setText("Date Exploration");
        jLabelDateExplore.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelDateExplore, gridBagConstraints);

        jTextDateExplore.setText("derni�re");
        jTextDateExplore.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jTextDateExplore, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTable1MouseClicked
    {//GEN-HEADEREND:event_jTable1MouseClicked
        if (SwingUtilities.isRightMouseButton(evt)) {
            int row = jTable1.rowAtPoint(new Point(evt.getX(), evt.getY()));
            int col = jTable1.columnAtPoint(new Point(evt.getX(), evt.getY()));
            jTable1.changeSelection(row,col, false, false);
            jPopupMenu1.show(jTable1, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTable1MousePressed
    {//GEN-HEADEREND:event_jTable1MousePressed

        // S'il s'agit d'un clic droit, on simule un clic gauche
        // pour s�lectionner l'�l�ment situ� sous la souris
        if (SwingUtilities.isRightMouseButton(evt)) {
            Robot r;
            try {
                r = new Robot();
                //r.mousePress(InputEvent.BUTTON1_MASK);
                //r.mouseRelease(InputEvent.BUTTON1_MASK);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }

    }//GEN-LAST:event_jTable1MousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxStatus;
    private javax.swing.JLabel jLabelComment;
    private javax.swing.JLabel jLabelDateExplore;
    private javax.swing.JLabel jLabelDateUpdate;
    private javax.swing.JLabel jLabelEnvironnement;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTable;
    private javax.swing.JLabel jLabelTableKey;
    private javax.swing.JLabel jLabelUserUpdate;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextComment;
    private javax.swing.JLabel jTextDateExplore;
    private javax.swing.JTextField jTextDateUpdate;
    private javax.swing.JLabel jTextEnvironnement;
    private javax.swing.JLabel jTextTable;
    private javax.swing.JLabel jTextTableKey;
    private javax.swing.JTextField jTextUserUpdate;
    private javax.swing.JMenuItem test;
    private javax.swing.JMenuItem test2;
    // End of variables declaration//GEN-END:variables

}
