import java.io.*;

//import org.ejml.ops.RandomMatrices;
//import java.util.Random;

import org.AMTL_Matrix.*;

class ClientMessage implements Serializable{
	
	private int error;
	private int dim;
	private int Blas;

	private AMTL_Matrix A;


	// Constructor
	public ClientMessage(int dim, int Blas){
		this.dim = dim;
		this.error = 0;
		this.Blas = Blas;
		
		this.A = new AMTL_Matrix(this.dim, 1, this.Blas);//RandomMatrices.createRandom(this.ROW, (int)1, new Random());

	}
	
	void setError(int errorCode){
		error = errorCode;
	}

	int getError(){
		return error;
	}


	AMTL_Matrix getVec(){
		return A;
	}

	public void copyVec(AMTL_Matrix a) {
		// TODO Auto-generated method stub
		for(int i = 0; i < dim; i++){
			A.setDouble(i, 0, a.getDouble(i,0));
		}
	}

}