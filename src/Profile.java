import java.util.ArrayList;
import java.util.HashMap;


public class Profile {

    private long id;
    private HashMap<Long, Answer> questions;
    
    /**
     * @param id
     * @param questions
     */
    public Profile(long id, HashMap<Long, Answer> questions) {
        this.id = id;
        this.questions = questions;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the questions
     */
    public HashMap<Long, Answer>  getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(HashMap<Long, Answer>  questions) {
        this.questions = questions;
    }
    
    
    
    
    
}
