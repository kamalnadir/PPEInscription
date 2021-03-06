package exceptions;

public class ExceptionAjoutEquipeCompetition extends ExceptionPrincipale 
{

	private String erreur;
	public ExceptionAjoutEquipeCompetition(String erreur)
	{
		this.erreur = erreur;
	}
	
	
	public String toString()
	{
		String message ="";
		if(erreur == "equipe")
			message =  "Vous ne pouvez inscrire une équipe dans une compétition réservée aux personnes";
		else if (erreur == "ouvert")
			message =  "les inscriptions sont closes";
		else if (erreur == "joueur")
			message = "Un joueur de cette équipe participe déjà à cette compétition avec une autre équipe";
		else
			message = "Vous ne pouvez inscrire une équipe ne comportant pas de membres !";
		return message;
	}
}
