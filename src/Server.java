import java.net.*;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.io.IOException;
import java.math.BigInteger;
//import java.security.MessageDigest;
import org.ejml.ops.MatrixIO;

import org.AMTL_Matrix.*;


import org.ejml.data.DenseMatrix64F;


public class Server {
	
	public static void main(String[] args){
				
		ReadAddress reader = new ReadAddress("/home/inci/Dropbox/AMTL_Java_Inci/address.txt");
		
		ArrayList<String> addressList = reader.readAddress();
		HashMap<String, BigInteger> addressSearch = reader.convertHash(addressList);
		
		int index = 0;
		
        int Blas = 0;
		
		//Initialization
		DenseMatrix64F W_load;
		try{
			W_load = MatrixIO.loadCSV("/home/inci/workspace/W.csv");
		}catch (IOException e1){
			throw new RuntimeException(e1);
		}
		
		AMTL_Matrix W = new AMTL_Matrix(W_load,Blas);
		
		int dim = W.NumRows;
		 
		
		double StepSize = 0.5;
		if (args.length > 0) {
		    try {
		        StepSize = Double.parseDouble(args[0]);
		    } catch (NumberFormatException e1) {
		        System.err.println("Argument" + args[0] + " must be a double.");
		        System.exit(1);
		    }
		}

		double Lambda = 0.9;
		if (args.length > 1) {
		    try {
		        Lambda = Double.parseDouble(args[1]);
		    } catch (NumberFormatException e2) {
		        System.err.println("Argument" + args[1] + " must be a double.");
		        System.exit(1);
		    }
		}
		
		
		
		try {
			//Creating a socket by binding the port number. Server is ready to listen 
			// from this port.
			int serverPort = 3457;
			ServerSocket serverSocket = new ServerSocket(serverPort);
			
			System.out.println("****** Get Ready (Starts listening) ******");
			
			while(true){
				// accept(): A blocking method call. When I client contacts, the method is unblocked 
				// and returns a Socket object to the server to communicate with the client. 
				Socket clientSocket = serverSocket.accept();
				System.out.println("Starts communicating a client.");
				
				try{
					//
					InetAddress address = clientSocket.getInetAddress();
					System.out.println("Current client IP: " + address.getHostAddress());
					
					// This index will specify the column of the model matrix server needs to 
					// return.
					index = reader.searchIndex(address.getHostAddress(), addressSearch);
					
					// If there is a new Client, permission will be denied and it will be terminated.
					if (index == -1){
						System.out.println("New Client!");
					} else {
						System.out.println("Current index: " + index);
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
				
				ServerThread t = new ServerThread(clientSocket, dim, index, W, StepSize, Lambda);
				// FutureTask interface takes a callable object. Object ft is used to call the call() 
				// method overridden in ServerThread.
				FutureTask<AMTL_Matrix> ft = new FutureTask<AMTL_Matrix>(t);
				// This invokes the thread where call() method of ServerThread operates.
				new Thread(ft).start();
				// get() is a method of FutureTask and returns the result of 
				// the computations of call() method of ServerThread.
				W = (AMTL_Matrix) ft.get();	
				System.out.println(W.M);
			}	
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}