package Model;

/**
 *
 * @author Ahmad
 */
public class Pertanyaan {
    String matkul;
    String pertanyaan;
    String jawaban;
    String fyi;
    String pilihan1;
    String pilihan2;
    String pilihan3;
    String pilihan4;
    int jumlahIdPertanyaan;
    int kalkulasiPertanyaan;
    
    public String getMatkul() { return matkul; }
    public String getPertanyaan() { return pertanyaan; }
    public String getJawaban() { return jawaban; }
    public String getFyi() { return fyi; }
    public String getPilihan1() { return pilihan1; }
    public String getPilihan2() { return pilihan2; }
    public String getPilihan3() { return pilihan3; }
    public String getPilihan4() { return pilihan4; }
    public int getJumlahIdPertanyaan() { return jumlahIdPertanyaan; }
    public int getKalkulasiPertanyaan() { return kalkulasiPertanyaan; }
    
    public void setMatkul(String matkul) { this.matkul = matkul; }
    public void setPertanyaan(String pertanyaan) { this.pertanyaan = pertanyaan; }
    public void setJawaban(String jawaban) { this.jawaban = jawaban; }
    public void setFyi(String fyi) { this.fyi = fyi; }
    public void setPilihan1(String pilihan1) { this.pilihan1 = pilihan1; }
    public void setPilihan2(String pilihan2) { this.pilihan2 = pilihan2; }
    public void setPilihan3(String pilihan3) { this.pilihan3 = pilihan3; }
    public void setPilihan4(String pilihan4) { this.pilihan4 = pilihan3; }
    public void setJumlahIdPertanyaan(int jumlahIdPertanyaan) { this.jumlahIdPertanyaan = jumlahIdPertanyaan; }
    public void setKalkulasiPertanyaan(int kalkulasiPertanyaan) { this.kalkulasiPertanyaan = kalkulasiPertanyaan; }
}
