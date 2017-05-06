package ru.spbau.mit;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        return albums
                .map(Album::getTracks)
                .flatMap(Collection::stream)
                .map(Track::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    // Список альбомов, в которых есть хотя бы один трек с рейтингом более 95, отсортированный по названию
    public static List<Album> sortedFavorites(Stream<Album> s) {
        final int threshold = 95;
        return s.filter(a -> a.getTracks().stream()
                                          .anyMatch(track -> track.getRating() > threshold))
                .sorted(Comparator.comparing(Album::getName))
                .collect(Collectors.toList());
    }

    // Сгруппировать альбомы по артистам
    public static Map<Artist, List<Album>> groupByArtist(Stream<Album> albums) {
        return albums.collect(Collectors.groupingBy(Album::getArtist));
    }

    // Сгруппировать альбомы по артистам (в качестве значения вместо объекта 'Album' использовать его имя)
    public static Map<Artist, List<String>> groupByArtistMapName(Stream<Album> albums) {
        return albums.collect(
                Collectors.groupingBy(
                        Album::getArtist,
                        Collectors.mapping(
                                Album::getName,
                                Collectors.toList()
                        )
                )
        );
    }

    // Число повторяющихся альбомов в потоке
    public static long countAlbumDuplicates(Stream<Album> albums) {
        Map<Album, Long> albumsCount = albums.collect(
                Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        Stream<Long> sameCounts = albumsCount.entrySet().stream().map(e -> e.getValue() - 1);
        return sameCounts.mapToLong(Long::longValue).sum();
    }

    // Альбом в котором максимум рейтинга минимален
    // (если в альбоме нет ни одного трека, считать, что максимум рейтинга в нем --- 0)
    public static Optional<Album> minMaxRating(Stream<Album> albums) {
        ToIntFunction<Album> ratingGetter = new ToIntFunction<Album>() {
            @Override
            public int applyAsInt(Album a) {
                return a.getTracks()
                        .stream()
                        .mapToInt(Track::getRating)
                        .max()
                        .orElse(0);
            }
        };
        Comparator<Album> albumComparator = Comparator.comparingInt(ratingGetter);
        return albums.min(albumComparator);
    }

    // Список альбомов, отсортированный по убыванию среднего рейтинга его треков (0, если треков нет)
    public static List<Album> sortByAverageRating(Stream<Album> albums) {
        ToDoubleFunction<Album> averageRatingGetter = value -> value.getTracks()
                .stream()
                .mapToDouble(Track::getRating)
                .average()
                .orElse(0.0);
        return albums.sorted(Comparator
                .comparingDouble(averageRatingGetter)
                .reversed())
                .collect(Collectors.toList());
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
        Stream<String> ss = Stream.of(strings);
        return ss.collect(Collectors.joining(", ", "<", ">"));
    }

    // Вернуть поток из объектов класса 'clazz'
    public static <R> Stream<R> filterIsInstance(Stream<?> s, Class<R> clazz) {
        return s.filter(clazz::isInstance).map(clazz::cast);
    }
}
