#import "com_codename1_nui_NTextFieldNativeImpl.h"
#import "com_codename1_nui_NTextField.h"
#import "CodenameOne_GLViewController.h"
#import "com_codename1_ui_plaf_UIManager.h"
#import "com_codename1_impl_ios_IOSImplementation.h"

extern int isIPad();
extern int isVKBAlwaysOpen();
extern int vkbHeight;
extern BOOL vkbAlwaysOpen;
extern void cn1_setStyleDoneButton(CN1_THREAD_STATE_MULTI_ARG UIBarButtonItem* btn);
extern NSString* toNSString(CN1_THREAD_STATE_MULTI_ARG JAVA_OBJECT str);
extern float scaleValue;

@implementation com_codename1_nui_NTextFieldNativeImpl

-(void*)createNativeTextField:(int)index param1:(int)constraint{
    idx = index;
    BOOL showToolbar = YES;
    BOOL isLastEdit = com_codename1_nui_NTextField_isLastEdit___int_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG idx);
    
    dispatch_sync(dispatch_get_main_queue(), ^{
        utf = [[UITextField alloc] init];
         if (isLastEdit) {
            utf.returnKeyType = UIReturnKeyDone;
        } else {
            utf.returnKeyType = UIReturnKeyNext;
        }
        utf.delegate = self;
        [[NSNotificationCenter defaultCenter]
         addObserver:utf.delegate
         selector:@selector(textFieldDidChange)
         name:UITextFieldTextDidChangeNotification
         object:utf];
        // INITIAL_CAPS_WORD
        if((constraint & 0x100000) == 0x100000) {
            utf.autocapitalizationType = UITextAutocapitalizationTypeWords;
        } else {
            // INITIAL_CAPS_SENTENCE
            if((constraint & 0x200000) == 0x200000) {
                utf.autocapitalizationType = UITextAutocapitalizationTypeSentences;
            } else {
                utf.autocapitalizationType = UITextAutocapitalizationTypeNone;
            }
        }

        // NON_PREDICTIVE
        if((constraint & 0x80000) == 0x80000) {
            utf.autocorrectionType = UITextAutocorrectionTypeNo;
        }

        // PASSWORD
        if((constraint & 0x10000) == 0x10000) {
            utf.secureTextEntry = YES;
            if (@available(iOS 11, *)) {
                utf.textContentType = UITextContentTypePassword;
            }
        }

        if ((constraint & 0x400000) == 0x400000) {
            if (@available(iOS 11, *)) {
                utf.textContentType = UITextContentTypeUsername;
                utf.keyboardType = UIKeyboardTypeEmailAddress;
            }
        }

        // EMAILADDR
        int cccc = constraint & 0xff;
        if(cccc == 1) {
            utf.keyboardType = UIKeyboardTypeEmailAddress;
            utf.autocapitalizationType = UITextAutocapitalizationTypeNone;
            if (@available(iOS 10, *)) {
                utf.textContentType = UITextContentTypeEmailAddress;
            }
        } else {
            // NUMERIC
            if(cccc == 2) {
                utf.keyboardType = UIKeyboardTypeNumberPad;
            } else {
                // PHONENUMBER
                if(cccc == 3) {
                    utf.keyboardType = UIKeyboardTypePhonePad;
                    if (@available(iOS 10, *)) {
                        utf.textContentType = UITextContentTypeTelephoneNumber;
                    }
                } else {
                    // URL
                    if(cccc == 4) {
                        utf.keyboardType = UIKeyboardTypeURL;
                        utf.autocapitalizationType = UITextAutocapitalizationTypeNone;
                        if (@available(iOS 10, *)) {
                            utf.textContentType = UITextContentTypeURL;
                        }
                    } else {
                        // DECIMAL
                        if(cccc == 5) {
                            utf.keyboardType = UIKeyboardTypeDecimalPad;
                        }
                    }
                }
            }
        }
        
        if ((utf.keyboardType == UIKeyboardTypeDecimalPad
                 || utf.keyboardType == UIKeyboardTypePhonePad
                 || utf.keyboardType == UIKeyboardTypeNumberPad
                 || utf.returnKeyType == UIReturnKeyNext) && !isIPad()) {
                //add navigation toolbar to the top of the keyboard
            if(showToolbar) {
#ifndef CN1_USE_ARC
                UIToolbar *toolbar = [[[UIToolbar alloc] init] autorelease];
#else
                UIToolbar *toolbar = [[UIToolbar alloc] init];
#endif
                [toolbar setBarStyle:UIBarStyleBlackTranslucent];
                [toolbar sizeToFit];

                //add a space filler to the left:
                UIBarButtonItem *flexButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:
                                               UIBarButtonSystemItemFlexibleSpace target: nil action:nil];

                NSString* buttonTitle;

                JAVA_OBJECT obj = com_codename1_ui_plaf_UIManager_getInstance___R_com_codename1_ui_plaf_UIManager(CN1_THREAD_GET_STATE_PASS_SINGLE_ARG);

                JAVA_OBJECT str;
                UIBarButtonItem *doneButton;
                NSArray *itemsArray = nil;
                if (isLastEdit) {
#ifndef NEW_CODENAME_ONE_VM
                    str = com_codename1_ui_plaf_UIManager_localize___java_lang_String_java_lang_String(obj, fromNSString(@"done"), fromNSString(@"Done"));
#else
                    str = com_codename1_ui_plaf_UIManager_localize___java_lang_String_java_lang_String_R_java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG obj, fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"done"), fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"Done"));
#endif
                    buttonTitle = toNSString(CN1_THREAD_GET_STATE_PASS_ARG str);
                    doneButton = [[UIBarButtonItem alloc]initWithTitle:buttonTitle style:UIBarButtonItemStyleDone target:utf.delegate action:@selector(keyboardDoneClicked)];
                    cn1_setStyleDoneButton(CN1_THREAD_GET_STATE_PASS_ARG doneButton);
                } else {

                    
                    str = com_codename1_ui_plaf_UIManager_localize___java_lang_String_java_lang_String_R_java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG obj, fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"next"), fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"Next"));

                    buttonTitle = toNSString(CN1_THREAD_GET_STATE_PASS_ARG str);
                    doneButton = [[UIBarButtonItem alloc]initWithTitle:buttonTitle style:UIBarButtonItemStyleDone target:utf.delegate action:@selector(keyboardNextClicked)];
                    cn1_setStyleDoneButton(CN1_THREAD_GET_STATE_PASS_ARG doneButton);
                    if(utf.keyboardType == UIKeyboardTypeDecimalPad
                                         || utf.keyboardType == UIKeyboardTypePhonePad
                                         || utf.keyboardType == UIKeyboardTypeNumberPad) {
                        // we need both done and next

                        str = com_codename1_ui_plaf_UIManager_localize___java_lang_String_java_lang_String_R_java_lang_String(CN1_THREAD_GET_STATE_PASS_ARG obj, fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"done"),
                                                                                                                              fromNSString(CN1_THREAD_GET_STATE_PASS_ARG @"Done"));
                                                                                                                              
                        buttonTitle = toNSString(CN1_THREAD_GET_STATE_PASS_ARG str);
                        UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc]initWithTitle:buttonTitle style:UIBarButtonItemStyleDone target:utf.delegate action:@selector(keyboardDoneClicked)];
                        cn1_setStyleDoneButton(CN1_THREAD_GET_STATE_PASS_ARG anotherButton);
                        itemsArray = [NSArray arrayWithObjects: flexButton, anotherButton, doneButton, nil];
#ifndef CN1_USE_ARC
                        [anotherButton release];
#endif
                    }
                }

                if(itemsArray == nil) {
                    itemsArray = [NSArray arrayWithObjects: flexButton, doneButton, nil];
                }

#ifndef CN1_USE_ARC
                [flexButton release];
                [doneButton release];
#endif
                [toolbar setItems:itemsArray];
                [utf setInputAccessoryView:toolbar];
            }
        }
    
    });
    return utf;
}

-(void)setText:(NSString*)text{
    dispatch_sync(dispatch_get_main_queue(), ^{
        utf.text = text;
    });
}

-(void)focus{
}

-(NSString*)getText{
    __block NSString* out = nil;
    dispatch_sync(dispatch_get_main_queue(), ^{
        out = [utf.text copy];
    });
    if (out == nil) return @"";
    return out;
}

-(void)blur{
    
}

-(BOOL)isSupported{
    return YES;
}

-(void)textFieldDidChange {
    com_codename1_nui_NTextField_fireChangeEvent___int(CN1_THREAD_GET_STATE_PASS_ARG idx);
}
-(void) keyboardDoneClicked {
    [utf resignFirstResponder];
    com_codename1_nui_NTextField_endEditing___int_int(CN1_THREAD_GET_STATE_PASS_ARG idx, 1);
}
-(void) keyboardNextClicked {
    [utf resignFirstResponder];
    com_codename1_nui_NTextField_endEditing___int_int(CN1_THREAD_GET_STATE_PASS_ARG idx, 2);
}
- (BOOL)textFieldShouldReturn:(UITextField *)theTextField {
    BOOL isLastEdit = com_codename1_nui_NTextField_isLastEdit___int_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG idx);
    [utf resignFirstResponder];
    if (!isLastEdit) {
        com_codename1_nui_NTextField_endEditing___int_int(CN1_THREAD_GET_STATE_PASS_ARG idx, 2);
    } else {
        com_codename1_nui_NTextField_endEditing___int_int(CN1_THREAD_GET_STATE_PASS_ARG idx, 1);
    }
    return YES;
}

- (BOOL) textFieldShouldEndEditing:(UITextField *)textField {
    com_codename1_nui_NTextField_fireActionEvent___int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    
    return YES;
}

- (BOOL) textFieldShouldBeginEditing:(UITextField *)textField {
    //com_codename1_nui_NTextField_fireActionEvent___int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    vkbAlwaysOpen=YES;
    BOOL isLastEdit = com_codename1_nui_NTextField_isLastEdit___int_R_boolean(CN1_THREAD_GET_STATE_PASS_ARG idx);
    
    if (isLastEdit) {
        utf.returnKeyType = UIReturnKeyDone;
    } else {
        utf.returnKeyType = UIReturnKeyNext;
    }
    com_codename1_nui_NTextField_requestFocus___int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    
    return YES;
}

-(void)updateStyle {
    int fgColor = com_codename1_nui_NTextField_getFgColor___int_R_int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    int bgColor =  com_codename1_nui_NTextField_getBgColor___int_R_int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    int alignment =  com_codename1_nui_NTextField_getTextAlign___int_R_int(CN1_THREAD_GET_STATE_PASS_ARG idx);
    struct ThreadLocalData* threadStateData = CN1_THREAD_GET_STATE_PASS_SINGLE_ARG;
    enteringNativeAllocations();
    JAVA_OBJECT jfont = com_codename1_nui_NTextField_getNativeFont___int_R_java_lang_Object(CN1_THREAD_GET_STATE_PASS_ARG idx);
    UIFont* font = (UIFont*)com_codename1_impl_ios_IOSImplementation_getFontPeer___com_codename1_impl_ios_IOSImplementation_NativeFont_R_long(threadStateData, jfont);
    finishedNativeAllocations();
    dispatch_async(dispatch_get_main_queue(), ^{
        
        utf.textAlignment = alignment == 4 ? NSTextAlignmentCenter :
            alignment == 3 ? NSTextAlignmentRight : NSTextAlignmentLeft;
        [utf setTextColor:UIColorFromRGB(fgColor, 255)];
        [utf setBackgroundColor:UIColorFromRGB(bgColor, 255)];
        float scale = scaleValue;
        if(scale != 1) {
            float s = font.pointSize / scale;
            utf.font = [font fontWithSize:s];
        } else {
            utf.font = font;
        }
    });
    
}

-(void)startEditingAsync {
    dispatch_async(dispatch_get_main_queue(), ^{
        [utf becomeFirstResponder];
    });
}

-(void)stopEditing {
    dispatch_async(dispatch_get_main_queue(), ^{
        [utf resignFirstResponder];
    });
}

-(BOOL)isEditing {
    __block BOOL out = NO;
    dispatch_sync(dispatch_get_main_queue(), ^{
        out = [utf isFirstResponder];
    });
    return out;
}

-(int)getKeyboardHeight {
    return (JAVA_INT)vkbHeight*scaleValue;
}

@end
