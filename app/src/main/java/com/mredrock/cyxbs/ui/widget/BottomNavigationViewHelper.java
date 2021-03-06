package com.mredrock.cyxbs.ui.widget;

import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by simonla on 2017/9/13.
 */

public class BottomNavigationViewHelper {

    private BottomNavigationView mMainBottomNavView;
    @Nullable
    private BottomNavigationMenuView mMenuView;

    public BottomNavigationViewHelper(BottomNavigationView mainBottomNavView) {
        mMainBottomNavView = mainBottomNavView;
        mMenuView = getMenuView();
    }

    private BottomNavigationMenuView getMenuView() {
        Field field;
        try {
            field = mMainBottomNavView.getClass().getDeclaredField("mMenuView");
            field.setAccessible(true);
            return (BottomNavigationMenuView) field.get(mMainBottomNavView);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void enableBottomNavAnim(boolean enable) {
        mMainBottomNavView.post(() -> enableBtNavAnimInternal(enable));
    }

    private void enableBtNavAnimInternal(boolean enable) {
        if (mMenuView == null) throw new RuntimeException("get mMenuView by reflect failed");
        if (!enable) {
            try {
                Field field1 = mMenuView.getClass().getDeclaredField("mShiftingMode");
                field1.setAccessible(true);
                field1.setBoolean(mMenuView, false);
                updateMenuView();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMenuView() {
        if (mMenuView == null) throw new RuntimeException("get mMenuView by reflect failed");
        try {
            Class<?> clazz = mMenuView.getClass();
            Method method = clazz.getDeclaredMethod("updateMenuView");
            method.invoke(mMenuView);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void setBottomNavTextSize(int size) {
        mMainBottomNavView.post(() -> setBtNavTextSizeInternal(size));
    }

    private void setBtNavTextSizeInternal(int size) {
        if (mMenuView == null) throw new RuntimeException("get mMenuView by reflect failed");
        try {
            Field field = mMenuView.getClass().getDeclaredField("mButtons");
            field.setAccessible(true);
            BottomNavigationItemView[] buttons = (BottomNavigationItemView[]) field.get(mMenuView);
            for (BottomNavigationItemView view : buttons) {
                Class<?> clazz = view.getClass();
                Field largeLabelField = clazz.getDeclaredField("mLargeLabel");
                largeLabelField.setAccessible(true);
                TextView largeLabel = (TextView) largeLabelField.get(view);
                largeLabel.setTextSize(size);
                updateMenuView();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
