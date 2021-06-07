package org.example;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.controller.RemindPaneController;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class Countdown {
    AnchorPane anchorPane;
    public Countdown(int targetTime, Label surplusTime, AnchorPane anchorPane){
        dosome(targetTime,surplusTime);
        this.anchorPane = anchorPane;
    }

    int totalSec;
    Timer timer;
    TimerTask timerTask;
    public void dosome(int targetTime,Label surplusTime){
        timer = new Timer();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        LocalTime localTime = LocalTime.now();
        int nowTime = Integer.parseInt(localTime.format(formatter));
        totalSec = targetTime/100*3600+targetTime%100*60-nowTime/10000*3600-nowTime%10000/100*60-nowTime%100;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                totalSec--;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        surplusTime.setText(totalSec/3600 + ":" + totalSec%3600/60 + ":" + totalSec%3600%60);
                    }
                });
                if (totalSec==1)voice.interact_talk("assistant");
                else if (totalSec<=0)timeout();

            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    Voice voice = new Voice();
    public void timeout(){
        timer.cancel();
        timerTask.cancel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<Node> observableList = RemindPaneController.taskListPane_copy.getChildren();
                int order = observableList.indexOf(anchorPane);
                observableList.remove(anchorPane);
                int size = observableList.size();
                for (;order<size;order++){
                    observableList.get(order).setLayoutY(observableList.get(order).getLayoutY()-65);
                }
            }
        });
        RemindPaneController.y-=65;
    }
}
