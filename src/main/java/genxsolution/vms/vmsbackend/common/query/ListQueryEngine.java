package genxsolution.vms.vmsbackend.common.query;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ListQueryEngine {

    private static final Set<String> RESERVED_KEYS = Set.of("q", "sortBy", "sortDir");
    private static final Map<Class<?>, Map<String, Method>> ACCESSOR_CACHE = new ConcurrentHashMap<>();

    private ListQueryEngine() {
    }

    public static <T> List<T> apply(List<T> source, Map<String, String> queryParams, Set<String> excludedKeys) {
        if (source == null || source.isEmpty() || queryParams == null || queryParams.isEmpty()) {
            return source;
        }

        Set<String> ignored = excludedKeys == null ? Set.of() : excludedKeys;
        Map<String, String> effective = queryParams.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
                .filter(entry -> !ignored.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));

        if (effective.isEmpty()) {
            return source;
        }

        String searchText = effective.remove("q");
        String sortBy = effective.remove("sortBy");
        String sortDir = effective.remove("sortDir");

        List<T> filtered = source.stream()
                .filter(item -> searchText == null || searchMatches(item, searchText))
                .filter(item -> matchesAll(item, effective))
                .collect(Collectors.toCollection(ArrayList::new));

        if (sortBy != null && !sortBy.isBlank()) {
            Comparator<T> comparator = buildComparator(sortBy, "desc".equalsIgnoreCase(sortDir));
            filtered.sort(comparator);
        }
        return filtered;
    }

    private static <T> boolean searchMatches(T item, String searchText) {
        String q = searchText.toLowerCase(Locale.ROOT);
        Map<String, Method> accessors = accessors(item.getClass());
        for (Method accessor : accessors.values()) {
            Object value = getValue(item, accessor);
            if (value != null && value.toString().toLowerCase(Locale.ROOT).contains(q)) {
                return true;
            }
        }
        return false;
    }

    private static <T> boolean matchesAll(T item, Map<String, String> filters) {
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            if (!matches(item, filter.getKey(), filter.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static <T> boolean matches(T item, String rawKey, String rawValue) {
        String key = rawKey;
        String operator = "eq";
        if (rawKey.endsWith("_like")) {
            operator = "like";
            key = rawKey.substring(0, rawKey.length() - 5);
        } else if (rawKey.endsWith("_from")) {
            operator = "from";
            key = rawKey.substring(0, rawKey.length() - 5);
        } else if (rawKey.endsWith("_to")) {
            operator = "to";
            key = rawKey.substring(0, rawKey.length() - 3);
        }

        Method accessor = findAccessor(item.getClass(), key);
        Object current = getValue(item, accessor);
        if (current == null) {
            return false;
        }

        return switch (operator) {
            case "like" -> current.toString().toLowerCase(Locale.ROOT).contains(rawValue.toLowerCase(Locale.ROOT));
            case "from" -> compare(current, rawValue) >= 0;
            case "to" -> compare(current, rawValue) <= 0;
            default -> equalsValue(current, rawValue);
        };
    }

    private static int compare(Object current, String rawValue) {
        if (!(current instanceof Comparable<?> comparableCurrent)) {
            throw new IllegalArgumentException("Field is not comparable for range filter");
        }
        Object parsed = parseValue(rawValue, current.getClass());
        if (parsed == null || !current.getClass().isAssignableFrom(parsed.getClass())) {
            throw new IllegalArgumentException("Invalid range value: " + rawValue);
        }
        @SuppressWarnings("unchecked")
        Comparable<Object> left = (Comparable<Object>) comparableCurrent;
        return left.compareTo(parsed);
    }

    private static boolean equalsValue(Object current, String rawValue) {
        Object parsed = parseValue(rawValue, current.getClass());
        if (parsed != null) {
            return Objects.equals(current, parsed);
        }
        return current.toString().equalsIgnoreCase(rawValue);
    }

    private static Object parseValue(String rawValue, Class<?> targetType) {
        try {
            if (targetType == String.class) {
                return rawValue;
            }
            if (targetType == UUID.class) {
                return UUID.fromString(rawValue);
            }
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(rawValue);
            }
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(rawValue);
            }
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(rawValue);
            }
            if (targetType == BigDecimal.class) {
                return new BigDecimal(rawValue);
            }
            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(rawValue);
            }
            if (targetType == LocalDate.class) {
                return LocalDate.parse(rawValue);
            }
            if (targetType == Instant.class) {
                return Instant.parse(rawValue);
            }
            if (Enum.class.isAssignableFrom(targetType)) {
                @SuppressWarnings("unchecked")
                Class<? extends Enum> enumType = (Class<? extends Enum>) targetType;
                return Enum.valueOf(enumType, rawValue.toUpperCase(Locale.ROOT));
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private static <T> Comparator<T> buildComparator(String fieldName, boolean desc) {
        return (left, right) -> {
            Object leftValue = getValue(left, findAccessor(left.getClass(), fieldName));
            Object rightValue = getValue(right, findAccessor(right.getClass(), fieldName));

            if (leftValue == null && rightValue == null) {
                return 0;
            }
            if (leftValue == null) {
                return 1;
            }
            if (rightValue == null) {
                return -1;
            }

            int base;
            if (leftValue instanceof String leftString && rightValue instanceof String rightString) {
                base = leftString.compareToIgnoreCase(rightString);
            } else if (leftValue instanceof Comparable<?> comparable) {
                @SuppressWarnings("unchecked")
                Comparable<Object> typed = (Comparable<Object>) comparable;
                base = typed.compareTo(rightValue);
            } else {
                base = leftValue.toString().compareToIgnoreCase(rightValue.toString());
            }
            return desc ? -base : base;
        };
    }

    private static Method findAccessor(Class<?> type, String fieldName) {
        Method accessor = accessors(type).get(fieldName.toLowerCase(Locale.ROOT));
        if (accessor == null) {
            throw new IllegalArgumentException("Unknown filter/sort field: " + fieldName);
        }
        return accessor;
    }

    private static Object getValue(Object target, Method accessor) {
        try {
            return accessor.invoke(target);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to read field " + accessor.getName(), ex);
        }
    }

    private static Map<String, Method> accessors(Class<?> type) {
        return ACCESSOR_CACHE.computeIfAbsent(type, ListQueryEngine::buildAccessorMap);
    }

    private static Map<String, Method> buildAccessorMap(Class<?> type) {
        Map<String, Method> accessorMap = new HashMap<>();
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE || method.getName().equals("getClass")) {
                continue;
            }
            if (method.getName().startsWith("get") && method.getName().length() > 3) {
                accessorMap.put(decapitalize(method.getName().substring(3)).toLowerCase(Locale.ROOT), method);
            } else if (method.getName().startsWith("is") && method.getName().length() > 2) {
                accessorMap.put(decapitalize(method.getName().substring(2)).toLowerCase(Locale.ROOT), method);
                accessorMap.put(method.getName().toLowerCase(Locale.ROOT), method);
            } else {
                accessorMap.put(method.getName().toLowerCase(Locale.ROOT), method);
            }
        }
        return accessorMap;
    }

    private static String decapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }
}






