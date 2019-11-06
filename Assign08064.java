import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

class cal_date {
    int date;
    int month;
    int year;

    Random rand = new Random();

    cal_date() {
        // generate random date
        this.date = rand.nextInt(30) + 1;
        this.month = rand.nextInt(11) + 1;
        this.year = rand.nextInt(40) + 1980;
    }

    cal_date(int d, int m, int y) {
        this.date = d;
        this.month = m;
        this.year = y;
    }

    void setCalDateToToday() {
        this.date = 4;
        this.month = 11;
        this.year = 2019;
    }

    public void printDate() {
        System.out.println(this.date + "/" + this.month + "/" + this.year);
    }

    public String getDate() {
        return (this.date + "/" + this.month + "/" + this.year).toString();
    }

    public static int getDiff(cal_date a, cal_date b) {
        // b - a
        int temp_a = 365 * (a.year - 2010) + 30 * a.month + a.date;
        int temp_b = 365 * (b.year - 2010) + 30 * b.month + b.date;
        return Math.abs(temp_b - temp_a);
    }
}

class Tag{
    String tagName;
    String tagDescription;
    Tag(String tgn,String td){
        this.tagName = tgn;
        this.tagDescription = td;
    }
}

class UserProfileSO{
    String userName;
    String LinkToUserProfile;
    int UserID;
    UserProfileSO(){
        
    }
}

class StackOverflow {
    String Question;
    String LinkToQuestion;
    // Tag tags;
    int NoOfViews;
    int NoOfAnswer;
    // UserProfileSO user;
    cal_date postDate;
    String AnswerSnippet;

    StackOverflow( String question , String LTQ, int NOV, int NOA, cal_date postdate, String ANS){
        this.Question = question;
        this.LinkToQuestion = LTQ;
        // this.tags = tg;  Tag tg,
        this.NoOfViews = NOV;
        this.NoOfAnswer = NOA;
        // this.user = u;   UserProfileSO u,
        this.postDate = postdate;
        this.AnswerSnippet = ANS;
    }
    
}

class readHtmlFile{
    public static String ReadFile(String fileName) throws Exception {
        StringBuilder fileData = new StringBuilder();
        File htmlFile = new File(fileName);

        Scanner input = new Scanner(htmlFile);
        while (input.hasNextLine()){
            fileData.append(input.nextLine());
        }
        input.close();

        return fileData.toString();
    }

    public static ArrayList<String> getQuestionList(String inputData) {
        
        // fetching the start point of quesiton block
        Pattern pattern = Pattern.compile("<div class=\"question-summary\"");
        Matcher StartSearch = pattern.matcher(inputData);
        
        ArrayList<Integer> startPointIndex = new ArrayList<>();
        while (StartSearch.find()) {
            startPointIndex.add(StartSearch.start());
        }
        
        // fetching the end point of quesiton block
        pattern = Pattern.compile("</div> [ ]* </div> [ ]* </div> [ ]* </div> [ ]* </div> [ ]* </div>");
        Matcher EndSearch = pattern.matcher(inputData);

        ArrayList<Integer> endPointIndex = new ArrayList<>();
        while (EndSearch.find()) {
            endPointIndex.add(EndSearch.end());
        }

        // storing in array so that each bolck can be processed individually
        ArrayList<String> questionBlocks = new ArrayList<String>();

        for (int i = 0; i < startPointIndex.size(); i++) {
            String data = inputData.substring(startPointIndex.get(i), endPointIndex.get(i));
            // data = data.replaceAll(",", "");
            questionBlocks.add(data);
        }

        return questionBlocks;
    }

    public static int readVotes(String questionString){
        Pattern StartSpan = Pattern.compile("<span class=\"vote-count-post \"><strong>");
        Matcher SpanOpenIndex = StartSpan.matcher(questionString);
        SpanOpenIndex.find();

        Pattern EndSpan = Pattern.compile("</strong></span>");
        Matcher SpanCloseIndex = EndSpan.matcher(questionString);
        SpanCloseIndex.find();

        String valueStr = questionString.substring(SpanOpenIndex.end(), SpanCloseIndex.start());

        int votesCount = Integer.valueOf(valueStr);

        return votesCount;
    }

    public static int readAnswerCount(String questionString) {
        Pattern StartAnswerCountDiv = Pattern.compile("<div class=\"status answered\">[ ]*<strong>");
        Matcher AnswerCountOpenIndex = StartAnswerCountDiv.matcher(questionString);
        
        if (!AnswerCountOpenIndex.find())
            StartAnswerCountDiv = Pattern.compile("<div class=\"status answered-accepted\">[ ]*<strong>");
            AnswerCountOpenIndex = StartAnswerCountDiv.matcher(questionString);
            if (!AnswerCountOpenIndex.find())
                StartAnswerCountDiv = Pattern.compile("<div class=\"status unanswered\">[ ]*<strong>");
                AnswerCountOpenIndex = StartAnswerCountDiv.matcher(questionString);
                AnswerCountOpenIndex.find();
        
        Pattern EndAnswerCountDiv = Pattern.compile("</strong>answer[s ][ ]*</div>");
        Matcher AnswerCountCloseIndex = EndAnswerCountDiv.matcher(questionString);
        AnswerCountCloseIndex.find();
        
        String valueStr = questionString.substring(AnswerCountOpenIndex.end(), AnswerCountCloseIndex.start());
        int answerCount = Integer.valueOf(valueStr);

        return answerCount;
    }

    public static int readViews(String questionString) {
        Pattern StartSpan = Pattern.compile("<div class=\"views \" title=\"[,0-9]* views\">");
        Matcher SpanOpenIndex = StartSpan.matcher(questionString);
        if (!SpanOpenIndex.find()){
            StartSpan = Pattern.compile("<div class=\"views warm\" title=\"[,0-9]* views\">");
            SpanOpenIndex = StartSpan.matcher(questionString);
            SpanOpenIndex.find();
        }

        Pattern EndSpan = Pattern.compile("views[ ]*</div>");
        Matcher SpanCloseIndex = EndSpan.matcher(questionString);
        SpanCloseIndex.find();

        String valueStr = questionString.substring(SpanOpenIndex.end(), SpanCloseIndex.start());
        valueStr = valueStr.replaceAll("\\s", "");
        valueStr = valueStr.replace("k", "000");
        int votesCount = Integer.valueOf(valueStr);

        return votesCount;
    }

    public static String readQuestion(String questionString) {
        Pattern StartSpan = Pattern.compile("class=\"question-hyperlink\">");
        Matcher SpanOpenIndex = StartSpan.matcher(questionString);
        SpanOpenIndex.find();

        Pattern EndSpan = Pattern.compile("</a>[ ]*</h3>");
        Matcher SpanCloseIndex = EndSpan.matcher(questionString);
        SpanCloseIndex.find();

        String valueStr = questionString.substring(SpanOpenIndex.end(), SpanCloseIndex.start());

        return valueStr;
    }

    public static String readQuestionLink(String questionString) {
        Pattern StartSpan = Pattern.compile("<h3>[ ]*<a href=\"");
        Matcher SpanOpenIndex = StartSpan.matcher(questionString);
        SpanOpenIndex.find();

        Pattern EndSpan = Pattern.compile("class=\"question-hyperlink\">");
        Matcher SpanCloseIndex = EndSpan.matcher(questionString);
        SpanCloseIndex.find();

        String valueStr = questionString.substring(SpanOpenIndex.end(), SpanCloseIndex.start());

        return valueStr;
    }

    public static String UserLink(String questionString) {
        Pattern StartSpan = Pattern.compile("<div class=\"user-details\">[ ]*<a href=\"");
        Matcher SpanOpenIndex = StartSpan.matcher(questionString);
        SpanOpenIndex.find();

        Pattern EndSpan = Pattern.compile("\">[a-zA-Z0-9]*</a>[ ]*<div class=\"-flair\">"); // still giving errors
        Matcher SpanCloseIndex = EndSpan.matcher(questionString);
        SpanCloseIndex.find();

        String valueStr = questionString.substring(SpanOpenIndex.end(), SpanCloseIndex.start());

        return valueStr;
    }
    
    

}



public class Assign08064 {
    public static void main(String[] args) throws Exception {
        String data = readHtmlFile.ReadFile("./test.html");
        ArrayList<String> questionList = readHtmlFile.getQuestionList(data);
        for (int i = 0; i < questionList.size(); i++) {
            // System.out.println(questionList.get(i));
            System.out.println( i+1 +
                " " +readHtmlFile.readVotes(questionList.get(i)) +
                " " + readHtmlFile.readAnswerCount(questionList.get(i)) +
                " " +readHtmlFile.readViews(questionList.get(i))
                // " " + readHtmlFile.UserLink(questionList.get(i))
            );
            // System.out.println("\n\n\n\n");
        }
    }
}