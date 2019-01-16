/*
 * Copyright (c) 2012, Codename One and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Codename One designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *  
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Codename One through http://www.codenameone.com/ if you 
 * need additional information or have any questions.
 */
package com.codename1.nui;

import com.codename1.ui.PeerComponent;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import com.codename1.ui.Display;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NSelectNativeImpl implements com.codename1.nui.NSelectNative{
    private JComboBox<String> cb;
    private int index;
    
    private class PeerComboBox extends PeerComponent {
        PeerComboBox() {
            super(cb);
        }
    }
    
    

    public com.codename1.ui.PeerComponent createNativeSelect(int param) {
        this.index = param;
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    cb = new JComboBox<>();
                    cb.putClientProperty("cn1-match-style", Boolean.TRUE);
                    cb.addItemListener(new ItemListener() {
                        
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            NSelect.fireSelectionChanged(index);
                        }
                        
                    });
                }
            });
            return com.codename1.impl.javase.JavaSEPort.instance.createNativePeer(cb);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

   

    public void setOptions(String param) {
        try {
            Scanner scanner = new Scanner(param);
            ArrayList<String> opts = new ArrayList<String>();
            while (scanner.hasNextLine()) {
                String next = scanner.nextLine().trim();
                if (next.isEmpty()) {
                    continue;
                }
                opts.add(next);
            }
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    cb.setModel(new DefaultComboBoxModel<String>(opts.toArray(new String[opts.size()])));
                }
            });
        } catch (Throwable  ex) {
           throw new RuntimeException(ex);
        }
    }

    public int getSelectedIndex() {
        return cb.getSelectedIndex();
    }
    
    public void setSelectedIndex(final int index) {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    cb.setSelectedIndex(index);
                }
            });
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public boolean isSupported() {
        return true;
    }

}
