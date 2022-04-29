/**
 * This class is used to model a hand of flush in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class Flush extends Hand {
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
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
		
		// if hand rank is 0(A) or 1(2), then add 13, because 2 is the largest, A is the second largest
		if (topCardRank == 0 || topCardRank == 1) {
			topCardRank+=13;
		}
		
		// find out the largest card
		for (int i = 1; i < 5; i++) {
			cardRank = getCard(i).rank;
			// if hand rank is 0(A) or 1(2), then add 13, because 2 is the largest, A is the second largest
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
	 * This method checks if this is a valid hand.
	 * 
	 * @return valid Whether this hand is a valid hand or not.
	 */
	public boolean isValid() {
		boolean valid = false;
		
		// check whether it is a five-cards hand
		if (size() == 5) {
			// check whether all 5 cards have the same suit 
			if (getCard(0).suit == getCard(1).suit && getCard(0).suit == getCard(2).suit && getCard(0).suit == getCard(3).suit && getCard(0).suit == getCard(4).suit) {
				valid = true;
			}
		} 
		
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "Flush" The name of this class as a String object.
	 */
	public String getType() {
		return "Flush";
	}
}
