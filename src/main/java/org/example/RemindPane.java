package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;


public class RemindPane {
    //鼠标在此界面中的位置
    double xOffset,yOffset;
    public static Stage remind_stage;

    public void create_remindWindow(){
        Stage remind_stage = new Stage();
        RemindPane.remind_stage = remind_stage;
        remind_stage.initStyle(StageStyle.TRANSPARENT);
        URL fxml_url = this.getClass().getClassLoader().getResource("fxml/remindPane.fxml");
        FXMLLoader loader = new FXMLLoader(fxml_url);
        AnchorPane root = null;
        try {
            root = (AnchorPane)loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        assert root != null;
        Scene scene_1 = new Scene(root);
        scene_1.setFill(Color.TRANSPARENT);
        URL css_url = this.getClass().getClassLoader().getResource("css/RemindPaneStyle.css");
        assert css_url != null;
        scene_1.getStylesheets().addAll(css_url.toExternalForm());
        remind_stage.setScene(scene_1);
        remind_stage.show();
        drag_and_drop(root);

    }
    public void drag_and_drop(AnchorPane root){
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            remind_stage.setX(event.getScreenX() - xOffset);
            remind_stage.setY(event.getScreenY() - yOffset);
        });
    }

    public String getImagePath(String imageName){
        return this.getClass().getClassLoader().getResource("image/"+imageName).toString();
    }
}
