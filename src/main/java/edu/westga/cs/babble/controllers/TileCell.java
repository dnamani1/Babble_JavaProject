package edu.westga.cs.babble.controllers;

import edu.westga.cs.babble.model.Tile;
import javafx.scene.control.ListCell;

public class TileCell extends ListCell<Tile> {
    @Override
    protected void updateItem(Tile item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(String.valueOf(item.getLetter()));
        }
    }
}
