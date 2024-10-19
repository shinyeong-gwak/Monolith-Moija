package com.example.monolithmoija.extractor;

import com.example.monolithmoija.entities.Recruit;

import java.util.Objects;
import java.util.function.Predicate;

public class Filter {
    public static Predicate<Recruit> category(String category) {
        return switch (category) {
            case "hobby" -> r -> r.getCategory().equals("hobby");
            case "language" -> r -> r.getCategory().equals("language");
            case "study" -> r -> r.getCategory().equals("study");
            case "employ" -> r -> r.getCategory().equals("employ");
            case "etc" -> r -> r.getCategory().equals("etc");
            default -> r -> true;
        };
    }


    public static Predicate<Recruit> availableOnly() {
        return Recruit::isAvailable;
    }
    public static Predicate<Recruit> stateEnable() {
        return Recruit::isStateRecruit;
    }
    public static Predicate<Recruit> stateDisable() {
        return r -> !r.isStateRecruit();
    }
}
