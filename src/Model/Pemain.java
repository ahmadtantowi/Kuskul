package Model;

/**
 *
 * @author Ahmad
 */
public class Pemain {
    private static String namaAkun;
    private static String kataSandi;
    private static String jurusan;
    private static String universitas;
    private static int totalMain;
    private static int nilaiAkhir;
    
    public static String getNamaAkun() { return namaAkun; }
    public static String getKataSandi() { return kataSandi; }
    public static String getJurusan() { return jurusan; }
    public static String getUniversitas() { return universitas; }
    public static int getTotalMain() { return totalMain; }
    public static int getNilaiAkhir() { return nilaiAkhir; }
    
    public static void setNamaAkun(String namaAkun) { Pemain.namaAkun = namaAkun; }
    public static void setKataSandi(String kataSandi) { Pemain.kataSandi = kataSandi; }
    public static void setJurusan(String jurusan) { Pemain.jurusan = jurusan; }
    public static void setUniversitas(String universitas) { Pemain.universitas = universitas; }
    public static void setTotalMain(int totalMain) { Pemain.totalMain = totalMain; }
    public static void setNilaiAkhir(int nilaiAkhir) { Pemain.nilaiAkhir = nilaiAkhir; }
}
