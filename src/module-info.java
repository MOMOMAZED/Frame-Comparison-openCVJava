module ProjectIR {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires opencv;
	requires javafx.base;
	requires javafx.swing;
	requires java.base;
	requires webcam.capture;
	
	
	opens application to javafx.graphics, javafx.fxml;
}
