import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class WebServer {

    private static final int PORT = 1989; // Port numarası sabit olarak tanımlandı

    static void main() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Web Sunucusu başlatıldı. Port: " + PORT + " dinleniyor...");

            while (true) {
                // Bağlantı kabul ediliyor
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n🤝 Yeni bağlantı kabul edildi: " + clientSocket.getInetAddress());

                // Bağlantıyı işlemek için metod çağırımı
                handleClient(clientSocket);
            }

        } catch (IOException e) {
            System.err.println("❌ Sunucu başlangıç hatası: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        // try-with-resources ile Socket dahil tüm kaynaklar otomatik kapanacak
        try (
                Socket socket = clientSocket; // Socket otomatik kapanması için eklendi
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Çıktı akışı, karakterleri byte'a çevirerek gönderir.
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {

            // Tarayıcının beklemesini önlemek için tüm başlıkların okunması
            String inputLine;
            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {
                // System.out.println("   > " + inputLine); // İsteği konsolda görmek için açılabilir
                if (inputLine.isEmpty()) break;
            }

            // HTML içeriğini hazırlama
            String htmlContent = generateHtmlContent();

            // Daha doğru bir HTTP yanıtı için
            int contentLength = htmlContent.getBytes(StandardCharsets.UTF_8).length;

            // Basit HTTP yanıtını oluşturma
            String responseHeaders =
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html; charset=utf-8\r\n" +
                            "Content-Length: " + contentLength + "\r\n" + // Content-Length eklendi
                            "Connection: close\r\n" + // Bağlantıyı kapatacağımızı bildirir
                            "\r\n"; // Başlık sonu

            // Yanıtı gönderme
            out.write(responseHeaders);
            out.write(htmlContent);

            // Buffer'daki veriyi ağa gönderme (manuel flush gereklidir)
            out.flush();
            System.out.println("   Yanıt gönderildi ve bağlantı kapatıldı.");


        } catch (IOException e) {
            System.err.println("❌ İstemci işleme hatası: " + e.getMessage());
        }
    }

    /**
     * Ödevde istenen tüm biçimlendirme kurallarını içeren HTML içeriğini üretir.
     */
    private static String generateHtmlContent() {

        String adSoyad = "Ad Soyad: Çağla Kökez ";
        String ogrenciNo = "Öğrenci No: *********";
        String kisaBiyografi = "Şu an Kırklareli Üniversitesi'nde Yazılım Mühendisliği öğrencisiyim. Yazılım geliştirme ve mobil uygulamalarla ilgileniyorum. " +
                "Boş zamanlarımda mesleğimde faydalı olacağını düşündüğüm eğitimler izliyorum. Hobilerim arasında ise amatör fotoğrafçılık ve strateji oyunları oynamak bulunuyor.";

        return "<html>" +
                "<head><title>Java Socket Sunucusu Ödevi</title></head>" +
                "<body style='background-color: #f4f4f4; color: #333; margin: 30px;'>" +

                // H1 Boyutunda Ad Soyad
                "<h1 style='color: #8B0000; border-bottom: 3px solid #8B0000; padding-bottom: 5px;'>" + adSoyad + "</h1>" +

                // H2 Boyutunda Öğrenci No
                "<h2 style='color: #008080;'>" + ogrenciNo + "</h2>" +

                // Biografi
                "<div style='border: 1px dashed #FF8C00; padding: 20px; background-color: #FFFFF0; margin-top: 20px;'>" +
                "<p style='font-family: Arial, sans-serif; font-size: 16px; color: #333; font-weight: bold;'>Kısa Biyografi:</p>" +
                "<p style='font-family: \"Courier New\", monospace; font-size: 14px; color: #556B2F; line-height: 1.6;'>" + // Font çeşidi ve renk değiştirildi
                kisaBiyografi +
                "</p>" +
                "</div>" +

                "<hr>" +
                "<p style='font-size: 10px; color: gray;'>Bu sunucu, 3. parti bileşen kullanılmadan Java Socket ile geliştirilmiştir.</p>" +

                "</body>" +
                "</html>";
    }
}
