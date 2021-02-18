import my.utils.Pair;
import my.utils.Utils;
import org.opentest4j.AssertionFailedError;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.*;
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

    @org.junit.jupiter.api.Test
    public void testPairOf() {
        int a = 10;
        String b = "Hello";
        Pair<Integer, String> p = pairOf(a, b);
        assertEquals(a, p.a);
        assertEquals(b, p.b);
    }

    @org.junit.jupiter.api.Test
    public void testPollAll() {
        Queue<String> q = new ArrayDeque<>();
        q.offer("Hello");
        q.offer("World");
        q.offer("!");
        assertArrayEquals(pollAll(q).toArray(), arrayOf(String.class, "Hello", "World", "!"));
    }

    @org.junit.jupiter.api.Test
    public void testPopAll() {
        Stack<String> s = new Stack<>();
        s.push("Hello");
        s.push("World");
        s.push("!");
        assertArrayEquals(castArray(popAll(s).toArray(), String.class), arrayOf(String.class, "!", "World", "Hello"));
    }

    @org.junit.jupiter.api.Test
    public void testCastArray() {
        final Object[] strings = arrayOf(Object.class, "Hello", "World", "!");
        assertThrows(AssertionFailedError.class, () -> assertInstanceOf(String[].class, strings));
        assertInstanceOf(String[].class, castArray(strings, String.class));
    }

    @org.junit.jupiter.api.Test
    public void testIsCastable() {
        assertTrue(isCastable("Hello", Object.class));
        assertTrue(isCastable(new ArrayList<Object>(), List.class));
        assertFalse(isCastable("Hello", Integer.class));
        assertFalse(isCastable(new ArrayList<Object>(), String.class));
    }

    @org.junit.jupiter.api.Test
    public void testHasToString() {
        assertFalse(hasToString(new ArrayList<Object>()));
        assertFalse(hasToString(new Pair<Object, Object>(null, null)));
        assertTrue(hasToString(new Object() {
            @Override
            public String toString() {
                return "";
            }
        }));
    }

    @org.junit.jupiter.api.Test
    public void testToString() {
        System.out.println(Utils.toString(new Pair<String, Integer>("Hello", -123)));
    }
}