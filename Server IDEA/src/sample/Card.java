package sample;
public class Card {
    
    String cSuit;
    int cNumber;
    String pathName;
    String cName;
    
    // contructor that starts the formation of the card path
    // the methods of this class allow the main class to obtain certain properties of a 
    // single card.
    public Card(String cName)
    {
        pathName = "resources/" + cName + ".jpg";
        this.cName = cName;
        parseCardName(cName);
    }
    // 
    public void parseCardName(String cName)
    {
        cSuit = cName.substring(0,1); // creates the color of a card
        cNumber = Integer.parseInt(cName.substring(1)); // creates the value of a card       
    }
    public int getCardNumber() // sends the card value when called
    {
        return cNumber;
    }
    public String getCardSuit() // sends the card suit when called
    {
        return cSuit;
    }
    public String getCardPath() // sends card path when called/ loaction in resources
    {
        return pathName;
    }

    public String getCardName()
    {
        return cName;
    }

    public int getCardSuitnum() // sends the card value when called
    {
        if (cSuit.equals("S")){
            return 0;
        }
        else if (cSuit.equals("D")){
            return 1;
        }
        else if (cSuit.equals("C")){
            return 2;
        }
        else if (cSuit.equals("H")){
            return 3;
        }
        else{
            return 4;
        }
    }

    
}
