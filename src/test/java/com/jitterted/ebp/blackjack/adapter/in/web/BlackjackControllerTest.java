package com.jitterted.ebp.blackjack.adapter.in.web;

import com.jitterted.ebp.blackjack.domain.Card;
import com.jitterted.ebp.blackjack.domain.Deck;
import com.jitterted.ebp.blackjack.domain.Game;
import com.jitterted.ebp.blackjack.domain.Rank;
import com.jitterted.ebp.blackjack.domain.StubDeck;
import com.jitterted.ebp.blackjack.domain.Suit;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BlackjackControllerTest {

    @Test
    public void startGameResultsInTwoCardsDealtToPlayer() throws Exception {
        Game game = new Game(new Deck());
        BlackjackController blackjackController = new BlackjackController(game);

        String redirectPage = blackjackController.startGame();

        assertThat(game.playerHand().cards())
                .hasSize(2);
        assertThat(redirectPage)
                .isEqualTo("redirect:/game");
    }

    @Test
    public void gameViewPopulatesViewModelWithAllCards() throws Exception {
        Deck stubDeck = new StubDeck(List.of(new Card(Suit.DIAMONDS, Rank.TEN),
                                             new Card(Suit.HEARTS, Rank.TWO),
                                             new Card(Suit.DIAMONDS, Rank.KING),
                                             new Card(Suit.CLUBS, Rank.THREE)));
        Game game = new Game(stubDeck);
        BlackjackController blackjackController = new BlackjackController(game);
        blackjackController.startGame();

        Model model = new ConcurrentModel();
        blackjackController.gameView(model);

        GameView gameView = (GameView) model.getAttribute("gameView");

        assertThat(gameView.getDealerCards())
                .containsExactly("2♥", "3♣");

        assertThat(gameView.getPlayerCards())
                .containsExactly("10♦", "K♦");
    }

    @Test
    public void hitCommandResultsInThirdCardDealtToPlayer() throws Exception {
        Deck playerHitsAndDoesNotGoBust = new StubDeck(Rank.TEN, Rank.EIGHT,
                                                       Rank.SEVEN, Rank.JACK,
                                                       Rank.THREE);
        Game game = new Game(playerHitsAndDoesNotGoBust);
        BlackjackController blackjackController = new BlackjackController(game);
        blackjackController.startGame();

        String redirectPage = blackjackController.hitCommand();

        assertThat(redirectPage)
                .isEqualTo("redirect:/game");
        assertThat(game.playerHand().cards())
                .hasSize(3);
    }

    @Test
    public void hitCommandAndPlayerBustsThenRedirectToDonePage() throws Exception {
        Game game = new Game(StubDeck.playerHitsAndGoesBust());
        BlackjackController blackjackController = new BlackjackController(game);
        blackjackController.startGame();

        String redirectPage = blackjackController.hitCommand();

        assertRedirectedToDonePage(redirectPage);
    }

    @Test
    public void donePageShowsFinalGameStateWithOutcome() throws Exception {
        Game game = new Game(StubDeck.playerStandsAndBeatsDealer());
        BlackjackController blackjackController = new BlackjackController(game);
        blackjackController.startGame();

        Model model = new ConcurrentModel();
        blackjackController.doneView(model);

        assertThat(model.containsAttribute("gameView"))
                .isTrue();

        String outcome = (String) model.getAttribute("outcome");

        assertThat(outcome)
                .isNotBlank();
    }

    @Test
    public void playerStandsResultsInRedirectToDonePageAndPlayerIsDone() throws Exception {
        Game game = new Game(StubDeck.playerStandsAndBeatsDealer());
        BlackjackController blackjackController = new BlackjackController(game);
        blackjackController.startGame();

        String redirectPage = blackjackController.standCommand();

        assertRedirectedToDonePage(redirectPage);
        assertThat(game.isPlayerDone())
                .isTrue();
    }

    @Test
    public void playerDealtBlackjackRedirectsToDone() throws Exception {
        Game game = new Game(StubDeck.playerDealtBlackjack());
        BlackjackController blackjackController = new BlackjackController(game);

        String redirectPage = blackjackController.startGame();

        assertRedirectedToDonePage(redirectPage);
    }

    private void assertRedirectedToDonePage(String redirectPage) {
        assertThat(redirectPage)
                .isEqualTo("redirect:/done");
    }

/* Kent Beck's 4 Rules of Simple Design
    1. Passes the tests
    2. Reveals intention
    3. No duplication - or similarity that can be eliminated?
        Why? Why are they the same/similar?
    4. Fewest elements - are there unnecessary fields, methods, constants, etc.?
*/

}