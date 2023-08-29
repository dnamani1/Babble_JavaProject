package edu.westga.cs.babble.controllers;

import edu.westga.cs.babble.model.TileBag;
import edu.westga.cs.babble.model.PlayedWord;
import edu.westga.cs.babble.model.Tile;
import edu.westga.cs.babble.model.EmptyTileBagException;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class BabbleController {
    
    @FXML private ListView<Tile> tilesListView;
    @FXML private TextField wordTextField;
    @FXML private TextField scoreTextField;

    private PlayedWord currentWord;
    private TileBag tileBag;
    private IntegerProperty currentScore;

    public BabbleController() {
        this.currentWord = new PlayedWord();
        this.tileBag = new TileBag();
        this.currentScore = new SimpleIntegerProperty();
    }

    @FXML
    private void initialize() {
        // Bind scoreTextField's text property to the currentScore property.
        this.scoreTextField.textProperty().bind(this.currentScore.asString());

        // Customize the display of Tile objects in the ListView.
        this.tilesListView.setCellFactory(param -> new ListCell<Tile>() {
            @Override
            protected void updateItem(Tile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item.getLetter()));
                }
            }
        });
        this.tilesListView.setOnMouseClicked(event -> this.addTileToWord());

        // Populate the tilesListView with tiles from the TileBag.
        this.refreshTiles();
    }

    private void addTileToWord() {
        Tile selectedTile = this.tilesListView.getSelectionModel().getSelectedItem();
        if (selectedTile != null) {
            // Append the letter of the selected tile to wordTextField.
            this.wordTextField.appendText(String.valueOf(selectedTile.getLetter()));

            // Remove the selected tile from the tilesListView.
            this.tilesListView.getItems().remove(selectedTile);
            
            // Add the tile to the currentWord.
            this.currentWord.append(selectedTile);
        }
    }

    @FXML
    private void playWord() {
        String wordText = this.wordTextField.getText();

        if (!this.currentWord.matches(wordText)) {
            // Show an alert indicating the word is invalid.
            Alert invalidWordAlert = new Alert(AlertType.WARNING, "Invalid word!");
            invalidWordAlert.showAndWait();
            return;
        }

        // Update the score.
        int score = this.currentWord.getScore();
        this.currentScore.set(this.currentScore.get() + score);

        // Clear the current word and refresh the tiles.
        this.currentWord.clear();
        this.wordTextField.clear();
        this.refreshTiles();
    }

    @FXML
    private void resetWord() {
        // Clear the current word and the wordTextField.
        this.currentWord.clear();
        this.wordTextField.clear();
    }

    private void refreshTiles() {
        this.tilesListView.getItems().clear();
        for (int i = 0; i < 7; i++) {  // Assuming 7 tiles to be drawn each time.
            try {
                Tile tile = this.tileBag.drawTile();
                this.tilesListView.getItems().add(tile);
            } catch (EmptyTileBagException ex) {
                // Handle the situation when the TileBag is empty.
                // For now, we'll break out of the loop if no tiles are left.
                break;
            }
        }
    }
}
