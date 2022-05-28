package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.annotations.Params;
import de.tudo.naantg.testproject.scheinboot.Hauptschein;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinEntityException;
import de.tudo.naantg.testproject.test.HauptscheinTG;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class HauptscheinTests /*implements HauptscheinTG*/ {


	@Test
	@Params
	public void whenCalc_thenReturnException() {
		Hauptschein hauptschein = new Hauptschein();
		String param1 = "one";
		String param2 = "one";

		assertThrows(ScheinEntityException.class, () -> {
				hauptschein.calc(param1, param2);
			});
	}





	@Test
	@Params
	public void whenFindOptGoodEntity() {
		Hauptschein hauptschein = new Hauptschein();

		Optional<ScheinEntity> actual = hauptschein.findOptGoodEntity();

		assertTrue(actual.isPresent());
		assertEquals("good", actual.get().getName());
		assertEquals("dtrfzujhil", actual.get().getPassword());
		assertEquals(7, actual.get().getScheinId());
	}


	@Test
	@Params
	public void whenCreateGoodMap() {
		Hauptschein hauptschein = new Hauptschein();

		Map<String,Integer> actual = hauptschein.createGoodMap();

		assert actual != null;
		assertEquals(7, actual.get("good"));
	}


	@Test
	@Params
	public void whenFindGoodEntity() {
		Hauptschein hauptschein = new Hauptschein();

		ScheinEntity actual = hauptschein.findGoodEntity();

		assert actual != null;
		assertEquals("good", actual.getName());
		assertEquals("dtrfzujhil", actual.getPassword());
		assertEquals(7, actual.getScheinId());
	}


}