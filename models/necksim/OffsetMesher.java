/**
 * @zhiping algorithm hex-meshing
 */
package artisynth.models.necksim;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import maspack.geometry.Face;
import maspack.geometry.HalfEdge;
import maspack.geometry.PolygonalMesh;
import maspack.geometry.Vertex3d;
import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;
import artisynth.core.modelbase.ModelComponent;
import artisynth.core.util.ArtisynthPath;

public class OffsetMesher {

   private static String objDir = "fem/";
   private static String femDir = "fem/";

   public static ArrayList<int[]> myEles = null;
   public static ArrayList<Point3d> myNodes = null;
   public static String inPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   public static String outPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   
   static boolean pair = true;

   public static void main (String[] args) throws Exception {
      //mne016 thickness=2
      //mne020 2
      //mne015_a 2.5
      //mne015_b 2
      String meshName = "mne015_a"; //mne015_a mne015_b mne016 mne020
      double thickness = 2.5;
      int division = 2;
      meshing("l_"+meshName, thickness, division);//2
      FileReader nodeFile = new FileReader (inPath + "l_"+meshName
         + ".1.node");
      FileReader elemFile = new FileReader (inPath + "l_"+meshName
         + ".1.ele");
      FEBioXML.write (outPath+"l_"+meshName+".feb", nodeFile, elemFile);
      
      if(pair)
      {
         writernode(ArtisynthPath.getSrcRelativePath(OffsetMesher.class,femDir+"r_"+meshName+".1.node"));
         writeele(ArtisynthPath.getSrcRelativePath(OffsetMesher.class,femDir+"r_"+meshName+".1.ele"));
         nodeFile = new FileReader (inPath + "r_"+meshName
            + ".1.node");
         elemFile = new FileReader (inPath + "r_"+meshName
            + ".1.ele");
         FEBioXML.write (outPath+"r_"+meshName+".feb", nodeFile, elemFile);
      }
   }
   //divisions = number of facets along -normal
   public static void meshing(String meshName, double offset, int divisions)throws Exception 
   {
      PolygonalMesh pm = new PolygonalMesh(
         new File (ArtisynthPath.getSrcRelativePath(OffsetMesher.class,
            objDir+meshName+".obj")));
      myEles = new ArrayList<int[]>();
      myNodes = new ArrayList<Point3d>();
      Vertex3d vertex;
      Vector3d vn;
      System.out.println("faces_"+pm.getNumFaces ());
      int dd = 0;
      for(int i = 0; i< pm.getVertices ().size (); i++)
      {
         vertex = (Vertex3d)pm.myVertices.get (i);
         Point3d vertexcopy = new Point3d(vertex.pnt.x, vertex.pnt.y, vertex.pnt.z);
         boolean duplicated = false;
         for(int j = 0; j< myNodes.size (); j++)
         {
            if(myNodes.get (j).x == vertexcopy.x && myNodes.get (j).z == vertexcopy.z && myNodes.get (j).z == vertexcopy.z)
            {
               System.out.println("find duplicates");
               duplicated = true;
               dd++;
               break;
            }
         }
         if(!duplicated)
            myNodes.add (vertexcopy);
      }
      System.out.println ("dulicates:_"+dd);
      ArrayList<Point3d> myNodescopy = new ArrayList<Point3d>();
      for(int i =0; i<  myNodes.size (); i++)
      {
         Point3d tmppoint = (Point3d)myNodes.get (i);
         myNodescopy.add (tmppoint);
      }
      for(int i = 0; i< myNodescopy.size (); i++)
      {
         Point3d tmppoint = (Point3d)myNodescopy.get (i);
         vn = new Vector3d (pm.myNormalList.get (i));
         Point3d vertexcopy = new Point3d(tmppoint.x, tmppoint.y, tmppoint.z);
         Vector3d vncopy = new Point3d(vn.x, vn.y, vn.z);
         double scale = offset*-1;
         vncopy.scale (scale);
         vertexcopy.add (vncopy);
         myNodes.add (vertexcopy);
      }
      int no  = myNodes.size ()/2;
      int id =0;
      for (Face face : pm.myFaces) {
         int[] ppp = face.getVertexIndices ();
         boolean isFace = true;
         for(int ll = 0; ll<4; ll++)
         {
            if(ppp[ll] > no-1)
            {
               isFace = false;
               break;
            }
         }
         //if(id<pm.getNumFaces ()/2)
         if(isFace)
         {
         HalfEdge he = face.he0; 
         int eleidx[] = new int[8];
         int idx = he.head.idx; //0
         System.out.println(idx);
         eleidx[0] = idx;
         eleidx[4] = idx+no;
         he = he.next;
         idx = he.head.idx; //1
         System.out.println(idx);
         eleidx[1] = idx;
         eleidx[5] = idx+no;
         he = he.next;
         idx = he.head.idx; //2
         System.out.println(idx);
         eleidx[2] = idx;
         eleidx[6] = idx+no;
         he = he.next;
         idx = he.head.idx;//3
         System.out.println(idx);
         eleidx[3] = idx;
         eleidx[7] = idx+no;
         myEles.add (eleidx);
         }
         id++;
      }
      System.out.println("fe nodes: "+myNodes.size ());
      System.out.println("eles: "+myEles.size ());
      writenode(ArtisynthPath.getSrcRelativePath(OffsetMesher.class,femDir+meshName+".1.node"));
      writeele(ArtisynthPath.getSrcRelativePath(OffsetMesher.class,femDir+meshName+".1.ele"));
   }
   public static void writenode(String meshName) throws Exception
   {
      FileOutputStream fos = new FileOutputStream (meshName);
      PrintStream ps = new PrintStream (new BufferedOutputStream (fos));  
      ps.println (myNodes.size() + " 3 0 0");
      int idx = 0;
      for (Point3d v : myNodes) {
         ps.println (idx + " " + v.x + " " + v.y + " " + v.z);
         idx++;
      }
      ps.flush();
      fos.close();
      System.out.println ("wrote " + meshName);
   }
   public static void writernode(String meshName) throws Exception
   {
      FileOutputStream fos = new FileOutputStream (meshName);
      PrintStream ps = new PrintStream (new BufferedOutputStream (fos));  
      ps.println (myNodes.size() + " 3 0 0");
      int idx = 0;
      for (Point3d v : myNodes) {
         v.x *=-1;
         ps.println (idx + " " + v.x + " " + v.y + " " + v.z);
         idx++;
      }
      ps.flush();
      fos.close();
      System.out.println ("wrote " + meshName);
   }
   public static void writeele(String meshName) throws Exception
   {
      FileOutputStream fos = new FileOutputStream (meshName);
      PrintStream ps = new PrintStream (new BufferedOutputStream (fos));  
      ps.println (myEles.size() + " 8 0");
      int idx = 0;
      for (int[] idxs : myEles) {
         ps.println (idx + " " + idxs[0] + " " + idxs[1] + " " + idxs[2]+
            " " + idxs[3] + " " + idxs[4] + " " + idxs[5]+
            " " + idxs[6] + " " + idxs[7]);
         idx++;
      }
      ps.flush();
      fos.close();
      System.out.println ("wrote " + meshName);
   }
}
