package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.*;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClydeTest {

    /**
     * Helper to set up a level from a text map.
     */
    private Level setupLevel(String... mapLines) {
        PacManSprites sprites = new PacManSprites();
        GhostFactory ghostFactory = new GhostFactory(sprites);
        LevelFactory levelFactory = new LevelFactory(sprites, ghostFactory);
        BoardFactory boardFactory = new BoardFactory(sprites);
        GhostMapParser parser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
        return parser.parseMap(List.of(mapLines));
    }

    /**
     * Helper to create and register a Pac-Man in the given level.
     */
    private void createAndRegisterPlayer(Level level) {
        PacManSprites sprites = new PacManSprites();
        PlayerFactory playerFactory = new PlayerFactory(sprites);
        Player pacman = playerFactory.createPacMan();
        pacman.setDirection(Direction.EAST);
        level.registerPlayer(pacman);
    }

    /**
     * T1: Clyde and Pac-Man far apart (>8 tiles) → Clyde moves TOWARD Pac-Man.
     */
    @Test
    void testClydeMovesTowardPacmanWhenFar() {
        Level level = setupLevel(
            "############",
            "#P        C#",
            "############"
        );
        createAndRegisterPlayer(level);

        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        assertNotNull(clyde);
        Optional<Direction> move = clyde.nextAiMove();

        assertTrue(move.isPresent());
        assertEquals(Direction.WEST, move.get());
    }

    /**
     * T2: Clyde and Pac-Man within 8 tiles → Clyde moves AWAY from Pac-Man.
     */
    @Test
    void testClydeMovesAwayWhenClose() {
        Level level = setupLevel(
            "############",
            "#P     C   #",
            "############"
        );
        createAndRegisterPlayer(level);

        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        assertNotNull(clyde);
        Optional<Direction> move = clyde.nextAiMove();

        assertTrue(move.isPresent());
        assertEquals(Direction.EAST, move.get());
    }

    /**
     * T3: Clyde blocked by wall between him and Pac-Man → Clyde should not move.
     */
    @Test
    void testClydeBlockedByWall() {
        Level level = setupLevel(
            "############",
            "#P#   C    #",
            "############"
        );
        createAndRegisterPlayer(level);

        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        assertNotNull(clyde);
        Optional<Direction> move = clyde.nextAiMove();


        assertFalse(move.isPresent());

    }

    /**
     * T4: No Pac-Man registered → Clyde has no target, should not move.
     */
    @Test
    void testClydeWithoutPlayer() {
        Level level = setupLevel(
            "############",
            "#     C    #",
            "############"
        );
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        assertNotNull(clyde);
        Optional<Direction> move = clyde.nextAiMove();

        assertTrue(move.isEmpty());
    }

}
