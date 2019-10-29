package com.jxmfkj.mvvmlibrary.base;

import android.app.Application;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application application;
    private final Bundle arguments;

    public ViewModelFactory(Application application, Bundle arguments) {
        this.application = application;
        this.arguments = arguments;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
            try {
                Class<?>[] params = new Class[]{Application.class, Bundle.class};
                return modelClass.getConstructor(params[0], params[1]).newInstance(application, arguments);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        return super.create(modelClass);
    }
}
