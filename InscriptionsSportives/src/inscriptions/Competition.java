package inscriptions;

import java.io.Serializable;
import java.util.Collections;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;


import dialogueUtilisateur.Utilitaires;
import exceptions.ExceptionAjoutEquipeCompetition;
import exceptions.ExceptionAjoutPersonneCompetition;
import exceptions.ExceptionCompetition;
import exceptions.ExceptionDateCompetition;

/**
 * Représente une compétition, c'est-à-dire un ensemble de candidats 
 * inscrits à un événement, les inscriptions sont closes à la date dateCloture.
 *
 */

public class Competition implements Comparable<Competition>, Serializable
{
	private static final long serialVersionUID = -2882150118573759729L;
	private Inscriptions inscriptions;
	private String nom;
	private int id;
	private Set<Candidat> candidats;
	private LocalDate dateCloture;
	private boolean enEquipe = false;

	Competition(Inscriptions inscriptions, String nom, LocalDate dateCloture, boolean enEquipe,int id)
	{
		this.enEquipe = enEquipe;
		this.inscriptions = inscriptions;
		this.nom = nom;
		this.dateCloture = dateCloture;
		candidats = new TreeSet<>();
		this.setId(id);
	}
	
	/**
	 * Retourne le nom de la compétition.
	 * @return
	 */
	
	public String getNom()
	{
		return nom;
	}
	
	/**
	 * Permet de changer le nom de la compétition.
	 * @param nom
	 * @throws ExceptionCompetition 
	 */
	public void setNom(String nom) throws ExceptionCompetition{
		if (inscriptions.persistance == inscriptions.BDD)
			inscriptions.pers.updateNomCompetition(this,nom);
		this.nom = nom;
	}
	
	/**
	 * Retourne vrai si les inscriptions sont encore ouvertes, 
	 * faux si les inscriptions sont closes.
	 * @return
	 */
	
	public boolean inscriptionsOuvertes()
	{
		// TODO retourner vrai si et seulement si la date système est antérieure à la date de clôture.
		return (LocalDate.now().isEqual(this.dateCloture) || LocalDate.now().isBefore(this.dateCloture));
	}
	
	/**
	 * Retourne la date de cloture des inscriptions.
	 * @return
	 */
	
	public LocalDate getDateCloture()
	{
		return dateCloture;
	}
	
	/**
	 * Est vrai si et seulement si les inscriptions sont réservées aux équipes.
	 * @return
	 */
	
	public boolean estEnEquipe()
	{
		return enEquipe;
	}
	
	/**
	 * Permet de changer le boolean correspondant au fait que la compétition autorise les équipes ou non.
	 * @param enEquipe
	 * @throws ExceptionCompetition 
	 */
	public void setEstEnEquipe(boolean enEquipe) throws ExceptionCompetition
	{
		if (inscriptions.persistance == inscriptions.BDD)
			inscriptions.pers.updateCompetitionBoolean(enEquipe,getId());
		this.enEquipe = enEquipe;
		
	}
	
	/**
	 * Modifie la date de cloture des inscriptions. Il est possible de la reculer 
	 * mais pas de l'avancer.
	 * @param dateCloture
	 * @throws ExceptionDateCompetition 
	 */
	
	public void setDateCloture(LocalDate dateCloture) throws ExceptionDateCompetition
	{
		// TODO vérifier que l'on avance pas la date.
		if(this.getDateCloture().isBefore(dateCloture))
		{
			if (inscriptions.persistance == inscriptions.BDD)
				inscriptions.pers.updateDateCompetition(dateCloture,getId());
			this.dateCloture = dateCloture;
		}
		else
		{
			throw new ExceptionDateCompetition(this.getDateCloture());
		}
			
	}
	
	/**
	 * Retourne l'ensemble des candidats inscrits.
	 * @return
	 */
	
	public Set<Candidat> getCandidats()
	{
		return Collections.unmodifiableSet(candidats);
	}
	
	/**
	 * Inscrit un candidat de type Personne à la compétition. Provoque une
	 * exception si la compétition est réservée aux équipes ou que les 
	 * inscriptions sont closes.
	 * @param personne
	 * @return
	 * @throws ExceptionAjoutPersonneCompetition 
	 */
	
	public boolean add(Personne personne) throws ExceptionAjoutPersonneCompetition
	{
		// TODO vérifier que la date de clôture n'est pas passée
		
		if ((enEquipe) && !inscriptions.pers.getInitialisation())
			throw new ExceptionAjoutPersonneCompetition("equipe");
		if(!inscriptionsOuvertes() && !inscriptions.pers.getInitialisation())
			throw new ExceptionAjoutPersonneCompetition("inscriptions");
		personne.add(this);
		return candidats.add(personne);
	}

	/**
	 * Inscrit un candidat de type Equipe à la compétition. Provoque une
	 * exception si la compétition est réservée aux personnes ou que 
	 * les inscriptions sont closes.
	 * Mais également si l'équipe comporte un joueur participant déjà avec une autre équipe
	 * @param personne
	 * @return
	 * @throws ExceptionAjoutEquipeCompetition 
	 */

	public boolean add(Equipe equipe) throws ExceptionAjoutEquipeCompetition
	{
		// TODO vérifier que la date de clôture n'est pas passée
		if ((!enEquipe) && !inscriptions.pers.getInitialisation())
			throw new ExceptionAjoutEquipeCompetition("equipe");
		if(!inscriptionsOuvertes() && !inscriptions.pers.getInitialisation())
			throw new ExceptionAjoutEquipeCompetition("ouvert");
		if(equipe.getMembres().isEmpty())
			throw new ExceptionAjoutEquipeCompetition("vide");
		
		
		for(Personne p : equipe.getMembres())
			for(Candidat e : this.getCandidats())
				if(e instanceof Equipe)
					for(Personne p2: ((Equipe) e).getMembres())
						if(p2.getMail() == p.getMail())
							throw new ExceptionAjoutEquipeCompetition("joueur");
					
					
				
				
		
		equipe.add(this);
		return candidats.add(equipe);
	}

	/**
	 * Désinscrit un candidat.
	 * @param candidat
	 * @return
	 */
	
	public boolean remove(Candidat candidat)
	{
		
		candidat.remove(this);
		return candidats.remove(candidat);
	}
	
	/**
	 * Retire de la compétition tous les candidats inscris
	 * @return
	 */
	public void removeAllCandidats()
	{
		Set<Candidat> aEnlever = new TreeSet<>();
		for (Candidat candidat : candidats)
		{
			aEnlever.add(candidat);
		}
		for(Candidat c : aEnlever)
		{
			c.remove(this);
			candidats.remove(c);
			if (inscriptions.persistance == inscriptions.BDD)
				inscriptions.pers.retirerCandidatCompetition(c, this);
		}
			
	}
	
	
	
	/**
	 * Supprime la compétition de l'application.
	 */
	
	public void delete()
	{
		removeAllCandidats();
		inscriptions.remove(this);
		if (inscriptions.persistance == inscriptions.BDD)
			inscriptions.pers.retirerCompetition(this.getId());
	}
	
	@Override
	public int compareTo(Competition o)
	{
		return getNom().compareTo(o.getNom());
	}
	
	@Override
	public String toString()
	{
		return Utilitaires.getMajuscule(getNom());
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
