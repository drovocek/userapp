package edu.volkov.userapp.testdata;

import edu.volkov.userapp.model.User;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestMatcher {
    private final BiConsumer<User, User> assertion;
    private final BiConsumer<Iterable<User>, Iterable<User>> iterableAssertion;

    public UserTestMatcher() {
        this.assertion = (a, e) -> assertThat(a).usingRecursiveComparison().ignoringFields("id").isEqualTo(e);
        this.iterableAssertion = (a, e) -> assertThat(a).usingElementComparatorIgnoringFields("id").isEqualTo(e);
    }

    public void assertMatch(User actual, User expected) {
        assertion.accept(actual, expected);
    }

    @SafeVarargs
    public final void assertMatch(Iterable<User> actual, User... expected) {
        assertMatch(actual, Stream.of(expected).collect(Collectors.toList()));
    }

    public void assertMatch(Iterable<User> actual, Iterable<User> expected) {
        iterableAssertion.accept(actual, expected);
    }
}