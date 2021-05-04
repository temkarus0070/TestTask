public class SearchData {
    public int currentIndex;
    public int matchIndex;
    public int matchEndIndex;
    public boolean isCompleteTag = true;
    public boolean isNotNeccesaryOpenTag = false;
    public boolean previousIsNotNeccessaryTag = false;
    public boolean previousIsComplete = true;
    public int minIndex = 0;

    public int currentNotNeccesaryTag = 0;

    public SearchData(int currentIndex, int matchIndex, int matchEndIndex) {
        this.currentIndex = currentIndex;
        this.matchEndIndex = matchEndIndex;
        this.matchIndex = matchIndex;
    }

    public SearchData(int currentIndex, int matchIndex, int matchEndIndex, boolean isCompleteTag) {
        this(currentIndex, matchIndex, matchEndIndex);
        this.isCompleteTag = isCompleteTag;
    }

    public SearchData(int currentIndex, int matchIndex, int matchEndIndex, boolean isCompleteTag, boolean isNotNeccesaryOpenTag) {
        this(currentIndex, matchIndex, matchEndIndex, isCompleteTag);
        this.isNotNeccesaryOpenTag = isNotNeccesaryOpenTag;
    }

    public SearchData(int currentIndex, int matchIndex, int matchEndIndex, boolean isCompleteTag, boolean isNotNeccesaryOpenTag, boolean previousIsNotNeccessaryTag) {
        this(currentIndex, matchIndex, matchEndIndex, isCompleteTag, isNotNeccesaryOpenTag);
        this.previousIsNotNeccessaryTag = previousIsNotNeccessaryTag;
    }

    public SearchData(int currentIndex, int matchIndex, int matchEndIndex, boolean isCompleteTag, boolean isNotNeccesaryOpenTag, boolean previousIsNotNeccessaryTag, boolean previousIsComplete) {
        this(currentIndex, matchIndex, matchEndIndex, isCompleteTag, isNotNeccesaryOpenTag, previousIsNotNeccessaryTag);
        this.previousIsComplete = previousIsComplete;
    }


    public void reset() {
        this.currentIndex = 0;
        this.matchIndex = -1;
        this.matchEndIndex = -1;
    }

    public boolean isValidData() {
        return matchIndex >= 0 && currentIndex >= 0;
    }
}
