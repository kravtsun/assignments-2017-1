package ru.spbau.mit;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static ru.spbau.mit.SecondPartTasks.*;
import static java.util.Collections.emptyList;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> files = Arrays.asList(
                "src/main/java/fru/spbau/mit/Artist.java"
                , "src/main/java/ru/spbau/mit/Album.java"
                , "src/main/java/ru/spbau/mit/.gitkeep"
                , "src/main/java/ru/spbau/mit/Track.java"
                , "src/main/java/ru/spbau/mit/FirstPartTasks.java"
                , "src/main/java/ru/spbau/mit/SecondPartTasks.java"
        );
        assertEquals(emptyList(), findQuotes(files, "qaef2wefadsf"));

        assertEquals(Stream.of(
                "    private String name;"
                , "    public Album(Artist artist, String name, Track... tracks) {"
                , "    private final String name;"
                , "    Track(String name, int rating) {"
                , "            String name = stringListEntry.getKey();").sorted().collect(Collectors.toList()),
                findQuotes(files, "String name").stream().sorted().collect(Collectors.toList()));
    }

    @Test
    public void testPiDividedBy4() {
        final double piDividedBy4 = 0.78539816339;
        final double delta = 1e-1;
        assertEquals(piDividedBy4, SecondPartTasks.piDividedBy4(), delta);
    }

    @Test
    public void testFindPrinter() {
        assertEquals(ARTIST_7, findPrinter(
                ImmutableMap.of(ARTIST_0, ALBUM_2
                , ARTIST_3, ALBUM_9
                , ARTIST_7, ALBUM_5
                , ARTIST_1, emptyList()
                )
                )
        );

        assertEquals(ARTIST_5, findPrinter(
                ImmutableMap.of(ARTIST_2, ALBUM_0
                        , ARTIST_4, ALBUM_1
                        , ARTIST_5, ALBUM_3
                        , ARTIST_6, ALBUM_4
                        , ALBUM_6.get(0), ALBUM_7
                )
                )
        );
    }

    @Test
    public void testCalculateGlobalOrder() {
        assertEquals(ImmutableMap.of(), calculateGlobalOrder(Arrays.asList(
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of(),
                ImmutableMap.of()
                )
        ));

        assertEquals(ImmutableMap.of(
                "pepper", 4,
                "bread", 47,
                "salt", 72,
                "meat", -1,
                "birds", 100004000
        ), calculateGlobalOrder(Arrays.asList(
                ImmutableMap.of(
                        "bread", 12,
                        "meat", -1,
                        "birds", 100000000,
                        "salt", 72
                ),
                ImmutableMap.of(
                        "birds", 4000,
                        "bread", 35
                ),
                ImmutableMap.of(
                        "pepper", 4
                )
                )
        ));
    }

    private static final String ARTIST_0 = "Morcheeba";
    private static final String ARTIST_1 = "Temples";
    private static final String ARTIST_2 = "God Help the Girl";
    private static final String ARTIST_3 = "All India Radio";
    private static final String ARTIST_4 = "UNKLE";
    private static final String ARTIST_5 = "Bonobo";
    private static final String ARTIST_6 = "Grimes";
    private static final String ARTIST_7 = "Massive Attack";
    private static final List<String> ALBUM_0 = Arrays.asList("Sun Structures", "Shelter Song", "Sun Structures", "The Golden Throne", "Keep in the Dark", "Mesmerise", "Move With The Season", "Colours to Life", "A Question Isn't Answered", "The Guesser", "Test of Time", "Sand Dance", "Fragment's Light");
    private static final List<String> ALBUM_1 = Arrays.asList("Keep In The Dark", "Keep in the Dark", "Jewel of Mine Eye");
    private static final List<String> ALBUM_2 = Arrays.asList("Big Calm", "The Sea", "Shoulder Holster", "Part of the Process", "Blindfold", "Let Me See", "Bullet Proof", "Over and Over", "Friction", "diggin' in a watery grave", "Fear and Love", "Big Calm");
    private static final List<String> ALBUM_3 = Arrays.asList("Charango", "Slow Down", "Otherwise", "Aqualung", "Sao Paulo", "Charango (Feat: Pace Won)", "What New York Couples Fight About (Feat: Kurt Wagner)", "Undress Me Now", "Way Beyond", "Women Lose Weight (Feat: Slick Rick)", "Get Along (Feat: Pace Won)", "Public Displays of Affection", "The Great London Traffic Warden Massacre", "Slow Down (Instrumental)", "Otherwise (Instrumental)", "Aqualung (Instrumental)", "Sao Paulo (Instrumental)", "Charango (Feat: Pace Won) (Instrumental)", "What New York Couples Fight About (Instrumental)", "Undress Me Now (Instrumental)", "Way Beyond (Instrumental)", "Women Lose Weight (Feat: Slick Rick) (Instrumental)", "Get Along (Feat: Pace Won) (Instrumental)", "Public Displays Of Affection (Instrumental)", "The Great London Traffic Warden Massacre (Instrumental)");
    private static final List<String> ALBUM_4 = Arrays.asList("Shelter Song", "Shelter Song", "Prisms");
    private static final List<String> ALBUM_5 = Arrays.asList("God Help The Girl", "I Suppose That Was A Prayer", "Act of the Apostle", "I Dumped You First", "Pretty When The Wind Blows", "I Know I Have To Eat", "God Help the Girl", "The Psychiatrist Is In", "The God Of Music", "If You Could Speak", "The Catwalk Of The Dukes", "Perfection as a Hipster", "Fuck This Shit", "Pretty Eve in the Tub", "A loving Kind Of Boy", "What Do You Want This Band To Sound Like", "Come Monday Night", "Collective Idiocy", "I'm Not Rich", "I'll Have to Dance With Cassie", "Stalinist Russia", "Baby’s Just Waiting", "Partick Whistle", "Musician, Please Take Heed", "I Just Want Your Jeans", "Invisible", "The World's Last Cassette", "A Down and Dusky Blonde", "Dress Up In You");
    private static final List<String> ALBUM_6 = Arrays.asList("God Help The Girl sd (Original Motion Picture Soundtrack)");
    private static final List<String> ALBUM_7 = Arrays.asList("Stills EP");
    private static final List<String> ALBUM_9 = Arrays.asList("Permanent Evolutions", "Open Sky Experiment (St-2Remix)", "Permanent Revolutions (Don Meers remix)", "Little Mexico", "How Many, For How Long (Morphodic Bliss Mix)", "Dark Ambient (am mix)", "Life and How to Do It", "For Angel (All India Radio vs. Don Meers Mix)", "Lo Fi Groovy", "Walking On A.I.R.", "Delhi Dub", "Pray To The TV Funk (Left Brain Mix)", "A Moment (TV Version)", "Old India", "The Long Goodbye");
}
