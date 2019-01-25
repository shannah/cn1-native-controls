/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.nui;

import com.codename1.charts.util.ColorUtil;
import com.codename1.io.Log;
import com.codename1.system.NativeLookup;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.callSerially;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Editable;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.VirtualInputDevice;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.util.UITimer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class NTextField extends Container {
    public static final int CODE_DONE=1;
    public static final int CODE_NEXT=2;
    
    private static int nextId;
    private VirtualInputDevice currentInput;
    private Component peerComp;
    private NTextFieldNative peer;
    private boolean isLightWeight;
    private int id;
    private EventDispatcher actionListeners = new EventDispatcher();
    private EventDispatcher changeListeners = new EventDispatcher();
    private EventDispatcher doneListeners = new EventDispatcher();
    private int constraint;
    private static Map<Integer,NTextField> activeMap = new HashMap<>();
    //private KeyboardListener keyboardListener = new KeyboardListener();
    
    public NTextField(int constraint) {
        super(new BorderLayout(), "TextField");
        id = nextId++;
        activeMap.put(id, this);
        this.constraint = constraint;
        peer = NativeLookup.create(NTextFieldNative.class);
        
        isLightWeight = !peer.isSupported();
        if (isLightWeight) {
            peerComp = new TextField();
            ((TextField)peerComp).setConstraint(constraint);
            ((TextField)peerComp).addActionListener(e->{
                fireActionEvent();
            });
            ((TextField)peerComp).addDataChangedListener(new DataChangedListener() {
                @Override
                public void dataChanged(int type, int index) {
                    fireChangeEvent();
                }
            });
            ((TextField)peerComp).setDoneListener(e->{
                doneListeners.fireActionEvent(e);
            });
        } else {
            peerComp = peer.createNativeTextField(id, constraint);
            peerComp.setEditingDelegate(new NTextFieldEditingDelegate());
            peerComp.setPreferredTabIndex(0);
        }
        $(peerComp).selectAllStyles()
                .setPadding(0)
                .setMargin(0)
                .setBgTransparency(255)
                .setBgColor(getStyle().getBgColor())
                .setFgColor(getStyle().getFgColor())
                .setAlignment(getStyle().getAlignment())
                .setBorder(Border.createEmpty());
        
        // Let's put this in the active map before initialization so that callbacks
        // will work
        
                
        peer.updateStyle();
        add(BorderLayout.CENTER, peerComp);
        
    }

    @Override
    public void styleChanged(String propertyName, Style source) {
        super.styleChanged(propertyName, source);
        if (!isLightWeight && peerComp != null && source == getUnselectedStyle()) {
            peer.updateStyle();
        } else if (isLightWeight && peerComp != null && source == getUnselectedStyle()) {
            $(peerComp).selectAllStyles()
                .setPadding(0)
                .setMargin(0)
                .setBgTransparency(255)
                .setBgColor(getBgColor(id))
                .setFgColor(getFgColor(id))
                .setAlignment(source.getAlignment())
                .setFont(source.getFont())
                .setBorder(Border.createEmpty());
        }
    }
    
    private static Component defaultField;
    
    private static Component defaultField() {
        if (defaultField == null) {
            defaultField = new Label("Test", "TextField");
            
            
        }
        return defaultField;
    }
    
    public static int getFgColor(int index) {
        Component fld = activeMap.get(index);
        if (fld == null) {
            fld = defaultField();
        }
        return fld.getStyle().getFgColor();
    }
    
    public static int getBgColor(int index) {
        Component fld = activeMap.get(index);
        if (fld == null) {
            fld = defaultField();
        }
        int out = fld.getStyle().getBgColor();
        if (true) return out;
        double a = fld.getStyle().getBgTransparency()/255.0;
        if (a < 0.001) {
            return 0xffffff;
        }
        int r = (int)(ColorUtil.red(out) * a);
        int g = (int)(ColorUtil.green(out) * a);
        int b = (int)(ColorUtil.blue(out) * a);
        return ColorUtil.rgb(r, g, b);
        
        
    }
    
    public static NTextField getInstance(int index) {
        return activeMap.get(index);
        
    }
    
    public static Object getNativeFont(int index) {
        Component fld = activeMap.get(index);
        if (fld == null) {
            fld = defaultField();
        }
        Font f = fld.getStyle().getFont();
        System.out.println(index+"Font "+f);
        if (f == null) {
            System.out.println(index+"Using default font");
            f = Font.getDefaultFont();
        }
        return f.getNativeFont();
        
        
    }
    
    public static int getTextAlign(int index) {
        Component fld = activeMap.get(index);
        if (fld == null) {
            fld = defaultField();
        }
        return fld.getStyle().getAlignment();
    }
    
    
    
    private TextField lpeer() {
        return (TextField)peerComp;
    }
    
    public Component getInternal() {
        return internal();
    }
    
    private Component internal() {
        return peerComp;
    }
    
    public void setText(String text) {
        if (isLightWeight) {
            lpeer().setText(text);
        } else {
            peer.setText(text);
        }
    }
    
    public String getText() {
        if (isLightWeight) {
            return lpeer().getText();
        } else {
            return peer.getText();
        }
    }

    public void addActionListener(ActionListener l) {
        actionListeners.addListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        actionListeners.removeListener(l);
    }
    
    public void addDoneListener(ActionListener l) {
        actionListeners.addListener(l);
    }
    
    public void removeDoneListener(ActionListener l) {
        actionListeners.removeListener(l);
    }
    
    public void addChangeListener(ActionListener l) {
        actionListeners.addListener(l);
    }
    
    public void removeChangeListener(ActionListener l) {
        actionListeners.removeListener(l);
    }

    


   
    public static void requestFocus(int index) {
        NTextField fld = activeMap.get(index);
        if (fld == null) return;
        fld.getInternal().requestFocus();
    }
   
    
    public static boolean isLastEdit(int idx) {
        try {
            NTextField fld = activeMap.get(idx);
            if (fld == null) {
                return true;
            }
            return fld.isLastEdit();
            
        } catch (Throwable t) {
            return true;
        }
        
    }
    
    
    
    private boolean isLastEdit() {
        Form f = getComponentForm();
        if (f == null) {
            return true;
        }
        return f.getNextComponent(getInternal()) == null;
    }
    
    /**
     * This is a notification method called from native code to indicate that 
     * editing has ended.
     * @deprecated For internal use only
     * @param index
     * @param code Either CODE_NEXT = The next button was pressed, or CODE_DONE = the done button was pressed.
     */
    public static void endEditing(int index, int code) {
        if (!CN.isEdt()) {
            callSerially(()->endEditing(index, code));
            return;
        }
        NTextField fld = activeMap.get(index);
        if (fld != null) {
            fld.endEditing(code);
        }
    }
    
    /**
     * This is a notification method called from native code to indicate that 
     * editing has ended.
     * @deprecated For internal use only
     * @param code Either CODE_NEXT = The next button was pressed, or CODE_DONE = the done button was pressed.
     */
    private void endEditing(int code) {
        switch (code) {
            case CODE_DONE:
                doneListeners.fireActionEvent(new ActionEvent(this));
                break;
            case CODE_NEXT: {
                Form f = this.getComponentForm();
                if (f != null) {
                    Component cmp = f.getNextComponent(getInternal());
                    if (cmp != null) {
                        cmp.requestFocus();
                        cmp.startEditingAsync();
                    }
                }
            }
                
        }
    }
    
    

    public static void fireActionEvent(int index) {
        if (!CN.isEdt()) {
            callSerially(()->fireActionEvent(index));
            return;
        } else {
            NTextField fld = activeMap.get(index);
            if (fld != null) {
                fld.fireActionEvent();
            }
        }
    }
    
    private void fireActionEvent() {
        actionListeners.fireActionEvent(new ActionEvent(this));
    }
    
    public static void fireChangeEvent(int index) {
        if (!CN.isEdt()) {
            callSerially(()->fireChangeEvent(index));
            return;
        } else {
            NTextField fld = activeMap.get(index);
            if (fld != null) {
                fld.fireChangeEvent();
            }
        }
    }
    
    private void fireChangeEvent() {
        changeListeners.fireActionEvent(new ActionEvent(this));
    }
    
    

    @Override
    protected void initComponent() {
        super.initComponent();
        activeMap.put(id, this);
        //Display.getInstance().addVirtualKeyboardListener(keyboardListener);
    }

    @Override
    protected void deinitialize() {
        //Display.getInstance().removeVirtualKeyboardListener(keyboardListener);
        activeMap.remove(id);
        super.deinitialize();
        
    }
    
    
    public void registerAsInputDevice() {
        if (isLightWeight) {
            // If we are lightweight then the actual input device
            // is the internal field.
            // Only in the native case do we need to concern ourselves
            // with these details.
            return;
        }
        final NTextField cmp = this;
        Form f = this.getComponentForm();
        
        if (f != null && f.getCurrentInputDevice() != currentInput) {
            try {
                NTextFieldInputDevice previousInput = null;
                if (f.getCurrentInputDevice() instanceof NTextFieldInputDevice) {
                    previousInput = (NTextFieldInputDevice)f.getCurrentInputDevice();
                    if (previousInput.editedTextArea == this) {
                        // If the previous input is the same input, let's disable it's close 
                        // handler altogether.
                        previousInput.enabled = false;
                    }
                    
                }
                
                currentInput = new NTextFieldInputDevice(this);
                f.setCurrentInputDevice(currentInput);
                
                
            } catch (Exception ex) {
                Log.e(ex);
                // Failed to edit string because the previous input device would not
                // give up control
                return;
            }
        }
    }
    private static class NTextFieldInputDevice implements VirtualInputDevice {
        private NTextField editedTextArea;

        private boolean enabled = true;

        NTextFieldInputDevice(NTextField ta) {
            editedTextArea = ta;
        }

        @Override
        public void close() throws Exception {
            if (!enabled) {
                return;
            }
            if (editedTextArea.getInternal().isEditing()) {
                editedTextArea.getInternal().stopEditing(null);
            }
        }
        
    }
    
    private class NTextFieldEditingDelegate implements Editable {

        @Override
        public boolean isEditable() {
            return true;
        }

        @Override
        public boolean isEditing() {
            return peer.isEditing();
        }

        @Override
        public void startEditingAsync() {
            registerAsInputDevice();
            peer.startEditingAsync();
        }

        @Override
        public void stopEditing(Runnable onFinish) {
            peer.stopEditing();
            if (onFinish != null) {
                onFinish.run();
            }
        }
        
    }
    
    /*
    private class KeyboardListener implements ActionListener {
        private int paddingTop, paddingLeft, paddingRight;
        
        @Override
        public void actionPerformed(ActionEvent evt) {
            System.out.println(id+"Keyboard event received");
            if (true) return;
            if (isLightWeight) {
                System.out.println(id+"lightweight .. ignoring");
                return;
            }
            
            Boolean show = (Boolean)evt.getSource();
            Form f = CN.getCurrentForm();
            if (f == null) {
                
                System.out.println(id+"no form... ignoring event");
                return;
            }
            if (show) {
                if (!isEditing()) {
                    System.out.println(id+"not currently editing.  ignoring event");
                    return;
                }
                System.out.println(id+" it is a show event");
                Style s = f.getContentPane().getStyle();
                paddingTop = s.getPaddingTop();
                paddingLeft = s.getPaddingLeft(true);
                paddingRight = s.getPaddingRight(true);
                f.getAnimationManager().flushAnimation(()->{
                    
                    System.out.println(id+"adding padding "+peer.getKeyboardHeight());
                    $(f.getContentPane()).selectAllStyles().setPadding(
                            paddingTop, 
                            paddingRight, 
                            peer.getKeyboardHeight(), 
                            paddingLeft
                    );
                    f.getContentPane().animateLayoutAndWait(300);
                    Container parent = NTextField.this.getParent();
                    if (parent != null) {
                        System.out.println(id+"Scrolling visible");
                        parent.scrollComponentToVisible(NTextField.this);
                        UITimer.timer(1000, false, ()->{
                            System.out.println("Now invisibleAreaUnderVKB="+peer.getKeyboardHeight());
                        });
                    }
                    
                });
                
                
                
            } else {
                evt.consume(); // Only one of the text fields need to handle this event.
                f.getAnimationManager().flushAnimation(()->{
                    
                    System.out.println(id+"removing padding");
                    $(f.getContentPane()).selectAllStyles().setPadding(
                            paddingTop, 
                            paddingRight, 
                            0, 
                            paddingLeft
                    );
                    f.revalidateWithAnimationSafety();
                });
            }
        }
    
    }
*/
}
    
    

