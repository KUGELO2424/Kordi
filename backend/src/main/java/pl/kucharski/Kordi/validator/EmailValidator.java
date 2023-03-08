package pl.kucharski.Kordi.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Class responsible for email pattern validation.
 *
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@Service
public class EmailValidator implements Predicate<String> {

    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Test if string match to pattern
     *
     * @return true if matched, false if not
     */
    @Override
    public boolean test(String s) {
        return s.matches(regex);
    }
}
