package controllers;

import java.util.Random;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Affine;
import models.Particle;

public class MainController {
	
	@FXML private AnchorPane canvasPane;
	@FXML private VBox editPanel;
	
	@FXML private TextField posXField, posYField, velXField, velYField, massField, nameField;
	@FXML private ColorPicker colorSelector;
	
	@FXML private ListView<Particle> particleListView;
	
	private ObservableList<Particle> particles;
	private Particle selectedParticle;

	@FXML private Canvas canvas;
	
	@FXML private Slider zoomSlider;
	private double zoom = 1;
	private double dragLastX, dragLastY, dragAmountX, dragAmountY;
	
	@FXML
	private void initialize() {
		
		particles = FXCollections.observableArrayList();
		
		particleListView.setItems(particles);
		particleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			selectedParticle = newValue;
			updateEditingPane();
		});
		
		editPanel.setVisible(false);
		
		configureDoubleField(posXField, true);
		configureDoubleField(posYField, true);
		configureDoubleField(massField, false);
		configureDoubleField(velXField, true);
		configureDoubleField(velYField, true);
		
		posXField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setPositionX(Double.parseDouble(newValue));
			drawParticles();
		});
		posYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setPositionY(Double.parseDouble(newValue));
			drawParticles();
		});
		
		velXField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setVelocityX(Double.parseDouble(newValue));
			drawParticles();
		});
		velYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setVelocityY(Double.parseDouble(newValue));
			drawParticles();
		});
		
		massField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setMass(Double.parseDouble(newValue));
			drawParticles();
		});
		
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null) {
				selectedParticle.setName(newValue);
				particleListView.refresh();
			}
			drawParticles();
		});
		
		colorSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setColor(newValue);
			drawParticles();
		});
		
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());
		canvas.getGraphicsContext2D().translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
		
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
			canvas.getGraphicsContext2D().translate(0, newValue.doubleValue() / 2 - oldValue.doubleValue() / 2);
			drawParticles();
		});
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
			canvas.getGraphicsContext2D().translate(newValue.doubleValue() / 2 - oldValue.doubleValue() / 2, 0);
			drawParticles();
		});
		
		canvas.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			canvas.setClip(new Rectangle(newValue.getMinX(), newValue.getMinY(), newValue.getWidth(), newValue.getHeight()));
		});
		
		zoomSlider.setMax(30);
		zoomSlider.setMin(-20);
		
		zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			double zoom = 1;
			zoom *= Math.pow(1.1, (newValue.doubleValue() - oldValue.doubleValue()));
				
			this.zoom *= zoom;
			Affine t = new Affine();
			t.setMxx(zoom);
			t.setMyy(zoom);
			canvas.getGraphicsContext2D().transform(t);
			drawParticles();
		});
		
	}
	
	private void configureDoubleField(TextField field, boolean negative) {
		
		if (negative)
			field.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					newValue = newValue.replace(",", ".");
					if (newValue.equals("-") || newValue.equals(""))
						return;
	                Double.parseDouble(newValue);
				} catch (NumberFormatException exc) {
					System.out.println(exc);
					((StringProperty) observable).setValue(oldValue);
				}
			});
		else
			field.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				newValue = newValue.replace(",", ".");
				if (newValue.equals(""))
					return;
                Double.parseDouble(newValue);
			} catch (NumberFormatException exc) {
				System.out.println(exc);
				((StringProperty) observable).setValue(oldValue);
			}
		});
	}
	
	private void updateEditingPane() {
		editPanel.setVisible(selectedParticle != null);
		
		if (selectedParticle == null)
			return;
		
		nameField.setText(selectedParticle.getName());
		colorSelector.setValue(selectedParticle.getColor());
		
		posXField.setText(""+selectedParticle.getPositionX());
		posYField.setText(""+selectedParticle.getPositionY());
		velXField.setText(""+selectedParticle.getVelocityX());
		velYField.setText(""+selectedParticle.getVelocityY());
		
		massField.setText(""+selectedParticle.getMass());
		
	}
	
	private void drawParticles() {
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		double width = canvas.getWidth();
		double height = canvas.getHeight();
		
		double leftLimit = (-width / 2 - dragAmountX) / zoom;
		double rightLimit = (width / 2 - dragAmountX) / zoom;
		double bottomLimit = (height / 2 - dragAmountY) / zoom;
		double topLimit = (-height / 2 - dragAmountY) / zoom;

		gc.clearRect(leftLimit, topLimit, width / zoom, height / zoom);
		
		gc.setStroke(Paint.valueOf("#303050"));
		gc.strokeLine(0, topLimit, 0, bottomLimit);
		gc.strokeLine(leftLimit, 0, rightLimit, 0);
		
		for (Particle p : particles) {
			gc.setFill(p.getColor());
			gc.setStroke(p.getColor());
			double radius = 5 + p.getMass() / 100;
			gc.fillOval(p.getPositionX() - radius / 2, p.getPositionY() - radius / 2, radius, radius);
			
			double x1 = p.getPositionX(), x2 = x1 + p.getVelocityX();
			double y1 = p.getPositionY(), y2 = y1 + p.getVelocityY();
			
			double phi= Math.toRadians(40);
			double barb = 1;
			double dx = x2 - x1;
            double dy = y2 - y1;
            double theta = Math.atan2( dy, dx );
            double x, y, rho = theta + phi;

		    gc.setLineWidth(0.5);
		    gc.setLineCap(StrokeLineCap.ROUND);
		    gc.strokeLine(p.getPositionX(), p.getPositionY(), p.getPositionX() + p.getVelocityX(), 
		    		p.getPositionY() + p.getVelocityY());

            x = x2 - barb * Math.cos( rho );
            y = y2 - barb * Math.sin( rho );
            gc.strokeLine(x2, y2, x, y);
            rho = theta - phi;
            x = x2 - barb * Math.cos( rho );
            y = y2 - barb * Math.sin( rho );
            gc.strokeLine(x2, y2, x, y);
		    gc.setLineWidth(1);
			
		}
		
	}
	
	
	@FXML
	private void mouseEnteredCanvas() {
		System.out.println("hello");
	}
	
	@FXML
	private void enteredAnchor() {
		System.out.println("hi2");
	}
	
	@FXML
	private void mousePressedCanvas(MouseEvent evt) {
		dragLastX = evt.getX();
		dragLastY = evt.getY();
	}
	
	@FXML
	private void mouseDraggedCanvas(MouseEvent evt) {
		dragAmountX += evt.getX() - dragLastX;
		dragAmountY += evt.getY() - dragLastY;
		

		Affine t = new Affine();
		t.setTx((evt.getX() - dragLastX) / zoom);
		t.setTy((evt.getY() - dragLastY) / zoom);
		canvas.getGraphicsContext2D().transform(t);
		drawParticles();
		
		dragLastX = evt.getX();
		dragLastY = evt.getY();
	}
	
	@FXML
	private void scrolledCanvas(ScrollEvent evt) {

		zoomSlider.setValue(zoomSlider.getValue() + evt.getDeltaY() / 40);
		
//		double thisZoom = 1;
//		if (evt.getDeltaY() > 0)
//			thisZoom *= evt.getDeltaY() / 30;
//		else
//			thisZoom /= -evt.getDeltaY() / 30;
//		
//		if ((zoom < 0.1 && thisZoom < 1) || 
//			(zoom > 10 && thisZoom > 1))
//			thisZoom = 1;
//		zoom *= thisZoom;
//		
//		
//		Affine t = new Affine();
//		t.setMxx(thisZoom);
//		t.setMyy(thisZoom);
//		canvas.getGraphicsContext2D().transform(t);
//		
//		drawParticles();
	}
	
	@FXML
	private void createParticle() {
		selectedParticle = new Particle("Particle " + (particles.size() + 1));
		
		Random rand = new Random();
		selectedParticle.setColor(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		
		particles.add(selectedParticle);
		
		particleListView.getSelectionModel().select(selectedParticle);
		updateEditingPane();
		drawParticles();
	}
	
	@FXML
	private void deleteSelected() {
		System.out.println("hi");
		if (selectedParticle == null)
			return;
		
		particles.remove(selectedParticle);
		updateEditingPane();
	}

	// =========================================
	// Menu Items
	// =========================================
	
	@FXML
	private void openFile() {
		System.out.println("open file");
	}
	
	@FXML
	private void saveFile() {
		
	}
	
	@FXML
	private void newFile() {
		
	}
	
	@FXML
	private void exit() {
		
	}
	
	@FXML 
	private void startSimulation() {
		
	}
	
}
