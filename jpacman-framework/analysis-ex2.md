# Specification-based Testing — Inky

## 1. Goal, inputs and outputs

### Goal
To verify that the AI of the ghost **Inky** (`nextAiMove()`) behaves according to its specification:
- Inky’s movement depends on **both Blinky’s and Pac-Man’s positions**.
- It targets a point **two tiles ahead of Pac-Man**, extends the line between that point and **Blinky** twice as far, and moves toward that destination.
- If **either Blinky or Pac-Man is missing**, or if **no valid path exists**, Inky should **not move** (`Optional.empty()`).

### Input domain
- **Inky’s position** on the board
- **Pac-Man’s position** and **direction**
- **Blinky’s position**
- **Obstacles (walls)** between Inky, Pac-Man, and Blinky
- The **map configuration** and **possible paths**

### Output domain
- A **Direction** (`NORTH`, `SOUTH`, `EAST`, `WEST`) toward the calculated target
- Or **no move** (`Optional.empty()`)

---

## 2. Explore the program (if needed)


---

## 3. Identify input and output partitions

### Input partitions

#### Presence partitions

#### Obstacle partitions

#### Pac-Man direction partitions

#### Combined partitions

---

### Output partitions


---

## 4. Identify boundaries


---

## 5. Select test cases

