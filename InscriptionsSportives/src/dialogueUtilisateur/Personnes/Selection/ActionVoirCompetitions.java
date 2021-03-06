package dialogueUtilisateur.Personnes.Selection;

import inscriptions.Competition;
import utilitaires.ligneDeCommande.Action;

/**
 * Construit l'action permettant de voir tous les membres de l'équipe.
 * @author Jean
 *
 */
public class ActionVoirCompetitions implements Action{
	
	private MenuSelectionPersonnes menu;

	public ActionVoirCompetitions(MenuSelectionPersonnes menu){
		this.menu = menu;
	}
	
	public void optionSelectionnee() {
		if(menu.getPersonne().getCompetitions().isEmpty()){
			System.out.println("\nPersonne inscrite à aucune compétition.");
		}
		else{
			int i=1;
			System.out.println("\nLes compétitions auxquelles est inscrite la personne sont : \n");
			for(Competition c : menu.getPersonne().getCompetitions()){
				System.out.println(i + ": " + c.toString());
				i++;
			}
		}
	}

}
