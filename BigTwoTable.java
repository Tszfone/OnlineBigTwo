import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import java.io.*;
/**
 * This class implements the CardGameTable interface.
 * It builds a GUI for the Big Two card game and handle all user actions.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class BigTwoTable implements CardGameTable{
	// a card game associates with this table
	private BigTwoClient game;
	// a boolean array indicating which cards are being selected
	private boolean[] selected;
	// an array of CardGamePlayer
	private ArrayList<CardGamePlayer> playerList; 
	// an array of Hand which stores the hands on the tables
	private ArrayList<Hand> handsOnTable;
	// an integer specifying the index of the active player
	private int activePlayer;
	// an array of integer which stores the selected card indices
	private int[] selectedCardIdx;
	
	// the main window of the application
	private JFrame frame;
	// a panel for showing the cards of each player and the cards played on the table
	private JPanel bigTwoPanel;
	// a panel for showing play and pass button
	private JPanel buttonsPanel;
	// a panel for typing chat message
	private JPanel chatPanel;
	// a ¡§Play¡¨ button for the active player to play the selected cards
	private JButton playButton;
	// a ¡§Pass¡¨ button for the active player to pass his/her turn to the next player
	private JButton passButton;
	// a text area for showing the current game status as well as end of game messages
	private JTextArea msgArea;
	// a text area for viewing chat messages
	private JTextArea chatArea;
	private JTextField chatInputField;
	// an array of JLabel which stores the name of each player
	private ArrayList<JLabel> playerLabels;
	// an array of JLabel which store player avatar images
	private ArrayList<JLabel> playerIconLabels;
	// a JLabel to show who plays the last hand on table
	private JLabel lastHandPlayer;
	// a JLabel to show "Message: "
	private JLabel msgLabel;
	
	private JPanel rightPanel;
	
	
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem1, menuItem2;  
	
	// a 2D array storing the images for the faces of the cards
	private ImageIcon[][] cardImageIcons;
	// an ImageIcon for the backs of the cards.
	private ImageIcon cardBackImageIcon;
	// an array storing the ImageIcon for the avatars
	private ImageIcon[] avatars;
	
	// local player name
	private String playerName;
	
	private String filename = "";
	private final String[] rank = {"a", "2", "3", "4", "5", "6", "7", "8", "9", "t", "j", "q", "k"};
	private final String[] suit = {"d","c","h","s"};
	
	
	
	/**
	 * This constructor creates a BigTwoTable.
	 * 
	 * @param game The card game associates with this tables.
	 */
	public BigTwoTable(BigTwoClient game) {
		this.game = game;
		playerList = game.getPlayerList();
		handsOnTable = game.getHandsOnTable();
		selected = new boolean[13];
		activePlayer = -1;  
		cardBackImageIcon = new ImageIcon("images/b.gif");
		cardImageIcons = new ImageIcon[4][13];
		avatars = new ImageIcon[4];
		
		resetSelected();
		
		// initialize cardImageIcons
		for (int i = 0; i < cardImageIcons.length; i++) {
			for (int j = 0; j < cardImageIcons[0].length; j++) {
				filename = rank[j] + "" + suit[i];
				cardImageIcons[i][j] = new ImageIcon("images/" + filename + ".gif");
			}
		}	
		
		// initialize avatars
		for (int i = 0; i < avatars.length; i++) {
			avatars[i] = new ImageIcon("images/p" + i + ".png");
		}
	
		initializeGUI();
		playerName = JOptionPane.showInputDialog("Player Name: ");
		game.setPlayerName(playerName);
		
	}
	
	
	/**
	 * This method sets the index of the active player.
	 * 
	 * @param activePlayer The current player.
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= playerList.size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
		
	}
	
	/**
	 * This method returns an array of indices of the cards selected.
	 * 
	 * @return selectedCardIdx The card indices which selected by the active player.
	 */
	public int[] getSelectedCardIdx() {
		return selectedCardIdx;
	}

	
	// This method initializes all the GUI components
	private void initializeGUI() {
		// setup the frame
		frame = new JFrame();
		frame.setSize(1366, 768);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(false);
		frame.setMinimumSize(new Dimension(1366, 768));
		frame.setPreferredSize(new Dimension(1366, 768));
		frame.setTitle("Big Two");
        frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		bigTwoPanel = new BigTwoPanel();
		bigTwoPanel.setBackground(new Color(41, 163, 41));
		
		// create play and pass button
		buttonsPanel = new JPanel();
		playButton = new JButton("Play");
		passButton = new JButton("Pass");
		
		// create chat input field
		chatPanel = new JPanel();
		msgLabel = new JLabel("Message: ");
		chatInputField = new JTextField();
		chatInputField.addActionListener(new EnterKeyListener());
		
		rightPanel = new JPanel();
		
		// create player Labels
		playerLabels = new ArrayList<JLabel>();
		for (int i = 0; i < 4 ; i++) {
			playerLabels.add(new JLabel());
		}
		lastHandPlayer = new JLabel("Played by ");
		
		// create scrollable msgArea
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		JScrollPane msgScrollPane = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// create scrollable chatArea
		chatArea = new JTextArea();
		chatArea.setForeground(Color.blue);
		chatArea.setEditable(false);
		JScrollPane chatScrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		rightPanel.setLayout(new GridLayout(2,1));
		rightPanel.add(msgScrollPane);
		rightPanel.add(chatScrollPane);
		
		
		
		// create menu and menu item
		menu = new JMenu("Game");
		menuItem1 = new JMenuItem("Connect");
		menuItem1.addActionListener(new ConnectMenuItemListener());
		menuItem2 = new JMenuItem("Quit");
		menuItem2.addActionListener(new QuitMenuItemListener());
		menu.add(menuItem1);
		menu.add(menuItem2);
		menu.setPopupMenuVisible(true);
		menuBar = new JMenuBar();
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		
		// adding bigTwoPanel
		bigTwoPanel.setLayout(null);
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		c1.gridheight = 1;
		c1.gridwidth = 1;
		c1.anchor = GridBagConstraints.CENTER;
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = 0.5;
		c1.weighty = 1;
		
		bigTwoPanel.setLayout(null);
		
		// adding player labels
		Dimension size = playerLabels.get(0).getPreferredSize();
		int y = 0;
		for(int i = 0; i < playerLabels.size(); i++) {
			playerLabels.get(i).setBounds(10, y, size.width, size.height);
			bigTwoPanel.add(playerLabels.get(i));
			y+=115;
		}
		
		// adding player avatars
		playerIconLabels = new ArrayList<JLabel>();
		
		for (int i = 0; i < playerList.size() ; i++) {
			y = playerLabels.get(i).getY() + 20;
			playerIconLabels.add(new JLabel());
			if (playerLabels.get(i).getText() != "" && playerLabels.get(i).getText() != null) {
				playerIconLabels.get(i).setIcon(avatars[i]);
			}
			size = playerIconLabels.get(i).getPreferredSize();
			playerIconLabels.get(i).setBounds(10, y, size.width, size.height);
			bigTwoPanel.add(playerIconLabels.get(i));
		}
		
		
		// adding last hand player label
		lastHandPlayer.setForeground(Color.black);
		size = lastHandPlayer.getPreferredSize();
		y = playerIconLabels.get(playerIconLabels.size()-1).getY() + 100;
		lastHandPlayer.setBounds(10, y, size.width, size.height);
		bigTwoPanel.add(lastHandPlayer);

		frame.add(bigTwoPanel,c1);
		
		// adding msgArea 
		c1.gridx = 1;
		c1.weighty = 1;
		c1.gridheight = 1;
		//frame.add(msgScrollPane,c1);
		frame.add(rightPanel, c1);
		
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridheight = 1;
		c1.gridwidth = 1;
		c1.weightx = 0;
		c1.weighty = 0;
		c1.fill = GridBagConstraints.BOTH;
		
		// adding buttons
		buttonsPanel.setLayout(new GridBagLayout());
		
		playButton.addActionListener(new PlayButtonListener());
		passButton.addActionListener(new PassButtonListener());
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 0;
		c2.gridheight = 1;
		c2.gridwidth = 1;
		c2.weightx = 0;
		c2.weighty = 0;
		//c2.anchor = GridBagConstraints.EAST;
		c2.insets = new Insets(5,10,5,10);
		buttonsPanel.add(playButton, c2);
		c2.gridx = 1;
		buttonsPanel.add(passButton, c2);
		frame.add(buttonsPanel, c1);
		
		
		chatPanel.setLayout(new GridBagLayout());
		c2.gridx = 0;
		c2.gridy = 0;
		
		chatPanel.add(msgLabel, c2);
		c2.gridx = 1;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.BOTH;
		chatPanel.add(chatInputField, c2);
		c1.gridx = 1;
		c1.gridy = 1;
		frame.add(chatPanel, c1);
		
		frame.setVisible(true);
		disable();
	}
	
	/**
	 * This method updates the playerLabels and playerIconLabels.
	 * 
	 * @param type The game message type.
	 * @param playerID The player that need to update his playerLabel name.
	 * @param name The name of the player.
	 */
	public void updateLabels(int type, int playerID, String name) {
		Dimension size;
		
		// if a new player join the game, update playerLabel
		if (type == CardGameMessage.JOIN) {
			for (int i = 0; i < playerLabels.size(); i++) {
				if ( i == playerID && playerList.get(i).getName() != "" && playerList.get(i).getName() != null) {
					playerLabels.get(i).setText(name);
					size = playerLabels.get(i).getPreferredSize();
					playerLabels.get(i).setSize(size);
				} else {
					playerLabels.get(i).setText(playerList.get(i).getName());
					size = playerLabels.get(i).getPreferredSize();
					playerLabels.get(i).setSize(size);
				}
				
				if (playerLabels.get(i).getText() != null) {
					playerIconLabels.get(i).setIcon(avatars[i]);
					size = playerIconLabels.get(i).getPreferredSize();
					playerIconLabels.get(i).setSize(size);
				}
			}
		// if a player leave, remove his/her playerLabel and avatar
		} else if (type == CardGameMessage.QUIT) {
			playerLabels.get(playerID).setText("");
			playerIconLabels.get(playerID).setIcon(null);
		}
		
	}
	


	/**
	 * This method get an array of indices of the cards selected.
	 * 
	 * @return selectedIdx The array of indices of the cards selected.
	 */
	public int[] getSelected() {
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j] == true) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}


	/**
	 * This method resets the list of selected cards.
	 */
	public void resetSelected() {
		for(int i = 0; i < selected.length; i++) {
			selected[i] = false;
		}
		
	}


	/**
	 * This method repaint the GUI.
	 */
	public void repaint() {
		// declare x and y for storing the coordinate of a swing component
		int x, y;
		
		
		int suitIdx;
		int rankIdx;
		
		Hand hand;
		CardList cardList;
		JLabel cardLabel;
		ImageIcon icon;
		// declare size for storing the size of a swing component
		Dimension size;
		
		bigTwoPanel.removeAll();
		
		// add playerLabels on bigTwoPanel
		
		y = 0;
		for (int i = 0; i < playerLabels.size(); i++) {
			// set player Labels text as player names
			if (i != game.getPlayerID()) {
				playerLabels.get(i).setText(playerList.get(i).getName());
			} else {
				playerLabels.get(i).setText("You");
			}
			
			size = playerLabels.get(i).getPreferredSize();
			playerLabels.get(i).setBounds(10, y, size.width, size.height);
			bigTwoPanel.add(playerLabels.get(i));
			y+=115;
			
			// mark the activePlayer to be blue, if not, then mark as black
			if (i == activePlayer) {
				playerLabels.get(i).setForeground(Color.blue);
			} else {
				playerLabels.get(i).setForeground(Color.black);
			}	
		}
		
		
		// add player avatars on bigTwoPanel
		for (int i = 0; i < avatars.length; i++) {
			y = playerLabels.get(i).getY() + 20;
			size = playerIconLabels.get(i).getPreferredSize();
			playerIconLabels.get(i).setBounds(10, y, size.width, size.height);
			bigTwoPanel.add(playerIconLabels.get(i));
		}
		
		
		lastHandPlayer.setForeground(Color.black);
		size = lastHandPlayer.getPreferredSize();
		lastHandPlayer.setBounds(10, y+100, size.width, size.height);
	
		bigTwoPanel.add(lastHandPlayer);
		
		
		// add cards on bigTwoPanel
		for (int i = 0; i < playerList.size(); i++) {
			cardList = playerList.get(i).getCardsInHand();
			x = playerLabels.get(i).getX() + 130;
			y = playerLabels.get(i).getY() + 20;
			for (int j = 0; j < cardList.size(); j++) {
				// add cardImageIcon instead of cardBackImageIcon to bigTwoPanel if player i is the active player
				
				if (i == game.getPlayerID() && game.getGameEnd() == false) {
					suitIdx = cardList.getCard(j).suit;
					rankIdx = cardList.getCard(j).rank;
					// get the corresponding cardImageIcon
					icon = cardImageIcons[suitIdx][rankIdx];
					cardLabel = new CardLabel(icon, j);
					
					// add the cardLabel to corresponding position
					size = cardLabel.getPreferredSize();
					cardLabel.setBounds(x, y, size.width, size.height);
					
					if (i != activePlayer) {
						disable();
					} else {
						enable();
					}	
				// hide other players' cards and add cardBackImageIcon to bigTwoPanel
				} else {
					icon = cardBackImageIcon;
					cardLabel = new JLabel(icon);
					
					// add the cardLabel to corresponding position
					size = cardLabel.getPreferredSize();
					cardLabel.setBounds(x, y, size.width, size.height);  
				}
				bigTwoPanel.add(cardLabel);
				bigTwoPanel.setComponentZOrder(cardLabel, 0);
				x+=15;
				
			}
			
		}
		
		
		// add cards of handsOnTable 
		handsOnTable = game.getHandsOnTable();
		if (handsOnTable.isEmpty() == false) {
			// get the last hand on table
			hand = handsOnTable.get(handsOnTable.size()-1);
			// check the hand on table whether played by the local player or not. If yes, display "you" instead of name
			if ((hand.getPlayer().getName()).equals(playerList.get(game.getPlayerID()).getName())) {
				lastHandPlayer.setForeground(Color.blue);
				lastHandPlayer.setText("Played by you");
				
			} else {
				lastHandPlayer.setForeground(Color.black);
				lastHandPlayer.setText("Played by " + hand.getPlayer().getName());
			}
			
			size = lastHandPlayer.getPreferredSize();
			// place the lastHandPlayer label into specified position
			lastHandPlayer.setBounds(lastHandPlayer.getX(), lastHandPlayer.getY(), size.width, size.height);
			// get the coordinate of the lastHandPlayer label, so that we can know where to place those cardLabels
			x = lastHandPlayer.getX();
			y = lastHandPlayer.getY() + 20;
			// add each cardLabel to bigTwoPanel
			for (int j = 0; j < hand.size(); j++) {
				suitIdx = hand.getCard(j).suit;
				rankIdx = hand.getCard(j).rank;
				
				// get the corresponding ImageIcon
				icon = cardImageIcons[suitIdx][rankIdx];
				cardLabel = new JLabel(icon);
				size = cardLabel.getPreferredSize();
				// place the lastHandPlayer label into specified position
				cardLabel.setBounds(x, y, size.width, size.height); 
				bigTwoPanel.add(cardLabel);
				bigTwoPanel.setComponentZOrder(cardLabel, 0);
				x+=15;
				
			}
		}
		frame.repaint();
		frame.revalidate();
	}


	/**
	 * This method prints the specified string to the message area of the GUI.
	 * 
	 * @param msg The message need to be printed in the msgArea.
	 */
	public void printMsg(String msg) {
		msgArea.append(msg + "\n");
	}
	
	/**
	 * This method prints the chat message to the chat area of the GUI.
	 * 
	 * @param chatMsg The message need to be printed in the chatArea.
	 */
	public void printChatMsg(String chatMsg) {
		chatArea.append(chatMsg + "\n");
	}
	
	/**
	 * This method displays the game result in showMessageDialog.
	 */
	public void printGameResult() {
		String result;
		CardList cardList;
		result = "Game ends\n";
		// print who is the winner and the number of remaining cards of losers
		for (CardGamePlayer player : playerList) {
			cardList = player.getCardsInHand();
			if (cardList.size() != 0) {
				result += player.getName() + " has " + cardList.size() + " cards in hand.\n";
			} else {
				result += player.getName() + " wins the game.\n";
			}
		}
		JOptionPane.showMessageDialog(frame, result);
		game.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}


	/**
	 * This method clears the message area of the GUI.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}
	
	/**
	 * This method clears the chat area of the GUI.
	 */
	public void clearChatMsgArea() {
		chatArea.setText("");
	}
	
	/**
	 * This method clears the lastHandPlayer label text.
	 */
	public void clearLabel() {
		lastHandPlayer.setText("");
	}


	/**
	 * This method resets the GUI.
	 */
	public void reset() {
		lastHandPlayer.setText("Played by");
		resetSelected();
		clearMsgArea();
		enable();
	}


	/**
	 * This method enables user interactions with the GUI.
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		bigTwoPanel.setEnabled(true);
	}


	/**
	 * This method disables user interactions with the GUI
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		bigTwoPanel.setEnabled(false);
	}
	
	
	
	
	/**
	 * This inner class extends the JPanel class.
	 * 
	 * @author Cheng Tsz Fung
	 *
	 */
	public class BigTwoPanel extends JPanel {
		
		public BigTwoPanel() {
			setBackground(new Color(41, 163, 41));
		}
		
	}
	
	/**
	 * This inner class extends the JLabel class and implements the MouseListener interface.
	 * 
	 * @author Cheng Tsz Fung
	 *
	 */
	public class CardLabel extends JLabel implements MouseListener {
		private int cardIdx;
		
		/**
		 * This constructor is for creating a CardLabel.
		 * 
		 * @param icon A ImageIcon of a card.
		 * @param cardIdx The index of a card.
		 */
		public CardLabel(ImageIcon icon, int cardIdx) {
			super(icon);
			this.cardIdx = cardIdx;
			addMouseListener(this);
		}
		
		/**
		 * This method is for getting the index of a card in player hand.
		 * 
		 * @return cardIdx The index of a card in player hand.
		 */
		public int getCardIdx() {
			return cardIdx;
		}

		/**
		 * This method handles the event when the cardLabel is clicked by mouse.
		 * 
		 * @param e The mouse click event.
		 * 
		 */
		public void mouseClicked(MouseEvent e) {
			JLabel cardLabel = (CardLabel) e.getComponent();
			Dimension size = cardLabel.getPreferredSize();
			int cardIdx = ((CardLabel) cardLabel).getCardIdx();
			
			// if the card is not selected, then raise it 
			if (selected[cardIdx] == false ) {
				cardLabel.setBounds(cardLabel.getX(), cardLabel.getY()-10, size.width, size.height);
				selected[cardIdx] = true; 
			// if the card is already selected, then lower it
			} else {
				cardLabel.setBounds(cardLabel.getX(), cardLabel.getY()+10, size.width, size.height);
				selected[cardIdx] = false; 
			}
			
		}

		/**
		 * This is a dummy method.
		 */
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * This is a dummy method.
		 */
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * This is a dummy method.
		 */
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * This is a dummy method.
		 */
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	
	private class EnterKeyListener implements ActionListener {
		
		// This method handles enter press events for pressing enter on the keyboard.
		public void actionPerformed(ActionEvent e) {
			
			try {
				// sends the text in the text field to the server
				game.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chatInputField.getText()));
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// resets the text field
			chatInputField.setText("");
			chatInputField.requestFocus();
		}
	}
	
	private class PlayButtonListener implements ActionListener {
		// declare nothingSelected for indicating this move is selected nothing
		private final int[] nothingSelected = {-999};
		
		// This method handles button-click events for the ¡§Play¡¨ button. 
		public void actionPerformed(ActionEvent e) {
			// get an array of the selected Card indices
			selectedCardIdx = getSelected();
			if (selectedCardIdx == null) {
				selectedCardIdx = nothingSelected;
			}
			game.makeMove(activePlayer, selectedCardIdx);
			resetSelected();
		}
		
	}
	
	private class PassButtonListener implements ActionListener {
		// his methods handle button-click events for the ¡§Pass¡¨ button.
		public void actionPerformed(ActionEvent e) {
			// set no cards being selected
			selectedCardIdx = null;
			game.makeMove(activePlayer, selectedCardIdx);  
			resetSelected();
		}
		
	}
	
	private class ConnectMenuItemListener implements ActionListener {
		// This method handles menu-item-click events for the ¡§Connect¡¨ menu item.
		public void actionPerformed(ActionEvent e) {
			game.makeConnection();	
		}
		
	}
	
	private class QuitMenuItemListener implements ActionListener {
		// This method handles menu-item-click events for the ¡§Quit¡¨ menu item.
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
			
		}
		
	}
}
