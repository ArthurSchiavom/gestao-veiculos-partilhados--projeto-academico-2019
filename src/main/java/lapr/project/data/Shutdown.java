package lapr.project.data;

import lapr.project.data.registers.Company;

public class Shutdown {
    private Shutdown() {}

    public static void shutdown() {
        Company.getInstance().getDataHandler().closeAll();
    }
}