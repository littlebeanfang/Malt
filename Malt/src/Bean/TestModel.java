package Bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.maltparser.ml.lib.FeatureMap;

import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

public class TestModel {
	public void TestMaltModelAccuracyOnInstanceFile(String liblinearmodel,String instfile,String featuremapfile) throws IOException, ClassNotFoundException{
    	Model libModel=Model.load(new File(liblinearmodel));
    	BufferedReader reader=new BufferedReader(new FileReader(instfile));
    	String line=reader.readLine();
    	ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(featuremapfile)));
	    FeatureMap map = (FeatureMap) input.readObject();
	    input.close();
    	int rightcount=0;
    	int totalcount=0;
    	while(line!=null){
    		totalcount++;
    		String columns[]=line.split("\t");
    		double[] estimates = new double[libModel.getNrClass()];
    		ArrayList<FeatureNode> featureList=new ArrayList<FeatureNode>();
    		for(int i=1;i<columns.length;i++){
    			//String feat_value[]=columns[i].split(":");
//    			fn[i-1]=new FeatureNode(Integer.parseInt(feat_value[0]), Double.parseDouble(feat_value[1]));
    			Integer code=Integer.parseInt(columns[i]);
    			//final long key = ((((long)i) << 48) | (long)code);
    			if(code!=-1){
	    			int index=map.getIndex(i, code);
	    			System.out.println("index:"+index);
	    			if(index!=-1){
	    				featureList.add(new FeatureNode(index, 1.0));
	    			}
    			}
    		}
    		FeatureNode[] fn=new FeatureNode[featureList.size()];
    		for(int i=0;i<featureList.size();i++){
    			fn[i]=featureList.get(i);
    		}
            double probabilityPrediction = Linear.predictValues(libModel, fn, estimates);
        	//System.out.println("prediction:"+probabilityPrediction);
        	if(Integer.parseInt(columns[0])==(int)probabilityPrediction){
        		rightcount++;
        	}
    		line=reader.readLine();
    	}
    	
    	System.out.println("Lib ActionAccuracy:"+(double)rightcount/totalcount);
	}
	public static void main(String args[]) throws ClassNotFoundException, IOException{
		String maltlibmodel=System.getenv("CODEDATA")+File.separator+"malt2-21beansave.model";
		String maltlibinstfile=System.getenv("CODEDATA")+File.separator+"odm0.liblinear.ins";
		String maltmapfile=System.getenv("CODEDATA")+File.separator+"odm0.liblinear.map";
		TestModel model=new TestModel();
		model.TestMaltModelAccuracyOnInstanceFile(maltlibmodel, maltlibinstfile, maltmapfile);
	}
}
