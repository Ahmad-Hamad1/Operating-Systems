package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Random;

public class Controller {
    private static boolean existingInputFile = false;
    private boolean isFileRead = false;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField numberOfProcessors;
    @FXML
    private Button btnProcess;
    @FXML
    private Button btnChooseFile;
    @FXML
    private Label filePath;

    private void randomGeneration(){
        try {
            FileWriter myWriter = new FileWriter("input.txt");
            Random random = new Random();
            int processesNum = random.nextInt(100 - 20) + 20;
            int burstsNum = random.nextInt(20 - 5) + 5;
            int[] delay = new int[processesNum];
            for(int i = 0; i < processesNum; i++){
                delay[i] = random.nextInt(100 - 20) + 20;
            }
            int[][] cpuBurst = new int[processesNum][burstsNum];
            int[][] IOBurst = new int[processesNum][burstsNum];
            for(int i = 0; i < processesNum; i++){
                for(int j = 0; j < burstsNum; j++){
                    cpuBurst[i][j] = random.nextInt(120 - 2) + 2;
                    IOBurst[i][j] = random.nextInt(120 - 2) + 2;
                }
            }
            int curr = 0;
            for(int i = 0; i < processesNum; i++){
                long sum = 0 ;
                Process process = new Process();
                myWriter.write(delay[i]+" ");
                myWriter.write((i+1) + " ");
                process.setArrived(delay[i]+curr);
                process.setDelay(delay[i]);
                process.setPid(i+1);
                curr += delay[i];
                for(int j = 0; j < burstsNum; j++){
                    myWriter.write(cpuBurst[i][j] + " ");
                    myWriter.write(IOBurst[i][j] + " ");
                    process.insertCPUBurstTime(cpuBurst[i][j]);
                    process.insertIOBurstTime(IOBurst[i][j]);
                    sum += cpuBurst[i][j]+IOBurst[i][j];
                    SimulateController.allCPUBursts += cpuBurst[i][j];
                }
                process.setBurstsSum(sum);
                Process.processes.add(process);
                myWriter.write("\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private void readFile(String path){
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            long arrivalForEachProcess = 0;
            while((line = br.readLine()) != null){
                long sum = 0;
                String[] numbers = line.split(" ");
                if(numbers.length < 4)
                    continue;
                Process process = new Process();
                arrivalForEachProcess += Long.parseLong(numbers[0]);
                process.setDelay(Integer.parseInt(numbers[0]));
                process.setArrived(arrivalForEachProcess);
                process.setPid(Integer.parseInt(numbers[1]));
                for(int i = 2; i < numbers.length; i++){
                    sum += Integer.parseInt(numbers[i]);
                    SimulateController.allCPUBursts += Integer.parseInt(numbers[i]);
                    process.insertCPUBurstTime(Integer.parseInt(numbers[i++]));
                    process.insertIOBurstTime(Integer.parseInt(numbers[i]));
                    sum += Integer.parseInt(numbers[i]);
                }
                process.setBurstsSum(sum);
                Process.processes.add(process);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chooseFile(ActionEvent event){
        Process.processes.clear();
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));  // To show only text files.
        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile != null){
            existingInputFile = true;
            readFile(selectedFile.getAbsolutePath());
            isFileRead = true;
            filePath.setText(selectedFile.getAbsolutePath());
            filePath.setAlignment(Pos.CENTER);
        }
        else{
            System.out.println("File Isn't Valid!!!");
        }
    }

    public void proceed(ActionEvent event){
        try {
            if(!existingInputFile && !isFileRead) {
                randomGeneration();
            }
            String proc = numberOfProcessors.getText();
            if(proc == null || proc.length() == 0){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Missing Value");
                alert.setContentText("Enter number of processors");
                alert.showAndWait();
            }
            else {
                int processors = Integer.parseInt(proc);
                SimulateController.setNumberOfProcessors(processors);
                AnchorPane pane = FXMLLoader.load(getClass().getResource("simulate.fxml"));
                rootPane.getChildren().setAll(pane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}