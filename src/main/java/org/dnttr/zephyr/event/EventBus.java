package org.dnttr.zephyr.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Scheduled for removal. This class is a temporary solution for the event bus.
 * It will be replaced with a more robust implementation in the future.
 */

public class EventBus {

    private final List<EventMethod> subscribers = new ArrayList<>();

    public void register(Object clazz) {
        Class<?> cl = clazz.getClass();
        Arrays.stream(cl.getDeclaredMethods()).filter(declaredMethod -> declaredMethod.isAnnotationPresent(EventSubscriber.class))
                .filter(declaredMethod -> declaredMethod.getParameterCount() > 0)
                .map(declaredMethod -> new EventMethod(declaredMethod, clazz))
                .forEach(subscribers::add);
    }

    public void unregister(Object clazz) {
        this.subscribers.removeAll(this.subscribers.stream().filter(eventMethod -> eventMethod.clazz().equals(clazz)).toList());
    }

    public void call(Event event) {
        this.subscribers.forEach(eventMethod -> {
            try {
                Method method = eventMethod.method();
                method.setAccessible(true);
                if (event.getClass().isAssignableFrom(method.getParameterTypes()[0])) {
                    method.invoke(eventMethod.clazz(), event);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}