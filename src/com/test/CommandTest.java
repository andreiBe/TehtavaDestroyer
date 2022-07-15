package com.test;

import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.datatypes.BeloClass;

public class CommandTest {
    @org.junit.jupiter.api.Test
    void run() throws BeloScriptException {
        String path = "pohjat/P=UI.kaava";
        String json = "{\"P\":\"8.8\", \"U\":\"3.43\", \"I\":\"?\",}";
        BeloClass result = BeloScript.runFile(path,"json:" + json);

        path = "pohjat/lämpömäärä.kaava";
        json = "{\"Q\":83, \"c\":8.32, \"m\":32, \"delta t\":\"?\"}";
        BeloScript.runFile(path,"json:"+json);
    }
}
