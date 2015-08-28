package artisynth.models.necksim;

import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;

import maspack.matrix.Point3d;
import maspack.util.ReaderTokenizer;

import artisynth.core.femmodels.TetGenReader;
import artisynth.core.util.ArtisynthPath;

public class TetgenBatching {
   static String objNames[] = {
                               
   };
   public static String inPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   public static String outPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   public static void main(String[] args) throws Exception
   {
      Hashtable<String,Integer> objNames =
      new Hashtable<String,Integer>();
      objNames.put ("mba023", 1);
      
      for (Iterator it2 = objNames.keySet().iterator(); it2.hasNext();) {
         String key = (String) it2.next();
         int value = (int)objNames.get(key);
         if(value == 1)
         {
            String meshName = "l_"+key;
            meshing(meshName,meshName, "L");
            FileReader nodeFile = new FileReader (inPath + meshName
               + ".1.node");
            FileReader elemFile = new FileReader (inPath + meshName
               + ".1.ele");
            FEBioXML.writeTetgen (outPath+meshName+".feb", nodeFile, elemFile);
            
            nodeFile = new FileReader (inPath + meshName
               + ".1.node");
            elemFile = new FileReader (inPath + meshName
               + ".1.ele");
            meshName = "r_"+key;
            writernode(nodeFile, outPath+meshName+".1.node");
            writeele(elemFile, outPath+meshName+".1.ele");
            nodeFile = new FileReader (inPath + meshName
               + ".1.node");
            elemFile = new FileReader (inPath + meshName
               + ".1.ele");
            FEBioXML.writeTetgen (outPath+meshName+".feb", nodeFile, elemFile);
         }
         else
         {
            String meshName = key;
            meshing(meshName,meshName, "L");
            FileReader nodeFile = new FileReader (inPath + meshName
               + ".1.node");
            FileReader elemFile = new FileReader (inPath + meshName
               + ".1.ele");
            FEBioXML.writeTetgen (outPath+meshName+".feb", nodeFile, elemFile);
         }
     }
      System.out.println("done!");
   }
   public static void writernode(FileReader nodeReader, String meshName) throws Exception
   {
      FileOutputStream fos = new FileOutputStream (meshName);
      PrintStream ps = new PrintStream (new BufferedOutputStream (fos));  
      ReaderTokenizer nodeFile =
      new ReaderTokenizer (new BufferedReader (nodeReader));
      String headinfo = "";
      nodeFile.nextToken();
      headinfo+=(int)nodeFile.lval+" ";
      nodeFile.nextToken();
      headinfo+=(int)nodeFile.lval+" ";
      nodeFile.nextToken();
      headinfo+=(int)nodeFile.lval+" ";
      nodeFile.nextToken();
      headinfo+=(int)nodeFile.lval;
      ps.println (headinfo);
      while (nodeFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!nodeFile.tokenIsInteger()) {
         }
         int index = (int)nodeFile.lval;
         Point3d coords = new Point3d();
         for (int i = 0; i < 3; i++) {
            coords.set (i, nodeFile.scanNumber());
         }
         coords.x *=-1;
         ps.println (index + " " + coords.x + " " + coords.y + " " + coords.z);
      }
      ps.flush();
      fos.close();
      nodeReader.close ();
      System.out.println ("wrote " + meshName);
   }
   public static void writeele(FileReader elemReader, String meshName) throws Exception
   {
      FileOutputStream fos = new FileOutputStream (meshName);
      PrintStream ps = new PrintStream (new BufferedOutputStream (fos));  
      ReaderTokenizer elemFile =
      new ReaderTokenizer (new BufferedReader (elemReader));
      String headinfo = "";
      elemFile.nextToken();
      headinfo += (int)elemFile.lval+" ";
      elemFile.nextToken();
      headinfo += (int)elemFile.lval+" ";
      elemFile.nextToken();
      headinfo += (int)elemFile.lval;
      ps.println(headinfo);
      while (elemFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!elemFile.tokenIsInteger()) {

         }
         int index = (int)elemFile.lval;
         int[] idxs = new int[4];
         for (int i = 0; i < 4; i++) {
            idxs[i] = elemFile.scanInteger();
         }
         ps.println (index+" "+idxs[0]+" "+idxs[1]+" "+idxs[2]+" "+idxs[3]);
      }
      ps.flush();
      fos.close();
      elemReader.close ();
      System.out.println ("wrote " + meshName);
   }
   public static void meshing(String meshName, String savename, String side) throws Exception
   {
      TetGenReader.writePolyFileFromSurfaceMesh (meshName, savename,inPath, outPath, side);
      Runtime r = Runtime.getRuntime();
      String[] cmd = new String[5];
      cmd[0] = "cmd ";
      cmd[1] = "/c ";
      cmd[2] = "start "; //promt cmd window
      cmd[3] = inPath;
      cmd[4] = "tetgen -pq2.0a1000.0 "+savename;
      Process p = Runtime.getRuntime().exec((cmd[0]+cmd[1]+cmd[4]),null,new File(cmd[3]));
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ( (line=br.readLine()) != null)
      System.out.println(line);
   }
}
