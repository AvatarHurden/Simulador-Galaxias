package controllers;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import models.Scale;
import models.Simulation;

public class MainController {
	
	private Simulation simulation;
	
	@FXML private AnchorPane canvasPane;
	@FXML private VBox editPanel;
	
	@FXML private TextField posXField, posYField, velXField, velYField, massField, nameField;
	@FXML private ColorPicker colorSelector;
	
	@FXML private ListView<Particle> particleListView;
	private Particle selectedParticle;
	private Particle hoveredParticle;

	@FXML private ComboBox<Scale> scaleSelection;
	@FXML private Button runButton;
	
	
	@FXML private Canvas canvas;
	@FXML private Label positionLabel, distanceLabel, timeLabel;
	@FXML private Slider zoomSlider;
	
	private double zoom = 1;
	private double dragLastX, dragLastY;
	private boolean isDragging = false;
	
	private boolean showGrid = true, showVectors = true;
	
	@FXML
	private void initialize() {
		
		simulation = new Simulation();
		
		scaleSelection.setItems(FXCollections.observableArrayList(Scale.values()));
		scaleSelection.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			simulation.setScale(newValue);
		});
		scaleSelection.getSelectionModel().select(0);
		
		particleListView.setItems(simulation.getParticles());
		particleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			hoveredParticle = newValue;
			selectedParticle = newValue;
			updateEditingPane();
			particleListView.scrollTo(selectedParticle);
		});
		
		editPanel.setVisible(false);
		
		configureDoubleField(posXField, true);
		configureDoubleField(posYField, true);
		configureDoubleField(massField, false);
		configureDoubleField(velXField, true);
		configureDoubleField(velYField, true);
		
		posXField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				simulation.setPositionXInUnit(selectedParticle, Double.parseDouble(newValue));
			drawCanvas();
		});
		posYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				simulation.setPositionYInUnit(selectedParticle, Double.parseDouble(newValue));
			drawCanvas();
		});
		
		velXField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				simulation.setVelocityXInUnit(selectedParticle, Double.parseDouble(newValue));
			drawCanvas();
		});
		velYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
			simulation.setVelocityYInUnit(selectedParticle, Double.parseDouble(newValue));
			drawCanvas();
		});
		
		massField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				simulation.setMassInUnit(selectedParticle, Double.parseDouble(newValue));
			drawCanvas();
		});
		
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null) {
				selectedParticle.setName(newValue);
				particleListView.refresh();
			}
			drawCanvas();
		});
		
		colorSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setColor(newValue);
			drawCanvas();
		});
		
		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());
		canvas.getGraphicsContext2D().translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
		
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
			Affine t = new Affine();
			t.setTy((newValue.doubleValue() / 2 - oldValue.doubleValue() / 2) / zoom);
			canvas.getGraphicsContext2D().transform(t);
			drawCanvas();
		});
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
			Affine t = new Affine();
			t.setTx((newValue.doubleValue() / 2 - oldValue.doubleValue() / 2) / zoom);
			canvas.getGraphicsContext2D().transform(t);
			drawCanvas();
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
			drawCanvas();
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
		
		posXField.setText(""+simulation.getPositionXInUnit(selectedParticle));
		posYField.setText(""+simulation.getPositionYInUnit(selectedParticle));
		velXField.setText(""+simulation.getVelocityXInUnit(selectedParticle));
		velYField.setText(""+simulation.getVelocityYInUnit(selectedParticle));
		
		massField.setText(""+simulation.getMassInUnit(selectedParticle));
	}
	
	private void drawCanvas() {
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Affine t = gc.getTransform();
		
		double width = canvas.getWidth();
		double height = canvas.getHeight();

		double leftLimit = - t.getTx() / zoom;
		double bottomLimit = - t.getTy() / zoom;

		gc.clearRect(leftLimit, bottomLimit, width / zoom, height / zoom);
		
		if (showGrid)
			drawGrid();
		
		for (Particle p : simulation.getParticles()) {
			if (hoveredParticle != null)
				drawAura(hoveredParticle);
			gc.setFill(p.getColor());
			gc.setStroke(p.getColor());
			double radius = 1;
			gc.fillOval(simulation.getPositionXInUnit(p) - radius / 2, 
					simulation.getPositionYInUnit(p) - radius / 2, radius, radius);
			
			if (showVectors)
				drawVelocityVector(gc, p);
		}
		
	}
	
	private void drawGrid() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Affine t = gc.getTransform();
		
		double width = canvas.getWidth();
		double height = canvas.getHeight();
		
		double leftLimit = - t.getTx() / zoom;
		double rightLimit = (width - t.getTx()) / zoom;
		double bottomLimit = - t.getTy() / zoom;
		double topLimit = (height- t.getTy()) / zoom;

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
		int deslocX = (int) Math.ceil(t.getTx() / zoom / step);
		int deslocY = (int) Math.ceil(t.getTy() / zoom / step);
		
		for (int i = 0; i < 2*lines; i++) {
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

		distanceLabel.setText(""+simulation.getScale().getDistanceConversion() * step);
	}
	
	private void drawPosition(double x, double y) {
		Affine t = canvas.getGraphicsContext2D().getTransform();
		String text = String.format("%.2f %.2f", (x - t.getTx())/zoom, (y - t.getTy())/zoom);
		
		positionLabel.setText(text);
	}
	
	private void drawVelocityVector(GraphicsContext gc, Particle p) {
	    if (p.getVelocityX() == 0 && p.getVelocityY() == 0) {
		    gc.setLineWidth(1);
			return;
	    }
	    
		double radius = 1 + simulation.getMassInUnit(p) / 100;
		
		double arctan = Math.atan2( p.getVelocityY(), p.getVelocityX() );
		
		double x1 = simulation.getPositionXInUnit(p) + (radius/2) *  Math.cos(arctan);
		double x2 = x1 + simulation.getVelocityXInUnit(p);
		double y1 = simulation.getPositionYInUnit(p) + (radius/2) *  Math.sin(arctan);
		double y2 = y1 + simulation.getVelocityYInUnit(p);
		
	    gc.setLineWidth(0.05 * radius);
	    gc.setLineCap(StrokeLineCap.ROUND);
	    gc.strokeLine(x1, y1, x2, y2);
	    
	    
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
	
	private void drawAura (Particle p) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		gc.setFill(p.getColor().deriveColor(0, 1, 1, 0.3));
		gc.setStroke(p.getColor().deriveColor(0, 1, 1, 0.3));
		double radius = 1 + simulation.getMassInUnit(p) / 100;
		double s = (radius / 10);
		gc.fillOval(-s + (simulation.getPositionXInUnit(p) - radius / 2), 
				-s + (simulation.getPositionYInUnit(p) - radius / 2), 
				radius + 2*s, radius + 2*s);
	}
	
	@FXML
	private void mouseMovedCanvas(MouseEvent evt) {
		drawPosition(evt.getX(), evt.getY());
		hoveredParticle = mouseOnParticle(evt.getX(), evt.getY());
		drawCanvas();
	}
	
	@FXML
	private void mouseExitedCanvas(MouseEvent evt) {
		positionLabel.setText("");
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
		
		Particle p = mouseOnParticle(x,y);
		if (p != null) 
			return;
				
		selectedParticle = simulation.createNewParticle((x - t.getTx())/zoom, (y - t.getTy())/zoom);

		particleListView.getSelectionModel().select(selectedParticle);
		drawCanvas();
		drawPosition(x, y);
	}
	
	@FXML
	private void mousePressedCanvas(MouseEvent evt) {
		if (evt.getButton() == MouseButton.PRIMARY) {
			
			double x = evt.getX();
			double y = evt.getY();
			
			Particle p = mouseOnParticle(x,y);
			isDragging = p != null;
			if (isDragging)
				particleListView.getSelectionModel().select(p);
			
		} else if (evt.getButton() == MouseButton.SECONDARY) {
			dragLastX = evt.getX();
			dragLastY = evt.getY();
		}
	}
	
	@FXML
	private void mouseDraggedCanvas(MouseEvent evt) {
		if (evt.getButton() != MouseButton.SECONDARY && evt.getButton() != MouseButton.PRIMARY)
			return;
		
		double x = evt.getX();
		double y = evt.getY();
		
		Affine a = canvas.getGraphicsContext2D().getTransform();
		
		if (evt.getButton() == MouseButton.PRIMARY && isDragging) {
			posXField.setText(""+(x - a.getTx())/zoom);
			posYField.setText(""+(y - a.getTy())/zoom);
			particleListView.refresh();
		} else if (evt.getButton() == MouseButton.SECONDARY) {
			
			Affine t = new Affine();
			t.setTx((evt.getX() - dragLastX) / zoom);
			t.setTy((evt.getY() - dragLastY) / zoom);
			canvas.getGraphicsContext2D().transform(t);
			
			dragLastX = evt.getX();
			dragLastY = evt.getY();
		}
		drawCanvas();
		drawPosition(x, y);
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
		
		Affine transform = new Affine();
		transform.setTx(-oldX + newX);
		transform.setTy(-oldY + newY);
		canvas.getGraphicsContext2D().transform(transform);
		
        drawCanvas();
        drawPosition(x, y);
	}
	
	private Particle mouseOnParticle(double x, double y) {
		
		Affine t = canvas.getGraphicsContext2D().getTransform();

		for (Particle p : simulation.getParticles()) 
			if (p.hasPoint((x - t.getTx())/zoom, (y - t.getTy())/zoom)) 
				return p;
		
		return null;
	}
	
	@FXML
	private void createParticle() {
		selectedParticle = simulation.createNewParticle(0, 0);
		
		particleListView.getSelectionModel().select(selectedParticle);
		drawCanvas();
	}
	
	@FXML
	private void deleteSelected() {
		if (selectedParticle == null)
			return;
		
		simulation.getParticles().remove(selectedParticle);
		drawCanvas();
	}
	
	// =========================================
	// Simulation
	// =========================================

	@FXML
	private void stepSimulation() {
		simulation.step();
		
		updateEditingPane();
		drawCanvas();
	}
	
	
	private boolean run = false;
	@FXML
	private void runSimulation() {
		run = !run;
		
		runButton.setText(run ? "Pause" : "Run");
		
		if (run)
			new Thread(() -> {
				while (run) {
					simulation.step();
			            
			        Platform.runLater(() -> {
			        	updateEditingPane();
				        drawCanvas();
				        timeLabel.setText(""+simulation.getTime());
			        });	
			        
			        try {
			        	Thread.sleep(20);
			        } catch (Exception e) {}
				}
			}).start();
		
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

		drawCanvas();
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
		drawCanvas();
	}
	
	@FXML
	private void toggleVectorVisibility(ActionEvent evt) {
		CheckMenuItem source = (CheckMenuItem) evt.getSource();
		showVectors = source.isSelected();
		drawCanvas();
	}
	
}
