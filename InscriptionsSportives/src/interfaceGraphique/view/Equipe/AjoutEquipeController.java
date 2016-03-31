package interfaceGraphique.view.Equipe;

import java.io.IOException;


import exceptions.ExceptionNomEquipe;
import interfaceGraphique.controls.MonAppli;
import interfaceGraphique.controls.Equipe.AjoutEquipe;
import interfaceGraphique.controls.Equipe.GestionEquipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AjoutEquipeController {
	
	@FXML
	private TextField newName;
	@FXML
	private Label messageErreur;
	private AjoutEquipe stageAjout;
	private GestionEquipe stageGestion;
	
	public AjoutEquipeController(){
		
	}
	
	@FXML
	private void initialize(){
		messageErreur(null);
	}
	
	public void setStage(AjoutEquipe stageAjout, GestionEquipe stageGestion){
		this.stageAjout = stageAjout;
		this.stageGestion = stageGestion;
	}
	
	public void actionClose(){
		stageAjout.close();
	}
	
	public void actionAjout(){
		if(!newName.getText().isEmpty())
		{
			try 
			{
				stageGestion.getList().add(MonAppli.getInscriptions().createEquipe(newName.getText().toLowerCase()));
				stageAjout.close();
			} 
			catch (ExceptionNomEquipe e1) 
			{
				messageErreur(e1.toString());
			}
		
		try
		{
			MonAppli.getInscriptions().sauvegarder();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		else
		{
			messageErreur("Impossible de rentrer un champs vide");
		}
			
	}
	
	public void messageErreur(Object o){
    	if(o == null){
    		messageErreur.setVisible(false);
    	}
    	else{
    		messageErreur.setVisible(true);
    		messageErreur.setText(o.toString());
    	}
    }
}
