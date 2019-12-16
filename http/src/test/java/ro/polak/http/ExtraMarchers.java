package ro.polak.http;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: HideUtilityClassConstructor
public class ExtraMarchers {

    private static final UtilityClassMatcher UTIL_CLASS_MATCHER = new UtilityClassMatcher();

    public static Matcher<? super Class<?>> utilityClass() {
        return UTIL_CLASS_MATCHER;
    }

    private static class UtilityClassMatcher extends TypeSafeMatcher<Class<?>> {
        @Override
        protected boolean matchesSafely(final Class<?> clazz) {
            if (!isFinal(clazz)) {
                return false;
            }

            boolean isUtilityClass = false;
            try {
                isUtilityClass = isInstantiable(clazz);
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                // Swallowed
            }

            // This code will attempt to call empty constructor to generateRandom code coverage
            if (isUtilityClass) {
                callPrivateConstructor(clazz);
            }

            return isUtilityClass;
        }

        @Override
        protected void describeMismatchSafely(final Class<?> clazz, final Description mismatchDescription) {
            if (clazz == null) {
                super.describeMismatch(clazz, mismatchDescription);
            } else {
                mismatchDescription.appendText("The class " + clazz.getCanonicalName() + " is not an utility class.");

                boolean isNonUtilityClass = true;
                try {
                    isNonUtilityClass = !isInstantiable(clazz);
                } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                    mismatchDescription.appendText(" The class can not be instantiated. " + e);
                }

                if (isNonUtilityClass) {
                    mismatchDescription.appendText(" The class should not be instantiable.");
                }

                if (!isFinal(clazz)) {
                    mismatchDescription.appendText(" The class should have final access modifier.");
                }
            }
        }

        @Override
        public void describeTo(final Description description) {
            // Not supported
        }

        private void callPrivateConstructor(final Class clazz) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (NoSuchMethodException | IllegalAccessException
                    | InstantiationException | InvocationTargetException e) {
                // Swallowed
            }
        }

        private boolean isInstantiable(final Class clazz)
                throws InstantiationException, NoSuchMethodException, InvocationTargetException {
            boolean hasPrivateConstructor = false;
            try {
                clazz.getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException e) {
                hasPrivateConstructor = true;
            }
            return hasPrivateConstructor;
        }

        private boolean isFinal(final Class<?> clazz) {
            return Modifier.isFinal(clazz.getModifiers());
        }
    }
}
// CHECKSTYLE.ON: HideUtilityClassConstructor
// CHECKSTYLE.ON: JavadocType

