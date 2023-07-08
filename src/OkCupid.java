import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class OkCupid {

    /**
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // TODO Auto-generated method stub


        JSONParser parser = new JSONParser();
        ArrayList<Profile> profiles = new ArrayList<Profile>();


        //parse the profiles from the input JSON
        try {

            //read in the input file
            Object obj = parser.parse(new FileReader("/home/nathan/Jobs/okcupid/input.json"));

            JSONObject profilesObj = (JSONObject) obj;
            JSONArray profileIds = (JSONArray) profilesObj.get("profiles");

            //loop through each profile and parse out its fields
            for(Object tempIdObj : profileIds) {
                JSONObject idObj = (JSONObject) tempIdObj;
                long id = (Long) idObj.get("id");

                HashMap<Long, Answer>  questions = new HashMap<Long, Answer>();
                JSONArray ansArray = (JSONArray) idObj.get("answers");
                for(Object ans : ansArray) {

                    JSONObject ansObj = (JSONObject) ans;

                    //set the score for a matches answer based on the importance of the question to the person
                    long importance = (Long) ansObj.get("importance");
                    if(importance == 2) {
                        importance = 10;
                    } else if (importance == 3) {
                        importance = 50;
                    } else if (importance == 4) {
                        importance = 250;
                    }

                    long questionId = (Long) ansObj.get("questionId");
                    long answer = (Long) ansObj.get("answer");
                    ArrayList<Long> acceptableAnswers = (ArrayList<Long>) ansObj.get("acceptableAnswers");
                    
                    questions.put(questionId, new Answer(answer, importance, acceptableAnswers));
                }

                profiles.add(new Profile(id, questions));

            }            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //build our JSON object that will will return
        JSONArray resultsArr = new JSONArray(); 
        for(int i = 0; i < profiles.size(); i++) {
            JSONArray matches = new JSONArray();
            JSONObject matchResults = new JSONObject();    
            matchResults.put("profileId", new Long(profiles.get(i).getId()));
            matchResults.put("matches", matches);
            resultsArr.add(matchResults);
        }
        JSONObject resultsObj = new JSONObject();
        resultsObj.put("results", resultsArr);


        //go through every profile we have and find the best 10 matches
        for (int i = 0; i < profiles.size(); i++) {

            Profile profileA = profiles.get(i);


            //this will be used to store the top 10 scores for profile A
            HashMap<Long, Double> scoresA = new HashMap<Long, Double>();

            //this is the lowest score and associated id that's been calculated for person A
            long lowestScoreIdA = 0;
            double lowestScoreA = 1.0;

            JSONObject matchResultsA = (JSONObject) resultsArr.get(i);
            JSONArray matchesA = (JSONArray) matchResultsA.get("matches");

            //scores may have already been calculated for this profile on a previous scan, so let's get the scores
            //that we're already calculated and then determine the lowest score for the profile
            if(matchesA.isEmpty() == false) {
                Iterator<JSONObject> it = matchesA.iterator();
                while(it.hasNext()) {
                    JSONObject matchAObj = (JSONObject) it.next();
                    Double tempScore = (Double) matchAObj.get("score");
                    scoresA.put((Long) matchAObj.get("profileId"), tempScore);
                    
                    if (tempScore < lowestScoreA) {
                        lowestScoreA = tempScore;
                        lowestScoreIdA = (Long) matchAObj.get("profileId");
                    }
                }

            }

            //this is the lowest score and associated id that's been calculated for person B
            long lowestScoreIdB = 100;
            double lowestScoreB = 1.0;
            //for each profile we have, compare it to all the remaining profiles, since person B's score is calculated for person A
            //on previous scans, start scanning the remaining profiles after person A
            for(int j = i + 1; j < profiles.size(); j++) {
                Profile profileB = profiles.get(j);

                //this will be used to store the top 10 scores for profile B
                HashMap<Long, Double> scoresB = new HashMap<Long, Double>();

                JSONObject matchResultsB = (JSONObject) resultsArr.get(j);
                JSONArray matchesB = (JSONArray) matchResultsB.get("matches");
                //scores may have already been calculated for this profile on a previous scan, so let's get the scores
                //that we're already calculated and then determine the lowest score for the profile
                if(matchesB.isEmpty() == false) {
                    Iterator<JSONObject> it = matchesB.iterator();
                    while(it.hasNext()) {
                        JSONObject matchBObj = (JSONObject) it.next();
                        Double tempScore = (Double) matchBObj.get("score");
                        scoresB.put((Long) matchBObj.get("profileId"), tempScore);
                        
                        if (tempScore < lowestScoreB) {
                            lowestScoreB = tempScore;
                            lowestScoreIdB = (Long) matchBObj.get("profileId");
                        }
                    }

                } 
                //this will be the total possible points B could score for person A
                long possiblePointsA = 0;
                //this will be the number of points person B earned by providing an acceptable answer for A's question
                long earnedPointsB = 0;
                //this will be the total possible points A could score for B
                long possiblePointsB = 0;
                //this will be the number of points person A earned by providing an acceptable answer for B's question
                long earnedPointsA = 0;

                //for each question that person A answered, check if person B also answered the question
                //a person can only have a match if they have both answered the same questions
                int commonAnswers = 0;
                HashMap<Long, Answer> aQuestions = profileA.getQuestions();
                for(Long questionId : aQuestions.keySet()) {

                    //see how well B answered A's question, if person B answered it at all and vice versa, 
                    //see how well person A answered for person B
                    Answer answerB = profileB.getQuestions().get(questionId);
                    if (answerB != null) {
                        commonAnswers++;
                        
                        Answer answerA = aQuestions.get(questionId);
                        //sum the importance factor of this question for personA
                        possiblePointsA += answerA.getImportance();
                        ArrayList<Long> acceptedAnswers = answerA.getAcceptableAnswers();

                        if (acceptedAnswers != null && acceptedAnswers.contains(answerB.getAnswer())) {
                            //if person B provided an acceptable answer to A's question, add up the points B has earned
                            earnedPointsB += aQuestions.get(questionId).getImportance();
                        }

                        possiblePointsB += answerB.getImportance();
                        acceptedAnswers = answerB.getAcceptableAnswers();

                        if (acceptedAnswers != null && acceptedAnswers.contains(answerA.getAnswer())) {
                            earnedPointsA += answerB.getImportance();
                        }

                    }

                }

                //calculate the score for A and B being a match, account for a margin of error and return the low end of the margin of error
                Double score = Math.sqrt(
                        ((double) earnedPointsB / (double) possiblePointsA) * 
                        ((double) earnedPointsA / (double) possiblePointsB));
                score -= 1.0 / (double) commonAnswers;

                //add the first 10 scores to the score list for person A, then only add higher scores
                if (scoresA.keySet().size() < 11 && scoresA.keySet().size() != 10) {
                    scoresA.put(profileB.getId(), score);
                    if (score < lowestScoreA) {
                        lowestScoreA = score;
                        lowestScoreIdA = profileB.getId();
                    }

                } else if (score > lowestScoreA) {

                    scoresA.remove(lowestScoreIdA);
                    scoresA.put(profileB.getId(), score);

                    ArrayList<Double> tempScores = new ArrayList<Double>(scoresA.values());
                    Collections.sort(tempScores);

                    for(Long id : scoresA.keySet()) {
                        if (tempScores.get(0) == scoresA.get(id)) {
                            lowestScoreIdA = id;
                            break;
                        }
                    }

                    lowestScoreA = tempScores.get(0);

                }

                //boolean variable to check if the best matches for B were updated
                boolean updatedBScores = false;
                //if person A is a better match for B, then add A to B's score set
                if (scoresB.keySet().size() < 11 && scoresB.keySet().size() != 10) {
                    scoresB.put(profileA.getId(), score);
                    if (score < lowestScoreB) {
                        lowestScoreB = score;
                        lowestScoreIdB = profileA.getId();
                    }
                    updatedBScores = true;

                } else if (score > lowestScoreB) {

                    scoresB.remove(lowestScoreIdB);
                    scoresB.put(profileA.getId(), score);

                    ArrayList<Double> tempScores = new ArrayList<Double>(scoresB.values());
                    Collections.sort(tempScores);

                    for(Long id : scoresB.keySet()) {
                        if (tempScores.get(0) == scoresB.get(id)) {
                            lowestScoreIdB = id;
                            break;
                        }
                    }

                    lowestScoreB = tempScores.get(0);
                    updatedBScores = true;

                }
                
                //only update our return results if a higher score was calculated
                if (updatedBScores) {
                    matchesB.clear();
                    for (Long id : scoresB.keySet()) {
                        JSONObject match = new JSONObject();
                        match.put("profileId", id);
                        match.put("score", scoresB.get(id));
                        matchesB.add(match);
                    }
                }

            } // close profileB for loop

            
            matchesA.clear();
            for (Long id : scoresA.keySet()) {
                JSONObject match = new JSONObject();
                match.put("profileId", id);
                //round to two decimal places and store the result
                BigDecimal bd = new BigDecimal(scoresA.get(id));
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                match.put("score", bd.doubleValue());
                matchesA.add(match);
            }
        

        } // close profileA for loop

        //print our results
        System.out.println(resultsObj);


    }

    
    
    

}
