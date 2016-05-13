package controllers;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Particle;

public class MainController {
	
	@FXML private Canvas canvas;
	@FXML private AnchorPane canvasPane;
	@FXML private VBox editPanel;
	
	@FXML private TextField posXField, posYField, velXField, velYField, massField;
	
	@FXML private ListView<Particle> particleListView;
	
	private ObservableList<Particle> particles;
	private Particle selectedParticle;
	
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
		});
		posYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setPositionY(Double.parseDouble(newValue));
		});
		
		velXField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setVelocityX(Double.parseDouble(newValue));
		});
		velYField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setVelocityY(Double.parseDouble(newValue));
		});
		
		massField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (selectedParticle != null)
				selectedParticle.setMass(Double.parseDouble(newValue));
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
		
		posXField.setText(""+selectedParticle.getPositionX());
		posYField.setText(""+selectedParticle.getPositionY());
		velXField.setText(""+selectedParticle.getVelocityX());
		velYField.setText(""+selectedParticle.getVelocityY());
		
		massField.setText(""+selectedParticle.getMass());
		
	}
	
	
	@FXML
	private void mouseEnteredCanvas() {
		System.out.println("hello");
	}
	
	@FXML
	private void createParticle() {
		selectedParticle = new Particle();
		particles.add(selectedParticle);
		
		particleListView.getSelectionModel().select(selectedParticle);
		updateEditingPane();
	}
	
	@FXML
	private void deleteSelected() {
		System.out.println("hi");
		if (selectedParticle == null)
			return;
		
		particles.remove(selectedParticle);
		updateEditingPane();
	}
	
}
