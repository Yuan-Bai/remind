package org.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.example.RemindPane;
import org.example.TaskPane;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RemindPaneController {
    //根node
    public AnchorPane root;
    //最小化按钮
    public Button minButton;
    //窗口标题
    public Label title;
    //任务列表画布
    public AnchorPane taskListPane;
    //添加任务按钮
    public Button addButton;
    //任务画布数组集合
    public static List<AnchorPane> labelList = new ArrayList<AnchorPane>();
    //taskListPane副本
    public static AnchorPane taskListPane_copy;
    //
    public static int y=-65;

    private File[] files;
    private List<File> fileList = new ArrayList<>();
    private Random random = new Random();
    private TaskPane taskPane;
    private String path;

    @FXML
    private void initialize(){
        //此处参数没有用
        taskPane = new TaskPane(files);
        taskListPane_copy = taskListPane;
        get_images();
    }
    @FXML
    private void enter(){
        minButton.setStyle("-fx-background-image: url(image/icon/min_image02.png);");
    }
    @FXML
    public void addEnter(MouseEvent mouseEvent) {
        addButton.setStyle("-fx-background-image: url(image/icon/add02.png);");
    }

    @FXML
    private void exited(){
        minButton.setStyle("-fx-background-image: url(image/icon/min_image01.png);");
    }
    @FXML
    public void addExited(MouseEvent mouseEvent) {
        addButton.setStyle("-fx-background-image: url(image/icon/add01.png);");
    }
    @FXML
    private void create_task(){
        get_images();
        if(y>=650)return;
        AnchorPane task = new AnchorPane();
        labelList.add(task);
        taskPane.createTaskPane();
        path = files[random.nextInt(files.length)].getPath();
        path = path.substring(path.indexOf("task_image")+11);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                task.setStyle("-fx-background-image: url(image/task_image/"+path+");");
                task.getStyleClass().add("task");
                task.setTranslateY(y+=65);
                taskListPane.getChildren().add(task);
            }
        });
        task.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount()==2){
                ObservableList<Node> observableList = RemindPaneController.taskListPane_copy.getChildren();
                int order = observableList.indexOf(task);
                observableList.remove(order);
                int size = observableList.size();
                for (;order<size;order++){
                    observableList.get(order).setLayoutY(observableList.get(order).getLayoutY()-65);
                }
                y-=65;
            }
        });
    }

    @FXML
    private void minimize(){
        RemindPane.remind_stage.close();
    }

    public void get_images(){
        String path = this.getClass().getClassLoader().getResource("image/task_image").getPath();
        if (path.contains("remind-1.0-SNAPSHOT.jar!/")){
            path = path.replace("remind-1.0-SNAPSHOT.jar!/","classes/");
            path = path.substring(6);
        }
        File taskImage_file = new File(path);
        files = taskImage_file.listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".png"));
    }


    public String getImagePath(String imageName){
        return this.getClass().getClassLoader().getResource("image/"+imageName+".png").toString();
    }
}
