package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


import javafx.scene.image.Image;


public class ProjectIRController {
	@FXML
	private Button start_btn;
	@FXML
	private ImageView currentFrame;
	@FXML
	public CheckBox show_fps;
	@FXML
	public Label fps_label;
	@FXML
	private CheckBox grey_scale;
	@FXML
	private Label invert_label;
	@FXML
	private ImageView find_image;
	@FXML
	private TextField file_path;
	@FXML
	private Button compare_frame;
	@FXML
	private Button comapre_image;
	
	public String fps_label_str;
	
	private ScheduledExecutorService timer;
		
	private static int cameraId = 0;
	
	private VideoCapture capture = new VideoCapture();

	private boolean cameraActive = false;
	
	private boolean greyScale = false;
	
	private boolean invertCamera = false;
	
	private boolean takePhoto = false;
	
	@FXML
	public Label greyscale_label;
	
	final public int FPS = 30;
	
	private double imageMatchPercentage = 0;
	
	private static boolean showFPS = false;
	
	private String currentFramePath = "src/application/images/" + "currentFrame001" + ".jpg"; //save current frame to this file every time, this is how we will use our comparison functions
		
	//public static final boolean DEV_MODE = true;
	

	

	
	@FXML
	protected void startCamera(ActionEvent event) { 
		/*
		 * OLD WEBCAM USED IN v.01
		 * if(isCamActive) { System.out.println("Webcam is already running"); return; }
		 * if (webcam != null) { System.out.println("Webcam: " + webcam.getName());
		 * WebcamPanel panel = new WebcamPanel(webcam);
		 * webcam.setViewSize(WebcamResolution.VGA.getSize());
		 * panel.setFPSDisplayed(true); panel.setImageSizeDisplayed(true);
		 * panel.setMirrored(true);
		 * 
		 * window.add(panel); window.setResizable(true);
		 * window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); window.pack();
		 * window.setVisible(true); isCamActive = true; } else {
		 * System.out.println("No webcam detected"); }
		 */
		
		if (!this.cameraActive)
		{
			ProgramLog(0, "Attemting to start Camera");
			// start the video capture
			this.capture.open(cameraId);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				ProgramLog(0, "Camera started successfully");
				this.cameraActive = true;
				
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						Mat frame = grabFrame();
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(currentFrame, imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, FPS, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.start_btn.setText("Stop Camera");
			}
			else
			{
				// log the error
				ProgramLog(1, "Impossible to open the camera connection...");
			}
		}
		else
		{
			ProgramLog(0, "Camera Stopped");
			this.cameraActive = false;
			this.start_btn.setText("Start Camera");
			//ProgramLog(0,"Camera :" + this.capture.getBackendName()); i was trying to get camera name, dk the func for it tho sadge
			
			// stop the timer
			this.stopAcquisition();
		}
		
	}
	
	
	private Mat grabFrame()
	{
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					if(!invertCamera)
					Core.flip(frame, frame, 1);
					if(greyScale)
					Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					Imgcodecs.imwrite(currentFramePath,
                             frame);
					if(takePhoto)
					{
						takePhoto = false;
						ProgramLog(0, "Attempting to take picture");
						String name = "capture";
						name = JOptionPane.showInputDialog("Save image as?");	
						String path = "src/application/images/" + name + ".jpg";
						Imgcodecs.imwrite(path,
                                 frame);
						 if(name == null)
						 {
							 ProgramLog(2, "User Abandoned Picture");
							 //need to implement proper handling for this
						 }
						 if(!(name.toCharArray().length > 0))
						 {
							 ProgramLog(2, "Name for file is blank");
						 }
						 ProgramLog(0, "Picture saved successfully at " + path);
						 	 						
						 int newFindImage = 0;
						 Object[] options = {"yes", "no"};
						 newFindImage = JOptionPane.showOptionDialog(null, "Do you want to update your find image with this picture?",
					                "Update Find Image",
					                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
						 if(newFindImage == 0)
						 {
						 Image imageToFind = new Image("file:C:\\Users\\Moham\\eclipse-workspace\\ProjectIR\\src\\application\\images\\"
						 		+ "" + name + ".jpg"); //this works to find any image, but the file length is long and cringe		 
						 //Image imageToFind = Utils.mat2Image(frame);		 // this works as a way to get the current frame as the new image to find
						 file_path.setText(imageToFind.getUrl());
				         updateImageView(find_image, imageToFind);
				         ProgramLog(0, "Update Image, Path: " + imageToFind.getUrl());
						 }

						 
					}

				}
				
			}
			catch (Exception e)
			{
				// log the error
				ProgramLog(1,"Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	private void stopAcquisition()
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(FPS, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				ProgramLog(1, "Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);

	}
	
	protected void setClosed()
	{
		this.stopAcquisition();
	}
	
	@FXML
	protected void takePhoto(ActionEvent event) {
		takePhoto = true;
	}
	
	
	//Check BOxes
	@FXML
	protected void showFPS(ActionEvent event) { 
		 if(showFPS)
		 {
			showFPS = false;
			ProgramLog(0, "Disable FPS");
			fps_label.setText("");
		 }
		 else
		 {
			showFPS = true;
			ProgramLog(0, "Enable FPS");
			fps_label.setText("Current FPS: " + FPS);


		 }

	}
	
	@FXML
	protected void greyScale(ActionEvent event)
	{
		if(greyScale)
		{
			greyScale = false;
			ProgramLog(0, "Disable Grey Scale");
			greyscale_label.setText("Grey Scale is Disabled");
		}
		else
		{
			greyScale = true;
			ProgramLog(0, "Enable Grey Scale");
			greyscale_label.setText("Grey Scale is Enabled");

		}
	}
	
	@FXML
	protected void invertCamera(ActionEvent event)
	{
		if(invertCamera)
		{
			invertCamera = false;
			ProgramLog(0, "Invert Camera Off");
			invert_label.setText("Invert Camera is Disabled");
		}
		else
		{
			invertCamera = true;
			ProgramLog(0, "Invert Camera On");
			invert_label.setText("Invert Camera is Enabled");
		}
	}
	//CHECK BOXES END
	
	//LOAD IMAGE
	@FXML
	protected void loadImage(ActionEvent event)
	{
		Object[] options = {"From PC", "From Web"};
		 int newFindImage = 0;
		 newFindImage = JOptionPane.showOptionDialog(null, "Load Image From?",
	                "Load Image",
	                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		 if(newFindImage == 1)
		 {
			 String urlLink = "";
			 urlLink = JOptionPane.showInputDialog("Enter image URL:");
			 file_path.setText(urlLink);
			 Image imageToFind = new Image(file_path.getText());
	         updateImageView(find_image, imageToFind);
	         ProgramLog(0, "Updating Image from Text Field, Path: " + imageToFind.getUrl());
		 }
		if(newFindImage == 0)
		{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "JPG,PNG,JPEG Images Only, dont be an ape", "jpg", "png", "jpeg");
			    chooser.setFileFilter(filter);
        int status = chooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }

            String fileName = chooser.getSelectedFile().getAbsolutePath();
            String[] imageFiles = {".jpg", ".png", ".jpeg"}; //current supported images
            var isSupported = false;
            for (var i = 0; i < imageFiles.length; i++)
            {
            	if(fileName.contains(imageFiles[i]))
            	{
            		isSupported = true;
            	}
            }
            if(!isSupported)
            {
          	  ProgramLog(2, "User attempted to load a non supported file");
            }
            Image imageToFind = new Image("file:" + fileName);
            updateImageView(find_image, imageToFind);
            file_path.setText("file:" + fileName);
            ProgramLog(0, "Find Image path updated -> " + fileName);
        }
        }
	}
	
	//TEXT FIELD
	@FXML
	protected void updateFindImage(ActionEvent event)
	{
		//This textfield was disabled, and this function is useless to have
		//String imageToFindPath = "file:C:\\Users\\Moham\\eclipse-workspace\\ProjectIR\\src\\application\\";
		// Image imageToFind = new Image("file:C:\\Users\\Moham\\eclipse-workspace\\ProjectIR\\src\\application\\download.jpg"); //this works to find any image, but the file length is long and cringe
		 Image imageToFind = new Image(file_path.getText());
         updateImageView(find_image, imageToFind);
         ProgramLog(0, "Updating Image from Text Field, Path: " + imageToFind.getUrl());
	}
	
	/**
	 * Program Log, logs the corresponding message and threat level to console
	 * @param threatlvl(int) indicates the threat level of the log (0-Informational Log, 1-Error Log, 2-Caution Log)
	 * @param message(string) prints the actual log to the console.
	 */
	void ProgramLog(int threatlvl, String message)
	{
		if(Main.DEV_MODE)
		{
		String status = "unknown";
		switch (threatlvl) {
		case 0: status = "Info Log"; break;
		case 1: status = "Error Log"; break;
		case 2: status = "Caution Log"; break;
		}
		System.out.println(status + " -> " + message);
		}
	}
	
	//Comparing Image
	//Handle the button event here, and use the corresponding class "Comparing Frame" to do the actual image comparison
	@FXML
	protected void compareImageToFrame(ActionEvent event) throws IOException
	{
		ProgramLog(0, "Compare frame clicked");
		ComparingFrame compare = new ComparingFrame();
		imageMatchPercentage = compare.compareImageToFrameColor(currentFramePath, find_image.getImage().getUrl()); //Using the file url instead of the frame so that the dimensions will be the same for comparison
		ProgramLog(0, "Match Percentage is " + imageMatchPercentage);
	}
	
	
	
}
	
