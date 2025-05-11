package org.dnttr.zephyr.event;

import java.lang.reflect.Method;

record EventMethod(Method method, Object clazz) {
}