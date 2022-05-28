# na-an-tg

###Namens-(na) und Annotationen (an) -gestütze Test-Generierung (tg)

Mithilfe der Namensgebung der Methoden und verschiedener Annotationen 
lassen sich Anweisungen zur Generierung der Testfälle angeben.

Im "main"-Ordner des Projekts befindet sich die Klasse "TGRunner" 
und in dieser die Einstiegsmethode "run()" zur Generierung der Tests.
Wenn diese Methode aufgerufen wird, wird in den Interfaces mit der Endung "TG"
nach Methoden gescannt, die mit "@TG" annotiert sind. Methoden ohne 
"@TG" werden ignoriert.

Das "testproject" dient zur Veranschaulichung und zum Test der möglichen
Generierungs-Möglichkeiten. In dem Test-Ordner des "testproject" 
befinden sich eine Reihe Interfaces mit der Endung "TG", deren Namen mit der
zu testenden Klasse korrespondieren.

Die Testklassen werden nach Anstoß der Generierung durch den "TGRunner" 
in den Ordner "testGen" generiert.
Dieser Ordner und seine Testklassen können nach belieben gelöscht werden, 
da sie bei jeder Aktivierung des "TGRunners" neu erstellt werden.

Welche Generierungen bereits möglich sind, ist den jeweiligen Interfaces
in "testproject.test" zu entnehmen.
Hier ist eine Übersicht der aktuellen Interfaces:

- ErsteKlasseTG: testet Methoden ohne Parameter mit primitiven Rückgabewerten,

- ZweiteKlasseTG: testet Methoden ohne Parameter mit primitiven Rückgabewerten mittels
weiterer Möglichkeiten

- InnereKlasseTG: testet drei verschiedene Möglichkeiten für die Generierungsanweisung 
einer Assertion für einen boolean-Rückgabewert

- NochEineKlasseTG: testet Methoden mit primitiven Parametern

- KlasseMitInnererKlasseTG: testet Methoden mit Collections, Listen oder Arrays
als Parameter oder Rückgabewert

- InhaltTG: testet Methoden mit Enums als Parameter oder Rückgabewert

- DritteKlasseTG: testet Methoden mit Objekten als Parameter oder Rückgabewert und
der Möglichkeit einer rekursiven Initialisierung aller Felder

- HauptscheinTG: testet erweiterte Assertions und die Generierung zusätzlicher 
Annotatonen für die generierten Tests

- NoNameTG: testet die Wahl eines beliebigen Namens für die TG-Klasse

- ScheinServiceTG: testet eine Service-Klasse mit Mocking

Zur externen Nutzung muss über Maven die folgende Dependency
eingebunden werden:

	<dependency>
	    <groupId>de.tudo.naantg</groupId>
	    <artifactId>naantg</artifactId>
	    <version>1.0-SNAPSHOT</version>
	</dependency>

 
