import java.io.*;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HtmlPageLoader {
    private BufferedWriter logWritter;
    public String usedEncode = "";
    private URL pageUrl;
    private String filePath;

    public HtmlPageLoader(String url) {
        try {
            this.pageUrl = new URL(url);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    /**
     * Загрузка html документа и определение его кодировки для корректного разбора страницы
     * в случае отсутствия кодировки или не unicode кодировки
     *
     * @param filePath Путь к файлу
     */
    public void load(String filePath) {
        enableLogging();
        this.filePath = filePath;
        try {
            var request = HttpRequest.newBuilder(pageUrl.toURI()).build();
            HttpResponse<InputStream> httpResponse = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofInputStream());
            var file = httpResponse.body();
            OutputStream outputStream = new FileOutputStream(filePath);
            file.transferTo(outputStream);
            file.close();
            outputStream.close();
            checkEncode();

        } catch (Exception exception) {
            Logger.writeToWritter(logWritter, exception, "HtmlPageLoader.load");
        }
        try {
            logWritter.close();
        } catch (Exception exception) {
            Logger.writeToWritter(Program.printWriter, exception, "HtmlPageLoader.load", "Во время закрытия log файла произошла ошибка ");
        }
    }

    public void enableLogging() {
        try {
            logWritter = new BufferedWriter(new FileWriter("log.txt", true));
        } catch (Exception ex) {
            String error = String.format("Во время открытия log файла произошла ошибка %s", ex);
            Logger.writeToWritter(Program.printWriter, error, "HtmlPageLoader.enableLogging");
        }
    }


    /**
     * Проверка кодировки
     */
    public void checkEncode() {
        try {
            String encode = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("charset=")) {
                    encode = getEncode(line);
                    break;

                }
            }
            bufferedReader.close();
            setEncoding(encode);
        } catch (Exception ex) {
            Logger.writeToWritter(logWritter, ex, "HtmlPageLoader.checkEncode");
        }
    }

    public void setEncoding(String encode) {
        if (encode.equals("")) {
            usedEncode = "windows-1251";
        } else {
            usedEncode = encode;
        }
    }


    /**
     * Получение кодировки из тега с параметром charset
     *
     * @param line строка с параметром charset
     * @return
     */
    public String getEncode(String line) {
        String encoding = "";
        String searchStr = "charset=";
        int pos = line.indexOf(searchStr);
        String substr = line.substring(pos + searchStr.length());
        Pattern pattern = Pattern.compile("[a-z-A-Z0-9]+");
        Matcher matcher = pattern.matcher(substr);
        if (matcher.find()) {
            encoding = substr.substring(matcher.start(), matcher.end());
        } else {
            Logger.writeToWritter(logWritter, "ошибка при поиске кодировки", "HtmlPageLoader.getEncode");
        }
        return encoding;
    }
}
