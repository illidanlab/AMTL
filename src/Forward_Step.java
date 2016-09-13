import org.AMTL_Matrix.*;

import org.AMTL_Matrix.MatrixOps.*;


public class Forward_Step {
	
	private AMTL_Matrix A;
	private AMTL_Matrix b;
	
	private double StepSize;
	
	// Constructor
	public Forward_Step(AMTL_Matrix A, AMTL_Matrix b, double StepSize){
		
		this.A = new AMTL_Matrix(A);
		this.b = new AMTL_Matrix(b);
		
		this.StepSize = StepSize;
		
	}
	
	public AMTL_Matrix Gradient_Update(AMTL_Matrix Input){
		
		AMTL_Matrix Intermediate_point = new AMTL_Matrix(Input.NumRows, Input.NumColumns,Input.BlasID);
		
		AMTL_Matrix grad = new AMTL_Matrix(Input.NumRows, Input.NumColumns,Input.BlasID);
		
		
		AMTL_Matrix c1 = new AMTL_Matrix(b.NumRows, b.NumColumns,b.BlasID);
		AMTL_Matrix c2 = new AMTL_Matrix(b.NumRows, b.NumColumns,b.BlasID);
		
		MatrixOps.MULT(A, Input, c1);
		
		MatrixOps.ReverseSign(b);
		
		MatrixOps.ADD(c1, b, c2);
		
		MatrixOps.Transpose(A);
		
		MatrixOps.MULT(A, c2, grad);
		
		MatrixOps.Scale(grad,StepSize);
		
		MatrixOps.ReverseSign(grad);
		
		MatrixOps.ADD(Input, grad, Intermediate_point);
		
		return Intermediate_point;
		
	}	
	

}
