package edu.volkov.userapp.testdata;

import edu.volkov.userapp.model.User;

import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTestMatcher {
    private final BiConsumer<User, User> assertion;
    private final BiConsumer<Iterable<User>, Iterable<User>> iterableAssertion;

    public UserTestMatcher() {
        this.assertion = (a, e) -> assertThat(a).isEqualTo(e);
        this.iterableAssertion = (a, e) -> assertThat(a).isEqualTo(e);
    }

    public void assertMatch(User actual, User expected) {
        assertion.accept(actual, expected);
    }

    public void assertMatch(Iterable<User> actual, Iterable<User> expected) {
        iterableAssertion.accept(actual, expected);
    }
}
