/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.awt.*;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.org.apache.bcel.internal.generic.IADD;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import javax.annotation.Generated;
import javax.swing.*;

import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

/**
 * FXML Controller class
 *
 * @author jtconnor
 */
public class ClientGUIController implements Initializable {



    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());



    private boolean isConnected, turn, serverUNO = false, clientUNO = false;

    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private FxSocketClient socket;
    private void connect() {
        socket = new FxSocketClient(new FxSocketListener(),
                hostTextField.getText(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    private void displayState(ConnectionDisplayState state) {
//        switch (state) {
//            case DISCONNECTED:
//                connectButton.setDisable(false);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case WAITING:
//            case AUTOWAITING:
//                connectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case CONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//            case AUTOCONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);


        

        Runtime.getRuntime().addShutdownHook(new ShutDownThread());

        /*
         * Uncomment to have autoConnect enabled at startup
         */
//        autoConnectCheckBox.setSelected(true);
//        displayState(ConnectionDisplayState.WAITING);
//        connect();
    }

    class ShutDownThread extends Thread {

        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");    
                }
                socket.shutdown();
            }
        }
    }



















    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton, sendButton,  SubmitBTNFX, btnStart;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField hostTextField;
    @FXML
    private TableView<HandSPoints> Points;

    @FXML
    private Label lblName3, lblName4, lblMessages, ClientHands, ServerHands, InstructionLbl;
    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9,

    imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9,

    imgP0,imgP1,imgP2,imgP3;
    @FXML
    private GridPane gPaneServer, gPaneClient;
    @FXML
    public TextField txtSubmit;
    @FXML
    public Label lblName1, lblName2 ,  turnChange;
    @FXML
    private ListView MSGListV;
    private String name1 = "";
    private String name2 = "";
    private ArrayList<Card> Hand1 = new ArrayList<>();
    private ArrayList<Card> Hand2 = new ArrayList<>();
    private ArrayList<Card> Using = new ArrayList<>();
    private ObservableList<HandSPoints> HandPoints = FXCollections.observableArrayList();

    private int Gone = 0, Set = 0;

    private int Size = 0;
    Card Use;
    private int PlayedCards = 0;

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            System.out.println("Line " + line);
            if (line.substring(0,3).equals("Msg")){ //Messages to put in list view
                MSGListV.getItems().add(line.substring(3));
            }
            else if (line.substring(0,3).equals("SZE")){ //the size of cards that are gonna be coming in
                Hand2.clear(); //clears the hand as this is now a new game
                Hand1.clear();
                Size = Integer.parseInt(line.substring(3));
            }
            else if (line.equals("HW!0")){ // When the Winner is found and their are still hands to play
                imgP0.setImage(null);
                imgP1.setImage(null);
                imgP2.setImage(null);
                imgP3.setImage(null);
                PlayedCards = 0;
            }
            else if (line.substring(0,4).equals("CCrd")){ //if its a card for the client deck add is to the Hand
                Hand2.add(new Card(line.substring(4)));
                if (Size == Hand2.size()){
                    DealImages();
                }
            }
            else if (line.substring(0,4).equals("SCrd")){//if its a card for the Server Deck
                Hand1.add(new Card(line.substring(4)));
            }
            else if (line.substring(0,4).equals("Name")){ //its the name of the other player
                name1 = line.substring(4);
                lblName1.setText(name1);
            }
            else if (line.substring(0,3).equals("Clk")) {//Show + number where you want to place it + Number you removed for it + Image
                int imgClicked = Integer.parseInt(line.substring(3));
                Card Use = Hand1.get(imgClicked);
                RemovefromSimg(imgClicked); //remove the image of img from the Server hand pane
                PlayedCards++;
                AddtoPHandimg(Use);
            }
            else if (line.equals("Clear")){
                imgP0.setImage(null); //reset the image
                imgP1.setImage(null);
                imgP2.setImage(null);
                imgP3.setImage(null);
                sendTextField.clear();
                txtSubmit.clear();
                ClientHands.setText("Hands");
                ServerHands.setText("Hands");
                Hand2.clear();
                Hand1.clear();
                InstructionLbl.setText("How many Hands can you make");
                btnStart.setDisable(true);
                Size = 0;
                Use = null;
                PlayedCards = 0;
            }
            else if (line.substring(0,4).equals("Sirs")){
                HandPoints.add(new HandSPoints(line.substring(4)));
                TableChange();
            }
            else if (line.substring(0,4).equals("SSCR")){
                HandPoints.get(Set).setSPointScored(Integer.parseInt(line.substring(4)));
                TableChange();
            }
            else if (line.substring(0,4).equals("CSCR")){
                HandPoints.get(Set).setCPointsScored(Integer.parseInt(line.substring(4)));
                TableChange();
            }
            else if (line.substring(0,5).equals("WSSCR")){
                HandPoints.get(Set).setSPointWanted(Integer.parseInt(line.substring(5)));
                TableChange();
            }
            else if (line.substring(0,5).equals("WCSCR")){
                HandPoints.get(Set).setCPointsWanted(Integer.parseInt(line.substring(5)));
                TableChange();
            }
            else if (line.substring(0,5).equals("DubHS")){ //the amount of hands the Server has won
                ServerHands.setText(line.substring(5));
            }
            else if (line.substring(0,5).equals("DubHC")){ //the amount of hands the Client has won
                ClientHands.setText(line.substring(5));
            }

            else if (line.equals("Enable")){ //Enable the client cards click
                imgC0.setDisable(false);
                imgC1.setDisable(false);
                imgC2.setDisable(false);
                imgC3.setDisable(false);
                imgC4.setDisable(false);
                imgC5.setDisable(false);
                imgC6.setDisable(false);
                imgC7.setDisable(false);
                imgC8.setDisable(false);
                imgC9.setDisable(false);
                turnChange.setText(name2 + " turn");
                socket.sendMessage("Disable");
            }
            else if (line.equals("Disable")){ //Disable the Client cards click
                imgC0.setDisable(true);
                imgC1.setDisable(true);
                imgC2.setDisable(true);
                imgC3.setDisable(true);
                imgC4.setDisable(true);
                imgC5.setDisable(true);
                imgC6.setDisable(true);
                imgC7.setDisable(true);
                imgC8.setDisable(true);
                imgC9.setDisable(true);
                turnChange.setText(name1 + " turn");
            }

            else if (line.substring(0,6).equals("setSet")){
                Set = (Integer.parseInt(line.substring(6)));
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
           
        }
    }

    private void RemovefromCimg(int x){ //removes a certain card from the Image view
        if (x == 0){
            imgC0.setImage(null);
        }
        else if (x == 1){
            imgC1.setImage(null);
        }
        else if (x == 2){
            imgC2.setImage(null);
        }
        else if (x == 3){
            imgC3.setImage(null);
        }
        else if (x == 4){
            imgC4.setImage(null);
        }
        else if (x == 5){
            imgC5.setImage(null);
        }
        else if (x == 6){
            imgC6.setImage(null);
        }
        else if (x == 7){
            imgC7.setImage(null);
        }
        else if (x == 8){
            imgC8.setImage(null);
        }
        else if (x == 9){
            imgC9.setImage(null);
        }
        if (x<=9){
            //Hand2.remove(x);
        }
    }

    private void RemovefromSimg(int x){ //Removes the image from the Image server hand
        if (x == 0){
            imgS0.setImage(null);
        }
        else if (x == 1){
            imgS1.setImage(null);
        }
        else if (x == 2){
            imgS2.setImage(null);
        }
        else if (x == 3){
            imgS3.setImage(null);
        }
        else if (x == 4){
            imgS4.setImage(null);
        }
        else if (x == 5){
            imgS5.setImage(null);
        }
        else if (x == 6){
            imgS6.setImage(null);
        }
        else if (x == 7){
            imgS7.setImage(null);
        }
        else if (x == 8){
            imgS8.setImage(null);
        }
        else if (x == 9){
            imgS9.setImage(null);
        }
        if (x<=9){
            //Hand1.remove(x);
        }
    }

    private void AddtoPHandimg(Card Use){ //Adds the card to the Image of the Played Hands.
        if (PlayedCards == 1){
            imgP0.setImage(new Image(Use.pathName));
        }
        else if (PlayedCards == 2){
            imgP1.setImage(new Image(Use.pathName));
        }
        else if (PlayedCards == 3){
            imgP2.setImage(new Image(Use.pathName));
        }
        else if (PlayedCards == 4){
            imgP3.setImage(new Image(Use.pathName));
        }
    }

    private void DealImages(){ //deals the UImages
        System.out.println(Hand2.size());
        if (Hand2.size() == 10){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));
            imgS5.setImage(new Image(Hand1.get(5).pathName));
            imgS6.setImage(new Image(Hand1.get(6).pathName));
            imgS7.setImage(new Image(Hand1.get(7).pathName));
            imgS8.setImage(new Image(Hand1.get(8).pathName));
            imgS9.setImage(new Image(Hand1.get(9).pathName));

            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));
            imgC5.setImage(new Image(Hand2.get(5).pathName));
            imgC6.setImage(new Image(Hand2.get(6).pathName));
            imgC7.setImage(new Image(Hand2.get(7).pathName));
            imgC8.setImage(new Image(Hand2.get(8).pathName));
            imgC9.setImage(new Image(Hand2.get(9).pathName));
        }
        else if (Hand2.size() == 9){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));
            imgC5.setImage(new Image(Hand2.get(5).pathName));
            imgC6.setImage(new Image(Hand2.get(6).pathName));
            imgC7.setImage(new Image(Hand2.get(7).pathName));
            imgC8.setImage(new Image(Hand2.get(8).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));
            imgS4.setImage(new Image("resources/BACK-1.jpg"));
            imgS5.setImage(new Image("resources/BACK-1.jpg"));
            imgS6.setImage(new Image("resources/BACK-1.jpg"));
            imgS7.setImage(new Image("resources/BACK-1.jpg"));
            imgS8.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 8){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));
            imgC5.setImage(new Image(Hand2.get(5).pathName));
            imgC6.setImage(new Image(Hand2.get(6).pathName));
            imgC7.setImage(new Image(Hand2.get(7).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));
            imgS4.setImage(new Image("resources/BACK-1.jpg"));
            imgS5.setImage(new Image("resources/BACK-1.jpg"));
            imgS6.setImage(new Image("resources/BACK-1.jpg"));
            imgS7.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 7){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));
            imgC5.setImage(new Image(Hand2.get(5).pathName));
            imgC6.setImage(new Image(Hand2.get(6).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));
            imgS4.setImage(new Image("resources/BACK-1.jpg"));
            imgS5.setImage(new Image("resources/BACK-1.jpg"));
            imgS6.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 6){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));
            imgC5.setImage(new Image(Hand2.get(5).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));
            imgS4.setImage(new Image("resources/BACK-1.jpg"));
            imgS5.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 5){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));
            imgC4.setImage(new Image(Hand2.get(4).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));
            imgS4.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 4){
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));
            imgC3.setImage(new Image(Hand2.get(3).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));
            imgS3.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 3){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));
            imgC2.setImage(new Image(Hand2.get(2).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));
            imgS2.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 2){
            imgC0.setImage(new Image(Hand2.get(0).pathName));
            imgC1.setImage(new Image(Hand2.get(1).pathName));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
            imgS1.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 1){
            imgC0.setImage(new Image("resources/BACK-1.jpg"));

            imgS0.setImage(new Image("resources/BACK-1.jpg"));
        }
    }


    @FXML
    private void SendMessageButton(ActionEvent event) { //The Send message button
        if (!sendTextField.getText().equals("")) {
            socket.sendMessage("Msg" + sendTextField.getText());
        }

    }


    @FXML
    private void handleConnectButton(ActionEvent event) { //Conects the Client to server
        connectButton.setDisable(true);
        btnStart.setDisable(false);
        displayState(ConnectionDisplayState.WAITING);
        InstructionLbl.setText("Type your name, then click START");
        connect();
    }


    private void TableChange(){
        Points.getColumns().clear();
        Points.getItems().clear();
        System.out.println("INSIDE");
        /*TableColumn<HandSPoints, String> SirsColumn = new TableColumn<>("Trump");
        SirsColumn.setMinWidth(200);
        SirsColumn.setCellValueFactory(new PropertyValueFactory<HandSPoints, String>("Sir"));

        TableColumn<HandSPoints, Integer> SPointsColumn = new TableColumn<>(name1 + " Point");
        SPointsColumn.setMinWidth(200);
        SPointsColumn.setCellValueFactory(new PropertyValueFactory<HandSPoints, Integer>("SPoints"));

        TableColumn<HandSPoints, Integer> CPointsColumn = new TableColumn<>(name2+ " Points");
        CPointsColumn.setMinWidth(200);
        CPointsColumn.setCellValueFactory(new PropertyValueFactory<HandSPoints, Integer>("CPoints"));

        Points.getItems().clear();
        Points = new TableView<>();
        Points.setItems(HandPoints);
        Points.getColumns().addAll(SirsColumn, SPointsColumn, CPointsColumn);*/
        //The Trump of the set
        TableColumn<HandSPoints, String> SirsColumn = new TableColumn<>("Trump");
        SirsColumn.setMinWidth(75);
        SirsColumn.setCellValueFactory(new PropertyValueFactory<>("Sir"));
        //how many hands the Server wants
        TableColumn<HandSPoints, Integer> SWantedColumn = new TableColumn<>(name1 + " Hands Wanted");
        SWantedColumn.setMinWidth(50);
        SWantedColumn.setCellValueFactory(new PropertyValueFactory<>("SPointWanted"));


        //How many hands the server got
        TableColumn<HandSPoints, Integer> SgotColumn = new TableColumn<>(name1 + " Hands Won");
        SgotColumn.setMinWidth(25);
        SgotColumn.setCellValueFactory(new PropertyValueFactory<>("SPointScored"));

        //How many points the server got
        TableColumn<HandSPoints, Integer> SPointColumn = new TableColumn<>(name1 + " Points");
        SPointColumn.setMinWidth(25);
        SPointColumn.setCellValueFactory(new PropertyValueFactory<>("SPoints"));

        //how many hands the Client wants
        TableColumn<HandSPoints, Integer> CWantedColumn = new TableColumn<>(name2+" Hands Wanted");
        CWantedColumn.setMinWidth(25);
        CWantedColumn.setCellValueFactory(new PropertyValueFactory<>("CPointsWanted"));

        //How many hands the Client got
        TableColumn<HandSPoints, Integer> CgotColumn = new TableColumn<>(name2+" Hands Won");
        CgotColumn.setMinWidth(25);
        CgotColumn.setCellValueFactory(new PropertyValueFactory<>("CPointsScored"));

        //How many points the server got
        TableColumn<HandSPoints, Integer> CPointColumn = new TableColumn<>(name2+" Points");
        CPointColumn.setMinWidth(25);
        CPointColumn.setCellValueFactory(new PropertyValueFactory<>("CPoints"));

        //Points = new TableView<>();
        Points.getColumns().clear();
        Points.getItems().clear();
        Points.setItems(getHandSP());
        Points.getColumns().addAll(SirsColumn,SPointColumn,SWantedColumn,SgotColumn,CPointColumn,CWantedColumn,CgotColumn);

    }

    private ObservableList<HandSPoints> getHandSP(){
        ObservableList<HandSPoints> x = FXCollections.observableArrayList();
        for (HandSPoints y: HandPoints){
            x.add(y);
        }
        return x;
    }


    @FXML
    private void handleStart(){ //Starts the game
        name2 = txtSubmit.getText();
        socket.sendMessage("Name" + name2);
        txtSubmit.clear();
        btnStart.setDisable(true);
        lblName2.setText(name2);
        InstructionLbl.setText("How many Hands can you make");
    }


    @FXML
    private void SubmitBTN(){ //uses the words in the submit text ield to submit things needed to play
        if(InstructionLbl.getText().equals("How many Hands can you make")){
            int HandC = Integer.parseInt(txtSubmit.getText());
            socket.sendMessage("HCW" + HandC);
        }
    }

    @FXML
    private void handleUse(MouseEvent event) { //When clicked
        //3 tasks,  remove from server, add to played cards hand,  send to client, remove from server hand.
        int imgClicked; ////////////in the client it is //Show + number where you want to place it + Number you removed for it + Image
        try {
            imgClicked =GridPane.getColumnIndex(((ImageView) event.getSource()));
        }
        catch (Exception e){
            imgClicked = 0;
        }
        Card Use = Hand2.get(imgClicked);
        RemovefromCimg(imgClicked); //remove the image of img from the Server hand pane
        PlayedCards++;
        AddtoPHandimg(Use);
        socket.sendMessage("Clk" + imgClicked);
        //socket.sendMessage("Enable");
        System.out.println("Clicked");
    }



////



}
