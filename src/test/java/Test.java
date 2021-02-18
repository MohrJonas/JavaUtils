import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static my.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("all")
public final class Test {

    @org.junit.jupiter.api.Test
    public void testRequireNotNull() {
        assertThrows(NullPointerException.class, () -> requireNotNull(null));
        assertDoesNotThrow(() -> requireNotNull("Hello"));
    }

    @org.junit.jupiter.api.Test
    public void testSubstituteIfNull() {
        assertNull(substituteIfNull(null, null));
        assertNotNull(substituteIfNull(null, "Hello"));
        assertNotNull(substituteIfNull("Hello", null));
        assertNotNull(substituteIfNull("Hello", "Bye"));
    }

    @org.junit.jupiter.api.Test
    public void testReadFile() throws URISyntaxException, MalformedURLException {
        final File file = new File(this.getClass().getClassLoader().getResource("ImportantFile.txt").toURI());
        assertNotNull(file);
        final List<String> lines = readFile(file);
        assertNotNull(lines);
        assertFalse(lines.isEmpty());
        assertEquals(lines.get(0), "Lorem ipsum");
    }

    @org.junit.jupiter.api.Test
    public void testLet() {
        let(new Point(), point -> assertNotNull(point));
    }

    @org.junit.jupiter.api.Test
    public void testAlso() {
        final Point point = also(new Point(), p -> p.x = 10);
        assertNotNull(point);
        assertEquals(point.x, 10);
    }

    @org.junit.jupiter.api.Test
    public void testWith() throws URISyntaxException, FileNotFoundException {
        final BufferedReader reader = new BufferedReader(new FileReader(new File(this.getClass().getClassLoader().getResource("ImportantFile.txt").toURI())));
        assertNotNull(reader);
        AtomicReference<String> line = new AtomicReference<>();
        with(reader, r -> {
            try {
                line.set(r.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        assertEquals(line.get(), "Lorem ipsum");
    }

    @org.junit.jupiter.api.Test
    public void testRunIf() {
        assertNotNull(runIf(() -> true, () -> "Hello"));
        assertNull(runIf(() -> false, () -> "Hello"));
    }

    @org.junit.jupiter.api.Test
    public void testRunIfElse() {
        assertEquals(runIfElse(() -> true, () -> 1, () -> 2), 1);
        assertEquals(runIfElse(() -> false, () -> 1, () -> 2), 2);
    }

    @org.junit.jupiter.api.Test
    public void testRepeat() {
        AtomicInteger i = new AtomicInteger();
        repeat(10, index -> i.getAndIncrement());
        assertEquals(i.get(), 10);
    }

    @org.junit.jupiter.api.Test
    public void testRunCatching() {
        assertDoesNotThrow(() -> runCatching(() -> {
            throw new NullPointerException();
        }));
    }

    @org.junit.jupiter.api.Test
    public void testReduceArrayAsNeeded() {
        final String[] originalArray = new String[]{"lorem", "ipsum", "dolor", "sit", "amet"};
        assertArrayEquals(reduceArrayAsNeeded(originalArray, 2, REDUCTION_METHOD.FIRST), new String[]{"lorem", "ipsum"});
        assertArrayEquals(reduceArrayAsNeeded(originalArray, 2, REDUCTION_METHOD.LAST), new String[]{"sit", "amet"});
        System.out.println(Arrays.toString(reduceArrayAsNeeded(originalArray, 4, REDUCTION_METHOD.RANDOM)));
    }

    @org.junit.jupiter.api.Test
    public void testPickRandomFromArray() {
        final String[] originalArray = new String[]{"lorem", "ipsum", "dolor", "sit", "amet"};
        repeat(10, () -> System.out.println(pickRandomFromArray(originalArray)));
    }

    @org.junit.jupiter.api.Test
    public void testFillArray() {
        assertArrayEquals(fillArray(5, String.class, (int i, String prev) -> "Hello"), new String[]{"Hello", "Hello", "Hello", "Hello", "Hello"});
    }

    @org.junit.jupiter.api.Test
    public void testArrayOf() {
        assertArrayEquals(arrayOf(String.class, "lorem", "ipsum", "dolor", "sit", "amet"), new String[]{"lorem", "ipsum", "dolor", "sit", "amet"});
    }

    @org.junit.jupiter.api.Test
    public void testNow() {
        assertEquals(LocalDateTime.now().getDayOfYear(), now().getDayOfYear());
    }

    @org.junit.jupiter.api.Test
    public void testShiftTime() {
        final LocalDateTime currentTime = now();
        assertEquals(shiftTime(currentTime, 1, 2, 2, 1, 0, 0, 0), currentTime.plusYears(1).plusMonths(2).plusWeeks(2).plusDays(1));
    }

    @org.junit.jupiter.api.Test
    public void testEqualsNullSafe() {
        String s1 = "Hello";
        String s2 = "Hello";
        assertTrue(equalsNullSafe(s1, s2));
        s1 = null;
        assertFalse(equalsNullSafe(s1, s2));
        s1 = "Hello";
        s2 = null;
        assertFalse(equalsNullSafe(s1, s2));
        s1 = null;
        assertFalse(equalsNullSafe(s1, s2));

    }
}