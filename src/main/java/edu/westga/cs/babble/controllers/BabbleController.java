package edu.westga.cs.babble.controllers;

import edu.westga.cs.babble.model.PlayedWord;
import edu.westga.cs.babble.model.Tile;
import edu.westga.cs.babble.model.TileBag;
import edu.westga.cs.babble.model.EmptyTileBagException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class BabbleController {
	@FXML
	private TextField scoreTextField;
	@FXML
	private TextField yourWordTextField;
	@FXML
	private Button resetButton;
	@FXML
	private ListView<Tile> tilesListView;
	private ObservableList<Tile> tilesRack;

	private IntegerProperty score;
	private PlayedWord currentWord;
	private TileBag tileBag;

	public BabbleController() {
		this.score = new SimpleIntegerProperty(0);
		this.currentWord = new PlayedWord();
		this.tileBag = new TileBag();
	}
	
	public void resetWord(ActionEvent event) {
	    // method logic here
	}
	
	
	

	@FXML
	public void initialize() {
		this.scoreTextField.textProperty().bind(score.asString());
		// Initialize the ObservableList and add tiles to it
		this.tilesRack = FXCollections.observableArrayList();
		addInitialTiles();

		// Bind the tiles list to the ListView
		this.tilesListView.setItems(this.tilesRack);
	}

	// New helper method to add initial tiles:
	private void addInitialTiles() {
		char[] initialTiles = { 'A', 'T', 'N', 'R', 'X', 'E', 'I' };
		for (char letter : initialTiles) {
			this.tilesRack.add(new Tile(letter));
		}
	}

	@FXML
	private void handlePlayWordButtonAction() {
		String word = yourWordTextField.getText().toUpperCase();

		// Check if the word is valid
		if (currentWord.matches(word)) {
			// Increase the score
			this.score.set(this.score.get() + currentWord.getScore());

			// Clear the current word
			this.currentWord.clear();

			// Refresh the tiles
			this.refreshTiles();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Word");
			alert.setHeaderText("The word you played is not valid.");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleResetButtonAction() {
		// Loop through each tile in the current word
		for (Tile tile : this.currentWord.tiles()) {
			// Add the tile back to the tile bag
			this.tileBag.addTile(tile);
		}

		// Clear the current word
		this.currentWord.clear();

		// Clear the 'Your Word' text field
		this.yourWordTextField.clear();
	}

	private void refreshTiles() {
		// Logic to refresh the tiles. For simplicity, this example only tries to draw a
		// new tile
		// You can expand on this to draw multiple tiles or implement your desired
		// behavior
		try {
			this.tileBag.drawTile(); // This is just an example, you'd want to add the drawn tile somewhere in the UI
		} catch (EmptyTileBagException ex) {
			// Handle the case where the bag is empty (e.g. show an alert or end the game)
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Tile Bag Empty");
			alert.setHeaderText("The tile bag is empty! Game over?");
			alert.showAndWait();
		}
	}
}
