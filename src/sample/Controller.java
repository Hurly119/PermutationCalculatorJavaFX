package sample;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    private ArrayList<String> solutionWords = new ArrayList<>();
    private ArrayList<String> solutionsDone = new ArrayList<String>();

    @FXML
    private TextField soltnInputTextField;

    @FXML
    private Label soltnExplanationLabel = new Label();


    @FXML
    private GridPane solutionHistoryGridPane = new GridPane();

    @FXML
    public void initialize() throws IOException {
        if(getAllDataFromTextFile() != null){
            populateArrayList(getAllDataFromTextFile());
        }

        soltnExplanationLabel.setText("");
        showHistoryInGridPane();

    }

    public void showReference(ActionEvent event){
        JOptionPane.showMessageDialog(null,"Meery, B. (2016, March 24). Permutations with Repetition. Retrieved March 24, 2020 from:\nhttps://www.ck12.org/probability/permutations-with-repetition/lesson/Permutations-with-Repetition-BSC-PST/","Website",JOptionPane.INFORMATION_MESSAGE);
    }

    public void showSolutionAndRecordIt(ActionEvent event) throws IOException {
        String userInput = soltnInputTextField.getText().trim().toUpperCase();


        if(stringIsNull(userInput)){
         soltnExplanationLabel.setText("Textfield not filled.");
        }else {
            String solution = explanationOfPermutationSolution(userInput);
            soltnExplanationLabel.setText(solution);

            addSolutionToArrayList(solution);
            writeAllToFile();
        }
    }

    private Parent getParentFromFXML(String FXMLFilename) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(FXMLFilename+".fxml"));
        return root;
    }

    public void gotoSolvePage(ActionEvent event) throws IOException {
        changePage("SolvePage",event);
    }

    public void gotoMainMenu(ActionEvent event) throws IOException {
        changePage("sample",event);
    }

    public void gotoSolutionHistoryPage(ActionEvent event) throws IOException {
        changePage("SolutionHistoryPage",event);

    }

    //Creates button that shows solutions when clicked
    private void showHistoryInGridPane(){

        for(int i=0; i<solutionsDone.size();i++){
            String solution = solutionsDone.get(i);
            int indexToPlace = i+1;
            Button gridPaneSolution = new Button(solutionWords.get(i));
            gridPaneSolution.setPrefWidth(150);
            Button gridPaneSolutionDelete = new Button("Delete");
            int finalI = i;

            //Creates Pane that shows solution when button clicked
            gridPaneSolution.setOnAction(event -> {
                BorderPane answerPane = new BorderPane();
                Label solutionLabel = new Label(solution);
                Button backToPrev = new Button("Back");
                Label theSolutionLabel = new Label(solutionWords.get(finalI)+"'s Permutation");

                answerPane.setTop(theSolutionLabel);
                answerPane.setBottom(backToPrev);
                answerPane.setCenter(solutionLabel);

                BorderPane.setAlignment(solutionLabel, Pos.TOP_CENTER);
                BorderPane.setAlignment(backToPrev,Pos.BASELINE_LEFT);
                BorderPane.setAlignment(theSolutionLabel, Pos.TOP_CENTER);

                Stage currentStage = getCurrentStageFromEvent(event);
                currentStage.setScene(new Scene(answerPane));

                backToPrev.setOnAction(event1 -> {
                    try {
                        gotoSolutionHistoryPage(event1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


            });

            //removes data from arrayList and overwrite textfiles with new information
            //then refreshes page
            gridPaneSolutionDelete.setOnAction(event -> {
                solutionsDone.remove(finalI);
                solutionWords.remove(finalI);
                try {
                    writeAllToFile();
                    gotoSolutionHistoryPage(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //adds delete button and the solution button in gridpane
            solutionHistoryGridPane.add(gridPaneSolutionDelete,0,indexToPlace);
            solutionHistoryGridPane.add(gridPaneSolution,1,indexToPlace);//adds everything to 2nd row onwards
        }
    }




    private void changePage(String FXMLSceneName, ActionEvent event) throws IOException {
        Parent scene = getParentFromFXML(FXMLSceneName);
        Stage currentStage = getCurrentStageFromEvent(event);

        currentStage.setScene(new Scene(scene));
        currentStage.show();
    }

    private Stage getCurrentStageFromEvent(ActionEvent event){
        Stage eventStageSource = (Stage)((Node)event.getSource()).getScene().getWindow();
        return eventStageSource;
    }


    private void addSolutionToArrayList(String solution){
        solutionsDone.add(solution);
    }

    private void writeSolutions(FileWriter solutionWriter) throws IOException {
        for(String solution: solutionsDone){
            solutionWriter.write(solution);
        }
        solutionWriter.close();
    }


    private void writeAllToFile() throws IOException {
        File fileOfSolutions = new File("solutionHistory.txt");

        //if file not created, append values
        //else overwrite all values with new values
        if(fileOfSolutions.createNewFile()){
            FileWriter writer = new FileWriter(fileOfSolutions,true);

            writeSolutions(writer);
        }else{
            FileWriter overWriter = new FileWriter(fileOfSolutions,false);

            writeSolutions(overWriter);
        }
    }

    //takes all words and their explanation.
    //used to make a parallel array
    private void populateArrayList(String [] historyData){
        String checker;

        for(String solution: historyData){
            solutionsDone.add(solution);

            checker = solution;

            //takes the first word that is found within parenthesis() for each String
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(checker);
            if (m.find()) {
                solutionWords.add(m.group(1));
            }
        }

    }

    private String[] getAllDataFromTextFile() throws IOException {
        File historyDatabase = new File("solutionHistory.txt");
        historyDatabase.createNewFile();

        FileReader readFile = new FileReader(historyDatabase);
        BufferedReader readAllData = new BufferedReader(readFile);

        String stringRepository = "";
        String[] allData;

        String line;


        //reads all data in textFile and compile them in stringRepository
        while((line=readAllData.readLine())!= null){
            line+="\n";
            stringRepository += line;
        }


        if((stringIsNull(stringRepository))) {
            return null;
        }else {
            allData = stringRepository.split("(\n\n\n)");

            //adds (\n\n\n) to data for reading and separating inputs.
            for (int i = 0; i < allData.length; i++) {
                allData[i] += "\n\n\n";
            }
            return allData;
        }
    }



    private double getPermutationWithRepetition(String characters) {
        double numberOfPermutation = getFactorial(characters.length());
        LinkedHashMap<Character, Integer> charactersFrequency = countRepetition(characters);
        for(int characterRepetition: charactersFrequency.values()){
            numberOfPermutation /= getFactorial(characterRepetition);
        }
        return Math.round(numberOfPermutation);
    }

    private String explanationOfPermutationSolution(String characters){

        int characterLength = characters.length();
        double numperOfPermutation = getFactorial(characterLength);

        String explanation = "This input ("+characters+") has a length of: "+characterLength+ "\n"
                +"We get the factorial of this number which is: "+numperOfPermutation+"\n";

        if(hasRepetition(characters)){
            LinkedHashMap<Character, Integer> charactersFrequency = countRepetition(characters);

            explanation += "Since your input has repetitions,\n we have to get the factorial of the frequency of each characters."+"\n\n";
            int identityForMultiplication = 1;

            double totalFactorialOfCharacters = identityForMultiplication;

            //take the character and their frequency and get its factorial
            for(Map.Entry<Character, Integer> characterInHashMap: charactersFrequency.entrySet()){
                char charIdentity = characterInHashMap.getKey();
                int charRepetition = characterInHashMap.getValue();
                double factorialOfCharacter = getFactorial(charRepetition);

                totalFactorialOfCharacters *= factorialOfCharacter;

                explanation += "Character "+charIdentity+" repeats "+charRepetition+" time(s)."+"\n"
                        +"the factorial is: " +factorialOfCharacter+"\n";
            }
            explanation += "\nWe then take the product\n"
                    +"of the factorial of each character's frequency ("+totalFactorialOfCharacters +")\n"
                    + "and divide it to the input length's factorial ("+numperOfPermutation+").\n"
                    +"The permutation of your input is then: "+getPermutationWithRepetition(characters)+"\n\n\n";

        }else{
            explanation += "Since there are no repetitions,\nthis would be your number of permutations.\n\n\n";

        }
        return explanation;
    }

    private Boolean hasRepetition(String characters){
        LinkedHashMap<Character,Integer> countCharacters = countRepetition(characters);

        //check String if it contains a character that repeats more than once.
        for(int characterCount : countCharacters.values()){
            if (characterCount > 1){
                return true;
            }
        }
        return false;
    }


    private Boolean stringIsNull(String string){
        return string.equals("");
    }

    private int getFactorial(int number){
        int identityForMultiplication = 1;

        int factorial = identityForMultiplication;
        while(number > 0){
            factorial *= number;
            number--;
        }
        return factorial;
    }

    private LinkedHashMap<Character, Integer> countRepetition(String characters){
        LinkedHashMap<Character, Integer> characterCountInString = new LinkedHashMap<Character, Integer>();

        //if hashmap contains the character add 1 to its count,
        // else add the character to the hashmap with count 1.
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
