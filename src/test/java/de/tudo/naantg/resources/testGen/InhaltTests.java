package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.weiter.Dinge;
import de.tudo.naantg.testproject.weiter.Inhalt;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class InhaltTests /*implements InhaltTG*/ {


	@Test
	public void whenSetContent_with_2_values_thenGetContent_hasEqualValues() {
		Inhalt inhalt = new Inhalt();
		List<Dinge> param1 = Arrays.asList(Dinge.BECHER, Dinge.BECHER);

		inhalt.setContent(param1);

		List<Dinge> actual = inhalt.getContent();
		assertEquals(param1, actual);
	}


	@Test
	public void whenSetContent_thenGetContent_hasEqualValue() {
		Inhalt inhalt = new Inhalt();
		List<Dinge> param1 = new ArrayList<>();

		inhalt.setContent(param1);

		List<Dinge> actual = inhalt.getContent();
		assertEquals(param1, actual);
	}


}