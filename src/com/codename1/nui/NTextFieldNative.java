/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.nui;

import com.codename1.system.NativeInterface;
import com.codename1.ui.PeerComponent;

/**
 *
 * @author shannah
 */
public interface NTextFieldNative extends NativeInterface {
    public PeerComponent createNativeTextField(int index, int constraint);
    public void setText(String text);
    public String getText();
    public void focus();
    public void blur();
    public void updateStyle();
    public int getKeyboardHeight();

    public void startEditingAsync();

    public boolean isEditing();

    public void stopEditing();
}
