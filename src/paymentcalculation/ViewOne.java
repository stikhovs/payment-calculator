
package paymentcalculation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ViewOne implements Initializable {
    static String chosenFile = "";
    static String savedPath = "";
    static String fileName = "";
    private static String saveFilePath = "";
    static String chosenMonth = "";
    
    @FXML
    TextField filePathTextField;
    @FXML
    ChoiceBox months;
    static int chosenMonthNumber;
    @FXML
    Spinner years;
    static int chosenYear;
    @FXML
    Button nextSceneButton;
    @FXML
    Button backButton;
    
    @FXML
    private void openFileAction(ActionEvent event) {
              
        FileChooser fc = new FileChooser();
        
        fc.setInitialDirectory(new File(System.getProperty("user.home")+"/Desktop"));        
        
        if(!savedPath.equals("")){
            fc.setInitialDirectory(new File(savedPath));
        }
        
        
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xlsm files","*.xlsm"));
        File file = fc.showOpenDialog(null);
        
           if(file != null){
                saveFilePath = file.getAbsolutePath();
                chosenFile = file.getAbsolutePath();
                fileName = file.getName();
                filePathTextField.setText(chosenFile);
                System.out.println("chosenFile: " + chosenFile);
                nextSceneButton.setDisable(false);
            }
           else if(chosenFile.isEmpty()) {
            System.out.println("Файл не был выбран");
            filePathTextField.setText("Выберите файл");
            nextSceneButton.setDisable(true);
        }
    }
    @FXML
    private void SceneTwoAction(ActionEvent event) throws IOException {
            ViewOne.chosenMonth = (String)months.getValue();
            ViewOne.chosenMonthNumber = months.getItems().indexOf(ViewOne.chosenMonth);
            System.out.println("ViewOne.chosenMonth :" + ViewOne.chosenMonth );
            System.out.println("ViewOne.chosenMonthNumber :" + ViewOne.chosenMonthNumber );
            ViewOne.chosenYear = (int)years.getValue();
            
            Parent next = FXMLLoader.load(getClass().getResource("ViewTwo.fxml"));
            Scene sceneTwo = new Scene(next);
            Node source = (Node) event.getSource();
            Stage thisStage = (Stage)source.getScene().getWindow();
            thisStage.setScene(sceneTwo);
            thisStage.show();
            
            
    }
    
    /* Функция для изменения первой буквы месяца на заглавную. 
       Иначе в ChoiceBox не подставляется начальное значение месяца при запуске
       jar-файла из папки dist */
    public static String firstUpperCase(String word){
	if(word == null || word.isEmpty()) return "";//или return word;
	return word.substring(0, 1).toUpperCase() + word.substring(1);
}
    
    
    @FXML
    private void backAction(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage) source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tooltip tooltip = new Tooltip("Выберите месяц");
        months.setTooltip(tooltip);
        months.setItems(FXCollections.observableArrayList("Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"));

        
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");        
        months.setValue(firstUpperCase(dateFormat.format(currentDate)));
        Calendar now = GregorianCalendar.getInstance();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, 3000, now.get(now.YEAR));
        years.setValueFactory(valueFactory);
    }
    
    
}

