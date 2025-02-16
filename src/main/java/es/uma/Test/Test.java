package es.uma.Test;

import es.uma.Llms;

public class Test {

    public static void main(String[] args) {
        ITest basic = Llms.getAgent(ITest.class, Llms.getModel("4o"));
        System.out.println(basic.chat("Hello!"));
    }

}
