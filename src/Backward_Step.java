import org.AMTL_Matrix.*;

import org.AMTL_Matrix.Norms.*;


public class Backward_Step {
	
	private double StepSize;
	private double Lambda;
	
	public Backward_Step(double StepSize, double Lambda){
	
		this.StepSize = StepSize;
		this.Lambda = Lambda;
	}
	
	public AMTL_Matrix  Prox_Nuclear(AMTL_Matrix Input){
		
		double threshold = StepSize*Lambda;
		
		Norms.SingularValueThresholding(Input, threshold);
		
		return Input;
	}


}
