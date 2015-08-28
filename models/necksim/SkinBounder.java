package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import artisynth.core.femmodels.FemElement3d;
import artisynth.core.femmodels.FemMarker;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.mechmodels.AxialSpring;
import artisynth.core.mechmodels.FrameMarker;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.Particle;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;
import maspack.geometry.Intersector;
import maspack.geometry.OBBTree;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.Point3d;
import maspack.matrix.Vector2d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.LineStyle;
import maspack.util.ReaderTokenizer;

public class SkinBounder {
   
   public  ArrayList<String>  snames = new ArrayList<String>();
   public  OBBTree obbt = new OBBTree();
   static String  path = "src/artisynth/models/necksim/skin/";
   static String boundfilename = "boundlist.txt";
   protected  ArrayList<BoundInfo> boundInfoList = new ArrayList<BoundInfo>();
   private class BoundInfo {
      public String meshname;
      public String bodyname;
      public boolean isPair;
      public boolean isFEM;
      public void scan(ReaderTokenizer rtok) throws IOException {
         meshname = rtok.sval;
         rtok.nextToken();
         bodyname = rtok.sval;
         rtok.nextToken();
         if(rtok.sval.compareTo ("p")==0)
            isPair = true;
         else
            isPair = false;
         rtok.nextToken();
         if(rtok.sval.compareTo("fem") == 0)
            isFEM = true;
         else
            isFEM = false;
      }
   }
   private  ArrayList<BoundInfo> readBoundInfoList(String filename)
   throws IOException {
      ReaderTokenizer rtok = new ReaderTokenizer(new FileReader(ArtisynthPath
         .getSrcRelativePath(SkinBounder.class, "skin/" + filename)));
      rtok.wordChars(".");
      ArrayList<BoundInfo> boundInfoList = new ArrayList<BoundInfo>();
      while (rtok.nextToken() != ReaderTokenizer.TT_EOF) {
         BoundInfo bi = new BoundInfo();
         bi.scan(rtok);
         boundInfoList.add(bi);
      }
      return boundInfoList;
   }
   public SkinBounder()
   {
   }
   public  void projectionByICP(String msh2)
   {
      PolygonalMesh mesh2 = new PolygonalMesh();
      try {
         
         Reader meshReader = new BufferedReader ( new FileReader (
            ArtisynthPath.getHomeRelativeFile (path+msh2+".obj", ".")));
         mesh2.read(meshReader);
         mesh2.scale (GlobalParameters.scale_to_human);
         obbt = mesh2.getObbtree();
      }
      catch (Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
   }
   public  void bound(ArrayList<String>  pnames, MechModel myMechModel) throws Exception
   {
      String skeletonname="skeleton";
      projectionByICP(skeletonname);
      boundInfoList = readBoundInfoList(boundfilename);
      femprops.setPointRadius (0.5*GlobalParameters.scale_to_human);
      femprops.setPointStyle (RenderProps.PointStyle.SPHERE);
      femprops.setPointColor (Color.red);
      femprops.setVisible (GlobalParameters.showCS);
      boneprops.setPointRadius (0.5*GlobalParameters.scale_to_human);
      boneprops.setPointStyle (RenderProps.PointStyle.SPHERE);
      boneprops.setPointColor (Color.white);
      boneprops.setVisible (GlobalParameters.showCS);
      ArrayList<Point3d>  nearests = new ArrayList<Point3d>();
      for(int i=0; i< pnames.size (); i++)
      {
         String name =(String) pnames.get (i);
         Particle p = (Particle)myMechModel.particles ().get (name);
         Point3d skinPnt = new Point3d(p.getPosition ());
         Point3d nearest = new Point3d();
         Vector2d coords = new Vector2d();
         obbt.nearestFace (skinPnt, null, nearest, coords, new Intersector());
         nearests.add (nearest);
      }   
      for (BoundInfo boundInfo : boundInfoList) {
         if(boundInfo.isPair)
         {
            String meshname = "l_"+boundInfo.meshname;
            String bodyname = "l_"+boundInfo.bodyname;
            attchSkin(meshname, bodyname, pnames, nearests, boundInfo, myMechModel);
            meshname = "r_"+boundInfo.meshname;
            bodyname = "r_"+boundInfo.bodyname;
            attchSkin(meshname, bodyname, pnames, nearests, boundInfo, myMechModel);
         }
         else
         {
            attchSkin(boundInfo.meshname, boundInfo.bodyname, pnames, nearests, boundInfo, myMechModel);
         }
      }
      nearests.clear ();
   }
   
    RenderProps femprops = (new Particle()).createRenderProps();
    RenderProps boneprops = (new Particle()).createRenderProps();

   public  void attchSkin(String meshname, String bodyname, ArrayList<String> pnames, ArrayList<Point3d>  nearests, BoundInfo boundInfo, MechModel myMechModel)
   {
      PolygonalMesh boundingmesh = GeoUtility.readAnatomy(meshname);
      for(int i=0; i< pnames.size (); i++)
      {
         String name =(String) pnames.get (i);
         int id = Integer.parseInt (name.split ("_")[2]);
         Particle p = (Particle)myMechModel.particles ().get (name);
         Point3d skinPnt = new Point3d(p.getPosition ());
         Point3d nearest = (Point3d)nearests.get (i);
         if(SkinBoundManul.skullParticles.contains (name)) //skull
         {
            if(!p.isAttached ())
            {
               p.setDynamic (true);
               RigidBody boundingbody = myMechModel.getMyRigidBodies().get("skull");
               myMechModel.attachPoint (p, boundingbody);
               p.setRenderProps (boneprops);
            }
         }
         else if (SkinBoundManul.baseParticles.contains (name))
         {
            if(!p.isAttached ())
            {
               p.setDynamic (true);
               RigidBody boundingbody = myMechModel.getMyRigidBodies().get("base");
               myMechModel.attachPoint (p, boundingbody);
               p.setRenderProps (boneprops);
            }
         }
         else if (SkinBoundManul.rthyrohyoidParticles.contains (name)) //1
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("r_thyrohyoid");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else if (SkinBoundManul.rsternocleidomastoidParticles.contains (name)) //5
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("r_sternocleidomastoid");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else if (SkinBoundManul.lsternocleidomastoidParticles.contains (name)) //6
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("l_sternocleidomastoid");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else if(SkinBoundManul.rsternohyoidParticles.contains (name)) //3
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("r_sternohyoid");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else if(SkinBoundManul.rtrapeziusParticles.contains (name)) //7
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("r_trapezius");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else if(SkinBoundManul.ltrapeziusParticles.contains (name)) //8
         {
            p.setDynamic (true);
            Point3d tmpPoint= new Point3d();
            FemModel3d tissue = (FemModel3d)myMechModel.models ().get ("l_trapezius");
            FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
            FemMarker mkr = new FemMarker (tmpPoint);
            tissue.addMarker(mkr, elem);
            RenderProps.setVisible (mkr, false);
            Point3d v1 = skinPnt;
            Point3d v2 = tmpPoint;
            Point3d v = new Point3d(0,0,0);
            v.sub (v1, v2);
            AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
            int id2 = snames.size ();
            spring.setName ("inner_spring_"+id+"_"+id2);
            snames.add ("inner_spring_"+id+"_"+id2);
            myMechModel.attachAxialSpring(p,mkr,spring);
            RenderProps.setLineColor (spring, Color.blue);
            RenderProps.setLineStyle(spring, LineStyle.LINE);
            RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
            RenderProps.setVisible (spring, GlobalParameters.showCS);
            p.setRenderProps (femprops);
         }
         else
         {
            if(GeoUtility.boundToAnatomy(boundingmesh,nearest))
            {
               if(boundInfo.isFEM)
               {  
                  p.setDynamic (true);
                  Point3d tmpPoint= new Point3d();
                  FemModel3d tissue = (FemModel3d)myMechModel.models ().get (bodyname);
                  FemElement3d elem = tissue.findNearestSurfaceElement(tmpPoint,nearest);
                  FemMarker mkr = new FemMarker (tmpPoint);
                  tissue.addMarker(mkr, elem);
                  RenderProps.setVisible (mkr, false);
                  Point3d v1 = skinPnt;
                  Point3d v2 = tmpPoint;
                  Point3d v = new Point3d(0,0,0);
                  v.sub (v1, v2);
                  AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
                  int id2 = snames.size ();
                  spring.setName ("inner_spring_"+id+"_"+id2);
                  snames.add ("inner_spring_"+id+"_"+id2);
                  myMechModel.attachAxialSpring(p,mkr,spring);
                  RenderProps.setLineColor (spring, Color.blue);
                  RenderProps.setLineStyle(spring, LineStyle.LINE);
                  RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
                  RenderProps.setVisible (spring, GlobalParameters.showCS);
                  p.setRenderProps (femprops);
               }
               else
               {
                  if(bodyname.compareTo ("skull")==0)
                  {
                     p.setDynamic (true);
                     RigidBody boundingbody = myMechModel.getMyRigidBodies().get(bodyname);
                     myMechModel.attachPoint (p, boundingbody);
                     p.setRenderProps (boneprops);
                  }
                  else if(bodyname.compareTo ("jaw")==0)
                  {
                     if(!p.isAttached ())
                     {
                        p.setDynamic (true);
                        RigidBody boundingbody = myMechModel.getMyRigidBodies().get(bodyname);
                        myMechModel.attachPoint (p, boundingbody);
                        p.setRenderProps (boneprops);
                     }
                  }
                  else if(bodyname.compareTo ("base")==0)
                  {
                     if(!p.isAttached ())
                     {
                        p.setDynamic (true);
                        RigidBody boundingbody = myMechModel.getMyRigidBodies().get(bodyname);
                        myMechModel.attachPoint (p, boundingbody);
                        p.setRenderProps (boneprops);
                     }
                  }
                  else
                  {
                     p.setDynamic (true);
                     RigidBody boundingbody = myMechModel.getMyRigidBodies().get(bodyname);
                     Point3d nearestcopy = new Point3d(nearest.x, nearest.y, nearest.z);
                     nearestcopy.sub (checkWhichRigid(meshname));
                     FrameMarker mkr = new FrameMarker(nearestcopy);
                     mkr.setFrame (boundingbody);
                     myMechModel.addFrameMarker (mkr);
                     RenderProps.setVisible (mkr, GlobalParameters.showCS);
                     Point3d v1 = skinPnt;
                     Point3d v2 = nearest;
                     Point3d v = new Point3d(0,0,0);
                     v.sub (v1, v2);
                     AxialSpring spring = new AxialSpring(GlobalParameters.inner_sk, GlobalParameters.inner_sd, v.norm ());
                     int id2 = snames.size ();
                     spring.setName ("inner_spring_"+id+"_"+id2);
                     snames.add ("inner_spring_"+id+"_"+id2);
                     myMechModel.attachAxialSpring(p,mkr,spring);
                     RenderProps.setLineColor (spring, Color.blue);
                     RenderProps.setLineStyle(spring, LineStyle.LINE);
                     RenderProps.setLineWidth (spring, (int)(400*GlobalParameters.scale_to_human));
                     RenderProps.setVisible (spring, GlobalParameters.showCS);
                     p.setRenderProps (boneprops);
                  }
               }
            }
         }
      }
   }
   
   public Point3d checkWhichRigid(String meshname)
   {
      Point3d pivot = null;
      if (meshname.compareTo("skull")==0)
         pivot = GlobalParameters.skullPivot;
      else if (meshname.compareTo("jaw")==0)
         pivot = GlobalParameters.jawPivot;
      else if (meshname.compareTo("base")==0)
         pivot = GlobalParameters.basePivot;
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
      return pivot;
   }
}
