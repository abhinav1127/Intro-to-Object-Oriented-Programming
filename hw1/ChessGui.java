import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.collections.transformation.SortedList;

/**
 * Makes the GUI itself
 *
 * @author atirath6
 * @version 1.0
 */
public class ChessGui extends Application {

    private TextField filterField = new TextField();

    /**
     * [start description]
     * @param  Stage       stage         [description]
     */
    @Override
    public void start(Stage stage) {

        //making the buttons
        Button viewGame = new Button("View Game");
        Button dismiss = new Button("Dismiss");
        filterField.setPromptText("Search");
        dismiss.setOnAction(e -> Platform.exit());

        //making the table
        ChessDb chessdb = new ChessDb();
        ObservableList<ChessGame> cgList = FXCollections
                    .observableArrayList(chessdb.getGames());
        TableView<ChessGame> table = createTable(cgList);
        viewGame.setOnAction(e -> {
                ChessGame selected = table
                            .getSelectionModel().getSelectedItem();
                viewDialog(selected);
            });
        viewGame.disableProperty().bind(Bindings.
                    isNull(table.getSelectionModel().selectedItemProperty()));

        //placing things on the screen
        HBox buttonsBox = new HBox();
        buttonsBox.getChildren().addAll(viewGame, dismiss, filterField);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(table, buttonsBox);

        //setting the Stage
        Scene complete = new Scene(vBox);
        stage.setScene(complete);
        stage.setTitle("Chess DB GUI");
        stage.show();
    }

    private TableView<ChessGame> createTable(ObservableList<ChessGame> games) {
        TableView<ChessGame> table = new TableView<ChessGame>();

        FilteredList<ChessGame> filteredData = new
                    FilteredList<>(games, p -> true);

        filterField.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                filteredData.setPredicate(theGame -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        String lowerCaseFilter = newValue.toLowerCase();

                        if (theGame.getEvent().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getSite().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getDate().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getWhite().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getBlack().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getResult().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        } else if (theGame.getOpening().toLowerCase()
                                    .contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false;
                    });
            });


        SortedList<ChessGame> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);

        TableColumn<ChessGame, String> eventCol =
            new TableColumn<ChessGame, String>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory("event"));

        TableColumn<ChessGame, String> siteCol =
            new TableColumn<ChessGame, String>("Site");
        siteCol.setCellValueFactory(new PropertyValueFactory("site"));

        TableColumn<ChessGame, String> dateCol =
            new TableColumn<ChessGame, String>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory("date"));

        TableColumn<ChessGame, String> whiteCol =
            new TableColumn<ChessGame, String>("White");
        whiteCol.setCellValueFactory(new PropertyValueFactory("white"));

        TableColumn<ChessGame, String> blackCol =
            new TableColumn<ChessGame, String>("Black");
        blackCol.setCellValueFactory(new PropertyValueFactory("black"));

        TableColumn<ChessGame, String> resultCol =
            new TableColumn<ChessGame, String>("Result");
        resultCol.setCellValueFactory(new PropertyValueFactory("result"));

        TableColumn<ChessGame, String> openingCol =
            new TableColumn<ChessGame, String>("Opening");
        openingCol.setCellValueFactory(new PropertyValueFactory("opening"));

        table.getColumns().setAll(eventCol, siteCol, dateCol, whiteCol,
                    blackCol, resultCol, openingCol);
        return table;
    }

    private void viewDialog(ChessGame selected) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(selected.getEvent());
        alert.setHeaderText(String.format("Event: %s%nSite: %s%nDate: "
                    + "%s%nWhite: %s%nBlack: %s%nResult: %s%nOpening: %s",
                                          selected.getEvent(),
                                          selected.getSite(),
                                          selected.getDate(),
                                          selected.getWhite(),
                                          selected.getBlack(),
                                          selected.getResult(),
                                          selected.getOpening()));
        String displayMoves = "Moves: \n";
        for (int i = 1; i <= selected.getMoves().size(); i++) {
            displayMoves += "" + i + ". " + selected.getMove(i) + "\n";
        }
        alert.setContentText(displayMoves);
        alert.showAndWait();
    }

}
