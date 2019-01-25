# CN1 Native Controls

This library will contain a few useful native controls for [Codename One](https://www.codenameone.com).  Currently it includes only two controls "NSelect", which is a native select widget, and "NTextField", which is a native text field.  NSelect is mostly useful when deploying to Javascript since the lightweight alternatives (Picker, and ComboBox) don't quite give a native feel on that platform.  NTextField was developed primarily to provide support for [native password managers](https://github.com/codenameone/CodenameOne/issues/2467).

## Requirements

This should work on all platforms. For the NSelect widget, it will use native widgets on the JavaSE and Javascript platforms only.  It will fall back to a lightweight ComboBox implementation on all other platforms.

## Installation

Either install this library through Codename One Settings, or [download it here](bin/cn1-native-controls.cn1lib), copy to your project's "lib" directory, and select "Codename One" > "Refresh CN1libs".

## NSelect Usage

A basic usage example.

~~~~
Form hi = new Form("Hi World", BoxLayout.y());

hi.add(new Label("Hi World"));
Label result = new Label();

// Create a new NSelect
NSelect<String> select = new NSelect<>();

// Notice that we can specify font, fgcolor, and bgcolor and these 
// will be propagated to the native widget.
// Other styles not supported.
// NOTE:  Native widgets are opaque.  You can't use alpha transparency.
$(select).selectAllStyles()
        .setFont(Font.createTrueTypeFont(Font.NATIVE_MAIN_LIGHT, 4f))
        .setFgColor(0xff0000)
        .setBgColor(0x00ff00)
        ;
select.setOptions("Red", "Green", "Blue", "Orange");
select.addSelectionListener((i1, i2)->{
    result.setText(select.getOption(select.getSelectedIndex()));
    hi.revalidateWithAnimationSafety();
});
hi.add(result);
hi.add(select);


hi.show();
~~~~

## NTextField Usage

A basic usage example.

~~~~
...
hi.add("Text fields");
hi.add("Username:");
NTextField tf1 = new NTextField(TextField.USERNAME);
System.out.println("Setting font to main light 15mm");
tf1.getAllStyles().setFont(Font.createTrueTypeFont(Font.NATIVE_MAIN_LIGHT, 15f));
System.out.println("Finished setting font");
tf1.getAllStyles().setFgColor(0x003300);
tf1.getAllStyles().setBgTransparency(255);
tf1.getAllStyles().setBgColor(0xcccccc);
tf1.getAllStyles().setAlignment(CENTER);
hi.add(tf1);
hi.add("Password:");
NTextField tf2 = new NTextField(TextField.PASSWORD);
hi.add(tf2);
hi.add("Email:");
NTextField emailField = new NTextField(TextField.EMAILADDR);
hi.add(emailField);

tf1.addActionListener(e->{
    //tf2.setText(tf1.getText());
});
tf1.addChangeListener(e->{
   result.setText(tf1.getText());
   hi.revalidateWithAnimationSafety();
});
tf2.addActionListener(e->{
    Log.p("Action listener fired on password field");
    result.setText(tf2.getText());
    hi.revalidateWithAnimationSafety();
});
tf2.addDoneListener(e->{
    Log.p("Done was clicked!!!");
});
...
~~~~


## Created by

[Steve Hannah](https://sjhannah.com)

