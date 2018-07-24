package paymentcalculation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainMenu implements Initializable {
    
    @FXML
    private void ViewOneOpen(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("ViewOne.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage)source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
    }
    
    @FXML
    private void DebtFinderOpen(ActionEvent event) throws IOException {
        Parent next = FXMLLoader.load(getClass().getResource("DebtFinder.fxml"));
        Scene sceneTwo = new Scene(next);
        Node source = (Node) event.getSource();
        Stage thisStage = (Stage)source.getScene().getWindow();
        thisStage.setScene(sceneTwo);
        thisStage.show();
    }
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
}
