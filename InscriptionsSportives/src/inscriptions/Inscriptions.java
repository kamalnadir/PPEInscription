package inscriptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

import dialogueUtilisateur.Main;
import donnees.persistance;
import exceptions.ExceptionMailPersonne;
import exceptions.ExceptionCompetition;
import exceptions.ExceptionNomEquipe;
/**
 * Point d'entrée dans l'application, un seul objet de type Inscription
 * permet de gérer les compétitions, candidats (de type equipe ou personne)
 * ainsi que d'inscrire des candidats à  des compétition.
 */

public class Inscriptions implements Serializable
{
	public static final int SERIALIZATION = 0,
							BDD = 1;
	public static int persistance = Main.choixPersistance;
	public static persistance pers = null;
	
	private static final long serialVersionUID = -3095339436048473524L;
	private static final String FILE_NAME = "Inscriptions.srz";
	private static Inscriptions inscriptions;
	
	private SortedSet<Competition> competitions = new TreeSet<>();
	private SortedSet<Candidat> candidats = new TreeSet<>();

	private Inscriptions()
	{
	}
	
	/**
	 * Retourne les compétitions.
	 * @return
	 */
	
	public SortedSet<Competition> getCompetitions()
	{
		return Collections.unmodifiableSortedSet(competitions);
	}
	
	/**
	 * Retourne tous les candidats (personnes et équipes confondues).
	 * @return
	 */
	
	public Set<Candidat> getCandidats()
	{
		return Collections.unmodifiableSortedSet(candidats);
	}

	/**
	 * Créée une compétition. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Competition}.
	 * @param nom
	 * @param dateCloture
	 * @param enEquipe
	 * @return
	 * @throws ExceptionNomCompetition 
	 */
	
	public Competition createCompetition(String nom, 
			LocalDate dateCloture, boolean enEquipe,int id) throws ExceptionCompetition
	{
		if(LocalDate.now().isAfter(dateCloture) && !inscriptions.pers.getInitialisation())
			throw new ExceptionCompetition(nom,"date passee");
		Competition competition = new Competition(this, nom, dateCloture, enEquipe,id);
		if (persistance == BDD)
			pers.ajoutCompetition(nom,dateCloture,enEquipe);
		competitions.add(competition);
		return competition;
	}

	/**
	 * Créée une Candidat de type Personne. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Personne}.

	 * @param nom
	 * @param prenom
	 * @param mail
	 * @return
	 * @throws ExceptionMailPersonne 
	 */
	
	public Personne createPersonne(String nom, String prenom, String mail,int id) throws ExceptionMailPersonne
	{
		if (persistance == BDD)
			pers.ajoutPersonne(nom,prenom,mail);
		Personne personne = new Personne(this, nom, prenom, mail,id);
		candidats.add(personne);
		
		return personne;
	}
	
	/**
	 * Créée une Candidat de type équipe. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Equipe}.
	 * @param nom
	 * @param prenom
	 * @param mail
	 * @return
	 * @throws SQLException 
	 * @throws ExceptionNomEquipe 
	 */
	
	public Equipe createEquipe(String nom,int id) throws  ExceptionNomEquipe
	{
		if (persistance == BDD)
			pers.ajoutEquipe(nom);
		Equipe equipe = new Equipe(this, nom,id);
		candidats.add(equipe);
		return equipe;
	}
	
	void remove(Competition competition)
	{
		competitions.remove(competition);
		if (persistance == BDD)
			pers.retirerCompetition(competition.getId());
	}
	
	void remove(Candidat candidat)
	{
		candidats.remove(candidat);
		if (persistance == BDD)
		{
			if (candidat instanceof Personne)
				pers.retirerPersonne(candidat.getId());
			else
				pers.retirerEquipe(candidat.getId());
		}
	}
	
	/**
	 * Retourne l'unique instance de cette classe.
	 * Crée cet objet s'il n'existe déjà .
	 * @return l'unique objet de type {@link Inscriptions}.
	 */
	
	public static Inscriptions getInscriptions()
	{
		
		if (persistance == BDD)
		{
			pers = new persistance();
			try 
			{
				inscriptions = pers.getBase(new Inscriptions());
				
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		
		}
		else
		{
			if (inscriptions == null)
			{
				inscriptions = readObject();
				if (inscriptions == null)
					inscriptions = new Inscriptions();
			}
			
		}
		return  inscriptions;
		
	}

	/**
	 * Retourne un object inscriptions vide. Ne modifie pas les compétitions
	 * et candidats déjà  existants.
	 */
	
	public Inscriptions reinitialiser()
	{
		inscriptions = new Inscriptions();
		return getInscriptions();
	}

	/**
	 * Efface toutes les modifications sur Inscriptions depuis la dernià¨re sauvegarde.
	 * Ne modifie pas les compétitions et candidats déjà  existants.
	 */
	
	public Inscriptions recharger()
	{
		inscriptions = null;
		return getInscriptions();
	}
	
	private static Inscriptions readObject()
	{
		ObjectInputStream ois = null;
		try
		{
			FileInputStream fis = new FileInputStream(FILE_NAME);
			ois = new ObjectInputStream(fis);
			return (Inscriptions)(ois.readObject());
		}
		catch (IOException | ClassNotFoundException e)
		{
			return null;
		}
		finally
		{
				try
				{
					if (ois != null)
						ois.close();
				} 
				catch (IOException e){}
		}	
	}
	
	/**
	 * Sauvegarde le gestionnaire pour qu'il soit ouvert automatiquement 
	 * lors d'une exécution ultérieure du programme.
	 * @throws IOException 
	 */
	
	public void sauvegarder() throws IOException
	{
		if (persistance == SERIALIZATION)
		{
			ObjectOutputStream oos = null;
			try
			{
				FileOutputStream fis = new FileOutputStream(FILE_NAME);
				oos = new ObjectOutputStream(fis);
				oos.writeObject(this);
			}
			catch (IOException e)
			{
				throw e;
			}
			finally
			{
				try
				{
					if (oos != null)
						oos.close();
				} 
				catch (IOException e){}
			}
		}
		
	}
	
	@Override
	public String toString()
	{
		return "Candidats : " + getCandidats().toString()
			+ "\nCompetitions  " + getCompetitions().toString();
	}
}
