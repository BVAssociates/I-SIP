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

/**
 *
 * @author BAUCHART
 */
public class IsipPanel extends javax.swing.JPanel {

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

        jLabelTable = new javax.swing.JLabel();
        jLabelDateHisto = new javax.swing.JLabel();
        jTextDateHisto = new javax.swing.JTextField();
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

        setLayout(new java.awt.GridBagLayout());

        jLabelTable.setText("Table en cours");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelTable, gridBagConstraints);

        jLabelDateHisto.setText("Date Collecte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelDateHisto, gridBagConstraints);

        jTextDateHisto.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        add(jTextDateHisto, gridBagConstraints);

        jLabelDateUpdate.setText("Date commentaire");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelDateUpdate, gridBagConstraints);

        jTextDateUpdate.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jTextDateUpdate, gridBagConstraints);

        jLabelUserUpdate.setText("User commentaire");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelUserUpdate, gridBagConstraints);

        jTextUserUpdate.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        add(jTextUserUpdate, gridBagConstraints);

        jLabelStatus.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelStatus, gridBagConstraints);

        jLabelComment.setText("Commentaire");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelComment, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jTextComment, gridBagConstraints);

        jTextTable.setText("###");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
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
        add(jTextTableKey, gridBagConstraints);

        jComboBoxStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBoxStatus, gridBagConstraints);

        jLabelEnvironnement.setText("Environnement");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(jLabelEnvironnement, gridBagConstraints);

        jTextEnvironnement.setText("###");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(jTextEnvironnement, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBoxStatus;
    private javax.swing.JLabel jLabelComment;
    private javax.swing.JLabel jLabelDateHisto;
    private javax.swing.JLabel jLabelDateUpdate;
    private javax.swing.JLabel jLabelEnvironnement;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTable;
    private javax.swing.JLabel jLabelTableKey;
    private javax.swing.JLabel jLabelUserUpdate;
    private javax.swing.JTextField jTextComment;
    private javax.swing.JTextField jTextDateHisto;
    private javax.swing.JTextField jTextDateUpdate;
    private javax.swing.JLabel jTextEnvironnement;
    private javax.swing.JLabel jTextTable;
    private javax.swing.JLabel jTextTableKey;
    private javax.swing.JTextField jTextUserUpdate;
    // End of variables declaration//GEN-END:variables

}
