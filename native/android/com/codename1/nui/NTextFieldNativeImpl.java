package com.codename1.nui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.codename1.impl.android.AndroidImplementation;
import com.codename1.impl.android.AndroidNativeUtil;
import com.codename1.ui.CN;
import com.codename1.ui.Component;
import com.codename1.ui.Display;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.FocusListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class NTextFieldNativeImpl {
    private int index;
    private static SparseIntArray mInputTypeMap = new SparseIntArray(10);
    private EditText view;
    private int codenameOneInputType = TextArea.ANY;
    static {
        initInputTypeMap();
    }
    
    /**
     * Prepare an int-to-int map that maps Codename One input-types to
     * Android input types
     */
    private static void initInputTypeMap() {
        mInputTypeMap.append(TextArea.ANY, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mInputTypeMap.append(TextArea.DECIMAL, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        mInputTypeMap.append(TextArea.EMAILADDR, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mInputTypeMap.append(TextArea.INITIAL_CAPS_SENTENCE, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mInputTypeMap.append(TextArea.INITIAL_CAPS_WORD, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mInputTypeMap.append(TextArea.NON_PREDICTIVE, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mInputTypeMap.append(TextArea.NUMERIC, InputType.TYPE_CLASS_NUMBER);
        mInputTypeMap.append(TextArea.PASSWORD, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mInputTypeMap.append(TextArea.PHONENUMBER, InputType.TYPE_CLASS_PHONE);
        mInputTypeMap.append(TextArea.URL, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        
    }

    private void setAutofillHints(int constraint) {
        boolean important = false;
        if ((constraint & TextArea.PASSWORD) != 0) {
            important = true;
            try {
                Method m = View.class.getMethod("setAutofillHints", String[].class);
                m.invoke(view, new Object[]{new String[]{"password"}});

            } catch (NoSuchMethodException nsme) {

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        if ((constraint & TextArea.USERNAME) != 0) {
            important = true;
            try {
                Method m = View.class.getMethod("setAutofillHints", String[].class);
                m.invoke(view, new Object[]{new String[]{"username"}});

            } catch (NoSuchMethodException nsme) {

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        if (important) {
            try {
                Method m = View.class.getMethod("setAutofillHints", String[].class);
                m.invoke(view, new Object[]{new String[]{"username"}});

            } catch (NoSuchMethodException nsme) {

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    
    public android.view.View createNativeTextField(final int index, final int constraint) {
        this.index = index;
        this.codenameOneInputType = constraint;
        dispatchSync(new Runnable() {
            @Override
            public void run() {
                int codenameOneInputType = constraint;
                view = new EditText(AndroidNativeUtil.getContext());
                setAutofillHints(constraint);
                view.setSingleLine(true);
                view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean bln) {
                        if (bln) {
                            handleFocusGained();
                        } else {
                            handleFocusLost();
                        }
                    }
                });
                view.setOnEditorActionListener(
                    new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            return handleEditorAction(actionId);
                        }
                    }
                );
                view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        NTextField.fireChangeEvent(index);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                view.setFocusableInTouchMode(true);
                view.setFocusable(true);
                
                view.setBackgroundDrawable(null);

                //FrameLayout.LayoutParams mEditLayoutParams = new FrameLayout().LayoutParams(0, 0);
                // Set the appropriate gravity so that the left and top margins will be
                // taken into account
                //mEditLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                //mEditLayoutParams.setMargins(0, 0, 0, 0);
                //mEditLayoutParams.width = textArea.getWidth();
                //mEditLayoutParams.height = textArea.getHeight();

                //view.setLayoutParams(mEditLayoutParams);
                
            }
        });
       
        
        NTextField ntf = NTextField.getInstance(index);
        if (ntf != null) {

            /*
            // focus should already by bound
            ntf.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(Component cmp) {
                    dispatchAsync(new Runnable() {
                        public void run() {
                            if (!view.isFocused()) {
                                view.requestFocus();
                            }
                        }
                    });
                }

                @Override
                public void focusLost(Component cmp) {
                    dispatchAsync(new Runnable() {
                        public void run() {
                            if (view.isFocused()) {
                                view.clearFocus();
                            }
                        }
                    });
                }
            });
*/

        }
        return view;
    }
    
    boolean handleEditorAction(int actionCode) {
        actionCode = actionCode & 0xf;
        Component next = null;
        NTextField ntf = NTextField.getInstance(index);
        boolean hasNext = false; 
        if (ntf == null) {
            return false;
        }
        if (EditorInfo.IME_ACTION_NEXT == actionCode) {
            hasNext = !NTextField.isLastEdit(index);
            
        }
        NTextField.endEditing(index, hasNext ? NTextField.CODE_NEXT : NTextField.CODE_DONE);
        return true;
        
    }
    
    // Called by native android focus listener on field
    // 
    private void handleFocusGained() {
        updateView();

        //NTextField.requestFocus(index);
        // Focus should already be bound for Android peers
        // no need to propagate this
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
    
    // Called by native android focus listener on field
    private void handleFocusLost() {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        NTextField.fireActionEvent(index);
    }
    
    
    private static boolean hasConstraint(int inputType, int constraint) {
        return ((inputType & constraint) == constraint);
    }
    
    private static boolean isNonPredictive(int inputType) {
        return hasConstraint(inputType, TextArea.NON_PREDICTIVE);
    }
    
    private static int makeNonPredictive(int codenameOneInputType, int inputType) {
        if (isNonPredictive(codenameOneInputType)) {
            return inputType | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        }
        return inputType;
    }
    
    private static int getAndroidInputType(int codenameOneInputType) {
        int type = mInputTypeMap.get(codenameOneInputType, -1);
        if (type == -1) {
            
            if (hasConstraint(codenameOneInputType, TextArea.NUMERIC)) {
                type = InputType.TYPE_CLASS_NUMBER;
            } else if (hasConstraint(codenameOneInputType, TextArea.DECIMAL)) {
                type = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED;
            } else if (hasConstraint(codenameOneInputType, TextArea.EMAILADDR)) {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                
            } else if (hasConstraint(codenameOneInputType, TextArea.INITIAL_CAPS_SENTENCE)) {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                
            } else if (hasConstraint(codenameOneInputType, TextArea.INITIAL_CAPS_WORD)) {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            } else if (hasConstraint(codenameOneInputType, TextArea.PASSWORD)) {
                type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            } else if (hasConstraint(codenameOneInputType, TextArea.PHONENUMBER)) {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_PHONE);
            } else if (hasConstraint(codenameOneInputType, TextArea.URL)) {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
            } else {
                type = makeNonPredictive(codenameOneInputType, InputType.TYPE_CLASS_TEXT);
            }
        }

        // If we're editing standard text, disable auto complete.
        // The name of the flag is a little misleading. From the docs:
        // the text editor is performing auto-completion of the text being entered
        // based on its own semantics, which it will present to the user as they type.
        // This generally means that the input method should not be showing candidates itself,
        // but can expect for the editor to supply its own completions/candidates from
        // InputMethodSession.displayCompletions().
        if ((type & InputType.TYPE_CLASS_TEXT) != 0) {
            type |= InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
        }
        return type;
    }
    
    public void updateStyle() {
        final NTextField ntf = NTextField.getInstance(index);
        if (ntf == null) {
            return;
        }
        com.codename1.ui.plaf.Style style = ntf.getStyle();
        final Object nativeFont = NTextField.getNativeFont(index);
        final int fgColor = style.getFgColor();
        final int bgColor = style.getBgColor();
        dispatchAsync(new Runnable() {
            public void run() {
                boolean changedSize = false;
                Object nfont = nativeFont;
                if (nfont == null) {
                    nfont = AndroidImplementation.getInstance().getDefaultFont();
                }
                view.setTextColor(Color.rgb(fgColor >> 16, (fgColor & 0x00ff00) >> 8, (fgColor & 0x0000ff)));
                view.setBackgroundColor(Color.rgb(bgColor >> 16, (bgColor & 0x00ff00) >> 8, (bgColor & 0x0000ff)));

                Paint p = (Paint) ((AndroidImplementation.NativeFont) nfont).font;
                float scaleX = view.getTextScaleX();
                Typeface typeFace = view.getTypeface();
                float textSize = view.getTextSize();
                if (scaleX != p.getTextScaleX() || p.getTextSize() != textSize || !typeFace.equals(p.getTypeface())) {
                    view.setTypeface(p.getTypeface());
                    view.setTextScaleX(p.getTextScaleX());
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, p.getTextSize());
                    view.invalidate();
                    CN.callSerially(new Runnable() {
                        public void run() {
                            ntf.revalidate();
                        }
                    });
                }
            }
        });
    }
    
    private void updateView() {
        boolean isLastEdit = NTextField.isLastEdit(index);
        NTextField ntf = NTextField.getInstance(index);
        if (ntf == null) {
            return;
        }
        
        boolean imeOptionTaken = true;
        int ime = EditorInfo.IME_FLAG_NO_EXTRACT_UI;

        if(ntf.getClientProperty("searchField") != null) {
            view.setImeOptions(ime | EditorInfo.IME_ACTION_SEARCH);
        } else {
            if(ntf.getClientProperty("sendButton") != null) {
                view.setImeOptions(ime | EditorInfo.IME_ACTION_SEND);
            } else {
                if(ntf.getClientProperty("goButton") != null) {
                    view.setImeOptions(ime | EditorInfo.IME_ACTION_GO);
                } else {
                    if (isLastEdit) {
                        view.setImeOptions(ime | EditorInfo.IME_ACTION_DONE);
                    } else {
                        view.setImeOptions(ime | EditorInfo.IME_ACTION_NEXT);
                    } 
                }
            }
        }


        boolean password = false;
        if((codenameOneInputType & TextArea.PASSWORD) == TextArea.PASSWORD){
            codenameOneInputType = codenameOneInputType ^ TextArea.PASSWORD;
            password = true;
        }


        view.setInputType(getAndroidInputType(codenameOneInputType));
        //if not ime was explicity requested and this is a single line textfield of type ANY add the emoji keyboard.
        if(!imeOptionTaken && codenameOneInputType == TextArea.ANY){
           view.setInputType(getAndroidInputType(codenameOneInputType) | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        }
        if(Display.getInstance().getProperty("andAddComma", "false").equals("true") &&
                (codenameOneInputType & TextArea.DECIMAL) == TextArea.DECIMAL) {
            KeyListener defaultKeyListener = view.getKeyListener();
            view.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
        }

        if (password) {
            int type = mInputTypeMap.get(codenameOneInputType, InputType.TYPE_CLASS_TEXT);
            if((type & InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) == InputType.TYPE_TEXT_FLAG_CAP_SENTENCES){
                type = type ^ InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            }
            //turn off suggestions for passwords
            view.setInputType(type | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            view.setTransformationMethod(new MyPasswordTransformationMethod());
        }
    }
    
    public int getKeyboardHeight() {
        
        return 0;
    }

    public void setText(final String param) {
        dispatchAsync(new Runnable() {
            @Override
            public void run() {
                view.setText(param);
            }
        });
        
    }

    private static void dispatchAsync(Runnable r) {
        AndroidNativeUtil.getActivity().runOnUiThread(r);
    }
    
    private static void dispatchSync(Runnable r) {
        AndroidImplementation.runOnUiThreadSync(r);
    }
    
    public void focus() {
        dispatchAsync(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
            }
        });
    }

    public String getText() {
        final String[] out = new String[1];
        dispatchSync(new Runnable() {
            public void run() {
                out[0] = view.getText().toString();
            }
        });
        return out[0];
    }

    public void blur() {
        dispatchSync(new Runnable() {
            public void run() {
                
            }
        });
    }

    public void stopEditing() {
        dispatchAsync(new Runnable() {
            public void run() {
                if (view.isFocused()) {
                    view.clearFocus();

                    
                }
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public boolean isEditing() {
        return view.isFocused();
    }

    public void startEditingAsync() {
        dispatchAsync(new Runnable() {
            public void run() {
                if (!view.isFocused()) {
                    view.requestFocus();
                }
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, 0);
            }
        });
    }

    public boolean isSupported() {
        return true;
    }
    
    public class MyPasswordTransformationMethod extends PasswordTransformationMethod {

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {

            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '\u25CF'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    };

}
