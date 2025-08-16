package utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Assertions {
    public static void assertObjects(Object original, Object updated, String... fieldsToSkip) {
        List<String> skipFields = Arrays.asList(fieldsToSkip);

        for (Field field : original.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!skipFields.contains(field.getName())) {
                try {
                    Object originalValue = field.get(original);
                    Object updatedValue = field.get(updated);

                    assertThat(
                            "Field '" + field.getName() + "' should remain unchanged",
                            updatedValue,
                            is(originalValue)
                    );

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }

}
