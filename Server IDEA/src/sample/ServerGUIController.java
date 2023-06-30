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
import socketfx.FxSocketServer;
import socketfx.SocketListener;

/**
 * FXML Controller class
 *
 * @author jtconnor
 */



public class ServerGUIController implements Initializable {




//    private String n1, n2;
//    private boolean cClicked,sClicked;

    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private boolean isConnected, serverUNO = false, clientUNO = false;
    private int counter = 0;
    private String color;

    public enum ConnectionDisplayState {
           
        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private FxSocketServer socket;

    private void connect() {
        socket = new FxSocketServer(new FxSocketListener(),
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
    private TableView<HandSPoints> Points;
    @FXML
    private Button connectButton, sendButton,  SubmitBTNFX, btnStart;
    @FXML
    private TextField portTextField, txtSubmit;
    @FXML
    private Label lblName3, lblName4, lblMessages, ClientHands, ServerHands, InstructionLbl;
    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9,

    imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9,

    imgP0,imgP1,imgP2,imgP3;
    @FXML
    private ListView MSGListV;
    @FXML
    public Label lblName1,lblName2, turnChange;
    private ArrayList<Card> deck= new ArrayList<>();
    private ArrayList<Card> Alldeck= new ArrayList<>();
    private ArrayList<Card> Hand2 = new ArrayList<>();
    private ArrayList<Card> Hand1 = new ArrayList<>();
    private ArrayList<Card> Played = new ArrayList<>();
    private ObservableList<HandSPoints> HandPoints = FXCollections.observableArrayList();
    private int GameNum = 10;
    private String name1 = "", name2 = "";
    private int HandsDubS, HandsDubC;
    private boolean start1 = false, start2 = false, HandSChosen = false, HandCChosen = false, Reverse = false, ReverseSuit = false;
    private boolean Turn; //True = Server, False = Client
    private int Trump = 0; //0 = Spades, 1 = Diamond, 2 = Clubs, 3 = Heart
    private int Set = 0;




    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) { //Receives Strings from Client
            //System.out.println("Line " + line);
            if (line.substring(0,4).equals("Name")){ //REcieves the name of the client
                start2 = true;
                name2 = line.substring(4);
                lblName2.setText(name2);
                checkDeal();
                TableChange();
            }
            else if (line.substring(0,3).equals("Clk")) { //recieves the click of the Client
                int imgClicked = Integer.parseInt(line.substring(3));
                Card Use = Hand2.get(imgClicked);
                RemovefromCimg(imgClicked); //remove the image of img from the Server hand pane
                AddtoPHandimg(Use);
                Played.add(Use);
                CheckHandDub();
            }
            else if (line.substring(0,3).equals("Msg")){ //recieves a Message sent from the client
                MSGListV.getItems().add(line.substring(3));
            }
            else if (line.substring(0,3).equals("HCW")){ //how many hands the client wants
                HandPoints.get(Set).setCPointsWanted(Integer.parseInt(line.substring(3)));
                socket.sendMessage("WCSCR" + Integer.parseInt(line.substring(3)));
                HandCChosen = true;
                if (HandSChosen && HandCChosen){
                    ChangeTurn();
                }
                TableChange();
            }
            else if (line.equals("Enable")){ //Enable the click for the Se
                socket.sendMessage("Disable");
                imgS0.setDisable(false);
                imgS1.setDisable(false);
                imgS2.setDisable(false);
                imgS3.setDisable(false);
                imgS4.setDisable(false);
                imgS5.setDisable(false);
                imgS6.setDisable(false);
                imgS7.setDisable(false);
                imgS8.setDisable(false);
                imgS9.setDisable(false);
            }
            else if (line.equals("Disable")){
                imgS0.setDisable(true);
                imgS1.setDisable(true);
                imgS2.setDisable(true);
                imgS3.setDisable(true);
                imgS4.setDisable(true);
                imgS5.setDisable(true);
                imgS6.setDisable(true);
                imgS7.setDisable(true);
                imgS8.setDisable(true);
                imgS9.setDisable(true);
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
           
        }
    }

    @FXML
    private void SendMessageBtn(ActionEvent event) { //Sends MESSAGES like Texts to client
        if (!sendTextField.getText().equals("")) {
            socket.sendMessage("Msg" + sendTextField.getText());
        }
    } //Message Send BTN

    /* What will the server send??
    the Name
    Any messages
    The cards in the start
        Client Cards
        Server Cards
    The hands wanted
    The card clicked
    the winner of the hand
     ... Extra stuff or whatever


     What will the Server Recieve
     The card clicked
     The hand wanted
     Messages

    */

    @FXML
    private void handleConnectButton(ActionEvent event) { //Connects
        connectButton.setDisable(true);
        btnStart.setDisable(false);
        displayState(ConnectionDisplayState.WAITING);
        InstructionLbl.setText("Type your name, then click START");
        connect();
    } //Connects server to client

    private void RemovefromCimg(int x){ //removes a the image of the wanted remove index from the image view
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

    private void RemovefromSimg(int x){ //removes a the image of the wanted remove index from the image view
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

    private void CheckHandDub(){ //checks who won the hand
        if(imgP0.getImage()!=null && imgP1.getImage()!=null){ //If the cards have been played
            int Domintantsuit = 0; //0 means none has a dominant suit, 1 = first card has dominant suit, 2 = second has dominant suit, 3 = both are a dominant suit
            if (Played.get(0).getCardSuitnum() == Trump){
                Domintantsuit = 1;
            }
            if(Played.get(1).getCardSuitnum() == Trump){
                if (Domintantsuit == 1){
                    Domintantsuit = 3;
                }
                else {
                    Domintantsuit = 2;
                }
            }
            boolean Dub = false;
            if (Domintantsuit == 1){ //First move
                //System.out.println("First Move wins (dominant)");
                if (Turn == true){
                    Dub = true;
                }
                else if (Turn == false){
                    Dub = false;
                }
            }
            else if(Domintantsuit == 2){ //Second Move
                //System.out.println("Second Move Wins(dominant)");
                if (Turn == true){
                    Dub = false;
                }
                else if (Turn == false){
                    Dub = true;
                }
                HandWinnerFound();
            }
            else if (Domintantsuit == 3){ //both have a dominat suit
                if (Played.get(0).getCardNumber()> Played.get(1).getCardNumber()){
                    //System.out.println("First move wins (Sirs)");
                    if (Turn == true){
                        Dub = true;
                    }
                    else if (Turn == false){
                        Dub = false;
                    }
                    HandWinnerFound();
                }
                else{
                    //System.out.println("Second move Wins(Sirs)");
                    if (Turn == true){
                        Dub = false;
                    }
                    else if (Turn == false){
                        Dub = true;
                    }
                    HandWinnerFound();
                }
            }
            else{
                int TrumpStart = Played.get(0).getCardSuitnum();
                if (Played.get(1).getCardSuitnum() == TrumpStart){ // if its the same as the card that the first move started with
                    if (Played.get(0).getCardNumber()> Played.get(1).getCardNumber()){ //if the first move is better
                        //System.out.println("First move wins(Better)");
                        if (Turn == true){
                            Dub = true;
                        }
                        else if (Turn == false){
                            Dub = false;
                        }
                        HandWinnerFound();
                    }
                    else{ //if second move is better
                        //System.out.println("Second move Wins(Better)");
                        if (Turn == true){
                            Dub = false;
                        }
                        else if (Turn == false){
                            Dub = true;
                        }
                        HandWinnerFound();
                    }
                }
                else{ //if the second move is not the same suit or trump
                    //System.out.println("First Move Wins(Start Suit)");
                    if (Turn == true){
                        Dub = true;
                    }
                    else if (Turn == false){
                        Dub = false;
                    }
                }
            }
            if (Dub == true){ //if the Server won
                HandsDubS++;
                Turn = true; //its the Servers turn
            }
            else{ //if the Client won
                HandsDubC++;
                Turn = false; // its the Clients turn
            }
            ChangeTurn();
            HandWinnerFound(); //one of the players won
        }
        else{ //if one has not gone
            if (Turn){
                socket.sendMessage("Enable");
            }
            else{
                socket.sendMessage("Disable");
                imgS0.setDisable(false);
                imgS1.setDisable(false);
                imgS2.setDisable(false);
                imgS3.setDisable(false);
                imgS4.setDisable(false);
                imgS5.setDisable(false);
                imgS6.setDisable(false);
                imgS7.setDisable(false);
                imgS8.setDisable(false);
                imgS9.setDisable(false);
            }
        }
    }

    private void HandWinnerFound(){
        imgP0.setImage(null); //reset the image
        imgP1.setImage(null);
        imgP2.setImage(null);
        imgP3.setImage(null);
        Played.clear(); //nothing in the Played list
        //HandSChosen = false;
        //HandCChosen = false;
        socket.sendMessage("DubHS" + HandsDubS); //sends the Wins
        ServerHands.setText(Integer.toString(HandsDubS));
        ClientHands.setText(Integer.toString(HandsDubC));
        socket.sendMessage("DubHC" + HandsDubC); //Sends the Wins

        if (HandsDubC + HandsDubS < GameNum){
            socket.sendMessage("HW!0");
            System.out.println("guess I Sent");
        }
        else{ //if there are no more cards to play
            SetEnded();
        }
    }

    private void SetEnded(){
        imgP0.setImage(null); //reset the image
        imgP1.setImage(null);
        imgP2.setImage(null);
        imgP3.setImage(null);
        sendTextField.clear();
        txtSubmit.clear();
        ClientHands.setText("Hands");
        ServerHands.setText("Hands");
        InstructionLbl.setText("How many Hands can you make");
        deck.clear();
        for (Card x: Alldeck){
            deck.add(x);
        }
        Hand2.clear();
        Hand1.clear();
        Played.clear();
        HandPoints.get(Set).setSPointScored(HandsDubS);
        socket.sendMessage("SSCR" + HandsDubS);
        HandPoints.get(Set).setCPointsScored(HandsDubC);
        socket.sendMessage("CSCR" + HandsDubC);
        System.out.println("RESET");
//        if (HandSwon.get(HandSwon.size()-1) == HandSwanted){
//            P1.add((HandSwanted+10));
//            socket.sendMessage("SSCR" + P1.get(P1.size()-1));
//        }
//        else {
//            P2.add(0);
//            socket.sendMessage("SSCR" + 0);
//        }
//        if (HandCwon.get(HandCwon.size()-1) == HandCwanted){
//            P2.add((HandCwanted+10));
//            socket.sendMessage("CSCR" + P2.get(P2.size()-1));
//        }
//        else {
//            P2.add(0);
//            socket.sendMessage("CSCR" + 0);
//        }
        Set++;
        socket.sendMessage("setSet" + Set);
        HandsDubS =0;
        HandsDubC = 0;
        if (!Reverse){
            if (GameNum == 2){
                Reverse = true;
            }
            GameNum --;
        }
        else if (GameNum == 10){
        }
        else{
            GameNum++;
        }
        HandSChosen = false;
        HandCChosen = false;
        int x = (int)Math.floor(Math.random()*2); //Make the Next one
        if (x == 0){
            Turn = true;
        }
        else{
            Turn = false;
        }
        if (ReverseSuit == false){
            Trump++;
            if (Trump == 3){
                ReverseSuit = true;
            }
        }
        else{
            Trump--;
            if (Trump == 0){
                ReverseSuit = false;
            }
        }
        TableChange();
        socket.sendMessage("Clear");
        ChangeTurn();
        checkDeal(); //Deals Cards
        InstructionLbl.setText("How many Hands can you make");
        btnStart.setDisable(true);
    }

    private void ObservableListChange(){
//        for (HandSPoints x : HandSP) {
//            P1.clear();
//            P1.add(x.getPoints());
//        }
//        for (HandCPoints x : HandCP) {
//            P2.clear();
//            P2.add(x.getPoints());
//        }
        TableChange();
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
    private void handleStart(){ //Starts the game by creating all the cards.
        for (int i = 2;i<15;i++) { //creates a object array of all the cards
            deck.add(new Card("C" +Integer.toString(i)));
            deck.add(new Card("D"+Integer.toString(i)));
            deck.add(new Card("H"+Integer.toString(i)));
            deck.add(new Card("S"+Integer.toString(i)));

            Alldeck.add(new Card("C" +Integer.toString(i)));
            Alldeck.add(new Card("D"+Integer.toString(i)));
            Alldeck.add(new Card("H"+Integer.toString(i)));
            Alldeck.add(new Card("S"+Integer.toString(i)));
        }
        for (int i = 0; i<5; i++){
            HandPoints.add(new HandSPoints("Spades"));
            HandPoints.add(new HandSPoints("Diamonds"));
            HandPoints.add(new HandSPoints("Clubs"));
            HandPoints.add(new HandSPoints("Hearts"));

            socket.sendMessage("Sirs" + "Spades");
            socket.sendMessage("Sirs" + "Diamonds");
            socket.sendMessage("Sirs" + "Clubs");
            socket.sendMessage("Sirs" + "Hearts");
        }

//        for (HandSPoints x : HandSP) {
//            Sirs.clear();
//            Sirs.add(x.getSir());
//        }
        int x = (int)Math.floor(Math.random()*2);
        if (x == 0){
            Turn = true;
        }
        else{
            Turn = false;
        }
        name1 = txtSubmit.getText();
        socket.sendMessage("Name" + name1);
        lblName1.setText(name1);
        start1 = true;
        ChangeTurn();

        //System.out.println(Turn);
        txtSubmit.clear();
        checkDeal(); //Deals Cards
        InstructionLbl.setText("How many Hands can you make");
        btnStart.setDisable(true);
        TableChange();
    }  //Starts the Game off

    @FXML
    private void handleUse(MouseEvent event) { //When you click a card, it calls this and USE is the card clicked
        //3 tasks,  remove from server, add to played cards hand,  send to client, remove from server hand.

        int imgClicked; ////////////in the client it is //Show + number where you want to place it + Number you removed for it + Image
        try {
            imgClicked =GridPane.getColumnIndex(((ImageView) event.getSource()));
        }
        catch (Exception e){
            imgClicked = 0;
        }
        Card Use = Hand1.get(imgClicked);
        RemovefromSimg(imgClicked); //remove the image of img from the Server hand pane
        AddtoPHandimg(Use);
        Played.add(Use);
        socket.sendMessage("Clk" + imgClicked);
        CheckHandDub();
        //System.out.println("Clicked");

    }
        //((ImageView) event.getSource());



    //Continue Game after deal
    /* Decide Trump
    Decide Hands dont allow the hands to be equal to be the number of cards
     * Give one person the start
      * Cause a Draw
      * Show which cards are playable
      * Select the Card you want to play
      * Let the client play their card
      * Check the heiracrchy
      * Remove cards
      * Add the hand
      * Run again with winner starting
      * At 0 deal new deck with 1- cards now and a diffrent trump and do this again
      * once at 0 run up again*/


    private void checkDeal(){ //Deals STARTING cards if both are ready
        if (start1 && start2){
            for (int i = 0; i <GameNum; i++){
                int card = (int) Math.floor(Math.random()*deck.size());
                Hand1.add(deck.get(card));
                deck.remove(card);
            }
            for (int i = 0; i <GameNum; i++){
                int card = (int) Math.floor(Math.random()*deck.size());
                Hand2.add(deck.get(card));
                deck.remove(card);
            }

            DealImages();

            socket.sendMessage("SZE" + Integer.toString(Hand2.size())); //send the amount of cards the client should anticipate to come.
            for (Card x:Hand1){
                socket.sendMessage("SCrd" + x.cName); //sends evrey card
            }
            for (Card x: Hand2){
                socket.sendMessage("CCrd"+x.cName); //sends every card
            }
        }
    } //Deals the starting cards only


    private void AddtoPHandimg(Card Use){ //Adds the card to the Image of the Played Hands.
        if (Played.size()== 0){
            imgP0.setImage(new Image(Use.pathName));
        }
        else if (Played.size() == 1){
            imgP1.setImage(new Image(Use.pathName));
        }
        else if (Played.size() == 2){
            imgP2.setImage(new Image(Use.pathName));
        }
        else if (Played.size() == 3){
            imgP3.setImage(new Image(Use.pathName));
        }
    }


    private void DealImages(){ //Shows cards in the game based on how many cards are in the array.
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
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));
            imgS5.setImage(new Image(Hand1.get(5).pathName));
            imgS6.setImage(new Image(Hand1.get(6).pathName));
            imgS7.setImage(new Image(Hand1.get(7).pathName));
            imgS8.setImage(new Image(Hand1.get(8).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));
            imgC4.setImage(new Image("resources/BACK-1.jpg"));
            imgC5.setImage(new Image("resources/BACK-1.jpg"));
            imgC6.setImage(new Image("resources/BACK-1.jpg"));
            imgC7.setImage(new Image("resources/BACK-1.jpg"));
            imgC8.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 8){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));
            imgS5.setImage(new Image(Hand1.get(5).pathName));
            imgS6.setImage(new Image(Hand1.get(6).pathName));
            imgS7.setImage(new Image(Hand1.get(7).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));
            imgC4.setImage(new Image("resources/BACK-1.jpg"));
            imgC5.setImage(new Image("resources/BACK-1.jpg"));
            imgC6.setImage(new Image("resources/BACK-1.jpg"));
            imgC7.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 7){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));
            imgS5.setImage(new Image(Hand1.get(5).pathName));
            imgS6.setImage(new Image(Hand1.get(6).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));
            imgC4.setImage(new Image("resources/BACK-1.jpg"));
            imgC5.setImage(new Image("resources/BACK-1.jpg"));
            imgC6.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 6){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));
            imgS5.setImage(new Image(Hand1.get(5).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));
            imgC4.setImage(new Image("resources/BACK-1.jpg"));
            imgC5.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 5){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));
            imgS4.setImage(new Image(Hand1.get(4).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));
            imgC4.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 4){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));
            imgS3.setImage(new Image(Hand1.get(3).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));
            imgC3.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 3){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));
            imgS2.setImage(new Image(Hand1.get(2).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));
            imgC2.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 2){
            imgS0.setImage(new Image(Hand1.get(0).pathName));
            imgS1.setImage(new Image(Hand1.get(1).pathName));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));
            imgC1.setImage(new Image("resources/BACK-1.jpg"));

        }
        else if (Hand2.size() == 1){
            imgS0.setImage(new Image("resources/BACK-1.jpg"));

            imgC0.setImage(new Image("resources/BACK-1.jpg"));

        }
    } //Puts dealt cards in the image views.

    @FXML
    private void SubmitBTN(){ //Submits the things put into the Textfield such as the hands or anything else
        if(InstructionLbl.getText().equals("How many Hands can you make")){
            int HandS = Integer.parseInt(txtSubmit.getText());
            HandPoints.get(Set).setSPointWanted(HandS);
            socket.sendMessage("WSSCR" + HandS);
            HandSChosen = true;
            TableChange();
            if (HandSChosen && HandCChosen){
                ChangeTurn();
            }
            if (Turn){
                turnChange.setText(name1 + " turn");
            }
            else{
                turnChange.setText(name2 + " turn");
            }
            //Now check if the Client has chosen and tell whom ever is first to go.
        }
    } //button next to submit field

    private void ChangeTurn(){
        if (Turn){
            socket.sendMessage("Disable");
            imgS0.setDisable(false);
            imgS1.setDisable(false);
            imgS2.setDisable(false);
            imgS3.setDisable(false);
            imgS4.setDisable(false);
            imgS5.setDisable(false);
            imgS6.setDisable(false);
            imgS7.setDisable(false);
            imgS8.setDisable(false);
            imgS9.setDisable(false);
            turnChange.setText(name1 + " turn");
        }
        else{
            socket.sendMessage("Enable");
            turnChange.setText(name2 + " turn");
        }

    }




}