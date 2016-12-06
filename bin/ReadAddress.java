import java.io.*;
import java.util.*;
import java.math.BigInteger;
import java.security.*;

class ReadAddress{
	
	String path;
	
	ReadAddress(String path){
		this.path = path;
	}
	
	public ArrayList<String> readAddress(){
		ArrayList<String> addressList = new ArrayList<String>();
		try{
	        File filename = new File(path);
	        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
	        BufferedReader br = new BufferedReader(reader);
	        String line;
	        while((line = br.readLine()) != null){
				//System.out.println(line);
				addressList.add(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return addressList;
	}
	
	public LinkedHashMap<String, BigInteger> convertHash(ArrayList<String> addressList){
		LinkedHashMap<String, BigInteger> addressSearch =  new LinkedHashMap<String, BigInteger>();
		BigInteger bigInteger = null;
		try{
			for(int i = 0; i < addressList.size(); i++){
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] inputData = addressList.get(i).getBytes(); 
				md.update(inputData);
				bigInteger = new BigInteger(md.digest());
				addressSearch.put(addressList.get(i), bigInteger);
			}
			//System.out.println("List size: " + addressList.size());
		}catch(Exception e){
	        	e.printStackTrace();
	    }
		return addressSearch;
	}

	public int searchIndex(String hostAddress, HashMap<String, BigInteger> addressSearch) {
		// TODO Auto-generated method stub
		MessageDigest md;
		BigInteger temp;
		int index = 0;
		try {
			md = MessageDigest.getInstance("MD5");
			BigInteger bigInteger = null;
			byte[] inputData = hostAddress.getBytes(); 
			md.update(inputData);
			bigInteger = new BigInteger(md.digest());
			//System.out.println("Current IP MD5: " + bigInteger);
			
			if(addressSearch.containsKey(hostAddress)){
				Iterator iter = addressSearch.entrySet().iterator();  
				while(iter.hasNext()){
					Map.Entry entry = (Map.Entry) iter.next();
					temp = (BigInteger) entry.getValue();
					if(temp.equals(bigInteger)){
						return index;
					}else{
						index++;
					}
				}
			}else{
				return -1;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}