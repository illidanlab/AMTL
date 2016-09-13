package org.AMTL_Matrix.Norms;

import org.AMTL_Matrix.*;
import org.AMTL_Matrix.MatrixOps.*;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.NormOps;

import org.ujmp.core.*;

import org.jblas.DoubleMatrix;



public class Norms {

	public static double L2_Norm(AMTL_Matrix obj){

		double norm = 0;

		if(obj.BlasID == 0){
			norm = NormOps.normP2((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			norm = ((Matrix) obj.M).norm2();
		} else if(obj.BlasID == 2){
			norm = ((Jama.Matrix) obj.M).norm2();
		} else if(obj.BlasID == 3){
			norm = ((DoubleMatrix) obj.M).norm2();
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}

		return norm;
	}

	public static double L1_Norm(AMTL_Matrix obj){

		double norm = 0;

		if(obj.BlasID == 0){
			norm = NormOps.normP1((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			norm = ((Matrix) obj.M).norm1();
		} else if(obj.BlasID == 2){
			norm = ((Jama.Matrix) obj.M).norm1();
		} else if(obj.BlasID == 3){
			norm = ((DoubleMatrix) obj.M).norm1();
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}

		return norm;
	}

	public static double Frobenius_Norm(AMTL_Matrix obj){

		double norm = 0;

		if(obj.BlasID == 0){
			norm = NormOps.normF((DenseMatrix64F) obj.M);
		} else if(obj.BlasID == 1){
			norm = ((Matrix) obj.M).normF();
		} else if(obj.BlasID == 2){
			norm = ((Jama.Matrix) obj.M).normF();
		} else if(obj.BlasID == 3){
			norm = ((DoubleMatrix) obj.M).norm2();
		} else{
			System.out.println("Unsupported BLAS library! Permission denied.");
			System.exit(0);
		}

		return norm;
	}

	public static double Trace_Norm(AMTL_Matrix obj){

		double norm = 0;

		AMTL_Matrix obj_U = new AMTL_Matrix(obj.NumRows,obj.NumRows,obj.BlasID);
		AMTL_Matrix obj_V = new AMTL_Matrix(obj.NumColumns,obj.NumColumns,obj.BlasID);
		AMTL_Matrix obj_S = new AMTL_Matrix(obj.NumRows,obj.NumColumns,obj.BlasID);

		MatrixOps.SVD(obj,obj_U,obj_V,obj_S);
		
		int Rank = MatrixOps.getRank(obj);
		
		for(int i = 0; i<Rank; i++){
			norm = norm + obj_S.getDouble(i, i);
		}

		return norm;
	}

	public static void SingularValueThresholding(AMTL_Matrix obj, double threshold){

		AMTL_Matrix obj_U = new AMTL_Matrix(obj.NumRows,obj.NumRows,obj.BlasID);
		AMTL_Matrix obj_V = new AMTL_Matrix(obj.NumColumns,obj.NumColumns,obj.BlasID);
		AMTL_Matrix obj_S = new AMTL_Matrix(obj.NumRows,obj.NumColumns,obj.BlasID);

		MatrixOps.SVD(obj,obj_U,obj_V,obj_S);

		double value;
		for(int i = 0; i< obj_S.NumRows; i++){
			for(int j = 0; j< obj_S.NumColumns; j++){
				value = obj_S.getDouble(i,j);
				if(value <= threshold){
					obj_S.setDouble(i,j,0);
				} else{
					obj_S.setDouble(i,j,(value-threshold));
				}
			}

		}		
		MatrixOps.Transpose(obj_V);
		AMTL_Matrix inter = new AMTL_Matrix(obj_U.NumRows,obj_S.NumColumns,obj.BlasID);
		MatrixOps.MULT(obj_U,obj_S,inter);
		MatrixOps.MULT(inter,obj_V,obj);
		

	}

}
