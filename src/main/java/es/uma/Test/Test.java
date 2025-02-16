package es.uma.Test;

import es.uma.Agents;
import es.uma.Models;

public class Test {

    public static void main(String[] args) {
        ITest basic = Agents.getTest(Models.getModel("4o"));
        System.out.println(basic.chat("Hello!"));
    }

}
