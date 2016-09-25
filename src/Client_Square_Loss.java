import java.io.*;
import java.net.*;
import java.util.*;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.MatrixIO;
import java.io.IOException;

import org.AMTL_Matrix.*;
import org.AMTL_Matrix.MatrixOps.*;
import org.AMTL_Matrix.Norms.Norms;

public class Client_Square_Loss {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//InputStream is;
		//InputStreamReader isr;
		//BufferedReader br;
		

		int ITER = 20; // Number of iterations 
		
		// Getting input arguments from command line
		if (args.length > 0) {
		    try {
		        ITER = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e2) {
		        System.err.println("Argument" + args[0] + " must be an integer.");
		        System.exit(1);
		    }
		}
		
		double StepSize = 0.1;
		if (args.length > 1) {
		    try {
		        StepSize = Double.parseDouble(args[1]);
		    } catch (NumberFormatException e3) {
		        System.err.println("Argument" + args[1] + " must be a double.");
		        System.exit(1);
		    }
		}
		
		// Blas is the ID that tells us which BLAS library we would like to use.
		/* BlasID 0: ejml
		 * BlasID 1: ujmp
		 * BlasID 2: jama
		 * BlasID 3: jblas
		*/
		int Blas = 0;
		
		// Loading data matrix A and the response b here.
		// I assume that the objective is square loss. ||Ax-b||^2
		DenseMatrix64F A_load;
		
		try{
			A_load = MatrixIO.loadCSV("A.csv");
		}catch (IOException e1){
			throw new RuntimeException(e1);
		}
		
		AMTL_Matrix A = new AMTL_Matrix(A_load,Blas);
		
		DenseMatrix64F b_load;
		
		try{
			b_load = MatrixIO.loadCSV("b.csv");
		}catch (IOException e2){
			throw new RuntimeException(e2);
		}
		
		AMTL_Matrix b = new AMTL_Matrix(b_load,Blas);
		
		// Dimension of the feature vectors
		int dim = A.NumColumns;
		double ARock_StepSize = 0.1;
		
		// Initializing some vectors to use in forward step
		AMTL_Matrix A_vec = new AMTL_Matrix(dim, 1, Blas);
		
		// Creating an object of ClientMessage class. 
		ClientMessage clientMsg = new ClientMessage(dim,Blas);

		// This is a standard way to keep the time.
		Date start_time = null;
		
		try{
			// Set the socket
			InetAddress serverHost = InetAddress.getByName("localhost");
			
			// Server port number should be same as the number defined in Server.java
			int serverPort = 3457;
			Socket clientSocket = null;
			
			// Start the timer
			start_time = new Date();
			
			// Start work
			for(int j = 0; j < ITER; j++){
				System.out.println("ITER: " + j);

				// In every iteration a new socket object is created.  
				// I will check whether we need to create a new object in each iteration
				// or we can create one outside the loop once.
				clientSocket = new Socket(serverHost, serverPort);
				ObjectOutputStream oos;
				ObjectInputStream ois;
				
				// Send a message (a vector) to server and this unblock the accept() method and 
				// invokes a communication. 
				// Serializing the vector.
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.writeObject(clientMsg);
				oos.flush();

				// Get the message at the end of the operation at server's end.
				ois = new ObjectInputStream(clientSocket.getInputStream());
				clientMsg = (ClientMessage)ois.readObject( );
				
				if(clientMsg.getError() == 0){
					// Operation needs to be done at client end.
					
					//Forward step (Gradient Update)
					Operators forward = new Operators(StepSize);
					
					A_vec = forward.SquareLoss_Forward(A, b, clientMsg.getVec());
					
					
					// Arock update
					AMTL_Matrix A_vec_latest = new AMTL_Matrix(clientMsg.getVec());
					
					AMTL_Matrix result = new AMTL_Matrix(A_vec.NumRows,A_vec.NumColumns,A_vec.BlasID);
					
					MatrixOps.ReverseSign(A_vec_latest);
					MatrixOps.ADD(A_vec, A_vec_latest, result);
					
					MatrixOps.Scale(result,ARock_StepSize);
					
					MatrixOps.ReverseSign(A_vec_latest);
					
					MatrixOps.ADD(A_vec_latest, result, A_vec);
					
					
					//System.out.println(A_vec.M);
						
				    // Update client message with the updated vector.
					clientMsg.copyVec(A_vec);
					
				
					} else if(clientMsg.getError() == 1){
					System.out.println("Error Message 1: Permission Denied! ");
					System.exit(0);
				    } else if(clientMsg.getError() == 3){
					System.out.println("Error Message 3: Vector Length Error!\nPlease change the ROW variable and restart the program!\nExit");
					System.exit(0);
				}else{
					// You can set more kinds of error in the server
					System.out.println("Unknown Error!");
				}
				oos.close();
				ois.close();
			}
			
				
			
			// After iterations are done, close the socket.
			clientSocket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Date stop_time = new Date();
		double etime = (stop_time.getTime() - start_time.getTime())/1000.;
		System.out.println("\nElapsed Time = " + fixedWidthDoubletoString(etime,12,3) + " seconds\n");
	}
	
	// Used to print out the elapsed time whose unit is second.
	public static String fixedWidthDoubletoString (double x, int w, int d) {
		java.text.DecimalFormat fmt = new java.text.DecimalFormat();
		fmt.setMaximumFractionDigits(d);
		fmt.setMinimumFractionDigits(d);
		fmt.setGroupingUsed(false);
		String s = fmt.format(x);
		while (s.length() < w) {
			s = " " + s;
		}
		return s;
	}
	

}
