package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import maspack.geometry.Intersector;
import maspack.geometry.OBBTree;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.Point3d;
import maspack.matrix.Vector2d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.LineStyle;
import maspack.util.ReaderTokenizer;
import artisynth.core.femmodels.FemElement3d;
import artisynth.core.femmodels.FemMarker;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemNode3d;
import artisynth.core.mechmodels.AxialSpring;
import artisynth.core.mechmodels.FrameMarker;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;
import artisynth.models.headneck.FEBioXML;

public class CoupleByICP {
   public  OBBTree obbt = null;
   protected  ArrayList<ViaInfo> viaInfoList = new ArrayList<ViaInfo>();
   public  ArrayList<String>  snames = new ArrayList<String>();
   public  String bonePath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/couple/", ".");
   private class ViaInfo {
      public String bonename;
      public void scan(ReaderTokenizer rtok) throws IOException {
         bonename = rtok.sval;
      }
   }
   private  ArrayList<ViaInfo> readViaInfoList(String filename)
   throws IOException {
      ReaderTokenizer rtok = new ReaderTokenizer(new FileReader(ArtisynthPath
         .getSrcRelativePath(NeckSkeleton.class, "couple/" + filename+".txt")));
      rtok.wordChars(".");
      ArrayList<ViaInfo> viaInfoList = new ArrayList<ViaInfo>();
      while (rtok.nextToken() != ReaderTokenizer.TT_EOF) {
         ViaInfo bi = new ViaInfo();
         bi.scan(rtok);
         viaInfoList.add(bi);
      }
      return viaInfoList;
   }
   public void couple(MechModel myMechModel, String musclename, String filename) throws Exception
   {
      if(obbt == null)
      {
         PolygonalMesh mesh = new PolygonalMesh();
         try {
            Reader meshReader = new BufferedReader ( new FileReader (bonePath+"skeleton.obj"));
            mesh.read(meshReader);
            mesh.scale (GlobalParameters.scale_to_human);
            obbt = mesh.getObbtree();
         }
         catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
         }
      }
      viaInfoList = readViaInfoList(musclename);
      ArrayList<Integer> bcs = FEBioXML.read (bonePath+"l_"+filename+".feb", 1);
      for (int i =0; i< bcs.size (); i++)
      {
         FemModel3d ltissue = (FemModel3d)myMechModel.models ().get ("l_"+musclename);
         FemNode3d node = (FemNode3d)ltissue.getNode (bcs.get (i)-1); 
         Point3d tmpPoint= new Point3d();
         FemElement3d elem = ltissue.findNearestSurfaceElement(tmpPoint,node.getRestPosition ());
         FemMarker mkr = new FemMarker (tmpPoint);
         RenderProps.setVisible (mkr, false);
         ltissue.addMarker(mkr, elem);
         Point3d musclePnt = new Point3d(node.getRestPosition ());
         Point3d nearest = new Point3d();
         Vector2d coords = new Vector2d();
         obbt.nearestFace (musclePnt, null, nearest, coords, new Intersector());
         attach(tmpPoint, myMechModel, mkr, node, musclename, nearest);
         
         FemModel3d rtissue = (FemModel3d)myMechModel.models ().get ("r_"+musclename);
         musclePnt = new Point3d(node.getRestPosition ().x*-1, node.getRestPosition ().y, node.getRestPosition ().z);
         FemNode3d node2 = (FemNode3d)rtissue.getNode (bcs.get (i)-1); 
         elem = rtissue.findNearestSurfaceElement(tmpPoint, musclePnt);
         mkr = new FemMarker (tmpPoint);
         rtissue.addMarker(mkr, elem);
         RenderProps.setVisible (mkr, false);
         obbt.nearestFace (musclePnt, null, nearest, coords, new Intersector());
         attach(tmpPoint, myMechModel, mkr, node2, musclename, nearest);
      }
   }
   public void attach(Point3d musclePnt, MechModel myMechModel, FemMarker mkr, FemNode3d node, String musclename, Point3d nearest)
   {
      for(ViaInfo viaInfo : viaInfoList)
      {
         PolygonalMesh boundingmesh = GeoUtility.readAnatomy2(viaInfo.bonename);
         if(GeoUtility.boundToAnatomy(boundingmesh,nearest))
         {
            if(viaInfo.bonename.compareTo ("base")==0)
            {
               node.setDynamic (false);
               RenderProps.setPointColor (node, Color.black);
               RenderProps.setPointRadius (node, 0.005);
               RenderProps.setVisible (node, GlobalParameters.showds);
            }
            else
            {
               RigidBody boundingbody = myMechModel.getMyRigidBodies().get(viaInfo.bonename);
               Point3d nearestcopy = new Point3d(nearest.x, nearest.y, nearest.z);
               nearestcopy.sub (checkWhichRigid(viaInfo.bonename));
               FrameMarker marker = new FrameMarker(nearestcopy);
               marker.setFrame (boundingbody);
               myMechModel.addFrameMarker (marker);
               RenderProps.setVisible (marker, false);
               Point3d v1 = musclePnt;
               Point3d v2 = nearest;
               Point3d v = new Point3d(0,0,0);
               v.sub (v1, v2);
               AxialSpring spring = new AxialSpring(GlobalParameters.deep_sk, GlobalParameters.deep_sd, v.norm ());
               int id2 = snames.size ();
               spring.setName ("deep_spring_"+musclename+"_"+id2);
               snames.add ("deep_spring_"+musclename+"_"+id2);
               myMechModel.attachAxialSpring(mkr,marker,spring);
               RenderProps.setLineColor (spring, Color.blue);
               RenderProps.setLineStyle(spring, LineStyle.LINE);
               RenderProps.setLineWidth (spring, (int)(300*GlobalParameters.scale_to_human));
               RenderProps.setVisible (spring, GlobalParameters.showds);
            }
            break;
         }
      }
   }
   
   public Point3d checkWhichRigid(String meshname)
   {
      Point3d pivot = null;
      if (meshname.compareTo("skull")==0)
         pivot = GlobalParameters.skullPivot;
      else if (meshname.compareTo("hyoid")==0)
         pivot = GlobalParameters.hyoidPivot;
      else if (meshname.compareTo("thyroid")==0)
         pivot = GlobalParameters.thyroidPivot;
      else if (meshname.compareTo("cricoid")==0)
         pivot = GlobalParameters.cricoidPivot;
      else if (meshname.compareTo("C1")==0)
         pivot = GlobalParameters.c1Pivot;
      else if (meshname.compareTo("C2")==0)
         pivot = GlobalParameters.c2Pivot;
      else if (meshname.compareTo("C3")==0)
         pivot = GlobalParameters.c3Pivot;
      else if (meshname.compareTo("C4")==0)
         pivot = GlobalParameters.c4Pivot;
      else if (meshname.compareTo("C5")==0)
         pivot = GlobalParameters.c5Pivot;
      else if (meshname.compareTo("C6")==0)
         pivot = GlobalParameters.c6Pivot;
      else if (meshname.compareTo("C7")==0)
         pivot = GlobalParameters.c7Pivot;
      else if (meshname.compareTo("base")==0)
         pivot = GlobalParameters.basePivot;
      return pivot;
   }

}
