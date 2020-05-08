package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class test {
    private static ArrayList<String> solutionsDone = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        populateArrayList(getAllDataFromTextFile());

        writeAllSolutionsToFile();


    }

    public Parent getParentFromFXML(String FXMLFilename) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(FXMLFilename+".fxml"));
        return root;
    }

    public void changePage(String FXMLSceneName , ActionEvent event) throws IOException {
        Parent scene = getParentFromFXML(FXMLSceneName);
        Stage currentStage = getCurrentStageFromEvent(event);

        currentStage.setScene(new Scene(scene));
        currentStage.show();
    }
    public Stage getCurrentStageFromEvent(ActionEvent event){
        Stage eventStageSource = (Stage)((Node)event.getSource()).getScene().getWindow();
        return eventStageSource;
    }

    private static void addSolutionToArrayList(String solution){
        solutionsDone.add(solution);
    }


    private static void writeAllSolutionsToFile() throws IOException {
        File fileOfSolutions = new File("solutionHistory.txt");

        if(fileOfSolutions.createNewFile()){
            FileWriter writer = new FileWriter(fileOfSolutions,true);

            writeSolutions(writer);
        }else{
            FileWriter overWriter = new FileWriter(fileOfSolutions,false);

            writeSolutions(overWriter);
        }
    }

    private static String[] getAllDataFromTextFile() throws IOException {
        File historyDatabase = new File("solutionHistory.txt");
        historyDatabase.createNewFile();

        FileReader readFile = new FileReader(historyDatabase);
        BufferedReader readAllData = new BufferedReader(readFile);

        String stringRepository = "";
        String[] allData;

        String line;

        while((line=readAllData.readLine())!= null){
            line+="\n";
            stringRepository += line;
        }
        System.out.println(stringRepository);

        allData = stringRepository.split("(\n\n\n)");

        for(int i =0; i<allData.length;i++){
            allData[i]+="\n\n\n";
        }
        return allData;
    }

    private static void populateArrayList(String [] historyData){
        for(String solution: historyData){
            solutionsDone.add(solution);
        }
    }

    private static void writeSolutions(FileWriter solutionWriter) throws IOException {
        for(String solution: solutionsDone){
            solutionWriter.write(solution);
        }
        solutionWriter.close();
    }

    private static double getPermutationWithRepetition(String characters) {
        double numberOfPermutation = getFactorial(characters.length());
        LinkedHashMap<Character, Integer> charactersFrequency = countRepetition(characters);
        for(int characterRepetition: charactersFrequency.values()){
            numberOfPermutation /= getFactorial(characterRepetition);
        }
        return numberOfPermutation;
    }

    private static String explanationOfPermutationSolution(String characters){

        int characterLength = characters.length();
        double numperOfPermutation = getFactorial(characterLength);

        String explanation = "This input ("+characters+") has a length of: "+characterLength+ "\n"
                            +"We get the factorial of this number which is: "+numperOfPermutation+"\n";

        if(hasRepetition(characters)){
            LinkedHashMap<Character, Integer> charactersFrequency = countRepetition(characters);

            explanation += "Since your input has repetitions, we have to get the factorial of the frequency of each characters."+"\n\n";
            int identityForMultiplication = 1;

            double totalFactorialOfCharacters = identityForMultiplication;

            for(Map.Entry<Character, Integer> characterInHashMap: charactersFrequency.entrySet()){
                char charIdentity = characterInHashMap.getKey();
                int charRepetition = characterInHashMap.getValue();
                double factorialOfCharacter = getFactorial(charRepetition);

                totalFactorialOfCharacters *= factorialOfCharacter;

                explanation += "Character "+charIdentity+" repeats "+charRepetition+" time(s)."+"\n"
                        +"the factorial is: " +factorialOfCharacter+"\n";
            }
            explanation += "\nWe then take the product of the factorial of each characters' frequency ("+totalFactorialOfCharacters +")\n"
                    + "and use it to divide the input length's factorial ("+numperOfPermutation+").\n"
                    +"The permutation of your input is then: "+getPermutationWithRepetition(characters)+"\n\n\n";

        }else{
            explanation += "Since there are no repetitions, this would be your number of permutations.\n\n\n";

        }
        return explanation;
    }

    private static Boolean hasRepetition(String characters){
        LinkedHashMap<Character,Integer> countCharacters = countRepetition(characters);

        for(int characterCount : countCharacters.values()){
            if (characterCount > 1){
                return true;
            }
        }
        return false;
    }


    private static int getFactorial(int number){
        int identityForMultiplication = 1;

        int factorial = identityForMultiplication;
        while(number > 0){
            factorial *= number;
            number--;
        }
        return factorial;
    }

    private static LinkedHashMap<Character, Integer> countRepetition(String characters){
        LinkedHashMap<Character, Integer> characterCountInString = new LinkedHashMap<Character, Integer>();

        for(char character: characters.toCharArray()){
            if(characterCountInString.containsKey(character)){
                int repetition = characterCountInString.get(character);
                repetition++;
                characterCountInString.replace(character,repetition);
            }else{
                characterCountInString.put(character,1);
            }
        }
        return characterCountInString;
    }
}
