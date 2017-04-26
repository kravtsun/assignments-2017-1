package ru.spbau.mit;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Stream.builder;

public final class FirstPartTasks {
//    private static Function<Album, Stream<Track>> albumTracksStreamGetter = ;
    private FirstPartTasks() {}
    // Список названий альбомов
    public static List<String> allNames(Stream<Album> albums) {
        return albums.map(Album::getName).collect(Collectors.toList());
    }

    // Список названий альбомов, отсортированный лексикографически по названию
    public static List<String> allNamesSorted(Stream<Album> albums) {
        return albums.map(Album::getName).sorted().collect(Collectors.toList());
    }

    // Список треков, отсортированный лексикографически по названию, включающий все треки альбомов из 'albums'
    public static List<String> allTracksSorted(Stream<Album> albums) {
        Stream<Album> sortedAlbums = albums.sorted(Comparator.comparing(Album::getName));
        Function<Album, Stream<String>> getTracksOfAlbum = (Album album) -> album.getTracks().stream().map(track -> {
            return track.getName();
        });
        return sortedAlbums.flatMap(getTracksOfAlbum).sorted(String::compareTo).collect(Collectors.toList());
    }

    // Список альбомов, в которых есть хотя бы один трек с рейтингом более 95, отсортированный по названию
    public static List<Album> sortedFavorites(Stream<Album> s) {
        final Predicate<Track> trackPredicate = track -> track.getRating() > 95;
        Predicate<Album> albumPredicate = a -> a.getTracks().stream().anyMatch(trackPredicate);
        return s.filter(albumPredicate).sorted(Comparator.comparing(Album::getName)).collect(Collectors.toList());
    }

    // Сгруппировать альбомы по артистам
    public static Map<Artist, List<Album>> groupByArtist(Stream<Album> albums) {
        return albums.collect(Collectors.groupingBy(Album::getArtist));
    }

    private static class Pair<S, T> {
        Pair(S rhs_first, T rhs_second) {
            this.first = rhs_first;
            this.second = rhs_second;
        }

        public S getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

        private final S first;
        private final T second;
    }

    // Сгруппировать альбомы по артистам (в качестве значения вместо объекта 'Album' использовать его имя)
    public static Map<Artist, List<String>> groupByArtistMapName(Stream<Album> albums) {
        Supplier<List<String>> listSupplier = () -> new ArrayList<String>();
        BiConsumer<List<String>, Album> listAccumulator = (strings, album) -> strings.add(album.getName());
        Collector<Album, ?, List<String>> albumToStringList = Collector.of(listSupplier, listAccumulator, listStringCombiner);

        Collector<Album, ?, Map<Artist, List<String>>> groupingCollector =
                Collectors.groupingBy(a -> a.getArtist(), albumToStringList);
        return albums.collect(groupingCollector);
    }

    // Число повторяющихся альбомов в потоке
    public static long countAlbumDuplicates(Stream<Album> albums) {
        Collector<Album, ?, Map<Album, Integer>> counter = Collectors.groupingBy(a -> a, Collectors.summingInt(a -> 1));
        Function<Map<Album, Integer>, Integer> nonDistinctCounter = (m) -> {
            Integer cnt = 0;
            for (Map.Entry<Album, Integer> p : m.entrySet()) {
                cnt += p.getValue();
            }
            return cnt - m.size();
        };

        return albums.collect(Collectors.collectingAndThen(counter, nonDistinctCounter));
    }

    // Альбом в котором максимум рейтинга минимален
    // (если в альбоме нет ни одного трека, считать, что максимум рейтинга в нем --- 0)
    public static Optional<Album> minMaxRating(Stream<Album> albums) {
        ToIntFunction<Album> ratingGetter = new ToIntFunction<Album>() {
            private final Track defaultTrack = new Track("", 0);
            @Override
            public int applyAsInt(Album a) {
                return a.getTracks().stream().max(Comparator.comparingInt(Track::getRating)).orElse(defaultTrack).getRating();
            }
        };
        Comparator<Album> albumComparator = Comparator.comparingInt(ratingGetter);
        return albums.min(albumComparator);
    }

    // Список альбомов, отсортированный по убыванию среднего рейтинга его треков (0, если треков нет)
    public static List<Album> sortByAverageRating(Stream<Album> albums) {
        ToDoubleFunction<Album> averageRatingGetter = new ToDoubleFunction<Album>() {
            @Override
            public double applyAsDouble(Album value) {
                return value.getTracks().stream().mapToDouble(Track::getRating).average().orElse(0.0);
            }
        };
        return albums.sorted(Comparator.comparingDouble(averageRatingGetter).reversed()).collect(Collectors.toList());
    }

    // Произведение всех чисел потока по модулю 'modulo'
    // (все числа от 0 до 10000)
    public static int moduloProduction(IntStream stream, int modulo) {
        IntBinaryOperator sumModule = (left, right) -> (left * right) % modulo;
        return stream.reduce(1, sumModule);
    }

    // Вернуть строку, состояющую из конкатенаций переданного массива, и окруженную строками "<", ">"
    // см. тесты
    public static String joinTo(String... strings) {
//        Stream<String> ss = Stream.concat(Stream.concat(Stream.of("<"), Stream.of(strings)), Stream.of(">"));
        Stream<String> ss = Stream.of(strings);
        return ss.collect(Collectors.joining(", ", "<", ">"));
    }

    // Вернуть поток из объектов класса 'clazz'
    public static <R> Stream<R> filterIsInstance(Stream<?> s, Class<R> clazz) {
        Predicate<Object> classPredicate = new Predicate<Object>() {
            @Override
            public boolean test(Object r) {
                return clazz.isAssignableFrom(r.getClass());
            }
        };
        Function<Object, R> converter = (o) -> (R)o;
        return s.filter(classPredicate).map(converter);
    }

    private static BinaryOperator<List<String>> listStringCombiner =
            (left, right) -> {
                left.addAll(right);
                return left;
            };
}
