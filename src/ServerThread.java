import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;

import org.AMTL_Matrix.*;


public class ServerThread implements Callable<AMTL_Matrix>{

	Socket socket;
	
	AMTL_Matrix A;
	AMTL_Matrix A_vec;
	
	ClientMessage clientMsg;
	
	int index;
	int dim;
	double StepSize;
	double Lambda;
	
	
	public ServerThread(Socket clientSocket, int dim, int index, AMTL_Matrix a, double StepSize, double Lambda) {
		// TODO Auto-generated constructor stub
		this.socket = clientSocket;
		this.dim = dim;
		this.index = index;
		
		// Model matrix which was initialized by loading from a file at Server's end.
		A = new AMTL_Matrix(a);
		A_vec = new AMTL_Matrix(dim,1,a.BlasID);
		
		this.StepSize = StepSize;
		this.Lambda = Lambda;
		
	}

	@Override
	public AMTL_Matrix call() throws Exception {
		
		// Get the message from the client
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream( ));
		// Client message was initialized in Client.java before. It has an initial vector which is 
		// the column of the model matrix that server needs to send back.
		clientMsg = (ClientMessage)ois.readObject( );
		
		//System.out.println(socket.getInputStream().available());

		
		if(index == -1){
			clientMsg.setError(1);
		} else if(clientMsg.getVec().NumRows == dim){
			// Initial vector carried by clientMsg.
			A_vec = clientMsg.getVec();
			
			// Change the corresponding column of the model matrix with the vector 
			// in the client message.
			
			for(int i = 0; i<dim; i++){
				A.setDouble(i, index, A_vec.getDouble(i,0));
			}
			
			// Operations need to be done by server.
			Backward_Step nuclear = new Backward_Step(StepSize,Lambda);
			AMTL_Matrix A_res = nuclear.Prox_Nuclear(A);
			
			for(int i = 0; i<A_res.NumRows; i++){
				for(int j = 0; j<A_res.NumColumns; j++){
					A.setDouble(i, j, A_res.getDouble(i,j));
				}
			}
			
			
			for(int i = 0; i<dim; i++){
				A_vec.setDouble(i,0, A.getDouble(i,index));
			}
			
			// Updated vector is copied back to clientMsg.
			clientMsg.copyVec(A_vec);
		} else{
			System.out.println("The vector of client" + index + "does not match the row number of the matrix!\n Permission Denied\n");
			clientMsg.setError(3);
		}
		
		// Serialize the clientMsg
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(clientMsg);
		//System.out.println(socket.getOutputStream().toString());
		oos.flush();
		
		// Close the socket one iteration is done.
		socket.close();
	
		return A;
	}

}
