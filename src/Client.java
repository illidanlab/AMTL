import java.io.*;
import java.net.*;
import java.util.*;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.MatrixIO;
import java.io.IOException;

import org.AMTL_Matrix.*;
import org.AMTL_Matrix.MatrixOps.*;;




public class Client {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//InputStream is;
		//InputStreamReader isr;
		//BufferedReader br;
		

		int ITER = 10;
		if (args.length > 0) {
		    try {
		        ITER = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e2) {
		        System.err.println("Argument" + args[0] + " must be an integer.");
		        System.exit(1);
		    }
		}
		
		double StepSize = 0.5;
		if (args.length > 1) {
		    try {
		        StepSize = Double.parseDouble(args[1]);
		    } catch (NumberFormatException e3) {
		        System.err.println("Argument" + args[1] + " must be a double.");
		        System.exit(1);
		    }
		}
		
		int Blas = 0;
		
		DenseMatrix64F A_load;
		
		try{
			A_load = MatrixIO.loadCSV("/home/inci/Dropbox/AMTL_Java_Inci/A.csv");
		}catch (IOException e1){
			throw new RuntimeException(e1);
		}
		
		AMTL_Matrix A = new AMTL_Matrix(A_load,Blas);
		
		DenseMatrix64F b_load;
		
		try{
			b_load = MatrixIO.loadCSV("/home/inci/Dropbox/AMTL_Java_Inci/b.csv");
		}catch (IOException e2){
			throw new RuntimeException(e2);
		}
		
		AMTL_Matrix b = new AMTL_Matrix(b_load,Blas);
		
		int dim = A.NumColumns;
		double ARock_StepSize = 0.1;
		
		AMTL_Matrix A_vec = new AMTL_Matrix(dim, 1, Blas);
		AMTL_Matrix A_vec_latest = new AMTL_Matrix(dim, 1, Blas);
	
		
		ClientMessage clientMsg = new ClientMessage(dim,Blas);

		Date start_time = null;
		
		try{
			// Set the socket
			InetAddress serverHost = InetAddress.getByName("localhost");
			
			
			int serverPort = 3457;
			Socket clientSocket = null;
			
			// Start the timer
			start_time = new Date();
			
			// Start work
			for(int j = 0; j < ITER; j++){
				System.out.println("ITER: " + j);

				// In every iteration a new socket object is created because at the end 
				// if ServerThread we are closing the socket.
				clientSocket = new Socket(serverHost, serverPort);
				ObjectOutputStream oos;
				ObjectInputStream ois;
				
				// Send a message (a vector) to server and this unblock the accept() method and 
				// invokes a communication. 
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				oos.writeObject(clientMsg);
				oos.flush();

				// Get the message at the end of the operation at server's end.
				//System.out.println(clientSocket.getInputStream().available());
				ois = new ObjectInputStream(clientSocket.getInputStream());
				clientMsg = (ClientMessage)ois.readObject( );
				
				if(clientMsg.getError() == 0){
					// Operation needs to be done at client end.
					Forward_Step grad = new Forward_Step(A, b, StepSize);
					A_vec = grad.Gradient_Update(clientMsg.getVec());
					
					
					A_vec_latest = clientMsg.getVec();
					
					AMTL_Matrix result = new AMTL_Matrix(A_vec.NumRows,A_vec.NumColumns,A_vec.BlasID);
					
					MatrixOps.ReverseSign(A_vec_latest);
					MatrixOps.ADD(A_vec, A_vec_latest, result);
					
					MatrixOps.Scale(result,ARock_StepSize);
					
					MatrixOps.ReverseSign(A_vec_latest);
					
					MatrixOps.ADD(A_vec_latest, result, A_vec);
					
					
					System.out.println(A_vec.M);
						
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
			
			// After iterations are done close the socket.
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
