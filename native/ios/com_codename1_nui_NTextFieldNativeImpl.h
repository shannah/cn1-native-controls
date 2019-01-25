#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface com_codename1_nui_NTextFieldNativeImpl : NSObject<UITextFieldDelegate> {
    UITextField* utf;
    int idx;
    
}

-(void*)createNativeTextField:(int)param param1:(int)param1;
-(void)setText:(NSString*)param;
-(void)focus;
-(void)updateStyle;
-(NSString*)getText;
-(void)blur;
-(BOOL)isSupported;
-(void)textFieldDidChange;
-(void) keyboardDoneClicked;
-(void) keyboardNextClicked;
-(void) startEditingAsync;
-(BOOL) isEditing;
-(BOOL) stopEditing;
-(int) getKeyboardHeight;
@end
