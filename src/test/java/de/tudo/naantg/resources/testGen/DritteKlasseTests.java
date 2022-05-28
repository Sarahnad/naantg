package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.ErsteKlasse;
import de.tudo.naantg.testproject.Statistik;
import de.tudo.naantg.testproject.ZweiteKlasse;
import de.tudo.naantg.testproject.test.DritteKlasseTG;
import de.tudo.naantg.testproject.weiter.Box;
import de.tudo.naantg.testproject.weiter.Dinge;
import de.tudo.naantg.testproject.weiter.Inhalt;
import de.tudo.naantg.testproject.weiter.Key;
import de.tudo.naantg.testproject.weiter.Komplex;
import de.tudo.naantg.testproject.weiter.Komplex2;
import de.tudo.naantg.testproject.weiter.Komplex3;
import de.tudo.naantg.testproject.weiter.Komplex4;
import de.tudo.naantg.testproject.weiter.Person;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class DritteKlasseTests /*implements DritteKlasseTG*/ {


	@Test
	public void whenInit_thenReturnTrue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		ErsteKlasse param1 = new ErsteKlasse();
		ZweiteKlasse param2 = new ZweiteKlasse();

		boolean actual = dritteKlasse.init(param1, param2);

		assertTrue(actual);
	}


	@Test
	public void whenGetKomplex_thenReturnNull() {
		DritteKlasse dritteKlasse = new DritteKlasse();

		Komplex actual = dritteKlasse.getKomplex();

		assert actual == null;
	}


	@Test
	public void whenSetKomplex_thenGetKomplex_hasEqualValue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		int iVar = 44;
		int iVar2 = 44;
		Komplex param1 = new Komplex(iVar, iVar2);

		dritteKlasse.setKomplex(param1);

		Komplex actual = dritteKlasse.getKomplex();
		assertEquals(param1, actual);
	}


	@Test
	public void whenSetKomplex2_thenGetKomplex2_hasEqualValue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person person = new Person(str);
		int iVar = 44;
		Box box = new Box(iVar);
		Komplex2 param1 = new Komplex2(person, box);

		dritteKlasse.setKomplex2(param1);

		Komplex2 actual = dritteKlasse.getKomplex2();
		assertEquals(param1, actual);
	}


	@Test
	public void whenDoComplexWithPerson_thenReturnNull_komplex_2() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person param1 = new Person(str);
		int a = 3;
		int b = 4;
		param1.init(a, b);

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual == null;
	}


	@Test
	public void whenDoComplexWithPerson_thenReturnNull_2() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person param1 = new Person(str);
		List<Box> boxes = null;
		createPersonWithBoxes(boxes);

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual == null;
	}


	private void createPersonWithBoxes(List boxes) {
	}


	@Test
	public void whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		List<Person> list = createNewPersonList();
		List<Box> list2 = createNewBoxList();
		Komplex3 param1 = new Komplex3(list, list2);

		dritteKlasse.setKomplex3(param1);

		Komplex3 actual = dritteKlasse.getKomplex3();
		assertEquals(param1, actual);
	}

	@Test
	public void whenDoComplex_thenReturnNull() {
		DritteKlasse dritteKlasse = new DritteKlasse();

		initDritteKlasseWithDefaultValues(dritteKlasse);

		Inhalt actual = dritteKlasse.doComplex();

		assert actual == null;
	}


	@Test
	public void whenDoComplexWithPerson_thenReturnNull() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		initDritteKlasseWithDefaultValues(dritteKlasse);
		String str = "";
		Person param1 = new Person(str);
		initPersonWithDefaultValues(param1);

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual == null;
	}


	@Test
	public void whenCanBuyBox_thenReturnFalse() {
		DritteKlasse dritteKlasse = new DritteKlasse();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void whenCanBuyBox_thenReturnTrue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		dritteKlasse.initBuy();

		boolean actual = dritteKlasse.canBuyBox();

		assertTrue(actual);
	}


	@Test
	public void givenThisInitBuy_whenCanBuyBox_thenReturnTrue() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		dritteKlasse.initBuy();

		boolean actual = dritteKlasse.canBuyBox();

		assertTrue(actual);
	}


	@Test
	public void whenDoComplexWithPerson_thenReturnValue_simple() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		Person param1 = dritteKlasse.createPerson();

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual != null;
	}


	@Test
	public void given_param_ofThisCreatePerson_whenDoComplexWithPerson_thenReturnValue_simple() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		Person param1 = dritteKlasse.createPerson();

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual != null;
	}


	@Test
	public void whenDoComplexWithPerson_thenReturnNull_komplex() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person param1 = new Person(str);
		param1.init();

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual == null;
	}



	@Test
	public void whenCanBuyBox_thenReturnFalse_6() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person p = new Person(str);
		List<Person> ps = Arrays.asList(p, p);
		int iVar = 44;
		int iVar2 = 44;
		Komplex k = new Komplex(iVar, iVar2);
		dritteKlasse.setKomplex(k);
		dritteKlasse.getKomplex().getPersons().addAll(ps);
		p.setName("Jan*a");

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void givenParamInit_whenDoComplexWithPerson_thenReturnNull() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		String str = "§Puiu";
		Person param1 = new Person(str);
		param1.init();

		Inhalt actual = dritteKlasse.doComplexWithPerson(param1);

		assert actual == null;
	}


	@Test
	public void whenCanBuyBox_thenReturnFalse_2() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		initBox(dritteKlasse);

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void givenTestInitBox_with_this_whenCanBuyBox_thenReturnFalse() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		initBox(dritteKlasse);

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void whenCanBuyBox_thenReturnFalse_3() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		Statistik.work();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void givenStaticStatistik_work_whenCanBuyBox_thenReturnFalse() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		Statistik.work();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void whenCanBuyBox_thenReturnFalse_4() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		initBox(dritteKlasse);
		Statistik.work();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void whenCanBuyBox_thenReturnFalse_5() {
		DritteKlasse dritteKlasse = initialBoxes();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void given_this_ofTestInitialBoxes_whenCanBuyBox_thenReturnFalse() {
		DritteKlasse dritteKlasse = initialBoxes();

		boolean actual = dritteKlasse.canBuyBox();

		assertFalse(actual);
	}


	@Test
	public void whenCanBuyBox_thenReturnTrue_2() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		dritteKlasse.setPoints(50);
		dritteKlasse.setAMillion(100000);

		boolean actual = dritteKlasse.canBuyBox();

		assertTrue(actual);
	}


	private void initBox(DritteKlasse dritteKlasse) {
	}


	private DritteKlasse initialBoxes() {
		DritteKlasse dritteKlasse = new DritteKlasse();
		return dritteKlasse;
	}


	private List<Person> createNewPersonList() {
		List<Person> list = new ArrayList<>();
		String[] personStrs = new String[] {"cat", "dog"};
		for (String elem : personStrs) {
			list.add(new Person(elem));
		}
		return list;
	}


	private List<Box> createNewBoxList() {
		List<Box> list2 = new ArrayList<>();
		int[] boxIVars = new int[] {1, -3};
		for (int elem : boxIVars) {
			list2.add(new Box(elem));
		}
		return list2;
	}


	private void initDritteKlasseWithDefaultValues(DritteKlasse dritteKlasse) {
		int points = 0;
		long aMillion = 0L;
		int iVar = 0;
		int iVar2 = 0;
		Komplex komplex = new Komplex(iVar, iVar2);
		String str = "";
		Person person = new Person(str);
		initPersonWithDefaultValues(person);
		int iVar3 = 0;
		Box box = new Box(iVar3);
		initBoxWithDefaultValues(box);
		Komplex2 komplex2 = new Komplex2(person, box);
		List<Person> list = new ArrayList<>();
		List<Box> list2 = new ArrayList<>();
		Komplex3 komplex3 = new Komplex3(list, list2);
		List<Person> list3 = new ArrayList<>();
		List<Box> list4 = new ArrayList<>();
		String[] array = new String[0];
		Komplex4 komplex4 = new Komplex4(list3, list4, array);
		List<Komplex> komplexes = new ArrayList<>();
		dritteKlasse.setPoints(points);
		dritteKlasse.setAMillion(aMillion);
		dritteKlasse.setKomplex(komplex);
		dritteKlasse.setKomplex2(komplex2);
		dritteKlasse.setKomplex3(komplex3);
		dritteKlasse.setKomplex4(komplex4);
		dritteKlasse.setKomplexes(komplexes);
	}


	private void initPersonWithDefaultValues(Person person) {
		List<Box> boxes = new ArrayList<>();
		Key key = new Key();
		String name = "";
		person.setBoxes(boxes);
		person.setKey(key);
		person.setName(name);
	}


	private void initBoxWithDefaultValues(Box box) {
		int boxId = 0;
		Inhalt content = new Inhalt();
		boolean hasGames = false;
		box.setBoxId(boxId);
		box.setContent(content);
		List<Dinge> content2 = new ArrayList<>();
		content.setContent(content2);
		box.setHasGames(hasGames);
	}


}