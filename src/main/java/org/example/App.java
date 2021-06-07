package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;


public class App extends Application{

    //---------------------------全局变量声明开始---------------------------

    //i为图片序列 armatureName_Idle_ + i
    int i=0;
    //设置播放速度快慢
    int speed = 42;
    //人物宽度,同时高度与之绑定
    int width = 150;
    //人物姓名
    public static String characterName = "cls";
    //标记，是否处于基建状态
    //boolean isBuild = false;
//    //标记，是否处于随机后第一次switch状态
//    boolean isRandom = false;
    //标记，是否处于interact动作中
    boolean isInteract = false;
    //显示器宽度
    double screen_width;
    //显示器高度
    double screen_height;
    //鼠标在界面中的位置
    double xOffset,yOffset;
    //人物图片
    ImageView imageView;
    //
    File[] interact,left_move,move,relax,sleep;
    //处理声音的类
    Voice voice = new Voice();
    //提醒窗口类
    RemindPane rp = new RemindPane();
    //定时器 控制图片不断显示 形成动画
    Timer timer = new Timer();
    //任务
    TimerTask task;
    //格式化数字
    DecimalFormat decimalFormat00 = new DecimalFormat("00");
    DecimalFormat decimalFormat000 = new DecimalFormat("000");

    //---------------------------全局变量声明结束---------------------------

    @Override
    public void start(Stage primaryStage) throws Exception {
        //透明窗口栏
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        //不显示任务栏图标,不能与透明窗口一起使用,导致美观下降
        //primaryStage.initStyle(StageStyle.UTILITY);
        //关闭后不退出
        Platform.setImplicitExit(false);
        //窗口总是在最前面
        primaryStage.setAlwaysOnTop(true);
        //创建镜像图片
        //TODO
        //mk_flipImage("ash/ash_left_move");
        voice.interact_talk("hello");

        AnchorPane mainPane = new AnchorPane();
        //
        Label label = new Label();
        imageView = new ImageView(getCharacterImagePath(characterName+"_relax/armatureName_Relax_000"));
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        label.setGraphic(imageView);
        mainPane.getChildren().add(label);
        mainPane.setStyle("-fx-background-position: center; " + "-fx-background-repeat: stretch;" + "-fx-background-color:  transparent;");
        mainPane.setPrefWidth(200);
        mainPane.setPrefHeight(200);
        Scene scene = new Scene(mainPane);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
        //设置任务栏图标
        primaryStage.getIcons().add(new Image(getImagePath("icon/icon")));
        //设置在屏幕中央
        primaryStage.centerOnScreen();



        //--------------------获取一些必要的参数值--------------------
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        screen_width = screenRectangle.getWidth();
        screen_height = screenRectangle.getHeight();
        //---------------------------结束---------------------------


        //------------------------------系统托盘设置开始------------------------------
        SystemTray systemTray = SystemTray.getSystemTray();
        //与this.getClass().getClassLoader().getResource("")相同
        URL img_url = App.class.getClassLoader().getResource("image/icon/icon.png");
        assert img_url != null;
        java.awt.Image icon = new ImageIcon(img_url).getImage();
        PopupMenu popupMenu = new PopupMenu();
        MenuItem menuItem_close = new MenuItem("close");
        MenuItem menuItem_show = new MenuItem("show");
        MenuItem menuItem_hide = new MenuItem("hide");
        MenuItem menuItem_remind = new MenuItem("remind");
        popupMenu.add(menuItem_close);
        popupMenu.add(menuItem_show);
        popupMenu.add(menuItem_hide);
        popupMenu.add(menuItem_remind);
        TrayIcon trayIcon = new TrayIcon(icon,characterName,popupMenu);
        //设置系统图片自动化大小
        trayIcon.setImageAutoSize(true);
        systemTray.add(trayIcon);
        //------------------------------系统托盘设置结束------------------------------

        //------------------------------一些方法调用(初始化一些功能)------------------------------
        remind_to_relax();
        launch_remind();
        interact = get_imagesFiles("interact");
        left_move = get_imagesFiles("left_move");
        move = get_imagesFiles("move");
        relax = get_imagesFiles("relax");
        sleep = get_imagesFiles("sleep");
        task_relax();
        timer.schedule(task, 1000, speed);

        //------------------------------监听事件开始------------------------------

        //=========awt组件监听开始=========
        menuItem_close.addActionListener(e -> {
            Platform.runLater(() -> {
                Platform.setImplicitExit(true);
                primaryStage.close();
                RemindPane.remind_stage.close();
            });
            systemTray.remove(trayIcon);
        });

        menuItem_hide.addActionListener(e -> Platform.runLater(() -> {
            Platform.setImplicitExit(false);
            primaryStage.hide();
        }));

        menuItem_remind.addActionListener(e -> Platform.runLater(()-> RemindPane.remind_stage.show()));

        menuItem_show.addActionListener(e -> Platform.runLater(primaryStage::show));
        //=========awt组件监听开始=========



        /**
         *处理拖拽事件
         *
         */
        mainPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        mainPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        /**
         *处理点击事件
         *
         */
        mainPane.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            if (mouseEvent.getClickCount()==2){
                //task_attack_idle();
                //修改图片大小
                //imageView.setFitWidth(width);
                //timer.schedule(task, 0, speed);
            }else if (mouseEvent.getClickCount()==1){
                if (y<50){
                    task_interact();
                    //修改图片大小
                    imageView.setFitWidth(width);
                    i=0;
                    timer.schedule(task, 0, speed);
                }else if (y<70){
                    voice.interact_talk("cls_talk2");
                }
            }
            if (mouseEvent.getClickCount()==2){
                task_relax();
                //修改图片大小
                imageView.setFitWidth(width);
                timer.schedule(task, 0, speed);
            }

        });


        //------------------------------监听事件结束------------------------------

        //随机事件
        random_event02(primaryStage);

    }

    int relax_count;
    public void task_relax(){
        relax_count++;
        if (relax_count%120==0)voice.interact_talk("relax");
        timer.cancel();
        if (task!=null)task.cancel();
        timer = new Timer();
        task = new TimerTask(){
            @Override
            public void run() {
                if (i>120)i=0;
                imageView.setImage(new Image(relax[i].toURI().toString()));
                i++;
            }
        };
    }

    public void task_attack_idle(){
        timer.cancel();
        if (task!=null)task.cancel();
        timer = new Timer();
        task = new TimerTask(){
            @Override
            public void run() {
                if (i>120)i=0;
                imageView.setImage(new Image(getCharacterImagePath(characterName+"_attack_idle/armatureName_Idle_"+ decimalFormat000.format(i))));
                i++;
            }
        };
    }

    //timer的间隔需要设置为42
    public void task_right_move(Stage primaryStage){
        timer.cancel();
        task.cancel();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (i>60)i=0;
                imageView.setImage(new Image(move[i].toURI().toString()));
                double x = primaryStage.getX();
                primaryStage.setX(x+1);
                i++;
                if (x+127>screen_width){
                    task_relax();
                    timer.schedule(task, 0, speed);
                }
            }
        };
    }

    public void task_left_move(Stage primaryStage){
        timer.cancel();
        task.cancel();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (i>60)i=0;
                imageView.setImage(new Image(left_move[i].toURI().toString()));
                double x = primaryStage.getX();
                primaryStage.setX(x-1);
                i++;
                if (x+1<-50){
                    task_relax();
                    timer.schedule(task, 0, speed);
                }
            }
        };
    }


    public void task_interact(){
        isInteract = true;
        timer.cancel();
        task.cancel();
        timer = new Timer();
        voice.interact_talk("touch");
        task = new TimerTask() {
            @Override
            public void run() {
                imageView.setImage(new Image(interact[i].toURI().toString()));
                imageView.setFitWidth(width);
                i++;
                if (i>60){
                    isInteract = false;
                    task_relax();
                    timer.schedule(task, 0, speed);
                }
            }
        };
    }

    public void random_event02(Stage primaryStage){
        Random random = new Random();
        //随机任务
        TimerTask random_task;
        //局部变量定时器
        Timer randomTimer = new Timer();
        random_task = new TimerTask() {
            @Override
            public void run() {
                switch (random.nextInt(4)){
                    case 0:
                        task_right_move(primaryStage);
                        //修改图片大小
                        imageView.setFitWidth(width);
                        timer.schedule(task,0,speed);
                        break;
                    case 1:
                        task_left_move(primaryStage);
                        //修改图片大小
                        imageView.setFitWidth(width);
                        timer.schedule(task,0,speed);
                        break;
                    default:
                        task_relax();
                        imageView.setFitWidth(width);
                        timer.schedule(task, 0,speed);
                        break;
                }
            }
        };
        //15120=120*42*3
        randomTimer.schedule(random_task,15120,15120+random.nextInt(4)*15120);
    }

    //生成水平翻转图片
//    public void mk_flipImage(String fileName){
//        File file = new File(this.getClass().getClassLoader().getResource("image").getPath()+"/"+fileName);
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        //TODO 将cls_move泛化
//        File from_file = new File(this.getClass().getClassLoader().getResource("image/ash/ash_move").getPath());
//        File[] files = from_file.listFiles();
//        Mat targetImage;
//        int k=0;//计数所用
//        for (File f : files) {
//            targetImage = Imgcodecs.imread(f.toString(),-1);
//            Core.flip(targetImage, targetImage, 1);
//            imwrite(file.getPath()+"/armatureName_leftMove_"+ decimalFormat000.format(k) +".png",targetImage);
//            k++;
//        }
//    }

    //定时提醒休息
    public void remind_to_relax(){
        Timer timer_1 = new Timer();
        TimerTask timerTask_1 = new TimerTask() {
            @Override
            public void run() {
                voice.interact_talk("cls_go");
            }
        };
        //30分钟一休息
        timer_1.schedule(timerTask_1, 60000*30, 60000*30);
    }

    public void launch_remind(){
        rp.create_remindWindow();
    }

    public String getCharacterImagePath(String imageName){
        return this.getClass().getClassLoader().getResource("image/"+characterName+"/"+imageName+".png").toString();
    }
    public String getImagePath(String imageName){
        return this.getClass().getClassLoader().getResource("image/"+imageName+".png").toString();
    }

    //todo todo todo
    public File[] get_imagesFiles(String actionName){
        String path = this.getClass().getClassLoader().getResource("image/"+characterName+"/"+characterName+"_"+actionName).getPath();
        if (path.contains("remind-1.0-SNAPSHOT.jar!/")){
            path = path.replace("remind-1.0-SNAPSHOT.jar!/","classes/");
            path = path.substring(6);
        }
        File taskImage_file = new File(path);
        return taskImage_file.listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".png"));
    }
}