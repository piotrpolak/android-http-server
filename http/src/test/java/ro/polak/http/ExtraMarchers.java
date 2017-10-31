package ro.polak.http;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ExtraMarchers {

    private static final UtilClassMatcher utilClassMatcher = new UtilClassMatcher();

    public static Matcher<? super Class<?>> utilityClass() {
        return utilClassMatcher;
    }

    private static class UtilClassMatcher implements Matcher<Class<?>> {
        @Override
        public boolean matches(Object item) {
            boolean isUtilityClass = false;
            try {
                isUtilityClass = isUtilityClass((Class) item);
            } catch (ClassNotFoundException | InstantiationException e) {
                // Swallowed
            }

            // This code will attempt to call empty constructor to generate code coverage
            if (isUtilityClass) {
                supportCodeCoverage((Class) item);
            }

            return isUtilityClass;
        }

        @Override
        public void describeMismatch(Object item, Description mismatchDescription) {
            Class clazz = ((Class) item);

            mismatchDescription.appendText("The class " + clazz.getCanonicalName() + " is not an utility class.");

            boolean isNonUtilityClass = true;
            try {
                isNonUtilityClass = !isUtilityClass((Class) item);
            } catch (ClassNotFoundException e) {
                mismatchDescription.appendText(" The class is not found. " + e);
            } catch (InstantiationException e) {
                mismatchDescription.appendText(" The class can not be instantiated. " + e);
            }

            if (isNonUtilityClass) {
                mismatchDescription.appendText(" The class should not be instantiable.");
            }
        }

        @Override
        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

        }

        @Override
        public void describeTo(Description description) {

        }

        private void supportCodeCoverage(Class clazz) {
            try {
                Class cls = Class.forName(clazz.getCanonicalName());
                Constructor<?> constructor = cls.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                    InstantiationException | InvocationTargetException e) {
                // Swallowed
            }
        }

        private boolean isUtilityClass(Class item) throws ClassNotFoundException, InstantiationException {
            boolean hasPrivateConstructor = false;
            try {
                Class cls = Class.forName(item.getCanonicalName());
                cls.newInstance();
            } catch (IllegalAccessException e) {
                hasPrivateConstructor = true;
            }
            return hasPrivateConstructor;
        }
    }
}
