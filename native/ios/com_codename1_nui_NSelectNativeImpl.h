#import <Foundation/Foundation.h>

@interface com_codename1_nui_NSelectNativeImpl : NSObject {
}

-(void*)createNativeSelect:(int)param;
-(void)setOptions:(NSString*)param;
-(int)getSelectedIndex;
-(void)setSelectedIndex:(int)param;
-(BOOL)isSupported;
@end
