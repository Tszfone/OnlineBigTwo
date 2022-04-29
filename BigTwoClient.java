import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is used to model a Big Two card game that supports 4 players playing over the Internet.
 * It implements the CardGame interface and NetworkGame interface.  
 * 
 * 
 * @author Cheng Tsz Fung
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {
	// an integer specifying the number of players
	private int numOfPlayers;
	// a deck of cards
	private Deck deck;
	// a list of players
	private ArrayList<CardGamePlayer> playerList;
	// a list of hands played on the table
	private ArrayList<Hand> handsOnTable;
	// an integer specifying the playerID (i.e., index) of the local player
	private int playerID;
	// a string specifying the name of the local player
	private String playerName;
	// a string specifying the IP address of the game server
	private String serverIP;
	// an integer specifying the TCP port of the game server
	private int serverPort;
	// a socket connection to the game server
	private Socket sock;
	// an ObjectOutputStream for sending messages to the server
	private ObjectOutputStream oos;
	// an ObjectIntputStream for sending messages to the server
	private ObjectInputStream ois;
	// an integer specifying the index of the player for the current turn.
	private int currentIdx;
	// a Big Two table which builds the GUI for the game and handles all user actions
	private BigTwoTable table;
	// declare round for storing the number of round
	private int round;
	// declare isLegalMove for checking whether the move is legal or not 
	private boolean isLegalMove;
	// declare winner for storing the name of the player
	private String winner;
	// a boolean value that state whether the game end
	private boolean gameEnd;
	
	
	
	
	public BigTwoClient() {
		gameEnd = false;
		isLegalMove = true;
		currentIdx = -1;
		playerList = new ArrayList<CardGamePlayer>();
		// create 4 players and add them to the playerList
		playerList.add(new CardGamePlayer());
		playerList.add(new CardGamePlayer());
		playerList.add(new CardGamePlayer());
		playerList.add(new CardGamePlayer());
		// create a Big Two table which builds the GUI for the game and handles user actions
		table = new BigTwoTable(this);
		// make a connection to the game server
		makeConnection();
	}
	
	/**
	 * This method is for getting gameEnd.
	 * 
	 * @return gameEnd whether the game end or not
	 */
	public boolean getGameEnd() {
		return gameEnd;
	}
	
	
	/**
	 * This method is for getting the playerID (i.e., index) of the local player.
	 * 
	 * @return playerID The playerID (i.e., index) of the local player
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * This method is for setting the playerID (i.e., index) of the local player. 
	 * 
	 * @param playerID The playerID (i.e., index) of the local player
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
		
	}

	/**
	 * This method is for getting the name of the local player.
	 * 
	 * @return playerName The name of the local player.
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * This method is for setting the name of the local player. 
	 * 
	 * @param playerName The name of the local players
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * This method is for getting the IP address of the game server.
	 * 
	 * @return serverIP The IP address of the game server.
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * This method is for setting the IP address of the game server.
	 * 
	 * @param serverIP The IP address of the game server.
	 */
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	/**
	 * This method is for getting the TCP port of the game server.
	 * 
	 * @return serverPort The TCP port of the game server.
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * This method is for setting the TCP port of the game server.
	 * 
	 * @param serverPort The TCP port of the game server.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * This method is for making a socket connection with the game server.
	 */
	public void makeConnection() {
		setServerIP("127.0.0.1");
		setServerPort(2396);
		Socket socket;
		try {
			socket = new Socket(serverIP, serverPort);
			
			// create ObjectOutputStream
			oos = new ObjectOutputStream(socket.getOutputStream());
			// create ObjectInputStream
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("networking established");
		} catch (Exception ex) {
			table.printMsg("Can't connect to the server.");
			ex.printStackTrace();
		}
		// create a new thread
		Thread readerThread = new Thread(new ServerHandler());
		readerThread.start();
	}
	
	
	
	

	/**
	 * This method is for parsing the messages received from the game server. 
	 * 
	 * @param message The messages received from the game server.
	 */
	public synchronized void parseMessage(GameMessage message) {
		if (message.getType() == CardGameMessage.PLAYER_LIST) {
			// set the playerID of the local player
			setPlayerID(message.getPlayerID());
			String[] names = (String[]) message.getData();
			// update the names in the player list
			for (int i = 0; i < playerList.size(); i++) {
				playerList.get(i).setName(names[i]);
			}
			sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
			
			
		} else if (message.getType() == CardGameMessage.JOIN) {
			String name = (String) message.getData();
			// add a new player to the player list by updating his/her name
			playerList.get(message.getPlayerID()).setName(name);
			
			/* 
			 * If the playerID is identical to the local player, 
			 * the client send a message of type READY, with playerID and data being -1 and null, respectively, to the server
			 */
			if (message.getPlayerID() == playerID) {
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			}
			
			table.updateLabels(CardGameMessage.JOIN, message.getPlayerID(), name);
			
			
		} else if (message.getType() == CardGameMessage.FULL) {
			table.printMsg("The server is full.");
			table.printMsg("No one can joing the server now.");
			
		} else if (message.getType() == CardGameMessage.QUIT) {
			// remove a player from the game by setting his/her name to an empty string
			playerList.get(message.getPlayerID()).setName("");
			table.updateLabels(CardGameMessage.QUIT, message.getPlayerID(), "");
			// stop the game
			table.disable();
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			
		} else if (message.getType() == CardGameMessage.READY) {
			String name = playerList.get(message.getPlayerID()).getName();
			// display a message in the text area of the BigTwoTable that the specified player is ready
			table.printMsg(name + " is ready.");
			
		} else if (message.getType() == CardGameMessage.START) {
			deck = (Deck) message.getData();
			// start a new game with the given deck of cards (already shuffled)
			start((BigTwoDeck) deck);
			table.printMsg("All players are ready. Game starts");
			printTurn();
			
		} else if (message.getType() == CardGameMessage.MOVE) {
			int[] selectedCardIdx = (int[]) message.getData();
			// check the move played by the specified player
			checkMove(message.getPlayerID(), selectedCardIdx);
			
		} else if (message.getType() == CardGameMessage.MSG) {
			String chatMsg = (String) message.getData();
			// display the chat message in the chat window
			table.printChatMsg(chatMsg);
		}
		
	}

	/**
	 * This method is for sending the specified message to the game server.
	 * 
	 * @param message The specified message need to be sent to the game server.
	 */
	public synchronized void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This class implements the Runnable interface.
	 * 
	 * @author Cheng Tsz Fung
	 *
	 */
	public class ServerHandler implements Runnable{

		/**
		 * This method handle message from the server.
		 */
		public void run() {
			GameMessage gameMessage;
			try {
				// reads incoming messages from the server
				while ((gameMessage = (CardGameMessage) ois.readObject()) != null) {
					parseMessage(gameMessage);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	


	/**
	 * This method is for getting the number of players.
	 * 
	 * @return numOfPlayers The number of players.
	 */
	public int getNumOfPlayers() {
		return numOfPlayers;
	}

	/**
	 * This method is for  getting the deck of cards being used.
	 * 
	 * @return deck The deck of cards being used.
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * This method is for getting the list of players.
	 * 
	 * @return playerList The list of players.
	 */
	public ArrayList<CardGamePlayer> getPlayerList() {
		return playerList;
	}

	/**
	 * This method is for getting the list of hands played on the table.
	 * 
	 * @return handsOnTable The list of hands played on the table.
	 */
	public ArrayList<Hand> getHandsOnTable() {
		return handsOnTable;
	}

	/**
	 * This method is for getting the index of the player for the current turn.
	 * 
	 * @return currentIdx  The index of the player for the current turn.
	 */
	public int getCurrentIdx() {
		return currentIdx;
	}

	/**
	 * This method is for starting/restarting the game with a given shuffled deck of cards.
	 */
	public void start(Deck deck) {
		// declare cardList for referencing cards
		CardList cardList;
		// declare card for referencing card
		Card card;		
		
		// remove all the cards from the players as well as from the table
		for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).removeAllCards();
		}
		handsOnTable = new ArrayList<Hand>();
		
		// distribute cards from deck to each player. Start from player 0 to 3 and then loop back to player 0 
		for (int i = 0; i < 52; i++) {
			// get a card from deck and add it to a player
			playerList.get(i%4).addCard(deck.getCard(i));
		}
	    // sort each players cards in Hand
		for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).sortCardsInHand();
		}
				
				
		int i = 0;
		// find which player has diamond 3 and set him be the first player
		for (CardGamePlayer player : playerList) {
			cardList = player.getCardsInHand();
			for (int j = 0; j < cardList.size(); j++) {
				card = cardList.getCard(j);
				if (card.suit == 0 && card.rank == 2) {
					currentIdx = i;
					table.setActivePlayer(currentIdx);
					}
				}
				i++;
			}
				
		round = 0;
		table.repaint();
	}
		
	private void printTurn() {
		if (isLegalMove == true) {
			table.printMsg(playerList.get(currentIdx).getName() + "'s turn:");
		}
	}
	

	/**
	 * This method is for making a move by a player with the specified playerID using the cards specified by the list of indices.
	 * 
	 * @param playerID The playerID of the player who makes the move.
	 * @param cardIdx A regular array of integers specifying the indices of the selected cards.
	 */
	public void makeMove(int playerID, int[] cardIdx) {
		CardGameMessage message = new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
		sendMessage(message);
	}

	/**
	 * This method is for checking a move made by a player.
	 * 
	 * @param playerID The playerID of the player who makes the move.
	 * @param cardIdx A regular array of integers specifying the indices of the selected cards.
	 */
	public void checkMove(int playerID, int[] cardIdx) {
		// declare currentPlayer for storing the name of the current active player
		String currentPlayer = playerList.get(currentIdx).getName();
		// declare greatestPlayer for plays the last hand on table
		String greatestPlayer = "";
		
		
		// get the greatestPlayer
		if (handsOnTable.isEmpty() == false) {
			greatestPlayer = handsOnTable.get(handsOnTable.size()-1).getPlayer().getName();
		}
		
		String message = "";
		
		Hand validHand = null;
		CardList selectedCards = null;
		int[] selectedCardIdx = cardIdx;
		
		
		// check whether active player did a legal move
		isLegalMove = false;
		
		// check whether the active player passed or not
		if (selectedCardIdx != null) {
			// check whether select no cards and click play button
			
			if (selectedCardIdx[0] != -999) {
				// get the cards which played by the active player
				selectedCards = playerList.get(currentIdx).play(selectedCardIdx);
				validHand = composeHand(playerList.get(currentIdx), selectedCards);
									
				// able to compose a valid hand, then check it is legal or not
				if (validHand != null) {
								
					//The first active player must play diamond 3 card
					if (round == 0) {
						if (validHand.getCard(0).suit == 0 && validHand.getCard(0).rank == 2) {
							isLegalMove = true;
							
							message = "{" + validHand.getType() + "} ";
							for (int i = 0; i < validHand.size(); i++) {
								message += "[" + validHand.getCard(i) + "] ";
							}
							table.printMsg(message);
							playerList.get(currentIdx).removeCards(selectedCards);
							handsOnTable.add(validHand);
						}
									
					// after the first round
					} else {
						
						// check whether the current player whether played the last hand of cards on the table or not
						if (currentPlayer.equals(greatestPlayer)) {
							
							// he can can play a hand of any legal combination of cards regardless of the last hand he played on the table
							isLegalMove = true;
							// reset greatestPlayer
							greatestPlayer = "";
							
							// setup the message
							message = "{" + validHand.getType() + "} ";
							for (int i = 0; i < validHand.size(); i++) {
								message += "[" + validHand.getCard(i) + "] ";
							}
							table.printMsg(message);
							playerList.get(currentIdx).removeCards(selectedCards);
							handsOnTable.add(validHand);
										
						} else {
							// check whether the current hand on table has the same type with validHand
							if (validHand.size() == handsOnTable.get(handsOnTable.size() - 1).size()) {
								// check whether the validHand beats the current hand on the table
								if (validHand.beats(handsOnTable.get(handsOnTable.size() - 1))) {
									isLegalMove = true;
									
									//setup the message
									message = "{" + validHand.getType() + "} ";
									for (int i = 0; i < validHand.size(); i++) {
										message += "[" + validHand.getCard(i) + "] ";
									}
									table.printMsg(message);
									playerList.get(currentIdx).removeCards(selectedCards);
									handsOnTable.add(validHand);	
								}
							}
						}	
					 }		
				  } 
			}
			
								
		} else {
			// check the current whether played the last hand of cards on the table or not
			// if yes, then he cannot pass
			if (currentPlayer.equals(greatestPlayer) == false && round != 0) {
				message = "{Pass}";
				table.printMsg(message);
				isLegalMove = true;
			}	
		}
		
		
		
		// if the move is illegal, then print the warning message to msgArea	
		if (isLegalMove == false) {
			
			// illegal move
			if (selectedCardIdx != null) {
				
				if (selectedCardIdx[0] != -999) {
					
					message = "Not a legal move!!!";
				// selected nothing and click play
				} else {
					
					message = "No card selected Not a legal move!!!";
				}
			} else {
				message = "{Pass} <== Not a legal move!!!";
			}
			table.printMsg(message);
		// if player make legal move, then go to next round
		} else {
			currentIdx = (currentIdx + 1) % 4;
			table.setActivePlayer(currentIdx);
			round++;
		}
		
		// refresh gui
		table.repaint();
		
		// check whether the game ends
		CardList cardList;
		if(endOfGame() == true) {
			table.disable(); // no do
			gameEnd = true;
			// print Game result
			table.printGameResult();
			table.clearMsgArea();
			table.clearChatMsgArea();
			table.clearLabel();
		 } else {
			 printTurn();
		 }
		
	}

	/**
	 * This method is for checking if the game ends.
	 * 
	 * @return end Whether the game ends or not.
	 */
	public boolean endOfGame() {
		boolean end = false;
		winner = "";
		
		for(int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).getNumOfCards() == 0) {
				winner = playerList.get(i).getName();
				end = true;
			}
		}
		
		
		return end;
	}
	
	
	/**
	 * This static method returns a valid hand from the specified list of cards of the player.
	 * 
	 * @param player The player who plays this hand.
	 * @param cards The cards which is played by the player.
	 * 
	 * @return hand The valid hand or null if there is no valid hand.
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand = new Single(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new Pair(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new Triple(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new Straight(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new FullHouse(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new Quad(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new StraightFlush(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
		
		hand = new Flush(player, cards);
		// check the hand valid or not
		if (hand.isValid()) {
			return hand;
		}
	
		return null;
	}
	
	
	/**
	 * This method is for creating an instance of BigTwoClient.
	 * 
	 * @param args An array of strings.
	 */
	public static void main(String[] args) {
		BigTwoClient game = new BigTwoClient();
	}

}
