/*
 * author: zhiping luo@uu
 */
package artisynth.models.necksim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.TetGenReader;
import artisynth.core.util.ArtisynthPath;
import maspack.geometry.Face;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;

public class GeoUtility {
   static double epsilon = 1e-10;
   public static boolean PointInTriangle(Point3d A, Point3d B, Point3d C, Point3d P)
   {
      Point3d u = new Point3d(0,0,0);
      Point3d v = new Point3d(0,0,0);
      Point3d w = new Point3d(0,0,0);
      u.sub (B, A);
      v.sub(C, A);
      w.sub(P, A);
      Point3d vCrossW = new Point3d();
      vCrossW.cross (v, w);
      Point3d vCrossU = new Point3d();
      vCrossU.cross (v, u);
      // Test sign of r
      double signr = vCrossW.dot (vCrossU);
      if (signr < -epsilon)
      {
         return false;
      }

      Point3d uCrossW = new Point3d();
      Point3d uCrossV = new Point3d();
      uCrossW.cross (u, w);
      uCrossV.cross(u, v);

      double signt = uCrossW.dot (uCrossV);
      if (signt < -epsilon)
      {
         return false;
      }
      double denom = uCrossV.norm ();
      double r = vCrossW.norm() / denom;
      double t = uCrossW.norm() / denom;
      return (r <= 1+epsilon && t <= 1+epsilon && r + t <= 1+epsilon);
   }

   public static PolygonalMesh readAnatomy(String name)
   {
      PolygonalMesh mesh = new PolygonalMesh();
      try
      {
         Reader  meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/skin/"+name+".obj", ".")));
         mesh.read(meshReader);
         mesh.scale (GlobalParameters.scale_to_human);
         mesh.triangulate ();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      return mesh;
   }
   public static PolygonalMesh readAnatomy2(String name)
   {
      PolygonalMesh mesh = new PolygonalMesh();
      try
      {
         Reader  meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/couple/"+name+".obj", ".")));
         mesh.read(meshReader);
         mesh.scale (GlobalParameters.scale_to_human);
         mesh.triangulate ();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         System.exit(1);
      }
      return mesh;
   }
   public static String inPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/skin/", ".");
   public static boolean boundToAnatomy(PolygonalMesh mesh, Point3d p)
   {
      boolean result = false;
      int numFace = mesh.myFaces.size ();
      for(int i=0; i< numFace; i++)
      {
         Face f = mesh.myFaces.get (i);
         Point3d p0 = f.getVertex (0).pnt;
         Point3d p1 = f.getVertex (1).pnt;
         Point3d p2 = f.getVertex (2).pnt;
         if(PointInTriangle(p0, p1, p2,p))
         {
            result = true;
            break;
         }
      }
      return result;
   }
}
