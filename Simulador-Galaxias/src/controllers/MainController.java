package controllers;

import java.io.File;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import models.Particle;
import models.Simulation;

public class MainController {
	
	private Simulation simulation;
	
	@FXML private AnchorPane canvasPane;
	@FXML private VBox editPanel;
	
	@FXML private TextField posXField, posYField, velXField, velYField, massField, nameField;
	@FXML private ColorPicker colorSelector;
	
	@FXML private ListView<Particle> particleListView;
	private Particle selectedParticle;

	@FXML private Canvas canvas;
	
	@FXML private Slider zoomSlider;
	
	private double zoom = 1;
	private double dragLastX, dragLastY, dragAmountX, dragAmountY;
	
	private boolean showGrid = true, showVectors = true, showGravity;
	
	@FXML
	private void initialize() {
		
		simulation = new Simulation();
		
		particleListView.setItems(simulation.getParticles());
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
			Affine t = new Affine();
			t.setTy((newValue.doubleValue() / 2 - oldValue.doubleValue() / 2) / zoom);
			canvas.getGraphicsContext2D().transform(t);
			drawParticles();
		});
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
			Affine t = new Affine();
			t.setTx((newValue.doubleValue() / 2 - oldValue.doubleValue() / 2) / zoom);
			canvas.getGraphicsContext2D().transform(t);
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
		double topLimit = (-height / 2 - dragAmountY) / zoom;

		gc.clearRect(leftLimit, topLimit, width / zoom, height / zoom);
		
		if (showGrid)
			drawGrid();
		
		for (Particle p : simulation.getParticles()) {
			gc.setFill(p.getColor());
			gc.setStroke(p.getColor());
			double radius = 1 + p.getMass() / 100;
			gc.fillOval(p.getPositionX() - radius / 2, p.getPositionY() - radius / 2, radius, radius);
			
			if (showVectors)
				drawVelocityVector(gc, p);
		}
		
	}
	
	private void drawGrid() {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double width = canvas.getWidth();
		double height = canvas.getHeight();
		
		double leftLimit = (-width / 2 - dragAmountX) / zoom;
		double rightLimit = (width / 2 - dragAmountX) / zoom;
		double bottomLimit = (-height / 2 - dragAmountY) / zoom;
		double topLimit = (height / 2 - dragAmountY) / zoom;
		
		double actualWidth = width / zoom;
		double step = 1;
		if (Math.round(actualWidth / step) > 20)
			while (Math.round(actualWidth / step) > 20)
				step *= 2;
		else 
			while (Math.round(actualWidth / step) < 20)
				step /= 2;
		
		gc.setStroke(Paint.valueOf("#303050"));
		gc.setLineWidth(1 / zoom);
		int lines = (int) (actualWidth / step);
		int deslocX = (int) Math.ceil(dragAmountX / zoom / step);
		int deslocY = (int) Math.ceil(dragAmountY / zoom / step);
		
		for (int i = -lines; i < lines; i++) {
			double amount = (i - deslocX) * step;
			gc.strokeLine(amount, topLimit, amount, bottomLimit);
			amount = (i - deslocY) * step;
			gc.strokeLine(leftLimit, amount, rightLimit, amount);
		}
		
		double scaleXStart = rightLimit - 30 / zoom;
		double scaleYStart = bottomLimit + 30 / zoom;
		double scaleWidth = step , scaleHeight = 5 / zoom;
		gc.setFill(Color.valueOf("#101030").deriveColor(0, 1, 20, 0.7));
		gc.setStroke(Color.WHITESMOKE);
		gc.strokeLine(scaleXStart - scaleWidth, scaleYStart, scaleXStart - scaleWidth, scaleYStart + scaleHeight);
		gc.strokeLine(scaleXStart, scaleYStart, scaleXStart, scaleYStart + scaleHeight);
		gc.strokeLine(scaleXStart - scaleWidth, scaleYStart, scaleXStart, scaleYStart);
		
	}
	
	private void drawVelocityVector(GraphicsContext gc, Particle p) {
		double radius = 1 + p.getMass() / 100;
		
		double arctan = Math.atan2( p.getVelocityY(), p.getVelocityX() );
		
		double x1 = p.getPositionX() + (radius/2) *  Math.cos(arctan), x2 = x1 + p.getVelocityX();
		double y1 = p.getPositionY() + (radius/2) *  Math.sin(arctan), y2 = y1 + p.getVelocityY();
		
	    gc.setLineWidth(0.05 * radius);
	    gc.setLineCap(StrokeLineCap.ROUND);
	    gc.strokeLine(x1, y1, x2, y2);
	    
	    if (p.getVelocityX() == 0 && p.getVelocityY() == 0) {
		    gc.setLineWidth(1);
			return;
	    }
	    
		double x[] = {0,0,0};
		double y[] = {0,0,0};
        x[0] = x2 + 0.10 * radius * Math.cos(arctan);
        y[0] = y2 + 0.10 * radius * Math.sin(arctan);
        double phi = 1;
		double dx = x[0] - x1;
        double dy = y[0] - y1;
        double size = Math.sqrt(dx*dx+dy*dy);
        double barb = Math.min(Math.max(1, radius/10),	size/3);
        double theta = Math.atan2( dy, dx );
        double rho = theta + phi;
        
        x[1] = x2 - barb * Math.cos( rho );	
        y[1] = y2 - barb * Math.sin( rho );
        //gc.strokeLine(x2, y2, x, y);
        rho = theta - phi;
        x[2] = x2 - barb * Math.cos( rho );
        y[2] = y2 - barb * Math.sin( rho );
        //gc.strokeLine(x2, y2, x, y);
        
        gc.fillPolygon(x,y,3);
	    gc.setLineWidth(1);	
	    
	}
	
	@FXML
	private void mouseMovedCanvas(MouseEvent evt) {
		
	}
	
	@FXML
	private void enteredAnchor() {
	}
	
	@FXML
	private void mouseClickedCanvas(MouseEvent evt) {
		if (evt.getButton() != MouseButton.PRIMARY)
			return;
		
		double x = evt.getX();
		double y = evt.getY();
		Affine t = canvas.getGraphicsContext2D().getTransform();
		
		selectedParticle = simulation.createNewParticle((x - t.getTx())/zoom, (y - t.getTy())/zoom);
				
		System.out.println(selectedParticle.getPositionX() + " " + selectedParticle.getPositionY());
		
		particleListView.getSelectionModel().select(selectedParticle);
		drawParticles();
	}
	
	@FXML
	private void mousePressedCanvas(MouseEvent evt) {
		if (evt.getButton() != MouseButton.SECONDARY)
			return;
		
		dragLastX = evt.getX();
		dragLastY = evt.getY();
	}
	
	@FXML
	private void mouseDraggedCanvas(MouseEvent evt) {
		if (evt.getButton() != MouseButton.SECONDARY)
			return;
		
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
		
		double x = evt.getX();
		double y = evt.getY();
		Affine t = canvas.getGraphicsContext2D().getTransform();
		
		double oldX = (x - t.getTx())/zoom;
		double oldY = (y - t.getTy())/zoom;
		
		zoomSlider.setValue(zoomSlider.getValue() + evt.getDeltaY() / 40);

		t = canvas.getGraphicsContext2D().getTransform();
		double newX = (x - t.getTx())/zoom;
		double newY = (y - t.getTy())/zoom;
		
		dragAmountX += (-oldX + newX) * zoom;
		dragAmountY += (-oldY + newY) * zoom;

		Affine transform = new Affine();
		transform.setTx(-oldX + newX);
		transform.setTy(-oldY + newY);
		canvas.getGraphicsContext2D().transform(transform);
		
        drawParticles();
	}
	
	@FXML
	private void createParticle() {
		selectedParticle = simulation.createNewParticle(0, 0);
		
		particleListView.getSelectionModel().select(selectedParticle);
		drawParticles();
	}
	
	@FXML
	private void deleteSelected() {
		if (selectedParticle == null)
			return;
		
		simulation.getParticles().remove(selectedParticle);
		drawParticles();
	}

	// =========================================
	// Menu Items
	// =========================================

	@FXML
	private void openFile() {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Simulation File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); 
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Simulations", "*.nbody"));
		
		File f = fileChooser.showOpenDialog(canvas.getScene().getWindow());
		
		if (f != null)
			simulation.loadFile(f);

		drawParticles();
	}
	
	@FXML
	private void saveFile() {
		
		File f = simulation.getSourceFile();
		
		if (f == null) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Simulation File");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); 
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Simulations", "*.nbody"));
			
			f = fileChooser.showSaveDialog(canvas.getScene().getWindow());
		}
		
		if (f != null)
			simulation.saveFile(f);
		
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
	
	@FXML
	private void toggleGridVisibility(ActionEvent evt) {
		CheckMenuItem source = (CheckMenuItem) evt.getSource();
		showGrid = source.isSelected();
		drawParticles();
	}
	
	@FXML
	private void toggleVectorVisibility(ActionEvent evt) {
		CheckMenuItem source = (CheckMenuItem) evt.getSource();
		showVectors = source.isSelected();
		drawParticles();
	}
	
}
