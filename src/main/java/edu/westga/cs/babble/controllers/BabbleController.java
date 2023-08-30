package edu.westga.cs.babble.controllers;

import edu.westga.cs.babble.model.TileBag;
import edu.westga.cs.babble.model.TileRack;
import edu.westga.cs.babble.model.TileRackFullException;
import edu.westga.cs.babble.model.PlayedWord;
import edu.westga.cs.babble.model.Tile;

import java.util.stream.Collectors;

import edu.westga.cs.babble.model.EmptyTileBagException;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BabbleController {

	@FXML
	private ListView<Tile> tilesListView;
	@FXML
	private ListView<Tile> wordListView;
	@FXML
	private TextField scoreTextField;
	private TileRack tileRack;

	private PlayedWord currentWord;
	private TileBag tileBag;
	private IntegerProperty currentScore;
	private WordDictionary wordDictionary;

	public BabbleController() {
		this.currentWord = new PlayedWord();
		this.tileBag = new TileBag();
		this.tileRack = new TileRack();
		this.currentScore = new SimpleIntegerProperty();
		this.wordDictionary = new WordDictionary();
	}

	@FXML
	private void initialize() {
		// Bind scoreTextField's text property to the currentScore property.
		this.scoreTextField.textProperty().bind(this.currentScore.asString());

		// Customize the display of Tile objects in both ListViews using the TileCell
		// class.
		this.wordListView.setCellFactory(param -> new TileCell());
		this.tilesListView.setCellFactory(param -> new TileCell());

		// Set orientation of ListView to horizontal
		this.wordListView.setOrientation(Orientation.HORIZONTAL);
		this.tilesListView.setOrientation(Orientation.HORIZONTAL);

		// Set a click listener for tilesListView to add tiles to the word.
		this.tilesListView.setOnMouseClicked(event -> this.addTileToWord());

		// Set a click listener for wordListView to remove tiles from the word.
		this.wordListView.setOnMouseClicked(event -> this.removeTileFromWord());

		// Populate the tilesListView with tiles from the TileBag.
		this.refreshTiles();
	}

	private void removeTileFromWord() {
		Tile selectedTile = this.wordListView.getSelectionModel().getSelectedItem();
		if (selectedTile != null) {
			// Remove the tile from the currentWord.
			this.currentWord.tiles().remove(selectedTile);

			// Remove the letter of the selected tile from wordListView.
			this.wordListView.getItems().remove(selectedTile);

			// Add the tile back to the tilesListView and the tileRack.
			this.tilesListView.getItems().add(selectedTile);
			this.tileRack.append(selectedTile);
		}
	}

	private void addTileToWord() {
		Tile selectedTile = this.tilesListView.getSelectionModel().getSelectedItem();
		if (selectedTile != null) {
			// Append the letter of the selected tile to wordTextField.
			this.wordListView.getItems().add(selectedTile);

			// Remove the selected tile from the tilesListView.
			this.tilesListView.getItems().remove(selectedTile);
			this.tileRack.tiles().remove(selectedTile);

			// Add the tile to the currentWord.
			this.currentWord.append(selectedTile);
		}
	}

	@FXML
    private void playWord() {
        String wordText = this.wordListView.getItems().stream().map(tile -> String.valueOf(tile.getLetter()))
                .collect(Collectors.joining());

        if (!this.currentWord.matches(wordText)) {
            // Show an alert indicating the word is invalid.
            Alert invalidWordAlert = new Alert(AlertType.WARNING, "Invalid word!");
            invalidWordAlert.showAndWait();
            return;
        }

        // Check if the word is valid using the WordDictionary instance.
        if (this.wordDictionary.isValidWord(wordText)) {
            // Update the score if the word is valid.
            int score = this.currentWord.getScore();
            this.currentScore.set(this.currentScore.get() + score);
        } else {
            // Show an alert indicating the word is not valid.
            Alert invalidWordAlert = new Alert(AlertType.WARNING, "Not a valid word!");
            invalidWordAlert.showAndWait();
        }

        // Clear the current word and refresh the tiles.
        this.currentWord.clear();
        this.wordListView.getItems().clear();
        this.tileRack.tiles().clear();
        this.refreshTiles();
    }


	@FXML
	private void resetWord() {
		// Clear the current word and the wordTextField.
		this.currentWord.clear();
		this.wordListView.getItems().clear();
	}

	private void refreshTiles() {
		this.tilesListView.getItems().clear();
		int numberOfTilesNeeded = this.tileRack.getNumberOfTilesNeeded();
		for (int i = 0; i < numberOfTilesNeeded; i++) {
			try {
				Tile tile = this.tileBag.drawTile();
				this.tileRack.append(tile); // Add to TileRack, it'll handle max size
				this.tilesListView.getItems().add(tile);
			} catch (EmptyTileBagException | TileRackFullException ex) {
				// Handle exceptions
				break;
			}
		}
	}
}
