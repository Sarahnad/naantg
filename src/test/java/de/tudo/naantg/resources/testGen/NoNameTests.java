package de.tudo.naantg.resources.testGen;


import de.tudo.naantg.testproject.scheinboot.ScheinJob;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoNameTests /*implements NoNameTG*/ {

    @Test
    public void generatedSetGetTestOfJob() {
        ScheinJob scheinJob = new ScheinJob();
        String param1 = "§Puiu";

        scheinJob.setJob(param1);

        String actual = scheinJob.getJob();
        assertEquals(param1, actual);
    }


    @Test
    public void generatedSetGetTestOfCompanyName() {
        ScheinJob scheinJob = new ScheinJob();
        String param1 = "§Puiu";

        scheinJob.setCompanyName(param1);

        String actual = scheinJob.getCompanyName();
        assertEquals(param1, actual);
    }


    @Test
    public void generatedSetGetTestOfPersonCount() {
        ScheinJob scheinJob = new ScheinJob();
        int param1 = 44;

        scheinJob.setPersonCount(param1);

        int actual = scheinJob.getPersonCount();
        assertEquals(param1, actual);
    }


    @Test
    public void generatedSetGetTestOfTitle() {
        ScheinJob scheinJob = new ScheinJob();
        String param1 = "§Puiu";

        scheinJob.setTitle(param1);

        String actual = scheinJob.getTitle();
        assertEquals(param1, actual);
    }


    @Test
    public void generatedSetGetTestOfJobId() {
        ScheinJob scheinJob = new ScheinJob();
        long param1 = 1628253323520968601L;

        scheinJob.setJobId(param1);

        long actual = scheinJob.getJobId();
        assertEquals(param1, actual);
    }


    private ScheinJob generatedConstructorOfScheinJob1() {
        String str = "";
        ScheinJob scheinJob = new ScheinJob(str);

        return scheinJob;
    }


}
