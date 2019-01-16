# CN1 Native Controls

This library will contain a few useful native controls for [Codename One](https://www.codenameone.com).  Currently (first release) it includes only a single control "NSelect", which is a native select widget.  This is mostly useful when deploying to Javascript since the lightweight alternatives (Picker, and ComboBox) don't quite give a native feel on that platform.

## Requirements

This should work on all platforms. For the NSelect widget, it will use native widgets on the JavaSE and Javascript platforms only.  It will fall back to a lightweight ComboBox implementation on all other platforms.

## Installation

Either install this library through Codename One Settings, or download it here, copy to your project's "lib" directory, and select "Codename One" > "Refresh CN1libs".

## Usage

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

## Created by

[Steve Hannah](https://sjhannah.com)

