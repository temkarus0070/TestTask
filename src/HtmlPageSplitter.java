import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlPageSplitter {
    private BufferedWriter logWritter;
    public String usedEncode = "";
    private String[] notNeccessaryTagRegex = {"<.?script[^>]*>+?", "<.?noscript[^>]*>+?>", "<.?style[^>]*>+?"};
    private String filePath;
    private HashMap<String, Integer> wordsStatisticMap;
    private SearchData searchTagData;

    public HtmlPageSplitter(String filePath, String usedEncode) {
        this.filePath = filePath;
        wordsStatisticMap = new HashMap<>();
        this.usedEncode = usedEncode;
    }

    /**
     * Построчная обработка документа
     * @return Словарь со статистикой по словам
     */
    public HashMap<String, Integer> getWordsStatistic() {

        enableLogging();
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath, Charset.forName(usedEncode)));
            var lines = fileReader.lines();
            searchTagData = new SearchData(0, -1, -1);
            lines.forEach(e ->
            {
                searchWords(e);
            });

            fileReader.close();
        } catch (Exception ex) {
            Logger.writeToWritter(logWritter, ex, "HtmlPageSpliter.getWordsStatistic");
        }
        try {
            logWritter.close();
        } catch (Exception ex) {
            Logger.writeToWritter(Program.printWriter, ex, "HtmlPageSpliter.getWordsStatistic", "во время закрытия файла");
        }
        return wordsStatisticMap;

    }

    public void enableLogging() {
        try {
            logWritter = new BufferedWriter(new FileWriter("log.txt", true));
        } catch (Exception ex) {
            String error = String.format("Во время открытия log файла произошла ошибка %s", ex);
            Logger.writeToWritter(Program.printWriter, error, "HtmlPageSpliter.enableLogging");
        }
    }


    public void searchWords(String line) {
        searchTagData.minIndex = 0;
        searchTagData.reset();
        searchWord(line);

    }

    /**
     * Обработка каждой строки документа
     * @param line
     */
    public void searchWord(String line) {
        Pattern pattern = Pattern.compile("[A-Za-zА-Яа-яё]+");
        int wordBeginIndex = 0;
        int wordEndIndex = 0;
        Matcher matcher = pattern.matcher(line);
        while (searchTagData.minIndex != -1 && searchTagData.minIndex < line.length() && matcher.find(searchTagData.minIndex) || (!searchTagData.previousIsComplete && !checkPreviousIsComplete(line))) {
            int notRegularTagLocation = isContainsNotRegularTag(line);
            if (notRegularTagLocation != -1) {
                searchTagData.minIndex = notRegularTagLocation;
                continue;
            }
            if (searchTagData.previousIsNotNeccessaryTag) {
                searchNotNeccessaryCloseTag(line);
                if (searchTagData.matchIndex == -1)
                    break;
                searchTagData.minIndex = searchTagData.matchEndIndex;
                searchTagData.previousIsNotNeccessaryTag = false;
                continue;
            }
            if (checkPreviousIsComplete(line))
                continue;
            if (!searchTagData.isCompleteTag) {
                searchTagData.previousIsComplete = false;
                break;
            }
            if (searchTagData.isValidData() && isNotNecessaryTag(line.substring(searchTagData.matchIndex, searchTagData.matchEndIndex))) {
                if (processNotNeccessaryTags(line)) {
                    searchTagData.previousIsNotNeccessaryTag = true;
                    break;
                } else {

                    continue;
                }
            }
            wordBeginIndex = matcher.start();
            wordEndIndex = matcher.end();
            checkWord(wordBeginIndex, wordEndIndex, line);
            searchTagData.currentIndex = searchTagData.minIndex;
        }
    }

    public void checkWord(int n1, int n2, String line) {


        if (!isWordIntersectWithTag(n1, n2, searchTagData.matchIndex, searchTagData.matchEndIndex) && searchTagData.isCompleteTag && !searchTagData.isNotNeccesaryOpenTag &&
                !searchTagData.previousIsNotNeccessaryTag && searchTagData.previousIsComplete) {
            addToHashMap(n1, n2, line);
        } else if (!searchTagData.isCompleteTag) {
            searchTagData.previousIsComplete = false;
            searchTagData.minIndex = -1;
        } else {
            searchTagData.minIndex = Math.max(searchTagData.matchEndIndex, n2);
        }
    }

    public boolean processNotNeccessaryTags(String line) {
        int currentIndex = searchTagData.matchIndex;
        searchNotNeccessaryCloseTag(line);


        if (searchTagData.matchIndex == -1 || searchTagData.matchIndex == currentIndex) {
            searchTagData.isNotNeccesaryOpenTag = true;
            searchTagData.previousIsNotNeccessaryTag = true;
            return true;
        } else {
            searchTagData.minIndex = searchTagData.matchEndIndex;
            return false;
        }
    }

    public boolean checkPreviousIsComplete(String line) {
        if (!searchTagData.previousIsComplete) {
            findEndInNotCompleteTag(line);

            if (searchTagData.matchEndIndex != -1) {
                searchTagData.previousIsComplete = true;
                searchTagData.minIndex = searchTagData.matchEndIndex;
            } else
                searchTagData.minIndex = line.length() - 1;
            return true;

        } else {
            searchTag(line, false);
            return false;
        }
    }

    public void searchNotNeccessaryCloseTag(String line) {
        Pattern pattern = Pattern.compile(notNeccessaryTagRegex[searchTagData.currentNotNeccesaryTag]);
        Matcher matcher = pattern.matcher(line);
        if (searchTagData.matchEndIndex == -1) {
            searchTagData.matchEndIndex = 0;
        }
        if (matcher.find(searchTagData.matchEndIndex)) {
            int a = matcher.start();
            int e = matcher.end();

            searchTagData.matchIndex = matcher.start();
            searchTagData.matchEndIndex = matcher.end();
        }

    }


    public void searchTag(String line, boolean strictMode) {
        boolean hasExactTag = searchExactTag(line);
        Pattern notCompleteTagPattern = Pattern.compile("<[^>]*");
        Matcher notCompleteTagMatcher = notCompleteTagPattern.matcher(line);
        if (hasExactTag)
            return;
        if (!hasExactTag && strictMode) {
            searchTagData.matchIndex = -1;
            return;
        } else if (notCompleteTagMatcher.find(searchTagData.currentIndex) && notCompleteTagPattern.matcher(line.substring(notCompleteTagMatcher.start())).matches()) {
            searchTagData.isCompleteTag = false;
        } else {
            findEndInNotCompleteTag(line);
        }

    }


    public boolean searchExactTag(String line) {
        Pattern pattern = Pattern.compile("<.+?>");
        int n1 = 0;
        int n2 = 0;
        Matcher matcher = pattern.matcher(line);
        if (matcher.find(searchTagData.minIndex)) {
            n1 = matcher.start();
            n2 = matcher.end();
            searchTagData = new SearchData(n2, n1, n2, searchTagData.isCompleteTag, false, false, searchTagData.isCompleteTag);
            searchTagData.isCompleteTag = true;
            searchTagData.previousIsComplete = true;
            return true;
        } else
            return false;
    }


    public void findEndInNotCompleteTag(String line) {
        Pattern pattern = Pattern.compile("[<]+|[>]+");
        Matcher matcher1 = pattern.matcher(line);
        searchTagData.isNotNeccesaryOpenTag = false;
        if (matcher1.find(searchTagData.currentIndex)) {
            searchTagData = new SearchData(matcher1.end(), matcher1.start(), matcher1.end(), true, searchTagData.isNotNeccesaryOpenTag,
                    searchTagData.isNotNeccesaryOpenTag, searchTagData.isCompleteTag);
        } else
            searchTagData = new SearchData(searchTagData.currentIndex, -1, -1, searchTagData.isCompleteTag, searchTagData.isNotNeccesaryOpenTag,
                    searchTagData.isNotNeccesaryOpenTag, searchTagData.isCompleteTag);
    }

    public boolean isWordIntersectWithTag(int wordBeginIndex, int wordEndIndex, int tagBeginIndex, int tagEndIndex) {
        return (wordBeginIndex > tagBeginIndex && (wordEndIndex <= tagEndIndex && tagBeginIndex != -1));
    }

    public boolean isNotNecessaryTag(String tag) {

        for (int i = 0; i < notNeccessaryTagRegex.length; i++) {
            Pattern pattern = Pattern.compile(notNeccessaryTagRegex[i]);
            if (pattern.matcher(tag).find()) {
                searchTagData.isNotNeccesaryOpenTag = true;
                searchTagData.currentNotNeccesaryTag = i;
                return true;
            }

        }
        return false;
    }


    /**
     * Вывод статистики по словам в консоль
     */
    public void printStatistic() {
        for (Map.Entry<String, Integer> entry : wordsStatisticMap.entrySet()) {
            System.out.printf("%s %d \n", entry.getKey(), entry.getValue());
        }
    }


    public void addToHashMap(int n1, int n2, String line) {
        String word = line.substring(n1, n2);

        searchTagData.minIndex = n2;
        word = word.toUpperCase();
        if (wordsStatisticMap.containsKey(word)) {
            int count = wordsStatisticMap.get(word) + 1;
            wordsStatisticMap.put(word, count);
        } else {
            wordsStatisticMap.put(word, 1);
        }
    }


    public int isContainsNotRegularTag(String line) {
        Pattern notRegularTagPattern = Pattern.compile("<!--[^\\w]*>");
        Matcher notRegularTagMatcher = notRegularTagPattern.matcher(line);
        if (notRegularTagMatcher.find(searchTagData.minIndex)) {
            return notRegularTagMatcher.end();
        } else return -1;
    }


}

