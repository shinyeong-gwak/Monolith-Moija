package com.example.monolithmoija.extractor;

import com.example.monolithmoija.entities.Recruit;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Sorter {
    public static Comparator<Recruit> viewType(String viewType) {
        return switch (viewType) {
            case "views" -> Comparator.comparing(Recruit::getViews).reversed();
            case "likes" -> Comparator.comparing(Recruit::getLikes).reversed();
            default -> Comparator.comparing(Recruit::getLatestWrite).reversed();
        };
    }
    public static Comparator<Recruit> state() {
        return Comparator.comparing(Recruit::isStateRecruit).reversed();
    }
}
