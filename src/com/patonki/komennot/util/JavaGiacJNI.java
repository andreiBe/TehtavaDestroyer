package com.patonki.komennot.util;

import com.patonki.util.NativeUtils;
import javagiac.context;
import javagiac.gen;
import javagiac.giac;

import java.io.IOException;
//lataa giac kirjaston käyttöön ja tarjoaa apumetodeja kirjaston käyttöön
public class JavaGiacJNI {
    static {
        try {
            NativeUtils.loadLibraryFromJar("/libraries/windows/javagiac.dll");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String[] ratkaise(String yhtalo) {
        context C=new context();
        gen h=new gen("solve("+yhtalo+")",C);
        h= giac._factor(h,C);
        h=giac._simplify(h,C);
        h=giac._evalf(h,C);
        if (h.getType()==7) {
            String list = h.print(C);
            list = list.substring(list.indexOf('[')+1,list.indexOf(']'));
            return list.split(",");
        }
        return new String[] {h.print(C)};
    }

}
