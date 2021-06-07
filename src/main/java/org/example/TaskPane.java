package org.example;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controller.RemindPaneController;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskPane {
    File[] files;
    Stage task_stage;
    double xOffset,yOffset;
    TextField textField;
    ComboBox hour,minute;
    LocalTime localTime;
    String nowTime;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
    //自定义类
    Countdown countdown;


    public static String textValue,timeValue;

    public TaskPane(File[] files){
        this.files=files;
    }

    public void createTaskPane(){
        Stage task_stage = new Stage();
        this.task_stage = task_stage;
        task_stage.initStyle(StageStyle.TRANSPARENT);
        Label name = new Label("提醒事件:");
        Label time = new Label("时间:");
        textField = new TextField();
        //设置时间选择
        hour = new ComboBox();
        minute = new ComboBox();
        hour.getItems().addAll("00时","01时","02时","03时","04时","05时","06时","07时","08时","09时","10时","11时","12时","13时","14时","15时","16时","17时","18时","19时","20时","21时","22时","23时");
        minute.getItems().addAll("00分","01分","02分","03分","04分","05分","06分","07分","08分","09分","10分","11分","12分","13分","14分","15分","16分","17分","18分","19分","20分","21分","22分","23分",
                "24分","25分","26分","27分","28分","29分","30分","31分","32分","33分","34分","35分","36分","37分","38分","39分","40分","41分","42分","43分","44分","45分","46分","47分",
                "48分","49分","50分","51分","52分","53分","54分","55分","56分","57分","58分","59分");
        localTime = LocalTime.now();
        nowTime = localTime.format(formatter);
        hour.getSelectionModel().select(Integer.parseInt(nowTime.substring(0,2)));
        minute.getSelectionModel().select(Integer.parseInt(nowTime.substring(2,4)));
        //创建网格布局
        GridPane task = new GridPane();
        Button determine = new Button("确定");
        Button cancel = new Button("取消");

        task.add(name, 0, 0, 2, 1);
        task.add(textField, 2, 0, 4, 1);
        task.add(time, 0, 1, 2, 1);
        task.add(hour, 2, 1, 2, 1);
        task.add(minute, 4, 1, 2, 1);
        task.add(determine, 0, 2, 2, 1);
        task.add(cancel, 4, 2,2,1);
        GridPane.setMargin(name,new Insets(30,0,0,20));
        GridPane.setMargin(textField,new Insets(30,0,0,10));
        GridPane.setMargin(time, new Insets(30,0,0,20));
        GridPane.setMargin(hour,new Insets(30,0,0,10));
        GridPane.setMargin(minute, new Insets(30,0,0,10));
        GridPane.setMargin(determine, new Insets(30,0,0,55));
        GridPane.setMargin(cancel,new Insets(30,0,0,0));


        task.setStyle("-fx-pref-width: 300px;" + "-fx-pref-height: 200px;" + "-fx-background-radius:15px;" +
                "-fx-background-image: url(/image/task/cool-background.png);" +
                "-fx-background-repeat: stretch;" + "fx-background-position: center;" + "-fx-background-color:  transparent;");


        task_stage.initOwner(RemindPane.remind_stage);
        Scene scene = new Scene(task);
        task_stage.setScene(scene);
        task_stage.show();
        drag_and_drop(task);


        //----------点击事件----------

        determine.setOnMouseClicked(mouseEvent -> {
            textValue = textField.getText();
            timeValue = hour.getValue().toString().substring(0,2)+minute.getValue().toString().substring(0,2);
            Label text = new Label(textValue);
            Label surplusTime = new Label();
            AnchorPane.setRightAnchor(surplusTime,1.0);
            surplusTime.setLayoutY(22);
            countdown = new Countdown(Integer.parseInt(timeValue),surplusTime,RemindPaneController.labelList.get(RemindPaneController.labelList.size()-1));
            text.setWrapText(true);
            text.setPrefWidth(180);
            RemindPaneController.labelList.get(RemindPaneController.labelList.size()-1).getChildren().addAll(text,surplusTime);

            //设置字体
            text.setFont(new Font("Microsoft YaHei", 15));
            surplusTime.setFont(new Font("Microsoft YaHei", 20));

            //设置黑体
            text.setStyle("-fx-font-style:italic;");
            surplusTime.setStyle("-fx-font-style:italic;");
            task_stage.close();
        });

        cancel.setOnMouseClicked(mouseEvent -> {
            RemindPaneController.taskListPane_copy.getChildren().remove(RemindPaneController.taskListPane_copy.getChildren().size()-1);
            RemindPaneController.y-=65;
            task_stage.close();
        });
    }


    public void drag_and_drop(GridPane task){
        task.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        task.setOnMouseDragged(event -> {
            task_stage.setX(event.getScreenX() - xOffset);
            task_stage.setY(event.getScreenY() - yOffset);
        });
    }
}
