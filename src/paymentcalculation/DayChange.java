
package paymentcalculation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DayChange implements Initializable {

    @FXML
    ChoiceBox chooseFrom;
    @FXML
    ChoiceBox chooseTo;
    @FXML
    Label addedDaysLabel;
    static String added = "";
    
    
    @FXML
    private void addDayChange(ActionEvent event) {
        ViewTwo.changedDaysMinus.add(chooseFrom.getValue().toString());
        ViewTwo.changedDaysPlus.add(chooseTo.getValue().toString());
        addedDaysLabel.setVisible(true);
        ViewTwo.addedChangesNum++;
        addedDaysLabel.setText("Добавлено: " + ViewTwo.addedChangesNum);
        added = "Добавлено: " + ViewTwo.addedChangesNum;
        
        
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage)source.getScene().getWindow();
        thisStage.fireEvent(new WindowEvent(thisStage, WindowEvent.WINDOW_CLOSE_REQUEST));  
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
         chooseFrom.setItems(FXCollections.observableArrayList("пн","вт","ср","чт","пт","сб","вс"));
         chooseTo.setItems(FXCollections.observableArrayList("пн","вт","ср","чт","пт","сб","вс"));
         chooseFrom.setValue("пн");
         chooseTo.setValue("пн");
         addedDaysLabel.setVisible(false);
    }
    
}
