package ro.polak.http;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ExtraMarchers {

    private static final UtilityClassMatcher utilClassMatcher = new UtilityClassMatcher();

    public static Matcher<? super Class<?>> utilityClass() {
        return utilClassMatcher;
    }

    private static class UtilityClassMatcher extends TypeSafeMatcher<Class<?>> {
        @Override
        protected boolean matchesSafely(Class<?> clazz) {
            if (!isFinal(clazz)) {
                return false;
            }

            boolean isUtilityClass = false;
            try {
                isUtilityClass = isInstantiable(clazz);
            } catch (ClassNotFoundException | InstantiationException e) {
                // Swallowed
            }

            // This code will attempt to call empty constructor to generate code coverage
            if (isUtilityClass) {
                callPrivateConstructor(clazz);
            }

            return isUtilityClass;
        }

        @Override
        protected void describeMismatchSafely(Class<?> clazz, Description mismatchDescription) {
            if (clazz == null) {
                super.describeMismatch(clazz, mismatchDescription);
            } else {
                mismatchDescription.appendText("The class " + clazz.getCanonicalName() + " is not an utility class.");

                boolean isNonUtilityClass = true;
                try {
                    isNonUtilityClass = !isInstantiable(clazz);
                } catch (ClassNotFoundException e) {
                    mismatchDescription.appendText(" The class is not found. " + e);
                } catch (InstantiationException e) {
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
        public void describeTo(Description description) {
            // Not supported
        }

        private void callPrivateConstructor(Class clazz) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException e) {
                // Swallowed
            }
        }

        private boolean isInstantiable(Class clazz) throws ClassNotFoundException, InstantiationException {
            boolean hasPrivateConstructor = false;
            try {
                clazz.newInstance();
            } catch (IllegalAccessException e) {
                hasPrivateConstructor = true;
            }
            return hasPrivateConstructor;
        }

        private boolean isFinal(Class<?> clazz) {
            return Modifier.isFinal(clazz.getModifiers());
        }
    }
}
