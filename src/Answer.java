import java.util.ArrayList;


public class Answer {
    
    private long answer;
    private long importance;
    private ArrayList<Long> acceptableAnswers;
    
    
    /**
     * @param id
     * @param answer
     * @param acceptableAnswers
     */
    public Answer(long answer, long importance, ArrayList<Long> acceptableAnswers) {
        this.answer = answer;
        this.importance = importance;
        this.acceptableAnswers = acceptableAnswers;
    }
    
    /**
     * @return the answer
     */
    public long getAnswer() {
        return answer;
    }
    /**
     * @param answer the answer to set
     */
    public void setAnswer(long answer) {
        this.answer = answer;
    }
    /**
     * @return the importance
     */
    public long getImportance() {
        return importance;
    }


    /**
     * @param importance the importance to set
     */
    public void setImportance(long importance) {
        this.importance = importance;
    }


    /**
     * @return the acceptableAnswers
     */
    public ArrayList<Long> getAcceptableAnswers() {
        return acceptableAnswers;
    }
    /**
     * @param acceptableAnswers the acceptableAnswers to set
     */
    public void setAcceptableAnswers(ArrayList<Long> acceptableAnswers) {
        this.acceptableAnswers = acceptableAnswers;
    }

}
