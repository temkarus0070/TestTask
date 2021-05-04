import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Logger {
    public static void writeToWritter(Writer logWritter, Exception exception, String method) {
        try {
            logWritter.write(String.format("%s  метод = %s  error =  %s  \n", LocalDateTime.now().toString(), method, exception));
        } catch (Exception ex) {
            System.out.printf("%s метод = %s  error = Во время записи log файла произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
        try {
            logWritter.flush();
        } catch (Exception ex) {
            System.out.printf("%s метод = %s error = Во время записи в log файл произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
    }

    public static void writeToWritter(Writer logWritter, Exception exception, String method, String message) {
        try {
            logWritter.write(String.format("%s  метод = %s %s  error =  %s  \n", LocalDateTime.now().toString(), method, message, exception));
        } catch (Exception ex) {
            System.out.printf("%s метод = %s  error = Во время записи log файла произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
        try {
            logWritter.flush();
        } catch (Exception ex) {
            System.out.printf("%s метод = %s error = Во время записи в log файл произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
    }

    public static void writeToWritter(Writer logWritter, String method, String message) {
        try {
            logWritter.write(String.format("%s  метод = %s   error = %s   \n", LocalDateTime.now().toString(), method, message));
        } catch (Exception ex) {
            System.out.printf("%s метод = %s  error = Во время записи log файла произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
        try {
            logWritter.flush();
        } catch (Exception ex) {
            System.out.printf("%s метод = %s error = Во время записи в log файл произошла ошибка %s", LocalDateTime.now().toString(), method, ex);
        }
    }


}
