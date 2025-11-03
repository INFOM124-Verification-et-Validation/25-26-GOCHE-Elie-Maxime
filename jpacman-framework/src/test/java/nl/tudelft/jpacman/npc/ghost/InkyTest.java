package nl.tudelft.jpacman.npc.ghost;

import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InkyTest {

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
    private void createAndRegisterPlayer(Level level, Direction direction) {
        PacManSprites sprites = new PacManSprites();
        PlayerFactory playerFactory = new PlayerFactory(sprites);
        Player pacman = playerFactory.createPacMan();
        pacman.setDirection(direction);
        level.registerPlayer(pacman);
    }

    /**
     * T1 – Good weather: Inky, Blinky et Pac-Man sur la même ligne, sans obstacle. Pacman regarde vers WEST. -> Inky se déplace vers WEST
     */
    @Test
    void testInkyNormalMovement() {
        Level level = setupLevel(
            "############",
            "#B   P   I #",
            "############"
        );
        createAndRegisterPlayer(level, Direction.WEST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        Assertions.assertNotNull(inky);
        Optional<Direction> move = inky.nextAiMove();


        assertTrue(move.isPresent());
        assertEquals(Direction.WEST, move.get());

    }

    /**
     * T2 – Good weather: Pac-Man regarde vers le haut, bug de ciblage “haut + gauche -> Inky ne bouge pas”.
     */
    @Test
    void testInkyFacingUpwards() {
        Level level = setupLevel(
            "############",
            "#B P   I   #",
            "############"
        );
        createAndRegisterPlayer(level, Direction.NORTH);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        Assertions.assertNotNull(inky);
        Optional<Direction> move = inky.nextAiMove();

        assertTrue(move.isEmpty());
    }

    /**
     * T3 – Bad weather: Pas de Blinky dans le niveau -> aucun mouvement.
     */
    @Test
    void testInkyWithoutBlinky() {
        Level level = setupLevel(
            "############",
            "#P     I   #",
            "############"
        );
        createAndRegisterPlayer(level, Direction.EAST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        Assertions.assertNotNull(inky);
        Optional<Direction> move = inky.nextAiMove();
        assertTrue(move.isEmpty());
    }

    /**
     * T4 – Bad weather: Pas de Pac-Man dans le niveau → aucun mouvement.
     */
    @Test
    void testInkyWithoutPlayer() {
        Level level = setupLevel(
            "############",
            "#B     I   #",
            "############"
        );
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        Assertions.assertNotNull(inky);
        Optional<Direction> move = inky.nextAiMove();
        assertTrue(move.isEmpty());
    }

    /**
     * T5 – Bad weather: Mur entre Inky et la cible → Inky avance quand même vers EAST.
     */
    @Test
    void testInkyBlockedByWall() {
        Level level = setupLevel(
            "############",
            "#B P#  I   #",
            "############"
        );
        createAndRegisterPlayer(level, Direction.EAST);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        Assertions.assertNotNull(inky);
        Optional<Direction> move = inky.nextAiMove();

        assertTrue(move.isPresent());
        assertEquals(Direction.EAST, move.get());
    }

}
