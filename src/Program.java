import java.io.PrintWriter;
import java.util.Scanner;

public class Program {
    static String[] urlsForTest = new String[]{"https://www.simbirsoft.com/", "https://www.microsoft.com/ru-ru/", "https://www.guru99.com/java-platform.html", "http://algolist.ru/"};
    public static PrintWriter printWriter = new PrintWriter(System.out);

    public static void main(String[] args) {
        if (args.length != 0) {
            if (args.length == 1) {
                String url = args[0];
                load(url, "");
            } else {
                String url = args[0];
                String path = args[1];
                load(url, path);

            }
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите url сайта для сбора статистики о словах");
        String url = "";
        String path = "";
        url = scanner.nextLine();
        System.out.println("Введите путь к файлу в который будет записана html страница ");
        path = scanner.nextLine();
        if (url == "")
            test();
        else {
            load(url, path);
        }


    }

    public static void test() {
        for (String url : urlsForTest) {
            load(url, "");
        }


    }

    public static void load(String url, String path) {

        if (path.equals("")) {
            path = "page.html";
        }
        System.out.printf("статистика слов  по %s \n ", url);
        System.out.println();
        HtmlPageLoader htmlPageLoader = new HtmlPageLoader(url);
        htmlPageLoader.load(path);
        HtmlPageSplitter htmlPageSplitter = new HtmlPageSplitter("page.html", htmlPageLoader.usedEncode);
        htmlPageSplitter.getWordsStatistic();
        htmlPageSplitter.printStatistic();
    }
}
