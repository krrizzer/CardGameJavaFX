
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CardGame extends Application {

	// ------------------Timer---------------------------------
	private AnimationTimer timer;
	private Label lblTime = new Label("0 .seconds");
	private int seconds;

	// ---------------------------------------
	private Image images[];
	private Image imagesCopy[];
	private Button buttons[];
	private int numImages = 8;
	private boolean firstClick = true;
	int secondclick = 0;
	int score = 0;
	int MaxScore = 0;
	private Button clickedButton;
	private int clickedButtonIndex;

	Media soundTrue = new Media(getClass().getResource("sounds/true.mp3").toString());
	MediaPlayer mediaPlayertrue = new MediaPlayer(soundTrue);

	Media soundFalse = new Media(getClass().getResource("sounds/false.mp3").toString());
	MediaPlayer mediaPlayerfalse = new MediaPlayer(soundFalse);
	
	Label MaxLabel = new Label(String.valueOf(MaxScore));
	Label ScoreLabel = new Label(String.valueOf(score));
	
	// this method load all the images.
	public void putImages() {

		images = new Image[numImages];

		for (int i = 0; i < images.length; i++) {

			images[i] = new Image(getClass().getResourceAsStream("images/" + (i + 1) + ".jpg"));

		}

	}
	
	// this method create buttons and images , then add the images to the buttons 
	public void setupButtons() {

		buttons = new Button[numImages * 2];

		imagesCopy = new Image[numImages * 2];

		
		System.arraycopy(images, 0, imagesCopy, 0, images.length);
		System.arraycopy(images, 0, imagesCopy, images.length, images.length);

		
		List<Image> list = Arrays.asList(imagesCopy);
		Collections.shuffle(list);

		imagesCopy = (Image[]) list.toArray();

		ImageView[] imageView = new ImageView[imagesCopy.length];

		for (int i = 0; i < imageView.length; i++) {
			imageView[i] = new ImageView(imagesCopy[i]);
			imageView[i].resize(100, 100);
			imageView[i].setFitHeight(100);
			imageView[i].setFitWidth(100);

		}

		for (int i = 0; i < imagesCopy.length; i++) {

			buttons[i] = new Button();
			buttons[i].setMaxSize(100, 100);

			buttons[i].setGraphic(imageView[i]);

			buttons[i].getGraphic().setVisible(false);

			int btnId = i;

			buttons[i].setOnAction(e -> {
				try {
					handleButtonsUnput(btnId);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});

		}

	}

	
	// this method adds the button to the pane
	public void addButtons(GridPane pane) {

		pane.getChildren().clear();

		int k = 0;

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {

				pane.add(buttons[k], i, j);
				k++;
			}

	}

	@Override

	public void start(Stage primaryStage) throws Exception {
		
		// --------------------------------------------------------------------------
		timer = new AnimationTimer() {
			private long lastTime = 0;
			@Override
			public void handle(long now) {
				if (lastTime != 0) {
					if (now > lastTime + 1_000_000_000) {
						seconds++;
						lblTime.setText(Integer.toString(seconds) + " .seconds");
						lastTime = now;
					}
				} else {
					lastTime = now;
				}
			}
			@Override
			public void stop() {
				super.stop();
				lastTime = 0;
				seconds = 0;
			}
		};

		
		//--------------------------------------------------------------------------------
		putImages();
		setupButtons();

		GridPane pane = new GridPane();
		pane.resize(60, 60);

		addButtons(pane);
		
		//-----------------------------Play button actions-------------------------------------
		ScoreLabel.setTextFill(Color.web("#0513a3"));
		MaxLabel.setTextFill(Color.web("#0513a3"));
		lblTime.setTextFill(Color.web("#FF0000"));
		Button playAgain = new Button("Play again");
		playAgain.setAlignment(Pos.BASELINE_CENTER);

		playAgain.setOnAction(e -> {

			if (score > MaxScore)
				MaxScore = score;
			MaxLabel.setText(String.valueOf(MaxScore));
			score = 0;
			ScoreLabel.setText(String.valueOf(score));
			

			setupButtons();

			addButtons(pane);
			timer.stop();
			lblTime.setText(Integer.toString(seconds) + " .seconds");

		});
		
		
		//--------------------------final Scene and panes-----------------------------------------
		

		HBox hBox = new HBox(20);
		HBox maxBox = new HBox(new Label("Max Score: "), MaxLabel);
		HBox scoreBox = new HBox(new Label("Score: "), ScoreLabel);
		HBox timeBox = new HBox(lblTime);
		hBox.getChildren().addAll(playAgain, maxBox, scoreBox, timeBox);
		hBox.setAlignment(Pos.CENTER);

		
		
		VBox vbox = new VBox(pane, hBox);
		vbox.setMaxSize(600, 600);

		
		Scene scene = new Scene(vbox, 465, 465);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Find the Cats");
		primaryStage.show();

	}

	public void handleButtonsUnput(int btnIndex) throws InterruptedException {

		Button b = buttons[btnIndex];
		
		
		if (firstClick) {

			firstClick = false;
			clickedButton = b;
			clickedButtonIndex = btnIndex;

			b.getGraphic().setVisible(true);
			
			timer.start();

		}

		else {
			if (imagesCopy[btnIndex].equals(imagesCopy[clickedButtonIndex]) && btnIndex != clickedButtonIndex) {

				mediaPlayertrue.play();
				mediaPlayertrue.seek(Duration.ZERO);

				b.getGraphic().setVisible(true);

				b.setDisable(true);
				clickedButton.setDisable(true);
				score++;
				
				ScoreLabel.setText(String.valueOf(score));
				if (score > MaxScore) {
					MaxLabel.setText(String.valueOf(score));
					
				}

			} else {
				b.getGraphic().setVisible(true);

				EventHandler<ActionEvent> eventHandler2 = e -> {

					b.getGraphic().setVisible(false);

					clickedButton.getGraphic().setVisible(false);

					mediaPlayerfalse.play();
					mediaPlayerfalse.seek(Duration.ZERO);

				};
				Timeline timeLine = new Timeline(new KeyFrame(Duration.seconds(0.2), eventHandler2));
				timeLine.play();

			}

			firstClick = true;
			
			if (score ==8)
				timer.stop();
				

		}

	}

	

	public static void main(String[] args) {

		launch(args);

	}
}