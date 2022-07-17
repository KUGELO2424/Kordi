package pl.kucharski.Kordi.validator;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * Class responsible for email pattern validation.
 *
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@Service
public class EmailValidator implements Predicate<String> {

    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    public boolean test(String s) {
        return s.matches(regex);
    }
}
