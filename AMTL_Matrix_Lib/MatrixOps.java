package org.AMTL_Matrix.MatrixOps;

import java.math.BigDecimal;

import org.AMTL_Matrix.*;

import org.ejml.ops.CommonOps;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.MatrixFeatures;

import org.ejml.data.*;

import org.ujmp.core.*;

import org.jblas.DoubleMatrix;
import org.jblas.Singular;




public class MatrixOps {

	public static void ADD(AMTL_Matrix obj1, AMTL_Matrix obj2, AMTL_Matrix obj_result){

		if(obj1.BlasID == obj2.BlasID){
			if(obj1.BlasID == 0){
				CommonOps.add((DenseMatrix64F) obj1.M, (DenseMatrix64F) obj2.M, (DenseMatrix64F) obj_result.M);
			} else if(obj1.BlasID == 1){
				obj_result.M = ((Matrix) obj1.M).plus((Matrix) obj2.M);  
			} else if(obj1.BlasID == 2){
				obj_result.M = ((Jama.Matrix) obj1.M).plus((Jama.Matrix) obj2.M);
			} else if(obj1.BlasID == 3){
				obj_result.M = ((DoubleMatrix) obj1.M).add((DoubleMatrix) obj2.M);
			} else{
				System.out.println("Unsupported BLAS library! Permission denied.");
				System.exit(0);
			}
		} else{
			System.out.println("Error: Blas IDs of two matrices should be same.");
			System.exit(0);
		}
		
	}
	
	
	public static void ReverseSign(AMTL_Matrix obj){
		if(obj.BlasID == 0){
			CommonOps.changeSign((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			obj.M = ((Matrix) obj.M).times(-1.0);
		} else if(obj.BlasID == 2){
			obj.M = ((Jama.Matrix) obj.M).times(-1.0);
		} else if(obj.BlasID == 3){
			obj.M = ((DoubleMatrix) obj.M).neg();
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}
	}
	
	public static void Scale(AMTL_Matrix obj, double val){
		if(obj.BlasID == 0){
			CommonOps.scale(val, (DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			obj.M = ((Matrix) obj.M).times(val);
		} else if(obj.BlasID == 2){
			obj.M = ((Jama.Matrix) obj.M).times(val);
		} else if(obj.BlasID == 3){
			obj.M = ((DoubleMatrix) obj.M).mul(val);
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}
	}
	
	public static void MULT(AMTL_Matrix obj1, AMTL_Matrix obj2, AMTL_Matrix obj_result){
		
		if(obj1.BlasID == obj2.BlasID){

			if(obj1.BlasID == 0){
				CommonOps.mult((DenseMatrix64F) obj1.M, (DenseMatrix64F) obj2.M, (DenseMatrix64F) obj_result.M);
			} else if(obj1.BlasID == 1){
				obj_result.M = ((Matrix) obj1.M).mtimes(((Matrix) obj2.M));
			} else if(obj1.BlasID == 2){
				obj_result.M = ((Jama.Matrix) obj1.M).times((Jama.Matrix) obj2.M);
			} else if(obj1.BlasID == 3){
				obj_result.M = ((DoubleMatrix) obj1.M).mmul((DoubleMatrix) obj2.M);
			} else{
				System.out.println("Unsupported BLAS library! Permission denied.");
				System.exit(0);
			}

		} else{
			System.out.println("Error: Blas IDs of two matrices should be same.");
			System.exit(0);
		}
	}
	
	public static void Transpose(AMTL_Matrix obj){
		
		int temp = obj.NumColumns;
		obj.NumColumns = obj.NumRows;
		obj.NumRows = temp;
		
		if(obj.BlasID == 0){
			CommonOps.transpose((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			obj.M = ((Matrix) obj.M).transpose();
		} else if(obj.BlasID == 2){
			obj.M = ((Jama.Matrix) obj.M).transpose();
		} else if(obj.BlasID == 3){
			obj.M = ((DoubleMatrix) obj.M).transpose();
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}
	}
	
	public static int getRank(AMTL_Matrix obj){
		
		int Rank = 0;
		
		if(obj.BlasID == 0){
			Rank = MatrixFeatures.rank((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			Rank = ((Matrix) obj.M).rank();
		} else if(obj.BlasID == 2){
			Rank = ((Jama.Matrix) obj.M).rank();
		} else{
			DoubleMatrix[] fullSVD = Singular.fullSVD((DoubleMatrix) obj.M);
			DoubleMatrix singularValuesDM = fullSVD[1];
		
			for(int i = 0; i<singularValuesDM.length; i++){
				
				double val = java.lang.Math.exp(singularValuesDM.get(i));
				BigDecimal value = new BigDecimal(val).setScale(5, BigDecimal.ROUND_HALF_UP);
				BigDecimal thresh = new BigDecimal(1.0000);
				if(value.compareTo(thresh) > 0){
					Rank++;
				} 
			}
		}
		
		return Rank;
	}
	
	public static void SVD(AMTL_Matrix obj,AMTL_Matrix obj_U,AMTL_Matrix obj_V,AMTL_Matrix obj_S){
		
		if(obj.BlasID == 0){
			
			SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(((DenseMatrix64F) obj.M).numRows, ((DenseMatrix64F) obj.M).numCols,true,true,false);
			
			svd.decompose(((DenseMatrix64F) obj.M));
			
			DenseMatrix64F U = svd.getU(null,false);
			DenseMatrix64F S = svd.getW(null); // diagonal matrix
			DenseMatrix64F V = svd.getV(null,false);
			
			obj_U.M = new DenseMatrix64F(U);
			obj_V.M = new DenseMatrix64F(V);
			obj_S.M = new DenseMatrix64F(S);
			
			obj_U.NumColumns = U.numCols;
			obj_U.NumRows = U.numRows;
			
			obj_V.NumColumns = V.numCols;
			obj_V.NumRows = V.numRows;
			
			obj_S.NumColumns = S.numCols;
			obj_S.NumRows = S.numRows;
			
			
			/*((DenseMatrix64F) obj_U.M).data = U.getData();
			((DenseMatrix64F) obj_S.M).data = S.getData();
			((DenseMatrix64F) obj_V.M).data = V.getData();*/
			
		} else if(obj.BlasID == 1){
			
			
			Matrix[] svd = ((Matrix) obj.M).svd();
			Matrix U = svd[0];
			Matrix S = svd[1];
			Matrix V = svd[2];
			
			
			obj_U.M = DenseMatrix.Factory.copyFromMatrix(U);
			obj_V.M = DenseMatrix.Factory.copyFromMatrix(V);
			obj_S.M = DenseMatrix.Factory.copyFromMatrix(S);
			
			obj_U.NumColumns = (int) U.getColumnCount();
			obj_U.NumRows = (int) U.getRowCount();
			
			obj_V.NumColumns = (int) V.getColumnCount();
			obj_V.NumRows = (int) V.getRowCount();
			
			obj_S.NumColumns = (int) S.getColumnCount();
			obj_S.NumRows = (int) S.getRowCount();
			
			/*for(int i = 0; i<obj.NumRows; i++){
				for(int j = 0; j<obj.NumRows; j++){
					((Matrix) obj_U.M).setAsDouble(U.getAsDouble(i,j), i,j);
				}
			}
			
			for(int i = 0; i<obj.NumRows; i++){
				for(int j = 0; j<obj.NumColumns; j++){
					((Matrix) obj_S.M).setAsDouble(S.getAsDouble(i,j), i,j);
				}
			}
			
			for(int i = 0; i<obj.NumColumns; i++){
				for(int j = 0; j<obj.NumColumns; j++){
					((Matrix) obj_V.M).setAsDouble(V.getAsDouble(i,j), i,j);
				}
			}*/
			
		} else if(obj.BlasID == 2){
			
			Jama.SingularValueDecomposition s = ((Jama.Matrix) obj.M).svd();
			Jama.Matrix U = s.getU();
			Jama.Matrix S = s.getS();
			Jama.Matrix V = s.getV();
			
			obj_U.M = U.copy();
			obj_S.M = S.copy();
			obj_V.M = V.copy();
			
			obj_U.NumColumns = U.getColumnDimension();
			obj_U.NumRows = U.getRowDimension();
			
			obj_V.NumColumns = V.getColumnDimension();
			obj_V.NumRows = V.getRowDimension();
			
			obj_S.NumColumns = S.getColumnDimension();
			obj_S.NumRows = S.getRowDimension();
			
			/*for(int i = 0; i<obj.NumRows; i++){
				for(int j = 0; j<obj.NumRows; j++){
					((Jama.Matrix) obj_U.M).set(i,j,U.get(i,j));
				}
			}
			
			for(int i = 0; i<obj.NumRows; i++){
				for(int j = 0; j<obj.NumColumns; j++){
					((Jama.Matrix) obj_S.M).set(i,j,S.get(i,j));
				}
			}
			
			for(int i = 0; i<obj.NumColumns; i++){
				for(int j = 0; j<obj.NumColumns; j++){
					((Jama.Matrix) obj_V.M).set(i,j,V.get(i,j));
				}
			}*/
			
			
		} else if(obj.BlasID == 3){
			DoubleMatrix[] svd = Singular.fullSVD((DoubleMatrix) obj.M);
			DoubleMatrix U = svd[0];
			DoubleMatrix Sing = svd[1];
			DoubleMatrix V = svd[2];
			
			DoubleMatrix S = new DoubleMatrix(U.columns,V.rows);
			
			for(int i = 0; i<Sing.length; i++){
				S.put(i,i,Sing.get(i));
			}
			
			((DoubleMatrix) obj_U.M).copy(U);
			((DoubleMatrix) obj_V.M).copy(V);
			((DoubleMatrix) obj_S.M).copy(S);
			
			obj_U.NumColumns = U.columns;
			obj_U.NumRows = U.rows;
			
			obj_V.NumColumns = V.columns;
			obj_V.NumRows = V.rows;
			
			obj_S.NumColumns = S.columns;
			obj_S.NumRows = S.rows;
			
			/*for(int i = 0; i<obj.NumRows; i++){
				for(int j = 0; j<obj.NumRows; j++){
					((DoubleMatrix) obj_U.M).put(i,j,U.get(i,j));
				}
			}
			
			for(int i = 0; i<S.length; i++){
				((DoubleMatrix) obj_S.M).put(i,S.get(i));
			}
			
			for(int i = 0; i<obj.NumColumns; i++){
				for(int j = 0; j<obj.NumColumns; j++){
					((DoubleMatrix) obj_V.M).put(i,j,V.get(i,j));
				}
			}*/
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}		
	}

	

}
