package application;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	final static String VERSION = "Project IR v.02";
	public final static boolean DEV_MODE = true;
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("controller/ProjectScene.fxml"));
			Scene scene = new Scene(root,1200,800);
			scene.getStylesheets().add(getClass().getResource("controller/application.css").toExternalForm());
			primaryStage.setTitle(VERSION);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	    System.out.println(DEV_MODE? "PROGRAM RUNNING IN DEVELOPER MODE [ " + VERSION + "]": "PROGRAM RUNNING [ " + VERSION + "]");	
		launch(args);
	}
}
