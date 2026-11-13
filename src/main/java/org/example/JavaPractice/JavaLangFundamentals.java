package org.example.JavaPractice;

import java.lang.ref.Cleaner;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.*;

/**
 * =============================================================================================
 *       MASTERCLASS: JAVA PACKAGES, java.lang, CHARACTER, UNICODE & PRIMITIVE/WRAPPER TYPES
 * =============================================================================================
 *
 * This file serves as a comprehensive, hands-on, runnable documentation and guide for mastering
 * Java's core language package (java.lang), the standard package ecosystem, primitives vs.
 * wrappers, autoboxing traps, String internals, and complete Unicode / Character handling.
 *
 * TABLE OF CONTENTS:
 *  1. Java Standard Packages & Module Ecosystem (Complete Import Roadmap)
 *  2. java.lang Core Classes (Object, System, Runtime, Math, StackWalker)
 *  3. Primitive Types vs. Wrapper Types (Memory, Ranges, Literals, IEEE 754 Pitfalls)
 *  4. Autoboxing, Unboxing, The Integer Cache Trap & NPE Gotchas
 *  5. java.lang.Character & Complete Unicode Handling (Code Units, Code Points, Graphemes)
 *  6. String Internals (Compact Strings, String Pool, StringBuilder, Modern APIs)
 *  7. Immutability, Locale Traps & Charsets (UTF-8 vs UTF-16)
 *  8. Reference Types & GC Interaction (Strong, Soft, Weak, Cleaner)
 *  9. Enum & Record Base Classes (java.lang.Enum & java.lang.Record)
 * 10. Throwable Hierarchy & AutoCloseable (try-with-resources & Suppressed Exceptions)
 * =============================================================================================
 */
public class JavaLangFundamentals {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("     JAVA.LANG, PACKAGES, UNICODE & PRIMITIVE/WRAPPER GUIDE        ");
        System.out.println("===================================================================\n");

        section1_JavaPackagesRoadmap();
        section2_JavaLangCoreClasses();
        section3_PrimitivesVsWrappers();
        section4_AutoboxingAndCacheTraps();
        section5_CharacterAndUnicodeDeepDive();
        section6_StringInternalsAndModernAPIs();
        section7_CharsetsLocaleAndImmutability();
        section8_ReferenceTypesAndGC();
        section9_EnumAndRecordInternals();
        section10_ThrowableAndAutoCloseable();

        System.out.println("\n===================================================================");
        System.out.println("           ALL JAVA.LANG & TYPE SYSTEM LESSONS COMPLETE!           ");
        System.out.println("===================================================================");
    }

    // =========================================================================================
    // SECTION 1: JAVA PACKAGES & IMPORT ECOSYSTEM (COMPLETE ROADMAP)
    // =========================================================================================
    private static void section1_JavaPackagesRoadmap() {
        printHeader("1. JAVA PACKAGES & STANDARD IMPORT ROADMAP");
        /*
         * RULE #1: java.lang is AUTOMATICALLY imported into every Java source file!
         * You never have to write `import java.lang.String;` or `import java.lang.System;`.
         *
         * COMPLETE MAP OF STANDARD JAVA PACKAGES (JDK 21 / Java SE):
         * -------------------------------------------------------------------------------------
         * 1. CORE LANGUAGE & REFLECTION (Module: java.base)
         *    - java.lang              : Fundamental classes (Object, String, System, Math, Thread,
         *                               Character, Integer, Throwable, Record, Enum, AutoCloseable)
         *    - java.lang.annotation   : Annotation support (@Retention, @Target, @Documented)
         *    - java.lang.constant     : JVM nominal descriptors (Java 12+)
         *    - java.lang.invoke       : Dynamic language support (MethodHandle, VarHandle, invokedynamic)
         *    - java.lang.module       : Module descriptors and programmatic module discovery (Java 9+)
         *    - java.lang.ref          : GC reference wrappers (WeakReference, SoftReference, Cleaner)
         *    - java.lang.reflect      : Runtime reflection (Method, Field, Constructor, Proxy)
         *
         * 2. UTILITIES, COLLECTIONS, STREAMS & CONCURRENCY (Module: java.base)
         *    - java.util              : Collections (List, Map, Set), Optional, Random, Objects, Scanner
         *    - java.util.concurrent   : ExecutorService, CompletableFuture, ConcurrentHashMap, Locks
         *    - java.util.concurrent.atomic : Lock-free CAS primitives (AtomicInteger, LongAdder)
         *    - java.util.function     : Functional interfaces (Predicate, Function, Consumer, Supplier)
         *    - java.util.stream       : Stream pipelines & Collectors
         *    - java.util.regex        : Regular expressions (Pattern, Matcher)
         *    - java.util.zip / jar    : ZIP/GZIP/JAR compression streams
         *    - java.util.random       : Modern PRNG algorithms (RandomGenerator, Java 17+)
         *
         * 3. DATE, TIME, MATH & TEXT (Module: java.base)
         *    - java.time              : Modern ISO-8601 Date/Time (LocalDate, Instant, ZonedDateTime)
         *    - java.time.format       : DateTimeFormatter
         *    - java.time.temporal     : ChronoUnit, TemporalAdjusters
         *    - java.math              : Arbitrary-precision numbers (BigDecimal, BigInteger, RoundingMode)
         *    - java.text              : Internationalization, BreakIterator, Normalizer, NumberFormat
         *
         * 4. I/O, NIO & NETWORKING (Modules: java.base, java.net.http)
         *    - java.io                : Classic stream-based I/O (InputStream, Reader, File, Serializable)
         *    - java.nio               : Buffers (ByteBuffer, CharBuffer)
         *    - java.nio.file          : Modern Path, Files, FileSystems (NIO.2)
         *    - java.nio.charset       : Charset, StandardCharsets (UTF_8, ISO_8859_1)
         *    - java.nio.channels      : Non-blocking selectors and FileChannel
         *    - java.net               : Sockets, URI, URL, InetAddress
         *    - java.net.http          : Modern HTTP/1.1 & HTTP/2 HttpClient (Java 11+, module java.net.http)
         *
         * 5. SECURITY & CRYPTOGRAPHY (Module: java.base)
         *    - java.security          : MessageDigest (SHA-256), KeyPairGenerator, Signature, SecureRandom
         *    - javax.crypto           : Cipher (AES, RSA), Mac (HMAC), SecretKey
         *
         * 6. DATABASES, LOGGING, MANAGEMENT & ENTERPRISE MODULES
         *    - java.sql / javax.sql   : JDBC Database access (Connection, PreparedStatement, DataSource)
         *    - java.util.logging      : Built-in JUL Logger (Module: java.logging)
         *    - java.lang.management   : JVM monitoring MXBeans (MemoryMXBean, ThreadMXBean, module java.management)
         *    - java.desktop / javax.swing / java.awt : Desktop GUI & graphics (Module: java.desktop)
         *    - java.xml / javax.xml   : XML DOM, SAX, StAX parsers (Module: java.xml)
         *    - java.naming            : JNDI directory lookup (Module: java.naming)
         *    - java.instrument        : Java Agents & bytecode instrumentation (Module: java.instrument)
         * -------------------------------------------------------------------------------------
         *
         * IMPORT RULES & MYTH BUSTING:
         *  - Wildcard imports (`import java.util.*;`) have ZERO runtime performance penalty!
         *    The compiler resolves exact class names into bytecode. However, explicit imports are
         *    preferred to avoid name collisions (e.g., `java.util.Date` vs `java.sql.Date`).
         *  - `import static java.lang.Math.*;` imports static members (e.g., `PI`, `sqrt()`).
         *  - Importing `java.util.*` does NOT import subpackages like `java.util.stream.*`!
         */

        // Demonstrating name collision resolution using FQN (Fully Qualified Name):
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        System.out.println("java.util.Date vs java.sql.Date: " + utilDate.getClass().getName()
                + " | " + sqlDate.getClass().getName());

        // Inspecting runtime module of core packages:
        System.out.println("Module containing String:     " + String.class.getModule().getName());     // java.base
        System.out.println("Module containing HttpClient: " + java.net.http.HttpClient.class.getModule().getName()); // java.net.http
        System.out.println("Module containing JDBC:       " + java.sql.Connection.class.getModule().getName());      // java.sql
    }

    // =========================================================================================
    // SECTION 2: java.lang CORE CLASSES DEEP DIVE
    // =========================================================================================
    private static void section2_JavaLangCoreClasses() {
        printHeader("2. java.lang CORE CLASSES (Object, System, Runtime, Math, StackWalker)");

        // 1. System: Properties, Environment, ArrayCopy, Clocks
        // System.currentTimeMillis() -> Wall-clock time (can jump backwards via NTP sync). Use for timestamps.
        // System.nanoTime()          -> Monotonic high-res timer (arbitrary origin). Use ONLY for elapsed duration!
        long startNano = System.nanoTime();
        int[] src = {10, 20, 30, 40, 50};
        int[] dest = new int[5];
        System.arraycopy(src, 0, dest, 0, src.length); // Native JVM intrinsic (extremely fast memory copy)
        long elapsedNano = System.nanoTime() - startNano;

        System.out.println("System.arraycopy result: " + Arrays.toString(dest) + " (took " + elapsedNano + " ns)");
        System.out.println("Java Version: " + System.getProperty("java.version")
                + " | OS: " + System.getProperty("os.name"));

        // 2. Runtime: Hardware & JVM Heap Metrics
        Runtime rt = Runtime.getRuntime();
        long mb = 1024 * 1024;
        System.out.printf("CPU Cores: %d | Heap Used: %d MB | Max Heap: %d MB%n",
                rt.availableProcessors(),
                (rt.totalMemory() - rt.freeMemory()) / mb,
                rt.maxMemory() / mb);

        // 3. Math Overflow Protection & Floor Modulus (Java 8+)
        // Classic trap: Integer.MAX_VALUE + 1 silently wraps around to Integer.MIN_VALUE!
        int silentOverflow = Integer.MAX_VALUE + 1;
        System.out.println("Silent int overflow (MAX_VALUE + 1): " + silentOverflow);

        try {
            Math.addExact(Integer.MAX_VALUE, 1); // Throws ArithmeticException instead of silent corruption!
        } catch (ArithmeticException e) {
            System.out.println("Math.addExact caught overflow safely: " + e.getMessage());
        }

        // Modulo operator (%) vs Math.floorMod() for negative numbers:
        // (-7) % 3 == -1 (remainder retains sign of dividend)
        // Math.floorMod(-7, 3) == 2 (true mathematical modulo, always positive for positive divisor!)
        System.out.println("Java (-7 % 3) = " + (-7 % 3) + " | Math.floorMod(-7, 3) = " + Math.floorMod(-7, 3));

        // 4. StackWalker (Java 9+ lazy, high-performance stack frame inspection)
        String callerMethod = StackWalker.getInstance()
                .walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName).orElse("unknown"));
        System.out.println("Current method via StackWalker: " + callerMethod);
    }

    // =========================================================================================
    // SECTION 3: PRIMITIVE TYPES VS. WRAPPER TYPES
    // =========================================================================================
    private static void section3_PrimitivesVsWrappers() {
        printHeader("3. PRIMITIVE TYPES VS. WRAPPER TYPES & FLOATING-POINT TRAPS");
        /*
         * THE 8 PRIMITIVE TYPES IN JAVA:
         * ---------------------------------------------------------------------------------
         * Primitive | Wrapper   | Size    | Default | Range / Notes
         * ----------|-----------|---------|---------|--------------------------------------
         * byte      | Byte      | 8-bit   | 0       | -128 to 127
         * short     | Short     | 16-bit  | 0       | -32,768 to 32,767
         * int       | Integer   | 32-bit  | 0       | -2^31 to 2^31 - 1 (~2.14 billion)
         * long      | Long      | 64-bit  | 0L      | -2^63 to 2^63 - 1
         * float     | Float     | 32-bit  | 0.0f    | IEEE 754 single-precision (~6-7 decimal digits)
         * double    | Double    | 64-bit  | 0.0d    | IEEE 754 double-precision (~15 decimal digits)
         * boolean   | Boolean   | ~1 bit* | false   | true / false (*JVM-dependent: 1 byte in arrays)
         * char      | Character | 16-bit  | '\u0000'| 0 to 65,535 (UNSIGNED UTF-16 code unit!)
         * ---------------------------------------------------------------------------------
         *
         * WHY DO WRAPPERS EXIST?
         *  1. Generics in Java DO NOT support primitives (`List<int>` is illegal -> must use `List<Integer>`).
         *  2. Nullability: Primitives cannot be `null`; Wrappers can represent missing values.
         *  3. Utility methods: `Integer.parseInt()`, `Double.isNaN()`, `Character.isLetter()`.
         *
         * MEMORY FOOTPRINT (64-bit HotSpot JVM with Compressed OOPs):
         *  - `int`     : 4 bytes (stored directly on stack or inline inside object/array).
         *  - `Integer` : 16 bytes (12-byte Object Header + 4-byte int value) + 4-byte reference pointer = 20 bytes!
         */

        // Numeric Literals (Binary, Hex, Octal, Underscores for readability):
        int million = 1_000_000;
        int binaryFlags = 0b1010_1100; // 172
        int hexColor = 0xFF_00_80;
        System.out.println("Readable literals: million=" + million + ", binary=" + binaryFlags + ", hex=" + hexColor);

        // Numeric Promotion Rule:
        // Arithmetic on byte/short/char automatically promotes operands to `int`!
        byte b1 = 10, b2 = 20;
        // byte b3 = b1 + b2; // COMPILE ERROR! (b1 + b2) evaluates to `int`!
        byte b3 = (byte) (b1 + b2);
        System.out.println("Byte promotion result (cast back): " + b3);

        // Unsigned Arithmetic Support (Java 8+):
        // Java doesn't have an `unsigned int` keyword, so Integer provides unsigned helper methods:
        int maxSigned = -1; // All 32 bits are 1 (0xFFFFFFFF)
        System.out.println("Signed value of 0xFFFFFFFF: " + maxSigned
                + " | Unsigned value: " + Integer.toUnsignedString(maxSigned)); // 4294967295

        // FLOATING-POINT (IEEE 754) PITFALLS:
        double calc = 0.1 + 0.2;
        System.out.println("IEEE 754 Trap: 0.1 + 0.2 = " + calc + " (equals 0.3? " + (calc == 0.3) + ")");

        // Solution for Financial / Exact Math: BigDecimal
        // NEVER use `new BigDecimal(0.1)` (passes inaccurate binary double)!
        // ALWAYS use `new BigDecimal("0.1")` or `BigDecimal.valueOf(0.1)`!
        BigDecimal exact = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        System.out.println("BigDecimal exact: 0.1 + 0.2 = " + exact + " (equals 0.3? " + (exact.compareTo(new BigDecimal("0.3")) == 0) + ")");

        // Division / Rounding with BigDecimal:
        BigDecimal ten = new BigDecimal("10");
        BigDecimal three = new BigDecimal("3");
        System.out.println("BigDecimal 10 / 3 (HALF_UP): " + ten.divide(three, 4, RoundingMode.HALF_UP));

        // NaN (Not a Number) & Infinity rules:
        double nan = 0.0 / 0.0;
        double inf = 1.0 / 0.0;
        System.out.println("0.0/0.0 is NaN: " + Double.isNaN(nan) + " | Note: (NaN == NaN) is ALWAYS " + (nan == nan));
        System.out.println("1.0/0.0 is Infinity: " + Double.isInfinite(inf));
    }

    // =========================================================================================
    // SECTION 4: AUTOBOXING, UNBOXING, THE INTEGER CACHE TRAP & NPE GOTCHAS
    // =========================================================================================
    private static void section4_AutoboxingAndCacheTraps() {
        printHeader("4. AUTOBOXING, UNBOXING & THE INTEGER CACHE TRAP");
        /*
         * Autoboxing = compiler inserts `Integer.valueOf(primitive)`
         * Unboxing   = compiler inserts `wrapper.intValue()`
         */

        // TRAP 1: THE INTEGER CACHE (-128 to 127)
        // Per JLS §5.1.7, Integer.valueOf() MUST cache values from -128 to 127.
        // Same applies to Byte, Short, Long (-128..127), Character (0..127), Boolean (TRUE/FALSE).
        Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;

        System.out.println("Integer 127 == 127 (Cached same object reference):     " + (a == b));      // TRUE!
        System.out.println("Integer 128 == 128 (New heap objects, != references):  " + (c == d));      // FALSE!
        System.out.println("Integer 128.equals(128) (Always use .equals for wrappers): " + c.equals(d)); // TRUE!

        // Note: `new Integer(127)` was deprecated in Java 9 and marked for removal because it bypasses the cache!

        // TRAP 2: UNBOXING NULL -> NullPointerException
        Integer nullableInt = null;
        try {
            int unboxed = nullableInt; // Compiles to: nullableInt.intValue() -> BOOM!
            System.out.println(unboxed);
        } catch (NullPointerException e) {
            System.out.println("Unboxing null threw NullPointerException as expected!");
        }

        // TRAP 3: TERNARY OPERATOR SILENT UNBOXING NPE!
        // If one branch of a ternary operator is a primitive (`0`) and the other is a Wrapper (`Integer`),
        // Java unboxes the Wrapper to match the primitive type!
        boolean flag = true;
        Integer maybeNull = null;
        try {
            Integer result = flag ? maybeNull : 0; // Unboxes `maybeNull` to `int` BEFORE re-boxing to `Integer`!
            System.out.println(result);
        } catch (NullPointerException e) {
            System.out.println("Ternary mixed primitive/wrapper trap threw NPE!");
        }

        // TRAP 4: ACCIDENTAL BOXING IN TIGHT LOOPS (Performance Killer)
        // Bad:  Long sum = 0L; for (long i = 0; i < 1_000_000; i++) sum += i;
        //       Every += unboxes `sum`, adds `i`, and creates a brand new `Long` object on the heap!
        // Good: Use primitive `long sum = 0L;`!
        long fastSum = 0L;
        for (int i = 1; i <= 1000; i++) {
            fastSum += i;
        }
        System.out.println("Primitive loop sum (zero heap allocation): " + fastSum);
    }

    // =========================================================================================
    // SECTION 5: java.lang.Character & COMPLETE UNICODE HANDLING
    // =========================================================================================
    private static void section5_CharacterAndUnicodeDeepDive() {
        printHeader("5. java.lang.Character & COMPLETE UNICODE HANDLING");
        /*
         * UNICODE FUNDAMENTALS (CRITICAL FOR EVERY JAVA ENGINEER):
         * ---------------------------------------------------------------------------------
         * 1. Unicode assigns a unique integer called a CODE POINT to every character in the world:
         *    Range:   (1,114,112 possible characters).
         *    - BMP (Basic Multilingual Plane): U+0000 to U+FFFF (fits in 16 bits).
         *    - Supplementary Planes:           U+10000 to U+10FFFF (Emojis, Math symbols, ancient scripts).
         *
         * 2. JAVA's `char` IS NOT A UNICODE CHARACTER!
         *    Java `char` is a 16-bit UNSIGNED UTF-16 CODE UNIT!
         *    - BMP characters fit in ONE `char` (e.g. 'A' = U+0041, 'अ' = U+0905).
         *    - Supplementary characters (> U+FFFF, like 🚀 U+1F680) DO NOT FIT in 16 bits!
         *      UTF-16 encodes them as a SURROGATE PAIR of TWO `char`s:
         *        - High Surrogate: \uD800 to \uDBFF
         *        - Low Surrogate:  \uDC00 to \uDFFF
         *
         * 3. CODE UNIT vs. CODE POINT vs. GRAPHEME CLUSTER:
         *    - Code Unit:        1 `char` (16 bits). `"🚀".length()` == 2!
         *    - Code Point:       1 true Unicode symbol. `"🚀".codePointCount(0, 2)` == 1!
         *    - Grapheme Cluster: What a human perceives as 1 visual character!
         *                        E.g. Family emoji "👨‍👩‍👧‍👦" = 7 code points (4 people + 3 Zero-Width Joiners) = 11 chars!
         */

        // 1. Code Unit vs Code Point Demonstration:
        String rocket = "🚀"; // U+1F680 (Rocket emoji, supplementary plane)
        System.out.println("String: " + rocket);
        System.out.println("   .length() [UTF-16 code units / chars]: " + rocket.length()); // 2!
        System.out.println("   .codePointCount() [actual Unicode chars]: " + rocket.codePointCount(0, rocket.length())); // 1!

        int rocketCodePoint = rocket.codePointAt(0);
        System.out.printf("   Code Point: U+%X | High Surrogate: \\u%04X | Low Surrogate: \\u%04X%n",
                rocketCodePoint, (int) rocket.charAt(0), (int) rocket.charAt(1));
        System.out.println("   Is charAt(0) high surrogate? " + Character.isHighSurrogate(rocket.charAt(0)));
        System.out.println("   Is charAt(1) low surrogate?  " + Character.isLowSurrogate(rocket.charAt(1)));
        System.out.println("   Java 21 Character.isEmoji(codePoint): " + Character.isEmoji(rocketCodePoint)); // true!

        // 2. Iterating Safely Over Code Points (NEVER use charAt(i) for arbitrary Unicode strings!):
        String mixed = "Hi 👋 Java ☕!";
        System.out.print("   Safe codePoints() stream: ");
        mixed.codePoints().forEach(cp -> System.out.print("[" + Character.toString(cp) + "] "));
        System.out.println();

        // 3. Reversing a String Safely (Naïve char[] reversal CORRUPTS surrogate pairs!):
        // Wrong: new StringBuilder("A🚀B").reverse() works because StringBuilder.reverse() explicitly
        // handles surrogate pairs internally, whereas a manual `char[]` swap breaks emojis into garbage!
        String reversedSafely = new StringBuilder("A🚀B").reverse().toString();
        System.out.println("   StringBuilder.reverse() preserves surrogate pairs: " + reversedSafely); // B🚀A

        // 4. Character Inspection & Conversion Utilities:
        char ch = '7';
        System.out.println("\nCharacter utility checks for '7':");
        System.out.println("   isDigit: " + Character.isDigit(ch) + " | isLetter: " + Character.isLetter(ch));
        System.out.println("   numericValue: " + Character.getNumericValue(ch) + " | hex digit('F', 16): " + Character.digit('F', 16));
        System.out.println("   Character.forDigit(15, 16): " + Character.forDigit(15, 16)); // 'f'
        System.out.println("   Unicode name of U+1F680: " + Character.getName(rocketCodePoint)); // ROCKET

        // Note on Character.isWhitespace vs Character.isSpaceChar:
        // isWhitespace('\n') is TRUE (includes tab, newline, form feed).
        // isSpaceChar('\n') is FALSE (only matches Unicode space separators like ' ', non-breaking space \u00A0).
        System.out.println("   isWhitespace('\\n'): " + Character.isWhitespace('\n')
                + " | isSpaceChar('\\n'): " + Character.isSpaceChar('\n'));

        // 5. GRAPHEME CLUSTERS (Counting Human-Perceived Characters with BreakIterator):
        String familyEmoji = "👨\u200D👩\u200D👧\u200D👦"; // 4 emojis joined by Zero-Width Joiner (U+200D)
        BreakIterator graphemeIterator = BreakIterator.getCharacterInstance(Locale.ROOT);
        graphemeIterator.setText(familyEmoji);
        int graphemeCount = 0;
        while (graphemeIterator.next() != BreakIterator.DONE) {
            graphemeCount++;
        }
        System.out.println("\nGrapheme Cluster analysis for family emoji (" + familyEmoji + "):");
        System.out.println("   char length (UTF-16 units): " + familyEmoji.length()); // 11
        System.out.println("   code points:                " + familyEmoji.codePointCount(0, familyEmoji.length())); // 7
        System.out.println("   visual grapheme clusters:   " + graphemeCount); // 1!

        // 6. UNICODE NORMALIZATION (NFC vs NFD):
        // "é" can be represented as ONE precomposed code point (\u00E9)
        // OR as TWO decomposed code points ('e' + combining acute accent \u0301)!
        String nfc = "\u00E9";       // Precomposed: é
        String nfd = "e\u0301";      // Decomposed: e + ´
        System.out.println("\nUnicode Normalization:");
        System.out.println("   nfc (" + nfc + ") equals nfd (" + nfd + ") BEFORE normalization? " + nfc.equals(nfd)); // FALSE!
        String normalizedNfd = Normalizer.normalize(nfd, Normalizer.Form.NFC);
        System.out.println("   nfc equals nfd AFTER Normalizer.Form.NFC?         " + nfc.equals(normalizedNfd)); // TRUE!
    }

    // =========================================================================================
    // SECTION 6: STRING INTERNALS (COMPACT STRINGS, POOL, STRINGBUILDER, MODERN API)
    // =========================================================================================
    private static void section6_StringInternalsAndModernAPIs() {
        printHeader("6. STRING INTERNALS, COMPACT STRINGS & MODERN APIs");
        /*
         * 1. JAVA 9+ COMPACT STRINGS (JEP 254):
         *    - Pre-Java 9: `String` stored a `char[]` (2 bytes per char, even for pure ASCII!).
         *    - Java 9+:    `String` stores a `byte[]` + a 1-byte `coder` flag:
         *                  `LATIN1 (0)` = 1 byte per char (if all chars <= 0xFF)
         *                  `UTF16  (1)` = 2 bytes per char (if any char > 0xFF)
         *    - Result: ~40-50% heap memory reduction for typical English/JSON/XML workloads!
         *
         * 2. THE STRING CONSTANT POOL:
         *    - Stored in Heap (moved out of PermGen in Java 7 so unused interned strings can be GC'd).
         *    - String literals ("hello") are automatically interned in the pool.
         *    - `new String("hello")` forces a NEW object on the regular heap outside the pool!
         */

        String lit1 = "Java";
        String lit2 = "Java";
        String heapStr = new String("Java");
        String internedHeap = heapStr.intern();

        System.out.println("lit1 == lit2 (both from String Pool):     " + (lit1 == lit2));        // true
        System.out.println("lit1 == heapStr (pool vs heap object):    " + (lit1 == heapStr));     // false
        System.out.println("lit1 == internedHeap (returned pool ref): " + (lit1 == internedHeap));// true

        // 3. StringBuilder vs StringBuffer:
        // - StringBuilder (Java 5+): Fast, NOT thread-safe. Default choice!
        // - StringBuffer (Java 1.0): Synchronized methods, slower. Legacy.
        // Note: Single-line `"a" + b + "c"` in Java 9+ uses `invokedynamic` (StringConcatFactory)
        // for optimal allocation; inside LOOPS, ALWAYS use `StringBuilder` explicitly!
        StringBuilder sb = new StringBuilder(32);
        sb.append("Built ").append("with ").append("StringBuilder");
        System.out.println("StringBuilder result: " + sb);

        // 4. Modern String APIs (Java 11 - Java 21):
        String padded = "\u2000  Hello Java 21  \u2000"; // \u2000 is Unicode En Quad whitespace!
        // Notice: trim() only removes <= U+0020 (ASCII control/space)! strip() removes ALL Unicode whitespace!
        System.out.println("trim()  leaves Unicode \\u2000: '" + padded.trim() + "'");
        System.out.println("strip() removes Unicode \\u2000: '" + padded.strip() + "'");

        System.out.println("'   '.isEmpty() vs .isBlank(): " + "   ".isEmpty() + " vs " + "   ".isBlank());
        System.out.println("repeat(3): " + "Ha".repeat(3));

        // Java 15+ Text Blocks & formatted():
        String json = """
                {
                  "language": "%s",
                  "version": %d
                }
                """.formatted("Java", 21);
        System.out.println("Text Block formatted():\n" + json);

        // Java 12+ transform() for fluent functional chaining on Strings:
        String transformed = "  hello world  "
                .transform(String::strip)
                .transform(String::toUpperCase);
        System.out.println("String.transform() pipeline: " + transformed);
    }

    // =========================================================================================
    // SECTION 7: CHARSETS, LOCALE TRAPS & STRING IMMUTABILITY
    // =========================================================================================
    private static void section7_CharsetsLocaleAndImmutability() {
        printHeader("7. CHARSETS (UTF-8), LOCALE TRAPS & IMMUTABILITY");

        // 1. Charset Encoding Trap:
        // Pre-Java 18, `str.getBytes()` used the OS default encoding (Windows-1252 on Windows, UTF-8 on Linux)!
        // Java 18+ (JEP 400) made UTF-8 the default charset across all platforms.
        // Best Practice: ALWAYS pass `StandardCharsets.UTF_8` explicitly!
        String text = "Hello € 🚀";
        byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] utf16Bytes = text.getBytes(StandardCharsets.UTF_16BE);
        System.out.println("'" + text + "' byte length -> UTF-8: " + utf8Bytes.length
                + " bytes | UTF-16BE: " + utf16Bytes.length + " bytes");
        String decoded = new String(utf8Bytes, StandardCharsets.UTF_8);
        System.out.println("Decoded back from UTF-8: " + decoded);

        // 2. The Infamous Turkish Locale Trap:
        // Calling `.toUpperCase()` without arguments uses Locale.getDefault()!
        // In Turkish locale, lowercase 'i' uppercases to dotted 'İ' (\u0130), NOT 'I'!
        // That breaks SQL keywords ("SELECT * FROM ..." or HTTP headers like "title".toUpperCase() -> "TİTLE")!
        Locale turkish = Locale.forLanguageTag("tr");
        System.out.println("Turkish 'title'.toUpperCase(tr):   " + "title".toUpperCase(turkish));    // TİTLE
        System.out.println("Safe Locale.ROOT toUpperCase():    " + "title".toUpperCase(Locale.ROOT)); // TITLE

        /*
         * 3. Why is String `final` and Immutable?
         *    - String Pool: Multiple references share the same string object safely.
         *    - Security: File paths, DB connection URLs, class names cannot be mutated after validation.
         *    - Thread-Safety: Immutable objects need zero synchronization across threads.
         *    - HashCode Caching: `String.hashCode()` is computed once and cached inside the object,
         *      making Strings blazingly fast keys in HashMap/HashSet!
         *
         * Why store passwords in `char[]` instead of `String`?
         *    - Strings sit in memory (or String Pool) until GC collects them and cannot be wiped.
         *    - A `char[]` can be overwritten immediately with `Arrays.fill(pwd, '\0')` after use!
         */
    }

    // =========================================================================================
    // SECTION 8: java.lang.ref (REFERENCE TYPES & GARBAGE COLLECTION)
    // =========================================================================================
    private static void section8_ReferenceTypesAndGC() {
        printHeader("8. REFERENCE TYPES (Strong, Soft, Weak, Cleaner)");
        /*
         *  Reference Strength Hierarchy (java.lang.ref):
         *   1. Strong Reference  : `Object o = new Object();` -> NEVER collected while reachable.
         *   2. SoftReference     : Collected ONLY when JVM is critically low on memory (before OOM).
         *                          Great for memory-sensitive image/data caches.
         *   3. WeakReference     : Collected on the VERY NEXT GC cycle once no strong refs exist.
         *                          Powers `WeakHashMap` and `ThreadLocal` internals.
         *   4. PhantomReference  : Always returns `null` on `.get()`. Enqueued into a `ReferenceQueue`
         *                          strictly to know when an object's memory was freed (used for off-heap cleanup).
         *   5. Cleaner (Java 9+) : Safe modern replacement for deprecated `Object.finalize()`.
         */

        // 1. WeakReference example:
        Object strongObj = new Object();
        WeakReference<Object> weakRef = new WeakReference<>(strongObj);
        System.out.println("WeakRef before nulling strong ref: " + (weakRef.get() != null)); // true

        strongObj = null; // No strong references left!
        System.gc();      // Hint JVM to run GC
        System.out.println("WeakRef after GC hint:             " + (weakRef.get() != null)); // Usually false!

        // 2. SoftReference example:
        SoftReference<byte[]> softCache = new SoftReference<>(new byte[1024]);
        System.out.println("SoftRef stays alive while heap has space: " + (softCache.get() != null)); // true

        // 3. Java 9+ Cleaner (Replacing finalize()):
        Cleaner cleaner = Cleaner.create();
        class NativeResource implements AutoCloseable {
            private final Cleaner.Cleanable cleanable;
            NativeResource(String name) {
                // Note: Cleaning action lambda MUST NOT capture `this`! Otherwise object stays alive forever!
                this.cleanable = cleaner.register(this, () -> System.out.println("   [Cleaner] Freed native resource: " + name));
            }
            @Override
            public void close() {
                cleanable.clean(); // Explicitly run cleanup once
            }
        }

        try (NativeResource res = new NativeResource("DirectByteBuffer-1")) {
            System.out.println("Using NativeResource inside try-with-resources...");
        }
    }

    // =========================================================================================
    // SECTION 9: java.lang.Enum & java.lang.Record INTERNALS
    // =========================================================================================
    private static void section9_EnumAndRecordInternals() {
        printHeader("9. java.lang.Enum & java.lang.Record INTERNALS");

        // 1. Enums compile to `final class OrderStatus extends java.lang.Enum<OrderStatus>`
        enum OrderStatus {
            PENDING(1), SHIPPED(2), DELIVERED(3);
            private final int code;
            OrderStatus(int code) { this.code = code; }
            public int code() { return code; }
        }

        OrderStatus status = OrderStatus.SHIPPED;
        System.out.println("Enum name: " + status.name()
                + " | ordinal: " + status.ordinal()
                + " | custom code: " + status.code());
        System.out.println("Enum superclass: " + OrderStatus.class.getSuperclass().getName()); // java.lang.Enum

        // 2. Records (Java 16+) compile to `final class Point extends java.lang.Record`
        // Automatically provides: private final fields, canonical constructor, accessors, equals, hashCode, toString.
        record Point(int x, int y) {
            // Compact Constructor (validation without repeating parameter assignments):
            Point {
                if (x < 0 || y < 0) throw new IllegalArgumentException("Coordinates must be non-negative");
            }
        }

        Point p = new Point(10, 20);
        System.out.println("Record instance: " + p + " | Superclass: " + Point.class.getSuperclass().getName()); // java.lang.Record
    }

    // =========================================================================================
    // SECTION 10: java.lang.Throwable HIERARCHY & AutoCloseable
    // =========================================================================================
    private static void section10_ThrowableAndAutoCloseable() {
        printHeader("10. java.lang.Throwable HIERARCHY & SUPPRESSED EXCEPTIONS");
        /*
         *  java.lang.Throwable
         *   ├── java.lang.Error (Unchecked - JVM failures, don't catch: OutOfMemoryError, StackOverflowError)
         *   └── java.lang.Exception
         *        ├── Checked Exceptions (Compile-time enforced: IOException, SQLException, ClassNotFoundException)
         *        └── java.lang.RuntimeException (Unchecked: NullPointerException, IllegalArgumentException)
         *
         *  TRY-WITH-RESOURCES & SUPPRESSED EXCEPTIONS:
         *   If an exception is thrown inside the `try` block AND another exception is thrown
         *   by `AutoCloseable.close()`, Java does NOT lose the primary exception!
         *   Instead, it attaches the `close()` exception via `primaryEx.addSuppressed(closeEx)`.
         */

        class FailingResource implements AutoCloseable {
            @Override
            public void close() {
                throw new IllegalStateException("Exception during close()");
            }
        }

        try (FailingResource ignored = new FailingResource()) {
            throw new RuntimeException("Primary exception in try block");
        } catch (RuntimeException primary) {
            System.out.println("Caught primary: " + primary.getMessage());
            for (Throwable suppressed : primary.getSuppressed()) {
                System.out.println("Attached suppressed exception: " + suppressed.getMessage());
            }
        }
    }

    // Helper method for clean console output separation
    private static void printHeader(String title) {
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println(">>> " + title);
        System.out.println("-------------------------------------------------------------------");
    }
}

