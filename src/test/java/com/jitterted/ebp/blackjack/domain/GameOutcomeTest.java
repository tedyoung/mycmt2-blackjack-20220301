package com.jitterted.ebp.blackjack.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameOutcomeTest {

    @Test
    public void playerHitsAndGoesBustThenPlayerLoses() throws Exception {
        Deck playerHitsAndGoesBustDeck = new StubDeck(Rank.TEN, Rank.EIGHT,
                                                      Rank.QUEEN, Rank.JACK,
                                                      Rank.THREE);
        Game game = new Game(playerHitsAndGoesBustDeck);
        game.initialDeal();

        game.playerHits();

        assertThat(game.determineOutcome())
                .isEqualTo("You Busted, so you lose.  💸");
    }

}