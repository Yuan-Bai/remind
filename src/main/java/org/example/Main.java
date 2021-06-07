package org.example;


import org.opencv.core.Core;

import java.awt.*;

import static javafx.application.Application.launch;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(App.class,args);
    }
}