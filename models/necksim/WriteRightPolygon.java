package artisynth.models.necksim;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

import artisynth.core.util.ArtisynthPath;
import maspack.geometry.PolygonalMesh;

public class WriteRightPolygon {
   
   public static void main (String[] args) {

      PolygonalMesh mesh = new PolygonalMesh();
      String name="mba023";
      try {
         Reader meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/fem/l_"+name+".obj", ".")));
         mesh.read(meshReader);
         mesh.scale (-1,1,1);
         PrintStream ps =
         new PrintStream (
            new FileOutputStream (ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/fem/r_"+name+".obj", "."))
         );
      mesh.write (ps, "%g");
      System.out.println ("done");
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
   }

}
