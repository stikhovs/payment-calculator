package paymentcalculation;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewTwo implements Initializable {
    @FXML
    DatePicker holidays;
    @FXML
    DatePicker holidayPeriodStart;
    @FXML
    DatePicker holidayPeriodEnd;
    @FXML
    AnchorPane holidayAnchor;
    @FXML
    TableView<String> holidayTable;
    @FXML
    TableColumn<String,String> holidayColumn;
    
    static List<String> nextMonthHolidays = new ArrayList<>();
    static List<String> nextMonthDaysOfWeek = new ArrayList<>();
    static List<String> nextMonthDays = new ArrayList<>();
    
    @FXML
    private void SceneOneAction(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("ViewOne.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage)source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
    }
    @FXML
    private void SceneThreeAction(ActionEvent event) throws IOException {
        
        for(String date : holidayTable.getItems()) {
            System.out.println("Выходной день: " + LocalDate.parse(date).getDayOfWeek());
            nextMonthHolidays.add(date);
        }
        Parent next = FXMLLoader.load(getClass().getResource("ViewThree.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage)source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> tableData = holidayTable.getItems();
        holidayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        holidays.setOnAction((event) -> {             
            //System.out.println(holidays.getValue());
            if(!tableData.contains(holidays.getValue().toString())){
                tableData.add(holidays.getValue().toString());
            }
        });
        holidayTable.setOnKeyReleased(event -> {
            if(event.getCode().equals(event.getCode().DELETE)){
                String selectedItem = holidayTable.getSelectionModel().getSelectedItem();
                holidayTable.getItems().remove(selectedItem);
            }
//            System.out.println("Кнопка: " + event.getCharacter());
//            System.out.println("Кнопка: " + event.getText());
//            System.out.println("Кнопка: " + event.getCode());
//            System.out.println("Кнопка: " + event.getCode().getName());
        });
        
        holidayPeriodStart.setOnAction((event) -> {
            if(!holidayPeriodStart.getEditor().getText().isEmpty()){
                holidayPeriodEnd.setDisable(false);
            }
            else {
                holidayPeriodEnd.getEditor().clear();
                holidayPeriodEnd.setDisable(true);
            }
        });
        
        holidayPeriodEnd.setOnAction((event) -> {
            LocalDate start = holidayPeriodStart.getValue();
            LocalDate end = holidayPeriodEnd.getValue();
            Period period = start.until(end);
            System.out.println("period: " + period.getDays());
            if(period.getDays() > 0){
                LocalDate local;
                for(int i = 0; i <= period.getDays(); i++){
                    local = start.plusDays(i);
                    if(!tableData.contains(local.toString())){
                        tableData.add(local.toString());
                    }                    
                }                
            }
            
            
        });
    }
    
}
