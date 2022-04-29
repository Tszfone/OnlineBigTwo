/**
 * This class is used to model a hand of single in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class Single extends Hand {
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	
	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return getCard(0) The top card of this hand.
	 */
	public Card getTopCard() {
		return getCard(0);
	}
	
	/**
	 * This method checks if this is a valid hand.
	 * 
	 * @return valid Whether this hand is a valid hand or not.
	 */
	public boolean isValid() {
		boolean valid;
		
		if (size() == 1) {
			valid = true;
		} else {
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "Single" The name of this class as a String object.
	 */
	public String getType() {
		return "Single";
	}
	
}
