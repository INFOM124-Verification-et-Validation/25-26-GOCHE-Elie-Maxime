package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PlayerCollisonsTest {

    private PlayerCollisions collisions;
    private GhostFactory ghostFactory;

    private Player player;
    private Ghost ghost;
    private Pellet pellet;

    @BeforeEach
    void setUp() {
        PacManSprites sprites = new PacManSprites();
        PlayerFactory playerFactory = new PlayerFactory(sprites);
        ghostFactory = new GhostFactory(sprites);
        LevelFactory levelFactory = new LevelFactory(sprites, ghostFactory);

        collisions = new PlayerCollisions();

        player = playerFactory.createPacMan();
        ghost = ghostFactory.createBlinky();
        pellet = levelFactory.createPellet();
    }

    /** Player eats pellet → +points, pellet removed */
    @Test
    void playerEatsPellet() {
        int pointsBefore = player.getScore();

        collisions.collide(player, pellet);

        assertThat(player.getScore()).isEqualTo(pointsBefore + pellet.getValue());

        assertThat(player.isAlive()).isTrue();
    }

    /** Player meets ghost → player dies */
    @Test
    void playerDiesWhenTouchingGhost() {
        assertThat(player.isAlive()).isTrue();

        collisions.collide(player, ghost);

        assertThat(player.isAlive()).isFalse();
    }

    /** Ghost meets pellet → nothing happens */
    @Test
    void ghostPelletDoesNothing() {
        int playerScoreBefore = player.getScore();

        collisions.collide(ghost, pellet);

        assertThat(player.getScore()).isEqualTo(playerScoreBefore);
        assertThat(player.isAlive()).isTrue();
    }

    /** Ghost vs ghost → nothing happens */
    @Test
    void ghostGhostDoesNothing() {
        Ghost otherGhost = ghostFactory.createBlinky();

        collisions.collide(ghost, otherGhost);

        assertThat(player.isAlive()).isTrue();
    }



}
