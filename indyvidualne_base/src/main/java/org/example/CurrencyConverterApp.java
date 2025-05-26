package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterApp extends Application {

    // Exchange rates (base: USD)
    private final Map<String, Double> exchangeRates = new HashMap<>();

    // GUI Components
    private TextField amountField;
    private ComboBox<String> fromCurrency;
    private ComboBox<String> toCurrency;
    private Label resultLabel;
    private TextArea historyArea;
    private TableView<ConversionHistory> historyTable;
    private ObservableList<ConversionHistory> historyData;
    private ProgressBar progressBar;
    private Slider amountSlider;
    private CheckBox reverseCheckBox;
    private RadioButton precisionLow, precisionHigh;

    @Override
    public void start(Stage primaryStage) {
        initializeExchangeRates();
        initializeComponents();

        primaryStage.setTitle("Currency Converter - JavaFX Project");
        primaryStage.setScene(createScene());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeExchangeRates() {
        exchangeRates.put("USD", 1.0);
        exchangeRates.put("EUR", 0.85);
        exchangeRates.put("GBP", 0.73);
        exchangeRates.put("JPY", 110.0);
        exchangeRates.put("CAD", 1.25);
        exchangeRates.put("AUD", 1.35);
        exchangeRates.put("CHF", 0.92);
        exchangeRates.put("CNY", 6.45);
        exchangeRates.put("UAH", 37.0);
    }

    private void initializeComponents() {
        // TextField for amount input
        amountField = new TextField("100");
        amountField.setPromptText("Enter amount to convert");

        // ComboBoxes for currency selection
        ObservableList<String> currencies = FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "UAH"
        );
        fromCurrency = new ComboBox<>(currencies);
        fromCurrency.setValue("USD");
        toCurrency = new ComboBox<>(currencies);
        toCurrency.setValue("EUR");

        // Label for displaying results
        resultLabel = new Label("Conversion result will appear here");
        resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resultLabel.setTextFill(Color.DARKGREEN);

        // TextArea for conversion history
        historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefRowCount(5);
        historyArea.setPromptText("Conversion history will appear here...");

        // TableView for detailed history
        historyTable = new TableView<>();
        historyData = FXCollections.observableArrayList();
        setupHistoryTable();

        // ProgressBar for visual feedback
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        // Slider for quick amount selection
        amountSlider = new Slider(1, 10000, 100);
        amountSlider.setShowTickLabels(true);
        amountSlider.setShowTickMarks(true);
        amountSlider.setMajorTickUnit(1000);
        amountSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            amountField.setText(String.valueOf(newVal.intValue()));
        });

        // CheckBox for reverse conversion
        reverseCheckBox = new CheckBox("Show reverse conversion");

        // RadioButtons for precision selection
        ToggleGroup precisionGroup = new ToggleGroup();
        precisionLow = new RadioButton("2 decimal places");
        precisionHigh = new RadioButton("4 decimal places");
        precisionLow.setToggleGroup(precisionGroup);
        precisionHigh.setToggleGroup(precisionGroup);
        precisionLow.setSelected(true);
    }

    private void setupHistoryTable() {
        TableColumn<ConversionHistory, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.setPrefWidth(120);

        TableColumn<ConversionHistory, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("fromAmount"));
        fromCol.setPrefWidth(100);

        TableColumn<ConversionHistory, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(new PropertyValueFactory<>("toAmount"));
        toCol.setPrefWidth(100);

        TableColumn<ConversionHistory, String> rateCol = new TableColumn<>("Rate");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
        rateCol.setPrefWidth(80);

        historyTable.getColumns().addAll(timeCol, fromCol, toCol, rateCol);
        historyTable.setItems(historyData);
        historyTable.setPrefHeight(150);
    }

    private Scene createScene() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Currency Converter");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        // Input section
        VBox inputSection = createInputSection();

        // Control buttons
        HBox buttonSection = createButtonSection();

        // Result section
        VBox resultSection = createResultSection();

        // History section
        VBox historySection = createHistorySection();

        // Options section
        VBox optionsSection = createOptionsSection();

        root.getChildren().addAll(
                titleLabel,
                new Separator(),
                inputSection,
                optionsSection,
                buttonSection,
                progressBar,
                resultSection,
                new Separator(),
                historySection
        );

        return new Scene(new ScrollPane(root), 600, 800);
    }

    private VBox createInputSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label amountLabel = new Label("Amount:");
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label sliderLabel = new Label("Quick Select Amount:");
        sliderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox currencyBox = new HBox(15);
        currencyBox.setAlignment(Pos.CENTER);

        Label fromLabel = new Label("From:");
        fromLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label toLabel = new Label("To:");
        toLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        currencyBox.getChildren().addAll(fromLabel, fromCurrency, toLabel, toCurrency);

        section.getChildren().addAll(
                amountLabel, amountField,
                sliderLabel, amountSlider,
                currencyBox
        );

        return section;
    }

    private VBox createOptionsSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label optionsLabel = new Label("Options:");
        optionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        HBox precisionBox = new HBox(15);
        precisionBox.setAlignment(Pos.CENTER);
        precisionBox.getChildren().addAll(precisionLow, precisionHigh);

        section.getChildren().addAll(optionsLabel, precisionBox, reverseCheckBox);

        return section;
    }

    private HBox createButtonSection() {
        HBox section = new HBox(15);
        section.setAlignment(Pos.CENTER);

        Button convertButton = new Button("Convert");
        convertButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        convertButton.setPrefWidth(100);
        convertButton.setOnAction(e -> performConversion());

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        clearButton.setPrefWidth(100);
        clearButton.setOnAction(e -> clearFields());

        Button swapButton = new Button("Swap");
        swapButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        swapButton.setPrefWidth(100);
        swapButton.setOnAction(e -> swapCurrencies());

        section.getChildren().addAll(convertButton, clearButton, swapButton);

        return section;
    }

    private VBox createResultSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label resultTitle = new Label("Conversion Result:");
        resultTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        section.getChildren().addAll(resultTitle, resultLabel);

        return section;
    }

    private VBox createHistorySection() {
        VBox section = new VBox(10);

        Label historyTitle = new Label("Conversion History:");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        TabPane tabPane = new TabPane();

        Tab textTab = new Tab("Text History", historyArea);
        textTab.setClosable(false);

        Tab tableTab = new Tab("Detailed History", historyTable);
        tableTab.setClosable(false);

        tabPane.getTabs().addAll(textTab, tableTab);

        Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.setOnAction(e -> clearHistory());

        section.getChildren().addAll(historyTitle, tabPane, clearHistoryButton);

        return section;
    }

    private void performConversion() {
        try {
            // Simulate processing time
            progressBar.setProgress(0.3);

            double amount = Double.parseDouble(amountField.getText());
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();

            progressBar.setProgress(0.6);

            double result = convertCurrency(amount, from, to);
            int decimals = precisionLow.isSelected() ? 2 : 4;
            String formatString = "%." + decimals + "f";

            String resultText = String.format("%.2f %s = " + formatString + " %s",
                    amount, from, result, to);

            if (reverseCheckBox.isSelected()) {
                double reverseResult = convertCurrency(result, to, from);
                resultText += String.format("\nReverse: " + formatString + " %s = %.2f %s",
                        result, to, reverseResult, from);
            }

            resultLabel.setText(resultText);

            // Add to history
            String historyEntry = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                    " - " + resultText.replace("\n", " | ");
            historyArea.appendText(historyEntry + "\n");

            // Add to table history
            ConversionHistory historyItem = new ConversionHistory(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    String.format("%.2f %s", amount, from),
                    String.format(formatString + " %s", result, to),
                    String.format("%.4f", result / amount)
            );
            historyData.add(0, historyItem);

            progressBar.setProgress(1.0);

            // Reset progress bar after 1 second
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> progressBar.setProgress(0));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
            progressBar.setProgress(0);
        }
    }

    private double convertCurrency(double amount, String from, String to) {
        double fromRate = exchangeRates.get(from);
        double toRate = exchangeRates.get(to);
        return (amount / fromRate) * toRate;
    }

    private void clearFields() {
        amountField.clear();
        amountField.setPromptText("Enter amount to convert");
        resultLabel.setText("Conversion result will appear here");
        amountSlider.setValue(100);
        progressBar.setProgress(0);
    }

    private void swapCurrencies() {
        String temp = fromCurrency.getValue();
        fromCurrency.setValue(toCurrency.getValue());
        toCurrency.setValue(temp);
    }

    private void clearHistory() {
        historyArea.clear();
        historyData.clear();
        historyArea.setPromptText("Conversion history will appear here...");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for table data
    public static class ConversionHistory {
        private String time;
        private String fromAmount;
        private String toAmount;
        private String rate;

        public ConversionHistory(String time, String fromAmount, String toAmount, String rate) {
            this.time = time;
            this.fromAmount = fromAmount;
            this.toAmount = toAmount;
            this.rate = rate;
        }

        public String getTime() { return time; }
        public String getFromAmount() { return fromAmount; }
        public String getToAmount() { return toAmount; }
        public String getRate() { return rate; }
    }
}