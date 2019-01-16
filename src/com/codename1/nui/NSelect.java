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

import com.codename1.system.NativeLookup;
import com.codename1.ui.CN;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.plaf.Style;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A heavyweight select widget.  On supported platforms (currently Javascript and JavaSE), this will use the platform's
 * native select widget.  On JavaSE this is a JComboBox.  On Javascript this is an HTML select widget.  This is 
 * primarily directed at the Javascript port since the Picker and ComboBox have 
 * some <a href="https://github.com/codenameone/CodenameOne/issues/2529">issues</a>.
 * @author shannah
 */
public class NSelect<T> extends Container {
    
    /**
     * Keep a running count of all NSelects made.  They will be stored in the {@link #lookups} map.
     * This is to allow callbacks from the native side using indices.
     */
    private static int nextIndex;
    
    /**
     * Index of all currently active NSelect widgets.  Widgets are added to this index
     * when they are initialzed and removed when they are deinitialized.
     * A trade-off of this approach is that callbacks from native (using this lookup mechanism)
     * will only work when the component is initialized.  This is acceptable currently
     * since it is only used for selection events which should only fire when the 
     * widget is part of the UI.
     */
    private static Map<Integer,NSelect> lookups = new HashMap<>();
    
    /**
     * The index used as a key in the {@link #lookups} table.
     */
    private int index;
    
    /**
     * Native peer
     */
    private NSelectNative peer;
    
    /**
     * Internal peer component.  On supported platforms, this will be a PeerComponent.  Otherwise
     * it will be a ComboBox
     */
    private Component peerComp;
    
    /**
     * Selection listeners to be notified when the selected value changes.
     */
    private List<SelectionListener> listeners = new ArrayList<>();
    
    /**
     * Options for this select list.
     */
    private List<T> options = new ArrayList<T>();
    
    /**
     * The styles that are bound between the NSelect itself (which is a Container)
     * and the peer component (which may be a PeerComponent or a ComboBox). 
     * Styles in this list will be propagated to the peer component when they are changed
     * int he NSelect itself.
     */
    private static final String[] BOUND_STYLES = new String[]{
        Style.FG_COLOR,
        Style.BG_COLOR,
        Style.FONT
    };
    
    /**
     * Flag to force to only use the lightweight peer component (i.e. the ComboBox).
     * If you set this flag, then all NSelects created with {@literal new NSelect()} will
     * use a lightweight internal widget.
     */
    private static boolean useLightWeightWidget;
    
    /**
     * Sets the NSelect class to only use lightweight internal widgets even if 
     * the platform supports native widgets.  Mostly for debugging purposes.
     * @param lw True to set to only use lightweight widgets.  False to allow supported platforms to use native widgets.
     */
    public static void setUseLightWeightWidget(boolean lw) {
        useLightWeightWidget = lw;
    }
    
    /**
     * Flag to indicate if this NSelect is using a lightweight widget.  If this is true
     * then the {@link #peerComp} can assumed to be a ComboBox.  If false, then it is a 
     * PeerComponent.
     */
    private boolean isLightweight;
    
    /**
     * Creates a new NSelect.
     */
    public NSelect() {
        super(new BorderLayout(), "NSelect");
        index = nextIndex++;
        peer = NativeLookup.create(NSelectNative.class);
        if (peer.isSupported() && !useLightWeightWidget) {
            peerComp = peer.createNativeSelect(index);
        } else {
            isLightweight = true;
            peerComp = new ComboBox<T>();
            ((ComboBox)peerComp).addSelectionListener(new SelectionListener() {
                @Override
                public void selectionChanged(int oldSelected, int newSelected) {
                    NSelect.this.fireSelectionChanged();
                }
                
            });
            ((ComboBox)peerComp).addActionListener(e->NSelect.this.fireSelectionChanged());
        }
        add(BorderLayout.CENTER, peerComp);
        //$(this).selectAllStyles().setPadding(0).setMargin(0);
        updateStyles();
        
    }
    

    /**
     * Propagates bound styles from the NSelect to the peer component.
     */
    private void updateStyles() {
        for (String style : BOUND_STYLES) {
            peerComp.styleChanged(style, getStyle());
        }
    }

    /**
     * This is overridden to propagate style changes from the NSelect
     * to its internal peer component.  Only {@link Style#FG_COLOR}, {@link Style#BG_COLOR}, and {@link Style#FONT}
     * will have any effect.
     * @param propertyName
     * @param source 
     */
    @Override
    public void styleChanged(String propertyName, Style source) {
        super.styleChanged(propertyName, source);
        if (source == getStyle()) {
            peerComp.styleChanged(propertyName, source);
        }
    }

    
    
    @Override
    public void setWidth(int width) {
        super.setWidth(width); 
        if (peerComp != null) {
            // Hack to work around weirdness in JS port where layout isn't run
            // on the first page.  We really just want our peer to match the size
            // of the parent so this is no brainer.
            peerComp.setWidth(width);
        }
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height); 
        if (peerComp != null) {
            // Hack to work around weirdness in JS port where layout isn't run
            // on the first page.  We really just want our peer to match the size
            // of the parent so this is no brainer.
            peerComp.setHeight(height);
        }
    }

    
    
    
    /**
     * Sets the options in this select.
     * @param options 
     */
    public void setOptions(T... options) {
        this.options.clear();
        this.options.addAll(Arrays.asList(options));
        
        if (!isLightweight) {
            StringBuilder sb = new StringBuilder();
            int len = options.length;
            for (int i=0; i<len; i++) {
                sb.append(options[i].toString()).append("\n");
            }
            peer.setOptions(sb.toString().trim());
        } else {
            if (peerComp instanceof ComboBox) {
                ((ComboBox)peerComp).setModel(new DefaultListModel<T>(options));
            } else {
                throw new RuntimeException("Unsupported type for peer component");
            }
        }
        peerComp.setShouldCalcPreferredSize(true);
        revalidate();
    }
    
    /**
     * Gets the selected index.
     * @return 
     */
    public int getSelectedIndex() {
        if (!isLightweight) {
            return peer.getSelectedIndex();
        } else {
            if (peerComp instanceof ComboBox) {
                return ((ComboBox)peerComp).getSelectedIndex();
            } else {
                throw new RuntimeException("Unsupported type for peer component");
            }
        }
    }
    
    /**
     * Sets the selected index.
     * @param index 
     */
    public void setSelectedIndex(int index) {
        if (!isLightweight) {
            peer.setSelectedIndex(index);
        } else {
            if (peerComp instanceof ComboBox) {
                ((ComboBox)peerComp).setSelectedIndex(index);
            } else {
                throw new RuntimeException("Unsupported type for peer component");
            }
        }
    }
    
    
    /**
     * Gets the selected item.
     * @return 
     */
    public T getSelectedItem() {
        int index = getSelectedIndex();
        if (index < 0 ) {
            return null;
        }
        return getOption(index);
    }
    
    /**
     * Gets the option at the given index.
     * @param index
     * @return 
     */
    public T getOption(int index) {
        return options.get(index);
    }
    
    /**
     * Gets the number of options.
     * @return 
     */
    public int getOptionCount() {
        return options.size();
    }
    
    /**
     * Adds a selection listener to be notified when the selected value changes.
     * @param l 
     */
    public void addSelectionListener(SelectionListener l) {
        listeners.add(l);
    }
    
    /**
     * Removes a selection listener.
     * @param l 
     */
    public void removeSelectionListener(SelectionListener l) {
        listeners.remove(l);
    }
    
    /**
     * For internal use only for native callbacks.
     * @param index 
     * @deprecated For internal use only.
     */
    public static void fireSelectionChanged(int index) {
        if (!CN.isEdt()) {
            CN.callSerially(()->fireSelectionChanged(index));
            return;
        }
        NSelect sel = lookups.get(index);
        if (sel != null) {
            sel.fireSelectionChanged();
        }
        
        
    }
    
    private void fireSelectionChanged() {
        ArrayList<SelectionListener> queue = new ArrayList<>(listeners);
        for (SelectionListener l : queue) {
            l.selectionChanged(-1, -1);
        }
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        lookups.put(index, this);
    }

    @Override
    protected void deinitialize() {
        lookups.remove(index);
        super.deinitialize();
    }
    
    
    
    
    
}
