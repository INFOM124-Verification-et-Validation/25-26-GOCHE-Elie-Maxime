package delft;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.within;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;

class AutoAssignerTest {

    private ZonedDateTime date(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault());
    }

    @Test
    void assignsStudentWhenWorkshopHasSpots() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 13, 14, 0);
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(d1, 2);
        Workshop java = new Workshop(1, "Java", spots);
        Student s = new Student(1, "Alice", "alice@example.com");
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(List.of(s), List.of(java));

        // Assert
        assertThat(log.getAssignments()).containsExactly("Java,Alice,13/07/2025 14:00");
        assertThat(log.getErrors()).isEmpty();

        assertThat(java.getSpotsPerDate().get(d1)).isEqualTo(1);
    }

    @Test
    void logsErrorWhenNoAvailableDate() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 13, 14, 0);
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(d1, 0); // no spots at all
        Workshop python = new Workshop(2, "Python", spots);
        Student s = new Student(2, "Bob", "bob@example.com");
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(List.of(s), List.of(python));

        // Assert
        assertThat(log.getAssignments()).isEmpty();
        assertThat(log.getErrors()).containsExactly("Python,Bob");
        assertThat(python.getSpotsPerDate().get(d1)).isZero(); // unchanged
    }

    @Test
    void assignsMultipleStudentsUntilFullThenLogsErrors() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 13, 14, 0);
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(d1, 2);
        Workshop java = new Workshop(3, "Java", spots);
        List<Student> students = List.of(
                new Student(1, "Alice", "a@x"),
                new Student(2, "Bob", "b@x"),
                new Student(3, "Charlie", "c@x")
        );
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(students, List.of(java));

        // Assert
        assertThat(log.getAssignments()).containsExactly(
                "Java,Alice,13/07/2025 14:00",
                "Java,Bob,13/07/2025 14:00"
        );
        assertThat(log.getErrors()).containsExactly("Java,Charlie");
        assertThat(java.getSpotsPerDate().get(d1)).isZero(); // full now
    }

    @Test
    void picksEarliestAvailableDate() {
        // Arrange
        ZonedDateTime d1 = date(2025, 8, 1, 9, 0);
        ZonedDateTime d2 = date(2025, 7, 20, 9, 0);
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(d1, 1);
        spots.put(d2, 1);
        Workshop cpp = new Workshop(4, "C++", spots);
        Student s = new Student(1, "David", "d@x");
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(List.of(s), List.of(cpp));

        // Assert
        assertThat(log.getAssignments()).containsExactly("C++,David,20/07/2025 09:00");
        // the earlier date lost one spot
        assertThat(cpp.getSpotsPerDate().get(d2)).isZero();
        assertThat(cpp.getSpotsPerDate().get(d1)).isEqualTo(1);
    }

    @Test
    void multipleWorkshopsHandledSeparately() {
        // Arrange
        ZonedDateTime jDate = date(2025, 7, 13, 14, 0);
        ZonedDateTime pDate = date(2025, 7, 14, 10, 0);
        Workshop java = new Workshop(1, "Java", new HashMap<>(Map.of(jDate, 2)));
        Workshop python = new Workshop(2, "Python", new HashMap<>(Map.of(pDate, 2)));
        Student s1 = new Student(1, "Alice", "a@x");
        Student s2 = new Student(2, "Bob", "b@x");
        List<Student> students = List.of(s1, s2);
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(students, List.of(java, python));

        // Assert
        assertThat(log.getAssignments()).containsExactlyInAnyOrder(
                "Java,Alice,13/07/2025 14:00",
                "Java,Bob,13/07/2025 14:00",
                "Python,Alice,14/07/2025 10:00",
                "Python,Bob,14/07/2025 10:00"
        );
        assertThat(log.getErrors()).isEmpty();
    }

    @Test
    void getNextAvailableDateChoosesFirstAvailableEvenWhenLaterDatesHaveMoreSpots() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 10, 10, 0);
        ZonedDateTime d2 = date(2025, 7, 11, 10, 0);
        Map<ZonedDateTime, Integer> map = new HashMap<>();
        map.put(d1, 1);
        map.put(d2, 5);
        Workshop ws = new Workshop(9, "Kotlin", map);
        Student s = new Student(1, "Zoe", "z@x");
        AutoAssigner assigner = new AutoAssigner();

        // Act
        AssignmentsLogger log = assigner.assign(List.of(s), List.of(ws));

        // Assert
        assertThat(log.getAssignments()).containsExactly("Kotlin,Zoe,10/07/2025 10:00");
        assertThat(ws.getSpotsPerDate().get(d1)).isZero();
        assertThat(ws.getSpotsPerDate().get(d2)).isEqualTo(5);
    }

    @Test
    void hasAvailableDateReturnsFalseWhenAllZero() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 13, 14, 0);
        ZonedDateTime d2 = date(2025, 7, 14, 10, 0);
        Map<ZonedDateTime, Integer> map = new HashMap<>();
        map.put(d1, 0);
        map.put(d2, 0);
        Workshop ws = new Workshop(1, "Ruby", map);

        // Act + Assert
        assertThat(ws.hasAvailableDate()).isFalse();
    }

    @Test
    void hasAvailableDateReturnsTrueWhenAtLeastOneDateAvailable() {
        // Arrange
        ZonedDateTime d1 = date(2025, 7, 13, 14, 0);
        ZonedDateTime d2 = date(2025, 7, 14, 10, 0);
        Map<ZonedDateTime, Integer> map = new HashMap<>();
        map.put(d1, 0);
        map.put(d2, 2);
        Workshop ws = new Workshop(1, "Ruby", map);

        // Act + Assert
        assertThat(ws.hasAvailableDate()).isTrue();
    }
}
