import java.util.ArrayList;

/**
 * This class is used to model a hand of cards.
 * It is a subclass of the CardList class.
 * 
 * @author Cheng Tsz Fung
 *
 */
public abstract class Hand extends CardList {
	private CardGamePlayer player;
	// declare fiveCardsHand for comparing different 5-cards hand
	private String[] fiveCardsHand = {"Straight", "Flush", "FullHouse", "Quad", "StraightFlush"};
	
	/**
	 * This constructor builds a hand with the specified player and list of cards.
	 * 
	 * @param player The player who play this hand.
	 * @param cards The cards played by the player
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		
		for(int i = 0; i < cards.size(); i++) {
			addCard(cards.getCard(i));
		}
	}
	
	
	
	/**
	 * This method retrieves the player of this hand.
	 * 
	 * @return player The player who plays hand.
	 */
	public CardGamePlayer getPlayer() {
		return player;
	}
	
	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return topCard The top card of this hand.
	 */
	public Card getTopCard() {
		Card topCard = getCard(0);
		int topCardRank = topCard.rank;
		int cardRank = 0;
		// the number of cards in this hand
		int sizeOfHand = this.size();
		
		// if hand rank is 0(A) or 1(2), then add 13, because 2 is the largest, A is the second largest
		if (topCardRank == 0 || topCardRank == 1) {
			topCardRank+=13;
		}
		
		for (int i = 1; i < sizeOfHand; i++) {
			cardRank = getCard(i).rank;
			if (cardRank == 0 || cardRank == 1) {
				cardRank+=13;
			}
			
			if (topCardRank < cardRank) {
				topCard = getCard(i);
				topCardRank = cardRank;
			}
		}

		return topCard;
	}
	
	/**
	 * This method checks if this hand beats a specified hand.
	 * 
	 * @param hand The specified hand.
	 * @return beat Whether this hand beats a specified hand or not.
	 */
	public boolean beats(Hand hand) {
		// declare beat for stating whether this hand beats the specified hand
		boolean beat = false;
		// declare typeOfHand for storing the type of this hand
		String typeOfHand = this.getType();
		// declare sizeOfHand for storing the size of this hand
		int sizeOfHand = this.size();
		
		if (sizeOfHand == 1) {
			if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
				beat = true;
			}
		} else if (sizeOfHand == 2) {
			if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
				beat = true;
			}
		} else if (sizeOfHand == 3) {
			if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
				beat = true;
			}
		} else {
			if (indexOf(this.getType()) > indexOf(hand.getType())) {
				beat = true;
			} else if (this.getType() == hand.getType()) {
				if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
					beat = true;
				}
			} else {
				beat = false;
			}
		}
		
		return beat;
	}
	
	// This method finds the corresponding index of a string value in fiveCardsHand array
	private int indexOf(String value) {
		int idx = 0;
		for(int i = 0; i < 5; i++) {
			if (value == fiveCardsHand[i]) {
				idx = i;
			}
		}
		
		return idx;
	}
	
	/**
	 * This method checks if this hand is a valid hand.
	 * 
	 * @return true or false whether this hand is a valid hand or not.
	 */
	public abstract boolean isValid();
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return the type of this hand.
	 */
	public abstract String getType();
}
