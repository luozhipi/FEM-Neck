package artisynth.models.necksim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;
import maspack.util.ReaderTokenizer;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemNode3d;
import artisynth.core.femmodels.HexElement;
import artisynth.core.femmodels.TetElement;

public class MeshReader {
   
   public static void read (
      FemModel3d model, double density, String nodeString, String elemString,
      Vector3d scale) throws Exception {
      FileReader nodeFile = new FileReader (nodeString);
      FileReader elemFile = new FileReader (elemString);
      read (model, density, scale, nodeFile, elemFile);
      nodeFile.close();
      elemFile.close();
   }

   public static void read (
      FemModel3d model, double density, Vector3d scale, Reader nodeReader,
      Reader elemReader) throws Exception {
      ReaderTokenizer nodeFile =
         new ReaderTokenizer (new BufferedReader (nodeReader));
      model.setDensity (density);
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();

      while (nodeFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!nodeFile.tokenIsInteger()) {

         }
         int index = (int)nodeFile.lval;
         Point3d coords = new Point3d();

         for (int i = 0; i < 3; i++) {
            coords.set (i, nodeFile.scanNumber());
         }

         // System.out.println(coords);
         if (scale != null) {
            coords.x *= scale.x;
            coords.y *= scale.y;
            coords.z *= scale.z;
         }

         model.addNode (new FemNode3d (coords));
      }

      ReaderTokenizer elemFile =
         new ReaderTokenizer (new BufferedReader (elemReader));

      elemFile.nextToken();
      elemFile.nextToken();
      elemFile.nextToken();

      while (elemFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!elemFile.tokenIsInteger()) {

         }
         int index = (int)elemFile.lval;

         int[] idxs = new int[8];
         for (int i = 0; i < 8; i++) {
            idxs[i] = elemFile.scanInteger();
            // System.out.print(idxs[i] + " ");
         }
         // System.out.println();

         FemNode3d n0 = model.getNode(idxs[0]);
         FemNode3d n1 = model.getNode(idxs[1]);
         FemNode3d n2 = model.getNode(idxs[2]);
         FemNode3d n3 = model.getNode(idxs[3]);
         
         FemNode3d n4 = model.getNode(idxs[4]);
         FemNode3d n5 = model.getNode(idxs[5]);
         FemNode3d n6 = model.getNode(idxs[6]);
         FemNode3d n7 = model.getNode(idxs[7]);

         // check to make sure that the tet is defined so that the
         // first three nodes are arranged clockwise about their face
         if (HexElement.computeVolume (n3, n2, n1, n0, n7, n6, n5, n4) >= 0) {
            model.addElement ( new HexElement (n3, n2, n1, n0, n7, n6, n5, n4));
         }
         else {
            model.addElement( new HexElement (n0, n1, n2, n3, n4, n5, n6, n7));
         }
      }
   }

}
