/**
 * This class is used to model a hand of quad in a Big Two card game.
 * 
 * @author Cheng Tsz Fung
 *
 */
public class Quad extends Hand {
	
	/**
	 * This constructor call Hand constructor for building a hand
	 * 
	 * @param player The player who plays this hand.
	 * @param cards  The cards who the player plays.
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * This method retrieves the top card of this hand.
	 * 
	 * @return topCard The top card of this hand.
	 */
	public Card getTopCard() {
		Card topCard = null;
		
		// the case when quadruplet locates at the left hand side
		if (getCard(0).rank == getCard(1).rank) {
			topCard = getCard(0);
			for (int i = 1; i < 4; i++) {
				if (topCard.suit < getCard(i).suit) {
					topCard = getCard(i);
				}
			}
		// the case when quadruplet locates at the right hand side
		} else {
			topCard = getCard(1);
			for (int i = 2; i < 5; i++) {
				if (topCard.suit < getCard(i).suit) {
					topCard = getCard(i);
				}
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
		
		if (size() == 5) {
			// one of the quad pattern : AAAAB
			if (getCard(0).rank == getCard(1).rank && getCard(0).rank == getCard(2).rank && getCard(0).rank == getCard(3).rank) {
				valid = true;
			// another quad pattern : ABBBB
			} else if (getCard(1).rank == getCard(2).rank && getCard(1).rank == getCard(3).rank && getCard(1).rank == getCard(4).rank) {
				valid = true;
			}
		}
		
		return valid;
	}
	
	/**
	 * This method returns a string specifying the type of this hand.
	 * 
	 * @return "Quad" The name of this class as a String object.
	 */
	public String getType() {
		return "Quad";
	}
}
