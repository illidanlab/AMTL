/* BlasID 0: ejml
 * BlasID 1: ujmp
 * BlasID 2: jama
 * BlasID 3: jblas
*/

package org.AMTL_Matrix;

import java.io.Serializable;

import org.ejml.data.*;
import org.ejml.ops.CommonOps;

import org.ujmp.core.*;


import org.jblas.DoubleMatrix;

import org.AMTL_Matrix.MatrixOps.*;



public class AMTL_Matrix implements Serializable{

	public int NumRows;
	public int NumColumns;
	public int BlasID;

	public Object M;

	
	public AMTL_Matrix(int NumRows, int NumColumns,int BlasID){
		this.NumRows = NumRows;
		this.NumColumns = NumColumns;
		this.BlasID = BlasID;

		if(this.BlasID == 0){
			M = new DenseMatrix64F(this.NumRows, this.NumColumns);
		} else if(this.BlasID == 1){
			M = DenseMatrix.Factory.zeros(this.NumRows, this.NumColumns);
		} else if(this.BlasID == 2){
			M = new Jama.Matrix(this.NumRows, this.NumColumns);
		} else {
			M = new DoubleMatrix(this.NumRows, this.NumColumns);
			
		} 
	}


	public AMTL_Matrix(double[][] Input, int BlasID){

		this.BlasID = BlasID;

		if(this.BlasID == 0){
			// Ejml
			M = new DenseMatrix64F(Input);
			this.NumRows = ((DenseMatrix64F) M).numRows;
			this.NumColumns = ((DenseMatrix64F) M).numCols;			
		} else if(this.BlasID == 1){
			//Ujmp
			M = DenseMatrix.Factory.importFromArray(Input);
			this.NumColumns = (int) ((Matrix) M).getColumnCount();
			this.NumRows = (int) ((Matrix) M).getRowCount();
			 
		} else if(this.BlasID == 2){
			M = new Jama.Matrix(Input);
			this.NumColumns = ((Jama.Matrix) M).getColumnDimension();
			this.NumRows = ((Jama.Matrix) M).getRowDimension();
		} else {
			M = new DoubleMatrix(Input);
			this.NumColumns = ((DoubleMatrix) M).columns;
			this.NumRows = ((DoubleMatrix) M).rows;
		}
	}
	
	
	public AMTL_Matrix(AMTL_Matrix Input){

		this.BlasID = Input.BlasID;
		this.NumColumns = Input.NumColumns;
		this.NumRows = Input.NumRows;
		
		if(this.BlasID == 0){
			M = new DenseMatrix64F((DenseMatrix64F) Input.M);
		} else if(this.BlasID == 1){
			M = DenseMatrix.Factory.copyFromMatrix((Matrix) Input.M);
		} else if(this.BlasID == 2){
			M = ((Jama.Matrix) Input.M).copy();
		} else {
			M = new DoubleMatrix(Input.NumRows, Input.NumColumns);
			((DoubleMatrix) M).copy((DoubleMatrix) Input.M);
		} 

		
	}
	
	public AMTL_Matrix(Object Input, int BlasID){
		
		this.BlasID = BlasID;

		if(this.BlasID == 0){
			M = new DenseMatrix64F((DenseMatrix64F) Input);
			this.NumRows = ((DenseMatrix64F) Input).numRows;
			this.NumColumns = ((DenseMatrix64F) Input).numCols;
		} else if(this.BlasID == 1){
			M = DenseMatrix.Factory.copyFromMatrix((Matrix) Input);
			this.NumRows = (int) ((Matrix) Input).getRowCount();
			this.NumColumns = (int) ((Matrix) Input).getColumnCount();
 		} else if(this.BlasID == 2){
			M = ((Jama.Matrix) Input).copy();
			this.NumRows = ((Jama.Matrix) Input).getRowDimension();
			this.NumColumns = ((Jama.Matrix) Input).getColumnDimension();
		} else {
			M = new DoubleMatrix(((DoubleMatrix) Input).rows, ((DoubleMatrix) Input).columns);
			((DoubleMatrix) M).copy((DoubleMatrix) Input);
			this.NumRows = ((DoubleMatrix) Input).rows;
			this.NumColumns = ((DoubleMatrix) Input).columns;
		} 
	}

	public int getNumRows(){
		return NumRows;
	}

	public int getNumColumns(){
		return NumColumns;
	}
	
	public AMTL_Matrix getSubMatrix(int[] rows, int[] columns){
		
		AMTL_Matrix SubMatrix;
		
		if(this.BlasID == 0){
			
			if(rows.length == 1){	
				DenseMatrix64F vector = CommonOps.extract((DenseMatrix64F) this.M, rows[0], (rows[0]+1), 0, columns.length);
				CommonOps.transpose(vector);
				SubMatrix = new AMTL_Matrix(vector, 0);
			} else{
				DenseMatrix64F vector = CommonOps.extract((DenseMatrix64F) this.M, 0, rows.length, columns[0], (columns[0]+1));
				SubMatrix = new AMTL_Matrix(vector, 0);
			}
			
		} else if(this.BlasID == 1){
			if(rows.length == 1){
				SubMatrix = new AMTL_Matrix(columns.length, 1, 1);
				long[] coordinates = new long[]{rows[0],0};
				for(int i = 0; i<columns.length; i++){
					coordinates[1] = i;
					SubMatrix.setDouble(i, 0, ((Matrix) this.M).getAsDouble(coordinates));
				}
			} else {
				SubMatrix = new AMTL_Matrix(rows.length, 1, 1);
				long[] coordinates = new long[]{0,columns[0]};
				for(int i = 0; i<rows.length; i++){
					coordinates[0] = i;
					SubMatrix.setDouble(i, 0, ((Matrix) this.M).getAsDouble(coordinates));
				}
			}
		} else if(this.BlasID == 2){
			if(rows.length == 1){
				Jama.Matrix sub  = ((Jama.Matrix) this.M).getMatrix(rows, columns);
				SubMatrix = new AMTL_Matrix(sub,2);	
				MatrixOps.Transpose(SubMatrix);
			} else{
				Jama.Matrix sub  = ((Jama.Matrix) this.M).getMatrix(rows, columns);
				SubMatrix = new AMTL_Matrix(sub,2);	
			}
			
		} else{
			if(rows.length == 1){
				DoubleMatrix sub = ((DoubleMatrix) this.M).getRow(rows[0]);
				sub = sub.transpose();
				SubMatrix = new AMTL_Matrix(sub,3);
			} else{
				DoubleMatrix sub = ((DoubleMatrix) this.M).getColumn(columns[0]);
				SubMatrix = new AMTL_Matrix(sub,3);
			}
		}
		
		return SubMatrix;
		
	}

	
	public double getDouble(int row, int column){
		
		if(row > this.NumRows){
			System.out.println("Row index is not valid!");
			System.exit(0);
		}
		
		if(column > this.NumColumns){
			System.out.println("Column index is not valid!");
			System.exit(0);
		}
		
		
		if(this.BlasID == 0){ 
			// Ejml
			return ((DenseMatrix64F) M).get(row,column);
		} else if(this.BlasID == 1){
			//Ujmp
			long[] coordinates = new long[]{row,column};
			return ((Matrix) M).getAsDouble(coordinates);
		} else if(this.BlasID == 2){
			return ((Jama.Matrix) M).get(row, column);
		} else {
			return ((DoubleMatrix) M).get(row,column);
		}
	}
	
	public void setDouble(int row, int column, double val){
		
		if(row > this.NumRows){
			System.out.println("Row index is not valid!");
			System.exit(0);
		}
		
		if(column > this.NumColumns){
			System.out.println("Column index is not valid!");
			System.exit(0);
		}
		
		if(this.BlasID == 0){ 
			// Ejml
			((DenseMatrix64F) M).set(row, column, val);
		} else if(this.BlasID == 1){
			//Ujmp
			long[] coordinates = new long[]{row,column};
			((Matrix) M).setAsDouble(val, coordinates);
		} else if(this.BlasID == 2){
			((Jama.Matrix) M).set(row, column, val);
		} else {
			((DoubleMatrix) M).put(row, column, val);
		}
	}


}
