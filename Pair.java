/**
 * This class is used to model a hand of pair in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class Pair extends Hand{
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return topCard The top card of this hand.
	 */
	public Card getTopCard() {
		Card topCard;
		if (getCard(0).suit >= getCard(1).suit) {
			topCard = getCard(0);
		} else {
			topCard = getCard(1);
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
		
		if (size() == 2) {
			if (getCard(0).rank == getCard(1).rank) {
				valid = true;
			}
		}
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "Pair" The name of this class as a String object.
	 */
	public String getType() {
		return "Pair";
	}
}
