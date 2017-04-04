package View;

import Helper.Koneksi;
import Model.Pemain;
import Model.Pertanyaan;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
import javax.swing.Timer;

/**
 *
 * @author Ahmad
 */
public class PlayPage extends javax.swing.JFrame {

    Pertanyaan pertanyaan = new Pertanyaan();
    List idPertanyaan = new ArrayList();
    List idAcak = new ArrayList();
    Timer pewaktu;
    int nyawa = 100;
    int nilaiPemain = 0;
    boolean sudahMenjawab = false;
    boolean selesai = false;

    public PlayPage() {
        initComponents();
        this.setTitle("Main");

        try {
            java.sql.Connection conn = (java.sql.Connection) Koneksi.GetConnection();
            java.sql.Statement stat = conn.createStatement();
            java.sql.ResultSet result = stat.executeQuery("SELECT IDSoal FROM tbSoal GROUP BY IDSoal ASC");

            while (result.next()) {
                idPertanyaan.add(result.getInt("IDSoal"));
            }
            pertanyaan.setJumlahIdPertanyaan(idPertanyaan.size());
        } catch (SQLException ex) {
            ta_Pertanyaan.setText("" + ex);
            ta_Pertanyaan.setForeground(Color.red);
        }

        pertanyaan.setKalkulasiPertanyaan(0);
        kalkulasiPertanyaan();
    }

    public void kalkulasiPertanyaan() {
        if (pertanyaan.getKalkulasiPertanyaan() < 10) {
            btn_Selanjutnya.setEnabled(false);
            aturPertanyaan();
        } else {
            ta_Pertanyaan.setText("SELAMAT! \nAnda berhasil menjawab " + nilaiPemain + " pertanyaan");
            ta_Pertanyaan.setFont(new java.awt.Font("Candara", 1, 24));
            selesai = true;
            
            Pemain.setNilaiAkhir((Pemain.getNilaiAkhir() + nilaiPemain));
            Pemain.setTotalMain((Pemain.getTotalMain() + 1));

            try {
                java.sql.Connection conn = (java.sql.Connection) Koneksi.GetConnection();
                java.sql.Statement stat = conn.createStatement();
                stat.executeUpdate(
                        "UPDATE tbAkun SET TotalMain='" + Pemain.getTotalMain() + "', NilaiAkhir='" 
                                + Pemain.getNilaiAkhir() + "' WHERE NamaAkun='" + Pemain.getNamaAkun() + "'"
                );
            } catch (SQLException ex) {
                System.out.println(ex);
                ta_Pertanyaan.setText("" + ex);
                ta_Pertanyaan.setForeground(Color.red);
            }
        }
    }

    public void aturPertanyaan() {
        nyawa = 100;
        sudahMenjawab = false;
        int acak = (int) (Math.random() * idPertanyaan.size());

        for (int x = 0; x < idAcak.size(); x++) {
            if ((int) idAcak.get(x) == acak) {
                acak = (int) (Math.random() * idPertanyaan.size());
                x = 0;
            }
        }
        idAcak.add(acak);

        try {
            java.sql.Connection conn = (java.sql.Connection) Koneksi.GetConnection();
            java.sql.Statement stat = conn.createStatement();
            java.sql.ResultSet result = stat.executeQuery(
                    "SELECT * FROM tbSoal WHERE IDSoal='" + idPertanyaan.get(acak) + "'"
            );
            if (result.next()) {
                pertanyaan.setMatkul(result.getString("Matkul"));
                pertanyaan.setPertanyaan(result.getString("Pertanyaan"));
                pertanyaan.setJawaban(result.getString("Jawaban"));
                pertanyaan.setFyi(result.getString("FYI"));
                pertanyaan.setPilihan1(result.getString("PilA"));
                pertanyaan.setPilihan2(result.getString("PilB"));
                pertanyaan.setPilihan3(result.getString("PilC"));
                pertanyaan.setPilihan4(result.getString("PilD"));
            }
            
            java.sql.ResultSet result2 = stat.executeQuery(
                    "SELECT NamaMatkul FROM tbMatkul WHERE IDMatkul='" + pertanyaan.getMatkul() + "'"
            );
            if (result2.next()) {
                pertanyaan.setMatkul(result2.getString("NamaMatkul"));
            }
            ta_Pertanyaan.setForeground(Color.black);
        } catch (SQLException ex) {
            ta_Pertanyaan.setText("" + ex);
            ta_Pertanyaan.setForeground(Color.red);
        }
        pertanyaan.setKalkulasiPertanyaan((pertanyaan.getKalkulasiPertanyaan() + 1));

        lbl_Judul.setText("Pertanyaan ke-" + pertanyaan.getKalkulasiPertanyaan());
        lbl_Matkul.setText("Mata kuliah: " + pertanyaan.getMatkul());
        ta_Pertanyaan.setText("Pertanyaan:\n" + pertanyaan.getPertanyaan());
        ta_Pertanyaan.setFont(new java.awt.Font("Candara", 1, 18));
        lbl_StatusJawaban.setText("");
        lbl_StatusJawaban.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 0));

        btn_Pilihan1.setText("[A] " + pertanyaan.getPilihan1());
        btn_Pilihan2.setText("[B] " + pertanyaan.getPilihan2());
        btn_Pilihan3.setText("[C] " + pertanyaan.getPilihan3());
        btn_Pilihan4.setText("[D] " + pertanyaan.getPilihan4());
        btn_Pilihan1.setEnabled(true);
        btn_Pilihan2.setEnabled(true);
        btn_Pilihan3.setEnabled(true);
        btn_Pilihan4.setEnabled(true);
        
        // memulai waktu untuk menghitung mundur nyawa
        pewaktu = new Timer(100, Waktu);
        pewaktu.start();
    }

    ActionListener Waktu = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (sudahMenjawab) {
                // jika pemain sudah menjawab sebelum nyawa habis
                pewaktu.stop();
            } else if (nyawa >= 0) {
                // mengurangi nyawa secara berulang
                prg_Waktu.setValue(nyawa);
                System.out.println(nyawa);
                nyawa = nyawa - 1;
            } else {
                // jika waktu habis
                pewaktu.stop();
                cekJawaban("X");
            }
        }
    };

    void cekJawaban(String jawabanPemain) {
        Toolkit.getDefaultToolkit().beep();
        sudahMenjawab = true;

        if (jawabanPemain.equals(pertanyaan.getJawaban())) {
            lbl_StatusJawaban.setText("BENAR!");
            lbl_StatusJawaban.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0), 2));
            nilaiPemain++;
        } else if (jawabanPemain.equals("X")) {
            lbl_StatusJawaban.setText("WAKTU HABIS!");
            lbl_StatusJawaban.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0), 2));
        } else {
            lbl_StatusJawaban.setText("SALAH!");
            lbl_StatusJawaban.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0), 2));
        }
        btn_Selanjutnya.setEnabled(true);
        btn_Selanjutnya.requestFocus();

        ta_Pertanyaan.setText("Pertanyaan:\n" + pertanyaan.getPertanyaan() + "\n\nUntuk Anda ketahui:\n" + pertanyaan.getFyi());
        ta_Pertanyaan.setFont(new java.awt.Font("Candara", 0, 18));

        btn_Pilihan1.setEnabled(false);
        btn_Pilihan2.setEnabled(false);
        btn_Pilihan3.setEnabled(false);
        btn_Pilihan4.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnl_Judul = new javax.swing.JPanel();
        lbl_Judul = new javax.swing.JLabel();
        lbl_StatusJawaban = new javax.swing.JLabel();
        sp_Pertanyaan = new javax.swing.JScrollPane();
        ta_Pertanyaan = new javax.swing.JTextArea();
        sp_Pilihan1 = new javax.swing.JScrollPane();
        btn_Pilihan1 = new javax.swing.JTextPane();
        sp_Pilihan2 = new javax.swing.JScrollPane();
        btn_Pilihan2 = new javax.swing.JTextPane();
        sp_Pilihan3 = new javax.swing.JScrollPane();
        btn_Pilihan3 = new javax.swing.JTextPane();
        sp_Pilihan4 = new javax.swing.JScrollPane();
        btn_Pilihan4 = new javax.swing.JTextPane();
        lbl_Matkul = new javax.swing.JLabel();
        prg_Waktu = new javax.swing.JProgressBar();
        btn_Selanjutnya = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setMinimumSize(new java.awt.Dimension(600, 400));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 400));

        pnl_Judul.setBackground(new java.awt.Color(0, 102, 255));

        lbl_Judul.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lbl_Judul.setForeground(new java.awt.Color(255, 255, 255));
        lbl_Judul.setText("Pertanyaan ke-n");

        lbl_StatusJawaban.setFont(new java.awt.Font("Candara", 1, 20)); // NOI18N
        lbl_StatusJawaban.setForeground(new java.awt.Color(255, 255, 255));
        lbl_StatusJawaban.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_StatusJawaban.setText("WAKTU HABIS!");
        lbl_StatusJawaban.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        org.jdesktop.layout.GroupLayout pnl_JudulLayout = new org.jdesktop.layout.GroupLayout(pnl_Judul);
        pnl_Judul.setLayout(pnl_JudulLayout);
        pnl_JudulLayout.setHorizontalGroup(
            pnl_JudulLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnl_JudulLayout.createSequentialGroup()
                .addContainerGap()
                .add(lbl_Judul)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(lbl_StatusJawaban, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnl_JudulLayout.setVerticalGroup(
            pnl_JudulLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnl_JudulLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnl_JudulLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lbl_StatusJawaban, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lbl_Judul, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        ta_Pertanyaan.setEditable(false);
        ta_Pertanyaan.setColumns(20);
        ta_Pertanyaan.setFont(new java.awt.Font("Candara", 1, 18)); // NOI18N
        ta_Pertanyaan.setLineWrap(true);
        ta_Pertanyaan.setRows(5);
        ta_Pertanyaan.setText("Pertanyaan");
        ta_Pertanyaan.setWrapStyleWord(true);
        ta_Pertanyaan.setAutoscrolls(false);
        sp_Pertanyaan.setViewportView(ta_Pertanyaan);

        btn_Pilihan1.setEditable(false);
        btn_Pilihan1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btn_Pilihan1.setText("Pilihan A");
        btn_Pilihan1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_Pilihan1.setPreferredSize(new java.awt.Dimension(285, 60));
        btn_Pilihan1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_Pilihan1MouseClicked(evt);
            }
        });
        sp_Pilihan1.setViewportView(btn_Pilihan1);

        btn_Pilihan2.setEditable(false);
        btn_Pilihan2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btn_Pilihan2.setText("Pilihan B");
        btn_Pilihan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_Pilihan2.setPreferredSize(new java.awt.Dimension(285, 60));
        btn_Pilihan2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_Pilihan2MouseClicked(evt);
            }
        });
        sp_Pilihan2.setViewportView(btn_Pilihan2);

        btn_Pilihan3.setEditable(false);
        btn_Pilihan3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btn_Pilihan3.setText("Pilihan C");
        btn_Pilihan3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_Pilihan3.setPreferredSize(new java.awt.Dimension(285, 60));
        btn_Pilihan3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_Pilihan3MouseClicked(evt);
            }
        });
        sp_Pilihan3.setViewportView(btn_Pilihan3);

        btn_Pilihan4.setEditable(false);
        btn_Pilihan4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btn_Pilihan4.setText("Pilihan D");
        btn_Pilihan4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_Pilihan4.setPreferredSize(new java.awt.Dimension(285, 60));
        btn_Pilihan4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_Pilihan4MouseClicked(evt);
            }
        });
        sp_Pilihan4.setViewportView(btn_Pilihan4);

        lbl_Matkul.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lbl_Matkul.setText("Mata kuliah:");

        prg_Waktu.setBackground(new java.awt.Color(255, 0, 0));
        prg_Waktu.setForeground(new java.awt.Color(51, 204, 0));
        prg_Waktu.setValue(40);
        prg_Waktu.setBorder(null);
        prg_Waktu.setBorderPainted(false);

        btn_Selanjutnya.setBackground(new java.awt.Color(0, 102, 255));
        btn_Selanjutnya.setForeground(new java.awt.Color(255, 255, 255));
        btn_Selanjutnya.setText("Selanjutnya >>>");
        btn_Selanjutnya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SelanjutnyaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(sp_Pilihan2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .add(6, 6, 6)
                        .add(sp_Pilihan4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(sp_Pilihan1)
                        .add(6, 6, 6)
                        .add(sp_Pilihan3)))
                .addContainerGap())
            .add(jPanel1Layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(lbl_Matkul, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btn_Selanjutnya)
                .addContainerGap())
            .add(jPanel1Layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(prg_Waktu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(10, 10, 10))
            .add(jPanel1Layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(sp_Pertanyaan)
                .add(10, 10, 10))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(pnl_Judul, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(pnl_Judul, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(prg_Waktu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbl_Matkul, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btn_Selanjutnya))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sp_Pertanyaan, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(sp_Pilihan1)
                    .add(sp_Pilihan3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sp_Pilihan2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sp_Pilihan4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_Pilihan1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_Pilihan1MouseClicked
        if (!sudahMenjawab) cekJawaban("A");
    }//GEN-LAST:event_btn_Pilihan1MouseClicked

    private void btn_Pilihan2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_Pilihan2MouseClicked
        if (!sudahMenjawab) cekJawaban("B");
    }//GEN-LAST:event_btn_Pilihan2MouseClicked

    private void btn_Pilihan3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_Pilihan3MouseClicked
        if (!sudahMenjawab) cekJawaban("C");
    }//GEN-LAST:event_btn_Pilihan3MouseClicked

    private void btn_Pilihan4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_Pilihan4MouseClicked
        if (!sudahMenjawab) cekJawaban("D");
    }//GEN-LAST:event_btn_Pilihan4MouseClicked

    private void btn_SelanjutnyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SelanjutnyaActionPerformed
        if (!selesai) {
            kalkulasiPertanyaan();
        } else {
            new LeaderboardPage().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btn_SelanjutnyaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlayPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane btn_Pilihan1;
    private javax.swing.JTextPane btn_Pilihan2;
    private javax.swing.JTextPane btn_Pilihan3;
    private javax.swing.JTextPane btn_Pilihan4;
    private javax.swing.JButton btn_Selanjutnya;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_Judul;
    private javax.swing.JLabel lbl_Matkul;
    private javax.swing.JLabel lbl_StatusJawaban;
    private javax.swing.JPanel pnl_Judul;
    private javax.swing.JProgressBar prg_Waktu;
    private javax.swing.JScrollPane sp_Pertanyaan;
    private javax.swing.JScrollPane sp_Pilihan1;
    private javax.swing.JScrollPane sp_Pilihan2;
    private javax.swing.JScrollPane sp_Pilihan3;
    private javax.swing.JScrollPane sp_Pilihan4;
    private javax.swing.JTextArea ta_Pertanyaan;
    // End of variables declaration//GEN-END:variables
}
