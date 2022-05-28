/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.helpers;

import de.tudo.naantg.model.SpringBootKey;
import de.tudo.naantg.model.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * Scanner for scanning the project to find classes, methods etc.
 */
public class Scanner {

    /**
     * the context of the logger information
     */
    private static final String LOG_INFO = "[Scanner] ";

    /**
     * Collects all classes in the package with the given package uri.
     * @param packageUri the uri of the package to search after classes
     * @return list of classes in the package
     * @throws ClassNotFoundException if a found class name can not be located during class loading
     * @throws IOException if I/O Exception occurs during resources loading
     */
    public static ArrayList<Class<?>> getClasses(String packageUri) throws  ClassNotFoundException, IOException {
        if (packageUri == null) return new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String message = "";
        String path = packageUri.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        message += "Size of Files: " + dirs.size() + ", ";
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageUri));
        }
        message += "Size of Classes: " + classes.size();
        //Logger.logInfo(LOG_INFO + message);
        return classes;
    }

    /**
     * Collects all classes in the directory located in the package with the given package uri.
     * @param directory a directory in the package
     * @param packageUri the uri of the package
     * @return list of all classes in the directory
     * @throws ClassNotFoundException if a found class name can not be located during class loading
     */
    private static ArrayList<Class<?>> findClasses(File directory, String packageUri) throws  ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        String pck = packageUri.replace("/", ".");
        if (!directory.exists()) {
            Logger.logWarning(LOG_INFO + "The directory " + directory + " does not exist!");
            return classes;
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, pck + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = pck + '.' + file.getName().substring(0,file.getName().length()-6);
                classes.add(Class.forName(className));
            }
        }
        return  classes;
    }

    /**
     * Collects all test generation methods that are annotated with @TG in the given class.
     * @param clas a test generation interface with test generation methods
     * @return list of all annotated test generation methods
     */
    public static ArrayList<Method> findMethodsWithTG(Class<?> clas) {
        ArrayList<Method> methods = new ArrayList<>();
        if (clas == null) return methods;
        for (Method m : clas.getMethods()) {
            Annotation[] annos = m.getDeclaredAnnotations();
            for (Annotation a : annos) {
                if (a.toString().contains("@de.tudo.naantg.annotations.TG")) {
                    methods.add(m);
                }
            }
        }
        return methods;
    }

    /**
     * Checks if the given method has an @AssertState annotation.
     * @param method a test generation method
     * @return true if the given method has an @AssertState annotation, otherwise false
     */
    public static boolean hasAssertStateAnnotation(Method method) {
        return hasAnnotation(method, "AssertState");
    }

    /**
     * Checks if the given method has an @AssertState annotation.
     * @param method a test generation method
     * @return true if the given method has an @AssertState annotation, otherwise false
     */
    public static boolean hasParamsAnnotation(Method method) {
        return hasAnnotation(method, "Params");
    }

    /**
     * Checks if the given method has an @InitState annotation.
     * @param method a test generation method
     * @return true if the given method has an @InitState annotation, otherwise false
     */
    public static boolean hasInitStateAnnotation(Method method) {
        return hasAnnotation(method, "InitState");
    }

    /**
     * Checks if the given method has an annotation with the given name.
     * @param method a test generation method
     * @param annotationName the name of the annotation class
     * @return true if the given method has the given annotation, otherwise false
     */
    public static boolean hasAnnotation(Method method, String annotationName) {
        String path = "de.tudo.naantg.annotations";
        return hasAnnotation(method, annotationName, path);
    }

    /**
     * Checks if the given method has an annotation with the given name.
     * @param method a test generation method
     * @param annotationName the name of the annotation class
     * @param path the path of the annotation
     * @return true if the given method has the given annotation, otherwise false
     */
    public static boolean hasAnnotation(Method method, String annotationName, String path) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.toString().contains(path + "." + annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given field has an annotation with the given name.
     * @param field a field
     * @param annotationName the name of the annotation class
     * @return true if the given field has the given annotation, otherwise false
     */
    public static boolean hasFieldAnnotation(Field field, String annotationName) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.toString().contains(annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given parameter has an annotation with the given name.
     * @param parameter a parameter
     * @param annotationName the name of the annotation class
     * @return true if the given parameter has the given annotation, otherwise false
     */
    public static boolean hasParameterAnnotation(Parameter parameter, String annotationName) {
        Annotation[] annotations = parameter.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.toString().contains(annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given class has an annotation with the given name.
     * @param clazz a test generation class
     * @param annotationName the name of the annotation class
     * @return true if the given method has an @AssertState annotation, otherwise false
     */
    public static boolean hasClassAnnotation(Class<?> clazz, String annotationName) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.toString().contains(annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches for the annotation of the method with the given name and returns it as Optional.
     * @param method a method to test
     * @param annotationName an annotation name
     * @return an Optional annotation
     */
    public static Optional<Annotation> findAnnotationClass(Method method, String annotationName) {
        Optional<Annotation> annoClass = Optional.empty();
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.toString().contains(annotationName)) {
                annoClass = Optional.of(annotation);
            }
        }
        return annoClass;
    }

    /**
     * Searches for a method with the given method name in the given class.
     * @param clas the class with the searched method
     * @param methodName the name of the searched method
     * @return the Optional searched method
     */
    public static Optional<Method> findMethodOfClass(Class<?> clas, String methodName) {
        return findMethodOfClass(clas, -1, methodName);
    }

    /**
     * Searches for a method with the given method name in the given class.
     * @param clas the class with the searched method
     * @param paramCount the number of params
     * @param methodName the name of the searched method
     * @return the Optional searched method
     */
    public static Optional<Method> findMethodOfClass(Class<?> clas, int paramCount, String methodName) {
        Optional<Method> optMethod = Optional.empty();
        if (clas != null && !methodName.equals("")) {
            Method[] methods = clas.getMethods();
            for (Method m : methods) {
                String name = m.getName();
                String modifier = Modifier.toString(m.getModifiers());
                if (name.equals(methodName) && modifier.startsWith("public") &&
                        (paramCount == -1 || paramCount == m.getParameterCount()) ) {
                    optMethod = Optional.of(m);
                }
            }
        }
        return optMethod;
    }

    /**
     * Searches for the class with the given class name in the package with the given package uri.
     * @param className the name of the searched class
     * @param packageUri the uri of the package with the searched class
     * @return the Optional searched class
     */
    public static Optional<Class<?>> findCorrespondingClass(String className, String packageUri) {
        Optional<Class<?>> optClass = Optional.empty();
        try {
            List<Class<?>> classes = getClasses(packageUri);
            if (classes.isEmpty()) {
                Logger.logWarning(LOG_INFO + "No classes found in " + packageUri + "!");
                return optClass;
            }
            for (Class<?> c : classes) {
                if (c.getSimpleName().equals(className)) {
                    return Optional.of(c);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return optClass;
    }

    /**
     * Searches for an entity class with the given class name in the package with the given package uri.
     * @param className the name of the searched entity class
     * @param packageUri the uri of the package with the searched class
     * @return the Optional searched entity class
     */
    public static Optional<Class<?>> findEntityClass(String className, String packageUri) {
        Optional<Class<?>> optClass = findCorrespondingClass(className, packageUri);
        if (optClass.isPresent() &&
                (hasClassAnnotation(optClass.get(), SpringBootKey.ENTITY.getKeyword()) ||
                hasClassAnnotation(optClass.get(), SpringBootKey.SUPERCLASS.getKeyword()))) {
            return optClass;
        }
        else return Optional.empty();
    }

    /**
     * Finds the first corresponding field with matching field name in the given project package.
     * Searches only in entity or mapped super classes.
     * @param fieldName the field to search
     * @param packageUri the project path
     * @return the optional field class
     */
    public static Optional<Class<?>> findCorrespondingField(String fieldName, String packageUri) {
        Optional<Class<?>> optClass = Optional.empty();
        try {
            List<Class<?>> classes = getClasses(packageUri);
            if (classes.isEmpty()) {
                Logger.logWarning(LOG_INFO + "No classes found in " + packageUri + "!");
                return optClass;
            }
            for (Class<?> c : classes) {
                if (!hasClassAnnotation(c, SpringBootKey.ENTITY.getKeyword()) &&
                        !hasClassAnnotation(c, SpringBootKey.SUPERCLASS.getKeyword())) continue;

                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        return Optional.of(field.getType());
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return optClass;
    }

    /**
     * Searches for the method with the given method name in the given class.
     * @param clas the class of the searched method
     * @param methodName the name of the searched method
     * @return the Optional searched method
     */
    public static Optional<Method> findCorrespondingMethod(Class<?> clas, String methodName) {
        Optional<Method> optMethod = Optional.empty();
        if (clas == null) return optMethod;
        Method[] methods = clas.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return Optional.of(method);
            }
        }
        return optMethod;
    }

    /**
     * Searches for parameters with generics and returns a map with all of them and their generics.
     * @param method method to test
     * @return a map with all parameters with generics
     */
    public static Map<String, List<Class<?>>> findGenericOfMethodParameters(Method method) {
        return getGenerics(method.getGenericParameterTypes());
    }

    /**
     * Searches for generics of the given constructor and returns a map with all of them and their generics.
     * @param constructor a constructor
     * @return a map with all constructor generics
     */
    public static Map<String, List<Class<?>>> findGenericOfConstructorParameters(Constructor<?> constructor) {
        return getGenerics(constructor.getGenericParameterTypes());
    }

    /**
     * Selects all generics of the given types and returns them in a map.
     * One generic can exists multiple times, so the key of the map is the generic type name
     * and the value is a list of classes of this type.
     * @param types an array of types
     * @return the generics of the types
     */
    private static Map<String, List<Class<?>>> getGenerics(Type[] types) {
        Map<String, List<Class<?>>> genericParameters = new HashMap<>();
        List<Class<?>> generics = new ArrayList<>();
        for (Type type : types) {
            String name = type.getTypeName().replaceAll("<.*?>", "");
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type[] args = pType.getActualTypeArguments();
                for (Type arg : args) {
                    Class<?> argClass = (Class<?>) arg;
                    generics.add(argClass);
                }
            }
            genericParameters.put(name, generics);
        }
        return genericParameters;
    }

    /**
     * Searches for the generics in the method return type and returns it.
     * @param method the method to test
     * @return the generics of the return type
     */
    public static List<Class<?>> findGenericOfMethodReturnType(Method method) {
        List<Class<?>> generics = new ArrayList<>();
        Type returnType = method.getGenericReturnType();

        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                if (typeArgument.getTypeName().equals("T")) continue;
                generics.add((Class<?>) typeArgument);
            }
        }

        return generics;
    }

    /**
     * Searches for the generics in the given field and returns it.
     * @param field a field of the cut
     * @return the generics of the field
     */
    public static List<Class<?>> findGenericOfField(Field field) {
        Type genericFieldType = field.getGenericType();
        List<Class<?>> generics = new ArrayList<>();

        if(genericFieldType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for(Type fieldArgType : fieldArgTypes){
                Class<?> fieldArgClass = (Class<?>) fieldArgType;
                generics.add(fieldArgClass);
            }
        }
        return generics;
    }

    /**
     * Returns an optional default constructor (constructor without parameters).
     * @param parameter a parameter
     * @return an optional constructor without parameters
     */
    public static Optional<Constructor<?>> getDefaultConstructor(Class<?> parameter) {
        if (parameter == null) return Optional.empty();
        Constructor<?>[] constructors = parameter.getConstructors();
        if (constructors.length != 0) {
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return Optional.of(constructor);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the simplest constructor of the given parameter.
     * A constructor is simple if its parameter count is small
     * and its primitive parameter count is high.
     * @param parameter a parameter
     * @return the simplest constructor
     */
    public static Optional<Constructor<?>> getSimplestConstructor(Class<?> parameter) {
        if (parameter == null) return Optional.empty();
        Constructor<?>[] constructors = parameter.getConstructors();
        if (constructors.length != 0) {
            PriorityQueue<ConstructorHeapElement> queue = new PriorityQueue<>(new ConstructorComparator());
            for (Constructor<?> constructor : constructors) {
                queue.offer(new ConstructorHeapElement(constructor));
            }
            ConstructorHeapElement first = queue.poll();
            if (first != null) return Optional.of(first.getConstructor());
        }
        return Optional.empty();
    }

    /**
     * Returns the size of primitive classes within the given classes.
     * @param parameters a list of classes
     * @return the size of primitive classes
     */
    public static int getPrimitiveParamCount(Class<?>[] parameters) {
        int count = 0;
        for (Class<?> parameter : parameters) {
            if (Utils.isSimpleType(parameter.getSimpleName())) count++;
        }
        return count;
    }

    /**
     * Checks if the given class has a setter for the given field.
     * @param parentClass the class to check for a setter
     * @param field a field
     * @return true if the class has a setter for the field
     */
    public static boolean hasSetterMethod(Class<?> parentClass, Field field) {
        String name = getSetterMethod(parentClass, field);
        return !name.equals("");
    }

    /**
     * Returns the setter method name of the parent class for the field if it exists.
     * @param parentClass the parent class
     * @param field a field
     * @return the setter method name of the parent class for the field if it exists,
     * otherwise an empty String.
     */
    public static String getSetterMethod(Class<?> parentClass, Field field) {
        String name = "set" + Utils.setUpperCaseFirstChar(field.getName());
        Optional<Method> optionalMethod = findMethodOfClass(parentClass, name);
        if (optionalMethod.isPresent()) return name;
        while (parentClass.getSuperclass() != null && !optionalMethod.isPresent()) {
            parentClass = parentClass.getSuperclass();
            optionalMethod = findMethodOfClass(parentClass, name);
        }
        return optionalMethod.isPresent() ? name : "";
    }

    /**
     * Checks if the given class has a setter for the given field name.
     * @param parentClass the class to check for a setter
     * @param fieldName the name of a field
     * @return true if the class has a setter for the field
     */
    public static boolean hasSetterMethod(Class<?> parentClass, String fieldName) {
        if (parentClass == null) return false;

        for (Field field : findAllFields(parentClass)) {
            if (field.getName().equals(fieldName) &&
                    hasSetterMethod(parentClass, field)) return true;
        }
        return false;
    }

    /**
     * Checks if the given class has a getter for the given field.
     * @param parentClass the class to check for a getter
     * @param field a field
     * @return true if the class has a getter for the field
     */
    public static boolean hasGetterMethod(Class<?> parentClass, Field field) {
        String name = getGetterMethod(parentClass, field);
        return !name.equals("");
    }

    /**
     * Returns the getter method name of the parent class for the field if it exists.
     * @param parentClass the parent class
     * @param field a field
     * @return the getter method name of the parent class for the field if it exists,
     * otherwise an empty String.
     */
    public static String getGetterMethod(Class<?> parentClass, Field field) {
        String name = "get" + Utils.setUpperCaseFirstChar(field.getName());
        Optional<Method> optionalMethod = findMethodOfClass(parentClass, name);
        if (optionalMethod.isPresent()) return name;
        while (parentClass.getSuperclass() != null && !optionalMethod.isPresent()) {
            parentClass = parentClass.getSuperclass();
            optionalMethod = findMethodOfClass(parentClass, name);
        }
        return optionalMethod.isPresent() ? name : "";
    }

    /**
     * Checks if the given class has a getter for the given field name.
     * @param parentClass the class to check for a getter
     * @param fieldName the name of a field
     * @return true if the class has a getter for the field
     */
    public static boolean hasGetterMethod(Class<?> parentClass, String fieldName) {
        if (parentClass == null) return false;

        for (Field field : findAllFields(parentClass)) {
            if (field.getName().equals(fieldName) &&
                    hasGetterMethod(parentClass, field)) return true;
        }
        return false;
    }

    /**
     * Finds the constructor for the given dataType with the given constructor parameters.
     * @param constructorParams the constructor parameters
     * @param dataType an object data type
     * @return an optional constructor for the given data type
     */
    public static Optional<Constructor<?>> findConstructor(List<String> constructorParams, Class<?> dataType) {
        Optional<Constructor<?>> optionalConstructor = Optional.empty();
        Constructor<?>[] constructors = dataType.getConstructors();
        if (constructors.length == 0) return optionalConstructor;
        int paramSize = constructorParams.size();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == paramSize) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                int count = paramSize;
                for (int i = 0; i < paramSize; i++) {
                    if (constructorParams.get(i).equals(paramTypes[i].getSimpleName())) {
                        count--;
                    }
                }
                if (count == 0) {
                    return Optional.of(constructor);
                }
            }
        }
        return optionalConstructor;
    }

    /**
     * Finds a assignable class for the given interface in the project with the given uri.
     * @param objectClass an interface
     * @param projectPackageUriWithDots the uri of the project
     * @return an optional assignable class
     */
    public static Optional<Class<?>> findAssignableClass(Class<?> objectClass, String projectPackageUriWithDots) {
        Optional<Class<?>> optionalClass = Optional.empty();
        List<Class<?>> allClasses;
        try {
            allClasses = getClasses(projectPackageUriWithDots);
            for (Class<?> c : allClasses) {
                if (objectClass.isAssignableFrom(c)) {
                    return Optional.of(c);
                }
            }
        } catch (Exception e) {
            return optionalClass;
        }
        return optionalClass;
    }

    public static List<Field> findAllFields(Class<?> child) {
        if (child == null) return new ArrayList<>();

        List<Field> allFields = new ArrayList<>(Arrays.asList(child.getDeclaredFields()));
        while (child.getSuperclass() != null) {
            child = child.getSuperclass();
            allFields.addAll(Arrays.asList(child.getDeclaredFields()));
        }
        return allFields;
    }

}
