package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import artisynth.core.driver.Main;
import artisynth.core.mechmodels.AxialSpring;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.Particle;
import artisynth.core.mechmodels.Point;
import artisynth.core.util.ArtisynthPath;

import maspack.geometry.Face;
import maspack.geometry.HalfEdge;
import maspack.geometry.PolygonalMesh;
import maspack.geometry.Vertex3d;
import maspack.matrix.Point3d;
import maspack.matrix.RigidTransform3d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.LineStyle;
import maspack.render.RenderProps.PointStyle;

public class SkinSpringMesh{
   
   public static ArrayList<String>  pnames = new ArrayList<String>();
   public static ArrayList<String>  snames = new ArrayList<String>();
   
   public static int indexVertex(ArrayList<Vertex3d> myVertices, Point3d p)
   {
      int index = 0;
      for(int i=0; i< myVertices.size (); i++)
      {
         if(myVertices.get (i).pnt.equals (p))
         {
            index = i;
            break;
         }
      }
      return index;
   }
   public static void createSpringMesh(PolygonalMesh mesh, MechModel myMechModel) throws Exception
   {
      boolean isShow = true;
      ArrayList<Vertex3d> myVertices = mesh.getVertices ();
      ArrayList<Point3d> vertices = new ArrayList<Point3d>();
      ArrayList<Face> myFaces = mesh.getFaces (); 
      Particle[] ps = new Particle[mesh.getVertices ().size ()];
      int idx = 0;
      for (int i =0; i< myVertices.size(); i++) {
         Vertex3d vertex = (Vertex3d) myVertices.get (i);
         Point3d pnt = vertex.pnt;
         Particle p = new Particle (GlobalParameters.epi_pmass, pnt.x, pnt.y, pnt.z);
         p.setPointDamping (GlobalParameters.epi_pd);
         p.setDynamic (true);
         p.setName ("skin_particle_"+idx);
         pnames.add ("skin_particle_"+idx);
         myMechModel.addParticle (p);
         RenderProps.setPointColor (p, Color.gray);
         RenderProps.setPointStyle (p, PointStyle.SPHERE);
         RenderProps.setPointRadius (p, 1.0*GlobalParameters.scale_to_human);
         RenderProps.setVisible (p,  GlobalParameters.showMS);
         ps[idx] = p;
         idx++;
      }
      vertices.clear ();
      SkinBounder skinBounder = new SkinBounder();
      skinBounder.bound (pnames, myMechModel);
      ArrayList<String> validlist = new ArrayList<String>();
      for (int k = 0; k<myFaces.size (); k++) 
      {
         Face face  = (Face) myFaces.get (k);
         int[] vertexIndices = face.getVertexIndices();
         for(int  i = 0; i< vertexIndices.length;i++)
         {
            int i1 = (i)%vertexIndices.length;
            int i2 = (i+1)%vertexIndices.length;
            Point3d p0 = face.getVertex (i1).pnt;
            Point3d p1 = face.getVertex (i2).pnt;
            int vp1 = indexVertex(myVertices, p0);
            int vp2 = indexVertex(myVertices, p1);
            if(!validlist.contains (vp1+"_"+vp2) && !validlist.contains (vp2+"_"+vp1))
            {
               validlist.add (vp1+"_"+vp2);
               validlist.add (vp2+"_"+vp1);
               Point3d v1 = ps[vp1].getPosition ();
               Point3d v2 = ps[vp2].getPosition ();
               Point3d v = new Point3d(0,0,0);
               v.sub (v1, v2);
               double restLen = v.norm ();
               AxialSpring spring = new AxialSpring(GlobalParameters.epi_sk, GlobalParameters.epi_sd, restLen);
               spring.setName ("skin_spring_"+vp1+"_"+vp2);
               snames.add ("skin_spring_"+vp1+"_"+vp2);
               myMechModel.attachAxialSpring(ps[vp1],ps[vp2],spring);
               RenderProps.setLineColor (spring, Color.green);
               RenderProps.setLineStyle(spring, LineStyle.LINE);
               RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
               RenderProps.setVisible (spring, GlobalParameters.showMS);
            }
         }
      }
   }
}
