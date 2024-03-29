package com.jxmfkj.mvvmlibrary.bus;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


/**
 * About : kelin的WeakBindingAction
 */
public class WeakAction<T> {
    private Action action;
    private Consumer<T> consumer;
    private boolean isLive;
    private Object target;
    private WeakReference reference;

    public WeakAction(Object target, Action action) {
        reference = new WeakReference(target);
        this.action = action;

    }

    public WeakAction(Object target, Consumer<T> consumer) {
        reference = new WeakReference(target);
        this.consumer = consumer;
    }

    public void execute() throws Exception {
        if (action != null && isLive()) {
            action.run();
        }
    }

    public void execute(T parameter) throws Exception {
        if (consumer != null
                && isLive()) {
            consumer.accept(parameter);
        }
    }

    public void markForDeletion() {
        reference.clear();
        reference = null;
        action = null;
        consumer = null;
    }

    public Action getAction() {
        return action;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public boolean isLive() {
        if (reference == null) {
            return false;
        }
        if (reference.get() == null) {
            return false;
        }
        return true;
    }


    public Object getTarget() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
}
