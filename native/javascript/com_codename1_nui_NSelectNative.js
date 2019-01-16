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
(function(exports){

var o = {};

    o.createNativeSelect__int = function(param1, callback) {
        this.index = param1;
        this.el = document.createElement('select');
        this.el.setAttribute("data-cn1-match-style", "true");
        var fireSelectionChanged = this.$GLOBAL$.com_codename1_nui_NSelect.fireSelectionChanged__int$async;
        jQuery(this.el).change(function() {
            fireSelectionChanged(param1);
        });
        callback.complete(this.el);
    };

    o.setOptions__java_lang_String = function(param1, callback) {
        var self = this;
        self.el.options.length = 0;
        var opts = param1.split("\n");
        console.log(opts);
        console.log('before: ', self.el);
        jQuery(opts).each(function(k,v) {
            var opt = document.createElement('option');
            opt.value=''+k;
            opt.text=v;
            self.el.add(opt);
        });
        console.log('after: ', self.el);
        callback.complete(null);
    };

    o.getSelectedIndex_ = function(callback) {
        callback.complete(this.el.selectedIndex);
    };

    o.setSelectedIndex__int = function(param1, callback) {
        this.el.selectedIndex = param1;
        callback.complete(null);
    };

    o.isSupported_ = function(callback) {
        callback.complete(true);
    };

exports.com_codename1_nui_NSelectNative= o;

})(cn1_get_native_interfaces());
