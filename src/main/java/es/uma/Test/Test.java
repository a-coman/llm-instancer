package es.uma.Test;

import es.uma.Llms;
import es.uma.Model;

public class Test {

    public static void main(String[] args) {
        ITest basic = Llms.getAgent(ITest.class, Llms.getModel(Model.GEMINI_2_FLASH_LITE));
        System.out.println(basic.chat("Hello!"));
    }

}
