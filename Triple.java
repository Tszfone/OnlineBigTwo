/**
 * This class is used to model a hand of triple in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class Triple extends Hand {
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return topCard The top card of this hand.
	 */
	public Card getTopCard() {
		Card topCard = getCard(0);
		for (int i = 1; i < 3; i++) {
			if (topCard.suit < getCard(i).suit) {
				topCard = getCard(i);
			}
		}
		
		return topCard;
	}
	
	/**
	 * This method checks if this is a valid hand.
	 * 
	 * @return true if this is a valid hand.
	 * @return false if this is not a valid hand.
	 */
	public boolean isValid() {
		boolean valid = false;
		
		if (size() == 3) {
			if (getCard(0).rank == getCard(1).rank && getCard(0).rank == getCard(2).rank) {
				valid = true;
			}
		}
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "Triple" The name of this class as a String object.
	 */
	public String getType() {
		return "Triple";
	}
}
