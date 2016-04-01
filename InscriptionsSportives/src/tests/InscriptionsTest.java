package tests;


import java.time.LocalDate;

import org.junit.Test;

import exceptions.ExceptionCompetition;
import exceptions.ExceptionMailPersonne;
import exceptions.ExceptionNomEquipe;
import inscriptions.Competition;
import inscriptions.Equipe;
import inscriptions.Inscriptions;
import inscriptions.Personne;
import junit.framework.TestCase;

public class InscriptionsTest extends TestCase {

	private Inscriptions inscriptions = Inscriptions.getInscriptions();
	Personne tony = null;
	Competition flechettes = null;
	Equipe lesManouches = null;
	
	public void setUp() throws Exception
	{
		tony = inscriptions.createPersonne("Tony", "Dent de plomb", "azerty");
		flechettes = inscriptions.createCompetition("flechette", LocalDate.now(), true);
		lesManouches = inscriptions.createEquipe("Les Manouches");
	}

	@Test
	public void testGetCompetitions() {
		assertEquals(true, inscriptions.getCompetitions().contains(flechettes));
	}

	@Test
	public void testGetCandidats() {
		assertEquals(true, inscriptions.getCandidats().contains(tony));
		assertEquals(true, inscriptions.getCandidats().contains(lesManouches));
	}

	@Test
	public void testCreateCompetition() throws ExceptionCompetition {
		Competition billard = inscriptions.createCompetition("billard", LocalDate.now(), true);
		assertEquals(true, inscriptions.getCompetitions().contains(billard));
	}

	@Test
	public void testCreatePersonne() throws ExceptionMailPersonne {
		Personne robert = inscriptions.createPersonne("Robert", "prenom", "lala");
		assertEquals(true, inscriptions.getCandidats().contains(robert));
	}

	@Test
	public void testCreateEquipe() throws ExceptionNomEquipe {
		Equipe lesGorets = inscriptions.createEquipe("Les gorets");
		assertEquals(true, inscriptions.getCandidats().contains(lesGorets));
	}
}
