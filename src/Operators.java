import org.AMTL_Matrix.*;

import org.AMTL_Matrix.MatrixOps.*;
import org.AMTL_Matrix.Norms.Norms;


public class Operators {
	
	public double step_size;
	
	public Operators(double step_size){
		this.step_size = step_size;
	}
	
	/* *********************
	    Gradient Operators
	 * *********************/
	
	/* Square Loss
	 * 1/2 ||A*w - b||^2, A is a nxd matrix, b is a nx1 vector and w is a dx1 vector
	   Returns: w - step_size* A' * (A*w - b) */
	
	public AMTL_Matrix SquareLoss_Forward(AMTL_Matrix A, AMTL_Matrix b, AMTL_Matrix w){
		
		AMTL_Matrix A_copy = new AMTL_Matrix(A);
		AMTL_Matrix b_copy = new AMTL_Matrix(b);
		
		AMTL_Matrix grad = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		AMTL_Matrix updated_point = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		AMTL_Matrix Aw = new AMTL_Matrix(b.NumRows, b.NumColumns, b.BlasID);
		AMTL_Matrix Aw_b = new AMTL_Matrix(b.NumRows, b.NumColumns, b.BlasID);
		
		MatrixOps.MULT(A_copy, w, Aw);
		MatrixOps.ReverseSign(b_copy);
		MatrixOps.ADD(Aw, b_copy, Aw_b);
		MatrixOps.Transpose(A_copy);
		MatrixOps.MULT(A_copy, Aw_b, grad);
		
		MatrixOps.Scale(grad, -step_size);
		MatrixOps.ADD(w, grad, updated_point);
		
		
		return updated_point;
	}
	
	/* Quadratic Loss
	 * 1/2 w' * Q * w + c' * w, Q is a dxd matrix, c is a dx1 vector and w is a dx1 vector
	   Returns: w - step_size* (Q * w + c) */
	
	public AMTL_Matrix Quadratic_Forward(AMTL_Matrix Q, AMTL_Matrix c, AMTL_Matrix w){
		
		AMTL_Matrix Q_copy = new AMTL_Matrix(Q);
		
		AMTL_Matrix grad = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		AMTL_Matrix Qw = new AMTL_Matrix(c.NumRows, c.NumColumns, c.BlasID);
		AMTL_Matrix updated_point = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		
		MatrixOps.MULT(Q_copy, w, Qw);
		MatrixOps.ADD(Qw, c, grad);
		
		MatrixOps.Scale(grad, -step_size);
		MatrixOps.ADD(w, grad, updated_point);
		
		return updated_point;
		
	}
	
	/* Log loss
	 * 1/N \sum_{i}^{N} log(1 + exp(- b_i* a_i ' * w)), a_i is a dx1 vector, b is a Nx1 vector and w is a dx1 vector
	   Returns: w - step_size * 1/N \sum_{i}^{N} (-b_i * a_i)/log(1 + exp(- b_i* a_i ' * w))*/
	
	public AMTL_Matrix LogLoss_Forward(AMTL_Matrix A, AMTL_Matrix b, AMTL_Matrix w){
		
		AMTL_Matrix A_copy = new AMTL_Matrix(A);
		AMTL_Matrix b_copy = new AMTL_Matrix(b);
		
		AMTL_Matrix updated_point = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		
		AMTL_Matrix vector = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		AMTL_Matrix obj_result = new AMTL_Matrix(1,1,w.BlasID);
		double product = 0;
		
		MatrixOps.ReverseSign(b_copy);
		AMTL_Matrix sum = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		double log_part;
		int[] rows = new int[]{0};
		int[] columns = new int[A_copy.NumColumns];
		for(int i = 0; i<A_copy.NumColumns; i++){
			columns[i] = i;
		}
		for(int i = 0; i<A_copy.NumRows; i++){
			rows[0] = i;
			vector = A_copy.getSubMatrix(rows, columns);
			MatrixOps.Transpose(vector);
			MatrixOps.MULT(vector, w, obj_result);
			
			product = obj_result.getDouble(0, 0);
						
			log_part = Math.log(1 + Math.exp(b_copy.getDouble(i, 0) * product));
		
			MatrixOps.Scale(vector, b_copy.getDouble(i, 0)); 
			MatrixOps.Scale(vector, (1/log_part));
			MatrixOps.Transpose(vector);
			MatrixOps.ADD(sum, vector, sum);
		
		}
		
		double R = A_copy.NumRows;
		
		MatrixOps.Scale(sum, (1 / R));
		MatrixOps.Scale(sum, -step_size);
		MatrixOps.ADD(w, sum, updated_point);
		
		return updated_point;
		
	}
	
	/* Hinge loss
	 * \sum_{i}^{N} max(0,(1- b_i* a_i ' * w)), a_i is a dx1 vector, b is a Nx1 vector and w is a dx1 vector
	  */

	public AMTL_Matrix HingeLoss_Forward(AMTL_Matrix A, AMTL_Matrix b, AMTL_Matrix w){
		
		AMTL_Matrix A_copy = new AMTL_Matrix(A);
		AMTL_Matrix b_copy = new AMTL_Matrix(b);
		
		AMTL_Matrix updated_point = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		
		AMTL_Matrix vector = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
		AMTL_Matrix obj_result = new AMTL_Matrix(1,1,w.BlasID);
		double product = 0;
		
		MatrixOps.ReverseSign(b_copy);
		AMTL_Matrix sum = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);

		int[] rows = new int[]{0};
		int[] columns = new int[A_copy.NumColumns];
		for(int i = 0; i<A_copy.NumColumns; i++){
			columns[i] = i;
		}
		for(int i = 0; i<A_copy.NumRows; i++){
			rows[0] = i;
			vector = A_copy.getSubMatrix(rows, columns);
			MatrixOps.Transpose(vector);
			MatrixOps.MULT(vector, w, obj_result);
			product = b_copy.getDouble(i, 0) * obj_result.getDouble(0, 0);;
			
			if((1 + product) > 0){
				MatrixOps.Transpose(vector);
				MatrixOps.Scale(vector, b_copy.getDouble(i, 0));
				MatrixOps.ADD(sum, vector, sum);
			} else{
				AMTL_Matrix Zeros = new AMTL_Matrix(w.NumRows, w.NumColumns, w.BlasID);
				MatrixOps.ADD(sum, Zeros, sum);
			}
			
		}
		
		double R = A_copy.NumRows;
		
		MatrixOps.Scale(sum, (1 / R));
		MatrixOps.Scale(sum, -step_size);
		MatrixOps.ADD(w, sum, updated_point);
		
		return updated_point;
	}
	
	/* *********************
    Proximal Operators
 * *********************/
	
	/* Proximal operator of Trace norm ||W||_{*}
	 * Singular value thresholding, Input is a dxn model matrix where d is the dimension of the model
	 * and n is the number of tasks in multi-task learning setting.
	   Returns: U(S - (step_size * Lambda)*I)V' */
	
	public AMTL_Matrix  Prox_Trace(AMTL_Matrix Input, double Lambda){
		
		double threshold = step_size*Lambda;
		
		Norms.SingularValueThresholding(Input, threshold);
		
		return Input;
	}
	
	/* Proximal operator of l1 norm ||w||_{1}
	 * Soft thresholding, Input is a dx1 model vector.
	   Returns: [prox(x)]_{i} = sign(x_i)max(|x_i| - (step_size * Lambda), 0) */
	
	public AMTL_Matrix Prox_l1(AMTL_Matrix Input, double Lambda){
		
		double val, x;
		for(int i = 0; i<Input.NumRows; i++){
			x = Input.getDouble(i, 0);
			val = Math.abs(x) - Lambda * step_size;
			if(val > 0){
				Input.setDouble(i, 0, ((x / Math.abs(x))*val));
			} else{
				Input.setDouble(i, 0, 0);
			}
		}
		
		return Input;
	}
	
	/* Proximal operator of l2 norm square 
	 * R(x) = Lambda/2 ||w||_{2}^{2}
	 * Returns: prox(x) = x / (1 + (step_size * Lambda) ) */
	
	public AMTL_Matrix Prox_l2_square(AMTL_Matrix Input, double Lambda){
		
		MatrixOps.Scale(Input, (1 / (1 + step_size * Lambda)));
		
		return Input;
	}
	
	/* Proximal operator of l2 norm  
	 * R(x) = Lambda ||w||_{2}, block soft thresholding
	 * Returns: prox(x) = w * (1 - (step_size * Lambda)/||w||_{2})_{+}*/
	
	public AMTL_Matrix Prox_l2(AMTL_Matrix Input, double Lambda){
		
		double val = Norms.L2_Norm(Input);
		double T = step_size*Lambda;
		
		if(val >= T){
			MatrixOps.Scale(Input, (1 - T/val));
		} else {
			MatrixOps.Scale(Input, 0);
		}
		
		return Input;
		
	}
	
	/* Proximal operator of elastic net 
	 * R(x) = Lambda1 ||w||_{1} + Lambda2/2 ||w||_{2}^{2}
	 * Returns: prox(x) = 1 / (1 + (step_size * Lambda2)) * prox_{l1,Lambda1} (w)*/
	
	public AMTL_Matrix Prox_ElasticNet(AMTL_Matrix Input, double Lambda1, double Lambda2){
		
		AMTL_Matrix prox = this.Prox_l1(Input, Lambda1);
		
		double val = 1 / (1 + (step_size * Lambda2));
		MatrixOps.Scale(prox, val);
		
		return prox;
	}
	

}
